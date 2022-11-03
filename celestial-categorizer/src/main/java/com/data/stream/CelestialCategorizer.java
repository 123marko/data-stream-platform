package com.data.stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class CelestialCategorizer {

    static String INPUT_TOPIC;
    static String METADATA_TOPIC;

    static String PLANETS_TOPIC;
    static String STARS_TOPIC;
    static String ASTEROIDS_TOPIC;

    private static final Logger logger = LogManager.getRootLogger();

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

            }
        }, 0, 3000);
    }
}
