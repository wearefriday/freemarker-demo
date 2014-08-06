package com.psddev.freemarker.model;

import com.psddev.cms.db.Renderer;

@Renderer.Paths(
        @Renderer.Path("/WEB-INF/renderer/content/author.ftl")
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
