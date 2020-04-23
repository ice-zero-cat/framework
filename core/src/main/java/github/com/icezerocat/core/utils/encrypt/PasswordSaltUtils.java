package github.com.icezerocat.core.utils.encrypt;

import java.util.UUID;

/**
 * 密码工具类
 *
 * @author Louis
 * @date Sep 1, 2018
 */
public class PasswordSaltUtils {

    /**
     * 匹配密码
     *
     * @param salt    盐
     * @param rawPass 明文
     * @param encPass 密文
     * @return boolean
     */
    public static boolean matches(String salt, String rawPass, String encPass) {
        return new PasswordSaltEncoder(salt).matches(encPass, rawPass);
    }

    /**
     * 明文密码加密
     *
     * @param rawPass 明文
     * @param salt    盐
     * @return 密文
     */
    public static String encode(String rawPass, String salt) {
        return new PasswordSaltEncoder(salt).encode(rawPass);
    }

    /**
     * 获取加密盐
     *
     * @return 获取加密盐
     */
    public static String getSalt() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
    }
}
