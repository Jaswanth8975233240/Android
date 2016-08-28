<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
  <head>
    <title>IntelliQ.me - Manage</title>
    <%@include file="../includes/en/common_head.jsp"%>
    <script type="text/javascript">
      var onUserReady = function(user) {
        updateAddBusinessUrl();

        intelliqApi.getBusinessesFrom(user.key.id).send().then(function(data){
          var businesses = intelliqApi.getBusinessesFromResponse(data);
          console.log(businesses);
          renderBusinesses(businesses, $("#businessContainer"));
        }).catch(function(error){
          console.log(error);
          showErrorMessage(error);
        });
      }

      function updateAddBusinessUrl() {
        var userProfile = authenticator.getInstance().getCurrentGoogleUser().getBasicProfile();
        var url = intelliqApi.getUrls().forBusiness().add(userProfile.getName(), userProfile.getEmail());
        $("#addBusinessButton").attr("href", url);
      }
    </script>

  </head>

  <body>
    <%@include file="../includes/en/common_navigation.jsp"%>
    <main>
      <div class="container">

        <!-- Businesses -->
        <div class="section">
          <h5>Your Businesses</h5>
          <div class="divider"></div>
          <div class="row">

            <!-- List -->
            <div class="col s12 m12 l8 vertical-spacing">
              <div class="col s12 emptyState hide">
                <p>We couldn't find any businesses associated to your account. Add a new one in order to setup new queues.</p>
              </div>
              <div id="businessContainer">
                <div class="col s12 card-spacing loadingState">
                  <div class="progress">
                    <div class="indeterminate"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Sidebar-->
            <div class="col s12 m12 l4 vertical-spacing">
              <div class="col s12 card-spacing">
                <a id="addBusinessButton" href="${rootUrl}/edit/business/" class="btn-large waves-effect waves-light primary-color fill-width">
                  <i class="material-icons right">business</i> 
                  Add Businenss
                </a>
                <blockquote>Businesses are in charge of managing one or more queues. If you want other users to be able to manage a business or some of its queues, you can grant them permissions to do so. Other users won't be able to perform administrative operations, though.</blockquote>
              </div>
            </div>

          </div>
        </div>

      </div>
    </main>
    <%@include file="../includes/en/common_footer.jsp"%>
  </body>
</html>