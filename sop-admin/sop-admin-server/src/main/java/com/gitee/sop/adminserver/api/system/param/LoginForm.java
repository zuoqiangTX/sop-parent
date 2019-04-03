package com.gitee.sop.adminserver.api.system.param;

import lombok.Data;

@Data
public class LoginForm {
    private String username;
    private String password;
}