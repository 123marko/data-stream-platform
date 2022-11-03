package com.data.stream;

import com.data.stream.config.ConfigurationManager;
import com.data.stream.model.TelescopeFeedGenerator;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

public class TelescopeProducer {

    static String TARGET_TOPIC;

    private static final Logger logger = LogManager.getRootLogger();

    public static void main(String[] args) {
        logger.info("Starting Telescope");
        PropertiesConfiguration applicationProperties = ConfigurationManager.getApplicationConfiguration();
        TARGET_TOPIC = applicationProperties.getString("TARGET_TOPIC");

        Properties properties = ConfigurationManager.getProducerConfigurationProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        logger.info("Loaded configuration: " + properties);
        Producer<Long, String> producer = new KafkaProducer<>(properties);

        logger.info("Starting producing");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Long key = System.currentTimeMillis();
                String value = StringUtils.join(TelescopeFeedGenerator.generateFeed());
                Future<RecordMetadata> sendTask = producer.send(new ProducerRecord<>(TARGET_TOPIC, key, value));
                while (!sendTask.isDone()) {
                    logger.info("Waiting for task completion ");
                }
                logger.info("Message sent: " + key + ": " + value);
            }
        }, 0, 3000);
    }
}
