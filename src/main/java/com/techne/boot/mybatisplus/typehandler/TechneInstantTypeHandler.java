package com.techne.boot.mybatisplus.typehandler;

import com.techne.boot.util.TechneUtil;
import org.apache.ibatis.type.InstantTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * 由于mysql中timestamp类型的精度会自动舍入
 * <br/>
 * 为了覆盖mybatis自带的InstantTypeHandler类，在此类中重写setNonNullParameter方法
 * <br/>
 * 此处保留3位小数，对应数据库中的timestamp(3)类型
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class TechneInstantTypeHandler extends InstantTypeHandler {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Instant parameter, JdbcType jdbcType) throws SQLException {
        Timestamp timestamp = Timestamp.from(parameter);
        timestamp.setNanos(TechneUtil.interceptNano(timestamp.getNanos()));
        ps.setTimestamp(i, timestamp);
    }

}
