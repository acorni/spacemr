
// var sys_spacemr_inventario_common_data = {};

function app_spacemr_inventario_doInitialize(callback) {
    log(" -- in spacemr_inventario page initialization...");
    var initfunction = function(callback) {
        app_doRequestMappingRequest("spacemr_inventario/spacemr_inventario_workflows"
                                    , { }
                                    , function(content) {
                                        // console.log(" -- setting workflows");
                                        // log("Got workflow: " + app_JSONStringify(content));
                                        var workflows = content.workflows;
                                        var keys = Object.keys(workflows);
                                        for (var i = 0; i < keys.length; i++) {
                                            var k = keys[i];
                                            // console.log(" -- new wf: " + k);
                                            app_workflow_set(k, workflows[k]);
                                            // sys_workflows[k] = workflows[k];
                                        }
                                        if (callback != undefined) {
                                            callback();
                                        }
                                    }
                                   );
    }
    sys_loginhooks.app_spacemr_inventario_doInitialize = initfunction;
    initfunction(callback);
    //-
    //- custom headers
    //-
    {
        s = "longstring_spacemr_inventario_links"
        mapper = {};
        mapper.hidden = false;
        mapper.name   = s;
        mapper.draw   = function(value, tr, mapIdName, row) {
            if (value == null) {
                value = "";
                //} else {
                //    if (value.length > 254) {
                //        value = value.substring(0,251)+"...";
                //    }
            }
            value = value.replace(/"spacemr_space_id":"(\d+)"/
                                  , '<a href="#?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id=$1" onclick="app_spacemr_icr_space_onclick($1)">"spacemr_space_id":"$1"</a>'
                                 );
            value = value.replace(/"spacemr_space_id":(\d+)/
                                  , '<a href="#?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id=$1" onclick="app_spacemr_icr_space_onclick($1)">"spacemr_space_id":$1</a>'
                                 );
            value = value.replace(/"spacemr_people_id":"(\d+)"/
                                  , '<a href="#?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id=$1" onclick="app_spacemr_icr_people_onclick($1)">"spacemr_people_id":"$1"</a>'
                                 );
            value = value.replace(/"spacemr_people_id":(\d+)/
                                  , '<a href="#?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id=$1" onclick="app_spacemr_icr_people_onclick($1)">"spacemr_people_id":$1</a>'
                                 );
            return("" + value);
        };
        app_appListTable_mappers[s] = mapper;
    }
}

// function app_spacemr_inventario_custom_replace_spacelinks_onclick(id) {
function app_spacemr_icr_space_onclick(id) {
    l="?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id="+id+"";
    app_initialization_setHashAndGoToPage(l);
}
// function app_spacemr_inventario_custom_replace_spacelinks_onclick(id) {
function app_spacemr_icr_people_onclick(id) {
    l="?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id="+id+"";
    app_initialization_setHashAndGoToPage(l);
}

