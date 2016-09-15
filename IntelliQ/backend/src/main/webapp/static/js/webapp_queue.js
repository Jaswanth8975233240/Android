var queue;
var business;
var queueItem;

$(function(){
  requestQueueDetails();

  // add customer modal
  $("#sbmitNewCustomerButton").click(onJoinQueueModalSubmitted);
  $('#newCustomerName').keypress(function(e) {
    if (e.which == 13) {
      onJoinQueueModalSubmitted();
      return false;
    }
  });
});

function requestQueueDetails() {
  var queueKeyId = getUrlParamOrCookie("queueKeyId");
  var request = intelliqApi.getQueue(queueKeyId).includeBusiness(true);
  request.send().then(function(data){
    try {
      var businesses = intelliqApi.getBusinessesFromResponse(data);
      if (businesses.length < 1) {
        throw "Business not found";
      }
      console.log(businesses);
      business = businesses[0];
      if (business.queues.length < 1) {
        throw "Queue not found";
      }

      queue = business.queues[0];
      var container = $("#queueContainer");
      renderQueues(business.queues, container);

      $("#joinQueueButton").click(function() {
        joinQueue(queue);
      });

      $("#leaveQueueButton").click(function() {
        leaveQueue(queue);
      });

      //renderQueueDescription(queue, container);
      $("#joinQueueButton").removeClass("disabled");

      requestQueueItem();
    } catch(error) {
      console.log(error);
      ui.showErrorMessage(error);
    }
  }).catch(function(error){
    console.log(error);
    ui.showErrorMessage(error);
  });
}

function requestQueueItem() {
  var queueItemKeyId = getUrlParamOrCookie("queueItemKeyId");
  if (queueItemKeyId == null || queueItemKeyId.length < 1) {
    return;
  }
  var queueKeyId = getUrlParam("queueKeyId");
  var queueItem = JSON.parse(getUrlParamOrCookie("queueItem"));
  if (queueItem.queueKeyId != queueKeyId) {
    // queueItem belongs to a different queue
    return;
  }

  var request = intelliqApi.getQueueItem(queueItemKeyId);
  request.send().then(function(data){
    try {
      var queueItems = intelliqApi.getQueueItemsFromResponse(data);
      if (queueItems.length < 1) {
        throw "Queue item not found";
      }
      queueItem = queueItems[0];
      onQueueJoined(queueItem);
    } catch(error) {
      console.log(error);
      ui.showErrorMessage(error);
    }
  }).catch(function(error){
    console.log(error);
    ui.showErrorMessage(error);
  });
}

function renderQueueDescription(queue, container) {
  var wrapper = generateCardWrapper();
  var descriptionCard = ui.generateQueueDescriptionCard(queue);
  descriptionCard.renderIn(wrapper);
  wrapper.appendTo(container);
}

function renderQueueItem(queueItem, container) {
  var wrapper = generateCardWrapper();
  var queueItemCard = ui.generateQueueItemCard(queueItem);
  queueItemCard.renderIn(wrapper);
  wrapper.appendTo(container);
}

function joinQueue(queue) {
  // check if queue requires signin
  if (queue.requiresSignIn) {
    authenticator.requestGoogleSignInStatus().then(function(isSignedIn) {
      if (isSignedIn) {
        showJoinQueueModal();
      } else {
        // request a sign in
        authenticator.registerStatusChangeListener({
          onGoogleSignIn: function() {
            showJoinQueueModal();
          }
        });
        ui.showSignInForm();
      }
    }).catch(function(error) {
      ui.showErrorMessage(error);
    });
  } else {
    showJoinQueueModal();
  }
}

