package com.psddev.freemarker.model;

import com.psddev.cms.db.Renderer;
import com.psddev.dari.util.StorageItem;

@Renderer.Paths(
        @Renderer.Path("/WEB-INF/renderer/content/image.ftl")
)
public class Image extends Content {

    @Indexed
    private String title;

    private StorageItem file;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StorageItem getFile() {
        return file;
    }

    public void setFile(StorageItem file) {
        this.file = file;
    }
}
