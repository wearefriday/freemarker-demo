[#include "/WEB-INF/ftl/common/taglibs.ftl" /]

[#-- @ftlvariable name="object" type="com.psddev.freemarker.model.Image" --]

<div class="fm-image">

    ${object.title!}

    [@cms.img src=object.file height="200" /]

</div>
