var tracking = function(){

  function log(message) {
    console.log("Tracking: " + message);
  }

  var tracking = {
  };

  tracking.CATEGORY_QUEUE_MANAGE = "Manage Queue";
  tracking.CATEGORY_QUEUE_EDIT = "Edit Queue";
  tracking.CATEGORY_BUSINESS_MANAGE = "Manage Business";
  tracking.CATEGORY_BUSINESS_EDIT = "Edit Business";

  tracking.trackEvent = function(category, action, label, value) {
    var fields = {
      hitType: "event",
      eventCategory: category,
      eventAction: action,
      eventLabel: label,
      eventValue: value
    }
    ga("send", fields);
    log("Tracked event: " + category + ", " + action + ", " + label + ", " + value);
  }

  tracking.trackException = function(description, fatal) {
    var fields = {
      "exDescription": description,
      "exFatal": fatal
    }
    ga('send', 'exception', fields);
    log("Tracked exception: " + description + ", " + fatal);
  }

  return tracking;
}();