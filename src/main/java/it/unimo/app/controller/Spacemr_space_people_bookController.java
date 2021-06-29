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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.Calendar;
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
public class Spacemr_space_people_bookController {
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
   private Spacemr_space_people_book spacemr_space_people_book;
   @Autowired
   private Spacemr_people spacemr_people;
   @Autowired
   private Spacemr_space spacemr_space;
   @Autowired
   private WorkflowManager workflowManager;
   // @Autowired
   // private Spacemr_space_people_book_super spacemr_space_people_book_super;
   //-


   @PostConstruct
   public void init_custom_headers() throws Exception {
      AppListTableMapper
         .addCustomHeaderFromExistingHeader("longstring", "longstring_spacemr_space_people_book_links");
      AppListTableMapper
         .addCustomHeaderFromExistingHeader("longstring", "longstring_spacemr_space_people_book_transactions");
      AppListTableMapper
         .addCustomHeaderFromExistingHeader("string", "string_spacemr_space_people_book_repetition");
   }


   @Transactional
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_insert_get_data")
   public void spacemr_space_people_book_insert_get_data(HttpSession session
                                                         , HttpServletResponse response
                                                         , @RequestParam("content") String scontent
                                                         ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      //-
      //-
      String current_username = appSessionTools.getUser_name(session);
      JSONObject current_people = spacemr_people.getByUsername(current_username);
      // System.out.println("current_people.toString(2): " + current_people.toString(2));
      if (current_people == null) {
         status = "People ["+current_username+"] not found"
            + " - please ask the administrator to add your user in the database"
            ;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
      }
      rv.put("current_people", current_people);
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println("content: " + content.toString(2));
      //-
      String s = content.optString("spacemr_people_id", "");
      if (!"".equals(s)) {
         rv.put("spacemr_people", spacemr_people.get(Integer.parseInt(s)));
      } else {
         rv.put("spacemr_people", current_people);
      }
      //-
      s = content.optString("spacemr_space_id", "");
      if (!"".equals(s)) {
         rv.put("spacemr_space", spacemr_space.get(Integer.parseInt(s)));
      }
      //-
      //-
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
   /**
    *  http://localhost:8080/spacemr_space_people_book/spacemr_space_people_book_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_insert")
   public void spacemr_space_people_book_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_insert")) != null) {
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
      String current_username = appSessionTools.getUser_name(session);
      JSONObject current_people = spacemr_people.getByUsername(current_username);
      if (content.getInt("spacemr_responsible_id") !=
          current_people.getInt("spacemr_people_id")) {
         if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_admin")) != null) {
            status = "permissionDenied " + status;
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      Workflow workflow = workflowManager
         .getWorkflow(spacemr_space_people_book.getWorkflowId(), appSessionTools, session);
      //-
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_admin")) != null) {
         //- check role
         JSONArray bookers_roles = workflow.getAuxData().getJSONArray("bookers_roles");
         String role = current_people.getString("role");
         boolean found = false;
         for (int i=0; i<bookers_roles.length() && !found; i++){
            if (bookers_roles.getString(i).equals(role)) {
               found = true;
            }
         }
         if (!found) {
            status = "permissionDenied " + status;
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      content.put("stato", workflow.getDefaultStatus());
      //-
      int id = spacemr_space_people_book.insert(content);
      app_log.writeLog(spacemr_space_people_book, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_space_people_book_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   /**
    * returns an array containing all workflows used
    * to be cached on browser.
    */
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_workflows/**")
   public void spacemr_space_people_book_workflows(HttpSession session
                                            , HttpServletResponse response
                                            , @RequestParam("content") String sqparams
                                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_workflow_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      // System.out.println("current_people.toString(2): " + current_people.toString(2));
      String current_username = appSessionTools.getUser_name(session);
      JSONObject current_people = spacemr_people.getByUsername(current_username);
      if (!current_username.equals("anonymous")
          && current_people == null
          ) {
         status = "People ["+current_username+"] not found"
            + " - please ask the administrator to add your user in the database"
            ;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //
      JSONObject workflows = new JSONObject();
      //-
      String workflowid = spacemr_space_people_book.getWorkflowId();
      JSONObject workflow =
         workflowManager.getWorkflow(workflowid, appSessionTools, session).toJson();
      workflows.put(workflowid, workflow);
      //-
      rv.put("workflows",workflows);
      //-
      rv.put("current_people", current_people);
      //-
      status = "ok";
      //-
      // System.out.println(" --- returning\n"+rv.toString(2));
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   //-
   /**
    */
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_list/**")
   public void spacemr_space_people_book_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_read")) != null) {
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
      Vector<String> checkVector    = (Vector<String>)spacemr_space_people_book.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_space_people_book"
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
      qtm.setCustomHeaderAndAddColumn("spacemr_space_people_book_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_in_map_id_default", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_people_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_responsible_id", "id");
      qtm.setCustomHeaderAndAddColumn("stato_hidden", "string_hidden");
      qtm.setCustomHeaderAndAddColumn("responsible_username_hidden", "string_hidden");
      qtm.setCustomHeader("transactions","longstring_spacemr_space_people_book_transactions");
      qtm.setCustomHeader("repetition","string_spacemr_space_people_book_repetition");
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
      qtm.checkColumns(spacemr_space_people_book.getComputedColumnsMap(), "spacemr_space_people_book");
      //-
      //- where
      //-
      {
         String username = null;
         if(qtm.getQwhereParamAsObject("this_username") != null) {
            if (qtm.getQwhere().getBoolean("this_username")) {
               username = appSessionTools.getUser_name(session);
            }
         } 
         if(qtm.getQwhereParamAsObject("username") != null) {
            username = qtm.getQwhere().getString("username");
         }
         if (username != null) {
            String keyr = "wc__current_usernamer";
            String keyp = "wc__current_usernamep";
            qtm.addAnd(" ( spacemr_people__responsible.username = :"+keyr+"  or "
                       + " spacemr_people.username = :"+keyp+")");
            qtm.addQswhere(keyr, username);
            qtm.addQswhere(keyp, username);
         }
      }
      // if ((appSessionTools.hasPermission(session, "db_spacemr_space_people_book_admin")) != null) {
      //    String current_username = appSessionTools.getUser_name(session);
      //    String keyr = "wc__current_usernamer";
      //    String keyp = "wc__current_usernamep";
      //    qtm.addAnd(" ( spacemr_people__responsible.username = :"+keyr+"  or "
      //               + " spacemr_people.username = :"+keyp+")");
      //    qtm.addQswhere(keyr, current_username);
      //    qtm.addQswhere(keyp, current_username);
      // }
      String current_username = appSessionTools.getUser_name(session);
      //-
      {
         // qtm.setWhereIntegerEqual("spacemr_space_people_book_super_id",  "spacemr_space_people_book.spacemr_space_people_book_super_id");
         //- spacemr_space_id non va messo! ci pensa "recursive_space"
         //- qtm.setWhereIntegerEqual("spacemr_space_id",   "spacemr_space_people_book.spacemr_space_id");
         qtm.setWhereIntegerEqual("spacemr_people_id",  "spacemr_space_people_book.spacemr_people_id");
         qtm.setWhereIntegerEqual("spacemr_responsible_id",  "spacemr_space_people_book.spacemr_responsible_id");
         qtm.setWhereWorkflow("stato",  "spacemr_space_people_book.stato"
                              , workflowManager.getWorkflow(spacemr_space_people_book.getWorkflowId(), appSessionTools, session));
         qtm.setWhereStringLike("spacemr_people_username",  "spacemr_people.username");
         qtm.setWhereStringLike("spacemr_people_first_name",  "spacemr_people.first_name");
         qtm.setWhereStringLike("spacemr_people_last_name",  "spacemr_people.last_name");
         qtm.setWhereStringLike("spacemr_people_role",  "spacemr_people.role");
         qtm.setWhereStringLike("spacemr_responsible_username",  "spacemr_people__responsible.username");
         qtm.setWhereStringLike("spacemr_responsible_first_name",  "spacemr_people__responsible.first_name");
         qtm.setWhereStringLike("spacemr_responsible_last_name",  "spacemr_people__responsible.last_name");
         qtm.setWhereStringLike("spacemr_responsible_role",  "spacemr_people__responsible.role");
         qtm.setWhereStringLike("spacemr_space_code",  "spacemr_space.code");
         qtm.setWhereStringLike("spacemr_space_description",  "spacemr_space.description");
         qtm.setWhereInteger("spacemr_responsible_id",  "spacemr_space_people_book.spacemr_responsible_id");
         qtm.setWhereStringLike("reason",  "spacemr_space_people_book.reason");
         qtm.setWhereInteger(   "people_number",  "spacemr_space_people_book.people_number");
         qtm.setWhereDate(      "date_from",  "spacemr_space_people_book.date_from");
         qtm.setWhereDate(      "date_to",  "spacemr_space_people_book.date_to");
         qtm.setWhereStringLike("stato",  "spacemr_space_people_book.stato");
         qtm.setWhereInIds("spacemr_space_people_book_ids",  "spacemr_space_people_book.spacemr_space_people_book_id");
         //-
         boolean recursive_space = false;
         if(qtm.getQwhereParamAsObject("recursive_space") == null) {
         } else {
            recursive_space = qtm.getQwhere().getBoolean("recursive_space");
         }
         if (!recursive_space) {
            qtm.setWhereIntegerEqual("spacemr_space_id",  "spacemr_space_people_book.spacemr_space_id");
         } else {
            String wc = "wc__spacemr_space_id";
            int spacemr_space_id = qtm.getQwhere().getInt("spacemr_space_id");
            qtm.addQswhere(wc, spacemr_space_id);
            qtm.addAnd("spacemr_space_people_book.spacemr_space_id in ("
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
         //-
         {
            String     key = "current_date";
            String v = (String)qtm.getQwhereParamAsObject(key);
            if (v != null) {
               Date d = new Date(Long.parseLong(v));
               String wc1 = "wc__1_"+key;
               qtm.addQswhere(wc1, d);
               String wc2 = "wc__2_"+key;
               qtm.addQswhere(wc2, d);
               String wc3 = "wc__3_"+key;
               qtm.addQswhere(wc3, d);
               //-
               qtm.addAnd("isNull(spacemr_space_people_book.date_to)   or spacemr_space_people_book.date_to   >= :"+wc1+"");
               qtm.addAnd("isNull(spacemr_space_people_book.date_from) or spacemr_space_people_book.date_from <= :"+wc2+"");
               qtm.addAnd("(repetition = 'd' or (DAYOFWEEK(:"+wc3+") = DAYOFWEEK(date_from)))");
            }
         }
         {
            String     key = "ccd_initial";
            String v = (String)qtm.getQwhereParamAsObject(key);
            if (v != null) {
               Date d = new Date(Long.parseLong(v));
               String wc = "wc__"+key;
               qtm.addQswhere(wc, d);
               qtm.addAnd("isNull(spacemr_space_people_book.date_to) or spacemr_space_people_book.date_to >= :"+wc+"");
            }
         }
         {
            String     key = "ccd_final";
            String v = (String)qtm.getQwhereParamAsObject(key);
            if (v != null) {
               Date d = new Date(Long.parseLong(v));
               String wc = "wc__"+key;
               qtm.addQswhere(wc, d);
               qtm.addAnd("isNull(spacemr_space_people_book.date_from) or spacemr_space_people_book.date_from <= :"+wc+"");
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
      qs.append("\n from spacemr_space_people_book"
                + "   left join spacemr_space  on spacemr_space_people_book.spacemr_space_id  = spacemr_space.spacemr_space_id"
                + "   left join spacemr_people on spacemr_space_people_book.spacemr_people_id = spacemr_people.spacemr_people_id"
                + "   left join spacemr_people as spacemr_people__responsible on spacemr_space_people_book.spacemr_responsible_id = spacemr_people__responsible.spacemr_people_id" 
                );
      // qs.append("\n ,    spacemr_space_people_book_super");
      // qtm.addAnd(" spacemr_space_people_book_super.spacemr_space_people_book_super_id = spacemr_space_people_book.spacemr_space_people_book_super_id");
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

   /**
    *
    */
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_list_sitting/**")
   public void spacemr_space_people_book_list_sitting(HttpSession session
                                                     , HttpServletResponse response
                                                     , @RequestParam("content") String sqparams
                                                     ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      //-
      {
         //-
         //- all the bookings for the given space on the given date
         //-
         HashMap<String, Object> qsargs = new HashMap<String, Object>();
         StringBuffer                qs = new StringBuffer();
         //-
         qs.append(""
                   + "\n  select spacemr_space_people_book_id"
                   + "\n       , spacemr_space_id"
                   //-qui- remove the following useless line
                   + "\n       , (select code from spacemr_space where spacemr_space.spacemr_space_id = spacemr_space_people_book.spacemr_space_id) as code"
                   + "\n       , stato"
                   + "\n       , date_from"
                   + "\n       , date_to"
                   + "\n       , repetition "
                   + "\n       , people_number "
                   + "\n    from spacemr_space_people_book"
                   + "\n            where"
                   + "\n             spacemr_space_id in ("
                   + "\n               with recursive root_spaces (spacemr_space_id, spacemr_space_in_id) as ("
                   + "\n                 select     a.spacemr_space_id,"
                   + "\n                            a.spacemr_space_in_id"
                   + "\n                   from     spacemr_space as a"
                   + "\n                  where     a.spacemr_space_id = :spacemr_space_id"
                   + "\n                 union all"
                   + "\n                 select     p.spacemr_space_id,"
                   + "\n                            p.spacemr_space_in_id"
                   + "\n                   from     spacemr_space as p"
                   + "\n                          , root_spaces as r"
                   + "\n                  where     p.spacemr_space_in_id = r.spacemr_space_id"
                   + "\n               )"
                   + "\n               select spacemr_space_id from root_spaces"
                   + "\n               )"
                   );
         //-
         qsargs.put("spacemr_space_id", qparams.getInt("spacemr_space_id"));
         //-
         {
            String     key = "ccd_initial";
            Long v = (Long)qparams.opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               qs.append("\n and (isNull(spacemr_space_people_book.date_to) or spacemr_space_people_book.date_to >= :"+key+")");
               qsargs.put(key, d);
            }
         }
         {
            String     key = "ccd_final";
            Long v = (Long)qparams.opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               qs.append("\n and (isNull(spacemr_space_people_book.date_from) or spacemr_space_people_book.date_from <= :"+key+")");
               qsargs.put(key, d);
            }
         }
         {
            String     key = "current_date";
            Long v = (Long)qparams.opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               String wc1 = "wc__1_"+key;
               qsargs.put(wc1, d);
               String wc2 = "wc__2_"+key;
               qsargs.put(wc2, d);
               String wc3 = "wc__3_"+key;
               qsargs.put(wc3, d);
               //-
               qs.append("\n and (isNull(spacemr_space_people_book.date_to)   or spacemr_space_people_book.date_to   >= :"+wc1+")");
               qs.append("\n and (isNull(spacemr_space_people_book.date_from) or spacemr_space_people_book.date_from <= :"+wc2+")");
               qs.append("\n and ((repetition = 'd' or (DAYOFWEEK(:"+wc3+") = DAYOFWEEK(date_from))))");
            }
         }
         //-
         // System.out.println(" ---sitting--- qs: " + qs + "\n qsargs: " + qsargs);
         //-
         AppListTableMapper mapper = new AppListTableMapper("obj");
         namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
         //-
         rv.put("spacemr_space_people_books", mapper.getJSON());
      }
      {
         //-
         //- tree of objects for the given space, this is time independent
         //- these information could be optimized e.g. cached in the javascript.
         //-
         HashMap<String, Object> qsargs = new HashMap<String, Object>();
         StringBuffer                qs = new StringBuffer();
         //-
         qs.append(""
                   + "\n   select spacemr_space_id"
                   //-qui- debug remove "code"
                   + "\n        , code"
                   + "\n        , number_of_seating"
                   + "\n        , number_of_seating_booking"
                   + "\n        , spacemr_space_in_id"
                   + "\n     from spacemr_space"
                   + "\n             where"
                   + "\n              spacemr_space_id in ("
                   + "\n                with recursive root_spaces (spacemr_space_id, spacemr_space_in_id) as ("
                   + "\n                  select     a.spacemr_space_id,"
                   + "\n                             a.spacemr_space_in_id"
                   + "\n                    from     spacemr_space as a"
                   + "\n                   where     a.spacemr_space_id = :spacemr_space_id"
                   + "\n                  union all"
                   + "\n                  select     p.spacemr_space_id,"
                   + "\n                             p.spacemr_space_in_id"
                   + "\n                    from     spacemr_space as p"
                   + "\n                           , root_spaces as r"
                   + "\n                   where     p.spacemr_space_in_id = r.spacemr_space_id"
                   + "\n                )"
                   + "\n                select spacemr_space_id from root_spaces"
                   + "\n                )"
                   );
         //-
         qsargs.put("spacemr_space_id", qparams.getInt("spacemr_space_id"));
         //-
         // System.out.println(" ---sitting--- qs: " + qs + "\n qsargs: " + qsargs);
         //-
         AppListTableMapper mapper = new AppListTableMapper("obj");
         namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
         //-
         rv.put("spacemr_spaces", mapper.getJSON());
      }
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   //-
   /**
    *  http://localhost:8080/spacemr_space_people_book/spacemr_space_people_book_get?content={"spacemr_space_people_book_id", "1"}
    */
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_get")
   public void spacemr_space_people_book_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_read")) != null) {
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
      int spacemr_space_people_book_id = content.getInt("spacemr_space_people_book_id");
      //-
      JSONObject obj = spacemr_space_people_book.get(spacemr_space_people_book_id);
      JSONObject jspacemr_people      = spacemr_people.get(obj.getInt("spacemr_people_id"));
      JSONObject jspacemr_responsible = spacemr_people.get(obj.getInt("spacemr_responsible_id"));
      String current_username = appSessionTools.getUser_name(session);
      // if ((!current_username.equals(jspacemr_responsible.getString("username"))
      //      && !current_username.equals(jspacemr_people.getString("username"))
      //      )
      //     && (status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_admin")) != null) {
      //    status = "permissionDenied " + status;
      //    appControllerTools.appDoRequestMappingResponse(response, status, rv);
      //    return;
      // }
      //-
      Workflow wf =
         workflowManager.getWorkflow(spacemr_space_people_book
                                     .getWorkflowId(), appSessionTools, session);
      wf.per_field_hideUnreadable(obj.getString("stato"), obj);
      //-
      rv.put("obj",obj);
      //-
      rv.put("spacemr_people",jspacemr_people);
      rv.put("spacemr_responsible",jspacemr_responsible);
      rv.put("spacemr_space",spacemr_space.get(obj.getInt("spacemr_space_id")));
      
      // JSONObject spacemr_space_people_book_superj = spacemr_space_people_book_super.get(obj.getInt("spacemr_space_people_book_super_id"));
      // rv.put("spacemr_space_people_book_super",spacemr_space_people_book_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-

   //-
   //- permissin checks
   //-
   private boolean spacemr_space_people_book_update_userCanUpdate(HttpSession session
                                                                  , HttpServletResponse response
                                                                  , JSONObject old_obj
                                                                  , JSONObject content
                                                                  , JSONObject rv
                                                                  ) throws Exception {
      String status = null;
      //-
      //-
      //- workflow transaction ckecks
      //-
      Workflow workflow = workflowManager
         .getWorkflow(spacemr_space_people_book.getWorkflowId(), appSessionTools, session);
      String stato_corrente = old_obj.getString("stato");
      String stato_nuovo    = content.getString("stato");
      String get_wf_permission = null;
      if (!stato_corrente.equals(stato_nuovo)) {
         get_wf_permission = workflow.getPermission(stato_corrente, stato_nuovo);
         //-
         // System.out.println(" -- get_wf_permission: " + get_wf_permission);
         if (get_wf_permission == null) {
            status = "status transaction not found from ["+stato_corrente+"] to ["+stato_nuovo+"]";
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return(false);
         }
      }
      if (appSessionTools.hasPermission(session, "db_spacemr_space_people_book_admin") == null) {
         // ok - it is the administrator
      } else {
         String current_username = appSessionTools.getUser_name(session);
         JSONObject current_people = spacemr_people.getByUsername(current_username);
         //-
         //- only responsibles can update a record.
         //-
         if (content.getInt("spacemr_responsible_id") !=
             current_people.getInt("spacemr_people_id")) {
            status = "permissionDenied " + status;
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return(false);
         }
      }
      //-
      //- user must have the transaction permission
      //-
      if ((get_wf_permission != null)
          && (status = appSessionTools.hasPermission(session, get_wf_permission)) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return(false);
      }
      //-
      //- single field permissions check
      //-
      workflow.per_field_restoreUnwritable(stato_corrente, content, old_obj);
      //-
      workflow.run_actions(stato_corrente, stato_nuovo, content);
      //-
      return(true);
   }
   
   @Transactional
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_update")
   public void spacemr_space_people_book_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_update")) != null) {
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
      int spacemr_space_people_book_id = content.getInt("spacemr_space_people_book_id");
      JSONObject old_obj = spacemr_space_people_book.get(spacemr_space_people_book_id);
      //-
      //-
      if (!spacemr_space_people_book_update_userCanUpdate(session,response,old_obj,content,rv)) {
         return;
      }
      //-
      app_log.writeLog(spacemr_space_people_book, spacemr_space_people_book_id, appSessionTools.getUser_name(session),content);
      //-
      spacemr_space_people_book.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   @Transactional
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_do_transaction")
   public void spacemr_space_people_book_do_transaction(HttpSession session
                                                        , HttpServletResponse response
                                                        , @RequestParam("content") String scontent
                                                        ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      JSONObject content_tansaction = new JSONObject(scontent);
      //-
      int spacemr_space_people_book_id = content_tansaction.getInt("spacemr_space_people_book_id");
      JSONObject old_obj               = spacemr_space_people_book.get(spacemr_space_people_book_id);
      JSONObject content               = spacemr_space_people_book.get(spacemr_space_people_book_id);
      //-
      content.put("stato", content_tansaction.getString("stato"));
      String tnota = content_tansaction.getString("nota");
      content.put("nota",
                  ""
                  + appSessionTools.getUser_name(session)
                  + " - " + Tools.getStringTimeStamp(new Date())
                  + " - " + tnota
                  + " - " + content.getString("stato")
                  + "\n" + content.getString("nota")
                  );
      //-
      if (!spacemr_space_people_book_update_userCanUpdate(session,response,old_obj,content,rv)) {
         return;
      }
      //-
      app_log.writeLog(spacemr_space_people_book, spacemr_space_people_book_id, appSessionTools.getUser_name(session),content);
      //-
      spacemr_space_people_book.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
   //-
   /**
    *  http://localhost:8080/spacemr_space_people_book/spacemr_space_people_book_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_delete")
   public void spacemr_space_people_book_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_delete")) != null) {
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
      int spacemr_space_people_book_id = content.getInt("spacemr_space_people_book_id");
      spacemr_space_people_book.delete(spacemr_space_people_book_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/spacemr_space_people_book/spacemr_space_people_book_logs
    */
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_logs")
   public void spacemr_space_people_book_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      //-
      //- custom headers management
      JSONObject customHeaders = new JSONObject();
      customHeaders.put("parameter_values","longstring_spacemr_space_people_book_links");
      qparams.put("customHeaders", customHeaders);
      //-
      int id = qparams.getInt("spacemr_space_people_book_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_space_people_book
                       , id
                       );
   }

   public JSONObject cloneJSONObject(JSONObject obj) throws Exception {
      JSONObject rv = new JSONObject();
      String names[] = JSONObject.getNames(obj);
      for (String n: names){
         rv.put(n, obj.get(n));
      }
      return(rv);
   }
   
   /**
    *  http://localhost:8080/spacemr_space_people_book/spacemr_space_people_cut_date
    */
   @Transactional
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_cut_date")
   public void spacemr_space_people_book_cut_date(HttpSession session
                                                  , HttpServletResponse response
                                                  , @RequestParam("content") String scontent
                                                  ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_update")
           ) != null) {
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
      int spacemr_space_people_book_id = content.getInt("spacemr_space_people_book_id");
      long date_cut_from = content.getLong("current_date");
      long date_cut_to   = date_cut_from;   // predisposto per tagliare piu' di un giorno.
      //-
      JSONObject obj = spacemr_space_people_book.get(spacemr_space_people_book_id);
      //-
      if (!spacemr_space_people_book_update_userCanUpdate(session,response,obj,obj,rv)) {
         return;
      }
      //-
      //-
      JSONObject params = new JSONObject();
      JSONObject objNew = null;
      params.put("obj", obj);
      params.put("objNew", objNew);
      params.put("date_cut_from", date_cut_from);
      params.put("date_cut_to", date_cut_to);
      //-
      String error_message = spacemr_space_people_book_cut_date_doCut(params);
      //-
      //-
      // in very case I update the obj
      //-
      obj    = params.optJSONObject("obj");
      objNew = params.optJSONObject("objNew");
      //-
      params.put("objNew", objNew);
      if (obj != null) {
         status = "ok";
         app_log.writeLog(spacemr_space_people_book, spacemr_space_people_book_id, appSessionTools.getUser_name(session),obj);
         spacemr_space_people_book.update(obj);
         //-
         if (objNew != null) {
            //-
            //- creating the other part of the booking
            //-
            objNew.remove("spacemr_space_people_book_id");
            String nota = objNew.optString("nota", "");
            nota = "- cut from spacemr_space_people_book_id="
               + obj.getInt("spacemr_space_people_book_id")
               + "\n"
               + nota
               ;
            objNew.put("nota", nota);
            int id = spacemr_space_people_book.insert(objNew);
            app_log.writeLog(spacemr_space_people_book, id
                             , appSessionTools.getUser_name(session)
                             , objNew);
         }
      } else {
         status = "error - something went wrong processing the 'cut' - " + error_message;
      }
      //-
      //-
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   /**
    *  http://localhost:8080/spacemr_space_people_book/spacemr_space_people_book_split_in_weekly
    */
   @Transactional
   @RequestMapping(value="spacemr_space_people_book/spacemr_space_people_book_split_in_weekly")
   public void spacemr_space_people_book_split_in_weekly(
                 HttpSession session
                 , HttpServletResponse response
                 , @RequestParam("content") String scontent
                 ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_people_book_update")
           ) != null) {
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
      int spacemr_space_people_book_id = content.getInt("spacemr_space_people_book_id");
      //-
      JSONObject obj = spacemr_space_people_book.get(spacemr_space_people_book_id);
      //-
      if (!spacemr_space_people_book_update_userCanUpdate(session,response,obj,obj,rv)) {
         return;
      }
      //- checks on repetition
      if (!obj.getString("repetition").equals("d")) {
         status = "error - repetition must be daily";
      }
      if (obj.getLong("date_to") < (obj.getLong("date_from") + (1000 * 60 * 60 * (3 + 24 * 7))) ) {
         status = "error - repetition must be of at least one week";
      }
      //-
      if (status == null) {
         //-
         //- updating the original object
         //-
         int weekdays = 0;
         //-
         Date date_from = new Date(obj.getLong("date_from"));
         Date date_to   = new Date(obj.getLong("date_to"));
         Calendar c = Calendar.getInstance();
         c.setTime(date_from);
         while(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
               || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) {
            c.add(Calendar.DATE, 1);
            weekdays = weekdays + 1;
         }
         if (weekdays > 0) {
            obj.put("date_from", c.getTime().getTime());
         }
         //-
         obj.put("repetition","w");
         app_log.writeLog(spacemr_space_people_book, spacemr_space_people_book_id, appSessionTools.getUser_name(session),obj);
         spacemr_space_people_book.update(obj);
         //-
         //-
         //- creating new bookings
         //-
         //-
         String nota = obj.optString("nota", "");
         nota = "- split from spacemr_space_people_book_id="
            + spacemr_space_people_book_id + "\n"
            + nota
            ;
         obj.put("nota", nota);
         //-
         while(weekdays<6){
            c.add(Calendar.DATE, 1);
            if (!(c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
               || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )){
               obj.put("date_from", c.getTime().getTime());
               obj.put("date_to",   date_to.getTime());
               int id = spacemr_space_people_book.insert(obj);
               app_log.writeLog(spacemr_space_people_book, id
                                , appSessionTools.getUser_name(session)
                                , obj);
            }
            weekdays = weekdays + 1;
         }
      }
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
public String spacemr_space_people_book_cut_date_doCut(JSONObject params) throws Exception {
   String rv = "";
   // System.out.println(" -- input params:\n" + params.toString(2));
   //- see test class
   //-   src/test/java/it/unimo/app/controller/Spacemr_space_people_bookController__Test.java
   //-
   JSONObject obj     = params.getJSONObject("obj");
   JSONObject objNew  = params.optJSONObject("objNew");
   long date_cut_to   = params.getLong("date_cut_to");
   long date_cut_from = params.getLong("date_cut_from");
   //-
   // System.out.println(" -- cutting! " + obj.toString(2));
   //-
   if (obj.getLong("date_to") >  obj.getLong("date_from")
       && obj.getLong("date_to") >=  date_cut_to
       && date_cut_to >=  date_cut_from
       && date_cut_from >= obj.getLong("date_from")
       && !(obj.getLong("date_to") ==  date_cut_to && date_cut_from == obj.getLong("date_from"))
       ){
      //-
      //- number of days to cut
      int nodtc = -1;
      if (obj.getString("repetition").equals("d")){
         //- ok
         nodtc=1;
      } else if (obj.getString("repetition").equals("w")){
         //-
         SimpleDateFormat sdf = new SimpleDateFormat("EEE");
         String dow_date_from = sdf.format(new Date(obj.getLong("date_from")));
         //- date_cut_to and from must be the same day of week of the start date
         if (dow_date_from.equals(sdf.format(new Date(date_cut_from)))
             && dow_date_from.equals(sdf.format(new Date(date_cut_to)))
             ) {
            //-ok
            nodtc=7;
         } else {
            //- no changes
            obj = null;
            rv = "one of the cut_date is not on the same day of week of the date_from";
         }
      } else {
         rv = "unknown type of repetition ["+obj.getString("repetition")+"]";
         obj = null;
      }
      if (obj != null) {
         if (obj.getLong("date_from") ==  date_cut_from){
            //-
            //- head
            //-
            obj.put("date_from"
                    , tools.date_floor_on_day(new Date(date_cut_to + (nodtc*24+2)*60*60*1000)).getTime()
                    );
         } else if (obj.getLong("date_to") ==  date_cut_to) {
            //-
            //- tail
            //-
            obj.put("date_to"
                    , tools.date_floor_on_day(new Date(date_cut_from - (nodtc*24-2)*60*60*1000)).getTime()
                    );
         } else {
            //-
            //- middle
            //-
            objNew = cloneJSONObject(obj);
            objNew.put("date_from"
                    , tools.date_floor_on_day(new Date(date_cut_to + (nodtc*24+2)*60*60*1000)).getTime()
                    );
            obj.put("date_to"
                    , tools.date_floor_on_day(new Date(date_cut_from - (nodtc*24-2)*60*60*1000)).getTime()
                    );
         }
      }
      if (obj != null
          && obj.getLong("date_to") <  obj.getLong("date_from")) {
         //- undo
         obj = null;
         rv = "inchoerent resulting range";
      }
      //-
   } else {
      obj = null;
      rv = "inchoerent input ranges";
   }
   //-
   params.put("obj", obj);
   params.put("objNew", objNew);
   //-
   // System.out.println("cut output:\n" + params.toString(2));
   return(rv);
}

   
}
