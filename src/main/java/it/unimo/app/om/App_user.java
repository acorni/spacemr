package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppMapperVectorString;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.Vector;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//-
public class App_user extends BaseObjectModel {
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private Tools tools;
   @Autowired
   private App_group_role_user app_group_role_user;


   public static final String[] obj_fields = {
      "user_name"
      , "first_name"
      , "last_name"
      , "email"
      , "nota"
   };
   private static final String _fieldsToGet =
      "app_user_id, user_name, first_name, last_name, email, created, nota";

   private static final Vector<String> obj_fieldsc = new Vector<String>();
   static  {
      for (String c: obj_fields) {
         obj_fieldsc.add(c);
      }
   }

   public Vector<String> getColumns() {
      return(obj_fieldsc);
   }

   public int insert(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content,getColumns());
      @SuppressWarnings("unchecked")
      Vector<String> columns = (Vector<String>)(getColumns().clone());
      columns.add("created");
      np.addValue("created", new Date());
      namedParameterJdbcTemplate
         .update(
                 "insert into app_user ("
                 + getSqlStringForInsert_fields(columns)
                 + ") values ("
                 + getSqlStringForInsert_values(columns)
                 + ")"
                 , np
                 );
      int id = jdbcTemplate
         .queryForObject("select app_user_id from app_user where user_name = ?"
                         , Integer.class, content.getString("user_name"))
         ;
      return(id);
   }

   public void setPassword(int app_user_id
                           , String newPassword
                           ) throws Exception {
      //-
      //-
      byte[] random = new byte[20];
      new Random().nextBytes(random);
      String password_salt = tools.base64Encode(random);
      //-
      String password = 
         tools.stringMd5ToBase64(newPassword
                                 + password_salt
                                 );
      //-
      //-
      String qs = "update app_user set "
                 + "\n   password = ?"
                 + "\n , password_salt = ?"
                 + "\n where app_user_id = ?"
         ;
      // System.out.println("qs: " + qs);
      jdbcTemplate.update(qs,  password, password_salt, app_user_id);
   }


   public void setLdap_info(String user_name
                           , String ldap_info
                           ) throws Exception {
      //-
      //-
      String qs = "update app_user set "
                 + "\n   ldap_info = ?"
                 + "\n where user_name = ?"
         ;
      // System.out.println("qs: " + qs);
      jdbcTemplate.update(qs,  ldap_info, user_name);
   }

   public boolean checkPassword(String otherPassword
                                , String password
                                , String password_salt
                                ) throws Exception {
      //-
      //-
      //-
      String cpassword = 
         tools.stringMd5ToBase64(""
                                 + otherPassword
                                 + password_salt
                                 );
      //-
      return(cpassword.equals(password));
   }

   public JSONObject get(int app_user_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select " + _fieldsToGet + " from app_user where app_user_id = ?"
                         , new Object[] { Integer.valueOf(app_user_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   public JSONObject getWithFields(int app_user_id, String field_list) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select " + _fieldsToGet + ", " + field_list
                         + " from app_user where app_user_id = ?"
                         , new Object[] { Integer.valueOf(app_user_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   public JSONObject get(String user_name) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select " + _fieldsToGet + " from app_user where user_name = ?", mapper, user_name);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   public JSONObject getWithFields(String user_name, String field_list) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select " + _fieldsToGet + ", " + field_list
                         + " from app_user where user_name = ?"
                         , mapper
                         , user_name);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   public JSONObject getLog(int app_user_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select " + _fieldsToGet + " from app_user where app_user_id = ?"
                         , new Object[] { Integer.valueOf(app_user_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      rv.put("permissions",app_group_role_user.getPermissionForUser(app_user_id));
      return(rv);
   }

   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content,getColumns());
      np.addValue("app_user_id", content.getInt("app_user_id"));
      @SuppressWarnings("unchecked")
      Vector<String> columnsToUpdate = (Vector<String>)getColumns().clone();
      columnsToUpdate.remove("user_name");
      String qs = 
         "update app_user set "
         + getSqlStringForUpdate(columnsToUpdate)
         + " where app_user_id = :app_user_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }

   public void delete(int app_user_id) throws Exception {
      app_log.deleteLogs(this, app_user_id);
      app_group_role_user.deleteForUser(app_user_id);
      deleteUserProperty(app_user_id);
      //-
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_user_id", app_user_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_user  "
                 + " where app_user_id = :app_user_id"
                 , np
                 );
   }


   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) {
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("user_name", content.getString("user_name"));
      np.addValue("first_name", content.getString("first_name"));
      np.addValue("last_name", content.getString("last_name"));
      np.addValue("email", content.getString("email"));
      np.addValue("nota", content.getString("nota"));
      return(np);
   }
      
      
   public void deleteUserProperty(int app_user_id) throws Exception {
      jdbcTemplate
         .update("delete from app_user_property where app_user_id = ?", app_user_id);
   }

   public void deleteUserPropertyWithNameLike(int app_user_id, String like) throws Exception {
      jdbcTemplate
         .update("delete from app_user_property where app_user_id = ? and name like ?"
                 , app_user_id, like);
   }

   /** 
    * a null value delete the record */
   public void setUserProperty(int app_user_id, String name, String value) throws Exception {
      // System.out.println(" setting["+app_user_id+"].["+name+"]: " + value);
      int rows = jdbcTemplate
         .queryForObject("select count(*) from app_user_property where app_user_id = ? and name = ?"
                         , Integer.class, app_user_id, name).intValue();
      if (value == null) {
         if (rows == 0) {
            // ok
         } else {
            jdbcTemplate
               .update("delete from app_user_property where app_user_id = ? and name = ?"
                       , app_user_id, name);
         }
      } else {
         if (rows == 0) {
            jdbcTemplate.update("insert into app_user_property (app_user_id, name , value) "
                                + " values (?,?,?)"
                                , app_user_id, name, value);
         } else {
            jdbcTemplate.update("update app_user_property set value = ? "
                                + " where app_user_id = ? and name = ?"
                                , value, app_user_id, name);
            // System.out.println(" do update.");
         }
      }
   }

   //-
   public String getUserPropertyAsString(int app_user_id, String name) throws Exception {
      String rv = null;
      int rows = 
         jdbcTemplate
         .queryForObject("select count(*) from app_user_property where app_user_id = ? and name = ?"
                         , Integer.class, app_user_id, name).intValue();
      if (rows > 0) {
         rv = jdbcTemplate
            .queryForObject("select value from app_user_property where app_user_id = ? and name = ?"
                            , String.class, app_user_id, name);
      }
      return(rv);
   }
   
   public JSONObject getUserPropertyAsJSONObject(int app_user_id, String name) throws Exception {
      JSONObject rv = null;
      String s = getUserPropertyAsString(app_user_id, name);
      if (s != null) {
         rv = new JSONObject(s);
      }
      return(rv);
   }

   public JSONObject getAllUserPropertyAsJSONObject(int app_user_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select name, value from app_user_property where app_user_id = ?"
                         , new Object[] { Integer.valueOf(app_user_id) }
                         , mapper);
      JSONObject rv = mapper.getJSON();
      return(rv);
   }

   public JSONArray getAllUserPropertyWithNameLike(int app_user_id, String like) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select name, value from app_user_property "
                         + " where app_user_id = ? "
                         + "   and name like ? "
                         , mapper
                         , app_user_id
                         , like
                         );
      JSONArray rv = mapper.getJSONArray();
      return(rv);
   }

   public void setAllUserPropertyAsJSONObject(int app_user_id, JSONObject content) throws Exception {
      //-
      Vector<String> oldnames = null;
      {
         AppMapperVectorString mapper = new AppMapperVectorString();
         jdbcTemplate.query("select name from app_user_property where app_user_id = ?"
                            , new Object[] { Integer.valueOf(app_user_id) }
                            , mapper);
         oldnames = mapper.getVector();
      }
      // System.out.println(" oldnames: " + oldnames);
      //-
      JSONArray rows = content.getJSONArray("rows");
      for (int i=0; i<rows.length(); i++ ) {
         JSONObject row = rows.getJSONObject(i);
         String name  = row.getString("name");
         String value = row.getString("value");
         if (value != null && value.equals("")) {
            value = null;
         }
         // System.out.println(" name: " + name);
         //-
         oldnames.remove(name);
         //-
         setUserProperty(app_user_id, name, value);
      }
      //-
      //- delete objects not in list
      //-
      for (String nameToDelete: oldnames) {
         setUserProperty(app_user_id, nameToDelete, null);
      }
   }


   public void setUserPropertyAsJSONObject(int app_user_id, String name, JSONObject jo) throws Exception {
      String s = null;
      if (jo != null) {
         s = jo.toString();
      }
      setUserProperty(app_user_id, name, s);
   }

}
