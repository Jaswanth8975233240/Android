package com.intelliq.appengine.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.PermissionEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;

public class PermissionHelper {

    private static final Logger log = Logger.getLogger(PermissionHelper.class.getSimpleName());

    public static Key saveEntry(PermissionEntry entry) throws Exception {
        if (entry == null) {
            throw new Exception("PermissionEntry is null");
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

    public static boolean hasPermission(PermissionEntry permissionEntry) {
        return hasPermission(permissionEntry.getUserKeyId(), permissionEntry.getSubjectKeyId(), permissionEntry.getPermission());
    }

    public static boolean hasPermission(long userKeyId, long subjectKeyId, int permission) {
        // check if this permission exists
        if (getPermission(userKeyId, subjectKeyId, permission) != null) {
            return true;
        }
        return false;
    }

    public static PermissionEntry getPermission(long userKeyId, long subjectKeyId, int permission) {
        try {
            List<PermissionEntry> permissionEntries = getPermissions(userKeyId, subjectKeyId);
            for (PermissionEntry permissionEntry : permissionEntries) {
                if (permissionEntry.matches(userKeyId, subjectKeyId, permission)) {
                    return permissionEntry;
                }
            }
            return null;
        } catch (Exception e) {
            log.warning("Unable to get permission");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean grantPermission(UserEntry user, BusinessEntry businessEntry, int permission) {
        try {
            return grantPermission(user.getKey().getId(), businessEntry.getKey().getId(), BusinessEntry.class.getSimpleName(), permission);
        } catch (Exception ex) {
            log.warning("Unable to grant permission: " + ex.getMessage());
            return false;
        }
    }

    public static boolean grantPermission(UserEntry user, QueueEntry queueEntry, int permission) {
        try {
            return grantPermission(user.getKey().getId(), queueEntry.getKey().getId(), QueueEntry.class.getSimpleName(), permission);
        } catch (Exception ex) {
            log.warning("Unable to grant permission: " + ex.getMessage());
            return false;
        }
    }

    public static boolean grantPermission(UserEntry user, QueueItemEntry queueItemEntry, int permission) {
        try {
            return grantPermission(user.getKey().getId(), queueItemEntry.getKey().getId(), QueueItemEntry.class.getSimpleName(), permission);
        } catch (Exception ex) {
            log.warning("Unable to grant permission: " + ex.getMessage());
            return false;
        }
    }

    public static boolean grantPermission(long userKeyId, long subjectKeyId, String subjectKind, int permission) {
        try {
            PermissionEntry entry = new PermissionEntry(userKeyId, subjectKeyId, subjectKind, permission);
            saveEntry(entry);
            log.info("New permission granted: " + userKeyId + " - " + subjectKeyId + " (" + subjectKind + "): " + permission);
            return true;
        } catch (Exception e) {
            log.warning("Unable to grant permission: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean revokePermission(long userKeyId, long subjectKeyId, int permission) {
        try {
            PermissionEntry permissionEntry = getPermission(userKeyId, subjectKeyId, permission);
            if (permissionEntry == null) {
                throw new Exception("Permission does not exist");
            }
            deleteEntryByKeyId(permissionEntry.getKey().getId());
            log.info("Permission revoked");
            return true;
        } catch (Exception e) {
            log.warning("Unable to revoke permission: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<PermissionEntry> getPermissions(long userKeyId, long subjectKeyId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(PermissionEntry.class);
        query.setFilter("userKeyId == value && subjectKeyId == value2");
        query.declareParameters("long value, long value2");
        query.setOrdering("permission descending");
        query.setRange(0, 100);

        List<PermissionEntry> results = new ArrayList<PermissionEntry>();
        try {
            results = (List<PermissionEntry>) query.execute(userKeyId, subjectKeyId);
            log.info("PermissionQuery execution returned " + results.size() + " item(s)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }
        return results;
    }

    public static List<PermissionEntry> getPermissions(long userKeyId, String subjectKind) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(PermissionEntry.class);
        query.setFilter("userKeyId == value && subjectKind == value2");
        query.declareParameters("long value, String value2");
        query.setOrdering("permission descending");
        query.setRange(0, 100);

        List<PermissionEntry> results = new ArrayList<PermissionEntry>();
        try {
            results = (List<PermissionEntry>) query.execute(userKeyId, subjectKind);
            log.info("PermissionQuery execution returned " + results.size() + " item(s)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }
        return results;
    }

    public static List<PermissionEntry> getPermissionsByUserKeyId(long userKeyId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(PermissionEntry.class);
        query.setFilter("userKeyId == '" + userKeyId + "'");
        query.setOrdering("permission descending");
        query.setRange(0, 1000);

        List<PermissionEntry> results = new ArrayList<PermissionEntry>();
        try {
            results = (List<PermissionEntry>) query.execute();
            log.info("PermissionQuery execution returned " + results.size() + " item(s)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }
        return results;
    }

    public static List<PermissionEntry> getPermissionsBySubjectKeyId(long subjectKeyId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(PermissionEntry.class);
        query.setFilter("subjectKeyId == '" + subjectKeyId + "'");
        query.setOrdering("permission descending");
        query.setRange(0, 1000);

        List<PermissionEntry> results = new ArrayList<PermissionEntry>();
        try {
            results = (List<PermissionEntry>) query.execute();
            log.info("PermissionQuery execution returned " + results.size() + " item(s)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }
        return results;
    }

    public static PermissionEntry getEntryByKeyId(String idString) {
        long id = Long.parseLong(idString);
        return getEntryByKeyId(id);
    }

    public static PermissionEntry getEntryByKeyId(long id) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Key key = KeyFactory.createKey(PermissionEntry.class.getSimpleName(), id);
        PermissionEntry entry = pm.getObjectById(PermissionEntry.class, key);
        pm.close();
        return entry;
    }

    public static void deleteEntryByKeyId(long id) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Key key = KeyFactory.createKey(PermissionEntry.class.getSimpleName(), id);
        PermissionEntry entry = pm.getObjectById(PermissionEntry.class, key);
        pm.deletePersistent(entry);
        pm.close();
    }
}
