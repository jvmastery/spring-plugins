package cn.jvmaster.core.constant;

/**
 * 加密算法
 * @author AI
 * @date 2024/12/31 9:20
 * @version 1.0
**/
public enum Algorithm {
    /**
     * rsa
     */
    RSA("RSA"),
    /**
     * RSA算法，此算法用了默认补位方式为RSA/ECB/PKCS1Padding
     */
    RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
    ;

    /**
     * 算法名称
     */
    private final String algorithm;

    Algorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
