/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import dev.rollczi.liteskullapi.SkullData;
import dev.rollczi.liteskullapi.extractor.SkullDataDefault;

class SkullDataDefaultImpl implements SkullDataDefault {

    private static final SkullData SKULL_DATA = new SkullData(SkullUtils.DEFAULT_SIGNATURE, SkullUtils.DEFAULT_VALUE);

    @Override
    public SkullData defaultSkullData() {
        return SKULL_DATA;
    }

}
