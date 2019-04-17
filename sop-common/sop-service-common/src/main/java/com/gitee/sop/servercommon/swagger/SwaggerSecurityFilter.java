package com.gitee.sop.servercommon.swagger;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tanghc
 */
@Slf4j
public class SwaggerSecurityFilter implements Filter {

    protected List<String> urlFilters = new ArrayList<>();

    private String secret = "b749a2ec000f4f29";

    public SwaggerSecurityFilter() {
        urlFilters.add(".*?/doc\\.html.*");
        urlFilters.add(".*?/v2/api-docs.*");
        urlFilters.add(".*?/v2/api-docs-ext.*");
        urlFilters.add(".*?/swagger-resources.*");
        urlFilters.add(".*?/swagger-ui\\.html.*");
        urlFilters.add(".*?/swagger-resources/configuration/ui.*");
        urlFilters.add(".*?/swagger-resources/configuration/security.*");
    }

    protected boolean match(String uri) {
        boolean match = false;
        if (uri != null) {
            for (String regex : urlFilters) {
                if (uri.matches(regex)) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        // 没有匹配到，直接放行
        if (!match(uri)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            if (validate(request)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                response.setContentType("text/palin;charset=UTF-8");
                response.setStatus(403);
                PrintWriter printWriter = response.getWriter();
                printWriter.write("access forbidden");
                printWriter.flush();
            }

        }
    }

    protected boolean validate(HttpServletRequest request) {
        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        if (StringUtils.isAnyBlank(time, sign)) {
            return false;
        }
        String source = secret + time + secret;
        String serverSign = DigestUtils.md5DigestAsHex(source.getBytes());
        return serverSign.equals(sign);
    }

    @Override
    public void destroy() {

    }
}
