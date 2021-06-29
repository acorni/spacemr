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
         --tests it.unimo.app.tools.AppListTableMapper__Test \
     && cat build/test-results/test/binary/output.bin
   --
   -- outupt is like this:
   --
    -- mapper.getJSON():
    {
        "headers": [
            {
                "name": "app_group_id",
                "label": "test_prefixName.app_group_id",
                "type": "integer"
            },
            {
                "name": "name",
                "label": "test_prefixName.name",
                "type": "string"
            },
            {
                "name": "nota",
                "label": "test_prefixName.nota",
                "type": "longstring"
            }
        ],
        "rows": [
            [
                1,
                "ingmo",
                "aaa"
            ],
            [
                2,
                "root",
                "Added by app system initializer on Mon Jun 16 10:49:29 CEST 2014"
            ],
            [
                3,
                "ditta1",
                "Nessuna nota"
            ],
            [
                4,
                "ditta2",
                ""
            ]
        ]
    }
 */
public class AppListTableMapper__Test {

   //-
   private JdbcTemplate jdbcTemplate = 
      (JdbcTemplate)Application.getStaticContext().getBean("jdbcTemplate");
   
   @Test
   public void test_select_from_app_group() throws Exception {
      //-
      AppListTableMapper mapper = new AppListTableMapper("test_prefixName");
      jdbcTemplate.query("select * "
                         + " from app_group"
                         , (Object[])null
                         , mapper);
      System.out.println(" -- mapper.getJSON():\n" 
                         + mapper.getJSON().toString(2));
   }

   
}
