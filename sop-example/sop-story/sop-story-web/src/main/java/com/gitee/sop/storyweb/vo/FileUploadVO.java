package com.gitee.sop.storyweb.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Data
public class FileUploadVO {

    private List<FileMeta> files = new ArrayList();

    @Data
    public static class FileMeta {

        public FileMeta(String filename, long size, String content) {
            this.filename = filename;
            this.size = size;
            this.content = content;
        }

        public FileMeta() {
        }

        private String filename;
        private long size;
        private String content;
    }
}
