package com.github.wekaito.backend.websocket;

import com.github.wekaito.backend.DigivolveCondition;

import java.util.List;

public record GameCard (
        String uniqueCardNumber,
        String name,
        String imgUrl,
        String cardType,
        List<String> color,
        String attribute,
        String cardNumber,
        List<DigivolveCondition> digivolveConditions,
        String specialDigivolve,
        String stage,
        List<String> digiType,
        Integer dp,
        Integer playCost,
        Integer level,
        String mainEffect,
        String inheritedEffect,
        String aceEffect,
        String burstDigivolve,
        String digiXros,
        String dnaDigivolve,
        String securityEffect,
        String restriction_en,
        String restriction_jp,
        String illustrator,
        Boolean isTilted,
        String id

) {

}
