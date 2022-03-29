/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskull.standard;

import dev.rollczi.liteskull.api.SkullData;
import dev.rollczi.liteskull.api.extractor.SkullDataDefault;

class SkullDataDefaultImpl implements SkullDataDefault {

    private static final SkullData SKULL_DATA = new SkullData(SkullUtils.DEFAULT_SIGNATURE, SkullUtils.DEFAULT_VALUE);

    @Override
    public SkullData defaultSkullData() {
        return SKULL_DATA;
    }

}
