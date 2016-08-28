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

        <!-- Called -->
        <div class="section">
          <h5>Called now</h5>
          <div class="divider"></div>
          <div class="row">

            <!-- Sidebar-->
            <div class="col s12 m12 l4 push-l8 vertical-spacing">
              <div class="col s12 card-spacing">
                <a id="markAllAsDoneButton" class="btn-large waves-effect waves-light primary-color fill-width">
                  <i class="material-icons right">done_all</i> 
                  All Done
                </a>
              </div>
            </div>

            <!-- List -->
            <div class="col s12 m12 l8 pull-l4 vertical-spacing">
              <div class="col s12 emptyState hide">
                <p>There's currently no one called.</p>
              </div>
              <div id="calledContainer" class="col s12 vertical-spacing">
                <div class="loadingState card-spacing">
                  <div class="progress">
                    <div class="indeterminate"></div>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>

        <!-- Waiting -->
        <div class="section">
          <h5>Waiting</h5>
          <div class="divider"></div>
          <div class="row">

            <!-- Sidebar-->
            <div class="col s12 m12 l4 push-l8 vertical-spacing">
              <div class="col s12 card-spacing">
                <a id="callNextCustomerButton" class="btn-large waves-effect waves-light primary-color fill-width">
                  <i class="material-icons right">done_all</i> 
                  Call Next
                </a>
                <a id="addNewCustomerButton" class="btn-large waves-effect waves-light primary-color fill-width">
                  <i class="material-icons right">done_all</i> 
                  Add new
                </a>
              </div>
            </div>

            <!-- List -->
            <div class="col s12 m12 l8 pull-l4 vertical-spacing">
              <div class="col s12 emptyState hide">
                <p>There's currently no one waiting.</p>
              </div>
              <div id="waitingContainer" class="col s12 vertical-spacing">
                <div class="loadingState card-spacing">
                  <div class="progress">
                    <div class="indeterminate"></div>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>

        <!-- Processed -->
        <div class="section">
          <h5>Processed</h5>
          <div class="divider"></div>
          <div class="row">

            <!-- Sidebar-->
            <div class="col s12 m12 l4 push-l8 vertical-spacing">
              <div class="col s12 card-spacing">
                <a id="clearProcessedCustomersButton" class="btn-large waves-effect waves-light primary-color fill-width">
                  <i class="material-icons right">done_all</i> 
                  Clear All
                </a>
              </div>
            </div>

            <!-- List -->
            <div class="col s12 m12 l8 pull-l4 vertical-spacing">
              <div class="col s12 emptyState hide">
                <p>There are no recently processed items</p>
              </div>
              <div id="processedContainer" class="col s12 vertical-spacing">
                <div class="loadingState card-spacing">
                  <div class="progress">
                    <div class="indeterminate"></div>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>

      </div>
    </main>
    <%@include file="../includes/en/common_footer.jsp"%>
    <script src="${staticUrl}js/manage_queue.js"></script>
  </body>
</html>