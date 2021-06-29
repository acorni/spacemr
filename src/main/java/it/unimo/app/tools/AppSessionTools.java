package it.unimo.app.tools;

// @author Alberto Corni [Al 20140605-19:22]
 
import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
 
import org.json.JSONArray;
 
public class AppSessionTools {
 

   public String hasPermission(HttpSession session
                               , String permission
                               ) throws Exception {
      String rv = hasPermission(session, "root", permission);
      return(rv);
   }

   public String hasPermission(HttpSession session
                               , String permissions[]
                               ) throws Exception {
      String rv = hasPermission(session, "root", permissions);
      return(rv);
   }

   public String hasPermission(HttpSession session
                               , String group
                               , String permission
                               ) throws Exception {
      String rv = "no permission " + permission;
      //-
      AppPermissionTools_userData userData = 
         (AppPermissionTools_userData)session.getAttribute("userData");
      if (userData != null) {
         if (userData.hasPermission(group,permission)) {
            rv = null;
         } else {
            if (userData.hasPermission("root",permission)) {
               rv = null;
            }
         }
      }
      // rv = null;
      //-
      return(rv);
   }


   public String hasPermission(HttpSession session
                               , String group
                               , String permissions[]
                               ) throws Exception {
      String rv = null;
      //-
      AppPermissionTools_userData userData = 
         (AppPermissionTools_userData)session.getAttribute("userData");
      if (userData != null) {
         for (String permission: permissions) {
            if (rv == null 
                && !userData.hasPermission(group,permission)) {
               rv = "no permission " + permission;
            }
         }
      }
      // rv = null;
      //-
      return(rv);
   }


   public JSONArray groupsHavingThePermission(HttpSession session
                                              , String permissionName
                                              ) throws Exception {
      JSONArray rv = null;
      //-
      AppPermissionTools_userData userData = 
         (AppPermissionTools_userData)session.getAttribute("userData");
      if (userData != null) {
         rv = userData.groupsHavingThePermission(permissionName);
      }
      //-
      return(rv);
   }

      
   public String getUser_name(HttpSession session) throws Exception {
      //-
      String rv = getUserData(session).getUser_name();
      //-
      return(rv);
   }

   public void setUserData(HttpSession session
                           , AppPermissionTools_userData userData
                           ) throws Exception {
      session.setAttribute("userData", userData);
   }

   public AppPermissionTools_userData getUserData(HttpSession session
                                                  ) throws Exception {
      AppPermissionTools_userData rv =
         (AppPermissionTools_userData)session.getAttribute("userData");
      return(rv);
   }

   public HashMap<String,Cookie> getCookiesMap(HttpServletRequest request
                                                ) throws Exception {
      HashMap<String,Cookie> cookiesMap = new HashMap<String,Cookie>();
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
         for (Cookie cookie: cookies) {
            cookiesMap.put(cookie.getName(), cookie);
            // JSONObject cj = new JSONObject();
            // cj.put("comment", cookie.getComment());
            // cj.put("domain", cookie.getDomain());
            // cj.put("maxAge", cookie.getMaxAge());
            // cj.put("name", cookie.getName());
            // cj.put("path", cookie.getPath());
            // cj.put("secure", cookie.getSecure());
            // cj.put("value", cookie.getValue());
            // cj.put("version", cookie.getVersion());
            // System.out.println(" ---- c: " + cj.toString(2));
         }
      }
      return(cookiesMap);
   }


}
