package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.Hashtable;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import it.unimo.app.tools.AppPermissionTools_userData;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


//-
@Controller
public class Mmd_ldap {
   @Autowired
   private AppControllerTools appControllerTools;
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private AppSessionTools appSessionTools;
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private App_user app_user;
   @Autowired
   private Tools tools;
   //-

   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(Mmd_ldap.class);

   @RequestMapping(value="mmd_ldap/mmd_ldap_search")
   @SuppressWarnings("unchecked")
   public void mmd_ldap_search(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      // System.out.println(" in mmd_ldap_search");
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_mmd_ldap_search")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      //-
      JSONObject conf = app_system_property.getAsJSONObject("mmd_ldap_search_configuration");
      //-
      String user_name       = content.optString("user_name","");
      String user_first_name = content.optString("user_first_name","");
      String user_last_name  = content.optString("user_last_name","");
      //-
      String query = conf.getString("query");
      query=tools.stringReplaceInString(query, "${user_name}", user_name);
      query=tools.stringReplaceInString(query, "${user_first_name}",  user_first_name);
      query=tools.stringReplaceInString(query, "${user_last_name}",   user_last_name);
      //-
      // System.out.println(" query: " + query);
      //-
      try {
         DirContext    dirContext = null;
         SearchControls      ctrl = null;
         //-
         //- ldap connection
         Hashtable env   = new Hashtable();
         env.put(javax.naming.Context.PROVIDER_URL, conf.getString("provider"));
         env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY
                 , "com.sun.jndi.ldap.LdapCtxFactory");
         // env.put(javax.naming.Context.SECURITY_PRINCIPAL,    securityPrincipal);
         // env.put(javax.naming.Context.SECURITY_CREDENTIALS , pass);
         env.put("java.naming.ldap.factory.socket"
                 , it.unimo.app.tools.authentication.AuthLdapGeneric_SSLFactory.class.getName());
         //-
         dirContext = new InitialDirContext(env);
         ctrl    = new SearchControls();
         ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
         //-
         // System.out.println(" query: " + query);
         NamingEnumeration<SearchResult> results = dirContext.search("", query, ctrl);;
         //-
         //-
         // rv = " nome: " + nome
         //    + " cognome: " + cognome
         //    + " provider: " + provider
         //    ;
         JSONArray  rows = new JSONArray();
         int counter = 100;
         // System.out.println(" searching....");
         while (results.hasMoreElements() && counter-- > 0) {
            SearchResult  r = results.nextElement();
            Attributes attr = r.getAttributes();
            // if (true){ 
            //    NamingEnumeration<String> ids = attr.getIDs();
            //    while (ids.hasMoreElements()) {
            //       String id = ids.nextElement();
            //       System.out.println("   "+id + " - " + attr.get(id));
            //    }
            // }
            String s = ""
               + attr.get("uid").get()+"\t"
               + attr.get("sn").get()+"\t"
               + attr.get("givenName").get()+"\t"
               + attr.get("uidNumber").get()+"\t"
               + attr.get("description").get()+"\t"
               ;
            // System.out.println(" s: " + s);
            //    ;
            // rv = rv + "\n" + s ;
            JSONObject j = new JSONObject()
               .put("user_name", mmd_ldap_search_checkNullAttribute(attr, "uid", ""))
               .put("user_last_name", mmd_ldap_search_checkNullAttribute(attr, "sn", ""))
               .put("user_first_name", mmd_ldap_search_checkNullAttribute(attr, "givenName", ""))
               .put("description", mmd_ldap_search_checkNullAttribute(attr, "description", "no description"))
               ;
            rows.put(j);
            //-
         }
         rv.put("rows",rows);
      } catch (Exception e) {
         status = "Problems connecting to the server \n" + tools.stringStackTrace(e);
         logger.error(status);
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   private String mmd_ldap_search_checkNullAttribute(Attributes attr
                                                     , String attributeName
                                                     , String message
                                                     ) throws Exception {
      String rv = message;
      if (attr.get(attributeName) != null) {
         rv = "" + attr.get(attributeName).get();
      }
      return(rv);
   }


}
