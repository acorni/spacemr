package it.unimo.app.tools;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.springframework.jdbc.core.JdbcTemplate;

import org.junit.*;
import static org.junit.Assert.*;

import it.unimo.app.Application;

/* 
   cd /dati/toolsZippati/projects/spacemr; gradle test  \
         --tests it.unimo.app.tools.AppMapperJson__Test \
     && cat build/test-results/test/binary/output.bin
     ls build/test-results/test
 */
public class AppMapperJson__Test {

   //-
   private JdbcTemplate jdbcTemplate = 
      (JdbcTemplate)Application.getStaticContext().getBean("jdbcTemplate");
   
   @Test
   public void test_select_from_app_group() throws Exception {
      //-
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * "
                         + " from app_group"
                         , (Object[])null
                         , mapper);
      System.out.println(" -- mapper.getFirstRow():\n" 
                         + mapper.getFirstRow().toString(2));
      System.out.println(" -- mapper.getJSONArray():\n" 
                         + mapper.getJSONArray().toString(2));
      //-
      mapper = new AppMapperJson();
      jdbcTemplate.query(""
                         + "\nselect *"
                         + "\n  from app_user"
                         + "\n where user_name like ?"
                         , new Object[] { "r%" }
                         , mapper);
      System.out.println(" -- mapper.getFirstRow():\n" 
                         + mapper.getFirstRow().toString(2));
      System.out.println(" -- mapper.getJSONArray():\n" 
                         + mapper.getJSONArray().toString(2));

   }

   
}
