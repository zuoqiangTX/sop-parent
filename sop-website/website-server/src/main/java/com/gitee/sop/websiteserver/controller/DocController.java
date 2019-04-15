package com.gitee.sop.websiteserver.controller;

import com.gitee.sop.websiteserver.bean.DocItem;
import com.gitee.sop.websiteserver.bean.DocModule;
import com.gitee.sop.websiteserver.manager.DocManager;
import com.gitee.sop.websiteserver.vo.DocBaseInfoVO;
import com.gitee.sop.websiteserver.vo.DocModuleVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tanghc
 */
@RestController
@RequestMapping("doc")
public class DocController {

    @Autowired
    DocManager docManager;

    @Value("${api.url-test}")
    String urlTest;

    @Value("${api.url-prod}")
    String urlProd;

    @Value("${api.pwd}")
    String pwd;

    @GetMapping("/getDocBaseInfo")
    public DocBaseInfoVO getDocBaseInfo() {
        List<DocModuleVO> docModuleVOList = docManager.listAll()
                .stream()
                .map(docModule -> {
                    DocModuleVO vo = new DocModuleVO();
                    BeanUtils.copyProperties(docModule, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        DocBaseInfoVO baseInfoVO = new DocBaseInfoVO();
        baseInfoVO.setUrlTest(urlTest);
        baseInfoVO.setUrlProd(urlProd);
        baseInfoVO.setDocModuleVOList(docModuleVOList);
        return baseInfoVO;
    }

    @GetMapping("/module/{module}")
    public DocModule getDocModule(@PathVariable("module") String module) {
        return docManager.getByTitle(module);
    }

    @GetMapping("/item/{method}/{version}/")
    public DocItem getDocItem(@PathVariable("method") String method, @PathVariable("version") String version) {
        return docManager.get(method, version);
    }


    @GetMapping("/doc/reload")
    public void reload(String pwd) {
        if (StringUtils.equals(this.pwd, pwd)) {
            docManager.load();
        }
    }
}
