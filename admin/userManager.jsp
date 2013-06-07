<%--
    Document    : userManager.jsp
    Created on  : 2013/03/03
    Author      : Jeff Lee
    Description : Manages user accounts
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ece1779.ec2.models.User"%>

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
							<li class="active dropdown">
								<a href="#" icon="cogs" class="dropdown-toggle" data-toggle="dropdown">
									Admin Panel
									<b icon="caret"></b>
								</a>
								<ul class="dropdown-menu">
									<li class="active"><a href="UserManager" icon="group">Manage Users</a></li>
									<li><a href="LoadManager" icon="sitemap">Load Balance</a></li>
								</ul>
							</li>
							<li class="dropdown">
								<a href="#" icon="picture" class="dropdown-toggle" data-toggle="dropdown">
									My Pictures
									<b icon="caret"></b>
								</a>
								<ul class="dropdown-menu">
								<% if (session.getAttribute("user") == null) { %>
									<li><a href="../user/Login" icon="lock">User Login</a></li>	
								<% } else { %>
									<li><a href="../user/ListImages" icon="eye-open">View Pictures</a></li>
									<li><a href="../user/FileUpload" icon="upload-alt">Upload Picture</a></li>
								<% } %>
								</ul>
							</li>
							<li><a href="../Logout" icon="off">Log Out Manager</a></li>
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
				<h2 icon="group">Users</h2>
				
				<%
					ArrayList<User> users= (ArrayList<User>) request.getAttribute("users");
				%>
				<table class="table-striped table-bordered">
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th>User ID</th>
							<th>Username</th>
							<th>Image Count</th>
						</tr>
					</thead>
					
					<tbody>
					<% for (User user : users) { %>
						<tr>
							<td>
								<a href="#" onClick="alert('Function not implemented yet.');return false;" icon="pencil">Change Password</a>&nbsp;
								<a href="#" onClick="alert('Function not implemented yet.');return false;" icon="remove">Delete</a>
							</td>
							<td><%=user.getId() %></td>
							<td><%=user.getUsername() %></td>
							<td><%=user.getImageCount() %></td>
						</tr>
					<% } %>
					</tbody>
				</table>
			</div>
			<footer>
				&copy;2013 <a href="#" target="_blank">Group 13</a>
			</footer>
		</div>
	</body>
</html>