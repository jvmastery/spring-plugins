package cn.jvmaster.spring.constant;

/**
 * session 中变量名称
 * @author AI
 * @date 2025/4/14 10:51
 * @version 1.0
**/ 
public interface SessionVariables {

    /**
     * 当前使用的公钥、私钥对，存放在session中
     */
    String CURRENT_KEY_PAIR = "CURRENT_KEY_PAIR";

    /**
     * 验证码
     */
    String CAPTCHA_CODE = "captchaCode";
}
