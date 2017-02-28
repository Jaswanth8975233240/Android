var authenticator = function(){

  function log(message) {
    if (typeof message !== "string") {
      message = "\n" + JSON.stringify(message, null, 2)
    }
    console.log("Authenticator: " + message);
  }

  var authenticator = {
  };

  authenticator.googleAuthenticationInitialized = false;
  authenticator.googleAuthenticationInitializing = false;
  authenticator.statusChangeListeners = [];

  authenticator.CLIENT_ID_WEB = "1008259459239-t1huos5n6bhkin3is2jlqgkjv9h7mheh.apps.googleusercontent.com";

  authenticator.initializeGoogleAuthentication = function() {
    var promise = new Promise(function(resolve, reject) {
      if (authenticator.googleAuthenticationInitialized) {
        resolve();
        return;
      }

      if (authenticator.googleAuthenticationInitializing) {
        var sleep = function(timeout) {
          return new Promise(function(resolve, reject) {
            setTimeout(resolve, timeout);
          });
        }

        var tries = 0;
        var checkAvailablitity = function() {
          if (authenticator.googleAuthenticationInitializing && tries < 10) {
            tries++;
            sleep(500).then(checkAvailablitity);
          } else {
            if (authenticator.googleAuthenticationInitialized) {
              resolve();
            } else {
              reject("Initialization failed");
            }
          }
        }
      }

      var onAuthApiAvailable = function() {
        log("Initializing Google authentication");
        var params = {
          client_id: authenticator.CLIENT_ID_WEB,
          fetch_basic_profile: true
        }
        gapi.auth2.init(params);

        var googleAuth = gapi.auth2.getAuthInstance();

        var onGoogleSignInInitialized = function () {
          log("Google authentication initialized");
          authenticator.googleAuthenticationInitializing = false;
          authenticator.googleAuthenticationInitialized = true;
          gapi.auth2.getAuthInstance().isSignedIn.listen(function(isSignedIn) {
            if (isSignedIn) {
              authenticator.onGoogleSignIn();
            } else {
              authenticator.onGoogleSignOut();
            }
          });
          resolve();
        }

        var onGoogleSignInInitializationFailed = function(error) {
          log("Google authentication initialization failed: " + error);
          authenticator.googleAuthenticationInitializing = false;
          authenticator.googleAuthenticationInitialized = false;
          reject(error);
        }

        googleAuth.then(onGoogleSignInInitialized, onGoogleSignInInitializationFailed);
      }

      if (gapi.auth2 == null) {
        log("Requesting Google auth2 API");
        authenticator.googleAuthenticationInitializing = true;
        gapi.load('auth2', onAuthApiAvailable);
      } else {
        onAuthApiAvailable();
      }
    });
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
      }).catch(function(error) {
        reject(error);
      });
    });
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
      }).catch(function(error) {
        reject(error);
      });
    });
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
      }).catch(function(error) {
        reject(error);
      });
    });
    return promise;
  }

  /*
    Asynchronous and synchronous getters
  */
  authenticator.requestGoogleSignInStatus = function() {
    var promise = new Promise(function(resolve, reject) {
      authenticator.initializeGoogleAuthentication().then(function() {
        resolve(gapi.auth2.getAuthInstance().isSignedIn.get());
      }).catch(function(error) {
        reject(error);
      });
    });
    return promise;
  }

  authenticator.getGoogleSignInStatus = function() {
    try {
      return gapi.auth2.getAuthInstance().isSignedIn.get();
    } catch (ex) {
      log("Unable to get Google sign in status")
      return false;
    }
  }

  authenticator.requestGoogleUser = function() {
    var promise = new Promise(function(resolve, reject) {
      authenticator.initializeGoogleAuthentication().then(function() {
        var googleUser = gapi.auth2.getAuthInstance().currentUser.get();
        resolve(googleUser);
      }).catch(function(error) {
        reject(error);
      });
    });
    return promise;
  }

  authenticator.getGoogleUser = function() {
    try {
      return gapi.auth2.getAuthInstance().currentUser.get();
    } catch (ex) {
      log("Unable to get Google user")
      return null;
    }
  }

  authenticator.getGoogleUserIdToken = function() {
    try {
      var googleUser = authenticator.getGoogleUser();
      return googleUser.getAuthResponse().id_token;
    } catch (ex) {
      log("Unable to get Google user ID token")
      return null;
    }
  }

  authenticator.requestIntelliqUserFromGoogleIdToken = function() {
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
          authenticator.onUserAvailable(user);
          resolve(user);
        }).catch(function(error) {
          reject(error);
        });
      } catch (ex) {
        reject(ex);
      }
    });
    return promise;
  }

  /*
    Status callback handling
  */
  authenticator.registerStatusChangeListener = function(listener) {
    authenticator.statusChangeListeners.push(listener);
  }

  authenticator.onUserAvailable = function(user) {
    for (var i = 0; i < authenticator.statusChangeListeners.length; i++) {
      var callback = authenticator.statusChangeListeners[i].onUserAvailable;
      if (typeof callback === 'function') {
        callback(user);
      }
    }
  }

  authenticator.onGoogleSignIn = function() {
    var googleUser = authenticator.getGoogleUser();
    var profile = googleUser.getBasicProfile();
    var googleIdToken = authenticator.getGoogleUserIdToken();
    log("Google user signed in: " + profile.getName());

    for (var i = 0; i < authenticator.statusChangeListeners.length; i++) {
      var callback = authenticator.statusChangeListeners[i].onGoogleSignIn;
      if (typeof callback === 'function') {
        callback();
      }
    }
  }

  authenticator.onGoogleSignOut = function() {
    log("Google user signed out");

    for (var i = 0; i < authenticator.statusChangeListeners.length; i++) {
      var callback = authenticator.statusChangeListeners[i].onGoogleSignOut;
      if (typeof callback === 'function') {
        callback();
      }
    }
  }

  return authenticator;
}();