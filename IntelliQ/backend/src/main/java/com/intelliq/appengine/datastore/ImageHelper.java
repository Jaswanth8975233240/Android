package com.intelliq.appengine.datastore;

import java.net.URL;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.intelliq.appengine.datastore.entries.ImageEntry;

public class ImageHelper {

	private static final Logger log = Logger.getLogger(ImageHelper.class.getName());

	public static Key saveEntry(ImageEntry entry) throws Exception {
		if (entry == null) {
			throw new Exception("EntryItem is null");
		}

		Key entryKey = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			entryKey = pm.makePersistent(entry).getKey();
		} finally {
			pm.close();
		}
		return entryKey;
	}

	public static ImageEntry fetchImageFromUrl(String url) throws Exception {
		log.info("Fetching image from " + url);

		URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
		HTTPResponse fetchResponse = fetchService.fetch(new URL(url));

		String fetchResponseContentType = null;
		for (HTTPHeader header : fetchResponse.getHeaders()) {
			if (header.getName().equalsIgnoreCase("content-type")) {
				fetchResponseContentType = header.getValue();
				break;
			}
		}

		if (fetchResponseContentType != null) {
			ImageEntry image = new ImageEntry();
			image.setImageType(fetchResponseContentType);
			image.setImage(fetchResponse.getContent());
			return image;
		}

		return null;
	}

	public static byte[] resizeImage(byte[] originalImage, String sizeString) {
		if (sizeString.equals(ImageEntry.SIZE_ORIGINAL)) {
			return originalImage;
		} else {
			try {
				int newWidth = Integer.parseInt(sizeString);
				return resizeImage(originalImage, newWidth, false);
			} catch (Exception e) {
				return originalImage;
			}
		}
	}

	public static byte[] resizeImage(byte[] originalImage, int newWidth) {
		return resizeImage(originalImage, newWidth, false);
	}

	public static byte[] resizeImage(byte[] originalImage, int newWidth, boolean scaleUp) {
		try {
			ImagesService imagesService = ImagesServiceFactory.getImagesService();

			Image oldImage = ImagesServiceFactory.makeImage(originalImage);

			// avoid scaling up images
			if (newWidth > oldImage.getWidth() && !scaleUp) {
				return originalImage;
			}

			// get new height to keep aspect ratio
			int newHeight = Math.round((newWidth * oldImage.getHeight()) / oldImage.getWidth());

			Transform resize = ImagesServiceFactory.makeResize(newWidth, newHeight);
			Image newImage = imagesService.applyTransform(resize, oldImage);
			return newImage.getImageData();
		} catch (Exception e) {
			e.printStackTrace();
			return originalImage;
		}
	}
}
