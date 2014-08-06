package com.psddev.freemarker.i18n;

import com.psddev.dari.util.Settings;

import ch.qos.cal10n.MessageConveyor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Locale;

public final class MessageConveyorCache {

    private static final LoadingCache<Locale, MessageConveyor> MESSAGE_CONVEYORS = CacheBuilder.newBuilder()
            .build(new CacheLoader<Locale, MessageConveyor>() {
                @Override
                public MessageConveyor load(Locale locale) {
                    return new MessageConveyor(locale);
                }
            });

    /**
     * Returns a MessageConveyor instance for the give locale.
     * @param locale the current locale.
     * @return Never null
     */
    public static MessageConveyor getMessageConveyor(Locale locale) {
        if (Settings.isProduction()) {
            return MESSAGE_CONVEYORS.getUnchecked(locale);
        } else {
            return new MessageConveyor(locale);
        }
    }
}
