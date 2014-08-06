package com.psddev.freemarker.util;

import com.psddev.cms.db.ElFunctionUtils;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.PageStage;
import com.psddev.cms.db.Site;
import com.psddev.dari.util.HtmlElement;
import com.psddev.dari.util.HtmlNode;
import com.psddev.dari.util.HtmlWriter;
import com.psddev.dari.util.JspUtils;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.Settings;
import com.psddev.dari.util.StringUtils;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * A base implementation of the Brightspot provided
 * {@link com.psddev.cms.db.PageStage.SharedUpdatable} interface that follows
 * conventions used by the <a href="https://github.com/perfectsense/brightspot-js-grunt">Brightspot Grunt Plugin</a>.
 */
public abstract class AbstractPageStageUpdater implements PageStage.SharedUpdatable {

    private static final String ATTRIBUTE_PREFIX = AbstractPageStageUpdater.class.getName() + ".";

    private static final String LESS_JS_ADDED_ATTRIBUTE = ATTRIBUTE_PREFIX + "lessJsAdded";

    private static final String GOOGLE_FONTS_API_BASE_URL = "http://fonts.googleapis.com/css?family=";

    private static final String CMS_IS_RESOURCE_IN_STORAGE_SETTING = "cms/isResourceInStorage";

    protected abstract String getBaseStylePath(Site site);

    protected abstract String getBaseScriptPath(Site site);

    protected abstract String getBaseFontPath(Site site);

    protected abstract boolean useMinifiedStyles(Site site);

    protected abstract boolean useMinifiedScripts(Site site);

    /* Sub-classes may override this method to provide the actual path to their
     *  LESS JS file. */
    protected String getLessJsPath() {
        return "/less.js";
    }

    /* Sub-classes may override this method to provide the actual path to their
     *  Require JS file. */
    protected String getRequireJsPath() {
        return "/require.js";
    }

    protected abstract void doUpdateStageBefore(Object object, PageStage stage);

    protected abstract void doUpdateStageAfter(Object object, PageStage stage);

    public final void updateStageBefore(Object object, PageStage stage) {

        doUpdateStageBefore(object, stage);
    }

    public final void updateStageAfter(Object object, final PageStage stage) {

        doUpdateStageAfter(object, stage);

        // Hack for dari grid to make it play nice when in development mode and css resources are un-minified.
        stage.getHeadNodes().add(new HtmlNode() {
            @Override
            public void writeHtml(HtmlWriter writer) throws IOException {
                stage.getRequest().setAttribute("com.psddev.dari.util.HtmlGrid.gridPaths", null);
            }
        });
    }

    private Site getSite(PageStage stage) {
        return PageFilter.Static.getSite(stage.getRequest());
    }

    /**
     *
     * @param stage the current page stage
     * @param googleWebFonts google web font specifier
     */
    protected final void addGoogleWebFonts(PageStage stage, String[][] googleWebFonts) {
        if (googleWebFonts != null && googleWebFonts.length > 0) {
            stage.findOrCreateHeadElement("link",
                    "rel", "stylesheet",
                    "type", "text/css",
                    "href", getGoogleFontApiUrl(googleWebFonts));
        }
    }