function leaveQueue(queue) {
  var cancelQueuetem = function() {
    Materialize.toast(getString("leavingQueue"), 3000);
    var queueItemKeyId = getUrlParamOrCookie("queueItemKeyId");
    var request = intelliqApi.markQueueItemAsCanceled(queue.key.id, queueItemKeyId);
    if (authenticator.getGoogleSignInStatus()) {
      request.setGoogleIdToken(authenticator.getGoogleUserIdToken());
    }

    request.send().then(function(data){
      try {
        console.log(data);

        // delete cookies
        deleteCookie("queueKeyId");
        deleteCookie("queueItemKeyId");
        deleteCookie("queueItem");

        onQueueLeft();

        tracking.trackEvent(tracking.CATEGORY_WEBAPP, "Queue ticket canceled", queue.name, queue.key.id);
        location.reload();
      } catch(error) {
        console.log(error);
        ui.showErrorMessage(error);
      }
    }).catch(function(error){
      console.log(error);
      ui.showErrorMessage(error);
    });
  }

  // check if queue requires signin
  if (queue.requiresSignIn) {
    authenticator.requestGoogleSignInStatus().then(function(isSignedIn) {
      if (isSignedIn) {
        cancelQueuetem();
      } else {
        // request a sign in
        authenticator.registerStatusChangeListener({
          onGoogleSignIn: function() {
            cancelQueuetem();
          }
        });
        ui.showSignInForm();
      }
    }).catch(function(error) {
      ui.showErrorMessage(error);
    });
  } else {
    cancelQueuetem();
  }

}

function showJoinQueueModal() {
  $("#newCustomerName").val("");
  authenticator.requestGoogleSignInStatus().then(function(isSignedIn) {
    if ($("#newCustomerName").val() == "") {
      var userName = authenticator.getGoogleUser().getBasicProfile().getName()
      $("#newCustomerName").val(userName);
    }
  });
  $("#joinQueueModal").openModal();
  $("#newCustomerName").focus();
  tracking.trackEvent(tracking.CATEGORY_WEBAPP, "Show join queue modal");
}

function onJoinQueueModalSubmitted() {
  try {
    var name = $("#newCustomerName").val();
    var hideName = $("#newCustomerVisibility").prop("checked") == false;

    Materialize.toast(getString("joiningQueue"), 3000);
    var request = intelliqApi.addQueueItem(queue.key.id)
        .withName(name)
        .hideName(hideName)
        .usingApp(true);

    if (authenticator.getGoogleSignInStatus()) {
      request.setGoogleIdToken(authenticator.getGoogleUserIdToken());
    }

    request.send().then(function(data){
      try {
        console.log(data);
        var queueItems = intelliqApi.getQueueItemsFromResponse(data);
        if (queueItems.length < 1) {
          throw "Queue item not found";
        }
        queueItem = queueItems[0];
        
        // persist data in cookies
        setCookie("queueKeyId", queueItem.queueKeyId);
        setCookie("queueItemKeyId", queueItem.key.id);
        setCookie("queueItem", JSON.stringify(queueItem));

        onQueueJoined(queueItem);

        tracking.trackEvent(tracking.CATEGORY_WEBAPP, "Queue ticket created", queue.name, queue.key.id);

        // update url
        var url = intelliqApi.getUrls().forQueue(queue).openInWebApp();
        url = intelliqApi.getUrls().replaceParameter("queueItemKeyId", queueItem.key.id, url);
        window.history.pushState(null, "Ticket", url);
      } catch(error) {
        console.log(error);
        ui.showErrorMessage(error);
      }
    }).catch(function(error){
      console.log(error);
      ui.showErrorMessage(error);
    });
    $("#joinQueueModal").closeModal();
    tracking.trackEvent(tracking.CATEGORY_WEBAPP, "Submit join queue modal", queue.name, queue.key.id);
  } catch(error) {
    console.log(error);
    ui.showErrorMessage(error);
  }
}

function onQueueJoined(queueItem) {
  renderQueueItem(queueItem, $("#queueContainer"));
  // TODO: scroll to rendered card

  $("#joinQueueContainer").addClass("hide");
  $("#joinQueueButton").addClass("disabled");
  $("#leaveQueueContainer").removeClass("hide");
  $("#leaveQueueButton").removeClass("disabled");
}

function onQueueLeft() {
  $("#joinQueueContainer").removeClass("hide");
  $("#joinQueueButton").removeClass("disabled");
  $("#leaveQueueContainer").addClass("hide");
  $("#leaveQueueButton").addClass("disabled");
}