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
         --tests it.unimo.app.tools.Workflow__Test2_exampleBooking \
     ; cat build/test-results/test/binary/output.bin
     cat build/test-results/test/TEST-it.unimo.app.tools.Workflow__Test2_exampleBooking.xml
     ls build/test-results/test
   ec spacemr/src/test/java/it/unimo/app/tools/Workflow__Test2_exampleBooking.java
 */
public class Workflow__Test2_exampleBooking {

   //-
   //
   
   @Test
   public void test_firstTest() throws Exception {
      //-
      System.out.println(" hello Workflow__Test2_exampleBooking!");
      //-
      ApplicationContext ac = Application.getStaticContext();
      Tools tools = (Tools)ac.getBean("tools");
      //-
      String examples =
         tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test2_exampleBooking.json");
      //-
      // System.out.println("examples: " + examples);
      JSONObject examplewf = new JSONObject(examples);
      //-
      Set<String> permissions =
         new HashSet<>(Arrays.asList("db_spacemr_space_people_book_wf_admin"));
      Workflow wf = new Workflow(examplewf, permissions);
      //-
      System.out.println(wf.toString());
      //-
      //    JSONObject statusOld =
      //       new JSONObject(tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test_exampleInventario_oldStatus.json"));
      //    JSONObject statusNew =
      //       new JSONObject(tools.string_loadFromFile("src/test/java/it/unimo/app/tools/Workflow__Test_exampleInventario_newStatus.json"));
      //    //-
      //    wf.per_field_restoreUnwritable(statusNew, statusOld, null, null);
      //-
      //-
      //-
      //- JSONObject obj = new JSONObject();
      //- obj.put("spacemr_space_people_book_id", 3);
      //- obj.put("spacemr_responsible_id", 860);
      //- obj.put("spacemr_people_id", 10);
      //- obj.put("date_from", 1590962400000L);
      //- obj.put("date_to",   1591048800000L);
      //- wf.run_actions("proposta", "accettata", obj);
      //-
      assert(true);
   }

   
}
