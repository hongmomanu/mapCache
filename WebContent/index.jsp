<%-- 
    Document   : index
    Created on : 2013-1-6, 12:51:25
    Author     : jack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
 if(request.getSession().getAttribute("username")==null) {
                    response.sendRedirect("login.jsp");
  }
        
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>地图缓存服务管理配置</title>

 <link rel="stylesheet" href="js/OpenLayers-2.12/theme/default/style.css" type="text/css">
  <link rel="stylesheet" href="css/style.css" type="text/css">
<link rel="stylesheet" type="text/css" href="js/ext-4.1.1a/resources/css/ext-all-gray.css" />

<style type="text/css">
 
.x-tree-icon { display: none !important; }
.feed-list-item {
            margin-top: 3px;
            padding-left: 20px;
            font-size: 11px;
            line-height: 20px;
            cursor: pointer;
/*            background: url(images/rss.gif) no-repeat 0 2px;*/
            border: 1px solid #fff;
        }

        .feed-list .x-item-selected {
            font-weight: bold;
            color: #15428B;
            background-color: #DFE8F6;
            border: 1px dotted #A3BAE9;
        }

        .feed-list-item-hover {
            background-color: #eee;
        }
</style>
 <script src="js/config.js" type="text/javascript"></script>
    <script src="js/OpenLayers-2.12/OpenLayers.js"></script>
    <script src="js/openlayers_tiandi.js" type="text/javascript"></script>
      <script src="js/openlayers_google.js" type="text/javascript"></script>
 
<script type="text/javascript" src="js/ext-4.1.1a/ext-all-debug-w-comments.js"></script>
<script type="text/javascript" src="js/ext-4.1.1a/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/layout.js"> </script>
<script type="text/javascript" src="js/map.js"> </script>
<script type="text/javascript" src="js/feedPanel.js"> </script>
<script type="text/javascript" src="js/initFunc.js"></script>
<script type="text/javascript">
    //Ext.util.CSS.swapStyleSheet('theme','js/ext-4.1.1a/resources/css/ext-all-gray.css');更改主题
        <% String hello="hello world!";%>
            var userobj={};
            userobj.username="<%=request.getSession().getAttribute("username")%>";
            userobj.logintime="<%=request.getSession().getAttribute("logintime")%>";
</script>

</head>
<body>
     
    <div id="center" style="width: 100%;height: 100%;">
    </div>
   
</body>
</html>