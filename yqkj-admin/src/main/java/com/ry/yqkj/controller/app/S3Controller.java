package com.ry.yqkj.controller.app;

import com.ry.yqkj.common.core.controller.WxBaseController;
import com.ry.yqkj.common.core.domain.R;
import com.ry.yqkj.controller.web.common.CommonController;
import com.ry.yqkj.framework.config.s3.S3ConfigProperties;
import com.ry.yqkj.framework.web.api.TemplateApi;
import com.ry.yqkj.model.common.vo.AttachmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 文件上传
 *
 * @author ry.yqkj
 */
@Api("文件上传")
@RestController
public class S3Controller extends WxBaseController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Resource
    private TemplateApi templateApi;

    @Resource
    private S3ConfigProperties configProperties;

    /**
     * 图片上传
     *
     * @param file 图片名称
     */
    @ApiOperation("图片上传")
    @PostMapping("/s3/upload/img")
    public R<AttachmentVO> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        AttachmentVO attachmentVO = templateApi
                .upload(configProperties.getBucketName(), "img", file.getOriginalFilename(), file.getContentType(), file.getInputStream());
        attachmentVO.setFilePath(configProperties.getImgUrl() + attachmentVO.getFilePath());
        return R.ok(attachmentVO);
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/s3/upload/file")
    @ApiOperation("文件上传")
    public R<AttachmentVO> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        AttachmentVO attachmentVO = templateApi
                .upload(configProperties.getBucketName(), "doc", file.getOriginalFilename(), file.getContentType(), file.getInputStream());
        return R.ok(attachmentVO);
    }
}