    /**
     *
     * @param stage the current page stage
     * @param fontFileName the font file name with out the file type extension
     * @param fontFamily the css font family name
     * @param fontWeight the css font weight
     * @param svgHash the svgHash
     */
    protected final void addLocalFont(PageStage stage, boolean useCmsResource, String fontFileName, String fontFamily, String fontWeight, String fontStyle, String svgHash) {

        String hostname = JspUtils.getHostname(stage.getRequest());

        StringBuilder fontFace = new StringBuilder();

        String baseFontPath = StringUtils.ensureSurrounding(getBaseFontPath(getSite(stage)), "/");
        String fontFilePath = baseFontPath + fontFileName;

        String eotSrc = useCmsResource ? ElFunctionUtils.resource(fontFilePath + ".eot") : fontFilePath + ".eot";
        String woffSrc = useCmsResource ? ElFunctionUtils.resource(fontFilePath + ".woff") : fontFilePath + ".woff";
        String ttfSrc = useCmsResource ? ElFunctionUtils.resource(fontFilePath + ".ttf") : fontFilePath + ".ttf";
        String svgSrc = useCmsResource ? ElFunctionUtils.resource(fontFilePath + ".svg") : fontFilePath + ".svg";

        eotSrc = StringUtils.addQueryParameters(eotSrc, "d", hostname);
        woffSrc = StringUtils.addQueryParameters(woffSrc, "d", hostname);
        ttfSrc = StringUtils.addQueryParameters(ttfSrc, "d", hostname);
        svgSrc = StringUtils.addQueryParameters(svgSrc, "d", hostname);

        final String nl = Settings.isProduction() ? "" : "\n";
        final String tab = Settings.isProduction() ? "" : "    ";

        fontFace.append(nl);
        fontFace.append("@font-face {").append(nl);
        fontFace.append(tab).append("font-family: '").append(fontFamily).append("';").append(nl);
        fontFace.append(tab).append("src: url('").append(eotSrc).append("');").append(nl);
        fontFace.append(tab).append("src: url('").append(eotSrc.replaceFirst("\\x3f", "?#iefix&")).append("') format('embedded-opentype'),").append(nl);
        fontFace.append(tab).append(tab).append("url('").append(woffSrc).append("') format('woff'),").append(nl);
        fontFace.append(tab).append(tab).append("url('").append(ttfSrc).append("') format('truetype'),").append(nl);
        fontFace.append(tab).append(tab).append("url('").append(svgSrc).append('#').append(svgHash).append("') format('svg');").append(nl);
        fontFace.append(tab).append("font-weight: ").append(fontWeight).append(';').append(nl);
        fontFace.append(tab).append("font-style: ").append(fontStyle).append(';').append(nl);
        fontFace.append('}').append(nl);

        addStyleInline(stage, fontFace.toString());
    }

    /**
     *
     * @param stage the current page stage
     * @param pathsToLessCssFiles paths to the LESS CSS files
     */
    protected final void addStylesForLessJs(PageStage stage, String... pathsToLessCssFiles) {
        addStylesForLessJs(stage, true, pathsToLessCssFiles);
    }

    /**
     *
     * @param stage the current page stage
     * @param pathsToLessCssFiles paths to the LESS CSS files
     */
    protected final void addStylesForLessJs(PageStage stage, boolean useCmsResource, String... pathsToLessCssFiles) {

        if (pathsToLessCssFiles != null) {

            for (String less : pathsToLessCssFiles) {
                addStyle(stage, useCmsResource, less);
            }

            if (!useMinifiedStyles(getSite(stage)) && pathsToLessCssFiles.length > 0) {

                if (!Boolean.TRUE.equals(stage.getRequest().getAttribute(LESS_JS_ADDED_ATTRIBUTE))) {

                    addScriptInline(stage, "window.less = window.less || { }; window.less.env = 'development'; window.less.poll = 500;");

                    // never use cms:resource for the less file
                    addScript(stage, false, getLessJsPath());

                    stage.getRequest().setAttribute(LESS_JS_ADDED_ATTRIBUTE, true);
                }
            }
        }
    }

    /**
     * @param stage the page stage
     * @param globalVariableName the global javascript variable name through
     *          which all other properties can be accessed.
     * @param properties key value pairs to be placed in the global javascript
     *          variable
     */
    protected final void addGlobalScriptVariables(
            PageStage stage,
            final String globalVariableName,
            final Object... properties) {

        Map<String, Object> propertyMap = new HashMap<>();

        if (properties != null) {

            for (int i = 1; i < properties.length; i += 2) {
                Object propName = properties[i - 1];
                Object propValue = properties[i];

                if (propName != null && propValue != null) {
                    propertyMap.put(propName.toString(), propValue);
                }
            }
        }

        // Javascript properties accessible from a single global variable
        addScriptInline(stage, "var " + globalVariableName + " = " + ObjectUtils.toJson(propertyMap) + ";");
    }

    protected final void addAllScriptsForRequireJs(PageStage stage,
                                                   String pathToConfigScript,
                                                   String pathToMainScript,
                                                   String... pathsToShimScripts) {

        addAllScriptsForRequireJs(stage, true, pathToConfigScript, pathToMainScript, pathsToShimScripts);
    }

