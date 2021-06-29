package it.unimo.app.tools;

// @author Alberto Corni [Al 20140605-19:22]
 
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Locale;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.json.JSONObject;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;

import it.unimo.app.om.App_system_property;
import it.unimo.app.om.App_user;

public class AppPermissionTools_userData {

   public void setApp_user(JSONObject app_user_json, boolean default_user
                           , Tools tools
                           , HttpServletRequest httpServletRequest
                           ) throws Exception {
      _app_user_json = app_user_json;
      _user_name   = app_user_json.getString("user_name");
      _user_first_name  = app_user_json.getString("first_name");
      _user_last_name   = app_user_json.getString("last_name");
      _app_user_id = app_user_json.getInt("app_user_id");
      _default_user = default_user;
      _user_language = "it";
      {
         String language = httpServletRequest.getHeader("Accept-Language");
         if (language != null){
            _user_language = language;
         }
         Locale locale = httpServletRequest.getLocale();
         if (locale != null) {
            _locale = locale;
            // System.out.println(" ----- set locale to: " + _locale);
         }
      }
      //-
      String languageFile = "language/language_it.json";
      if (_resource_bundle == null) {
         //- load or create dynamically....
         //- in this first implementation I load it from file
         try {
            _resource_bundle = 
               new JSONObject(tools.string_loadFromResources(languageFile)
                              // removing comments
                              .replaceAll("(?m)^[ \t]*//.*\n", "")
                              );
         } catch (org.json.JSONException e) {
            String m = "Error parsing language file ["+languageFile+"]";
            System.out.println(" ######### error #########\n" + m + "\n" + e.getMessage());
            throw new Exception(m, e);
         }
      }
   }

   public void update_app_first_login_note(App_system_property app_system_property
                                           , App_user app_user
                                           ) throws Exception {
      _app_first_login_note = null;
      // System.out.println(" -- _user_name: " + _user_name);
      // System.out.println(" -- login_anonymous_user: " + app_system_property.getAsString("login_anonymous_user"));
      if (!_user_name.equals(app_system_property.getAsString("login_anonymous_user"))) {
         // System.out.println(" -- logged_and_not_anonymous");
         String app_first_login_note = app_system_property.getAsString("app_first_login_note");
         // System.out.println(" -- app_first_login_note: " + app_first_login_note);
         if (app_first_login_note != null
             && !app_first_login_note.equals("")) {
            JSONObject note_data = 
               app_user.getUserPropertyAsJSONObject(_app_user_id,
                                                    "app_first_login_note_data");
            // System.out.println(" -- note_data: " + note_data);
            if (note_data == null
                || note_data.get("app_first_login_note_date") == null) {
               _app_first_login_note = app_first_login_note;
               // System.out.println(" -- adding to userData_json");
            }
         }
      }
   }

   public void addPermission(String groupName, String permissionName) {
      HashSet<String> hp = _group_permissions.get(groupName);
      if (hp == null) {
         hp = new HashSet<String>();
         _group_permissions.put(groupName, hp);
         allGroupNamesAddGroup(groupName);
      }
      hp.add(permissionName);
   }

   public Locale getLocale() {
      if (_locale == null) {
         // System.out.println(" ------------- getting locale for lang: " + _user_language);
         _locale = new Locale(_user_language.substring(0,2));
      }
      return(_locale);
   }

   public boolean hasPermission(String groupName, String permissionName) {
      boolean rv = false;
      HashSet<String> hp = _group_permissions.get(groupName);
      if (hp != null) {
         rv = hp.contains(permissionName);
      }
      return(rv);
   }


   private JSONArray _allGroupNames = null;
   public void setAllGroupNames(JSONArray a) {
      _allGroupNames = a;
   }
   public void allGroupNamesAddGroup(String groupName) {
      if(_allGroupNames == null){
         _allGroupNames = new JSONArray();
      }
      boolean doAdd = true;
      for (int i=0; doAdd  && i<_allGroupNames.length();i++){
         if (groupName.equals(_allGroupNames.getString(i))) {
            doAdd = false;
         }
      }
      if (doAdd) {
         _allGroupNames.put(groupName);
      }
   }
   /**
    * Used to retrieve the groups the user can associate Objects to */
   public JSONArray groupsHavingThePermission(String permissionName) {
      JSONArray rv = new JSONArray();
      if (hasPermission("root", permissionName)) {
         rv = _allGroupNames;
      } else {
         // private HashMap<String, HashSet<String>> _group_permissions = 
         // System.out.println(" _group_permissions.keySet(): " + _group_permissions.keySet());
         // String groups[] = (String[])_group_permissions.keySet().toArray();
         String groups[] = new String[_group_permissions.keySet().size()];
         //- -qui- optimize me. [Al 20210128-15:37]
         //-   si potrebbe creare prima l'array filtrato, ordinarlo e poi fare il push?
         //-      usare     groups = ArrayList<String>
         //-                if has permession:   grups.add(g)
         //-     Collections.sort(groups);
         //-     for (String g: groups) ...put()
         //-
         int i = 0;
         for (Object o: _group_permissions.keySet()){
            groups[i++]=(String)o;
         }
         Arrays.sort(groups);
         for(String group: groups) {
            if (hasPermission(group, permissionName)) {
               rv.put(group);
            }
         }
      }
      return(rv);
   }
   
