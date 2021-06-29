package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppMapperJsonForLog;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Calendar;
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
public class Spacemr_space_user_presence extends BaseObjectModel {
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
         + "insert into spacemr_space_user_presence ("
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
   public JSONObject get(int spacemr_space_user_presence_id) throws Exception {
      return(get(spacemr_space_user_presence_id, getColumns()));
   }
   
   public JSONObject get_ancestors_with_presence_query(int spacemr_space_id
                                                       , Date date
                                                       ) throws Exception {
      date = normalizeDate(date);
      String qs = ""
         + "\n select"
         + "\n    JSON_OBJECT("
         + "\n       'spacemr_space_id',   spacemr_space.spacemr_space_id"
         + "\n       , 'code', spacemr_space.code"
         + "\n       , 'number_of_seating', spacemr_space.number_of_seating"
         + "\n       , 'number_of_seating_booking', spacemr_space.number_of_seating_booking"
         + "\n       , 'spacemr_space_in_id', spacemr_space.spacemr_space_in_id"
         + "\n       , 'presents' , ("
         + "\n    select sum(people_number) from spacemr_space_user_presence"
         + "\n      where date_time >= ?"
         + "\n        and date_time <  ( ? + INTERVAL 1 DAY)"
         + "\n      and spacemr_space_id in ("
         + "\n        with recursive root_spaces (spacemr_space_id, spacemr_space_in_id) as ("
         + "\n          select     a.spacemr_space_id,"
         + "\n                     a.spacemr_space_in_id"
         + "\n            from     spacemr_space as a"
         + "\n           where     a.spacemr_space_id = ?"
         + "\n          union all"
         + "\n          select     p.spacemr_space_id,"
         + "\n                     p.spacemr_space_in_id"
         + "\n            from     spacemr_space as p"
         + "\n                   , root_spaces as r"
         + "\n           where     p.spacemr_space_in_id = r.spacemr_space_id"
         + "\n        )"
         + "\n        select spacemr_space_id from root_spaces"
         + "\n      )"
         + "\n    )  "
         + "\n    )"
         + "\n    from spacemr_space"
         + "\n    where spacemr_space_id=?"
         + "\n    ;"
         ;
      JSONObject rv = null;
      String rvs =
         (String)jdbcTemplate
         .queryForObject(qs
                         , new Object[] { date, date, spacemr_space_id, spacemr_space_id }
                         , String.class);
      if (rvs != null) {
         rv = new JSONObject(rvs);
      }
      return(rv);
   }


   private Date normalizeDate(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      date = calendar.getTime();
      return(date);
   }

 
   public int get_user_presence_sum(int app_user_id
                                    , int spacemr_space_id
                                    , Date date
                                    ) throws Exception {
      date = normalizeDate(date);
      String qs = ""
         + "\n   select sum(people_number) from spacemr_space_user_presence"
         + "\n    where date_time >= ?"
         + "\n      and date_time <  ( ? + INTERVAL 1 DAY)"
         + "\n      and app_user_id = ?"
         + "\n      and spacemr_space_id = ?"
         ;
      int rv = 0;
      Integer rvo =
         jdbcTemplate
         .queryForObject(qs
                         , new Object[] { date, date, app_user_id, spacemr_space_id }
                         , Integer.class);
      if (rvo != null) {
         rv = rvo.intValue();
      }
      return(rv);
   }
 
   public int get_presence_sum(int spacemr_space_id
                               , Date date
                               ) throws Exception {
      date = normalizeDate(date);
      String qs = ""
         + "\n   select sum(people_number) from spacemr_space_user_presence"
         + "\n    where date_time >= ?"
         + "\n      and date_time <  ( ? + INTERVAL 1 DAY)"
         + "\n      and spacemr_space_id = ?"
         ;
      int rv = 0;
      Integer rvo =
         jdbcTemplate
         .queryForObject(qs
                         , new Object[] { date, date, spacemr_space_id }
                         , Integer.class);
      if (rvo != null) {
         rv = rvo.intValue();
      }
      return(rv);
   }
 
   public JSONArray get_ancestors_with_presence(int spacemr_space_id, Date date) throws Exception {
      JSONArray rv = new JSONArray();
      JSONObject spacemr_space = get_ancestors_with_presence_query(spacemr_space_id, date);
      rv.put(spacemr_space);
      while (!spacemr_space.isNull("spacemr_space_in_id")) {
         spacemr_space =
            get_ancestors_with_presence_query(spacemr_space.getInt("spacemr_space_in_id"), date);
         rv.put(spacemr_space);
      }
      return(rv);
   }
   
