package it.unimo.app.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import org.json.JSONArray;


/* 
   manages three kinds of algorithms
   *
         {
            "algorithm": "user_list"
            , "users": "acorni,list,of,users"
          }

         oppure
          {
            "algorithm": "week_even_odd"
            , "even": "list,of,users,on,even,weeks"
            , "odd": "list,of,users,on,odd,weeks"
          }

         oppure
          {
            "algorithm": "date"
            , groups: [
             {"name": "groupa", "users":"list,of,users,of,the,groupa"}
             , {"name": "groupb", "users":"list,of,users,of,the,groupb"}
             , {"name": "groupc", "users":"list,of,users,of,the,groupc"}
            ]
            , "dates": [
              {"date": "20210208", "group": "groupa"}
              , {"date": "20210209", "group": "groupb"}
              , {"date": "20210209", "group": "groupa"}
              , {"date": "20210210", "users": "custom,list,of,users,on,this,day,groupc"}
            ]
          }
  */
public class Spacemr_user_white_list_parser {

   /**  
    * return a white_list checker
    */
   public Spacemr_user_white_list_parser getSpacemr_user_white_list_parser(String json_string
                                                                           ) throws Exception {
      return(getSpacemr_user_white_list_parser(json_string, null));
   }
   public Spacemr_user_white_list_parser getSpacemr_user_white_list_parser(String json_string, String space_code
                                                                           ) throws Exception {
      JSONObject json = null;
      if (json_string == null
          || json_string.trim().equals("")) {
         json_string = null;
         json = new JSONObject();
         json.put("algorithm", "none");
      } else {
         try{
            json = new JSONObject(json_string);
         } catch (Exception e) {
            String m = "Error detected parsing JSON file:"
               + "\n" + e.getMessage() + "\n"
               + "\n in \n--begin--\n" + json_string + "\n--end--"
               ;
            throw (new Exception(m,e));
         }
      }
      Spacemr_user_white_list_parser rv = getSpacemr_user_white_list_parser(json, space_code);
      return(rv);
   }
   
   public Spacemr_user_white_list_parser getSpacemr_user_white_list_parser(JSONObject json
                                                                           , String space_code
                                                                           ) throws Exception {
      String space_codej = getJSONString(json, new String[]{"space_code", "codice_spazio"});
      if (space_code != null
          && space_codej != null
          && !space_codej.equals(space_code)) {
         String m = "mismatch on space_code, ["+space_code+"] vs ["+space_codej+"] in\n" + json.toString(2);
         throw (new Exception(m));
      }
      Spacemr_user_white_list_parser rv = getSpacemr_user_white_list_parser(json);
      return(rv);
   }
   
   public Spacemr_user_white_list_parser getSpacemr_user_white_list_parser(JSONObject json
                                                                           ) throws Exception {
      Spacemr_user_white_list_parser rv = null;
      if (json.length() == 0) {
         rv = new Agorithm__none();
         rv.setAlgorithm_name("none");
      } else {
         String algorithm_name = getJSONStringWithMessage(json, new String[]{"algorithm", "algoritmo"});
         if (algorithm_name.equals("everyone")) {
            rv = new Agorithm__everyone();
            //-
            //-
            //-
         } else if (algorithm_name.equals("none")) {
            rv = new Agorithm__none();
            //-
            //-
            //-
         } else if (algorithm_name.equals("user_list")
                    || algorithm_name.equals("lista_utenti")) {
            rv = new Agorithm__user_list();
            //-
            //-
            //-
         } else if (algorithm_name.equals("week_even_odd")
                    || algorithm_name.equals("settimane_alterne")) {
            rv = new Agorithm__week_even_odd();
            //-
            //-
            //-
         } else if (algorithm_name.equals("weekly")
                    || algorithm_name.equals("settimanale")) {
            rv = new Agorithm__weekly();
            //-
            //-
            //-
         } else if (algorithm_name.equals("weekly_groups")
                    || algorithm_name.equals("settimanale_gruppi")) {
            rv = new Agorithm__weekly_groups();
            //-
            //-
            //-
         } else if (algorithm_name.equals("date_by_date")
                    || algorithm_name.equals("data_per_data")) {
            rv = new Agorithm__date_by_date();
            //-
            //-
            //-
         } else {
            String m = "'algorithm' ["+algorithm_name+"] not found in\n" + json.toString(2);
            throw (new Exception(m));
         }
         rv.setAlgorithm_name(algorithm_name);
         // System.out.println("json:\n" + json.toString(2));
         rv.parse(json);
      }
      return(rv);
   }
   
