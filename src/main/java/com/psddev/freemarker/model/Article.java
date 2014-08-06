package com.psddev.freemarker.model;

import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Renderer;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.ReferentialText;
import com.psddev.dari.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Renderer.Paths(
        @Renderer.Path("/WEB-INF/ftl/renderer/mainContent/article.ftl")
//        @Renderer.Path("/WEB-INF/jsp/renderer/mainContent/article.jsp")
)
public class Article extends Content implements Directory.Item {

    @Indexed
    private String title;

    @Indexed
    private Author primaryAuthor;

    @Indexed
    private List<Author> authors;

    private ReferentialText body;

    private Image image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getPrimaryAuthor() {
        return primaryAuthor;
    }

    public void setPrimaryAuthor(Author primaryAuthor) {
        this.primaryAuthor = primaryAuthor;
    }

    public List<Author> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<>();
        }
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public ReferentialText getBody() {
        return body;
    }

    public void setBody(ReferentialText body) {
        this.body = body;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String createPermalink(Site site) {
        if (title != null) {
            return "/articles/" + StringUtils.toNormalized(title);
        } else {
            return null;
        }
    }
}
