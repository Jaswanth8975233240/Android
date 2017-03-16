var intelliqApi = function(){

  function log(message) {
    if (typeof message !== "string") {
      message = "\n" + JSON.stringify(message, null, 2)
    }
    console.log("IntelliQ.me API: " + message);
  }

  function useDevelopmentServer() {
    return top.location.hostname == "localhost";
  }

  var api = {
    lastRequestTimestamp: -1,
  };

  // Google client ID
  api.CLIENT_ID_WEB = "1008259459239-t1huos5n6bhkin3is2jlqgkjv9h7mheh.apps.googleusercontent.com";
  api.GOOGLE_API_TOKEN = "AIzaSyBtc8JtwfK8qT9TX8Tkln4nd7IwR0rP9dY";

  // Website (for displaying links)
  api.HOST_INTELLIQ_ME = "https://intelliq.me/";

  // Development server
  api.HOST_LOCAL = "http://localhost:8080/";

  // App Engine (for api requests)
  api.APP_ENGINE_VERSION = 3;
  api.HOST_APP_ENGINE = "https://intelliq-me.appspot.com/";
  api.HOST_APP_ENGINE_VERSIONED = "https://" + api.APP_ENGINE_VERSION + "-dot-intelliq-me.appspot.com/";

  if (useDevelopmentServer()) {
    api.HOST = api.HOST_LOCAL;
  } else {
    api.HOST = api.HOST_APP_ENGINE;
  }

  // Request endpoints
  api.ENDPOINT_API = api.HOST + "api/";
  
  api.ENDPOINT_USER = api.ENDPOINT_API + "user/";
  api.ENDPOINT_USER_GET = api.ENDPOINT_USER + "get/";
  api.ENDPOINT_USER_SIGN_IN = api.ENDPOINT_USER + "signin/";
  api.ENDPOINT_USER_SET = api.ENDPOINT_USER + "set/";
  api.ENDPOINT_USER_SET_LOCATION = api.ENDPOINT_USER_SET + "location/";
  api.ENDPOINT_USER_SET_STATUS = api.ENDPOINT_USER_SET + "status/";

  api.ENDPOINT_BUSINESS = api.ENDPOINT_API + "business/";
  api.ENDPOINT_BUSINESS_GET = api.ENDPOINT_BUSINESS + "get/";
  api.ENDPOINT_BUSINESS_ADD = api.ENDPOINT_BUSINESS + "add/";
  api.ENDPOINT_BUSINESS_EDIT = api.ENDPOINT_BUSINESS + "edit/";
  api.ENDPOINT_BUSINESS_FROM = api.ENDPOINT_BUSINESS + "from/";

  api.ENDPOINT_QUEUE = api.ENDPOINT_API + "queue/";
  api.ENDPOINT_QUEUE_GET = api.ENDPOINT_QUEUE + "get/";
  api.ENDPOINT_QUEUE_NEARBY = api.ENDPOINT_QUEUE + "nearby/";
  api.ENDPOINT_QUEUE_ADD = api.ENDPOINT_QUEUE + "add/";
  api.ENDPOINT_QUEUE_EDIT = api.ENDPOINT_QUEUE + "edit/";
  api.ENDPOINT_QUEUE_POPULATE = api.ENDPOINT_QUEUE + "populate/";
  api.ENDPOINT_QUEUE_DONE = api.ENDPOINT_QUEUE + "done/";
  api.ENDPOINT_QUEUE_CLEAR = api.ENDPOINT_QUEUE + "clear/";
  api.ENDPOINT_QUEUE_ITEMS = api.ENDPOINT_QUEUE + "items/";

  api.ENDPOINT_QUEUE_ITEM = api.ENDPOINT_API + "item/";
  api.ENDPOINT_QUEUE_ITEM_GET = api.ENDPOINT_QUEUE_ITEM + "get/";
  api.ENDPOINT_QUEUE_ITEM_FROM = api.ENDPOINT_QUEUE_ITEM + "from/";
  api.ENDPOINT_QUEUE_ITEM_ADD = api.ENDPOINT_QUEUE_ITEM + "add/";
  api.ENDPOINT_QUEUE_ITEM_DELETE = api.ENDPOINT_QUEUE_ITEM + "delete/";
  api.ENDPOINT_QUEUE_ITEM_STATUS = api.ENDPOINT_QUEUE_ITEM + "status/";
  api.ENDPOINT_QUEUE_ITEM_REPORT = api.ENDPOINT_QUEUE_ITEM + "report/";

  api.ENDPOINT_IMAGE = "image/";

  // Webpages
  if (useDevelopmentServer()) {
    api.PAGE_LINK = api.HOST_LOCAL;
  } else {
    api.PAGE_LINK = api.HOST_INTELLIQ_ME;
  }
  api.PAGE_LINK_MANAGE = api.PAGE_LINK + "manage/";
  api.PAGE_LINK_EDIT = api.PAGE_LINK + "edit/";
  api.PAGE_LINK_CREATE = api.PAGE_LINK + "create/";
  api.PAGE_LINK_DISPLAY = api.PAGE_LINK + "display/";

  api.PAGE_LINK_WEB_APP = api.PAGE_LINK + "apps/web/";
  api.PAGE_LINK_WEB_APP_NEARBY = api.PAGE_LINK_WEB_APP + "nearby/";
  api.PAGE_LINK_WEB_APP_QUEUE = api.PAGE_LINK_WEB_APP + "queue/";
  api.PAGE_LINK_WEB_APP_BUSINESS = api.PAGE_LINK_WEB_APP + "business/";
  api.PAGE_LINK_WEB_APP_TICKETS = api.PAGE_LINK_WEB_APP + "tickets/";
  
  api.PATH_BUSINESS = "business/";
  api.PATH_QUEUE = "queue/";

  // Queue Items Status
  api.STATUS_ALL = -1;
  api.STATUS_WAITING = 0;
  api.STATUS_CANCELED = 1;
  api.STATUS_CALLED = 2;
  api.STATUS_DONE = 3;

  // Queue visibility
  api.VISIBILITY_PRIVATE = 0;
  api.VISIBILITY_PUBLIC = 1;

  // Entry types
  api.ENTRY_TYPE_BUSINESS = "BusinessEntry";
  api.ENTRY_TYPE_QUEUE = "QueueEntry";
  api.ENTRY_TYPE_QUEUE_ITEM = "QueueItemEntry";
  api.ENTRY_TYPE_USER = "UserEntry";
  api.ENTRY_TYPE_PERMISSION = "PermissionEntry";
  api.ENTRY_TYPE_IMAGE = "ImageEntry";

  // Image types
  api.IMAGE_TYPE_LOGO = 0;
  api.IMAGE_TYPE_PHOTO = 1;

  // Update intervals
  api.UPDATE_INTERVAL_CASUAL = 1000 * 30;
  api.UPDATE_INTERVAL_DEFAULT = 1000 * 15;
  api.UPDATE_INTERVAL_FAST = 1000 * 10;
  api.UPDATE_INTERVAL_DEMO = 1000 * 5;

  /*
    Requests
  */
  api.request = function(endpoint) {
    var request = {};

    request.endpoint = endpoint;

    request.addParameters = function(params) {
      for (var i = 0; i < params.length; i++) {
        request.addParameter(params[i]);
      }
      return request;
    }

    request.addParameter = function(key, value) {
      if (request.params == null) {
        request.params = [];
      }

      // type-check the passed argument
      var param;
      if (typeof key == 'string') {
        param = [key, value];
      } else {
        param = key;
      }

      // make sure that key & value are not null
      if (param == null || param[0] == null || param[1] == null) {
        return request;
      }

      // avoid dublicate keys
      for (var i = 0; i < request.params.length; i++) {
        if (request.params[i][0] == param[0]) {
          //log("Overwriting dublicate parameter: " + request.params[i][0]);
          request.params.splice(i, 1);
        }
      }

      request.params.push(param);
      return request;
    }

    request.invalidateCache = function(value) {
      return request.addParameter("invalidateCache", value);
    }

    request.setCount = function(value) {
      return request.addParameter("count", value);
    }

    request.setOffset = function(value) {
      return request.addParameter("offset", value);
    }

    request.setGoogleIdToken = function(value) {
      return request.addParameter("googleIdToken", value);
    }

    request.setFacebookIdToken = function(value) {
      return request.addParameter("facebookIdToken", value);
    }

    request.onSuccess = function(callback) {
      request.onSuccessCallback = callback;
      return request;
    }

    request.onError = function(callback) {
      request.onErrorCallback = callback;
      return request;
    }

    request.buildRequestUrl = function() {
      var requestUrl = request.endpoint;
      if (request.params) {
        requestUrl += "?";
        for (var i = 0; i < request.params.length; i++) {
          requestUrl += request.params[i][0] + "=" + encodeURIComponent(request.params[i][1]) + "&";
        }
        requestUrl = requestUrl.substring(0, requestUrl.length - 1);
      }
      return requestUrl;
    }

    request.send = function() {
      request.url = request.buildRequestUrl();
      return api.sendRequest(request);
    }

    request.reload = function() {
      if (request.data != null && request.onSuccessCallback != null) {
        request.onSuccessCallback(request.data);
        return request;
      } else {
        return request.send();
      }
    }
    
    return request;
  }

  api.sendRequest = function(request) {
    var promise = new Promise(function(resolve, reject) {
      if (request.completed != null && !request.completed) {
        reject("Previous request has not completed yet.");
        return;
      }
      $.ajax({
        url : request.url,
        type : 'GET',
        dataType : 'json',
        beforeSend : function(xhr) {
          log("Requesting: " + request.url);
          request.completed = false;
          api.lastRequestTimestamp = (new Date()).getTime();
          //xhr.setRequestHeader("X-Mashape-Authorization", api.apiKey);
        },
        success : function(data, status, xhr) {
          request.data = data;
          // check if the API returned a valid response
          if (data.statusCode && data.statusCode == 200) {
            // looks good
            if (request.onSuccessCallback != null) {
              request.onSuccessCallback(data);
            }
            resolve(data);
          } else {
            // something went wrong, try to extract error message
            if (data.statusMessage) {
              this.error(xhr, status, data.statusMessage);
            } else {
              this.error(xhr, status, "Request didn't return a valid response:\n" + JSON.stringify(data));
            }
          }
        },
        error : function(xhr, status, error) {
          console.log(xhr);
          console.log(status);
          console.log(error);

          request.error = error;
          log(error)

          if (xhr.responseText) {
            request.data = JSON.parse(xhr.responseText);
          } else {
            request.data = { "error": error };
          }
          
          if (request.onErrorCallback != null) {
            request.onErrorCallback(error);
          }

          reject(error);
        },
        complete : function (){
          request.completed = true;
        }
      });
    });
    return promise;
  }

  /*
    User endpoints
  */
  api.getUser = function(userKeyId) {
    var request = api.request(api.ENDPOINT_USER_GET);
    request.addParameter("userKeyId", userKeyId);
    return request;
  }

  api.signInUser = function() {
    var request = api.request(api.ENDPOINT_USER_SIGN_IN);
    return request;
  }

  api.setUserLocation = function(userKeyId, latitude, longitude) {
    var request = api.request(api.ENDPOINT_USER_SET_LOCATION);
    request.addParameter("userKeyId", userKeyId);
    request.addParameter("latitude", latitude);
    request.addParameter("longitude", longitude);
    return request;
  }

  api.setUserStatus = function(userKeyId, status) {
    var request = api.request(api.ENDPOINT_USER_SET_STATUS);
    request.addParameter("userKeyId", userKeyId);
    request.addParameter("status", status);
    return request;
  }

  /*
    Business endpoints
  */
  api.getBusiness = function(businessKeyId) {
    var request = api.request(api.ENDPOINT_BUSINESS_GET);
    request.addParameter("businessKeyId", businessKeyId);
    request.addParameter("includeQueues", "true");

    request.withQueues = function(value) {
      if (value) {
        request.addParameter("includeQueues", "true");
      } else {
        request.addParameter("includeQueues", "false");
      }
      return request;
    }

    return request;
  }

  api.addBusiness = function(name, mail) {
    var request = api.request(api.ENDPOINT_BUSINESS_ADD);
    request.addParameter("name", name);
    request.addParameter("mail", mail);
    request.addParameter("addQueue", "false");

    request.addQueue = function(value) {
      if (value) {
        request.addParameter("addQueue", "true");
      } else {
        request.addParameter("addQueue", "false");
      }
      return request;
    }

    return request;
  }

  api.editBusiness = function(businessKeyId, name, mail) {
    var request = api.request(api.ENDPOINT_BUSINESS_EDIT);
    request.addParameter("businessKeyId", businessKeyId);
    request.addParameter("name", name);
    request.addParameter("mail", mail);

    return request;
  }

  api.getBusinessesFrom = function(userKeyId) {
    var request = api.request(api.ENDPOINT_BUSINESS_FROM);
    request.addParameter("userKeyId", userKeyId);
    return request;
  }

  /*
    Queue endpoints
  */
  api.getQueue = function(queueKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_GET);
    request.addParameter("queueKeyId", queueKeyId);

    request.includeBusiness = function(value) {
      if (value) {
        request.addParameter("includeBusiness", "true");
      } else {
        request.addParameter("includeBusiness", "false");
      }
      return request;
    }

    return request;
  }

  api.getNearbyQueues = function(latitude, longitude) {
    var request = api.request(api.ENDPOINT_QUEUE_NEARBY);
    request.addParameter("latitude", latitude);
    request.addParameter("longitude", longitude);

    request.inRange = function(distance) {
      request.addParameter("distance", distance);
      return request;
    }

    request.includeBusinesses = function(value) {
      if (value) {
        request.addParameter("includeBusinesses", "true");
      } else {
        request.addParameter("includeBusinesses", "false");
      }
      return request;
    }

    return request;
  }

  api.addQueue = function(queue) {
    var request = api.request(api.ENDPOINT_QUEUE_ADD);
    request.addParameter("businessKeyId", queue.businessKeyId);
    request.addParameter("name", queue.name);
    request.addParameter("description", queue.description);

    request.addParameter("averageWaitingTime", queue.averageWaitingTime);
    request.addParameter("visibility", queue.visibility);

    request.addParameter("latitude", queue.latitude);
    request.addParameter("longitude", queue.longitude);

    request.addParameter("country", queue.country);
    request.addParameter("city", queue.city);
    request.addParameter("postalCode", queue.postalCode);
    request.addParameter("street", queue.street);
    request.addParameter("number", queue.number);

    return request;
  }

  api.addQueueWithParameters = function(businessKeyId, name, description, averageWaitingTime) {
    var request = api.request(api.ENDPOINT_QUEUE_ADD);
    request.addParameter("businessKeyId", businessKeyId);
    request.addParameter("name", name);
    request.addParameter("description", description);
    request.addParameter("averageWaitingTime", averageWaitingTime);
    request.addParameter("visibility", api.VISIBILITY_PRIVATE);
    request.addParameter("latitude", -1);
    request.addParameter("longitude", -1);

    request.withLocation = function(latitude, longitude) {
      request.addParameter("latitude", latitude);
      request.addParameter("longitude", longitude);
      return request;
    }

    request.withAddress = function(country, city, postalCode, street, number) {
      request.addParameter("country", country);
      request.addParameter("city", city);
      request.addParameter("postalCode", postalCode);
      request.addParameter("street", street);
      request.addParameter("number", number);
      return request;
    }

    request.withVisibility = function(value) {
      request.addParameter("visibility", value);
      return request;
    }

    return request;
  }

  api.editQueue = function(queue) {
    var request = api.request(api.ENDPOINT_QUEUE_EDIT);
    request.addParameter("queueKeyId", queue.key.id);
    request.addParameter("businessKeyId", queue.businessKeyId);
    request.addParameter("name", queue.name);
    request.addParameter("description", queue.description);

    request.addParameter("averageWaitingTime", queue.averageWaitingTime);
    request.addParameter("visibility", queue.visibility);

    request.addParameter("latitude", queue.latitude);
    request.addParameter("longitude", queue.longitude);

    request.addParameter("country", queue.country);
    request.addParameter("city", queue.city);
    request.addParameter("postalCode", queue.postalCode);
    request.addParameter("street", queue.street);
    request.addParameter("number", queue.number);

    return request;
  }

  api.editQueueWithParameters = function(queueKeyId, name, description, averageWaitingTime) {
    var request = api.request(api.ENDPOINT_QUEUE_EDIT);
    request.addParameter("queueKeyId", queueKeyId);
    request.addParameter("name", name);
    request.addParameter("description", description);
    request.addParameter("averageWaitingTime", averageWaitingTime);

    request.withLocation = function(latitude, longitude) {
      request.addParameter("latitude", latitude);
      request.addParameter("longitude", longitude);
      return request;
    }

    request.withAddress = function(country, city, postalCode, street, number) {
      request.addParameter("country", country);
      request.addParameter("city", city);
      request.addParameter("postalCode", postalCode);
      request.addParameter("street", street);
      request.addParameter("number", number);
      return request;
    }

    request.withVisibility = function(value) {
      request.addParameter("visibility", value);
      return request;
    }

    return request;
  }

  api.populateQueue = function(queueKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_POPULATE);
    request.addParameter("queueKeyId", queueKeyId);
    request.addParameter("count", 25);

    request.withItems = function(value) {
      request.addParameter("count", value);
      return request;
    }

    return request;
  }

  /*
    Queue item endpoints
  */
  api.getQueueItems = function(queueKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_ITEMS);
    request.addParameter("queueKeyId", queueKeyId);
    return request;
  }

  api.getQueueItem = function(queueItemKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_ITEM_GET);
    request.addParameter("queueItemKeyId", queueItemKeyId);
    return request;
  }

  api.getQueueItemsFrom = function(userKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_ITEM_FROM);
    request.addParameter("userKeyId", userKeyId);
    return request;
  }

  api.addQueueItem = function(queueKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_ITEM_ADD);
    request.addParameter("queueKeyId", queueKeyId);
    request.addParameter("showName", "true");
    request.addParameter("usingApp", "false");

    request.withName = function(value) {
      request.addParameter("name", value);
      return request;
    }

    request.hideName = function(value) {
      if (value) {
        request.addParameter("showName", "false");
      } else {
        request.addParameter("showName", "true");
      }
      return request;
    }

    request.usingApp = function(value) {
      if (value) {
        request.addParameter("usingApp", "true");
      } else {
        request.addParameter("usingApp", "false");
      }
      return request;
    }

    request.withPhoneNumber = function(value) {
      request.addParameter("phoneNumber", value);
      return request;
    }

    return request;
  }

  api.deleteQueueItem = function(queueKeyId, queueItemKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_ITEM_DELETE);
    request.addParameter("queueKeyId", queueKeyId);
    request.addParameter("queueItemKeyId", queueItemKeyId);
    return request;
  }

  api.reportQueueItem = function(queueKeyId, queueItemKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_ITEM_REPORT);
    request.addParameter("queueKeyId", queueKeyId);
    request.addParameter("queueItemKeyId", queueItemKeyId);
    return request;
  }

  api.setQueueItemStatus = function(queueKeyId, queueItemKeyId, status) {
    var request = api.request(api.ENDPOINT_QUEUE_ITEM_STATUS);
    request.addParameter("queueKeyId", queueKeyId);
    request.addParameter("queueItemKeyId", queueItemKeyId);
    request.addParameter("status", status);
    return request;
  }

  api.markQueueItemAsCalled = function(queueKeyId, queueItemKeyId) {
    return api.setQueueItemStatus(queueKeyId, queueItemKeyId, api.STATUS_CALLED);
  }

  api.markQueueItemAsCanceled = function(queueKeyId, queueItemKeyId) {
    return api.setQueueItemStatus(queueKeyId, queueItemKeyId, api.STATUS_CANCELED);
  }

  api.markQueueItemAsDone = function(queueKeyId, queueItemKeyId) {
    return api.setQueueItemStatus(queueKeyId, queueItemKeyId, api.STATUS_DONE);
  }

  api.markQueueItemAsWaiting = function(queueKeyId, queueItemKeyId) {
    return api.setQueueItemStatus(queueKeyId, queueItemKeyId, api.STATUS_WAITING);
  }

  api.markAllQueueItemsAsDone = function(queueKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_DONE);
    request.addParameter("queueKeyId", queueKeyId);
    return request;
  }

  api.clearAllQueueItems = function(queueKeyId) {
    return api.clearQueueItems(queueKeyId).withStatus(api.STATUS_ALL).keepWaiting(false);
  }

  api.clearProcessedQueueItems = function(queueKeyId) {
    return api.clearQueueItems(queueKeyId).withStatus(api.STATUS_ALL).keepWaiting(true);
  }

  api.clearQueueItems = function(queueKeyId) {
    var request = api.request(api.ENDPOINT_QUEUE_CLEAR);
    request.addParameter("queueKeyId", queueKeyId);
    request.addParameter("status", api.STATUS_ALL);
    request.addParameter("clearWaiting", "false");
    request.addParameter("clearCalled", "false");

    request.withStatus = function(value) {
      request.addParameter("status", value);
      return request;
    }

    request.keepWaiting = function(value) {
      if (value) {
        request.addParameter("clearWaiting", "false");
      } else {
        request.addParameter("clearWaiting", "true");
      }
      return request;
    }

    request.keepCalled = function(value) {
      if (value) {
        request.addParameter("clearCalled", "false");
      } else {
        request.addParameter("clearCalled", "true");
      }
      return request;
    }

    return request;
  }

  /*
    Image endpoints
  */
  api.uploadImage = function(parentKeyId, type, file, googleIdToken) {
    var promise = new Promise(function(resolve, reject) {
      var formData = new FormData();
      formData.append("parentKeyId", parentKeyId);
      formData.append("type", type);
      formData.append("image", file);

      var request = new XMLHttpRequest();
      request.onreadystatechange = function () {
        if(request.readyState === XMLHttpRequest.DONE) {
          console.log("onreadystatechange");

          data = JSON.parse(request.responseText);
          console.log(data);

          // check if the API returned a valid response
          if (data.statusCode != null && data.statusCode == 200) {
            // looks good
            resolve(data);
          } else {
            // something went wrong, try to extract error message
            if (data.statusMessage != null) {
              reject(data.statusMessage);
            } else {
              reject("Request didn't return a valid response: " + data);
            }
          }
        }
      };

      var requestUrl = intelliqApi.HOST + intelliqApi.ENDPOINT_IMAGE + "?googleIdToken=" + googleIdToken;
      request.open("POST", requestUrl);
      request.send(formData);
    });
    return promise;
  }

  api.uploadBusinessLogo = function(businessKeyId, file, googleIdToken) {
    return api.uploadImage(businessKeyId, api.IMAGE_TYPE_LOGO, file, googleIdToken);
  }

  api.uploadQueuePhoto = function(queueKeyId, file, googleIdToken) {
    return api.uploadImage(queueKeyId, api.IMAGE_TYPE_PHOTO, file, googleIdToken);
  }

  /*
    Response helper
  */
  api.getEntriesFromResponse = function(data, entryType) {
    var entries = [];

    if (data == null || data.content == null) {
      return entries;
    }

    var responseEntries = data.content;

    if (responseEntries instanceof Array) {
      for (var i = 0; i < responseEntries.length; i++) {
        var entry = responseEntries[i];
        if (entry.key.kind == entryType) {
          entries.push(entry);
        }
      }
    } else {
      if (responseEntries.key.kind == entryType) {
        entries.push(responseEntries);
      }
    }

    return entries;
  }

  api.getBusinessesFromResponse = function(data) {
    return api.getEntriesFromResponse(data, api.ENTRY_TYPE_BUSINESS);
  }

  api.getQueuesFromResponse = function(data) {
    return api.getEntriesFromResponse(data, api.ENTRY_TYPE_QUEUE);
  }

  api.getQueueItemsFromResponse = function(data) {
    return api.getEntriesFromResponse(data, api.ENTRY_TYPE_QUEUE_ITEM);
  }

  api.getUsersFromResponse = function(data) {
    return api.getEntriesFromResponse(data, api.ENTRY_TYPE_USER);
  }

  api.getQueuesFromBusinessResponse = function(data) {
    var queues = [];
    var businesses = api.getEntriesFromResponse(data, api.ENTRY_TYPE_BUSINESS);
    if (businesses.length < 1) {
      return queues;
    }
    var business = businesses[0];
    if (business.queues == null) {
      return queues;
    } else {
      return business.queues;
    }
  }

  /*
    Queue Item helper
  */
  api.filterQueueItems = function(queueItems) {
    var filter = {};

    filter.byStatus = function(status) {
      var items = [];
      if (queueItems == null) {
        return items;
      }
      for (var i = 0; i < queueItems.length; i++) {
        var item = queueItems[i];
        if (item.status == status) {
          items.push(item);
        }
      }
      return items;
    }

    return filter;
  }

  api.sortQueueItems = function(queueItems) {
    var sort = {};

    sort.byTicketNumber = function() {
      queueItems.sort(function(a, b) {
        if (a.ticketNumber < b.ticketNumber)
          return -1;
        if (a.ticketNumber > b.ticketNumber)
          return 1;
        return 0;
      });
      return queueItems;
    }

    sort.byStatusChange = function() {
      queueItems.sort(function(a, b) {
        if (a.lastStatusChangeTimestamp > b.lastStatusChangeTimestamp)
          return -1;
        if (a.lastStatusChangeTimestamp < b.lastStatusChangeTimestamp)
          return 1;
        return 0;
      });
      return queueItems;
    }

    return sort;
  }

  /*
    URL helper
  */
  api.getUrls = function() {
    var urls = {};

    urls.replaceParameter = function(key, value, url) {
      if (!url) url = window.location.href;
      var re = new RegExp("([?&])" + key + "=.*?(&|#|$)(.*)", "gi"),
        hash;

      if (re.test(url)) {
        if (typeof value !== 'undefined' && value !== null)
          return url.replace(re, '$1' + key + "=" + value + '$2$3');
        else {
          hash = url.split('#');
          url = hash[0].replace(re, '$1$3').replace(/(&|\?)$/, '');
          if (typeof hash[1] !== 'undefined' && hash[1] !== null) 
            url += '#' + hash[1];
          return url;
        }
      }
      else {
        if (typeof value !== 'undefined' && value !== null) {
          var separator = url.indexOf('?') !== -1 ? '&' : '?';
          hash = url.split('#');
          url = hash[0] + separator + key + '=' + value;
          if (typeof hash[1] !== 'undefined' && hash[1] !== null) 
            url += '#' + hash[1];
          return url;
        }
        else
          return url;
      }
    }
    
    urls.forImage = function(imageKeyId) {

      urls.resizedTo = function(size) {
        if (top.location.origin == "file://") {
          return api.HOST_LOCAL + "image/" + imageKeyId + "/" + size + ".jpg";
        } else {
          return api.PAGE_LINK + "image/" + imageKeyId + "/" + size + ".jpg";
        }
      }

      urls.original = function() {
        return urls.resizedTo("original");
      }

      return urls;
    }

    urls.forQueue = function(queueEntry) {
      var queue = queueEntry;

      urls.add = function(businessKeyId, name) {
        var url = api.PAGE_LINK_EDIT + api.PATH_QUEUE;
        url = urls.replaceParameter("businessKeyId", businessKeyId, url);
        url = urls.replaceParameter("name", name, url);
        return url;
      }

      urls.display = function() {
        var url = api.PAGE_LINK_DISPLAY + api.PATH_QUEUE;
        return urls.replaceParameter("queueKeyId", queue.key.id, url);
      }

      urls.edit = function() {
        var url = api.PAGE_LINK_EDIT + api.PATH_QUEUE;
        url = urls.replaceParameter("businessKeyId", queue.businessKeyId, url);
        return urls.replaceParameter("queueKeyId", queue.key.id, url);
      }

      urls.manage = function() {
        var url = api.PAGE_LINK_MANAGE + api.PATH_QUEUE;
        return urls.replaceParameter("queueKeyId", queue.key.id, url);
      }

      urls.openInWebApp = function() {
        var url = api.PAGE_LINK_WEB_APP_QUEUE;
        return urls.replaceParameter("queueKeyId", queue.key.id, url);
      }

      return urls;
    }

    urls.forQueueItem = function(queueItemEntry) {
      var queueItem = queueItemEntry;

      urls.openInWebApp = function() {
        var url = api.PAGE_LINK_WEB_APP_TICKETS;
        return urls.replaceParameter("queueItemKeyId", queueItem.key.id, url);
      }

      return urls;
    }

    urls.forBusiness = function(businessEntry) {
      var business = businessEntry;

      urls.add = function(name, mail) {
        var url = api.PAGE_LINK_EDIT + api.PATH_BUSINESS;
        url = urls.replaceParameter("name", name, url);
        url = urls.replaceParameter("mail", mail, url);
        return url;
      }

      urls.display = function() {
        var url = api.PAGE_LINK_DISPLAY + api.PATH_BUSINESS;
        return urls.replaceParameter("businessKeyId", business.key.id, url);
      }

      urls.edit = function() {
        var url = api.PAGE_LINK_EDIT + api.PATH_BUSINESS;
        return urls.replaceParameter("businessKeyId", business.key.id, url);
      }

      urls.manage = function() {
        var url = api.PAGE_LINK_MANAGE + api.PATH_BUSINESS;
        return urls.replaceParameter("businessKeyId", business.key.id, url);
      }

      urls.openInWebApp = function() {
        var url = api.PAGE_LINK_WEB_APP_BUSINESS;
        return urls.replaceParameter("businessKeyId", business.key.id, url);
      }

      return urls;
    }

    return urls;
  }

  return api;
}();