   /*
    *
    * static methods
    *
    *
    */
   private static Spacemr_user_white_list_parser _parser_factory = null;
   public static  Spacemr_user_white_list_parser getParser(String json_s) throws Exception {
      return(getParser(json_s, null));
   }
   public static  Spacemr_user_white_list_parser getParser(String json_s, String space_code) throws Exception {
      if(_parser_factory == null){
         _parser_factory = new Spacemr_user_white_list_parser();
      }
      return(_parser_factory.getSpacemr_user_white_list_parser(json_s, space_code));
   }
   public static Spacemr_user_white_list_parser getParser(JSONObject json) throws Exception {
      return(getParser(json, null));
   }
   public static Spacemr_user_white_list_parser getParser(JSONObject json, String space_code) throws Exception {
      if(_parser_factory == null){
         _parser_factory = new Spacemr_user_white_list_parser();
      }
      return(_parser_factory.getSpacemr_user_white_list_parser(json,space_code));
   }
   /*
    *
    *  shared methods
    *
    *
    */
   protected void setJSON(JSONObject json) {
      _json = json;
   }
   public JSONObject getJSON() {
      return(_json);
   }
   protected void setAlgorithm_name(String n) {
      _algorithm_name = n;
   }
   public String getAlgorithm_name() {
      return(_algorithm_name);
   }
   protected Object getFromJSONWithMessage(JSONObject json, String[] names
                                           ) throws Exception {
      Object rv = getFromJSON(json, names);
      if (rv == null) {
         String m = "'"+Arrays.toString(names)+"' JSON attribute is required in\n" + json.toString(2);
         throw (new Exception(m));
      }
      return(rv);
   }
   protected Object getFromJSON(JSONObject json, String[] names
                                ) throws Exception {
      Object rv = null;
      for (String name: names) {
         if (rv == null) {
            rv = json.opt(name);
         }
      }
      return(rv);
   }
   protected String getJSONString_check(JSONObject json, String[] names, Object rvo
                                        ) throws Exception {
      String rv = null;
      if (rvo != null) {
         if (rvo instanceof String) {
            rv = (String)rvo;
         } else {
            String m = "'"+Arrays.toString(names)+"' JSON attribute is expected to be a String in\n" + json.toString(2);
            throw (new Exception(m));
         }
      }
      return(rv);
   }
   protected String getJSONStringWithMessage(JSONObject json, String[] names
                                       ) throws Exception {
      Object rvo  = getFromJSONWithMessage(json, names);
      return(getJSONString_check(json, names, rvo));
   }
   protected String getJSONString(JSONObject json, String[] names
                                  ) throws Exception {
      Object rvo  = getFromJSON(json, names);
      return(getJSONString_check(json, names, rvo));
   }
   protected JSONArray getJSONJSONArray_check(JSONObject json, String[] names, Object rvo
                                        ) throws Exception {
      JSONArray rv = null;
      if (rvo != null) {
         if (rvo instanceof JSONArray) {
            rv = (JSONArray)rvo;
         } else {
            String m = "'"+Arrays.toString(names)+"' JSON attribute is expected to be a JSONArray in\n" + json.toString(2);
            throw (new Exception(m));
         }
      }
      return(rv);
   }
   protected JSONArray getJSONJSONArrayWithMessage(JSONObject json, String[] names
                                       ) throws Exception {
      Object rvo  = getFromJSONWithMessage(json, names);
      return(getJSONJSONArray_check(json, names, rvo));
   }
   protected JSONArray getJSONJSONArray(JSONObject json, String[] names
                                  ) throws Exception {
      Object rvo  = getFromJSON(json, names);
      return(getJSONJSONArray_check(json, names, rvo));
   }
   private HashSet<String> csvToHashset(String users_s
                                        ) throws Exception {
      HashSet<String> rv = new HashSet<String>();
      if (users_s != null) {
         Vector<String> users_v = Tools.splitString(users_s, ',');
         for(String u: users_v) {
            if (u != null) {
               u = u.trim();
               if (u.length() > 0) {
                  rv.add(u);
                  // System.out.println(" " + u +  " " + rv.size());
               }
            }
         }
      }
      return(rv);
   }
   public JSONArray getAllInvolvedUsersAsJson() throws Exception {
      JSONArray rv = new JSONArray();
      // Set<String> userset = getAllInvolvedUsers();
      // List<String> sortedList = new ArrayList<String>(userset);
      // Collections.sort(sortedList);
      Object[] array = getAllInvolvedUsers().toArray();
      Arrays.sort(array);
      //-
      for(Object o: array) {
         rv.put(o);
      }
      // System.out.println(" rv.size: " + rv.length());
      return(rv);
   }
   public String getTextDescription() throws Exception {
      return(getTextDescription((HashMap<String,JSONObject>)null));
   }
   protected String getTextDescription_user_list_info(Set<String> users
                                                      , HashMap<String,JSONObject> info
                                                      ) throws Exception {
      StringBuffer sb = new StringBuffer();
      Object[] array = users.toArray();
      Arrays.sort(array);
      for(Object user_nameo: array) {
         String user_name = (String)user_nameo;
         sb.append(user_name);
         if (info != null) {
            JSONObject j = info.get(user_name);
            if (j != null) {
               sb.append(
                         "\t"+j.optString("last_name", "")
                         +"\t"+j.optString("first_name", "")
                         +"\t"+j.optString("email", "")
                         +"\t"+j.optString("source", "")
                         );
            }
         }
         sb.append("\n");
      }
      return(sb.toString());
   }

