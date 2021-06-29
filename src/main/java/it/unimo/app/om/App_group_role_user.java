package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppMapperJsonForLog;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.Vector;
import java.util.List;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.ResultSet;
import java.sql.SQLException;
//-
public class App_group_role_user extends BaseObjectModel {

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

   //-
   public JSONArray getPermissionForUser(int app_user_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query(""
                         + "\n select g.name as gr, r.name as rl "
                         + "\n   from app_group_role_user gru"
                         + "\n      , app_group g"
                         + "\n      , app_role  r"
                         + "\n  where gru.app_group_id = g.app_group_id"
                         + "\n    and gru.app_role_id  = r.app_role_id"
                         + "\n    and gru.app_user_id = ?"
                         , mapper , app_user_id );
      JSONArray rv = mapper.getJSONArray();
      return(rv);
   }


   public List<String> getPermissionForUserAsVectorOfCSVString(int app_user_id
                                                               , String rPrefix
                                                               ) throws Exception {
      List<String> rv =
         jdbcTemplate.query(""
                            + "\n select g.name as gr, r.name as rl "
                            + "\n   from app_group_role_user gru"
                            + "\n      , app_group g"
                            + "\n      , app_role  r"
                            + "\n  where gru.app_group_id = g.app_group_id"
                            + "\n    and gru.app_role_id  = r.app_role_id"
                            + "\n    and gru.app_user_id = ?"
                            + "\n    and r.name like ?"
                            , new RowMapper<String>() {
                               public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                                  return (rs.getString(1) + "," + rs.getString(2));
                               }
                            }
                            , app_user_id
                            , rPrefix + "%"
                            );
      return(rv);
   }

   public void deleteForUser(int app_user_id) throws Exception {
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_user_id", app_user_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_group_role_user "
                 + " where app_user_id = :app_user_id"
                 , np
                 );
   }

   public void insert(String user_name
                      , String group_name
                      , String role_name
                      ) throws Exception {
      if (jdbcTemplate
          .queryForObject("select count(*) from app_group_role_user "
                          + " where app_group_id = (select app_group_id from app_group where name = ?)"
                          + "   and app_role_id = (select app_role_id from app_role where name = ?)"
                          + "   and app_user_id = (select app_user_id from app_user where user_name = ?)"
                          , Integer.class, group_name, role_name, user_name) < 1
          ) {
         // System.out.println(" adding root user group role permissions ");
         jdbcTemplate.update("\n insert into app_group_role_user ("
                             + "\n      app_group_id"
                             + "\n    , app_role_id"
                             + "\n    , app_user_id"
                             + "\n   ) values ( "
                             + "\n      (select app_group_id from app_group where name = ?)"
                             + "\n     ,(select app_role_id from app_role where name = ?)"
                             + "\n     ,(select app_user_id from app_user where user_name = ?)"
                             + "\n   )"
                             , group_name, role_name, user_name
                             );
      }
   }

   public void delete(String user_name
                      , String group_name
                      , String role_name
                      ) throws Exception {
      jdbcTemplate.update("delete from app_group_role_user "
                          + " where app_group_id = (select app_group_id from app_group where name = ?)"
                          + "   and app_role_id = (select app_role_id from app_role where name = ?)"
                          + "   and app_user_id = (select app_user_id from app_user where user_name = ?)"
                          , group_name, role_name, user_name
                          );
   }
   
   //-
   @SuppressWarnings("unchecked")
   public int insert(JSONObject content) throws Exception {
      Vector columns = (Vector<String>)getColumnsWrite().clone();
      // columns.add( "foreign_reference_id");
      MapSqlParameterSource np = getMapSqlParameterSource(content, columns);
      KeyHolder keyHolder = new GeneratedKeyHolder();
      String qs = ""
         + "insert into app_group_role_user ("
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

   public void insert(int app_user_id
                      , int app_group_id
                      , int app_role_id
                      ) throws Exception {
      if (jdbcTemplate
          .queryForObject("select count(*) from app_group_role_user "
                          + " where app_group_id = ?"
                          + "   and app_role_id = ?"
                          + "   and app_user_id = ?"
                          , Integer.class, app_group_id, app_role_id, app_user_id) < 1
          ) {
         // System.out.println(" adding root user group role permissions ");
         jdbcTemplate.update("\n insert into app_group_role_user ("
                             + "\n      app_group_id"
                             + "\n    , app_role_id"
                             + "\n    , app_user_id"
                             + "\n   ) values ( ?,?,?)"
                             , app_group_id, app_role_id, app_user_id
                             );
      }
   }


   //-
   public void delete(int app_group_role_user_id) throws Exception {
      // app_log.deleteLogs(this, app_group_role_user_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_group_role_user_id", app_group_role_user_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_group_role_user  "
                 + " where app_group_role_user_id = :app_group_role_user_id"
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
         case "app_group_role_user_id":
            break;
         case "app_group_id":
            np.addValue("app_group_id", tools.jsonObject_getInteger(content, "app_group_id"));
            break;
         case "app_role_id":
            np.addValue("app_role_id", tools.jsonObject_getInteger(content, "app_role_id"));
            break;
         case "app_user_id":
            np.addValue("app_user_id", tools.jsonObject_getInteger(content, "app_user_id"));
            break;
         default:
            throw new Exception("column name ["+k+"] not found");
         }
      }
      return(np);
   }

   public JSONObject getLog(int id) throws Exception {
      return(null);
   }



   public Vector<String> getColumns() {
      return(_columns);
   }
   public Vector<String> getColumnsWrite() {
      return(_columnsWrite);
   }
   public Vector<String> getColumnsMattoni() {
      return(_columnsMattoni);
   }
   public HashMap<String,String> getComputedColumnsMap() {
      return(computedColumnsMap);
   }
   private static final Vector<String> _columns = new Vector<String>();
   private static final Vector<String> _columnsWrite = new Vector<String>();
   private static final Vector<String> _columnsMattoni = new Vector<String>();
   private static final HashMap<String,String> computedColumnsMap = new HashMap<String,String>();
   private static final String[] _columnsWrite_a = {
      "app_group_id"
      , "app_role_id"
      , "app_user_id"
   };
   private static final String[] _columns_toGet_a = {
      "app_group_name"
      , "app_role_name"
      , "app_user_user_name"
      , "app_user_first_name"
      , "app_user_last_name"
      , "app_user_created"
      , "app_user_nota"
   };
   static  {
      for (String c: _columnsWrite_a) {
         _columnsWrite.add(c);
         _columns.add(c);
      }
      for (String c: _columns_toGet_a) {
         _columns.add(c);
      }
      for (String c: _columns_toGet_a) {
         _columnsMattoni.add(c);
      }
   }
   static  {
      computedColumnsMap.put("app_group_name", "app_group.name");
      computedColumnsMap.put("app_role_name", "app_role.name");
      computedColumnsMap.put("app_user_user_name", "app_user.user_name");
      computedColumnsMap.put("app_user_first_name", "app_user.first_name");
      computedColumnsMap.put("app_user_last_name", "app_user.last_name");
      computedColumnsMap.put("app_user_created", "app_user.created");
      computedColumnsMap.put("app_user_nota", "app_user.nota");
   }


}
