package com.example.namo2.domain.schedule.ui;

import static java.util.Arrays.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.namo2.domain.category.dao.repository.CategoryRepository;
import com.example.namo2.domain.category.dao.repository.PaletteRepository;

import com.example.namo2.domain.schedule.application.ScheduleFacade;
import com.example.namo2.domain.schedule.dao.repository.ScheduleRepository;
import com.example.namo2.domain.schedule.ui.dto.ScheduleRequest;
import com.example.namo2.domain.schedule.ui.dto.ScheduleResponse;

import com.example.namo2.domain.user.dao.repository.UserRepository;

import com.example.namo2.global.config.interceptor.AuthenticationInterceptor;
import com.example.namo2.global.utils.Converter;
import com.example.namo2.global.utils.JwtUtils;

@WebMvcTest(controllers = ScheduleController.class)
public class ScheduleControllerTest {

	@Autowired
	private MockMvc mockMvc;//애플리케이션 서버에 배포하지 않고도 테스트할 수 있는 유틸리티 클래스

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ScheduleRepository scheduleRepository;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private CategoryRepository categoryRepository;
	@MockBean
	private PaletteRepository paletteRepository;

	@MockBean
	private ScheduleFacade scheduleFacade;
	@MockBean
	private Converter converter;
	@MockBean
	private JwtUtils jwtUtils;
	@MockBean
	private RedisTemplate<String, String> redisTemplate;

	@MockBean
	AuthenticationInterceptor authenticationInterceptor;

	@BeforeEach
	void initTest() throws Exception {
		when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
	}

	@Test
	@DisplayName("일정을 생성한다")
	public void createSchedule() throws Exception {
		//given
		Long scheduleId = 1L;
		ScheduleRequest.PostScheduleDto request = ScheduleRequest.PostScheduleDto.builder()
			.name("user")
			.startDate(1676052480L)
			.endDate(1676052590L)
			.interval(3)
			.alarmDate(new HashSet<Integer>(asList(5, 10, 30)))
			.x(37.5170112)
			.y(126.9019532)
			.locationName("경복궁")
			.kakaoLocationId("temp")
			.categoryId(22L)
			.build();
		ScheduleResponse.ScheduleIdDto scheduleIdDto = ScheduleResponse.ScheduleIdDto.builder()
			.scheduleId(scheduleId)
			.build();

		given(scheduleFacade.createSchedule(any(), any())).willReturn(scheduleIdDto);

		//when then
		mockMvc.perform(
				post("/api/v1/schedules")
					.header(HttpHeaders.FROM, "localhost")
					.content(objectMapper.writeValueAsString(request))//request body을 JSON 형식으로 설정
					.contentType(MediaType.APPLICATION_JSON) //Content-Type 헤더를 application/json으로 설정
			)
			.andDo(print())//테스트 실행 결과를 콘솔에 출력
			.andExpect(status().isOk());// HTTP 상태 코드가 200(OK)인지 검증
	}

