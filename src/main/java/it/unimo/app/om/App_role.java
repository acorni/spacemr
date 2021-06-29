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
public class App_role extends BaseObjectModel {
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
   @Autowired
   private App_role_permission app_role_permission;
   //-
   @SuppressWarnings("unchecked")
   public int insert(JSONObject content) throws Exception {
      Vector columns = (Vector<String>)getColumnsWrite().clone();
      // columns.add( "foreign_reference_id");
      MapSqlParameterSource np = getMapSqlParameterSource(content, columns);
      KeyHolder keyHolder = new GeneratedKeyHolder();
      String qs = ""
         + "insert into app_role ("
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
   public JSONObject get(int app_role_id) throws Exception {
      return(get(app_role_id, getColumns()));
   }
   //-
   public JSONObject get(int app_role_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "app_role_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "app_role");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from app_role"
                         // + "     , app_role_super"
                         + " where app_role_id = ?"
                         // + "   and app_role_super.app_role_super_id = app_role.app_role_super_id"
                         , new Object[] { Integer.valueOf(app_role_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int app_role_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_role where app_role_id = ?"
                         , new Object[] { Integer.valueOf(app_role_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      rv.put("permissions",app_role_permission.getPermissionForRoleForLog(app_role_id));
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("app_role_id", content.getInt("app_role_id"));
      String qs = 
         "update app_role set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where app_role_id = :app_role_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int app_role_id) throws Exception {
      app_log.deleteLogs(this, app_role_id);
      app_role_permission.deletePermissionsForRole(app_role_id);
      //-
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_role_id", app_role_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_role  "
                 + " where app_role_id = :app_role_id"
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
         case "app_role_id":
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
   public JSONArray getAllRoles() throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select app_role_id, name from app_role order by name", mapper);
      JSONArray rv = mapper.getJSONArray();
      return(rv);
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
      // "app_role_super_name"
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
      // computedColumnsMap.put("app_role_super_name", "app_role_super.name");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = app_role.user_name)"
      //         );
   }
}
