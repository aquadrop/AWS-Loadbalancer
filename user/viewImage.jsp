<!-- DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>View Image by User ID</title>
	</head>

	<body>
		<h1>View Image by User ID</h1>
	    <form action="ImageView" method="post">
			<label for="userID">User ID:</label>
			<input type="text" name="userID" id="userID" />
			
		   	<input type="submit" value="View Image" />
	    </form>
	</body>
</html-->

<%--
    Document    : viewImage.jsp
    Created on  : 2013/03/09
    Author      : Jeff Lee
    Description : View single image with different transformations
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="ece1779.ec2.models.Image"%>

<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<title>ECE 1779 - Assignment 1</title>
		
		<link rel="stylesheet" type="text/css" href="../css/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
		<link rel="stylesheet" type="text/css" href="../css/bootstrap-responsive.min.css" />
		<link rel="stylesheet" type="text/css" href="../css/jquery.chosen.css" />
		<link rel="stylesheet" type="text/css" href="https://code.leejefon.com/font-awesome/css/font-awesome.min.css" />
		
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
		<script type="text/javascript" src="https://code.leejefon.com/jquery.bootstrap.min.js"></script>
		<script type="text/javascript" src="https://code.leejefon.com/jquery.chosen.min.js"></script>
		<script type="text/javascript" src="https://code.leejefon.com/jquery.autosize.min.js"></script>
		<script type="text/javascript" src="https://code.leejefon.com/jquery.cakeBootstrap.js"></script>
		<script type="text/javascript" src="../js/script.js"></script>
	</head>
	
	<body data-spy="scroll" data-target=".subnav" data-offset="50">
		<div class="navbar navbar-fixed-top">
			<div class="navbar-inner">
				<div class="container">
					<a data-target=".nav-collapse" data-toggle="collapse" class="btn btn-navbar">
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</a>
					
					<a href="../Welcome" class="brand">ECE 1779 - Assignment 1</a>
					
					<div class="nav-collapse pull-right">
						<ul class="nav">
							<li class="dropdown">
								<a href="#" icon="cogs" class="dropdown-toggle" data-toggle="dropdown">
									Admin Panel
									<b icon="caret"></b>
								</a>
								<ul class="dropdown-menu">
								<% if (session.getAttribute("admin") == null) { %>
									<li><a href="../admin/Login" icon="lock">Manager Login</a></li>	
								<% } else { %>
									<li><a href="../admin/UserManager" icon="group">Manage Users</a></li>
									<li><a href="../admin/LoadManager" icon="sitemap">Load Balance</a></li>
								<% } %>
								</ul>
							</li>
							<li class="active dropdown">
								<a href="ViewImage" icon="picture" class="dropdown-toggle" data-toggle="dropdown">
									My Pictures
									<b icon="caret"></b>
								</a>
								<ul class="dropdown-menu">
									<li><a href="ListImages" icon="eye-open">View Pictures</a></li>
									<li><a href="FileUpload" icon="upload-alt">Upload Picture</a></li>
								</ul>
							</li>
							<li><a href="../Logout" icon="off">Log Out</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		
		<div class="container">
			<header></header>
			
			<% if (session.getAttribute("error-message") != null) { %>
				<div id="flashMessage" class="flash-error alert alert-error">
					<%=session.getAttribute("error-message") %>
				</div>
			<% 	session.removeAttribute("error-message"); } %>
			
			<div id="content">
				<h5><a icon="backward" href="ListImages">Back to index</a></h5>
			
				<h2 icon="eye-open">View Image</h2>	
				
				<%
					Image image = (Image) request.getAttribute("imageObj");
					if (image == null) {
						response.sendRedirect(response.encodeRedirectURL("ListImages"));
					}
					
					HashMap<String, String> keys = image.getKeys();
				%>
				
				<div class="row">
					<div class="span12">
						<h4>Original Image</h4>
						<img src="https://s3.amazonaws.com/uoftece1779winter2013/<%=keys.get("key1") %>" />	
					</div>
				</div>
				
				<div class="row">
					<div class="span4">
						<h4>Flipped</h4>
						<img src="https://s3.amazonaws.com/uoftece1779winter2013/<%=keys.get("key2") %>" />					
					</div>
					
					<div class="span4">
						<h4>Flopped</h4>
						<img src="https://s3.amazonaws.com/uoftece1779winter2013/<%=keys.get("key3") %>" />					
					</div>
					
					<div class="span4">
						<h4>Resized</h4>
						<img src="https://s3.amazonaws.com/uoftece1779winter2013/<%=keys.get("key4") %>" />
					</div>
				</div>
			</div>
			
			<footer>
				&copy;2013 <a href="#" target="_blank">Group 13</a>
			</footer>
		</div>
	</body>
</html>