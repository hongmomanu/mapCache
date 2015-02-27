
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Enumeration"%>
<%@ page import="java.io.*" %>

<%@page import="mapcatche.conmmon.*"%>
<%@page import="mapcatche.business.fact.*;"%>
<%@ page language="java"  contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
                  
                 //  System.out.println(request.getRequestURI().split("/")[3]);
if(request.getParameter("taskid")==null){
}
else{
    
    
	Map<String,Object> params=new HashMap<String,Object>();
	response.setContentType("image/png");
	/*
	*获取请求参数
	*/
	Enumeration  e  =(Enumeration) request.getParameterNames();
    while(e.hasMoreElements()){   
    		String  parName=(String)e.nextElement();
    		params.put(parName,request.getParameter(parName));
    }
    
    
     response.setContentType("application/x-download");  
     
     String filename=request.getParameter("name")+".png";
 response.setHeader("Location",filename);  
 //response.setHeader("Cache-Control", "max-age=" + cacheTime);  
 response.setHeader("Content-Disposition", "attachment; filename=" + filename); //filename应该是编码后的(utf-8)  
 //response.setContentLength(filelength);  
 OutputStream outputStream = response.getOutputStream();  
    byte[] data=MapcacheFactory.DownTileimg(params);
    
    InputStream in = new ByteArrayInputStream(data);  
    int len;  
    byte[] buf = new byte[1024];  
        
    while ((len = in.read(buf)) != -1) {  
    	outputStream.write(buf, 0, len);  
             }  
    
 
 byte[] buffer = new byte[1024];  
 int i = -1;  
 while ((i = in.read(buffer)) != -1) {  
  outputStream.write(buffer, 0, i);  
  }  
 
    
  
 outputStream.flush();  
 out.clear();  
 outputStream.close();  
 in.close();  
out = pageContext.pushBody();  
 outputStream = null;  
}
   
%>
