
function app_spacemr_toolsqueries_doInitialize(callback) {
    log("page initialization...");
    callback();
}

function app_spacemr_toolsqueries_queries() {
    var divPage = $("<div>");
    var grid = app_ui_standard_getGrid(divPage);
    var ul = $("<ul/>");
    //-
    var createLink =  function(label, link){
        var l = $('<a/>')
            .text(label)
            .attr('href' , "#" + link )
            .click(function(event){
                app_initialization_setHashAndGoToPage(link);
            }) ;
        return l;
    };
    //-
    //-
    //-
    //- Le mie presenze
    //-
    //-
    if (app_userHasPermission("db_spacemr_space_user_presence_read")) {
        ul.append($("<li/>")
                  .append(createLink(gRb("db.spacemr_space_user_presence.my_presences_of_today"), "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list&qparams={%22where%22:{%22this_user_name%22:true,%22from_today%22:true}}")));
    }
    //-
    //-
    //-
    //- Le mie prenotazioni
    //-
    //-
    if (app_userHasPermission("db_spacemr_space_people_book_read")) {
        ul.append($("<li/>")
                  .append(createLink("Le mie Prenotazioni", "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list&qparams={%22allcolumns%22:[%22reason%22,%22date_from%22,%22date_to%22,%22repetition%22,%22stato%22,%22nota%22,%22spacemr_people_username%22,%22spacemr_people_first_name%22,%22spacemr_people_last_name%22,%22spacemr_people_role%22,%22spacemr_responsible_username%22,%22spacemr_responsible_first_name%22,%22spacemr_responsible_last_name%22,%22spacemr_responsible_role%22,%22spacemr_space_code%22,%22spacemr_space_description%22,%22transactions%22],%22pageNumber%22:1,%22pages%22:1,%22labelPrefix%22:%22db.spacemr_space_people_book%22,%22columns%22:[%22transactions%22,%22spacemr_space_code%22,%22spacemr_responsible_last_name%22,%22spacemr_people_last_name%22,%22reason%22,%22date_from%22,%22date_to%22,%22repetition%22,%22people_number%22,%22nota%22],%22tableid%22:%22table_spacemr_space_people_book_list%22,%22pageSize%22:50,%22where%22:{%22this_username%22:true,%22from_today%22:true}}")));
    }
    //-
    //-
    //- persone in ldap e relativi spazi occupati
    //-
    //-
    if (app_userHasPermission("db_spacemr_people_read")) {
        ul.append($("<li/>")
                  .append(createLink("Persone in ldap e relativi spazi occupati", "?page=app_spacemr_people__app_spacemr_people_list&qparams=%7B%0A%20%20%22allcolumns%22:%20%5B%0A%20%20%20%20%22username%22,%0A%20%20%20%20%22first_name%22,%0A%20%20%20%20%22last_name%22,%0A%20%20%20%20%22email%22,%0A%20%20%20%20%22role%22,%0A%20%20%20%20%22department%22,%0A%20%20%20%20%22fg_in_ldap%22,%0A%20%20%20%20%22fg_has_a_seat%22,%0A%20%20%20%20%22nota%22,%0A%20%20%20%20%22configuration%22,%0A%20%20%20%20%22spacemr_people_current_spaces%22,%0A%20%20%20%20%22spacemr_main_space_id_code%22,%0A%20%20%20%20%22spacemr_main_space_in_map_id_default_description%22%0A%20%20%5D,%0A%20%20%22pageNumber%22:%201,%0A%20%20%22pages%22:%2017,%0A%20%20%22labelPrefix%22:%20%22db.spacemr_people%22,%0A%20%20%22columns%22:%20%5B%0A%20%20%20%20%22fg_in_ldap%22,%0A%20%20%20%20%22fg_has_a_seat%22,%0A%20%20%20%20%22username%22,%0A%20%20%20%20%22first_name%22,%0A%20%20%20%20%22last_name%22,%0A%20%20%20%20%22spacemr_people_current_spaces%22,%0A%20%20%20%20%22role%22%0A%20%20%5D,%0A%20%20%22tableid%22:%20%22table_spacemr_people_list%22,%0A%20%20%22pageSize%22:%2050,%0A%20%20%22where%22:%20%7B%7D,%0A%20%20%22order%22:%20%5B%0A%20%20%20%20%7B%0A%20%20%20%20%20%20%22column%22:%20%22fg_in_ldap%22,%0A%20%20%20%20%20%20%22desc%22:%20true%0A%20%20%20%20%7D,%0A%20%20%20%20%7B%0A%20%20%20%20%20%20%22column%22:%20%22spacemr_people_current_spaces%22%0A%20%20%20%20%7D,%0A%20%20%20%20%7B%0A%20%20%20%20%20%20%22column%22:%20%22last_name%22%0A%20%20%20%20%7D%0A%20%20%5D%0A%7D")));
    }
    //-
    //-
    //- i miei beni inventariati
    //-
    //-
    if (app_userHasPermission("db_spacemr_inventario_read")) {
        ul.append($("<li/>")
                  .append(createLink("I miei beni inventariati", "?page=app_spacemr_inventario__app_spacemr_inventario_list_my")));
    }



    //-
    //-
    //- lines_to_csv
    //-
    //-
    if (app_userHasPermission("db_spacemr_space_user_presence_admin")) {
        let id = "id_user_white_list";
        let span = $('<span />');
        let csvgenout = $("<div>").hide();
        let csvgenout_text = $("<div>");
        let csvgenout_in   = 
            $("<textarea class='w3-input w3-border w3-round w3-theme-light'>")
            .val(`input.example
aaa
aa1
ab3
before
first
`)
        ;
        let csvgenfunction = function() {
            csvgenout_text.html("");
            let rv = "";
            let comma = "";
            let re1 = /([a-zA-Z0-9\\._-]+)@.*/i;
            let re2 = /(.*\s)?([a-zA-Z0-9\\._-]+)@.*/i;
            csvgenout_in.val().split("\n").forEach(function(line,i){
                line = line.trim();
                if (line != "") {
                    let reArray = re1.exec(line);
                    if (reArray == null) {
                    reArray = re2.exec(line);
                    }
                    if (reArray != null) {
                        line = reArray[1];
                    }
                    rv=rv + comma + line;
                    comma = ", ";
                }
            });
            csvgenout_text.html('"nome_gruppo": "'+rv+'"');
        };
        let csvgenclick = function(){
            if (csvgenout.html() == "") {
                csvgenout.show("fast");
                csvgenout.append(
                    app_ui_standard_appendRow(csvgenout)
                        .append(
                            app_ui_standard_button()
                                .click(function(event){
                                    csvgenfunction();
                                })
                                .append(gRb("db.sys.compute"))
                        )
                )
                    .append(csvgenout_text)
                    .append(csvgenout_in)
                ;
            } else {
                csvgenout.hide("fast");
                csvgenout.html("");
            }
        };
        let csvgen = $("<span>")
            .append(app_getIcon("database", 15))
            .append(" ")
            .append(gRb("db.spacemr_space.tools.lines_to_csv"))
            .click(csvgenclick);
        ul.append($("<li/>")
                  .append(csvgen)
                  .append(csvgenout)
                 );
    }

    
    //-
    //-
    //- space user_white_list mass update
    //-
    //-
    if (app_userHasPermission("db_spacemr_space_user_presence_admin")) {
	var li = $("<li/>");
        let massupdateout = $("<div>").hide();
        let massupdateout_text = $("<div>");
        var massupdateout_in   = 
            $("<textarea wrap='off' rows='24' class='w3-input w3-border w3-round w3-theme-light'>")
        ;
        var massupdateout_in_default_value   = `
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
`;
        app_doRequestMappingRequest("spacemr_space/spacemr_space_get_user_white_list_mass_update"
                                    , { }
                                    , function(content) {
                                        if ("space_user_white_list_mass_update" in content) {
                                            massupdateout_in.val(content["space_user_white_list_mass_update"]);
                                        } else {
                                            massupdateout_in.val(massupdateout_in_default_value);
                                        }
					if (content.spacemr_space_id_user_white_list_mass_update_help){
					    let span = $('<span />');
					    let helpout = $("<div>").hide();
					    let helpout_text = $("<div>");
					    let helpclick = function(){
						if (helpout.html() == "") {
						    helpout.html(content.spacemr_space_id_user_white_list_mass_update_help);
						    helpout.show("fast");
						} else {
						    helpout.hide("fast");
						    helpout.html("");
						}
					    };
					    let help = $("<span>")
						.append(" -- ")
						.append(app_getIcon("question-circle", 15))
						.append(gRb("db.spacemr_space.tools.help"))
						.click(helpclick);
					    li.append( help )
						.append(helpout);
					}
					//-
					
                                    });
        ;
        let massupdatefunction_show_out = function(content) {
            massupdateout_text.html("");
            var spaces_rv = content.spaces_rv;
            var keys = Object.keys(spaces_rv);
            keys.sort();
            keys.forEach(function(k) {
                var sp = spaces_rv[k];
                var div = $("<div>");
                var color = "orange";
                if (sp.status.startsWith("ok - same value")) {
                    color = "GreenYellow";
                } else if (sp.status.startsWith("ok - updated")) {
                    color = "Green";
                }
                var edit = "";
                if (sp.spacemr_space_id) {
                    edit = $('<span>').attr('title',gRb("db.spacemr_space.update.title")).append(
                        app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update"
                                             + "&spacemr_space_id="+sp.spacemr_space_id
                                            )
                            .append(app_getIcon("edit", 15))
                    );
                }
                div
                    .append(app_getIcon("circle", 15).css("color",color) )
                    .append(" ")
                    .append(edit)
                    .append(" ")
                    .append($("<span>").append(k))
                    .append(" - ")
                    .append($("<span>").append(sp.status))                
                massupdateout_text.append(div);
            });
        }
        let massupdatefunction = function() {
            massupdateout_text.html(gRb("db.sys.computing")+"...");
            var gruppi  = {};
            var criteri = {};
            var eval_error = "";
            var settimanale = function(aula, criterio) {
                var rv = {};
                rv["algoritmo"] = "settimanale_gruppi";
                var gruppi_usati = {};
                var nomi_giorni = {
                    "lun": "gruppo_lunedi"
                    , "mar": "gruppo_martedi"
                    , "mer": "gruppo_mercoledi"
                    , "gio": "gruppo_giovedi"
                    , "ven": "gruppo_venerdi"
                    , "sab": "gruppo_sabato"
                    , "dom": "gruppo_domenica"
                };
                Object.keys(criterio).forEach(function(key) {
                    // console.log("--" + aula + " " + key + " " +  criterio[key]);
                    if (criterio[key].trim() != ""){
                        key.split(",").forEach(function(day){
                            var d = day.trim();
                            if (!(d in nomi_giorni)){
                                throw('day not found ['+d+']');
                            }
                            criterio[key].split(",").forEach(function(gns) {
                                var gn = gns.trim();
                                if (gn in gruppi) {
                                    gruppi_usati[gn] = true;
                                } else {
                                    throw('group name not found ['+gn+']')
                                }
                            });
                            var crit_d = criterio[key];
                            if (nomi_giorni[d] in rv) {
                                crit_d = rv[nomi_giorni[d]] + ", " + crit_d;
                            }
                            rv[nomi_giorni[d]] = crit_d;
                        });
                    }
                });
                var groups = [];
                Object.keys(gruppi_usati).forEach(function(gn) {
                    groups.push({"nome": gn, "users": gruppi[gn]})
                });
                rv["gruppi"] = groups;
                criteri[aula] = rv;
            }
            let union_groups = function(comma_separated_groups_names) {
                var obj = {};
                let rv = [];
                comma_separated_groups_names.split(",").forEach(function(gns) {
                    var gn = gns.trim();
                    if (gn in gruppi) {
                        // console.log("gruppi["+gn+"]: " + gruppi[gn]);
                        gruppi[gn].split(",").forEach(function(ids) {
                            var id = ids.trim();
                            obj[id] = id;
                        });
                    } else {
                        throw('group name not found ['+gn+']')
                    }
                });
                for (var k in obj) {
                    if (obj.hasOwnProperty(k))  // <-- optional
                        rv.push(obj[k]);
                }
                // console.log("comma_separated_groups_names: " + comma_separated_groups_names)
                // console.log("rv: " + rv);
                return(rv.join(','));
            }
            var lista_utenti = function(aula, lista_gruppi) {
                var rv = {};
                rv.algoritmo = "user_list";
                rv.users = union_groups(lista_gruppi);
                criteri[aula] = rv;
            }
            var alterne = function(aula, gruppo_dispari, gruppo_pari) {
                var rv = {};
                rv["algoritmo"] = "settimane_alterne";
                rv.utenti_dispari = union_groups(gruppo_dispari);
                rv.utenti_pari    = union_groups(gruppo_pari);
                criteri[aula] = rv;
            }
            var everyone = function(aula) {
                var rv = {};
                rv["algoritmo"] = "everyone";
                criteri[aula] = rv;
            }
            var none = function(aula) {
                var rv = {};
                criteri[aula] = rv;
            }
            try {
                eval(massupdateout_in.val());
                //-
            } catch (error) {
                eval_error = "Error evaluating: " + error;
            } 
            if (eval_error == "") {
                // massupdateout_text.html("");
                // massupdateout_text
                //     .append($("<pre>").append(JSON.stringify(criteri,null,2))
                //            );
                app_doRequestMappingRequest("spacemr_space/spacemr_space_set_user_white_list_mass_update"
                                            , { space_user_white_list_mass_update: massupdateout_in.val() }
                                            , function(content) {
                                                // console.log("saved.");
                                            });
            }
            if (eval_error == "") {
                if (!confirm(gRb("db.spacemr_space.tools.json_mass_update.reallydo"))) {
                    return;
                }
                app_doRequestMappingRequest("spacemr_space/spacemr_space_white_list_mass_update"
                                            , { spaces: criteri }
                                            , function(content) {
                                                massupdateout_text.html("");
                                                massupdatefunction_show_out(content);
                                            });
            }
            if (eval_error != "") {
                massupdateout_text.html(eval_error);
            }
        };
        let massupdateclick = function(){
            if (massupdateout.html() == "") {
                massupdateout.show("fast");
                massupdateout.append(
                    app_ui_standard_appendRow(massupdateout)
                        .append(
                            app_ui_standard_button()
                                .click(function(event){
                                    massupdatefunction();
                                })
                                .append(gRb("db.sys.compute"))
                        )
                )
                    .append(massupdateout_text)
                    .append(massupdateout_in)
                ;
            } else {
                massupdateout.hide("fast", function(){
                    massupdateout.html("");
                    massupdateout_text.html("");
                });
            }
        };
	li.append($("<span>").click(massupdateclick)
                  .append(app_getIcon("save", 15))
                  .append(" ")
                  .append(gRb("db.spacemr_space.tools.json_mass_update")))
            .append(massupdateout);
        ul.append(li);
    }


    
    //-
    //- first_login_note_acceptance
    //-
    if (!sys_session.userData.default_user) {
        app_doRequestMappingRequest("app_user/app_user_get_app_first_login_note_acceptance"
                                    , { }
                                    , function(content) {
                                        if(content.app_first_login_note) {
                                            var li = $("<li/>");
                                            ul.append(li);
                                            li.append($("<div>").text(gRb("db.sys.app_first_login_note")))
                                            if(content.app_first_login_note_data) {
                                                li.append(appConvertTimestampToString(content.app_first_login_note_data.app_first_login_note_date));
                                            }
                                            li.append($("<div>").html(content.app_first_login_note))
                                        }
                                    });
    }

    
    //-
    //-
    grid.append(ul);
    //-
    // app_test_showMobilePropertiesInfo();
    appSetPage(divPage, gRb("db.spacemr_menu_queries.title"));
}





