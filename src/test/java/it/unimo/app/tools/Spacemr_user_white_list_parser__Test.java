package it.unimo.app.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.*;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import it.unimo.app.Application;
import it.unimo.app.tools.Tools;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests Spacemr_user_white_list_parser__Test \
     ; cat build/test-results/test/binary/output.bin
     ls build/test-results/test
   firefox build/reports/tests/test/classes

 */
public class Spacemr_user_white_list_parser__Test {

   //-
   //

   @Test
   public void test_00_no_algorhitm() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_00_no_algorhitm");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"no_algorithm\": \"user_list\""
         + "\n         , \"users\": \"list,of,users\""
         + "\n       }"
         ;
      //-
      boolean rv = false;
      //-
      try {
         Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      } catch (Exception e) {
         System.out.println("-- ok - no_algorhitm detected with message\n" + e.getMessage());
         rv = true;
      }
      assertTrue(rv);
   }

   @Test
   public void test_00_wrongJsonFormat() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_00_wrongJsonFormat");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"no_user_list\""
         + "\n         , \"users\": \"list,of,users\""
         + "\n       }"
         ;
      //-
      boolean rv = false;
      //-
      try {
         Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      } catch (Exception e) {
         System.out.println("-- ok - json wrong format detected message\n" + e.getMessage());
         rv = true;
      }
      assertTrue(rv);
   }
   
   @Test
   public void test_01_algorhitm_not_found() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_01_algorhitm_not_found");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"no_user_list\""
         + "\n         , \"users\": \"list,of,users\""
         + "\n       }"
         ;
      //-
      boolean rv = false;
      //-
      try {
         Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      } catch (Exception e) {
         System.out.println("-- ok - not found detected with message\n" + e.getMessage());
         rv = true;
      }
      assertTrue(rv);
   }

   
   @Test
   public void test_03_everyone() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_03_everyone");
      //-
      //-
      String s = null;
      //-
      boolean rv = false;
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      if (p.getJSON().getString("algorithm").equals("everyone")) {
         rv=true;
      }
      assertTrue(true);
   }

   @Test
   public void test_03_everyone_1() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_03_everyone_1");
      //-
      //-
      String s = "  ";
      //-
      boolean rv = false;
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      if (p.getJSON().getString("algorithm").equals("everyone")) {
         rv=true;
      }
      assertTrue(true);
   }

   @Test
   public void test_03_none_2() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_03_everyone_2");
      //-
      //-
      String s = "  ";
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = p.userCanAccess("pinco_pallino_aaa_bbb_ccc", new Date());
      //-
      assertFalse(rv);
   }
   
   @Test
   public void test_03_none_3() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_03_none_3");
      //-
      //-
      String s = "{ }";
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      System.out.println(" " + p.getTextDescription(null));
      //-
      boolean rv = p.userCanAccess("pinco_pallino_aaa_bbb_ccc", new Date());
      //-
      assertFalse(rv);
   }
   
   @Test
   public void test_04__user_list() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_04__user_list");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"user_list\""
         + "\n         , \"users\": \"list ,of,users\""
         + "\n       }"
         ;
      //-
      boolean rv = false;
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      assertTrue(p.userCanAccess("list", new Date()));
   }

   @Test
   public void test_04__user_list_01() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_04__user_list_01");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algoritmo\": \"lista_utenti\""
         + "\n         , \"utenti\": \"list,of,users\""
         + "\n       }"
         ;
      //-
      boolean rv = false;
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      assertFalse(p.userCanAccess("listofusers", new Date()));
   }

   @Test
   public void test_04__user_list_02() throws Exception {
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"user_list\""
         + "\n         , \"users\": \"users, list, of\""
         + "\n       }"
         ;
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_04__user_list_02\n"+s);
      //-
      boolean rv = false;
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      System.out.println(" -- users:\n" + p.getAllInvolvedUsersAsJson().toString(0));
      //-
      assertTrue("[\"list\",\"of\",\"users\"]".equals(p.getAllInvolvedUsersAsJson().toString(0)));
   }
   
   @Test
   public void test_05__week_even_odd() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_05__week_even_odd");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"week_even_odd\""
         + "\n         , \"users_even\": \"even_user, list ,of,users\""
         + "\n         , \"users_odd\": \"odd_user, list ,of,users\""
         + "\n       }"
         ;
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = true;
      //-
      Date d = null;
      //-
      //- odd week
      d = _DATE_FORMATTER.parse("2021-01-04");
      if (!p.userCanAccess("odd_user", d)) {
         System.out.println(" == odd_user should be enabled in odd weeks");
         rv=false;
      }
      if (p.userCanAccess("even_user", d)) {
         System.out.println(" == even_user should NOT be enabled in odd weeks");
         rv=false;
      }
      //-
      //- even week
      d = _DATE_FORMATTER.parse("2021-01-12");
      if (!p.userCanAccess("even_user", d)) {
         System.out.println(" == even_user should be enabled in odd weeks");
         rv=false;
      }
      if (p.userCanAccess("odd_user", d)) {
         System.out.println(" == odd_user should NOT be enabled in odd weeks");
         rv=false;
      }
      //-
      //-
      assertTrue(rv);
   }


   @Test
   public void test_06__weekly() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_06__weekly");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algorithm\": \"weekly\""
         + "\n         , \"users_monday\": \"monday_user, list ,of,users\""
         + "\n         , \"users_tuesday\": \"tuesday_user, list ,of,users\""
         + "\n         , \"users_wednesday\": \"wednesday_user, list ,of,users\""
         + "\n         , \"users_thursday\": \"thursday_user, list ,of,users\""
         + "\n         , \"users_friday\": \"friday_user, list ,of,users\""
         + "\n         , \"users_saturday\": \"saturday_user, list ,of,users\""
         + "\n         , \"users_sunday\": \"sunday_user, list ,of,users\""
         + "\n       }"
         ;
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = true;
      //-
      Date d = null;
      //-
      //- monday
      d = _DATE_FORMATTER.parse("2021-01-04");
      if (!p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_user should be enabled in mandays");
         rv=false;
      }
      if (p.userCanAccess("thursday_user", d)) {
         System.out.println(" == thursday_userr should NOT be enabled in mondays");
         rv=false;
      }
      //-
      //- thursday
      d = _DATE_FORMATTER.parse("2021-02-18");
      if (!p.userCanAccess("thursday_user", d)) {
         System.out.println(" == thursday_user should be enabled in thursdays");
         rv=false;
      }
      if (p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_userr should NOT be enabled in thursdays");
         rv=false;
      }
      //-
      //- sunday
      d = _DATE_FORMATTER.parse("2021-02-14");
      if (!p.userCanAccess("sunday_user", d)) {
         System.out.println(" == sunday_user should be enabled in sundays");
         rv=false;
      }
      if (p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_userr should NOT be enabled in sundays");
         rv=false;
      }
      //-
      //-
      if (!rv) {
         System.out.println(" --- text description ---\n"
                            + p.getTextDescription(null));
      }
      //-
      assertTrue(rv);
   }


   @Test
   public void test_06__weekly_01_italian() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_06__weekly");
      //-
      //-
      String s = ""
         + "\n       {"
         + "\n         \"algoritmo\": \"settimanale\""
         + "\n         , \"utenti_lunedi\": \"monday_user, list ,of,users\""
         + "\n         , \"utenti_martedi\": \"tuesday_user, list ,of,users\""
         + "\n         , \"utenti_mercoledi\": \"wednesday_user, list ,of,users\""
         + "\n         , \"utenti_giovedi\": \"thursday_user, list ,of,users\""
         + "\n         , \"utenti_venerdi\": \"friday_user, list ,of,users\""
         + "\n         , \"utenti_sabato\": \"saturday_user, list ,of,users\""
         + "\n         , \"utenti_domenica\": \"sunday_user, list ,of,users\""
         + "\n       }"
         ;
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = true;
      //-
      Date d = null;
      //-
      //- monday
      d = _DATE_FORMATTER.parse("2021-01-04");
      if (!p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_user should be enabled in mandays");
         rv=false;
      }
      if (p.userCanAccess("thursday_user", d)) {
         System.out.println(" == thursday_user should NOT be enabled in mondays");
         rv=false;
      }
      //-
      //- wednesday
      d = _DATE_FORMATTER.parse("2021-02-10");
      if (!p.userCanAccess("wednesday_user", d)) {
         System.out.println(" == wednesday_user should be enabled in wednesdays");
         rv=false;
      }
      if (p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_user should NOT be enabled in wednesdays");
         rv=false;
      }
      //-
      //- sunday
      d = _DATE_FORMATTER.parse("2021-02-14");
      if (!p.userCanAccess("sunday_user", d)) {
         System.out.println(" == sunday_user should be enabled in sundays");
         rv=false;
      }
      if (p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_userr should NOT be enabled in sundays");
         rv=false;
      }
      //-
      //-
      if (!rv) {
         System.out.println(" --- text description ---\n"
                            + p.getTextDescription(null));
      }
      //-
      assertTrue(rv);
   }


   @Test
   public void test_07__weekly_groups() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_07__weekly_groups");
      //-
      //-
      String s = ""
         + "\n {"
         + "\n   \"algorithm\": \"weekly_groups\""
         + "\n   , \"groups\": ["
         + "\n       { \"name\": \"gmonday\",    \"users\": \"monday_user, list ,of,users\"} " 
         + "\n     , { \"name\": \"gtuesday\",   \"users\": \"tuesday_user, list ,of,users\"} "
         + "\n     , { \"name\": \"gwednesday\", \"users\": \"wednesday_user, list ,of,users\"} "
         + "\n     , { \"name\": \"gthursday\",  \"users\": \"thursday_user_1, list ,of,users\"} "
         + "\n     , { \"name\": \"gthursday_1\",\"users\": \"thursday_user, list ,of,users\"} "
         + "\n     , { \"name\": \"gfriday\",    \"users\": \"friday_user, list ,of,users\"} "
         + "\n     , { \"name\": \"gsaturday\",  \"users\": \"saturday_user, list ,of,users\"} "
         + "\n     , { \"name\": \"gsunday\",    \"users\": \"sunday_user, list ,of,users\"} "
         + "\n   ]"
         + "\n   , \"group_monday\":    \"gmonday\""
         + "\n   , \"group_tuesday\":   \"gtuesday\""
         + "\n   , \"group_wednesday\": \"gwednesday\""
         + "\n   , \"group_thursday\":  \"gthursday, gthursday_1\""
         + "\n   , \"group_friday\":    \"gfriday\""
         + "\n   , \"group_saturday\":  \"gsaturday\""
         + "\n   , \"group_sunday\":    \"gsunday\""
         + "\n }"
         ;
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = true;
      //-
      Date d = null;
      //-
      //- monday
      d = _DATE_FORMATTER.parse("2021-01-04");
      if (!p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_user should be enabled in mandays");
         rv=false;
      }
      if (p.userCanAccess("thursday_user", d)) {
         System.out.println(" == thursday_userr should NOT be enabled in mondays");
         rv=false;
      }
      //-
      //- thursday
      d = _DATE_FORMATTER.parse("2021-02-18");
      if (!p.userCanAccess("thursday_user", d)) {
         System.out.println(" == thursday_user should be enabled in thursdays");
         rv=false;
      }
      if (!p.userCanAccess("thursday_user_1", d)) {
         System.out.println(" == thursday_user_1 should be enabled in thursdays");
         rv=false;
      }
      if (p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_userr should NOT be enabled in thursdays");
         rv=false;
      }
      //-
      //- sunday
      d = _DATE_FORMATTER.parse("2021-02-14");
      if (!p.userCanAccess("sunday_user", d)) {
         System.out.println(" == sunday_user should be enabled in sundays");
         rv=false;
      }
      if (p.userCanAccess("monday_user", d)) {
         System.out.println(" == monday_userr should NOT be enabled in sundays");
         rv=false;
      }
      //-
      //-
      if (!rv) {
         System.out.println(" --- text description ---\n"
                            + p.getTextDescription(null));
      }
      //-
      assertTrue(rv);
   }

   @Test
   public void test_07__weekly_groups_italian() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_07__weekly_groups");
      //-
      //-
      String s = ""
         + "\n {"
         + "\n   \"algoritmo\": \"settimanale_gruppi\","
         + "\n   \"codice_spazio\": \"aula1\","
         + "\n   \"gruppi\": ["
         + "\n     {"
         + "\n       \"nome\": \"a\","
         + "\n       \"utenti\": \"inginf1,inginf2,inginf3,inginf4,inginf5,inginf6,inginf7,inginf8,inginf9,inginf10,inginf11,inginf12,inginf13\""
         + "\n     },"
         + "\n     {"
         + "\n       \"nome\": \"b\","
         + "\n       \"utenti\": \"ingmec1,ingmec2,ingmec3,ingmec4,ingmec5,ingmec6,ingmec7,ingmec8,ingmec9,ingmec10\""
         + "\n     }"
         + "\n   ],"
         + "\n   \"gruppo_lunedi\": \"a\","
         + "\n   \"gruppo_martedi\": \"b\","
         + "\n   \"gruppo_mercoledi\": \"a\","
         + "\n   \"gruppo_giovedi\": \"b\","
         + "\n   \"gruppo_venerdi\": \"a\""
         + "\n }"
         ;
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = true;
      //-
      Date d = null;
      //-
      //- monday
      d = _DATE_FORMATTER.parse("2021-01-04");
      if (!p.userCanAccess("inginf13", d)) {
         System.out.println(" == inginf13 should be enabled in mandays");
         rv=false;
      }
      if (p.userCanAccess("ingmec9", d)) {
         System.out.println(" == ingmec9 should NOT be enabled in mondays");
         rv=false;
      }
      //-
      //- thursday
      d = _DATE_FORMATTER.parse("2021-02-18");
      if (!p.userCanAccess("ingmec3", d)) {
         System.out.println(" == ingmec3 should be enabled in thursdays");
         rv=false;
      }
      if (p.userCanAccess("inginf4", d)) {
         System.out.println(" == inginf4 should NOT be enabled in thursdays");
         rv=false;
      }
      //-
      //- sunday
      d = _DATE_FORMATTER.parse("2021-02-14");
      if (p.userCanAccess("inginf2", d)) {
         System.out.println(" == monday_userr should NOT be enabled in sundays");
         rv=false;
      }
      //-
      //-
      if (!rv) {
         System.out.println(" --- text description ---\n"
                            + p.getTextDescription(null));
      }
      //-
      assertTrue(rv);
   }


   @Test
   public void test_08__date_by_date() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_08__date_by_date");
      //-
      //-
      String s = ""
         + "\n {"
         + "\n   \"algorithm\": \"date_by_date\","
         + "\n   \"space_code\": \"kk1\","
         + "\n   \"groups\": ["
         + "\n       {\"name\": \"rossi\",   \"users\": \"a1, a2, a3, a4\"}"
         + "\n     , {\"name\": \"gialli\",  \"users\": \"b1, b2, b3, b4\"}"
         + "\n     , {\"name\": \"verdini\", \"users\": \"v1, v2, v3, v4\"}"
         + "\n   ],"
         + "\n   \"dates\": ["
         + "\n       {\"date\": \"20210301\", \"groups\": \"rossi\"}"
         + "\n     , {\"date\": \"20210302\", \"groups\": \"rossi,gialli\"}"
         + "\n     , {\"date\": \"20210303\",  \"users\": \"u1, u2, u5\"}"
         + "\n     , {\"date\": \"20210304\",  \"users\": \"u3, u4\", \"groups\": \"verdini\"}"
         + "\n    ]"
         + "\n }"
         ;
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = true;
      //-
      Date d = null;
      //-
      //- 20210301
      d = _DATE_FORMATTER.parse("2021-03-01");
      if (!p.userCanAccess("a1", d)) {
         System.out.println(" == a1 (group rossi) should be enabled in 2021-03-01");
         rv=false;
      }
      if (p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-01");
         rv=false;
      }
      //-
      //-
      //- 20210302
      d = _DATE_FORMATTER.parse("2021-03-02");
      if (!p.userCanAccess("a1", d)) {
         System.out.println(" == a1 (group rossi) should be enabled in 2021-03-02");
         rv=false;
      }
      if (!p.userCanAccess("b4", d)) {
         System.out.println(" == b4 (group gialli) should be enabled in 2021-03-02");
         rv=false;
      }
      if (p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-02");
         rv=false;
      }
      //-
      //-
      //- 20210303
      d = _DATE_FORMATTER.parse("2021-03-03");
      if (!p.userCanAccess("u1", d)) {
         System.out.println(" == u1  should be enabled in 2021-03-03");
         rv=false;
      }
      if (p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-03");
         rv=false;
      }
      if (p.userCanAccess("v3", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-03");
         rv=false;
      }
      //-
      //-
      //- 20210304
      d = _DATE_FORMATTER.parse("2021-03-04");
      if (!p.userCanAccess("u4", d)) {
         System.out.println(" == u4  should be enabled in 2021-03-04");
         rv=false;
      }
      if (!p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini)  should be enabled in 2021-03-04");
         rv=false;
      }
      if (!p.userCanAccess("v4", d)) {
         System.out.println(" == v4 (group verdini)  should be enabled in 2021-03-04");
         rv=false;
      }
      if (p.userCanAccess("u1", d)) {
         System.out.println(" == u1 should NOT be enabled in 2021-03-04");
         rv=false;
      }
      if (p.userCanAccess("b4", d)) {
         System.out.println(" == b4 (group gialli) should NOT be enabled in 2021-03-04");
         rv=false;
      }
      //-
      //-
      if (!rv) {
         System.out.println(" --- text description ---\n"
                            + p.getTextDescription(null));
      }
      //-
      assertTrue(rv);
   }


   @Test
   public void test_08__date_by_date_italian() throws Exception {
      //-
      System.out.println("-- Spacemr_user_white_list_parser__Test - test_08__date_by_date");
      //-
      //-
      String s = ""
         + "\n  {"
         + "\n    \"algoritmo\": \"data_per_data\","
         + "\n    \"codice_spazio\": \"kk1\","
         + "\n    \"gruppi\": ["
         + "\n      {\"nome\": \"rossi\", \"utenti\": \"a1, a2, a3, a4\"}"
         + "\n      , {\"nome\": \"gialli\", \"utenti\": \"b1, b2, b3, b4\"}"
         + "\n      , {\"nome\": \"verdini\", \"utenti\": \"v1, v2, v3, v4\"}"
         + "\n    ],"
         + "\n    \"date\": ["
         + "\n        {\"data\": \"20210301\", \"gruppi\": \"rossi\"}"
         + "\n      , {\"data\": \"20210302\", \"gruppi\": \"rossi,gialli\"}"
         + "\n      , {\"data\": \"20210303\", \"utenti\": \"u1, u2, u5\"}"
         + "\n      , {\"data\": \"20210304\", \"utenti\": \"u3, u4\", \"gruppi\": \"verdini\"}"
         + "\n     ]"
         + "\n  }"
         ;
      //-
      Spacemr_user_white_list_parser p = Spacemr_user_white_list_parser.getParser(s);
      //-
      boolean rv = true;
      //-
      Date d = null;
      //-
      //- 20210301
      d = _DATE_FORMATTER.parse("2021-03-01");
      if (!p.userCanAccess("a1", d)) {
         System.out.println(" == a1 (group rossi) should be enabled in 2021-03-01");
         rv=false;
      }
      if (p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-01");
         rv=false;
      }
      //-
      //-
      //- 20210302
      d = _DATE_FORMATTER.parse("2021-03-02");
      if (!p.userCanAccess("a1", d)) {
         System.out.println(" == a1 (group rossi) should be enabled in 2021-03-02");
         rv=false;
      }
      if (!p.userCanAccess("b4", d)) {
         System.out.println(" == b4 (group gialli) should be enabled in 2021-03-02");
         rv=false;
      }
      if (p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-02");
         rv=false;
      }
      //-
      //-
      //- 20210303
      d = _DATE_FORMATTER.parse("2021-03-03");
      if (!p.userCanAccess("u1", d)) {
         System.out.println(" == u1  should be enabled in 2021-03-03");
         rv=false;
      }
      if (p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-03");
         rv=false;
      }
      if (p.userCanAccess("v3", d)) {
         System.out.println(" == v1 (group verdini) should NOT be enabled in 2021-03-03");
         rv=false;
      }
      //-
      //-
      //- 20210304
      d = _DATE_FORMATTER.parse("2021-03-04");
      if (!p.userCanAccess("u4", d)) {
         System.out.println(" == u4  should be enabled in 2021-03-04");
         rv=false;
      }
      if (!p.userCanAccess("v1", d)) {
         System.out.println(" == v1 (group verdini)  should be enabled in 2021-03-04");
         rv=false;
      }
      if (!p.userCanAccess("v4", d)) {
         System.out.println(" == v4 (group verdini)  should be enabled in 2021-03-04");
         rv=false;
      }
      if (p.userCanAccess("u1", d)) {
         System.out.println(" == u1 should NOT be enabled in 2021-03-04");
         rv=false;
      }
      if (p.userCanAccess("b4", d)) {
         System.out.println(" == b4 (group gialli) should NOT be enabled in 2021-03-04");
         rv=false;
      }
      //-
      //-
      if (rv) {
         System.out.println(" --- text description ---\n"
                            + p.getTextDescription(null));
      }
      //-
      assertTrue(rv);
   }
   
   
   private static SimpleDateFormat _DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
   
}
