package cn.jvmaster.security.filter;

import cn.jvmaster.core.util.AesUtils;
import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.spring.domain.RequestAesKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对请求参数进行解密操作
 * @author AI
 * @date 2025/6/18 10:32
 * @version 1.0
**/
public class ParameterDecryptFilter extends AbstractParameterDecryptFilter {
    public static final String SECS_K = "secs-k";
    public static final String SECS_V = "secs-v";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String key = request.getHeader(SECS_K);
        String iv = request.getHeader(SECS_V);
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestAesKey requestAesKey = new RequestAesKey(AesUtils.DECODER.decode(StringUtils.parseHex(key)), AesUtils.DECODER.decode(StringUtils.parseHex(iv)));
        request.setAttribute(RequestAesKey.class.getName(), requestAesKey);
        buildDecryptHttpServletRequestWrapper(request, response, filterChain, requestAesKey.key(), requestAesKey.iv());
    }
}
