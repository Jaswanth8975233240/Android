package com.intelliq.appengine.website;

import com.intelliq.appengine.RequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class IntelliQServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(IntelliQServlet.class.getName());

    /*
     * Handles all requests for the IntelliQ website and redirects to the matching JSP.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String forwardUrl;
        String requestUrl = req.getRequestURL().toString();
        String uri = req.getRequestURI();
        String rootUrl = requestUrl.substring(0, requestUrl.indexOf(uri));

        // detect language of request origin
        String languageCode = RequestFilter.getRequestLanguageCode(req);
        forwardUrl = "/intelliq/" + languageCode + "/";

        // forward requests to available JSPs
        if (requestUrl.contains("/manage/")) {
            if (requestUrl.contains("/queue/")) {
                forwardUrl += "manage/queueservlet/";
            } else if (requestUrl.contains("/business/")) {
                forwardUrl += "manage/businessservlet/";
            } else {
                forwardUrl += "manage/overviewservlet/";
            }
        } else if (requestUrl.contains("/edit/")) {
            if (requestUrl.contains("/queue/")) {
                forwardUrl += "edit/queueservlet/";
            } else if (requestUrl.contains("/business/")) {
                forwardUrl += "edit/businessservlet/";
            }
        } else if (requestUrl.contains("/display/")) {
            if (requestUrl.contains("/queue/")) {
                forwardUrl += "display/queueservlet/";
            } else if (requestUrl.contains("/business/")) {
                //forwardUrl += "display/businessservlet/";
            } else {
                //forwardUrl += "manage/overviewservlet/";
            }
        } else if (requestUrl.contains("/apps/")) {
            if (requestUrl.contains("/web/")) {
                if (requestUrl.contains("/queue/")) {
                    forwardUrl += "webapp/queueservlet/";
                } else {
                    forwardUrl += "webappservlet/";
                }
            } else {
                forwardUrl += "appsservlet/";
            }
        } else if (requestUrl.contains("/unternehmen/") || requestUrl.contains("/business/")) {
            forwardUrl += "businessservlet/";
        } else if (requestUrl.contains("/imprint/") || requestUrl.contains("/impressum/")) {
            forwardUrl += "imprintservlet/";
        } else if (requestUrl.contains("/signin/")) {
            forwardUrl += "signinservlet/";
        } else {
            // landing page
            forwardUrl += "homeservlet/";
        }

        // get dispatcher for the generated URL
        RequestDispatcher rd = getServletContext().getRequestDispatcher(forwardUrl);

        // set JSP attributes
        req.setAttribute("rootUrl", rootUrl);
        req.setAttribute("requestUrl", requestUrl.replace("/intelliq", ""));
        req.setAttribute("staticUrl", rootUrl + "/static/");
        req.setAttribute("appUrl", rootUrl + "/apps/web/");
        req.setAttribute("manageUrl", rootUrl + "/manage/");

        try {
            rd.forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
