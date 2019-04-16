package com.gitee.easyopen.server.config;

import com.gitee.easyopen.doc.ApiDocHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseSopDocController {

    public abstract String getDocTitle();

    @RequestMapping("/v2/api-docs")
    @ResponseBody
    public Map<String, Object> getDocInfo() {
        Map<String, Object> context = this.getContext();
        context.put("easyopen", "1.16.3");
        context.put("apiModules", ApiDocHolder.getApiDocBuilder().getApiModules());
        context.put("title", getDocTitle());
        return context;
    }

    public Map<String, Object> getContext() {
        return new HashMap<>(8);
    }

}