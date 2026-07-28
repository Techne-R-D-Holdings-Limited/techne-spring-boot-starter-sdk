package com.technehq.boot.pojo.basic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询标准出参
 * <p>
 * 与 MyBatis-Plus 的 {@link TechnePage} 解耦，只暴露给前端约定好的字段，
 * 不向调用方泄漏 MP 内部字段（如 searchCount / optimizeCountSql / orders 等）。
 * 入参约定见 {@link BasicPage}（pageNum / pageSize），前后端协议同名同义。
 *
 * <p>标准结构示例：
 * <pre>{@code
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": {
 *     "list": [...],
 *     "total": 235,
 *     "pageNum": 1,
 *     "pageSize": 10,
 *     "pages": 24,
 *     "hasNext": true,
 *     "hasPrevious": false
 *   },
 *   "time": "...",
 *   "traceId": "..."
 * }
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@Schema(description = "分页查询标准出参")
@Accessors(chain = true)
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页数据列表
     */
    @Schema(description = "当前页数据列表")
    private List<T> list;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private long total;

    /**
     * 当前页（与入参 pageNum 同名同义）
     */
    @Schema(description = "当前页")
    private long pageNum;

    /**
     * 每页条数（与入参 pageSize 同名同义）
     */
    @Schema(description = "每页条数")
    private long pageSize;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private long pages;

    /**
     * 是否还有下一页
     */
    @Schema(description = "是否还有下一页")
    private boolean hasNext;

    /**
     * 是否还有上一页
     */
    @Schema(description = "是否还有上一页")
    private boolean hasPrevious;

    /**
     * 附加元数据（可选，如合计/统计等非列表数据）
     */
    @Schema(description = "附加元数据（可选，如合计/统计）", nullable = true)
    private Object metadata;

    /**
     * 从 MyBatis-Plus 分页对象转换，屏蔽内部字段
     *
     * @param page 分页对象
     * @param <T>  数据类型
     * @return 标准分页出参
     */
    public static <T> PageResult<T> of(TechnePage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.list = page.getRecords();
        result.total = page.getTotal();
        result.pageNum = page.getCurrent();
        result.pageSize = page.getSize();
        result.metadata = page.getMetadata();
        // 不分页（pageSize <= 0，对应入参传 -1）时特殊处理，避免总页数除零/负数
        if (page.getSize() <= 0) {
            result.pages = 1;
            result.hasNext = false;
            result.hasPrevious = false;
            if (result.total <= 0 && result.list != null) {
                result.total = result.list.size();
            }
        } else {
            result.pages = page.getPages();
            result.hasNext = page.getCurrent() < page.getPages();
            result.hasPrevious = page.getCurrent() > 1;
        }
        return result;
    }

    /**
     * 构造空分页（无数据）
     *
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @param <T>      数据类型
     * @return 标准分页出参
     */
    public static <T> PageResult<T> empty(long pageNum, long pageSize) {
        PageResult<T> result = new PageResult<>();
        result.list = Collections.emptyList();
        result.total = 0;
        result.pageNum = pageNum;
        result.pageSize = pageSize;
        result.pages = 0;
        result.hasNext = false;
        result.hasPrevious = pageNum > 1;
        return result;
    }

}
