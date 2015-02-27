
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Enumeration"%>
<%@ page import="java.io.*" %>

<%@page import="mapcatche.conmmon.*"%>
<%@page import="mapcatche.business.fact.MapcacheFactory"%>
<%@ page language="java"  contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

                 //  System.out.println(request.getRequestURI().split("/")[3]);
if(request.getParameter("X")==null){

out.print("缺少X参数");
}else if(request.getParameter("Y")==null){
out.print("缺少Y参数");
}else if(request.getParameter("L")==null){
out.print("缺少L参数");
}
else{


	Map<String,Object> params=new HashMap<String,Object>();
	response.setContentType("image/jpeg");
	/*
	*获取请求参数
	*/
	Enumeration  e  =(Enumeration) request.getParameterNames();
    while(e.hasMoreElements()){   
    		String  parName=(String)e.nextElement();
    		params.put(parName,request.getParameter(parName));
    }
    
    /*将图片字节写入页面*/ 
    OutputStream outStream = response.getOutputStream();  
    byte[] data=MapcacheFactory.Readmapfactory(params);
    /*
           访问错误处理
    */
    if(data==null||data.length==0){
    	String errorPath = request.getSession().getServletContext().getRealPath(
    		     "/")+File.separator+"img/" + File.separator+"nothing.png";
    	data=ErrorFunc.errorMapTitle(errorPath);
    }
    InputStream in = new ByteArrayInputStream(data);  
    int len;  
    byte[] buf = new byte[1024];  
        
    while ((len = in.read(buf)) != -1) {  
    	outStream.write(buf, 0, len);  
             }  
    
    outStream.flush();  
    out.clear();  
    out = pageContext.pushBody();  
       }
    
%>
