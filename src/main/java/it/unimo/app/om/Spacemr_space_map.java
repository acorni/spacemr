package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppMapperJsonForLog;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.HashMap;
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
public class Spacemr_space_map extends BaseObjectModel {
   //-
   //-
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private App_file app_file;
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
         + "insert into spacemr_space_map ("
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
   public JSONObject get(int spacemr_space_map_id) throws Exception {
      return(get(spacemr_space_map_id, getColumns()));
   }
   //-
   public JSONObject get(int spacemr_space_map_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_space_map_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_space_map");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_space_map"
                         + "   left join spacemr_space  on spacemr_space_map.spacemr_space_id  = spacemr_space.spacemr_space_id"
                         + "   left join app_group      on app_group.app_group_id = spacemr_space.app_group_id"
                         + " where spacemr_space_map_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_space_map_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int spacemr_space_map_id) throws Exception {
      AppMapperJsonForLog mapper = new AppMapperJsonForLog();
      jdbcTemplate.query("select * from spacemr_space_map where spacemr_space_map_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_space_map_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("spacemr_space_map_id", content.getInt("spacemr_space_map_id"));
      String qs = 
         "update spacemr_space_map set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where spacemr_space_map_id = :spacemr_space_map_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int spacemr_space_map_id) throws Exception {
      app_log.deleteLogs(this, spacemr_space_map_id);
      app_file.deleteFiles(this, spacemr_space_map_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("spacemr_space_map_id", spacemr_space_map_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from spacemr_space_map  "
                 + " where spacemr_space_map_id = :spacemr_space_map_id"
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
         case "spacemr_space_map_id":
            break;
         case "spacemr_space_id":
            np.addValue("spacemr_space_id", tools.jsonObject_getInteger(content, "spacemr_space_id"));
            break;
         case "description":
            np.addValue("description", content.optString("description", null));
            break;
         case "fg_default_map":
            np.addValue("fg_default_map", tools.jsonObject_getBoolean(content, "fg_default_map"));
            break;
         case "info":
            np.addValue("info", content.getString("info"));
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
      "spacemr_space_id"
      , "description"
      , "fg_default_map"
      , "info"
      , "nota"
   };
   private static final String[] _columns_toGet_a = {
      "spacemr_space_code"
      , "spacemr_space_description"
      , "app_group_name"
   };
   private static final String[] _columns_hidden_in_list = {
      "spacemr_space_id"
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
      computedColumnsMap.put("spacemr_space_code", "spacemr_space.code");
      computedColumnsMap.put("spacemr_space_description", "spacemr_space.description");
      computedColumnsMap.put("app_group_name", "app_group.name");
   }
}
