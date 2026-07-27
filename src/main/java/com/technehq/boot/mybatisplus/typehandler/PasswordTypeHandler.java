package com.technehq.boot.mybatisplus.typehandler;

import com.technehq.boot.util.PasswordUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>将明文密码使用 BCrypt 单向加盐哈希后存入数据库；从数据库取出来时不解密（哈希不可逆）。</p>
 * <p>注意！！ 使用 typeHandler，必须开启 autoResultMap 映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = PasswordTypeHandler.class)</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class PasswordTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, PasswordUtil.encode(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getString(columnIndex);
    }

}
