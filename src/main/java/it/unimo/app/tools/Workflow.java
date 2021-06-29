package it.unimo.app.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimo.app.Application;
import it.unimo.app.tools.AppSessionTools;

import org.springframework.context.ApplicationContext;
import java.lang.reflect.Method;

// import org.springframework.beans.factory.annotation.Autowired;

public class Workflow {
   /**
      gestione dei "per_field_permisson", ovvero
      quali campi posso
        leggere 
        scrivere
      I tre livelli sono
       hide
       ro
       rw
      il livello di defaulto e'
       rw
      ovvero per "hide" un capo devo esplicitamente dichiararlo.
      - L'oggetto
         "default_per_field_permisson"
        definisce le proprieta' di default.
        Dove
         "default": "ro"
        abbassa il default a "ro", per
        se e' presente un 
         "rw": "permission name"
        allora l'utente lo puo' modificare solo se ha la permission.
        Analogamente
         "default": "hide"
        abbassa il default a "hide"
        possono essere presenti le permission
         "ro": "ro_permission name"
         "rw": "rw_permission name"
    */
   //get log4j handler
   private static final Logger logger = LoggerFactory.getLogger(Workflow.class);
   
   private boolean _useSession  = false;
   private HttpSession _session = null;
   private AppSessionTools _appSessionTools = null;
   private Set _permissionsSet = null;

   public Workflow(JSONObject wfjson
                   , AppSessionTools appSessionTools
                   , HttpSession session) throws Exception {
      _session = session;
      _appSessionTools = appSessionTools;
      _useSession = true;
      init(wfjson);
      _session = null;
      _appSessionTools = null;
   }

   public Workflow(JSONObject wfjson
                   , Set permissionsSet) throws Exception {
      _useSession = false;
      _permissionsSet = permissionsSet;
      init(wfjson);
   }

   private boolean userHasPermission(String permissionName) throws Exception {
      boolean rv = false;
      if (_useSession) {
         rv = (_appSessionTools.hasPermission(_session, permissionName) == null);
      } else {
         rv = _permissionsSet.contains(permissionName);
      }
      return(rv);
   }
   
