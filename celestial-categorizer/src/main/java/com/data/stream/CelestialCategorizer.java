package com.data.stream;

import com.data.stream.config.ConfigurationManager;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CelestialCategorizer {

    static String TELESCOPE_FEED_TOPIC;
    static String METADATA_TOPIC;

    static String PLANETS_TOPIC;
    static String STARS_TOPIC;
    static String ASTEROIDS_TOPIC;

    private static final Logger logger = LogManager.getRootLogger();

    static void initApp() {
        PropertiesConfiguration properties = ConfigurationManager.getApplicationConfiguration();
        TELESCOPE_FEED_TOPIC = properties.getString("TELESCOPE_FEED_TOPIC");
        METADATA_TOPIC = properties.getString("METADATA_TOPIC");
        PLANETS_TOPIC = properties.getString("PLANETS_TOPIC");
        STARS_TOPIC = properties.getString("STARS_TOPIC");
        ASTEROIDS_TOPIC = properties.getString("ASTEROIDS_TOPIC");
    }

    static Properties initStream() {
        Properties propsStreams = ConfigurationManager.getStreamConfigurationProperties();
        propsStreams.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        propsStreams.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return propsStreams;
    }

    public static void main(String[] args) {
        logger.info("Starting Categorizer application");

        logger.info("Configuration initialization");
        initApp();

        logger.info("Setting up Kafka Streams");
        Properties properties = initStream();

        logger.info("Building topology");
        final StreamsBuilder builder = new StreamsBuilder();

        final GlobalKTable<String, String> metadataGlobalTable = builder.globalTable(METADATA_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));

        final KStream<String, String> dataStream = builder.stream(TELESCOPE_FEED_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));

        // ----------------- inputTopic and metadata join and output topic Upload ----------------- //
        dataStream
                .join(metadataGlobalTable,
                        (streamKey, streamValue) -> {
                            String keyValue = getJoinKeyValue(streamValue);
                            logger.debug("Mapping value  as key for join", keyValue);
                            return keyValue;
                        },
                        (streamValue, globalTableValue) -> {
                            logger.debug("Join value result: {}", streamValue);
                            String newValue = streamValue.concat(globalTableValue);
                            return newValue;
                        })
                .split()
                .branch((key, value) -> value.contains("PLANETS"), Branched.withConsumer(ks -> ks.to(PLANETS_TOPIC)))
                .branch((key, value) -> value.contains("STARS"), Branched.withConsumer(ks -> ks.to(STARS_TOPIC)))
                .branch((key, value) -> value.contains("ASTERIODS"), Branched.withConsumer(ks -> ks.to(ASTEROIDS_TOPIC)));
        ;
        Topology topology = builder.build();

        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);

        logger.info("Starting stream processing");
        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping streaming application");
            kafkaStreams.close();
        })); //for graceful shutdowns
    }

    private static String getJoinKeyValue(String streamValue) {
        String value = streamValue.trim()
                .replaceAll(" ","")
                .replaceAll("\\{", "")
                .substring(0, streamValue.indexOf("colors=")-7);
        Map<String, String> map = Stream.of(value.split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
        String key = map.get("velocity") + "_" + map.get("flashingLights") + "_" + (map.get("path"));
        return key;
    }
}
