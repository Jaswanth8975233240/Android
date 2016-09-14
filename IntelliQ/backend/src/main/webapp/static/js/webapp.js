var deviceLocation;

(function($){
  $(function(){
    initAuthentication();
  });
})(jQuery);

/*
  Initializes the authentication
*/
function initAuthentication() {
  var statusChangeListener = {
    onGoogleSignIn: function() {
      ui.hideSignInForm();
      authenticator.requestIntelliqUserFromGoogleIdToken().then(function(user) {
        
      }).catch(function(error) {
        console.log("Unable to get IntelliQ user from Google ID token: " + error);
        ui.showErrorMessage(error);
      });
    },

    onGoogleSignOut: function() {
      ui.hideSignOutForm();
      //ui.showSignInForm();
    },
    
    onUserAvailable: function(user) {
      
    }
  };
  authenticator.registerStatusChangeListener(statusChangeListener);

  authenticator.requestGoogleSignInStatus().then(function(isSignedIn) {
    if (isSignedIn) {
      statusChangeListener.onGoogleSignIn();
    } else {
      statusChangeListener.onGoogleSignOut();
    }
  }).catch(function(error) {
    ui.showErrorMessage(error);
  });
}

function requestNearbyQueues() {
  // request device location
  requestDeviceLocation().then(function(location){
    deviceLocation = location;

    var latitude = location.coords.latitude;
    var longitude = location.coords.longitude;

    // request queues at location
    var request = intelliqApi.getNearbyQueues(latitude, longitude).includeBusinesses(true);
    request.send().then(function(data){
      var businesses = intelliqApi.getBusinessesFromResponse(data);
      console.log(businesses);
      renderBusinesses(businesses, $("#businessesContainer"));
    }).catch(function(error){
      console.log(error);
      showErrorMessage(error);
    });
  }).catch(function(error){
    console.log(error);
    showErrorMessage(getString("locationUnavailable") + ": " + error);
  });
}

function renderBusinesses(entries, container) {
  var generateCardWrapper = function() {
    var className = ui.generateColumnClassName(12, 6, 6);
    return ui.generateCardWrapper(className);
  }

  var options = {};
  options.itemGenerator = ui.generateBusinessWithQueuesCard;
  options.itemWrapperGenerator = generateCardWrapper;
  ui.renderEntries(entries, container, options);
}