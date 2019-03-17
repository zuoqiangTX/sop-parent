package com.gitee.sop.adminserver.api.system;

import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.doc.annotation.ApiDocMethod;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("系统接口")
public class SystemApi {

    @Value("${sop-admin.profiles}")
    private String profiles;

    private List<String> profileList;

    @ApiDocMethod(description = "获取profile列表")
    @Api(name = "system.profile.list")
    public List<String> listProfiles() {
        if (profileList == null) {
            String[] arr = profiles.split("\\,");
            profileList = Stream.of(arr).collect(Collectors.toList());
        }
        return profileList;
    }
}
