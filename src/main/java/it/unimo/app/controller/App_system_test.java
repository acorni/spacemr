package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import it.unimo.app.tools.AppPermissionTools_userData;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
//-
/*
   cd /dati/bin/workspace/tmp/mvc; gradle run
*/
@Controller
public class App_system_test {
   @Autowired
   private AppControllerTools appControllerTools;
   @Autowired
   private AppSessionTools appSessionTools;
   @Autowired
   private Tools tools;
   //-
   /**
    *  http://localhost:8080/app_system_test/app_system_test_get_request_headers
    */
   @Transactional
   @RequestMapping(value="app_system_test/app_system_test_get_request_headers")
   public void app_system_test_get_request_headers(HttpSession session
                                                   , HttpServletRequest  request
                                                   , HttpServletResponse response
                                                   , @RequestParam("content") String scontent
                                                   ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_sys_tests")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject headers = new JSONObject();
      Enumeration<String> headerNames = request.getHeaderNames();
      if (headerNames != null) {
         while (headerNames.hasMoreElements()) {
            String n = headerNames.nextElement();
            headers.put(n, request.getHeader(n));
         }
      }
      rv.put("headers",  headers);
      //-
      //-
      AppPermissionTools_userData userData = 
         (AppPermissionTools_userData)session.getAttribute("userData");
      if (userData != null) {
         rv.put("userData",  userData.getJSONObject());
      }

      
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
}
