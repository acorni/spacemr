[TOC]

This file contains a set of utility code collected during the application usage.

## links
 * test-me
    markdown README_note.md > README_note.html
    markdown_py -x toc  README_note.md > README_note.html
    firefox README_note.html


## Developing javascript while  "gradle run"

because of the separation of "build" static resources from "src" static resource
to speed up the the development of the javascript client,
I edit the "build" code,
and then I rsync it back to src
like this

       cd ...../spacemr; rsync -c --recursive --links --owner --times --group --perms --progress --stats --partial build/resources/main/static src/main/resources


## Persone LDAP search, importa la configurazione di un utente da LDAP
[Al 20180704-19:10] per abilitare la ricerca LDAP occorre:

 - in "proprieta' di sistema" creare la variabile
 
        name:        spacemr_ldap_search_configuration
        description: Configuratione LDAP per la ricerca di Persone in UNIMORE
        value:       {
         "provider": "ldaps://ldap.ing.unimo.it:636/dc=unimore,dc=it"
         , "query": "(&(uid=${user_name}*)(givenName=${user_first_name}*)(sn=${user_last_name}*))"
        }
       
 - tra le permission creare
 
       nome: spacemr_people_ldap_search
       nota: abilita la ricerca in ldap (spacemr_ldap_search_configuration)
    
   aggiungere tale permission al ruolo che deve effettuare la ricerca


## apache proxy configuration for "fail2ban"
 - system property
 
        auth_x-forwarded-for
    
    - authentication - detects ips forwarded by mod_proxy - ip
      address of the proxy, leave blank for no proxy
    - a possible value is
       0:0:0:0:0:0:0:1


## remember me configuration
 - system properties to configure:
   - login_remember_me_enabled:      true
   - login_remember_me_days_to_live: 30


## ldap configuration
 - permissions
   - spacemr_people_ldap_search  - enable search on ldap.
 - spacemr_ldap_search_configuration
   <pre> 
       {
        "provider": "ldaps://ldap5.unimo.it:636/dc=unimore,dc=it"
        , "query_people_ldap_search": "(&(uid=${user_name}*)(givenName=${user_first_name}*)(sn=${user_last_name}*))"
        , "query": "(&(|(ou=dottorandi)...))"
        ,    "parser": "function(ldapRow) {\n    ...    return(rv);\n}\n"
       }
   </pre> 
 - system properties to configure:
   - auth_custom_context_name:       authLdapGeneric
   - auth_custom_ldap_config
   
         rPrefix=ldap_
         query=uid=${user}
         PROVIDER_URL=ldaps://ldap.ing.unimo.it:636/dc=unimore\,dc=it
         SECURITY_PRINCIPAL=uid=${user}\,ou=people\,dc=unimore\,dc=it
         role110=spazi-dief:ldap_user:unimoreDiporg2...
         role120=spazi-dief:ldap_user:ou:Scuola ...

   - auth_custom_fallback_to_db: false


## shibboleth configuration
 - permissions
   - spacemr_people_ldap_search  - enable search on ldap.
 - system properties to configure:
   - auth_custom_context_name
      authShibbolethGeneric
   - auth_single_sign_on_protected_url
      authentication/auth_shibboleth_generic
   - auth_custom_shibboleth_config
      rPrefix=ldap_
      role110=spazi-dief:ldap_user:unimoreDiporg2:{1}{300024}Dipartimento di Ingegneria "Enzo Ferrari"
      role120=spazi-dief:ldap_user:ou:Scuola di D.R. in INFORMATION AND COMMUNICATION TECHNOLOGIES (ICT)
      ...



