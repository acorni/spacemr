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
public class Spacemr_space_people_type extends BaseObjectModel {
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
         + "insert into spacemr_space_people_type ("
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
   public JSONArray getAllForCombo() throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select spacemr_space_people_type_id, name"
                         + "  from spacemr_space_people_type"
                         , new Object[] { }
                         , mapper);
      JSONArray rv = mapper.getJSONArray();
      return(rv);
   }

   
   //-
   //-
   public JSONObject get(int spacemr_space_people_type_id) throws Exception {
      return(get(spacemr_space_people_type_id, getColumns()));
   }
   //-
   public JSONObject get(int spacemr_space_people_type_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_space_people_type_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_space_people_type");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_space_people_type"
                         // + "     , spacemr_space_people_type_super"
                         + " where spacemr_space_people_type_id = ?"
                         // + "   and spacemr_space_people_type_super.spacemr_space_people_type_super_id = spacemr_space_people_type.spacemr_space_people_type_super_id"
                         , new Object[] { Integer.valueOf(spacemr_space_people_type_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int spacemr_space_people_type_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from spacemr_space_people_type where spacemr_space_people_type_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_space_people_type_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("spacemr_space_people_type_id", content.getInt("spacemr_space_people_type_id"));
      String qs = 
         "update spacemr_space_people_type set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where spacemr_space_people_type_id = :spacemr_space_people_type_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int spacemr_space_people_type_id) throws Exception {
      app_log.deleteLogs(this, spacemr_space_people_type_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("spacemr_space_people_type_id", spacemr_space_people_type_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from spacemr_space_people_type  "
                 + " where spacemr_space_people_type_id = :spacemr_space_people_type_id"
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
         case "spacemr_space_people_type_id":
            break;
         case "name":
            np.addValue("name", content.optString("name", null));
            break;
         case "description":
            np.addValue("description", content.optString("description", null));
            break;
         case "fg_is_a_seat":
            np.addValue("fg_is_a_seat", tools.jsonObject_getBoolean(content, "fg_is_a_seat"));
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
      , "description"
      , "fg_is_a_seat"
      , "nota"
   };
   private static final String[] _columns_toGet_a = {
      // "spacemr_space_people_type_super_name"
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
      // computedColumnsMap.put("spacemr_space_people_type_super_name", "spacemr_space_people_type_super.name");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = spacemr_space_people_type.user_name)"
      //         );
   }
}
