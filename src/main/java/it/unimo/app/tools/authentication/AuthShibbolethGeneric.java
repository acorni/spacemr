package it.unimo.app.tools.authentication;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.json.JSONArray;
import it.unimo.app.om.App_system_property;
import it.unimo.app.om.App_user;
import it.unimo.app.om.App_role;
import it.unimo.app.om.App_group;
import it.unimo.app.om.App_group_role_user;
import it.unimo.app.om.App_permission;
import it.unimo.app.tools.Tools;
import it.unimo.app.controller.App_userController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthShibbolethGeneric implements AuthInterface {

   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(AuthShibbolethGeneric.class);

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
   @Autowired
   private App_userController app_userController;


   public void runUsage() {
      String s = ""
         + "\n tool usage:"
         + "\n  run authShibbolethGeneric <option> [args]"
         + "\n supported options:"
         + "\n  test    - various tests "
         + "\n  loguser <username> <password>"
         ;
      System.out.println(s);
   }


   /**  
    * the (to be) protected bounce page
    *    http://localhost:8080/authentication/auth_shibbolet_generic
    * example of authenticated headers - UniMORE
    *   {
    *     "headers": {
    *       "accept": "* /*",
    *       "accept-encoding": "gzip, deflate, br",
    *       "accept-language": "en-US,en;q=0.5",
    *       "affiliation": "staff@unimore.it;member@unimore.it;employee@unimore.it",
    *       "city": "",
    *       "cn": "Alberto CORNI",
    *       "connection": "Keep-Alive",
    *       "content-length": "14",
    *       "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
    *       "cookie": "JSESSIONID=4A5741CBE5D7B4DCDEEC24099A2ECC6F; _shibsession_64656661756c7468747470733a2f2f70726573656e7a652e756e696d6f72652e69742f73686962626f6c657468=_92024136cd9d1e7f4a1679ae02205827",
    *       "country": "",
    *       "description": "",
    *       "dollymail": "",
    *       "entitlement": "",
    *       "eppn": "acorni@unimore.it",
    *       "givenname": "Alberto",
    *       "host": "presenze.unimore.it",
    *       "initials": "",
    *       "ismemberof": "",
    *       "mail": "alberto.corni@unimore.it",
    *       "origin": "https://presenze.unimore.it",
    *       "ou": "people;Dipendenti",
    *       "persistent-id": "https://idp.unimore.it/idp/shibboleth!https://presenze.unimore.it/shibboleth!Pd7qTnu0omPxvz/6UzxJgDs48KY=",
    *       "referer": "https://presenze.unimore.it/spacemr/",
    *       "remote_user": "acorni@unimore.it",
    *       "shib-application-id": "default",
    *       "shib-assertion-count": "",
    *       "shib-authentication-instant": "2021-01-30T19:26:10.998Z",
    *       "shib-authentication-method": "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport",
    *       "shib-authncontext-class": "urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport",
    *       "shib-authncontext-decl": "",
    *       "shib-cookie-name": "",
    *       "shib-handler": "https://presenze.unimore.it/Shibboleth.sso",
    *       "shib-identity-provider": "https://idp.unimore.it/idp/shibboleth",
    *       "shib-session-expires": "1612063571",
    *       "shib-session-id": "_92024136cd9d1e7f4a1679ae02205827",
    *       "shib-session-inactivity": "1612042438",
    *       "shib-session-index": "_e9525f9212bb80e02890a87e5b5a5ca7",
    *       "sn": "CORNI",
    *       "telephonenumber": "",
    *       "title": "",
    *       "uid": "acorni",
    *       "unimore_taxpayer_number": "",
    *       "unimorebadge": "",
    *       "unimorecodicefiscale": "",
    *       "unimorecorscodicecorso": "",
    *       "unimorecorscodiceindirizzo": "",
    *       "unimorecorscodicetipocorso": "",
    *       "unimorecorsdescrizioneindirizzo": "",
    *       "unimorecorstipocorso": "",
    *       "unimoredescruolo": "",
    *       "unimoredipafferenzadidattica": "",
    *       "unimoredipcodicedidattica": "",
    *       "unimoredipcodiceruolo": "",
    *       "unimoredipcodicesettore": "",
    *       "unimoredipdescrizioneinquadramento": "",
    *       "unimoredipmatricola": "",
    *       "unimorediporg2": "{1}{300024}Dipartimento di Ingegneria \"Enzo Ferrari\"",
    *       "unimorediporg4": "{1}{300122}Dipartimento di Ingegneria \"Enzo Ferrari\""
    *       "unimoredipprofilo": "",
    *       "unimoredipruolo": "",
    *       "unimoredipsettore": "",
    *       "unimorefacsimiletelephonenumber": "",
    *       "unimorestudannocorso": "",
    *       "unimorestudcorso": "",
    *       "unimorestuddescrcorso": "",
    *       "unimorestuddescrfacolta": "",
    *       "unimorestudfacolta": "",
    *       "unimorestudmatricola": "",
    *       "unimorestudnumerotessera": "",
    *       "unimorestudtipodidattica": "",
    *       "unimorestudtipoiscrizione": "",
    *       "unimorestudultimoannoaccademico": "",
    *       "unimoretelephonenumber": "",
    *       "user-agent": "Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0",
    *       "x-forwarded-for": "151.67.120.177",
    *       "x-forwarded-host": "presenze.unimore.it",
    *       "x-forwarded-server": "presenze.dmz-ext.unimo.it",
    *       "x-requested-with": "XMLHttpRequest",
    *     },
    *
    * Lepida spid provider: 
    *   E-Mail Surname Fiscal_code Name
    *
    */
   @Transactional
   @RequestMapping(value="authentication/auth_shibboleth_generic/**")
   public void auth_shibbolet_generic(HttpSession session
                                      , HttpServletRequest  request
                                      , HttpServletResponse response
                                      ) throws Exception {
      String status = null;
      String auth_single_sign_on_protected_url =
         app_system_property.getAsString("auth_single_sign_on_protected_url");
      String rv = ""
         + "\n <!doctype html>"
         + "\n <html>"
         + "\n   <head>"
         + "\n     <title>Spacemr Single sign on redirect page.</title>"
         + "\n   </head>"
         + "\n   <body >"
         + "\n     Spacemr Single sign on redirect page."
         ;
      //-
      //-
      {
         //-
         //-
         //- loop detection
         //-
         //-
         int counter = 1;
         Object o = session.getAttribute("auth_single_sign_on_protected_url_counter");
         if (o != null) {
            counter = ((Integer)o).intValue();
         }
         counter = counter + 1;
         session.setAttribute("auth_single_sign_on_protected_url_counter", counter);
         //-
         if ((counter % 32) == 0) {
            String message = "It may exists an authentication loop, ask your Application Administrator to check the system variable  auth_single_sign_on_protected_url  and  auth_single_sign_on_protected_url";
            message = message + ""
               + "\n current auth_single_sign_on_protected_url:["
               + auth_single_sign_on_protected_url +"]"
               + "\n."
               ;
            //- app_system_property.set("auth_single_sign_on_protected_url", (String)null);
            /* 
               update app_system_property
                set value = NULL
                where name = 'auth_single_sign_on_protected_url'
                ;
               update app_system_property
                set value = 'authentication/auth_shibboleth_generic'
                where name = 'auth_single_sign_on_protected_url'
                ;
             */
            throw new Exception(message);
         }
         //-
         //-
      }
      JSONObject headers = new JSONObject();
      Enumeration<String> headerNames = request.getHeaderNames();
      if (headerNames != null) {
         while (headerNames.hasMoreElements()) {
            String n = headerNames.nextElement();
            JSONArray ja = new JSONArray();
            ja.put(request.getHeader(n));
            headers.put(n, ja);
         }
      }
      // System.out.println(" -- heders:\n" + headers.toString(2));
      // rv = rv + "\n" + headers.toString(2);
      //-
      rv = rv + ""
         + "\n   <script>"
         // + "\n     var url = '../' + location.hash    ;"
         // + "\n     window.location=url;"
         + "\n     var auth_single_sign_on_protected_url = '"+auth_single_sign_on_protected_url+"';"
         + "\n     var u = location.href;"
         + "\n     var pos = u.indexOf(auth_single_sign_on_protected_url);"
         + "\n     var prefix = u.substring(0,pos-1);"
         + "\n     pos = pos + auth_single_sign_on_protected_url.length + 1;"
         + "\n     u = atob(u.substring(pos));"
         + "\n     var url = prefix + u    ;"
         + "\n     window.location=url;"
         + "\n   </script>"
         + "\n   </body>"
         + "\n </html>"
         ;
      //-
      //-
      //-
      //-
      if (_initialized == Boolean.FALSE) { 
         init();
      }
      //-
      //- configure and save user in session
      //-
      {
         String username = headers.getJSONArray("uid").getString(0);
         //-
         // System.out.println(" -- user to identify: " + username);
         //-
         JSONObject user = authGeneric_base
            .setUserInfo(username
                         , headers
                         , "Added by AuthShibbolethGeneric on " + new Date()
                         );
         app_userController.app_user_login_post_auth(user, session, request, response);
         // session.setAttribute("auth_shibbolet_generic_user", user);
      }
      //-
      //-
      //-
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      response.getOutputStream().write(rv.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      response.getOutputStream().flush();
   }


   private void init() throws Exception {
      //-
      // System.out.println(" --- ldap authentication initialization");
      //-
      Properties tProperties =
         app_system_property.getAsProperties("auth_custom_shibboleth_config");
      JSONObject conf = new JSONObject();
      for (Enumeration e = tProperties.propertyNames(); e.hasMoreElements();) {
         String ks = (String)e.nextElement();
         // System.out.println(" -- got ["+ks+"]");
         conf.put(ks, tProperties.getProperty(ks));
         // System.out.println(" -- added ["+ks+"]["+tProperties.getProperty(ks)+"]");
      }
      // System.out.println("conf: \n" + conf.toString(2));
      //-
      authGeneric_base.init(conf);
      //-
      _initialized = Boolean.TRUE;
   }

   
   public JSONObject doLogUser(String username
                               , String password
                               ) throws Exception {
      // JSONObject user = (JSONObject)session.getAttribute("auth_shibbolet_generic_user");
      // return(user);
      return(null);
   }



   
   private Boolean    _initialized = Boolean.FALSE;

}
