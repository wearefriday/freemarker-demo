[#include "/WEB-INF/common/taglibs.ftl" /]

[#-- @ftlvariable name="Messages" type="com.psddev.freemarker.i18n.Messages" --]
[#-- @ftlvariable name="object" type="com.psddev.freemarker.model.Author" --]

<div class="fm-author">

    <b>${Messages.COM_PSDDEV_FREEMARKER_MODEL_AUTHOR_NAME}:</b> ${object.firstName!} ${object.lastName!}

</div>
