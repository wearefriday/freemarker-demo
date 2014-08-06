<%@ include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%@ page import="com.psddev.freemarker.app.Constants" %>
<%@ page import="com.psddev.freemarker.app.LocalizationFilter" %>
<%@ page import="com.psddev.freemarker.i18n.Messages" %>

<jsp:useBean id="mainObject" scope="request" type="com.psddev.freemarker.model.Article" />

<div class="fm-article">

    <b>Intro:</b> <%= Messages.HELLO.valueFor(LocalizationFilter.Static.getLocale(request)) %> - <%= Constants.DARI_MESSAGE %>

    <div class="fm-article-title">
        <b>Title:</b>
        <c:out value="${not empty mainObject.title ? mainObject.title : 'No Title!'}" />
    </div>


    <div class="fm-article-primaryAuthor">
        <b>Primary Author:</b>
        <c:out value="${not empty mainObject.primaryAuthor.firstName ? mainObject.primaryAuthor.firstName : 'No Primary Author!'}" />
    </div>


    <b>Authors:</b>
    <c:forEach items="${mainObject.authors}" var="author">
        <div class="fm-article-author">
            <cms:render value="${author}" />
        </div>
    </c:forEach>


    <c:if test="${not empty mainObject.body}">
        <div class="fm-article-body">
            <cms:render value="${mainObject.body}" />
        </div>
    </c:if>


    <c:if test="${not empty mainObject.image}">
        <b>Image: </b>
        <div class="fm-article-image">
            <cms:render value="${mainObject.image}" />
        </div>
    </c:if>


    <b>Outro:</b> <%= Messages.GOODBYE.valueFor(LocalizationFilter.Static.getLocale(request)) %> - <%= Constants.BSP_MESSAGE %>

</div>
