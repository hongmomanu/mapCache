<%-- 
    Document   : index
    Created on : 2013-1-26, 12:51:25
    Author     : jack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户登入</title>

<link rel="stylesheet" type="text/css" href="css/login.css" />

<script type="text/javascript">
    function checkPassword(p1, p2)
   {
     if (p1.value != p2.value) {
       p2.setCustomValidity('密码不匹配');
     } else {
       p2.setCustomValidity('');
     }
   }
     
</script>

</head>

<body>
    
    <header class="body">
        <%
if(request.getSession().getAttribute("usertype")==null||Integer.parseInt(request.getSession().getAttribute("usertype").toString())!=0){
    //out.print("对不起，只有管理员有权限访问此页");
     response.sendRedirect("authoritylimit.jsp");
    
}        
%>
        <h1>
            <a style="padding-right:10px;text-decoration:none;" href="login.jsp">用户登录</a>
            <a style="padding-right:10px; color:white; text-decoration:none;"  href="register.jsp">新用户</a>
        </h1>
        <h2 ><%= request.getSession().getAttribute("registererromsg")==null?"":request.getSession().getAttribute("registererromsg")%></h2>
        <%request.getSession().setAttribute("registererromsg", null);%>
    </header>
    <section class="body">
        <form method="post" action="register">
            <label>用户名</label>
            <input name="username" required="true"  placeholder="输入用户名">
            <label>密码</label>
            <input name="password" required="true" id="p1" placeholder="输入密码" type="password">
            <label>重复密码</label>
            <input name="passwordd" required="true" 
                   onfocus="checkPassword(document.getElementById('p1'),this);" 
                   oninput="checkPassword(document.getElementById('p1'), this);" placeholder="重复密码"
                   type="password">
            <input id="submit" type="submit" value="登录" name="submit">
        </form>
        
    </section>
		
    
</body>
</html>