<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.chicm.hieroglyph.*" import="java.net.URL" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>古埃及圣书体在线翻译</title>

</head>
<%String prefix = "http://" + request.getServerName() + ":" + request.getServerPort()
+ request.getContextPath();
System.out.println("context:" + prefix);%>

<body background="<%=prefix %>/pic/bg.jpg">

<center><h1><b>古埃及圣书体/象形文字在线翻译</b></h1></center>

<form method="post" action="translator.jsp" >

<% String text = ""; %>
<input name="text" type="text" size="20" value=<%=text %>> <input type="submit">

<% request.setCharacterEncoding("UTF-8"); 
 text = request.getParameter("text"); 
  
 System.out.println("context path:" + request.getContextPath());
 System.out.println("url:" + request.getRequestURL());
 System.out.println("servername:" + request.getServerName());
 System.out.println("serverport:" + request.getServerPort());
  
 %>


<% if(text != null && text.length() > 0) {
	out.println("<br>");
		String pinyin = Chinese2Pinyin.convert(text);
		String2Hieroglyph convertor = new String2Hieroglyph(pinyin, request);
		while(convertor.hasNext()) {
			Object obj = convertor.next();
			if(obj instanceof URL)
				out.println("<img src=\"" + obj.toString() + "\"/>");
			else
				out.println(obj.toString());
		}
}%>

</form>
</body>
</html>