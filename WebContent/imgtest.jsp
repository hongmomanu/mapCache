<%@ page language="java"  contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="mapcatche.implmapfunc.ImplToPg" %>

<%
ImplToPg test=new ImplToPg();
String imgurl="http://tile5.tianditu.com/DataServer?T=B0627_EMap1112&X=1706&Y=341&L=11";
response.setContentType("image/jpeg"); 
OutputStream outStream = response.getOutputStream();  
byte[] data=test.readUrlmap(imgurl);
outStream.write(data, 0,data.length);  
outStream.flush();  
outStream.close();  
outStream=null;
response.flushBuffer();
out.clear();
out = pageContext.pushBody();

//out.print(test.readUrlmap(imgurl));
%>