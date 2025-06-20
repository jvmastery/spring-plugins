package cn.jvmaster.spring.wrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 加密请求参数
 * @author AI
 * @date 2025/6/18 17:08
 * @version 1.0
**/
public class DecryptHttpServletRequestWrapper extends HttpServletRequestWrapper {
    // 定义加密参数字段
    public static final String ENCRYPTED_PARAMS = "data";
    private final Map<String, String[]> customParams = new HashMap<>();

    public DecryptHttpServletRequestWrapper(HttpServletRequest request, Map<String, Object> decryptedParams) {
        super(request);
        if (decryptedParams != null) {
            decryptedParams.forEach((k, v) -> this.customParams.put(k, new String[]{v.toString()}));
        }
    }

    @Override
    public String getParameter(String name) {
        String[] values = customParams.get(name);
        if (values != null) {
            return values[0];
        }

        if (name.equals(ENCRYPTED_PARAMS)) {
            return null;
        }

        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> combined = new HashMap<>(super.getParameterMap());
        combined.remove(ENCRYPTED_PARAMS);
        combined.putAll(customParams);

        return combined;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Set<String> names = new HashSet<>(super.getParameterMap().keySet());
        names.remove(ENCRYPTED_PARAMS);

        names.addAll(customParams.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public String[] getParameterValues(String name) {
        return customParams.containsKey(name) ? customParams.get(name) : super.getParameterValues(name);
    }
}
