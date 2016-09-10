(function($){
  $(function(){

    $('.button-collapse').sideNav();
    $('.parallax').parallax();
    $('.tooltipped').tooltip({delay: 50});
    $('.modal-trigger').leanModal();
    $('select').material_select();
    
    setupNavigation();
    setupAuthenticationButtons();
  }); // end of document ready
})(jQuery); // end of jQuery name space

function setupNavigation() {
  $("#nav-account-button").click(onAccountButtonClicked);
  $("#nav-mobile-account-button").click(onAccountButtonClicked);
}

function setupAuthenticationButtons() {
  $("#signInWithGoogleButton").click(function() {
    authenticator.signInToGoogle().then(function() {
      ui.hideSignInForm();
    }).catch(function(error) {
      ui.showErrorMessage(error);
    });
  });

  $("#signOutButton").click(function() {
    authenticator.signOutFromGoogle().then(function() {
      ui.hideSignOutForm();
    }).catch(function(error) {
      ui.showErrorMessage(error);
    });
  });

  $("#switchAccountButton").click(function() {
    authenticator.disconnectFromGoogle().then(function() {
      ui.hideSignOutForm();
    }).catch(function(error) {
      ui.showErrorMessage(error);
    });
  });
}

function onAccountButtonClicked() {
  authenticator.requestGoogleSignInStatus().then(function(isSignedIn) {
    if (isSignedIn) {
      ui.showSignOutForm();
    } else {
      ui.showSignInForm();
    }
  }).catch(function(error) {
    ui.showErrorMessage(error);
  });
}

function openContactForm() {
  $("#modal-contact").openModal();
}

function closeContactForm() {
  $("#modal-contact").closeModal();
}

function submitContactForm() {
  try {
    var email = document.getElementById("contact_email").value;
    var name = document.getElementById("contact_first_name").value + " " + document.getElementById("contact_last_name").value;
    var message = document.getElementById("contact_message").value;

    if (name.length < 5 || email.length < 5 || message.length < 5) {
      throw "Invalid fields";
    }

    var url = "http://steppschuh.net/php/mail.php";
    var params = "?to=" + "mail@intelliq.me";
    params = params + "&from_mail=" + email;
    params = params + "&from_name=" + encodeURIComponent(name);
    params = params + "&reply_to=" + email;
    params = params + "&subject=" + encodeURIComponent("Kontakt Anfrage");
    params = params + "&message=" + encodeURIComponent(message);
    
    var http = new XMLHttpRequest();
    http.open("GET", url+params, true);

    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    http.onreadystatechange = function() {
      if (http.readyState == 4 && http.status == 200) {
        Materialize.toast("Kontakt Anfrage wurde gesendet", 4000)
        console.log(http.responseText);       
      }
    }
    http.send(null);
  } catch(ex) {
    Materialize.toast("Leider lief etwas schief", 4000)
    var win = window.open("mailto:mailintelliq.me?body=" + document.getElementById("contact_message").value, '_blank');
    win.focus();
  }
  return false;
}