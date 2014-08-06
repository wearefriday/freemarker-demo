[#include "/WEB-INF/common/taglibs.ftl" /]

[#-- @ftlvariable name="mainObject" type="com.psddev.freemarker.model.Content" --]
[#-- @ftlvariable name="stage" type="com.psddev.cms.db.PageStage" --]

<!DOCTYPE html>
<html class="fm-html">

    <head>
        [@cms.render value=stage.headNodes /]
    </head>

<body class="fm-body">