   /*
    *
    *
    *  methods to ovverride  
    *
    *
    */
   /** method to ovverride  */
   public boolean userCanAccess(String username, Date date) throws Exception {
      return(false);
   }
   /** method to ovverride  */
   public void parse(JSONObject json) throws Exception {
      setJSON(json);
   }
   /** method to ovverride  */
   public Set<String> getAllInvolvedUsers() throws Exception {
      return(null);
   }
   public String getTextDescription(HashMap<String,JSONObject> info
                                    ) throws Exception {
      return(null);
   }
   /* 
    *
    * Agorithm__everyone
    *
         {
            "algorithm": "everyone"
         }
    *
    */
   private class Agorithm__everyone extends Spacemr_user_white_list_parser {
      public void parse(JSONObject json) throws Exception {
         super.parse(json);
         // System.out.println(" -- parsing   Agorithm__everyone");
      }
      public boolean userCanAccess(String username, Date date) throws Exception {
         return(true);
      }
      public Set<String> getAllInvolvedUsers() throws Exception {
         @SuppressWarnings("unchecked")
         Set<String> rv = (Set<String>)_users.clone();
         return(rv);
      }
      public String getTextDescription(HashMap<String,JSONObject> info
                                    ) throws Exception {
         return("algorithm\teveryone");
      }
      private HashSet<String> _users = new HashSet<String>();
   }

