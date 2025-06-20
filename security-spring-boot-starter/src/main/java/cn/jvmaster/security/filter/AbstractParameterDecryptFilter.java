package cn.jvmaster.security.filter;

import cn.jvmaster.core.util.AesUtils;
import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.security.util.ServletUtils;
import cn.jvmaster.spring.wrapper.DecryptHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 参数解密过滤器，对加密的参数进行统一解密处理
 * @author AI
 * @date 2025/6/18 17:35
 * @version 1.0
**/
public abstract class AbstractParameterDecryptFilter extends OncePerRequestFilter {

    /**
     * 构建解密request对象
     * @param request       request
     * @param response      response
     * @param filterChain   chain
     * @param aesKey        aes加密秘钥
     * @param aesIv         aes加密偏移量
     */
    @SuppressWarnings("unchecked")
    public void buildDecryptHttpServletRequestWrapper(HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain,
                    byte[] aesKey,
                    byte[] aesIv) throws ServletException, IOException {
        if (aesKey == null || aesIv == null) {
            // 没有加密参数，也不需要进行处理
            filterChain.doFilter(request, response);
            return;
        }

        // 约定：只有设置了请求头secs-a为1，则表示使用参数加密功能
        boolean useEncrypt = StringUtils.equals("1", request.getHeader("secs-a"));
        if (!useEncrypt) {
            // 使用请求参数加密功能
            filterChain.doFilter(request, response);
            return;
        }

        /*
          1、如果是form 提交，则从参数中获取加密数据
          2、如果是json提交，则从body中获取加密数据
          3、如果是get请求，则不需要额外处理
         */
        String encryptData;
        if (request.getHeader("content-type").contains("application/json")) {
            // JSON 请求体
            encryptData = ServletUtils.readRequestBody(request);
        } else {
            encryptData = request.getParameter(DecryptHttpServletRequestWrapper.ENCRYPTED_PARAMS);
        }

        if (StringUtils.isEmpty(encryptData)) {
            // 没有加密参数，不需要额外进行处理
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest wrappedRequest = new DecryptHttpServletRequestWrapper(request,
            StringUtils.parseStrToObj(AesUtils.decryptAes(StringUtils.parseHex(encryptData), aesKey, aesIv), Map.class));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(wrappedRequest));

        filterChain.doFilter(wrappedRequest, response);
    }

}
