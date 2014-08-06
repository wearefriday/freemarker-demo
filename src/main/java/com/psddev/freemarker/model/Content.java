package com.psddev.freemarker.model;

import com.psddev.cms.db.PageStage;
import com.psddev.cms.db.Renderer;
import com.psddev.freemarker.app.PageStageUpdater;

@PageStage.UpdateClass(PageStageUpdater.class)
@Renderer.LayoutPath("/WEB-INF/renderer/layout/default.ftl")
public class Content extends com.psddev.cms.db.Content {

    // nothing here yet...
}
