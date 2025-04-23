/*
 * Copyright (c) 2022 Rollczi
 */

package dev.rollczi.liteskullapi.standard;

import dev.rollczi.liteskullapi.PlayerIdentification;
import dev.rollczi.liteskullapi.SkullData;
import dev.rollczi.liteskullapi.extractor.SkullDataDefault;

class SkullDataDefaultImpl implements SkullDataDefault {

    @Override
    public SkullData defaultSkullData(PlayerIdentification playerIdentification) {
        String extractedName = playerIdentification.map(name -> name, uuid -> "default");
        return new SkullData(extractedName, SkullUtils.DEFAULT_SIGNATURE, SkullUtils.DEFAULT_VALUE);
    }

}
