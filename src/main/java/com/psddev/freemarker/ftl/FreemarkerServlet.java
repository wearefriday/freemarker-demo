package com.psddev.freemarker.ftl;

import com.psddev.dari.util.ClassFinder;
import com.psddev.dari.util.FrameTag;
import com.psddev.dari.util.Lazy;
import com.psddev.dari.util.Settings;

import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom extension of the <a href="http://freemarker.org/docs/pgui_misc_servlet.html">Freemarker servlet</a>
 * that's packages with the Freemarker distribution.
 *
 * FreemarkerServlet settings that should go in your web.xml.
 * <br>
 * <pre>
 *     {@code
 *     <init-param>
 *         <param-name>TemplatePath</param-name>
 *         <param-value>/</param-value>
 *     </init-param>
 *     <init-param>
 *         <param-name>NoCache</param-name>
 *         <param-value>false</param-value>
 *     </init-param>
 *     <init-param>
 *         <param-name>ContentType</param-name>
 *         <param-value>text/html; charset=UTF-8</param-value>
 *     </init-param>
 *     }
 * </pre>
 */
public class FreemarkerServlet extends freemarker.ext.servlet.FreemarkerServlet {

    public static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerServlet.class);

    private static final int DEFAULT_TEMPLATE_UPDATE_DELAY_SECONDS = 10;

    private LocalizingWrapper localizingWrapper;

    // Additional Enums from 3rd party dependencies that should be available in the templates.
    private static final List<Class<? extends Enum>> EXTERNAL_ENUM_CLASSES = Arrays.asList(
            FrameTag.InsertionMode.class);

    private final Lazy<Map<String, TemplateHashModel>> templateHashModels = new Lazy<Map<String, TemplateHashModel>>() {

        @Override
        protected Map<String, TemplateHashModel> create() {

            Map<String, TemplateHashModel> templateHashModels = new HashMap<>();

            ObjectWrapper wrapper = FreemarkerServlet.this.getObjectWrapper();

            if (wrapper instanceof BeansWrapper) {

                TemplateHashModel enumModels = ((BeansWrapper) wrapper).getEnumModels();

                TemplateHashModel staticModels = ((BeansWrapper) wrapper).getStaticModels();

                List<Class<?>> enumClasses = new ArrayList<>();

                enumClasses.addAll(ClassFinder.Static.findClasses(EnumModel.class));
                enumClasses.addAll(EXTERNAL_ENUM_CLASSES);

                for (Class<?> klass : enumClasses) {

                    if (klass.isEnum()) {
                        try {
                            TemplateHashModel templateHashModel = (TemplateHashModel) enumModels.get(klass.getName());
                            if (templateHashModel != null) {

                                templateHashModels.put(klass.getSimpleName(), templateHashModel);
                                templateHashModels.put(klass.getName(), templateHashModel);
                            }

                        } catch (TemplateModelException e) {
                            LOGGER.debug(e.getMessage(), e);
                        }
                    }
                }

                for (Class<? extends StaticModel> klass : ClassFinder.Static.findClasses(StaticModel.class)) {

                    try {
                        TemplateHashModel templateHashModel = (TemplateHashModel) staticModels.get(klass.getName());
                        if (templateHashModel != null) {

                            templateHashModels.put(klass.getSimpleName(), templateHashModel);
                            templateHashModels.put(klass.getName(), templateHashModel);
                        }

                    } catch (TemplateModelException e) {
                        LOGGER.debug(e.getMessage(), e);
                    }
                }
            }

            return templateHashModels;
        }
    };

    /**
     * Creates a TemplateLoader that escapes HTML by default.
     *
     * @param templatePath the path to the freemarker template.
     * @return a template loader that escapes HTML by default.
     * @throws java.io.IOException
     */
    @Override
    protected TemplateLoader createTemplateLoader(String templatePath) throws IOException {
        return new HtmlTemplateLoader(super.createTemplateLoader(templatePath));
    }

//    @Override
//    protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) {
//        // delegates to default implementation
//        return super.deduceLocale(templatePath, request, response);
//    }
//
//    @Override
//    protected TemplateModel createModel(ObjectWrapper wrapper, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {
//        // delegates to default implementation
//        return super.createModel(wrapper, servletContext, request, response);
//    }
//
//    @Override
//    protected String requestUrlToTemplatePath(HttpServletRequest request) {
//        // delegates to default implementation
//        return super.requestUrlToTemplatePath(request);
//    }
//
//    @Override
//    protected boolean preprocessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // delegates to default implementation
//        return super.preprocessRequest(request, response);
//    }

    /**
     * Overrides the default Configuration creation
     * @return the freemarker configuration.
     */
    @Override
    protected Configuration createConfiguration() {

        Configuration configuration = super.createConfiguration();

        configuration.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX);
        configuration.setTemplateUpdateDelay(Settings.isProduction() ? DEFAULT_TEMPLATE_UPDATE_DELAY_SECONDS : 0);
        configuration.setDefaultEncoding("UTF-8");

        return configuration;
    }

    /**
     * Overrides the default ObjectWrapper creation
     * @return the object wrapper.
     */
    @Override
    protected ObjectWrapper createObjectWrapper() {
        // Returns a special ObjectWrapper to include i18n and l10n support.
        return (localizingWrapper = new LocalizingWrapper());
    }

//    @Override
//    protected ObjectWrapper getObjectWrapper() {
//        // delegates to default implementation
//        return super.getObjectWrapper();
//    }
//
//    @Override
//    protected HttpRequestParametersHashModel createRequestParametersHashModel(HttpServletRequest request) {
//        // delegates to default implementation
//        return super.createRequestParametersHashModel(request);
//    }
//
//    @Override
//    protected void initializeServletContext(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // delegates to default implementation
//        super.initializeServletContext(request, response);
//    }
//
//    @Override
//    protected void initializeSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // delegates to default implementation
//        super.initializeSession(request, response);
//    }

    /**
     *
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param template the current Freemarker template
     * @param data the current Freemarker template data
     * @return true if the template should be processed. true by default.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {

        // first - this should be the first block in the method
        if (localizingWrapper != null) {
            localizingWrapper.setRequest(request);
        }

        if (data instanceof SimpleHash) {
            ((SimpleHash) data).putAll(getTemplateHashModels());
        }

        return true;
    }

    /**
     *
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param template the current Freemarker template
     * @param data the current Freemarker template data
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void postTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {
        // nothing to do...
    }

    private Map<String, TemplateHashModel> getTemplateHashModels() {
        return templateHashModels.get();
    }

    /**
     * Marker interface that tells the system to add implementing enums to the
     * TemplateHashModel so it is available from within the templates.
     */
    public static interface EnumModel { }

    /**
     * Marker interface that tells the system to add implementing classes to the
     * TemplateHashModel so their static variables are available within the
     * templates.
     */
    public static interface StaticModel { }
}
