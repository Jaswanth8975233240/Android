<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
  <head>
    <title>IntelliQ.me - Nearby</title>
    <%@include file="../includes/en/common_head.jsp"%>
  </head>

  <body>
    <%@include file="../includes/en/webapp_navigation.jsp"%>
    <main>
      <div class="container">

        <%@include file="../includes/en/webapp_tickets.jsp"%>

        <!-- Nearby queues -->
        <div class="section">
          <h5>Nearby Queues</h5>
          <div class="divider"></div>
          <div class="row">

            <!-- List -->
            <div class="col s12 m12 l8 vertical-spacing">
              <div class="col s12 emptyState hide">
                <p>Looks like there aren't any queues nearby.</p>
              </div>
              <div id="businessesContainer">
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
                <a id="createQueueButton" href="${rootUrl}/manage/" class="btn-large waves-effect waves-light primary-color fill-width">
                  <i class="material-icons right">group_add</i> 
                  Create a Queue
                </a>
                <blockquote>You can create a new queue at any time. It's free, no payment info required!</blockquote>
              </div>
            </div>

          </div>
        </div>

      </div>
    </main>
    <%@include file="../includes/en/webapp_footer.jsp"%>
    <script src="${staticUrl}js/webapp.js" defer></script>
    <script type="text/javascript">
      $(function(){
        requestNearbyQueues();
      });
    </script>
  </body>
</html>