package com.data.stream.config;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.Properties;

public final class ConfigurationManager {

    public static PropertiesConfiguration getApplicationConfiguration() {
        try {
            return getConfiguration( "/application.properties");
        } catch (ConfigurationException e) {
            // loading of the configuration file failed
            throw new ConfigurationLoadingException("Loading of application properties failed", e);
        }
    }

    public static PropertiesConfiguration getStreamsConfiguration() {
        try {
            return getConfiguration("/streams.properties");
        } catch (ConfigurationException e) {
            // loading of the configuration file failed
            throw new ConfigurationLoadingException("Loading of streams properties failed", e);
        }
    }

    public static Properties getStreamConfigurationProperties() {
        PropertiesConfiguration propertiesConfiguration = getStreamsConfiguration();
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        compositeConfiguration.addConfiguration(propertiesConfiguration);
        return ConfigurationConverter.getProperties(compositeConfiguration);
    }

    private static PropertiesConfiguration getConfiguration(String url) throws ConfigurationException {
        return new Configurations().propertiesBuilder()
                .configure(new Parameters().properties()
                        .setURL(ConfigurationManager.class.getResource(url))
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(',')))
                .getConfiguration();
    }
}