   /* 
    *
    * Agorithm__none
    *
         {
            "algorithm": "none"
         }
    *
    */
   private class Agorithm__none extends Spacemr_user_white_list_parser {
      public void parse(JSONObject json) throws Exception {
         super.parse(json);
         // System.out.println(" -- parsing   Agorithm__none");
      }
      public boolean userCanAccess(String username, Date date) throws Exception {
         return(false);
      }
      public Set<String> getAllInvolvedUsers() throws Exception {
         @SuppressWarnings("unchecked")
         Set<String> rv = (Set<String>)_users.clone();
         return(rv);
      }
      public String getTextDescription(HashMap<String,JSONObject> info
                                    ) throws Exception {
         return("algorithm\tnone");
      }
      private HashSet<String> _users = new HashSet<String>();
   }

   
   /* 
    *
    * Agorithm__user_list
    *
         {
            "algorithm": "user_list"
            , "users": "list,of,users"
         }
    *
    *  "regexp": "list,of,regexp,in,or" - not implemented - may be useful?
    *             should I use a differente "Algorithm"?
    *
    */
   private class Agorithm__user_list extends Spacemr_user_white_list_parser {
      public void parse(JSONObject json) throws Exception {
         super.parse(json);
         // System.out.println(" -- parsing   Agorithm__user_list");
         _users = csvToHashset(getJSONStringWithMessage(json, new String[]{"users", "utenti"}));
      }
      public boolean userCanAccess(String username, Date date) throws Exception {
         boolean rv = false;
         rv = _users.contains(username);
         return(rv);
      }
      public Set<String> getAllInvolvedUsers() throws Exception {
         @SuppressWarnings("unchecked")
         Set<String> rv = (Set<String>)_users.clone();
         return(rv);
      }
      public String getTextDescription(HashMap<String,JSONObject> info
                                       ) throws Exception {
         StringBuffer sb = new StringBuffer();
         sb.append("algorithm\tuser_list");
         //-
         sb.append("\n");
         sb.append("\nusers");
         sb.append("\n");
         Set<String> users = getAllInvolvedUsers();
         sb.append(getTextDescription_user_list_info(users, info));
         //-
         return(sb.toString());
      }
      private HashSet<String> _users = null;
   }
   

   /* 
    *
    * Agorithm__week_even_odd
    *
          {
            "algoritmo": "settimane_alterne",
            "codice_spazio": "kk2",
            "utenti_pari": "utente1, utente5",
            "utenti_dispari": "utente2, utente3, utente4"
          }
    *
    *  "regexp": "list,of,regexp,in,or" - not implemented - may be useful?
    *             should I use a differente "Algorithm"?
    *
    */
   private class Agorithm__week_even_odd extends Spacemr_user_white_list_parser {
      @SuppressWarnings("unchecked")
      public void parse(JSONObject json) throws Exception {
         super.parse(json);
         _users = (HashSet<String>[])(new HashSet[2]);
         // System.out.println(" -- parsing   Agorithm__week_even_odd");
         _users[0] = csvToHashset(getJSONStringWithMessage(json, new String[]{"users_even", "utenti_pari"}));
         _users[1] = csvToHashset(getJSONStringWithMessage(json, new String[]{"users_odd", "utenti_dispari"}));
      }
      public boolean userCanAccess(String username, Date date) throws Exception {
         // System.out.println(" -hello from Agorithm__week_even_odd for ["+username+"] ["+date+"]");
         //-
         Calendar cal = Calendar.getInstance(Locale.ITALY);
         cal.setTime(date);
         int week = cal.get(Calendar.WEEK_OF_YEAR);
         // System.out.println(" WEEK_OF_YEAR   " + week);
         week = week % 2;
         // System.out.println(" week is   " + week);
         //-
         boolean rv = false;
         rv = _users[week].contains(username);
         return(rv);
      }
      public Set<String> getAllInvolvedUsers() throws Exception {
         @SuppressWarnings("unchecked")
         Set<String> rv = (Set<String>)_users[0].clone();
         rv.addAll(_users[1]);
         return(rv);
      }
      public String getTextDescription(HashMap<String,JSONObject> info
                                       ) throws Exception {
         StringBuffer sb = new StringBuffer();
         sb.append("algorithm\tweek_even_odd");
         //-
         sb.append("\n");
         sb.append("\nusers_even");
         sb.append("\n");
         sb.append(getTextDescription_user_list_info(_users[0], info));
         sb.append("\n");
         sb.append("\nusers_odd");
         sb.append("\n");
         sb.append(getTextDescription_user_list_info(_users[1], info));
         //-
         return(sb.toString());
      }
      private HashSet<String>[] _users = null;
   }



