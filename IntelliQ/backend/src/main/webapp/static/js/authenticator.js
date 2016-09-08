var authenticator = function(){

  function log(message) {
    console.log("Authenticator: " + message);
  }

  var authenticator = {
  };

  authenticator.CLIENT_ID_WEB = "1008259459239-t1huos5n6bhkin3is2jlqgkjv9h7mheh.apps.googleusercontent.com";

  authenticator.initializeGoogleSignIn = function() {
    var promise = new Promise(function(resolve, reject) {
      var onAuthApiAvailable = function() {
        log("Initializing Google sign in");
        var params = {
          client_id: CLIENT_ID_WEB,
          fetch_basic_profile: true
        }
        gapi.auth2.init(params);

        var googleAuth = gapi.auth2.getAuthInstance();

        var onGoogleSignInInitialized = function () {
          log("Google sign in initialized");
          //authenticator.onGoogleSignInStatusChanged(authenticator.isSignedIn());
          gapi.auth2.getAuthInstance().isSignedIn.listen(authenticator.onGoogleSignInStatusChanged);
          resolve();
        }

        var onGoogleSignInInitializationFailed = function(error) {
          log("Google sign in initialization failed: " + error);
          reject(error);
        }

        googleAuth.then(onGoogleSignInInitialized, onGoogleSignInInitializationFailed);
      }

      if (gapi.auth2 == null) {
        log("Requesting Google auth2 API");
        gapi.load('auth2', onAuthApiAvailable);
      } else {
        onAuthApiAvailable();
      }
    }
    return promise;
  }

  authenticator.onGoogleSignInStatusChanged = function(isSignedIn) {
    if (isSignedIn) {
      log("Google user status changed: signed in");
      authenticator.onGoogleSignIn();
    } else {
      log("Google user status changed: signed out");
      authenticator.onGoogleSignOut();
    }

    // TODO: notify callbacks
  }

  authenticator.onGoogleSignIn = function () {
    authenticator.setCurrentGoogleUser(authenticator.getCurrentGoogleUser());
    if (googleUser != null) {
      var profile = googleUser.getBasicProfile();
      log("Current user set to: " + profile.getName());
      var googleIdToken = googleUser.getAuthResponse().id_token;
      //log("Google ID Token: " + googleIdToken);
    }
  }

  // callback for the Google sign out
  authenticator.onGoogleSignOut = function () {
    googleUser = null;
  }

  // returns the id token of the current user
  authenticator.getUserIdToken = function () {
    try {
      return gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse().id_token;
    } catch (ex) {
      log("Unable to get token")
      return null;
    }
  }

  authenticator.isSignedIn: function () {
    return gapi.auth2.getAuthInstance().isSignedIn.get();
  }

  return authenticator;
}();