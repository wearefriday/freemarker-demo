<%@ include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<jsp:useBean id="object" scope="request" type="com.psddev.freemarker.model.Image" />

<div class="fm-image">

    <c:out value="${object.title}" />

    <cms:img src="${object.file}" height="200" />

</div>