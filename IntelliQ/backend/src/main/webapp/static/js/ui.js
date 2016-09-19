var ui = function(){

  function log(message) {
    console.log("UI: " + message);
  }

  var ui = {
  };

  ui.makeTooltipped = function(element) {
    element.addClass("tooltipped");
    element.attr("data-tooltip", "");
    element.attr("data-position", "left");

    element.withTooltipText = function(text) {
      element.attr("data-tooltip", text);
      return element;
    }

    element.withTooltipPosition = function(position) {
      element.attr("data-position", position);
      return element;
    }

    return element;
  }

  ui.generateIcon = function(id) {
    var icon = $("<i>", {
      "class": "material-icons"
    }).html(id);
    return icon;
  }

  ui.generateClickableIcon = function(id) {
    var icon = ui.generateIcon(id);
    var link = $("<a>");
    link.append(icon);
    return link;
  }

  ui.generateButton = function(text, url) {
    var button = $("<a>", {
      "href": url,
      "class": "btn waves-effect waves-light primary-color"
    }).text(text);

    button.withIcon = function(id) {
      var icon = ui.generateIcon(id);
      icon.addClass("right");
      button.prepend(icon);
      return button;
    }

    button.large = function() {
      button.removeClass("btn").addClass("btn-large");
      return button;
    }

    button.fillWidth = function(value) {
      if (value) {
        button.addClass("fill-width");
      } else {
        button.removeClass("fill-width")
      }
      return button;
    }

    return button;
  }

  ui.generateToastAction = function(text, url) {
    var button = $("<a>", {
      "href": url,
      "class": "btn-flat waves-effect waves-light accent-color-text"
    }).text(text);

    return button;
  }

  ui.generateCardWrapper = function(className) {
    if (className == null) {
      className = ui.generateColumnClassName(12, 6, 4);
    }
    var wrapper = $("<div>", { "class": className });
    return wrapper;
  }

  ui.generateColumnClassName = function(small, medium, large) {
    return "col s" + small + " m" + medium + " l" + large;
  }

  ui.generateAction = function(name, href) {
    var action = $("<a>", {
      "href": href
    }).text(name);
    return action;
  }

  ui.generateCard = function() {
    var card = $("<div>", { "class": "card hoverable" });

    card.withImage = function(image) {
      var imageContainer = $("<div>", { "class": "card-image invisible" })
      imageContainer.append(image);
      imageContainer.prependTo(card);

      card.withImageRatio = function(ratio) {

        card.resizeImageContainer = function() {
          var imageContainer = card.find(".card-image");
          var currentImageContainerWidth = imageContainer.width();
          var currentImageContainerHeight = imageContainer.height();

          var desiredHeight = Math.ceil(currentImageContainerWidth / ratio);
          if (currentImageContainerHeight != desiredHeight) {
            imageContainer.height(desiredHeight);
          }

          card.fillImage();
          return card;
        }

        card.fillImage = function() {
          function adjustImageDimensions(originalWidth, originalHeight) {
            var currentImageWidth = originalWidth;
            var currentImageHeight = originalHeight;

            var imageContainer = card.find(".card-image");
            var currentImageContainerWidth = imageContainer.width();
            var currentImageContainerHeight = imageContainer.height();

            var imageRatio = currentImageWidth / currentImageHeight;
            var imageContainerRatio = currentImageContainerWidth / currentImageContainerHeight;

            var marginTop = 0;
            var marginLeft = 0;

            if (imageRatio >= imageContainerRatio) {
              // fit height
              var newImageWidth = Math.ceil(currentImageContainerHeight * imageRatio);
              var newImageHeight = currentImageContainerHeight;
              marginLeft = 0 - (newImageWidth - currentImageContainerWidth) / 2;
            } else {
              // fit width
              var newImageWidth = currentImageContainerWidth;
              var newImageHeight = Math.ceil(currentImageContainerWidth / imageRatio);
              marginTop = 0 - (newImageHeight - currentImageContainerHeight) / 2;
            }

            marginTop -= 1;
            marginLeft -= 1;

            var imageElement = imageContainer.find("img");
            imageElement.width(newImageWidth + 2);
            imageElement.height(newImageHeight + 2);
            imageElement.css({
              "margin-left" : marginLeft + "px", 
              "margin-top" : marginTop + "px"
            });
            imageElement.attr("originalWidth", currentImageWidth);
            imageElement.attr("originalHeight", currentImageHeight);
            return card;
          }

          var imageContainer = card.find(".card-image");
          var imageElement = imageContainer.find("img");

          if (imageElement.attr("originalWidth") != null && imageElement.attr("originalHeight") != null) {
            adjustImageDimensions(imageElement.attr("originalWidth"), imageElement.attr("originalHeight"));
          } else {
            var image = new Image();
            image.onload = function(){
              adjustImageDimensions(image.width, image.height);
              animationUi.fadeIn(imageContainer);
            }
            image.src = imageElement.attr("src");
          }

          return card;
        }

        $(window).resize(card.resizeImageContainer);
        return card;
      }

      card.withImageOverlay = function(overlay, clickHandler) {
        // get the first image that hasn't any overlay
        var image = card.find(".card-image").last();
        card.find(".card-image").each(function(index) {
          if ($(this).find(".overlay-content").length == 0) {
            image = $(this);
            return false;
          }
        });

        var overlayContainer = $("<div>", { "class": "overlay-content" })
        overlay.click(clickHandler);

        overlayContainer.append(overlay);
        overlayContainer.appendTo(image);
        return card;
      }

      return card;
    }

    card.withContent = function(content) {
      if (card.find(".card-content").length > 0) {
        return card;
      }

      var contentContainer = $("<div>", { "class": "card-content" })
      contentContainer.append(content);

      if (card.find(".card-image").length > 0) {
        contentContainer.insertAfter(card.find(".card-image").last());
      } else if (card.find(".card-action").length > 0) {
        contentContainer.insertBefore(card.find(".card-action").first());
      } else {
        contentContainer.appendTo(card);
      }

      card.withTitle = function(text, includeIcon) {
        var title = $("<span>", {
          "class": "card-title activator grey-text text-darken-4 truncate"
        }).html(text);

        if (includeIcon) {
          var icon = $("<i>", {
            "class": "material-icons right"
          }).html("more_vert");
          icon.appendTo(title);
        }

        card.find(".card-content").prepend(title);
        return card;
      }

      return card;
    }

    card.withRevealableContent = function(content) {
      if (card.find(".card-reveal").length > 0) {
        return card;
      }

      var contentContainer = $("<div>", { "class": "card-reveal" })
      contentContainer.append(content);
      contentContainer.appendTo(card);

      card.withTitle = function(text) {
        var title = $("<span>", {
          "class": "card-title activator grey-text text-darken-4 truncate"
        }).html(text);

        var icon = $("<i>", {
          "class": "material-icons right"
        }).html("close");
        icon.appendTo(title);

        card.find(".card-reveal").prepend(title);
        return card;
      }

      return card;
    }

    card.withTiles = function(tiles, options) {
      if (card.find(".card-tiles").length > 0) {
        return card;
      }

      var defaultOptions = {
        minimumTilesPerRow: 1,
        maximumTilesPerRow: 2
      };

      if (options == null) {
        options = defaultOptions;
      }

      var addTileImageLoadedHandler = function(tileImage) {
        tileImage.load(function() {
          // get swatches
          var image = tileImage.get(0);
          var vibrant = new Vibrant(image);
          var swatches = vibrant.swatches()
          
          // adjust overlay color
          var overlayColor = swatches.DarkMuted.getHex();
          var overlayContainer = tileImage.parent().find(".overlay-container");
          overlayContainer.css("opacity", "0");

          var overlay = overlayContainer.find(".overlay");
          overlay.css("opacity", "0.75");
          overlay.css("background-color", overlayColor);
          
          // add event listeners
          overlayContainer.hover(function() {
            overlayContainer.css("opacity", "1");
          }, function() {
            overlayContainer.css("opacity", "0");
          });
        });
      }

      var tileWrappers = [];

      var smallGrid = 12 / options.minimumTilesPerRow;
      var mediumGrid = 12 / options.maximumTilesPerRow;

      var tilesCount = Math.floor(tiles.length / options.maximumTilesPerRow) * options.maximumTilesPerRow;
      for (var i = 0; i < tiles.length && i < tilesCount; i++) {
        var tileWrapper = $("<div>", { "class": "col s" + smallGrid + " m" + mediumGrid + " no-padding" });
        var tileContainer = $("<div>", { "class": "tile-container" });

        addTileImageLoadedHandler(tiles[i].find("img"));
        
        tileContainer.append(tiles[i]);
        tileWrapper.append(tileContainer);
        tileWrappers.push(tileWrapper);
      }

      var tilesContainer = $("<div>", { "class": "card-tiles row no-margin" })
      tilesContainer.append(tileWrappers);

      if (card.find(".card-action").length > 0) {
        tilesContainer.insertBefore(card.find(".card-action").first());
      } else {
        tilesContainer.appendTo(card);
      }

      return card;
    }

    card.withActions = function(actions) {
      if (card.find(".card-action").length > 0) {
        return card;
      }
      
      var actionsContainer = $("<div>", { "class": "card-action" })
      actionsContainer.append(actions)
      actionsContainer.appendTo(card);
      return card;
    }

    card.withLink = function(url) {
      card.css("cursor", "pointer");
      card.click(function() {
            redirect(url);
        });
    }
    
    card.renderIn = function(container) {
      container.html(card);
      window.setTimeout(function() {
        if (card.resizeImageContainer != null) {
          card.resizeImageContainer();
        }
      }, 1);
    }

    return card;
  }

  ui.generateCollection = function() {
    var collection = $("<ul>", { "class": "collection z-depth-1" });
    return collection;
  }

  ui.generateCollectionItem = function() {
    var collectionItem = $("<li>", { "class": "collection-item avatar" });

    collectionItem.withIcon = function(icon) {
      collectionItem.append(icon);
      return collectionItem;
    }

    collectionItem.withCircleIcon = function(icon) {
      icon.addClass("circle");
      collectionItem.append(icon);
      return collectionItem;
    }

    collectionItem.withTitle = function(titleText) {
      var title = $("<span>", { "class": "title truncate" });
      title.text(titleText);
      collectionItem.append(title);
      return collectionItem;
    }

    collectionItem.withStatus = function(statusText) {
      var status = $("<span>", { "class": "status" });
      status.text(statusText);
      collectionItem.append(status);
      return collectionItem;
    }

    collectionItem.withActionIcon = function(icon) {
      icon.addClass("secondary-content");
      icon.addClass("grey-text");
      collectionItem.append(icon);
      return collectionItem;
    }

    collectionItem.renderIn = function(container) {
      container.html(collectionItem);
    }
    
    return collectionItem;
  }

  ui.generateBusinessWithQueuesCard = function(business) {
    var card = ui.generateCard()

    var imageWidth = Math.min(500, $(window).width() / 2);
    for (var queueIndex = 0; queueIndex < business.queues.length; queueIndex++) {
      var queue = business.queues[queueIndex];

      var imageSrc = intelliqApi.getUrls().forImage(queue.photoImageKeyId).resizedTo(imageWidth);
      var image = $("<img>", {
        "src": imageSrc,
        "class": "animated medium-blur",
        "alt": business.name + " Cover"
      });

      card.withImage(image);


      var url = intelliqApi.getUrls().forQueue(queue).openInWebApp();
      var clickHandler = function() {
        navigateTo(url);
      }

      var overlay = $("<a>", {
        "href": url,
        "class": "truncate"
      }).text(queue.name);
      card.withImageOverlay(overlay, clickHandler);
    }

    card.withImageRatio(3/1);
    
    card.withContent().withTitle(business.name, false);
    return card;
  }

  ui.generateManageBusinessCard = function(business) {
    var card = ui.generateCard()

    var imageWidth = Math.min(500, $(window).width() / 2);
    var imageSrc = intelliqApi.getUrls().forImage(business.logoImageKeyId).resizedTo(imageWidth);
    var image = $("<img>", {
      "src": imageSrc,
      "class": "animated activator",
      "alt": business.name + " Cover"
    });

    card.withImage(image);
    card.withImageRatio(3/2);

    card.withContent().withTitle(business.name, false);

    var revealableContent = $("<p>").text(business.mail);
    card.withRevealableContent(revealableContent).withTitle(business.name);

    var manageUrl = intelliqApi.getUrls().forBusiness(business).manage();
    var manageAction = ui.generateAction(getString("manage"), manageUrl);

    var editUrl = intelliqApi.getUrls().forBusiness(business).edit();
    var editAction = ui.generateAction(getString("edit"), editUrl);
    card.withActions([manageAction, editAction]);

    return card;
  }

  ui.generateManageQueueCard = function(queue) {
    var card = ui.generateCard()

    var imageWidth = Math.min(500, $(window).width() / 2);
    var imageSrc = intelliqApi.getUrls().forImage(queue.photoImageKeyId).resizedTo(imageWidth);
    var image = $("<img>", {
      "src": imageSrc,
      "class": "animated activator",
      "alt": queue.name + " Cover"
    });

    card.withImage(image);
    card.withImageRatio(3/2);

    card.withContent().withTitle(queue.name, false);

    var revealableContent = $("<p>").text(queue.description);
    card.withRevealableContent(revealableContent).withTitle(queue.name);

    var manageUrl = intelliqApi.getUrls().forQueue(queue).manage();
    var manageAction = ui.generateAction(getString("manage"), manageUrl);

    var editUrl = intelliqApi.getUrls().forQueue(queue).edit();
    var editAction = ui.generateAction(getString("edit"), editUrl);
    card.withActions([manageAction, editAction]);

    return card;
  }

  ui.generateQueueCard = function(queue) {
    var card = ui.generateCard()

    var imageWidth = Math.min(500, $(window).width() / 2);
    var imageSrc = intelliqApi.getUrls().forImage(queue.photoImageKeyId).resizedTo(imageWidth);
    var image = $("<img>", {
      "src": imageSrc,
      "class": "animated activator",
      "alt": queue.name + " Cover"
    });

    card.withImage(image);
    card.withImageRatio(3/2);

    card.withContent().withTitle(queue.name, false);

    var revealableContent = $("<p>").text(queue.description);
    card.withRevealableContent(revealableContent).withTitle(queue.name);

    return card;
  }

  ui.generateQueueDescriptionCard = function(queue) {
    var card = ui.generateCard()

    var title = getString("description");
    var content = $("<p>").text(queue.description);

    card.withContent(content).withTitle(title, false);
    return card;
  }

  ui.generateQueueItemCard = function(queueItem) {
    var card = ui.generateCard()

    var title = getString("ticket") + " #" + queueItem.ticketNumber;
    var status = ui.getStatusNameByQueueItemStatus(queueItem.status);

    var table = $("<table>");
    var tableBody = $("<tbody>");

    var tr;
    tr = $("<tr>");
    tr.append($("<td>").text(getString("number")));
    tr.append($("<td>").text(queueItem.ticketNumber));
    tr.appendTo(tableBody);

    tr = $("<tr>");
    tr.append($("<td>").text(getString("status")));
    tr.append($("<td>").text(status));
    tr.appendTo(tableBody);
    
    tr = $("<tr>");
    tr.append($("<td>").text(getString("name")));
    tr.append($("<td>").text(queueItem.name));
    tr.appendTo(tableBody);

    tr = $("<tr>");
    tr.append($("<td>").text(getString("joined")));
    tr.append($("<td>").text(queueItem.entryTimestamp));
    tr.appendTo(tableBody);

    tableBody.appendTo(table);

    card.withContent(table)
    //card.withTitle(title, false);

    var openUrl = intelliqApi.getUrls().forQueueItem(queueItem).openInWebApp();
    card.click(function() {
      navigateTo(openUrl);
    })

    /*
    var openAction = ui.generateAction(getString("show"), openUrl);
    card.withActions([openAction]);
    */
    return card;
  }

  ui.generateQueueItemCollectionItem = function(queueItem) {
    var collectionItem = ui.generateCollectionItem();
    collectionItem.css("cursor", "pointer");
    collectionItem.click(function() { showQueueItemDetailsModal(queueItem); })

    // circle icon
    var iconId = ui.getIconIdByQueueItemStatus(queueItem.status, queueItem.usingApp);
    var icon = ui.generateIcon(iconId);
    collectionItem.withCircleIcon(icon);

    // title
    collectionItem.withTitle(queueItem.name);

    // status
    var status = ui.getStatusNameByQueueItemStatus(queueItem.status);
    collectionItem.withStatus(status);

    // action
    var actionIcon;
    var tooltip = getString("setStatusTo") + " ";
    if (queueItem.status == intelliqApi.STATUS_WAITING) {
      actionIcon = ui.generateClickableIcon("clear");
      actionIcon.click(function(e) {
        cancelQueueItem(queueItem);
        e.stopPropagation();
      });
      tooltip += getString("statusCanceled").toLowerCase();
    } else if (queueItem.status == intelliqApi.STATUS_CALLED) {
      actionIcon = ui.generateClickableIcon("done");
      actionIcon.click(function(e) {
        markQueueItemAsDone(queueItem);
        e.stopPropagation();
      });
      tooltip += getString("statusDone").toLowerCase();
    } else {
      actionIcon = ui.generateClickableIcon("delete");
      actionIcon.click(function(e) {
        deleteQueueItem(queueItem);
        e.stopPropagation();
      });
      tooltip += getString("statusDeleted").toLowerCase();
    }

    // add tooltip to action icon
    ui.makeTooltipped(actionIcon)
        .withTooltipPosition("left")
        .withTooltipText(tooltip);

    collectionItem.withActionIcon(actionIcon);

    return collectionItem;
  }

  ui.getIconIdByQueueItemStatus = function(queueItemStatus, usingApp) {
    var iconId = "receipt";
    if (queueItemStatus == intelliqApi.STATUS_WAITING) {
      if (usingApp) {
        iconId = "smartphone";
      } else {
        iconId = "dvr";
      }
    } else if (queueItemStatus == intelliqApi.STATUS_CALLED) {
      iconId = "announcement";
    } else if (queueItemStatus == intelliqApi.STATUS_CANCELED) {
      iconId = "clear";
    } else if (queueItemStatus == intelliqApi.STATUS_DONE) {
      iconId = "done";
    }
    return iconId;
  }

  ui.getStatusNameByQueueItemStatus = function(queueItemStatus) {
    if (queueItemStatus == intelliqApi.STATUS_WAITING) {
      return getString("statusWaiting");
    } else if (queueItemStatus == intelliqApi.STATUS_CALLED) {
      return getString("statusCalled");
    } else if (queueItemStatus == intelliqApi.STATUS_CANCELED) {
      return getString("statusCanceled");
    } else if (queueItemStatus == intelliqApi.STATUS_DONE) {
      return getString("statusDone");
    }
    return getString("unknown");
  }

  ui.renderEntries = function(entries, container, options) {
    if (container == null || container.length < 1) {
      return;
    }
    container.empty();
    var wrapper = container;

    if (options.wrapperGenerator != null) {
      wrapper = options.wrapperGenerator();
    }

    for (var i = 0; i < entries.length; i++) {
      try {
        // create a item
        var item = options.itemGenerator(entries[i]);

        if (options.itemWrapperGenerator != null) {
          // create a div that wraps the item
          var itemWrapper = options.itemWrapperGenerator();

          // render the item in the item wrapper
          item.renderIn(itemWrapper);

          // add the item wrapper to the wrapper
          wrapper.append(itemWrapper);
        } else {
          // add the item to the wrapper
          wrapper.append(item);
        }
      } catch (ex) {
        console.log("Unable to render entry:");
        console.log(ex);
      }
    }

    if (options.wrapperGenerator != null) {
      container.append(wrapper);
    } else {
      container = wrapper;
    }

    if (entries.length < 1) {
      container.hide();
      container.parent().find(".emptyState").removeClass("hide");
    } else {
      container.show();
      container.parent().find(".emptyState").addClass("hide");
    }

    // re-initialize tooltips
    $(".material-tooltip").remove();
    $(".tooltipped").tooltip({ delay: 250 });
  }

  /*
    Location
  */
  ui.getDistance = function() {
    var distance = {};

    var isValidLocation = function(lat, lon) {
      if (lat != 0 && lat != -1 && lon != 0 && lon != -1) {
        return true;
      } else {
        return false;
      }
    }

    var calculateDistance = function(lat1, lon1, lat2, lon2) {
      var p = 0.017453292519943295; // Math.PI / 180
      var c = Math.cos;
      var a = 0.5 - c((lat2 - lat1) * p)/2 + 
              c(lat1 * p) * c(lat2 * p) * 
              (1 - c((lon2 - lon1) * p))/2;
      return 12742 * Math.asin(Math.sqrt(a)) * 1000; // 2 * R; R = 6371 km
    }

    distance.from = function(lat, lon) {
      distance.fromLat = lat;
      distance.fromLon = lon;
      return distance;
    }

    distance.to = function(lat, lon) {
      distance.toLat = lat;
      distance.toLon = lon;

      var validFrom = isValidLocation(distance.fromLat, distance.fromLon);
      var validTo = isValidLocation(distance.toLat, distance.toLon);
      if (!validFrom || !validTo) {
        distance.value = -1;
      } else {
        distance.value = calculateDistance(distance.fromLat, distance.fromLon, distance.toLat, distance.toLon);
      }
      return distance;
    }

    distance.inMeters = function() {
      return distance.value;
    }

    return distance;
  }

  /*
    Modals
  */
  ui.showSignInForm = function() {
    $("#modal-signin").openModal();
  }

  ui.hideSignInForm = function() {
    $("#modal-signin").closeModal();
  }

  ui.showSignOutForm = function() {
    $("#modal-signout").openModal();
  }

  ui.hideSignOutForm = function() {
    $("#modal-signout").closeModal();
  }

  ui.showErrorMessage = function(message) {
    $("#modal-error-message").text(message);
    $("#modal-error").openModal();
  }

  ui.hideErrorMessage = function(message) {
    $("#modal-error").closeModal();
  }

  return ui;
}();