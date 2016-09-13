var baseUrl = location.protocol + "//" + location.hostname + (location.port && ":" + location.port) + "";

function whenAvailable(name, callback) {
    var interval = 50;
    window.setTimeout(function() {
        if (window[name]) {
            callback(window[name]);
        } else {
            window.setTimeout(arguments.callee, interval);
        }
    }, interval);
}

function getDeviceLocation() {
  var promise = new Promise(function(resolve, reject) {
    try {
      navigator.geolocation.getCurrentPosition(function(position) {
        console.log(position);
        //var lat = position.coords.latitude;
        //var long = position.coords.longitude;
        resolve(position)
      }, function() {
        reject("Location request blocked");
      });
    } catch(ex) {
      reject(ex);
    }
  });
  return promise;
}

function getDecodedUrlParam(sParam) {
  var value = getUrlParam(sParam);
  if (value != null) {
    return decodeURIComponent(value);
  } else {
    return null;
  }
}

function getUrlParam(sParam) {
  var sPageURL = window.location.search.substring(1);
  var sURLVariables = sPageURL.split('&');
  for (var i = 0; i < sURLVariables.length; i++) {
    var sParameterName = sURLVariables[i].split('=');
    if (sParameterName[0] == sParam) {
      return sParameterName[1];
    }
  }
}

function getUrlParamOrCookie(key, createCookie) {
  var value = getUrlParam(key);
  if (value != null) {
    if (createCookie) {
      setCookie(key, value, 7);
    }
    return value;
  }
  
  value = getCookie(key);
  if (value != "") {
    return value;
  }
  
  return null;
}

function getHostNameFromUrl(url) {
  var l = document.createElement("a");
  l.href = url;
  return l.hostname;
}

function getString(key, value) {
  var string  = res[key];
  if (string == null) {
    string = "Resource Error";
  }
  string = string.replace("[VALUE]", value);
  return string;
}

function addClassName(div, newClass) {
  if (!div.className) {
    div.className = newClass;
    return;
  }
  if (div.className.indexOf(newClass) > -1) {
    return;
  } else {
    div.className += " " + newClass;
  }
}

function removeClassName(div, newClass) {
  if (!div.className) {
    return;
  }
  if (div.className.indexOf(newClass) > -1) {
    div.className = div.className.replace(newClass, "").trim();
  }
}

function setCookie(cname, cvalue, exdays) {
  var d = new Date();
  d.setTime(d.getTime() + (exdays*24*60*60*1000));
  var expires = "expires=" + d.toUTCString();
  var cookieString = cname + "=" + cvalue + "; " + expires + "; path=/";
  document.cookie = cookieString;
  console.log("Created cookie: " + cookieString);
}

function getCookie(cname) {
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i=0; i<ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') c = c.substring(1);
    if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
  }
  return "";
}

function getDistanceBetween(lat1, lon1, lat2, lon2) {
  var p = 0.017453292519943295; // Math.PI / 180
  var c = Math.cos;
  var a = 0.5 - c((lat2 - lat1) * p)/2 + 
          c(lat1 * p) * c(lat2 * p) * 
          (1 - c((lon2 - lon1) * p))/2;
  return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
}