   public void init(JSONObject wfjson) throws Exception {
      _wfjson = wfjson;
      _statusList   = new Vector<String>();
      //-
      _statusIndex  = new JSONObject();
      //-
      //-
      //-
      //-
      if (!_wfjson.has("statuses")) {
         String m = "wokflow array of 'statuses' is required";
         throw (new Exception(m));
      }
      JSONArray   statuses = _wfjson.getJSONArray("statuses");
      //-
      //- compiling and indexing
      //-
      //-
      if (!_wfjson.has("defaultStatus")) {
         String m = "the 'defaultStatus' is required";
         throw (new Exception(m));
      }
      _defaultStatus         = _wfjson.getString("defaultStatus");
      _defaultSearchStatuses = _wfjson.optJSONArray("defaultSearchStatuses");
      //-
      //-
      //- first step
      //-   getting all the statuses
      //-
      //-
      for (int i=0; i<statuses.length();i++){
         JSONObject si = statuses.getJSONObject(i);
         JSONObject so = new JSONObject();
         if (!si.has("name")) {
            String m = "the 'name' for the status is required";
            throw (new Exception(m));
         }
         String name = si.getString("name");
         if (_statusList.contains(name)) {
            String m = "the status '"+name+"' is defined twice";
            throw (new Exception(m));
         }
         so.put("name", name);
         if (!si.has("color")) {
            String m = "the 'color' for the status ["+name+"] is required";
            throw (new Exception(m));
         }
         so.put("color", si.getString("color"));
         if (!si.has("description")) {
            String m = "the 'description' for the status ["+name+"] is required";
            throw (new Exception(m));
         }
         so.put("description", si.getString("description"));
         //-
         _statusList.add(name);
         _statusIndex.put(name, so);
         // System.out.println(" --- " + name);
      }
      //-
      //-
      //-
      //- step.2 - processing default_per_field_permisson
      //-
      //-
      //-
      Vector<String> _fieldList = new Vector<String>();
      //-
      //-  defaultPermissions<"attributeName", "permi">
      //-  e.g.:
      //-   defaultPermissions<"fielda", "ro">
      //-   defaultPermissions<"fielda", "rw">
      //-
      HashMap<String, String> defaultPermissions = new HashMap<String, String>();
      //-
      //-  defaultPermissions_status<"status"<"attributeName", "permi">>
      //-  e.g.:
      //-   defaultPermissions_status<"statusa"<"fielda", "ro">>
      //-   defaultPermissions_status<"statusa"<"fieldb", "rw">>
      HashMap<String, HashMap<String, String>> defaultPermissions_status =
         new HashMap<String, HashMap<String, String>>();
      //-
      //- per_field_permisson
      //- 
      if (wfjson.has("default_per_field_permisson")) {
         JSONObject default_per_field_permisson =
            wfjson.getJSONObject("default_per_field_permisson");
         Iterator keyi = default_per_field_permisson.keys();
         while (keyi.hasNext()) {
            Object     keyo = keyi.next();
            String     fieldName       = (String)keyo;
            JSONObject fieldPermission = default_per_field_permisson.getJSONObject(fieldName);
            //-
            defaultPermissions.put(fieldName,init_get_field_permisson(fieldPermission));
            if (!_fieldList.contains(fieldName)) {
               _fieldList.add(fieldName);
            }
         }
      }
      //-
      //-
      //- step - processing
      //-   -  allowed transitions
      //-   -  status per_field_permisson
      //-
      //-
      for (int i=0; i<_statusList.size();i++){
         JSONObject si = statuses.getJSONObject(i);
         String name = si.getString("name");
         JSONObject so = _statusIndex.getJSONObject(name);
         // System.out.println(" ---steps--- " + so.getString("name"));
         //-
         if (!si.has("steps")) {
            String m = "the 'steps' array for the status ["+name+"] is required";
            throw (new Exception(m));
         }
         JSONArray  stepsi   = si.optJSONArray("steps");
         JSONArray  stepso   = new JSONArray();
         JSONObject stepsidx = new JSONObject();
         //-
         JSONObject actionsIndex = new JSONObject();
         so.put("actionsIndex", actionsIndex);
         //-
         for (int is=0; is<stepsi.length();is++){
            JSONObject trani = null;
            try {
               trani = stepsi.getJSONObject(is);
            } catch (Exception e) {
               String m = ""
                  + "Error getting the JSONObject defining a step (transaction definition)"
                  + " in status ["+name+"]"
                  + ", please check the position of the commas"
                  + "\n" + si.toString(2)
                  ;
               throw (new Exception(m, e));
            }
            JSONObject trano = new JSONObject();
            String destination = trani.getString("destination");
            if (!_statusIndex.has(destination)) {
               String m = ""
                  + "Unknown destination status ["+destination+"]"
                  + " in status ["+name+"]"
                  ;
               throw (new Exception(m));
            }
            {
               JSONArray actions;
               if (trani.has("actions")) {
                  actions = trani.getJSONArray("actions");
               } else {
                  actions = new JSONArray();
               }
               actionsIndex.put(destination,actions);
            }
            
            String permission = trani.getString("permission");
            trano.put("permission",  permission);
            if (userHasPermission(permission)) {
               trano.put("destination", destination);
               trano.put("description", trani.getString("description"));
               stepso.put(destination);
               stepsidx.put(destination, trano);
            }
         }
         so.put("steps", stepso);
         so.put("stepsIndex", stepsidx);
         //-
         //- per_field_permisson
         //- 
         if (si.has("per_field_permisson")) {
            JSONObject per_field_permisson =
               si.getJSONObject("per_field_permisson");
            HashMap<String, String> innerHash = new HashMap<String, String>();
            defaultPermissions_status.put(name, innerHash);
            //-
            Iterator keyi = per_field_permisson.keys();
            while (keyi.hasNext()) {
               Object     keyo = keyi.next();
               String     fieldName       = (String)keyo;
               JSONObject fieldPermission = per_field_permisson.getJSONObject(fieldName);
               //-
               innerHash.put(fieldName,init_get_field_permisson(fieldPermission));
               if (!_fieldList.contains(fieldName)) {
                  _fieldList.add(fieldName);
               }
            }
         }
      }
      //- 
      //- post-processing per_field permissions
      //-
      _per_field_permission_canRead  = new HashMap<String, HashMap<String, Boolean>>();
      _per_field_permission_canWrite = new HashMap<String, HashMap<String, Boolean>>();
      //-
      {
         _jsonFieldList = new JSONArray();
         for(String fieldName: _fieldList) {
            _jsonFieldList.put(fieldName);
         }         
      }
      for(String statusName: _statusList) {
         HashMap<String, Boolean> inner_canRead = new HashMap<String, Boolean>();
         _per_field_permission_canRead.put(statusName, inner_canRead);
         HashMap<String, Boolean> inner_canWrite = new HashMap<String, Boolean>();
         _per_field_permission_canWrite.put(statusName, inner_canWrite);
         //-
         JSONObject so = _statusIndex.getJSONObject(statusName);
         JSONObject so_inner_canRead = new JSONObject();
         so.put("per_field_permission_canRead", so_inner_canRead);
         JSONObject so_inner_canWrite = new JSONObject();
         so.put("per_field_permission_canWrite", so_inner_canWrite);
         //-
         HashMap<String, String> defaultPermissions_status_inner =
            defaultPermissions_status.get(statusName);
         //-
         for(String fieldName: _fieldList) {
            //-
            String permission = null;
            if (defaultPermissions_status_inner != null) {
               permission = defaultPermissions_status_inner.get(fieldName);
            }
            if (permission == null) {
               permission = defaultPermissions.get(fieldName);
            }
            if (permission == null) {
               permission = _defaultPermission;
            }
            boolean canRead=true;
            boolean canWrite=true;
            if ("hidden".equals(permission)) {
               canRead=false;
               canWrite=false;
            } else if ("ro".equals(permission)) {
               canRead=true;
               canWrite=false;
            } else {
               canRead=true;
               canWrite=true;
            }
            inner_canRead.put(fieldName,  Boolean.valueOf(canRead));
            inner_canWrite.put(fieldName, Boolean.valueOf(canWrite));
            so_inner_canRead.put(fieldName, canRead);
            so_inner_canWrite.put(fieldName, canWrite);
         }
      }
      //-
      if (_wfjson.has("aux")) {
         _auxData = _wfjson.getJSONObject("aux");
      }
   }
   
