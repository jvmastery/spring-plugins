package cn.jvmaster.spring.domain;

/**
 * 请求参数加密密钥信息
 * @author AI
 * @date 2025/6/19 9:54
 * @version 1.0
**/
public record RequestAesKey(byte[] key, byte[] iv) {

}
