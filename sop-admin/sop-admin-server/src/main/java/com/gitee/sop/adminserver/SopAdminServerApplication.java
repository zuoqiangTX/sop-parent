package com.gitee.sop.adminserver;

import com.gitee.fastmybatis.core.FastmybatisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class SopAdminServerApplication {

	public static void main(String[] args) {
		FastmybatisConfig.defaultIgnoreUpdateColumns = Arrays.asList("gmt_create", "gmt_modified");
		SpringApplication.run(SopAdminServerApplication.class, args);
	}
}
