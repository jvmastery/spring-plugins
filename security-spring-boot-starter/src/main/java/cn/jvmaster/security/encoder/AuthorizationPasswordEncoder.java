package cn.jvmaster.security.encoder;

import cn.jvmaster.core.util.RsaUtils;
import cn.jvmaster.spring.constant.SessionVariables;
import java.security.KeyPair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 密码认证器
 * @author AI
 * @date 2024/1/29 22:12
 */
public class AuthorizationPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }

        KeyPair keyPair = (KeyPair) attributes.getRequest().getSession().getAttribute(SessionVariables.CURRENT_KEY_PAIR);
        return RsaUtils.decrypt(rawPassword.toString(), keyPair.getPrivate()).equals(encodedPassword);
    }
}
