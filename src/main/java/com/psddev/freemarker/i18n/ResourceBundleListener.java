package com.psddev.freemarker.i18n;

import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ResourceBundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceBundleListener.class);

    private static final String GROOVY_SCRIPT_NAME = "ResourceBundleEnumGenerator.groovy";

    private static final String RESOURCE_BUNDLE_PATH_PROPERTY = "resourceBundlePath";
    private static final String ENUM_PACKAGE_NAME_PROPERTY = "enumPackageName";
    private static final String ENUM_INTERFACE_MARKERS_PROPERTY = "interfaceMarkers";

    private static final String RESOURCE_BUNDLE_PATH = "bundles";
    private static final String ENUM_PACKAGE_NAME = "com.psddev.freemarker.i18n";
    private static final String ENUM_INTERFACE_MARKERS = "com.psddev.freemarker.ftl.FreemarkerServlet.EnumModel";

    public static void updateResourceBundleEnums() throws IOException {

        URL groovyScript = ResourceBundleListener.class.getClassLoader().getResource(GROOVY_SCRIPT_NAME);
        if (groovyScript != null) {

            GroovyScriptEngine scriptEngine = new GroovyScriptEngine(new URL[] { groovyScript });

            Binding binding = new Binding();
            binding.setVariable("log", LoggerFactory.getLogger(GROOVY_SCRIPT_NAME));
            binding.setVariable("project", ImmutableMap.of("properties", ImmutableMap.of(
                    RESOURCE_BUNDLE_PATH_PROPERTY, RESOURCE_BUNDLE_PATH,
                    ENUM_PACKAGE_NAME_PROPERTY, ENUM_PACKAGE_NAME,
                    ENUM_INTERFACE_MARKERS_PROPERTY, ENUM_INTERFACE_MARKERS
            )));
            try {
                scriptEngine.run(GROOVY_SCRIPT_NAME, binding);

            } catch (ResourceException | ScriptException e) {
                LOGGER.error(e.getMessage(), e);
            }

        } else {
            LOGGER.error("Could not find groovy script [" + GROOVY_SCRIPT_NAME + "] on classpath.");
        }
    }
}
