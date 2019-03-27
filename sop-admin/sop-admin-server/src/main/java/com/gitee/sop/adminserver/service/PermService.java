package com.gitee.sop.adminserver.service;

import com.gitee.sop.adminserver.entity.PermIsvRole;
import com.gitee.sop.adminserver.mapper.PermIsvRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thc
 */
@Service
public class PermService {
    @Autowired
    PermIsvRoleMapper permClientRoleMapper;

    /**
     * 获取客户端角色码列表
     *
     * @param clientId
     * @return
     */
    public List<String> listClientRoleCode(Long clientId) {
        List<PermIsvRole> list = permClientRoleMapper.listByColumn("client_id", clientId);
        List<String> retList = new ArrayList<>(list.size());
        for (PermIsvRole permClientRole : list) {
            retList.add(permClientRole.getRoleCode());
        }
        return retList;
    }

}
