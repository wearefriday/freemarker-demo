[#include "/WEB-INF/common/taglibs.ftl" /]

[#-- @ftlvariable name="mainObject" type="com.psddev.freemarker.model.Content" --]

[@include path="/WEB-INF/common/pageStart.ftl" /]

<div class="fm-pageBody">
    <div class="fm-pageContainer">

        [@include path="/WEB-INF/common/pageHeader.ftl"/]

        <div class="fm-pageContent">
            [@cms.render value=mainObject /]
        </div>

        [@include path="/WEB-INF/common/pageFooter.ftl"/]

    </div>
</div>

[@include path="/WEB-INF/common/pageEnd.ftl" /]
