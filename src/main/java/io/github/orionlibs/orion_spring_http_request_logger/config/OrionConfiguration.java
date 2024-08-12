package io.github.orionlibs.orion_spring_http_request_logger.config;

import io.github.orionlibs.orion_spring_http_request_logger.LoggingInterceptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.springframework.core.env.Environment;

/**
 * Properties-based class that holds configuration.
 */
public class OrionConfiguration extends Properties
{
    /**
     * The location of the configuration file that has the logging configuration only e.g. log levels.
     */
    public static final String LOGGER_CONFIGURATION_FILE = "/io/github/orionlibs/orion_spring_http_request_logger/configuration/orion-logger.prop";
    /**
     * The location of the configuration file that has configuration for the features of this plugin.
     */
    public static final String FEATURE_CONFIGURATION_FILE = "/io/github/orionlibs/orion_spring_http_request_logger/configuration/feature-configuration.prop";


    public static OrionConfiguration loadLoggerConfigurationAndGet(Environment springEnv) throws IOException
    {
        OrionConfiguration loggerConfiguration = new OrionConfiguration();
        InputStream defaultConfigStream = LoggingInterceptor.class.getResourceAsStream(LOGGER_CONFIGURATION_FILE);
        try
        {
            loggerConfiguration.loadDefaultAndCustomConfiguration(defaultConfigStream, springEnv);
            return loggerConfiguration;
        }
        catch(IOException e)
        {
            throw new IOException("Could not setup logger configuration for the Orion Spring HTTP Request Logger Plugin: ", e);
        }
    }


    public static OrionConfiguration loadFeatureConfiguration(Environment springEnv) throws IOException
    {
        OrionConfiguration featureConfiguration = new OrionConfiguration();
        InputStream defaultConfigStream = LoggingInterceptor.class.getResourceAsStream(FEATURE_CONFIGURATION_FILE);
        try
        {
            featureConfiguration.loadDefaultAndCustomConfiguration(defaultConfigStream, springEnv);
            return featureConfiguration;
        }
        catch(IOException e)
        {
            throw new IOException("Could not setup feature configuration for the Orion Spring HTTP Request Logger Plugin: ", e);
        }
    }


    /**
     * It takes default configuration and custom configuration from the Spring environment.
     * For each default configuration property, it registers that one if there is no custom
     * Spring configuration for that property. Otherwise it registers the custom one.
     * @param defaultConfiguration
     * @param springEnv
     * @throws IOException if an error occurred when reading from the input stream
     */
    public void loadDefaultAndCustomConfiguration(InputStream defaultConfiguration, Environment springEnv) throws IOException
    {
        Properties tempProperties = new Properties();
        tempProperties.load(defaultConfiguration);
        Map<String, String> allProperties = new HashMap<>();
        for(Entry<Object, Object> prop : tempProperties.entrySet())
        {
            String value = springEnv.getProperty((String)prop.getKey());
            if(value == null)
            {
                value = (String)prop.getValue();
            }
            allProperties.put((String)prop.getKey(), value);
        }
        putAll(allProperties);
    }


    /**
     * It converts the configuration this object holds into an InputStream
     * @return an InputStream of the configuration this object holds
     * @throws IOException if writing this property list to the specified output stream throws an IOException
     */
    public InputStream getAsInputStream() throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        store(output, null);
        return new ByteArrayInputStream(output.toByteArray());
    }


    /**
     * remaps the given key to the given value
     * @param key
     * @param value
     */
    public void updateProp(String key, String value)
    {
        put(key, value);
    }
}
