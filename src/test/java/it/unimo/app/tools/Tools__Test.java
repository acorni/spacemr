package it.unimo.app.tools;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.*;
import static org.junit.Assert.*;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests Tools__Test \
     && cat build/test-results/test/binary/output.bin
     ls build/test-results/test

   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests Tools__Test \
     && cat build/test-results/test/binary/output.bin
     ls build/test-results/test
     cat build/test-results/test/TEST-it.unimo.app.tools.Tools__Test.xml

 */
public class Tools__Test {

   private Tools tools = new Tools();

   @Test
   public void test_bas64() {
      System.out.println(" ----- test_base64");
      String startString;
      startString = "questaE'LaProva";
      System.out.println(" -- startString: " + startString);
      String base64Encoded = tools.base64Encode(startString.getBytes());
      System.out.println(" -- base64Encoded: " + base64Encoded);
      String base64Decoded = new String(tools.base64Decode(base64Encoded));
      System.out.println(" -- base64Decoded: " + base64Decoded);
      assertEquals(startString, base64Decoded);
   }

   @Test
   public void test_password_generation() throws Exception {
      System.out.println(" ----- test_password_generation");
      String thePassword = "thePassword";
      System.out.println(" -- thePassword: " + thePassword);
      byte[] random = new byte[20];
      new Random().nextBytes(random);
      String theSalt = tools.base64Encode(random);
      //-
      System.out.println(" -- theSalt: " + theSalt
                         + " len: " + theSalt.length()
                         );
      String encoded1 = tools.stringMd5ToBase64(thePassword+theSalt);
      System.out.println(" -- encoded1: " + encoded1
                         + " len: " + encoded1.length()
                         + " THIS IS THE recorded PASSWORD (with the salt)"
                         );
      //-
      String encoded2 = tools.stringMd5ToBase64(thePassword+theSalt);
      System.out.println(" -- encoded2: " + encoded2);
      assertEquals(encoded1, encoded2);
   }


   @Test
   public void jsonObject_clone_test() throws Exception {
      String s = ""
         + "\n {"
         + "\n   'spacemr_responsible_id': '435',"
         + "\n   'reason': 'Ricerca',"
         + "\n   'stato': 'autorizzata',"
         + "\n   'spacemr_people_id': '435',"
         + "\n   'spacemr_space_id': '209',"
         + "\n   'people_number': '1',"
         + "\n   'date_to': 1601589600000,"
         + "\n   'repetition': 'd',"
         + "\n   'spacemr_space_people_book_id': '1972',"
         + "\n   'nota': 'ebbri - 20200928:140456 -  - autorizzata',"
         + "\n   'date_from': 1601244000000"
         + "\n }"
         ;
      System.out.println("s:\n"+s+"\n");
      JSONObject o  = new JSONObject(s);
      JSONObject o1 = tools.jsonObject_clone(o);
      //-
      JSONObject di = tools.jsonChanges(o, o1);
      assertTrue(di == null);
      //-
      o1.put("nota", "ciao");
      di = tools.jsonChanges(o, o1);
      String dis = di.toString(2);
      System.out.println(" dis: " + dis);
      assertEquals("{\"changed\": {\"nota\": \"ciao\"}}", dis);
   }

}
