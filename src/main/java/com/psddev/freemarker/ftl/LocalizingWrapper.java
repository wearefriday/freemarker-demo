package com.psddev.freemarker.ftl;

import com.psddev.freemarker.app.LocalizationFilter;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.MessageParameterObj;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class LocalizingWrapper extends BeansWrapper {

    private transient HttpServletRequest request;

    public LocalizingWrapper() {
    }

    void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public TemplateModel wrap(Object object) throws TemplateModelException {

        if (object instanceof Enum && object.getClass().isAnnotationPresent(BaseName.class)) {
            return new LocalizedEnumModel((Enum<?>) object, getLocale(), this);
        }

        if (object instanceof MessageParameterObj) {
            return new LocalizedEnumModel((MessageParameterObj) object, getLocale(), this);
        }

        return super.wrap(object);
    }

    private Locale getLocale() {

        Locale locale = null;
        if (request != null) {
            locale = LocalizationFilter.Static.getLocale(request);
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }
}