    protected final void addAllScriptsForRequireJs(PageStage stage,
                                                   boolean useCmsResource,
                                                   String pathToConfigScript,
                                                   String pathToMainScript,
                                                   String... pathsToShimScripts) {

        if (pathsToShimScripts != null) {
            for (String js : pathsToShimScripts) {
                addScript(stage, useCmsResource, js);
            }
        }

        addScriptInline(stage, "var require = " + ObjectUtils.toJson(ImmutableMap.of(
                "baseUrl", JspUtils.getAbsolutePath(stage.getRequest(), getBaseScriptPath(getSite(stage)) + (useMinifiedScripts(getSite(stage)) ? ".min" : "")),
                "urlArgs", "_=" + System.currentTimeMillis())) + ";");

        addScript(stage, useCmsResource, getRequireJsPath());

        if (pathToConfigScript != null) {
            addScript(stage, useCmsResource, pathToConfigScript);
        }

        if (pathToMainScript != null) {
            addScript(stage, useCmsResource, pathToMainScript);
        }
    }

    /**
     * Adds {@code <link rel="stylsheet" type="text/css" href="href">}.
     *
     * @param href If blank, does nothing.
     */
    protected final void addStyle(PageStage stage, boolean useCmsResource, String href) {
        if (!StringUtils.isBlank(href)) {

            if (useCmsResource) {
                stage.addStyleSheet(getAbsoluteStylePath(stage, href));

            } else {
                // Hack to force it not to use cms:resource AND re-use the
                // existing PageStage#addStyleSheet implementation.
                Object originalSetting = Settings.get(Boolean.class, CMS_IS_RESOURCE_IN_STORAGE_SETTING);
                try {
                    Settings.setOverride(CMS_IS_RESOURCE_IN_STORAGE_SETTING, false);

                    stage.addStyleSheet(getAbsoluteStylePath(stage, href));

                } finally {
                    Settings.setOverride(CMS_IS_RESOURCE_IN_STORAGE_SETTING, originalSetting);
                }
            }
        }
    }

    /**
     *  Adds
     * <pre>
     * {@code <style type="text/css" data-id="123">}
     *     {@code var foo = 'bar';}
     * {@code </style>}</pre>
     *
     * within the {@code <head>} element.
     * <p>
     * @param stage the current page stage
     * @param code the source css code to be included in the tag.
     */
    protected final void addStyleInline(PageStage stage, final String code) {

        if (!StringUtils.isBlank(code)) {

            HtmlElement element = stage.findOrCreateHeadElement("style",
                    "type", "text/css",
                    "data-id", JspUtils.createId(stage.getRequest()));

            element.getChildren().add(new HtmlNode() {
                @Override
                public void writeHtml(HtmlWriter writer) throws IOException {
                    writer.write(code);
                }
            });
        }
    }

    /**
     * Adds {@code <script type="text/javascript" src="src"></script>}
     * within the {@code <head>} element.
     *
     * @param stage the current page stage
     * @param useCmsResource whether to use cms:resource or not
     * @param src If blank, does nothing.
     * @param attributes additional attributes to be added to the script tag.
     */
    protected final HtmlElement addScript(PageStage stage, boolean useCmsResource, String src, Object... attributes) {
        if (!StringUtils.isBlank(src)) {

            List<Object> attributesList = new ArrayList<>();

            attributesList.add("type");
            attributesList.add("text/javascript");

            attributesList.add("src");
            attributesList.add(useCmsResource ? ElFunctionUtils.resource(getAbsoluteScriptPath(stage, src)) : getAbsoluteScriptPath(stage, src));

            if (attributes != null) {
                Collections.addAll(attributesList, attributes);
            }

            return stage.findOrCreateHeadElement("script", attributesList.toArray());

        } else {
            return null;
        }
    }

    /**
     *  Adds
     * <pre>
     * {@code <script type="text/javascript" data-id="123">}
     *     {@code var foo = 'bar';}
     * {@code </script>}</pre>
     *
     * within the {@code <head>} element.
     * <p>
     * @param stage the current page stage
     * @param code the source javascript code to be included in the tag.
     */
    protected final void addScriptInline(PageStage stage, final String code) {

        if (!StringUtils.isBlank(code)) {

            HtmlElement element = stage.findOrCreateHeadElement("script",
                    "type", "text/javascript",
                    "data-id", JspUtils.createId(stage.getRequest()));

            element.getChildren().add(new HtmlNode() {
                @Override
                public void writeHtml(HtmlWriter writer) throws IOException {
                    writer.write(code);
                }
            });
        }
    }

