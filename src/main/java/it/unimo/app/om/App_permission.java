package it.unimo.app.om;
//-

import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppPermissionTools_userData;
import it.unimo.app.tools.AppMapperVectorString;
import it.unimo.app.tools.AppMapperVectorInteger;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

//-
public class App_permission extends BaseObjectModel {
   //-
   //-
   @Autowired
   private App_group_role_user app_group_role_user;
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private App_user app_user;
   @Autowired
   private App_group app_group;
   
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private Tools tools;

   //-
   @SuppressWarnings("unchecked")
   public int insert(JSONObject content) throws Exception {
      Vector columns = (Vector<String>)getColumnsWrite().clone();
      // columns.add( "foreign_reference_id");
      MapSqlParameterSource np = getMapSqlParameterSource(content, columns);
      KeyHolder keyHolder = new GeneratedKeyHolder();
      String qs = ""
         + "insert into app_permission ("
         + getSqlStringForInsert_fields(columns)
         + ") values ("
         + getSqlStringForInsert_values(columns)
         + ")"
         ;
      // System.out.println(" qs: " + qs );
      //for(Object k: np.getValues().keySet() ) { System.out.println("k.["+k+"]:["+np.getValues().get(k)+"]");}
      namedParameterJdbcTemplate.update(qs , np, keyHolder);
      int id = keyHolder.getKey().intValue();
      //-
      return(id);
   }

