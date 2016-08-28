<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
	<head>
		<title>IntelliQ.me - Smarter Warten</title>
		<%@include file="../includes/de/common_head.jsp"%>
	</head>
	
	<body>
		<%@include file="../includes/de/common_navigation.jsp"%>
	
		<main>
		<div id="index-banner">
			<div class="section no-pad-bot">
				<div class="container">
					<div class="row">
						<h1 class="header center white-text light hide-on-small-only">IntelliQ.me</h1>
	
						<div class="col s12 m6 l4 offset-l2" style="margin-top: 20px; margin-bottom: 40px;">
							<div class="icon-block">
								<h2 class="center white-text">
									<i class="material-icons">group</i>
								</h2>
								<h5 class="light center white-text">Für Nutzer</h5>
	
								<p class="light white-text center">Nutzen Sie Ihre Wartezeit effektiver indem Sie die kostenlose IntelliQ app verwenden</p>
	
								<div class="row center">
									<a href="${rootUrl}/apps/" class="btn-large waves-effect waves-light accent-color">Nutzer werden</a>
								</div>
							</div>
						</div>
	
						<div class="col s12 m6 l4" style="margin-top: 20px; margin-bottom: 40px;">
							<div class="icon-block">
								<h2 class="center white-text">
									<i class="material-icons">business</i>
								</h2>
								<h5 class="light center white-text">Für Unternehmen</h5>
	
								<p class="light white-text center">Finden Sie heraus, wie Sie mit IntelliQ zufriedenere Kunden gewinnen und Kosten sparen können</p>
	
								<div class="row center">
									<a href="${rootUrl}/unternehmen/" class="btn-large waves-effect waves-light accent-color">Mehr erfahren</a>
								</div>
							</div>
						</div>
	
					</div>
				</div>
			</div>
	
		</div>
	
		<div class="background-image-container primary-color">
			<img src="${staticUrl}images/queue_large.jpg">
		</div>
	
		</main>
	
		<%@include file="../includes/de/common_footer.jsp"%>
	
	</body>
</html>
