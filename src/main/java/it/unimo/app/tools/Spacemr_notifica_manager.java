package it.unimo.app.tools;

import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unimo.app.om.App_notifica;
import it.unimo.app.tools.Tools;
import org.springframework.transaction.annotation.Transactional;

   
public class Spacemr_notifica_manager {
   
   @Autowired
   private Tools tools;

   @Autowired
   private App_notifica app_notifica;
   
   public void runUsage() {
      String s = ""
         + "\n spacemr_notifica_manager:"
         + "\n  add <bean_name> <object_id> <json_info>"
         + "\n  serve_all_notifica"
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
            //-
            //-  cd /dati/toolsZippati/projects/spacemr; time gradle bootRun -Pargs="spacemr_notifica_manager test"
            //-
            System.out.println("-- hello! Spacemr_notifica_manager test.");
            System.out.println("args:");
            for (;ipos<args.length;ipos++) {
               System.out.println("   " + ipos + ": " + args[ipos]);
            }
         } else if (option.equals("add")) {
            //-
            //- cd /dati/toolsZippati/projects/spacemr; time gradle bootRun -Pargs="spacemr_notifica_manager add spacemr_space_people_book:notifica_serve 7 ''"
            //-
            JSONObject obj = new JSONObject();
            obj.put("date"     , (new Date()).getTime());
            obj.put("bean_name" , args[ipos++]);
            obj.put("object_id", Integer.parseInt(args[ipos++]));
            obj.put("json_info", args[ipos++]);
            obj.put("fg_serving", Boolean.FALSE);
            //-
            app_notifica.insert(obj);
            //-
            System.out.println(" done.");
            //-
         } else if (option.equals("serve_all_notifica")) {
            //-
            //- cd /dati/toolsZippati/projects/spacemr; time gradle bootRun -Pargs="spacemr_notifica_manager serve_all_notifica"
            //-
            do_serve_notificas();
            //-
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }

   /** serves all notificas - use this, this is transactional! */
   @Transactional
   public boolean do_serve_notificas() throws Exception {
      return(app_notifica.do_serve_notificas());
   }

   
}
