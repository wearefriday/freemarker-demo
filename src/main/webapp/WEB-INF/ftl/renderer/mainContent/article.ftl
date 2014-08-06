[#include "/WEB-INF/ftl/common/taglibs.ftl" /]

[#-- @ftlvariable name="Constants" type="com.psddev.freemarker.app.Constants" --]
[#-- @ftlvariable name="Messages" type="com.psddev.freemarker.i18n.Messages" --]
[#-- @ftlvariable name="mainObject" type="com.psddev.freemarker.model.Article" --]

<div class="fm-article">

    <b>Intro:</b> ${Messages.HELLO} - ${Constants.DARI_MESSAGE}


    <div class="fm-article-title">
        <b>Title:</b>
        ${mainObject.title!"No Title!"}
    </div>


    <div class="fm-article-primaryAuthor">
        <b>Primary Author:</b>
        ${(mainObject.primaryAuthor.firstName)!"No Primary Author!"}
    </div>


    <b>Authors:</b>
    [#list mainObject.authors as author]
        <div class="fm-article-author">
            [@cms.render value=author /]
        </div>
    [/#list]


    [#if mainObject.body??]
        <div class="fm-article-body">
            [@cms.render value=mainObject.body /]
        </div>
    [/#if]


    [#if mainObject.image??]
        <b>Image: </b>
        <div class="fm-article-image">
            [@cms.render value=mainObject.image /]
        </div>
    [/#if]


    <b>Outro:</b> ${Messages.GOODBYE} - ${Constants.BSP_MESSAGE}

</div>