   /* 
    *
    * Agorithm__weekly
    *
          {
            "algoritmo": "settimanale",
            "codice_spazio": "kk1",
            "utenti_lunedi": "1,2,5,7",
            "utenti_mercoledi": "1,2,5,7",
            "utenti_venerdi": "1,2,3,4,5,7,6,8",
            "utenti_martedi": "3,4,6,8",
            "utenti_giovedi": "3,4,6,8"
          }
    *
    *  "regexp": "list,of,regexp,in,or" - not implemented - may be useful?
    *             should I use a differente "Algorithm"?
    *
    */
   private class Agorithm__weekly extends Spacemr_user_white_list_parser {
      @SuppressWarnings("unchecked")
      public void parse(JSONObject json) throws Exception {
         super.parse(json);
         _users = (HashSet<String>[])(new HashSet[7]);
         // System.out.println(" -- parsing   Agorithm__weekly");
         boolean one = false;
         for (int i=0; i < _days.length; i++) {
            _users[i] = csvToHashset(getJSONString(json, _days[i]));
            if (_users[i].size() > 1) {
               one = true;
            };
         }
         if (!one) {
            String m = "at least a day of the week must be populated not found in\n"
               + json.toString(2)
               + "\n valid 'names' are:\n"
               ;
            String comma="";
            for (String[] d: _days) {
               m = m + comma + Arrays.toString(d);
               comma=", ";
            }
            throw (new Exception(m));
         }
      }
      public boolean userCanAccess(String username, Date date) throws Exception {
         // System.out.println(" -hello from Agorithm__weekly for ["+username+"] ["+date+"]");
         //-
         Calendar cal = Calendar.getInstance(Locale.ITALY);
         cal.setTime(date);
         int day = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
         // System.out.println(" DAY_OF_WEEK   " + day);
         //-
         boolean rv = false;
         rv = _users[day].contains(username);
         return(rv);
      }
      public Set<String> getAllInvolvedUsers() throws Exception {
         @SuppressWarnings("unchecked")
         Set<String> rv = (Set<String>)_users[0].clone();
         for (int i=1; i < 7; i++) {
            rv.addAll(_users[i]);
         }
         return(rv);
      }
      public String getTextDescription(HashMap<String,JSONObject> info
                                       ) throws Exception {
         StringBuffer sb = new StringBuffer();
         sb.append("algorithm\tweekly");
         //-
         for (int i=0; i < 7; i++) {
            sb.append("\n");
            sb.append("\n" + _days[i][0]);
            sb.append("\n");
            sb.append(getTextDescription_user_list_info(_users[i], info));
         }
         //-
         return(sb.toString());
      }
      private HashSet<String>[] _users = null;
      private String _days[][] = {
            {"users_monday", "utenti_lunedi"}
            , {"users_tuesday", "utenti_martedi"}
            , {"users_wednesday", "utenti_mercoledi"}
            , {"users_thursday", "utenti_giovedi"}
            , {"users_friday", "utenti_venerdi"}
            , {"users_saturday", "utenti_sabato"}
            , {"users_sunday", "utenti_domenica"}
         };
   }



