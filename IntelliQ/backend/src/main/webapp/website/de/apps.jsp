<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
	<head>
		<title>IntelliQ.me - App laden</title>
		<%@include file="../includes/de/common_head.jsp"%>
	</head>
	
	<body>
		<%@include file="../includes/de/common_navigation.jsp"%>
	
		<main>
	
		<div id="index-banner" class="parallax-container">
			<div class="section no-pad-bot">
				<div class="container">
					<br>
					<br>
					<h1 class="header center white-text light">IntelliQ.me</h1>
					<div class="row center">
						<h5 class="header col s12 light">Nutzen Sie unsere kostenlose App für Android oder iOS</h5>
	
					</div>
					<div class="row center">
						<a href="#" class="btn-large waves-effect waves-light accent-color">Smarter warten</a>
					</div>
					<br>
					<br>
	
				</div>
			</div>
			<div class="parallax primary-color">
				<img src="${staticUrl}images/queue_small.jpg">
			</div>
		</div>
	
	
		<div class="container">
			<div class="section">
	
				<div class="row">
					<div class="col s12 m4" style="margin-top: 20px; margin-bottom: 20px;">
						<div class="icon-block">
							<h2 class="center primary-color-text">
								<i class="material-icons">phone_android</i>
							</h2>
							<h5 class="center">Android</h5>
	
							<p class="light center">Für Ihr Android Smartphone, Tablet oder Wear erhalten Sie IntelliQ direkt aus dem Play Store.</p>
	
							<div class="row center">
								<a href="https://play.google.com/store/apps/details?id=com.steppschuh.intelliq" class="btn-large waves-effect waves-light primary-color">Google Play</a>
							</div>
						</div>
					</div>
	
					<div class="col s12 m4" style="margin-top: 20px; margin-bottom: 20px;">
						<div class="icon-block">
							<h2 class="center primary-color-text">
								<i class="material-icons">phone_iphone</i>
							</h2>
							<h5 class="center">iOS</h5>
	
							<p class="light center">Im Apple App Store finden Sie die IntelliQ app für Ihr iPhone und auch für die Apple Watch.</p>
	
							<div class="row center">
								<a href="https://itunes.apple.com/pg/app/intelliq/id1019495717" class="btn-large waves-effect waves-light primary-color">iTunes</a>
							</div>
						</div>
					</div>
	
					<div class="col s12 m4" style="margin-top: 20px; margin-bottom: 20px;">
						<div class="icon-block">
							<h2 class="center primary-color-text">
								<i class="material-icons">laptop_chromebook</i>
							</h2>
							<h5 class="center">Web</h5>
	
							<p class="light center">Als Besitzer eines Chromebooks können Sie IntelliQ auch als Chrome extension nutzen.</p>
	
							<div class="row center">
								<a href="https://chrome.google.com/webstore/detail/intelliq/mbflidackmmffjigimaecbfkndgaaahd?hl=en" class="btn-large waves-effect waves-light primary-color">Chrome Store</a>
							</div>
						</div>
					</div>
				</div>
	
			</div>
		</div>
	
		<div id="modal-contact" class="modal">
			<div class="modal-content">
				<h4>Kontakt</h4>
				<p>Wenn Sie Anregungen oder Fragen zu IntelliQ haben, kontaktieren Sie uns bitte jeder Zeit. Wir werden schnellst möglich auf Ihre Anfrage zurück kommen.</p>
	
				<div class="row">
					<form class="col s12">
						<div class="row">
							<div class="input-field col s6">
								<input id="contact_first_name" type="text" class=""> <label for="contact_first_name">Vorname</label>
							</div>
							<div class="input-field col s6">
								<input id="contact_last_name" type="text" class=""> <label for="contact_last_name">Nachname</label>
							</div>
						</div>
						<div class="row">
							<div class="input-field col s12">
								<input id="contact_email" type="email" class=""> <label for="contact_email">Ihre E-Mail Adresse</label>
							</div>
						</div>
						<div class="row">
							<div class="input-field col s12">
								<textarea id="contact_message" class="materialize-textarea"></textarea>
								<label for="contact_message">Nachricht</label>
							</div>
						</div>
					</form>
				</div>
	
			</div>
	
			<div class="modal-footer">
				<a href="javascript:submitContactForm();" class=" modal-action modal-close waves-effect waves-green btn-flat">Senden</a>
			</div>
		</div>
	
		</main>
	
		<%@include file="../includes/de/common_footer.jsp"%>
	
	</body>
</html>
