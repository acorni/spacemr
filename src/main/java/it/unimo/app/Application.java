package it.unimo.app;

import java.io.File;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.json.JSONObject;
import org.json.JSONArray;

import it.unimo.app.controller.AppControllerTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.*;
import it.unimo.app.tools.authentication.AuthGeneric_base;
import it.unimo.app.tools.authentication.AuthLdapGeneric;
import it.unimo.app.tools.authentication.WrongLoginManager;
import it.unimo.app.tools.peopleImport.PeopleImportLdap;
import it.unimo.app.tools.spacemr_inventarioImport.Spacemr_inventarioImport_unimoreUgov;
import it.unimo.app.tools.Spacemr_email_manager;
import it.unimo.app.tools.Spacemr_notifica_manager;
import it.unimo.app.tools.Spacemr_user_white_list_cache;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
// @ImportResource("classpath:/database-config.xml")
public class Application {


   @Bean
   public DataSource dataSource() {
      //-
      JSONObject conf = getConfigurationFromFile().getJSONObject("data_source");
      //-
      System.out.println(" ---- dataSource initialization.");
      //-
      String driverName = conf.getString("driverclass");
      //-
      HikariConfig hconfig = new HikariConfig();
      //-
      try {
         hconfig.setDriverClassName(driverName);
      } catch (Exception e) {
         String m = "Error instantiating jdbc driver ["+driverName+"]\n"
            + Tools.stringStackTrace(e);
         System.out.println(m);
      }
      //-
      hconfig.setUsername(conf.getString("username"));
      hconfig.setJdbcUrl(conf.getString("url"));
      hconfig.setPassword(conf.getString("password"));
      //-
      hconfig.setMaximumPoolSize(10);
      hconfig.setAutoCommit(true);
      hconfig.addDataSourceProperty("cachePrepStmts", "true");
      hconfig.addDataSourceProperty("prepStmtCacheSize", "250");
      hconfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      //-
      DataSource rv = new HikariDataSource(hconfig);
      //-
      Db_versioning.checkIfCreateDb(rv);
      //-
      return (rv);
   }

   
   /* 
      tomcat - file:/dati/bin/apache-tomcat-7.0.40/webapps/spacemr/WEB-INF/classes/
      gradle - file:/dati/toolsZippati/projects/spacemr/build/classes/main/
   */
   private static JSONObject _configurationFromFile = null;
   private static String     _dataPath = null;
   
   public static String getConfigurationFullPath() {
      String appPath = Application.class.getResource("/").toString();
      // System.out.println(" ---- appPath - orig: " + appPath);
      //-
      //
      // jar:  jar:file:/home/corni/tmp/bb/spacemr-spring-boot-1.0.1.jar!/BOOT-INF/classes!/
      if (appPath.endsWith(".jar!/BOOT-INF/classes!/")) {
         appPath = appPath.substring(9);
         int p1 = appPath.indexOf("!/");
         int p2 = appPath.lastIndexOf("/", p1);
         appPath = appPath.substring(0,p2);
      } else if (appPath.endsWith("/build/classes/java/main/")) {
         appPath = appPath.substring(5);
         int pos = appPath.lastIndexOf("/build/classes/java/main/");
         appPath = appPath.substring(0, pos) + "/app";
      } else if (appPath.endsWith("/WEB-INF/classes/")) {
         appPath = appPath.substring(5);
         int pos = appPath.lastIndexOf("/WEB-INF/classes/");
         appPath = appPath.substring(0, pos);
      } else {
         appPath = appPath.substring(5);
      }
      // System.out.println(" ---- appPath final: " + appPath);
      return(appPath);
   }
   
   public static JSONObject getConfigurationFromFile() {
      if (_configurationFromFile == null) {
         String appPath = getConfigurationFullPath();
         String dataPath = appPath + "_data";
         _dataPath = dataPath;
         System.out.println(" ---- dataPath: " + dataPath);
         try {
            {
               File fdataPath = new File(dataPath);
               if (!fdataPath.exists()) {
                  System.out.println(" --- configuration directory ["+dataPath+"] DOES NOT EXISTS!"
                                     + "\n  I try to create a new one...");
                  fdataPath.mkdir();
                  System.out.println(" --- ok configuration directory created.");
               }
            }
            {
               String confFile = dataPath + "/config.json";
               if ((new File(confFile)).exists()) {
                  _configurationFromFile = 
                     new JSONObject(Tools.string_loadFromFile(confFile));
               } else {
                  System.out.println(" --- configuration file ["+confFile+"] DOES NOT EXISTS!"
                                     + "\n  I try to create a default one...");
                  _configurationFromFile = getConfigurationFromFileDefault();
                  Tools.string_writeToFile(confFile, _configurationFromFile.toString(2));
                  System.out.println(" --- configuration file ["+confFile+"] created.");
               }
               System.out.println(" ---- configuration file ["+confFile+"].");
            }
         } catch (Exception e) {
            String m = "Error reading or creating the application configuration file\n"
               + Tools.stringStackTrace(e);
            System.out.println(m);
         }
      }
      return(_configurationFromFile);
   }

