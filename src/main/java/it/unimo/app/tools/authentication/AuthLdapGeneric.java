package it.unimo.app.tools.authentication;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;
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

public class AuthLdapGeneric implements AuthInterface {

   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(AuthLdapGeneric.class);

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
   @Autowired
   private AuthGeneric_base authGeneric_base;

   public void runUsage() {
      String s = ""
         + "\n tool usage:"
         + "\n  run authLdapGeneric <option> [args]"
         + "\n supported options:"
         + "\n  test    - various tests "
         + "\n  loguser <username> <password>"
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
         } else if (option.equals("loguser")) {
            String user = args[ipos++];
            String pwd  = args[ipos++];
            System.out.println(" logging user ["+user+"] p.["+pwd+"]");
            JSONObject rv = doLogUser(user, pwd);
            if (rv == null) {
               System.out.println(" - login failed ");
            } else {
               System.out.println(" - login ok ");
               System.out.println(" rv: " + rv.toString(2));
            }
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }

   /** 
    * 
    cd /dati/toolsZippati/projects/spacemr; gradle bootRun -Pargs="authLdapGeneric test"
    */
   public void doTest() {
      String s = ""
         + "\n hello: Auth!"
         ;
      System.out.println(s);
   }
   
   /**
    * configuration example:
    *   rPrefix=ldap_
    *   query=uid=${user}
    *   PROVIDER_URL=ldaps://ldap.ing.unimo.it:636/dc=unimore\,dc=it
    *   SECURITY_PRINCIPAL=uid=${user}\,ou=people\,dc=unimore\,dc=it
    *   role110=spazi-dief:ldap_user:unimoreDiporg2:{1}{300024}Dipartimento di Ingegneria "Enzo Ferrari"
    *   role120=spazi-dief:ldap_user:ou:Scuola di D.R. in INFORMATION AND COMMUNICATION TECHNOLOGIES (ICT)
    *   role130=spazi-dief:ldap_user:unimoreDiporg2:{1}{020088}Dip. INGEGNERIA DELL'INFORMAZIONE
    *   role140=spazi-dief:ldap_user:unimoreDiporg2:{1}{020089}Dip. INGEGNERIA MECCANICA E CIVILE
    *   role150=spazi-dief:ldap_user:unimoreDiporg2:{1}{020087}Dip. INGEGNERIA DEI MATERIALI E DELL'AMBIENTE
    *   role160=spazi-dief:ldap_user:unimoreDiporg2:{1}{020124}Centro Interd. per la Ricerca Applicata e i Servizi
    *   role170=spazi-dief:ldap_user:unimoreDiporg2:{1}{020128}Centro Interdipartimentale di ricerca
    *   role180=spazi-dief:ldap_user:unimoreDiporg2:{1}{020124}INTERMECH-Centro Interd. per la Ricerca Applicata
    *   role190=spazi-dief:ldap_user:unimoreDiporg2:{1}{020132}Centro Interdipartimentale di Ricerca Artificial
    *   role210=root:ldap_user:unimoreDiporg2:{1}{300024}Dipartimento di Ingegneria "Enzo Ferrari"
    *   role220=root:ldap_user:ou:Scuola di D.R. in INFORMATION AND COMMUNICATION TECHNOLOGIES (ICT)
    *   role230=root:ldap_user:unimoreDiporg2:{1}{020088}Dip. INGEGNERIA DELL'INFORMAZIONE
    *   role240=root:ldap_user:unimoreDiporg2:{1}{020089}Dip. INGEGNERIA MECCANICA E CIVILE
    *   role250=root:ldap_user:unimoreDiporg2:{1}{020087}Dip. INGEGNERIA DEI MATERIALI E DELL'AMBIENTE
    *   role260=root:ldap_user:unimoreDiporg2:{1}{020124}Centro Interd. per la Ricerca Applicata e i Servizi
    *   role270=root:ldap_user:unimoreDiporg2:{1}{020128}Centro Interdipartimentale di ricerca
    *   role280=root:ldap_user:unimoreDiporg2:{1}{020124}INTERMECH-Centro Interd. per la Ricerca Applicata
    *   role290=root:ldap_user:unimoreDiporg2:{1}{020132}SOFTECH-Centro Interdipartimentale di Ricerca
    *   role300=root:ldap_user:unimoreDiporg2:{1}{300562}Centro Intrerdipartimentale di Ricerca e pe
    *
  */
   private void init() throws Exception {
      //-
      // System.out.println(" --- ldap authentication initialization");
      //-
      Properties tProperties = app_system_property.getAsProperties("auth_custom_ldap_config");
      JSONObject conf = new JSONObject();
      for (Enumeration e = tProperties.propertyNames(); e.hasMoreElements();) {
         String ks = (String)e.nextElement();
         // System.out.println(" -- got ["+ks+"]");
         conf.put(ks, tProperties.getProperty(ks));
         // System.out.println(" -- added ["+ks+"]["+tProperties.getProperty(ks)+"]");
      }
      // System.out.println("conf: \n" + conf.toString(2));
      authGeneric_base.init(conf);
      _initialized = Boolean.TRUE;
   }


