package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import it.unimo.app.tools.Workflow;
import it.unimo.app.tools.WorkflowManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.annotation.PostConstruct;
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
public class Spacemr_inventarioController {
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
   private WorkflowManager workflowManager;
   @Autowired
   private Spacemr_inventario spacemr_inventario;
   // @Autowired
   // private Spacemr_inventario_super spacemr_inventario_super;
   //-


   @PostConstruct
   public void init_custom_headers() throws Exception {
      AppListTableMapper
         .addCustomHeaderFromExistingHeader("longstring", "longstring_spacemr_inventario_links");
   }

   /**
    *  http://localhost:8080/spacemr_inventario/spacemr_inventario_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_inventario/spacemr_inventario_insert")
   public void spacemr_inventario_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_inventario_insert")) != null) {
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
      Workflow workflow =
         workflowManager.getWorkflow(spacemr_inventario.getWorkflowId(), appSessionTools, session);
      content.put("stato", workflow.getDefaultStatus());
      //-
      int id = spacemr_inventario.insert(content);
      app_log.writeLog(spacemr_inventario, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_inventario_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   
   /**
    * returns an array containing all workflows used by inventario objects
    * to be cached on browser.
    */
   @RequestMapping(value="spacemr_inventario/spacemr_inventario_workflows/**")
   public void spacemr_inventario_workflows(HttpSession session
                                            , HttpServletResponse response
                                            , @RequestParam("content") String sqparams
                                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_inventario_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject workflows = new JSONObject();
      //-
      String workflowid = spacemr_inventario.getWorkflowId();
      JSONObject workflow = workflowManager.getWorkflow(workflowid, appSessionTools, session).toJson();
      workflows.put(workflowid, workflow);
      //-
      rv.put("workflows",workflows);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
     
   /**
    */
   @RequestMapping(value="spacemr_inventario/spacemr_inventario_list/**")
   public void spacemr_inventario_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_inventario_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      //- System.out.println(" \n--- qparams"
      //-                    + " \n" + sqparams
      //-                    + " \n" + qparams.toString(2)
      //-                    + " \n" + "---"
      //-                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      String              outputtype = qparams.optString("outputtype","json");
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)spacemr_inventario.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_inventario"
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
      qtm.setCustomHeaderAndAddColumn("spacemr_inventario_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_people_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_in_map_id_default", "id");
      qtm.setCustomHeaderAndAddColumn("stato_hidden", "string_hidden");
      qtm.setCustomHeader("old_values_changes", "longstring_spacemr_inventario_links");
      qtm.setCustomHeader("old_values", "longstring_spacemr_inventario_links");
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
      qtm.checkColumns(spacemr_inventario.getComputedColumnsMap(), "spacemr_inventario");
      //-
      //- where
      //-
      {
         qtm.setWhereIntegerEqual("spacemr_people_id",  "spacemr_inventario.spacemr_people_id");
         // qtm.setWhereStringLike("stato",  "spacemr_inventario.stato");
         qtm.setWhereStringEqual("stato_equal",  "spacemr_inventario.stato");
         qtm.setWhereWorkflow("stato",  "spacemr_inventario.stato"
                              , workflowManager.getWorkflow(spacemr_inventario.getWorkflowId(), appSessionTools, session));
         qtm.setWhereStringLike("spacemr_people_username",  "spacemr_people.username");
         qtm.setWhereStringLike("spacemr_people_first_name",  "spacemr_people.first_name");
         qtm.setWhereStringLike("spacemr_people_last_name",  "spacemr_people.last_name");
         qtm.setWhereStringLike("spacemr_people_role",  "spacemr_people.role");
         qtm.setWhereStringLike("spacemr_space_code",  "spacemr_space.code");
         qtm.setWhereStringLike("spacemr_space_description",  "spacemr_space.description");
         qtm.setWhereBoolean("fg_validato",  "spacemr_inventario.fg_validato");
         qtm.setWhereBoolean("fg_adesivo",  "spacemr_inventario.fg_adesivo");
         qtm.setWhereStringLike("codice_inventario_unimore",  "spacemr_inventario.codice_inventario_unimore");
         qtm.setWhereStringLike("inventario_numero",  "spacemr_inventario.inventario_numero");
         qtm.setWhereStringLike("inventario_numero_sub",  "spacemr_inventario.inventario_numero_sub");
         qtm.setWhereStringLike("inventario_etichetta",  "spacemr_inventario.inventario_etichetta");
         qtm.setWhereStringLike("tipo_carico_scarico",  "spacemr_inventario.tipo_carico_scarico");
         qtm.setWhereDate(      "carico_data",  "spacemr_inventario.carico_data");
         qtm.setWhereStringLike("attivita_tipo",  "spacemr_inventario.attivita_tipo");
         qtm.setWhereStringLike("descrizione",  "spacemr_inventario.descrizione");
         qtm.setWhereStringLike("categoria_inventario",  "spacemr_inventario.categoria_inventario");
         qtm.setWhereDecimal("valore",  "spacemr_inventario.valore");
         qtm.setWhereStringLike("fornitore",  "spacemr_inventario.fornitore");
         qtm.setWhereStringLike("scarico_numero_buono",  "spacemr_inventario.scarico_numero_buono");
         qtm.setWhereDate(      "scarico_data",  "spacemr_inventario.scarico_data");
         qtm.setWhereStringLike("old_values",  "spacemr_inventario.old_values");
         qtm.setWhereStringLike("old_values_changes",  "spacemr_inventario.old_values_changes");
         qtm.setWhereStringLike("nota",  "spacemr_inventario.nota");
         //-
         boolean recursive_space = false;
         if(qtm.getQwhereParamAsObject("recursive_space") == null) {
         } else {
            recursive_space = qtm.getQwhere().getBoolean("recursive_space");
         }
         if (!recursive_space) {
            qtm.setWhereIntegerEqual("spacemr_space_id",  "spacemr_inventario.spacemr_space_id");
         } else {
            String wc = "wc__spacemr_space_id";
            int spacemr_space_id = qtm.getQwhere().getInt("spacemr_space_id");
            qtm.addQswhere(wc, spacemr_space_id);
            qtm.addAnd("spacemr_inventario.spacemr_space_id in ("
                       + "\n  with recursive root_spaces (spacemr_space_id, spacemr_space_in_id) as ("
                       + "\n    select     a.spacemr_space_id,"
                       + "\n               a.spacemr_space_in_id"
                       + "\n      from     spacemr_space as a"
                       + "\n     where     a.spacemr_space_id = :"+wc+""
                       + "\n    union all"
                       + "\n    select     p.spacemr_space_id,"
                       + "\n               p.spacemr_space_in_id"
                       + "\n      from     spacemr_space as p"
                       + "\n             , root_spaces as r"
                       + "\n     where     p.spacemr_space_in_id = r.spacemr_space_id"
                       + "\n  )"
                       + "\n  select spacemr_space_id from root_spaces"
                       + "\n )");
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
      qs.append("\n from spacemr_inventario"
                + "   left join spacemr_space  on spacemr_inventario.spacemr_space_id  = spacemr_space.spacemr_space_id"
                + "   left join spacemr_people on spacemr_inventario.spacemr_people_id = spacemr_people.spacemr_people_id"
                );
      // qs.append("\n ,    spacemr_inventario_super");
      // qtm.addAnd(" spacemr_inventario_super.spacemr_inventario_super_id = spacemr_inventario.spacemr_inventario_super_id");
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
         // System.out.println(" list: " + rv.getJSONObject("list").getJSONArray("headers").toString(2));
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   //-
   
   /**
    *  http://localhost:8080/spacemr_inventario/spacemr_inventario_get?content={"spacemr_inventario_id", "1"}
    */
   @RequestMapping(value="spacemr_inventario/spacemr_inventario_get")
   public void spacemr_inventario_get(HttpSession session
                                      , HttpServletResponse response
                                      , @RequestParam("content") String scontent
                                      ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_inventario_read")) != null) {
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
      int spacemr_inventario_id = content.getInt("spacemr_inventario_id");
      JSONObject obj = spacemr_inventario.get(spacemr_inventario_id);
      //-
      Workflow wf =
         workflowManager.getWorkflow(spacemr_inventario.getWorkflowId(), appSessionTools, session);
      wf.per_field_hideUnreadable(obj.getString("stato"), obj);
      //-
      rv.put("obj",obj);
      // JSONObject spacemr_inventario_superj = spacemr_inventario_super.get(obj.getInt("spacemr_inventario_super_id"));
      // rv.put("spacemr_inventario_super",spacemr_inventario_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   //-
   @Transactional
   @RequestMapping(value="spacemr_inventario/spacemr_inventario_update")
   public void spacemr_inventario_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_inventario_update")) != null) {
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
      int spacemr_inventario_id = content.getInt("spacemr_inventario_id");
      //-
      //-
      JSONObject old_obj = spacemr_inventario.get(spacemr_inventario_id);
      if (old_obj.optBoolean("fg_validato",false) && (status = appSessionTools.hasPermission(session, "db_spacemr_inventario_admin")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      //- workflow transaction ckecks
      //-
      Workflow workflow =
         workflowManager.getWorkflow(spacemr_inventario.getWorkflowId(), appSessionTools, session);
      String stato_corrente = old_obj.getString("stato");
      String stato_nuovo    = content.getString("stato");
      String get_wf_permission = workflow.getPermission(stato_corrente, stato_nuovo);
      //-
      // System.out.println(" -- get_wf_permission: " + get_wf_permission);
      if (get_wf_permission != null) {
         if ((status = appSessionTools.hasPermission(session, get_wf_permission)) != null) {
            status = "permissionDenied " + status;
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      //- single field permissions check
      //-
      workflow.per_field_restoreUnwritable(stato_corrente, content, old_obj);
      //-
      //-
      app_log.writeLog(spacemr_inventario, spacemr_inventario_id, appSessionTools.getUser_name(session),content);
      //-
      spacemr_inventario.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
   //-
   /**
    *  http://localhost:8080/spacemr_inventario/spacemr_inventario_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_inventario/spacemr_inventario_delete")
   public void spacemr_inventario_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_inventario_delete")) != null) {
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
      int spacemr_inventario_id = content.getInt("spacemr_inventario_id");
      spacemr_inventario.delete(spacemr_inventario_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/spacemr_inventario/spacemr_inventario_logs
    */
   @RequestMapping(value="spacemr_inventario/spacemr_inventario_logs")
   public void spacemr_inventario_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_inventario_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      //-
      //- custom headers management
      JSONObject customHeaders = new JSONObject();
      customHeaders.put("parameter_values","longstring_spacemr_inventario_links");
      qparams.put("customHeaders", customHeaders);
      //-
      int id = qparams.getInt("spacemr_inventario_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_inventario
                       , id
                       );
   }
}