   //-
   public JSONObject get(int spacemr_space_user_presence_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_space_user_presence_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_space_user_presence");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_space_user_presence"
                         + "   left join spacemr_space    on spacemr_space_user_presence.spacemr_space_id  = spacemr_space.spacemr_space_id"
                         + "   left join spacemr_space as spacemr_space_in "
                         + "                              on spacemr_space_in.spacemr_space_id             = spacemr_space.spacemr_space_in_id"
                         + "   left join app_user         on spacemr_space_user_presence.app_user_id  = app_user.app_user_id"
                         + "   left join app_group        on app_group.app_group_id                   = spacemr_space.app_group_id"
                         + " where spacemr_space_user_presence_id = ?"
                         // + "   and spacemr_space_user_presence_super.spacemr_space_user_presence_super_id = spacemr_space_user_presence.spacemr_space_user_presence_super_id"
                         , new Object[] { Integer.valueOf(spacemr_space_user_presence_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   
   //-
   public JSONObject getLog(int spacemr_space_user_presence_id) throws Exception {
      AppMapperJsonForLog mapper = new AppMapperJsonForLog();
      jdbcTemplate.query("select * from spacemr_space_user_presence where spacemr_space_user_presence_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_space_user_presence_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("spacemr_space_user_presence_id", content.getInt("spacemr_space_user_presence_id"));
      String qs = 
         "update spacemr_space_user_presence set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where spacemr_space_user_presence_id = :spacemr_space_user_presence_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   
   //-
   public void delete(int spacemr_space_user_presence_id) throws Exception {
      app_log.deleteLogs(this, spacemr_space_user_presence_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("spacemr_space_user_presence_id", spacemr_space_user_presence_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from spacemr_space_user_presence  "
                 + " where spacemr_space_user_presence_id = :spacemr_space_user_presence_id"
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
         case "spacemr_space_user_presence_id":
            break;
         case "app_user_id":
            np.addValue("app_user_id", tools.jsonObject_getInteger(content, "app_user_id"));
            break;
         case "spacemr_space_id":
            np.addValue("spacemr_space_id", tools.jsonObject_getInteger(content, "spacemr_space_id"));
            break;
         case "date_time":
            np.addValue("date_time", tools.jsonObject_getDate(content, "date_time"));
            break;
         case "people_number":
            np.addValue("people_number", tools.jsonObject_getInteger(content, "people_number"));
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
      "app_user_id"
      , "spacemr_space_id"
      , "date_time"
      , "people_number"
      , "nota"
   };
   private static final String[] _columns_toGet_a = {
        "app_user_user_name"
      , "app_user_first_name"
      , "app_user_last_name"
      , "app_user_email"
      , "app_group_name"
      , "spacemr_space_code"
      , "spacemr_space_description"
      , "spacemr_space_in_code"
      , "spacemr_space_in_description"
      , "spacemr_space_in_map_id_default"
      , "spacemr_space_people_names"
      , "space_day_numeration"
   };
   private static final String[] _columns_hidden_in_list = {
      "spacemr_space_id"
      , "app_user_id"
      , "app_user_user_name_hidden"
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
      computedColumnsMap.put("app_user_user_name", "app_user.user_name");
      computedColumnsMap.put("app_user_user_name_hidden", "app_user.user_name");
      computedColumnsMap.put("app_user_first_name", "app_user.first_name");
      computedColumnsMap.put("app_user_last_name", "app_user.last_name");
      computedColumnsMap.put("app_user_email", "app_user.email");
      computedColumnsMap.put("app_group_name", "app_group.name");
      computedColumnsMap.put("spacemr_space_code", "spacemr_space.code");
      computedColumnsMap.put("spacemr_space_description", "spacemr_space.description");
      computedColumnsMap
         .put("spacemr_space_in_map_id_default"
              , ""
              + "\n ("
              + "\n  select spacemr_space_map.spacemr_space_map_id"
              + "\n    from spacemr_space_map"
              + "\n   where spacemr_space_map.spacemr_space_id = spacemr_space_in.spacemr_space_id"
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
      computedColumnsMap
         .put("space_day_numeration"
              , ""
              + "\n  ("
              + "\n   cast("
              + "\n   coalesce ((select (sum(s1.people_number) + 1)"
              + "\n       from spacemr_space_user_presence s1"
              + "\n      where s1.spacemr_space_id = spacemr_space_user_presence.spacemr_space_id"
              + "\n        and s1.spacemr_space_user_presence_id < spacemr_space_user_presence.spacemr_space_user_presence_id"
              + "\n        and date(s1.date_time) = date(spacemr_space_user_presence.date_time)"
              + "\n    ), 1) "
              + "\n    as SIGNED ) "
              + "\n  )"
              );
      computedColumnsMap.put("spacemr_space_in_code",        "spacemr_space_in.code");
      computedColumnsMap.put("spacemr_space_in_description", "spacemr_space_in.description");
   }
}
