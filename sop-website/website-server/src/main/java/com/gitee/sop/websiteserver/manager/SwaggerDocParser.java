package com.gitee.sop.websiteserver.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gitee.sop.websiteserver.bean.DocInfo;
import com.gitee.sop.websiteserver.bean.DocItem;
import com.gitee.sop.websiteserver.bean.DocModule;
import com.gitee.sop.websiteserver.bean.DocParameter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
public class SwaggerDocParser implements DocParser {
    @Override
    public DocInfo parseJson(JSONObject docRoot) {
        String title = docRoot.getJSONObject("info").getString("title");
        List<DocItem> docItems = new ArrayList<>();

        JSONObject paths = docRoot.getJSONObject("paths");
        Set<String> pathNameSet = paths.keySet();
        for (String pathName : pathNameSet) {
            JSONObject pathInfo = paths.getJSONObject(pathName);
            Set<String> pathSet = pathInfo.keySet();
            Optional<String> first = pathSet.stream().findFirst();
            if (first.isPresent()) {
                String path = first.get();
                JSONObject docInfo = pathInfo.getJSONObject(path);
                DocItem docItem = buildDocItem(docInfo, docRoot);
                docItems.add(docItem);
            }
        }

        List<DocModule> docModuleList = docItems.stream()
                .collect(Collectors.groupingBy(DocItem::getModule))
                .entrySet()
                .stream()
                .map(entry -> {
                    DocModule docModule = new DocModule();
                    docModule.setModule(entry.getKey());
                    docModule.setDocItems(entry.getValue());
                    return docModule;
                })
                .collect(Collectors.toList());


        DocInfo docInfo = new DocInfo();
        docInfo.setTitle(title);
        docInfo.setDocModuleList(docModuleList);
        return docInfo;
    }

    protected DocItem buildDocItem(JSONObject docInfo, JSONObject docRoot) {
        DocItem docItem = new DocItem();
        docItem.setName(docInfo.getString("sop_name"));
        docItem.setVersion(docInfo.getString("sop_version"));
        docItem.setSummary(docInfo.getString("summary"));
        docItem.setDescription(docInfo.getString("description"));
        String moduleName = this.buildModuleName(docInfo, docRoot);
        docItem.setModule(moduleName);
        Optional<JSONArray> parametersOptional = Optional.ofNullable(docInfo.getJSONArray("parameters"));
        JSONArray parameters = parametersOptional.orElse(new JSONArray());
        List<DocParameter> docParameterList = parameters.toJavaList(DocParameter.class);
        docItem.setRequestParameters(docParameterList);

        List<DocParameter> responseParameterList = this.buildResponseParameterList(docInfo, docRoot);
        docItem.setResponseParameters(responseParameterList);

        return docItem;
    }

    protected String buildModuleName(JSONObject docInfo, JSONObject docRoot) {
        String title = docRoot.getJSONObject("info").getString("title");
        JSONArray tags = docInfo.getJSONArray("tags");
        if (tags != null && tags.size() > 0) {
            return tags.getString(0);
        }
        return title;
    }

    protected List<DocParameter> buildResponseParameterList(JSONObject docInfo, JSONObject docRoot) {
        String responseRef = getResponseRef(docInfo);
        List<DocParameter> respParameterList = new ArrayList<>();
        if (StringUtils.isNotBlank(responseRef)) {
            JSONObject responseObject = docRoot.getJSONObject("definitions").getJSONObject(responseRef);
            JSONObject properties = responseObject.getJSONObject("properties");
            Set<String> fieldNames = properties.keySet();
            for (String fieldName : fieldNames) {
                JSONObject fieldInfo = properties.getJSONObject(fieldName);
                DocParameter respParam = fieldInfo.toJavaObject(DocParameter.class);
                respParam.setName(fieldName);
                respParameterList.add(respParam);
            }
        }
        return respParameterList;
    }

    protected String getResponseRef(JSONObject docInfo) {
        String ref = Optional.ofNullable(docInfo.getJSONObject("responses"))
                .flatMap(jsonObject -> Optional.of(jsonObject.getJSONObject("200")))
                .flatMap(jsonObject -> Optional.of(jsonObject.getJSONObject("schema")))
                .flatMap(jsonObject -> Optional.of(jsonObject.getString("originalRef")))
                .orElse("");
        return ref;
    }
}
