package com.technehq.boot.pojo.bo;

import com.technehq.boot.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 前端直传S3的预签名上传信息
 * Presigned upload information for uploading a file directly from the frontend to S3
 *
 * @author Lil' Doe
 * 2026/7/2 10:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "前端直传S3的预签名上传信息")
public class PresignedUploadBO extends AbstractBasicSerializable {

    /**
     * 前端用于上传文件的预签名URL
     */
    @Schema(description = "前端用于上传文件的预签名URL")
    private String uploadUrl;

    /**
     * 前端上传时使用的HTTP方法，固定为PUT
     */
    @Schema(description = "前端上传时使用的HTTP方法，固定为PUT")
    private String method;

    /**
     * 前端上传时必须携带的请求头，例如x-amz-acl、x-amz-meta-*，缺少会导致上传失败（已过滤host）
     */
    @Schema(description = "前端上传时必须携带的请求头，例如x-amz-acl、x-amz-meta-*，缺少会导致上传失败")
    private Map<String, String> headers;

    /**
     * 上传成功后文件的最终访问URL，启用CDN时为CDN地址
     */
    @Schema(description = "上传成功后文件的最终访问URL，启用CDN时为CDN地址")
    private String fileUrl;

    /**
     * 文件在S3中的存储key
     */
    @Schema(description = "文件在S3中的存储key")
    private String key;

}
