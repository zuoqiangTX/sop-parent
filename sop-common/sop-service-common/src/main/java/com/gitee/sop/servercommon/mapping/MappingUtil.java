package com.gitee.sop.servercommon.mapping;

import org.springframework.util.StringUtils;

/**
 * @author tanghc
 */
public class MappingUtil {
    /**
     * 将springmvc接口路径转换成SOP方法名
     * @param path springmvc路径，如/a/b,/goods/listGoods
     * @return
     */
    public static String buildApiName(String path) {
        path = StringUtils.trimLeadingCharacter(path, '/');
        path = StringUtils.trimTrailingCharacter(path, '/');
        path = path.replace("/", ".");
        return path;
    }
}
