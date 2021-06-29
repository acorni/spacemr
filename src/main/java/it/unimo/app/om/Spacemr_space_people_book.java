package it.unimo.app.om;

import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppMapperJsonForLog;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.Workflow;
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
import it.unimo.app.tools.Spacemr_email_manager;
import java.util.Calendar;


//-
public class Spacemr_space_people_book
   extends BaseObjectModel  {
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
   private App_notifica app_notifica;
   @Autowired
   private Spacemr_email_manager spacemr_email_manager;
   @Autowired
   private App_system_property app_system_property;
   //-

   public String getWorkflowId() {
      return("spacemr_space_people_book_workflow");
   }


   @SuppressWarnings("unchecked")
   public int insert(JSONObject content) throws Exception {
      Vector columns = (Vector<String>)getColumnsWrite().clone();
      // columns.add( "foreign_reference_id");
      MapSqlParameterSource np = getMapSqlParameterSource(content, columns);
      KeyHolder keyHolder = new GeneratedKeyHolder();
      //-
      //- checks
      content_checks(content);
      //-
      String qs = ""
         + "insert into spacemr_space_people_book ("
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
   
   private void content_checks(JSONObject content) throws Exception {
      {
         //- repetition
         String s = content.getString("repetition");
         if ( !("d".equals(s) || "w".equals(s))) {
            content.put("repetition", "d");
         }
      }
      {
         //- date_from check
         if (content.isNull("date_from")) {
            String m = "date_from can not be null";
            throw(new Exception(m));
         }
      }
      {
         //- date_to check
         if (content.isNull("date_to")) {
            //- ok
         } else {
            Date date_from = new Date(content.getLong("date_from"));
            Date date_to   = new Date(content.getLong("date_to"));
            //-
            if (date_from.getTime() > date_to.getTime()) {
               String m = "date_from must be bigger or equal than date_to: "
                  + "["+date_from+"] >= ["+date_to+"]";
               throw(new Exception(m));
            }
            if (content.getString("repetition").equals("w")) {
               Calendar c = Calendar.getInstance();
               c.setTime(date_from);
               int day_from = c.get(Calendar.DAY_OF_WEEK);
               //-
               c.setTime(date_to);
               int day_to = c.get(Calendar.DAY_OF_WEEK);
               //-
               boolean changed = false;
               while(day_from != day_to) {
                  c.add(Calendar.DATE, -1);
                  day_to = c.get(Calendar.DAY_OF_WEEK);
                  changed = true;
                  // System.out.println(" -- changed! day_to: " + day_to);
               }
               if (changed) {
                  date_to = c.getTime();
                  content.put("date_to", date_to.getTime());
               }
            }
         }
      }
   }

   
   //-
   //-
   public JSONObject get(int spacemr_space_people_book_id) throws Exception {
      return(get(spacemr_space_people_book_id, getColumns()));
   }
   //-
   public JSONObject get(int spacemr_space_people_book_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_space_people_book_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_space_people_book");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_space_people_book"
                         + "   left join spacemr_space  on spacemr_space_people_book.spacemr_space_id  = spacemr_space.spacemr_space_id"
                         + "   left join spacemr_people on spacemr_space_people_book.spacemr_people_id = spacemr_people.spacemr_people_id"
                         + "   left join spacemr_people as spacemr_people__responsible on spacemr_space_people_book.spacemr_responsible_id = spacemr_people__responsible.spacemr_people_id" 
                         + " where spacemr_space_people_book_id = ?"
                         // + "   and spacemr_space_people_book_super.spacemr_space_people_book_super_id = spacemr_space_people_book.spacemr_space_people_book_super_id"
                         , new Object[] { Integer.valueOf(spacemr_space_people_book_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int spacemr_space_people_book_id) throws Exception {
      AppMapperJsonForLog mapper = new AppMapperJsonForLog();
      jdbcTemplate.query("select * from spacemr_space_people_book where spacemr_space_people_book_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_space_people_book_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      //- checks
      content_checks(content);
      //-
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("spacemr_space_people_book_id", content.getInt("spacemr_space_people_book_id"));
      String qs = 
         "update spacemr_space_people_book set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where spacemr_space_people_book_id = :spacemr_space_people_book_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int spacemr_space_people_book_id) throws Exception {
      app_log.deleteLogs(this, spacemr_space_people_book_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("spacemr_space_people_book_id", spacemr_space_people_book_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from spacemr_space_people_book  "
                 + " where spacemr_space_people_book_id = :spacemr_space_people_book_id"
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
         case "spacemr_space_people_book_id":
            break;
         case "spacemr_people_id":
            np.addValue("spacemr_people_id", tools.jsonObject_getInteger(content, "spacemr_people_id"));
            break;
         case "spacemr_responsible_id":
            np.addValue("spacemr_responsible_id", tools.jsonObject_getInteger(content, "spacemr_responsible_id"));
            break;
         case "spacemr_space_id":
            np.addValue("spacemr_space_id", tools.jsonObject_getInteger(content, "spacemr_space_id"));
            break;
         case "reason":
            np.addValue("reason", content.optString("reason", null));
            break;
         case "people_number":
            np.addValue("people_number", tools.jsonObject_getInteger(content, "people_number"));
            break;
         case "date_from":
            np.addValue("date_from", tools.jsonObject_getDate(content, "date_from"));
            break;
         case "date_to":
            np.addValue("date_to", tools.jsonObject_getDate(content, "date_to"));
            break;
         case "hour_from":
            np.addValue("hour_from", tools.jsonObject_getInteger(content, "hour_from"));
            break;
         case "hour_to":
            np.addValue("hour_to", tools.jsonObject_getInteger(content, "hour_to"));
            break;
         case "repetition":
            np.addValue("repetition", content.optString("repetition", null));
            break;
         case "stato":
            np.addValue("stato", content.optString("stato", null));
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

   public boolean workflow_write_notifica(Workflow wf
                                          , String stato_corrente
                                          , String stato_nuovo
                                          , JSONObject obj
                                          ) throws Exception {
      
      // System.out.println(" -- hello from workflow_write_notifica!"+ " obj:\n" + obj.toString(2));
      //-
      String bean_name = "spacemr_space_people_book:notifica_serve";
      long   object_id = obj.getInt("spacemr_space_people_book_id");
      //-
      app_notifica.insert(new Date(), bean_name, object_id, obj);
      return(true);
   }
   
   public boolean notifica_serve(JSONObject notifica) throws Exception {
      //-
      //-
      //- update spacemr_people set email='alberto.corni@unimore.it';
      //-
      //-
      System.out.println(" -- serving notifica\n" + notifica.toString(2));
      JSONObject obj = get(notifica.getInt("object_id"));
      //-
      if (obj == null) {
         System.out.println(" -- object not found reference for:\n"
                            + notifica.toString(2));
         app_notifica.deleteByBeanAndObjectId(notifica);
      } else {
         //-
         String date_from = tools.getItalianDateAsString(new Date(obj.getLong("date_from")));
         String date_to   = tools.getItalianDateAsString(new Date(obj.getLong("date_to")));
         String to = ""
            + obj.getString("spacemr_responsible_email")
            + ", " + obj.getString("spacemr_people_email")
            ;
         String subject = "prenotazioni - "+obj.getString("stato")+" - "
            + " Presenza "+obj.getString("spacemr_people_first_name")+" "+obj.getString("spacemr_people_last_name")+" dal "+ date_from+" al " + date_to
            ;
         //-
         System.out.println(" ----- to:" + to + " - subject:" + subject);
         //-
         //-
         String template =
            app_system_property.getAsString("spacemr_space_people_book_email_html_template");
         //-
         HashMap<String, Object> root = new HashMap<String, Object>();
         root.put("spacemr_space_people_book", obj);
         root.put("spacemr_notifica", notifica);
         root.put("date_from", date_from);
         root.put("date_to",   date_to);
         root.put("tools",     tools);
         //-
         String m = spacemr_email_manager.applyTemplate(template, root);
         //-
         //-
         //-
         boolean debug = false;
         //-
         //-
         //-
         if(debug) {
            //-
            //-  see Spacemr_notifica_manager.java
            //-
            System.out.println("m:\n" + m);
         } else {
            spacemr_email_manager.doSendEmailHtml(to, subject, m);
            // System.out.println(m);
            // System.out.println("\n\n");
            app_notifica.deleteByBeanAndObjectId(notifica);
         }
      }
      return(true);
   }
   
   private static final Vector<String> _columns = new Vector<String>();
   private static final Vector<String> _columnsHiddenInList = new Vector<String>();
   private static final Vector<String> _columnsForList = new Vector<String>();
   private static final Vector<String> _columnsWrite = new Vector<String>();
   private static final HashMap<String,String> computedColumnsMap = new HashMap<String,String>();
   private static final String[] _columnsWrite_a = {
      "spacemr_people_id"
      , "spacemr_responsible_id"
      , "spacemr_space_id"
      , "reason"
      , "people_number"
      , "date_from"
      , "date_to"
      , "repetition"
      , "stato"
      , "nota"
   };
   private static final String[] _columns_toGet_a = {
      "spacemr_people_username"
      , "spacemr_people_first_name"
      , "spacemr_people_last_name"
      , "spacemr_people_role"
      , "spacemr_people_email"
      , "spacemr_responsible_username"
      , "spacemr_responsible_first_name"
      , "spacemr_responsible_last_name"
      , "spacemr_responsible_role"
      , "spacemr_responsible_email"
      , "spacemr_space_code"
      , "spacemr_space_description"
      , "transactions"
      , "app_log_last_date"
      , "app_log_last_user_name"
      , "spacemr_space_in_map_id_default"
      , "spacemr_space_people_names"
   };
   private static final String[] _columns_hidden_in_list = {
      "spacemr_space_id"
      , "spacemr_people_id"
      , "spacemr_responsible_id"
      , "stato_hidden"
      , "responsible_username_hidden"
      , "spacemr_space_in_map_id_default"
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
      computedColumnsMap.put("stato_hidden", "stato");
      computedColumnsMap.put("responsible_username_hidden", "spacemr_people__responsible.username");
      computedColumnsMap.put("transactions", "stato");
      computedColumnsMap.put("spacemr_people_username", "spacemr_people.username");
      computedColumnsMap.put("spacemr_people_first_name", "spacemr_people.first_name");
      computedColumnsMap.put("spacemr_people_last_name", "spacemr_people.last_name");
      computedColumnsMap.put("spacemr_people_role", "spacemr_people.role");
      computedColumnsMap.put("spacemr_people_email", "spacemr_people.email");
      computedColumnsMap.put("spacemr_space_code", "spacemr_space.code");
      computedColumnsMap.put("spacemr_space_description", "spacemr_space.description");
      computedColumnsMap.put("spacemr_responsible_username", "spacemr_people__responsible.username");
      computedColumnsMap.put("spacemr_responsible_first_name", "spacemr_people__responsible.first_name");
      computedColumnsMap.put("spacemr_responsible_last_name", "spacemr_people__responsible.last_name");
      computedColumnsMap.put("spacemr_responsible_role", "spacemr_people__responsible.role");
      computedColumnsMap.put("spacemr_responsible_email", "spacemr_people__responsible.email");
      // computedColumnsMap.put("spacemr_space_people_book_super_name", "spacemr_space_people_book_super.name");
      computedColumnsMap
         .put("app_log_last_date"
              , ""
              + "(select date "
              + "   from app_log "
              + "  where app_log.object_type = 1080"
              + "    and app_log.object_id   = spacemr_space_people_book.spacemr_space_people_book_id"
              + "  order by date desc "
              + "  limit 1"
              + ")"
              );
      computedColumnsMap
         .put("app_log_last_user_name"
              , ""
              + "(select user_name "
              + "   from app_log "
              + "  where app_log.object_type = 1080"
              + "    and app_log.object_id   = spacemr_space_people_book.spacemr_space_people_book_id"
              + "  order by date desc "
              + "  limit 1"
              + ")"
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
   }
}
