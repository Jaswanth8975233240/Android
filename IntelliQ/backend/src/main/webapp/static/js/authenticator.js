var authenticator = function(){

  function log(message) {
    console.log("Authenticator: " + message);
  }

  var authenticator = {
  };

  authenticator.googleAuthenticationInitialized = false;

  authenticator.CLIENT_ID_WEB = "1008259459239-t1huos5n6bhkin3is2jlqgkjv9h7mheh.apps.googleusercontent.com";

  authenticator.initializeGoogleAuthentication = function() {
    var promise = new Promise(function(resolve, reject) {
      if (authenticator.googleAuthInitialized) {
        resolve();
      }

      var onAuthApiAvailable = function() {
        log("Initializing Google authentication");
        var params = {
          client_id: CLIENT_ID_WEB,
          fetch_basic_profile: true
        }
        gapi.auth2.init(params);

        var googleAuth = gapi.auth2.getAuthInstance();

        var onGoogleSignInInitialized = function () {
          log("Google authentication initialized");
          authenticator.googleAuthInitialized = true;
          gapi.auth2.getAuthInstance().isSignedIn.listen(authenticator.onGoogleSignInStatusChanged);
          resolve();
        }

        var onGoogleSignInInitializationFailed = function(error) {
          log("Google authentication initialization failed: " + error);
          authenticator.googleAuthInitialized = false;
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

  authenticator.signInToGoogle = function() {
    var promise = new Promise(function(resolve, reject) {
      authenticator.initializeGoogleAuthentication().then(function() {
        try {
          log("Signing in to Google");
          var googleAuth = gapi.auth2.getAuthInstance();
          var params =  {
            fetch_basic_profile: true
          }
          googleAuth.signIn().then(function(value) {
            log("Google sign in succeeded");
            resolve();
          }, function(error) {
            log("Google sign in failed: " + error);
            reject(error);
          });
        } catch (ex) {
          log("Google sign in invoking failed: " + ex);
          reject(error);
        }
      }).catch(error) {
        reject(error);
      }
    };
    return promise;
  }

  authenticator.signOutFromGoogle = function() {
    var promise = new Promise(function(resolve, reject) {
      authenticator.initializeGoogleAuthentication().then(function() {
        try {
          log("Signing out from Google");
          var googleAuth = gapi.auth2.getAuthInstance();
          googleAuth.signOut().then(function(value) {
            log("Google sign out succeeded");
            resolve();
          }, function(error) {
            log("Google sign out failed: " + error);
            reject(error);
          });
        } catch (ex) {
          log("Google sign out invoking failed: " + ex);
          reject(error);
        }
      }).catch(error) {
        reject(error);
      }
    };
    return promise;
  }

  authenticator.disconnectFromGoogle = function() {
    var promise = new Promise(function(resolve, reject) {
      authenticator.initializeGoogleAuthentication().then(function() {
        try {
          log("Disconnecting from Google");
          var googleAuth = gapi.auth2.getAuthInstance();
          googleAuth.disconnect().then(function(value) {
            log("Google disconnection succeeded");
            resolve();
          }, function(error) {
            log("Google disconnection failed: " + error);
            reject(error);
          });
        } catch (ex) {
          log("Google disconnection invoking failed: " + ex);
          reject(error);
        }
      }).catch(error) {
        reject(error);
      }
    };
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

  authenticator.onGoogleUserSignedIn = function() {
    var googleUser = authenticator.getGoogleUser();
    var profile = googleUser.getBasicProfile();
    var googleIdToken = authenticator.getGoogleUserIdToken();
    log("Google user signed in: " + profile.getName());
  }

  // callback for the Google sign out
  authenticator.onGoogleUserSignedOut = function () {
    log("Google user signed out");
  }

  authenticator.requestGoogleSignInStatus = function() {
    var promise = new Promise(function(resolve, reject) {
      authenticator.initializeGoogleAuthentication().then(function() {
        resolve(gapi.auth2.getAuthInstance().isSignedIn.get());
      }).catch(error) {
        reject(error);
      }
    };
    return promise;
  }

  authenticator.getGoogleSignInStatus = function {
    try {
      return gapi.auth2.getAuthInstance().isSignedIn.get();
    } catch (ex) {
      log("Unable to get Google sign in status")
      return false;
    }
  }

  authenticator.requestGoogleUser = function {
    var promise = new Promise(function(resolve, reject) {
      authenticator.initializeGoogleAuthentication().then(function() {
        var googleUser = gapi.auth2.getAuthInstance().currentUser.get();
        resolve(googleUser);
      }).catch(error) {
        reject(error);
      }
    };
    return promise;
  }

  authenticator.getGoogleUser = function {
    try {
      return gapi.auth2.getAuthInstance().currentUser.get();
    } catch (ex) {
      log("Unable to get Google user")
      return null;
    }
  }

  authenticator.getGoogleUserIdToken = function {
    try {
      var googleUser = authenticator.getGoogleUser();
      return googleUser.getAuthResponse().id_token;
    } catch (ex) {
      log("Unable to get Google user ID token")
      return null;
    }
  }

  authenticator.requestUserFromGoogleIdToken = function() {
    var promise = new Promise(function(resolve, reject) {
      try {
        var googleIdToken = authenticator.getGoogleUserIdToken();
        if (googleIdToken == null) {
          throw "Token is null";
        }

        var signInRequest = intelliqApi.signInUser().setGoogleIdToken(googleIdToken);
        signInRequest.send().then(function(data){
          var user = intelliqApi.getUsersFromResponse(data)[0];
          if (user == null) {
            reject("Returned user is null");
          }
          resolve(user);
        }).catch(function(error){
          reject(error);
        });
      } catch (ex) {
        reject(ex);
      }
    });
    return promise;
  }

  return authenticator;
}();