   /* 
    *
    * Agorithm__weekly_groups
    *
          {
            "algoritmo": "settimanale_gruppi",
            "codice_spazio": "kk1",
            "gruppi": [
              {"nome": "rossi", "utenti": "a1, a2, a3, a4"}
              , {"nome": "gialli", "utenti": "b1, b2, b3, b4"}
              , {"nome": "verdini", "utenti": "v1, v2, v3, v4"}
            ],
            "gruppo_lunedi": "rossi",
            "gruppo_martedi": "gialli",
            "gruppo_mercoledi": "verdini",
            "gruppo_giovedi": "rossi",
            "gruppo_venerdi": "gialli"
          }
    *
    *  "regexp": "list,of,regexp,in,or" - not implemented - may be useful?
    *             should I use a differente "Algorithm"?
    *
    */
   private class Agorithm__weekly_groups extends Spacemr_user_white_list_parser {
      @SuppressWarnings("unchecked")
      public void parse(JSONObject json) throws Exception {
         super.parse(json);
         //-
         _users = (HashSet<String>[])(new HashSet[7]);
         _daily_groups = new String[7];
         _groups = new HashMap<String,HashSet<String>>();
         //-
         // System.out.println(" -- parsing   Agorithm__weekly_groups");
         //-
         boolean one = false;
         JSONArray groupsj = getJSONJSONArrayWithMessage(json, new String[]{"groups", "gruppi"});
         for (int i=0; i<groupsj.length(); i++) {
            JSONObject g = groupsj.getJSONObject(i);
            String name   = getJSONStringWithMessage(g, new String[]{"name", "nome"});
            String userss = getJSONStringWithMessage(g, new String[]{"users", "utenti"});
            _groups.put(name, csvToHashset(userss));
            one = true;
         }
         if (!one) {
            String m = "at least one group must be defined in\n" + json.toString(2) ;
            throw (new Exception(m));
         }
         //-
         one = false;
         for (int i=0; i < _days.length; i++) {
            String group_name = getJSONString(json, _days[i]);
            _daily_groups[i] = group_name;
            _users[i] = new HashSet<String>();
            if (group_name == null) {
               _users[i] = new HashSet<String>();
            } else {
               Vector<String> groups_v = Tools.splitString(group_name, ',');
               for(String g: groups_v) {
                  if (g != null) {
                     g = g.trim();
                     if (g.length() > 0) {
                        HashSet<String> group = _groups.get(g);
                        if (group != null) {
                           _users[i].addAll(group);
                           one = true;
                        } else {
                           String m = "group name "+g+" not found in defined groups "
                              + "["+Arrays.toString(_groups.keySet().toArray())+"]";
                           throw new Exception(m);
                        }
                        // System.out.println(" " + u +  " " + rv.size());
                     }
                  }
               }               
            };
         }
         if (!one) {
            String m = "at least a day of the week must be populated not found in\n"
               + json.toString(2)
               + "\n valid 'names' are:\n"
               ;
            String comma="";
            for (String[] d: _days) {
               m = m + comma + Arrays.toString(d);
               comma=", ";
            }
            throw (new Exception(m));
         }
      }
      public boolean userCanAccess(String username, Date date) throws Exception {
         // System.out.println(" -hello from Agorithm__weekly_groups for ["+username+"] ["+date+"]");
         //-
         Calendar cal = Calendar.getInstance(Locale.ITALY);
         cal.setTime(date);
         int day = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
         // System.out.println(" DAY_OF_WEEK   " + day);
         //-
         boolean rv = false;
         rv = _users[day].contains(username);
         return(rv);
      }
      public Set<String> getAllInvolvedUsers() throws Exception {
         @SuppressWarnings("unchecked")
         Set<String> rv = (Set<String>)_users[0].clone();
         for (int i=1; i < 7; i++) {
            rv.addAll(_users[i]);
         }
         return(rv);
      }
      @SuppressWarnings("unchecked")
      public String getTextDescription(HashMap<String,JSONObject> info
                                       ) throws Exception {
         StringBuffer sb = new StringBuffer();
         sb.append("algorithm\tweekly_groups");
         //-
         sb.append("\n");
         for (int i=0; i < 7; i++) {
            sb.append("\n" + _days[i][0] + ": " + _daily_groups[i]);
         }
         sb.append("\n");
         //-
         Object groupNames[] = _groups.keySet().toArray();
         Arrays.sort(groupNames);
         for (Object group_nameo: groupNames) {
            String group_name = (String)group_nameo;
            sb.append("\n");
            sb.append("\n group " + group_name);
            sb.append("\n");
            sb.append(getTextDescription_user_list_info(_groups.get(group_name), info));
         }
         //-
         return(sb.toString());
      }
      private HashSet<String>[] _users = null; // "exploded" users
      private String[]   _daily_groups = null; // stores the groups information
      private HashMap<String,HashSet<String>> _groups = null;
      private String _days[][] = {
            {"group_monday", "gruppo_lunedi"}
            , {"group_tuesday", "gruppo_martedi"}
            , {"group_wednesday", "gruppo_mercoledi"}
            , {"group_thursday", "gruppo_giovedi"}
            , {"group_friday", "gruppo_venerdi"}
            , {"group_saturday", "gruppo_sabato"}
            , {"group_sunday", "gruppo_domenica"}
         };
   }

   

