package com.psddev.freemarker.app;

import com.psddev.cms.db.PageFilter;
import com.psddev.dari.util.AbstractFilter;

import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationFilter extends AbstractFilter implements AbstractFilter.Auto {

    private static final String WEB_PAGE_CONTEXT_ATTRIBUTE = "wpc";

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {

        // Sets a global attribute for accessing the page context
        WebPageContext webPageContext = new WebPageContext(request.getServletContext(), request, response);
        request.setAttribute(WEB_PAGE_CONTEXT_ATTRIBUTE, webPageContext);

        chain.doFilter(request, response);
    }

    @Override
    protected Iterable<Class<? extends Filter>> dependencies() {
        return Arrays.<Class<? extends Filter>>asList(
                LocalizationFilter.class);
    }

    @Override
    public void updateDependencies(Class<? extends AbstractFilter> filterClass, List<Class<? extends Filter>> dependencies) {
        if (PageFilter.class.isAssignableFrom(filterClass)) {
            dependencies.add(getClass());
        }
    }
}
