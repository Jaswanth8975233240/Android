package com.steppschuh.intelliq;

public class ApiHelper {

    public static final String BASE_URL = "https://intelliq.herokuapp.com/api/";

    public static String getAllCompaniesUrl() {
        return BASE_URL + "companies";
    }

    public static String getAddQueueItemUrl() {
        return BASE_URL + "qitems";
    }

    public static String getCancelQueueItemUrl(String id) {
        return BASE_URL + "qitems/cancel?id=" + id;
    }

}
