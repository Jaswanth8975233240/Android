package com.intelliq.appengine.datastore.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.datastore.PMF;
import com.intelliq.appengine.datastore.entries.ImageEntry;

public class ImageQuery {

    private static final Logger log = Logger.getLogger(ImageQuery.class.getName());

    PersistenceManager pm = PMF.get().getPersistenceManager();
    Query query = pm.newQuery(ImageEntry.class);

    public ImageQuery() {
        super();
    }

    public List<ImageEntry> execute(Query query, String value) {
        List<ImageEntry> results = new ArrayList<ImageEntry>();
        try {
            results = (List<ImageEntry>) query.execute(value);
            results.size();
            log.info("ImageEntry execution returned " + results.size() + " item(s)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query.closeAll();
            pm.close();
        }
        return results;
    }

    public ImageEntry getImageByKeyId(String idString) {
        long id = Long.parseLong(idString);
        return getImageByKeyId(id);
    }

    public ImageEntry getImageByKeyId(long id) {
        Key key = KeyFactory.createKey(ImageEntry.class.getSimpleName(), id);
        ImageEntry image = pm.getObjectById(ImageEntry.class, key);
        return image;
    }

    public Query getQueryWithUrlEquals() {
        query.setFilter("url == valueParam");
        query.declareParameters("String valueParam");
        query.setRange(0, 10);
        return query;
    }

}
