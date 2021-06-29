function app_spacemr_people_doInitialize(callback) {
    log("spacemr_people page initialization...");
    callback();
}
//-

function app_spacemr_people_form_insertLdapButton(grid) {
    if(app_userHasPermission("spacemr_people_ldap_search")){
        var search_output = $("<div>");
        var search_function_server = function(content) {
            // console.log(app_JSONStringify(content));
            var parser = new Function("return " + content.parser)();
            //-
            //- debug:
            //-
            // var parser = 
            //     function(ldapRow) {
            //         ...
            //     };
            // console.log(parser.toString());
            // var jtmp = {};
            // jtmp.parser = parser.toString();
            // console.log(app_JSONStringify(jtmp));
            //
            var table =
                $('<table data-role="table" class="w3-striped w3-hoverable w3-responsive"/>')
                .append($("<tr>")
                        .append($("<th>").text(""))
                        .append($("<th>").text(gRb("db.spacemr_people.username")))
                        .append($("<th>").text(gRb("db.spacemr_people.first_name")))
                        .append($("<th>").text(gRb("db.spacemr_people.last_name")))
                        .append($("<th>").text(gRb("db.spacemr_people.role")))
                        .append($("<th>").text(gRb("db.spacemr_people.department")))
                        .append($("<th>").text(gRb("db.spacemr_people.email")))
                       )
            ;
            var rows = content.rows;
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                var tr = $("<tr/>");
                var parsed = row;
                var f = function(parsed) {
                    tr.append($('<abbr>')
                              .attr('title',gRb("db.sys.doSelect"))
                              .append(app_getIcon("check", 15)
                                      .click(function(){
                                          search_output.html("");
                                          $('#id_username').val(parsed.username);
                                          $('#id_first_name').val(parsed.first_name);
                                          $('#id_last_name').val(parsed.last_name);
                                          $('#id_email').val(parsed.email);
                                          $('#id_role').val(parsed.role);
                                          $('#id_department').val(parsed.department);
                                      })
                                     ))
                    ;
                    tr.append($("<td>").append(parsed.username));
                    tr.append($("<td>").append(parsed.first_name));
                    tr.append($("<td>").append(parsed.last_name));
                    tr.append($("<td>").append(parsed.role));
                    tr.append($("<td>").append(parsed.department));
                    tr.append($("<td>").append(parsed.email));
                    // tr.append($("<td>").append($("<pre>").text(app_JSONStringify(row))));
                    table.append(tr);
                }
                f(parsed);
            }
            search_output.html("");
            search_output.append(table);
            // search_output.append($("<pre>").text(app_JSONStringify(content)));
        };
        var search_button = app_ui_standard_button()
            .append(app_getIcon("lens_search", 15))
            .append(gRb("db.spacemr_people.searchldap") )
            .click(function(){
                search_output.text(gRb("db.sys.searching"));
                var search_parameters = {
                    username:   $('#id_username').val()
                    , first_name:   $('#id_first_name').val()
                    , last_name:   $('#id_last_name').val()
                };
                // setTimeout(function() {
                //     search_function_server(app_JSONStringify(search_parameters));
                // }, 200);
                app_doRequestMappingRequest("spacemr_people/spacemr_people_ldap_search"
                                            , search_parameters
                                            , search_function_server
                                           );
                // see https://web.ing.unimo.it/mmd/mmd_ldap/mmd_ldap_search
                // in
                //  /dati/bin/workspace/mmd/src/main/java/it/unimo/app/controller/Mmd_ldap.java
                //
            })
        ;
        grid.append(search_button)
            .append(search_output);
    }
}


