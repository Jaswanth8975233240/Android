package com.intelliq.appengine.datastore.entries;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class ImageEntry {

	public static final int TYPE_LOGO = 0;
	public static final int TYPE_PHOTO = 1;

	public static final String SIZE_ORIGINAL = "original";

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Key key;

	@Persistent
	long parentKeyId;

	@Persistent
	int type = TYPE_LOGO;
	
	@Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
    private String imageType;
	
	@Persistent
	private Blob image;

	public ImageEntry() {

	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key generateKey() {
		String keyName = String.valueOf(parentKeyId);
		if (type == TYPE_LOGO) {
			keyName += "_logo";
		} else if (type == TYPE_PHOTO) {
			keyName += "_photo";
		}
		return KeyFactory.createKey(ImageEntry.class.getSimpleName(), keyName);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getParentKeyId() {
		return parentKeyId;
	}

	public void setParentKeyId(long parentKeyId) {
		this.parentKeyId = parentKeyId;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public byte[] getImage() {
		if (image == null) {
			return null;
		}
		return image.getBytes();
	}

	public void setImage(byte[] bytes) {
		this.image = new Blob(bytes);
	}

}
