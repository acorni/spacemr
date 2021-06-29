package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppPermissionTools_userData;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import it.unimo.app.tools.Spacemr_user_white_list_cache;
import it.unimo.app.tools.Spacemr_user_white_list_parser;
import java.util.Calendar;
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
public class Spacemr_space_user_presenceController {
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
   private Spacemr_space_user_presence spacemr_space_user_presence;
   // @Autowired
   // private Spacemr_space_user_presence_super spacemr_space_user_presence_super;
   //-
   @Autowired
   private Spacemr_space spacemr_space;
   @Autowired
   private App_user app_user;
   @Autowired
   private Spacemr_user_white_list_cache spacemr_user_white_list_cache;


   @Transactional
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_insert_get_data")
   public void spacemr_space_user_presence_insert_get_data(HttpSession session
                                                         , HttpServletResponse response
                                                         , @RequestParam("content") String scontent
                                                         ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      //-
      String current_username = appSessionTools.getUser_name(session);
      JSONObject current_user = app_user.get(current_username);
      // System.out.println("current_user.toString(2): " + current_user.toString(2));
      if (current_user == null) {
         status = "User ["+current_username+"] not found"
            + " - please report this fact to this application administrator"
            ;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
      }
      rv.put("current_user", current_user);
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println("content: " + content.toString(2));
      //-
      String s = content.optString("spacemr_space_id", "");
      if (!"".equals(s)) {
         rv.put("spacemr_space", spacemr_space.get(Integer.parseInt(s)));
      }
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   @Transactional
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_get_check_info")
   public void spacemr_space_user_presence_get_check_info(HttpSession session
                                                          , HttpServletResponse response
                                                          , @RequestParam("content") String scontent
                                                          ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println("content: " + content.toString(2));
      //-
      //- inputs
      //-
      int  app_user_id      = content.getInt("app_user_id");
      Date current_date     = new Date(content.getLong("current_date"));
      //-
      //- space related - space limits of the ancestors
      //-
      int  spacemr_space_id = content.optInt("spacemr_space_id", -1);
      //-
      rv.put("user_can_insert", true);
      if (spacemr_space_id > 0) {
         JSONObject space = spacemr_space.get(spacemr_space_id);
         if (appSessionTools.hasPermission(session
                                           , space.getString("app_group_name")
                                           , "db_spacemr_space_user_presence_insert_in_any_space") != null) {
            Spacemr_user_white_list_parser p =
               spacemr_user_white_list_cache.getParser(spacemr_space_id);
            if (!p.userCanAccess(appSessionTools.getUser_name(session), new Date())) {
               AppPermissionTools_userData userData = appSessionTools.getUserData(session);
               status = "permissionDenied " + userData.gRb("db.spacemr_space.user_white_list.permission_denied");
               rv.put("user_can_insert", false);
               rv.put("user_can_insert_reason", status);
            }
         }
         rv.put("spacemr_space_ancestors_with_presence"
                , spacemr_space_user_presence
                .get_ancestors_with_presence(spacemr_space_id, current_date));
         //-
         //-
         //- user related - existing presences in the space
         //-
         int user_presence_count = spacemr_space_user_presence
            .get_user_presence_sum(app_user_id, spacemr_space_id, current_date);
         rv.put("user_presence_count" , user_presence_count);
         if (user_presence_count > 0 && (
                                         (status =
                                          appSessionTools.hasPermission(session
                                           , space.getString("app_group_name")
                                           , "db_spacemr_space_user_presence_insert_multiple_time_in_space")
                                          ) != null
                                         )) {
            rv.put("user_can_insert", false);
            rv.put("user_can_insert_reason", status);
         }
         //-
      } else {
         if ((status = appSessionTools.hasPermission(session
                                           , "db_spacemr_space_user_presence_insert_in_any_space"
                                           )) != null) {
            rv.put("user_can_insert", false);
            rv.put("user_can_insert_reason", status);
         }
      }
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   
   /**
    *  http://localhost:8080/spacemr_space_user_presence/spacemr_space_user_presence_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_insert")
   public void spacemr_space_user_presence_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int spacemr_space_id = content.getInt("spacemr_space_id");
      JSONObject space = spacemr_space.get(spacemr_space_id);
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , space.getString("app_group_name")
                                                  , "db_spacemr_space_user_presence_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      if ((status = appSessionTools.hasPermission(session
                                        , space.getString("app_group_name")
                                        , "db_spacemr_space_user_presence_insert_in_any_space")) != null) {
         Spacemr_user_white_list_parser p =
            spacemr_user_white_list_cache.getParser(spacemr_space_id);
         if (!p.userCanAccess(appSessionTools.getUser_name(session), new Date())) {
            AppPermissionTools_userData userData = appSessionTools.getUserData(session);
            status = "permissionDenied " + userData.gRb("db.spacemr_space.user_white_list.permission_denied");
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      //-
      if(appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_admin") != null) {
         //- no admin permission
         content.put("app_user_id", app_user.get(appSessionTools.getUser_name(session)).get("app_user_id"));
         //-
      }
      if(appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_insert_choose_date_time") != null) {
         //- can not choose the date
         long d = (new Date()).getTime();
         content.put("date_time", d);
         // System.out.println(" -- date: " + ( new Date(d)));
      }
      if(appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_insert_choose_people_number") != null) {
         //- db_spacemr_space_user_presence_insert_choose_people_number
         content.put("people_number", 1);
         //-
      }
      //-
      //-
      {
         //- can instert twice?
         int user_presence_count = spacemr_space_user_presence
            .get_user_presence_sum(content.getInt("app_user_id")
                                   , spacemr_space_id
                                   , new Date(content.getLong("date_time"))
                                   );
         // System.out.println(" -- user_presence_count: " + user_presence_count);
         if (user_presence_count > 0
             && (
                 (status =
                  appSessionTools.hasPermission(session
                                                , space.getString("app_group_name")
                                                , "db_spacemr_space_user_presence_insert_multiple_time_in_space")
                  ) != null
                 )) {
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      //- over_booking check
      //-
      // System.out.println(" -- over_booking check");
      if ((status = appSessionTools.hasPermission(session
                                        , space.getString("app_group_name")
                                        , "db_spacemr_space_user_presence_insert_over_booking")
           ) != null) {
         // System.out.println(" -- no db_spacemr_space_user_presence_insert_over_booking permission");
         int limit = space.optInt("number_of_seating_booking", -1);
         // System.out.println(" -- limit: "+limit);
         if (limit >= 0) {
            int presence_count =
               spacemr_space_user_presence
               .get_presence_sum(spacemr_space_id
                                 , new Date(content.getLong("date_time"))
                                 );
            // System.out.println(" -- presence count: " + presence_count);
            // System.out.println(" -- people_number:  " + content.getInt("people_number"));
            // System.out.println(" -- content.getInt(\"people_number\") +  presence_count: " + content.getInt("people_number") +  presence_count);
            if (content.getInt("people_number") +  presence_count > limit) {
               // System.out.println(" status: " + status);
               appControllerTools.appDoRequestMappingResponse(response, status, rv);
               return;
            }
         }
      }
      //-
      //-
      //- insert
      //-
      int id = spacemr_space_user_presence.insert(content);
      app_log.writeLog(spacemr_space_user_presence, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_space_user_presence_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   /**
    */
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_list/**")
   public void spacemr_space_user_presence_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_read")) != null) {
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
      Vector<String> checkVector    = (Vector<String>)spacemr_space_user_presence.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_space_user_presence"
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
      qtm.setCustomHeaderAndAddColumn("spacemr_space_user_presence_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_in_map_id_default", "id");
      qtm.setCustomHeaderAndAddColumn("app_user_id", "id");
      qtm.setCustomHeaderAndAddColumn("app_user_user_name_hidden", "string_hidden");
      //-
      //-
      AppListTableMapper mapper = null;
      AppPermissionTools_userData userData = appSessionTools.getUserData(session);
      if (outputtype.equals("csv")) {
         mapper = qtm.getMapperCsv(response.getWriter(), userData);
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
      qtm.checkColumns(spacemr_space_user_presence.getComputedColumnsMap(), "spacemr_space_user_presence");
      //-
      //- where
      //-
      if (
          (appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_admin") == null)
          || (appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_read_all") == null)
          ) {
         String user_name = null;
         if(qtm.getQwhereParamAsObject("this_user_name") != null) {
            if (qtm.getQwhere().getBoolean("this_user_name")) {
               user_name = appSessionTools.getUser_name(session);
            }
         } 
         if(qtm.getQwhereParamAsObject("user_name") != null) {
            user_name = qtm.getQwhere().getString("user_name");
         }
         if (user_name != null) {
            String key = "wc__current_user_name";
            qtm.addAnd(" ( app_user.user_name = :"+key+")");
            qtm.addQswhere(key, user_name);
         }
      } else {
         qtm.addAnd(" spacemr_space_user_presence.app_user_id = :app_user_id_no_other ");
         qtm.addQswhere("app_user_id_no_other", userData.getApp_user_id());
      }
      {
         // qtm.setWhereIntegerEqual("spacemr_space_user_presence_super_id",  "spacemr_space_user_presence.spacemr_space_user_presence_super_id");
         qtm.setWhereIntegerEqual("app_user_id",  "spacemr_space_user_presence.app_user_id");
         qtm.setWhereDate(      "date_time",  "spacemr_space_user_presence.date_time");
         qtm.setWhereInteger("people_number",  "spacemr_space_user_presence.people_number");
         qtm.setWhereStringLike("nota",  "spacemr_space_user_presence.nota");
         qtm.setWhereStringLike("app_user_user_name",  "app_user.user_name");
         qtm.setWhereStringLike("app_user_first_name",  "app_user.first_name");
         qtm.setWhereStringLike("app_user_last_name",  "app_user.last_name");
         qtm.setWhereStringLike("spacemr_space_code",  "spacemr_space.code");
         qtm.setWhereStringLike("spacemr_space_description",  "spacemr_space.description");
         qtm.setWhereInIds("spacemr_space_user_presence_ids",  "spacemr_space_user_presence.spacemr_space_user_presence_id");

         //-
         boolean recursive_space = false;
         if(qtm.getQwhereParamAsObject("recursive_space") == null) {
         } else {
            recursive_space = qtm.getQwhere().getBoolean("recursive_space");
         }
         if (!recursive_space) {
            qtm.setWhereIntegerEqual("spacemr_space_id",  "spacemr_space_user_presence.spacemr_space_id");
         } else {
            String wc = "wc__spacemr_space_id";
            int spacemr_space_id = qtm.getQwhere().getInt("spacemr_space_id");
            qtm.addQswhere(wc, spacemr_space_id);
            qtm.addAnd("spacemr_space_user_presence.spacemr_space_id in ("
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
         //-
         {
            String     key = "ccd_initial";
            Long v = (Long)qtm.getQwhere().opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               qtm.addQswhere(key, d);
               qtm.addAnd("spacemr_space_user_presence.date_time >= :"+key);
            }
         }
         {
            String     key = "ccd_final";
            Long v = (Long)qtm.getQwhere().opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               qtm.addQswhere(key, d);
               qtm.addAnd("spacemr_space_user_presence.date_time <= :"+key);
            }
         }
         {
            String     key = "current_date";
            Long v = (Long)qtm.getQwhere().opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               String wc1 = "wc__1_"+key;
               qtm.addQswhere(wc1, d);
               String wc2 = "wc__2_"+key;
               qtm.addQswhere(wc2, new Date(d.getTime() + 1000 * 24 * 60 * 60 - 1000));
               //-
               qtm.addAnd("(spacemr_space_user_presence.date_time   >= :"+wc1+")");
               qtm.addAnd("(spacemr_space_user_presence.date_time   <= :"+wc2+")");
            }
         }
         //-
      }
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from spacemr_space_user_presence"
                + "\n   left join spacemr_space  on spacemr_space_user_presence.spacemr_space_id  = spacemr_space.spacemr_space_id"
                + "\n   left join spacemr_space as spacemr_space_in"
                + "                              on spacemr_space_in.spacemr_space_id = spacemr_space.spacemr_space_in_id"
                + "\n   left join app_user       on spacemr_space_user_presence.app_user_id  = app_user.app_user_id"
                + "\n   left join app_group      on app_group.app_group_id                   = spacemr_space.app_group_id"
                );
      // qs.append("\n ,    spacemr_space_user_presence_super");
      // qtm.addAnd(" spacemr_space_user_presence_super.spacemr_space_user_presence_super_id = spacemr_space_user_presence.spacemr_space_user_presence_super_id");
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
    *  http://localhost:8080/spacemr_space_user_presence/spacemr_space_user_presence_get?content={"spacemr_space_user_presence_id", "1"}
    */
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_get")
   public void spacemr_space_user_presence_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_read")) != null) {
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
      int spacemr_space_user_presence_id = content.getInt("spacemr_space_user_presence_id");
      JSONObject obj = spacemr_space_user_presence.get(spacemr_space_user_presence_id);
      rv.put("obj",obj);
      if (obj == null) {
         status = "error - spacemr_space_user_presence ["+spacemr_space_user_presence_id+"] not found";
      } else {
         rv.put("spacemr_space",spacemr_space.get(obj.getInt("spacemr_space_id")));
         // JSONObject spacemr_space_user_presence_superj = spacemr_space_user_presence_super.get(obj.getInt("spacemr_space_user_presence_super_id"));
         // rv.put("spacemr_space_user_presence_super",spacemr_space_user_presence_superj);
         status = "ok";
      }
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   @Transactional
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_update")
   public void spacemr_space_user_presence_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      int spacemr_space_user_presence_id = content.getInt("spacemr_space_user_presence_id");
      //-
      JSONObject old = spacemr_space_user_presence.get(spacemr_space_user_presence_id);
      String app_group_name_new = null;
      String app_group_name_old = old.getString("app_group_name");
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , app_group_name_old
                                                  , "db_spacemr_space_user_presence_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      if (content.getInt("spacemr_space_id") == old.getInt("spacemr_space_id")) {
         app_group_name_new = app_group_name_old;
      } else {
         JSONObject space   = spacemr_space.get(content.getInt("spacemr_space_id"));
         app_group_name_new = space.getString("app_group_name");
         if ((status = appSessionTools.hasPermission(session
                                                     , app_group_name_new
                                                     , "db_spacemr_space_user_presence_update")) != null) {
            status = "permissionDenied " + status;
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      //-
      //-
      if(
         (appSessionTools.hasPermission(session, app_group_name_old, "db_spacemr_space_user_presence_admin") != null)
         || (appSessionTools.hasPermission(session, app_group_name_new, "db_spacemr_space_user_presence_admin") != null)
         ) {
         //- no admin permission - restore old values
         String fields[] = {"app_user_id", "spacemr_space_id", "date_time datetime", "people_number"};
         for (String s: fields) {
            content.put(s, old.opt(s));
         }
         //-
      }
      //-
      app_log.writeLog(spacemr_space_user_presence, spacemr_space_user_presence_id, appSessionTools.getUser_name(session),content);
      //-
      spacemr_space_user_presence.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   /**
    *  http://localhost:8080/spacemr_space_user_presence/spacemr_space_user_presence_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_delete")
   public void spacemr_space_user_presence_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_delete")) != null) {
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
      int spacemr_space_user_presence_id = content.getInt("spacemr_space_user_presence_id");
      spacemr_space_user_presence.delete(spacemr_space_user_presence_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   /**
    *  http://localhost:8080/spacemr_space_user_presence/spacemr_space_user_presence_logs
    */
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_logs")
   public void spacemr_space_user_presence_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("spacemr_space_user_presence_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_space_user_presence
                       , id
                       );
   }


   /**
    *
    */
   @RequestMapping(value="spacemr_space_user_presence/spacemr_space_user_presence_list_sitting/**")
   public void spacemr_space_user_presence_list_sitting(HttpSession session
                                                        , HttpServletResponse response
                                                        , @RequestParam("content") String sqparams
                                                        ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_user_presence_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      //-
      //-
      {
         //-
         //- all the bookings for the given space on the given date
         //-
         HashMap<String, Object> qsargs = new HashMap<String, Object>();
         StringBuffer                qs = new StringBuffer();
         //-
         qs.append(""
                   + "\n  select spacemr_space_id"
                   //-qui- remove the following useless line
                   // + "\n       , (select code from spacemr_space where spacemr_space.spacemr_space_id = spacemr_space_user_presence.spacemr_space_id) as code"
                   + "\n       , date(date_time) as date_time"
                   + "\n       , sum(people_number ) as people_number "
                   + "\n       , group_concat(spacemr_space_user_presence_id) as ids "
                   + "\n    from spacemr_space_user_presence"
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
               qs.append("\n and spacemr_space_user_presence.date_time >= :"+key);
               qsargs.put(key, d);
            }
         }
         {
            String     key = "ccd_final";
            Long v = (Long)qparams.opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               qs.append("\n and spacemr_space_user_presence.date_time <= :"+key);
               qsargs.put(key, d);
            }
         }
         {
            String     key = "current_date";
            Long v = (Long)qparams.opt(key);
            if (v != null) {
               Date d = new Date(v.longValue());
               d = tools.date_floor_on_day(d);
               String wc1 = "wc__1_"+key;
               qsargs.put(wc1, d);
               String wc2 = "wc__2_"+key;
               qsargs.put(wc2, new Date(d.getTime() + 1000 * 24 * 60 * 60 - 1000));
               //-
               qs.append("\n and (spacemr_space_user_presence.date_time   >= :"+wc1+")");
               qs.append("\n and (spacemr_space_user_presence.date_time   <= :"+wc2+")");
            }
         }
         qs.append("\n group by spacemr_space_id, date(date_time)");
         //-
         // System.out.println(" ---sitting--- qs: " + qs + "\n qsargs: " + qsargs);
         //-
         AppListTableMapper mapper = new AppListTableMapper("obj");
         namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
         //-
         // System.out.println(" ---spacemr_space_user_presences--- " + mapper.getJSON().toString(2));
         //-
         rv.put("spacemr_space_user_presences", mapper.getJSON());
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
   
}
