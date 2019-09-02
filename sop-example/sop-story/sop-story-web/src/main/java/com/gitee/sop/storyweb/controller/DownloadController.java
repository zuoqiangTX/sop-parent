package com.gitee.sop.storyweb.controller;

import com.gitee.sop.servercommon.annotation.ApiMapping;
import com.gitee.sop.storyweb.controller.param.StoryParam;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

/**
 * 演示文件下载
 *
 * @author tanghc
 */
@Controller
public class DownloadController {

    @ApiMapping(value = "story.download")
    public ResponseEntity<byte[]> export(StoryParam param) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        // 假设下载classpath下的application.properties文件
        ClassPathResource resource = new ClassPathResource("/application.properties");
        File file = resource.getFile();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());

        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }
}
