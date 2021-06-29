package it.unimo.app.om;
//-
import it.unimo.app.tools.*;
import it.unimo.app.controller.AppControllerTools;
import it.unimo.app.controller.AppControllerTableMattoni;
import java.util.Date;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//-
public class App_log extends BaseObjectModel {
   //-

   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(App_log.class);
   
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private Tools tools;
   @Autowired
   private AppControllerTools appControllerTools;
   //-
   public App_log() {
      registerClass(App_user.class,            10);
      registerClass(App_permission.class,      20);
      registerClass(App_role.class,            30);
      registerClass(App_group.class,           40);
      registerClass(App_system_property.class, 50);
      registerClass(App_system_log.class,      60);
      registerClass(App_file.class,            70);
      registerClass(App_notifica.class,        80);
      registerClass(Spacemr_people.class,      1010);
      registerClass(Spacemr_space_type.class,  1020);
      registerClass(Spacemr_space_people_type.class, 1030);
      registerClass(Spacemr_space.class,       1040);
      registerClass(Spacemr_space_people.class, 1050);
      registerClass(Spacemr_inventario.class,  1060);
      registerClass(Spacemr_space_map.class,   1070);
      registerClass(Spacemr_space_people_book.class,     1080);
      registerClass(Spacemr_space_user_presence.class,   1090);
   }

