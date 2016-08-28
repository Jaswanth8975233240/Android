package com.intelliq.appengine.chron;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressWarnings("serial")
public class ChronServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ChronServlet.class.getName());

	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
		
		String requestUrl = req.getRequestURL().toString();
		
		String response = "";
		Object responseObject = null;
		
		try {
			if (requestUrl.contains("/clean/datastore/")) {
				responseObject = processCleanDatastoreRequest(req);
			} else {
				throw new Exception("Unknown chron job");
			}
			
			if (responseObject != null) {
				Gson gson = new Gson();
				response = gson.toJson(responseObject);
			} else {
				response = "{}";
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				response = e.getMessage();
			} else {
				response = e.toString();
			}			
			e.printStackTrace();
		}
		
		resp.setContentType("application/json");
		resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.getWriter().write(response);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
	
	public Object processCleanDatastoreRequest(HttpServletRequest req) throws Exception {
		log.info("Datastore cleaning invoked");
		
		// TODO: implement datastore cleaning
		
		return null;
	}

}
