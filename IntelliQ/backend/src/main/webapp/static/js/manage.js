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
      authenticator.requestIntelliqUserFromGoogleIdToken().then(function(user) {
        statusChangeListener.onUserAvailable();
      }).catch(function(error) {
        console.log("Unable to get IntelliQ user from Google ID token: " + error);
        ui.showErrorMessage(error);
      });
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
  options.itemGenerator = ui.generateBusinessCard;
  options.itemWrapperGenerator = generateCardWrapper;
  renderEntries(entries, container, options);
}

function renderQueues(entries, container) {
  var generateCardWrapper = function() {
    var className = ui.generateColumnClassName(12, 6, 6);
    return ui.generateCardWrapper(className);
  }

  var options = {};
  options.itemGenerator = ui.generateQueueCard;
  options.itemWrapperGenerator = generateCardWrapper;
  renderEntries(entries, container, options);
}

function renderQueueItems(entries, container) {
  var wrapperGenerator = function() {
    var wrapper = ui.generateCollection();
    return wrapper;
  }

  var options = {};
  options.itemGenerator = ui.generateQueueItemCollectionItem;
  options.wrapperGenerator = wrapperGenerator;
  renderEntries(entries, container, options);
}

function renderEntries(entries, container, options) {
  if (container == null || container.length < 1) {
    return;
  }
  container.empty();
  var wrapper = container;

  if (options.wrapperGenerator != null) {
    wrapper = options.wrapperGenerator();
  }

  for (var i = 0; i < entries.length; i++) {
    try {
      // create a item
      var item = options.itemGenerator(entries[i]);

      if (options.itemWrapperGenerator != null) {
        // create a div that wraps the item
        var itemWrapper = options.itemWrapperGenerator();

        // render the item in the item wrapper
        item.renderIn(itemWrapper);

        // add the item wrapper to the wrapper
        wrapper.append(itemWrapper);
      } else {
        // add the item to the wrapper
        wrapper.append(item);
      }
    } catch (ex) {
      console.log("Unable to render entry:");
      console.log(ex);
    }
  }

  if (options.wrapperGenerator != null) {
    container.append(wrapper);
  } else {
    container = wrapper;
  }

  if (entries.length < 1) {
    container.hide();
    container.parent().find(".emptyState").removeClass("hide");
  } else {
    container.show();
    container.parent().find(".emptyState").addClass("hide");
  }

  // re-initialize tooltips
  $(".material-tooltip").remove();
  $(".tooltipped").tooltip({ delay: 250 });
}
