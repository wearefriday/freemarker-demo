package com.psddev.freemarker.model;

import com.psddev.cms.db.PageStage;
import com.psddev.cms.db.Renderer;
import com.psddev.freemarker.app.PageStageUpdater;

@PageStage.UpdateClass(PageStageUpdater.class)
@Renderer.LayoutPath("/WEB-INF/ftl/renderer/layout/default.ftl")
//@Renderer.LayoutPath("/WEB-INF/jsp/renderer/layout/default.jsp")
public class Content extends com.psddev.cms.db.Content {

    // nothing here yet...
}
