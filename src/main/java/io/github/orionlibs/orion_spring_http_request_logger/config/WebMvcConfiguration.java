package io.github.orionlibs.orion_spring_http_request_logger.config;

import io.github.orionlibs.orion_spring_http_request_logger.LoggingInterceptor;
import java.io.IOException;
import java.util.logging.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC configurator. It loads the logger and the features configuration and
 * registers the {@link LoggingInterceptor} with the Spring MVC registry.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer
{
    private final Environment springEnv;
    private final LoggingInterceptor loggingInterceptor;
    private final OrionConfiguration featureConfiguration;


    @Autowired
    public WebMvcConfiguration(final Environment springEnv, LoggingInterceptor loggingInterceptor) throws IOException
    {
        this.springEnv = springEnv;
        this.loggingInterceptor = loggingInterceptor;
        this.featureConfiguration = OrionConfiguration.loadFeatureConfiguration(springEnv);
        loadLoggerConfiguration();
        ConfigurationService.registerConfiguration(featureConfiguration);
    }


    private void loadLoggerConfiguration() throws IOException
    {
        LogManager.getLogManager().readConfiguration(OrionConfiguration.loadLoggerConfigurationAndGet(springEnv).getAsInputStream());
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        boolean enableInterceptor = Boolean.parseBoolean(featureConfiguration.getProperty("orionlibs.orion_spring_http_request_logger.interceptor.enabled"));
        if(enableInterceptor)
        {
            registry.addInterceptor(loggingInterceptor);
        }
    }
}