function app_spacemr_people_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var spacemr_people_super_id = getLocationParameterByName('spacemr_people_super_id');
    // if (spacemr_people_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("spacemr_people_super/spacemr_people_super_get"
    //                                 , { spacemr_people_super_id: spacemr_people_super_id}
    //                                 , function(content) {
    //                                     var spacemr_people_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_people_super_tabs(spacemr_people_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="spacemr_people_super_id" id="id_spacemr_people_super_id" type="hidden" />')
    //                 .val(spacemr_people_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_username", "username", gRb("db.spacemr_people.username"),"");
    
    app_ui_standard_appendFieldText(grid, "id_first_name", "first_name", gRb("db.spacemr_people.first_name"),"");
    app_ui_standard_appendFieldText(grid, "id_last_name", "last_name", gRb("db.spacemr_people.last_name"),"");
    //-
    app_spacemr_people_form_insertLdapButton(grid);
    //-
    
    app_ui_standard_appendFieldText(grid, "id_email", "email", gRb("db.spacemr_people.email"),"");
    app_ui_standard_appendFieldText(grid, "id_role", "role", gRb("db.spacemr_people.role"),"");
    app_ui_standard_appendFieldText(grid, "id_department", "department", gRb("db.spacemr_people.department"),"");
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_in_ldap", "fg_in_ldap", gRb("db.spacemr_people.fg_in_ldap"),false);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_has_a_seat", "fg_has_a_seat", gRb("db.spacemr_people.fg_has_a_seat"),false);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_people.nota"),"");
    // app_ui_standard_appendFieldTextArea(grid, "id_configuration", "configuration", gRb("db.spacemr_people.configuration"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_people_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_people_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_people.insert.title"));
    // }
}
//-
function app_spacemr_people_form_insert_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_spacemr_people_form_insert_sendData() {
    var data =  { 
         username:   $('#id_username').val()
         , first_name:   $('#id_first_name').val()
         , last_name:   $('#id_last_name').val()
         , email:   $('#id_email').val()
         , role:   $('#id_role').val()
         , department:   $('#id_department').val()
         , nota:   $('#id_nota').val()
         , configuration:   ""
         , fg_in_ldap:   $('#id_fg_in_ldap').prop("checked")
         , fg_has_a_seat:   $('#id_fg_has_a_seat').prop("checked")
         // , configuration:   $('#id_configuration').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_people/spacemr_people_insert"
                                , data
                                , function(content) {
                                    var spacemr_people_id = content.spacemr_people_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_people__app_spacemr_people_form_update"
                                            + "&spacemr_people_id="+spacemr_people_id);
                                });
    //-
}
//-
function app_spacemr_people_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_people.list.title"));
    //-
    //-
    var page    = $("<div>");
    var tableid = "table_spacemr_people_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(
                       $('<abbr>').attr('title',gRb("db.spacemr_people.update.title")).append(
                           app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update"
                                                + "&spacemr_people_id="+row[mapIdName["spacemr_people_id"]]
                                               )
                               .append(app_getIcon("user", 15))
                       ))
                   .append(
                       $('<abbr>').attr('title',gRb("db.spacemr_space_people.list.title")).append(
                           app_ui_clickableLink("?page=app_spacemr_space_people__app_spacemr_space_people_list"
                                                + "&spacemr_people_id="+row[mapIdName["spacemr_people_id"]]
                                               )
                               .append(app_getIcon("home", 15))
                       ))
                   .append(
                       app_userHasPermission("db_spacemr_space_map_read") && (row[mapIdName["spacemr_main_space_in_map_id_default"]] !== null ) ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space_map.show.on.map")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                    + "&spacemr_space_map_id="+row[mapIdName["spacemr_main_space_in_map_id_default"]]
                                                    + "&spacemr_space_child_id="+row[mapIdName["spacemr_main_space_id"]]
                                                   )
                                   .append(app_getIcon("map_black", 15))
                           )
                           
                       ) : ""
                   )
                   // .append(spacemr_people_super)
                   // .append(spacemr_people_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "spacemr_people/spacemr_people_list"
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
    // var spacemr_people_super_id = getLocationParameterByName('spacemr_people_super_id');
    // if (spacemr_people_super_id != "") {
    //     app_doRequestMappingRequest("spacemr_people_super/spacemr_people_super_get"
    //                                 , { spacemr_people_super_id: spacemr_people_super_id}
    //                                 , function(content) {
    //                                     var spacemr_people_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_people_super_tabs(spacemr_people_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.spacemr_people_super_id = spacemr_people_super_id;
    // };
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
        if (app_userHasPermission("db_spacemr_people_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_people.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_spacemr_people__app_spacemr_people_form_insert"
                                                              // + "&spacemr_people_super_id=" + spacemr_people_super_id
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
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.username"), "username", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.first_name"), "first_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.last_name"), "last_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.email"), "email", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.role"), "role", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.department"), "department", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.spacemr_people.fg_in_ldap"), "fg_in_ldap", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.spacemr_people.fg_has_a_seat"), "fg_has_a_seat", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.nota"), "nota", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_people.configuration"), "configuration", divTable);
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
                    + "?page=app_spacemr_people__app_spacemr_people_list"
                    + "&qparams="+qp
                ;
                app_initialization_setHash(url);
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
        grid.append(fieldcontain);
        buttons_div.append(form);
    }
    //-
    appSetPage(page, gRb("db.spacemr_people.list.title"));
    //-
}

