package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
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
public class App_system_log extends BaseObjectModel {
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
         + "insert into app_system_log ("
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
   public JSONObject get(int app_system_log_id) throws Exception {
      return(get(app_system_log_id, getColumns()));
   }
   //-
   public JSONObject get(int app_system_log_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "app_system_log_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "app_system_log");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from app_system_log"
                         // + "     , app_system_log_super"
                         + " where app_system_log_id = ?"
                         // + "   and app_system_log_super.app_system_log_super_id = app_system_log.app_system_log_super_id"
                         , new Object[] { Integer.valueOf(app_system_log_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int app_system_log_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_system_log where app_system_log_id = ?"
                         , new Object[] { Integer.valueOf(app_system_log_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("app_system_log_id", content.getInt("app_system_log_id"));
      String qs = 
         "update app_system_log set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where app_system_log_id = :app_system_log_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int app_system_log_id) throws Exception {
      app_log.deleteLogs(this, app_system_log_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_system_log_id", app_system_log_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_system_log  "
                 + " where app_system_log_id = :app_system_log_id"
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
         case "app_system_log_id":
            break;
         case "date":
            np.addValue("date", tools.jsonObject_getDate(content, "date"));
            break;
         case "user_name":
            np.addValue("user_name", content.optString("user_name", null));
            break;
         case "category":
            np.addValue("category", content.optString("category", null));
            break;
         case "value":
            np.addValue("value", content.getString("value"));
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
      "date"
      , "user_name"
      , "category"
      , "value"
   };
   private static final String[] _columns_toGet_a = {
      // "app_system_log_super_name"
   };
   private static final String[] _columns_hidden_in_list = {
      // "app_system_log_super_id"
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
      // computedColumnsMap.put("app_system_log_super_name", "app_system_log_super.name");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = app_system_log.user_name)"
      //         );
   }
}
