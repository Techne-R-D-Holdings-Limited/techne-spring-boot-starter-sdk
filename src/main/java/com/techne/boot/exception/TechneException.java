package com.techne.boot.exception;

import com.techne.boot.constants.TechneCode;
import com.techne.boot.pojo.basic.ResponseData;
import com.techne.boot.pojo.bo.RetBO;
import com.techne.boot.util.TechneUtil;
import lombok.Getter;

import java.io.Serial;
import java.util.function.Supplier;

/**
 * TechneException
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
public class TechneException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接口统一返回值
     */
    private final ResponseData<?> responseData;

    /**
     * 构造函数
     */
    public TechneException() {
        super(TechneUtil.formatMessage(TechneCode.FAIL.getMessage()));
        this.responseData = ResponseData.instance(TechneCode.FAIL);
    }

    /**
     * 异常
     *
     * @param message 消息
     */
    public TechneException(String message) {
        super(TechneUtil.formatMessage(message));
        this.responseData = ResponseData.instance(super.getMessage());
    }

    /**
     * 异常
     *
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TechneException(String message, Object[] args) {
        super(TechneUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(super.getMessage());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     */
    public TechneException(int code, String message) {
        super(TechneUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TechneException(int code, String message, Object[] args) {
        super(TechneUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param <T>     T
     */
    public <T> TechneException(int code, String message, T data) {
        super(TechneUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     */
    public <T> TechneException(int code, String message, T data, Object[] args) {
        super(TechneUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param <T>      T
     */
    public <T> TechneException(Object metadata, int code, String message) {
        super(TechneUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     */
    public <T> TechneException(Object metadata, int code, String message, Object[] args) {
        super(TechneUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param <T>      T
     */
    public <T> TechneException(Object metadata, int code, String message, T data) {
        super(TechneUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     */
    public <T> TechneException(Object metadata, int code, String message, T data, Object[] args) {
        super(TechneUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param retBO 消息
     */
    public TechneException(RetBO retBO) {
        super(TechneUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage());
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TechneException(RetBO retBO, Object[] args) {
        super(TechneUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage());
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param <T>   T
     */
    public <T> TechneException(RetBO retBO, T data) {
        super(TechneUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     */
    public <T> TechneException(RetBO retBO, T data, Object[] args) {
        super(TechneUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     */
    public TechneException(Object metadata, RetBO retBO) {
        super(TechneUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TechneException(Object metadata, RetBO retBO, Object[] args) {
        super(TechneUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     */
    public <T> TechneException(Object metadata, RetBO retBO, T data) {
        super(TechneUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     */
    public <T> TechneException(Object metadata, RetBO retBO, T data, Object[] args) {
        super(TechneUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param responseData responseData
     */
    public TechneException(ResponseData<?> responseData) {
        super(responseData.getMessage());
        this.responseData = responseData;
    }

    /**
     * 包装成Supplier
     *
     * @return Supplier
     */
    public static Supplier<TechneException> supplier() {
        return TechneException::new;
    }

    /**
     * 包装成Supplier
     *
     * @param message 消息
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(String message) {
        return () -> new TechneException(message);
    }

    /**
     * 包装成Supplier
     *
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(String message, Object[] args) {
        return () -> new TechneException(message, args);
    }

    /**
     * 包装成Supplier
     *
     * @param code    状态码
     * @param message 消息
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(int code, String message) {
        return () -> new TechneException(code, message);
    }

    /**
     * 包装成Supplier
     *
     * @param code    状态码
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(int code, String message, Object[] args) {
        return () -> new TechneException(code, message, args);
    }

    /**
     * 包装成Supplier
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param <T>     T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(int code, String message, T data) {
        return () -> new TechneException(code, message, data);
    }

    /**
     * 包装成Supplier
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(int code, String message, T data, Object[] args) {
        return () -> new TechneException(code, message, data, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(Object metadata, int code, String message) {
        return () -> new TechneException(metadata, code, message);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(Object metadata, int code, String message, Object[] args) {
        return () -> new TechneException(metadata, code, message, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(Object metadata, int code, String message, T data) {
        return () -> new TechneException(metadata, code, message, data);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(Object metadata, int code, String message, T data, Object[] args) {
        return () -> new TechneException(metadata, code, message, data, args);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(RetBO retBO) {
        return () -> new TechneException(retBO);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(RetBO retBO, Object[] args) {
        return () -> new TechneException(retBO, args);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param <T>   T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(RetBO retBO, T data) {
        return () -> new TechneException(retBO, data);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(RetBO retBO, T data, Object[] args) {
        return () -> new TechneException(retBO, data, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(Object metadata, RetBO retBO) {
        return () -> new TechneException(metadata, retBO);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(Object metadata, RetBO retBO, Object[] args) {
        return () -> new TechneException(metadata, retBO, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(Object metadata, RetBO retBO, T data) {
        return () -> new TechneException(metadata, retBO, data);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TechneException> supplier(Object metadata, RetBO retBO, T data, Object[] args) {
        return () -> new TechneException(metadata, retBO, data, args);
    }

    /**
     * 包装成Supplier
     *
     * @param responseData responseData
     * @return Supplier
     */
    public static Supplier<TechneException> supplier(ResponseData<?> responseData) {
        return () -> new TechneException(responseData);
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param retBO    消息
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(retBO);
        }
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO, Object[] args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(retBO, args);
        }
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO, T data) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(retBO, data);
        }
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO, T data, Object[] args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(retBO, data, args);
        }
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(metadata, retBO);
        }
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO, Object[] args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(metadata, retBO, args);
        }
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO, T data) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(metadata, retBO, data);
        }
    }

    /**
     * 执行方法且将异常包装成TechneException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO, T data, Object[] args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TechneException(metadata, retBO, data, args);
        }
    }

}
