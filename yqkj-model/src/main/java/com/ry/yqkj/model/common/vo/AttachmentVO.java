package com.ry.yqkj.model.common.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/*
 * since: 2024/5/15 16:06
 * author: lihy
 * description: 文件上传结果
 */
@Data
@Builder
public class AttachmentVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 文件名称 sxx.png,xxx.txt
     */
    private String fileOriName;
    /**
     * 文件名称 sxx.png,xxx.txt
     */
    private String fileType;
    /**
     * 文件路径 /bucket/文件夹名称/xxx.txt
     */
    private String filePath;

    private int height;

    private int width;

}
