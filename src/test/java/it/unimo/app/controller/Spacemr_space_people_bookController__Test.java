package it.unimo.app.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import org.junit.*;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import it.unimo.app.Application;
import it.unimo.app.tools.Tools;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests Spacemr_space_people_bookController__Test \
     && cat build/test-results/test/binary/output.bin
     ls build/test-results/test
     cat build/test-results/test/TEST-it.unimo.app.controller.Spacemr_space_people_bookController__Test.xml
 */

public class Spacemr_space_people_bookController__Test {
   
   @Test
   public void test_00_daily_repetitions() throws Exception {
      Spacemr_space_people_bookController spacemr_space_people_bookController = 
         (Spacemr_space_people_bookController)Application.getStaticContext()
         .getBean(Class.forName("it.unimo.app.controller.Spacemr_space_people_bookController"));
      //-
      JSONObject params = null;
      String out = null;
      String outExpected = null;
      //-
      //- cut at the start of the period
      //-
      params = getParamsDaily();
      params.put("date_cut_from", gd("2020-09-21"));
      params.put("date_cut_to"  , gd("2020-09-22"));
      //-
      System.out.println(" ========  DAILY REPETITION ====== \n");
      //-
      System.out.println("--------- cut at the start of the period :\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nrepetition: d"
         + "\ndate_from : 2020-09-23 Wed"
         + "\ndate_to : 2020-09-25 Fri"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-21 Mon"
         + "\n- cut_date_to : 2020-09-22 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //- cut at the end of the period 
      //-
      params = getParamsDaily();
      params.put("date_cut_from", gd("2020-09-24"));
      params.put("date_cut_to"  , gd("2020-09-25"));
      //-
      System.out.println("--------- cut at the end of the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nrepetition: d"
         + "\ndate_from : 2020-09-21 Mon"
         + "\ndate_to : 2020-09-23 Wed"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-24 Thu"
         + "\n- cut_date_to : 2020-09-25 Fri"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //- cut before the period 
      //-
      params = getParamsDaily();
      params.put("date_cut_from", gd("2020-09-20"));
      params.put("date_cut_to"  , gd("2020-09-20"));
      //-
      System.out.println("--------- cut before the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-20 Sun"
         + "\n- cut_date_to : 2020-09-20 Sun"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- cut after the period 
      //-
      params = getParamsDaily();
      params.put("date_cut_from", gd("2020-09-26"));
      params.put("date_cut_to"  , gd("2020-09-26"));
      //-
      System.out.println("--------- cut after the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-26 Sat"
         + "\n- cut_date_to : 2020-09-26 Sat"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- cut wider than the period 
      //-
      params = getParamsDaily();
      params.put("date_cut_from", gd("2020-09-20"));
      params.put("date_cut_to"  , gd("2020-09-26"));
      //-
      System.out.println("--------- cut wider than period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-20 Sun"
         + "\n- cut_date_to : 2020-09-26 Sat"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- cut inside the period 
      //-
      params = getParamsDaily();
      params.put("date_cut_from", gd("2020-09-23"));
      params.put("date_cut_to"  , gd("2020-09-24"));
      //-
      System.out.println("--------- cut inside the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nrepetition: d"
         + "\ndate_from : 2020-09-21 Mon"
         + "\ndate_to : 2020-09-22 Tue"
         + "\n- objNew:"
         + "\nrepetition: d"
         + "\ndate_from : 2020-09-25 Fri"
         + "\ndate_to : 2020-09-25 Fri"
         + "\n- cut_date_from : 2020-09-23 Wed"
         + "\n- cut_date_to : 2020-09-24 Thu"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
   }
   private JSONObject getParamsDaily() throws Exception {
      JSONObject obj    = new JSONObject();
      obj.put("repetition", "d");
      obj.put("date_from", gd("2020-09-21"));
      obj.put("date_to",   gd("2020-09-25"));
      //-
      JSONObject objNew = null;
      //-
      JSONObject params = new JSONObject();
      params.put("obj",    obj);
      params.put("objNew", (JSONObject)null);
      //-
      return(params);
   }

   @Test
   public void test_01_weekly_repetitions() throws Exception {
      Spacemr_space_people_bookController spacemr_space_people_bookController = 
         (Spacemr_space_people_bookController)Application.getStaticContext()
         .getBean(Class.forName("it.unimo.app.controller.Spacemr_space_people_bookController"));
      //-
      JSONObject params = null;
      String out = null;
      String outExpected = null;
      //-
      //- cut at the start of the period 2020-09-22 - 2020-10-20
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-09-22"));
      params.put("date_cut_to"  , gd("2020-09-29"));
      //-
      System.out.println(" ========  WEEKLY REPETITION ====== \n");
      //-
      System.out.println("--------- cut at the start of the period :\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nrepetition: w"
         + "\ndate_from : 2020-10-06 Tue"
         + "\ndate_to : 2020-10-20 Tue"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-22 Tue"
         + "\n- cut_date_to : 2020-09-29 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //- cut at the end of the period 
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-10-13"));
      params.put("date_cut_to"  , gd("2020-10-20"));
      //-
      System.out.println("--------- cut at the end of the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nrepetition: w"
         + "\ndate_from : 2020-09-22 Tue"
         + "\ndate_to : 2020-10-06 Tue"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-10-13 Tue"
         + "\n- cut_date_to : 2020-10-20 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //- cut before the period 
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-09-01"));
      params.put("date_cut_to"  , gd("2020-09-08"));
      //-
      System.out.println("--------- cut before the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-01 Tue"
         + "\n- cut_date_to : 2020-09-08 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- cut after the period 
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-10-27"));
      params.put("date_cut_to"  , gd("2020-10-27"));
      //-
      System.out.println("--------- cut after the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-10-27 Tue"
         + "\n- cut_date_to : 2020-10-27 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- cut wider than the period 
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-09-01"));
      params.put("date_cut_to"  , gd("2020-10-27"));
      //-
      System.out.println("--------- cut wider than period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-09-01 Tue"
         + "\n- cut_date_to : 2020-10-27 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- cut inside the period 
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-10-06"));
      params.put("date_cut_to"  , gd("2020-10-13"));
      //-
      System.out.println("--------- cut inside the period:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nrepetition: w"
         + "\ndate_from : 2020-09-22 Tue"
         + "\ndate_to : 2020-09-29 Tue"
         + "\n- objNew:"
         + "\nrepetition: w"
         + "\ndate_from : 2020-10-20 Tue"
         + "\ndate_to : 2020-10-20 Tue"
         + "\n- cut_date_from : 2020-10-06 Tue"
         + "\n- cut_date_to : 2020-10-13 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- not right day of week from
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-10-07"));
      params.put("date_cut_to"  , gd("2020-10-13"));
      //-
      System.out.println("--------- not right day of week from:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-10-07 Wed"
         + "\n- cut_date_to : 2020-10-13 Tue"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
      //-
      //- not right day of week to
      //-
      params = getParamsWeekly();
      params.put("date_cut_from", gd("2020-10-06"));
      params.put("date_cut_to"  , gd("2020-10-14"));
      //-
      System.out.println("--------- not right day of week to:\n" + printParams(params));
      spacemr_space_people_bookController.spacemr_space_people_book_cut_date_doCut(params);
      out = printParams(params);
      outExpected=""
         + "- obj:"
         + "\nnull"
         + "\n- objNew:"
         + "\nnull"
         + "\n- cut_date_from : 2020-10-06 Tue"
         + "\n- cut_date_to : 2020-10-14 Wed"
         ;
      System.out.println("--------- out\n"+ out);
      Assert.assertTrue(outExpected.equals(out));
      //-
   }
   private JSONObject getParamsWeekly() throws Exception {
      JSONObject obj    = new JSONObject();
      obj.put("repetition", "w");
      obj.put("date_from", gd("2020-09-22"));
      obj.put("date_to",   gd("2020-10-20"));
      //-
      JSONObject objNew = null;
      //-
      JSONObject params = new JSONObject();
      params.put("obj",    obj);
      params.put("objNew", (JSONObject)null);
      //-
      return(params);
   }

   
   private String printParams(JSONObject params) throws Exception {
      StringBuffer rv = new StringBuffer();
      if(params == null) {
         rv.append("null");
      } else {
         rv.append("- obj:\n" + toDateString(params.optJSONObject("obj")));
         rv.append("\n- objNew:\n" + toDateString(params.optJSONObject("objNew")));
         rv.append("\n- cut_date_from : " + _sdf1.format(new Date(params.getLong("date_cut_from"))));
         rv.append("\n- cut_date_to : " + _sdf1.format(new Date(params.getLong("date_cut_to"))));
      }
      return(rv.toString());
   }
   private String toDateString(JSONObject obj) throws Exception {
      StringBuffer rv = new StringBuffer();
      if(obj == null) {
         rv.append("null");
      } else {
         rv.append("repetition: " + obj.getString("repetition"));
         rv.append("\ndate_from : " + _sdf1.format(new Date(obj.getLong("date_from"))));
         rv.append("\ndate_to : " + _sdf1.format(new Date(obj.getLong("date_to"))));
      }
      return(rv.toString());
   }
   private long gd(String ds) throws Exception {
      return(_sdf.parse(ds).getTime());
   }
   private static SimpleDateFormat _sdf1 = new SimpleDateFormat("yyyy-MM-dd EEE");
   private static SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd");

}