//-
function app_spacemr_inventario_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var spacemr_inventario_super_id = getLocationParameterByName('spacemr_inventario_super_id');
    // if (spacemr_inventario_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("spacemr_inventario_super/spacemr_inventario_super_get"
    //                                 , { spacemr_inventario_super_id: spacemr_inventario_super_id}
    //                                 , function(content) {
    //                                     var spacemr_inventario_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_inventario_super_tabs(spacemr_inventario_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="spacemr_inventario_super_id" id="id_spacemr_inventario_super_id" type="hidden" />')
    //                 .val(spacemr_inventario_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    //- space
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),"");
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", gRb("db.spacemr_space_people.spacemr_space_id"),"");
    app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                          , "id_spacemr_space_code"
                                                          , "id_spacemr_space_id"
                                                         );
    //- people
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_username", "spacemr_people_username", gRb("db.spacemr_space_people.spacemr_people_username"),"");
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_first_name", "spacemr_people_first_name", gRb("db.spacemr_space_people.spacemr_people_first_name"),"");
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_last_name", "spacemr_people_last_name", gRb("db.spacemr_space_people.spacemr_people_last_name"),"");
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_people_id", "spacemr_people_id", gRb("db.spacemr_space_people.spacemr_people_id"),"");
    app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                           , "id_spacemr_people_username"
                                                           , "id_spacemr_people_first_name"
                                                           , "id_spacemr_people_last_name"
                                                           , "id_spacemr_people_id"
                                                         );
    app_ui_standard_appendFieldText(grid, "id_stato", "stato", gRb("db.spacemr_inventario.stato"),"");
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_validato", "fg_validato", gRb("db.spacemr_inventario.fg_validato"),false);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_adesivo", "fg_adesivo", gRb("db.spacemr_inventario.fg_adesivo"),false);
    app_ui_standard_appendFieldText(grid, "id_codice_inventario_unimore", "codice_inventario_unimore", gRb("db.spacemr_inventario.codice_inventario_unimore"),"");
    app_ui_standard_appendFieldText(grid, "id_inventario_numero", "inventario_numero", gRb("db.spacemr_inventario.inventario_numero"),"");
    app_ui_standard_appendFieldText(grid, "id_inventario_numero_sub", "inventario_numero_sub", gRb("db.spacemr_inventario.inventario_numero_sub"),"");
    app_ui_standard_appendFieldText(grid, "id_inventario_etichetta", "inventario_etichetta", gRb("db.spacemr_inventario.inventario_etichetta"),"");
    app_ui_standard_appendFieldText(grid, "id_tipo_carico_scarico", "tipo_carico_scarico", gRb("db.spacemr_inventario.tipo_carico_scarico"),"");
    app_ui_standard_appendFieldDate(grid, "id_carico_data", "carico_data", gRb("db.spacemr_inventario.carico_data"),"");
    app_ui_standard_appendFieldText(grid, "id_attivita_tipo", "attivita_tipo", gRb("db.spacemr_inventario.attivita_tipo"),"");
    app_ui_standard_appendFieldText(grid, "id_descrizione", "descrizione", gRb("db.spacemr_inventario.descrizione"),"");
    app_ui_standard_appendFieldText(grid, "id_categoria_inventario", "categoria_inventario", gRb("db.spacemr_inventario.categoria_inventario"),"");
    app_ui_standard_appendFieldDecimal(grid, "id_valore", "valore", gRb("db.spacemr_inventario.valore"),"");
    app_ui_standard_appendFieldText(grid, "id_fornitore", "fornitore", gRb("db.spacemr_inventario.fornitore"),"");
    app_ui_standard_appendFieldText(grid, "id_scarico_numero_buono", "scarico_numero_buono", gRb("db.spacemr_inventario.scarico_numero_buono"),"");
    app_ui_standard_appendFieldDate(grid, "id_scarico_data", "scarico_data", gRb("db.spacemr_inventario.scarico_data"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_old_values", "old_values", gRb("db.spacemr_inventario.old_values"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_old_values_changes", "old_values_changes", gRb("db.spacemr_inventario.old_values_changes"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_inventario.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_inventario_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_inventario_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_inventario.insert.title"));
    // }
}
//-
function app_spacemr_inventario_form_insert_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_spacemr_space_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.spacemr_space_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_people_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.spacemr_people_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_dateFormatUi_validation.test($('#id_carico_data').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.carico_data") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_decimal_validation.test($('#id_valore').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.valore") + " - " + gRb("db.sys.invalidValue") )));
    $('#id_scarico_data').val() == ""
        || sys_dateFormatUi_validation.test($('#id_scarico_data').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.scarico_data") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_inventario_form_insert_sendData() {
    var data =  { 
         spacemr_space_id:   $('#id_spacemr_space_id').val()
         , spacemr_people_id:   $('#id_spacemr_people_id').val()
         , stato:   $('#id_stato').val()
         , fg_validato:   $('#id_fg_validato').prop("checked")
         , fg_adesivo:   $('#id_fg_adesivo').prop("checked")
         , codice_inventario_unimore:   $('#id_codice_inventario_unimore').val()
         , inventario_numero:   $('#id_inventario_numero').val()
         , inventario_numero_sub:   $('#id_inventario_numero_sub').val()
         , inventario_etichetta:   $('#id_inventario_etichetta').val()
         , tipo_carico_scarico:   $('#id_tipo_carico_scarico').val()
         , carico_data:   appConvertStringToTimestamp($('#id_carico_data').val())
         , attivita_tipo:   $('#id_attivita_tipo').val()
         , descrizione:   $('#id_descrizione').val()
         , categoria_inventario:   $('#id_categoria_inventario').val()
         , valore:   appConvertStringToDecimal($('#id_valore').val())
         , fornitore:   $('#id_fornitore').val()
         , scarico_numero_buono:   $('#id_scarico_numero_buono').val()
         , scarico_data:   appConvertStringToTimestamp($('#id_scarico_data').val())
         , old_values:   $('#id_old_values').val()
         , old_values_changes:   $('#id_old_values_changes').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_inventario/spacemr_inventario_insert"
                                , data
                                , function(content) {
                                    var spacemr_inventario_id = content.spacemr_inventario_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_inventario__app_spacemr_inventario_form_update"
                                            + "&spacemr_inventario_id="+spacemr_inventario_id);
                                });
    //-
}
//-

