package io.github.orionlibs.orion_spring_http_request_logger;

import io.github.orionlibs.orion_spring_http_request_logger.config.ConfigurationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service whose job is to build the components of the HTTP request log
 */
public class LogService
{
    /**
     * It logs this HTTP request's data before it is handled by the controller framework.
     * It builds the components of the log message based on the configuration and
     * if there are components, it logs the message.
     * @param request
     * @return the log message optional
     */
    static Optional<String> buildLogForPrehandle(HttpServletRequest request)
    {
        String logRecordPattern = ConfigurationService.getProp("orionlibs.orion_spring_http_request_logger.log.pattern.for.each.log.record.element");
        String ipAddressLog = getIpAddressLog(request, logRecordPattern);
        String httpMethodLog = getHttpMethodLog(request);
        String uriLog = getUriLog(request);
        String queryParametersLog = getURIQueryString(request);
        List<String> logElements = new ArrayList<>();
        if(ipAddressLog != null)
        {
            logElements.add(ipAddressLog);
        }
        if(httpMethodLog != null && uriLog != null)
        {
            logElements.add(String.format(logRecordPattern, "URI", httpMethodLog + " " + buildCompleteURILog(uriLog, queryParametersLog)));
        }
        else if(httpMethodLog == null && uriLog != null)
        {
            logElements.add(String.format(logRecordPattern, "URI", buildCompleteURILog(uriLog, queryParametersLog)));
        }
        else if(httpMethodLog != null && uriLog == null)
        {
            logElements.add(String.format(logRecordPattern, "URI", httpMethodLog));
        }
        if(!logElements.isEmpty())
        {
            return Optional.<String>of(String.join(", ", logElements.toArray(new String[0])));
        }
        return Optional.empty();
    }


    private static String getURIQueryString(HttpServletRequest request)
    {
        String queryParametersLog = null;
        if(ConfigurationService.getBooleanProp("orionlibs.orion_spring_http_request_logger.log.uri.query.params.enabled"))
        {
            queryParametersLog = "?" + request.getQueryString();
        }
        return queryParametersLog;
    }


    private static String getUriLog(HttpServletRequest request)
    {
        String uriLog = null;
        if(ConfigurationService.getBooleanProp("orionlibs.orion_spring_http_request_logger.log.uri.enabled"))
        {
            String uriPatternExpression = ConfigurationService.getProp("orionlibs.orion_spring_http_request_logger.log.uris.logged.pattern");
            if("*".equals(uriPatternExpression))
            {
                uriLog = request.getRequestURI();
            }
            else
            {
                Pattern uriPattern = Pattern.compile(uriPatternExpression);
                Matcher matcher = uriPattern.matcher(request.getRequestURI());
                if(matcher.matches())
                {
                    uriLog = request.getRequestURI();
                }
            }
        }
        return uriLog;
    }


    private static String getHttpMethodLog(HttpServletRequest request)
    {
        String httpMethodLog = null;
        if(ConfigurationService.getBooleanProp("orionlibs.orion_spring_http_request_logger.log.http.method.enabled"))
        {
            String httpMethodsToLogPattern = ConfigurationService.getProp("orionlibs.orion_spring_http_request_logger.log.http.methods.logged");
            String[] httpMethodsToLog = httpMethodsToLogPattern.split(",");
            if("*".equals(httpMethodsToLogPattern)
                            || Arrays.stream(httpMethodsToLog).anyMatch(m -> m.equalsIgnoreCase(request.getMethod())))
            {
                httpMethodLog = request.getMethod();
            }
        }
        return httpMethodLog;
    }


    private static String getIpAddressLog(HttpServletRequest request, String logRecordPattern)
    {
        String ipAddressLog = null;
        if(ConfigurationService.getBooleanProp("orionlibs.orion_spring_http_request_logger.log.ip.address.enabled"))
        {
            ipAddressLog = String.format(logRecordPattern, "IP", request.getRemoteAddr());
        }
        return ipAddressLog;
    }


    private static String buildCompleteURILog(String uriLog, String queryParametersLog)
    {
        String completeURILog = uriLog;
        if(queryParametersLog != null)
        {
            completeURILog += queryParametersLog;
        }
        return completeURILog;
    }
}
