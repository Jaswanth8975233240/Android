var existingBusiness; // holds the existing business, as returned by the API
var newBusiness; // holds the new business, created by the local changes

window.onload = function(event) {
  $("#saveBusinessButton").click(saveNewBusiness);
  updateFormWithUrlParameterData();
};

var onUserReady = function(user) {
  var businessKeyId = getUrlParam("businessKeyId");
  requestExistingBusinessData(businessKeyId);

  // update the UI
  if (businessKeyId == null) {
    showAddBusinessUi();
  } else {
    showEditBusinessUi();
  }
}

// fetches an existing business from the API, if the required
// url param is set. Then updates the form with the business data
function requestExistingBusinessData(businessKeyId) {
  if (businessKeyId == null) {
    console.log("No business key ID specified");
    $(".loadingState").hide();
    return;
  }

  intelliqApi.getBusiness(businessKeyId).send().then(function(data){
    var businesses = intelliqApi.getBusinessesFromResponse(data);
    existingBusiness = businesses[0];
    console.log(existingBusiness);
    $(".loadingState").hide();
    updateFormWithBusinessData(existingBusiness);
  }).catch(function(error){
    console.log(error);
    $(".loadingState").hide();
    showErrorMessage(error);
  });
}

function showAddBusinessUi() {
  $("#businessHeading").text(getString("addBusiness"));
  $("#saveBusinessButton").text(getString("save"));
}

function showEditBusinessUi() {
  $("#businessHeading").text(getString("editBusiness"));
  $("#saveBusinessButton").text(getString("applyChanges"));
}

// fills the form fields with data from URL params
function updateFormWithUrlParameterData() {
  $("#form-name").val(getDecodedUrlParam("name"));
  $("#form-mail").val(getDecodedUrlParam("mail"));
}

// fills the form fields with data from the passed business
function updateFormWithBusinessData(business) {
  if (business == null) {
    console.log("Can't update form, passed business is invalid");
    return;
  }

  console.log("Updating form with data from business: " + business.name);

  $("#form-key-id").val(business.key.id);
  $("#form-name").val(business.name);
  $("#form-mail").val(business.mail);
}

// creates a new business object by parsing the form data
function parseFormToBusiness() {
  var parsedBusiness = {};
  parsedBusiness.name = $("#form-name").val();
  parsedBusiness.mail = $("#form-mail").val();
  return parsedBusiness;
}

// applies the changes that have been made to the business
function saveNewBusiness() {
  console.log("Saving new business");
  newBusiness = parseFormToBusiness();
  var mergedBusiness = mergeBusinesses(existingBusiness, newBusiness);
  console.log(mergedBusiness);

  var promise;
  if (mergedBusiness.key != null) {
    promise = updateExistingBusiness(mergedBusiness);
  } else {
    promise = addNewBusiness(mergedBusiness);
  }

  promise.then(function(data) {
    console.log(data);
    window.location.href = intelliqApi.PAGE_LINK_MANAGE;
  }, function(error) {
    showErrorMessage(error);
  });
}

function updateExistingBusiness(business) {
  var googleIdToken = authenticator.getInstance().getUserIdToken()
  return intelliqApi
      .editBusiness(business.key.id, business.name, business.mail)
      .setGoogleIdToken(googleIdToken)
      .send();
}

function addNewBusiness(business) {
  var googleIdToken = authenticator.getInstance().getUserIdToken()
  return intelliqApi
      .addBusiness(business.name, business.mail)
      .addQueue(false)
      .setGoogleIdToken(googleIdToken)
      .send();
}

// merges two businesses by returning the existing business with
// values overwritten by the new business
function mergeBusinesses(existingBusiness, newBusiness) {
  if (existingBusiness == null) {
    return newBusiness;
  }
  for (var key in newBusiness) {
    if (newBusiness.hasOwnProperty(key)) {
      existingBusiness[key] = newBusiness[key];
    }
  }
  return existingBusiness;
}