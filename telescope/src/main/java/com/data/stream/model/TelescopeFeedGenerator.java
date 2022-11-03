package com.data.stream.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TelescopeFeedGenerator {

    public static Map<String, String> generateFeed() {
        Map<String, String> feedMap = new HashMap<>();

        //pick random colors
        List<Colors> colorValues = Arrays.asList(Colors.values());
        Collections.shuffle(colorValues);
        List<Colors> colors = colorValues.subList(0, Colors.values().length - new Random().nextInt(Colors.values().length-1));
        feedMap.put("colors", StringUtils.join(colors));
        feedMap.put("flashingLights", FlashingLights.values()[new Random().nextInt(FlashingLights.values().length)].name());
        feedMap.put("path", Path.values()[new Random().nextInt(Path.values().length)].name());
        feedMap.put("size", Size.values()[new Random().nextInt(Size.values().length)].name());
        feedMap.put("velocity", Velocity.values()[new Random().nextInt(Velocity.values().length)].name());
        feedMap.put("visibleAtmosphere", VisibleAtmosphere.values()[new Random().nextInt(VisibleAtmosphere.values().length)].name());
        feedMap.put("visibleTail", VisibleTail.values()[new Random().nextInt(VisibleTail.values().length)].name());

        return feedMap;
    }
}
