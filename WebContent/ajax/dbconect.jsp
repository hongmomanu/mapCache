<%-- 
    Document   : maptree
    Created on : 2013-1-7, 11:33:55
    Author     : jack
--%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Enumeration"%>
<%@page import="mapcatche.business.fact.MapcacheFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

    if (request.getParameter("username") == null || request.getParameter("username").replaceAll(" ", "") == "") {
        out.print("缺少参数username");
    } else if (request.getParameter("url") == null || request.getParameter("url").replaceAll(" ", "") == "") {
        out.print("缺少参数url");
    } else {
        Map<String, Object> params = new HashMap<String, Object>();
        /*
         *获取请求参数
         */
        Enumeration e = (Enumeration) request.getParameterNames();
        while (e.hasMoreElements()) {
            String parName = (String) e.nextElement();
            params.put(parName, request.getParameter(parName));
        }
        out.print(MapcacheFactory.Readdbconenct(params));
    }
%>
