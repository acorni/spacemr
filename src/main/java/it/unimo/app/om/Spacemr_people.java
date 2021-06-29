package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
//-
public class Spacemr_people extends BaseObjectModel {
   //-
   //-
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
         + "insert into spacemr_people ("
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
   
   //-
   //-
   public JSONObject get(int spacemr_people_id) throws Exception {
      return(get(spacemr_people_id, getColumns()));
   }
   
   //-
   public JSONObject get(int spacemr_people_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_people_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_people");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_people"
                         // + "     , spacemr_people_super"
                         + " where spacemr_people_id = ?"
                         // + "   and spacemr_people_super.spacemr_people_super_id = spacemr_people.spacemr_people_super_id"
                         , new Object[] { Integer.valueOf(spacemr_people_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   //-
   public JSONObject getByUsername(String username) throws Exception {
      Vector<String> columns = getColumns();
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_people_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_people");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_people"
                         + " where username = ?"
                         , new Object[] { username }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   public JSONObject getByLastFirstName(String lastfirstname) throws Exception {
      Vector<String> columns = getColumns();
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_people_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_people");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_people"
                         + " where concat(last_name, ' ', first_name) = ?"
                         , new Object[] { lastfirstname }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   
   public List<String> getAllUsernamesInLdap() throws Exception {
      String qs = "select username from spacemr_people where fg_in_ldap <> 0;";
      List<String> rv = (List<String>) jdbcTemplate.queryForList(qs, String.class);
      return(rv);
   }

   
   //-
   public JSONObject getLog(int spacemr_people_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from spacemr_people where spacemr_people_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_people_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("spacemr_people_id", content.getInt("spacemr_people_id"));
      String qs = 
         "update spacemr_people set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where spacemr_people_id = :spacemr_people_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   
   //-
   public void delete(int spacemr_people_id) throws Exception {
      app_log.deleteLogs(this, spacemr_people_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("spacemr_people_id", spacemr_people_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from spacemr_people  "
                 + " where spacemr_people_id = :spacemr_people_id"
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
         case "spacemr_people_id":
            break;
         case "username":
            np.addValue("username", content.optString("username", null));
            break;
         case "first_name":
            np.addValue("first_name", content.optString("first_name", null));
            break;
         case "last_name":
            np.addValue("last_name", content.optString("last_name", null));
            break;
         case "email":
            np.addValue("email", content.optString("email", null));
            break;
         case "role":
            np.addValue("role", content.optString("role", null));
            break;
         case "department":
            np.addValue("department", content.optString("department", null));
            break;
         case "fg_in_ldap":
            np.addValue("fg_in_ldap", tools.jsonObject_getBoolean(content, "fg_in_ldap"));
            break;
         case "fg_has_a_seat":
            np.addValue("fg_has_a_seat", tools.jsonObject_getBoolean(content, "fg_has_a_seat"));
            break;
         case "nota":
            np.addValue("nota", content.getString("nota"));
            break;
         case "configuration":
            np.addValue("configuration", content.getString("configuration"));
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
   public Vector<String> getColumnsForList() {
      return(_columnsForList);
   }
   public HashMap<String,String> getComputedColumnsMap() {
      return(computedColumnsMap);
   }
   private static final Vector<String> _columns = new Vector<String>();
   private static final Vector<String> _columnsHiddenInList = new Vector<String>();
   private static final Vector<String> _columnsForList = new Vector<String>();
   private static final Vector<String> _columnsWrite = new Vector<String>();
   private static final HashMap<String,String> computedColumnsMap = new HashMap<String,String>();
   private static final String[] _columnsWrite_a = {
      "username"
      , "first_name"
      , "last_name"
      , "email"
      , "role"
      , "department"
      , "fg_in_ldap"
      , "fg_has_a_seat"
      , "nota"
      , "configuration"
   };
   private static final String[] _columns_toGet_a = {
      "spacemr_people_current_spaces"
      , "spacemr_main_space_id"
      , "spacemr_main_space_id_code"
      , "spacemr_main_space_in_map_id_default"
      , "spacemr_main_space_in_map_id_default_description"
   };
   private static final String[] _columns_hidden_in_list = {
      "spacemr_main_space_id"
      , "spacemr_main_space_in_map_id_default"
   };
   static  {
      for (String c: _columnsWrite_a) {
         _columnsWrite.add(c);
         _columns.add(c);
      }
      for (String c: _columns_toGet_a) {
         _columns.add(c);
      }
      for (String c: _columns_hidden_in_list) {
         _columnsHiddenInList.add(c);
      }
      for (String c: _columns) {
         if (!_columnsHiddenInList.contains(c)) {
            _columnsForList.add(c);
         }
      }
   }
   static  {
      computedColumnsMap
         .put("spacemr_people_current_spaces"
              , ""
              + "\n  ("
              + "\n  select coalesce(group_concat(smt.s),'') current_spaces"
              + "\n    from ("
              + "\n        select spacemr_space_people.spacemr_people_id"
              + "\n             , concat(spacemr_space.code,  ' ', coalesce(spacemr_space_people.date_to,'')) s"
              + "\n          from spacemr_space_people"
              + "\n             , spacemr_space"
              + "\n         where spacemr_space_people.spacemr_space_id = spacemr_space.spacemr_space_id"
              + "\n           and spacemr_space_people.date_from <= now()"
              + "\n           and ("
              + "\n                spacemr_space_people.date_to is null"
              + "\n                or spacemr_space_people.date_to >= now()"
              + "\n               )"
              + "\n            ) smt"
              + "\n      where smt.spacemr_people_id = spacemr_people.spacemr_people_id"
              + "\n     group by smt.spacemr_people_id"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_main_space_id"
              , ""
              + "\n  ("
              + "\n    select spacemr_space.spacemr_space_id"
              + "\n      from spacemr_space_people"
              + "\n         , spacemr_space"
              + "\n         , spacemr_space_people_type"
              + "\n     where spacemr_space_people.spacemr_people_id = spacemr_people.spacemr_people_id"
              + "\n       and spacemr_space_people.spacemr_space_id  = spacemr_space.spacemr_space_id"
              + "\n       and spacemr_space_people_type.spacemr_space_people_type_id"
              + "\n             = spacemr_space_people.spacemr_space_people_type_id"
              + "\n       and spacemr_space_people_type.fg_is_a_seat <> 0"
              + "\n       and spacemr_space_people.date_from <= now()"
              + "\n       and ( "
              + "\n             spacemr_space_people.date_to is null"
              + "\n             or spacemr_space_people.date_to >= now()"
              + "\n           )"
              + "\n     limit 1"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_main_space_id_code"
              , ""
              + "\n  ("
              + "\n    select spacemr_space.code"
              + "\n      from spacemr_space_people"
              + "\n         , spacemr_space"
              + "\n         , spacemr_space_people_type"
              + "\n     where spacemr_space_people.spacemr_people_id = spacemr_people.spacemr_people_id"
              + "\n       and spacemr_space_people.spacemr_space_id  = spacemr_space.spacemr_space_id"
              + "\n       and spacemr_space_people_type.spacemr_space_people_type_id"
              + "\n             = spacemr_space_people.spacemr_space_people_type_id"
              + "\n       and spacemr_space_people_type.fg_is_a_seat <> 0"
              + "\n       and spacemr_space_people.date_from <= now()"
              + "\n       and ( "
              + "\n             spacemr_space_people.date_to is null"
              + "\n             or spacemr_space_people.date_to >= now()"
              + "\n           )"
              + "\n     limit 1"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_main_space_in_map_id_default"
              , ""
              + "\n  ("
              + "\n     select spacemr_space_map.spacemr_space_map_id"
              + "\n       from spacemr_space_people"
              + "\n          , spacemr_space"
              + "\n          , spacemr_space sp"
              + "\n          , spacemr_space_people_type"
              + "\n          , spacemr_space_map"
              + "\n      where spacemr_space_people.spacemr_people_id = spacemr_people.spacemr_people_id"
              + "\n        and spacemr_space_people.spacemr_space_id  = spacemr_space.spacemr_space_id"
              + "\n        and spacemr_space_people_type.spacemr_space_people_type_id"
              + "\n              = spacemr_space_people.spacemr_space_people_type_id"
              + "\n        and spacemr_space_people_type.fg_is_a_seat <> 0"
              + "\n        and sp.spacemr_space_id = spacemr_space.spacemr_space_in_id"
              + "\n        and spacemr_space_map.spacemr_space_id = sp.spacemr_space_id"
              + "\n        and spacemr_space_people.date_from <= now()"
              + "\n        and ( "
              + "\n              spacemr_space_people.date_to is null"
              + "\n              or spacemr_space_people.date_to >= now()"
              + "\n            )"
              + "\n      limit 1"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_main_space_in_map_id_default_description"
              , ""
              + "\n  ("
              + "\n     select spacemr_space_map.description"
              + "\n       from spacemr_space_people"
              + "\n          , spacemr_space"
              + "\n          , spacemr_space sp"
              + "\n          , spacemr_space_people_type"
              + "\n          , spacemr_space_map"
              + "\n      where spacemr_space_people.spacemr_people_id = spacemr_people.spacemr_people_id"
              + "\n        and spacemr_space_people.spacemr_space_id  = spacemr_space.spacemr_space_id"
              + "\n        and spacemr_space_people_type.spacemr_space_people_type_id"
              + "\n              = spacemr_space_people.spacemr_space_people_type_id"
              + "\n        and sp.spacemr_space_id = spacemr_space.spacemr_space_in_id"
              + "\n        and spacemr_space_people_type.fg_is_a_seat <> 0"
              + "\n        and spacemr_space_map.spacemr_space_id = sp.spacemr_space_id"
              + "\n        and spacemr_space_people.date_from <= now()"
              + "\n        and ( "
              + "\n              spacemr_space_people.date_to is null"
              + "\n              or spacemr_space_people.date_to >= now()"
              + "\n            )"
              + "\n      limit 1"
              + "\n  )"
              );

      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = spacemr_people.user_name)"
      //         );
   }
}
