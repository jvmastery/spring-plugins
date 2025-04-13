package cn.jvmaster.core.util;

import cn.jvmaster.core.constant.Algorithm;
import cn.jvmaster.core.exception.SystemException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具类
 * @author AI
 * @date 2024/12/31 9:05
 * @version 1.0
**/
public class CryptoUtils {

    /**
     * 构建RSA秘钥对
     * @return  rsa秘钥对
     */
    public static KeyPair generateRsaKey() {
        return generateKeyPair(Algorithm.RSA.getAlgorithm(), 1024);
    }

    /**
     * 生成SecretKey
     * @param algorithm 加密算法
     * @param keySize   秘钥长度
     * @return  SecretKey
     */
    public static SecretKey generateKey(String algorithm, int keySize) {
        return generateKey(algorithm, keySize, null);
    }

    /**
     * 生成SecretKey
     * @param algorithm 加密算法
     * @param keySize   秘钥长度
     * @param secureRandom  随机数生成
     * @return  SecretKey
     */
    public static SecretKey generateKey(String algorithm, int keySize, SecureRandom secureRandom) {
        return generateKey(algorithm, null, keySize, secureRandom);
    }

    /**
     * 生成SecretKey
     * @param algorithm 加密算法
     * @param provider Provider
     * @param keySize   秘钥长度
     * @param secureRandom  随机数生成
     * @return  SecretKey
     */
    public static SecretKey generateKey(String algorithm, Provider provider, int keySize, SecureRandom secureRandom) {
        try {
            KeyGenerator keyGenerator = provider == null ? KeyGenerator.getInstance(algorithm) : KeyGenerator.getInstance(algorithm, provider);
            if (secureRandom != null) {
                keyGenerator.init(keySize, secureRandom);
            } else {
                keyGenerator.init(keySize);
            }

            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * 生成KeyPair
     * @param algorithm 算法名称
     * @param keySize   秘钥长度
     * @return KeyPair
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize) {
        return generateKeyPair(algorithm, keySize, null);
    }

    /**
     * 生成KeyPair
     * @param algorithm 算法名称
     * @param keySize   秘钥长度
     * @param secureRandom  随机数生成
     * @return KeyPair
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom secureRandom) {
        return generateKeyPair(algorithm, null, keySize, secureRandom);
    }

    /**
     * 生成KeyPair
     * @param algorithm 算法名称
     * @param provider  Provider
     * @param keySize   秘钥长度
     * @param secureRandom  随机数生成
     * @return KeyPair
     */
    public static KeyPair generateKeyPair(String algorithm, Provider provider, int keySize, SecureRandom secureRandom) {
        try {
            KeyPairGenerator keyPairGenerator =getKeyPairGenerator(algorithm, provider);
            if (secureRandom == null) {
                keyPairGenerator.initialize(keySize);
            } else {
                keyPairGenerator.initialize(keySize, secureRandom);
            }

            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * 获取KeyPairGenerator
     * @param algorithm 加密算法
     * @param provider  provider
     * @return KeyPairGenerator
     */
    public static KeyPairGenerator getKeyPairGenerator(String algorithm, Provider provider) {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = (null == provider)
                ? KeyPairGenerator.getInstance(algorithm)
                : KeyPairGenerator.getInstance(algorithm, provider);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException(e.getMessage(), e);
        }
        return keyPairGen;
    }

    public static String calculateHMAC(String data, String key) throws Exception {
        // 指定使用的 HMAC 算法，例如 HmacSHA1 或 HmacSHA256
        String algorithm = "HmacSHA1";
        // 获取 Mac 实例
        Mac mac = Mac.getInstance(algorithm);
        // 构造密钥
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
        // 初始化 Mac
        mac.init(secretKeySpec);
        // 执行 HMAC 计算
        byte[] hmacBytes = mac.doFinal(data.getBytes("UTF-8"));
        // 使用 Base64 编码
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static void main(String[] args) throws Exception {
//        KeyPair secretKey = generateRsaKey();
//        System.out.println( secretKey.getPrivate());
//        System.out.println(secretKey.getPublic());

        System.out.println(calculateHMAC("eyJzY29wZSI6ImJsb2ctc3ByaW5naHViLWNuIiwiZGVhZGxpbmUiOjE0NTE0OTEyMDB9", "WdqFUPOs3xyHTAZCvIaAUZmRQAVRBTNOxELedt5d"));
    }
}
