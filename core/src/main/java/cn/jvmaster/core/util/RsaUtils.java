package cn.jvmaster.core.util;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * rsa加密方法
 * @author AI
 * @date 2024/10/3 9:07
 */
public class RsaUtils {

    public static final String RSA = "RSA";
    public static final int DEFAULT_KEY_SIZE = 2048;

    /**
     * 生成rsa密钥对
     * @return KeyPair
     */
    public static KeyPair generate() {
        return generate(DEFAULT_KEY_SIZE);
    }

    /**
     * 生成rsa密钥对
     * @param keySize   秘钥长度
     * @return  KeyPair
     */
    public static KeyPair generate(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(keySize);

            return keyPairGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void encrypt() {

    }

    /**
     * 对加密字符串进行解密
     * @param encryptedString   加密字符串
     * @param privateKey        私钥
     */
    public static String decrypt(String encryptedString, PrivateKey privateKey) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output= cipher.doFinal(Base64.getDecoder().decode(encryptedString));

            return new String(output, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
