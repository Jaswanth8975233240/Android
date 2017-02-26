var existingQueue; // the existing queue, as returned by the API
var newQueue; // the new queue, created by the local changes
var deviceLocation; // the current location of the visitors device
var googleMap; // the Google Maps API object
var deviceLocationMarker; // map marker for the current location
var queueLocationMarker; // map marker for the new queue location

(function($){
  $(function(){

    $("#saveQueueButton").click(saveNewQueue);
    updateFormWithUrlParameterData();

    setupImageUpload();

    var statusChangeListener = {
      onUserAvailable: function(user) {
        var queueKeyId = getUrlParam("queueKeyId");
        requestExistingQueueData(queueKeyId);

        // update the UI
        if (queueKeyId == null) {
          showAddQueueUi();
          updateFormWithDeviceLocation();
        } else {
          showEditQueueUi();
        }
      }
    };
    authenticator.registerStatusChangeListener(statusChangeListener);
  });
})(jQuery);

// fetches an existing queue from the API, if the required
// url param is set. Then updates the form with the queue data
function requestExistingQueueData(queueKeyId) {
  if (queueKeyId == null) {
    console.log("No queue key ID specified");
    $(".loadingState").hide();
    return;
  }

  intelliqApi.getQueue(queueKeyId).send().then(function(data){
    var queues = intelliqApi.getQueuesFromResponse(data);
    existingQueue = queues[0];
    console.log(existingQueue);
    $(".loadingState").hide();
    updateFormWithQueueData(existingQueue);
  }).catch(function(error){
    console.log(error);
    $(".loadingState").hide();
    ui.showErrorMessage(error);
  });
}

function showAddQueueUi() {
  $("#businessHeading").text(getString("addQueue"));
  $("#saveQueueButton").text(getString("save"));
  $("#changeImageContainer").addClass("hide");
}

function showEditQueueUi() {
  $("#businessHeading").text(getString("editQueue"));
  $("#saveQueueButton").text(getString("applyChanges"));
  $("#changeImageContainer").removeClass("hide");
}

// fills the form fields with data from URL params
function updateFormWithUrlParameterData() {
  var name = getDecodedUrlParam("name");
  if (name != null) {
    $("#form-name").val(name);
  }

  var businessKeyId = getDecodedUrlParam("businessKeyId");
  if (businessKeyId != null) {
    $("#form-business-key-id").val(businessKeyId);
  }
}

// fills the form fields with data from the passed queue
function updateFormWithQueueData(queue) {
  if (queue == null) {
    console.log("Can't update form, passed queue is invalid");
    return;
  }

  console.log("Updating form with data from queue: " + queue.name);
  $("#form-key-id").val(queue.key.id);
  $("#form-business-key-id").val(queue.businessKeyId);
  $("#form-name").val(queue.name);
  $("#form-description").val(queue.description);

  $("#changeImageButton").removeClass("disabled");

  // waiting time
  var enteredTimeUnit = $("#form-average-waiting-time-unit :selected").val();
  if (enteredTimeUnit == "minutes") {
    $("#form-average-waiting-time").val(queue.averageWaitingTime / 1000 / 60);
  } else {
    $("#form-average-waiting-time").val(queue.averageWaitingTime / 1000);
  }

  // visibility
  if (queue.visibility == intelliqApi.VISIBILITY_PRIVATE) {
    $("#form-visibility-hidden").prop("checked", true);
    $("#form-visibility-visible").prop("checked", false);
  } else {
    $("#form-visibility-hidden").prop("checked", false);
    $("#form-visibility-visible").prop("checked", true);
  }

  // location & address
  $("#form-latitude").val(queue.latitude);
  $("#form-longitude").val(queue.longitude);
  $("#form-address-country").val(queue.country);
  $("#form-address-city").val(queue.city);
  $("#form-address-postal-code").val(queue.postalCode);
  $("#form-address-street").val(queue.street);
  $("#form-address-street-number").val(queue.number);

  if (queue.latitude == -1) {
    updateFormWithDeviceLocation();
  } else {
    centerMapAt(queue.latitude, queue.longitude);
    updateQueueLocationMarker(queue.latitude, queue.longitude);
  }
}

// creates a new queue object by parsing the form data
function parseFormToQueue() {
  var parsedQueue = {};
  parsedQueue.businessKeyId = $("#form-business-key-id").val();
  parsedQueue.name = $("#form-name").val();
  parsedQueue.description = $("#form-description").val();

  // waiting time
  var enteredTime = $("#form-average-waiting-time").val();
  var enteredTimeUnit = $("#form-average-waiting-time-unit :selected").val();
  parsedQueue.averageWaitingTime = enteredTime * 1000;
  if (enteredTimeUnit == "minutes") {
    parsedQueue.averageWaitingTime *= 60;
  }

  // visibility
  if ($("#form-visibility-hidden").prop("checked")) {
    parsedQueue.visibility = intelliqApi.VISIBILITY_PRIVATE;
  } else {
    parsedQueue.visibility = intelliqApi.VISIBILITY_PUBLIC;
  }

  // location & address
  parsedQueue.latitude = $("#form-latitude").val();
  parsedQueue.longitude = $("#form-longitude").val();
  parsedQueue.country = $("#form-address-country").val();
  parsedQueue.city = $("#form-address-city").val();
  parsedQueue.postalCode = $("#form-address-postal-code").val();
  parsedQueue.street = $("#form-address-street").val();
  parsedQueue.number = $("#form-address-street-number").val();
  
  return parsedQueue;
}