function app_spacemr_inventario_list_my() {
    location.href = "#?page=app_spacemr_inventario__app_spacemr_inventario_list&qparams={%22where%22:{%22spacemr_people_username%22:%22"+sys_session.userData.user_name+"%22}}"
}

function app_spacemr_inventario_list() {
    // console.log(" -- in app_spacemr_inventario_list");
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_inventario.list.title"));
    var wf = app_workflow_get("spacemr_inventario_workflow");
    if (wf == undefined) {
        app_spacemr_inventario_doInitialize(app_spacemr_inventario_list);
        return;
    }
    //-
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    if ( spacemr_space_id == "") spacemr_space_id = undefined;
    var spacemr_people_id = getLocationParameterByName('spacemr_people_id');
    if ( spacemr_people_id == "") spacemr_people_id = undefined;
    var spacemr_people_username = getLocationParameterByName('spacemr_people_username');
    if ( spacemr_people_username == "") spacemr_people_username = undefined;
    //-
    var page    = $("<div>");
    if ( spacemr_space_id != undefined) {
        page.append(app_spacemr_space_tabs_get(spacemr_space_id));
    }
    if ( spacemr_people_id != undefined) {
        page.append(app_spacemr_people_tabs_get(spacemr_people_id));
    }
    var tableid = "table_spacemr_inventario_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_spacemr_inventario__app_spacemr_inventario_form_update"
                                 + "&spacemr_inventario_id="+row[mapIdName["spacemr_inventario_id"]]
                                )
            .attr('title',gRb("db.spacemr_inventario.update.title"))
            .append(app_getIcon("edit", 15)
                    .css("color", wf.statusIndex[row[mapIdName["stato_hidden"]]].color)
                   )
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   .append(
                       app_userHasPermission("db_spacemr_people_read") ? (
                           app_ui_clickableLink("?page=app_spacemr_inventario__app_spacemr_inventario_list"
                                                + "&spacemr_people_id="+row[mapIdName["spacemr_people_id"]]
                                               )
                               .attr('title',gRb("db.spacemr_people..single"))
                               .append(app_getIcon("user", 15))
                       ) : ""
                   ) 
                   .append(
                       app_userHasPermission("db_spacemr_space_read") ? (
                           app_ui_clickableLink("?page=app_spacemr_inventario__app_spacemr_inventario_list"
                                                + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                               )
                               .attr('title',gRb("db.spacemr_space..single"))
                               .append(app_getIcon("home", 15))
                       ) : ""
                   ) 
                   .append(
                       app_userHasPermission("db_spacemr_space_read") ? (
                           app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                + "&spacemr_space_child_id="+row[mapIdName["spacemr_space_id"]]
                                                + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_in_map_id_default"]]
                                                + "&app_spacemr_space_people_view_mode=inventarios"
                                               )
                               .attr('title',gRb("db.spacemr_space_map.maps.people"))
                               .append(
                                   app_getIcon("map", 15)
                               )
                       ) : ""
                   )
                  );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "spacemr_inventario/spacemr_inventario_list"
                                , buttonsCell: buttonsCell
                                , content: {
                                    qparams: { } 
                                }
                                , enable_csv_output: true
                               };
    var tabsDiv = $("<div>");
    page.append(tabsDiv);
    //-
    let buttons_div = $("<div>");
    page.append(buttons_div);
    //-
    tableMattoni_options.content.qparams.where = {};
    app_where_set_workflow_defaults(tableMattoni_options.content.qparams, "stato", wf);
    {
        //-
        //- navigation and filter hooks
        //-
        var qvalue = getLocationParameterByName("qparams");
        if (qvalue != "") {
            tableMattoni_options.content.qparams = JSON.parse(qvalue);
        }
    }
    // -- if this is the "list" of detail of the "super" master, add the super in the where clause
    // var spacemr_inventario_super_id = getLocationParameterByName('spacemr_inventario_super_id');
    // if (spacemr_inventario_super_id != "") {
    //     app_doRequestMappingRequest("spacemr_inventario_super/spacemr_inventario_super_get"
    //                                 , { spacemr_inventario_super_id: spacemr_inventario_super_id}
    //                                 , function(content) {
    //                                     var spacemr_inventario_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_inventario_super_tabs(spacemr_inventario_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.spacemr_inventario_super_id = spacemr_inventario_super_id;
    // };
    if ( spacemr_space_id != undefined) {
        tableMattoni_options.content.qparams.where.spacemr_space_id = spacemr_space_id;
    }
    if ( spacemr_people_id != undefined) {
        tableMattoni_options.content.qparams.where.spacemr_people_id = spacemr_people_id;
    }
    if ( spacemr_people_username != undefined) {
        tableMattoni_options.content.qparams.where.spacemr_people_username = spacemr_people_username;
    }
    //-
    //- the div containing the table
    var divTable=$("<div>");
    appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
    page.append(divTable);
    //-
    {
        //-
        //- area containing insert button and "show/hide"  filters
        //-
        var form = $('<form name="fm">');
        grid = app_ui_standard_getGrid(form);
        var fieldcontain = $('<div>').hide();
        //-
        form.submit(function(){ return(false);} );
        form.keypress(function (event) {
            if (event.keyCode == 10 || event.keyCode == 13) {
                divTable.tableMattoni().render();
                fieldcontain.hide("slow");
            }
        });
        //-
        //-
        //-
        var buttonline = app_ui_standard_appendRow(grid);
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.refresh"))
                .click(function(event) {
                    // console.log(app_JSONStringify(divTable.tableMattoni().options));
                    divTable.tableMattoni().render();
                    fieldcontain.hide("slow");
                }
                      )
        );
        if (app_userHasPermission("db_spacemr_inventario_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_inventario.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_spacemr_inventario__app_spacemr_inventario_form_insert"
                                                              // + "&spacemr_inventario_super_id=" + spacemr_inventario_super_id
                                                             )
                    }
                          )
            );
        }
        //-
        //- where clause
        //-
        fieldcontain.append($("<h3>").text(gRb("db.sys.ricercaPerParametri")));
        //-
        //-        
        //-
        app_where_append_workflow(fieldcontain, "db.spacemr_inventario.stato", "stato", divTable, wf);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.spacemr_space_code"), "spacemr_space_code", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.spacemr_people_username"), "spacemr_people_username", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.spacemr_people_first_name"), "spacemr_people_first_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.spacemr_people_last_name"), "spacemr_people_last_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.spacemr_people_role"), "spacemr_people_role", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.spacemr_inventario.fg_validato"), "fg_validato", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.spacemr_inventario.fg_adesivo"), "fg_adesivo", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.codice_inventario_unimore"), "codice_inventario_unimore", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.inventario_etichetta"), "inventario_etichetta", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.inventario_numero"), "inventario_numero", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.inventario_numero_sub"), "inventario_numero_sub", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.tipo_carico_scarico"), "tipo_carico_scarico", divTable);
        app_where_append_date(fieldcontain, gRb("db.spacemr_inventario.carico_data"), "carico_data", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.attivita_tipo"), "attivita_tipo", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.descrizione"), "descrizione", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.categoria_inventario"), "categoria_inventario", divTable);
        app_where_append_decimal(fieldcontain, gRb("db.spacemr_inventario.valore"), "valore", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.fornitore"), "fornitore", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_inventario.scarico_numero_buono"), "scarico_numero_buono", divTable);
        app_where_append_date(fieldcontain, gRb("db.spacemr_inventario.scarico_data"), "scarico_data", divTable);
        //-
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.filtri"))
                .click(function(event) {
                        if (fieldcontain.is(":visible")) {
                            fieldcontain.hide("slow");
                        } else {
                            fieldcontain.show("slow");
                        }
                }
                      )
        );
        {
            //-
            //- navigation and filter hooks
            //-
            divTable.tableMattoni().render_addHook(function(){
                var qp=app_JSONStringify(divTable.tableMattoni().qparams(),0).replace(/%/g,"%25");
                var url = ""
                    + "?page=app_spacemr_inventario__app_spacemr_inventario_list"
                    + "&qparams="+ qp;
                ;
                app_initialization_setHash(url);
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
        //-
        grid.append(fieldcontain);
        buttons_div.append(form);
    }
    //-
    appSetPage(page, gRb("db.spacemr_inventario.list.title"));
    //-
}
//-
function app_spacemr_inventario_tabs(spacemr_inventario) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_spacemr_inventario_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_inventario__app_spacemr_inventario_form_update&spacemr_inventario_id="+spacemr_inventario.spacemr_inventario_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_inventario..single") + ": " + spacemr_inventario.inventario_etichetta)
        );
    }
    if (app_userHasPermission("db_spacemr_people_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id="+spacemr_inventario.spacemr_people_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_people..single") + ": " + spacemr_inventario.spacemr_people_username)
        );
    }
    if (app_userHasPermission("db_spacemr_space_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id="+spacemr_inventario.spacemr_space_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space..single") + ": " + spacemr_inventario.spacemr_space_code)
        );
    }
    return(rv);
}
//-
function app_spacemr_inventario_form_update() {
    var wf = app_workflow_get("spacemr_inventario_workflow");
    if (wf == undefined) {
        app_spacemr_inventario_doInitialize(app_spacemr_inventario_form_update);
        return;
    }
    var spacemr_inventario_id = getLocationParameterByName('spacemr_inventario_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_inventario.update.title"));
    //-
    app_doRequestMappingRequest("spacemr_inventario/spacemr_inventario_get"
                                , { spacemr_inventario_id: spacemr_inventario_id}
                                , function(content) {
                                    app_spacemr_inventario_form_update_data(content);
                                });
}

//-
var app_spacemr_inventario_form_update_old_data = undefined;
//-
function app_spacemr_inventario_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_spacemr_space_tabs_get(obj.spacemr_space_id));
    page.append(app_spacemr_inventario_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    var wf = app_workflow_get("spacemr_inventario_workflow");
    //-
    var updateFunction = function(event) {
                    var onSuccess = function() {
                        if (logTable != undefined ) {
                            setTimeout(function() {
                                logTable.tableMattoni().render();
                            }, 1000);
                        }
                    }
                    app_spacemr_inventario_form_update_doUpdate(onSuccess);
                };
    //-
    var topbuttonline = app_ui_standard_appendRow(grid);
    //-
    var logs = $("<div>"); grid.append(logs);
    //-
    if (app_userHasPermission("db_spacemr_inventario_admin")
        || (!obj.fg_validato && app_userHasPermission("db_spacemr_inventario_update"))) {
        topbuttonline.append(" ").append(app_ui_standard_button()
                                         .text(gRb("db.sys.update"))
                                         .click(updateFunction));
    }
    //-
    // app_ui_standard_appendFieldHidden(form, "id_spacemr_inventario_id", "spacemr_inventario_id", "label", obj.spacemr_inventario_id);
    app_ui_standard_appendFieldHidden(form, "id_spacemr_inventario_id", "spacemr_inventario_id", "label", obj.spacemr_inventario_id);
    app_ui_standard_appendFieldWorkflow(grid, "id_stato", "stato", "db.spacemr_inventario.stato",obj.stato,wf);
    //-
    //- space
    // app_ui_standard_appendFieldInteger(grid, "id_spacemr_space_id", "spacemr_space_id", gRb("db.spacemr_inventario.spacemr_space_id"),obj.spacemr_space_id);
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),obj.spacemr_space_code);
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", gRb("db.spacemr_space_people.spacemr_space_id"),obj.spacemr_space_id);
    app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                          , "id_spacemr_space_code"
                                                          , "id_spacemr_space_id"
                                                         );

    
    //-
    //- people
    // app_ui_standard_appendFieldInteger(grid, "id_spacemr_people_id", "spacemr_people_id", gRb("db.spacemr_inventario.spacemr_people_id"),obj.spacemr_people_id);
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_username", "spacemr_people_username", gRb("db.spacemr_space_people.spacemr_people_username"),obj.spacemr_people_username);
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_first_name", "spacemr_people_first_name", gRb("db.spacemr_space_people.spacemr_people_first_name"),obj.spacemr_people_first_name);
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_last_name", "spacemr_people_last_name", gRb("db.spacemr_space_people.spacemr_people_last_name"),obj.spacemr_people_last_name);
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_people_id", "spacemr_people_id", gRb("db.spacemr_space_people.spacemr_people_id"),obj.spacemr_people_id);
    app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                           , "id_spacemr_people_username"
                                                           , "id_spacemr_people_first_name"
                                                           , "id_spacemr_people_last_name"
                                                           , "id_spacemr_people_id"
                                                         );
    //-
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_validato", "fg_validato", gRb("db.spacemr_inventario.fg_validato"),obj.fg_validato);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_adesivo", "fg_adesivo", gRb("db.spacemr_inventario.fg_adesivo"),obj.fg_adesivo);
    app_ui_standard_appendFieldText(grid, "id_codice_inventario_unimore", "codice_inventario_unimore", gRb("db.spacemr_inventario.codice_inventario_unimore"),obj.codice_inventario_unimore);
    app_ui_standard_appendFieldText(grid, "id_inventario_numero", "inventario_numero", gRb("db.spacemr_inventario.inventario_numero"),obj.inventario_numero);
    app_ui_standard_appendFieldText(grid, "id_inventario_numero_sub", "inventario_numero_sub", gRb("db.spacemr_inventario.inventario_numero_sub"),obj.inventario_numero_sub);
    app_ui_standard_appendFieldText(grid, "id_inventario_etichetta", "inventario_etichetta", gRb("db.spacemr_inventario.inventario_etichetta"),obj.inventario_etichetta);
    app_ui_standard_appendFieldText(grid, "id_tipo_carico_scarico", "tipo_carico_scarico", gRb("db.spacemr_inventario.tipo_carico_scarico"),obj.tipo_carico_scarico);
    app_ui_standard_appendFieldDate(grid, "id_carico_data", "carico_data", gRb("db.spacemr_inventario.carico_data"),appConvertDateToString(obj.carico_data));
    app_ui_standard_appendFieldText(grid, "id_attivita_tipo", "attivita_tipo", gRb("db.spacemr_inventario.attivita_tipo"),obj.attivita_tipo);
    app_ui_standard_appendFieldText(grid, "id_descrizione", "descrizione", gRb("db.spacemr_inventario.descrizione"),obj.descrizione);
    app_ui_standard_appendFieldText(grid, "id_categoria_inventario", "categoria_inventario", gRb("db.spacemr_inventario.categoria_inventario"),obj.categoria_inventario);
    app_ui_standard_appendFieldDecimal(grid, "id_valore", "valore", gRb("db.spacemr_inventario.valore"),obj.valore);
    app_ui_standard_appendFieldText(grid, "id_fornitore", "fornitore", gRb("db.spacemr_inventario.fornitore"),obj.fornitore);
    app_ui_standard_appendFieldText(grid, "id_scarico_numero_buono", "scarico_numero_buono", gRb("db.spacemr_inventario.scarico_numero_buono"),obj.scarico_numero_buono);
    app_ui_standard_appendFieldDate(grid, "id_scarico_data", "scarico_data", gRb("db.spacemr_inventario.scarico_data"),appConvertDateToString(obj.scarico_data));
    app_ui_standard_appendFieldTextArea(grid, "id_old_values", "old_values", gRb("db.spacemr_inventario.old_values"),obj.old_values);
    app_ui_standard_appendFieldTextArea(grid, "id_old_values_changes", "old_values_changes", gRb("db.spacemr_inventario.old_values_changes"),obj.old_values_changes);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_inventario.nota"),obj.nota);
    //-
    app_workflow_apply_per_field_permisson(wf, obj.stato, grid);
    //-
    var buttonlinestatus = app_ui_standard_appendRow(grid);
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_inventario_admin")
        || (!obj.fg_validato && app_userHasPermission("db_spacemr_inventario_update"))) {
        {
            let stato = wf.statusIndex[obj.stato];
            for (let is = 0; is < stato.steps.length; is++) {
                let sname = stato.steps[is];
                let sstato = wf.statusIndex[sname];
                let ssi = stato.stepsIndex[sname];
                buttonlinestatus.append(" ").append(
                    app_ui_standard_button()
                        .append(gRb("db.sys.update") + " ")
                        .append(app_getIcon("arrow_right", 15))
                        .append(app_getIcon("circle", 15).css("color",sstato.color))
                        .append(sstato.name)
                        .attr('title',gRb("db.sys.workflow.change.status.and.update"))
                        .click(function(){
                            $('#stato__' + sstato.name).prop('checked',true);
                            updateFunction();
                        })
                );
            }
        }
        buttonline.append(" ").append(app_ui_standard_button()
                                         .text(gRb("db.sys.update"))
                                         .click(updateFunction));
    }
    if (app_userHasPermission("db_spacemr_inventario_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // console.log("ok do delete");
                        var data =  { 
                            spacemr_inventario_id:   $('#id_spacemr_inventario_id').val()
                        };
                        // console.log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_inventario/spacemr_inventario_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_inventario__app_spacemr_inventario_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_inventario_logs")) {
        topbuttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_inventario_logs"
                                        , controller: "spacemr_inventario/spacemr_inventario_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_inventario_id: obj.spacemr_inventario_id
                                                , order:[{ column:"date", desc: true}]
                                            }
                                        }
                                       });
                        logTable.hide();
                        logs.html(logTable);
                        setTimeout(function() {
                            logTable.show("fast");
                        }, 500);
                    } else {
                        logTable.hide("fast");
                        setTimeout(function() {
                            logTable.html("");
                            logTable = undefined;
                        }, 500);
                    }
                })
        );
    }
    //-
    //-
    //- logs
    //-
    appSetPage(page, gRb("db.spacemr_inventario.update.title") + " " + obj.spacemr_inventario_id);
    //-
    // app_test_showMobilePropertiesInfo();
    app_spacemr_inventario_form_update_old_data = app_spacemr_inventario_form_update_getData();
}

