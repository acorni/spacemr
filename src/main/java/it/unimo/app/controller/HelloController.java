package it.unimo.app.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.json.JSONObject;
import org.json.JSONArray;

import it.unimo.app.tools.DbTools;
import it.unimo.app.tools.AppSessionTools;

/* 
 cd /dati/bin/workspace/tmp/mvc; gradle run
 */
@Controller
public class HelloController {


   @Autowired
   private AppSessionTools appSessionTools;

   @Autowired
   private AppControllerTools appControllerTools;

   @Autowired
   private DbTools dbTools;

   // /** 
   //  *  http://localhost:8080/hello
   //  */
   //  @RequestMapping(value="/hello", method=RequestMethod.GET)
   //  public String index(Model model) {
   //     Date date = new Date();
   //     model.addAttribute("date", date);
   //     model.addAttribute("name", "provaname");
   //     System.out.println(" ---");
   //     dbTools.doTest();
   //     return "index";
   //  }
   // 
   // 
   // 
   // 
   // /** 
   //  *  http://localhost:8080/helloCsv
   //  */
   // @RequestMapping(value = "/helloCsv", method = RequestMethod.GET)
   // public void helloCsv(HttpServletResponse response
   //                      ) throws Exception {
   //    String m = 
   //       "[[1362092425000,3],[1362092572000,3],[1362092669000,3],[1362092791000,3],[1362092912000,3]]"
   //       ;
   //    response.setContentType("text/plain");
   //    response.getOutputStream().write(m.getBytes());
   //    response.getOutputStream().flush();
   // }

   /**
    *  http://localhost:8080/spacemr_carica/spacemr_carica_get?content={"spacemr_carica_id", "1"}
    */
   @RequestMapping(value="hello/hello_get_application_directory")
   public void hello_get_application_directory(HttpSession session
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
      rv.put("applicationDirectory", session.getServletContext().getRealPath("/"));
      rv.put("HelloController.realPath", this.getClass().getResource("/").toString());
      rv.put("localAddr", request.getLocalAddr());
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
}

