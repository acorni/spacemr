# spacemr

SpaceMR - java/javascript application to track student presence in University spaces during COVID19.

[TOC]

## Introduction

This application has been developed and
is used in my University to track presences during lessons
and in my Department to book spaces (offices, labs, etc.) during the covid19 emergency in italy.

 * [2 minutes of demo - how navigate presences](https://web.ing.unimo.it/~acorni/shared/20210421%20-%20spacemr%20-%20presenze%20-%20come%20navigare%20le%20presenze%20per%20e%20spazi%20e%20data.ogv)
 * [3 minutes of demo - booking](http://web.ing.unimo.it/~acorni/shared/20200625%20-%20spacemr%20-%20demo%20prenotazioni.ogv)

Main features are:

 * shows presences and space bookings on map and on calendars
 * presence generate a daily "pass" (badge) for access control
 * quick booking using clicks on the map.
 * integrated (in browser) map editor.
 * customizable workflow (with email notification) for the booking request.
 * hierarchical view of the bookings both on maps and on calendars.
 * calendars (bookings and presences) for every space.
 * quick bookings approval features on both map and calendars.

The application is in italian and can be interazionalized,
all the user displayed strings are stored in a json file:
  src/main/resources/language/language_it.json


## Introduzione

Questa applicazione e' stata sviluppata ed usata
all'Universita' degli studi di Modena e Reggio Emilia per tracciare le presenze
e nel dipartimento universitario in cui lavoro (DIEF)
per prenotare gli spazi (studi, laboratori, etc.) durante l'emergenza covid19.

 * [2 minuti di demo - navigare le presenze](https://web.ing.unimo.it/~acorni/shared/20210421%20-%20spacemr%20-%20presenze%20-%20come%20navigare%20le%20presenze%20per%20e%20spazi%20e%20data.ogv)
 * [3 minuti di demo - prenotare spazi](http://web.ing.unimo.it/~acorni/shared/20200625%20-%20spacemr%20-%20demo%20prenotazioni.ogv)

Funzionalita' principali:

 * mostra presenze e prenotazioni sia sulla mappa sia sui calendari
 * le presenze possono generare un "pass" (bage) giornaliero per i controlli d'accesso all'edificio
 * creazione veloce della prenotazione facendo click sul nome della persona o del laboratorio sulla mappa
 * le mappe vengono create e modificate via browser (editor di mappe integrato)
 * il flusso con cui sono gestite le richieste e' personalizzabile con eventuale invio di notifiche via email.
 * viste gerarchiche delle prenotazioni sia sulle mappe sia su calendari
 * calendario per ogni spazio
 * funzionalita' di approvazione rapida sia sulle mappe sia sui calendari


## Credits and Technologies

SpaceMR is a project started at the University of Modena and
Reggio Emilia by Alberto Corni.

Credits, libraries and tools used by the application:

 * Java - GNU General Public License (GNU GPL) version 2 with a linking exception - https://openjdk.java.net/
 * JQuery - JavaScript library - MIT license - https://jquery.org/license/
 * Spring - Java develpmente Framework - Apache License 2.0 - https://spring.io/
 * Moment - dates and times in JavaScript - MIT license - https://momentjs.com/
 * pikaday - refreshing JavaScript Datepicker - MIT license - https://pikaday.com/
 * hikariCP - JDBC Connection Pooling - Apache License 2.0 - https://github.com/brettwooldridge/HikariCP/
 * freemarker - template language - Apache License 2.0 - https://freemarker.apache.org/
 * fontawesome - svg images - CC BY 4.0 License - https://fontawesome.com/
 * logback - logging framework - EPL v1.0 and the LGPL 2.1 - http://logback.qos.ch/
 * com.sun.mail - email client - Common Development and Distribution License (CDDL) v1.1 and GNU General Public License (GPL) v2 with Classpath Exception - https://javaee.github.io/javamail/JavaMail-License/
 * QRCode.js - javascript QRCode generator - MIT License - https://davidshimjs.github.io/qrcodejs/


## Development - setup and compiling:

Requirements

 * get [mariadb](https://mariadb.org/)
 * get [java](https://openjdk.java.net/)
 * get [gradle](https://gradle.org/)
 * get [git](https://git-scm.com/)
 * other, a good editor, a browser, many time and patience

on debian 10 burst (and may be Ubuntu)

    sudo apt-get install \
        git default-jdk gradle mariadb-server

start the application from sources

 * get the soruces

         git clone whatsever/is/the/path/spacemr

   the following path are relative to the dir spacemr

 * configure your DB connection
   editing the file

         app_data/config.json

 * connect to mariadb and create an empty db
   and a user with rights to write such db:

         mysql -u root
         create database tomcatspacemr;
         grant all on tomcatspacemr.* to tomcatspacemr@localhost identified by 'tomcatspacemrp';
         \q

 * run your local instance of the application

       gradle bootRun

   this will create all db tables and will
   add some default values.

 * connect for the first time to the development application,
   open the url

        http://localhost:8080/

   and login with the credentials

        root / approot


 * create the "war" for deploying the application

        gradle war
        ls -las build/libs/spacemr.war



## Deploy on Debian 10



Requirements:

 * get [mariadb](https://mariadb.org/)
 * get [java](https://openjdk.java.net/)
 * get [tomcat](http://tomcat.apache.org/)
 * get the application "war" by compiling or downloading it
   from [spacemr.war](https://web.ing.unimo.it/~corni/shared/spacemr.war)

on debian 10 burst

    sudo apt-get install \
      tomcat9 mariadb-server

steps

 * connect to mariadb and create an empty db
   and a user with rights to write such db:

         mysql -u root
         drop database tomcatspacemr;
         create database tomcatspacemr;
         grant all on tomcatspacemr.* to tomcatspacemr@localhost identified by 'tomcatspacemrp';
         \q

 * copy the application file spacemr.war
   in the webapps directory of tomcat.

   On debian 10 burst

         gradle war
         scp build/libs/spacemr.war root@your.spacemr.server:/var/lib/tomcat9/webapps

   or

         ssh root@your.spacemr.server
         cd /var/lib/tomcat9/webapps
         wget https://web.ing.unimo.it/~corni/shared/spacemr.war

 * the application will respond at the url

        http://your.spacemr.server:8080/spacemr

   and login with the credentials

        root / approot

 * create the documentation directory,

        mkdir /var/lib/tomcat9/webapps/spacemr_demo_data/docs
        chown tomcat:tomcat /var/lib/tomcat9/webapps/spacemr_demo_data/docs

    and edit the system property by the web interface (Menu / Elenco proprieta di sistema)

        configuration_docs_directory

    or by sql

       update app_system_property set value =
          '/var/lib/tomcat9/webapps/spacemr_demo_data/docs'
         where name = 'configuration_docs_directory';



## How to Contribute to the Project

I am new to public open source projects,
so please contact me at

     alberto.corni at unimore.it

Things I would like to learn to do:

  * separate library, auto generation code from production/application repository.

    E.g. The application is able to generate code to "edit" a new Database table.

           gradle bootRun -Pargs="toolDb describeTable spacemr_space spacemr_space"

    But it should be done in production only.

    Also there is a "library" for javascript

            src/main/resources/static/js/app_initialization.js

    that should be separated along with the "mattoni" management.