// applies the changes that have been made to the queue
function saveNewQueue() {
  console.log("Saving new queue");
  newQueue = parseFormToQueue();
  var mergedQueue = mergeQueues(existingQueue, newQueue);
  console.log(mergedQueue);

  var promise;
  if (mergedQueue.key != null) {
    promise = updateExistingQueue(mergedQueue);
  } else {
    promise = addNewQueue(mergedQueue);
  }

  promise.then(function(data) {
    console.log(data);
    var business = { "key": { "id": newQueue.businessKeyId } };
    window.location.href = intelliqApi.getUrls().forBusiness(business).manage();
  }, function(error) {
    ui.showErrorMessage(error);
  });
}

function updateExistingQueue(queue) {
  var googleIdToken = authenticator.getGoogleUserIdToken();
  return intelliqApi
      .editQueue(queue)
      .setGoogleIdToken(googleIdToken)
      .send();
}

function addNewQueue(queue) {
  var googleIdToken = authenticator.getGoogleUserIdToken();
  return intelliqApi
      .addQueue(queue)
      .setGoogleIdToken(googleIdToken)
      .send();
}

// merges two queues by returning the existing queue with
// values overwritten by the new queue
function mergeQueues(existingQueue, newQueue) {
  if (existingQueue == null) {
    return newQueue;
  }
  for (var key in newQueue) {
    if (newQueue.hasOwnProperty(key)) {
      existingQueue[key] = newQueue[key];
    }
  }
  return existingQueue;
}

/**
* Location handling
**/
function initializeMap() {
  var latitude = 0 + $("#form-latitude").val();
  var longitude = 0 + $("#form-longitude").val();

  if (latitude == "0") {
    latitude = 52.520815;
    longitude = 13.409419;
  }

  googleMap = new google.maps.Map(document.getElementById('map'), {
    zoom: 15,
    center: {
      lat: latitude,
      lng: longitude
    }
  });

  google.maps.event.addListener(googleMap, 'click', function(event) {
    var latitude = event.latLng.lat();
    var longitude = event.latLng.lng();
    updateQueueLocationMarker(latitude, longitude);
  });

  $("#updateLocationFromAddressButton").click(function() {
    var address = getAddressStringFromForm();
    var geocoder = new google.maps.Geocoder();
    geocodeAddress(address, geocoder).then(function(location) {
      centerMapAt(location.latitude, location.longitude);
      updateQueueLocationMarker(location.latitude, location.longitude);
    }).catch(function(error){
      console.log(error);
      ui.showErrorMessage(error);
    });
  });

  $("#updateAddressFromLocationButton").click(function() {
    var latitude = $("#form-latitude").val();
    var longitude = $("#form-longitude").val();
    var geocoder = new google.maps.Geocoder();
    geocodeLocation(latitude, longitude, geocoder, googleMap).then(function(results){
      updateFormWithGoogleAddressData(results);
    }).catch(function(error){
      console.log(error);
      ui.showErrorMessage(error);
    });
  });

  $("#updateDeviceLocationButton").click(function() {
    updateFormWithDeviceLocation();
  });
}

function centerMapAt(latitude, longitude) {
  var latlng = new google.maps.LatLng(latitude, longitude);
  googleMap.setCenter(latlng);
}

function updateDeviceLocationMarker(latitude, longitude) {
  // delete existing marker
  if (deviceLocationMarker != null) {
    deviceLocationMarker.setMap(null);
  }

  // create info window
  var infoWindow = new google.maps.InfoWindow({
    content: "Current device location."
  });

  // add new marker
  var latlng = new google.maps.LatLng(latitude, longitude);
  deviceLocationMarker = new google.maps.Marker({
    map: googleMap,
    label: "Device",
    title: "Current Device Location",
    position: latlng
  });

  // add event handler for info window
  deviceLocationMarker.addListener('click', function() {
    infoWindow.open(googleMap, deviceLocationMarker);
  });
}

