<%@ include file="/WEB-INF/jsp/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<jsp:useBean id="mainObject" scope="request" type="com.psddev.freemarker.model.Content" />

<jsp:include page="/WEB-INF/jsp/common/pageStart.jsp" />

<div class="fm-pageBody">
    <div class="fm-pageContainer">

        <jsp:include page="/WEB-INF/jsp/common/pageHeader.jsp" />

        <div class="fm-pageContent">
            <cms:render value="${mainObject}" />
        </div>

        <jsp:include page="/WEB-INF/jsp/common/pageFooter.jsp" />

    </div>
</div>

<jsp:include page="/WEB-INF/jsp/common/pageEnd.jsp" />
