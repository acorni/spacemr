package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppMapperJSONArray;
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
public class Spacemr_space_peopleController {
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
   private Spacemr_space_people spacemr_space_people;
   @Autowired
   private Spacemr_space_people_type spacemr_space_people_type;
   // @Autowired
   // private Spacemr_space_people_super spacemr_space_people_super;
   //-

   @Transactional
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_insert_get_data")
   public void spacemr_space_people_insert_get_data(HttpSession session
                                                    , HttpServletResponse response
                                                    , @RequestParam("content") String scontent
                                                    ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      status = "ok";
      JSONArray spacemr_space_people_types = spacemr_space_people_type.getAllForCombo();
      rv.put("spacemr_space_people_types",  spacemr_space_people_types);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
   /**
    *  http://localhost:8080/spacemr_space_people/spacemr_space_people_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_insert")
   public void spacemr_space_people_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_insert")) != null) {
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
      int id = spacemr_space_people.insert(content);
      app_log.writeLog(spacemr_space_people, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_space_people_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   
   /**
    */
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_list/**")
   public void spacemr_space_people_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_read")) != null) {
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
      Vector<String> checkVector    = (Vector<String>)spacemr_space_people.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_space_people"
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
      qtm.setCustomHeaderAndAddColumn("spacemr_space_people_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_in_map_id_default", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_people_id", "id");
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
      qtm.checkColumns(spacemr_space_people.getComputedColumnsMap(), "spacemr_space_people");
      //-
      //- where
      //-
      {
         qtm.setWhereIntegerEqual("spacemr_space_id",  "spacemr_space_people.spacemr_space_id");
         qtm.setWhereIntegerEqual("spacemr_people_id",  "spacemr_space_people.spacemr_people_id");
         qtm.setWhereIntegerEqual("spacemr_space_people_type_id",  "spacemr_space_people.spacemr_space_people_type_id");
         qtm.setWhereStringEqual("spacemr_people_username",   "spacemr_people.username");
         qtm.setWhereInteger("spacemr_space_people_type_id",  "spacemr_space_people.spacemr_space_people_type_id");
         qtm.setWhereInteger("spacemr_space_id",  "spacemr_space_people.spacemr_space_id");
         qtm.setWhereInteger("spacemr_people_id",  "spacemr_space_people.spacemr_people_id");
         qtm.setWhereDate(      "date_from",  "spacemr_space_people.date_from");
         qtm.setWhereDate(      "date_to",  "spacemr_space_people.date_to");
         qtm.setWhereStringLike("description",  "spacemr_space_people.description");
         qtm.setWhereStringLike("nota",  "spacemr_space_people.nota");
         qtm.setWhereBoolean("fg_is_a_seat",  "spacemr_space_people_type.fg_is_a_seat");
         
         //-
         //- history or not history?
         //-    spacemr_people_spaces_history
         {
            boolean doFilter = true;
            String key = "spacemr_people_spaces_history";
            JSONObject qwhere = qtm.getQwhere();
            if (qwhere != null) {
               Boolean t =  tools.jsonObject_getBoolean(qwhere,key);
               if (t != null && t.booleanValue()) {
                  doFilter = false;
               }
            }
            if (doFilter) {
                  qswhere.append(""
                                  + ((qswhere.length()>0) ? " and " : "")
                                  + "\n           ( spacemr_space_people.date_from <= now()"
                                  + "\n             and ("
                                  + "\n                spacemr_space_people.date_to is null"
                                  + "\n                or spacemr_space_people.date_to >= now()"
                                  + "\n               )"
                                  + "\n            ) "
                               );
            }
         }
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
      qs.append("\n from spacemr_space_people");
      qs.append("\n ,    spacemr_space");
      qtm.addAnd(" spacemr_space.spacemr_space_id = spacemr_space_people.spacemr_space_id");
      qs.append("\n ,    spacemr_people");
      qtm.addAnd(" spacemr_people.spacemr_people_id = spacemr_space_people.spacemr_people_id");
      qs.append("\n ,    spacemr_space_people_type");
      qtm.addAnd(" spacemr_space_people_type.spacemr_space_people_type_id = spacemr_space_people.spacemr_space_people_type_id");
      qs.append("\n ,    spacemr_space_type");
      qtm.addAnd(" spacemr_space_type.spacemr_space_type_id = spacemr_space.spacemr_space_type_id");
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
    *  http://localhost:8080/spacemr_space_people/spacemr_space_people_get?content={"spacemr_space_people_id", "1"}
    */
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_get")
   public void spacemr_space_people_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_read")) != null) {
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
      int spacemr_space_people_id = content.getInt("spacemr_space_people_id");
      JSONObject obj = spacemr_space_people.get(spacemr_space_people_id);
      rv.put("obj",obj);
      // JSONObject spacemr_space_people_superj = spacemr_space_people_super.get(obj.getInt("spacemr_space_people_super_id"));
      // rv.put("spacemr_space_people_super",spacemr_space_people_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   //-
   @Transactional
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_update")
   public void spacemr_space_people_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_update")) != null) {
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
      int spacemr_space_people_id = content.getInt("spacemr_space_people_id");
      //-
      app_log.writeLog(spacemr_space_people, spacemr_space_people_id, appSessionTools.getUser_name(session),content);
      //-
      spacemr_space_people.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   
   /**
    *  http://localhost:8080/spacemr_space_people/spacemr_space_people_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_delete")
   public void spacemr_space_people_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_delete")) != null) {
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
      int spacemr_space_people_id = content.getInt("spacemr_space_people_id");
      spacemr_space_people.delete(spacemr_space_people_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/spacemr_space_people/spacemr_space_people_logs
    */
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_logs")
   public void spacemr_space_people_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("spacemr_space_people_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_space_people
                       , id
                       );
   }


