package com.steppschuh.intelliq;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class ApiHelper {

    public static final String BASE_URL = "https://intelliq.herokuapp.com/api/";

    public static String getAllCompaniesUrl() {
        return BASE_URL + "companies";
    }

    public static String getAddQueueItemUrl(String name, String id) {
        return BASE_URL + "qitems/post?company=" + id + "&name=" + name;
    }

    public static String getCancelQueueItemUrl(String id) {
        return BASE_URL + "qitems/cancel/post?id=" + id;
    }

    public static String getQueueItemsForCompanyUrl(String id) {
        return BASE_URL + "qitems?companyId=" + id;
    }

}
