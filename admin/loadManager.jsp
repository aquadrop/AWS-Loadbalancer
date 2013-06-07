<%--
    Document    : loadManager.jsp
    Created on  : 2013/03/03
    Author      : Jeff Lee
    Description : Manages load balance
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="ece1779.ec2.models.CloudWatchStats"%>
<%@page import="com.amazonaws.services.cloudwatch.model.Datapoint"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>ECE 1779 - Assignment 1</title>

<link rel="stylesheet" type="text/css" href="../css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="../css/style.css" />
<link rel="stylesheet" type="text/css"
	href="../css/bootstrap-responsive.min.css" />
<link rel="stylesheet" type="text/css" href="../css/jquery.chosen.css" />
<link rel="stylesheet" type="text/css"
	href="https://code.leejefon.com/font-awesome/css/font-awesome.min.css" />

<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript"
	src="https://code.leejefon.com/jquery.bootstrap.min.js"></script>
<script type="text/javascript"
	src="https://code.leejefon.com/jquery.chosen.min.js"></script>
<script type="text/javascript"
	src="https://code.leejefon.com/jquery.autosize.min.js"></script>
<script type="text/javascript"
	src="https://code.leejefon.com/jquery.cakeBootstrap.js"></script>
<script type="text/javascript" src="../js/script.js"></script>
<script type="text/javascript">
	/* window.onload = setupRefresh;
	function setupRefresh() {
		setInterval("refreshBlock();", 1000);
	}

	function refreshBlock() {
		$('#content').load("LoadManager");
	} */
</script>


</head>

<body data-spy="scroll" data-target=".subnav" data-offset="50">
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a data-target=".nav-collapse" data-toggle="collapse"
					class="btn btn-navbar"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a href="../Welcome" class="brand">ECE 1779 - Assignment 1</a>

				<div class="nav-collapse pull-right">
					<ul class="nav">
						<li class="active dropdown"><a href="#" icon="cogs"
							class="dropdown-toggle" data-toggle="dropdown"> Admin Panel <b
								icon="caret"></b>
						</a>
							<ul class="dropdown-menu">
								<li><a href="UserManager" icon="group">Manage Users</a></li>
								<li class="active"><a href="LoadManager" icon="sitemap">Load
										Balance</a></li>
							</ul></li>
						<li class="dropdown"><a href="#" icon="picture"
							class="dropdown-toggle" data-toggle="dropdown"> My Pictures <b
								icon="caret"></b>
						</a>
							<ul class="dropdown-menu">
								<%
									if (session.getAttribute("user") == null) {
								%>
								<li><a href="../user/Login" icon="lock">User Login</a></li>
								<%
									} else {
								%>
								<li><a href="../user/ListImages" icon="eye-open">View
										Pictures</a></li>
								<li><a href="../user/FileUpload" icon="upload-alt">Upload
										Picture</a></li>
								<%
									}
								%>
							</ul></li>
						<li><a href="../Logout" icon="off">Log Out Manager</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>

	<div class="container">
		<header></header>

		<div id="content">

			<div>
				<a class="btn btn-large btn-primary" href="StartInstance"
					icon="sitemap">StartNewInstance</a>
				<%
					if (session.getAttribute("loadBalance") != null
							|| this.getServletContext()
									.getAttribute("backgroundThread") != null) {
				%>
				<a class="btn btn-large btn-primary disabled" icon="sitemap"
					name="StartNewInstance">LoadBalancing Running</a>
				<%
					} else {
				%>
				<a class="btn btn-large btn-primary disabled" icon="sitemap">LoadBalancing
					Terminated</a>
				<%
					}
				%>

			</div>
			<h2 icon="sitemap">Instances</h2>

			<%
				List<CloudWatchStats> cloudWatchStats = (List<CloudWatchStats>) request
						.getAttribute("cloudWatchStats");
			%>
			<table class="table-striped table-bordered">
				<thead>
					<tr>
						<th>Instance</th>
						<th>Namespace</th>
						<th>Metric</th>
						<th>Values</th>
						<th>Operation</th>
					</tr>
				</thead>

				<tbody>
					<%
						for (CloudWatchStats cws : cloudWatchStats) {
					%>
					<!-- skip the terminated instance metric show -->
					<%
						List<Datapoint> datapoints = cws.getStats().getDatapoints();
					%>
					<%
						if (datapoints.size() == 0)
								continue;
					%>
					<tr>
						<!-- indicate the main instance that should not be terminated -->
						<%
							if (cws.getDimensions().get(0).getValue().equals("i-b809dfcb")) {
						%>
						<td><%=cws.getDimensions().get(0).getValue()%> <br>
							<p>Main Instance</p></td>
						<%
							} else {
						%>
						<td><%=cws.getDimensions().get(0).getValue()%></td>
						<%
							}
						%>
						<td><%=cws.getNamespace()%></td>
						<td><%=cws.getMetricName()%></td>
						<td>
							<ul>
								<%
									for (Datapoint dp : datapoints) {
								%>
								<li>Maximum: <b><%=dp.getMaximum()%>%</b> @ <%=dp.getTimestamp()%></li>
								<%
									}
								%>
							</ul>
						</td>
						<%
							if (cws.getDimensions().get(0).getValue().equals("i-b809dfcb")) {
						%>
						<td><div>
								<a class="btn btn-small btn-primary disabled" icon="sitemap">ShutdownInstance</a>
							</div></td>
						<%
							} else {
						%>
						<td><div>
								<a class="btn btn-small btn-primary" input type="hidden"
									name="ShutdownInstance" value="Hello"
									href="ShutdownInstance?ShutdownInstance=<%=cws.getDimensions().get(0).getValue()%>"
									icon="sitemap">ShutdownInstance</a>
							</div></td>
						<%
							}
						%>
					</tr>
					<%
						}
					%>
				</tbody>
			</table>
		</div>

		<footer>
			&copy;2013 <a href="#" target="_blank">Group 13</a>
		</footer>
	</div>
</body>
</html>