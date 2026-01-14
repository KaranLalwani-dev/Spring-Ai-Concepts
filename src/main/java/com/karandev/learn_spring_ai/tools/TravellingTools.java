package com.karandev.learn_spring_ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TravellingTools {

    @Tool(description = "Get the weather of a city")
    public String getWeather(@ToolParam(description = "City name for which to get the weather information") String city) {
        return switch(city) {
            case "Delhi" -> "Sunny 26 degrees";
            case "London" -> "Cloudy 15 degrees";
            default -> "Cannot identify teh city";
        };
    }


}
