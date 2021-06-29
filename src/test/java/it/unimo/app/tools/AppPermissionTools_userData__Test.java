package it.unimo.app.tools;

import java.util.*;
import org.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.*;
import static org.junit.Assert.*;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests AppPermissionTools_userData__Test \
     && cat build/test-results/test/binary/output.bin
     ls build/test-results/test
 */
public class AppPermissionTools_userData__Test {

   private Tools tools = new Tools();

   @Test
   public void test_helloWorld() {
      System.out.println(" ----- Hello AppPermissionTools_userData__Test");
   }

   @Test
   public void test_groupsHavingThePermission() throws Exception {
      AppPermissionTools_userData apud = new AppPermissionTools_userData();
      String[][] data = {
           {"root", "p1"}
         , {"g1", "p2"}
         , {"g2", "p2"}
         , {"g2", "p3"}
         , {"g3", "p3"}
         , {"g3", "p4"}
         , {"g4", "p5"}
      };
      for (String datai[]: data) {
         apud.addPermission(datai[0], datai[1]);
         // System.out.println(" group."+datai[0] + "\t permission."+datai[1]);
      }
      assertTrue(test_groupsHavingThePermission_check(apud, "p1", new String[]{"g1","g2","g3","g4","root"}));
      assertTrue(test_groupsHavingThePermission_check(apud, "p2", new String[]{"g1","g2"}));
      assertTrue(test_groupsHavingThePermission_check(apud, "p3", new String[]{"g2","g3"}));
      assertTrue(test_groupsHavingThePermission_check(apud, "p4", new String[]{"g3"}));
      assertTrue(test_groupsHavingThePermission_check(apud, "p5", new String[]{"g4"}));
      System.out.println("--- test_groupsHavingThePermission Passed!");
   }
   private boolean test_groupsHavingThePermission_check(AppPermissionTools_userData apud
                                                        , String p
                                                        , String[] expected) throws Exception {
      boolean rv = false;
      JSONArray value = apud.groupsHavingThePermission(p);
      String valuea[] = new String[value.length()];
      System.out.println(" value: " + value);
      
      for (int i = 0; i < value.length(); i++) {
         String s = value.getString(i);
         valuea[i]=s;
         System.out.println("    vg:" + s);
      }
      Arrays.sort(valuea);
      String values = Arrays.toString(valuea);
      String expecteds = Arrays.toString(expected);
      //-
      rv = values.equals(expecteds);
      if (!rv) {
         System.out.println(" checking groups for permission ["+p+"]"
                            + "\n  got."+values
                            + " and expected."+expecteds
                            );
      }
      return(rv);
   }
   
}
