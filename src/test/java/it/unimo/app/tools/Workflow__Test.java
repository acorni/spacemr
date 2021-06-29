package it.unimo.app.tools;

import java.util.HashSet;
import java.util.Arrays;
import org.junit.*;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import it.unimo.app.Application;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.Workflow;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests Workflow__Test \
     && cat build/test-results/test/binary/output.bin
     ls build/test-results/test
 */
public class Workflow__Test {

   //-
   //
   
   @Test
   public void test_00() throws Exception {
      //-
      // System.out.println(" hello Workflow__Test!");
      //-
      //-
      String s = ""
         + "\n   {"
         + "\n       'defaultStatus': 'crea'"
         + "\n       , 'statuses': ["
         + "\n           {"
         + "\n               'name':'creata'"
         + "\n               , 'color':'#B0B0B0'"
         + "\n               , 'description': 'Richiesta creata'"
         + "\n               , 'steps':["
         + "\n                   {"
         + "\n                       'destination':'prop'"
         + "\n                       , 'description':'Invia richiesta'"
         + "\n                       , 'permission':'db_spacemr_space_people_book_read'"
         + "\n                   }"
         + "\n                   , {"
         + "\n                       'destination':'annullata'"
         + "\n                       , 'description':'Richiesta annullata'"
         + "\n                       , 'permission':'db_spacemr_space_people_book_read'"
         + "\n                   }"
         + "\n               ]"
         + "\n             }"
         + "\n           , {"
         + "\n               'name':'creata'"
         + "\n               , 'color':'#66ccff'"
         + "\n               , 'description':'Richiesta in approvazione'"
         + "\n               , 'steps':["
         + "\n                   {"
         + "\n                       'destination':'annullata'"
         + "\n                       , 'description':'Richiesta annullata'"
         + "\n                       , 'permission':'db_spacemr_space_people_book_update'"
         + "\n                   }"
         + "\n               ]"
         + "\n           }"
         + "\n       ]"
         + "\n   }"
         ;
      // System.out.println(s);
      boolean rv = false;
      HashSet<String> permissions =
         new HashSet<>(Arrays.asList("db_spacemr_space_people_book_read"));
      try {
         Workflow wf = new Workflow(new JSONObject(s), permissions);
      } catch (Exception e) {
         String m = "the status 'creata' is defined twice";
         // System.out.println("m: ["+m+"]");
         // System.out.println("e: ["+e.getMessage()+"]");
         if (m.equals(e.getMessage())){
            rv=true;
         }
      }
      assertTrue(rv);
   }

   @Test
   public void test_01() throws Exception {
      //-
      //-
      //-
      String s = ""
         + "\n   {"
         + "\n       'defaultStatus': 'crea'"
         + "\n       , 'statuses': ["
         + "\n           {"
         + "\n               'name':'creata'"
         + "\n               , 'color':'#B0B0B0'"
         + "\n               , 'description': 'Richiesta creata'"
         + "\n               , 'steps':["
         + "\n                   {"
         + "\n                       'destination':'prop'"
         + "\n                       , 'description':'Invia richiesta'"
         + "\n                       , 'permission':'db_spacemr_space_people_book_read'"
         + "\n                   }"
         + "\n                   , {"
         + "\n                       'destination':'annullata'"
         + "\n                       , 'description':'Richiesta annullata'"
         + "\n                       , 'permission':'db_spacemr_space_people_book_read'"
         + "\n                   }"
         + "\n               ]"
         + "\n             }"
         + "\n           , {"
         + "\n               'name':'prip'"
         + "\n               , 'color':'#66ccff'"
         + "\n               , 'description':'Richiesta in approvazione'"
         + "\n               , 'steps':["
         + "\n                   {"
         + "\n                       'destination':'annullata'"
         + "\n                       , 'description':'Richiesta annullata'"
         + "\n                       , 'permission':'db_spacemr_space_people_book_update'"
         + "\n                   }"
         + "\n               ]"
         + "\n           }"
         + "\n       ]"
         + "\n   }"
         ;
      // System.out.println(s);
      boolean rv = false;
      HashSet<String> permissions =
         new HashSet<>(Arrays.asList("db_spacemr_space_people_book_read"));
      try {
         Workflow wf = new Workflow(new JSONObject(s), permissions);
      } catch (Exception e) {
         String m = "Unknown destination status [prop] in status [creata]";
         // System.out.println("m: ["+m+"]");
         // System.out.println("e: ["+e.getMessage()+"]");
         if (m.equals(e.getMessage())){
            rv=true;
         }
      }
      assertTrue(rv);
   }


