package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppPermissionTools_userData;
import it.unimo.app.tools.AppSessionTools;
import it.unimo.app.tools.Spacemr_user_white_list_parser;
import it.unimo.app.tools.Spacemr_user_white_list_cache;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//-
/*
   cd /dati/bin/workspace/tmp/mvc; gradle run
*/
@Controller
public class Spacemr_spaceController {
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
   private Spacemr_space spacemr_space;
   @Autowired
   private Spacemr_space_type spacemr_space_type;
   @Autowired
   private App_group app_group;
   @Autowired
   private DataSource dataSource;
   @Autowired
   private Spacemr_user_white_list_cache spacemr_user_white_list_cache;
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private App_user app_user;


   /**
    *  http://localhost:8080/spacemr_space/spacemr_space_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_space/spacemr_space_insert_get_data")
   public void spacemr_space_insert_get_data(HttpSession session
                                             , HttpServletResponse response
                                             , @RequestParam("content") String scontent
                                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println("content:" + content.toString(2));
      //-
      if (content.has("spacemr_space_in_id")) {
         int spacemr_space_id = content.getInt("spacemr_space_in_id");
         //-
         Vector<String> columns = new Vector<String>();
         columns.add("app_group_name");
         JSONObject obj = spacemr_space.get(spacemr_space_id, columns);
         //-
         if ((status = appSessionTools.hasPermission(session
                                                     , obj.getString("app_group_name")
                                                     , "db_spacemr_space_read")) != null) {
            status = "permissionDenied " + status;
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
         columns = get_columns_permitted(session, obj.getString("app_group_name"));
         obj = spacemr_space.get(spacemr_space_id, columns);
         rv.put("spacemr_space_in",obj);
         rv.put("ancestors", spacemr_space.getAncestors(obj, columns));
      }
      //-
      status = "ok";
      JSONArray user_groups =
         appSessionTools.groupsHavingThePermission(session, "db_spacemr_space_update");
      rv.put("user_groups",  user_groups);
      rv.put("space_types",  spacemr_space_type.getAllSpaceTypes());
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-

   
   /**
    *  http://localhost:8080/spacemr_space/spacemr_space_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_space/spacemr_space_insert")
   public void spacemr_space_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      //-
      JSONObject content = new JSONObject(scontent);
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , content.getString("app_group_name")
                                                  , "db_spacemr_space_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_group_id =
         app_group.get(content.getString("app_group_name")).getInt("app_group_id")
         ;
      content.put("app_group_id", app_group_id);
      //-
      if (content.getString("spacemr_space_in_id").toString().equals("")) {
         content.put("spacemr_space_in_id", (String)null);
      }
      int id = spacemr_space.insert(content);
      app_log.writeLog(spacemr_space, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_space_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   
   /**
    */
   @RequestMapping(value="spacemr_space/spacemr_space_list/**")
   public void spacemr_space_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      // if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_read")) != null) {
      //    status = "permissionDenied " + status;
      //    appControllerTools.appDoRequestMappingResponse(response, status, rv);
      //    return;
      // }
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
      Vector<String> checkVector    =
         (Vector<String>)get_columns_permittedOnAnyGroup(session).clone();
      checkVector.retainAll(spacemr_space.getColumnsForList());
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_space"
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
      qtm.setCustomHeaderAndAddColumn("spacemr_space_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_code", "string_hidden");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_map_id_default", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_in_map_id_default", "id");
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
      qtm.checkColumns(spacemr_space.getComputedColumnsMap(), "spacemr_space");
      {         
         qtm.setWhereIntegerEqual("spacemr_space_in_id", "spacemr_space.spacemr_space_in_id");
         qtm.setWhereInteger("app_group_id",  "spacemr_space.app_group_id");
         qtm.setWhereInteger("spacemr_space_type_id",  "spacemr_space.spacemr_space_type_id");
         qtm.setWhereInteger("spacemr_space_in_id",  "spacemr_space.spacemr_space_in_id");
         qtm.setWhereStringLike("code",  "spacemr_space.code");
         qtm.setWhereStringLike("description",  "spacemr_space.description");
         qtm.setWhereDecimal("area_in_meters2",  "spacemr_space.area_in_meters2");
         qtm.setWhereInteger("number_of_seating",  "spacemr_space.number_of_seating");
         qtm.setWhereStringLike("user_white_list",  "spacemr_space.user_white_list");
         qtm.setWhereStringLike("user_presence_progressive_code_map",  "spacemr_space.user_presence_progressive_code_map");
         qtm.setWhereStringLike("nota",  "spacemr_space.nota");
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
                
      qs.append("\n from spacemr_space left join spacemr_space as spacemr_space_in"
                + "       on spacemr_space_in.spacemr_space_id = spacemr_space.spacemr_space_in_id"
                );
      qs.append("\n ,    spacemr_space_type");
      qtm.addAnd(" spacemr_space_type.spacemr_space_type_id = spacemr_space.spacemr_space_type_id");
      //-
      qs.append("\n ,    app_group");
      qtm.addAnd(" app_group.app_group_id = spacemr_space.app_group_id");
      //-
      if (appSessionTools.hasPermission(session, "db_spacemr_space_read") != null ) {
         //- -qui- selezionare solo quelli che l'utente puo' vedere
         //--- if subito se ha "root"
         //-    allora NON aggiungo la where restrittiva!!
         //- where
         //   join tra
         //      app_group_role_user, app_role, app_role_permission, app_permission
         //   where  app_group_role_user = theGroupId
         //     and  app_permission.name = 'db_spacemr_space_read'.
         //- userid:
         //-
         // user_id:  appSessionTools.getUserData(session).getApp_user_id()
         //-
         // qtm.setWhereIntegerEqual("spacemr_space_super_id",  "spacemr_space.spacemr_space_super_id");
         qs.append("\n ,    app_group_role_user");
         qtm.addAnd(" app_group_role_user.app_group_id = spacemr_space.app_group_id");
         //-
         qs.append("\n ,    app_role_permission");
         qtm.addAnd(" app_role_permission.app_role_id = app_group_role_user.app_role_id");
         //-
         qs.append("\n ,    app_permission");
         qtm.addAnd(" app_permission.app_permission_id = app_role_permission.app_permission_id");
         qtm.addAnd(" app_permission.name = 'db_spacemr_space_read' ");
         //-
      }
      
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
      //-
      if (!outputtype.equals("csv")) {
         //-
         //-
         //- data replacement
         //-
         //- indexing columns
         JSONObject list = mapper.getJSON();
         HashMap<String, Integer> headersIndex = new HashMap<String, Integer>();
         JSONArray headers = list.getJSONArray("headers");
         for (int i=0; i < headers.length(); i++){
            headersIndex.put(headers.getJSONObject(i).getString("name"), new Integer(i));
         }
         
         //- bookings
         if (headersIndex.get("bookings")!=null){
            java.sql.Date current_date = null;
            {
               String v = (String)qtm.getQwhereParamAsObject("current_date");
               if (v != null) {
                  current_date = new java.sql.Date(Long.parseLong(v));
               }
            }
            if (current_date != null) {
               String bqs = ""
                  + "\n  select concat('[',coalesce(group_concat(smt.s),''),']') current_bookings"
                  + "\n   from ("
                  + "\n   select JSON_OBJECT(stato, sum(people_number)) s"
                  + "\n    from spacemr_space_people_book"
                  + "\n   where"
                  + "\n         (isNull(spacemr_space_people_book.date_to) or spacemr_space_people_book.date_to     >= ?)"
                  + "\n     and (isNull(spacemr_space_people_book.date_from) or spacemr_space_people_book.date_from <= ?)"
                  + "\n     and (repetition = 'd' or (DAYOFWEEK(?) = DAYOFWEEK(date_from)))"
                  + "\n    and  (spacemr_space_people_book.spacemr_space_id in ("
                  + "\n           with recursive root_spaces (spacemr_space_id, spacemr_space_in_id) as ("
                  + "\n             select     a.spacemr_space_id,"
                  + "\n                        a.spacemr_space_in_id"
                  + "\n               from     spacemr_space as a"
                  + "\n              where     a.spacemr_space_id = ?"
                  + "\n             union all"
                  + "\n             select     p.spacemr_space_id,"
                  + "\n                        p.spacemr_space_in_id"
                  + "\n               from     spacemr_space as p"
                  + "\n                      , root_spaces as r"
                  + "\n              where     p.spacemr_space_in_id = r.spacemr_space_id"
                  + "\n           )"
                  + "\n           select spacemr_space_id from root_spaces"
                  + "\n           )"
                  + "\n           )"
                  + "\n   group by stato"
                  + "\n  ) smt"
                  ;
               Connection con = null;
               try {
                  con = dataSource.getConnection();
                  PreparedStatement pstmt = con.prepareStatement(bqs);
                  //-
                  pstmt.setDate(1, current_date);
                  pstmt.setDate(2, current_date);
                  pstmt.setDate(3, current_date);
                  //-
                  // System.out.println(" --- bqs: " + bqs + "\n current_date: " + current_date);
                  //-
                  //- pstmt.setInt(4,  spacemr_space_id);
                  //-
                  JSONArray rows = list.getJSONArray("rows");
                  int row_pos_spacemr_space_id = headersIndex.get("spacemr_space_id").intValue();
                  int row_pos_bookings         = headersIndex.get("bookings").intValue();
                  for (int i=0; i < rows.length(); i++){
                     JSONArray row = rows.getJSONArray(i);
                     int spacemr_space_id = row.getInt(row_pos_spacemr_space_id);
                     // System.out.println("spacemr_space_id: " + spacemr_space_id);
                     pstmt.setInt(4,  spacemr_space_id);
                     //-
                     String bjson = null;
                     ResultSet rs = pstmt.executeQuery();
                     if (rs.next()) {
                        bjson = rs.getString(1);
                     }
                     row.put(row_pos_bookings, bjson);
                  }
               } finally {
                  if (con != null) {
                     con.close();
                  }
               }
            }
         }


         //- presences
         if (headersIndex.get("presences")!=null){
            java.sql.Date current_date = null;
            {
               String v = (String)qtm.getQwhereParamAsObject("current_date");
               if (v != null) {
                  current_date = new java.sql.Date(Long.parseLong(v));
               }
            }
            if (current_date != null) {
               String bqs = ""
                  + "\n   select concat('[',coalesce(group_concat(smt.s),''),']') current_bookings"
                  + "\n    from ("
                  + "\n    select JSON_OBJECT('present', sum(people_number)) s"
                  + "\n      from spacemr_space_user_presence"
                  + "\n    where"
                  + "\n          (spacemr_space_user_presence.date_time   >= ?)"
                  + "\n      and (spacemr_space_user_presence.date_time   < DATE_ADD(?, INTERVAL 1 DAY))"
                  + "\n     and  (spacemr_space_user_presence.spacemr_space_id in ("
                  + "\n            with recursive root_spaces (spacemr_space_id, spacemr_space_in_id) as ("
                  + "\n              select     a.spacemr_space_id,"
                  + "\n                         a.spacemr_space_in_id"
                  + "\n                from     spacemr_space as a"
                  + "\n               where     a.spacemr_space_id = ?"
                  + "\n              union all"
                  + "\n              select     p.spacemr_space_id,"
                  + "\n                         p.spacemr_space_in_id"
                  + "\n                from     spacemr_space as p"
                  + "\n                       , root_spaces as r"
                  + "\n               where     p.spacemr_space_in_id = r.spacemr_space_id"
                  + "\n            )"
                  + "\n            select spacemr_space_id from root_spaces"
                  + "\n            )"
                  + "\n            )"
                  + "\n   ) smt"
                  ;
               Connection con = null;
               try {
                  con = dataSource.getConnection();
                  PreparedStatement pstmt = con.prepareStatement(bqs);
                  //-
                  pstmt.setDate(1, current_date);
                  pstmt.setDate(2, current_date);
                  //-
                  // System.out.println(" --- bqs: " + bqs + "\n current_date: " + current_date);
                  //-
                  //-
                  JSONArray rows = list.getJSONArray("rows");
                  int row_pos_spacemr_space_id = headersIndex.get("spacemr_space_id").intValue();
                  int row_pos_presences        = headersIndex.get("presences").intValue();
                  for (int i=0; i < rows.length(); i++){
                     JSONArray row = rows.getJSONArray(i);
                     int spacemr_space_id = row.getInt(row_pos_spacemr_space_id);
                     // System.out.println("spacemr_space_id: " + spacemr_space_id);
                     pstmt.setInt(3,  spacemr_space_id);
                     //-
                     String bjson = null;
                     ResultSet rs = pstmt.executeQuery();
                     if (rs.next()) {
                        bjson = rs.getString(1);
                     }
                     row.put(row_pos_presences, bjson);
                  }
               } finally {
                  if (con != null) {
                     con.close();
                  }
               }
            }
         }

         //- inventarios
         if (headersIndex.get("inventarios")!=null){
            String bqs = ""
               + "\n  select concat('[',coalesce(group_concat(smt.s),''),']') current_inventarios"
               + "\n   from ("
               + "\n   select JSON_OBJECT(stato, count(spacemr_inventario_id)) s"
               + "\n    from spacemr_inventario"
               + "\n   where (spacemr_inventario.spacemr_space_id in ("
               + "\n           with recursive root_spaces (spacemr_space_id, spacemr_space_in_id) as ("
               + "\n             select     a.spacemr_space_id,"
               + "\n                        a.spacemr_space_in_id"
               + "\n               from     spacemr_space as a"
               + "\n              where     a.spacemr_space_id = ?"
               + "\n             union all"
               + "\n             select     p.spacemr_space_id,"
               + "\n                        p.spacemr_space_in_id"
               + "\n               from     spacemr_space as p"
               + "\n                      , root_spaces as r"
               + "\n              where     p.spacemr_space_in_id = r.spacemr_space_id"
               + "\n           )"
               + "\n           select spacemr_space_id from root_spaces"
               + "\n           )"
               + "\n           )"
               + "\n   group by stato"
               + "\n  ) smt"
               ;
            Connection con = null;
            try {
               con = dataSource.getConnection();
               PreparedStatement pstmt = con.prepareStatement(bqs);
               //-
               //-
               // System.out.println(" --- bqs: " + bqs + "\n current_date: " + current_date);
               //-
               //- pstmt.setInt(4,  spacemr_space_id);
               //-
               JSONArray rows = list.getJSONArray("rows");
               int row_pos_spacemr_space_id = headersIndex.get("spacemr_space_id").intValue();
               int row_pos_inventarios         = headersIndex.get("inventarios").intValue();
               for (int i=0; i < rows.length(); i++){
                  JSONArray row = rows.getJSONArray(i);
                  int spacemr_space_id = row.getInt(row_pos_spacemr_space_id);
                  // System.out.println("spacemr_space_id: " + spacemr_space_id);
                  pstmt.setInt(1,  spacemr_space_id);
                  //-
                  String bjson = null;
                  ResultSet rs = pstmt.executeQuery();
                  if (rs.next()) {
                     bjson = rs.getString(1);
                  }
                  row.put(row_pos_inventarios, bjson);
               }
            } finally {
               if (con != null) {
                  con.close();
               }
            }
         }

         //-
         //-
         //-
         rv.put("list",       list);
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   //-


   @SuppressWarnings("unchecked")
   private Vector<String> get_columns_permitted(HttpSession session
                                                , String app_group_name
                                                ) throws Exception {
      //-
      String cache_name = "spacemr_space__columns_permitted";
      HashMap<String,String> columns_permissions = spacemr_space.getColumnsPermissions();
      //-
      Vector<String> rv = new Vector<String>();
      AppPermissionTools_userData userData = 
         (AppPermissionTools_userData)session.getAttribute("userData");
      if (userData != null) {
         HashMap<String, Vector<String>> cache =
            (HashMap<String, Vector<String>>)
            userData.getSessionCustomData(cache_name);
         if (cache == null) {
            cache = new HashMap<String, Vector<String>>();
            userData.setSessionCustomData(cache_name, cache);
         }
         rv = cache.get(app_group_name);
         if (rv == null) {
            rv = new Vector<String>();
            cache.put(app_group_name, rv);
            //-
            for (String c: spacemr_space.getColumns()) {
               if (columns_permissions.get(c) == null) {
                  rv.add(c);
               } else {
                  if ( appSessionTools.hasPermission(session
                                                     , app_group_name
                                                     , columns_permissions.get(c)) == null) {
                     rv.add(c);
                  }
               }
            }
            // System.out.println(" -- get_columns_permitted: " + rv);
         }
      }
      return(rv);
   }

   @SuppressWarnings("unchecked")
   private Vector<String> get_columns_permittedOnAnyGroup(HttpSession session
                                                          ) throws Exception {
      //-
      String cache_name = "spacemr_space__columns_permittedOnAnyGroup";
      HashMap<String,String> columns_permissions = spacemr_space.getColumnsPermissions();
      //-
      Vector<String> rv = new Vector<String>();
      AppPermissionTools_userData userData = 
         (AppPermissionTools_userData)session.getAttribute("userData");
      if (userData != null) {
         rv = (Vector<String>)userData.getSessionCustomData(cache_name);
         if (rv == null) {
            rv = new Vector<String>();
            //-
            for (String c: spacemr_space.getColumns()) {
               if (columns_permissions.get(c) == null) {
                  rv.add(c);
               } else {
                  if ( userData.groupsHavingThePermission(columns_permissions.get(c)).length()>0) {
                     rv.add(c);
                  }
               }
            }
            // System.out.println(" -- get_columns_permittedOnAnyGroup: " + rv);
            userData.setSessionCustomData(cache_name, rv);
         }
      }
      return(rv);
   }
   
   /**
    */
   private JSONObject spacemr_space_get_common(HttpSession session
                                               , HttpServletResponse response
                                               , String scontent
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
      Vector<String> columns = new Vector<String>();
      columns.add("app_group_name");
      JSONObject obj = spacemr_space.get(spacemr_space_id, columns);
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , obj.getString("app_group_name")
                                                  , "db_spacemr_space_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return(null);
      }
      columns = get_columns_permitted(session, obj.getString("app_group_name"));
      obj = spacemr_space.get(spacemr_space_id, columns);
      rv.put("obj",obj);
      rv.put("ancestors", spacemr_space.getAncestors(obj, columns));
      //-
      // JSONObject spacemr_space_superj = spacemr_space_super.get(obj.getInt("spacemr_space_super_id"));
      // rv.put("spacemr_space_super",spacemr_space_superj);
      status = "ok";
      //-
      JSONArray user_groups =
         appSessionTools.groupsHavingThePermission(session, "db_spacemr_space_update");
      rv.put("user_groups",  user_groups);
      rv.put("columns",      tools.vector_of_string_to_sjonarray(columns));
      rv.put("space_types",  spacemr_space_type.getAllSpaceTypes());
      return(rv);
   }

   /**
    *  http://localhost:8080/spacemr_space/spacemr_space_get?content={"spacemr_space_id", "1"}
    */
   @RequestMapping(value="spacemr_space/spacemr_space_get")
   public void spacemr_space_get(HttpSession session
                                 , HttpServletResponse response
                                 , @RequestParam("content") String scontent
                                 ) throws Exception {
      JSONObject rv = spacemr_space_get_common(session,response,scontent);
      //-
      if (rv != null) {
         appControllerTools.appDoRequestMappingResponse(response, "ok", rv) ;
      }
   }


   /**
    *  http://localhost:8080/spacemr_space/spacemr_space_get?content={"spacemr_space_id", "1"}
    */
   @RequestMapping(value="spacemr_space/spacemr_space_get_form")
   public void spacemr_space_get_form(HttpSession session
                                      , HttpServletResponse response
                                      , @RequestParam("content") String scontent
                                      ) throws Exception {
      JSONObject rv = spacemr_space_get_common(session,response,scontent);
      //-
      if (rv != null) {
         //-
         rv.put("spacemr_space_id_user_white_list_plugins"
                , app_system_property.getAsString("spacemr_space_id_user_white_list_plugins"));
         rv.put("spacemr_space_id_user_white_list_help"
                , app_system_property.getAsString("spacemr_space_id_user_white_list_help"));
         //-
         appControllerTools.appDoRequestMappingResponse(response, "ok", rv) ;
      }
   }
   

   //-
   @Transactional
   @RequestMapping(value="spacemr_space/spacemr_space_update")
   public void spacemr_space_update(HttpSession session
                                    , HttpServletResponse response
                                    , @RequestParam("content") String scontent
                                    ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      //-
      JSONObject content = new JSONObject(scontent);
      //-
      int spacemr_space_id = content.getInt("spacemr_space_id");
      Vector<String> columns = new Vector<String>();
      columns.add("app_group_name");
      JSONObject obj = spacemr_space.get(spacemr_space_id, columns);
      //-
      //- The user MUST have update permission
      //- on both source and destination group
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , obj.getString("app_group_name")
                                                  , "db_spacemr_space_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      columns = get_columns_permitted(session, obj.getString("app_group_name"));
      //-
      if (!obj.getString("app_group_name").equals(content.getString("app_group_name"))) {
         if ((status = appSessionTools.hasPermission(session
                                                     , content.getString("app_group_name")
                                                     , "db_spacemr_space_update")) != null) {
            status = "permissionDenied " + status;
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      //-
      //-
      //- ancestors check
      //-
      if (!content.isNull("spacemr_space_in_id")
          && !"".equals(content.get("spacemr_space_in_id"))) {
         if (content.getInt("spacemr_space_in_id") == spacemr_space_id) {
            status = "error - loop in tree";
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      JSONArray ancestors = spacemr_space.getAncestors(content, columns);
      for(int i=0; i<ancestors.length(); i++) {
         JSONObject aj = ancestors.getJSONObject(i);
         if (aj.getInt("spacemr_space_id") == spacemr_space_id) {
            status = "error - loop in tree";
            appControllerTools.appDoRequestMappingResponse(response, status, rv);
            return;
         }
      }
      //-
      //-
      //-
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      if (columns.contains("user_white_list")) {
         //- user_white_list syntax check
         //-
         Spacemr_user_white_list_parser p =
            Spacemr_user_white_list_parser.getParser(content.getString("user_white_list")
                                                     , content.getString("code"));
         //-
         //- cache clean
         spacemr_user_white_list_cache.remove(spacemr_space_id);
      }
      //-
      //-
      int app_group_id =
         app_group.get(content.getString("app_group_name")).getInt("app_group_id")
         ;
      content.put("app_group_id", app_group_id);
      //-
      //-
      app_log.writeLog(spacemr_space, spacemr_space_id, appSessionTools.getUser_name(session),content);
      spacemr_space.update(content, columns);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   //-
   @Transactional
   @RequestMapping(value="spacemr_space/spacemr_space_white_list_mass_update")
   public void spacemr_space_white_list_mass_update(HttpSession session
                                                    , HttpServletResponse response
                                                    , @RequestParam("content") String scontent
                                                    ) throws Exception {
      /* 
        input example
         {
           "space-code1": {
             "algoritmo": "settimanale_gruppi",
             "gruppo_lunedi": "LP-1",
             "gruppi": [
               {
                 "nome": "LP-1",
                 "users": "user-1, user-2, user-3"
               }
             ]
           },
           "space-code2": {
             "algoritmo": "settimanale_gruppi",
             "gruppo_martedi": "LP-1",
             "gruppi": [
               {
                 "nome": "LP-1",
                 "users": "user-1, user-2, user-3"
               }
             ]
           }
       */
      //-
      // System.out.println(" -test me- ");
      String status = null;
      //-
      //-
      JSONObject rv = new JSONObject();
      JSONObject spaces_rv = new JSONObject();
      rv.put("spaces_rv", spaces_rv);
      //-
      JSONObject content = new JSONObject(scontent);
      JSONObject spaces = content.getJSONObject("spaces");
      //-
      Iterator keys = spaces.keys();
      while (keys.hasNext()) {
         JSONObject space_rv = new JSONObject();
         String space_code = (String)keys.next();
         JSONObject user_white_list_new = spaces.getJSONObject(space_code);
         // System.out.println(" space_code: "+ space_code);
         // System.out.println(" user_white_list_new: "+user_white_list_new.toString(2)+"\n\n");
         //-
         Vector<String> columns = new Vector<String>();
         columns.add("app_group_name");
         columns.add("user_white_list");
         String space_status = "";
         int spacemr_space_id = -1;
         //-
         //-
         JSONObject obj = spacemr_space.getByCode(space_code, columns);
         if (obj == null) {
            space_status = "not found space ["+space_code+"]";
         }
         if (space_status.equals("")) {
            // System.out.println(" obj: "+obj.toString(2)+"\n\n");
            //-
            spacemr_space_id = obj.getInt("spacemr_space_id");
            //-
            String status_permission;
            if ((status_permission = appSessionTools.hasPermission(session
                                                              , obj.getString("app_group_name")
                                                              , "db_spacemr_space_update")) != null) {
               //-
               //- global permission db_spacemr_space_update check
               space_status = "permissionDenied " + status_permission;
            }
         }
         if (space_status.equals("")) {
            //-
            space_rv.put("spacemr_space_id", spacemr_space_id);
            //-
            //- the user can update the colulm user_white_list?
            columns = get_columns_permitted(session, obj.getString("app_group_name"));
            if (!columns.contains("user_white_list")) {
               space_status = "permissionDenied on user_white_list";
            }
         }
         if (space_status.equals("")) {
            //-
            //- the JSON as parameter is a valid algorithm?
            try {
               Spacemr_user_white_list_parser p =
                  Spacemr_user_white_list_parser.getParser(user_white_list_new.toString(), space_code);
               //-
            } catch (Exception e) {
               space_status = "Error parsing the user_white_list algoritm:\n" + e.getMessage();
            }
         }
         if (space_status.equals("")) {
            //-
            //- does the user_white_list changes?
            //-
            JSONObject old = new JSONObject();
            if (obj.optString("user_white_list", null) != null) {
               if (obj.getString("user_white_list").trim().length() > 0) {
                  old = new JSONObject(obj.getString("user_white_list"));
               }
            }
            JSONObject valuesDiff =
               tools.jsonChanges(user_white_list_new, old);
            if (valuesDiff == null) {
               space_status = "ok - same value - nothing to update";
            }
         }
         if (space_status.equals("")) {
            //-
            //- ok, it is time to update the user_white_list
            //-
            //- cache clean
            spacemr_user_white_list_cache.remove(spacemr_space_id);
            //-
            obj = spacemr_space.get(spacemr_space_id, columns);
            String user_white_list_new_string = "";
            if (user_white_list_new.length() > 0) {
               user_white_list_new_string = user_white_list_new.toString(2);
            }
            obj.put("user_white_list",user_white_list_new_string);
            //-
            app_log.writeLog(spacemr_space, spacemr_space_id, appSessionTools.getUser_name(session),obj);
            //-
            try {
               spacemr_space.update(obj, columns);
               space_status = "ok - updated";
            } catch (Exception e) {
               space_status = "error - " + e.getMessage();
               // space_rv.put("obj", obj);
               // throw e;
            }
            //-
         }
         //-
         space_rv.put("status", space_status);
         spaces_rv.put(space_code, space_rv);
      }
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
   //-
   @Transactional
   @RequestMapping(value="spacemr_space/spacemr_space_update_check_user_white_list")
   public void spacemr_space_update_check_user_white_list(HttpSession session
                                    , HttpServletResponse response
                                    , @RequestParam("content") String scontent
                                    ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      //-
      JSONObject content = new JSONObject(scontent);
      //-
      int spacemr_space_id = content.getInt("spacemr_space_id");
      JSONObject obj = spacemr_space.get(spacemr_space_id);
      //-
      //- The user MUST have update permission on source group
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , obj.getString("app_group_name")
                                                  , "db_spacemr_space_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      String user_white_list = content.getString("user_white_list");
      String code = content.optString("code", null);
      //-
      Spacemr_user_white_list_parser p = null;
      try {
         p = Spacemr_user_white_list_parser.getParser(user_white_list, code);
         rv.put("algorithm", p.getAlgorithm_name());
      } catch (Exception e) {
         rv.put("errors", e.getMessage());
         status = "ok";
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
         return;
      }
      //-
      status = "ok";
      //-
      rv.put("all_involved_users",      p.getAllInvolvedUsersAsJson());
      rv.put("text_description"
             , p.getTextDescription(spacemr_user_white_list_cache.allInvolvedUsersInfo(p)));
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   
   //-
   /**
    *  http://localhost:8080/spacemr_space/spacemr_space_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_space/spacemr_space_delete")
   public void spacemr_space_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      //-
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      JSONObject content = new JSONObject(scontent);
      int spacemr_space_id = content.getInt("spacemr_space_id");
      JSONObject obj = spacemr_space.get(spacemr_space_id);
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , obj.getString("app_group_name")
                                                  , "db_spacemr_space_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      spacemr_space.delete(spacemr_space_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   //-
   /**
    *  http://localhost:8080/spacemr_space/spacemr_space_logs
    */
   @RequestMapping(value="spacemr_space/spacemr_space_logs")
   public void spacemr_space_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      //-
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("spacemr_space_id");
      JSONObject obj = spacemr_space.get(id);
      //-
      if ((status = appSessionTools.hasPermission(session
                                                  , obj.getString("app_group_name")
                                                  , "db_spacemr_space_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_space
                       , id
                       );
   }

   @RequestMapping(value="spacemr_space/spacemr_space_set_user_white_list_mass_update")
   public void spacemr_space_set_user_white_list_mass_update(HttpSession session
                                                            , HttpServletResponse response
                                                            , HttpServletRequest  request
                                                            , @RequestParam("content") String scontent
                                                            ) throws Exception {
      //-
      JSONObject rv = new JSONObject();
      JSONObject content = new JSONObject(scontent);
      AppPermissionTools_userData userData = appSessionTools.getUserData(session);
      if (userData != null) {
         app_user.setUserProperty(userData.getApp_user_id()
                                  , "space_user_white_list_mass_update"
                                  , content.getString("space_user_white_list_mass_update"));
      }
      appControllerTools.appDoRequestMappingResponse(response, "ok", rv) ;
   }

   @RequestMapping(value="spacemr_space/spacemr_space_get_user_white_list_mass_update")
   public void spacemr_space_get_user_white_list_mass_update(HttpSession session
                                                            , HttpServletResponse response
                                                            , HttpServletRequest  request
                                                            , @RequestParam("content") String scontent
                                                            ) throws Exception {
      //-
      JSONObject rv = new JSONObject();
      JSONObject content = new JSONObject(scontent);
      AppPermissionTools_userData userData = appSessionTools.getUserData(session);
      if (userData != null) {
         //-
         String space_user_white_list_mass_update = 
            app_user.getUserPropertyAsString(userData.getApp_user_id(), "space_user_white_list_mass_update");
         rv.put("space_user_white_list_mass_update", space_user_white_list_mass_update);
         rv.put("spacemr_space_id_user_white_list_mass_update_help"
                , app_system_property.getAsString("spacemr_space_id_user_white_list_mass_update_help"));
      }
      appControllerTools.appDoRequestMappingResponse(response, "ok", rv) ;
   }

   
}
