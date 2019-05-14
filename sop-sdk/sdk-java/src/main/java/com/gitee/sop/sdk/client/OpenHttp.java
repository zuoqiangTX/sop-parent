package com.gitee.sop.sdk.client;

import com.gitee.sop.sdk.common.OpenConfig;
import com.gitee.sop.sdk.common.UploadFile;
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
public class OpenHttp {
    private Map<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

    private OkHttpClient httpClient;

    public OpenHttp(OpenConfig openConfig) {
        this.initHttpClient(openConfig);
    }

    protected void initHttpClient(OpenConfig openConfig) {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(openConfig.getConnectTimeoutSeconds(), TimeUnit.SECONDS) // 设置链接超时时间，默认10秒
                .readTimeout(openConfig.getReadTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(openConfig.getWriteTimeoutSeconds(), TimeUnit.SECONDS)
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
     * @param url
     * @param form
     * @param header header内容
     * @return
     * @throws IOException
     */
    public String postFormBody(String url, Map<String, String> form, Map<String, String> header) throws IOException {
        FormBody.Builder paramBuilder = new FormBody.Builder(StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entry : form.entrySet()) {
            paramBuilder.add(entry.getKey(), entry.getValue());
        }
        FormBody formBody = paramBuilder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(formBody);
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
    public String postFile(String url, Map<String, String> form, Map<String, String> header, List<UploadFile> files)
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
