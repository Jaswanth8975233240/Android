    
    <!-- Sign in modal -->
    <div id="modal-signin" class="modal">
      <div class="modal-content">
        <h4>Sign In</h4>
        <p>Please sign in with your Google account. In order to prevent misuse and spam, certain actions you perform will be tracked and coupled with your account.</p>
        <a id="signInWithGoogleButton" class="btn-large waves-effect waves-light primary-color">
          <i class="material-icons right">account_circle</i>
          Google Sign In
        </a>
      </div>
    </div>

    <!-- Sign out modal -->
    <div id="modal-signout" class="modal">
      <div class="modal-content">
        <h4>Sign Out</h4>
        <p>You can sign out or switch your account using the buttons below.</p>
        <a id="signOutButton" class="btn-large waves-effect waves-light primary-color">
          <i class="material-icons right">account_circle</i>
          Sign Out
        </a>
        <a id="switchAccountButton" class="btn-large waves-effect waves-light primary-color">
          <i class="material-icons right">supervisor_account</i>
          Switch Account
        </a>
      </div>
    </div>

    <!-- Error modal -->
    <div id="modal-error" class="modal">
      <div class="modal-content">
        <h4>Aw, Snap!</h4>
        <p id="modal-error-message">Something went wrong! We're not sure what, though. Please go back and try again.</p>
        <p>If you think this isn't your fault, please feel free to drop us a mail.</p>
        <a id="contactUsButton" href="mailto:mail@intelliq.me" class="btn-large waves-effect waves-light primary-color">
          <i class="material-icons right">email</i>
          Contact us
        </a>
      </div>
    </div>
    
    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script src="${staticUrl}js/materialize.js"></script>
    <script src="${staticUrl}js/init.js"></script>
    <script src="${staticUrl}js/tracking.js"></script>
    <script src="${staticUrl}js/general.js"></script>
    <script src="${staticUrl}js/authenticator.js" defer></script>
    <script src="${staticUrl}js/api.js" defer></script>
    <script src="${staticUrl}js/ui.js" defer></script>
    <script src="${staticUrl}js/animationui.js" defer></script>
    <script src="https://apis.google.com/js/platform.js?onload=initGoogleSignIn" defer></script>
    
    <!-- GA -->
    <script>
      (function(i, s, o, g, r, a, m) {
        i['GoogleAnalyticsObject'] = r;
        i[r] = i[r] || function() {
          (i[r].q = i[r].q || []).push(arguments)
        }, i[r].l = 1 * new Date();
        a = s.createElement(o), m = s.getElementsByTagName(o)[0];
        a.async = 1;
        a.src = g;
        m.parentNode.insertBefore(a, m)
      })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');
      ga('create', 'UA-15327134-25', 'auto');
      ga('send', 'pageview');
    </script>
    