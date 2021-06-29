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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//-
public class Spacemr_inventario extends BaseObjectModel {
   //-
   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(Spacemr_inventario.class);
   
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
         + "insert into spacemr_inventario ("
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
   public JSONObject get(int spacemr_inventario_id) throws Exception {
      return(get(spacemr_inventario_id, getColumns()));
   }
   
   //-
   public JSONObject get(int spacemr_inventario_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "spacemr_inventario_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "spacemr_inventario");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from spacemr_inventario"
                         + "   left join spacemr_space  on spacemr_inventario.spacemr_space_id  = spacemr_space.spacemr_space_id"
                         + "   left join spacemr_people on spacemr_inventario.spacemr_people_id = spacemr_people.spacemr_people_id"
                         + " where spacemr_inventario_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_inventario_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }

   public String getWorkflowId() {
      return("spacemr_inventario_workflow");
   }
   
   //-
   public JSONObject getByCodiceInventario(String codice_inventario_unimore
                                           , String inventario_numero
                                           , String inventario_numero_sub
                                           ) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select "
                         + "   spacemr_inventario_id "
                         + "   , stato "
                         + "   , old_values "
                         + "   , scarico_numero_buono "
                         + "   , scarico_data "
                         + "  from spacemr_inventario"
                         + " where codice_inventario_unimore = ?"
                         + "   and inventario_numero = ?"
                         + "   and inventario_numero_sub = ?"
                         , new Object[] { codice_inventario_unimore
                                          , inventario_numero
                                          , inventario_numero_sub
                         }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   
   //-
   public JSONObject getLog(int spacemr_inventario_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from spacemr_inventario where spacemr_inventario_id = ?"
                         , new Object[] { Integer.valueOf(spacemr_inventario_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   
   //-
   public void update(JSONObject content) throws Exception {
      //-
      //- computing differences with old_values
      //-
      JSONObject old_values = null;
      try {
         old_values = new JSONObject(content.getString("old_values"));
      } catch (Exception e) {
         String m = "Error parsing 'old_values' for " + content.toString(2)
            + ": " + tools.stringStackTrace(e);
         logger.error(m);
         //- if oldvalues was not correctly initialized
         old_values = new JSONObject(content.toString());
         content.put("old_values", old_values.toString());
      }
      //-
      String n;
      n="old_values_changes"; if (old_values.has(n)) { old_values.remove(n); }
      n="old_values";  if (old_values.has(n)) { old_values.remove(n); }
      n="fg_validato"; if (old_values.has(n)) { old_values.remove(n); }
      n="fg_adesivo";  if (old_values.has(n)) { old_values.remove(n); }
      //-
      String old_values_changess = "";
      JSONObject old_values_changes = tools.jsonChanges(old_values, content);
      if (old_values_changes != null
          && old_values_changes.has("changed")){
         old_values_changess = old_values_changes.getJSONObject("changed").toString();
      }
      content.put("old_values_changes", old_values_changess);
      //-
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("spacemr_inventario_id", content.getInt("spacemr_inventario_id"));
      String qs = 
         "update spacemr_inventario set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where spacemr_inventario_id = :spacemr_inventario_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   
   //-
   public void delete(int spacemr_inventario_id) throws Exception {
      app_log.deleteLogs(this, spacemr_inventario_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("spacemr_inventario_id", spacemr_inventario_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from spacemr_inventario  "
                 + " where spacemr_inventario_id = :spacemr_inventario_id"
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
         case "spacemr_inventario_id":
            break;
         case "spacemr_space_id":
            np.addValue("spacemr_space_id", tools.jsonObject_getInteger(content, "spacemr_space_id"));
            break;
         case "spacemr_people_id":
            np.addValue("spacemr_people_id", tools.jsonObject_getInteger(content, "spacemr_people_id"));
            break;
         case "stato":
            np.addValue("stato", content.optString("stato", null));
            break;
         case "fg_validato":
            np.addValue("fg_validato", tools.jsonObject_getBoolean(content, "fg_validato"));
            break;
         case "fg_adesivo":
            np.addValue("fg_adesivo", tools.jsonObject_getBoolean(content, "fg_adesivo"));
            break;
         case "codice_inventario_unimore":
            np.addValue("codice_inventario_unimore", content.optString("codice_inventario_unimore", null));
            break;
         case "inventario_numero":
            np.addValue("inventario_numero", content.optString("inventario_numero", null));
            break;
         case "inventario_numero_sub":
            np.addValue("inventario_numero_sub", content.optString("inventario_numero_sub", null));
            break;
         case "inventario_etichetta":
            np.addValue("inventario_etichetta", content.optString("inventario_etichetta", null));
            break;
         case "tipo_carico_scarico":
            np.addValue("tipo_carico_scarico", content.optString("tipo_carico_scarico", null));
            break;
         case "carico_data":
            np.addValue("carico_data", tools.jsonObject_getDate(content, "carico_data"));
            break;
         case "attivita_tipo":
            np.addValue("attivita_tipo", content.optString("attivita_tipo", null));
            break;
         case "descrizione":
            np.addValue("descrizione", content.optString("descrizione", null));
            break;
         case "categoria_inventario":
            np.addValue("categoria_inventario", content.optString("categoria_inventario", null));
            break;
         case "valore":
            np.addValue("valore", tools.jsonObject_getBigDecimal(content, "valore"));
            break;
         case "fornitore":
            np.addValue("fornitore", content.optString("fornitore", null));
            break;
         case "scarico_numero_buono":
            np.addValue("scarico_numero_buono", content.optString("scarico_numero_buono", null));
            break;
         case "scarico_data":
            np.addValue("scarico_data", tools.jsonObject_getDate(content, "scarico_data"));
            break;
         case "old_values":
            np.addValue("old_values", content.getString("old_values"));
            break;
         case "old_values_changes":
            np.addValue("old_values_changes", content.getString("old_values_changes"));
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
      "spacemr_space_id"
      , "spacemr_people_id"
      , "fg_validato"
      , "fg_adesivo"
      , "stato"
      , "codice_inventario_unimore"
      , "inventario_numero"
      , "inventario_numero_sub"
      , "inventario_etichetta"
      , "tipo_carico_scarico"
      , "carico_data"
      , "attivita_tipo"
      , "descrizione"
      , "categoria_inventario"
      , "valore"
      , "fornitore"
      , "scarico_numero_buono"
      , "scarico_data"
      , "old_values"
      , "old_values_changes"
      , "nota"
   };
   private static final String[] _columns_toGet_a = {
      "spacemr_people_username"
      , "spacemr_people_first_name"
      , "spacemr_people_last_name"
      , "spacemr_people_role"
      , "spacemr_space_code"
      , "spacemr_space_description"
      , "app_log_last_date"
      , "app_log_last_user_name"
      , "spacemr_space_in_map_id_default"
   };
   private static final String[] _columns_hidden_in_list = {
      "spacemr_space_id"
      , "spacemr_people_id"
      , "stato_hidden"
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
      computedColumnsMap.put("spacemr_people_username", "spacemr_people.username");
      computedColumnsMap.put("spacemr_people_first_name", "spacemr_people.first_name");
      computedColumnsMap.put("spacemr_people_last_name", "spacemr_people.last_name");
      computedColumnsMap.put("spacemr_people_role", "spacemr_people.role");
      computedColumnsMap.put("spacemr_space_code", "spacemr_space.code");
      computedColumnsMap.put("spacemr_space_description", "spacemr_space.description");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = spacemr_inventario.user_name)"
      //         );
      computedColumnsMap
         .put("app_log_last_date"
              , ""
              + "(select date "
              + "   from app_log "
              + "  where app_log.object_type = 1060"
              + "    and app_log.object_id   = spacemr_inventario.spacemr_inventario_id"
              + "  order by date desc "
              + "  limit 1"
              + ")"
              );
      computedColumnsMap
         .put("app_log_last_user_name"
              , ""
              + "(select user_name "
              + "   from app_log "
              + "  where app_log.object_type = 1060"
              + "    and app_log.object_id   = spacemr_inventario.spacemr_inventario_id"
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
   }
}
