package com.steppschuh.intelliq.api.entry;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.steppschuh.intelliq.R;
import com.steppschuh.intelliq.api.DatastoreKey;
import com.steppschuh.intelliq.api.request.ApiRequestHelper;

public class ImageEntry {

    public static final int TYPE_LOGO = 0;
    public static final int TYPE_PHOTO = 1;

    public static final String SIZE_ORIGINAL = "original";

    private DatastoreKey key;
    private long parentKeyId;
    private int type;
    private String imageType;

    public ImageEntry(long keyId, int type) {
        key = new DatastoreKey();
        key.setId(keyId);
        key.setKind("ImageEntry");
       this.type = type;
    }

    public String getUrl() {
        return getUrl(key.getId(), imageType, SIZE_ORIGINAL);
    }

    public String getUrl(int width) {
        return getUrl(String.valueOf(width));
    }

    public String getUrl(String width) {
        return getUrl(key.getId(), imageType, width);
    }

    public static String getUrl(long keyId, String imageType, String size) {
        return ApiRequestHelper.HOST + ApiRequestHelper.ENDPOINT_IMAGE + String.valueOf(keyId) + "/" + size + getImageExtension(imageType, TYPE_PHOTO);
    }

    public void loadIntoImageView(ImageView imageView, Context context) {
        loadIntoImageView(imageView, null, context);
    }

    public void loadIntoImageView(ImageView imageView, Transformation transformation, Context context) {
        loadIntoImageView(imageView, transformation, context, null);
    }

    public void loadIntoImageView(ImageView imageView, Transformation transformation, Context context, Callback callback) {
        String imageWidth = SIZE_ORIGINAL;
        if (imageView != null) {
            int imageViewWidth = imageView.getWidth();
            if (imageViewWidth > 0) {
                imageWidth = String.valueOf(imageViewWidth);
            }
        }

        String url = getUrl(imageWidth);
        if (transformation != null) {
            Picasso.with(context)
                    .load(url)
                    .transform(transformation)
                    .placeholder(R.drawable.no_photo)
                    .error(R.drawable.no_photo)
                    .into(imageView, callback);
        } else {
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.no_photo)
                    .error(R.drawable.no_photo)
                    .into(imageView, callback);
        }
    }

    public String getImageExtension() {
        return getImageExtension(imageType, type);
    }

    public static String getImageExtension(String imageType, int type) {
        if (imageType != null) {
            if (imageType.contains("png")) {
                return ".png";
            } else {
                return ".jpg";
            }
        } else {
            if (type == TYPE_LOGO) {
                return ".png";
            } else {
                return ".jpg";
            }
        }
    }

    /**
     * Getter & Setter
     */
    public DatastoreKey getKey() {
        return key;
    }

    public void setKey(DatastoreKey key) {
        this.key = key;
    }

    public long getParentKeyId() {
        return parentKeyId;
    }

    public void setParentKeyId(long parentKeyId) {
        this.parentKeyId = parentKeyId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
