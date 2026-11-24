package com.hardy.fawatir.utils;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import static nl.basjes.parse.useragent.UserAgent.*;

public class RequestUtils {

    public static final String X_FORWARDED_FOR_HEADER = "x-forwarded-for";
    public static final String USER_AGENT_HEADER = "User-Agent";

    public static String getIpAddress(HttpServletRequest request) {
        String ip = "UnKnown IP Address";

        if(request != null){
            ip = request.getHeader(X_FORWARDED_FOR_HEADER);
            if (ip == null || ip.isEmpty()){
                ip = request.getRemoteAddr();
            }
        }
        return ip;
    }

    public static String getDevice(HttpServletRequest request) {
        UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();

        UserAgent userAgent = userAgentAnalyzer.parse(request.getHeader(USER_AGENT_HEADER));
        System.out.println(userAgent);

        return userAgent.getValue(OPERATING_SYSTEM_NAME) + " - " + userAgent.getValue(AGENT_NAME ) + " - " + userAgent.getValue(DEVICE_NAME);
    }
}
