package com.psddev.freemarker.ftl;

import com.psddev.freemarker.i18n.MessageConveyorCache;

import ch.qos.cal10n.MessageConveyor;
import ch.qos.cal10n.MessageParameterObj;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

import java.util.Locale;

public class LocalizedEnumModel extends BeanModel implements TemplateScalarModel {

    private Enum<?> localizedEnum;
    private MessageParameterObj messageParameterObj;
    private final Locale locale;

    public LocalizedEnumModel(Enum<?> localizedEnum, Locale locale, BeansWrapper wrapper) {
        super(localizedEnum, wrapper);
        this.localizedEnum = localizedEnum;
        this.locale = locale;
    }

    public LocalizedEnumModel(MessageParameterObj messageParameterObj, Locale locale, BeansWrapper wrapper) {
        super(messageParameterObj, wrapper);
        this.messageParameterObj = messageParameterObj;
        this.locale = locale;
    }

    @Override
    public String getAsString() throws TemplateModelException {

        String value;

        MessageConveyor messageConveyor = MessageConveyorCache.getMessageConveyor(locale);

        if (localizedEnum != null) {
            value = messageConveyor.getMessage(localizedEnum);
        } else {
            value = messageConveyor.getMessage(messageParameterObj);
        }

        return value;
    }
}
