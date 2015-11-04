package com.steppschuh.intelliq.api.response;

import com.steppschuh.intelliq.api.QueueEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueueListApiResponse extends ApiResponse {

    private ArrayList<QueueEntry> content;

    public ArrayList<QueueEntry> getContent() {
        if (this.content == null) {
            parseContent();
        }
        return this.content;
    }

    private void parseContent() {
        this.content = new ArrayList<>();
        
        try {
            /*
            ArrayList<LinkedHashMap> response = (ArrayList<LinkedHashMap>) super.getContent();
            for (LinkedHashMap linkedHashMap : response) {

                QueueEntry queueEntry = new QueueEntry();
                queueEntry.setName((String) linkedHashMap.get("name"));

                for (Object entryObject : linkedHashMap.entrySet()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) entryObject;
                }
            }
            */
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
