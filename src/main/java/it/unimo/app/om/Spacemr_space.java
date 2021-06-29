package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
//-
public class Spacemr_space extends BaseObjectModel {
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
         + "insert into spacemr_space ("
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
   public JSONObject get(int spacemr_space_id) throws Exception {
      return(get(spacemr_space_id, getColumns()));
   }

   
   //-
   /* 
      select spacemr_space.code, spacemr_space_in.code
        from spacemr_space left join spacemr_space as spacemr_space_in
                on spacemr_space_in.spacemr_space_id = spacemr_space.spacemr_space_in_id
       ;
    */
   public JSONObject get(int spacemr_space_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_space.spacemr_space_id spacemr_space_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_space");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_space "
                         + "   left join spacemr_space as spacemr_space_in"
                         + "       on spacemr_space_in.spacemr_space_id = spacemr_space.spacemr_space_in_id"
                         + "     , spacemr_space_type"
                         + "     , app_group"
                         + " where spacemr_space.spacemr_space_id = ?"
                         + "   and spacemr_space_type.spacemr_space_type_id = spacemr_space.spacemr_space_type_id"
                         + "   and app_group.app_group_id = spacemr_space.app_group_id"
                         , new Object[] { Integer.valueOf(spacemr_space_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }


   public JSONObject getByCode(String code) throws Exception {
      Vector<String> columns = getColumns();
      return(getByCode(code, columns));
   }

   public JSONObject getByCode(String code, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_space.spacemr_space_id spacemr_space_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_space");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_space left join spacemr_space as spacemr_space_in"
                         + "       on spacemr_space_in.spacemr_space_id = spacemr_space.spacemr_space_in_id"
                         + "     , spacemr_space_type"
                         + "     , app_group"
                         + " where spacemr_space.code = ?"
                         + "   and spacemr_space_type.spacemr_space_type_id = spacemr_space.spacemr_space_type_id"
                         + "   and app_group.app_group_id = spacemr_space.app_group_id"
                         , new Object[] { code }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   
   /** Get all Space ancestors.
       The first is the root, the last is the "obj" father.
    */
   public JSONArray getAncestors(JSONObject obj, Vector<String> columns) throws Exception {
      JSONArray rv = new JSONArray();
      getAncestors(obj, rv, columns);
      return(rv);
   }
   private void getAncestors(JSONObject child
                             , JSONArray storage
                             , Vector<String> columns) throws Exception {
      if (child.has("spacemr_space_in_id")
          && !child.isNull("spacemr_space_in_id")
          && !"".equals(child.get("spacemr_space_in_id"))
          ) {
         JSONObject obj = get(child.getInt("spacemr_space_in_id"), columns);
         getAncestors(obj, storage, columns);
         storage.put(obj);
      }
   }
   
   //-
   public JSONObject getLog(int spacemr_space_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from spacemr_space where spacemr_space_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_space_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   
   //-
   @SuppressWarnings("unchecked")
   public void update(JSONObject content, Vector<String> columns) throws Exception {
      Vector<String> c = (Vector<String>)getColumnsWrite().clone();
      c.retainAll(columns);
      // System.out.println(" -- update c: " + c);
      MapSqlParameterSource np = getMapSqlParameterSource(content, c);
      np.addValue("spacemr_space_id", content.getInt("spacemr_space_id"));
      String qs = 
         "update spacemr_space set "
         + getSqlStringForUpdate(c)
         + " where spacemr_space_id = :spacemr_space_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   
   //-
   public void delete(int spacemr_space_id) throws Exception {
      app_log.deleteLogs(this, spacemr_space_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("spacemr_space_id", spacemr_space_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from spacemr_space  "
                 + " where spacemr_space_id = :spacemr_space_id"
                 , np
                 );
   }
   //-
   public HashMap<String, String> getColumnsPermissions() {
      if (_columns_permission_hashmap == null) {
         _columns_permission_hashmap = new HashMap<String, String>();
         Iterator keys = _columns_permission.keys();
         while (keys.hasNext()) {
            String column     = (String)keys.next();
            String permission = _columns_permission.getString(column);
            _columns_permission_hashmap.put(column, permission);
         }
      }
      return(_columns_permission_hashmap);
   }
   //-

   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) throws Exception {
      MapSqlParameterSource np = new MapSqlParameterSource();
      for (String k: columns) {
         switch (k) {
         case "spacemr_space_id":
            break;
         case "app_group_id":
            np.addValue("app_group_id", tools.jsonObject_getInteger(content, "app_group_id"));
            break;
         case "spacemr_space_type_id":
            np.addValue("spacemr_space_type_id", tools.jsonObject_getInteger(content, "spacemr_space_type_id"));
            break;
         case "spacemr_space_in_id":
            np.addValue("spacemr_space_in_id", tools.jsonObject_getInteger(content, "spacemr_space_in_id"));
            break;
         case "name":
            np.addValue("name", content.optString("name", null));
            break;
         case "code":
            np.addValue("code", content.optString("code", null));
            break;
         case "description":
            np.addValue("description", content.optString("description", null));
            break;
         case "number_of_seating":
            np.addValue("number_of_seating", tools.jsonObject_getInteger(content, "number_of_seating"));
            break;
         case "number_of_seating_booking":
            np.addValue("number_of_seating_booking", tools.jsonObject_getInteger(content, "number_of_seating_booking"));
            break;
         case "area_in_meters2":
            np.addValue("area_in_meters2", tools.jsonObject_getBigDecimal(content, "area_in_meters2"));
            break;
         case "user_white_list":
            np.addValue("user_white_list", content.getString("user_white_list"));
            break;
         case "user_presence_progressive_code_map":
            np.addValue("user_presence_progressive_code_map", content.getString("user_presence_progressive_code_map"));
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
      "app_group_id"
      , "spacemr_space_type_id"
      , "spacemr_space_in_id"
      , "code"
      , "description"
      , "area_in_meters2"
      , "number_of_seating"
      , "number_of_seating_booking"
      , "user_white_list"
      , "user_presence_progressive_code_map"
      , "nota"
   };
   private static final String[] _columns_toGet_a = {
      "spacemr_space_type_name"
      , "app_group_name"
      , "spacemr_space_in_code"
      , "spacemr_space_contained_spaces_count"
      , "spacemr_space_people_count"
      , "spacemr_space_people_count_delta"
      , "spacemr_space_people_names"
      , "spacemr_space_map_id_default"
      , "spacemr_space_in_map_id_default"
      , "spacemr_space_people_json"
      , "spacemr_space_people_non_sitting_json"
      , "bookings"
      , "presences"
      , "inventarios"
      , "app_log_last_date"
      , "app_log_last_user_name"
   };
   private static final String[] _columns_hidden_in_list = {
      "app_group_id"
      , "spacemr_space_type_id"
      , "spacemr_space_in_id"
      , "spacemr_space_map_id_default"
      , "spacemr_space_in_map_id_default"
   };

   private static final JSONObject _columns_permission =
      new JSONObject( ""
                      + "\n {"
                      + "\n   \"user_white_list\"                    : \"db_spacemr_space_user_presence_read_all\""
                      + "\n , \"user_presence_progressive_code_map\" : \"db_spacemr_space_user_presence_read_all\""
                      + "\n , \"spacemr_space_people_count\"            : \"db_spacemr_space_people_read\""
                      + "\n , \"spacemr_space_people_count_delta\"      : \"db_spacemr_space_people_read\""
                      + "\n , \"spacemr_space_people_names\"            : \"db_spacemr_space_people_read\""
                      + "\n , \"spacemr_space_people_json\"             : \"db_spacemr_space_people_read\""
                      + "\n , \"spacemr_space_people_non_sitting_json\" : \"db_spacemr_space_people_read\""
                      + "\n , \"bookings\"                              : \"db_spacemr_space_people_book_read\""
                      + "\n , \"presences\"                             : \"db_spacemr_space_user_presence_read_all\""
                      + "\n , \"inventarios\"                           : \"db_spacemr_inventario_read\""
                      + "\n }"
                      );
   private HashMap<String, String> _columns_permission_hashmap = null;
   
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
      computedColumnsMap.put("spacemr_space_code", "spacemr_space.code");
      computedColumnsMap.put("spacemr_space_type_name", "spacemr_space_type.name");
      computedColumnsMap.put("app_group_name", "app_group.name");
      computedColumnsMap.put("spacemr_space_in_code", "spacemr_space_in.code");
      computedColumnsMap
         .put("spacemr_space_contained_spaces_count"
              , ""
              + "(select count(s1.spacemr_space_id) from spacemr_space s1 where s1.spacemr_space_in_id = spacemr_space.spacemr_space_id)"
              );
      computedColumnsMap
         .put("spacemr_space_people_count"
              , ""
              + "\n  ("
              + "\n   select count(*) from spacemr_space_people s1"
              + "\n        , spacemr_space_people_type t"
              + "\n    where s1.spacemr_space_people_type_id = t.spacemr_space_people_type_id"
              + "\n      and s1.spacemr_space_id = spacemr_space.spacemr_space_id"
              + "\n      and s1.date_from <= now()"
              + "\n      and t.fg_is_a_seat <> 0"
              + "\n      and (    s1.date_to is null or s1.date_to >= now() )"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_space_people_count_delta"
              , ""
              + "\n spacemr_space.number_of_seating - ("
              + "\n   select count(*) from spacemr_space_people s1"
              + "\n        , spacemr_space_people_type t"
              + "\n    where s1.spacemr_space_people_type_id = t.spacemr_space_people_type_id"
              + "\n      and s1.spacemr_space_id = spacemr_space.spacemr_space_id"
              + "\n      and s1.date_from <= now()"
              + "\n      and t.fg_is_a_seat <> 0"
              + "\n      and (    s1.date_to is null or s1.date_to >= now() )"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_space_people_names"
              , ""
              + "\n  ("
              + "\n     select coalesce(group_concat(smt.s),'') current_people"
              + "\n       from ("
              + "\n           select ssp.spacemr_space_id"
              + "\n                , concat(sp.first_name,  ' ', sp.last_name,  ' ', coalesce(ssp.date_to,'')) s"
              + "\n             from spacemr_space_people ssp"
              + "\n                , spacemr_people sp"
              + "\n            where ssp.spacemr_people_id = sp.spacemr_people_id"
              + "\n              and ssp.date_from <= now()"
              + "\n              and ( ssp.date_to is null or ssp.date_to >= now() )"
              + "\n            ) smt"
              + "\n         where smt.spacemr_space_id = spacemr_space.spacemr_space_id"
              + "\n        group by smt.spacemr_space_id"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_space_people_json"
              , ""
              + "\n   ("
              + "\n      select concat('[',coalesce(group_concat(smt.s),''),']') current_people"
              + "\n        from ("
              + "\n            select ssp.spacemr_space_id"
              + "\n                 , JSON_OBJECT('spacemr_people_id', sp.spacemr_people_id"
              + "\n                             , 'first_name', sp.first_name"
              + "\n                             , 'last_name', sp.last_name"
              + "\n                             ) s"
              + "\n              from spacemr_space_people ssp"
              + "\n                 , spacemr_people sp"
              + "\n                 , spacemr_space_people_type t"
              + "\n             where ssp.spacemr_people_id = sp.spacemr_people_id"
              + "\n               and ssp.spacemr_space_people_type_id = t.spacemr_space_people_type_id"
              + "\n               and ssp.date_from <= now()"
              + "\n               and t.fg_is_a_seat <> 0"
              + "\n               and ( ssp.date_to is null or ssp.date_to >= now() )"
              + "\n             order by sp.last_name, sp.first_name"
              + "\n             ) smt"
              + "\n          where smt.spacemr_space_id = spacemr_space.spacemr_space_id"
              + "\n         group by smt.spacemr_space_id"
              + "\n   )"
              );
      computedColumnsMap
         .put("spacemr_space_people_non_sitting_json"
              , ""
              + "\n   ("
              + "\n      select concat('[',coalesce(group_concat(smt.s),''),']') current_people"
              + "\n        from ("
              + "\n            select ssp.spacemr_space_id"
              + "\n                 , JSON_OBJECT('spacemr_people_id', sp.spacemr_people_id"
              + "\n                             , 'first_name', sp.first_name"
              + "\n                             , 'last_name', sp.last_name"
              + "\n                             , 'spacemr_space_people_type_name', t.name"
              + "\n                             ) s"
              + "\n              from spacemr_space_people ssp"
              + "\n                 , spacemr_people sp"
              + "\n                 , spacemr_space_people_type t"
              + "\n             where ssp.spacemr_people_id = sp.spacemr_people_id"
              + "\n               and ssp.spacemr_space_people_type_id = t.spacemr_space_people_type_id"
              + "\n               and ssp.date_from <= now()"
              + "\n               and t.fg_is_a_seat = 0"
              + "\n               and ( ssp.date_to is null or ssp.date_to >= now() )"
              + "\n             order by t.name, sp.last_name, sp.first_name"
              + "\n             ) smt"
              + "\n          where smt.spacemr_space_id = spacemr_space.spacemr_space_id"
              + "\n         group by smt.spacemr_space_id"
              + "\n   )"
              );
      computedColumnsMap
         .put("spacemr_space_map_id_default"
              , ""
              + "\n  ("
              + "\n  select spacemr_space_map.spacemr_space_map_id"
              + "\n    from spacemr_space_map"
              + "\n   where spacemr_space_map.spacemr_space_id = spacemr_space.spacemr_space_id"
              + "\n   order by spacemr_space_map.fg_default_map desc"
              + "\n    limit 1"
              + "\n  )"
              );
      computedColumnsMap
         .put("spacemr_space_in_map_id_default"
              , ""
              + "\n ("
              + "\n  select spacemr_space_map.spacemr_space_map_id"
              + "\n    from spacemr_space_map"
              + "\n       , spacemr_space sp"
              + "\n   where sp.spacemr_space_id = spacemr_space.spacemr_space_in_id"
              + "\n     and spacemr_space_map.spacemr_space_id = sp.spacemr_space_id"
              + "\n   order by spacemr_space_map.fg_default_map desc"
              + "\n    limit 1"
              + "\n  ) "
              );
      computedColumnsMap
         .put("app_log_last_date"
              , ""
              + "(select date "
              + "   from app_log "
              + "  where app_log.object_type = 1040"
              + "    and app_log.object_id   = spacemr_space.spacemr_space_id"
              + "  order by date desc "
              + "  limit 1"
              + ")"
              );
      computedColumnsMap
         .put("app_log_last_user_name"
              , ""
              + "(select user_name "
              + "   from app_log "
              + "  where app_log.object_type = 1040"
              + "    and app_log.object_id   = spacemr_space.spacemr_space_id"
              + "  order by date desc "
              + "  limit 1"
              + ")"
              );
      computedColumnsMap.put("bookings",  "spacemr_space.code");
      computedColumnsMap.put("presences", "spacemr_space.code");
      computedColumnsMap.put("inventarios", "spacemr_space.code");
   }
}
