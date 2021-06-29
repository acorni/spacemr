package it.unimo.app.tools;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import it.unimo.app.Application;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.Workflow;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests Workflow__Test1_exampleInventario \
     && cat build/test-results/test/binary/output.bin
     ls build/test-results/test
 */
public class Workflow__Test1_exampleInventario {

   //-
   //
   
   @Test
   public void test_restoreUnwritable() throws Exception {
      //-
      //-  System.out.println(" hello Workflow__Test1_exampleInventario!");
      //-
      ApplicationContext ac = Application.getStaticContext();
      Tools tools = (Tools)ac.getBean("tools");
      //-
      String examples =
         tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test1_exampleInventario.json");
      //-
      // System.out.println("examples: " + examples);
      JSONObject examplewf = new JSONObject(examples);
      //-
      Set<String> permissions = new HashSet<>(Arrays.asList("db_spacemr_inventario_wf_admin"));
      Workflow wf = new Workflow(examplewf, permissions);
      //-
      //- System.out.println(wf.toString());
      //-
      JSONObject statusOld =
         new JSONObject(tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test1_exampleInventario_oldStatus.json"));
      JSONObject statusNew =
         new JSONObject(tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test1_exampleInventario_newStatus.json"));
      JSONObject statusNew_compare =
         new JSONObject(tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test1_exampleInventario_newStatus.json"));
      //-
      // System.out.println("--- requested changes old values:\n"+Tools.jsonChanges(statusNew, statusOld).toString(2));
      //-
      // System.out.println(" - statusNew before:\n" + statusNew.toString(2));
      wf.per_field_restoreUnwritable(statusOld.getString("stato"), statusNew, statusOld);
      // System.out.println(" - statusNew after:\n" + statusNew.toString(2));
      // System.out.println("--- actual changes old values:\n"+Tools.jsonChanges(statusNew, statusOld).toString(2));
      // System.out.println("--- actual changes new values:\n"+Tools.jsonChanges(statusOld, statusNew).toString(2));
      JSONObject restored = Tools.jsonChanges(statusNew, statusNew_compare);
      //-
      boolean rv = true;
      //-
      String o = ""
         + "{'changed': {"
         + "\n  'codice_inventario_unimore': 'I.004-changed',"
         + "\n  'inventario_numero': 'changed'"
         + "\n}}"
         ;
      o = o.replace('\'', '"');
      if ( restored != null && o.equals(restored.toString(2))) {
         rv = true;
      } else {
         if (restored == null) {
            System.out.println("--- no restored values this is bad.");
         } else {
            System.out.println("--- restored.toString(2):\n"+restored.toString(2));
            System.out.println("-- expected json:\n" + o);
         }
         rv = false;
      }
      assertTrue(rv);
   }


   @Test
   public void test_per_field_hideUnreadable() throws Exception {
      //-
      //-  System.out.println(" hello Workflow__Test1_exampleInventario!");
      //-
      ApplicationContext ac = Application.getStaticContext();
      Tools tools = (Tools)ac.getBean("tools");
      //-
      String examples =
         tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test1_exampleInventario.json");
      //-
      // System.out.println("examples: " + examples);
      JSONObject examplewf = new JSONObject(examples);
      //-
      Set<String> permissions = new HashSet<>(Arrays.asList("db_spacemr_inventario_wf_admin"));
      Workflow wf = new Workflow(examplewf, permissions);
      //-
      //- System.out.println(wf.toString());
      //-
      JSONObject statusOld =
         new JSONObject(tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test1_exampleInventario_oldStatus.json"));
      JSONObject statusOld_compare =
         new JSONObject(tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test1_exampleInventario_oldStatus.json"));
      //-
      // System.out.println("--- requested changes old values:\n"+Tools.jsonChanges(statusNew, statusOld).toString(2));
      //-
      wf.per_field_hideUnreadable(statusOld.getString("stato"), statusOld);
      // System.out.println("--- actual changes new values:\n"+Tools.jsonChanges(statusOld, statusNew).toString(2));
      JSONObject hidden = Tools.jsonChanges(statusOld, statusOld_compare);
      //-
      boolean rv = true;
      //-
      String o = ""
         + "{'removed': {"
         + "\n  'inventario_numero_sub': '0',"
         + "\n  'tipo_carico_scarico': 'CAR_ACQU',"
         + "\n  'valore': 780.45,"
         + "\n  'codice_inventario_unimore': 'I.004',"
         + "\n  'old_values': '',"
         + "\n  'scarico_numero_buono': '',"
         + "\n  'inventario_numero': '1',"
         + "\n  'attivita_tipo': 'Istituzionale',"
         + "\n  'categoria_inventario': 'B.MAT.04.01.08'"
         + "\n}}"
         ;
      o = o.replace('\'', '"');
      if ( hidden != null && o.equals(hidden.toString(2))) {
         rv = true;
      } else {
         if (hidden == null) {
            System.out.println("--- no hidden values this is bad.");
         } else {
            System.out.println("--- hidden.toString(2):\n"+hidden.toString(2));
            System.out.println("-- expected json:\n" + o);
         }
         rv = false;
      }
      System.out.println("json:\n"+wf.toString());
      assertTrue(rv);
   }
  
   
}