   /**
    * Check if a "username" has a sit in the applications spaces.
    *  http://localhost:8080/spacemr_space_people/spacemr_space_people_has_current_space
    * params:
    *  an array of usernames to check if has current space in it
    *  used in department user page for linking to the spaces.
    *  If a user has a space the link is shown.
    * example:
    *   content: ["acorni","sonia","71401"]
    */
   @RequestMapping(value="spacemr_space_people/spacemr_space_people_has_current_spaces")
   public void spacemr_space_people_has_current_spaces(HttpSession session
                                                       , HttpServletResponse response
                                                       , @RequestParam("content") String scontent
                                                       ) throws Exception {
      JSONObject rv = new JSONObject();
      String status = null;
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONArray content = new JSONArray(scontent);
      //-
      String comma = "";
      StringBuffer sb = new StringBuffer();
      Object[] parameters = new Object[content.length()];
      for (int i = 0; i < content.length(); i++) {
         parameters[i] = content.getString(i);
         sb.append(comma + "?");
         comma=", ";
      }
      //-
      AppMapperJSONArray mapper = new AppMapperJSONArray();
      String qs = ""
         + "\n        select distinct username"
         + "\n          from spacemr_people"
         + "\n             , spacemr_space_people"
         + "\n             , spacemr_space_people_type"
         + "\n           where username in ( " + sb.toString() + " )"
         + "\n             and spacemr_space_people.spacemr_people_id = spacemr_people.spacemr_people_id"
         + "\n             and spacemr_space_people_type.spacemr_space_people_type_id = spacemr_space_people.spacemr_space_people_type_id"
         + "\n             and spacemr_space_people_type.fg_is_a_seat = true"
         + "\n             and spacemr_space_people.date_from <= now()"
         + "\n             and ("
         + "\n                  spacemr_space_people.date_to is null"
         + "\n                  or spacemr_space_people.date_to >= now()"
         + "\n                 )"
         ;
      jdbcTemplate.query(qs , parameters , mapper);
      rv.put("usernames", mapper.getJSONArray());
      //-
      // System.out.println(" rv: " + rv.toString(2));
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
}
