package com.psddev.freemarker.app;

import com.psddev.dari.util.AbstractFilter;

import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocalizationFilter extends AbstractFilter {

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {

        // Nothing to do right now...
        chain.doFilter(request, response);
    }

    public static final class Static {

        private Static() {
        }

        /**
         * Returns the locale for the current request, either based on the main
         * content being viewed or the site user's preference.
         *
         * @param request the current HTTP request.
         * @return the Locale for the current request.
         */
        public static Locale getLocale(HttpServletRequest request) {

            Locale locale = null;

            String lang = request.getParameter("lang");

            if (lang != null) {
                locale = new Locale(lang);
            }

            if (locale == null) {
                locale = Locale.getDefault();
            }

            return locale;
        }
    }
}
