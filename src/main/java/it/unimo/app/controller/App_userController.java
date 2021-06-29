package it.unimo.app.controller;
//-
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
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
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppPermissionTools_userData;
import it.unimo.app.tools.AppSessionTools;
import it.unimo.app.tools.authentication.AuthInterface;
import it.unimo.app.tools.authentication.WrongLoginManager;

//-
/*
   cd /dati/bin/workspace/tmp/mvc; gradle run
*/
@Controller
public class App_userController {
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private AppControllerTools appControllerTools;
   @Autowired
   private App_log app_log;
   @Autowired
   private App_user app_user;
   @Autowired
   private App_permission app_permission;
   @Autowired
   private AppSessionTools appSessionTools;
   @Autowired
   private App_role app_role;
   @Autowired
   private App_role_permission app_role_permission;
   @Autowired
   private App_group app_group;
   @Autowired
   private App_group_role_user app_group_role_user;
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private ApplicationContext applicationContext;
   @Autowired
   private Tools tools;
   @Autowired
   private WrongLoginManager wrongLoginManager;

   @Transactional
   @RequestMapping(value="app_user/app_user_login")
   public void app_user_login(HttpSession session
                              , HttpServletRequest  request
                              , HttpServletResponse response
                              , @RequestParam("user_name") String user_name
                              , @RequestParam("password") String password
                              , @RequestParam("app_url") String app_url
                              ) throws Exception {
      JSONObject rv = new JSONObject();
      String status = "ok";
      //-
      String ip = request.getRemoteAddr();
      // System.out.println(" -- ip: " + ip);
      String proxyIp = app_system_property.getAsString("auth_x-forwarded-for");
      if (proxyIp != null
          && (!proxyIp.equals(""))
          && proxyIp.equals(ip)
          && request.getHeader("x-forwarded-for") != null
          ) {
         ip = request.getHeader("x-forwarded-for");
         // System.out.println(" -- ip-forwarded: " + ip);
      }
      if (wrongLoginManager.isIpBanned(ip)) {
         status = "error - ip banned for too much wrong login";
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
         return;
      }
      //-
      //-
      // System.out.println(" user_name: " + user_name);
      //-
      JSONObject app_userj = null;
      boolean doDbAuth = true;
      //-
      //- custom loggers
      String authcontextname = app_system_property.getAsString("auth_custom_context_name");
      if (authcontextname != null
          && !authcontextname.equals("")){
         AuthInterface auth = (AuthInterface)applicationContext.getBean(authcontextname);
         if (auth!=null) {
            doDbAuth = false;
            app_userj = auth.doLogUser(user_name, password);
            if (app_userj == null) {
               doDbAuth = app_system_property.getAsBoolean("auth_custom_fallback_to_db");
            }
         }
      }
      //-
      if (app_userj == null && doDbAuth) {
         //- 
         //- default login procedure
         JSONObject uj = app_user.getWithFields(user_name, "password, password_salt");
         if (uj != null) {
            if (app_user.checkPassword(password
                                       , uj.getString("password")
                                       , uj.getString("password_salt")
                                       )) {
               app_userj = uj;
            }
         }
      }
      if (app_userj == null) {
         //-
         wrongLoginManager.wrongLogin(ip);
      } else {
         app_userj.put("authentication_ip", ip);
         //-
         app_user_login_post_auth(app_userj, session, request, response);
      }
      //-
      response.sendRedirect(app_url);
   }

   //-
   //- shared code with Shibboleth single sign-on
   //-
   public void app_user_login_post_auth(JSONObject app_userj
                                        , HttpSession session
                                        , HttpServletRequest  request
                                        , HttpServletResponse response
                                        ) throws Exception {
      AppPermissionTools_userData userData =
         app_permission
         .getPermissionsForUser(app_userj.getInt("app_user_id"), app_userj, false, request);
      //-
      session.setAttribute("sessionWorkflowCache", null);
      //-
      appSessionTools.setUserData(session, userData);
      //-
      //-
      rememberMe_setCookies(request, response, app_userj);
   }


