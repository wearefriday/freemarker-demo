<%@ include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%@ page import="com.psddev.freemarker.app.LocalizationFilter" %>
<%@ page import="com.psddev.freemarker.i18n.Messages" %>

<jsp:useBean id="object" scope="request" type="com.psddev.freemarker.model.Author" />

<div class="fm-author">

    <b><%= Messages.COM_PSDDEV_FREEMARKER_MODEL_AUTHOR_NAME.valueFor(LocalizationFilter.Static.getLocale(request)) %>:</b> <c:out value="${object.firstName}" /> <c:out value="${object.lastName}" />

</div>
