var authenticator = (function () {

	var instance;
	
	function createInstance() {
		
		function log(message) {
			console.log("Authenticator: " + message);
		}

		var CLIENT_ID_WEB = "1008259459239-t1huos5n6bhkin3is2jlqgkjv9h7mheh.apps.googleusercontent.com";
		var googleUser;

		var instanceInitializedCallback;
		var statusChangedCallback;

		return {
			
			// initializes the instance
			initialize: function () {
				log("Initializing authenticator instance");
				instance.parseSite();
				instance.initializeGoogleSignIn();
				instance.onInstanceInitialized();
			},

			// callback for the initialization
			onInstanceInitialized: function () {
				log("Authenticator initialized");
				if (instance.instanceInitializedCallback != null) {
					instance.instanceInitializedCallback(instance);
				}
			},

			// looks for sign in buttons and registers onclick listeners
			parseSite: function () {
				var signInWithGoogleButton = document.getElementById("signInWithGoogleButton");
				instance.registerSignInButton(signInWithGoogleButton);
			},

			// loads the Google auth API and initializes it
			initializeGoogleSignIn: function () {
				if (gapi.auth2 == null) {
					log("Requesting Google auth2 API");
					gapi.load('auth2', instance.initializeGoogleSignIn);
					return;
				}

				log("Initializing Google sign in");

				var params = {
					client_id: CLIENT_ID_WEB,
					fetch_basic_profile: true
				}
				gapi.auth2.init(params);

				var googleAuth = gapi.auth2.getAuthInstance();
				googleAuth.then(instance.onGoogleSignInInitialized, instance.onGoogleSignInInitializationFailed);
			},

			onGoogleSignInInitialized: function () {
				log("Google sign in initialized");
				instance.onGoogleSignInStatusChanged(instance.isSignedIn());
				gapi.auth2.getAuthInstance().isSignedIn.listen(instance.onGoogleSignInStatusChanged);
			},

			onGoogleSignInInitializationFailed: function (error) {
				log("Google sign in initialization failed: " + error);
			},

			// invokes a Google sign in
			signInWithGoogle: function () {
				try {
					log("Google sign in invoked");
					var googleAuth = gapi.auth2.getAuthInstance();
					var params =  {
						fetch_basic_profile: true
					}

					var promise = googleAuth.signIn();
					promise.then(function(value) {
						log("Google sign in successfull");
					}, function(error) {
						log("Google sign in failed: " + error);
					});
				} catch (ex) {
					log("Google sign in failed: " + ex);
				}
			},

			// invokes a Google sign out
			signOutFromGoogle: function () {
				log("Google sign out invoked");
				var auth2 = gapi.auth2.getAuthInstance();
				auth2.signOut().then(function () {
					log("User signed out");
				}, function(error) {
					log("Google sign out failed: " + error);
				});
			},

			// revokes all permissions
			disconnectFromGoogle: function () {
				log("Disconnection from Google invoked");
				var auth2 = gapi.auth2.getAuthInstance();
				auth2.disconnect().then(function () {
					log("User disconnected");
				}, function(error) {
					log("Disconnecting from Google failed: " + error);
				});
			},

			// set the currently signed in Google user
			setCurrentGoogleUser: function (user) {
				try {
					googleUser = user;
					localStorage.setItem("googleUser", JSON.stringify(googleUser));
				} catch (ex) {
					log("Unable to set current Google user.")
					return null;
				}
			},

			// returns the currently signed in Google user, if available
			getCurrentGoogleUser: function () {
				try {
					googleUser = gapi.auth2.getAuthInstance().currentUser.get();
					if (googleUser == null) {
						//googleUser = JSON.parse(localStorage.getItem("googleUser"));
					}
					return googleUser;
				} catch (ex) {
					log("Unable to get current Google user.")
					return null;
				}
			},

			// listener for the Google sign in status
			onGoogleSignInStatusChanged: function (isSignedIn) {
				if (isSignedIn) {
					log("Google user status changed: signed in");
					instance.onGoogleSignIn();
				} else {
					log("Google user status changed: signed out");
					instance.onGoogleSignOut();
				}

				if (instance.statusChangedCallback != null) {
					instance.statusChangedCallback(isSignedIn);
				} else {
					log("Status change callback is null");
				}
			},

			// callback for the Google sign in
			onGoogleSignIn: function () {
				instance.setCurrentGoogleUser(instance.getCurrentGoogleUser());
				if (googleUser != null) {
					var profile = googleUser.getBasicProfile();
					log("Current user set to: " + profile.getName());
					//log('ID: ' + profile.getId());
					//log('Name: ' + profile.getName());
					//log('Image URL: ' + profile.getImageUrl());
					//log('Email: ' + profile.getEmail());

					var googleIdToken = googleUser.getAuthResponse().id_token;
					log("Google ID Token: " + googleIdToken);
				}
			},

			// callback for the Google sign out
			onGoogleSignOut: function () {
				googleUser = null;
			},

			// returns the id token of the current user
			getUserIdToken: function () {
				try {
					return gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse().id_token;
				} catch (ex) {
					log("Unable to get token")
					return null;
				}
			},

			isSignedIn: function () {
				return gapi.auth2.getAuthInstance().isSignedIn.get();
			},

			// adds onclick event listener to a passed DOM element
			registerSignInButton: function (div) {
				if (div != null) {
					log("Registering sign in button: " + div.id);
					div.onclick = instance.signInWithGoogle;
				}
			}

		};
	};

	return {
		// Get the Singleton instance if one exists
		// or create one if it doesn't
		getInstance: function (newInstanceInitializedCallback, newStatusChangedCallback) {
			if (!instance) {
				instance = createInstance();
				instance.instanceInitializedCallback = newInstanceInitializedCallback;
				instance.statusChangedCallback = newStatusChangedCallback;
				instance.initialize();
			}
			return instance;
		}
	};

})();

// Usage:
// var auth = authenticator.getInstance();