   @Test
   public void test_02() throws Exception {
      //-
      //- json output and default_per_field_permisson management
      //-
      String s = ""
         + "\n{"
         + "\n  'defaultStatus': 'crea'"
         + "\n  , 'default_per_field_permisson': {"
         + "\n      'spacemr_people_id': { 'default':'ro', 'rw': 'db_spacemr_space_people_book_update'}"
         + "\n      , 'spacemr_space_id':  { 'default':'ro'}"
         + "\n      , 'spacemr_test':      { 'default':'hidden'}"
         + "\n     }"
         + "\n  , 'statuses': ["
         + "\n      {"
         + "\n          'name':'creata'"
         + "\n          , 'color':'#B0B0B0'"
         + "\n          , 'description': 'Richiesta creata'"
         + "\n          , 'steps':["
         + "\n              {"
         + "\n                  'destination':'prop'"
         + "\n                  , 'description':'Invia richiesta'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_update'"
         + "\n              }"
         + "\n              , {"
         + "\n                  'destination':'annullata'"
         + "\n                  , 'description':'Richiesta annullata'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_admin'"
         + "\n              }"
         + "\n          ]"
         + "\n        }"
         + "\n      , {"
         + "\n          'name':'prop'"
         + "\n          , 'color':'#66ccff'"
         + "\n          , 'description':'Richiesta in approvazione'"
         + "\n          , 'steps':["
         + "\n              {"
         + "\n                  'destination':'annullata'"
         + "\n                  , 'description':'Richiesta annullata'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_update'"
         + "\n              }"
         + "\n          ]"
         + "\n      }"
         + "\n      , {"
         + "\n          'name':'annullata'"
         + "\n          , 'color':'#000000'"
         + "\n          , 'description':'Annullata'"
         + "\n          , 'steps':["
         + "\n              {"
         + "\n                  'destination':'creata'"
         + "\n                  , 'description':'Restart from create'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_admin'"
         + "\n              }"
         + "\n          ]"
         + "\n      }"
         + "\n  ]"
         + "\n}"
         ;
      // System.out.println(s);
      boolean rv = false;
      HashSet<String> permissions =
         new HashSet<>(Arrays.asList("db_spacemr_space_people_book_update"));
      Workflow wf = new Workflow(new JSONObject(s), permissions);
      String o = ""
         + "{"
         + "\n  'defaultStatus': 'crea',"
         + "\n  'statusList': ["
         + "\n    'creata',"
         + "\n    'prop',"
         + "\n    'annullata'"
         + "\n  ],"
         + "\n  'statusIndex': {"
         + "\n    'annullata': {"
         + "\n      'per_field_permission_canRead': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': true,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'color': '#000000',"
         + "\n      'per_field_permission_canWrite': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': false,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'name': 'annullata',"
         + "\n      'description': 'Annullata',"
         + "\n      'steps': [],"
         + "\n      'stepsIndex': {}"
         + "\n    },"
         + "\n    'prop': {"
         + "\n      'per_field_permission_canRead': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': true,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'color': '#66ccff',"
         + "\n      'per_field_permission_canWrite': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': false,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'name': 'prop',"
         + "\n      'description': 'Richiesta in approvazione',"
         + "\n      'steps': ['annullata'],"
         + "\n      'stepsIndex': {'annullata': {"
         + "\n        'destination': 'annullata',"
         + "\n        'description': 'Richiesta annullata',"
         + "\n        'permission': 'db_spacemr_space_people_book_update'"
         + "\n      }}"
         + "\n    },"
         + "\n    'creata': {"
         + "\n      'per_field_permission_canRead': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': true,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'color': '#B0B0B0',"
         + "\n      'per_field_permission_canWrite': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': false,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'name': 'creata',"
         + "\n      'description': 'Richiesta creata',"
         + "\n      'steps': ['prop'],"
         + "\n      'stepsIndex': {'prop': {"
         + "\n        'destination': 'prop',"
         + "\n        'description': 'Invia richiesta',"
         + "\n        'permission': 'db_spacemr_space_people_book_update'"
         + "\n      }}"
         + "\n    }"
         + "\n  }"
         + "\n}"
         ;
      o = o.replace('\'', '"');
      if (o.equals(wf.toJson().toString(2))) {
         rv = true;
      } else {
         System.out.println("--- wf.toJson():\n"+wf.toJson().toString(2));
         System.out.println("-- expected json:\n"+o);
      }
      assertTrue(rv);
   }


