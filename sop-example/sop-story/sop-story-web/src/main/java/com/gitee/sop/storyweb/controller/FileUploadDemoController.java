package com.gitee.sop.storyweb.controller;

import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.storyweb.controller.param.FileUploadParam;
import com.gitee.sop.storyweb.vo.FileUploadVO;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 演示文件上传
 * @author tanghc
 */
@RestController
public class FileUploadDemoController {

    /**
     * 接收客户端上传的文件，然后把文件信息返回给客户端
     * @param param
     * @param request
     * @return
     */
    @ApiMapping(value = "demo.file.upload", ignoreValidate = true)
    public FileUploadVO file(FileUploadParam param, HttpServletRequest request) {
        System.out.println(param.getRemark());
        FileUploadVO vo = new FileUploadVO();
        //检查form中是否有enctype="multipart/form-data"
        if (ServletFileUpload.isMultipartContent(request)) {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> fileMap = multiRequest.getFileMap();
            fileMap.entrySet()
                    .stream()
                    .forEach(entry->{
                        MultipartFile multipartFile = entry.getValue();
                        try {
                            String fileName = multipartFile.getOriginalFilename();
                            long size = multipartFile.getSize();
                            String fileContent = IOUtils.toString(multipartFile.getInputStream(), "UTF-8");
                            FileUploadVO.FileMeta fileMeta = new FileUploadVO.FileMeta(fileName, size, fileContent);
                            vo.getFiles().add(fileMeta);
                            System.out.println(fileContent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return vo;
    }
}
