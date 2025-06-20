package cn.jvmaster.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密方法
 * @author AI
 * @date 2025/5/30 17:09
 * @version 1.0
**/
public class Md5Utils {

    /**
     * md5加密
     * @param input 带加密字符串
     * @return  md5加密字符串
     */
    public static String md5(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }

        try {
            // 创建 MD5 摘要器
            final MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算摘要
            byte[] digest = md.digest(input.getBytes());
            return StringUtils.toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
