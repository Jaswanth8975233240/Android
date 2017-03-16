<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html lang="en">
  <head>
    <title>IntelliQ.me - Queue</title>
    <%@include file="../includes/en/common_head.jsp"%>
  </head>

  <body>
    <%@include file="../includes/en/webapp_navigation.jsp"%>
    <main>
      <div class="container">
        
        <%@include file="../includes/en/webapp_tickets.jsp"%>
        
        <!-- Queue -->
        <div class="section">
          <h5>Queue</h5>
          <div class="divider"></div>
          <div class="row">

            <!-- List -->
            <div class="col s12 m12 l8 vertical-spacing">
              <div class="col s12 emptyState hide">
                <p>We're unable to find the requested queue.</p>
              </div>
              <div id="queueContainer">
                <div class="col s12 card-spacing loadingState">
                  <div class="progress">
                    <div class="indeterminate"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Sidebar-->
            <div class="col s12 m12 l4 vertical-spacing">
              <div id="joinQueueContainer" class="col s12 card-spacing">
                <a id="joinQueueButton" class="btn-large waves-effect waves-light primary-color fill-width disabled">
                  <i class="material-icons right">group_add</i> 
                  Join this Queue
                </a>
                <blockquote>In order to claim your ticket, join the queue. We'll then provide you with your ticket details.</blockquote>
              </div>
              <div id="leaveQueueContainer" class="col s12 card-spacing hide">
                <a id="leaveQueueButton" class="btn-large waves-effect waves-light warning-color fill-width disabled">
                  <i class="material-icons right">clear</i> 
                  Leave this Queue
                </a>
                <blockquote>Leaving this queue will cancel your current ticket. Keep in mind that doing this often will hurt your reputation.</blockquote>
              </div>
            </div>

          </div>
        </div>

        <!-- Join queue modal -->
        <div id="joinQueueModal" class="modal">
          <div class="modal-content">
            <h4>Join Queue</h4>
            
            <div class="row">
              <!-- Name -->
              <div class="input-field col s12 l6">
                <input id="newCustomerName" type="text">
                <label for="newCustomerName">Name</label>
              </div>

              <!-- Phone number -->
              <div id="phoneNumberContainer" class="input-field col s12 l6">
                <input id="phoneNumber" type="tel" placeholder="Optional">
                <label for="phoneNumber">Mobile Phone Number</label>
              </div>
            </div>

            <div class="switch hide">
              <label>hide name <input id="newCustomerVisibility" type="checkbox" checked="checked"> <span class="lever"></span> show name</label>
            </div>

          </div>
          <div class="modal-footer">
            <a id="sbmitNewCustomerButton" class="modal-action modal-close waves-effect waves-green btn-flat">Join</a>
          </div>
        </div>

      </div>
    </main>
    <%@include file="../includes/en/webapp_footer.jsp"%>
    <script src="${staticUrl}js/webapp.js" defer></script>
    <script src="${staticUrl}js/webapp_queue.js" defer></script>
  </body>
</html>