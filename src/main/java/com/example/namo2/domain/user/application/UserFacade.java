package com.example.namo2.domain.user.application;

import java.net.HttpURLConnection;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.example.namo2.domain.category.application.converter.CategoryConverter;
import com.example.namo2.domain.category.application.impl.CategoryService;
import com.example.namo2.domain.category.application.impl.PaletteService;
import com.example.namo2.domain.category.domain.Category;
import com.example.namo2.domain.category.domain.CategoryKind;

import com.example.namo2.domain.memo.application.impl.MoimMemoLocationService;

import com.example.namo2.domain.moim.application.impl.MoimAndUserService;
import com.example.namo2.domain.moim.application.impl.MoimScheduleAndUserService;
import com.example.namo2.domain.moim.domain.MoimScheduleAndUser;

import com.example.namo2.domain.schedule.application.impl.AlarmService;
import com.example.namo2.domain.schedule.application.impl.ImageService;
import com.example.namo2.domain.schedule.application.impl.ScheduleService;
import com.example.namo2.domain.schedule.domain.Schedule;

import com.example.namo2.domain.user.application.converter.TermConverter;
import com.example.namo2.domain.user.application.converter.UserConverter;
import com.example.namo2.domain.user.application.impl.UserService;
import com.example.namo2.domain.user.domain.Term;
import com.example.namo2.domain.user.domain.User;
import com.example.namo2.domain.user.domain.UserStatus;
import com.example.namo2.domain.user.ui.dto.UserRequest;
import com.example.namo2.domain.user.ui.dto.UserResponse;

