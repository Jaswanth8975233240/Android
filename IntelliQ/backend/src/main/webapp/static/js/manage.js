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
      ui.showSignInForm();
    },
    
    onUserAvailable: function(user) {
      console.log(user);
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

function renderBusinesses(entries, container) {
  var generateCardWrapper = function() {
    var className = ui.generateColumnClassName(12, 6, 6);
    return ui.generateCardWrapper(className);
  }

  var options = {};
  options.itemGenerator = ui.generateManageBusinessCard;
  options.itemWrapperGenerator = generateCardWrapper;
  ui.renderEntries(entries, container, options);
}

function renderQueues(entries, container) {
  var generateCardWrapper = function() {
    var className = ui.generateColumnClassName(12, 6, 6);
    return ui.generateCardWrapper(className);
  }

  var options = {};
  options.itemGenerator = ui.generateManageQueueCard;
  options.itemWrapperGenerator = generateCardWrapper;
  ui.renderEntries(entries, container, options);
}

function renderQueueItems(entries, container) {
  var wrapperGenerator = function() {
    var wrapper = ui.generateCollection();
    return wrapper;
  }

  var options = {};
  options.itemGenerator = ui.generateQueueItemCollectionItem;
  options.wrapperGenerator = wrapperGenerator;
  ui.renderEntries(entries, container, options);
}