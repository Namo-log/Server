package com.namo.spring.db.mysql.domains.category.type;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum PaletteEnum {
    BASIC(ColorCategory.BASIC,
            new ColorChip[]{
                    ColorChip.COLOR_1,
                    ColorChip.COLOR_2,
                    ColorChip.COLOR_3,
                    ColorChip.COLOR_4}),
    PALETTE(ColorCategory.PALETTE,
            new ColorChip[]{
                    ColorChip.COLOR_5,
                    ColorChip.COLOR_6,
                    ColorChip.COLOR_7,
                    ColorChip.COLOR_8,
                    ColorChip.COLOR_9,
                    ColorChip.COLOR_10,
                    ColorChip.COLOR_11,
                    ColorChip.COLOR_12,
                    ColorChip.COLOR_13,
                    ColorChip.COLOR_14});

    private final ColorCategory category;
    private final ColorChip[] containColors;

    public static ColorChip[] getBasicColors() {
        return BASIC.containColors;
    }

    public static ColorChip[] getPaletteColors() {
        return PALETTE.containColors;
    }

    public static long[] getPaletteColorIds() {
        return Arrays.stream(PALETTE.containColors)
                .mapToLong(colorChip -> colorChip.getId())
                .toArray();
    }
}
