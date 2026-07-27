package com.technehq.boot.util;

import cn.hutool.crypto.digest.BCrypt;
import lombok.experimental.UtilityClass;

/**
 * 密码工具：使用 BCrypt 单向加盐哈希存储，不可逆。
 *
 * <p>用于替代原 {@code PasswordTypeHandler} 中基于 AES 的可逆加密。
 * 消费方在登录校验时必须调用 {@link #matches(String, String)}，
 * 切勿直接比较明文或对存储值做解密。</p>
 *
 * <p>BCrypt 哈希自带随机盐，相同明文每次哈希结果都不同；
 * 默认强度 {@link #STRENGTH} = 12（约 2^12 次 Key Expansion），可按需调整。</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@UtilityClass
public class PasswordUtil {

    /**
     * BCrypt 成本因子（log rounds），默认 12。越大越安全，但登录校验越慢。
     */
    public static final int STRENGTH = 12;

    /**
     * 对明文密码进行 BCrypt 哈希。
     *
     * @param rawPassword 明文密码
     * @return BCrypt 哈希，形如 {@code $2a$12$...}
     */
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(STRENGTH));
    }

    /**
     * 校验明文密码与存储的 BCrypt 哈希是否匹配。
     *
     * @param rawPassword 用户输入的明文密码
     * @param stored      数据库中存储的 BCrypt 哈希
     * @return 匹配返回 true
     */
    public boolean matches(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, stored);
    }

}
