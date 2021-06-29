package it.unimo.app.tools.peopleImport;

import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

import org.json.JSONArray;
import org.json.JSONObject;

//-ldap
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.unimo.app.om.App_system_property;
import it.unimo.app.om.Spacemr_people;
import it.unimo.app.om.App_log;
import it.unimo.app.tools.Tools;

   
public class PeopleImportLdap {
   
   @Autowired
   private JdbcTemplate jdbcTemplate;
   
   @Autowired
   private Tools tools;

   @Autowired
   private App_system_property app_system_property;

   @Autowired
   private Spacemr_people spacemr_people;

   @Autowired
   private App_log app_log;
   
   public void runUsage() {
      String s = ""
         + "\n peopleImportLdap usage:"
         + "\n  import_ldap_users      - runs the import routine"
         + "\n  get_ldap_users         - queries ldap by db configuration and show ldap users"
         + "\n  show_db_configuration  - show current db configuration"
         + "\n  ldap_test_bed          - useful in creating the 'parser' see source code for usage"
         ;
      System.out.println(s);
   }

   /** 
    * command line parameter parser */
   public void run(String args[]) throws Exception {
      int ipos = 1;
      if (args.length < 2) {
         runUsage();
      } else {
         String option = args[ipos++];
         if (option.equals("test")) {
            //- cd /dati/toolsZippati/projects/spacemr; gradle bootRun -Pargs="peopleImportLdap test arg1 arg2 etc..." 
            System.out.println("-- hello! test.");
            System.out.println("args:");
            for (;ipos<args.length;ipos++) {
               System.out.println("   " + ipos + ": " + args[ipos]);
            }
            
         } else if (option.equals("show_db_configuration")) {
            //- cd /dati/toolsZippati/projects/spacemr; gradle bootRun -Pargs="peopleImportLdap show_db_configuration" 
            //-
            JSONObject property = app_system_property.get("spacemr_ldap_search_configuration");
            System.out.println("property---\n"  + property.toString(2));
            //-
            JSONObject values = new JSONObject(property.getString("value"));
            //-
            System.out.println("values---\n"  + values.toString(2));
            //-
            String ka[] = {"parser", "provider", "query"};
            for (String k: ka) {
               String v = values.getString(k);
               System.out.println("\n-"+k+"---\n"  + v);
            }
            //-
            
         } else if (option.equals("ldap_test_bed")) {
            //- parameters:
            //-  jsonConfigurationFileName
            //-  parserJavascriptFileName
            //- to create these files run
            //-   show_db_configuration
            //- and copy the info from such output.
            //- example:
            //-
            //-  cd /dati/toolsZippati/projects/spacemr; gradle bootRun -Pargs="peopleImportLdap ldap_test_bed tmp/ldap_test_bed_jsonConfigurationFileName.json tmp/ldap_test_bed_parserJavascriptFileName.js"
            //-
            //-
            String jsonConfigurationFileName = args[ipos++];
            String parserJavascriptFileName  = args[ipos++];
            //-
            JSONObject jsondata =
               new JSONObject(tools.string_loadFromFile(jsonConfigurationFileName));
            String parser = tools.string_loadFromFile(parserJavascriptFileName);
            //-
            //-
            jsondata.put("parser", parser);
            //-
            System.out.println("------------------------------------------------------------\n"
                               + "   put the followin json in the system property 'spacemr_ldap_search_configuration'"
                               + "\n------------------------------------------------------------\n"
                               + jsondata.toString(2)
                               );
            //-
            JSONArray users = getUsers(jsondata);
            System.out.println("\n----------------------- users\n"
                               + users.toString(2)
                               );
            //-
            //-
            //-
            System.out.println("\n----------------------- done.\n");
            
         } else if (option.equals("get_ldap_users")) {
            //- cd /dati/toolsZippati/projects/spacemr; gradle bootRun -Pargs="peopleImportLdap get_ldap_users" 
            //-
            JSONObject property = app_system_property.get("spacemr_ldap_search_configuration");
            //-
            JSONObject conf = new JSONObject(property.getString("value"));
            System.out.println("-------------------------- ldap configuration\n"
                               + conf.toString(2)
                               );
            //-
            //-
            JSONArray users = getUsers(conf);
            System.out.println("\n----------------------- users\n"
                               + users.toString(2)
                               );
            //-
            //-
            
         } else if (option.equals("import_ldap_users")) {
            //- cd /dati/toolsZippati/projects/spacemr; time gradle bootRun -Pargs="peopleImportLdap import_ldap_users" 
            //-
            JSONObject property = app_system_property.get("spacemr_ldap_search_configuration");
            //-
            JSONObject conf = new JSONObject(property.getString("value"));
            // System.out.println("---- ldap configuration\n" + conf.toString(2) );
            //-
            //-
            JSONArray users = getUsers(conf);
            // System.out.println("\n---- users\n" + users.toString(2) );
            //-
            //-
            doImportUsers(users);
            //- 
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }

   @SuppressWarnings("unchecked")
   public void doImportUsers(JSONArray users) throws Exception {
      HashSet usersProcessed = new HashSet();
      final String system_user = "sys-peopleImportLdap" ;
      //-
      //-
      //- step 1
      //- update all users from ldap to the DB
      //- 
      //-
      for (int i=0;i<users.length();i++) {
         JSONObject userl   = users.getJSONObject(i);
         String username = userl.getString("username");
         System.out.print(" --------------- processing - " + username);
         // System.out.println(" --- to" + "\n" + rowrv.toString(2) + "\n from\n" + row.toString(2) + "\n\n" );
         JSONObject obj = spacemr_people.getByUsername(username);
         //-
         final String[] fields = {"role", "last_name", "department", "first_name", "email"};
         //-
         if (obj == null) {
            // create ---;
            // obj = new JSONObject();
            // obj.setString("")
            obj = new JSONObject();
            for  (String k: fields){
               obj.put(k, userl.optString(k,""));
            }
            obj.put("username", username);
            obj.put("fg_in_ldap", Boolean.TRUE);
            obj.put("nota", "");
            obj.put("configuration", "");
            //-
            int id = spacemr_people.insert(obj);
            app_log.writeLog(spacemr_people, id, system_user, obj);
            System.out.println(" - added");
            //-
         } else {
            // update ---;
            // System.out.println(" ----- found: \n" + obj.toString(2));
            // System.out.println(" vs: \n" + userl.toString(2));
            String objs=obj.toString();
            for  (String k: fields){
               obj.put(k, userl.optString(k,""));
            }
            obj.put("fg_in_ldap", Boolean.TRUE);
            //-
            // System.out.println(" to: \n" + obj.toString(2));
            if(!obj.toString().equals(objs)) {
               // System.out.println(" ------ saving");
               app_log.writeLog(spacemr_people, obj.getInt("spacemr_people_id"), system_user,obj);
               spacemr_people.update(obj);
               System.out.println(" - updated");
            } else {
               System.out.println(" - ok");
            }
         }
         usersProcessed.add(username);
      }
      //-
      //-
      //- step 2
      //- for each user in the db "in ldap"
      //- unset "in ldap" for those not in "users"
      //- 
      //-
      // -qui-
      // select username, fg_in_ldap from spacemr_people where fg_in_ldap <> 0 ;
      for (String username: spacemr_people.getAllUsernamesInLdap()) {
         if (usersProcessed.contains(username)) {
            // ok
         } else {
            // System.out.println(" not in ldap: " + username);
            JSONObject obj = spacemr_people.getByUsername(username);
            obj.put("fg_in_ldap", Boolean.FALSE);
            app_log.writeLog(spacemr_people, obj.getInt("spacemr_people_id"), system_user, obj);
            spacemr_people.update(obj);
         }
      }
   }
   
   public JSONArray getUsers(JSONObject conf
                             ) throws Exception {
      //-
      //-
      JSONArray users_ldap = getUsersFromLdap(conf);
      // System.out.println("\n----------------------- ldap users\n"
      //                    + users_ldap.toString(2)
      //                    );
      //-
      //-
      JSONArray users_transformed = getUsersTransformed(conf, users_ldap);
      // System.out.println("\n----------------------- users_transformed\n"
      //                    + users_transformed.toString(2)
      //                    );
      //-
      //-
      return(users_transformed);
   }

   public JSONArray getUsersTransformed(JSONObject conf
                                        , JSONArray users_ldap
                                        ) throws Exception {
      JSONArray rv = new JSONArray();
      //-
      ScriptEngineManager factory = new ScriptEngineManager();
      // create a JavaScript engine
      ScriptEngine engine = factory.getEngineByName("JavaScript");
      //-
      engine.eval("parser="+conf.getString("parser")+";");
      //-
      for (int i=0;i<users_ldap.length();i++) {
         JSONObject row   = users_ldap.getJSONObject(i);
         JSONObject rowrv = ldap_parser_transform(engine, row);
         // System.out.println(" --- to" + "\n" + rowrv.toString(2) + "\n from\n" + row.toString(2) + "\n\n" );
         rv.put(rowrv);
      }
      return(rv);
   }

   public JSONObject ldap_parser_transform(ScriptEngine engine
                                           , JSONObject ldap_row
                                           ) throws Exception {
      JSONObject rv = new JSONObject();
      javax.script.Bindings parser_in = engine.createBindings();
      java.util.Iterator i = ldap_row.keys();
      // System.out.println("ldap_row: " + ldap_row);
      while (i.hasNext()) {
         String name=(String)i.next();
         // System.out.println(" n.["+name+"]: ["+row.get(name)+"]");
         parser_in.put(name, ldap_row.get(name));
      }
      engine.put("parser_in", parser_in);
      // engine.eval("print(in.cn + ': ' + in.mail);");
      engine.eval("parser_out=parser(parser_in);");
      javax.script.Bindings parser_out = (javax.script.Bindings)engine.get("parser_out");
      Set parser_out_keys = parser_out.keySet();
      for(Object o: parser_out_keys) {
         String name = (String)o;
         rv.put(name, parser_out.get(name));
         // System.out.println("   n.["+name+"]: ["+parser_out.get(name)+"]");
      }
      // rv.put("ldapRow", ldap_row);
      return(rv);
   }

   public JSONArray getUsersFromLdap(JSONObject conf) throws Exception {
      return(getUsersFromLdap(conf, conf.getString("query")));
   };

   @SuppressWarnings("unchecked")
   public JSONArray getUsersFromLdap(JSONObject conf, String query) throws Exception {
      //-
      JSONArray  rows = new JSONArray();
      //-
      DirContext    dirContext = null;
      SearchControls      ctrl = null;
      //-
      //- ldap connection
      Hashtable env   = new Hashtable();
      env.put(javax.naming.Context.PROVIDER_URL, conf.getString("provider"));
      env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY
              , "com.sun.jndi.ldap.LdapCtxFactory");
      env.put("java.naming.ldap.factory.socket"
              , it.unimo.app.tools.authentication.AuthLdapGeneric_SSLFactory.class.getName());
      //-
      dirContext = new InitialDirContext(env);
      ctrl       = new SearchControls();
      ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
      //-
      // System.out.println(" query: " + query);
      NamingEnumeration<SearchResult> results =
         dirContext.search("", query, ctrl);;
      //-
      //-
      // rv = " nome: " + nome
      //    + " cognome: " + cognome
      //    + " provider: " + provider
      //    ;
      int counter = 10000;
      // System.out.println(" searching....");
      while (results.hasMoreElements() && counter-- > 0) {
         JSONObject j = new JSONObject();
         SearchResult  r = results.nextElement();
         Attributes attr = r.getAttributes();
         if (true){ 
            NamingEnumeration<String> ids = attr.getIDs();
            while (ids.hasMoreElements()) {
               String id = ids.nextElement();
               // System.out.println("   "+id + " - " + attr.get(id));
               j.put(id, spacemr_ldap_search_checkNullAttribute(attr, id, ""));
            }
         }
         rows.put(j);
         //-
      }
      return(rows);
   };

   
   private Object spacemr_ldap_search_checkNullAttribute(Attributes attr
                                                         , String attributeName
                                                         , String message
                                                         ) throws Exception {
      Object rv = message;
      if (attr.get(attributeName) != null) {
         if (attr.get(attributeName).size() == 1) {
            rv = "" + attr.get(attributeName).get();
         } else {
            JSONArray ja = new JSONArray();
            for (int i=0; i<attr.get(attributeName).size(); i++) {
               ja.put("" + attr.get(attributeName).get(i));
            }
            rv = ja;
         }
      }
      return(rv);
   }

   
}
