package com.gitee.sop.websiteserver.bean;

import lombok.Data;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author tanghc
 */
public class HttpTool {
    private static final String METHOD_GET = "get";
    private Map<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

    private OkHttpClient httpClient;

    public HttpTool() {
        this(new HttpToolConfig());
    }

    public HttpTool(HttpToolConfig httpToolConfig) {
        this.initHttpClient(httpToolConfig);
    }

    protected void initHttpClient(HttpToolConfig httpToolConfig) {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(httpToolConfig.connectTimeoutSeconds, TimeUnit.SECONDS) // 设置链接超时时间，默认10秒
                .readTimeout(httpToolConfig.readTimeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(httpToolConfig.writeTimeoutSeconds, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }
                    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();
    }

    @Data
    public static class HttpToolConfig {
        /** 请求超时时间 */
        private int connectTimeoutSeconds = 10;
        /** http读取超时时间 */
        private int readTimeoutSeconds = 10;
        /** http写超时时间 */
        private int writeTimeoutSeconds = 10;
    }

    /**
     * get请求
     *
     * @param url
     * @param header
     * @return
     * @throws IOException
     */
    public String get(String url, Map<String, String> header) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).get();
        // 添加header
        addHeader(builder, header);

        Request request = builder.build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 提交表单
     *
     * @param url url
     * @param form 参数
     * @param header header
     * @param method 请求方式，post，get等
     * @return
     * @throws IOException
     */
    public String request(String url, Map<String, String> form, Map<String, String> header, String method) throws IOException {
        Request.Builder requestBuilder;
        if (METHOD_GET.equalsIgnoreCase(method)) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            for (Map.Entry<String, String> entry : form.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            requestBuilder = new Request.Builder()
                    .url(urlBuilder.build())
                    .get();
        } else {
            FormBody.Builder paramBuilder = new FormBody.Builder(StandardCharsets.UTF_8);
            for (Map.Entry<String, String> entry : form.entrySet()) {
                paramBuilder.add(entry.getKey(), entry.getValue());
            }
            FormBody formBody = paramBuilder.build();
            requestBuilder = new Request.Builder()
                    .url(url)
                    .method(method, formBody);
        }
        // 添加header
        addHeader(requestBuilder, header);

        Request request = requestBuilder.build();
        Response response = httpClient
                .newCall(request)
                .execute();
        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }

    /**
     * 提交表单，并且上传文件
     *
     * @param url
     * @param form
     * @param header
     * @param files
     * @return
     * @throws IOException
     */
    public String requestFile(String url, Map<String, String> form, Map<String, String> header, List<UploadFile> files)
            throws IOException {
        // 创建MultipartBody.Builder，用于添加请求的数据
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);

        for (UploadFile uploadFile : files) {
            bodyBuilder.addFormDataPart(uploadFile.getName(), // 请求的名字
                    uploadFile.getFileName(), // 文件的文字，服务器端用来解析的
                    RequestBody.create(null, uploadFile.getFileData()) // 创建RequestBody，把上传的文件放入
            );
        }

        Set<Map.Entry<String, String>> entrySet = form.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        RequestBody requestBody = bodyBuilder.build();

        Request.Builder builder = new Request.Builder().url(url).post(requestBody);

        // 添加header
        addHeader(builder, header);

        Request request = builder.build();
        Response response = httpClient.newCall(request).execute();
        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }

    private void addHeader(Request.Builder builder, Map<String, String> header) {
        if (header != null) {
            Set<Map.Entry<String, String>> entrySet = header.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                builder.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }

    public void setCookieStore(Map<String, List<Cookie>> cookieStore) {
        this.cookieStore = cookieStore;
    }

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

}
