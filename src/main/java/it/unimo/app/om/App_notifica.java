package it.unimo.app.om;
//-
import it.unimo.app.Application;
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
import org.springframework.context.ApplicationContext;
import java.lang.reflect.Method;
//-
public class App_notifica extends BaseObjectModel {
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
      //-
      String parts[] = content.getString("bean_name").split(":");
      String bean_name   = parts[0];
      String method_name = parts[1];
      //-
      ApplicationContext context = Application.getStaticContext();
      Object existing_bean = context.getBean(bean_name);
      Method method =  existing_bean.getClass().getMethod(method_name, JSONObject.class);
      //-
      Vector columns = (Vector<String>)getColumnsWrite().clone();
      // columns.add( "foreign_reference_id");
      MapSqlParameterSource np = getMapSqlParameterSource(content, columns);
      KeyHolder keyHolder = new GeneratedKeyHolder();
      String qs = ""
         + "insert into app_notifica ("
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

   public void insert(Date date
                      , String bean_name
                      , long object_id
                      , JSONObject json_info
                      ) throws Exception {
      JSONObject notifica = new JSONObject();
      notifica.put("date"     , (date).getTime());
      notifica.put("bean_name" , bean_name);
      notifica.put("object_id", object_id);
      notifica.put("json_info", json_info.toString());
      //-
      insert(notifica);
   }

   
   //-
   //-
   public JSONObject get(int app_notifica_id) throws Exception {
      return(get(app_notifica_id, getColumns()));
   }
   
   //-
   public JSONObject get(int app_notifica_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "app_notifica_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "app_notifica");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from app_notifica"
                         // + "     , app_notifica_super"
                         + " where app_notifica_id = ?"
                         // + "   and app_notifica_super.app_notifica_super_id = app_notifica.app_notifica_super_id"
                         , new Object[] { Integer.valueOf(app_notifica_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   
   //-
   public JSONObject getLog(int app_notifica_id) throws Exception {
      AppMapperJsonForLog mapper = new AppMapperJsonForLog();
      jdbcTemplate.query("select * from app_notifica where app_notifica_id = ?"
                         , new Object[] { Integer.valueOf(app_notifica_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   
   //-
   public void update(JSONObject content) throws Exception {
      String parts[] = content.getString("bean_name").split(":");
      String bean_name   = parts[0];
      String method_name = parts[1];
      //-
      ApplicationContext context = Application.getStaticContext();
      Object existing_bean = context.getBean(bean_name);
      Method method =  existing_bean.getClass().getMethod(method_name, JSONObject.class);
      //-
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("app_notifica_id", content.getInt("app_notifica_id"));
      String qs = 
         "update app_notifica set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where app_notifica_id = :app_notifica_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   
   //-
   public void delete(int app_notifica_id) throws Exception {
      app_log.deleteLogs(this, app_notifica_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_notifica_id", app_notifica_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_notifica  "
                 + " where app_notifica_id = :app_notifica_id"
                 , np
                 );
   }

   //-
   public void delete(JSONObject notifica) throws Exception {
      delete(notifica.getInt("app_notifica_id"));
   }

   //-
   public void deleteByBeanAndObjectId(JSONObject notifica) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select app_notifica_id"
                         + "  from app_notifica"
                         + " where object_id = ?"
                         + "   and bean_name = ?"
                         , new Object[] { Integer.valueOf(notifica.getInt("object_id"))
                                          , notifica.getString("bean_name")}
                         , mapper);
      JSONArray rows = mapper.getJSONArray();
      // System.out.println(" rows: " + rows.toString(2));
      for (int i=0; i<rows.length(); i++) {
         JSONObject row = rows.getJSONObject(i);
         // System.out.println("deleting row: " + row.toString(2));
         delete(row.getInt("app_notifica_id"));
      }
   }

   
   //-
   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) throws Exception {
      MapSqlParameterSource np = new MapSqlParameterSource();
      for (String k: columns) {
         switch (k) {
         case "app_notifica_id":
            break;
         case "date":
            np.addValue("date", tools.jsonObject_getDate(content, "date"));
            break;
         case "bean_name":
            np.addValue("bean_name", content.optString("bean_name", null));
            break;
         case "object_id":
            np.addValue("object_id", tools.jsonObject_getInteger(content, "object_id"));
            break;
         case "json_info":
            np.addValue("json_info", content.getString("json_info"));
            break;
         case "fg_serving":
            np.addValue("fg_serving", tools.jsonObject_getBoolean(content, "fg_serving"));
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

   public boolean do_serve(JSONObject the_notifica) throws Exception {
      String parts[] = the_notifica.getString("bean_name").split(":");
      String bean_name   = parts[0];
      String method_name = parts[1];
      //-
      the_notifica.put("fg_serving", Boolean.TRUE);
      update(the_notifica);
      //-
      //-
      ApplicationContext context = Application.getStaticContext();
      Object existing_bean = context.getBean(bean_name);
      Method method =  existing_bean.getClass().getMethod(method_name, JSONObject.class);
      //-
      Object o = method.invoke(existing_bean, the_notifica);
      boolean rv = ((Boolean)o).booleanValue();
      //-
      return(rv);
   }

   public JSONObject getFirstNotServed() throws Exception {
      Vector<String> columns = getColumns();
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "app_notifica_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "app_notifica");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from app_notifica"
                         + " where fg_serving = false"
                         + "  limit 1"
                         , new Object[] {  }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   
   /** serves all notificas, use from a transaction
    * e.g. from Spacemr_notifica_manager */
   public boolean do_serve_notificas() throws Exception {
      jdbcTemplate.update("update app_notifica set fg_serving = false");
      //-
      JSONObject the_notifica = getFirstNotServed();
      while(the_notifica != null) {
         do_serve(the_notifica);
         the_notifica = getFirstNotServed();
      }
      //-
      return(true);
   }

   
   private static final Vector<String> _columns = new Vector<String>();
   private static final Vector<String> _columnsHiddenInList = new Vector<String>();
   private static final Vector<String> _columnsForList = new Vector<String>();
   private static final Vector<String> _columnsWrite = new Vector<String>();
   private static final HashMap<String,String> computedColumnsMap = new HashMap<String,String>();
   private static final String[] _columnsWrite_a = {
      "date"
      , "bean_name"
      , "object_id"
      , "json_info"
      , "fg_serving"
   };
   private static final String[] _columns_toGet_a = {
      // "app_notifica_super_name"
   };
   private static final String[] _columns_hidden_in_list = {
      // "app_notifica_super_id"
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
      // computedColumnsMap.put("app_notifica_super_name", "app_notifica_super.name");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = app_notifica.user_name)"
      //         );
   }
}
