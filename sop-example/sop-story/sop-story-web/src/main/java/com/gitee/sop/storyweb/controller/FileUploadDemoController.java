package com.gitee.sop.storyweb.controller;

import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.servercommon.util.UploadUtil;
import com.gitee.sop.storyweb.controller.param.FileUploadParam;
import com.gitee.sop.storyweb.controller.param.FileUploadParam2;
import com.gitee.sop.storyweb.vo.FileUploadVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;

/**
 * 演示文件上传
 *
 * @author tanghc
 */
@RestController
@Api(tags = "文件上传")
public class FileUploadDemoController {

    /**
     * 方式1：将文件写在参数中，可直接获取。好处是可以校验是否上传
     * @param param
     * @return
     */
    @ApiOperation(value = "文件上传例1", notes = "上传文件demo")
    @ApiMapping(value = "demo.file.upload")
    public FileUploadVO file1(FileUploadParam param) {
        System.out.println(param.getRemark());
        // 获取上传的文件
        MultipartFile file1 = param.getFile1();
        MultipartFile file2 = param.getFile2();

        FileUploadVO vo = new FileUploadVO();
        FileUploadVO.FileMeta fileMeta1 = buildFileMeta(file1);
        FileUploadVO.FileMeta fileMeta2 = buildFileMeta(file2);

        vo.getFiles().add(fileMeta1);
        vo.getFiles().add(fileMeta2);
        return vo;
    }

    /**
     * 方式2：从request中获取上传文件
     *
     * @param param
     * @return
     */
    @ApiOperation(value = "文件上传例2", notes = "可上传多个文件"
            // 多文件上传、不确定文件数量上传，必须申明下面这句，否则沙盒界面不会出现上传控件
            , extensions = @Extension(properties = @ExtensionProperty(name = "multiple", value = "multiple")))
    @ApiMapping(value = "demo.file.upload2")
    public FileUploadVO file2(FileUploadParam2 param, HttpServletRequest request) {
        System.out.println(param.getRemark());
        FileUploadVO vo = new FileUploadVO();
        // 获取上传的文件
        Collection<MultipartFile> uploadFiles = UploadUtil.getUploadFiles(request);
        for (MultipartFile multipartFile : uploadFiles) {
            FileUploadVO.FileMeta fileMeta = buildFileMeta(multipartFile);
            vo.getFiles().add(fileMeta);
        }
        return vo;
    }

    private FileUploadVO.FileMeta buildFileMeta(MultipartFile multipartFile) {
        // 文件名
        String fileName = multipartFile.getOriginalFilename();
        // 文件大小
        long size = multipartFile.getSize();
        // 文件内容
        String fileContent = null;
        try {
            fileContent = IOUtils.toString(multipartFile.getInputStream(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileUploadVO.FileMeta(fileName, size, fileContent);
    }
}
