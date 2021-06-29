package it.unimo.app.controller;

//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import it.unimo.app.tools.peopleImport.PeopleImportLdap;
//-
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
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

//- logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//-
/*
   cd /dati/bin/workspace/tmp/mvc; gradle run
*/
@Controller
public class Spacemr_peopleController {
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
   private Spacemr_people spacemr_people;
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private PeopleImportLdap peopleImportLdap;

   
   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(Spacemr_peopleController.class);

   // @Autowired
   // private Spacemr_people_super spacemr_people_super;
   //-
   /**
    *  http://localhost:8080/spacemr_people/spacemr_people_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_people/spacemr_people_insert")
   public void spacemr_people_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_people_insert")) != null) {
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
      int id = spacemr_people.insert(content);
      app_log.writeLog(spacemr_people, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_people_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    */
   @RequestMapping(value="spacemr_people/spacemr_people_list/**")
   public void spacemr_people_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_people_read")) != null) {
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
      Vector<String> checkVector    = (Vector<String>)spacemr_people.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_people"
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
      qtm.setCustomHeaderAndAddColumn("spacemr_people_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_main_space_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_main_space_in_map_id_default", "id");
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
      qtm.checkColumns(spacemr_people.getComputedColumnsMap(), "spacemr_people");
      //-
      //- where
      //-
      {
         // qtm.setWhereIntegerEqual("spacemr_people_super_id",  "spacemr_people.spacemr_people_super_id");
         qtm.setWhereStringLike("username",  "spacemr_people.username");
         qtm.setWhereStringLike("first_name",  "spacemr_people.first_name");
         qtm.setWhereStringLike("last_name",  "spacemr_people.last_name");
         qtm.setWhereStringLike("email",  "spacemr_people.email");
         qtm.setWhereStringLike("role",  "spacemr_people.role");
         qtm.setWhereStringLike("department",  "spacemr_people.department");
         qtm.setWhereBoolean("fg_in_ldap",  "spacemr_people.fg_in_ldap");
         qtm.setWhereBoolean("fg_has_a_seat",  "spacemr_people.fg_has_a_seat");
         qtm.setWhereStringLike("nota",  "spacemr_people.nota");
         qtm.setWhereStringLike("configuration",  "spacemr_people.configuration");
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
      qs.append("\n from spacemr_people");
      // qs.append("\n ,    spacemr_people_super");
      // qtm.addAnd(" spacemr_people_super.spacemr_people_super_id = spacemr_people.spacemr_people_super_id");
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
    *  http://localhost:8080/spacemr_people/spacemr_people_get?content={"spacemr_people_id", "1"}
    */
   @RequestMapping(value="spacemr_people/spacemr_people_get")
   public void spacemr_people_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_people_read")) != null) {
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
      JSONObject obj = null;
      if (content.has("spacemr_people_id") && !"".equals(content.getString("spacemr_people_id"))){
         int spacemr_people_id = content.getInt("spacemr_people_id");
         obj = spacemr_people.get(spacemr_people_id);
      } else {
         String spacemr_people_username = content.getString("spacemr_people_username");
         obj = spacemr_people.getByUsername(spacemr_people_username);
      }
      rv.put("obj",obj);
      // JSONObject spacemr_people_superj = spacemr_people_super.get(obj.getInt("spacemr_people_super_id"));
      // rv.put("spacemr_people_super",spacemr_people_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   @Transactional
   @RequestMapping(value="spacemr_people/spacemr_people_update")
   public void spacemr_people_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_people_update")) != null) {
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
      int spacemr_people_id = content.getInt("spacemr_people_id");
      //-
      app_log.writeLog(spacemr_people, spacemr_people_id, appSessionTools.getUser_name(session),content);
      //-
      spacemr_people.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/spacemr_people/spacemr_people_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_people/spacemr_people_delete")
   public void spacemr_people_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_people_delete")) != null) {
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
      int spacemr_people_id = content.getInt("spacemr_people_id");
      spacemr_people.delete(spacemr_people_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/spacemr_people/spacemr_people_logs
    */
   @RequestMapping(value="spacemr_people/spacemr_people_logs")
   public void spacemr_people_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_people_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("spacemr_people_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_people
                       , id
                       );
   }
   //-
   //-
   @RequestMapping(value="spacemr_people/spacemr_people_ldap_search")
   public void spacemr_people_ldap_search(HttpSession session
                                          , HttpServletResponse response
                                          , @RequestParam("content") String scontent
                                          ) throws Exception {
      String status = null;
      // System.out.println(" in spacemr_ldap_search");
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "spacemr_people_ldap_search")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      //-
      JSONObject conf = app_system_property.getAsJSONObject("spacemr_ldap_search_configuration");
      //-
      String user_name       = content.optString("username","");
      String user_first_name = content.optString("first_name","");
      String user_last_name  = content.optString("last_name","");
      //-
      String query = conf.getString("query_people_ldap_search");
      query=tools.stringReplaceInString(query, "${user_name}", user_name);
      query=tools.stringReplaceInString(query, "${user_first_name}",  user_first_name);
      query=tools.stringReplaceInString(query, "${user_last_name}",   user_last_name);
      //-
      // System.out.println(" query: " + query);
      //-
      try {
         JSONArray users_ldap = peopleImportLdap.getUsersFromLdap(conf, query);
         JSONArray rows       = peopleImportLdap.getUsersTransformed(conf, users_ldap);
         rv.put("rows",rows);
         rv.put("parser",conf.getString("parser"));
         status = "ok";
      } catch (Exception e) {
         status = "Problems connecting to the server \n" + tools.stringStackTrace(e);
         logger.error(status);
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   
}
