package it.unimo.app.om;
//-
import java.util.Date;
import java.util.Vector;
import java.util.HashMap;
import java.util.Properties;
import java.io.StringReader;
import javax.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppMapperVectorString;

//-
public class App_system_property extends BaseObjectModel {
   /** 
    * Initialize system permissions */
   @PostConstruct
   public void initSystem_propertys() throws Exception {
      initSystem_propertys_addSystem_propertys();
   }

   @SuppressWarnings("unchecked")
   private void initSystem_propertys_addSystem_propertys() throws Exception {
      System.out.println(" ------------------------------ initSystem_properties");
      Vector<String> vNew = new Vector<String>();
      Vector<String> vOld = null;
      Vector<String> vToAdd    = null;
      Vector<String> vToDelete = null;
      HashMap<String,String[]> properties = new HashMap<String,String[]>();
      //-
      for(String[] p: system_propertys) { 
         String name = p[0];
         vNew.add(name); 
         properties.put(name, p);
      };
      {
         AppMapperVectorString m = new AppMapperVectorString();
         jdbcTemplate.query("select name from app_system_property", m );
         vOld = m.getVector();
      }
      //-
      vToAdd     = ((Vector<String>)vNew.clone()); vToAdd.removeAll(vOld);
      vToDelete  = ((Vector<String>)vOld.clone()); vToDelete.removeAll(vNew);
      //-
      if (vToAdd.size() > 0) {
         System.out.println(" adding: "   + vToAdd);
         //- 
         String nota = "Added by app system initializer on " + new Date();
         for (String name: vToAdd) {
            String[] p = properties.get(name);
            JSONObject obj = new JSONObject();
            obj.put("name",          p[0]);
            obj.put("description",   p[1]);
            obj.put("value",         p[2]);
            obj.put("value_default", p[2]);
            obj.put("nota",          nota);
            insert(obj);
         }
      }
      if (vToDelete.size()>0) {
         System.out.println(" Non system system_propertys: " + vToDelete);
      }
   }

