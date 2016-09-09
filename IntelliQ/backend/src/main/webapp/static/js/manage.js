(function($){
  $(function(){
    initAuthentication();
  });
})(jQuery);

/*
  Initializes the authentication and prompts the user to sign in
*/
function initAuthentication() {
  authenticator.requestGoogleSignInStatus().then(function(isSignedIn) {
    if (isSignedIn) {
      var userIdToken = authenticator.getGoogleUserIdToken();
      authenticator.requestIntelliqUserFromGoogleIdToken.then(function(user) {
        console.log(user);
        intelliqUi.hideSignInForm();

      }).catch(function(error) {
        console.log("Unable to get IntelliQ user from Google ID token: " + error);
        intelliqUi.showErrorMessage(error);
      });
    } else {
      intelliqUi.showSignInForm();
    }
  }).catch(function(error) {
    intelliqUi.showErrorMessage(error);
  })
}

/*
  Tries to get an (IntelliQ-) user object from the currently
  active Google ID token
*/
function requestUserFromGoogleIdToken() {
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

function renderBusinesses(entries, container) {
  var generateCardWrapper = function() {
    var className = intelliqUi.generateColumnClassName(12, 6, 6);
    return intelliqUi.generateCardWrapper(className);
  }

  var options = {};
  options.itemGenerator = intelliqUi.generateBusinessCard;
  options.itemWrapperGenerator = generateCardWrapper;
  renderEntries(entries, container, options);
}

function renderQueues(entries, container) {
  var generateCardWrapper = function() {
    var className = intelliqUi.generateColumnClassName(12, 6, 6);
    return intelliqUi.generateCardWrapper(className);
  }

  var options = {};
  options.itemGenerator = intelliqUi.generateQueueCard;
  options.itemWrapperGenerator = generateCardWrapper;
  renderEntries(entries, container, options);
}

function renderQueueItems(entries, container) {
  var wrapperGenerator = function() {
    var wrapper = intelliqUi.generateCollection();
    return wrapper;
  }

  var options = {};
  options.itemGenerator = intelliqUi.generateQueueItemCollectionItem;
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
