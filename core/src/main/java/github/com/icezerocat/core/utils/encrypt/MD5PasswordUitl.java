package github.com.icezerocat.core.utils.encrypt;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * ProjectName: [core-master]
 * Package:     [com.zero.core.utils.MD5PasswordUitl]
 * Description: md5密码加密工具类
 * CreateDate:  2020/3/30 0:17
 *
 * @author 0.0.0
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("unused")
public class MD5PasswordUitl {
    private static final Integer SALT_LENGTH = 12;

    /**
     * 获得加密后的16进制形式口令
     *
     * @param password 密码
     * @return 加密密码
     */
    public static String getEncryptedPwd(String password) {
        //声明加密后的口令数组变量
        byte[] pwd;
        //随机数生成器
        SecureRandom random = new SecureRandom();
        //声明盐数组变量
        byte[] salt = new byte[SALT_LENGTH];
        //将随机数放入盐变量中
        random.nextBytes(salt);

        //声明消息摘要对象
        MessageDigest md = null;
        //创建消息摘要
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("获取算法异常：{}", e.getMessage());
            e.printStackTrace();
        }
        //将盐数据传入消息摘要对象
        Objects.requireNonNull(md).update(salt);
        //将口令的数据传给消息摘要对象
        md.update(password.getBytes(StandardCharsets.UTF_8));
        //获得消息摘要的字节数组
        byte[] digest = md.digest();

        //因为要在口令的字节数组中存放盐，所以加上盐的字节长度
        pwd = new byte[digest.length + SALT_LENGTH];
        //将盐的字节拷贝到生成的加密口令字节数组的前12个字节，以便在验证口令时取出盐
        System.arraycopy(salt, 0, pwd, 0, SALT_LENGTH);
        //将消息摘要拷贝到加密口令字节数组从第13个字节开始的字节
        System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);
        //将字节数组格式加密后的口令转化为16进制字符串格式的口令
        return EncryptUtil.parseByte2HexStr(pwd);
    }

    /**
     * 验证口令是否合法
     *
     * @param sourcePassword    原密码
     * @param encryptedPassword 加密密码
     * @return 校验结果
     */
    public static boolean validPassword(String sourcePassword, String encryptedPassword) {
        //将16进制字符串格式口令转换成字节数组
        byte[] pwdInDb = EncryptUtil.parseHexStr2Byte(encryptedPassword);
        //声明盐变量
        byte[] salt = new byte[SALT_LENGTH];
        //将盐从数据库中保存的口令字节数组中提取出来
        System.arraycopy(Objects.requireNonNull(pwdInDb), 0, salt, 0, SALT_LENGTH);
        //创建消息摘要对象
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("获取算法异常：{}", e.getMessage());
            e.printStackTrace();
        }
        //将盐数据传入消息摘要对象
        Objects.requireNonNull(md).update(salt);
        //将口令的数据传给消息摘要对象
        md.update(sourcePassword.getBytes(StandardCharsets.UTF_8));
        //生成输入口令的消息摘要
        byte[] digest = md.digest();
        //声明一个保存数据库中口令消息摘要的变量
        byte[] digestInDb = new byte[pwdInDb.length - SALT_LENGTH];
        //取得数据库中口令的消息摘要
        System.arraycopy(pwdInDb, SALT_LENGTH, digestInDb, 0, digestInDb.length);
        //比较根据输入口令生成的消息摘要和数据库中消息摘要是否相同
        //口令正确返回口令匹配消息
        //口令不正确返回口令不匹配消息
        return Arrays.equals(digest, digestInDb);
    }
}