   private HashMap<String, HashMap<String, Boolean>> _per_field_permission_canRead  = null;
   private HashMap<String, HashMap<String, Boolean>> _per_field_permission_canWrite = null;
   private String _defaultPermission = "rw";

   private String init_get_field_permisson(JSONObject fieldPermission
                                           ) throws Exception {
      String defaultValue    = fieldPermission.optString("default",_defaultPermission);
      String rvvalue = defaultValue;
      if ("rw".equals(defaultValue)) {
         //- ok
      } else if ("ro".equals(defaultValue)) {
         String permission = fieldPermission.optString("rw", null);
         if (permission != null && userHasPermission(permission)) {
            rvvalue = "rw";
         }
      } else if ("hidden".equals(defaultValue)) {
         String permission = fieldPermission.optString("rw", null);
         if (permission != null && userHasPermission(permission)) {
            rvvalue = "rw";
         } else {
            permission = fieldPermission.optString("ro", null);
            if (permission != null && userHasPermission(permission)) {
               rvvalue = "ro";
            }
         }
      }
      return(rvvalue);
   }

   private JSONArray _jsonFieldList = null;
   private JSONObject _auxData = null;
   public JSONObject getAuxData() {
      return(_auxData);
   }
   
   public JSONObject toJson() throws Exception {
      JSONObject rv = new JSONObject();
      rv.put("statusList", _statusList);
      rv.put("fieldList", _jsonFieldList);
      rv.put("statusIndex", _statusIndex);
      rv.put("defaultStatus", _defaultStatus);
      rv.put("defaultSearchStatuses", _defaultSearchStatuses);
      if (_auxData != null) {
         rv.put("aux", _auxData);
      }
      // if (session != null) {
      //    //- per permission informations
      //    JSONObject default_per_field_permisson = new JSONObject();
      //    Iterator<String> _per_field_isReadOnly_i = _per_field_isReadOnly.keySet().iterator();
      //    while (_per_field_isReadOnly_i.hasNext()) {
      //       String fieldName = _per_field_isReadOnly_i.next();
      //       //-
      //       //- it must return one of the states: rw, ro, hidden
      //       //-
      //       String perm = "ro";
      //       if (per_field_isHidden(fieldName)) {
      //          perm = "hidden";
      //          String permRw = per_field_getWritePermission(fieldName);
      //          if ((permRw != null) && (userHasPermission(permRw) == null)) {
      //             perm = "rw";
      //          } else {
      //             String permRo = per_field_getReadOnlyPermission(fieldName);
      //             if ((permRo != null) && (userHasPermission(permRo) == null)) {
      //                perm = "ro";
      //             }
      //          }
      //       } else {
      //          String permRw = per_field_getWritePermission(fieldName);
      //          if ((permRw != null) && (appSessionTools.hasPermission(session, permRw)==null)) {
      //             perm = "rw";
      //          }
      //       }
      //       default_per_field_permisson.put(fieldName, perm);
      //    }
      //    rv.put("default_per_field_permisson",default_per_field_permisson);
      // }
      return(rv);
   }

