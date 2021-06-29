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
public class Spacemr_space_typeController {
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
   private Spacemr_space_type spacemr_space_type;
   // @Autowired
   // private Spacemr_space_type_super spacemr_space_type_super;
   //-
   /**
    *  http://localhost:8080/spacemr_space_type/spacemr_space_type_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_space_type/spacemr_space_type_insert")
   public void spacemr_space_type_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_type_insert")) != null) {
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
      int id = spacemr_space_type.insert(content);
      app_log.writeLog(spacemr_space_type, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_space_type_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    */
   @RequestMapping(value="spacemr_space_type/spacemr_space_type_list/**")
   public void spacemr_space_type_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_type_read")) != null) {
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
      Vector<String> checkVector    = (Vector<String>)spacemr_space_type.getColumns().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_space_type"
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
      qtm.setCustomHeaderAndAddColumn("spacemr_space_type_id", "id");
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
      qtm.checkColumns(spacemr_space_type.getComputedColumnsMap(), "spacemr_space_type");
      //-
      //- where
      //-
      {
         // qtm.setWhereIntegerEqual("spacemr_space_type_super_id",  "spacemr_space_type.spacemr_space_type_super_id");
         qtm.setWhereStringLike("name",  "spacemr_space_type.name");
         qtm.setWhereStringLike("description",  "spacemr_space_type.description");
         qtm.setWhereStringLike("nota",  "spacemr_space_type.nota");
         qtm.setWhereStringLike("configuration",  "spacemr_space_type.configuration");
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
      qs.append("\n from spacemr_space_type");
      // qs.append("\n ,    spacemr_space_type_super");
      // qtm.addAnd(" spacemr_space_type_super.spacemr_space_type_super_id = spacemr_space_type.spacemr_space_type_super_id");
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
    *  http://localhost:8080/spacemr_space_type/spacemr_space_type_get?content={"spacemr_space_type_id", "1"}
    */
   @RequestMapping(value="spacemr_space_type/spacemr_space_type_get")
   public void spacemr_space_type_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_type_read")) != null) {
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
      int spacemr_space_type_id = content.getInt("spacemr_space_type_id");
      JSONObject obj = spacemr_space_type.get(spacemr_space_type_id);
      rv.put("obj",obj);
      // JSONObject spacemr_space_type_superj = spacemr_space_type_super.get(obj.getInt("spacemr_space_type_super_id"));
      // rv.put("spacemr_space_type_super",spacemr_space_type_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   @Transactional
   @RequestMapping(value="spacemr_space_type/spacemr_space_type_update")
   public void spacemr_space_type_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_type_update")) != null) {
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
      int spacemr_space_type_id = content.getInt("spacemr_space_type_id");
      //-
      app_log.writeLog(spacemr_space_type, spacemr_space_type_id, appSessionTools.getUser_name(session),content);
      //-
      spacemr_space_type.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/spacemr_space_type/spacemr_space_type_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_space_type/spacemr_space_type_delete")
   public void spacemr_space_type_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_type_delete")) != null) {
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
      int spacemr_space_type_id = content.getInt("spacemr_space_type_id");
      spacemr_space_type.delete(spacemr_space_type_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/spacemr_space_type/spacemr_space_type_logs
    */
   @RequestMapping(value="spacemr_space_type/spacemr_space_type_logs")
   public void spacemr_space_type_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_type_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("spacemr_space_type_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_space_type
                       , id
                       );
   }
}
