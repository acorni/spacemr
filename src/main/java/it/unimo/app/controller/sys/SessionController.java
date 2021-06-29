package it.unimo.app.controller.sys;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

import org.json.JSONObject;
import org.json.JSONArray;


/* 
 cd /dati/bin/workspace/spacemr; gradle run
 */
@Controller
public class SessionController {

   /** 
    *  http://localhost:8080/sys/sessioninfo
    */
   @RequestMapping(value = "/sys/sessioninfo", method = RequestMethod.GET)
   public void sessioninfo(HttpServletRequest request
                           , HttpServletResponse response
                           ) throws Exception {
      // System.out.println(" here! --- ");
      JSONObject json = new JSONObject();
      {
         JSONObject json1 = new JSONObject();
         json1.put("pathInfo", request.getPathInfo());
         json1.put("pathTranslated", request.getPathTranslated());
         json1.put("queryString", request.getQueryString());
         json1.put("remoteUser", request.getRemoteUser());
         json1.put("requestedSessionId", request.getRequestedSessionId());
         json1.put("requestURI", request.getRequestURI());
         json1.put("servletPath", request.getServletPath());
         json1.put("contextPath", request.getContextPath());
         json1.put("authType", request.getAuthType());
         {
            JSONObject json2 = new JSONObject();
            int pos=0;
            if (request.getCookies() != null) {
               for (Cookie c: request.getCookies()) {
                  JSONObject json3 = new JSONObject();
                  json3.put("name", c.getName());
                  json3.put("comment", c.getComment());
                  json3.put("domain", c.getDomain());
                  json3.put("path", c.getPath());
                  json3.put("value", c.getValue());
                  json2.put("" + (pos++), json3);
               }
               json1.put("cookies", json2);
            }
         }
         //-
         json.put("request", json1);
      }
      //-
      response.setContentType("text/plain");
      response.getOutputStream().write(json.toString().getBytes());
      response.getOutputStream().flush();
   }



}

