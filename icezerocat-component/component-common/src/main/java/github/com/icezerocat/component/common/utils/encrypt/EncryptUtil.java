package github.com.icezerocat.component.common.utils.encrypt;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ProjectName: [core-master]
 * Package:     [com.zero.core.utils.EncryptUtil]
 * Description: 加密工具类
 * CreateDate:  2020/3/29 23:57
 *
 * @author 0.0.0
 * @version 1.0
 */
@SuppressWarnings("all")
public class EncryptUtil extends Md5Util {
    private static Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";
    public static final String HMACM_D5 = "HmacMD5";
    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String DES = "DES";
    public static final String AES = "AES";

    /**
     * 编码格式；默认使用uft-8
     */
    public String charset = "utf-8";
    /**
     * DES
     */
    public int keySizeDES = 0;
    /**
     * AES
     */
    public int keySizeAES = 128;

    public static EncryptUtil me;

    private EncryptUtil() {
        //单例
    }

    //双重锁
    public static EncryptUtil getInstance() {
        if (me == null) {
            synchronized (EncryptUtil.class) {
                if (me == null) {
                    me = new EncryptUtil();
                }
            }
        }
        return me;
    }

    /**
     * 使用MessageDigest进行单向加密（无密码）
     *
     * @param res       被加密的文本
     * @param algorithm 加密算法名称
     * @return 密文
     */
    private String messageDigest(String res, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] resBytes = charset == null ? res.getBytes() : res.getBytes(charset);
            return base64(md.digest(resBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用KeyGenerator进行单向/双向加密（可设密码）
     *
     * @param res       被加密的原文
     * @param algorithm 加密使用的算法名称
     * @param key       加密使用的秘钥
     * @return 密文
     */
    private String keyGeneratorMac(String res, String algorithm, String key) {
        try {
            SecretKey sk;
            if (key == null) {
                KeyGenerator kg = KeyGenerator.getInstance(algorithm);
                sk = kg.generateKey();
            } else {
                byte[] keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
                sk = new SecretKeySpec(keyBytes, algorithm);
            }
            Mac mac = Mac.getInstance(algorithm);
            mac.init(sk);
            byte[] result = mac.doFinal(res.getBytes());
            return base64(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用KeyGenerator双向加密，DES/AES，注意这里转化为字符串的时候是将2进制转为16进制格式的字符串，不是直接转，因为会出错
     *
     * @param res       加密的原文
     * @param algorithm 加密使用的算法名称
     * @param key       加密的秘钥
     * @param keysize   大小
     * @param isEncode  是否使用编码
     * @return 密文
     */
    private String keyGeneratorES(String res, String algorithm, String key, int keysize, boolean isEncode) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(algorithm);
            if (keysize == 0) {
                byte[] keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
                kg.init(new SecureRandom(keyBytes));
            } else if (key == null) {
                kg.init(keysize);
            } else {
                byte[] keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
                kg.init(keysize, new SecureRandom(keyBytes));
            }
            SecretKey sk = kg.generateKey();
            SecretKeySpec sks = new SecretKeySpec(sk.getEncoded(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            if (isEncode) {
                cipher.init(Cipher.ENCRYPT_MODE, sks);
                byte[] resBytes = charset == null ? res.getBytes() : res.getBytes(charset);
                return parseByte2HexStr(cipher.doFinal(resBytes));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, sks);
                return new String(cipher.doFinal(parseHexStr2Byte(res)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String base64(byte[] res) {
        return Base64.encode(res);
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf 二进制数组
     * @return 16进制
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return String.valueOf(sb);
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr 16进制字符串
     * @return 二进制
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 把中文字符串转换为十六进制Unicode编码字符串
     *
     * @param s 中文字符串
     * @return unicode编码
     */
    public static String stringToUnicode(String s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            if (ch > 255) {
                str.append("\\u").append(Integer.toHexString(ch));
            } else {
                str.append("\\").append(Integer.toHexString(ch));
            }
        }
        return str.toString();
    }

    /**
     * 把十六进制Unicode编码字符串转换为中文字符串, 将\u848B\u4ECB\u77F3转化成蒋介石，注意格式
     *
     * @param str eg:\u848B\u4ECB\u77F3
     * @return 蒋介石
     */
    public static String unicodeToString(String str) {
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");

        }
        return str;
    }

    /**
     * md5加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @return 密文
     */
    public String MD5(String res) {
        return messageDigest(res, MD5);
    }

    /**
     * md5加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @param key 秘钥
     * @return 密文
     */
    public String MD5(String res, String key) {
        return keyGeneratorMac(res, HMACM_D5, key);
    }

    /**
     * 使用SHA1加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @return 密文
     */
    public String SHA1(String res) {
        return messageDigest(res, SHA1);
    }

    /**
     * 使用SHA1加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @param key 秘钥
     * @return 密文
     */
    public String SHA1(String res, String key) {
        return keyGeneratorMac(res, HMAC_SHA1, key);
    }

    /**
     * 使用DES加密算法进行加密（可逆）
     *
     * @param res 需要加密的原文
     * @param key 秘钥
     * @return 密文
     */
    public String DESencode(String res, String key) {
        return keyGeneratorES(res, DES, key, keySizeDES, true);
    }

    /**
     * 对使用DES加密算法的密文进行解密（可逆）
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return 密文
     */
    public String DESdecode(String res, String key) {
        return keyGeneratorES(res, DES, key, keySizeDES, false);
    }

    /**
     * 使用AES加密算法经行加密（可逆）
     *
     * @param res 需要加密的密文
     * @param key 秘钥
     * @return 密文
     */
    public String AESencode(String res, String key) {
        return keyGeneratorES(res, AES, key, keySizeAES, true);
    }

    /**
     * 对使用AES加密算法的密文进行解密
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return 密文
     */
    public String AESdecode(String res, String key) {
        return keyGeneratorES(res, AES, key, keySizeAES, false);
    }

    /**
     * 使用XO异或进行加密
     *
     * @param res 需要加密的密文
     * @param key 秘钥
     * @return 密文
     */
    public String XORencode(String res, String key) {
        byte[] bs = res.getBytes();
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((bs[i]) ^ key.hashCode());
        }
        return parseByte2HexStr(bs);
    }

    /**
     * 使用XO异或进行解密
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return 密文
     */
    public String XORdecode(String res, String key) {
        byte[] bs = parseHexStr2Byte(res);
        if (bs == null) {
            return "";
        }
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((bs[i]) ^ key.hashCode());
        }
        return new String(bs);
    }

    /**
     * 直接使用异或（第一调用加密，第二次调用解密）
     *
     * @param res 密文
     * @param key 秘钥
     * @return 结果
     */
    public int XOR(int res, String key) {
        return res ^ key.hashCode();
    }

    /**
     * 使用Base64进行加密
     *
     * @param res 字符串
     * @return 密文
     */
    public String Base64Encode(String res) {
        return Base64.encode(res.getBytes());
    }

    /**
     * 使用Base64进行解密
     *
     * @param res 密文
     * @return 字符串
     */
    public String Base64Decode(String res) {
        return new String(Base64.decode(res));
    }
}