function app_spacemr_people_tabs_get(spacemr_people_id) {
    var rv = "";
    if (app_userHasPermission("db_spacemr_people_read")) {
        rv = $("<div>");
        app_doRequestMappingRequest("spacemr_people/spacemr_people_get"
                                    , { spacemr_people_id: spacemr_people_id}
                                    , function(content) {
                                        rv.append(app_spacemr_people_tabs(content.obj));
                                    });
    }
    return(rv);
}
//-
function app_spacemr_people_tabs(spacemr_people) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_spacemr_people_read")) {
        var ls = "?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id="+spacemr_people.spacemr_people_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_people..single") + ": "+spacemr_people.first_name+" "+spacemr_people.last_name+" - " + spacemr_people.username)
        );
    }
    if (app_userHasPermission("db_spacemr_space_read")) {
        var ls = "?page=app_spacemr_space_people__app_spacemr_space_people_list&spacemr_people_id="+spacemr_people.spacemr_people_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space..plural"))
        );
    }
    if (app_userHasPermission("db_spacemr_inventario_read")) {
        var ls = "?page=app_spacemr_inventario__app_spacemr_inventario_list&spacemr_people_id="+spacemr_people.spacemr_people_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_inventario..plural"))
        );
    }
    return(rv);
}
//-
function app_spacemr_people_form_update() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_people.update.title"));
    //-
    var spacemr_people_id = getLocationParameterByName('spacemr_people_id');
    var spacemr_people_username = getLocationParameterByName('spacemr_people_username');
        app_doRequestMappingRequest("spacemr_people/spacemr_people_get"
                                    , { spacemr_people_id: spacemr_people_id
                                        , spacemr_people_username: spacemr_people_username
                                      }
                                    , function(content) {
                                        app_spacemr_people_form_update_data(content);
                                    });
}

//-
var app_spacemr_people_form_update_old_data = undefined;
//-
function app_spacemr_people_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_spacemr_people_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_spacemr_people_id", "spacemr_people_id", "label", obj.spacemr_people_id);
    app_ui_standard_appendFieldHidden(form, "id_spacemr_people_id", "spacemr_people_id", "label", obj.spacemr_people_id);
    app_ui_standard_appendFieldText(grid, "id_username", "username", gRb("db.spacemr_people.username"),obj.username);
    app_ui_standard_appendFieldText(grid, "id_first_name", "first_name", gRb("db.spacemr_people.first_name"),obj.first_name);
    app_ui_standard_appendFieldText(grid, "id_last_name", "last_name", gRb("db.spacemr_people.last_name"),obj.last_name);
    app_spacemr_people_form_insertLdapButton(grid);
    app_ui_standard_appendFieldText(grid, "id_email", "email", gRb("db.spacemr_people.email"),obj.email);
    app_ui_standard_appendFieldText(grid, "id_role", "role", gRb("db.spacemr_people.role"),obj.role);
    app_ui_standard_appendFieldText(grid, "id_department", "department", gRb("db.spacemr_people.department"),obj.department);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_in_ldap", "fg_in_ldap", gRb("db.spacemr_people.fg_in_ldap"),obj.fg_in_ldap);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_has_a_seat", "fg_has_a_seat", gRb("db.spacemr_people.fg_has_a_seat"),obj.fg_has_a_seat);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_people.nota"),obj.nota);
    app_ui_standard_appendFieldTextArea(grid, "id_configuration", "configuration", gRb("db.spacemr_people.configuration"),obj.configuration);
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_people_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(function(event) {
                    var onSuccess = function() {
                        if (logTable != undefined ) {
                            setTimeout(function() {
                                logTable.tableMattoni().render();
                            }, 1000);
                        }
                    }
                    app_spacemr_people_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_spacemr_people_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            spacemr_people_id:   $('#id_spacemr_people_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_people/spacemr_people_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_people__app_spacemr_people_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_people_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_people_logs"
                                        , controller: "spacemr_people/spacemr_people_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_people_id: obj.spacemr_people_id
                                                , order:[{ column:"date", desc: true}]
                                            }
                                        }
                                       });
                        logTable.hide();
                        logs.html(logTable);
                        setTimeout(function() {
                            logTable.show("fast", function(){
                                $('html, body').animate({
                                    scrollTop: logTable.offset().top 
                                }, 200);
                            });
                        }, 500);
                    } else {
                        logTable.hide("slow");
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
    var logs = $("<div>"); page.append(logs);
    //-
    appSetPage(page, gRb("db.spacemr_people.update.title") + " " + obj.username);
    //-
    // app_test_showMobilePropertiesInfo();
    app_spacemr_people_form_update_old_data = app_spacemr_people_form_update_getData();
}

//-
function app_spacemr_people_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_people_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_people_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_people_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_people_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("spacemr_people/spacemr_people_update"
                                        , data
                                        , function(content) {
                                            app_spacemr_people_form_update_old_data = app_spacemr_people_form_update_getData();
                                            app_setMessage(gRb("db.spacemr_people.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_people_form_update_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_spacemr_people_form_update_getData() {
    var data =  { 
         spacemr_people_id:   $('#id_spacemr_people_id').val()
         , username:   $('#id_username').val()
         , first_name:   $('#id_first_name').val()
         , last_name:   $('#id_last_name').val()
         , email:   $('#id_email').val()
         , role:   $('#id_role').val()
         , department:   $('#id_department').val()
         , nota:   $('#id_nota').val()
         , configuration:   $('#id_configuration').val()
         , fg_in_ldap:   $('#id_fg_in_ldap').prop("checked")
         , fg_has_a_seat:   $('#id_fg_has_a_seat').prop("checked")
    };
    //- log(app_JSONStringify(data));
    return(data);
}
