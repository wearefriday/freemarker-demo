package com.psddev.freemarker.app;

import com.psddev.cms.db.Directory;
import com.psddev.cms.db.PageStage;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.State;
import com.psddev.freemarker.util.AbstractPageStageUpdater;

public class PageStageUpdater extends AbstractPageStageUpdater {

    @Override
    protected String getBaseStylePath(Site site) {
        return "/assets/style";
    }

    @Override
    protected String getBaseScriptPath(Site site) {
        return "/assets/script";
    }

    @Override
    protected String getBaseFontPath(Site site) {
        return getBaseStylePath(site) + "/fonts";
    }

    @Override
    protected boolean useMinifiedStyles(Site site) {
        return true;
    }

    @Override
    protected boolean useMinifiedScripts(Site site) {
        return true;
    }

    @Override
    protected void doUpdateStageBefore(Object object, PageStage stage) {

        State state = State.getInstance(object);

        addStylesForLessJs(stage, true, "/freemarker.less");

        addAllScriptsForRequireJs(stage,
                "/config.js", // requireJS config
                "/freemarker.js", // main JS
                "/jquery.js"); // shims

        stage.findOrCreateHeadElement("meta", "charset", "UTF-8");

        stage.setCanonicalUrl(state.as(Directory.ObjectModification.class).getPermalink());
    }

    @Override
    protected void doUpdateStageAfter(Object object, PageStage stage) {
        // nothing to do...
    }
}
