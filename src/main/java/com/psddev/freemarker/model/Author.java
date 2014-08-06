package com.psddev.freemarker.model;

import com.psddev.cms.db.Renderer;

@Renderer.Paths(
        @Renderer.Path("/WEB-INF/ftl/renderer/content/author.ftl")
//        @Renderer.Path("/WEB-INF/jsp/renderer/content/author.jsp")
)
public class Author extends Content {

    @Required
    @Indexed
    private String firstName;

    @Indexed
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
