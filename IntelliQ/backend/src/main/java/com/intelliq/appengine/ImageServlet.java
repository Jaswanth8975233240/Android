package com.intelliq.appengine;

import com.google.appengine.api.datastore.Key;
import com.google.common.io.ByteStreams;
import com.intelliq.appengine.api.ApiResponse;
import com.intelliq.appengine.datastore.BusinessHelper;
import com.intelliq.appengine.datastore.ImageHelper;
import com.intelliq.appengine.datastore.QueueHelper;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.ImageEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.queries.ImageQuery;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ImageServlet.class.getName());

    // larger images will be rejected
    private static final long MAXIMUM_UPLOAD_IMAGE_SIZE = 1024 * 1024 * 5;

    // larger images will be resized
    private static final long MAXIMUM_PERSISTED_IMAGE_SIZE = 1024 * 1024; // 1 mb is the Data Store entity limit
    private static final int MAXIMUM_PERSISTED_IMAGE_WIDTH = 2000;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            // image request pattern: .../image/[imageKeyId]/[size].jpg

            String imageSizeString = ParserHelper.getStringAfter("/", req.getPathInfo(), ParserHelper.MODE_LAST_LAST);
            if (imageSizeString.contains(".png")) {
                imageSizeString = ParserHelper.getStringBefore(".png", imageSizeString);
            } else if (imageSizeString.contains(".jpg")) {
                imageSizeString = ParserHelper.getStringBefore(".jpg", imageSizeString);
            } else {
                throw new Exception("Unknown file type requested");
            }

            String imageKeyIdParam = ParserHelper.getStringBefore("/" + imageSizeString, req.getPathInfo(), ParserHelper.MODE_LAST_LAST);
            imageKeyIdParam = ParserHelper.getStringAfter("/", imageKeyIdParam, ParserHelper.MODE_LAST_LAST);
            long imageKeyId = Long.parseLong(imageKeyIdParam);

            if (imageKeyId < 0) {
                throw new Exception("Image key ID is invalid");
            }

            ImageQuery imageQuery = new ImageQuery();
            ImageEntry image = imageQuery.getImageByKeyId(imageKeyId);

            if (image.getImageType() == null) {
                throw new Exception("Image type unavailable");
            }

            if (image.getImage() == null) {
                throw new Exception("Image data unavailable");
            }

            resp.setContentType(image.getImageType());
            resp.getOutputStream().write(ImageHelper.resizeImage(image.getImage(), imageSizeString));
        } catch (Exception e) {
            log.warning("Unable to get image: " + e.getMessage());
            resp.sendRedirect("/static/images/not_found.jpg");
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ApiResponse responseObject = new ApiResponse();
        String response;

        try {
            ServletFileUpload upload = new ServletFileUpload();
            res.setContentType("text/plain");

            FileItemIterator iterator = upload.getItemIterator(req);

            ImageEntry image = new ImageEntry();

            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream stream = item.openStream();

                if (item.isFormField()) {
                    String key = item.getFieldName();
                    String value = Streams.asString(stream);
                    if (key.equals("parentKeyId")) {
                        image.setParentKeyId(Long.parseLong(value));
                    } else if (key.equals("type")) {
                        image.setType(Byte.parseByte(value));
                    } else if (key.equals("image")) {
                        String contentType = item.getContentType();
                        image.setImageType(contentType);
                        image.setImage(ByteStreams.toByteArray(stream));
                    }
                } else {
                    String contentType = item.getContentType();
                    image.setImageType(contentType);
                    image.setImage(ByteStreams.toByteArray(stream));
                }
            }

            // verify that parentKeyId is set
            if (image.getParentKeyId() == 0) {
                responseObject.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                throw new Exception("Invalid parentKeyId specified");
            }

            // verify image type
            if (image.getImageType() == null) {
                responseObject.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                throw new Exception("No image type available");
            }

            // verify image data
            if (image.getImage() == null) {
                responseObject.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                throw new Exception("No image data available");
            }

            // verify file size
            if (image.getImage().length > MAXIMUM_UPLOAD_IMAGE_SIZE) {
                String readableImageSize = Math.round(image.getImage().length / 1024) + " kb";
                String readableMaximumSize = Math.round(MAXIMUM_UPLOAD_IMAGE_SIZE / 1024) + " kb";
                responseObject.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
                throw new Exception("Image exceeds the maximum file size. File size limit is "
                        + readableMaximumSize + ", uploaded image size is " + readableImageSize);
            }

            // resize image
            try {
                image.resizeTo(MAXIMUM_PERSISTED_IMAGE_WIDTH, MAXIMUM_PERSISTED_IMAGE_SIZE);
            } catch (Exception e) {
                log.warning("Unable to resize image: " + e.getMessage());
            }

            // save image entry
            Key imageKey = ImageHelper.saveEntry(image);
            image.setKey(imageKey);

            // add the new image key id to the parent business or queue
            long oldImageKeyId;
            if (image.getType() == ImageEntry.TYPE_LOGO) {
                BusinessEntry businessEntry = BusinessHelper.getEntryByKeyId(image.getParentKeyId());
                if (businessEntry != null) {
                    oldImageKeyId = businessEntry.getLogoImageKeyId();
                    businessEntry.setLogoImageKeyId(image.getKey().getId());
                } else {
                    throw new Exception("Can't find parent business with Id: " + image.getParentKeyId());
                }
                BusinessHelper.saveEntry(businessEntry);
            } else {
                QueueEntry queueEntry = QueueHelper.getEntryByKeyId(image.getParentKeyId());
                if (queueEntry != null) {
                    oldImageKeyId = queueEntry.getPhotoImageKeyId();
                    queueEntry.setPhotoImageKeyId(image.getKey().getId());
                } else {
                    throw new Exception("Can't find parent queue with Id: " + image.getParentKeyId());
                }
                QueueHelper.saveEntry(queueEntry);
            }

            // delete the old image, if set
            if (oldImageKeyId > 0) {
                try {
                    ImageHelper.deleteImageByKeyId(oldImageKeyId);
                } catch (Exception e) {
                    log.warning("Unable to delete old image: " + e.getMessage());
                }
            }

            // remove the image data to use the same object as response
            image.setImage(null);

            // TODO: log image update to slack, re-verify business / queue

            responseObject.setContent(image);
            response = responseObject.toJSON();
        } catch (Exception e) {
            responseObject.setException(e);
            response = responseObject.toJSON();
            e.printStackTrace();
        }

        res.setContentType("application/json");
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.getWriter().write(response);
        res.getWriter().flush();
        res.getWriter().close();
    }

}
