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

//-
/*
   cd /dati/bin/workspace/tmp/mvc; gradle run
*/
@Controller
public class App_systemController {
   @Autowired
   private AppControllerTools appControllerTools;
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private AppSessionTools appSessionTools;
   @Autowired
   private App_log app_log;
   @Autowired
   private Tools tools;
   @Autowired
   private App_system_property app_system_property;

   //-
   /**
    *  http://localhost:8080/app_system/app_system_property_insert
    *  , method=RequestMethod.POST
    */
   @RequestMapping(value="app_system/app_system_property_insert")
   public void app_system_property_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_system_property_insert")) != null) {
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
      int id = app_system_property.insert(content);
      app_log.writeLog(app_system_property, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("app_system_property_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-

   /**
     http://localhost:8080/app_system/app_system_property_list?content={'columns':['name'],'order':[{'column':'name','desc':'true'}]}
    */
   @RequestMapping(value="app_system/app_system_property_list")
   public void app_system_property_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_system_property_read")) != null) {
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
      //-
      //-
      Vector<String> columns        = new Vector<String>();
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)app_system_property.getColumns().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_system_property"
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
      qtm.setCustomHeaderAndAddColumn("app_system_property_id", "id");
      //-
      //-
      AppListTableMapper mapper = qtm.getMapperJson();
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns();
      //-
      //- where
      //-
      {
         qtm.setWhereStringLike("name",  "app_system_property.name");
         qtm.setWhereStringLike("description",  "app_system_property.description");
         qtm.setWhereStringLike("value",  "app_system_property.value");
         qtm.setWhereStringLike("value_default",  "app_system_property.value_default");
         qtm.setWhereStringLike("nota",  "app_system_property.nota");
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
      qs.append("\n from app_system_property");
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
      rv.put("list",       mapper.getJSON());
      rv.put("qparams",    qparams);
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   //-
   /**
    *  http://localhost:8080/app_system/app_system_property_get?content={"app_system_property_id", "1"}
    */
   @RequestMapping(value="app_system/app_system_property_get")
   public void app_system_property_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_system_property_read")) != null) {
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
      int app_system_property_id = content.getInt("app_system_property_id");
      rv.put("obj",app_system_property.get(app_system_property_id));
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_system/app_system_property_update
    */
   @RequestMapping(value="app_system/app_system_property_update")
   public void app_system_property_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_system_property_update")) != null) {
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
      int app_system_property_id = content.getInt("app_system_property_id");
      //-
      app_log.writeLog(app_system_property, app_system_property_id, appSessionTools.getUser_name(session),content);
      //-
      app_system_property.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_system/app_system_property_delete
    */
   @RequestMapping(value="app_system/app_system_property_delete")
   public void app_system_property_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_system_property_delete")) != null) {
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
      int app_system_property_id = content.getInt("app_system_property_id");
      app_system_property.delete(app_system_property_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_system/app_system_property_logs
    */
   @RequestMapping(value="app_system/app_system_property_logs")
   public void app_system_property_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_system_property_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("app_system_property_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , app_system_property
                       , id
                       );
   }


   /**
    */
   @RequestMapping(value="app_system/app_system_property_set_table_mattoni_defaults")
   public void app_system_property_set_table_mattoni_defaults(HttpSession session
                                                              , HttpServletResponse response
                                                              , @RequestParam("content") String scontent
                                                              ) throws Exception {
      String status = "ok";
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_table_mattoni_admin")) != null) {
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
      String      tableid    = content.getString("tableid");
      JSONObject  conf       = content.getJSONObject("conf");
      String propertyName = "table_mattoni__"+tableid;
      //-
      // System.out.println(" conf: " + conf);
      //-
      app_system_property.set(propertyName, conf);
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


}
