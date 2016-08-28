package com.intelliq.appengine.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.intelliq.appengine.datastore.entries.BusinessEntry;
import com.intelliq.appengine.datastore.entries.QueueEntry;
import com.intelliq.appengine.datastore.entries.QueueItemEntry;
import com.intelliq.appengine.stuff.FakeData;

public class QueueHelper {

	private static final Logger log = Logger.getLogger(QueueHelper.class.getName());

	public static Key saveEntry(QueueEntry entry) throws Exception {
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
	
	/**
	 * Gets a list of QueueEntries that are in range of the given location
	 */
	public static List<QueueEntry> getQueuesByLocation(float latitude, float longitude, long distance) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		float[] offsetLocation = QueueEntry.offsetLocationToRange(latitude, longitude, distance);
		
		float latitudeMin = offsetLocation[0];
		float longitudeMin = offsetLocation[1];
		float latitudeMax = offsetLocation[2];
		float longitudeMax = offsetLocation[3];
		
		//log.severe("Latitude: " + latitude + " Min: " + latitudeMin + " Max: " + latitudeMax);
		//log.severe("Longitude: " + longitude + " Min: " + longitudeMin + " Max: " + longitudeMax);
		
		// get queues that match the latitude range
		Query latitudeQuery = pm.newQuery(QueueEntry.class);
		latitudeQuery.setFilter("latitude < latitudeMax && latitude > latitudeMin");
		latitudeQuery.setOrdering("latitude ascending");
		latitudeQuery.declareParameters("float latitudeMin, float latitudeMax");
		
		List<QueueEntry> latitudeResults = new ArrayList<QueueEntry>();
		try {
			latitudeResults = (List<QueueEntry>) latitudeQuery.execute(latitudeMin, latitudeMax);
			latitudeResults.size();
			for (QueueEntry entry : latitudeResults) {
				log.info("Matching latitude items: " + entry.getName());
			}
			log.info("Query execution returned " + latitudeResults.size() + " item(s)");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			latitudeQuery.closeAll();
		}
		
		// get queues that match the longitude range
		Query longitudeQuery = pm.newQuery(QueueEntry.class);
		longitudeQuery.setFilter("longitude < longitudeMax && longitude > longitudeMin");
		longitudeQuery.setOrdering("longitude ascending");
		longitudeQuery.declareParameters("float longitudeMin, float longitudeMax");
		
		List<QueueEntry> longitudeResults = new ArrayList<QueueEntry>();
		try {
			longitudeResults = (List<QueueEntry>) longitudeQuery.execute(longitudeMin, longitudeMax);
			longitudeResults.size();
			for (QueueEntry entry : longitudeResults) {
				log.info("Matching longitude items: " + entry.getName());
			}
			log.info("Query execution returned " + longitudeResults.size() + " item(s)");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			longitudeQuery.closeAll();
			pm.close();
		}

		// merge latitude and longitude results into one list
		List<QueueEntry> results = mergeQueueEntries(latitudeResults, longitudeResults);
		return results;
	}
	