   /* 
    cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="authLdapGeneric loguser test ptest" | emacs_filter_maven.pl
    */
   public JSONObject doLogUser(String username
                               , String password
                               ) throws Exception {
      JSONObject user =  null;
      //-
      if (_initialized == Boolean.FALSE) { 
         init(); 
      }
      //-
      JSONObject attribs = null;
      boolean       fgOk = true;
      //-
      if (fgOk) {
         //-
         //- Ldap login
         //-
         try {
            attribs = convertAttributesToJson(ldapLogUser(username, password));
            //-
            //-
            if (attribs == null) {
               fgOk = false;
            } else {
               // System.out.println(" --- attribs:\n" + attribs.toString(2));
            }
         } catch (javax.naming.AuthenticationException e) {
            // ignored, managed as login failure
            fgOk = false;
         }
      }
      //-
      //-
      if (fgOk) {
         //-
         // System.out.println(" json attribs:\n" + attribsj.toString(2));
         //-
         //- the user is logged!
         //-
         user = authGeneric_base
            .setUserInfo(username
                         , attribs
                         , "Added by AuthLdapGeneric on " + new Date()
                         );
      }         
      //-
      return(user);
   }



   /** 
    * Returns all Ldap attributes of the user.
    * If the login is not valid, returns null
    */
   @SuppressWarnings("unchecked")
   private Attributes ldapLogUser(String user
                                  , String pass
                                  ) throws Exception {
      // System.out.println("  -- ldapLogUser");
      Attributes attribs = null;
      String s;
      //-
      String provider          = authGeneric_base.getConfValue("PROVIDER_URL");
      s = "query";s=tools.stringReplaceInString(authGeneric_base.getConfValue(s), "${user}", user);
      String query             = s;
      s="SECURITY_PRINCIPAL";s=tools.stringReplaceInString(authGeneric_base.getConfValue(s),"${user}", user);
      String securityPrincipal = s;
      //-
      // System.out.println("provider:          " + provider);
      // System.out.println("query:             " + query);
      // System.out.println("securityPrincipal: " + securityPrincipal);
      //-
      //-
      Hashtable env   = new Hashtable();
      //-
      env.put(Context.PROVIDER_URL, provider);
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      env.put(Context.SECURITY_PRINCIPAL,    securityPrincipal);
      env.put(Context.SECURITY_CREDENTIALS , pass);
      env.put("java.naming.ldap.factory.socket"
              , AuthLdapGeneric_SSLFactory.class.getName());
      //-
      //- Make a directory context by connecting with the above details.
      try {
         DirContext context = new InitialDirContext(env);
         //-
         //- Set up how we want to search - in this case the entire subtree
         //- under the specified directory root.
         SearchControls ctrl = new SearchControls();
         ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
         //-   
         // Now do the search, using the specified query and search controls.
         NamingEnumeration<SearchResult> enumeration =
            context.search("", query, ctrl);
         //-
         // We get back an enumeration which may contain any number of results,
         // so work through them one at a time.
         if (enumeration.hasMore()) {
            // System.out.println("  -- ldapLogUser: logged! ");
            SearchResult result = enumeration.next();
            //-
            //- The Distinguished Name is the primary key for an LDAP entry.
            String dn = result.getName() ;
            // System.out.println("DN : " + dn);
            // System.out.println("------------------------------");
            //-
            //- Extract the attributes (if any) from the current entry.
            attribs = result.getAttributes();
            //-
            //-
            // BasicAttribute cnAttrib   = (BasicAttribute) attribs.get("cn");
            // String         commonName = (String) cnAttrib.get(0);
            // System.out.println("Known As    : " + commonName);
            //-
         }
      } catch (Exception e ) {
         // e.printStackTrace(System.out);
         // logger.error("Error logging ["+user+"]:\n" + tools.stringStackTrace(e));
         logger.error("Error logging ["+user+"]:\n" + e.getMessage());
      }
      return(attribs);
   }


   private JSONObject convertAttributesToJson(Attributes attributes
                                              ) throws Exception {
      JSONObject rv = null;
      if (attributes != null) {
         rv = new JSONObject();
         //-
         for (Enumeration e = attributes.getIDs(); e.hasMoreElements();) {
            String id = (String)e.nextElement();
            JSONArray ja = new JSONArray();
            Attribute attribute = attributes.get(id);
            for (Enumeration e1 = attribute.getAll(); e1.hasMoreElements();) {
               Object value = e1.nextElement();
               ja.put(value.toString());
            }
            rv.put(id, ja);
         }
      }
      return(rv);
   }

   private Boolean _initialized = Boolean.FALSE;

}
