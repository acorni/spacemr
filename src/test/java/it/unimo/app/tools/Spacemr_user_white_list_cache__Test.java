package it.unimo.app.tools;

import java.util.Date;
import org.junit.*;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import it.unimo.app.Application;
import it.unimo.app.tools.Tools;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests Spacemr_user_white_list_cache__Test \
     ; cat build/test-results/test/binary/output.bin
     ls build/test-results/test

 */
public class Spacemr_user_white_list_cache__Test {
   
   @Test
   public void test_00_hello() throws Exception {
      System.out.println(" --- hello cache -- ");
      ApplicationContext ac = Application.getStaticContext();
      Spacemr_user_white_list_cache cache =
         (Spacemr_user_white_list_cache)ac.getBean("spacemr_user_white_list_cache");
      assert(true);
   }
   
   @Test
   public void test_00_users_info() throws Exception {
      ApplicationContext ac = Application.getStaticContext();
      Spacemr_user_white_list_cache spacemr_user_white_list_cache =
         (Spacemr_user_white_list_cache)ac.getBean("spacemr_user_white_list_cache");
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"user_list\""
         + "\n         , \"users\": \"acorni,fguerra,list,of,users,tatttartarttrrnotexisting\""
         + "\n       }"
         ;
      //-
      boolean rv = false;
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      System.out.println("--info--\n"
                         + spacemr_user_white_list_cache.allInvolvedUsersInfoAsJson(p)
                         .toString(2)
                         );
      //-
      assert(true);
   }

   @Test
   public void test_01_text_description_user_list() throws Exception {
      ApplicationContext ac = Application.getStaticContext();
      Spacemr_user_white_list_cache spacemr_user_white_list_cache =
         (Spacemr_user_white_list_cache)ac.getBean("spacemr_user_white_list_cache");
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"user_list\""
         + "\n         , \"users\": \"acorni,fguerra,list,of,users,tatttartarttrrnotexisting\""
         + "\n       }"
         ;
      //-
      boolean rv = false;
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      System.out.println("--info--\n"
                         + spacemr_user_white_list_cache.allInvolvedUsersInfoAsJson(p)
                         .toString(2)
                         );
      String text_description =
         p.getTextDescription(spacemr_user_white_list_cache.allInvolvedUsersInfo(p));
      System.out.println(" --- text_description ---\n" + text_description + "\n");
      //-
      assert(true);
   }
   
}