	@Test
	@DisplayName("월별 개인 & 모임 일정을 조회한다")
	public void getSchedulesByUserPerMonth() throws Exception {
		List<ScheduleResponse.GetScheduleDto> list = new ArrayList<>();
		ScheduleResponse.GetScheduleDto getScheduleDto = ScheduleResponse.GetScheduleDto.builder()
			.scheduleId(1L)
			.isMoimSchedule(false)
			.name("일정")
			.kakaoLocationId("카카오")
			.alarmDate(asList(5, 10, 30))
			.startDate(1676052480L)
			.categoryId(1L)
			.hasDiary(true)
			.endDate(1676052590L)
			.interval(3)
			.locationName("경복궁")
			.x(3.242)
			.y(5.421)
			.build();

		list.add(getScheduleDto);
		list.add(getScheduleDto);
		list.add(getScheduleDto);
		given(scheduleFacade.getSchedulesByUser(any(), anyList())).willReturn(list);

		//when & then
		mockMvc.perform(
				get("/api/v1/schedules/2024,02")
					.header(HttpHeaders.FROM, "localhost")
			).andDo(print())//테스트 실행 결과를 콘솔에 출력
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("월별 모임 일정을 조회한다")
	public void getMoimSchedulesByUserPerMonth() throws Exception {
		List<ScheduleResponse.GetScheduleDto> list = new ArrayList<>();
		ScheduleResponse.GetScheduleDto getScheduleDto = ScheduleResponse.GetScheduleDto.builder()
			.scheduleId(1L)
			.isMoimSchedule(true)
			.name("일정")
			.kakaoLocationId("카카오")
			.alarmDate(asList(5, 10, 30))
			.startDate(1676052480L)
			.categoryId(1L)
			.hasDiary(true)
			.endDate(1676052590L)
			.interval(3)
			.locationName("경복궁")
			.x(3.242)
			.y(5.421)
			.build();
		list.add(getScheduleDto);
		list.add(getScheduleDto);
		list.add(getScheduleDto);
		System.out.println("리스트" + list.size());
		given(scheduleFacade.getMoimSchedulesByUser(any(), anyList())).willReturn(list);

		//when & then
		mockMvc.perform(
				get("/api/v1/schedules/moim/2024,02")
					.header(HttpHeaders.FROM, "localhost")
			).andDo(print())//테스트 실행 결과를 콘솔에 출력
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("모든 개인 & 모임 일정을 조회한다")
	public void getAllSchedulesByUser() throws Exception {
		List<ScheduleResponse.GetScheduleDto> list = new ArrayList<>();
		ScheduleResponse.GetScheduleDto getScheduleDto = ScheduleResponse.GetScheduleDto.builder()
			.scheduleId(1L)
			.isMoimSchedule(false)
			.name("일정")
			.kakaoLocationId("카카오")
			.alarmDate(asList(5, 10, 30))
			.startDate(1676052480L)
			.categoryId(1L)
			.hasDiary(true)
			.endDate(1676052590L)
			.interval(3)
			.locationName("경복궁")
			.x(3.242)
			.y(5.421)
			.build();

		list.add(getScheduleDto);
		list.add(getScheduleDto);
		list.add(getScheduleDto);
		given(scheduleFacade.getAllSchedulesByUser(any())).willReturn(list);

		//when & then
		mockMvc.perform(
				get("/api/v1/schedules/all")
					.header(HttpHeaders.FROM, "localhost")
			).andDo(print())//테스트 실행 결과를 콘솔에 출력
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("모든 모임 일정을 조회한다")
	public void getAllMoimSchedulesByUser() throws Exception {
		List<ScheduleResponse.GetScheduleDto> list = new ArrayList<>();
		ScheduleResponse.GetScheduleDto getScheduleDto = ScheduleResponse.GetScheduleDto.builder()
			.scheduleId(1L)
			.isMoimSchedule(true)
			.name("일정")
			.kakaoLocationId("카카오")
			.alarmDate(asList(5, 10, 30))
			.startDate(1676052480L)
			.categoryId(1L)
			.hasDiary(true)
			.endDate(1676052590L)
			.interval(3)
			.locationName("경복궁")
			.x(3.242)
			.y(5.421)
			.build();

		list.add(getScheduleDto);
		list.add(getScheduleDto);
		list.add(getScheduleDto);
		given(scheduleFacade.getAllMoimSchedulesByUser(any())).willReturn(list);

		//when & then
		mockMvc.perform(
				get("/api/v1/schedules/moim/all")
					.header(HttpHeaders.FROM, "localhost")
			).andDo(print())//테스트 실행 결과를 콘솔에 출력
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("일정을 수정한다.")
	public void modifyUserSchedule() throws Exception {
		Long scheduleId = 1L;
		ScheduleRequest.PostScheduleDto request = ScheduleRequest.PostScheduleDto.builder()
			.name("user")
			.startDate(1676052480L)
			.endDate(1676052590L)
			.interval(3)
			.alarmDate(new HashSet<Integer>(asList(5, 10, 30)))
			.x(37.5170112)
			.y(126.9019532)
			.locationName("경복궁")
			.kakaoLocationId("temp")
			.categoryId(22L)
			.build();
		ScheduleResponse.ScheduleIdDto scheduleIdDto = ScheduleResponse.ScheduleIdDto.builder()
			.scheduleId(scheduleId)
			.build();

		given(scheduleFacade.modifySchedule(anyLong(), any(), any())).willReturn(scheduleIdDto);

		mockMvc.perform(
				patch("/api/v1/schedules/{scheduleId}", scheduleId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("일정을 삭제한다.")
	public void deleteScehdule() throws Exception {
		Long scheduleId = 1L;
		Long kind = 0L;

		mockMvc.perform(
				delete("/api/v1/schedules/{scheduleId}/{kind}", scheduleId, kind)
			).andDo(print())
			.andExpect(status().isOk());
	}
}
