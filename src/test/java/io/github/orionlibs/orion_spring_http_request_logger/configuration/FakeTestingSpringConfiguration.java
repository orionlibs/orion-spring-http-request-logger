package io.github.orionlibs.orion_spring_http_request_logger.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

public class FakeTestingSpringConfiguration
{
    @Configuration
    @Import(
                    {FakeSpringMVCConfiguration.class})
    public static class FakeConfiguration
    {
    }
}