   /* 
    *
    * agorithm: date_by_date
    *
          {
            "algorithm": "date_by_date",
            "space_code": "kk1",
            "groups": [
                {"name": "rossi",   "users": "a1, a2, a3, a4"}
              , {"name": "gialli",  "users": "b1, b2, b3, b4"}
              , {"name": "verdini", "users": "v1, v2, v3, v4"}
            ],
            "dates": [
                {"date": "20210301", "groups": "rossi"}
              , {"date": "20210302", "groups": "rossi,gialli"}
              , {"date": "20210303",  "users": "u1, u2, u5"}
              , {"date": "20210304",  "users": "u3, u4", "groups": "verdini"}
             ]
          }
          {
            "algoritmo": "data_per_data",
            "codice_spazio": "kk1",
            "gruppi": [
              {"nome": "rossi", "utenti": "a1, a2, a3, a4"}
              , {"nome": "gialli", "utenti": "b1, b2, b3, b4"}
              , {"nome": "verdini", "utenti": "v1, v2, v3, v4"}
            ],
            "date": [
                {"data": "20210301", "gruppi": "rossi"}
              , {"data": "20210302", "gruppi": "rossi,gialli"}
              , {"data": "20210303", "utenti": "u1, u2, u5"}
              , {"data": "20210304", "utenti": "u3, u4", "gruppi": "verdini"}
             ]
          }
    *
    */
   private class Agorithm__date_by_date extends Spacemr_user_white_list_parser {
      @SuppressWarnings("unchecked")
      public void parse(JSONObject json) throws Exception {
         super.parse(json);
         //-
         _date_users = new HashMap<Long, HashSet<String>>();
         _groups     = new HashMap<String,HashSet<String>>();
         _date_s_groups = new HashMap<Long, HashSet<String>>();
         _date_s_users  = new HashMap<Long, HashSet<String>>();
         //-
         // System.out.println(" -- parsing   Agorithm__weekly_groups");
         //-
         boolean one = false;
         JSONArray groupsj = (JSONArray)getFromJSON(json, new String[]{"groups", "gruppi"});
         if (groupsj != null) {
            for (int i=0; i<groupsj.length(); i++) {
               JSONObject g = groupsj.getJSONObject(i);
               String name   = getJSONStringWithMessage(g, new String[]{"name", "nome"});
               String userss = getJSONStringWithMessage(g, new String[]{"users", "utenti"});
               _groups.put(name, csvToHashset(userss));
            }
         }
         //-
         JSONArray dates = (JSONArray)getFromJSONWithMessage(json, new String[]{"dates", "date"});
         for (int i=0; i < dates.length(); i++) {
            JSONObject datej = dates.getJSONObject(i);
            String date_s   = getJSONStringWithMessage(datej, new String[]{"date", "data"});
            long date = _DATE_FORMATTER.parse(date_s).getTime();
            //-
            HashSet<String> date_users    = new HashSet<String>();
            HashSet<String> date_s_groups = new HashSet<String>();
            HashSet<String> date_s_users  = null;
            //-
            one = false;
            {
               String users_s   = getJSONString(datej, new String[]{"users", "utenti"});
               if (users_s != null) {
                  date_s_users = csvToHashset(users_s);
                  date_users.addAll(date_s_users);
                  one = true;
               } else {
                  date_s_users = new HashSet<String>();
               }
            }
            {
               String groups_s   = getJSONString(datej, new String[]{"groups", "gruppi"});
               if (groups_s != null) {
                  Vector<String> groups_v = Tools.splitString(groups_s, ',');
                  for(String g: groups_v) {
                     if (g != null) {
                        g = g.trim();
                        if (g.length() > 0) {
                           HashSet<String> group = _groups.get(g);
                           if (group != null) {
                              date_users.addAll(group);
                              date_s_groups.add(g);
                              one = true;
                           } else {
                              String m = "group name "+g+" not found in defined groups "
                                 + "["+Arrays.toString(_groups.keySet().toArray())+"]";
                              throw new Exception(m);
                           }
                           // System.out.println(" " + u +  " " + rv.size());
                        }
                     }
                  }
               }
            }
            if (!one) {
               String m = "at least a user must be definde for the date ["+date_s+"]\n"
                  + json.toString(2)
                  + "\n"
                  ;
               throw (new Exception(m));
            }
            //-
            _date_users.put(date, date_users);
            _date_s_groups.put(date, date_s_groups);
            _date_s_users.put(date, date_s_users);
            //-
         }
      }
      public boolean userCanAccess(String username, Date date) throws Exception {
         // System.out.println(" -hello from Agorithm__date_by_date for ["+username+"] ["+date+"]");
         //-
         boolean rv = false;
         //-
         Calendar cal = Calendar.getInstance(Locale.ITALY);
         cal.setTime(date);
         cal.set(Calendar.HOUR_OF_DAY, 0);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         long time = cal.getTimeInMillis();
         //-
         HashSet<String> users = _date_users.get(time);
         if (users != null) {
            rv = users.contains(username);
         }
         return(rv);
      }
      @SuppressWarnings("unchecked")
      public Set<String> getAllInvolvedUsers() throws Exception {
         if (_involvedUsers == null) {
            HashSet<String> rv = new HashSet<String>();
            //-
            for (Map.Entry entry :_date_users.entrySet()) {
               rv.addAll((HashSet<String>)entry.getValue());
            }
            _involvedUsers = rv;
         }
         return(_involvedUsers);
      }
      @SuppressWarnings("unchecked")
      public String getTextDescription(HashMap<String,JSONObject> info
                                       ) throws Exception {
         StringBuffer sb = new StringBuffer();
         sb.append("algorithm\tdate_by_date");
         //-
         sb.append("\n");
         //-
         Object groupNames[] = _groups.keySet().toArray();
         Arrays.sort(groupNames);
         for (Object group_nameo: groupNames) {
            String group_name = (String)group_nameo;
            sb.append("\n");
            sb.append("\n group " + group_name);
            sb.append("\n");
            sb.append(getTextDescription_user_list_info(_groups.get(group_name), info));
         }
         //-
         sb.append("\n");
         sb.append("\n");
         sb.append("\n");
         Object dates[] = _date_users.keySet().toArray();
         Arrays.sort(dates);
         for (Object dateo: dates) {
            Long date = (Long)dateo;
            sb.append("\n --- date: " + _DATE_FORMATTER.format(new Date(date.longValue())));
            HashSet<String>  date_s_groups = _date_s_groups.get(date);
            HashSet<String>  date_s_users  = _date_s_users.get(date);
            sb.append("\n groups: " + date_s_groups);
            sb.append("\n users:\n" );
            sb.append(getTextDescription_user_list_info(date_s_users, info));
            //-
            sb.append("\n");
            sb.append("\n");
         }
         //-
         return(sb.toString());
      }
      private HashMap<Long, HashSet<String>>  _date_users = null;
      private HashMap<String,HashSet<String>> _groups = null;
      private HashMap<Long, HashSet<String>>  _date_s_groups = null; // stores the groups information
      private HashMap<Long, HashSet<String>>  _date_s_users  = null; // stores the users   information
      private HashSet<String> _involvedUsers = null;
   }



   
   private JSONObject _json = null;
   private String _algorithm_name = null;
   private static SimpleDateFormat _DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
}


