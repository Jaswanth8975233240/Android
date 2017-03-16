<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
  <head>
    <title>IntelliQ.me - Edit Queue</title>
    <%@include file="../includes/en/common_head.jsp"%>
  </head>

  <body>
    <%@include file="../includes/en/common_navigation.jsp"%>
    <main>
      <div class="container">

        <!-- Queue -->
        <div class="section">
          <h5 id="queueHeading">Add Queue</h5>
          <div class="divider"></div>
          <div class="row">

            <div class="col s12 card-spacing loadingState">
              <div class="progress">
                <div class="indeterminate"></div>
              </div>
            </div>

            <!-- Form -->
            <form class="col s12 vertical-spacing">
              
              <div class="row hide">
                <!-- ID -->
                <div class="input-field col s12 m6">
                  <input disabled value="Not yet set" id="form-key-id" type="text">
                  <label for="form-key-id">Key ID</label>
                </div>

                <!-- Business ID -->
                <div class="input-field col s12 m6">
                  <input disabled value="Not yet set" id="form-business-key-id" type="text">
                  <label for="form-business-key-id">Business Key ID</label>
                </div>
              </div>
              
              <div class="row">
                <!-- Name -->
                <div class="input-field col s12 m6 l4">
                  <input placeholder="Your Queue Name" id="form-name" type="text">
                  <label for="form-name">Name</label>
                </div>

                <!-- Description -->
                <div class="input-field col s12 m6 l8">
                  <input placeholder="What do people queue up for?" id="form-description" type="text">
                  <label for="form-description">Description</label>
                </div>
              </div>

              <!-- Photo -->
              <div class="row">
                <div id="changeImageContainer" class="input-field col s12 m6 hide">
                  <a id="changeImageButton" class="btn-large waves-effect waves-light primary-color disabled">
                    Change Photo
                  </a>
                </div>
              </div>

              <!-- Visibility -->
              <div class="row">
                <div class="col s12">
                  <h5>Visibility</h5>
                  <p>Select if this queue should show up for everyone in the IntelliQ app. Customers will have to enter a code or open a link in order to join a private queue.</p>
                  <p>
                    <input name="group-visibility" type="radio" id="form-visibility-hidden" class="with-gap" checked />
                    <label for="form-visibility-hidden">Private → Invisible, code required</label>
                  </p>
                  <p>
                    <input name="group-visibility" type="radio" id="form-visibility-visible" class="with-gap" />
                    <label for="form-visibility-visible">Public → Visible to anyone</label>
                  </p>
                </div>
              </div>

              <!-- Waiting Time -->
              <div class="row">
                <div class="col s12">
                  <h5>Average processing time</h5>
                  <p>Please let us know how long it takes to process one customer. This duration will be used to calculate waiting time estimations and might be adjusted automatically.</p>
                </div>
              </div>

              <div class="row">
                <div class="input-field col s6 m3 l2">
                  <input placeholder="3" value="3" id="form-average-waiting-time" type="number" min="1" max="86400" step="5">
                  <label for="form-average-waiting-time">Processing time</label>
                </div>
                <div class="input-field col s6 m3 l2" id="form-average-waiting-time-unit">
                  <select>
                    <option value="seconds">Seconds</option>
                    <option value="minutes" selected>Minutes</option>
                  </select>
                  <label>Unit</label>
                </div>
              </div>

              <!-- Location -->
              <div class="row">
                <div class="col s12">
                  <h5>Location</h5>
                  <p>Please provide the most accurate location possible, it will be important for geofencing and estimations. To get the latitude and longitude, click <a id="updateLocationFromAddressButton">get location from address</a> after filling out the address fields. To get the address of the current location, click <a id="updateAddressFromLocationButton">get address from location</a> after placing a marker on the map. You can also <a id="updateDeviceLocationButton">use your current location</a>.</p>
                </div>
              </div>

              <div class="row">
                <!-- Postal Code -->
                <div class="input-field col s4 m2">
                  <input placeholder="10178" id="form-address-postal-code" type="text">
                  <label for="form-address-postal-code">Postal Code</label>
                </div>

                <!-- City -->
                <div class="input-field col s8 m4">
                  <input placeholder="Berlin" id="form-address-city" type="text">
                  <label for="form-address-city">City</label>
                </div>

                <!-- Country -->
                <div class="input-field col s12 m6">
                  <input placeholder="Germany" id="form-address-country" type="text">
                  <label for="form-address-country">Country</label>
                </div>
              </div>

              <div class="row">
                <!-- Street -->
                <div class="input-field col s8 m4">
                  <input placeholder="Panoramastraße" id="form-address-street" type="text">
                  <label for="form-address-street">Street</label>
                </div>

                <!-- Number -->
                <div class="input-field col s4 m2">
                  <input placeholder="1A" id="form-address-street-number" type="text">
                  <label for="form-address-street-number">Number</label>
                </div>

                <!-- Latitude -->
                <div class="input-field col s6 m3">
                  <input placeholder="52.520815" id="form-latitude" type="text">
                  <label for="form-latitude">Latitude</label>
                </div>

                <!-- Longitude -->
                <div class="input-field col s6 m3">
                  <input placeholder="13.409419" id="form-longitude" type="text">
                  <label for="form-longitude">Longitude</label>
                </div>
              </div>

              <!-- Map -->
              <div class="row">
                <div class="col s12">
                  <div id="map" style="width: 100%; height: 300px;"></div>
                </div>
              </div>

              <!-- Buttons -->
              <div class="row">
                <div class="input-field col s12 m6">
                  <a id="saveQueueButton" class="btn-large waves-effect waves-light primary-color">
                    <i class="material-icons right">done</i> 
                    Save
                  </a>
                </div>
              </div>
              
            </form>

          </div>
        </div>

      </div>

      <!-- Image upload modal -->
      <div id="imageUploadModal" class="modal">
        <div class="modal-content">
          <h4>Queue Photo</h4>
          
          <p>The photo of your queue will be publicly visible in all apps for every user. A vibrant photo without any text works best!</p>

          <br/>

          <form enctype="multipart/form-data" method="post" action="${rootUrl}/image/">
            <div class="file-field input-field">
              <div class="btn waves-effect waves-light primary-color">
                <span>Browse</span>
                <input id="imageFileInput" name="image" type="file" accept="image/jpeg,image/jpg,image/png">
              </div>
              <div class="file-path-wrapper">
                <input class="file-path validate" type="text">
              </div>
            </div>
          </form>

        </div>
        <div class="modal-footer">
          <a id="closeImageUploadButton" class="modal-action modal-close waves-effect waves-light btn-flat">Close</a>
        </div>
      </div>

    </main>
    <%@include file="../includes/en/common_footer.jsp"%>
    <script src="${staticUrl}js/edit_queue.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBtc8JtwfK8qT9TX8Tkln4nd7IwR0rP9dY&callback=initializeMap"></script>
    <script src="${staticUrl}js/manage.js" defer></script>
  </body>
</html>