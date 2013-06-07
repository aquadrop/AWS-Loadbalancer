<%--
    Document    : listImages.jsp
    Created on  : 2013/03/03
    Author      : Jeff Lee
    Description : List all the images uploaded
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
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
								<a href="#" icon="picture" class="dropdown-toggle" data-toggle="dropdown">
									My Pictures
									<b icon="caret"></b>
								</a>
								<ul class="dropdown-menu">
									<li class="active"><a href="ListImages" icon="eye-open">View Pictures</a></li>
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

			<div id="content">
				<h2 icon="picture">My Images</h2>
				
				<%
					ArrayList<Image> images = (ArrayList<Image>) request.getAttribute("imageObjs");
					if (images.size() == 0) {
				%>
						No image
				<%
					} else {
				%>
						<table class="table-striped table-bordered">
							<thead>
								<tr>
									<th>&nbsp;</th>
									<th>Image ID</th>
									<th>Preview</th>
								</tr>
							</thead>
							
							<tbody>
							<% for (Image image : images) { %>
								<tr>
									<td><a href="ViewImage?id=<%=image.getId() %>" icon="eye-open">View Full Size</a></td>
									<td><%=image.getId() %></td>
									<td>
										<img src="https://s3.amazonaws.com/uoftece1779winter2013/<%=image.getKeys().get("key1") %>" alt="Original" class="image-preview" />
										<img src="https://s3.amazonaws.com/uoftece1779winter2013/<%=image.getKeys().get("key2") %>" alt="Flipped" class="image-preview" />
										<img src="https://s3.amazonaws.com/uoftece1779winter2013/<%=image.getKeys().get("key3") %>" alt="Flopped" class="image-preview" />
									</td>
								</tr>
							<% } %>
							</tbody>
						</table>
				<%						
					}

				%>
				
			</div>
			
			<footer>
				&copy;2013 <a href="#" target="_blank">Group 13</a>
			</footer>
		</div>
	</body>
</html>