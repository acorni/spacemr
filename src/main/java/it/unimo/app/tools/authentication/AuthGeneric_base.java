package it.unimo.app.tools.authentication;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import java.util.Hashtable;
import org.springframework.beans.factory.annotation.Autowired;

import org.json.JSONObject;
import org.json.JSONArray;
import it.unimo.app.om.App_system_property;
import it.unimo.app.om.App_user;
import it.unimo.app.om.App_role;
import it.unimo.app.om.App_group;
import it.unimo.app.om.App_group_role_user;
import it.unimo.app.om.App_permission;
import it.unimo.app.tools.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthGeneric_base {

   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(AuthGeneric_base.class);

   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private App_user app_user;
   @Autowired
   private App_role app_role;
   @Autowired
   private App_group app_group;
   @Autowired
   private App_permission app_permission;
   @Autowired
   private Tools tools;
   @Autowired
   private App_group_role_user app_group_role_user;

   public void runUsage() {
      String s = ""
         + "\n tool usage:"
         + "\n  run authGeneric_base <option> [args]"
         + "\n supported options:"
         + "\n  test    - various tests "
         + "\n  setUserInfo  configuration_file_name username attributes_file_name"
         ;
      System.out.println(s);
   }

   /** 
    * command line parameter parser */
   public void run(String args[]) throws Exception {
      int ipos = 1;
      String option = args[ipos++];
      if (args.length < 2) {
         runUsage();
      } else {
         if (option.equals("test")) {
            System.out.println(" hello! test.");
            doTest();
         } else if (option.equals("setUserInfo")) {
            String configuration_file_name = args[ipos++];
            String username = args[ipos++];
            String attributes_file_name = args[ipos++];
            //-
            JSONObject attributes =
               new JSONObject(Tools.string_loadFromFile(attributes_file_name));
            JSONObject conf       =
               new JSONObject(Tools.string_loadFromFile(configuration_file_name));
            //-
            System.out.println(" setUserInfo ["+username+"]");
            //-
            init(conf);
            //-
            JSONObject user = setUserInfo(username
                                          , attributes
                                          , "Added by AuthLdapGeneric on " + new Date()
                                          );
            System.out.println(" user:\n" + user.toString(2));
            //-
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }


   /** 
    * 
      cd /dati/toolsZippati/projects/spacemr
      gradle bootRun -Pargs="authGeneric_base setUserInfo tmp/auth-conf-ldap.txt acorni tmp/auth-attribs-ldap.txt"
      conf example:
        {
          "rPrefix": "ldap_",
          "role110": "spazi-dief:ldap_user:unimoreDiporg2:{1}{300024}Dipartimento di Ingegneria \"Enzo Ferrari\"",
          "role120": "spazi-dief:ldap_user:ou:Scuola di D.R. in INFORMATION AND COMMUNICATION TECHNOLOGIES (ICT)",
          "role130": "spazi-dief:ldap_user:unimoreDiporg2:{1}{020088}Dip. INGEGNERIA DELL'INFORMAZIONE",
          "role140": "spazi-dief:ldap_user:unimoreDiporg2:{1}{020089}Dip. INGEGNERIA MECCANICA E CIVILE",
          "role150": "spazi-dief:ldap_user:unimoreDiporg2:{1}{020087}Dip. INGEGNERIA DEI MATERIALI E DELL'AMBIENTE",
          "role160": "spazi-dief:ldap_user:unimoreDiporg2:{1}{020124}Centro Interd. per la Ricerca Applicata e i Servizi",
          "role170": "spazi-dief:ldap_user:unimoreDiporg2:{1}{020128}Centro Interdipartimentale di ricerca",
          "role180": "spazi-dief:ldap_user:unimoreDiporg2:{1}{020124}INTERMECH-Centro Interd. per la Ricerca Applicata",
          "role190": "spazi-dief:ldap_user:unimoreDiporg2:{1}{020132}Centro Interdipartimentale di Ricerca Artificial",
          "role210": "root:ldap_user:unimoreDiporg2:{1}{300024}Dipartimento di Ingegneria \"Enzo Ferrari\"",
          "role220": "root:ldap_user:ou:Scuola di D.R. in INFORMATION AND COMMUNICATION TECHNOLOGIES (ICT)",
          "role230": "root:ldap_user:unimoreDiporg2:{1}{020088}Dip. INGEGNERIA DELL'INFORMAZIONE",
          "role240": "root:ldap_user:unimoreDiporg2:{1}{020089}Dip. INGEGNERIA MECCANICA E CIVILE",
          "role250": "root:ldap_user:unimoreDiporg2:{1}{020087}Dip. INGEGNERIA DEI MATERIALI E DELL'AMBIENTE",
          "role260": "root:ldap_user:unimoreDiporg2:{1}{020124}Centro Interd. per la Ricerca Applicata e i Servizi",
          "role270": "root:ldap_user:unimoreDiporg2:{1}{020128}Centro Interdipartimentale di ricerca",
          "role280": "root:ldap_user:unimoreDiporg2:{1}{020124}INTERMECH-Centro Interd. per la Ricerca Applicata",
          "role290": "root:ldap_user:unimoreDiporg2:{1}{020132}SOFTECH-Centro Interdipartimentale di Ricerca",
          "role300": "root:ldap_user:unimoreDiporg2:{1}{300562}Centro Intrerdipartimentale di Ricerca e pe",
        }

    */
   
   /** 
    * 
    cd /dati/toolsZippati/projects/spacemr; gradle bootRun -Pargs="authGeneric_base test"
    */
   public void doTest() {
      String s = ""
         + "\n hello: Auth!"
         ;
      System.out.println(s);
   }
   
   public void init(JSONObject conf) throws Exception {
      _conf = conf;
      //-
      Vector<RuleGroupRole> rules = new Vector<RuleGroupRole>();
      //-
      _rules   = rules;
      _rPrefix = conf.getString("rPrefix");
      //-
      String propPrefix = "role";
      for (String k: conf.getNames(conf)) {
         // System.out.println(" k: " + k);
         if (k.startsWith(propPrefix)) {
            String s = conf.getString(k);
            // System.out.println("s: " + s);
            int pos      = s.indexOf(':');
            if (pos < 0) {
               String m = "Error parsing auth rule ["+k+"] first colon(:) not found.";
               throw (new Exception(m));
            }
            String group = s.substring(0,pos);
            pos++;
            int pos1     = s.indexOf(':', pos);
            if (pos < 0) {
               String m = "Error parsing auth rule ["+k+"] second colon(:) not found.";
               throw (new Exception(m));
            }
            String role  = s.substring(pos,pos1);
            pos = pos1+1;
            String triggersString = s.substring(pos);
            RuleGroupRole gar =
               new RuleGroupRole(group, role, triggersString);
            rules.add(gar);
         }
      }
      // System.out.println("rules: " + rules);
   }

   public JSONObject setUserInfo(String username
                                 , JSONObject attribs
                                 , String comment
                                 ) throws Exception {
      JSONObject user =
         writeApp_userInfo(username, attribs, "Added by AuthLdapGeneric on " + new Date());
      //-
      app_group_role_user.insert(username,"root", "user");
      setGroupsAndRoles(user, attribs);
      //-
      return(user);
   }


   public JSONObject writeApp_userInfo(String username
                                       , JSONObject attribs
                                       , String nota
                                       ) throws Exception {
      // System.out.println(" --- attribs: " + attribs.toString(2));
      JSONObject user =  app_user.get(username);
      if (user == null) {
         //-
         int app_user_id = app_permission.initPermissions_addRoles_addUser(username, nota);
         {
            byte[] random = new byte[15];
            new Random().nextBytes(random);
            String dbpassword = tools.base64Encode(random);
            app_user.setPassword(app_user_id, dbpassword);
         }
         user =  app_user.get(username);
         //-
         //-
         //- Granting base user roles.
         //-
         //-
      }
      //-
      //- updating
      //-
      {
         JSONArray a = attribs.optJSONArray("givenName");
         //- case sensitive, sometime
         if (a == null) {
            a = attribs.optJSONArray("givenname");
         }
         user.put("first_name", a.getString(0));
      }
      String n = null;
      //-
      if (attribs.optJSONArray("sn") != null) {
         n = attribs.getJSONArray("sn").getString(0);
      }
      user.put("last_name",  n);
      //-
      n=null;
      if (attribs.optJSONArray("mail") != null) {
         n = attribs.getJSONArray("mail").getString(0);
      }
      user.put("email",      n);
      //-
      app_user.update(user);
      app_user.setLdap_info(username,  attribs.toString(2));
      //-
      return(user);
   }
   
   @SuppressWarnings("unchecked")
   private void setGroupsAndRoles(JSONObject user
                                  , JSONObject attributes
                                  ) throws Exception {
      //-
      String user_name = user.getString("user_name");
      int app_user_id  = user.getInt("app_user_id");
      //-
      // System.out.println(" -- setGroupsAndRoles");
      //-
      //- retrieve all current auto-roles
      //-
      //- These will be compared with the ones from LDAP
      //- and if not confimed, will be deleted
      //-
      //-
      //- Stored as  <groupName>,<roleName>
      Vector<String> oldRoles = new Vector<String>();
      oldRoles.addAll(app_group_role_user.getPermissionForUserAsVectorOfCSVString(app_user_id, _rPrefix));
      //-
      Vector<String> groupRoles = getGroupRoles(attributes);
      // System.out.println("groupRoles: " + groupRoles);
      // System.out.println("oldRoles: " + oldRoles);
      //-
      Vector<String> rolesToAdd = (Vector<String>)groupRoles.clone();
      rolesToAdd.removeAll(oldRoles);
      //-
      Vector<String> rolesToRemove = (Vector<String>)oldRoles.clone();
      rolesToRemove.removeAll(groupRoles);
      //-
      // System.out.println("rolesToAdd: " + rolesToAdd);
      // System.out.println("rolesToRemove: " + rolesToRemove);
      //-
      {
         //- removing roles
         for (String groupRoleName: rolesToRemove) {
            Vector<String> v = tools.splitString(groupRoleName, ',');
            String groupName = v.get(0);
            String roleName  = v.get(1);
            //-
            app_group_role_user.delete(user_name,groupName,roleName);
            // System.out.println(" ... revoking "+groupName+"|"+roleName+"");
         }
      }
      {
         //- adding roles
         for (String groupRoleName: rolesToAdd) {
            Vector<String> v = tools.splitString(groupRoleName, ',');
            String groupName = v.get(0);
            String roleName  = v.get(1);
            //-
            app_group_role_user.insert(user_name,groupName,roleName);
            // System.out.println(" ... granting "+groupName+"|"+roleName+"");
         }
      }
   }

   
   /** 
    * matches user attrebutes and configuration
    * rules.
    * Returns allowed groups and roles in the format
    *      "<groupName>,<roleName>"
    */   
   public Vector<String> getGroupRoles(JSONObject attributes
                                       ) throws Exception {
      //-
      Vector<String> rv = new Vector<String>();
      //-
      for (RuleGroupRole ruleGroupRole: getRules()) {
         // System.out.println(" -------- ruleGroupRole: " + ruleGroupRole);
         if (ruleGroupRole.matches(attributes)) {
            rv.add(ruleGroupRole.getGroupName()
                   + "," + ruleGroupRole.getRoleName()
                   );
         }
      }
      return(rv);
   }

   
   /**
    * @name Private Classes
    */
   //@{
   private class RuleGroupRole {
      public RuleGroupRole(String groupName
                           , String roleName
                           , String triggerString
                           ) {
         _groupName = groupName;
         _roleName  = roleName;
         //-
         Vector<String> v = tools.splitString(triggerString, ':');
         // System.out.println("v: " + v);
         for (int i=0; i < v.size()-1; i += 2) {
            String  ldapFieldName = v.get(i);
            String  value = v.get(i+1);
            Trigger trigger = new Trigger(ldapFieldName,  value);
            _triggers.add(trigger);
         }
      }
      /** 
       * Getter for the _trigger field */
      public Vector<Trigger> getTriggers(){
         return(_triggers);
      }
      /** 
       * Getter for the _groupName field */
      public String getGroupName(){
         return(_groupName);
      }
      /** 
       * Getter for the _roleName field */
      public String getRoleName(){
         return(_roleName);
      }
      /** 
       * true if matches all triggers */
      public boolean matches(JSONObject attributes) throws Exception {
         boolean rv = true;
         for (Trigger trigger : _triggers) {
            // System.out.println(" -aaaa------- trigger: " + trigger);
            rv = rv && trigger.matches(attributes);
         }
         return(rv);
      }
      /** 
       * Returns a string representation of this object */
      public String toString() {
         boolean fgFirst;
         StringBuffer rv = new StringBuffer();
         try {
            rv.append(getClass().getName() + ".[\n");
            rv.append(" triggers.["+ getTriggers()+"]\n");
            rv.append(" groupName.["+ getGroupName()+"]\n");
            rv.append(" roleName.["+ getRoleName()+"]\n");
            rv.append("]\n");
         } catch (Exception e) {
            rv.append("Problemi " + e);
         }
         return(rv.toString());
      }
      Vector<Trigger> _triggers = new Vector<Trigger>();
      String  _groupName;
      String  _roleName;
   }


   //@}

   private class Trigger {
      public Trigger(String ldapFieldName
                     , String value
                     ) {
         _ldapFieldName = ldapFieldName;
         _value        = value;
      }
      /** 
       * Getter for the _ldapFieldName field */
      public String getLdapFieldName(){
         return(_ldapFieldName);
      }
      /** 
       * Getter for the _value field */
      public String getValue(){
         return(_value);
      }
      /** 
       * true if exists a matching attribute name with
       * matching value
       */
      public boolean matches(JSONObject attributes) throws Exception {
         boolean rv = false;
         JSONArray names = attributes.names();
         for(int i = 0; i<names.length(); i++){
            String id = names.getString(i);
            // System.out.println(" ---id------- id: " + id);
            if (getLdapFieldName().equals(id) && !rv) {
               boolean localRv = false;
               JSONArray attribute = attributes.getJSONArray(id);
               for (int i1 = 0; i1<attribute.length(); i1++){
                  String value = attribute.getString(i1);
                  if (value.startsWith(getValue())) {
                     localRv = true;
                  }
               }
               // System.out.println(" ---localRv------- : " + localRv);
               rv = rv || localRv;
            }
         }
         // System.out.println(" --trigger-- for ["+getLdapFieldName()+"],["+getValue()+"]: " + rv);
         return(rv);
      }
      /** 
       * Returns a string representation of this object
       */
      public String toString() {
         boolean fgFirst;
         StringBuffer rv = new StringBuffer();
         try {
            rv.append(getClass().getName() + ".[\n");
            rv.append(" ldapFieldName.["+ getLdapFieldName()+"]\n");
            rv.append(" value.["+ getValue()+"]\n");
            rv.append("]\n");
         } catch (Exception e) {
            rv.append("Problemi " + e);
         }
         return(rv.toString());
      }
      String _ldapFieldName;
      String _value;
   }

   protected String getConfValue(String key) {
      String rv = _conf.getString(key);
      return(rv);
   }

   /** 
    * Getter for the _rules field */
   public Vector<RuleGroupRole> getRules(){
      return(_rules);
   }


   private JSONObject             _conf  = null;
   private Vector<RuleGroupRole>  _rules = null;
   private String                 _rPrefix = "rPrefix";

}
