<%@ include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<jsp:useBean id="mainObject" scope="request" type="com.psddev.freemarker.model.Content" />
<jsp:useBean id="stage" scope="request" type="com.psddev.cms.db.PageStage" />

<!DOCTYPE html>
<html class="fm-html">

<head>
    <cms:render value="${stage.headNodes}" />
</head>

<body class="fm-body">
