
  CREATE TABLE app_log (
      app_log_id integer not null auto_increment
      , date datetime not null
      , object_type integer not null
      , object_id integer not null
      , user_name varchar (254)
      , parameter_values mediumtext
      , primary key(app_log_id)
      , index app_log_idx (object_type, object_id)
   )  engine=innodb ;

  CREATE TABLE app_system_log (
      app_system_log_id integer not null auto_increment
      , date datetime not null
      , user_name varchar (254)
      , category varchar (254)
      , value mediumtext
      , primary key(app_system_log_id)
   )  engine=innodb ;

   CREATE TABLE app_user (
      app_user_id integer not null auto_increment
      , user_name varchar (254) not null
      , first_name varchar (254)
      , last_name varchar (254)
      , email varchar (254)
      , created datetime DEFAULT NULL
      , nota mediumtext
      , password varchar (254)
      , password_salt varchar (254)
      , ldap_info mediumtext
      , primary key(app_user_id)
      , unique (user_name)
   )  engine=innodb ;


   CREATE TABLE app_permission (
      app_permission_id integer not null auto_increment
      , name varchar (254) not null
      , nota mediumtext
      , primary key(app_permission_id)
      , unique (name)
   )  engine=innodb ;

   CREATE TABLE app_role (
      app_role_id integer not null auto_increment
      , name varchar (254) not null
      , nota mediumtext
      , primary key(app_role_id)
      , unique (name)
   )  engine=innodb ;

   CREATE TABLE app_role_permission (
      app_role_permission_id integer not null auto_increment
      , app_role_id integer not null
      , app_permission_id integer not null
      , primary key(app_role_permission_id)
      , unique (app_role_id, app_permission_id)
      , foreign key (app_role_id) references app_role (app_role_id)
      , foreign key (app_permission_id) references app_permission (app_permission_id)
   )  engine=innodb ;

   CREATE TABLE app_group (
      app_group_id integer not null auto_increment
      , name varchar (254) not null
      , nota mediumtext
      , primary key(app_group_id)
      , unique (name)
   )  engine=innodb ;

   CREATE TABLE app_group_role_user (
      app_group_role_user_id integer not null auto_increment
      , app_group_id integer not null
      , app_role_id integer not null
      , app_user_id integer not null
      , primary key(app_group_role_user_id)
      , unique (app_group_id, app_role_id, app_user_id)
      , foreign key (app_group_id) references app_group (app_group_id)
      , foreign key (app_role_id) references app_role (app_role_id)
      , foreign key (app_user_id) references app_user (app_user_id)
   )  engine=innodb ;


   CREATE TABLE app_system_property (
      app_system_property_id integer not null auto_increment
      , name varchar (254) not null
      , description varchar (254)
      , value mediumtext
      , value_default mediumtext
      , nota mediumtext
      , primary key(app_system_property_id)
      , unique (name)
   )  engine=innodb ;

   CREATE TABLE app_user_property (
      app_user_property_id integer not null auto_increment
      , app_user_id integer not null
      , name varchar (254) not null
      , value mediumtext
      , primary key(app_user_property_id)
      , foreign key (app_user_id) references app_user (app_user_id)
      , unique app_user_property_idx (app_user_id, name)
   )  engine=innodb ;


   create table app_file
   (
      app_file_id integer not null auto_increment
      , owner_object_type integer
      , owner_object_id integer
      , file_name varchar (254)
      , file_size integer
      , primary key(app_file_id)
      , index (owner_object_type, owner_object_id)
   )  engine=innodb ;

   CREATE TABLE spacemr_people (
      spacemr_people_id integer not null auto_increment
      , username varchar (254) not null
      , first_name varchar (254)
      , last_name varchar (254)
      , email varchar (254)
      , role       varchar (254)
      , department varchar (254)
      , fg_in_ldap boolean default 0
      , fg_has_a_seat boolean default 1
      , nota mediumtext
      , configuration mediumtext
      , primary key(spacemr_people_id)
      , unique app_user_property_idx (username)
   )  engine=innodb ;


   CREATE TABLE spacemr_space_type (
      spacemr_space_type_id integer not null auto_increment
      , name varchar (254) not null
      , description varchar (254)
      , nota mediumtext
      , configuration mediumtext
      , primary key(spacemr_space_type_id)
   )  engine=innodb ;


   CREATE TABLE spacemr_space_people_type (
      spacemr_space_people_type_id integer not null auto_increment
      , name varchar (254) not null
      , description varchar (254)
      , fg_is_a_seat boolean default 0
      , nota mediumtext
      , primary key(spacemr_space_people_type_id)
   )  engine=innodb ;


   CREATE TABLE spacemr_space (
      spacemr_space_id integer not null auto_increment
      , app_group_id integer not null
      , spacemr_space_type_id integer not null
      , spacemr_space_in_id integer
      , code varchar (254) not null
      , description varchar (254) not null
      , number_of_seating     integer
      , number_of_seating_booking     integer  default 0
      , area_in_meters2       decimal(12,5) default 0
      , user_white_list       mediumtext
      , user_presence_progressive_code_map mediumtext
      , nota mediumtext
      , primary key(spacemr_space_id)
      , unique spacemr_space_code_idx (code)
      , foreign key (app_group_id) references app_group (app_group_id)
      , foreign key (spacemr_space_type_id)
                    references spacemr_space_type (spacemr_space_type_id)
      , foreign key (spacemr_space_in_id)
                    references spacemr_space (spacemr_space_id)
   )  engine=innodb ;

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

   CREATE TABLE spacemr_space_people (
      spacemr_space_people_id integer not null auto_increment
      , spacemr_space_people_type_id integer not null
      , spacemr_space_id integer not null
      , spacemr_people_id integer not null
      , date_from date not null
      , date_to   date default null
      , description varchar (254)
      , nota mediumtext
      , primary key(spacemr_space_people_id)
      , foreign key (spacemr_space_people_type_id)
                    references spacemr_space_people_type (spacemr_space_people_type_id)
      , foreign key (spacemr_space_id)
                    references spacemr_space (spacemr_space_id)
      , foreign key (spacemr_people_id)
                    references spacemr_people (spacemr_people_id)
      , index spacemr_space_people_date_from (date_from)
      , index spacemr_space_people_date_to   (date_to)
   )  engine=innodb ;


   CREATE TABLE spacemr_inventario (
      spacemr_inventario_id integer not null auto_increment
      , spacemr_space_id integer  not null
      , spacemr_people_id integer not null
      , stato varchar (30) not null default 'da_controllare'
      , fg_validato boolean default 0
      , fg_adesivo  boolean default 0
      , codice_inventario_unimore varchar (254)
      , inventario_numero varchar (30)
      , inventario_numero_sub varchar (30)
      , inventario_etichetta varchar (254)
      , tipo_carico_scarico varchar (30)
      , carico_data date
      , attivita_tipo varchar (30)
      , descrizione   mediumtext
      , categoria_inventario varchar (30)
      , valore decimal(12,5)
      , fornitore varchar (254)
      , scarico_numero_buono varchar (30)
      , scarico_data date
      , old_values         mediumtext
      , old_values_changes mediumtext
      , nota mediumtext
      , primary key(spacemr_inventario_id)
      , foreign key (spacemr_space_id)
                    references spacemr_space (spacemr_space_id)
      , foreign key (spacemr_people_id)
                    references spacemr_people (spacemr_people_id)
   )  engine=innodb ;


   CREATE TABLE spacemr_space_people_book (
     spacemr_space_people_book_id integer not null auto_increment
     , spacemr_people_id integer not null
     , spacemr_responsible_id integer not null
     , spacemr_space_id integer not null
     , people_number  integer default 1 NOT NULL
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
   

   CREATE TABLE app_notifica (
       app_notifica_id integer not null auto_increment
       , date datetime not null
       , bean_name varchar (254) not null
       , object_id integer not null
       , json_info mediumtext
       , fg_serving  boolean default 0
       , primary key(app_notifica_id)
       , index app_notifica_idx (bean_name, object_id)
    )  engine=innodb ;

   CREATE TABLE spacemr_space_user_presence (
     spacemr_space_user_presence_id integer not null auto_increment
     , app_user_id integer not null
     , spacemr_space_id integer not null
     , date_time datetime
     , people_number  integer default 1 NOT NULL
     , nota mediumtext
     , primary key(spacemr_space_user_presence_id)
     , foreign key (spacemr_space_id)
                   references spacemr_space (spacemr_space_id)
     , foreign key (app_user_id)
                   references app_user (app_user_id)
     , index spacemr_space_user_presence_date_time (date_time)
   )  engine=innodb;
   
