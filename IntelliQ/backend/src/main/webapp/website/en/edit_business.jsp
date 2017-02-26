<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
  <head>
    <title>IntelliQ.me - Edit Business</title>
    <%@include file="../includes/en/common_head.jsp"%>
  </head>

  <body>
    <%@include file="../includes/en/common_navigation.jsp"%>
    <main>
      <div class="container">

        <!-- Business -->
        <div class="section">
          <h5 id="businessHeading">Add Business</h5>
          <div class="divider"></div>
          <div class="row">

            <div class="col s12 card-spacing loadingState">
              <div class="progress">
                <div class="indeterminate"></div>
              </div>
            </div>

            <!-- Form -->
            <form class="col s12 vertical-spacing">
              
              <!-- ID -->
              <div class="row">
                <div class="input-field col s12 m6">
                  <input disabled value="Not yet set" id="form-key-id" type="text">
                  <label for="form-key-id">Key ID</label>
                </div>
              </div>

              <div class="row">
              <!-- Name -->
                <div class="input-field col s12 m6">
                  <input placeholder="Your Business Name" id="form-name" type="text">
                  <label for="form-name">Name</label>
                </div>

                <!-- Email -->
                <div class="input-field col s12 m6">
                  <input placeholder="contact@business-name.com" id="form-mail" type="email">
                  <label for="form-mail">Contact Email</label>
                </div>
              </div>

              <!-- Logo -->
              <div class="row">
                <div id="changeImageContainer" class="input-field col s12 m6 hide">
                  <a id="changeImageButton" class="btn-large waves-effect waves-light primary-color disabled">
                    Change Logo
                  </a>
                </div>
              </div>

              <!-- Buttons -->
              <div class="row">
                <div class="input-field col s12 m6">
                  <a id="saveBusinessButton" class="btn-large waves-effect waves-light primary-color">
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
          <h4>Business Logo</h4>
          
          <p>Your logo will be visible in different parts of the app, mainly as small icon below queues that belong to your business. Square images with a transparent background will look best!</p>

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
    <script src="${staticUrl}js/edit_business.js"></script>
    <script src="${staticUrl}js/manage.js" defer></script>
  </body>
</html>