   @Test
   public void test_03() throws Exception {
      //-
      //- per_field_permisson management
      //-
      String s = ""
         + "\n{"
         + "\n  'defaultStatus': 'crea'"
         + "\n  , 'default_per_field_permisson': {"
         + "\n      'spacemr_people_id': { 'default':'ro', 'rw': 'db_spacemr_space_people_book_update'}"
         + "\n      , 'spacemr_space_id':  { 'default':'ro'}"
         + "\n      , 'spacemr_test':      { 'default':'hidden'}"
         + "\n     }"
         + "\n  , 'statuses': ["
         + "\n      {"
         + "\n          'name':'creata'"
         + "\n          , 'color':'#B0B0B0'"
         + "\n          , 'description': 'Richiesta creata'"
         + "\n          , 'steps':["
         + "\n              {"
         + "\n                  'destination':'prop'"
         + "\n                  , 'description':'Invia richiesta'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_update'"
         + "\n              }"
         + "\n              , {"
         + "\n                  'destination':'annullata'"
         + "\n                  , 'description':'Richiesta annullata'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_admin'"
         + "\n              }"
         + "\n          ]"
         + "\n          , 'per_field_permisson': {"
         + "\n              'spacemr_people_id':   { 'default':'rw'}"
         + "\n              , 'spacemr_space_id':  { 'default':'rw'}"
         + "\n              , 'spacemr_test':      { 'default':'rw'}"
         + "\n             }"
         + "\n        }"
         + "\n      , {"
         + "\n          'name':'prop'"
         + "\n          , 'color':'#66ccff'"
         + "\n          , 'description':'Richiesta in approvazione'"
         + "\n          , 'steps':["
         + "\n              {"
         + "\n                  'destination':'annullata'"
         + "\n                  , 'description':'Richiesta annullata'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_update'"
         + "\n              }"
         + "\n          ]"
         + "\n          , 'per_field_permisson': {"
         + "\n              'spacemr_people_id':   { 'default':'rw'}"
         + "\n              , 'spacemr_test':      { 'default':'hidden', 'ro': 'db_spacemr_space_people_book_update'}"
         + "\n             }"
         + "\n      }"
         + "\n      , {"
         + "\n          'name':'annullata'"
         + "\n          , 'color':'#000000'"
         + "\n          , 'description':'Annullata'"
         + "\n          , 'steps':["
         + "\n              {"
         + "\n                  'destination':'creata'"
         + "\n                  , 'description':'Restart from create'"
         + "\n                  , 'permission':'db_spacemr_space_people_book_admin'"
         + "\n              }"
         + "\n          ]"
         + "\n      }"
         + "\n  ]"
         + "\n}"
         ;
      // System.out.println(s);
      boolean rv = false;
      HashSet<String> permissions =
         new HashSet<>(Arrays.asList("db_spacemr_space_people_book_update"));
      Workflow wf = new Workflow(new JSONObject(s), permissions);
      String o = ""
         + "{"
         + "\n  'defaultStatus': 'crea',"
         + "\n  'statusList': ["
         + "\n    'creata',"
         + "\n    'prop',"
         + "\n    'annullata'"
         + "\n  ],"
         + "\n  'statusIndex': {"
         + "\n    'annullata': {"
         + "\n      'per_field_permission_canRead': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': true,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'color': '#000000',"
         + "\n      'per_field_permission_canWrite': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': false,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'name': 'annullata',"
         + "\n      'description': 'Annullata',"
         + "\n      'steps': [],"
         + "\n      'stepsIndex': {}"
         + "\n    },"
         + "\n    'prop': {"
         + "\n      'per_field_permission_canRead': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': true,"
         + "\n        'spacemr_test': true"
         + "\n      },"
         + "\n      'color': '#66ccff',"
         + "\n      'per_field_permission_canWrite': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': false,"
         + "\n        'spacemr_test': false"
         + "\n      },"
         + "\n      'name': 'prop',"
         + "\n      'description': 'Richiesta in approvazione',"
         + "\n      'steps': ['annullata'],"
         + "\n      'stepsIndex': {'annullata': {"
         + "\n        'destination': 'annullata',"
         + "\n        'description': 'Richiesta annullata',"
         + "\n        'permission': 'db_spacemr_space_people_book_update'"
         + "\n      }}"
         + "\n    },"
         + "\n    'creata': {"
         + "\n      'per_field_permission_canRead': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': true,"
         + "\n        'spacemr_test': true"
         + "\n      },"
         + "\n      'color': '#B0B0B0',"
         + "\n      'per_field_permission_canWrite': {"
         + "\n        'spacemr_people_id': true,"
         + "\n        'spacemr_space_id': true,"
         + "\n        'spacemr_test': true"
         + "\n      },"
         + "\n      'name': 'creata',"
         + "\n      'description': 'Richiesta creata',"
         + "\n      'steps': ['prop'],"
         + "\n      'stepsIndex': {'prop': {"
         + "\n        'destination': 'prop',"
         + "\n        'description': 'Invia richiesta',"
         + "\n        'permission': 'db_spacemr_space_people_book_update'"
         + "\n      }}"
         + "\n    }"
         + "\n  }"
         + "\n}"
         ;
      o = o.replace('\'', '"');
      if (o.equals(wf.toJson().toString(2))) {
         rv = true;
      } else {
         System.out.println("--- wf.toJson():\n"+wf.toJson().toString(2));
         System.out.println("-- expected json:\n"+o);
      }
      if (rv) {
         if (wf.per_field_user_canRead("annullata", "spacemr_test") == true) {
            rv = false;
            System.out.println("--- per_field_user_canRead('annullata', 'spacemr_test') must be 'false'");
         }
      }
      if (rv) {
         if (wf.per_field_user_canRead("prop", "spacemr_test") != true) {
            rv = false;
            System.out.println("--- per_field_user_canRead('prop', 'spacemr_test') must be 'true'");
         }
      }
      if (rv) {
         if (wf.per_field_user_canWrite("annullata", "spacemr_test") == true) {
            rv = false;
            System.out.println("--- per_field_user_canWrite('annullata', 'spacemr_test') must be 'false'");
         }
      }
      if (rv) {
         if (wf.per_field_user_canWrite("prop", "spacemr_test") == true) {
            rv = false;
            System.out.println("--- per_field_user_canWrite('prop', 'spacemr_test') must be 'false'");
         }
      }
      if (rv) {
         if (wf.per_field_user_canWrite("annullata", "spacemr_people_id") != true) {
            rv = false;
            System.out.println("--- per_field_user_canWrite('prop', 'spacemr_test') must be 'true'");
         }
      }
      if (rv) {
         if (wf.per_field_user_canWrite("creata", "spacemr_test") != true) {
            rv = false;
            System.out.println("--- per_field_user_canWrite('crea', 'spacemr_test') must be 'true'");
         }
      }
      assertTrue(rv);
   }
   
   
}
