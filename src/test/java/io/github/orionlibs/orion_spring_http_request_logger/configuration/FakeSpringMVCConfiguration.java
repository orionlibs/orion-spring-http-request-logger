package io.github.orionlibs.orion_spring_http_request_logger.configuration;

import io.github.orionlibs.orion_spring_http_request_logger.LoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages =
                {"io.github.orionlibs"})
public class FakeSpringMVCConfiguration implements WebMvcConfigurer
{
    @Bean
    public LoggingInterceptor loggingInterceptor()
    {
        return new LoggingInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
    }
}
