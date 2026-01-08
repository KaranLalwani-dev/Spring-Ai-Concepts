package com.karandev.learn_spring_ai.dto;

public record Joke(
        String text,
        String category,
        String laughScore,
        Boolean isNSFW
) {

}
