package it.unimo.app.controller;

import java.util.Date;
import java.util.Vector;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import it.unimo.app.tools.DbTools;

import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.json.JSONObject;
import org.json.JSONArray;

import it.unimo.app.tools.Tools;

/** 
   Controllers tools library
*/
public class AppControllerTools {

   @Autowired
   private Tools tools;

   /** 
    * "standard" way to reply to a mvc call */
   public void appDoRequestMappingResponse(HttpServletResponse response
                                           , String returnStatus
                                           , JSONObject content
                                           ) throws Exception {
      JSONObject rv = new JSONObject();
      rv.put("status",  returnStatus);
      //-
      if (content != null) {
         rv.put("content", content);
      }
      //-
      response.setContentType("text/plain");
      response.setCharacterEncoding("UTF-8");
      response.getOutputStream().write(rv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
      response.getOutputStream().flush();
   }


   /**
    *  */
   public static SimpleDateFormat getMvcDateFormat() {
      return(_MVC_DATE_FORMATTER);
   }

   public static SimpleDateFormat getMvcTimestampFormat() {
      return(_MVC_TIMESTAMP_FORMATTER);
   }
   

   private static SimpleDateFormat _MVC_DATE_FORMATTER =
      new SimpleDateFormat("yyyyMMdd");


   private static SimpleDateFormat _MVC_TIMESTAMP_FORMATTER =
      new SimpleDateFormat("yyyyMMddHHmmss");


}