import com.example.namo2.global.feignclient.apple.AppleAuthClient;
import com.example.namo2.global.feignclient.apple.AppleProperties;
import com.example.namo2.global.feignclient.apple.AppleResponse;
import com.example.namo2.global.feignclient.apple.AppleResponseConverter;
import com.example.namo2.global.feignclient.kakao.KakaoAuthClient;
import com.example.namo2.global.feignclient.naver.NaverAuthClient;
import com.example.namo2.global.utils.JwtUtils;
import com.example.namo2.global.utils.SocialUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserFacade {
	private final Logger logger = LoggerFactory.getLogger(UserFacade.class);
	private final SocialUtils socialUtils;
	private final JwtUtils jwtUtils;
	private final RedisTemplate<String, String> redisTemplate;

	private final UserService userService;
	private final PaletteService paletteService;
	private final CategoryService categoryService;
	private final ScheduleService scheduleService;
	private final AlarmService alarmService;
	private final ImageService imageService;
	private final MoimAndUserService moimAndUserService;
	private final MoimScheduleAndUserService moimScheduleAndUserService;
	private final MoimMemoLocationService moimMemoLocationService;

	private final KakaoAuthClient kakaoAuthClient;
	private final NaverAuthClient naverAuthClient;
	private final AppleAuthClient appleAuthClient;
	private final AppleProperties appleProperties;

	@Transactional
	public UserResponse.SignUpDto signupKakao(UserRequest.SocialSignUpDto signUpDto) {
		HttpURLConnection con = socialUtils.connectKakaoResourceServer(signUpDto);
		socialUtils.validateSocialAccessToken(con);

		String result = socialUtils.findSocialLoginUsersInfo(con);

		log.debug("result = " + result);

		Map<String, String> response = socialUtils.findResponseFromKakako(result);
		User user = UserConverter.toUser(response);
		User savedUser = saveOrNot(user);
		UserResponse.SignUpDto signUpRes = jwtUtils.generateTokens(savedUser.getId());

		userService.updateRefreshToken(savedUser.getId(), signUpRes.getRefreshToken());
		return signUpRes;
	}

	@Transactional
	public UserResponse.SignUpDto signupNaver(UserRequest.SocialSignUpDto signUpDto) {
		HttpURLConnection con = socialUtils.connectNaverResourceServer(signUpDto);
		socialUtils.validateSocialAccessToken(con);

		String result = socialUtils.findSocialLoginUsersInfo(con);

		Map<String, String> response = socialUtils.findResponseFromNaver(result);
		User user = UserConverter.toUser(response);
		User savedUser = saveOrNot(user);
		UserResponse.SignUpDto signUpRes = jwtUtils.generateTokens(savedUser.getId());

		userService.updateRefreshToken(savedUser.getId(), signUpRes.getRefreshToken());
		return signUpRes;
	}

	@Transactional
	public UserResponse.SignUpDto signupApple(UserRequest.AppleSignUpDto req) {
		AppleResponse.ApplePublicKeyListDto applePublicKeys = appleAuthClient.getApplePublicKeys();
		AppleResponse.ApplePublicKeyDto applePublicKey = null;
		String email = "";

		JSONObject headerJson = userService.getHeaderJson(req);
		Object kid = headerJson.get("kid"); //개발자 계정에서 얻은 10자리 식별자 키
		Object alg = headerJson.get("alg"); //토큰을 암호화하는데 사용되는 암호화 알고리즘

		//identityToken 검증
		applePublicKey = AppleResponseConverter.toApplePublicKey(applePublicKeys, alg, kid);

		PublicKey publicKey = userService.getPublicKey(applePublicKey);
		userService.validateToken(publicKey, req.getIdentityToken());

		//identity에서 email뽑기
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(publicKey)
			.build()
			.parseClaimsJws(req.getIdentityToken())
			.getBody();
		String appleOauthId = claims.get("sub", String.class);
		String appleEmail = claims.get("email", String.class);
		log.debug("email: {}, oauthId : {}", appleEmail, appleOauthId);

		if (!req.getEmail().isBlank()) { //첫 로그인
			email = req.getEmail();
		} else { //재로그인
			email = appleEmail;
		}

		//로그인 분기처리
		User savedUser;
		Optional<User> userByEmail = userService.getUserByEmail(email);
		if (userByEmail.isEmpty()) { //첫로그인
			userService.checkEmailAndName(req.getEmail(), req.getUsername());
			savedUser = userService.createUser(UserConverter.toUser(req.getEmail(), req.getUsername()));
			makeBaseCategory(savedUser);
		} else { //재로그인
			savedUser = userByEmail.get();
			savedUser.setStatus(UserStatus.ACTIVE);
		}

		UserResponse.SignUpDto signUpRes = jwtUtils.generateTokens(savedUser.getId());
		userService.updateRefreshToken(savedUser.getId(), signUpRes.getRefreshToken());
		return signUpRes;
	}

	@Transactional
	public UserResponse.SignUpDto reissueAccessToken(UserRequest.SignUpDto signUpDto) {
		userService.checkRefreshTokenValidation(signUpDto.getRefreshToken());
		userService.checkLogoutUser(signUpDto);

		User user = userService.getUserByRefreshToken(signUpDto.getRefreshToken());
		UserResponse.SignUpDto signUpRes = jwtUtils.generateTokens(user.getId());
		user.updateRefreshToken(signUpRes.getRefreshToken());
		return signUpRes;
	}

	@Transactional
	public void logout(UserRequest.LogoutDto logoutDto) {
		// AccessToken 만료시 종료
		userService.checkAccessTokenValidation(logoutDto.getAccessToken());

		Long expiration = jwtUtils.getExpiration(logoutDto.getAccessToken());
		redisTemplate.opsForValue().set(logoutDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
	}

	private User saveOrNot(User user) {
		Optional<User> userByEmail = userService.getUserByEmail(user.getEmail());
		if (userByEmail.isEmpty()) {
			User save = userService.createUser(user);
			makeBaseCategory(save);
			return save;
		}
		User eixtingUser = userByEmail.get();
		eixtingUser.setStatus(UserStatus.ACTIVE);
		return eixtingUser;
	}

	private void makeBaseCategory(User save) {
		Category baseCategory = CategoryConverter.toCategory(
			CategoryKind.SCHEDULE.getCategoryName(),
			paletteService.getReferenceById(1L),
			Boolean.TRUE,
			save,
			CategoryKind.SCHEDULE
		);
		Category groupCategory = CategoryConverter.toCategory(
			CategoryKind.MOIM.getCategoryName(),
			paletteService.getReferenceById(4L),
			Boolean.TRUE,
			save,
			CategoryKind.MOIM
		);

		categoryService.create(baseCategory);
		categoryService.create(groupCategory);
	}

	@Transactional(readOnly = false)
	public void createTerm(UserRequest.TermDto termDto, Long userId) {
		User user = userService.getUser(userId);
		List<Term> terms = TermConverter.toTerms(termDto, user);
		userService.createTerm(terms);
	}

	@Transactional
	public void removeKakaoUser(HttpServletRequest request, String kakaoAccessToken) {
		//유저 토큰 만료시 예외 처리
		String accessToken = request.getHeader("Authorization");
		userService.checkAccessTokenValidation(accessToken);

		kakaoAuthClient.unlinkKakao(kakaoAccessToken);

		setUserInactive(request);
	}

	@Transactional
	public void removeNaverUser(HttpServletRequest request, String naverAccessToken) {
		//유저 토큰 만료시 예외 처리
		String accessToken = request.getHeader("Authorization");
		userService.checkAccessTokenValidation(accessToken);

		naverAuthClient.tokenAvailability(naverAccessToken);
		naverAuthClient.unlinkNaver(naverAccessToken);

		setUserInactive(request);
	}

	@Transactional
	public void removeAppleUser(HttpServletRequest request, String authorizationCode) {
		//유저 토큰 만료시 예외 처리
		String accessToken = request.getHeader("Authorization");
		userService.checkAccessTokenValidation(accessToken);

		String clientSecret = "";

		clientSecret = createClientSecret();

		String appleToken = appleAuthClient.getAppleToken(clientSecret, authorizationCode);
		logger.debug("appleToken {}", appleToken);
		appleAuthClient.revoke(clientSecret, appleToken);

		setUserInactive(request);
	}

	public String createClientSecret() {
		Date expirationDate = Date.from(
			LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder()
			.setHeaderParam("kid", appleProperties.getKeyId())
			.setHeaderParam("alg", "ES256")
			.setIssuer(appleProperties.getTeamId())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(expirationDate)
			.setAudience("https://appleid.apple.com")
			.setSubject(appleProperties.getClientId())
			.signWith(SignatureAlgorithm.ES256, userService.getPrivateKey())
			.compact();
	}

	private void setUserInactive(HttpServletRequest request) {
		User user = userService.getUser(jwtUtils.resolveRequest(request));
		user.setStatus(UserStatus.INACTIVE);

		//token 만료처리
		String accessToken = request.getHeader("Authorization");
		Long expiration = jwtUtils.getExpiration(accessToken);
		redisTemplate.opsForValue().set(accessToken, "delete", expiration, TimeUnit.MILLISECONDS);
	}

	/**
	 * [유저삭제]
	 * 카테고리 삭제
	 * 스케줄 삭제
	 * - 스케줄 알람 삭제
	 * - 스케줄 이미지 삭제
	 * moimAndUser삭제
	 * moimScheduleAndUser 삭제
	 * - moimScheduleAlarm 삭제
	 * moimMemoLocationAndUser 삭제
	 */
	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
	@Transactional
	public void removeUserFromDB() {
		List<User> users = userService.getInactiveUser();
		users.forEach(
			user -> { //db에서 삭제
				logger.debug("[Delete] user name : " + user.getName());

				categoryService.removeCategoriesByUser(user);

				List<Schedule> schedules = scheduleService.getSchedulesByUser(user);
				alarmService.removeAlarmsBySchedules(schedules);
				imageService.removeImgsBySchedules(schedules);
				scheduleService.removeSchedules(schedules);

				moimAndUserService.removeMoimAndUsersByUser(user);

				List<MoimScheduleAndUser> moimScheduleAndUsers = moimScheduleAndUserService.getAllByUser(user);
				moimScheduleAndUserService.removeMoimScheduleAlarms(moimScheduleAndUsers);
				moimScheduleAndUserService.removeMoimScheduleAndUsers(moimScheduleAndUsers);

				moimMemoLocationService.removeMoimMemoLocationAndUsersByUser(user);
				userService.removeUser(user);
			}
		);

	}
}
