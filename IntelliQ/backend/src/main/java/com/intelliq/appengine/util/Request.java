package com.intelliq.appengine.util;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steppschuh on 06/03/2017.
 */

public class Request {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";

    private URL url;
    private Map<String, Object> parameters;
    private Map<String, String> headers;
    private JsonObject jsonObject = new JsonObject();

    public Request() {
    }

    public String post() throws IOException {
        return post(url, parameters, headers);
    }

    public String postJson() throws IOException {
        return post(url, jsonObject.toString(), headers);
    }

    public static String post(URL url, Map<String, Object> parameters, Map<String, String> headers) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> parameterEntry : parameters.entrySet()) {
            sb.append(parameterEntry.getKey())
                    .append("=")
                    .append(parameterEntry.getValue())
                    .append("\n");
        }
        return post(url, sb.toString(), headers);
    }

    public static String post(URL url, String data, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod(METHOD_POST);

        // set headers
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            connection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
        }

        // send data
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        writer.close();
        connection.getOutputStream().close();

        // read response
        InputStream stream;
        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            stream = connection.getErrorStream();
        }
        return readStreamToString(stream);
    }

    public URL getUrl() {
        return url;
    }

    public Request setUrl(URL url) {
        this.url = url;
        return this;
    }

    public Request setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Request setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Request setEncodedParameter(String key, Object value) {
        try {
            return setParameter(key, encode(value));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Request setParameter(String key, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Request setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Request setHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
        return this;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public Request setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    public Request setJsonProperty(String key, String value) {
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        jsonObject.addProperty(key, value);
        return this;
    }


    public static String readStreamToString(InputStream inputStream) throws IOException {
        StringBuilder responseString = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }
        reader.close();
        return responseString.toString();
    }

    public static String encode(Object value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value.toString(), StandardCharsets.UTF_8.name());
    }

}