	/**
	 * This method will get the parent business for each queue
	 * and group queues from the same business inside them
	 */
	public static ArrayList<BusinessEntry> getBusinessesForQueues(List<QueueEntry> queueEntries) {
		ArrayList<BusinessEntry> results = new ArrayList<BusinessEntry>();
		
		// get all the business key IDs that are needed
		List<Long> businessKeyIds = new ArrayList<Long>();
		for (QueueEntry queueEntry : queueEntries) {
			if (!businessKeyIds.contains(queueEntry.getBusinessKeyId())) {
				businessKeyIds.add(queueEntry.getBusinessKeyId());
			}
		}
		
		// get all business entries for the needed key IDs
		for (long businessKeyId : businessKeyIds) {
			try {
				BusinessEntry businessEntry = BusinessHelper.getEntryByKeyId(businessKeyId);
				
				// add all queues owned by this business
				if (businessEntry != null) {
					businessEntry.setQueues(filterQueuesByBusinessKeyId(queueEntries, businessKeyId));
					results.add(businessEntry);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return results;
	}
	
	/**
	 * This method will get the parent business for the given queue
	 */
	public static BusinessEntry getBusinessForQueue(QueueEntry queueEntry) {
		BusinessEntry result = null;
		
		try {
			BusinessEntry businessEntry = BusinessHelper.getEntryByKeyId(queueEntry.getBusinessKeyId());

			// add the queue owned by this business
			if (businessEntry != null) {
				ArrayList<QueueEntry> queueEntries = new ArrayList<QueueEntry>();
				queueEntries.add(queueEntry);
				businessEntry.setQueues(queueEntries);
				result = businessEntry;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Returns only queues with a matching business key ID
	 * from a given list
	 */
	public static ArrayList<QueueEntry> filterQueuesByBusinessKeyId(List<QueueEntry> queueEntries, long businessKeyId) {
		ArrayList<QueueEntry> results = new ArrayList<QueueEntry>();
		for (QueueEntry queueEntry : queueEntries) {
			if (queueEntry.getBusinessKeyId() == businessKeyId) {
				results.add(queueEntry);
			}
		}
		return results;
	}
	
	public static ArrayList<QueueEntry> getQueuesByPostalCode(String postalCode) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueEntry.class);
		
		query.setFilter("postalCode == value");
		query.declareParameters("String value");
		query.setRange(0, 1000);
		
		ArrayList<QueueEntry> results = new ArrayList<QueueEntry>();
		try {
			List<QueueEntry> queryResults = (List<QueueEntry>) query.execute(postalCode);
			for (QueueEntry entry : queryResults) {
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
	
	public static int getLastTicketNumberInQueue(long queueKeyId) {
		return getLastTicketNumberInQueue(queueKeyId, (byte) QueueItemEntry.STATUS_ALL);
	}
	
	public static int getLastTicketNumberInQueue(long queueKeyId, byte status) {		
		QueueItemEntry lastQueueItemEntry = getLastAssignedTicketInQueue(queueKeyId, status);
		if (lastQueueItemEntry != null) {
			return lastQueueItemEntry.getTicketNumber();
		}
		return -1;
	}

	public static List<QueueItemEntry> getItemsInQueue(long queueKeyId, int startIndex, int count) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueItemEntry.class);

		query.setFilter("queueKeyId == value");
		query.setOrdering("entryTimestamp descending");
		query.declareParameters("long value");
		query.setRange(startIndex, count);
		
		List<QueueItemEntry> results = new ArrayList<QueueItemEntry>();
		try {
			results = (List<QueueItemEntry>) query.execute(queueKeyId);
			results.size();
			for (QueueItemEntry entry : results) {
				entry.getName();
				//log.info("Item found: " + entry.getName());
			}
			//log.info("Query execution returned " + results.size() + " item(s)");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			query.closeAll();
			pm.close();
		}

		return results;
	}
	
	public static ArrayList<QueueEntry> getQueuesByBusiness(long businessKeyId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueEntry.class);

		query.setFilter("businessKeyId == value");
		query.declareParameters("long value");
		query.setRange(0, 1000);
		
		ArrayList<QueueEntry> results = new ArrayList<QueueEntry>();
		try {
			List<QueueEntry> queryResults = (List<QueueEntry>) query.execute(businessKeyId);
			for (QueueEntry entry : queryResults) {
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
	
	public static void markAllQueueItemsAsDone(long queueKeyId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueItemEntry.class);

		query.setFilter("queueKeyId == value && status == value2");
		query.declareParameters("long value, long value2");
		query.setRange(0, 10000);
		
		try {
			List<QueueItemEntry> queryResults = (List<QueueItemEntry>) query.execute(queueKeyId, QueueItemEntry.STATUS_CALLED);
			for (QueueItemEntry entry : queryResults) {
				entry.setStatus(QueueItemEntry.STATUS_DONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			query.closeAll();
			pm.close();
		}
	}
	
	public static int getNumberOfItemsInQueue(long queueKeyId, byte status) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueItemEntry.class);

		query.setFilter("queueKeyId == value && status == value2");
		query.declareParameters("long value, byte value2");
		query.setRange(0, 10000);
		
		int count = 0;
		try {
			List<QueueItemEntry> queryResults = (List<QueueItemEntry>) query.execute(queueKeyId, status);
			count = queryResults.size();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			query.closeAll();
			pm.close();
		}

		return count;
	}

	public static QueueItemEntry getLastAssignedTicketInQueue(long queueKeyId, byte status) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueItemEntry.class);
		
		if (status > QueueItemEntry.STATUS_ALL) {
			query.setFilter("queueKeyId == value && status == value2");
			query.declareParameters("long value, byte value2");
		} else {
			query.setFilter("queueKeyId == value");
			query.declareParameters("long value");
		}
		query.setOrdering("entryTimestamp descending");
		query.setRange(0, 5);

		List<QueueItemEntry> results = new ArrayList<QueueItemEntry>();
		try {
			if (status > -1) {
				results = (List<QueueItemEntry>) query.execute(queueKeyId, status);
			} else {
				results = (List<QueueItemEntry>) query.execute(queueKeyId);
			}
			results.size();
			//log.info("Query execution returned " + results.size() + " item(s)");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			query.closeAll();
			pm.close();
		}

		// just in case some items have the same entry timestamp
		// return the item with the highest ticket number
		QueueItemEntry lastQueueItemEntry = null;
		int highestTicketNumber = -1;
		for (QueueItemEntry queueItemEntry : results) {
			if (queueItemEntry.getTicketNumber() > highestTicketNumber) {
				lastQueueItemEntry = queueItemEntry;
				highestTicketNumber = lastQueueItemEntry.getTicketNumber();
			}
		}

		return lastQueueItemEntry;
	}

	public static QueueItemEntry getHighestTicketNumberInQueue(long queueKeyId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueItemEntry.class);
		query.setFilter("queueKeyId == '" + queueKeyId + "'");
		query.setOrdering("ticketNumber descending");
		query.setRange(0, 1);

		List<QueueItemEntry> results = new ArrayList<QueueItemEntry>();
		try {
			results = (List<QueueItemEntry>) query.execute();
			//log.info("ActorQuery execution returned " + results.size() + " item(s)");
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

	public static void deleteItemsInQueue(long queueKeyId, byte status) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(QueueItemEntry.class);
		
		try {
			if (status != QueueItemEntry.STATUS_ALL) {
				query.setFilter("queueKeyId == value && status == value2");
				query.declareParameters("long value, byte value2");
				query.deletePersistentAll(queueKeyId, status);
			} else {
				query.setFilter("queueKeyId == value");
				query.declareParameters("long value");
				query.deletePersistentAll(queueKeyId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			query.closeAll();
			pm.close();
		}
	}
	
	public static void populateQueueWithSampleItems(long queueKeyId, int count) {
		ArrayList<QueueItemEntry> dummyItems = new ArrayList<QueueItemEntry>();
		
		for (int i = 0; i < count; i++) {
			QueueItemEntry item = new QueueItemEntry(queueKeyId);
			item.makeDummyItem();
			dummyItems.add(item);
		}
		
		Collections.sort(dummyItems, new QueueItemEntry.EntryTimestampComparator());
		
		String[] dummyNames = FakeData.getNames(count);
		
		int lastTicketNumber = -1;
		for (int i = 0; i < count; i++) {
			try {
				if (lastTicketNumber < 0) {
					lastTicketNumber = getLastTicketNumberInQueue(queueKeyId, QueueItemEntry.STATUS_ALL) + 1;
				}
				
				lastTicketNumber += 1;
				
				QueueItemEntry item = dummyItems.get(i);
				item.setTicketNumber(lastTicketNumber);
				item.setName(dummyNames[i]);
				QueueItemHelper.saveEntry(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns a list of QueueEntries that are presenst in both given lists
	 */
	public static List<QueueEntry> mergeQueueEntries(List<QueueEntry> list1, List<QueueEntry> list2) {
		List<QueueEntry> entries = new ArrayList<QueueEntry>();
		ArrayList<Long> keyIds = new ArrayList<Long>();
		
		//log.severe("Merging lists with " + list1.size() + " and " + list2.size() + " items");
		
		for (int i = 0; i < list1.size(); i++) {
			keyIds.add(list1.get(i).getKey().getId());
		}
		
		for (int i = 0; i < list2.size(); i++) {
			if (keyIds.contains(list2.get(i).getKey().getId())) {
				entries.add(list2.get(i));
			}
		}
		
		return entries;
	}
	
	public static QueueEntry getEntryByKeyId(String idString) {
		long id = Long.parseLong(idString);
		return getEntryByKeyId(id);
	}

	public static QueueEntry getEntryByKeyId(long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(QueueEntry.class.getSimpleName(), id);
		QueueEntry entry = pm.getObjectById(QueueEntry.class, key);
		pm.close();
		return entry;
	}
}
