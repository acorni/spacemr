package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
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
public class App_notificaController {
   @Autowired
   private AppControllerTools appControllerTools;
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private AppSessionTools appSessionTools;
   @Autowired
   private Tools tools;
   @Autowired
   private App_notifica app_notifica;
   // @Autowired
   // private App_notifica_super app_notifica_super;
   //-
   /**
    *  http://localhost:8080/app_notifica/app_notifica_insert
    */
   @Transactional
   @RequestMapping(value="app_notifica/app_notifica_insert")
   public void app_notifica_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_notifica_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int id = app_notifica.insert(content);
      app_log.writeLog(app_notifica, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("app_notifica_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    */
   @RequestMapping(value="app_notifica/app_notifica_list/**")
   public void app_notifica_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_notifica_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      String              outputtype = qparams.optString("outputtype","json");
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)app_notifica.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_notifica"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("app_notifica_id", "id");
      //-
      //-
      AppListTableMapper mapper = null;
      if (outputtype.equals("csv")) {
         mapper = qtm.getMapperCsv(response.getWriter(), appSessionTools.getUserData(session));
         response.setContentType("text/csv");
      } else {
         mapper = qtm.getMapperJson();
      }
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns(app_notifica.getComputedColumnsMap(), "app_notifica");
      //-
      //- where
      //-
      {
         // qtm.setWhereIntegerEqual("app_notifica_super_id",  "app_notifica.app_notifica_super_id");
         qtm.setWhereDate(      "date",  "app_notifica.date");
         qtm.setWhereStringLike("bean_name",  "app_notifica.bean_name");
         qtm.setWhereInteger("object_id",  "app_notifica.object_id");
         qtm.setWhereStringLike("json_info",  "app_notifica.json_info");
         qtm.setWhereBoolean("fg_serving",  "app_notifica.fg_serving");
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from app_notifica");
      // qs.append("\n ,    app_notifica_super");
      // qtm.addAnd(" app_notifica_super.app_notifica_super_id = app_notifica.app_notifica_super_id");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      if (!outputtype.equals("csv")) {
         rv.put("list",       mapper.getJSON());
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   //-
   /**
    *  http://localhost:8080/app_notifica/app_notifica_get?content={"app_notifica_id", "1"}
    */
   @RequestMapping(value="app_notifica/app_notifica_get")
   public void app_notifica_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_notifica_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_notifica_id = content.getInt("app_notifica_id");
      JSONObject obj = app_notifica.get(app_notifica_id);
      rv.put("obj",obj);
      // JSONObject app_notifica_superj = app_notifica_super.get(obj.getInt("app_notifica_super_id"));
      // rv.put("app_notifica_super",app_notifica_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   @Transactional
   @RequestMapping(value="app_notifica/app_notifica_update")
   public void app_notifica_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_notifica_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_notifica_id = content.getInt("app_notifica_id");
      //-
      app_log.writeLog(app_notifica, app_notifica_id, appSessionTools.getUser_name(session),content);
      //-
      app_notifica.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_notifica/app_notifica_delete
    */
   @Transactional
   @RequestMapping(value="app_notifica/app_notifica_delete")
   public void app_notifica_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_notifica_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_notifica_id = content.getInt("app_notifica_id");
      app_notifica.delete(app_notifica_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_notifica/app_notifica_logs
    */
   @RequestMapping(value="app_notifica/app_notifica_logs")
   public void app_notifica_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_notifica_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("app_notifica_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , app_notifica
                       , id
                       );
   }
}
