package cn.jvmaster.security.util;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.constant.Variables;
import cn.jvmaster.core.domain.LocalCache;
import cn.jvmaster.core.exception.SystemException;
import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.redis.constant.CacheConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 *  servlet相关请求工具类
 * @author         艾虎
 * @date      2018/7/4 9:46
 * @version       1.0
 */
public class ServletUtils {
    private static final String UNKNOWN = "unknown";
    private static final LocalCache<AntPathRequestMatcher> MATCHER_LOCAL_CACHE = new LocalCache<>();

    /**
     *  输出纯文本内容
     * @param response  reponse
     * @param message   输出内容
     */
    public static void outText(HttpServletResponse response, String message) {
        outMsg(response, "text/plain", StandardCharsets.UTF_8, message);
    }

    /**
     *  输出json内容
     * @param response  reponse
     * @param message   输出内容
     */
    public static void outJson(HttpServletResponse response, String message) {
        outMsg(response, "", StandardCharsets.UTF_8, message);
    }

    /**
     *  输出html内容
     * @param response  reponse
     * @param message   输出内容
     */
    public static void outHtml(HttpServletResponse response, String message) {
        outMsg(response, "text/html", StandardCharsets.UTF_8, message);
    }

    /**
     *  输出数据
     * @param response      reponse
     * @param contentType   输出数据格式
     * @param encoding      输出数据字符编码
     * @param message       输出数据内容
     */
    public static void outMsg(HttpServletResponse response, String contentType, Charset encoding, String message) {
        response.setContentType(contentType);
        response.setCharacterEncoding(encoding.name());

        try (PrintWriter writer = response.getWriter()) {
            writer.write(message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  获取请求参数集合
     *      如果有数组，则将数组切换成字符串，以，号隔开
     * @param request   request
     */
    public static Map<String, Object> getRequestParams(HttpServletRequest request) {
        Map<String, Object> paramSting = new HashMap<>();
        if(request == null) {
            return paramSting;
        }

        Map<String, String[]> params = request.getParameterMap();
        if(params == null) {
            return paramSting;
        }

        for(Map.Entry<String, String[]> item : params.entrySet()) {
            if(item.getValue().length == 1) {
                paramSting.put(item.getKey(), item.getValue()[0]);
            } else {
                // 按照内容升序排序，以，号生产字符串
                Arrays.sort(item.getValue(), String::compareToIgnoreCase);
                paramSting.put(item.getKey(), StringUtils.join(item.getValue(), Variables.WORD_SEPARATOR));
            }
        }

        return paramSting;
    }

    /**
     *  获取访问ip
     * @param request request
     * @return ip地址
     */
    public static String getRemoteAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (isLocalHost(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException ignored) {
                }

            }
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        //"***.***.***.***".length() = 15
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(Variables.WORD_SEPARATOR) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(Variables.WORD_SEPARATOR));
            }
        }

        return ipAddress == null ? "" : ipAddress;
    }

    /**
     * 判断是本机ip
     * @param ip  ip地址
     * @return 是否是本机
     */
    public static boolean isLocalHost(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }

    /**
     *  读取请求体内容
     * @param request request
     * @return 请求体内容
     */
    public static String readRequestBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = request.getReader();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            throw new SystemException(Code.ERROR, e.getMessage(), e);
        }

        return sb.toString();
    }

    /**
     *  获取请求头
     * @param request request
     * @return  请求头
     */
    public static Map<String, String> getRequestHeader(HttpServletRequest request) {
        Enumeration<String> headers =  request.getHeaderNames();
        Map<String, String> headersMap = new HashMap<>();
        if(headers == null) {
            return headersMap;
        }

        while (headers.hasMoreElements()) {
            String nextHeader = headers.nextElement();

            headersMap.put(nextHeader, request.getHeader(nextHeader));
        }

        return headersMap;
    }

    /**
     * 判断请求是否匹配
     * @param pattern   请求表达式
     * @param method    请求方法
     * @param request   request
     * @return  是否匹配
     */
    public static boolean matches(String pattern, String method, HttpServletRequest request) {
        if (StringUtils.isEmpty(pattern)) {
            return false;
        }

        AntPathRequestMatcher matcher = MATCHER_LOCAL_CACHE.get(pattern + CacheConstant.SEPARATOR + method, () -> new AntPathRequestMatcher(pattern, method));
        return matcher.matches(request);
    }
}
