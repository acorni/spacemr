package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppMapperJSONArray;
import java.util.Date;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//-
public class App_group extends BaseObjectModel {
   //-
   public Vector<String> getColumns() {
      return(obj_fieldsc);
   }
   //-
   public int insert(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content,getColumns());
      namedParameterJdbcTemplate
         .update(
                 "insert into app_group ("
                 + getSqlStringForInsert_fields(getColumns())
                 + ") values ("
                 + getSqlStringForInsert_values(getColumns())
                 + ")"
                 , np
                 );
      int id = jdbcTemplate
         .queryForObject("select app_group_id from app_group where name = ?"
                         , Integer.class, content.getString("name"))
         ;
      return(id);
   }
   //-
   public JSONObject get(int app_group_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_group where app_group_id = ?"
                         , new Object[] { Integer.valueOf(app_group_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject get(String app_group_name) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_group where name = ?"
                         , new Object[] { app_group_name }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int app_group_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_group where app_group_id = ?"
                         , new Object[] { Integer.valueOf(app_group_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumns());
      np.addValue("app_group_id", content.getInt("app_group_id"));
      String qs = 
         "update app_group set "
         + getSqlStringForUpdate(getColumns())
         + " where app_group_id = :app_group_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int app_group_id) throws Exception {
      app_log.deleteLogs(this, app_group_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_group_id", app_group_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_group  "
                 + " where app_group_id = :app_group_id"
                 , np
                 );
   }
   //-
   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) {
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("name", content.getString("name"));
      np.addValue("nota", content.getString("nota"));
      return(np);
   }
   //-
   public JSONArray getAllGroups() throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select app_group_id, name from app_group order by name", mapper);
      JSONArray rv = mapper.getJSONArray();
      return(rv);
   }
   public JSONArray getAllGroupNames() throws Exception {
      AppMapperJSONArray mapper = new AppMapperJSONArray();
      jdbcTemplate.query("select name from app_group order by name", mapper);
      JSONArray rv = mapper.getJSONArray();
      return(rv);
   }
   //-
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   //-
   public static final String[] obj_fields = {
      "name"
      , "nota"
   };
   //-
   private static final Vector<String> obj_fieldsc = new Vector<String>();
   static  {
      for (String c: obj_fields) {
         obj_fieldsc.add(c);
      }
   }
}
