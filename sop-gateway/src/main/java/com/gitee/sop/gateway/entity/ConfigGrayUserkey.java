package com.gitee.sop.gateway.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


/**
 * 表名：config_gray_userkey
 * 备注：灰度发布用户key
 *
 * @author tanghc
 */
@Table(name = "config_gray_userkey")
@Data
public class ConfigGrayUserkey {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**  数据库字段：id */
    private Long id;

    /** instanceId, 数据库字段：instance_id */
    private String instanceId;

    /** 用户key，多个用引文逗号隔开, 数据库字段：user_key_content */
    private String userKeyContent;

    /** 需要灰度的接口，goods.get=1.2,order.list=1.2, 数据库字段：name_version_content */
    private String nameVersionContent;

    /** 0：禁用，1：启用, 数据库字段：status */
    private Byte status;

    /**  数据库字段：gmt_create */
    private Date gmtCreate;

    /**  数据库字段：gmt_modified */
    private Date gmtModified;
}
