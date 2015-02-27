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


	Map<String,Object> params=new HashMap<String,Object>();
	/*
	*获取请求参数
	*/
	Enumeration  e  =(Enumeration) request.getParameterNames();
    while(e.hasMoreElements()){   
    		String  parName=(String)e.nextElement();
                                      if(parName.equals("layerlevel")){
                                          params.put(parName,request.getParameterValues(parName));
                                      }else{
                                      params.put(parName,request.getParameter(parName));
                                      }
    		
    }
        if(request.getParameter("taskid")!=null&&(!request.getParameter("taskid").replaceAll(" ", "").equals(""))){
            if(request.getParameter("type")!=null&&(request.getParameter("type").replaceAll(" ", "").equals("del"))){
             out.print(MapcacheFactory.deltask(Integer.parseInt(request.getParameter("taskid"))));
            }else if(request.getParameter("type")!=null&&(request.getParameter("type").replaceAll(" ", "").equals("cancel"))){
                out.print(MapcacheFactory.canceltask(Integer.parseInt(request.getParameter("taskid"))));
            
            }else{
                out.print(MapcacheFactory.refinishtask(Integer.parseInt(request.getParameter("taskid"))));
            }
            
        }else{
            out.print(MapcacheFactory.Imptilefactory(params));
        }
   
%>
