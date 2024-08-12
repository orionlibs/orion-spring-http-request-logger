package io.github.orionlibs.orion_spring_http_request_logger;

import io.github.orionlibs.orion_spring_http_request_logger.config.ConfigurationService;
import java.util.logging.Handler;
import java.util.logging.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring MVC interceptor whose job is to log HTTP requests
 */
@NoArgsConstructor
public class LoggingInterceptor implements HandlerInterceptor
{
    private final static Logger log;
    private Runnable callback;

    static
    {
        log = Logger.getLogger(LoggingInterceptor.class.getName());
    }

    /**
     * it accepts a {@link Runnable} object which executes right after logging
     * @param callback
     */
    public LoggingInterceptor(Runnable callback)
    {
        this.callback = callback;
    }


    /**
     * It logs this HTTP request's data before it is handled by the controller framework.
     * It builds the components of the log message based on the configuration and
     * if there are components, it logs the message.
     * @param request HTTP request
     * @param response HTTP response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        LogService.buildLogForPrehandle(request).ifPresent(x -> log.info(x));
        if(ConfigurationService.getBooleanProp("orionlibs.orion_spring_http_request_logger.log.request.processing.duration.enabled"))
        {
            long startTime = System.nanoTime();
            request.setAttribute("orionlibs.orion_spring_http_request_logger", startTime);
        }
        if(callback != null)
        {
            callback.run();
        }
        return true;
    }


    /**
     * It logs this HTTP request's data after it is handled and before the response is built.
     * @param request HTTP request
     * @param response HTTP response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                    @Nullable ModelAndView modelAndView) throws Exception
    {
        if(ConfigurationService.getBooleanProp("orionlibs.orion_spring_http_request_logger.log.request.processing.duration.enabled"))
        {
            long startTime = (Long)request.getAttribute("orionlibs.orion_spring_http_request_logger");
            long endTime = System.nanoTime();
            long executeTime = endTime - startTime;
            log.info("Handler: " + handler + " took " + executeTime + "ns");
        }
    }


    /**
     * It logs this HTTP request's data after it is handled and after the response is built.
     * @param request HTTP request
     * @param response HTTP response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                    @Nullable Exception ex) throws Exception
    {
    }


    static void addLogHandler(Handler handler)
    {
        log.addHandler(handler);
    }


    static void removeLogHandler(Handler handler)
    {
        log.removeHandler(handler);
    }
}