//-
function app_spacemr_inventario_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_inventario_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_inventario_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_inventario_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_inventario_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("spacemr_inventario/spacemr_inventario_update"
                                        , data
                                        , function(content) {
                                            app_spacemr_inventario_form_update_old_data = app_spacemr_inventario_form_update_getData();
                                            app_setMessage(gRb("db.spacemr_inventario.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_inventario_form_update_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_spacemr_space_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.spacemr_space_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_people_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.spacemr_people_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_dateFormatUi_validation.test($('#id_carico_data').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.carico_data") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_decimal_validation.test($('#id_valore').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.valore") + " - " + gRb("db.sys.invalidValue") )));
    $('#id_scarico_data').val() == ""
        || sys_dateFormatUi_validation.test($('#id_scarico_data').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_inventario.scarico_data") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_inventario_form_update_getData() {
    var data =  { 
         spacemr_inventario_id:   $('#id_spacemr_inventario_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , spacemr_people_id:   $('#id_spacemr_people_id').val()
         , stato:               $("input[name='stato']:checked").val()
         , fg_validato:   $('#id_fg_validato').prop("checked")
         , fg_adesivo:   $('#id_fg_adesivo').prop("checked")
         , codice_inventario_unimore:   $('#id_codice_inventario_unimore').val()
         , inventario_numero:   $('#id_inventario_numero').val()
         , inventario_numero_sub:   $('#id_inventario_numero_sub').val()
         , inventario_etichetta:   $('#id_inventario_etichetta').val()
         , tipo_carico_scarico:   $('#id_tipo_carico_scarico').val()
         , carico_data:   appConvertStringToTimestamp($('#id_carico_data').val())
         , attivita_tipo:   $('#id_attivita_tipo').val()
         , descrizione:   $('#id_descrizione').val()
         , categoria_inventario:   $('#id_categoria_inventario').val()
         , valore:   appConvertStringToDecimal($('#id_valore').val())
         , fornitore:   $('#id_fornitore').val()
         , scarico_numero_buono:   $('#id_scarico_numero_buono').val()
         , scarico_data:   appConvertStringToTimestamp($('#id_scarico_data').val())
         , old_values:   $('#id_old_values').val()
         , old_values_changes:   $('#id_old_values_changes').val()
         , nota:   $('#id_nota').val()
    };
    // console.log(app_JSONStringify(data));
    return(data);
}




function app_spacemr_inventario_getVerifier() {
    //- no verification requirde in inventario
    //- used for the tooltip
    let rv = {
        init: function(wf) {
            //-
            let wid = this;
            //-
            wid.wf = wf;
            //-
        }
        , tooltip_inventarios(element, spacemr_space_id, stato, refresh_callback, current_time, menu_function) {
            let wid = this;
            let wf = wid.wf;
            app_tooltip_set_click(element, function(evt, tooltip_div){
                //-
                //- this a popup is like
                //-   app_spacemr_inventario_list
                //-
                //- start tooltip_generation
                //-
                let page = tooltip_div;
                var tableid = "table_spacemr_inventario_list_tooltip";
                //-
                var buttonsCell = function(tr, mapIdName, row) {
                    //-
                    // console.log(stato);
                    // console.log(wf.statusIndex);
                    tr.prepend($('<td>')
                               .append(
                                   app_ui_clickableLink("?page=app_spacemr_inventario__app_spacemr_inventario_form_update"
                                                        + "&spacemr_inventario_id="+row[mapIdName["spacemr_inventario_id"]]
                                                       )
                                       .attr('title',wf.statusIndex[stato].description)
                                       .append(
                                           app_getIcon("edit", 15).css("color", wf.statusIndex[stato].color)
                                       )
                               )
                              )
                }
                
                var tableMattoni_options = {tableid: tableid
                                            , controller: "spacemr_inventario/spacemr_inventario_list"
                                            , buttonsCell: buttonsCell
                                            , content: {
                                                qparams: {
                                                    where: {
                                                        stato_equal: stato
                                                        , spacemr_space_id: ""+spacemr_space_id
                                                        , recursive_space: true
                                                    }
                                                }
                                            }
                                            , enable_csv_output: true
                                            , render_post_hooks: [
                                                function(){
                                                    app_tooltip_center();
                                                }
                                            ]
                                           };
                //-
                //- the div containing the table
                var divTable=$("<div>");
                appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
                //-
		if (menu_function != null) {
                    var form = $('<form name="fm">');
                    let grid = app_ui_standard_getGrid(form);
                    var buttonline = app_ui_standard_appendRow(grid);
		    menu_function(buttonline);
                    page.append(form);
		}
                page.append(divTable);
                //-
                //- end tooltip_generation
                //-
            });
        }
    }
    return(rv);
}
