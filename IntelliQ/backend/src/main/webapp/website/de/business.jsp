<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
	<head>
		<title>IntelliQ.me - Für Unternehmen</title>
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
						<h5 class="header col s12 light">
							Das intelligente Warteschlangen System für zufriedene Kunden
						</h5>
	
					</div>
					<div class="row center">
						<a href="javascript:openContactForm();" class="btn-large waves-effect waves-light accent-color">Details fordern</a>
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
	
				<!--   Icon Section   -->
				<div class="row">
					<div class="col s12 m6 l4">
						<div class="icon-block">
							<h2 class="center primary-color-text">
								<i class="material-icons">hourglass_empty</i>
							</h2>
							<h5 class="center">Weniger Wartezeit</h5>
	
							<p class="light">Ihre Kunden werden durch die IntelliQ app darüber informiert, wie viele Leute noch vor ihnen an der Reihe sind und erhalten eine Schätzung über die verbleibende Wartezeit.
								So können sie Ihre Zeit auch außerhalb Ihres Wartezimmers nutzen. Die App wird Ihre Kunden rechtzeitig daran erinnern sich wieder in Ihr Unternehmen zu begeben, sollten sie Ihr Wartezimmer
								verlassen haben.</p>
						</div>
					</div>
	
					<div class="col s12 m6 l4">
						<div class="icon-block">
							<h2 class="center primary-color-text">
								<i class="material-icons">local_atm</i>
							</h2>
							<h5 class="center">Geringere Kosten</h5>
	
							<p class="light">Existierende Ticketsysteme benötigen ständige Wartung und Nachschub an bedruckbarem Papier. IntelliQ verursacht keine steigenden Kosten und benötigt keine proprietäre
								Hardware, ist also auch für Unternehmen mit vielen Kunden eine sehr effiziente und umweltfreundliche Lösung. Erstanschaffungskosten für Ticketspender und Zubehör entfallen ebenfalls.</p>
						</div>
					</div>
	
					<div class="col s12 m12 l4">
						<div class="icon-block">
							<h2 class="center primary-color-text">
								<i class="material-icons">group_add</i>
							</h2>
							<h5 class="center">Mehr zufriedene Kunden</h5>
	
							<p class="light">Die Zeit im Wartezimmer wird generell als unangenehm empfunden, unabhängig davon wie gut Sie es gestallten. Mit IntelliQ können Ihre Kunden die Wartezeit nach eigenem
								Ermessen nutzen und bringen so keine negativen Erfahrungen beim Warten mit Ihrem Unternehmen in Verbindung. Zudem bietet Ihnen die Gewissheit über verbleibende Wartezeit einen
								Wettbewerbsvorteil.</p>
						</div>
					</div>
				</div>
	
			</div>
		</div>
	
		<div class="container hide">
			<div class="section">
	
				<div class="row">
					<div class="icon-block">
						<h5 class="center">Das Problem</h5>
	
						<p class="light">Pellentesque dolor mi, maximus eu sodales nec, sollicitudin vestibulum libero. Suspendisse potenti. Proin neque mi, gravida sit amet diam ut, pulvinar ornare libero.
							Curabitur in ligula eleifend, aliquet risus et, placerat enim. Donec dictum leo ut magna congue, at vulputate nunc finibus. Suspendisse potenti. Proin neque mi, gravida sit amet diam ut,
							pulvinar ornare libero. Curabitur in ligula eleifend, aliquet risus et, placerat enim. Donec dictum leo ut magna congue, at vulputate nunc finibus.</p>
					</div>
				</div>
			</div>
	
			<div class="row">
				<div class="icon-block">
					<h5 class="center">Die Lösung</h5>
	
					<p class="light">IntelliQ.me sed posuere felis. In blandit, ipsum et interdum volutpat, tellus tortor ultrices mi, luctus tincidunt purus sapien sed dui. Pellentesque dolor mi, maximus eu
						sodales nec, sollicitudin vestibulum libero. Suspendisse potenti. Proin neque mi, gravida sit amet diam ut, pulvinar ornare libero. Curabitur in ligula eleifend, aliquet risus et, placerat enim.
						Donec dictum leo ut magna congue, at vulputate nunc finibus. Suspendisse potenti. Proin neque mi, gravida sit amet diam ut, pulvinar ornare libero. Curabitur in ligula eleifend, aliquet risus et,
						placerat enim. Donec dictum leo ut magna congue, at vulputate nunc finibus.</p>
				</div>
			</div>
		</div>
	
		<div id="modal-contact" class="modal">
			<div class="modal-content">
				<h4>Kontakt</h4>
				<p>Wenn Sie mehr über IntelliQ.me erfahren möchten, kontaktieren Sie uns bitte jeder Zeit. Gerne demonstrieren wir Ihnen unser Produkt auch vor Ort.</p>
	
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