   public JSONObject get(int app_permission_id) throws Exception {
      return(get(app_permission_id, getColumns()));
   }
   //-
   public JSONObject get(int app_permission_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "app_permission_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "app_permission");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from app_permission"
                         // + "     , app_permission_super"
                         + " where app_permission_id = ?"
                         // + "   and app_permission_super.app_permission_super_id = app_permission.app_permission_super_id"
                         , new Object[] { Integer.valueOf(app_permission_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int app_permission_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_permission where app_permission_id = ?"
                         , new Object[] { Integer.valueOf(app_permission_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("app_permission_id", content.getInt("app_permission_id"));
      String qs = 
         "update app_permission set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where app_permission_id = :app_permission_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int app_permission_id) throws Exception {
      app_log.deleteLogs(this, app_permission_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_permission_id", app_permission_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_role_permission  "
                 + " where app_permission_id = :app_permission_id"
                 , np
                 );
      namedParameterJdbcTemplate
         .update(
                 "delete from app_permission  "
                 + " where app_permission_id = :app_permission_id"
                 , np
                 );
   }
   //-
   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) throws Exception {
      MapSqlParameterSource np = new MapSqlParameterSource();
      for (String k: columns) {
         switch (k) {
         case "app_permission_id":
            break;
         case "name":
            np.addValue("name", content.optString("name", null));
            break;
         case "nota":
            np.addValue("nota", content.getString("nota"));
            break;
         default:
            throw new Exception("column name ["+k+"] not found");
         }
      }
      return(np);
   }
   //-
   public Vector<String> getColumns() {
      return(_columns);
   }
   public Vector<String> getColumnsWrite() {
      return(_columnsWrite);
   }
   public HashMap<String,String> getComputedColumnsMap() {
      return(computedColumnsMap);
   }
   private static final Vector<String> _columns = new Vector<String>();
   private static final Vector<String> _columnsWrite = new Vector<String>();
   private static final HashMap<String,String> computedColumnsMap = new HashMap<String,String>();
   private static final String[] _columnsWrite_a = {
      "name"
      , "nota"
   };
   private static final String[] _columns_toGet_a = {
      // "app_permission_super_name"
   };
   static  {
      for (String c: _columnsWrite_a) {
         _columnsWrite.add(c);
         _columns.add(c);
      }
      for (String c: _columns_toGet_a) {
         _columns.add(c);
      }
   }
   static  {
      // computedColumnsMap.put("app_permission_super_name", "app_permission_super.name");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = app_permission.user_name)"
      //         );
   }

   
   /** 
    * Initialize system permissions */
   @PostConstruct
   public void initPermissions() throws Exception {
      
      initPermissions_addPermissions();
      initPermissions_addRoles();
   }

   @SuppressWarnings("unchecked")
   private void initPermissions_addPermissions() throws Exception {
      System.out.println(" ------------------------------ initPermissions");
      Vector<String> vNew = new Vector<String>();
      Vector<String> vOld = null;
      Vector<String> vToAdd    = null;
      Vector<String> vToDelete = null;
      //-
      for(String p: system_permissions) { 
         vNew.add(p); 
      };
      {
         AppMapperVectorString m = new AppMapperVectorString();
         jdbcTemplate.query("select name from app_permission"
                            , new Object[] { }, m );
         vOld = m.getVector();
      }
      //-
      vToAdd     = ((Vector<String>)vNew.clone()); vToAdd.removeAll(vOld);
      vToDelete  = ((Vector<String>)vOld.clone()); vToDelete.removeAll(vNew);
      //-
      if (vToAdd.size() > 0) {
         System.out.println(" adding: "   + vToAdd);
         //- 
         String nota = "Added by app system initializer on " + new Date();
         for (String s: vToAdd) {
         jdbcTemplate.update("insert into app_permission (name, nota)"
                             + "\n values (?,?)"
                             , new Object[] { s, nota }
                             );
         }
      }
      if (vToDelete.size()>0) {
         System.out.println(" Non system permissions: " + vToDelete);
      }
   }


   private void initPermissions_addRoles() throws Exception {
      String rootRoleName = "root";
      String nota = "Added by app system initializer on " + new Date();
      //-
      //-
      //- roles
      //-
      //-
      int user_role_id = initPermissions_addRoles_addRole("user", nota);
      //-
      int root_role_id = initPermissions_addRoles_addRole(rootRoleName, nota);
      //-
      Vector<Integer> vToAdd    = null;
      {
         String qs = ""
            + "\n select p.app_permission_id"
            + "\n   from app_permission p"
            + "\n  where p.app_permission_id not in"
            + "\n   ("
            + "\n    select rp.app_permission_id"
            + "\n      from app_role_permission rp"
            + "\n         , app_role r"
            + "\n     where rp.app_role_id = r.app_role_id"
            + "\n       and r.name = ?"
            + "\n     )"
            ;
         AppMapperVectorInteger m = new AppMapperVectorInteger();
         jdbcTemplate.query(qs , new Object[] { rootRoleName }, m );
         vToAdd = m.getVector();
      }
      if (vToAdd.size() > 0) {
         System.out.println(" Adding to role "+rootRoleName+" permission: : " + vToAdd);
         for (Integer i: vToAdd) {
            jdbcTemplate
               .update("insert into app_role_permission (app_role_id, app_permission_id)"
                       + "\n values (?,?)"
                       , new Object[] { Integer.valueOf(root_role_id), i}
                       );
         }
      }
      //-
      //-
      //- users
      //-
      //-
      int anonymous_user_id = initPermissions_addRoles_addUser("anonymous", nota);
      //-
      String rootUserName = "root";
      int root_user_id = initPermissions_addRoles_addUser(rootUserName, nota);
      //-
      //-
      //- groups
      //-
      //-
      String rootGroupName = "root";
      if (jdbcTemplate
          .queryForObject("select count(*) from app_group where name = ?"
                          , Integer.class, rootGroupName) < 1
          ) {
         System.out.println(" adding root group " + rootGroupName);
         jdbcTemplate.update("insert into app_group (name, nota)"
                             + "\n values (?,?)"
                             , new Object[] { rootGroupName, nota }
                             );
      }
      //-
      int root_group_id = 
         jdbcTemplate.queryForObject("select app_group_id from app_group where name = ?"
                                     , Integer.class, rootGroupName);
      //-
      //-
      //- default roles to root user
      //-
      app_group_role_user.insert(root_user_id, root_group_id, root_role_id);
      //-
      // app_group_role_user.insert(anonymous_user_id, root_group_id, user_role_id);
      //-
   }


   private int initPermissions_addRoles_addRole(String roleName
                                                , String nota
                                                ) throws Exception {
      if (jdbcTemplate
          .queryForObject("select count(*) from app_role where name = ?"
                          , Integer.class, roleName) < 1
          ) {
         System.out.println(" adding role " + roleName);
         jdbcTemplate.update("insert into app_role (name, nota)"
                             + "\n values (?,?)"
                             , new Object[] { roleName, nota }
                             );
      }
      //-
      int role_id = 
         jdbcTemplate.queryForObject("select app_role_id from app_role where name = ?"
                                     , Integer.class, roleName);
      return(role_id);
   }      

   public int initPermissions_addRoles_addUser(String userName, String nota) throws Exception {
      int user_id = -1;
      boolean newUser = 
         (jdbcTemplate
          .queryForObject("select count(*) from app_user where user_name = ?"
                          , Integer.class, userName) < 1);
      if (newUser) {
         System.out.println(" adding user " + userName);
         jdbcTemplate.update("insert into app_user (user_name, first_name, last_name, created, nota)"
                             + "\n values (?,?,?,?,?)"
                             , new Object[] { userName, userName, userName, new Date(), nota }
                             );
      }
      user_id = jdbcTemplate.queryForObject("select app_user_id from app_user where user_name = ?"
                                            , Integer.class, userName);
      if (newUser) {
         app_user.setPassword(user_id, "app" + userName);
      }
      return(user_id);
   }


   public AppPermissionTools_userData getPermissionsForUser(int app_user_id
                                                            , JSONObject app_userj
                                                            , boolean default_user
                                                            , HttpServletRequest httpServletRequest
                                                            ) throws Exception {
      AppPermissionTools_userData  rv = getPermissionsForUser(app_user_id);
      rv.setApp_user(app_userj, default_user, tools, httpServletRequest);
      rv.setRemember_me_enabled(app_system_property.getAsBoolean("login_remember_me_enabled"));
      return rv;
   }
   public AppPermissionTools_userData getPermissionsForUser(int app_user_id
                                                            ) throws Exception {
      String qs = ""
         + "\n  select g.name"
         + "\n       , p.name"
         + "\n    from app_group_role_user gru "
         + "\n       , app_role_permission rp"
         + "\n       , app_permission p"
         + "\n       , app_group g"
         + "\n   where gru.app_role_id = rp.app_role_id"
         + "\n     and rp.app_permission_id = p.app_permission_id"
         + "\n     and gru.app_group_id = g.app_group_id"
         + "\n     and gru.app_user_id = ?"
         + "\n   order by g.name, p.name"
         ;
      GetPermissionsForUser_TmpRowMapper mapper = new GetPermissionsForUser_TmpRowMapper();
      jdbcTemplate.query(qs, mapper , app_user_id);
      AppPermissionTools_userData rv = mapper.getUserData();
      rv.setAuth_single_sign_on_protected_url(app_system_property
                                              .getAsString("auth_single_sign_on_protected_url"));
      return rv;
   }
   private class GetPermissionsForUser_TmpRowMapper implements RowMapper<String> {
      private AppPermissionTools_userData _userData = new AppPermissionTools_userData();
      private String last_group      = null;
      private HashSet<String> groupSet = null;
      @Override
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
         String group      = rs.getString(1);
         String permission = rs.getString(2);
         if (last_group == null
             || !last_group.equals(group)) {
            groupSet = new HashSet<String>();
            last_group = group;
            _userData.setPermissionForGroup(group, groupSet);
         }
         groupSet.add(permission);
         return group;
      }
      AppPermissionTools_userData getUserData() throws Exception {
         _userData.setAllGroupNames(app_group.getAllGroupNames());
         return(_userData);
      }
   }

   private static String system_permissions[] = {
      "db_app_sys_tests"
      , "db_app_user_read"
      , "db_app_user_insert"
      , "db_app_user_delete"
      , "db_app_user_update"
      , "db_app_user_admin"
      , "db_app_user_logs"
      , "db_app_table_mattoni_save"
      , "db_app_table_mattoni_admin"
      , "db_app_permission_read"
      , "db_app_permission_insert"
      , "db_app_permission_delete"
      , "db_app_permission_update"
      , "db_app_permission_logs"
      , "db_app_role_read"
      , "db_app_role_insert"
      , "db_app_role_delete"
      , "db_app_role_update"
      , "db_app_role_logs"
      , "db_app_group_read"
      , "db_app_group_insert"
      , "db_app_group_delete"
      , "db_app_group_update"
      , "db_app_group_logs"
      , "db_app_group_role_user_read"
      , "db_app_group_role_user_insert"
      , "db_app_group_role_user_delete"
      , "db_app_system_property_read"
      , "db_app_system_property_insert"
      , "db_app_system_property_delete"
      , "db_app_system_property_update"
      , "db_app_system_property_logs"
      , "db_app_system_log_read"
      , "db_app_system_log_insert"
      , "db_app_system_log_delete"
      , "db_app_system_log_update"
      , "db_app_system_log_logs"
      , "db_spacemr_people_read"
      , "db_app_file_read"
      , "db_app_file_insert"
      , "db_app_file_delete"
      , "db_app_file_update"
      , "db_app_file_logs"
      , "db_spacemr_people_insert"
      , "db_spacemr_people_delete"
      , "db_spacemr_people_update"
      , "db_spacemr_people_logs"
      , "db_spacemr_space_type_read"
      , "db_spacemr_space_type_insert"
      , "db_spacemr_space_type_delete"
      , "db_spacemr_space_type_update"
      , "db_spacemr_space_type_logs"
      , "db_spacemr_space_people_type_read"
      , "db_spacemr_space_people_type_insert"
      , "db_spacemr_space_people_type_delete"
      , "db_spacemr_space_people_type_update"
      , "db_spacemr_space_people_type_logs"
      , "db_spacemr_space_read"
      , "db_spacemr_space_insert"
      , "db_spacemr_space_insert_mass"
      , "db_spacemr_space_delete"
      , "db_spacemr_space_update"
      , "db_spacemr_space_logs"
      , "db_spacemr_space_people_read"
      , "db_spacemr_space_people_insert"
      , "db_spacemr_space_people_delete"
      , "db_spacemr_space_people_update"
      , "db_spacemr_space_people_logs"
      , "db_spacemr_inventario_read"
      , "db_spacemr_inventario_insert"
      , "db_spacemr_inventario_delete"
      , "db_spacemr_inventario_update"
      , "db_spacemr_inventario_admin"
      , "db_spacemr_inventario_logs"
      , "db_spacemr_inventario_wf_user"
      , "db_spacemr_inventario_wf_admin"
      , "db_spacemr_space_map_read"
      , "db_spacemr_space_map_insert"
      , "db_spacemr_space_map_delete"
      , "db_spacemr_space_map_update"
      , "db_spacemr_space_map_logs"
      //- with book_admin you can operate on any record
      //- else you can change only the one related to "you"
      , "db_spacemr_space_people_book_admin"
      , "db_spacemr_space_people_book_admin_update"
      , "db_spacemr_space_people_book_read"
      , "db_spacemr_space_people_book_insert"
      , "db_spacemr_space_people_book_delete"
      , "db_spacemr_space_people_book_update"
      , "db_spacemr_space_people_book_logs"
      , "db_spacemr_space_people_book_responsible"
      , "db_spacemr_space_people_book_verifier"
      , "db_spacemr_space_people_book_workflow_read"
      , "db_app_notifica_read"
      , "db_app_notifica_insert"
      , "db_app_notifica_delete"
      , "db_app_notifica_update"
      , "db_app_notifica_logs"
      , "db_spacemr_space_user_presence_read"
      , "db_spacemr_space_user_presence_read_all" // allow to read all presences
      , "db_spacemr_space_user_presence_insert"
      , "db_spacemr_space_user_presence_insert_over_booking"
      , "db_spacemr_space_user_presence_insert_choose_date_time"
      , "db_spacemr_space_user_presence_insert_choose_people_number" // puo' scegliere il numero di persone presenti
      , "db_spacemr_space_user_presence_insert_in_any_space"  // no white list required
      , "db_spacemr_space_user_presence_insert_multiple_time_in_space"
      , "db_spacemr_space_user_presence_delete"
      , "db_spacemr_space_user_presence_update"
      , "db_spacemr_space_user_presence_logs"
      , "db_spacemr_space_user_presence_admin"
      , "db_spacemr_space_user_presence_qrcode"
      , "db_spacemr_space_user_presence_hide_pass_icon"
   };


}
