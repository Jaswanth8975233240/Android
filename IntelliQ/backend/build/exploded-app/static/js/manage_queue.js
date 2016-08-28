var UPDATE_INTERVAL_CASUAL = 1000 * 30;
var UPDATE_INTERVAL_DEFAULT = 1000 * 15;
var UPDATE_INTERVAL_DEMO = 1000 * 5;

var queue;
var queueItems;

var lastQueueItemsUpdate = -1;
var queueItemsUpdateInterval = UPDATE_INTERVAL_DEFAULT;
var queueItemsUpdateIntervalObject;

var onUserReady = function(user) {
  updateQueue();
}

function updateQueue() {
  requestQueue().then(function(queue){
    renderQueue(queue);
    updateQueueItems();
    startUpdatingQueueItems(UPDATE_INTERVAL_DEFAULT);
  }).catch(function(error){
    console.log(error);
    showErrorMessage(error);
  });
}

function updateQueueItems() {
  requestQueueItems().then(function(queueItems){
    renderAllQueueItems(queueItems);
    renderQueueItemChangesSinceLastUpdate(queueItems);
    lastQueueItemsUpdate = (new Date()).getTime();
  }).catch(function(error){
    console.log(error);
    showErrorMessage(error);
  });
}

function requestQueue() {
  var promise = new Promise(function(resolve, reject) {
    var queueKeyId = getUrlParamOrCookie("queueKeyId");
    var googleIdToken = authenticator.getInstance().getUserIdToken();

    var request = intelliqApi.getQueue(queueKeyId)
        .setGoogleIdToken(googleIdToken);

    request.send().then(function(data){
      queues = intelliqApi.getQueuesFromResponse(data);
      if (queues.length > 0) {
        queue = queues[0];
        resolve(queue);
      } else {
        reject("Queue not found");
      }
    }).catch(function(error){
      reject("Unable to get queue: " + error);
    });
  });
  return promise;
}

function requestQueueItems() {
  var promise = new Promise(function(resolve, reject) {
    var googleIdToken = authenticator.getInstance().getUserIdToken();

    var request = intelliqApi.getQueueItems(queue.key.id)
        .setCount(100)
        .setGoogleIdToken(googleIdToken);

    request.send().then(function(data){
      queueItems = intelliqApi.getQueueItemsFromResponse(data);
      resolve(queueItems);
    }).catch(function(error){
      reject("Unable to get queue items: " + error);
    });
  });
  return promise;
}

/*
  Automatic update handling
*/
function startUpdatingQueueItems(interval) {
  // set the interval
  queueItemsUpdateInterval = interval;
  if (queueItemsUpdateInterval == null) {
    queueItemsUpdateInterval = UPDATE_INTERVAL_DEFAULT;
  }

  // clear existing interval
  if (queueItemsUpdateIntervalObject != null) {
    stopUpdatingQueueItems();
  }

  console.log("Starting to update queue items every " + queueItemsUpdateInterval / 1000 + " seconds");
  queueItemsUpdateIntervalObject = setInterval(function(){
    if (shouldUpdateQueueItems()) {
      updateQueueItems();
    }
  }, queueItemsUpdateInterval);
}

function stopUpdatingQueueItems() {
  console.log("Stopping to update queue items");
  clearInterval(queueItemsUpdateIntervalObject);
}

function shouldUpdateQueueItems() {
  // check if the items have been updated recently
  var now = (new Date()).getTime();
  if (now < lastQueueItemsUpdate + queueItemsUpdateInterval) {
    return false;
  }

  // TODO: check if site is inactive

  return true;
}

function renderQueueItemChangesSinceLastUpdate(queueItems) {
  // skip if this is the first update
  if (lastQueueItemsUpdate < 0) {
    return;
  }

  var newQueueItems = [];
  var changedQueueItems = [];

  queueItems.forEach(function(queueItem) {
    if (queueItem.entryTimestamp > lastQueueItemsUpdate) {
      newQueueItems.push(queueItem);
    } else if (queueItem.lastStatusChangeTimestamp > lastQueueItemsUpdate) {
      changedQueueItems.push(queueItem);
    }
  });

  console.log(newQueueItems.length + " new queue item(s)");
  console.log(changedQueueItems.length + " changed queue item(s)");

  //TODO: show toasts for items
}

/*
  Item rendering
*/

function renderQueue(queue) {
  console.log(queue);
  //TODO: update labels & buttons
}

function renderAllQueueItems(queueItems) {
  console.log("Rendering " + queueItems.length + " queue items");
  //console.log(queueItems);
  renderCalledQueueItems(queueItems);
  renderWaitingQueueItems(queueItems);
  renderProcessedQueueItems(queueItems);
}

function renderCalledQueueItems(queueItems) {
  var container = $("#calledContainer");
  var items = intelliqApi.filterQueueItems(queueItems)
      .byStatus(intelliqApi.STATUS_CALLED);
  renderQueueItems(items, container);
}

function renderWaitingQueueItems(queueItems) {
  var container = $("#waitingContainer");
  var items = intelliqApi.filterQueueItems(queueItems)
      .byStatus(intelliqApi.STATUS_WAITING);
  renderQueueItems(items, container);
}

function renderProcessedQueueItems(queueItems) {
  var container = $("#processedContainer");
  var items = intelliqApi.filterQueueItems(queueItems)
      .byStatus(intelliqApi.STATUS_DONE);
  renderQueueItems(items, container);
}

function onQueueItemsModified() {
  //renderAllQueueItems(queueItems);
  updateQueueItems();
}

/*
  Actions
*/

function callQueueItem(queueItem) {
  Materialize.toast(getString("calling", queueItem.name), 3000);
  var request = intelliqApi.markQueueItemAsCalled(queue.key.id, queueItem.key.id)
      .setGoogleIdToken(authenticator.getInstance().getUserIdToken());

  request.send().then(function(data){
    console.log(data);
    onQueueItemsModified();
  }).catch(function(error){
    console.log(error);
    showErrorMessage(error);
  });
}

function cancelQueueItem(queueItem) {
  Materialize.toast(getString("cancelling", queueItem.name), 3000);
  var request = intelliqApi.markQueueItemAsCanceled(queue.key.id, queueItem.key.id)
      .setGoogleIdToken(authenticator.getInstance().getUserIdToken());

  request.send().then(function(data){
    console.log(data);
    onQueueItemsModified();
  }).catch(function(error){
    console.log(error);
    showErrorMessage(error);
  });
}

function markQueueItemAsDone(queueItem) {
  Materialize.toast(getString("markingAsDone", queueItem.name), 3000);
  var request = intelliqApi.markQueueItemAsDone(queue.key.id, queueItem.key.id)
      .setGoogleIdToken(authenticator.getInstance().getUserIdToken());

  request.send().then(function(data){
    console.log(data);
    onQueueItemsModified();
  }).catch(function(error){
    console.log(error);
    showErrorMessage(error);
  });
}

function deleteQueueItem(queueItem) {
  Materialize.toast(getString("deleting", queueItem.name), 3000);
  var request = intelliqApi.deleteQueueItem(queue.key.id, queueItem.key.id)
      .setGoogleIdToken(authenticator.getInstance().getUserIdToken());
  
  request.send().then(function(data){
    console.log(data);
    onQueueItemsModified();
  }).catch(function(error){
    console.log(error);
    showErrorMessage(error);
  });
}
