package it.unimo.app.om;
//-
import java.util.Date;
import java.util.Vector;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//-
import it.unimo.app.tools.*;
import it.unimo.app.Application;

import javax.annotation.PostConstruct;

//-
public class Db_versioning {

   //-
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private Tools tools;
   @Autowired
   private App_system_property app_system_property;

   /**
    * check db */
   @PostConstruct
   public void initDb() throws Exception {
      System.out.println(" -------------------- Db_versioning --------- ");
      while (initDb_update()) {
         //- iterate on versions
      }
   }

   public static void checkIfCreateDb(DataSource datasource) {
      try {
         Connection c = datasource.getConnection();
         boolean doCreate = false;
         try {
            String query =
               "select count(app_system_property_id) from app_system_property";
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.close();
            statement.close();
         } catch (Exception e) {
            System.out.println(" -- db to be created? -- ");
            e.printStackTrace(System.out);
            doCreate = true;
         }
         if (doCreate) {
            String dbstring = Tools.string_loadFromClasspath("db.sql");
            if (dbstring == null) {
               dbstring = Tools.string_loadFromFile(
                     Application.getConfigurationFullPath()+"/WEB-INF/classes/db.sql");
            }
            System.out.println(" db: " + dbstring);
            Vector<String> sqls = Tools.splitString(dbstring, ';' );
            //-
            Statement statement = c.createStatement();
            for (String sql: sqls) {
               if (sql.trim().length() > 0) {
                  System.out.println("executing: " + sql);
                  statement.executeUpdate(sql);
               }
            }
         }
         c.close();
      } catch (Exception e) {
         e.printStackTrace(System.out);
      }
   }

