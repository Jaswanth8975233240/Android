<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
<head>
<title>IntelliQ.me - Sign In</title>
<%@include file="../includes/de/common_head.jsp"%>

<script type="text/javascript">
	window.onload = function() {		
		
	}
</script>

</head>

<body>
	<%@include file="../includes/de/common_navigation.jsp"%>

	<main>

	<div id="index-banner" class="parallax-container">
			<div class="section no-pad-bot">
				<div class="container">
					<br>
					<br>
					<h1 class="header center white-text light">Sign In</h1>
					<div class="row center">
						<h5 class="header col s12 light">Connect your account to create or manage a queue.</h5>
	
					</div>
					<div class="row center">
						<a href="#" class="btn-large waves-effect waves-light accent-color">Smarter Waiting</a>
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
	
							<p class="light center">Get IntelliQ for your Android smartphone or Wear device from the Play Store</p>
	
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
	
							<p class="light center">Visit the Apple App Store to get IntelliQ for your iPhone or Apple Watch.</p>
	
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
	
							<p class="light center">If you own a Google Chromebook you can get the IntelliQ Chrome extension.</p>
	
							<div class="row center">
								<a href="https://chrome.google.com/webstore/detail/intelliq/mbflidackmmffjigimaecbfkndgaaahd?hl=en" class="btn-large waves-effect waves-light primary-color">Chrome Store</a>
							</div>
						</div>
					</div>
				</div>
	
			</div>
		</div>
	
	<div id="modal_add_customer" class="modal">
		<div class="modal-content">
			<h4>Add a customer</h4>
			<p>Customer name</p>
			<div class="row">
				<div class="input-field col s12 m6">
					<input id="add_customer_first_name" type="text"> <label for="add_customer_first_name">First Name</label>
				</div>
				<div class="input-field col s12 m6">
					<input id="add_customer_last_name" type="text"> <label for="add_customer_last_name">Last Name</label>
				</div>
			</div>

			<p>Display name</p>
			<div class="switch">
				<label> private <input id="add_customer_show_name" type="checkbox" checked="checked"> <span class="lever"></span> public
				</label>
			</div>
		</div>
		<div class="modal-footer">
			<a href="javascript:addCustomerDialogSubmitted();" class=" modal-action modal-close waves-effect waves-green btn-flat">Add</a>
		</div>
	</div>

	<div class="hide">
		<span id="proto_status_waiting_since">waiting since [VALUE] </span> <span id="proto_status_called_since">called since [VALUE] </span> <span id="proto_status_waiting">waiting</span> <span id="proto_status_canceled">canceled</span> <span id="proto_status_called">called</span>
		<span id="proto_status_done">done</span> <span id="proto_created_ticket">Created ticket #[VALUE]</span> <span id="proto_status_changed">Status updated</span> <span id="proto_something_went_wrong">Something
			went wrong</span> <span id="proto_unit_minute">minute</span> <span id="proto_unit_minutes">minutes</span> <span id="proto_unit_seconds">seconds</span>

	</div>

	</main>

	<%@include file="../includes/en/common_footer.jsp"%>
	<script src="${staticUrl}js/manage.js"></script>

</body>
</html>
