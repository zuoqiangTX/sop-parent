package com.gitee.sop.adminserver.api.isv.result;

import com.gitee.easyopen.doc.annotation.ApiDocField;
import lombok.Data;

import java.util.Date;

/**
 * @author thc
 */
@Data
public class RoleVO {
	private Long id;
	@ApiDocField(description = "角色码")
	private String roleCode;
	@ApiDocField(description = "描述")
	private String description;
	private Date gmtCreate;
}