   private String[]   _statusListAsArray = null;
   public String[] getStatusListAsArray(){
      if (_statusListAsArray == null) {
         _statusListAsArray = new String[_statusList.size()];
         for (int i=0; i<_statusList.size();i++){
            _statusListAsArray[i] = _statusList.get(i);
         }
      }
      return(_statusListAsArray);
   }

   public String getDefaultStatus() {
      return(_defaultStatus);
   }
   
   public String getPermission(String stato_corrente, String stato_nuovo) {
      String rv = null;
      if (stato_corrente == null
          || !_statusIndex.has(stato_corrente)
          ) {
         stato_corrente = _defaultStatus;
      }
      if (!stato_corrente.equals(stato_nuovo)) {
         rv = _statusIndex.getJSONObject(stato_corrente)
            .getJSONObject("stepsIndex")
            .getJSONObject(stato_nuovo)
            .getString("permission")
            ;
      }
      return(rv);
   }

   public boolean per_field_user_canRead(String currentStatus
                                         , String fieldName
                                         ) throws Exception {
      boolean rv = true;
      HashMap<String, Boolean> hs = _per_field_permission_canRead.get(currentStatus);
      if (hs == null) {
         String m = ""
            + "status name ["+currentStatus+"] not found"
            ;
         throw (new Exception(m));
      } else {
         Boolean hf = hs.get(fieldName);
         if (hf != null) {
            rv = hf.booleanValue();
         }
      }
      return(rv);
   }
   public boolean per_field_user_canWrite(String currentStatus
                                          , String fieldName
                                         ) throws Exception {
      boolean rv = true;
      HashMap<String, Boolean> hs = _per_field_permission_canWrite.get(currentStatus);
      if (hs == null) {
         String m = ""
            + "status name ["+currentStatus+"] not found"
            ;
         throw (new Exception(m));
      } else {
         Boolean hf = hs.get(fieldName);
         if (hf != null) {
            rv = hf.booleanValue();
         }
      }
      return(rv);
   }
   