   //-
   public Vector<String> getColumns() {
      return(obj_fieldsc);
   }
   //-
   public int insert(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content,getColumns());
      namedParameterJdbcTemplate
         .update(
                 "insert into app_system_property ("
                 + getSqlStringForInsert_fields(getColumns())
                 + ") values ("
                 + getSqlStringForInsert_values(getColumns())
                 + ")"
                 , np
                 );
      int id = jdbcTemplate
         .queryForObject("select app_system_property_id from app_system_property where name = ?"
                         , Integer.class, content.getString("name"))
         ;
      return(id);
   }

   //-
   public JSONObject get(int app_system_property_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_system_property where app_system_property_id = ?"
                         , new Object[] { Integer.valueOf(app_system_property_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject get(String name) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_system_property where name = ?"
                         , mapper, name);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int app_system_property_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_system_property where app_system_property_id = ?"
                         , new Object[] { Integer.valueOf(app_system_property_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content,getColumns());
      np.addValue("app_system_property_id", content.getInt("app_system_property_id"));
      String qs = 
         "update app_system_property set "
         + getSqlStringForUpdate(getColumns())
         + " where app_system_property_id = :app_system_property_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }

   //-
   public void delete(int app_system_property_id) throws Exception {
      app_log.deleteLogs(this, app_system_property_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_system_property_id", app_system_property_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_system_property  "
                 + " where app_system_property_id = :app_system_property_id"
                 , np
                 );
   }
   //-
   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) {
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("name", content.getString("name"));
      np.addValue("description", content.getString("description"));
      np.addValue("value", content.getString("value"));
      np.addValue("value_default", content.getString("value_default"));
      np.addValue("nota", content.getString("nota"));
      return(np);
   }

   //-
   public String getAsString(String name) throws Exception {
      String rv = null;
      int rows = jdbcTemplate
         .queryForObject("select count(*) from app_system_property where name = ?"
                         , Integer.class, name).intValue();
      if (rows > 0) {
         rv = jdbcTemplate.queryForObject("select value from app_system_property where name = ?"
                                          , String.class, name);
      }
      return(rv);
   }
   //-
   public void set(String name, String value) throws Exception {
      int rows = jdbcTemplate
         .queryForObject("select count(*) from app_system_property where name = ?"
                         , Integer.class, name).intValue();
      if (rows == 0) {
         jdbcTemplate.update("insert into app_system_property (name , value) "
                             + " values (?,?)"
                             , name, value);
      } else {
         jdbcTemplate.update("update app_system_property set value = ? "
                             + " where name = ?"
                             , value, name);
      }
   }
   //-
   public boolean getAsBoolean(String name) throws Exception {
      String s = getAsString(name);
      boolean rv = "true".equalsIgnoreCase(s);
      return(rv);
   }
   //-
   public int getAsInt(String name) throws Exception {
      String s = getAsString(name);
      int rv = Integer.parseInt(s);
      return(rv);
   }
   //-
   public Properties getAsProperties(String name) throws Exception {
      String s = getAsString(name);
      Properties rv = new Properties();
      if (s == null) {
         String m = "Error reading NULL system property ["+name+"]";
         throw(new Exception(m));
      }
      rv.load(new StringReader(s));
      return(rv);
   }
   public JSONObject getAsJSONObject(String name) throws Exception {
      JSONObject rv = null;
      // System.out.println(" getting: " + name);
      String s = getAsString(name);
      // System.out.println(" value: " + s);
      if (s != null) {
         rv = new JSONObject(s);
      }
      return(rv);
   }

   public void set(String name, JSONObject jo) throws Exception {
      String s = null;
      if (jo != null) {
         s = jo.toString();
      }
      set(name, s);
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
      , "description"
      , "value"
      , "value_default"
      , "nota"
   };
   //-
   private static final Vector<String> obj_fieldsc = new Vector<String>();
   static  {
      for (String c: obj_fields) {
         obj_fieldsc.add(c);
      }
   }

   private static String system_propertys[][] = {
      {"sys_application_identifier", "the application id used by cookies", "app" }
      , {"app_about_custom"        , "Custom About html message to show in the 'about' page", "" }
      , {"app_first_login_note"    , "Html message to be confirmed at first login by the user - blank mean 'do not display it'", "" }
      , {"login_remember_me_enabled", "Remember me - browser login though sessions", "true" }
      , {"login_remember_me_days_to_live", "Remember me - how many days the login is valid", "30" }
      , {"login_anonymous_user", "the default user logged at first connection", "anonymous" }
      , {"auth_custom_context_name"
         , "use this provider for authentication, blank means no custom auth", "" }
      , {"auth_custom_fallback_to_db"
         , "if the custom auth fails goes back to the db auth", "true" }
      ,{"spacemr_inventario_workflow"
        , "workflow bene inventariato"
        , ""
        + "\n  {"
        + "\n      \"defaultStatus\": \"da_controllare\""
        + "\n      , \"default_per_field_permisson\": {"
        + "\n          \"fg_validato\":  { \"default\":\"ro\", \"rw\": \"db_spacemr_inventario_wf_admin\"}"
        + "\n          , \"codice_inventario_unimore\": { \"default\":\"hidden\"}"
        + "\n          , \"inventario_numero\":   { \"default\":\"hidden\"}"
        + "\n          , \"inventario_numero_sub\":   { \"default\":\"hidden\"}"
        + "\n          , \"inventario_etichetta\":   { \"default\":\"ro\"}"
        + "\n          , \"tipo_carico_scarico\":   { \"default\":\"hidden\"}"
        + "\n          , \"carico_data\":     { \"default\":\"ro\"}"
        + "\n          , \"attivita_tipo\":   { \"default\":\"hidden\"}"
        + "\n          , \"categoria_inventario\":   { \"default\":\"hidden\"}"
        + "\n          , \"valore\":      { \"default\":\"hidden\"}"
        + "\n          , \"fornitore\":   { \"default\":\"hidden\"}"
        + "\n          , \"scarico_numero_buono\":   { \"default\":\"hidden\"}"
        + "\n          , \"scarico_data\":   { \"default\":\"ro\"}"
        + "\n          , \"old_values\":   { \"default\":\"hidden\"}"
        + "\n          , \"note\":         { \"default\":\"rw\"}"
        + "\n          , \"old_values_changes\":   { \"default\":\"hidden\", \"ro\": \"db_spacemr_inventario_wf_admin\"}"
        + "\n      }"
        + "\n      , statuses: ["
        + "\n          {"
        + "\n              \"name\":\"da_controllare\""
        + "\n              , \"color\":\"#B0B0B0\""
        + "\n              , \"description\": \"Da controllare\""
        + "\n              , \"steps\":["
        + "\n                  {"
        + "\n                      \"destination\":\"smaltito\""
        + "\n                      , \"description\":\"Bene smaltito\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_user\""
        + "\n                  }"
        + "\n                  , {"
        + "\n                      \"destination\":\"trovato\""
        + "\n                      , \"description\":\"Bene trovato\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_user\""
        + "\n                  }"
        + "\n                  , {"
        + "\n                      \"destination\":\"in_altra_struttura\""
        + "\n                      , \"description\":\"Bene in altra struttura\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_user\""
        + "\n                  }"
        + "\n                  , {"
        + "\n                      \"destination\":\"da_smaltire\""
        + "\n                      , \"description\":\"Bene da smaltire\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_user\""
        + "\n                  }"
        + "\n              ]"
        + "\n          }"
        + "\n          , {"
        + "\n              \"name\":\"smaltito\""
        + "\n              , \"color\":\"#0000C0\""
        + "\n              , \"description\":\"Bene smaltito\""
        + "\n              , \"steps\":["
        + "\n                  {"
        + "\n                      \"destination\":\"da_controllare\""
        + "\n                      , \"description\":\"Imposta come Da Controllare\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_admin\""
        + "\n                  }"
        + "\n              ]"
        + "\n          }"
        + "\n          , {"
        + "\n              \"name\":\"trovato\""
        + "\n              , \"color\":\"#10FF10\""
        + "\n              , \"description\":\"Bene trovato e correttamente assegnato\""
        + "\n              , \"steps\":["
        + "\n                  {"
        + "\n                      \"destination\":\"da_controllare\""
        + "\n                      , \"description\":\"Imposta come Da Controllare\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_admin\""
        + "\n                  }"
        + "\n              ]"
        + "\n          }"
        + "\n          , {"
        + "\n              \"name\":\"in_altra_struttura\""
        + "\n              , \"color\":\"#10ffda\""
        + "\n              , \"description\":\"Bene segnalato come in altra struttura/spazio\""
        + "\n              , \"steps\":["
        + "\n                  {"
        + "\n                      \"destination\":\"da_controllare\""
        + "\n                      , \"description\":\"Imposta come Da Controllare\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_admin\""
        + "\n                  }"
        + "\n              ]"
        + "\n          }"
        + "\n          , {"
        + "\n              \"name\":\"da_smaltire\""
        + "\n              , \"color\":\"#ffd510\""
        + "\n              , \"description\":\"Bene in corso di smaltimento\""
        + "\n              , \"steps\":["
        + "\n                  {"
        + "\n                      \"destination\":\"da_controllare\""
        + "\n                      , \"description\":\"Imposta come Da Controllare\""
        + "\n                      , \"permission\":\"db_spacemr_inventario_wf_admin\""
        + "\n                  }"
        + "\n              ]"
        + "\n          }"
        + "\n      ]"
        + "\n  }"
      }
      ,{"spacemr_space_people_book_workflow"
        , "workflow prenotazione spazi"
        , ""
        + "\n {"
        + "\n     'defaultStatus': 'richiesta'"
        + "\n     , 'defaultSearchStatuses': ['richiesta', 'verificata', 'autorizzata', 'riformulare']"
        + "\n     , 'default_per_field_permisson': {"
        + "\n         'spacemr_people_id':            { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_people_username':   { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_people_first_name': { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_people_last_name':  { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_people_role':       { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_responsible_id': { 'default':'ro', 'rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_responsible_username':   { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_responsible_first_name': { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_responsible_last_name':  { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_responsible_role':       { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_space_id':     { 'default':'ro', 'rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'spacemr_space_code':   { 'default':'ro', 'rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'reason':      { 'default':'ro', 'rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'date_from':   { 'default':'ro', 'rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'date_to':     { 'default':'ro', 'rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n         , 'repetition':  { 'default':'ro', 'rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n     }"
        + "\n     , statuses: ["
        + "\n         {"
        + "\n             'name':'richiesta'"
        + "\n             , 'color':'#66ccff'"
        + "\n             , 'description':'Richiesta in approvazione'"
        + "\n             , 'steps':["
        + "\n                 {"
        + "\n                     'destination':'autorizzata'"
        + "\n                     , 'description':'Autorizza la richiesta'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_responsible'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n                 , {"
        + "\n                     'destination':'verificata'"
        + "\n                     , 'description':'Richiesta verificata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_verifier'"
        + "\n                 }"
        + "\n                 , {"
        + "\n                     'destination':'annullata'"
        + "\n                     , 'description':'Richiesta annullata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_update'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n                 , {"
        + "\n                     'destination':'riformulare'"
        + "\n                     , 'description':'Richiesta non autorizzata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_verifier'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n             ]"
        + "\n             , 'per_field_permisson': {"
        + "\n                 'spacemr_people_id':  { 'default':'rw'}"
        + "\n                 , 'spacemr_people_username':   { 'default':'rw'}"
        + "\n                 , 'spacemr_people_first_name': { 'default':'rw'}"
        + "\n                 , 'spacemr_people_last_name':  { 'default':'rw'}"
        + "\n                 , 'spacemr_people_role':       { 'default':'rw'}"
        + "\n                 , 'spacemr_responsible_id':         { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_username':   { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_first_name': { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_last_name':  { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_role':       { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_space_id':     { 'default':'rw'}"
        + "\n                 , 'spacemr_space_code':   { 'default':'rw'}"
        + "\n                 , 'reason':      { 'default':'rw'}"
        + "\n                 , 'date_from':   { 'default':'rw'}"
        + "\n                 , 'date_to':     { 'default':'rw'}"
        + "\n                 , 'repetition':  { 'default':'rw'}"
        + "\n             }"
        + "\n         }"
        + "\n         , {"
        + "\n             'name':'verificata'"
        + "\n             , 'color':'#e5fc62'"
        + "\n             , 'description':'Richiesta verificata'"
        + "\n             , 'steps':["
        + "\n                 {"
        + "\n                     'destination':'autorizzata'"
        + "\n                     , 'description':'Autorizza la richiesta'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_responsible'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n                 , {"
        + "\n                     'destination':'riformulare'"
        + "\n                     , 'description':'Richiesta non autorizzata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_verifier'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n                 , {"
        + "\n                     'destination':'annullata'"
        + "\n                     , 'description':'Richiesta annullata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_update'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n             ]"
        + "\n         }"
        + "\n         , {"
        + "\n             'name':'autorizzata'"
        + "\n             , 'color':'#00FF00'"
        + "\n             , 'description':'Richiesta autorizzata'"
        + "\n             , 'steps':["
        + "\n                 {"
        + "\n                     'destination':'annullata'"
        + "\n                     , 'description':'Richiesta annullata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_update'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n                 , {"
        + "\n                     'destination':'riformulare'"
        + "\n                     , 'description':'Richiesta non autorizzata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_verifier'"
        + "\n                     , 'actions': [ 'spacemr_space_people_book:workflow_write_notifica' ]"
        + "\n                 }"
        + "\n             ]"
        + "\n         }"
        + "\n         , {"
        + "\n             'name':'riformulare'"
        + "\n             , 'color':'#ffae00'"
        + "\n             , 'description':'Riformulare la richesta'"
        + "\n             , 'steps':["
        + "\n                 {"
        + "\n                     'destination':'annullata'"
        + "\n                     , 'description':'Richiesta annullata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_update'"
        + "\n                 }"
        + "\n                 , {"
        + "\n                     'destination':'richiesta'"
        + "\n                     , 'description':'Invia richiesta'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_update'"
        + "\n                 }"
        + "\n             ]"
        + "\n             , 'per_field_permisson': {"
        + "\n                 'spacemr_people_id':  { 'default':'rw'}"
        + "\n                 , 'spacemr_people_username':   { 'default':'rw'}"
        + "\n                 , 'spacemr_people_first_name': { 'default':'rw'}"
        + "\n                 , 'spacemr_people_last_name':  { 'default':'rw'}"
        + "\n                 , 'spacemr_people_role':       { 'default':'rw'}"
        + "\n                 , 'spacemr_responsible_id':         { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_username':   { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_first_name': { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_last_name':  { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_responsible_role':       { 'default':'ro','rw': 'db_spacemr_space_people_book_admin_update'}"
        + "\n                 , 'spacemr_space_id':     { 'default':'rw'}"
        + "\n                 , 'spacemr_space_code':   { 'default':'rw'}"
        + "\n                 , 'reason':      { 'default':'rw'}"
        + "\n                 , 'date_from':   { 'default':'rw'}"
        + "\n                 , 'date_to':     { 'default':'rw'}"
        + "\n                 , 'repetition':  { 'default':'rw'}"
        + "\n             }"
        + "\n         }"
        + "\n         , {"
        + "\n             'name':'annullata'"
        + "\n             , 'color':'#707070'"
        + "\n             , 'description':'Richiesta annullata'"
        + "\n             , 'steps':["
        + "\n                 {"
        + "\n                     'destination':'riformulare'"
        + "\n                     , 'description':'Richiesta non autorizzata'"
        + "\n                     , 'permission':'db_spacemr_space_people_book_verifier'"
        + "\n                 }"
        + "\n             ]"
        + "\n         }"
        + "\n     ]"
        + "\n     , 'aux': {"
        + "\n         'bookers_roles' : ['COLLABORATORE IN SPIN OFF'"
        + "\n                              , 'Personale tecnico amministrativo'"
        + "\n                              , 'Professore Associato'"
        + "\n                              , 'PROFESSORE EMERITO'"
        + "\n                              , 'PROFESSORE FUORI RUOLO Esterno'"
        + "\n                              , 'Professore Ordinario'"
        + "\n                              , 'Ricercatore Legge 240/10 - t.det.'"
        + "\n                              , 'Ricercatore Universitario'"
        + "\n                              , 'SENIOR PROFESSOR'"
        + "\n                              , 'VISITING PROFESSOR'"
        + "\n                             ]"
        + "\n         , 'bookings_counted_statuses': ['richiesta', 'verificata', 'autorizzata']"
        + "\n     }"
        + "\n }"
      }
      ,{"table_mattoni__table_app_user_list", "", "{ \"pageSize\": \"20\", \"columns\": [ \"user_name\", \"first_name\", \"last_name\" ] }" }
      ,{"table_mattoni__table_app_user_logs", "", "{ \"pageSize\": \"20\", \"columns\": [ \"date\", \"user_name\", \"parameter_values\" ] }" }
      ,{"table_mattoni__table_app_role_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"name\",    \"nota\"  ]}" }      
      ,{"table_mattoni__table_app_role_logs", "", "{ \"pageSize\": \"20\", \"columns\": [ \"date\", \"user_name\", \"parameter_values\" ] }" }
      ,{"table_mattoni__table_app_permission_list", "", "{ \"pageSize\": \"20\", \"columns\": [ \"name\", \"nota\" ] }" }
      ,{"table_mattoni__table_app_permission_logs", "", "{ \"pageSize\": \"20\", \"columns\": [ \"date\", \"user_name\", \"parameter_values\" ] }" }
      ,{"table_mattoni__table_app_group_list", "", "{ \"pageSize\": \"20\", \"columns\": [ \"name\", \"nota\" ] }" }
      ,{"table_mattoni__table_app_group_logs", "", "{ \"pageSize\": \"20\", \"columns\": [ \"date\", \"user_name\", \"parameter_values\" ] }" }
      ,{"table_mattoni__table_app_system_property_list", "", "{ \"pageSize\": \"20\", \"columns\": [ \"name\", \"description\", \"value\" ] }" }
      ,{"table_mattoni__table_app_system_property_logs", "", "{ \"pageSize\": \"20\", \"columns\": [ \"date\", \"user_name\", \"parameter_values\" ] }" }
      ,{"table_mattoni__table_app_group_role_user_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"app_user_user_name\",    \"app_role_name\",    \"app_group_name\",    \"app_user_first_name\",    \"app_user_last_name\"  ]}" }
      ,{"table_mattoni__table_app_file_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_app_user_search", "", "{ \"pageSize\": \"50\", \"columns\": [ \"user_name\", \"first_name\", \"last_name\", \"email\" ]}" }
      ,{"table_mattoni__table_spacemr_people_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"username\",    \"first_name\",    \"last_name\",    \"role\",    \"department\"  ]}" }
      ,{"table_mattoni__table_spacemr_people_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_type_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"name\",    \"description\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_type_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_people_type_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"name\",    \"description\",    \"fg_is_a_seat\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_people_type_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_search", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"code\",    \"name\",    \"app_group_name\",    \"description\", \"spacemr_space_people_names\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_people_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_people_search", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"username\",    \"first_name\",    \"last_name\",    \"role\",    \"department\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_people_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date_from\",    \"date_to\",    \"spacemr_space_people_type_name\",    \"spacemr_space_code\",    \"spacemr_people_username\",    \"spacemr_people_first_name\",    \"spacemr_people_last_name\",    \"spacemr_space_type_name\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"code\",    \"description\",    \"spacemr_space_type_name\",    \"spacemr_space_contained_spaces_count\",    \"area_in_meters2\",    \"number_of_seating\",    \"spacemr_space_people_count\",    \"spacemr_space_people_count_delta\",    \"spacemr_space_people_names\"  ]}" }
      ,{"table_mattoni__table_app_system_log_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_app_file_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"file_name\",    \"file_size\",    \"owner_object_type\",    \"owner_object_id\"  ]}" }
      ,{"configuration_docs_directory", "Uploaded files (app_file)", "app_data/docs"}
      ,{"table_mattoni__table_spacemr_inventario_logs", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_inventario_list", "", "{  \"pageSize\": \"20\",  \"columns\": [    \"stato\",    \"spacemr_people_username\",    \"spacemr_space_code\",    \"fg_validato\",    \"fg_adesivo\",    \"inventario_etichetta\",    \"carico_data\",    \"scarico_data\",    \"descrizione\",    \"valore\",    \"old_values_changes\",    \"nota\"  ]}" }
      ,{"table_mattoni__table_spacemr_inventario_list_tooltip", "", "{ \"pageSize\": \"50\", \"columns\": [ \"spacemr_people_username\", \"inventario_etichetta\", \"descrizione\", \"carico_data\", \"scarico_data\", \"fg_adesivo\", \"fg_validato\", \"valore\", \"old_values_changes\", \"nota\" ]}" }
      ,{"table_mattoni__table_spacemr_space_map_logs", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_map_list", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"fg_default_map\",    \"description\",    \"nota\",    \"spacemr_space_code\"  ]}" }
      ,{"table_mattoni__table_app_system_log_list", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"date\",    \"user_name\",    \"category\",    \"value\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_people_book_logs", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_people_book_list", "",         "{ \"pageSize\": \"20\", \"columns\": [ \"transactions\", \"people_number\", \"spacemr_responsible_last_name\", \"spacemr_people_last_name\", \"reason\", \"date_from\", \"date_to\", \"repetition\", \"nota\", \"spacemr_space_code\", \"spacemr_space_people_names\", \"app_log_last_user_name\", \"app_log_last_date\" ]}" }
      ,{"table_mattoni__table_spacemr_space_people_book_list_tooltip", "", "{ \"pageSize\": \"20\", \"columns\": [ \"transactions\", \"people_number\", \"spacemr_responsible_last_name\", \"spacemr_people_last_name\", \"reason\", \"date_from\", \"date_to\", \"repetition\", \"nota\", \"spacemr_space_code\", \"spacemr_space_people_names\", \"app_log_last_user_name\", \"app_log_last_date\" ]}" }
      ,{"sys_email_configuration", "email_configuration", ""
        + "#"
        + "\n mail.smtp.starttls.enable=true"
        + "\n mail.smtp.port=587"
        + "\n mail.smtp.auth=true"
        + "\n mail.smtp.host=smtp.gmail.com"
        + "\n mail.smtp.username="
        + "\n mail.smtp.password="
        + "\n mail.smtp.from_email="
      }
      ,{"table_mattoni__table_app_notifica_logs", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_app_notifica_list", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"date\",    \"bean_name\",    \"object_id\",    \"json_info\"  ]}" }
      ,{"spacemr_space_people_book_email_html_template", "spacemr_space_people_book_email_html_template", "This is an email template"}
      ,{"table_mattoni__table_spacemr_space_user_presence_logs", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }
      ,{"table_mattoni__table_spacemr_space_user_presence_list", "", "{ \"pageSize\": \"50\", \"columns\": [ \"date_time\", \"app_user_first_name\", \"app_user_last_name\", \"spacemr_space_code\", \"spacemr_space_people_names\" ]}" }
      ,{"table_mattoni__table_spacemr_space_user_presence_list_tooltip", "", "{ \"pageSize\": \"50\", \"columns\": [ \"people_number\", \"date_time\", \"app_user_last_name\", \"spacemr_space_code\", \"nota\", \"spacemr_space_description\", \"spacemr_space_people_names\" ]}" }
   };


}
