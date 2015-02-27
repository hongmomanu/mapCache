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

 
    if (request.getParameter("maplabel") == null || request.getParameter("maplabel").replaceAll(" ", "") == "") {
        out.print("缺少参数");
    } else  {
        
        out.print(MapcacheFactory.ReadmapConfig(request.getParameter("maplabel") ));
    }
%>