   public void setPermissionForGroup(String groupName, HashSet<String> hp) {
      _group_permissions.put(groupName, hp);
   }

   public JSONObject getJSONObject() {
      JSONObject rv = new JSONObject();
      rv.put("user_name", _user_name);
      rv.put("user_first_name", _user_first_name);
      rv.put("user_last_name",  _user_last_name);
      rv.put("user_language", _user_language);
      rv.put("default_user", _default_user);
      rv.put("app_user_id", _app_user_id);
      rv.put("remember_me_enabled", _remember_me_enabled);
      rv.put("logged_with_remember_me", _logged_with_remember_me);
      rv.put("authentication_ip", _app_user_json.optString("authentication_ip","null"));
      rv.put("auth_single_sign_on_protected_url", _auth_single_sign_on_protected_url);
      rv.put("app_first_login_note", _app_first_login_note);
      JSONObject permsj = new JSONObject();
      {
         for(String group: (new TreeSet<String>(_group_permissions.keySet()))) {
            JSONArray hpj = new JSONArray();
            for(String permission: (new TreeSet<String>(_group_permissions.get(group)))) {
               hpj.put(permission);
            }
            permsj.put(group, hpj);
         }
      }
      rv.put("permissions", permsj);
      rv.put("resource_bundle", getResourceBundle());
      //-
      return(rv);
   }

   public boolean getLogged_with_remember_me() {
      return _logged_with_remember_me;
   }
   public void setLogged_with_remember_me(boolean value) {
      _logged_with_remember_me = value;
   }
   public boolean getRemember_me_enabled() {
      return _remember_me_enabled;
   }
   public void setRemember_me_enabled(boolean value) {
      _remember_me_enabled = value;
   }
   public String getUser_name() {
      return _user_name;
   }
   public int getApp_user_id() {
      return _app_user_id;
   }
   public String getAuth_single_sign_on_protected_url() {
      return(_auth_single_sign_on_protected_url);
   }
   public void setAuth_single_sign_on_protected_url(String v) {
      _auth_single_sign_on_protected_url = v;
   }

   public String gRb(String label) {
      String rv = "-";
      if (_resource_bundle != null) {
         if (_resource_bundle.has(label)) {
            rv = _resource_bundle.getString(label);
         }
      }
      return(rv);
   }

   public JSONObject getResourceBundle() {
      return(_resource_bundle);
   }

   public DecimalFormat getDecimalFormat() {
      if (_decimalFormat == null) {
         _decimalFormat = 
            new DecimalFormat("#.#####", new DecimalFormatSymbols( getLocale() ));
         // System.out.println(" --------------------- locale: " + getLocale());
      }
      return(_decimalFormat);
   }

   //-
   //- session data lost on logout
   //-
   public Object getSessionCustomData(String name) {
      return _session_custom_data.get(name);
   }
   public void setSessionCustomData(String name, Object value) {
      _session_custom_data.put(name, value);
   }
   
   private HashMap<String, HashSet<String>> _group_permissions = 
      new HashMap<String, HashSet<String>>();

   private String _user_name      = null;
   private String _user_first_name     = null;
   private String _user_last_name      = null;
   private Locale _locale = null;
   private String _user_language  = null;
   private boolean _default_user   = false;
   private boolean _remember_me_enabled   = false;
   private boolean _logged_with_remember_me = false;
   private int    _app_user_id = -1;
   private String _auth_single_sign_on_protected_url = null;
   private JSONObject _app_user_json = null;
   private JSONObject _resource_bundle = null;
   private DecimalFormat _decimalFormat = null;
   private String _app_first_login_note = null;
   private HashMap<String, Object> _session_custom_data = new HashMap<String, Object>();
}
