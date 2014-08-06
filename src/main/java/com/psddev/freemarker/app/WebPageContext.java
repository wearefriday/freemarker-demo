package com.psddev.freemarker.app;

import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.util.Settings;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public class WebPageContext extends com.psddev.dari.util.WebPageContext {

    /**
     * Creates an instance based on the given {@code pageContext}.
     */
    public WebPageContext(PageContext pageContext) {
        super(pageContext);
    }

    /**
     * Creates an instance based on the given servlet parameters.
     */
    public WebPageContext(
            ServletContext servletContext,
            HttpServletRequest request,
            HttpServletResponse response) {

        super(servletContext, request, response);
    }

    /**
     * Returns true if PRODUCTION mode is enabled.
     */
    public boolean getIsProduction() {
        return Settings.isProduction();
    }

    /**
     * Returns true if DEBUG mode is enabled.
     */
    public boolean getIsDebug() {
        return Settings.isDebug();
    }

    /**
     * @return the locale of the current request.
     */
    public Locale getLocale() {
        return LocalizationFilter.Static.getLocale(getRequest());
    }

    /**
     *
     * @return the site of the current request.
     */
    public Site getSite() {
        return PageFilter.Static.getSite(getRequest());
    }
}