   public int insert(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content,getColumns());
      String qs = ""
         + "insert into app_log ("
         + getSqlStringForInsert_fields(getColumns())
         + ") values ("
         + getSqlStringForInsert_values(getColumns())
         + ")"
         ;
      KeyHolder keyHolder = new GeneratedKeyHolder();
      namedParameterJdbcTemplate.update(qs , np, keyHolder );
      int id = keyHolder.getKey().intValue();
      return(id);
   }

   public JSONObject get(int app_log_id) throws Exception {
      throw(new Exception("not implemented"));
   }

   public JSONObject getLog(int app_log_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_log where app_log_id = ?"
                         , new Object[] { Integer.valueOf(app_log_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }

   public void update(JSONObject content) throws Exception {
      throw(new Exception("not implemented"));
   }

   public void delete(int app_log_id) throws Exception {
      throw(new Exception("not implemented"));
   }

   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) {
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("date", content.has("date")? new Date(content.getLong("date")):null);
      np.addValue("object_type", content.has("object_type")? Integer.valueOf(content.getInt("object_type")):null);
      np.addValue("object_id", content.has("object_id")? Integer.valueOf(content.getInt("object_id")):null);
      np.addValue("user_name", content.getString("user_name"));
      np.addValue("parameter_values", content.getString("parameter_values"));
      return(np);
   }


   public void writeLog(BaseObjectModel om
                        , int id
                        , String user
                        , JSONObject valuesNew
                        ) throws Exception {
      JSONObject valuesOld  = om.getLog(id);
      //System.out.println("  old: " + valuesOld.toString(2));
      //System.out.println("  new: " + valuesNew.toString(2));
      //-
      //- The problem. In this point I do not know which 
      //-
      //- Workaround.
      //- write a two JSONObject filter
      //- that
      //- where with the same key matches a long and a string
      //- and the string has date pattern
      //- transform the string to long ".getTime()" format
      //- and then compare the valuesNew with the transformed Obj
      //-
      JSONObject valuesNew_transformed = tools.jsonObject_clone(valuesNew);
      for (String k: JSONObject.getNames(valuesNew)) {
         Object oOld = valuesOld.opt(k);
         Object oNew = valuesNew.opt(k);
         if(oOld != null && oNew != null) {
            // System.out.println(" oNew.getClass(): " + oNew.getClass().getName());
            if (oOld instanceof String
                && oNew instanceof Long) {
               long   oNewl = ((Long)oNew).longValue();
               String oOlds = (String)oOld;
               //System.out.println(" oNewl: " + oNewl + " oOlds: " + oOlds);
               //System.out.println(" oOlds.length(): " + oOlds.length());
               if(oOlds.length() == 8){
                  String s = AppControllerTableMattoni.getMvcDateFormat().format(new Date(oNewl));
                  //System.out.println(" s8: " + s);
                  valuesNew_transformed.put(k, s);
               } else if(oOlds.length() == 14) {
                  String s = AppControllerTableMattoni.getMvcTimestampFormat().format(new Date(oNewl));
                  //System.out.println(" s14: " + s);
                  valuesNew_transformed.put(k, s);
               }
            }
         }
      }
      //System.out.println("  valuesNew_transformed: " + valuesNew_transformed.toString(2));
      //-
      //-
      //-
      JSONObject valuesDiff = tools.jsonChanges(valuesNew_transformed, valuesOld);
      if (valuesDiff != null && valuesDiff.has("changed")) {
         //System.out.println("  valuesDiff: " + valuesDiff.toString(2));
         valuesDiff = valuesDiff.getJSONObject("changed");
      } else {
         valuesDiff = new JSONObject();
      }
      //-
      JSONObject o = new JSONObject();
      o.put("date", (new Date()).getTime());
      // System.out.println(" -- om.getClass().getName(): " + om.getClass().getName());
      o.put("object_type", getClassNumber(om.getClass()));
      o.put("object_id", id);
      o.put("user_name", user);
      o.put("parameter_values", valuesDiff.toString());
      //-
      int logId = insert(o);
      // System.out.println("  --- new log id: " + logId);
   }

   public void deleteLogs(BaseObjectModel om, int id) throws Exception {
      //- do nothing, by now!
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("object_type", getClassNumber(om.getClass()));
      np.addValue("object_id",   id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_log  "
                 + " where object_type = :object_type"
                 + "   and object_id   = :object_id"
                 , np
                 );
   }




   public void log_list(HttpSession session
                        , HttpServletResponse response
                        , JSONObject qparams
                        , BaseObjectModel om
                        , int object_id 
                        ) throws Exception {
      //-
      String status = null;
      JSONObject rv = new JSONObject();
      //-
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int object_type = object_type = getClassNumber(om.getClass());
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> defaultColumns = (Vector<String>)getColumns().clone();; 
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)getColumns().clone();
      checkVector.remove("object_type");
      checkVector.remove("object_id");
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_log"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("app_log_id", "id");
      qtm.setCustomHeaderAndAddColumn("object_type", "id");
      qtm.setCustomHeaderAndAddColumn("object_id", "id");
      //-
      if (qparams.has("customHeaders")) {
         JSONObject customHeaders = qparams.getJSONObject("customHeaders");
         Iterator keys = customHeaders.keys();
         while (keys.hasNext()) {
            String k = (String)keys.next();
            String headerType = customHeaders.getString(k);
            qtm.setCustomHeader(k, headerType);
         }
      }
      //-
      //-
      AppListTableMapper mapper = qtm.getMapperJson();
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns();
      //-
      //- where
      //-
      {
         qswhere.append("     app_log.object_id   = :object_id ");
         qswhere.append(" and app_log.object_type = :object_type ");
         qsargs.put("object_id", Integer.valueOf(object_id));
         qsargs.put("object_type", Integer.valueOf(object_type));
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from app_log");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      rv.put("list",       mapper.getJSON());
      rv.put("qparams",    qparams);
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   public Class getClass(int number) throws Exception {
      Class rv = typesByNumber.get(Integer.valueOf(number));
      return(rv);
   }

   public int getClassNumber(Class theClass) throws Exception {
      int rv = -1;
      Integer n = typesByClass.get(theClass);
      // if (n != null) {   // I want the exception to be thrown!
      rv = n.intValue();
      // }
      return(rv);
   }

   public void registerClass(Class theClass
                             , int number
                             )  {
      try {
      if (typesByClass.get(theClass) != null) {
         // ok
      } else {
         Integer n = Integer.valueOf(number);
         Class   c = typesByNumber.get(n);
         if ( c != null) {
            String m = ""
               + "Error registering ObjectModel Id Number..."
               + "\nThe number ["+number+"]"
               + "\nis already assigned to the class ["+c.getName()+"]"
               ;
            throw(new Exception(m));
         } else {
            typesByClass.put(theClass, n);
            typesByNumber.put(n, theClass);
         }
      }
      } catch (Exception e ) {
         // e.printStackTrace(System.out);
         logger.error(tools.stringStackTrace(e));
      }
   }


   public static final String[] obj_fields = {
      "date"
      , "object_type"
      , "object_id"
      , "user_name"
      , "parameter_values"
   };

   private static final Vector<String> obj_fieldsc = new Vector<String>();
   static  {
      for (String c: obj_fields) {
         obj_fieldsc.add(c);
      }
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
      "date"
      , "object_type"
      , "object_id"
      , "user_name"
      , "parameter_values"
   };
   private static final String[] _columns_toGet_a = {
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

   private static HashMap<Class, Integer> typesByClass  = new HashMap<Class, Integer>();
   private static HashMap<Integer, Class> typesByNumber = new HashMap<Integer, Class>();



}
