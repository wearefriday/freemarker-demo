[#-- @ftlvariable name="wpc" type="com.psddev.freemarker.app.WebPageContext" --]
[#assign cms=JspTaglibs["http://psddev.com/cms"]]
[#assign c=JspTaglibs["http://java.sun.com/jsp/jstl/core"]]
[#macro include path]
    [#if wpc.isProduction]
        [#include path]
    [#else]
        [@include_page path=path/]
    [/#if]
[/#macro]

[#--
     CMS Image Macro

     ex: [@image object=object.image size="rectangle-medium" context="responsive" lazy="true"/]
 --]
[#macro image object options...]
    [@cms.local]
        [#list options?keys as param]
            [@cms.attr name="${param}" value="${options[param]}" /]
        [/#list]
        [@cms.render value=object context="${context}" /]
    [/@cms.local]
[/#macro]
