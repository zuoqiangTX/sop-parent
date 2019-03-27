package com.gitee.sop.adminserver.api.isv.result;

import lombok.Data;

import java.util.Date;

/**
 * @author thc
 */
@Data
public class RoleVO {
	private Long id;
	private String roleCode;
	private String description;
	private Date gmtCreate;
}
