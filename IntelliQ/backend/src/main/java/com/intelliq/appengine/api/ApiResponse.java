package com.intelliq.appengine.api;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class ApiResponse {

	int statusCode = HttpServletResponse.SC_OK;
	String statusMessage = "OK";
	Object content;
	
	public String toJSON() {
		String json = "{}";
		try {
			Gson gson = new Gson();
			json = gson.toJson(this);
		} catch (Exception ex) {
			
		}
		return json;
	}
	
	public void setException(Exception exception) {
		if (exception != null) {
			if (exception.getMessage() != null) {
				statusMessage = exception.getMessage();
			} else {
				statusMessage = exception.toString();
			}
		} else {
			statusMessage = "Unknown internal server error";
		}
		if (statusCode == HttpServletResponse.SC_OK) {
			statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
	
	
}
