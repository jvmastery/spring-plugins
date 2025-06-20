package cn.jvmaster.core.util;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

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

    /**
     * 获取私钥
     * @return 当前的私钥对象
     */
    public static PrivateKey getPrivateKey(String key) {
        AssertUtils.notEmpty(key, "私钥不能为空");

        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取公钥
     * @return 当前的公钥对象
     */
    public static PublicKey getPublicKey(String key) {
        AssertUtils.notEmpty(key, "公钥不能为空");

        try {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密字符串
     * @param encryptedString   待加密字符串
     * @param publicKey         公钥
     */
    public static String encrypt(String encryptedString, PublicKey publicKey) {
        byte[] result = encrypt(encryptedString.getBytes(StandardCharsets.UTF_8), publicKey);
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 加密字符串
     * @param encryptedString   待加密字符串
     * @param publicKey         公钥
     */
    public static byte[] encrypt(byte[] encryptedString, PublicKey publicKey) {
        AssertUtils.notNull(publicKey, "公钥不能为空");
        AssertUtils.notEmpty(encryptedString, "待加密字符串不能为空");

        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(encryptedString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对加密字符串进行解密
     * @param encryptedString   加密字符串
     * @param privateKey        私钥
     */
    public static String decrypt(String encryptedString, PrivateKey privateKey) {
        byte[] result = decrypt(Base64.getDecoder().decode(encryptedString), privateKey);
        return new String( result, StandardCharsets.UTF_8);
    }

    /**
     * 对加密字符串进行解密
     * @param encryptedString   加密字符串
     * @param privateKey        私钥
     */
    public static byte[] decrypt(byte[] encryptedString, PrivateKey privateKey) {
        AssertUtils.notNull(privateKey, "私钥不能为空");
        AssertUtils.notEmpty(encryptedString, "待解密字符串不能为空");

        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