## spaces white-list configuration
 - system properties to configure:
    - spacemr_space_id_user_white_list_plugins
       <pre> 
          let id = "id_user_white_list";
          let span = $('<span />');
          let sheetsout = $("<div>").hide();
          let sheetsout_text = $("<div>");
          let sheetsclick = function(){
              if (sheetsout.html() == "") {
                  sheetsout.show("fast");
                  var iframe =
                      $('<iframe id="spacemr_space_import_excel_white_list_frame" src="/spacemr_space_import_excel_white_list/index.html" title="excel_to_json">')
                      .width($("#"+id+"").width())
                .css("border","none")
            ;
            iframe.load(function(){
                var ta            = iframe.contents().find("#xlx_json");
                var click_trigger = iframe.contents().find("#click_trigger");
                ta.hide();
                click_trigger
                    .hide()
                    .click(function(){
                        //-
                        var j = JSON.parse(ta.val());
                        $('#id_user_white_list').val(app_JSONStringify(j));
                });
            })
                  sheetsout
                      .append( gRb("db.spacemr_space.tools.select_sheet_file") )
                      // .append( ttt )
                      .append(iframe);
                  //-
                  //-
              //-
              } else {
                  sheetsout.hide("fast");
                  sheetsout.html("");
              }
          };
          let sheets = $("<span>")
              .append(" -- ")
              .append(app_getIcon("table", 15))
              .append(gRb("db.spacemr_space.tools.sheets"))
              .click(sheetsclick);
          grid.find("label[id='"+id+"_label']")
              .after( sheets ) ;
          grid.find("textarea[id='"+id+"']")
              .after( sheetsout ) ;
       </pre> 

    - spacemr_space_id_user_white_list_help .
    
          Esempi di formati JSON accettati:
          <ul>
          <li>Nessuno - e' la white-list di default,
              nessun utente con la "permission" di marcare la presenza
              puo' inserire la presenza in questo spazio.
              <pre>
               {
                "algorithm": "none"
               }
              </pre>
              </li>
          <li>Chiunque - qualunque utente con la "permission" di marcare la presenza
              puo' inserire la presenza in questo spazio.
              <pre>
               {
                "algorithm": "everyone"
               }
              </pre>
              </li>
          <li>Lista Utenti - lista degli utenti che possono marcare la presenza in questo spazio:
              <pre>
               {
                "algorithm": "user_list"
                , "users": "aaa,bbb,ccc"
               }
              </pre>
              </li>
          <li>settimane_alterne - utenti definiti per settimane pari e dispari.
              Le settimane si contano a partire dall'inizio dell'anno.
              <pre>
              {
                "algoritmo": "settimane_alterne",
                "utenti_pari": "aaa,bbb",
                "utenti_dispari": "ccc,ddd"
              }
              </pre>
              </li>
          <li>settimanale - lista di utenti per ogni giorno della settimana
              <pre>
              {
                "algoritmo": "settimanale",
                "utenti_lunedi": "aaa,bbb",
                "utenti_martedi": "ccc,ddd",
                "utenti_mercoledi": "eee,fff",
                "utenti_giovedi": "ggg,hhh",
                "utenti_venerdi": "iii,lll"
              }
              </pre>
              </li>
          <li>settimanale_gruppi - funziona come il "settimanale" ma la lista degli
              utenti e' dichiarata come "gruppo":
              <pre>
              {
                "algoritmo": "settimanale_gruppi",
                "gruppi": [
                  {"nome": "rossi", "utenti": "a1, a2, a3, a4"}
                  , {"nome": "gialli", "utenti": "b1, b2, b3, b4"}
                  , {"nome": "verdini", "utenti": "v1, v2, v3, v4"}
                ],
                 "gruppo_lunedi": "rossi",
                 "gruppo_martedi": "gialli",
                 "gruppo_mercoledi": "verdini",
                 "gruppo_giovedi": "rossi",
                 "gruppo_venerdi": "rossi,gialli,verdini"
               }
               </pre>
               </li>
           <li>data_per_data - per ogni data si specifica un gruppo o una lista di utenti:
               <pre>
               {
                 "algoritmo": "data_per_data",
                 "gruppi": [
                   {"nome": "rossi", "utenti": "a1, a2, a3, a4"}
                   , {"nome": "gialli", "utenti": "b1, b2, b3, b4"}
                   , {"nome": "verdini", "utenti": "v1, v2, v3, v4"}
                 ],
                 "date": [
                     {"data": "20210301", "gruppi": "rossi"}
                   , {"data": "20210302", "gruppi": "rossi,gialli"}
                   , {"data": "20210303", "utenti": "u1, u2, u5"}
                   , {"data": "20210304", "utenti": "u3, u4", "gruppi": "verdini"}
                  ]
               }
               </pre>
               </li>
          </ul>
          Esempi di file excel accettati: <a target="_blank" href="https://your_site">https://your_site_help</a>

    - spacemr_space_id_user_white_list_mass_update_help
       Il "Mass update" per i Criteri per la gestione accesso utenti agli spazi
       e' uno strumento per la gestione dei "Criteri per la gestione accesso utenti agli spazi"
       di piu' spazi contemporaneamente.
       <br/>
       E' basato su uno script in cui vengono definiti gruppi
       ed invocate funzioni (settimanale, alterne, etc.)
       che definiscono i criteri di accesso degli spazi in base ai gruppi definiti.
       <br/>
       Vantaggi:
       <ul>
         <li>Definisce in modo compatto i vincoli di accesso alle aule,
             i gruppi di utenti sono scritti una sola volta ed usati nelle aule che occupano.
             </li>
         <li>Effettua controlli preliminari sui nomi dei gruppi
             segnalando eventuali inconsistenze
             </li>
         <li>Quando si calcola vengono controllati in una sola volta tutti
             gli spazi interessati
             ed aggiornati solo quelli per cui vi e' stata una modifica.
             Ad esempio quando si altera la composizione di un gruppo
             di untenti, verranno aggiornati solo gli spazi in cui e' coinvolto
             tale gruppo.
             </li>
         <li>Il mass-update e' "persistente", lo script viene salvato tra le proprieta'
             dell'utente che lo scrive.
             <br>
             lo script e' testo libero, puo' essere salvato in qualunque editor di testi
             o inviato anche nel corpo di una email.
             </li>
         <li>nello script possono essere inseriti dei commenti o commentate parte
             delle informazioni.</li>
       </ul>
       Funzioni definite:
       <ul>
        <li><i>alterne</i>
            <br/>
            definizione del criterio di accesso
            <i>settimane_alterne</i>.
            sintassi:
            <pre>
             alterne ("codice-aula", "nome_gruppo_dispari", "nome_gruppo_pari")
            </pre>
             </li>
        <li><i>settimanale</i>
            <br/>
            definizione del criterio di accesso
            <i>settimanale_gruppi</i>.
            <br/>
            sintassi:
            <pre>
             settimanale ("codice-aula", criterio)
            </pre>
            dove criterio e' un insieme di dichiarazioni
            <pre>
             lista-giorni-della-settimana: lista-gruppi-che-accedono-tali-giorni
            </pre>
            i nomi dei giorni della settimana accettati sono:
            <pre>
             lun, mar, mer, gio, ven, sab, dom
            </pre>
            i nomi dei gruppi accettati sono tutti e soli quelli definiti
            nello script
             </li>
        <li><i>everyone</i>
            <br/>
            definizione dell'omonimo criterio di accesso,
            chiunque puo' entrare.
            <br/>
            sintassi:
            <pre>
             everyone("codice-aula")
            </pre>
             </li>
        <li><i>none</i>
            <br/>
            permette la definizione dell'omonimo criterio di accesso,
            nessuno puo' entrare.
            Viene interpretato svuotando (stringa vuota)
            il campo "Criteri per la gestione accesso utenti"
            del relativo spazio.
            <br/>
            sintassi:
            <pre>
             none("codice-aula")
            </pre>
             </li>
       </ul>
       Esempio di script:
       <pre>
         //- mass-update criteri di accesso.
         //-
         gruppi = {
                "LM-AAE-1-A": "utente1, utente2"
             ,  "LM-AAE-1-B": "utente3, utente4"
             ,  "LM-CivileAmbientale-1-A": "utente5, utente6"
             ,  "LM-CivileAmbientale-1-B": "utente7, utente8"
         };
         //-
         //- sintassi di alterne:
         //-   alterne ("codice-aula", "nome_gruppo_dispari", "nome_gruppo_pari")
         //- esempio:
         alterne    ("codice-aula1", "LM-AAE-1-A", "LM-AAE-1-B" );
         //-
         //- sintassi di settimanale:
         //-   settimanale ("codice-aula", criterio)
         //- dove criterio e' un insieme di dichiarazioni
         //-   lista-giorni-della-settimana: lista-gruppi-che-accedono-tali-giorni
         //- esempi:
         settimanale("codice-aula2", {
                 "lun": "LM-AAE-1-A"
               , "mar": "LM-AAE-1-B"
               , "mer": "LM-CivileAmbientale-1-A"
               , "gio": "LM-CivileAmbientale-1-B"
               } );
         settimanale("codice-aula3", {
                 "lun, mar, ven": "LM-AAE-1-A"
               , "mer, gio, ven": "LM-AAE-1-B"
               } );
         settimanale("codice-aula4", {
                 "lun, mer": "LM-AAE-1-A, LM-AAE-1-B"
               , "mar, gio": "LM-CivileAmbientale-1-A, LM-CivileAmbientale-1-B"
               } );
       </pre>
       <br/>


## Application custom "about" text
Custom html text can be added in the top of the about page
by editing the system properties
 app_about_custom


## first login note
Custom html text can be shown to the user on every page
until he does not acknlowlege it
then it will be hidden.
Such will however appear in the "useful queries" page.
The html of the text is the one in the system property:

 - app_first_login_note - if is null/absent/blank it is not shown
    the text you want the user accepts
   e.g. Io sottoscritto dichiaro di accettare ...
 - it stores the "ok" date in the  app_user_property
    app_first_login_note_data - json object { app_first_login_note_date: long }
 - useful sql:
    #
    # to see users that accepted:
    select app_user_id, value from app_user_property where name = 'app_first_login_note_data';
    #
    # to reset all users
    delete from app_user_property where name = 'app_first_login_note_data';
 - in the page
    http://localhost:8080/#?page=app_spacemr_toolsqueries__app_spacemr_toolsqueries_queries
   create a re-view app_first_login_note and acceptance date
 - todo: in the page
    app_test__app_test_loadTestPage
   create a tool that resets all the users app_first_login_note_date

