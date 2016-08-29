    
    <!-- Sign in modal -->
    <div id="modal-signin" class="modal">
      <div class="modal-content">
        <h4>Connect your account</h4>
        <p>Please sign in with your Google account to use this feature. In order to prevent misuse and spam, certain actions you perform will be tracked and coupled with your account.</p>
        <a id="signInWithGoogleButton" class="btn-large waves-effect waves-light primary-color">
          <i class="material-icons right">account_circle</i>
          Connect Google Account
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

    <!-- Footer -->
    <footer class="page-footer primary-color">
      <div class="container">
        <div class="row">
          <div class="col s12 m12 l6">
            <h5 class="white-text">About IntelliQ.me</h5>
            <p class="grey-text text-lighten-4">We think no one should waste lifetime while waiting. It doesn't matter where you are - you should be able to use the time until it's your turn effectively.
            IntelliQ is a smart system to manage waiting queues and offers estimations about remaining waiting time.</p>
          </div>
          <div class="col s6 m4 l3">
            <h5 class="white-text">Connect</h5>
            <ul>
              <li><a class="white-text" href="https://www.facebook.com/Intelliq.me">Facebook</a></li>
              <li><a class="white-text" href="https://twitter.com/IntelliQMe">Twitter</a></li>
              <li><a class="white-text" href="https://www.google.com/+IntelliqMe">Google+</a></li>
              <li><a class="white-text" href="mailto:mail@intelliq.me">E-Mail</a></li>
            </ul>
          </div>
          <div class="col s6 m4 l3">
            <h5 class="white-text">Links</h5>
            <ul>
              <li><a class="white-text" href="https://play.google.com/store/apps/details?id=com.steppschuh.intelliq">Android App</a></li>
              <li><a class="white-text" href="https://itunes.apple.com/pg/app/intelliq/id1019495717">iOS App</a></li>
              <li><a class="white-text" href="${rootUrl}/manage/">Manage</a></li>
              <li><a class="white-text" href="${rootUrl}/press/">Press</a></li>
            </ul>
          </div>
        </div>
      </div>
      <div class="footer-copyright">
        <div class="container">
          Developed with love by 
          <a class="accent-color-text text-lighten-3" href="http://markus-petrykowski.de/">Markus</a> and 
          <a class="accent-color-text text-lighten-3" href="http://steppschuh.net/">Stephan</a>
        </div>
      </div>
    </footer>
    
    <!-- Scripts -->
    <script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script src="${staticUrl}js/materialize.js"></script>
    <script src="${staticUrl}js/init.js"></script>
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
    