    /**
     * Adds
     * <pre>
     * {@code <!--[if (gte IE 6)&(lte IE 8)]>}
     *     {@code <script type="text/javascript" src="src"></script>}
     * {@code <![endif]-->}</pre>
     *
     * within the {@code <head>} element.
     * <p>
     *
     * @param stage the current page stage
     * @param useCmsResource whether to use cms:resource or not
     * @param condition if blank, behaves as {@link #addScript(com.psddev.cms.db.PageStage, boolean, String, Object...)}
     * @param src if blank, does nothing
     * @param attributes additional attributes to be added to the script tag.
     */
    protected final void addScriptConditionally(
            PageStage stage,
            boolean useCmsResource,
            String condition,
            String src,
            Object... attributes) {

        if (!StringUtils.isBlank(condition)) {

            HtmlElement scriptElement = addScript(stage, useCmsResource, src, attributes);

            if (scriptElement != null) {

                for (ListIterator<HtmlNode> i = stage.getHeadNodes().listIterator(); i.hasNext();) {
                    HtmlNode node = i.next();

                    if (node instanceof HtmlElement) {

                        HtmlElement element = (HtmlElement) node;

                        if (scriptElement.getName().equals(element.getName()) &&
                                element.hasAttributes(scriptElement.getAttributes())) {

                            List<HtmlNode> nodes = new ArrayList<>();
                            nodes.add(scriptElement);

                            Conditional conditional = new Conditional();
                            conditional.setCondition(condition);
                            conditional.setNodes(nodes);

                            i.set(conditional);

                            break;
                        }
                    }
                }
            }

        } else {
            addScript(stage, useCmsResource, src, attributes);
        }
    }

    private String getAbsoluteStylePath(PageStage stage, String stylePath) {

        String baseStylePath = getBaseStylePath(getSite(stage));

        if (useMinifiedStyles(getSite(stage))) {
            stylePath = StringUtils.replaceAll(stylePath, "^(.*)\\x2e(less|css)$", "$1") + ".min.css";
        }

        return getAbsoluteResourcePath(baseStylePath, stylePath);
    }

    private String getAbsoluteScriptPath(PageStage stage, String scriptPath) {

        String baseScriptPath = StringUtils.removeEnd(getBaseScriptPath(getSite(stage)), "/");

        if (useMinifiedScripts(getSite(stage))) {
            baseScriptPath += ".min";
        }

        return getAbsoluteResourcePath(baseScriptPath, scriptPath);
    }

    private String getAbsoluteResourcePath(String basePath, String path) {

        if (basePath == null) {
            basePath = "/";

        } else {
            basePath = StringUtils.ensureSurrounding(basePath, "/");
        }

        path = StringUtils.ensureStart(path, "/");

        // Ensures the path doesn't already contain the basePath
        if (!"/".equals(basePath) && path.startsWith(basePath)) {
            basePath = "/";
        }

        return basePath + StringUtils.removeStart(path, "/");
    }

    private String getGoogleFontApiUrl(String[][] fontsData) {

        String googleFontApiUrl = null;
        try {
            StringBuilder family = new StringBuilder();

            for (String[] fontData : fontsData) {
                if (fontData.length > 0) {

                    family.append(URLEncoder.encode(fontData[0], "UTF-8"));

                    if (fontData.length > 1) {
                        family.append(':');
                        for (int i = 1; i < fontData.length; i++) {
                            family.append(fontData[i]).append(',');
                        }
                        family.setLength(family.length() - 1);
                    }

                    family.append('|');
                }
            }
            if (family.length() > 0) {
                family.setLength(family.length() - 1);
            }

            if (family.length() > 0) {
                googleFontApiUrl = family.insert(0, GOOGLE_FONTS_API_BASE_URL).toString();
            }

        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        return googleFontApiUrl;
    }

    /**
     *
     */
    public static class Conditional extends HtmlNode {

        private String condition;
        private List<HtmlNode> nodes;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public List<HtmlNode> getNodes() {
            if (nodes == null) {
                nodes = new ArrayList<>();
            }
            return nodes;
        }

        public void setNodes(List<HtmlNode> nodes) {
            this.nodes = nodes;
        }

        @Override
        public void writeHtml(HtmlWriter writer) throws IOException {
            if (getCondition() != null) {
                writer.write("\n<!--[if " + getCondition() + "]>");
                for (HtmlNode node : getNodes()) {
                    node.writeHtml(writer);
                }
                writer.write("\n<![endif]-->\n");
            }
        }
    }
}

