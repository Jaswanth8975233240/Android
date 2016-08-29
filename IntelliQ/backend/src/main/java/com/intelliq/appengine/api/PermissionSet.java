package com.intelliq.appengine.api;

import java.util.ArrayList;
import java.util.List;

import com.intelliq.appengine.datastore.entries.PermissionEntry;

public class PermissionSet {

	public static final byte REQUIRE_ANY = 0;
	public static final byte REQUIRE_ALL = 1;
	
	private List<PermissionEntry> permissions;
	private byte mode;
	
	public PermissionSet() {
		super();
		this.permissions = new ArrayList<PermissionEntry>();
		this.mode = REQUIRE_ANY;
	}
	
	public PermissionSet(List<PermissionEntry> permissions, byte mode) {
		super();
		this.permissions = permissions;
		this.mode = mode;
	}
	
	public List<PermissionEntry> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionEntry> permissions) {
		this.permissions = permissions;
	}

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}
}
