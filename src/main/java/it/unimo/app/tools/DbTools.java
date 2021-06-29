package it.unimo.app.tools;

import freemarker.template.*;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class DbTools {

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @Autowired
   private DataSource dataSource;


   public void runUsage() {
      String s = ""
         + "\n toolDb usage:"
         + "\n  run toolDb <option> [args]"
         + "\n supported options:"
         + "\n  test - various tools db-related"
         + "\n  describeTable <tableName> <mvcPrefix> - display table info"
         + "\n  templateTable <tableName> <templateName> <mvcPrefix> "
         + "\n                apply table data to the given template"
         ;
      System.out.println(s);
   }

   /** 
    * command line parameter parser */
   public void run(String args[]) throws Exception {
      int ipos = 1;
      if (args.length < 2) {
         runUsage();
      } else {
         String option = args[ipos++];
         if (option.equals("test")) {
            System.out.println(" hello! test.");
            doTest();
         } else if (option.equals("describeTable")) {
            String tableName = args[ipos++];
            String mvcPrefix = "mvcPrefix";
            //-
            if (ipos < args.length) mvcPrefix = args[ipos++];
            //-
            doTemplateTable("DbTools_templateTable", tableName, mvcPrefix);
            //- 
            //- cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="toolDb describeTable app_user" 
            //- 
         } else if (option.equals("templateTable")) {
            String templateName = args[ipos++];
            String tableName = args[ipos++];
            String mvcPrefix = "mvcPrefix";
            //-
            if (ipos < args.length) mvcPrefix = args[ipos++];
            //-
            doTemplateTable(templateName, tableName, mvcPrefix);
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }


   /** 
    * 
    cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="toolDb templateTable DbTools_test app_user" 
    */
   public void doTemplateTable(String templateName
                               , String tableName
                               , String mvcPrefix
                               ) throws Exception {
      String qs = "select * from " + tableName ;
      String templateFullName = "templates/"+templateName+".ftl";
      //-
      Connection con = null;
      try {
         con = dataSource.getConnection();
         DbToolsTable  table = new DbToolsTable(tableName, con);
         //-
         System.out.println(" table: " + table);
         //-
         //- apply freemarker template
         //-
         HashMap<String, Object> root = new HashMap<String, Object>();
         root.put("table", table);
         root.put("mvcPrefix", mvcPrefix);
         root.put("tools",   new Tools());
         //-
         Configuration cfg = new Configuration();
         cfg.setClassForTemplateLoading(this.getClass(), "/");
         Template temp = cfg.getTemplate(templateFullName);
         temp.process(root, new OutputStreamWriter(System.out));
         //-
      } finally {
         if (con != null) {
            con.close();
         }
      }
      System.out.println(".");
   }

   /** 
    * 
    cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="toolDb test"
    */
   @SuppressWarnings("unchecked")
   public void doTest() {
      String qs = ""
         + "select user_id, user_name, email from user"
         ;
      jdbcTemplate
         .query(qs
                , new RowMapper() {
                      public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                         String user_name  = rs.getString("user_name");
                         String email  = rs.getString("email");
                         System.out.println(" ####  " + user_name + " " + email);
                         return null;
                      }
                   }
                );
      System.out.println(".");
   }

   

}
