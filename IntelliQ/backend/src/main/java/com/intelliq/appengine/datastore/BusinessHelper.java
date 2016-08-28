package com.intelliq.appengine.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.PermissionEntry;

public class BusinessHelper {

	private static final Logger log = Logger.getLogger(BusinessHelper.class.getName());

	public static Key saveEntry(BusinessEntry entry) throws Exception {
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
	
	public static List<BusinessEntry> getBusinessesByUserKeyId(long userKeyId, int minimumPermission) {
		List<Long> businessKeyIds = getBusinessKeyIdsByUserKeyId(userKeyId, minimumPermission);
		List<BusinessEntry> results = new ArrayList<BusinessEntry>();
		for (Long businessKeyId : businessKeyIds) {
			try {
				BusinessEntry businessEntry = getEntryByKeyId(businessKeyId);
				if (businessEntry == null) {
					throw new Exception("Business is null");
				}
				results.add(businessEntry);
			} catch (Exception ex) {
				log.warning("Unable to get business with key ID: " + businessKeyId + ": " + ex.getMessage());
			}
		}
		return results;
	}
	
	public static List<Long> getBusinessKeyIdsByUserKeyId(long userKeyId, int minimumPermission) {
		List<PermissionEntry> permissions = PermissionHelper.getPermissions(userKeyId, BusinessEntry.class.getSimpleName());
		List<Long> businessKeyIds = new ArrayList<>();
		for (PermissionEntry permission : permissions) {
			if (permission.getPermission() >= minimumPermission) {
				businessKeyIds.add(permission.getSubjectKeyId());
			}
		}
		return businessKeyIds;
	}
	
	public static BusinessEntry getEntryByKeyId(String idString) {
		long id = Long.parseLong(idString);
		return getEntryByKeyId(id);
	}
	
	public static BusinessEntry getEntryByKeyId(long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(BusinessEntry.class.getSimpleName(), id);
		BusinessEntry entry = pm.getObjectById(BusinessEntry.class, key);
		pm.close();
		return entry;
	}
	
}
