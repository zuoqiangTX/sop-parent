package com.gitee.sop.adminserver.api.isv;

import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;
import com.gitee.easyopen.util.CopyUtil;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import com.gitee.fastmybatis.core.support.PageEasyui;
import com.gitee.fastmybatis.core.util.MapperUtil;
import com.gitee.sop.adminserver.api.isv.result.RoleVO;
import com.gitee.sop.adminserver.mapper.PermRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@ApiService
@ApiDoc("ISV管理")
@Slf4j
public class RoleApi {

    @Autowired
    PermRoleMapper permRoleMapper;

    @Api(name = "role.listall")
    List<RoleVO> roleListall() {
        Query query = new Query();
        query.orderby("id", Sort.ASC);
        return permRoleMapper.list(query).stream()
                .map(permRole -> {
                    RoleVO vo = new RoleVO();
                    CopyUtil.copyProperties(permRole, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
