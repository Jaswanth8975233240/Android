package com.intelliq.appengine.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;
import com.intelliq.appengine.datastore.entries.UserEntry;

public class QueueItemHelper {

    private static final Logger log = Logger.getLogger(QueueItemHelper.class.getName());

    public static Key saveEntry(QueueItemEntry entry) throws Exception {
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

    public static QueueItemEntry getEntryByKeyId(String idString) {
        long id = Long.parseLong(idString);
        return getEntryByKeyId(id);
    }

    public static QueueItemEntry getEntryByKeyId(long id) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Key key = KeyFactory.createKey(QueueItemEntry.class.getSimpleName(), id);
        QueueItemEntry entry = pm.getObjectById(QueueItemEntry.class, key);
        pm.close();
        return entry;
    }

    public static void deleteEntryByKeyId(long id) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Key key = KeyFactory.createKey(QueueItemEntry.class.getSimpleName(), id);
        QueueItemEntry entry = pm.getObjectById(QueueItemEntry.class, key);
        pm.deletePersistent(entry);
        pm.close();
    }

    public static List<QueueItemEntry> getQueueItemsByUserKeyId(long userKeyId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(QueueItemEntry.class);

        query.setFilter("userKeyId == value");
        query.declareParameters("long value");
        query.setRange(0, 25);

        ArrayList<QueueItemEntry> results = new ArrayList<QueueItemEntry>();
        try {
            List<QueueItemEntry> queryResults = (List<QueueItemEntry>) query.execute(userKeyId);
            for (QueueItemEntry entry : queryResults) {
                results.add(entry);
            }
            //results = pm.detachCopy(queryResults);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }

        return results;
    }

    public static QueueItemEntry getQueueItemByUserKeyId(long userKeyId, long queueKeyId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(QueueItemEntry.class);

        query.setFilter("userKeyId == value && queueKeyId == value2");
        query.declareParameters("long value, long value2");
        query.setRange(0, 25);

        ArrayList<QueueItemEntry> results = new ArrayList<QueueItemEntry>();
        try {
            List<QueueItemEntry> queryResults = (List<QueueItemEntry>) query.execute(userKeyId, queueKeyId);
            for (QueueItemEntry entry : queryResults) {
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

}
