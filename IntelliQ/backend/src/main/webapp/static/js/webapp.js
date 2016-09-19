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
    renderActiveQueueItems();
  }).catch(function(error) {
    ui.showErrorMessage(error);
  });
}

function onQueueItemsChanged() {
  renderActiveQueueItems();
  $("html, body").animate({
    scrollTop: 0
  }, "slow");
}

function renderActiveQueueItems() {
  console.log("Requesting active queue items");
  requestRecentQueueItems().then(function(queueItems) {
    var calledQueueItems = intelliqApi.filterQueueItems(queueItems).byStatus(intelliqApi.STATUS_CALLED);
    var waitingQueueItems = intelliqApi.filterQueueItems(queueItems).byStatus(intelliqApi.STATUS_WAITING);
    console.log("Called queue items: " + calledQueueItems.length + ", waiting queue items: " + waitingQueueItems.length);

    var visibleQueueItems = calledQueueItems.concat(waitingQueueItems);
    renderQueueItems(visibleQueueItems, $("#queueItemsContainer"));
    if (visibleQueueItems.length > 0) {
      $("#activeQueueItemsSection").removeClass("hide");
    } else {
      $("#activeQueueItemsSection").addClass("hide");
    }
  }).catch(function(error) {
    console.log(error);
  })
}

function requestRecentQueueItems() {
  var promise = new Promise(function(resolve, reject) {
    requestRecentQueueItemKeyIds().then(function(recentQueueItemKeyIds) {
      var queueItems = [];
      var receivedResponses = 0;
      var expectedResponses = 0;

      var addRecentQueueItem = function(queueItem) {
        queueItems.push(queueItem);
      };

      var onResponseReceived = function() {
        receivedResponses++;
        if (receivedResponses == expectedResponses) {
          resolve(queueItems);
        }
      }

      // get queue items that have been assigned to the current user
      expectedResponses += 1;
      authenticator.requestGoogleSignInStatus().then(function(isSignedIn) {
        if (isSignedIn) {
          authenticator.requestIntelliqUserFromGoogleIdToken().then(function(user) {
            var request = intelliqApi.getQueueItemsFrom(user.key.id);
            request.send().then(function(data){
              var existingQueueItems = intelliqApi.getQueueItemsFromResponse(data);
              for (var existingIndex = 0; existingIndex < existingQueueItems.length; existingIndex++) {
                addRecentQueueItem(existingQueueItems[existingIndex]);
              }
              onResponseReceived();
            }).catch(function(error){
              console.log(error);
              onResponseReceived();
            });
          }).catch(function(error) {
            console.log(error);
            onResponseReceived();
          });
        } else {
          onResponseReceived();
        }
      }).catch(function(error) {
        console.log(error);
        onResponseReceived();
      });

      // get queue items from recent queue item key IDs
      expectedResponses += recentQueueItemKeyIds.length;
      for (var queueItemIndex = 0; queueItemIndex < recentQueueItemKeyIds.length; queueItemIndex++) {
        var request = intelliqApi.getQueueItem(recentQueueItemKeyIds[queueItemIndex]);
        request.send().then(function(data){
          var queueItem = intelliqApi.getQueueItemsFromResponse(data)[0];
          addRecentQueueItem(queueItem);
          onResponseReceived();
        }).catch(function(error){
          console.log(error);
          onResponseReceived();
        });
      }

    }).catch(function(error) {
      reject(error);
    });
  });
  return promise;
}

function requestRecentQueueItemKeyIds() {
  var promise = new Promise(function(resolve, reject) {
    var queueItemKeyIds = [];

    var addQueueItemKeyId = function(queueItemKeyId) {
      if (queueItemKeyId == null && queueItemKeyId < 0) {
        return;
      }
      if (queueItemKeyIds.indexOf(queueItemKeyId) > -1) {
        return;
      }
      queueItemKeyIds.push(queueItemKeyId);
    };

    // add id from URL paramter or cookie
    addQueueItemKeyId(getUrlParamOrCookie("queueItemKeyId"));

    resolve(queueItemKeyIds);
  });
  return promise;
}

function requestNearbyQueues() {
  // request device location
  requestDeviceLocation().then(function(location){
    deviceLocation = location;

    var latitude = location.coords.latitude;
    var longitude = location.coords.longitude;

    // request queues at location
    var request = intelliqApi.getNearbyQueues(latitude, longitude)
        .includeBusinesses(true)
        .inRange(50000); // TODO: set this to something meaningful
    request.send().then(function(data){
      var businesses = intelliqApi.getBusinessesFromResponse(data);
      console.log(businesses);
      renderBusinesses(businesses, $("#businessesContainer"));
    }).catch(function(error){
      console.log(error);
      ui.showErrorMessage(error);
    });
  }).catch(function(error){
    console.log(error);
    ui.showErrorMessage(getString("locationUnavailable") + ": " + error);
  });
}

function renderBusinesses(entries, container) {
  var options = {};
  options.itemGenerator = ui.generateBusinessWithQueuesCard;
  options.itemWrapperGenerator = generateCardWrapper;
  ui.renderEntries(entries, container, options);
}

function renderQueues(entries, container) {
  var options = {};
  options.itemGenerator = ui.generateQueueCard;
  options.itemWrapperGenerator = generateCardWrapper;
  ui.renderEntries(entries, container, options);
}

function renderQueueItems(entries, container) {
  var options = {};
  options.itemGenerator = ui.generateQueueItemCard;
  options.itemWrapperGenerator = generateCardWrapper;
  ui.renderEntries(entries, container, options);
}

function generateCardWrapper() {
  var className = ui.generateColumnClassName(12, 6, 6);
  return ui.generateCardWrapper(className);
}