   /**
    */
   @Transactional
   @RequestMapping(value="app_user/app_user_become_user")
   public void app_user_become_user(HttpSession session
                                    , HttpServletRequest  request
                                    , HttpServletResponse response
                                    , @RequestParam("content") String scontent
                                    ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_admin")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      String user_name = content.getString("user_name");
      JSONObject userj = app_user.get(user_name);
      //-
      if (userj == null) {
         status = "User not found";
      } else {
         status = "ok";
         app_user_login_post_auth(userj, session, request, response);
      }
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   
   @RequestMapping(value="app_user/app_user_logout")
   @Transactional
   public void app_user_logout(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = "ok";
      JSONObject rv = new JSONObject();
      //-
      //- delete all remember_me tokens
      //-
      AppPermissionTools_userData userData = appSessionTools.getUserData(session);
      if (userData != null) {
         int app_user_id = userData.getApp_user_id();
         app_user.deleteUserPropertyWithNameLike(app_user_id, "remember_me_%");
      }
      //-
      //- reset user's session
      //-
      appSessionTools.setUserData(session, null);
      session.setAttribute("sessionWorkflowCache", null);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   @RequestMapping(value="app_user/app_user_getloggeduserinfo")
   public void app_user_getloggeduserinfo(HttpSession session
                                          , HttpServletResponse response
                                          , HttpServletRequest  request
                                          , @RequestParam("content") String scontent
                                          ) throws Exception {
      String status = "ok";
      JSONObject rv = new JSONObject();
      //-
      AppPermissionTools_userData userData =
         appSessionTools.getUserData(session);
      if (userData == null) {
         //-
         //- try the remember_me
         //-
         userData = rememberMe_getUserData(session, request, response);
         //-
         //-
         if (userData == null) {
            //-
            //- log-in as anonymous
            //-
            String user_name = app_system_property.getAsString("login_anonymous_user");
            // System.out.println(" - default user-name: " + user_name);
            JSONObject app_userj = 
               app_user.get(user_name);
            userData =
               app_permission
               .getPermissionsForUser(app_userj.getInt("app_user_id"), app_userj, true, request);
            //-
            //-
         } else {
            //-
            //- app_first_login_note
            //- logged_and_not_anonymous
            //-
         }
         appSessionTools.setUserData(session, userData);
      }
      //-
      userData.update_app_first_login_note(app_system_property, app_user);
      //-
      JSONObject userData_json = userData.getJSONObject();
      rv.put("userData", userData_json );
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   @RequestMapping(value="app_user/app_user_set_app_first_login_note_acceptance")
   public void app_user_set_app_first_login_note_acceptance(HttpSession session
                                                            , HttpServletResponse response
                                                            , HttpServletRequest  request
                                                            , @RequestParam("content") String scontent
                                                            ) throws Exception {
      //-
      JSONObject rv = new JSONObject();
      AppPermissionTools_userData userData =
         appSessionTools.getUserData(session);
      if (userData != null) {
         JSONObject note_data = new JSONObject();
         note_data.put("app_first_login_note_date", (new Date()).getTime());
         //-
         app_user.setUserPropertyAsJSONObject(userData.getApp_user_id()
                                              , "app_first_login_note_data"
                                              , note_data);
         userData.update_app_first_login_note(app_system_property, app_user);
      }
      appControllerTools.appDoRequestMappingResponse(response, "ok", rv) ;
   }


   @RequestMapping(value="app_user/app_user_get_app_first_login_note_acceptance")
   public void app_user_get_app_first_login_note_acceptance(HttpSession session
                                                            , HttpServletResponse response
                                                            , HttpServletRequest  request
                                                            , @RequestParam("content") String scontent
                                                            ) throws Exception {
      //-
      JSONObject rv = new JSONObject();
      AppPermissionTools_userData userData =
         appSessionTools.getUserData(session);
      if (userData != null) {
         //-
         String app_first_login_note = app_system_property.getAsString("app_first_login_note");
         rv.put("app_first_login_note", app_first_login_note);
         //-
         JSONObject note_data =
            app_user.getUserPropertyAsJSONObject(userData.getApp_user_id(), "app_first_login_note_data");
         rv.put("app_first_login_note_data", note_data);
      }
      appControllerTools.appDoRequestMappingResponse(response, "ok", rv) ;
   }

   
   private void rememberMe_setCookies(HttpServletRequest request
                                      , HttpServletResponse response
                                      , JSONObject app_userj
                                      ) throws Exception {
      //- here
      // System.out.println(" -- getRequestURI: " + request.getRequestURI());
      // System.out.println(" -- servlet path: " + request.getServletPath());
      int    app_user_id = app_userj.getInt("app_user_id");
      String user_name   = app_userj.getString("user_name");
      //-
      HashMap<String,Cookie> cookiesMap = appSessionTools.getCookiesMap(request);
      //-
      String application_identifier = app_system_property.getAsString("sys_application_identifier");
      String browser_id_cookieName = application_identifier+"_browser_id";
      String browser_id = null;
      String user_name_cookieName = application_identifier+"_user";
      String user_key_cookieName = application_identifier+"_user_key";
      String user_key = null;
      int cookie_seconds_to_live =
         24 * 60 * 60 * app_system_property.getAsInt("login_remember_me_days_to_live");
      // System.out.println("cookie_seconds_to_live: " + cookie_seconds_to_live);
      Cookie c;
      {
         Cookie browser_idC = cookiesMap.get(browser_id_cookieName);
         if (browser_idC != null) {
            browser_id = browser_idC.getValue();
         }
      }
      if (browser_id == null) {
         browser_id = tools.base64RandomBytes(10);
         c = new Cookie(browser_id_cookieName, browser_id);
         c.setPath("/");
         c.setMaxAge(cookie_seconds_to_live);
         response.addCookie(c);
      }
      {
         user_key = tools.base64RandomBytes(20);
         c = new Cookie(user_key_cookieName, user_key);
         c.setPath("/");
         c.setMaxAge(cookie_seconds_to_live);
         response.addCookie(c);
         c = new Cookie(user_name_cookieName, user_name);
         c.setPath("/");
         c.setMaxAge(cookie_seconds_to_live);
         response.addCookie(c);
         //-
         String property_name = "remember_me_" + browser_id;
         JSONObject property_value = 
            (new JSONObject())
            .put("browser_id", browser_id)
            .put("user_key",user_key)
            .put("date",(new Date()).getTime())
            ;
         // System.out.println(" remember_me new key: " + property_value.toString(2));
         app_user.setUserPropertyAsJSONObject(app_user_id, property_name, property_value);
         // if (1 == 1) {
         //    JSONObject t = new JSONObject();
         //    t.put("browser_id_cookieName", browser_id_cookieName);
         //    t.put("user_name_cookieName", user_name_cookieName);
         //    t.put("user_key_cookieName", user_key_cookieName);
         //    t.put("user_name", user_name);
         //    t.put("property_name", property_name);
         //    t.put("property_value", property_value);
         //    //-
         //    System.out.println(" -- set cookies: " + t.toString(2));
         // }
      }
   }


   private AppPermissionTools_userData rememberMe_getUserData(HttpSession session
                                                              , HttpServletRequest request
                                                              , HttpServletResponse response
                                                              ) throws Exception {
      AppPermissionTools_userData rv = null;
      if (app_system_property.getAsBoolean("login_remember_me_enabled")) {
         // System.out.println(" -- login_remember_me_enabled");
         HashMap<String,Cookie> cookiesMap = appSessionTools.getCookiesMap(request);
         String application_identifier = app_system_property.getAsString("sys_application_identifier");
         String browser_id_cookieName = application_identifier+"_browser_id";
         String user_name_cookieName  = application_identifier+"_user";
         String user_key_cookieName   = application_identifier+"_user_key";
         Cookie browser_id_cookie     = cookiesMap.get(browser_id_cookieName);
         Cookie user_name_cookie      = cookiesMap.get(user_name_cookieName);
         Cookie user_key_cookie       = cookiesMap.get(user_key_cookieName);
         if(browser_id_cookie != null
            && user_name_cookie != null
            && user_key_cookie != null ) {
            //-
            //- the browser may be a "remember_me" browser
            //- claims for a user
            // System.out.println(" -- try a remember_me login...");
            //-
            String browser_id = browser_id_cookie.getValue();
            String user_name  = user_name_cookie.getValue();
            String user_key   = user_key_cookie.getValue();
            //-
            // if (1 == 1) {
            //    JSONObject t = new JSONObject();
            //    t.put("browser_id_cookieName", browser_id_cookieName);
            //    t.put("user_name_cookieName", user_name_cookieName);
            //    t.put("user_key_cookieName", user_key_cookieName);
            //    t.put("browser_id", browser_id);
            //    t.put("user_name", user_name);
            //    t.put("user_key", user_key);
            //    //-
            //    System.out.println(" -- check remember_me: " + t.toString(2));
            // }
            //-
            JSONObject app_userj = app_user.get(user_name);
            if (app_userj != null && app_userj.has("app_user_id")) {
               int app_user_id = app_userj.getInt("app_user_id");
               //-
               //- getting user's saved keys and check for time_to_live
               //-
               int days_to_live = app_system_property.getAsInt("login_remember_me_days_to_live");
               long days_to_live_start_date = (new Date()).getTime();
               days_to_live_start_date = days_to_live_start_date - ( days_to_live * 1000L * 60L * 60L * 24L);
               // System.out.println(" days_to_live_start_date: " + days_to_live_start_date + " " + (new Date(days_to_live_start_date)));
               JSONArray allRememberMeProperties = 
                  app_user.getAllUserPropertyWithNameLike(app_user_id, "remember_me_%");
               HashMap<String,JSONObject> propertiesMap = new HashMap<String,JSONObject>();
               for(int i=0; i<allRememberMeProperties.length(); i++) {
                  JSONObject    jp = allRememberMeProperties.getJSONObject(i);
                  String      name = jp.getString("name");
                  JSONObject value = new JSONObject(jp.getString("value"));
                  long        date = value.getLong("date");
                  // System.out.println(" name:["+name+"]");
                  // System.out.println(" jp: "+ jp.toString(2));
                  // System.out.println(" date:["+new Date(date)+"], days_to_live_start_date ["+new Date(days_to_live_start_date)+"]");
                  if (date <= days_to_live_start_date) {
                     //- 
                     //- too old so I delete it
                     //- 
                     app_user.setUserProperty(app_user_id, name, null);
                     // System.out.println(" -- deleting old remember_me key: " + jp.toString(2));
                  } else {
                     propertiesMap.put(name, value);
                     // System.out.println(" keeping jp: " + name);
                  }
               }
               //-
               String property_name = "remember_me_" + browser_id;
               // System.out.println(" look for " + property_name);
               JSONObject jp = propertiesMap.get(property_name);
               if (jp != null) {
                  //-
                  String user_key_expected = jp.getString("user_key");
                  //-
                  // System.out.println(" -- check for key, user_key_expected: " + user_key_expected);
                  if (user_key_expected.equals(user_key)) {
                     // System.out.println(" -- key ok!");
                     //-
                     //- set-up session information
                     //-
                     rv = app_permission.getPermissionsForUser(app_userj.getInt("app_user_id")
                                                               , app_userj, false, request);
                     rv.setLogged_with_remember_me(true);
                     //-
                     //- resetting cookies with new date and key.
                     //-
                     rememberMe_setCookies(request, response, app_userj);
                     //-
                  }
               } else {
                  // System.out.println(" ... not found!");
               }
            }
         }
      }
      return(rv);
   }


   /**
    *  http://localhost:8080/app_user/app_user_insert
    *  , method=RequestMethod.POST
    */
   @Transactional
   @RequestMapping(value="app_user/app_user_insert")
   public void app_user_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      if (jdbcTemplate
          .queryForObject("select count(*) from app_user where user_name = ?"
                          , Integer.class, content.getString("user_name")) > 0) {
         //-
         status = "user_name already used";
      } else {
         //-
         int          id = app_user.insert(content);
         String password = content.getString("password");
         app_user.setPassword(id, password);
         //-
         app_log.writeLog(app_user, id, appSessionTools.getUser_name(session),content);
         //-
         app_group_role_user.insert(content.getString("user_name"), "root", "user");
         //-
         status = "ok";
         rv.put("app_user_id",  id);
      }
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   /**
     http://localhost:8080/app_user/app_user_list?content={'columns':['user_name'],'order':[{'column':'user_name','desc':'true'}]}
    */
   @RequestMapping(value="app_user/app_user_list")
   public void app_user_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)app_user.getColumns().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_user"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("app_user_id", "id");
      //-
      //-
      AppListTableMapper mapper = qtm.getMapperJson();
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns();
      //-
      //- where
      //-
      {
         qtm.setWhereStringLike("user_name",  "app_user.user_name");
         qtm.setWhereStringLike("first_name",  "app_user.first_name");
         qtm.setWhereStringLike("last_name",  "app_user.last_name");
         qtm.setWhereStringLike("email",  "app_user.email");
         qtm.setWhereDate(      "created",  "app_user.created");
         qtm.setWhereStringLike("nota",  "app_user.nota");
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from app_user");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      rv.put("list",       mapper.getJSON());
      rv.put("qparams",    qparams);
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   @RequestMapping(value="app_user/app_user_logs")
   public void app_user_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("app_user_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , app_user
                       , id
                       );
   }


   /**
    *  http://localhost:8080/app_user/app_user_get?content={"app_user_id", "1"}
    */
   @RequestMapping(value="app_user/app_user_get")
   public void app_user_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_user_id = content.getInt("app_user_id");
      //-
      JSONObject obj = app_user.getWithFields(app_user_id, "ldap_info");
      obj.put("app_user_propertys", app_user.getAllUserPropertyAsJSONObject(app_user_id));
      //-
      rv.put("obj", obj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   /**
    *  http://localhost:8080/app_user/app_user_get?content={"app_user_id", "1"}
    */
   @RequestMapping(value="app_user/app_user_getallrolesandgroups")
   public void app_user_getAllRolesandGroups(HttpSession session
                                             , HttpServletResponse response
                                             , @RequestParam("content") String scontent
                                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      //-
      rv.put("all_roles", app_role.getAllRoles());
      rv.put("all_groups", app_group.getAllGroups());
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   /**
    *  http://localhost:8080/app_user/app_user_update
    */
   @Transactional
   @RequestMapping(value="app_user/app_user_update")
   public void app_user_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_user_id = content.getInt("app_user_id");
      //-
      app_log.writeLog(app_user, app_user_id, appSessionTools.getUser_name(session),content);
      //-
      app_user.update(content);
      //-
      app_user.setAllUserPropertyAsJSONObject(app_user_id, content.getJSONObject("app_user_propertys"));
      //-
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   /**
    *  http://localhost:8080/app_user/app_user_getForPassword?content={"app_user_id", "1"}
    */
   @RequestMapping(value="app_user/app_user_getForPassword")
   public void app_user_getForPassword(HttpSession session
                                       , HttpServletResponse response
                                       , @RequestParam("content") String scontent
                                       ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_user_id = content.getInt("app_user_id");
      rv.put("obj", app_user.get(app_user_id));
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   /**
    *  http://localhost:8080/app_user/app_user_update
    */
   @RequestMapping(value="app_user/app_user_updateForPassword")
   public void app_user_updateForPassword(HttpSession session
                                          , HttpServletResponse response
                                          , @RequestParam("content") String scontent
                                          ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_user_id = content.getInt("app_user_id");
      String password = content.getString("password");
      //-
      app_log.writeLog(app_user, app_user_id, appSessionTools.getUser_name(session),content);
      //-
      app_user.setPassword(app_user_id, password);
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   /**
    *  http://localhost:8080/app_user/app_user_delete
    */
   @RequestMapping(value="app_user/app_user_delete")
   public void app_user_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_user_id = content.getInt("app_user_id");
      app_user.delete(app_user_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   //-
   //-
   //-
   //- permissions
   //-
   //-
   //-

   /**
    *  http://localhost:8080/app_user/app_permission_insert
    */
   @Transactional
   @RequestMapping(value="app_user/app_permission_insert")
   public void app_permission_insert(HttpSession session
                                     , HttpServletResponse response
                                     , @RequestParam("content") String scontent
                                     ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_permission_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int id = app_permission.insert(content);
      app_log.writeLog(app_permission, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("app_permission_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    */
   @RequestMapping(value="app_user/app_permission_list/**")
   public void app_permission_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_permission_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      String              outputtype = qparams.optString("outputtype","json");
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)app_permission.getColumns().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_permission"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("app_permission_id", "id");
      //-
      //-
      AppListTableMapper mapper = null;
      if (outputtype.equals("csv")) {
         mapper = qtm.getMapperCsv(response.getWriter(), appSessionTools.getUserData(session));
         response.setContentType("text/csv");
      } else {
         mapper = qtm.getMapperJson();
      }
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns(app_permission.getComputedColumnsMap(), "app_permission");
      //-
      //- where
      //-
      {
         // qtm.setWhereIntegerEqual("app_permission_super_id",  "app_permission.app_permission_super_id");
         qtm.setWhereStringLike("name",  "app_permission.name");
         qtm.setWhereStringLike("nota",  "app_permission.nota");
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from app_permission");
      // qs.append("\n ,    app_permission_super");
      // qtm.addAnd(" app_permission_super.app_permission_super_id = app_permission.app_permission_super_id");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      if (!outputtype.equals("csv")) {
         rv.put("list",       mapper.getJSON());
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_permission_get?content={"app_permission_id", "1"}
    */
   @RequestMapping(value="app_user/app_permission_get")
   public void app_permission_get(HttpSession session
                                  , HttpServletResponse response
                                  , @RequestParam("content") String scontent
                                  ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_permission_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_permission_id = content.getInt("app_permission_id");
      JSONObject obj = app_permission.get(app_permission_id);
      rv.put("obj",obj);
      // JSONObject app_permission_superj = app_permission_super.get(obj.getInt("app_permission_super_id"));
      // rv.put("app_permission_super",app_permission_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   @Transactional
   @RequestMapping(value="app_user/app_permission_update")
   public void app_permission_update(HttpSession session
                                     , HttpServletResponse response
                                     , @RequestParam("content") String scontent
                                     ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_permission_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_permission_id = content.getInt("app_permission_id");
      //-
      app_log.writeLog(app_permission, app_permission_id, appSessionTools.getUser_name(session),content);
      //-
      app_permission.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_permission_delete
    */
   @Transactional
   @RequestMapping(value="app_user/app_permission_delete")
   public void app_permission_delete(HttpSession session
                                     , HttpServletResponse response
                                     , @RequestParam("content") String scontent
                                     ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_permission_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_permission_id = content.getInt("app_permission_id");
      app_permission.delete(app_permission_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_permission_logs
    */
   @RequestMapping(value="app_user/app_permission_logs")
   public void app_permission_logs(HttpSession session
                                   , HttpServletResponse response
                                   , @RequestParam("content") String sqparams
                                   ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_permission_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("app_permission_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , app_permission
                       , id
                       );
   }


   //-
   //- roles
   //-

   /**
    *  http://localhost:8080/app_user/app_role_insert
    */
   @Transactional
   @RequestMapping(value="app_user/app_role_insert")
   public void app_role_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_role_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int id = app_role.insert(content);
      app_log.writeLog(app_role, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("app_role_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    */
   @RequestMapping(value="app_user/app_role_list/**")
   public void app_role_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_role_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      String              outputtype = qparams.optString("outputtype","json");
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)app_role.getColumns().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_role"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("app_role_id", "id");
      //-
      //-
      AppListTableMapper mapper = null;
      if (outputtype.equals("csv")) {
         mapper = qtm.getMapperCsv(response.getWriter(), appSessionTools.getUserData(session));
         response.setContentType("text/csv");
      } else {
         mapper = qtm.getMapperJson();
      }
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns(app_role.getComputedColumnsMap(), "app_role");
      //-
      //- where
      //-
      {
         // qtm.setWhereIntegerEqual("app_role_super_id",  "app_role.app_role_super_id");
         qtm.setWhereStringLike("name",  "app_role.name");
         qtm.setWhereStringLike("nota",  "app_role.nota");
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from app_role");
      // qs.append("\n ,    app_role_super");
      // qtm.addAnd(" app_role_super.app_role_super_id = app_role.app_role_super_id");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      if (!outputtype.equals("csv")) {
         rv.put("list",       mapper.getJSON());
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_role_get?content={"app_role_id", "1"}
    */
   @RequestMapping(value="app_user/app_role_get")
   public void app_role_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_role_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_role_id = content.getInt("app_role_id");
      JSONObject obj = app_role.get(app_role_id);
      rv.put("obj",obj);
      // JSONObject app_role_superj = app_role_super.get(obj.getInt("app_role_super_id"));
      // rv.put("app_role_super",app_role_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   /**
    *  http://localhost:8080/app_user/app_role_get?content={"app_role_id", "1"}
    */
   @RequestMapping(value="app_user/app_role_get_with_permissions")
   public void app_role_get_with_permissions(HttpSession session
                                             , HttpServletResponse response
                                             , @RequestParam("content") String scontent
                                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_role_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_role_id = content.getInt("app_role_id");
      JSONObject obj = app_role.get(app_role_id);
      rv.put("obj",obj);
      rv.put("permission_for_role",
             app_role_permission.getPermissionForRole_sortablecheckboxes(app_role_id));
      //-
      // JSONObject app_role_superj = app_role_super.get(obj.getInt("app_role_super_id"));
      // rv.put("app_role_super",app_role_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   //-
   @Transactional
   @RequestMapping(value="app_user/app_role_update")
   public void app_role_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_role_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_role_id = content.getInt("app_role_id");
      //-
      app_log.writeLog(app_role, app_role_id, appSessionTools.getUser_name(session),content);
      //-
      app_role.update(content);
      app_role_permission.setPermissionForRole(app_role_id, content.getJSONArray("permissions"));
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_role_delete
    */
   @Transactional
   @RequestMapping(value="app_user/app_role_delete")
   public void app_role_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_role_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_role_id = content.getInt("app_role_id");
      app_role.delete(app_role_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_role_logs
    */
   @RequestMapping(value="app_user/app_role_logs")
   public void app_role_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_role_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("app_role_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , app_role
                       , id
                       );
   }
   
   
   //-
   //- groups
   //-
   /**
    *  http://localhost:8080/app_user/app_group_insert
    *  , method=RequestMethod.POST
    */
   @RequestMapping(value="app_user/app_group_insert")
   public void app_group_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int id = app_group.insert(content);
      app_log.writeLog(app_group, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("app_group_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-



   /**
     http://localhost:8080/app_user/app_group_list?content={'columns':['name'],'order':[{'column':'name','desc':'true'}]}
    */
   @RequestMapping(value="app_user/app_group_list")
   public void app_group_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)app_group.getColumns().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_group"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("app_group_id", "id");
      //-
      //-
      AppListTableMapper mapper = qtm.getMapperJson();
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns();
      //-
      //- where
      //-
      {
         qtm.setWhereStringLike("name",  "app_group.name");
         qtm.setWhereStringLike("nota",  "app_group.nota");
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from app_group");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      rv.put("list",       mapper.getJSON());
      rv.put("qparams",    qparams);
      //-
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   //-
   /**
    *  http://localhost:8080/app_user/app_group_get?content={"app_group_id", "1"}
    */
   @RequestMapping(value="app_user/app_group_get")
   public void app_group_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_group_id = content.getInt("app_group_id");
      rv.put("obj", app_group.get(app_group_id));
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_group_update
    */
   @RequestMapping(value="app_user/app_group_update")
   public void app_group_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_group_id = content.getInt("app_group_id");
      //-
      app_log.writeLog(app_group, app_group_id, appSessionTools.getUser_name(session),content);
      //-
      app_group.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_group_delete
    */
   @RequestMapping(value="app_user/app_group_delete")
   public void app_group_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_group_id = content.getInt("app_group_id");
      app_group.delete(app_group_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/app_user/app_group_logs
    */
   @RequestMapping(value="app_user/app_group_logs")
   public void app_group_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("app_group_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , app_group
                       , id
                       );
   }

   /**
    */
   @RequestMapping(value="app_user/app_user_property_set_table_mattoni_defaults")
   public void app_user_property_set_table_mattoni_defaults(HttpSession session
                                                              , HttpServletResponse response
                                                              , @RequestParam("content") String scontent
                                                              ) throws Exception {
      String status = "ok";
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_table_mattoni_save")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // User.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_user_id = appSessionTools.getUserData(session).getApp_user_id();
      String      tableid    = content.getString("tableid");
      JSONObject  conf       = content.getJSONObject("conf");
      String propertyName = "table_mattoni__"+tableid;
      //-
      // System.out.println(" conf: " + conf);
      //-
      app_user.setUserPropertyAsJSONObject(app_user_id, propertyName, conf);
      //-
      status = "ok";
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   /**
    *  http://localhost:8080/app_user/app_group_role_user_insert
    */
   @Transactional
   @RequestMapping(value="app_user/app_group_role_user_insert")
   public void app_group_role_user_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_role_user_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int id = app_group_role_user.insert(content);
      //-qui-
      // app_log.writeLog(app_group_role_user, id, appSessionTools.getUser_name(session));
      status = "ok";
      rv.put("app_group_role_user_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


   //-
   /**
    */
   @RequestMapping(value="app_user/app_group_role_user_list/**")
   public void app_group_role_user_list(HttpSession session
                                        , HttpServletResponse response
                                        , @RequestParam("content") String sqparams
                                        ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_role_user_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      String              outputtype = qparams.optString("outputtype","json");
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)app_group_role_user.getColumnsMattoni().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.app_group_role_user"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("app_group_role_user_id", "id");
      qtm.setCustomHeaderAndAddColumn("app_group_id", "id");
      qtm.setCustomHeaderAndAddColumn("app_role_id", "id");
      qtm.setCustomHeaderAndAddColumn("app_user_id", "id");
      //-
      //-
      AppListTableMapper mapper = null;
      if (outputtype.equals("csv")) {
         mapper = qtm.getMapperCsv(response.getWriter(), appSessionTools.getUserData(session));
         response.setContentType("text/csv");
      } else {
         mapper = qtm.getMapperJson();
      }
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns(app_group_role_user.getComputedColumnsMap(), "app_group_role_user");
      //-
      //- where
      //-
      {
         qtm.setWhereIntegerEqual("app_group_id",  "app_group_role_user.app_group_id");
         qtm.setWhereIntegerEqual("app_role_id",  "app_group_role_user.app_role_id");
         qtm.setWhereIntegerEqual("app_user_id",  "app_group_role_user.app_user_id");
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from app_group_role_user");
      //-
      qs.append("\n ,    app_group");
      qs.append("\n ,    app_role");
      qs.append("\n ,    app_user");
      //-
      qtm.addAnd(" app_group.app_group_id = app_group_role_user.app_group_id");
      qtm.addAnd(" app_role.app_role_id  = app_group_role_user.app_role_id");
      qtm.addAnd(" app_user.app_user_id  = app_group_role_user.app_user_id");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      if (!outputtype.equals("csv")) {
         rv.put("list",       mapper.getJSON());
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   
   /**
    *  http://localhost:8080/app_user/app_group_role_user_delete
    */
   @Transactional
   @RequestMapping(value="app_user/app_group_role_user_delete")
   public void app_group_role_user_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_group_role_user_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_group_role_user_id = content.getInt("app_group_role_user_id");
      app_group_role_user.delete(app_group_role_user_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }


      /**
   @RequestMapping(value="app_user/app_user_delete_permission")
   public void app_user_delete_permission(HttpSession session
                                          , HttpServletResponse response
                                          , @RequestParam("content") String scontent
                                          ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_app_user_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int app_user_id = content.getInt("app_user_id");
      int app_role_id = content.getInt("app_role_id");
      int app_group_id = content.getInt("app_group_id");
      //-
      app_log.writeLog(app_user, app_user_id, appSessionTools.getUser_name(session));
      //-
      app_group_role_user.delete(app_user_id, app_group_id, app_role_id);
      //-
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
    */

   /**
    */
   @RequestMapping(value="app_user/app_get_system_property_app_about_custom")
   public void app_get_system_property_app_about_custom(HttpSession session
                                                        , HttpServletResponse response
                                                        , @RequestParam("content") String scontent
                                                        ) throws Exception {
      JSONObject rv = new JSONObject();
      //-
      rv.put("app_about_custom", app_system_property.getAsString("app_about_custom"));
      //-
      appControllerTools.appDoRequestMappingResponse(response, "ok", rv) ;
   }

   
}