   private boolean initDb_update() throws Exception {
      boolean rv = true;   //- true means there were changes
      String version = app_system_property.getAsString("db_version");
      String newVersion = null;
      if (current_db_version.equals(version)) {
         //- nothing to do
         rv = false;
      } else if (version == null) {
         //-
         version = current_db_version;
         newVersion = version;
         System.out.println("DB update - assuming last version");
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.0")) {
         //-
         newVersion = "0.0.1";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate
            .update("drop table spacemr_space;");
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.1")) {
         //-
         newVersion = "0.0.2";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate
            .update(""
                    + "\n    CREATE TABLE spacemr_people ("
                    + "\n       spacemr_people_id integer not null auto_increment"
                    + "\n       , username varchar (254) not null"
                    + "\n       , first_name varchar (254)"
                    + "\n       , last_name  varchar (254)"
                    + "\n       , email      varchar (254)"
                    + "\n       , role       varchar (254)"
                    + "\n       , department varchar (254)"
                    + "\n       , nota mediumtext"
                    + "\n       , configuration mediumtext"
                    + "\n       , primary key(spacemr_people_id)"
                    + "\n       , unique app_user_property_idx (username)"
                    + "\n    )  engine=innodb ;"
                    );
         jdbcTemplate
            .update(""
                    + "\n    CREATE TABLE spacemr_space_type ("
                    + "\n       spacemr_space_type_id integer not null auto_increment"
                    + "\n       , name varchar (254) not null"
                    + "\n       , description varchar (254)"
                    + "\n       , nota mediumtext"
                    + "\n       , configuration mediumtext"
                    + "\n       , primary key(spacemr_space_type_id)"
                    + "\n    )  engine=innodb ;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.2")) {
         //-
         newVersion = "0.0.3";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate
            .update(""
                    + "\n    CREATE TABLE spacemr_space_people_type ("
                    + "\n       spacemr_space_people_type_id integer not null auto_increment"
                    + "\n       , name varchar (254) not null"
                    + "\n       , description varchar (254)"
                    + "\n       , nota mediumtext"
                    + "\n       , primary key(spacemr_space_people_type_id)"
                    + "\n    )  engine=innodb ;"
                    );
         jdbcTemplate
            .update(""
                    + "\n    CREATE TABLE spacemr_space ("
                    + "\n       spacemr_space_id integer not null auto_increment"
                    + "\n       , app_group_id integer not null"
                    + "\n       , spacemr_space_type_id integer not null"
                    + "\n       , spacemr_space_in_id integer"
                    + "\n       , name varchar (254) not null"
                    + "\n       , code varchar (254) not null"
                    + "\n       , description varchar (254) not null"
                    + "\n       , nota mediumtext"
                    + "\n       , primary key(spacemr_space_id)"
                    + "\n       , foreign key (app_group_id) references app_group (app_group_id)"
                    + "\n       , foreign key (spacemr_space_type_id)"
                    + "\n                     references spacemr_space_type (spacemr_space_type_id)"
                    + "\n       , foreign key (spacemr_space_in_id)"
                    + "\n                     references spacemr_space (spacemr_space_id)"
                    + "\n    )  engine=innodb ;"
                    );
         jdbcTemplate
            .update(""
                    + "\n    CREATE TABLE spacemr_space_people ("
                    + "\n       spacemr_space_people_id integer not null auto_increment"
                    + "\n       , spacemr_space_people_type_id integer not null"
                    + "\n       , spacemr_space_id integer not null"
                    + "\n       , spacemr_people_id integer not null"
                    + "\n       , description varchar (254)"
                    + "\n       , nota mediumtext"
                    + "\n       , primary key(spacemr_space_people_id)"
                    + "\n       , foreign key (spacemr_space_people_type_id)"
                    + "\n                     references spacemr_space_people_type (spacemr_space_people_type_id)"
                    + "\n       , foreign key (spacemr_space_id)"
                    + "\n                     references spacemr_space (spacemr_space_id)"
                    + "\n       , foreign key (spacemr_people_id)"
                    + "\n                     references spacemr_people (spacemr_people_id)"
                    + "\n    )  engine=innodb ;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.3")) {
         //-
         newVersion = "0.0.4";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate
            .update("ALTER TABLE spacemr_space drop column name;");
         jdbcTemplate
            .update("ALTER TABLE spacemr_space add unique spacemr_space_code_idx (code);");
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.4")) {
         //-
         newVersion = "0.0.5";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate.update(""
                             + "\n alter table spacemr_space_people add column"
                             + "\n    date_from date not null;"
                             );
         jdbcTemplate.update(""
                             + "\n alter table spacemr_space_people add column"
                             + "\n    date_to   date default null;"
                             );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.5")) {
         //-
         newVersion = "0.0.6";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate.update(""
                             + "\n alter table spacemr_space add column"
                             + "\n    number_of_seating     integer;"
                             );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.6")) {
         //-
         newVersion = "0.0.7";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate.update(""
                             + "\n   CREATE TABLE app_system_log ("
                             + "\n       app_system_log_id integer not null auto_increment"
                             + "\n       , date datetime not null"
                             + "\n       , user_name varchar (254)"
                             + "\n       , category varchar (254)"
                             + "\n       , value mediumtext"
                             + "\n       , primary key(app_system_log_id)"
                             + "\n    )  engine=innodb ;"
                             );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.7")) {
         //-
         newVersion = "0.0.8";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate.update(""
                             + "\n ALTER TABLE spacemr_people  add column  fg_in_ldap boolean default 0;"
                             );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.8")) {
         //-
         newVersion = "0.0.9";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         jdbcTemplate.update(""
                             + "\n    create table app_file"
                             + "\n    ("
                             + "\n       app_file_id integer not null auto_increment"
                             + "\n       , owner_object_type integer"
                             + "\n       , owner_object_id integer"
                             + "\n       , file_name varchar (254)"
                             + "\n       , file_size integer"
                             + "\n       , primary key(app_file_id)"
                             + "\n       , index (owner_object_type, owner_object_id)"
                             + "\n    )  engine=innodb ;"
                             );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.9")) {
         //-
         newVersion = "0.0.10";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         //-  drop table spacemr_inventario;
         //-  delete from app_log where object_type = 1060;
         //-
         jdbcTemplate.update(""
                             + "\n    CREATE TABLE spacemr_inventario ("
                             + "\n       spacemr_inventario_id integer not null auto_increment"
                             + "\n       , spacemr_space_id integer  not null"
                             + "\n       , spacemr_people_id integer  not null"
                             + "\n       , fg_validato boolean default 0"
                             + "\n       , fg_adesivo  boolean default 0"
                             + "\n       , codice_inventario_unimore varchar (254)"
                             + "\n       , inventario_numero varchar (30)"
                             + "\n       , inventario_numero_sub varchar (30)"
                             + "\n       , tipo_carico_scarico varchar (30)"
                             + "\n       , carico_data date"
                             + "\n       , attivita_tipo varchar (30)"
                             + "\n       , descrizione mediumtext"
                             + "\n       , categoria_inventario varchar (30)"
                             + "\n       , valore decimal(12,5)"
                             + "\n       , fornitore varchar (254)"
                             + "\n       , scarico_numero_buono varchar (30)"
                             + "\n       , scarico_data date"
                             + "\n       , old_values mediumtext"
                             + "\n       , old_values_changes mediumtext"
                             + "\n       , nota mediumtext"
                             + "\n       , primary key(spacemr_inventario_id)"
                             + "\n       , foreign key (spacemr_space_id)"
                             + "\n                     references spacemr_space (spacemr_space_id)"
                             + "\n       , foreign key (spacemr_people_id)"
                             + "\n                    references spacemr_people (spacemr_people_id)"
                             + "\n    )  engine=innodb ;"
                             );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.10")) {
         //-
         newVersion = "0.0.11";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_inventario  add column stato varchar (30) not null default 'da_controllare';
           ALTER TABLE spacemr_inventario  add column inventario_etichetta varchar (254);
           -- changes undo test
           ALTER TABLE spacemr_inventario  drop column stato;
           ALTER TABLE spacemr_inventario  drop column inventario_etichetta;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE spacemr_inventario  add column stato varchar (30) not null default 'da_controllare';"
                             );
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE spacemr_inventario  add column inventario_etichetta varchar (254);"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.11")) {
         //-
         newVersion = "0.0.12";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_space_people_type  add column fg_is_a_seat boolean default 0;
           -- changes undo test
           ALTER TABLE spacemr_space_people_type  drop column fg_is_a_seat;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE spacemr_space_people_type  add column fg_is_a_seat boolean default 0;"
                             );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.12")) {
         //-
         newVersion = "0.0.13";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           CREATE TABLE spacemr_space_map (
              spacemr_space_map_id integer not null auto_increment
              , spacemr_space_id integer not null
              , description varchar (254) not null
              , fg_default_map boolean default 0
              , info mediumtext
              , nota mediumtext
              , primary key(spacemr_space_map_id)
              , foreign key (spacemr_space_id) references spacemr_space (spacemr_space_id)
           )  engine=innodb ;
           -- changes undo test
           DROP TABLE spacemr_space_map;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n  CREATE TABLE spacemr_space_map ("
                    + "\n     spacemr_space_map_id integer not null auto_increment"
                    + "\n     , spacemr_space_id integer not null"
                    + "\n     , description varchar (254) not null"
                    + "\n     , fg_default_map boolean default 0"
                    + "\n     , info mediumtext"
                    + "\n     , nota mediumtext"
                    + "\n     , primary key(spacemr_space_map_id)"
                    + "\n     , foreign key (spacemr_space_id) references spacemr_space (spacemr_space_id)"
                    + "\n  )  engine=innodb ;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         //-
      } else if (version.equals("0.0.13")) {
         //-
         newVersion = "0.0.14";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_people   add column fg_has_a_seat boolean default 1;
           -- changes undo test
           ALTER TABLE spacemr_people  drop column fg_has_a_seat;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE spacemr_people "
                    + "\n add column fg_has_a_seat boolean default 1;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         
      } else if (version.equals("0.0.14")) {
         //-
         newVersion = "0.0.15";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_space  add  column area_in_meters2 decimal(12,5) default 0;
           -- changes undo test
           ALTER TABLE spacemr_space  drop column area_in_meters2;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE spacemr_space "
                    + "\n add  column area_in_meters2 decimal(12,5) default 0;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-

      } else if (version.equals("0.0.15")) {
         //-
         newVersion = "0.0.16";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           CREATE TABLE spacemr_space_people_book (
             spacemr_space_people_book_id integer not null auto_increment
             , spacemr_people_id integer not null
             , spacemr_responsible_id integer not null
             , spacemr_space_id integer not null
             , reason varchar (254)
             , date_from date
             , date_to   date
             , repetition varchar(2)
             , stato varchar(30) default 'crea' NOT NULL
             , nota mediumtext
             , primary key(spacemr_space_people_book_id)
             , foreign key (spacemr_space_id)
                           references spacemr_space (spacemr_space_id)
             , foreign key (spacemr_people_id)
                           references spacemr_people (spacemr_people_id)
             , foreign key (spacemr_responsible_id)
                           references spacemr_people (spacemr_people_id)
             , index spacemr_space_people_book_date_from (date_from)
             , index spacemr_space_people_book_date_to (date_to)
             , index spacemr_space_people_book_stato (stato)
           )  engine=innodb;
           -- changes undo test
           DROP TABLE spacemr_space_people_book;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n  CREATE TABLE spacemr_space_people_book ("
                    + "\n   spacemr_space_people_book_id integer not null auto_increment"
                    + "\n   , spacemr_people_id integer not null"
                    + "\n   , spacemr_responsible_id integer not null"
                    + "\n   , spacemr_space_id integer not null"
                    + "\n   , reason varchar (254)"
                    + "\n   , date_from date"
                    + "\n   , date_to   date"
                    + "\n   , repetition varchar(2)"
                    + "\n   , stato varchar(30) default 'crea' NOT NULL"
                    + "\n   , nota mediumtext"
                    + "\n   , primary key(spacemr_space_people_book_id)"
                    + "\n   , foreign key (spacemr_space_id)"
                    + "\n                 references spacemr_space (spacemr_space_id)"
                    + "\n   , foreign key (spacemr_people_id)"
                    + "\n                 references spacemr_people (spacemr_people_id)"
                    + "\n   , foreign key (spacemr_responsible_id)"
                    + "\n                 references spacemr_people (spacemr_people_id)"
                    + "\n   , index spacemr_space_people_book_date_from (date_from)"
                    + "\n   , index spacemr_space_people_book_date_to (date_to)    "
                    + "\n   , index spacemr_space_people_book_stato (stato)        "
                    + "\n  )  engine=innodb;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-

      } else if (version.equals("0.0.16")) {
         //-
         newVersion = "0.0.17";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           CREATE TABLE app_notifica (
               app_notifica_id integer not null auto_increment
               , date datetime not null
               , bean_name varchar (254) not null
               , object_id integer not null
               , json_info mediumtext
               , primary key(app_notifica_id)
               , index app_notifica_idx (bean_name, object_id)
            )  engine=innodb ;
           -- changes undo test
           DROP TABLE app_notifica;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n CREATE TABLE app_notifica ("
                    + "\n     app_notifica_id integer not null auto_increment"
                    + "\n     , date datetime not null"
                    + "\n     , bean_name varchar (254) not null"
                    + "\n     , object_id integer not null"
                    + "\n     , json_info mediumtext"
                    + "\n     , primary key(app_notifica_id)"
                    + "\n     , index app_notifica_idx (bean_name, object_id)"
                    + "\n  )  engine=innodb ;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-

      } else if (version.equals("0.0.17")) {
         //-
         newVersion = "0.0.18";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE app_notifica  add  column fg_serving  boolean default 0;
           -- changes undo test
           ALTER TABLE app_notifica  drop column fg_serving;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE app_notifica  add  column fg_serving  boolean default 0;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-

      } else if (version.equals("0.0.18")) {
         //-
         newVersion = "0.0.19";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_space  add  column number_of_seating_booking integer;
           -- changes undo test
           ALTER TABLE spacemr_space  drop column number_of_seating_booking;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE spacemr_space  add  column number_of_seating_booking integer;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-

      } else if (version.equals("0.0.19")) {
         //-
         newVersion = "0.0.20";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_space_people_book  add people_number integer default 1 NOT NULL;
           -- changes undo test
           ALTER TABLE spacemr_space_people_book  drop column people_number;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n ALTER TABLE spacemr_space_people_book  add people_number integer default 1 NOT NULL;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
      } else if (version.equals("0.0.20")) {
         //-
         newVersion = "0.0.21";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           CREATE TABLE spacemr_space_user_presence ...
           -- changes undo test
           drop table CREATE TABLE spacemr_space_user_presence;
          */
         //-
         jdbcTemplate
            .update(""
                    + "\n    CREATE TABLE spacemr_space_user_presence ("
                    + "\n      spacemr_space_user_presence_id integer not null auto_increment"
                    + "\n      , app_user_id integer not null"
                    + "\n      , spacemr_space_id integer not null"
                    + "\n      , date_time datetime"
                    + "\n      , people_number  integer default 1 NOT NULL"
                    + "\n      , nota mediumtext"
                    + "\n      , primary key(spacemr_space_user_presence_id)"
                    + "\n      , foreign key (spacemr_space_id)"
                    + "\n                    references spacemr_space (spacemr_space_id)"
                    + "\n      , foreign key (app_user_id)"
                    + "\n                    references app_user (app_user_id)"
                    + "\n      , index spacemr_space_user_presence_date_time (date_time)"
                    + "\n    )  engine=innodb;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
      } else if (version.equals("0.0.21")) {
         //-
         newVersion = "0.0.22";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_space add  column user_white_list mediumtext;
           -- changes undo test
           ALTER TABLE spacemr_space drop column user_white_list;
          */
         //-
         jdbcTemplate
            .update("ALTER TABLE spacemr_space add  column user_white_list mediumtext;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         
      } else if (version.equals("0.0.22")) {
         //-
         newVersion = "0.0.23";
         System.out.println("DB update from ["+version+"] to ["+newVersion+"]");
         //-
         /*
           -- changes test
           ALTER TABLE spacemr_space add  column user_presence_progressive_code_map mediumtext;
           -- changes undo test
           ALTER TABLE spacemr_space drop column user_presence_progressive_code_map;
          */
         //-
         jdbcTemplate
            .update("ALTER TABLE spacemr_space add column user_presence_progressive_code_map mediumtext;"
                    );
         //-
         app_system_property.set("db_version",newVersion);
         //-
         
      } else {
         /* to format the java string from emacs "eval this region":
            (setq last-kbd-macro
             [home ?  ?+ ?  ?| backspace ?\" ?\\ ?n delete ?  ?\C-e ?| backspace ?O backspace ?\" delete home tab down])
            //-
            update app_system_property 
               set value = '0.0.18'
             where name = 'db_version';
          */
         rv = false;
      }
      return(rv);
   }

   public static String current_db_version = "0.0.23";


}
