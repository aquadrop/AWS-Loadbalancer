<%--
    Document    : login.jsp
    Created on  : 2013/03/03
    Author      : Jeff Lee
    Description : Login page
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Collection"%>
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
					
					<a href="Welcome" class="brand">ECE 1779 - Assignment 1</a>
					
					<div class="nav-collapse pull-right">
						<ul class="nav">
							<li><a href="Register" icon="plus">Register</a></li>
							<li><a href="../admin/Login" icon="lock">Manager Login</a></li>
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
				<h2>User Login</h2>
				<form action="Login" method="post" accept-charset="utf-8">
					<div class="input text">
						<label for="username">Username</label>
						<input name="username" placeholder="Enter Your Username" type="text" id="username" maxlength="60" />
					</div>
					
					<div class="input password">
						<label for="password">Password</label>
						<input name="password" placeholder="Enter Your Password" type="password" id="password" />
					</div>
					
					<div class="submit">
						<input type="submit" value="Login" />
					</div>
				</form>
				
				<script type="text/javascript">
					$(document).ready(function() {
						$('#username').focus();
					});
				</script>
			</div>
			<footer>
				&copy;2013 <a href="#" target="_blank">Group 13</a>
			</footer>
		</div>
	</body>
</html>