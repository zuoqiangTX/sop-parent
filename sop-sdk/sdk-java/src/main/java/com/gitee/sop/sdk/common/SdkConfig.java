package com.gitee.sop.sdk.common;

public class SdkConfig {

	public static String SUCCESS_CODE = "10000";
	
	public static String DEFAULT_VERSION = "1.0";

	public static String FORMAT_TYPE = "json";

	public static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String CHARSET = "UTF-8";

	public static String SIGN_TYPE = "RSA2";

	public static volatile DataNameBuilder dataNameBuilder = new DefaultDataNameBuilder();
}
