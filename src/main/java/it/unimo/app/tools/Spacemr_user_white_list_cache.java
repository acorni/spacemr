package it.unimo.app.tools;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;

import it.unimo.app.om.App_system_property;
import it.unimo.app.om.Spacemr_space;
import it.unimo.app.tools.peopleImport.PeopleImportLdap;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class Spacemr_user_white_list_cache {

   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private Spacemr_space spacemr_space;
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private PeopleImportLdap peopleImportLdap;
   
   public Spacemr_user_white_list_parser getParser(int spacemr_space_id
                                                   ) throws Exception {
      Spacemr_user_white_list_parser p = null;
      ParserWithDate pd = _cache.get(spacemr_space_id);
      if (pd != null){
         if ((pd.date + _timeout) > (new Date()).getTime()) {
            p = pd.parser;
         } else {
            System.out.println(" -- cache time-out");
         }
      }
      if (p == null) {
         JSONObject obj = spacemr_space.get(spacemr_space_id);
         p = Spacemr_user_white_list_parser.getParser(obj.optString("user_white_list", null));
         _cache.put(spacemr_space_id, new ParserWithDate(p));
         // System.out.println(" -- cache new parser");
      }
      return(p);
   }

   public void remove(int spacemr_space_id) throws Exception {
      _cache.remove(spacemr_space_id);
   }
   
   private class ParserWithDate {
      public ParserWithDate(Spacemr_user_white_list_parser p) {
         parser = p;
         date   = (new Date()).getTime();
      }
      public Spacemr_user_white_list_parser parser = null;
      public long date = -1;
   }



   public JSONObject allInvolvedUsersInfoAsJson(Spacemr_user_white_list_parser p) throws Exception {
      JSONObject rv = new JSONObject();
      HashMap<String,JSONObject> info = allInvolvedUsersInfo(p);
      //-
      // Set<String> userset = getAllInvolvedUsers();
      // List<String> sortedList = new ArrayList<String>(userset);
      // Collections.sort(sortedList);
      Object[] array = info.keySet().toArray();
      Arrays.sort(array);
      //-
      for(Object o: array) {
         String s = (String)o;
         rv.put(s, info.get(s));
      }
      // System.out.println(" rv.size: " + rv.length());
      return(rv);
   }
   
   public HashMap<String,JSONObject> allInvolvedUsersInfo(Spacemr_user_white_list_parser p) throws Exception {
      HashMap<String,JSONObject> rv = allInvolvedUsersInfo(p.getAllInvolvedUsers());
      return(rv);
   }
   
   public HashMap<String,JSONObject> allInvolvedUsersInfo(Set<String> users) throws Exception {
      HashMap<String,JSONObject> rv = new HashMap<String,JSONObject>();
      // allInvolvedUsersInfo(getAllInvolvedUsers());
      Set foundInDb = allInvolvedUsersInfo_db(users, rv);
      HashSet<String> remaining = null;
      //-
      remaining = new HashSet<String>(); remaining.addAll(users); remaining.removeAll(foundInDb);
      //-
      Set<String> foundInLdap = allInvolvedUsersInfo_ldap(remaining, rv);
      //-
      // remaining = remaining.removeAll(foundInLdap);
      //-
      return(rv);
   }

   private Set<String> allInvolvedUsersInfo_db(Set<String> users
                                               , HashMap<String,JSONObject>info
                                               ) throws Exception {
      HashSet<String> rv = new HashSet<String>();
      if ((users != null) && (users.size() > 0)) {
         String qsp = "";
         {
            StringBuffer sb = new StringBuffer();
            String comma = "";
            for (String s: users) {
               sb.append(comma + "?");
               comma = ",";
            }
            qsp = sb.toString();
         }
         /* 
            select user_name, first_name, last_name, email
            from app_user
            where user_name in ('acorni', 'paperino');
         */
         String qs = ""
            + "\n       select user_name, first_name, last_name, email"
            + "\n         from app_user"
            + "\n        where user_name in ("+qsp+")"
            ;
         AppMapperJson mapper = new AppMapperJson();
         jdbcTemplate.query(qs
                            , users.toArray()
                            , mapper);
         JSONArray results = mapper.getJSONArray();
         for (int i = 0; i < results.length(); i++) {
            JSONObject j = results.getJSONObject(i);
            j.put("source","db");
            String user_name = j.getString("user_name");
            rv.add(user_name);
            info.put(user_name, j);
         }
      }
      return(rv);
   }

   private Set<String> allInvolvedUsersInfo_ldap(Set<String> users
                                                 , HashMap<String,JSONObject>info
                                                 ) throws Exception {
      HashSet<String> rv = new HashSet<String>();
      //-
      JSONObject conf = app_system_property.getAsJSONObject("spacemr_ldap_search_configuration");
      if (conf != null) {
         //-
         //-
         String qsp = "";
         {
            StringBuffer sb = new StringBuffer();
            String comma = "";
            for (String s: users) {
               sb.append(comma + "(uid="+s+")");
               comma = ",";
            }
            qsp = sb.toString();
         }
         String query = "(|"+qsp+")";
         //-
         // System.out.println(" query: " + query);
         //-
         try {
            JSONArray users_ldap = peopleImportLdap.getUsersFromLdap(conf, query);
            JSONArray rows       = peopleImportLdap.getUsersTransformed(conf, users_ldap);
            // System.out.println(" rows\n" + rows.toString(2));
            // rv.put("rows",rows);
            // rv.put("parser",conf.getString("parser"));
            // status = "ok";
            for (int i = 0; i < rows.length(); i++) {
               JSONObject js = rows.getJSONObject(i);
               JSONObject j = new JSONObject();
               String user_name = js.getString("username");
               j.put("user_name",user_name);
               j.put("source","ldap");
               j.put("first_name",js.optString("first_name",""));
               j.put("last_name",js.optString("last_name",""));
               j.put("email",js.optString("email",""));
               rv.add(user_name);
               info.put(user_name, j);
            }
         } catch (Exception e) {
            String m = "Problems connecting to the LDAP server";
            throw (new Exception(m,e));
         }
      }
      return(rv);
   }
   
   private HashMap<Integer, ParserWithDate> _cache = new HashMap<Integer, ParserWithDate>();
   private long _timeout = ( 1 * 60 * 60 * 1000 ); // one hour
   
}