function updateQueueLocationMarker(latitude, longitude) {
  // delete existing marker
  if (queueLocationMarker != null) {
    queueLocationMarker.setMap(null);
  }

  // create info window
  var infoWindow = new google.maps.InfoWindow({
    content: "New queue location"
  });

  // add new marker
  var latlng = new google.maps.LatLng(latitude, longitude);
  queueLocationMarker = new google.maps.Marker({
    map: googleMap,
    label: "Queue",
    title: "Queue Location",
    draggable: true,
    position: latlng
  });

  var onQueueLocationUpdated = function(latitude, longitude) {
    // update form
    $("#form-latitude").val(latitude);
    $("#form-longitude").val(longitude);
  }

  // add event handler for info window
  queueLocationMarker.addListener('click', function() {
    infoWindow.open(googleMap, queueLocationMarker);
  });

  // add event handler for drag end
  queueLocationMarker.addListener('dragend', function(event) {
    onQueueLocationUpdated(event.latLng.lat(), event.latLng.lng());
  });

  // update UI
  onQueueLocationUpdated(latitude, longitude);
}

// requests the current device location, updates the map
// and reverse-geocodes the address
function updateFormWithDeviceLocation() {
  Materialize.toast(getString("locatingDevice"), 3000);
  requestDeviceLocation().then(function(location){
    deviceLocation = location;

    var latitude = location.coords.latitude;
    var longitude = location.coords.longitude;

    // update form
    $("#form-latitude").val(latitude);
    $("#form-longitude").val(longitude);

    // update map
    centerMapAt(latitude, longitude);
    updateDeviceLocationMarker(latitude, longitude);
    updateQueueLocationMarker(latitude, longitude);

    // reverse-geocode
    var geocoder = new google.maps.Geocoder();
    geocodeLocation(latitude, longitude, geocoder, googleMap).then(function(results){
      updateFormWithGoogleAddressData(results);
    }).catch(function(error){
      console.log(error);
      ui.showErrorMessage(error);
    });
  }).catch(function(error){
    console.log(error);
    ui.showErrorMessage(error);
  });
}

// extracts address parts of a Google Maps location
// object and updates the form
function updateFormWithGoogleAddressData(data) {
  try {
    console.log(data[0]);
    var components = data[0].address_components;
    for (component of components) {
      for (type of component.types) {
        switch (type) {
          case "street_number":
            $("#form-address-street-number").val(component.long_name);
            break;
          case "route":
            $("#form-address-street").val(component.long_name);
            break;
          case "locality":
            $("#form-address-city").val(component.long_name);
            break;
          case "country":
            $("#form-address-country").val(component.long_name);
            break;
          case "postal_code":
            $("#form-address-postal-code").val(component.long_name);
            break;
        }
      }
    }
  } catch(ex) {
    console.log(ex);
  }
}

// creates an address string from all form values
function getAddressStringFromForm() {
  var address = $("#form-address-street").val() + " ";
  address += $("#form-address-street-number").val() + ", ";
  address += $("#form-address-postal-code").val() + " ";
  address += $("#form-address-city").val() + ", ";
  address += $("#form-address-country").val();
  return address;
}

// converts an address string into a location
function geocodeAddress(address, geocoder) {
  Materialize.toast(getString("geocodingAddress"), 3000);

  var promise = new Promise(function(resolve, reject) {
    try {
      geocoder.geocode({
        "address": address
      }, function(results, status) {
        if (status === google.maps.GeocoderStatus.OK) {
          var latitude = results[0].geometry.location.lat();
          var longitude = results[0].geometry.location.lng();
          var location = {
            "latitude": latitude,
            "longitude": longitude
          }
          resolve(location);
        } else {
          reject(status);
        }
      });
    } catch(ex) {
      reject(ex);
    }
  });
  return promise;
}

// converts a location string into an address
function geocodeLocation(latitude, longitude, geocoder, map) {
  Materialize.toast(getString("geocodingLocation"), 3000);

  var promise = new Promise(function(resolve, reject) {
    try {
      var latlng = new google.maps.LatLng(latitude, longitude);
      geocoder.geocode({
        'location': latlng
      }, function(results, status) {
        if (status === google.maps.GeocoderStatus.OK) {
          resolve(results);
        } else {
          reject(status);
        }
      });
    } catch(ex) {
      reject(ex);
    }
  });
  return promise;
}

function setupImageUpload() {
  $("#changeImageButton").click(function() {
    $("#imageUploadModal").openModal();
  });

  var imageFileInput = $("#imageFileInput").get(0);
  imageFileInput.addEventListener('change', function(event) {
    var queueKeyId = existingQueue.key.id;
    var files = $("#imageFileInput").get(0).files;
    if (files.length < 1) {
      return;
    }

    var file = files[0];

    Materialize.toast(getString("uploadStarted"), 3000);
    $(".loadingState").show();

    intelliqApi.uploadQueuePhoto(queueKeyId, file, authenticator.getGoogleUserIdToken()).then(function(data){
      Materialize.toast(getString("uploadSuccessful"), 3000);
      $(".loadingState").hide();
      $("#imageUploadModal").closeModal();
    }).catch(function(error){
      console.log(error);
      Materialize.toast(getString("uploadFailed"), 3000);
      ui.showErrorMessage(error);
      $(".loadingState").hide();
    });
  });
}