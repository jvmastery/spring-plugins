package cn.jvmaster.core.util;

import cn.jvmaster.core.constant.Code;
import cn.jvmaster.core.exception.SystemException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * aes加密解密操作
 * @author AI
 * @date 2025/6/18 10:35
 * @version 1.0
**/
public class AesUtils {
    public final static Base64.Decoder DECODER = Base64.getDecoder();

    public static String decryptAes(String base64CipherText, String key, String iv)  {
        return decryptAes(base64CipherText, DECODER.decode(key), DECODER.decode(iv));
    }

    /**
     * aes解密操作
     * @param base64CipherText 密文
     * @param key   key
     * @param iv    偏移
     */
    public static String decryptAes(String base64CipherText, byte[] key, byte[] iv) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 对应 Pkcs7
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] encryptedBytes = Base64.getDecoder().decode(base64CipherText);
            byte[] original = cipher.doFinal(encryptedBytes);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SystemException(Code.ERROR, e.getMessage(), e);
        }
    }

    /**
     * 加密算法
     * @param base64CipherText 加密对象
     * @param key       秘钥
     * @param iv        偏移量
     */
    public static String encryptAes(String base64CipherText, String key, String iv)  {
        return encryptAes(base64CipherText, DECODER.decode(key), DECODER.decode(iv));
    }

    /**
     * 加密算法
     * @param plainText 加密对象
     * @param key       秘钥
     * @param iv        偏移量
     */
    public static String encryptAes(String plainText, byte[] key, byte[] iv) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new SystemException(Code.ERROR, e.getMessage(), e);
        }
    }
}