   public void per_field_restoreUnwritable(String oldStatus
                                           , JSONObject objnew
                                           , JSONObject objold
                                           ) throws Exception {
      Iterator keylist = objold.keys();
      while (keylist.hasNext()) {
         Object keyo = keylist.next();
         String key = (String)keyo;
         // System.out.println(" --- key: " + key);
         if (!per_field_user_canWrite(oldStatus, key)) {
            objnew.put(key, objold.get(key));
            // System.out.println(" -- restoring ["+key+"]");
         }
      }
   }

   public void per_field_hideUnreadable(String status
                                        , JSONObject obj
                                        ) throws Exception {
      ArrayList<String> keyList = new ArrayList<String>();
      Iterator keyIterator = obj.keys();
      while (keyIterator.hasNext())
         keyList.add((String)keyIterator.next());
      for(String key: keyList) {
         // System.out.println(" --- key: " + key);
         if (!per_field_user_canRead(status, key)) {
            obj.put(key, (JSONObject)null);
            // System.out.println(" -- hiding ["+key+"]");
         }
      }
   }

   public boolean run_actions(String stato_corrente
                              , String stato_nuovo
                              , JSONObject obj
                              ) throws Exception {
      boolean rv = true;
      if (!stato_corrente.equals(stato_nuovo)) {
         // System.out.println(" -- hello from run_actions!");
         if (!_statusIndex.has(stato_corrente)) {
            String m = "workflow stato_corrente not found '"+stato_corrente+"'";
            throw (new Exception(m));
         }
         if (!_statusIndex.has(stato_nuovo)) {
            String m = "workflow stato_nuovo not found '"+stato_nuovo+"'";
            throw (new Exception(m));
         }
         JSONObject stato  = _statusIndex.getJSONObject(stato_corrente);
         // System.out.println(" --- stato:\n" + stato.toString(2));
         //-      
         JSONArray actions =
            stato.getJSONObject("actionsIndex")
            .getJSONArray(stato_nuovo);
         for (int i=0; i<actions.length();i++){
            String action = actions.getString(i);
            // System.out.println(" ---- action:  " + action);
            String parts[] = action.split(":");
            String bean_name   = parts[0];
            String method_name = parts[1];
            ApplicationContext context = Application.getStaticContext();
            Object existing_bean = context.getBean(bean_name);
            Method method =  existing_bean.getClass()
               .getMethod(method_name
                          , Workflow.class
                          , String.class
                          , String.class
                          , JSONObject.class);
            Object o = method.invoke(existing_bean, this, stato_corrente, stato_nuovo, obj);
         }
         // System.out.println(" -- bye from run_actions!");
      }
      return(rv);
   }

   
   public String toString()  {
      StringBuffer rv = new StringBuffer();
      rv.append(" -- "
                + getClass().getName()
                +"\n"
                );
       if(_wfjson == null) {
          rv.append("null");
       } else {
          try {
             rv.append("original wf:\n" + _wfjson.toString(2) + "\n");
          } catch (Exception e) {
             String m = "Error serializing ["+_wfjson+"]";
             rv.append(Tools.stringStackTrace(new Exception(m,e)));
          }
          try {
             rv.append("json     wf:\n" + toJson().toString(2) + "\n");
          } catch (Exception e) {
             String m = "Error serializing [toJson]";
             rv.append(Tools.stringStackTrace(new Exception(m,e)));
          }
       }
       return(rv.toString());
   }

   
   private JSONObject _wfjson = null;
   private JSONObject _statusIndex = null;
   Vector<String>     _statusList   = new Vector<String>();
   private String     _defaultStatus = null;
   private JSONArray  _defaultSearchStatuses = null;

   
   //-
   //-
   //- static methods
   //-
   //-
   
   
}
