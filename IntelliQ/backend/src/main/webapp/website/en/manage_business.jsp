<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
  <head>
    <title>IntelliQ.me - Manage</title>
    <%@include file="../includes/en/common_head.jsp"%>
  </head>

  <body>
    <%@include file="../includes/en/common_navigation.jsp"%>
    <main>
      <div class="container">

        <!-- Queues -->
        <div class="section">
          <h5>Your Queues</h5>
          <div class="divider"></div>
          <div class="row">

            <!-- List -->
            <div class="col s12 m12 l8 vertical-spacing">
              <div class="col s12 emptyState hide">
                <p>We couldn't find any queues associated to this business. Add a new one in order to manage your customers.</p>
              </div>
              <div id="queuesContainer">
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
                <a id="addQueueButton" href="${rootUrl}/edit/queue/" class="btn-large waves-effect waves-light primary-color fill-width">
                  <i class="material-icons right">business</i> 
                  Add Queue
                </a>
                <blockquote>Queues can keep track of your customers. You can add multiple queues to one business. Make sure that you don't confuse your customers and choose unique names.</blockquote>
              </div>
            </div>

          </div>
        </div>

      </div>
    </main>
    <%@include file="../includes/en/common_footer.jsp"%>
    <script src="${staticUrl}js/manage.js" defer></script>
    <script type="text/javascript">
      $(function(){
        var statusChangeListener = {
          onUserAvailable: function(user) {
            updateAddQueueUrl();
            var businessKeyId = getUrlParamOrCookie("businessKeyId");
            requestQueues(businessKeyId);
          }
        };
        authenticator.registerStatusChangeListener(statusChangeListener);
      });

      function requestQueues(businessKeyId) {
        var request = intelliqApi.getBusiness(businessKeyId).withQueues(true);
        request.send().then(function(data){
          var businesses = intelliqApi.getBusinessesFromResponse(data);
          console.log(businesses);

          var queues = intelliqApi.getQueuesFromBusinessResponse(data);
          console.log(queues);
          renderQueues(queues, $("#queuesContainer"));
        }).catch(function(error){
          console.log(error);
          showErrorMessage(error);
        });
      }

      function updateAddQueueUrl() {
        var userProfile = authenticator.getGoogleUser().getBasicProfile();
        var businessKeyId = getUrlParamOrCookie("businessKeyId");
        var url = intelliqApi.getUrls().forQueue().add(businessKeyId, userProfile.getName());
        $("#addQueueButton").attr("href", url);
      }
    </script>
  </body>
</html>