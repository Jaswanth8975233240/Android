package com.intelliq.appengine.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;
import com.intelliq.appengine.datastore.entries.UserStatsEntry;

public class UserHelper {

    private static final Logger log = Logger.getLogger(UserHelper.class.getName());

    public static Key saveEntry(UserEntry entry) throws Exception {
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

    public static UserEntry getUserByGoogleUserId(String googleUserId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(UserEntry.class);

        query.setFilter("googleUserId == value");
        query.declareParameters("String value");
        query.setRange(0, 10);

        ArrayList<UserEntry> results = new ArrayList<UserEntry>();
        try {
            List<UserEntry> queryResults = (List<UserEntry>) query.execute(googleUserId);
            for (UserEntry entry : queryResults) {
                // touch child entities
                if (entry.getStats() != null) {
                    entry.getStats().getLastSignIn();
                } else {
                    entry.setStats(new UserStatsEntry());
                }
                results.add(entry);
            }
            //results = pm.detachCopy(queryResults);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public static UserEntry getUserByFacebookUserId(String googleUserId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(UserEntry.class);

        query.setFilter("facebookUserId == value");
        query.declareParameters("String value");
        query.setRange(0, 10);

        ArrayList<UserEntry> results = new ArrayList<UserEntry>();
        try {
            List<UserEntry> queryResults = (List<UserEntry>) query.execute(googleUserId);
            for (UserEntry entry : queryResults) {
                // touch child entities
                entry.getStats();
                results.add(entry);
            }
            //results = pm.detachCopy(queryResults);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }

        if (results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    public static UserEntry getEntryByKeyId(String idString) {
        long id = Long.parseLong(idString);
        return getEntryByKeyId(id);
    }

    public static UserEntry getEntryByKeyId(long id) throws NucleusObjectNotFoundException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Key key = KeyFactory.createKey(UserEntry.class.getSimpleName(), id);
        UserEntry entry = pm.getObjectById(UserEntry.class, key);
        // touch child entities
        entry.getStats();
        pm.close();
        return entry;
    }
}