   public static JSONObject getConfigurationFromFileDefault() {
      JSONObject rv = new JSONObject();
      {
         JSONObject c = new JSONObject();
         c.put("driverclass", "org.gjt.mm.mysql.Driver");
         c.put("username", "tomcatspacemr");
         c.put("url", "jdbc:mysql://127.0.0.1:3306/tomcatspacemr");
         c.put("password", "tomcatspacemrp");
         rv.put("data_source", c);
      }
      return(rv);
   }

   public static String getDataPath() {
      return(_dataPath);
   }

   @Bean(name = "multipartResolver")
   public CommonsMultipartResolver multipartResolver() {
      CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
      multipartResolver.setMaxUploadSize(500000);
      return multipartResolver;
   }
   @Bean
   public JdbcTemplate jdbcTemplate() {
      JdbcTemplate rv = new JdbcTemplate(dataSource);
      return rv;
   }
   @Bean
   public Tools tools() {
      return (new Tools());
   }
   @Bean
   public WorkflowManager workflowManager() {
      return (new WorkflowManager());
   }
   @Bean
   public AuthGeneric_base authGeneric_base() {
      return (new AuthGeneric_base());
   }
   @Bean
   public AuthLdapGeneric authLdapGeneric() {
      return (new AuthLdapGeneric());
   }
   @Bean
   public WrongLoginManager wrongLoginManager() {
      return (new WrongLoginManager());
   }
   @Bean
   public AppSessionTools appSessionTools() {
      return (new AppSessionTools());
   }
   @Bean
   public AppControllerTools appControllerTools() {
      return (new AppControllerTools());
   }
   @Bean
   public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
      NamedParameterJdbcTemplate rv = new NamedParameterJdbcTemplate(dataSource);
      return rv;
   }
   @Bean
   public DbTools dbTools() {
      DbTools rv = new DbTools();
      return rv;
   }
   @Bean
   public PeopleImportLdap peopleImportLdap() {
      PeopleImportLdap rv = new PeopleImportLdap();
      return rv;
   }
   @Bean
   public Spacemr_inventarioImport_unimoreUgov spacemr_inventarioImport_unimoreUgov() {
      Spacemr_inventarioImport_unimoreUgov rv = new Spacemr_inventarioImport_unimoreUgov();
      return rv;
   }
   @Bean
   public Spacemr_email_manager spacemr_email_manager() {
      Spacemr_email_manager rv = new Spacemr_email_manager();
      return rv;
   }
   @Bean
   public Spacemr_notifica_manager spacemr_notifica_manager() {
      Spacemr_notifica_manager rv = new Spacemr_notifica_manager();
      return rv;
   }
   @Bean
   public Spacemr_user_white_list_cache spacemr_user_white_list_cache() {
      Spacemr_user_white_list_cache rv = new Spacemr_user_white_list_cache();
      return rv;
   }

   @Bean
   public Db_versioning db_versioning() throws Exception {
      Db_versioning rv = new Db_versioning();
      return rv;
   }
   @Bean
   public App_log app_log() {
      App_log rv = new App_log();
      return rv;
   }
   @Bean
   public App_notifica app_notifica() throws Exception {
      App_notifica rv = new App_notifica();
      return rv;
   }
   @Bean
   public App_user app_user() {
      App_user rv = new App_user();
      return rv;
   }
   @Bean
   public App_permission app_permission() {
      App_permission rv = new App_permission();
      return rv;
   }
   @Bean
   public App_role app_role() throws Exception {
      App_role rv = new App_role();
      return rv;
   }
   @Bean
   public App_role_permission app_role_permission() throws Exception {
      App_role_permission rv = new App_role_permission();
      return rv;
   }
   @Bean
   public App_group app_group() throws Exception {
      App_group rv = new App_group();
      return rv;
   }
   @Bean
   public App_group_role_user app_group_role_user() throws Exception {
      App_group_role_user rv = new App_group_role_user();
      return rv;
   }
   @Bean
   public App_system_property app_system_property() throws Exception {
      App_system_property rv = new App_system_property();
      return rv;
   }
   @Bean
   public App_system_log app_system_log() throws Exception {
      App_system_log rv = new App_system_log();
      return rv;
   }
   @Bean
   public App_file app_file() throws Exception {
      App_file rv = new App_file();
      return rv;
   }
   @Bean
   public Spacemr_people spacemr_people() throws Exception {
      Spacemr_people rv = new Spacemr_people();
      return rv;
   }
   @Bean
   public Spacemr_space_type spacemr_space_type() throws Exception {
      Spacemr_space_type rv = new Spacemr_space_type();
      return rv;
   }
   @Bean
   public Spacemr_space_people_type spacemr_space_people_type() throws Exception {
      Spacemr_space_people_type rv = new Spacemr_space_people_type();
      return rv;
   }
   @Bean
   public Spacemr_space spacemr_space() throws Exception {
      Spacemr_space rv = new Spacemr_space();
      return rv;
   }
   @Bean
   public Spacemr_space_people spacemr_space_people() throws Exception {
      Spacemr_space_people rv = new Spacemr_space_people();
      return rv;
   }
   @Bean
   public Spacemr_inventario spacemr_inventario() throws Exception {
      Spacemr_inventario rv = new Spacemr_inventario();
      return rv;
   }
   @Bean
   public Spacemr_space_map spacemr_space_map() throws Exception {
      Spacemr_space_map rv = new Spacemr_space_map();
      return rv;
   }
   @Bean
   public Spacemr_space_people_book spacemr_space_people_book() throws Exception {
      Spacemr_space_people_book rv = new Spacemr_space_people_book();
      return rv;
   }
   @Bean
   public Spacemr_space_user_presence spacemr_space_user_presence() throws Exception {
      Spacemr_space_user_presence rv = new Spacemr_space_user_presence();
      return rv;
   }


   
   /**  used in tests and in main */
   private static ApplicationContext _staticContext = null;
   /**  used in tests and in main */
   public static ApplicationContext getStaticContext() {
      if (_staticContext == null) {
         try {
            _staticContext = 
               new AnnotationConfigApplicationContext(Application.class);
         } catch (Exception e) {
            e.printStackTrace(System.err);
         }
      }
      return(_staticContext);
   }

   /* 
      cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="test"
    */
   // @Inject DataSource dataSource; // from XML
   public static void main(String[] args) throws Exception  {
      if (args.length > 0) {
         //-
         ApplicationContext context = getStaticContext();
         //-
         String option = args[0];
         if (option.equals("toolDb")) {
            // DbTools tool = new DbTools();
            DbTools tool = (DbTools) context.getBean("dbTools");
            tool.run(args);
         } else if (option.equals("peopleImportLdap")) {
            PeopleImportLdap peopleImportLdap =
               (PeopleImportLdap) context.getBean("peopleImportLdap");
            peopleImportLdap.run(args);
         } else if (option.equals("spacemr_inventarioImport_unimoreUgov")) {
            Spacemr_inventarioImport_unimoreUgov spacemr_inventarioImport_unimoreUgov =
               (Spacemr_inventarioImport_unimoreUgov) context.getBean("spacemr_inventarioImport_unimoreUgov");
            spacemr_inventarioImport_unimoreUgov.run(args);
         } else if (option.equals("spacemr_email_manager")) {
            Spacemr_email_manager spacemr_email_manager =
               (Spacemr_email_manager) context.getBean("spacemr_email_manager");
            spacemr_email_manager.run(args);
         } else if (option.equals("spacemr_notifica_manager")) {
            Spacemr_notifica_manager spacemr_notifica_manager =
               (Spacemr_notifica_manager) context.getBean("spacemr_notifica_manager");
            spacemr_notifica_manager.run(args);
         } else if (option.equals("tools")) {
            Tools tools = (Tools) context.getBean("tools");
            tools.run(args);
         } else if (option.equals("test")) {
            // getConfigurationFromFile();
            // cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="test"
            System.out.println(" hello test");
         } else if (option.equals("authLdapGeneric")) {
            AuthLdapGeneric authLdapGeneric = (AuthLdapGeneric) context.getBean("authLdapGeneric");
            authLdapGeneric.run(args);
         } else if (option.equals("authGeneric_base")) {
            AuthGeneric_base authGeneric_base = (AuthGeneric_base) context.getBean("authGeneric_base");
            authGeneric_base.run(args);
         } else {
            String s = ""
               + "\n usage:"
               + "\n  run <option> [args]"
               + "\n supported options:"
               + "\n  toolDb - various tools db-related"
               + "\n  tools  - various app tools "
               ;
            System.out.println(s);
         }
         // for (String arg: args) {
         //    System.out.println(" ------- arg ---- : " + arg);
         // }
      } else {
         // application.initialize(args);
         ApplicationContext ctx = SpringApplication.run(Application.class, args);
         // System.out.println("Let's inspect the beans provided by Spring Boot:");
         // String[] beanNames = ctx.getBeanDefinitionNames();
         // Arrays.sort(beanNames);
         // for (String beanName : beanNames) {
         //    System.out.println(beanName);
         // }
      }
   }


   @Autowired
   private DataSource dataSource;

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

   @Autowired
   private App_permission app_permission;


}
