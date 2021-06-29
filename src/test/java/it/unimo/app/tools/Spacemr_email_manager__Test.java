package it.unimo.app.tools;

import java.util.HashMap;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import it.unimo.app.Application;
import it.unimo.app.tools.Tools;


import org.junit.*;
import static org.junit.Assert.*;

/* 
   cd /dati/toolsZippati/projects/spacemr; \
   echo ' ' >> src/test/java/it/unimo/app/tools/Spacemr_email_manager__Test.java ;\
     gradle test  \
         --tests it.unimo.app.tools.Spacemr_email_manager__Test \
     ; cat build/test-results/test/binary/output.bin
     cat build/test-results/test/TEST-it.unimo.app.tools.Spacemr_email_manager__Test.xml
     ls build/test-results/test
   ec spacemr/src/test/java/it/unimo/app/tools/Spacemr_email_manager__Test.java
 */

public class Spacemr_email_manager__Test {
   
   @Test
   public void test_email_notifica_template() throws Exception {
      System.out.println(" hello Spacemr_email_manager__Test!");
      ApplicationContext ac = Application.getStaticContext();
      Tools tools = (Tools)ac.getBean("tools");
      Spacemr_email_manager spacemr_email_manager =
        (Spacemr_email_manager)ac.getBean("spacemr_email_manager");
      JSONObject spacemr_space_people_book =
         new JSONObject(
                        tools
                        .string_loadFromFile("src/test/java/it/unimo/app/tools/Spacemr_email_manager__Test.notifica.spacemr_space_people_book.json")
)
         ;
      System.out.println("spacemr_space_people_book: " + spacemr_space_people_book.toString(2));
      JSONObject spacemr_notifica =
         new JSONObject(
                        tools
                        .string_loadFromFile("src/test/java/it/unimo/app/tools/Spacemr_email_manager__Test.notifica.spacemr_notifica.json")
)
         ;
      System.out.println("spacemr_notifica: " + spacemr_notifica.toString(2));
      //-
      String template =
         tools
         .string_loadFromFile("src/test/java/it/unimo/app/tools/Spacemr_email_manager__Test.template.ftl")
         ;
      //-
      System.out.println("template\n" + template);
      //-
      HashMap<String, Object> root = new HashMap<String, Object>();
      root.put("spacemr_space_people_book", spacemr_space_people_book);
      root.put("spacemr_notifica", spacemr_notifica);
      root.put("tools",   tools);
      //-
      String to      = "alberto.corni@unimore.it";
      String subject = "Spacemr_email_manager__Test output";
      //-
      // spacemr_email_manager.doSendEmailHtmlTemplate(to, subject, template, root);
      String out = spacemr_email_manager.applyTemplate(template, root);
      System.out.println("out\n" + out);
      //-
   }
   
}
