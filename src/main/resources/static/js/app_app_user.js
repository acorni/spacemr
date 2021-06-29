function app_app_user_doInitialize(callback) {
    // log("app_user page initialization...");
    callback();
}

function app_app_user_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_user_super_id = getLocationParameterByName('app_user_super_id');
    // if (app_user_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_user_super/app_user_super_get"
    //                                 , { app_user_super_id: app_user_super_id}
    //                                 , function(content) {
    //                                     var app_user_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_user_super_tabs(app_user_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_user_super_id" id="id_app_user_super_id" type="hidden" />')
    //                 .val(app_user_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_user_name", "user_name", gRb("db.app_user.user_name"),"");
    app_ui_standard_appendFieldText(grid, "id_first_name", "first_name", gRb("db.app_user.first_name"),"");
    app_ui_standard_appendFieldText(grid, "id_last_name", "last_name", gRb("db.app_user.last_name"),"");
    app_app_user_form_insertLdapButton(grid);
    //-
    app_ui_standard_appendFieldText(grid, "id_email", "email", gRb("db.app_user.email"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_user.nota"),"");
    app_ui_standard_appendFieldPassword(grid, "id_password", "password", gRb("db.app_user.password"),"");
    app_ui_standard_appendFieldPassword(grid, "id_password_confirm", "password_confirm", gRb("db.app_user.password_confirm"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_app_user_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_user_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_user.insert.title"));
    // }
}

function app_app_user_form_insertLdapButton(grid, options = {
    field_name_ids: {
        user_name: '#id_user_name'
        , first_name: '#id_first_name'
        , last_name: '#id_last_name'
        , email: '#id_email'
    }
    , on_select_callback: false
}) {
    var search_button = null;
    var field_name_ids = options.field_name_ids;
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
            //-
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
                                          $(field_name_ids.user_name).val(parsed.username);
                                          $(field_name_ids.first_name).val(parsed.first_name);
                                          $(field_name_ids.last_name).val(parsed.last_name);
                                          if (field_name_ids.email) {
                                              $(field_name_ids.email).val(parsed.email);
                                          }
                                          if (options.on_select_callback) {
                                              options.on_select_callback();
                                          }
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
        hide_button = app_ui_standard_button()
            .append(gRb("db.sys.hide") )
            .click(function(){
                hide_button.hide();
                search_output.html("");
            })
            .hide();
        search_button = app_ui_standard_button()
            .append(app_getIcon("lens_search", 15))
            .append(gRb("db.spacemr_people.searchldap") )
            .click(function(){
                if ($(field_name_ids.user_name).val() != ""
                    || $(field_name_ids.first_name).val() != ""
                    || $(field_name_ids.last_name).val() != ""
                   ) {
                    hide_button.show("fast");
                    search_output.text(gRb("db.sys.searching"));
                    var search_parameters = {
                        username:   $(field_name_ids.user_name).val()
                        , first_name:   $(field_name_ids.first_name).val()
                        , last_name:   $(field_name_ids.last_name).val()
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
                    //-
                }
            })
        ;
        grid.append(search_button)
            .append(" ")
            .append(hide_button)
            .append(search_output);
    }
    return(search_button);
}


//-
function app_app_user_form_insert_validate() {
    var rv = $('<ul/>');
    ((/^[a-zA-z0-9\-\.]+$/).test($('#id_user_name').val()) || (rv.append($("<li/>").text("Username non corretto"))));
    ((/^.+$/).test($('#id_first_name').val()) || (rv.append($("<li/>").text("Il nome non puo' essere vuoto"))));
    ((/^.+$/).test($('#id_last_name').val()) || (rv.append($("<li/>").text("Il cognome non puo' essere vuoto"))));
    (($('#id_password').val() == $('#id_password_confirm').val() ) || (rv.append($("<li/>").text("La password di conferma differisce dalla password"))));
    return rv;
}


function app_app_user_form_insert_sendData() {
    var data =  { 
         user_name:   $('#id_user_name').val()
         , first_name:   $('#id_first_name').val()
         , last_name:   $('#id_last_name').val()
         , email:   $('#id_email').val()
         , password:   $('#id_password').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("app_user/app_user_insert"
                                , data
                                , function(content) {
                                    var app_user_id = content.app_user_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_user__app_app_user_form_update"
                                            + "&app_user_id="+app_user_id);
                                });
    //-
}

function app_app_user_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_user.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_user_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            $('<abbr>').attr('title',gRb("db.app_user.update.title")).append(
                app_ui_clickableLink("?page=app_app_user__app_app_user_form_update"
                                     + "&app_user_id="+row[mapIdName["app_user_id"]]
                                    )
                    .append(app_getIcon("edit", 15))
            )
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(app_user_super)
                   // .append(app_user_sub)                   
                   .append((app_userHasPermission("db_app_user_update")) ?
                           (
                               $('<abbr>')
                                   .attr('title',gRb("db.app_user.update.changePassword"))
                                   .append(
                                       app_ui_clickableLink("?page=app_app_user__app_app_user_form_changePassword"
                                                            + "&app_user_id="+row[mapIdName["app_user_id"]]
                                                           )
                                           .append(app_getIcon("locker", 15))
                                   )
                           ) : ""
                          )
                   .append((app_userHasPermission("db_app_group_role_user_read")) ?
                           (
                               $('<abbr>')
                                   .attr('title',gRb("db.app_group_role_user..plural"))
                                   .append(
                                       app_ui_clickableLink("?page=app_app_user__app_app_group_role_user_list&app_user_id="+row[mapIdName["app_user_id"]]
                                                           )
                                           .append(app_getIcon("user_lock", 15))
                                   )
                           ) : ""
                          )
                  );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_user/app_user_list"
                                , buttonsCell: buttonsCell
                                , content: {
                                    qparams: { } 
                                }
                                , enable_csv_output: true
                               };
    var tabsDiv = $("<div>");
    page.append(tabsDiv);
    //-
    var form = $('<form name="fm">');
    page.append(form);
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
    // var app_user_super_id = getLocationParameterByName('app_user_super_id');
    // if (app_user_super_id != "") {
    //     app_doRequestMappingRequest("app_user_super/app_user_super_get"
    //                                 , { app_user_super_id: app_user_super_id}
    //                                 , function(content) {
    //                                     var app_user_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_user_super_tabs(app_user_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_user_super_id = app_user_super_id;
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
        var fieldcontain = $('<div>').hide();
        //-
        grid = app_ui_standard_getGrid(form);
        //-
        form.submit(function(){ return(false);} );
        form.keypress(function (event) {
            if (event.keyCode == 10 || event.keyCode == 13) {
                divTable.tableMattoni().render();
                fieldcontain.hide("fast");
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
                    fieldcontain.hide("fast");
                }
                      )
        );
        if (app_userHasPermission("db_app_user_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_user.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_user_form_insert"
                                                              // + "&app_user_super_id=" + app_user_super_id
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
        app_where_append_string(fieldcontain, gRb("db.app_user.user_name"), "user_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_user.first_name"), "first_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_user.last_name"), "last_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_user.email"), "email", divTable);
        app_where_append_timestamp(fieldcontain, gRb("db.app_user.created"), "created", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_user.nota"), "nota", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_user.password"), "password", divTable);
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
                    + "?page=app_app_user__app_app_user_list"
                    + "&qparams="+qp
                ;
                app_initialization_setHash(url);
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
        //-
        grid.append(fieldcontain);
    }
    //-
    appSetPage(page, gRb("db.app_user.list.title"));
    //-
}


function app_app_user_tabs(app_user) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_user_read")) {
        var ls = "?page=app_app_user__app_app_user_form_update&app_user_id="+app_user.app_user_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_user..single") + ": " + app_user.user_name)
        );
    }
    if (app_userHasPermission("db_app_user_update")) {
        var ls = "?page=app_app_user__app_app_user_form_changePassword&app_user_id="+app_user.app_user_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .append(app_getIcon("locker", 15))
                .append(gRb("db.app_user.update.changePassword"))
        );
    }
    if (app_userHasPermission("db_app_group_role_user_read")) {
        var ls = "?page=app_app_user__app_app_group_role_user_list&app_user_id="+app_user.app_user_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .append(app_getIcon("user_lock", 15))
                .append(gRb("db.app_group_role_user..plural"))
        );
    }
    if (app_userHasPermission("db_app_user_admin")) {
        rv.append(
            $('<a/>')
                .click(function(event){
                    event.stopPropagation();
                    // alert("Here - vorresti diventare "+app_user.user_name+"!!!");
                    app_doRequestMappingRequest("app_user/app_user_become_user"
                                                , { user_name: app_user.user_name}
                                                , function(content) {
                                                    window.location.href = "#";
                                                    window.location.reload();
                                                });
                }) 
                .attr("class", "w3-bar-item w3-button")
                .append(app_getIcon("check", 15))
                .append(gRb("db.app_user.update.become_this_user") + " ("+app_user.user_name+")")
        );
    }
    // if (app_userHasPermission("db_app_user_sub_read")) {
    //     var la = "?page=app_app_user_sub__app_app_user_sub_list&app_user_id="+app_user.app_user_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.app_user_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rv);
}


function app_app_user_form_update() {
    var app_user_id = getLocationParameterByName('app_user_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_user.update.title"));
    //-
    app_doRequestMappingRequest("app_user/app_user_get"
                                , { app_user_id: app_user_id}
                                , function(content) {
                                    app_app_user_form_update_data(content);
                                });
}


var app_app_user_form_update_old_data = undefined;
function app_app_user_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_user_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_app_user_id", "app_user_id", "label", obj.app_user_id);
    app_ui_standard_appendFieldHidden(form, "id_app_user_id", "app_user_id", "label", obj.app_user_id);
    app_ui_standard_appendFieldText(grid, "id_user_name", "user_name", gRb("db.app_user.user_name"),obj.user_name).prop('disabled', true);
    app_ui_standard_appendFieldText(grid, "id_first_name", "first_name", gRb("db.app_user.first_name"),obj.first_name);
    app_ui_standard_appendFieldText(grid, "id_last_name", "last_name", gRb("db.app_user.last_name"),obj.last_name);
    app_ui_standard_appendFieldText(grid, "id_email", "email", gRb("db.app_user.email"),obj.email);
    app_ui_standard_appendFieldTimestamp(grid, "id_created", "created", gRb("db.app_user.created"),appConvertTimestampToString(obj.created)).prop('disabled', true);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_user.nota"),obj.nota);
    app_ui_standard_appendFieldTextArea(grid, "id_ldap_info", "ldap_info", gRb("db.app_user.ldap_info"),obj.ldap_info).prop('disabled', true);
    app_ui_standard_appendFieldTextArea(grid, "id_app_user_propertys", "app_user_propertys", gRb("db.app_user.propertys"),app_JSONStringify(obj.app_user_propertys));
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_user_update")) {
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
                    app_app_user_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_app_user_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_user_id:   $('#id_app_user_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_user/app_user_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_user_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_user_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_app_user_logs"
                                        , controller: "app_user/app_user_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                app_user_id: obj.app_user_id
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
    appSetPage(page, gRb("db.app_user.update.title") + " " + obj.user_name);
    //-
    // app_test_showMobilePropertiesInfo();
    app_app_user_form_update_old_data = app_app_user_form_update_getData();
}


//-
function app_app_user_form_update_doUpdate(onSuccessCallback) {
    var validate = app_app_user_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_user_form_update_getData();
        // console.log(app_JSONStringify(app_app_user_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_user_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_user/app_user_update"
                                        , data
                                        , function(content) {
                                            app_app_user_form_update_old_data = app_app_user_form_update_getData();
                                            app_setMessage(gRb("db.app_user.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}



function app_app_user_form_update_getData() {
    var data =  { 
         app_user_id:   $('#id_app_user_id').val()
         , user_name:   $('#id_user_name').val()
         , first_name:   $('#id_first_name').val()
         , last_name:   $('#id_last_name').val()
         , email:   $('#id_email').val()
         , created:   appConvertStringToTimestamp($('#id_created').val())
         , nota:   $('#id_nota').val()
         , ldap_info:   $('#id_ldap_info').val()
         , app_user_propertys: JSON.parse($('#id_app_user_propertys').val())
    };
    //- log(app_JSONStringify(data));
    return(data);
}


function app_app_user_form_update_validate() {
    var rv = $('<ul/>');
    ($('#id_created').val() == "") || sys_timestampFormatUi_validation.test($('#id_created').val())
        || (rv.append($("<li/>").text(gRb("db.app_user.created") + " - " + gRb("db.sys.invalidValue") )));
    ((/^[a-zA-z0-9\-\.]+$/).test($('#id_user_name').val()) || (rv.append($("<li/>").text("Username non corretto"))));
    ((/^.+$/).test($('#id_first_name').val()) || (rv.append($("<li/>").text("Il nome non puo' essere vuoto"))));
    ((/^.+$/).test($('#id_last_name').val()) || (rv.append($("<li/>").text("Il cognome non puo' essere vuoto"))));
    ($('#id_email').val() == "") || (sys_email_validation.test($('#id_email').val()) || (rv.append($("<li/>").text("Formato Email non valido"))));
    // (sys_timestampFormatUi_validation.test($('#id_created').val()) || (rv.append($("<li/>").text("Data creazione non valida"))));
    try{
        JSON.parse($('#id_app_user_propertys').val());
    } catch (err) {
        rv.append($("<li/>").text(gRb("db.app_user.propertys_nonValide")) + " " + err);
    }
    return rv;
}


function app_app_user_form_changePassword() {
    appSetPage(form, gRb("db.app_user.update.changePassword"));
    var app_user_id = getLocationParameterByName('app_user_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append($('<h3/>').text("Aggiorna app_user"));
    form.append("retrieving " + app_user_id)
    //-
    appSetPage(form, gRb("db.app_user.update.title"));
    //-
    app_doRequestMappingRequest("app_user/app_user_getForPassword"
                                , { app_user_id: app_user_id}
                                , function(content) {
                                    app_app_user_form_changePassword_data(content);
                                });
}

//-
var app_app_user_form_changePassword_old_data = undefined;
//-
function app_app_user_form_changePassword_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    //-
    var obj = content.obj;
    form.submit(function(){ return(false);} );
    //-
    var obj = content.obj;
    page.append(app_app_user_tabs(obj));
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    app_ui_standard_appendFieldHidden(form, "id_app_user_id", "app_user_id", "label", obj.app_user_id);
    app_ui_standard_appendFieldText(grid, "id_user_name", "user_name", gRb("db.app_user.user_name"),obj.user_name).prop('disabled', true);
    app_ui_standard_appendFieldPassword(grid, "id_password", "password", gRb("db.app_user.password"),"");
    app_ui_standard_appendFieldPassword(grid, "id_password_confirm", "password_confirm", gRb("db.app_user.password_confirm"),"");
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_user_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(function(event) {
                    var onSuccess = function() { };
                    app_app_user_form_changePassword_doUpdate(onSuccess);
                })
        );
    }
    appSetPage(page, gRb("db.app_user.update.changePassword") + " " + obj.user_name);
    //-
    // app_test_showMobilePropertiesInfo();
    app_app_user_form_changePassword_old_data = app_app_user_form_changePassword_getData();
    //-
}

function app_app_user_form_changePassword_doUpdate(onSuccessCallback) {
    var validate = app_app_user_form_changePassword_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_user_form_changePassword_getData();
        // console.log(app_JSONStringify(app_app_user_form_changePassword_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_user_form_changePassword_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_user/app_user_updateForPassword"
                                        , data
                                        , function(content) {
                                            app_app_user_form_changePassword_old_data = app_app_user_form_changePassword_getData();
                                            app_setMessage(gRb("db.app_user.changePassword.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

function app_app_user_form_changePassword_validate() {
    var rv = $('<ul/>');
    (($('#id_password').val() == $('#id_password_confirm').val() ) || (rv.append($("<li/>").text("La password di conferma differisce dalla password"))));
    return rv;
}

function app_app_user_form_changePassword_getData() {
    var data =  { 
         app_user_id:   $('#id_app_user_id').val()
         , password:   $('#id_password').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}


//-
//-
//- permissions
//-
//-



function app_app_permission_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_permission_super_id = getLocationParameterByName('app_permission_super_id');
    // if (app_permission_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_permission_super/app_permission_super_get"
    //                                 , { app_permission_super_id: app_permission_super_id}
    //                                 , function(content) {
    //                                     var app_permission_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_permission_super_tabs(app_permission_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_permission_super_id" id="id_app_permission_super_id" type="hidden" />')
    //                 .val(app_permission_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_permission.name"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_permission.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                // event.preventDefault();
                var validate = app_app_permission_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_permission_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_permission.insert.title"));
}
//-
function app_app_permission_form_insert_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_permission_form_insert_sendData() {
    var data =  { 
         name:   $('#id_name').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("app_user/app_permission_insert"
                                , data
                                , function(content) {
                                    var app_permission_id = content.app_permission_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_user__app_app_permission_form_update"
                                            + "&app_permission_id="+app_permission_id);
                                });
    //-
}
//-

function app_app_permission_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_permission.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_permission_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_app_user__app_app_permission_form_update"
                                 + "&app_permission_id="+row[mapIdName["app_permission_id"]]
                                )
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(app_permission_super)
                   // .append(app_permission_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_user/app_permission_list"
                                , buttonsCell: buttonsCell
                                , content: {
                                    qparams: { } 
                                }
                                , enable_csv_output: true
                               };
    var tabsDiv = $("<div>");
    page.append(tabsDiv);
    tableMattoni_options.content.qparams.where = {};
    // -- if this is the "list" of detail of the "super" master, add the super in the where clause
    // var app_permission_super_id = getLocationParameterByName('app_permission_super_id');
    // if (app_permission_super_id != "") {
    //     app_doRequestMappingRequest("app_permission_super/app_permission_super_get"
    //                                 , { app_permission_super_id: app_permission_super_id}
    //                                 , function(content) {
    //                                     var app_permission_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_permission_super_tabs(app_permission_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_permission_super_id = app_permission_super_id;
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
        //-
        form.submit(function(){ return(false);} );
        form.keypress(function (event) {
            if (event.keyCode == 10 || event.keyCode == 13) {
                divTable.tableMattoni().render();
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
                }
                      )
        );
        if (app_userHasPermission("db_app_permission_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_permission.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_permission_form_insert")
                    }
                          )
            );
        }
        //-
        //- where clause
        //-
        var fieldcontain = $('<div>').hide();
        //-
        fieldcontain.append($("<h3>").text(gRb("db.sys.ricercaPerParametri")));
        //-
        app_where_append_string(fieldcontain, gRb("db.app_permission.name"),"name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_permission.nota"),"nota", divTable);
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
        grid.append(fieldcontain);
        page.append(form);
    }
    //-
    appSetPage(page, gRb("db.app_permission.list.title"));
    //-
}


//-
function app_app_permission_tabs(app_permission) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_permission_read")) {
        var ls = "?page=app_app_permission__app_app_permission_form_update&app_permission_id="+app_permission.app_permission_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_permission..single") + ": " + app_permission.name)
        );
    }
    // if (app_userHasPermission("db_app_permission_sub_read")) {
    //     var la = "?page=app_app_permission_sub__app_app_permission_sub_list&app_permission_id="+app_permission.app_permission_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.app_permission_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rv);
}

//-
function app_app_permission_form_update() {
    var app_permission_id = getLocationParameterByName('app_permission_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_permission.update.title"));
    //-
    app_doRequestMappingRequest("app_user/app_permission_get"
                                , { app_permission_id: app_permission_id}
                                , function(content) {
                                    app_app_permission_form_update_data(content);
                                });
}
//-
var app_app_permission_form_update_old_data = undefined;
//-
function app_app_permission_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_permission_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    app_ui_standard_appendFieldHidden(form, "id_app_permission_id", "app_permission_id", "label", obj.app_permission_id);
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_permission.name"),obj.name);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_permission.nota"),obj.nota);
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_permission_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(function(event) {
                    var validate = app_app_permission_form_update_doUpdate();
                    if (validate.children().length == 0 && logTable != undefined ) {
                        setTimeout(function() {
                            logTable.tableMattoni().render();
                        }, 1000);
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_permission_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_permission_id:   $('#id_app_permission_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_user/app_permission_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_permission_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_permission_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable = $("<div>");
                        tableMattoni_options =
                            {
                                tableid: "table_app_permission_logs"
                                , controller: "app_user/app_permission_logs"
                                , buttonsCell: function(tr, mapIdName, row) {
                                    tr.prepend($('<td>').append(""));
                                }
                                , content: {
                                    qparams: {
                                        app_permission_id: obj.app_permission_id
                                        , order:[{ column:"date", desc: true}]
                                    }
                                }
                            }
                        ;
                        appGetWidget("custom.tableMattoni", logTable, tableMattoni_options);
                        logTable.hide();
                        logs.html(logTable);
                        setTimeout(function() {
                            logTable.show("slow")
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
    appSetPage(page, gRb("db.app_permission.update.title") + " " + obj.app_permission_id);
    //-
    page.trigger('create');
    // app_test_showMobilePropertiesInfo();
    app_app_permission_form_update_old_data = app_app_permission_form_update_getData();
}
//-
function app_app_permission_form_update_doUpdate() {
    var validate = app_app_permission_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_permission_form_update_getData();
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_permission_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_user/app_permission_update"
                                        , data
                                        , function(content) {
                                            app_app_permission_form_update_old_data = app_app_permission_form_update_getData();
                                            app_setMessage(gRb("db.app_permission.update.Updated"));
                                        }
                                       );
            //-
        }
    }
    return(validate);
}
//-
function app_app_permission_form_update_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_permission_form_update_getData() {
    var data =  { 
         app_permission_id:   $('#id_app_permission_id').val()
         , name:   $('#id_name').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}




//-
//-
//- roles
//-
//-
//-
function app_app_role_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_role_super_id = getLocationParameterByName('app_role_super_id');
    // if (app_role_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_role_super/app_role_super_get"
    //                                 , { app_role_super_id: app_role_super_id}
    //                                 , function(content) {
    //                                     var app_role_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_role_super_tabs(app_role_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_role_super_id" id="id_app_role_super_id" type="hidden" />')
    //                 .val(app_role_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_role.name"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_role.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_app_role_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_role_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_role.insert.title"));
    // }
}
//-
function app_app_role_form_insert_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_role_form_insert_sendData() {
    var data =  { 
         name:   $('#id_name').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("app_user/app_role_insert"
                                , data
                                , function(content) {
                                    var app_role_id = content.app_role_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_user__app_app_role_form_update"
                                            + "&app_role_id="+app_role_id);
                                });
    //-
}
//-
function app_app_role_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_role.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_role_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_app_user__app_app_role_form_update"
                                 + "&app_role_id="+row[mapIdName["app_role_id"]]
                                )
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   .append(
                       app_userHasPermission("db_app_user_read") ? (
                           app_ui_clickableLink("?page=app_app_user__app_app_group_role_user_list"
                                                + "&app_role_id="+row[mapIdName["app_role_id"]]
                                               )
                               .attr('title',gRb("db.app_group_role_user..plural"))
                               .append(app_getIcon("user", 15))
                       ) : ""
                   )
                   // .append(app_role_super)
                   // .append(app_role_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_user/app_role_list"
                                , buttonsCell: buttonsCell
                                , content: {
                                    qparams: { } 
                                }
                                , enable_csv_output: true
                               };
    var tabsDiv = $("<div>");
    page.append(tabsDiv);
    tableMattoni_options.content.qparams.where = {};
    // -- if this is the "list" of detail of the "super" master, add the super in the where clause
    // var app_role_super_id = getLocationParameterByName('app_role_super_id');
    // if (app_role_super_id != "") {
    //     app_doRequestMappingRequest("app_role_super/app_role_super_get"
    //                                 , { app_role_super_id: app_role_super_id}
    //                                 , function(content) {
    //                                     var app_role_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_role_super_tabs(app_role_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_role_super_id = app_role_super_id;
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
        //-
        form.submit(function(){ return(false);} );
        form.keypress(function (event) {
            if (event.keyCode == 10 || event.keyCode == 13) {
                divTable.tableMattoni().render();
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
                }
                      )
        );
        if (app_userHasPermission("db_app_role_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_role.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_role_form_insert"
                                                              // + "&app_role_super_id=" + app_role_super_id
                                                             )
                    }
                          )
            );
        }
        //-
        //- where clause
        //-
        var fieldcontain = $('<div>').hide();
        //-
        fieldcontain.append($("<h3>").text(gRb("db.sys.ricercaPerParametri")));
        //-
        app_where_append_string(fieldcontain, gRb("db.app_role.name"), "name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_role.nota"), "nota", divTable);
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
        grid.append(fieldcontain);
        page.append(form);
    }
    //-
    appSetPage(page, gRb("db.app_role.list.title"));
    //-
}
//-
function app_app_role_tabs(app_role) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_role_read")) {
        var ls = "?page=app_app_role__app_app_role_form_update&app_role_id="+app_role.app_role_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_role..single") + ": " + app_role.name)
        );
    }
    if (app_userHasPermission("db_app_group_role_user_read")) {
        var ls = "?page=app_app_user__app_app_group_role_user_list&app_role_id="+app_role.app_role_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .append(app_getIcon("user_lock", 15))
                .append(gRb("db.app_group_role_user..plural"))
        );
    }
    // if (app_userHasPermission("db_app_role_sub_read")) {
    //     var la = "?page=app_app_role_sub__app_app_role_sub_list&app_role_id="+app_role.app_role_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.app_role_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rv);
}
//-
function app_app_role_form_update() {
    var app_role_id = getLocationParameterByName('app_role_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_role.update.title"));
    //-
    app_doRequestMappingRequest("app_user/app_role_get_with_permissions"
                                , { app_role_id: app_role_id}
                                , function(content) {
                                    app_app_role_form_update_data(content);
                                });
}

//-
var app_app_role_form_update_old_data = undefined;
//-
function app_app_role_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_role_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_app_role_id", "app_role_id", "label", obj.app_role_id);
    app_ui_standard_appendFieldHidden(form, "id_app_role_id", "app_role_id", "label", obj.app_role_id);
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_role.name"),obj.name);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_role.nota"),obj.nota);
    //-
    //- permissions
    //-
    //-
    var permission_for_role = content.permission_for_role;
    permission_for_role.legend = gRb("db.app_permission..plural");
    permission_for_role.name   = "permissions_sortablecheckboxes";
    permission_for_role.change = function(theWidget){
        $("#id_permissions_text").val(theWidget.values());
    };
    var permissions_sortablecheckboxes =
        appGetWidget("custom.sortablecheckboxes",
                     $("<div>")
                     , permission_for_role);
    grid.append(permissions_sortablecheckboxes.element);
    var permissions_text =
        $("<input type='hidden' id='id_permissions_text' name='permissions_text'>")
        .val(permissions_sortablecheckboxes.values());
    ;
    grid.append(permissions_text);
    //-
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_role_update")) {
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
                    app_app_role_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_app_role_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_role_id:   $('#id_app_role_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_user/app_role_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_role_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_role_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_app_role_logs"
                                        , controller: "app_user/app_role_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                app_role_id: obj.app_role_id
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
    appSetPage(page, gRb("db.app_role.update.title") + " " + obj.app_role_id);
    //-
    // app_test_showMobilePropertiesInfo();
    app_app_role_form_update_old_data = app_app_role_form_update_getData();
}

//-
function app_app_role_form_update_doUpdate(onSuccessCallback) {
    var validate = app_app_role_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_role_form_update_getData();
        // console.log(app_JSONStringify(app_app_role_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_role_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_user/app_role_update"
                                        , data
                                        , function(content) {
                                            app_app_role_form_update_old_data = app_app_role_form_update_getData();
                                            app_setMessage(gRb("db.app_role.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_app_role_form_update_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_role_form_update_getData() {
    var val = $('#id_permissions_text').val();
    // alert("val: [" + val+"]");
    var permissions = [];
    if (val != "") {
        permissions = val.split(",")
    }
    var data =  { 
        app_role_id:   $('#id_app_role_id').val()
        , name:   $('#id_name').val()
        , nota:   $('#id_nota').val()
        , permissions: permissions
    };
    //- log(app_JSONStringify(data));
    return(data);
}


//-
//-
//- groups
//-

//-
function app_app_group_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_group_super_id = getLocationParameterByName('app_group_super_id');
    // if (app_group_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_group_super/app_group_super_get"
    //                                 , { app_group_super_id: app_group_super_id}
    //                                 , function(content) {
    //                                     var app_group_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_group_super_tabs(app_group_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_group_super_id" id="id_app_group_super_id" type="hidden" />')
    //                 .val(app_group_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_group.name"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_group.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_app_group_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_group_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_group.insert.title"));
    // }
}
//-
function app_app_group_form_insert_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_group_form_insert_sendData() {
    var data =  { 
         name:   $('#id_name').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("app_user/app_group_insert"
                                , data
                                , function(content) {
                                    var app_group_id = content.app_group_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_user__app_app_group_form_update"
                                            + "&app_group_id="+app_group_id);
                                });
    //-
}
//-
function app_app_group_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_group.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_group_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_app_user__app_app_group_form_update"
                                 + "&app_group_id="+row[mapIdName["app_group_id"]]
                                )
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(app_group_super)
                   // .append(app_group_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_user/app_group_list"
                                , buttonsCell: buttonsCell
                                , content: {
                                    qparams: { } 
                                }
                                , enable_csv_output: true
                               };
    var tabsDiv = $("<div>");
    page.append(tabsDiv);
    tableMattoni_options.content.qparams.where = {};
    // -- if this is the "list" of detail of the "super" master, add the super in the where clause
    // var app_group_super_id = getLocationParameterByName('app_group_super_id');
    // if (app_group_super_id != "") {
    //     app_doRequestMappingRequest("app_group_super/app_group_super_get"
    //                                 , { app_group_super_id: app_group_super_id}
    //                                 , function(content) {
    //                                     var app_group_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_group_super_tabs(app_group_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_group_super_id = app_group_super_id;
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
        //-
        form.submit(function(){ return(false);} );
        form.keypress(function (event) {
            if (event.keyCode == 10 || event.keyCode == 13) {
                divTable.tableMattoni().render();
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
                }
                      )
        );
        if (app_userHasPermission("db_app_group_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_group.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_group_form_insert"
                                                              // + "&app_group_super_id=" + app_group_super_id
                                                             )
                    }
                          )
            );
        }
        //-
        //- where clause
        //-
        var fieldcontain = $('<div>').hide();
        //-
        fieldcontain.append($("<h3>").text(gRb("db.sys.ricercaPerParametri")));
        //-
        app_where_append_string(fieldcontain, gRb("db.app_group.name"), "name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_group.nota"), "nota", divTable);
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
        grid.append(fieldcontain);
        page.append(form);
    }
    //-
    appSetPage(page, gRb("db.app_group.list.title"));
    //-
}
//-
function app_app_group_tabs(app_group) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_group_read")) {
        var ls = "?page=app_app_group__app_app_group_form_update&app_group_id="+app_group.app_group_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_group..single") + ": " + app_group.name)
        );
    }
    if (app_userHasPermission("db_app_group_role_user_read")) {
        var ls = "?page=app_app_user__app_app_group_role_user_list&app_group_id="+app_group.app_group_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .append(app_getIcon("user_lock", 15))
                .append(gRb("db.app_group_role_user..plural"))
        );
    }
    // if (app_userHasPermission("db_app_group_sub_read")) {
    //     var la = "?page=app_app_group_sub__app_app_group_sub_list&app_group_id="+app_group.app_group_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.app_group_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rv);
}
//-
function app_app_group_form_update() {
    var app_group_id = getLocationParameterByName('app_group_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_group.update.title"));
    //-
    app_doRequestMappingRequest("app_user/app_group_get"
                                , { app_group_id: app_group_id}
                                , function(content) {
                                    app_app_group_form_update_data(content);
                                });
}

//-
var app_app_group_form_update_old_data = undefined;
//-
function app_app_group_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_group_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_app_group_id", "app_group_id", "label", obj.app_group_id);
    app_ui_standard_appendFieldHidden(form, "id_app_group_id", "app_group_id", "label", obj.app_group_id);
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_group.name"),obj.name);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_group.nota"),obj.nota);
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_group_update")) {
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
                    app_app_group_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_app_group_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_group_id:   $('#id_app_group_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_user/app_group_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_user__app_app_group_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_group_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_app_group_logs"
                                        , controller: "app_user/app_group_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                app_group_id: obj.app_group_id
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
    appSetPage(page, gRb("db.app_group.update.title") + " " + obj.app_group_id);
    //-
    // app_test_showMobilePropertiesInfo();
    app_app_group_form_update_old_data = app_app_group_form_update_getData();
}

//-
function app_app_group_form_update_doUpdate(onSuccessCallback) {
    var validate = app_app_group_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_group_form_update_getData();
        // console.log(app_JSONStringify(app_app_group_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_group_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_user/app_group_update"
                                        , data
                                        , function(content) {
                                            app_app_group_form_update_old_data = app_app_group_form_update_getData();
                                            app_setMessage(gRb("db.app_group.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_app_group_form_update_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_group_form_update_getData() {
    var data =  { 
         app_group_id:   $('#id_app_group_id').val()
         , name:   $('#id_name').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}


//-
function app_app_group_role_user_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_group_role_user.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_group_role_user_list";
    var divTable=$("<div>");
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
                       $('<abbr>').attr('title',gRb("db.app_user..single")).append(
                           app_ui_clickableLink("?page=app_app_user__app_app_user_form_update"
                                                + "&app_user_id="+row[mapIdName["app_user_id"]]
                                               )
                               .append(app_getIcon("user", 15))
                       ))
                   .append(
                       $('<abbr>').attr('title',gRb("db.app_role..single")).append(
                           app_ui_clickableLink("?page=app_app_user__app_app_role_form_update"
                                                + "&app_role_id="+row[mapIdName["app_role_id"]]
                                               )
                               .append(app_getIcon("user_role", 15))
                       ))
                   .append(
                       $('<abbr>').attr('title',gRb("db.app_group..single")).append(
                           app_ui_clickableLink("?page=app_app_user__app_app_group_form_update"
                                                + "&app_group_id="+row[mapIdName["app_group_id"]]
                                               )
                               .append(app_getIcon("user_group", 15))
                       ))
                   .append(
                       (!app_userHasPermission("db_app_group_role_user_delete")) ?
                           "" :
                           (
                               $('<abbr>').attr('title',gRb("db.sys.deleteThisRecord"))
                                   .append(
                                       app_getIcon("times_circle", 15)
                                           .click(function() {
                                               var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                                               if (answer) {
                                                   app_doRequestMappingRequest(
                                                       "app_user/app_group_role_user_delete;"
                                                       , { app_group_role_user_id:
                                                           row[mapIdName["app_group_role_user_id"]] }
                                                       , function(content) {
                                                           divTable.tableMattoni().render();
                                                       });
                                               }
                                           }
                                                 )
                                   )
                           )
                       
                   )
                  );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_user/app_group_role_user_list"
                                , buttonsCell: buttonsCell
                                , content: {
                                    qparams: { } 
                                }
                                , enable_csv_output: true
                               };
    var tabsDiv = $("<div>");
    page.append(tabsDiv);
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
    //-
    //-
    //-
    var app_user_id = getLocationParameterByName('app_user_id');
    if (app_user_id != "") {
        app_doRequestMappingRequest("app_user/app_user_get"
                                    , { app_user_id: app_user_id}
                                    , function(content) {
                                        var user = content.obj;
                                        tabsDiv.append(app_app_user_tabs(user))
                                        ;
                                    });
        tableMattoni_options.content.qparams.where.app_user_id = app_user_id;
    };
    var app_role_id = getLocationParameterByName('app_role_id');
    if (app_role_id != "") {
        app_doRequestMappingRequest("app_user/app_role_get"
                                    , { app_role_id: app_role_id}
                                    , function(content) {
                                        var user = content.obj;
                                        tabsDiv.append(app_app_role_tabs(user))
                                        ;
                                    });
        tableMattoni_options.content.qparams.where.app_role_id = app_role_id;
    };
    var app_group_id = getLocationParameterByName('app_group_id');
    if (app_group_id != "") {
        app_doRequestMappingRequest("app_user/app_group_get"
                                    , { app_group_id: app_group_id}
                                    , function(content) {
                                        var user = content.obj;
                                        tabsDiv.append(app_app_group_tabs(user))
                                        ;
                                    });
        tableMattoni_options.content.qparams.where.app_group_id = app_group_id;
    };
    //-
    //- the div containing the table
    appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
    page.append(divTable);
    //-
    {
        //-
        //- area containing insert button and "show/hide"  filters
        //-
        var form = $('<form name="fm">');
        grid = app_ui_standard_getGrid(form);
        //-
        form.submit(function(){ return(false);} );
        form.keypress(function (event) {
            if (event.keyCode == 10 || event.keyCode == 13) {
                divTable.tableMattoni().render();
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
                }
                      )
        );
        //-
        {
            //-
            //- navigation and filter hooks
            //-
            divTable.tableMattoni().render_addHook(function(){
                var qp=app_JSONStringify(divTable.tableMattoni().qparams(),0).replace(/%/g,"%25");
                var url = ""
                    + "?page=app_app_user__app_app_group_role_user_list"
                    + "&qparams="+qp
                ;
                app_initialization_setHash(url);
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
        //-
        //-
        //-
        if (app_user_id != "") {
            //-
            //-
            //- Inserimento nuova permission.
            //- Appare solo quando mostra le permission di un utente.
            //-
            //-
            //- chiedo al server ruoli e gruppi
            app_doRequestMappingRequest("app_user/app_user_getallrolesandgroups"
                                        , { }
                                        , function(content) {
                                            //-
                                            // console.log(app_JSONStringify(content));
                                            //-
                                            var select_data_roles = [];
                                            var select_data_groups = [];
                                            { // converting roles
                                                select_data_roles
                                                    .push({ "value":"", "label":""});
                                                var i,a,o;
                                                a = content.all_roles;
                                                for (i = 0; i < a.length; i++) {
                                                    var o = a[i];
                                                    select_data_roles
                                                        .push({ "value":o.app_role_id
                                                                , "label":o.name});
                                                }
                                            }
                                            { // converting groups
                                                select_data_groups
                                                    .push({ "value":"", "label":""});
                                                var i,a,o;
                                                a = content.all_groups;
                                                for (i = 0; i < a.length; i++) {
                                                    var o = a[i];
                                                    select_data_groups
                                                        .push({ "value":o.app_group_id
                                                                , "label":o.name});
                                                }
                                            }
                                            //-
                                            //-
                                            //-
                                            var grid = app_ui_standard_getGrid(form);
                                            var insertline = app_ui_standard_appendRow(grid);
                                            var insert_button = app_ui_standard_button()
                                                .prop('disabled', true)
                                                .text(gRb("db.sys.insert"))
                                                .click(function(event) {
                                                    app_doRequestMappingRequest(
                                                        "app_user/app_group_role_user_insert"
                                                        , {
                                                            app_user_id: app_user_id
                                                            , app_group_id: $("#id_select_group").val()
                                                            , app_role_id: $("#id_select_role").val() }
                                                        , function(content) {
                                                            divTable.tableMattoni().render();
                                                        });
                                                    
                                                }
                                                      );
                                            //-
                                            function enable_insert() {
                                                insert_button
                                                    .prop('disabled'
                                                          , (($("#id_select_role").val() == "")
                                                             || ($("#id_select_group").val() == "")));
                                            }
                                            //-
                                            //-
                                            //-
                                            //-
                                            insertline
                                                .append(" ")
                                                .append(insert_button)
                                                .append(" " + gRb("db.app_role..single") + ": ")
                                                .append(
                                                    app_ui_standard_select("id_select_role"
                                                                           , "select_role"
                                                                           , select_data_roles
                                                                           , ""
                                                                          )
                                                        .change(function(event) {
                                                            enable_insert();
                                                        }
                                                               )
                                                )
                                                .append(" " + gRb("db.app_group..single") + ": ")
                                                .append(
                                                    app_ui_standard_select("id_select_group"
                                                                           , "select_group"
                                                                           , select_data_groups
                                                                           , ""
                                                                          )
                                                        .change(function(event) {
                                                            enable_insert();
                                                        }
                                                               )
                                                )
                                            ;
                                            
                                        });
        };
        page.append(form);
    }
    //-
    appSetPage(page, gRb("db.app_group_role_user.list.title"));
    //-
}



function app_app_user_ui_standard_appendSearch_user(grid
                                                    , id_app_user_user_name
                                                    , id_app_user_first_name
                                                    , id_app_user_last_name
                                                    , destination_value_field_id
                                                    , callback = null
                                                    , action_on_search    = false
                                                    , action_on_noRecords = false
                                                   ) {
    var rv = "";
    if (app_userHasPermission("db_app_user_read")) {
        var search_button =
            app_ui_standard_button()
            .text(gRb("db.sys.search"));
        var clear_button =
            app_ui_standard_button()
            .text(gRb("db.sys.clear"))
            .click(function(){
                $('#'+id_app_user_user_name).val("")
                $('#'+id_app_user_first_name).val("")
                $('#'+id_app_user_last_name).val("")
            })
        ;
        var search_area = $('<div>').html("").hide();
        var hide_button =
            app_ui_standard_button()
            .text(gRb("db.sys.hide"))
            .hide()
            .click(function() {
                search_area.hide(200,function(){ search_area.html(""); });
                hide_button.hide(200);
            });
        //-
        app_ui_standard_appendRow(grid)
            .append($('<span >')
                    .append(search_button)
                    .append(" ")
                    .append(clear_button)
                    .append(" ")
                    .append(hide_button)
                    .append(search_area)
                   )
        ;
        var divTable=null;
        search_button.click(function(event){
            if(action_on_search) {
                action_on_search();
            }
            if (search_area.html() == "") {
                hide_button.show();
                //- setup search area
                divTable=$("<div>");
                var tableid = "table_app_user_search";
                var buttonsCell = function(tr, mapIdName, row) {
                    var select =
                        $('<abbr>').attr('title',gRb("db.sys.doSelect")).append(
                            app_getIcon("check_circle", 15)
                                .click(function() {
                                    // alert("Hello " +row[mapIdName["app_user_id"]] + " - " + row[mapIdName["app_user_code"]]);
                                    $('#'+id_app_user_user_name).val(row[mapIdName["user_name"]])
                                    $('#'+id_app_user_first_name).val(row[mapIdName["first_name"]])
                                    $('#'+id_app_user_last_name).val(row[mapIdName["last_name"]])
                                    //-
                                    $('#'+destination_value_field_id).val(row[mapIdName["app_user_id"]]);
                                    search_area.hide(200,function(){ search_area.html(""); });
                                    hide_button.hide(200);
                                    if (callback != null) {
                                        callback();
                                    }
                                }
                                      )
                        );
                    // var edit =
                    //     $('<abbr>').attr('title',gRb("db.app_user.update.title")).append(
                    //         app_ui_clickableLink("?page=app_app_user__app_app_user_form_update"
                    //                              + "&app_user_id="+row[mapIdName["app_user_id"]]
                    //                             )
                    //             .append(app_getIcon("edit", 15))
                    //     );
                    tr.prepend($('<td>')
                               .append(select)
                               // .append(edit)
                              );
                    // log(" buttonsCell: " + );
                }
                var tableMattoni_options = {tableid: tableid
                                            , controller: "app_user/app_user_list"
                                            , buttonsCell: buttonsCell
                                            , content: {
                                                qparams: { } 
                                            }
                                            , enable_csv_output: true
                                            , action_on_noRecords: action_on_noRecords
                                           };
                tableMattoni_options.content.qparams.where = {
                    "user_name": $('#'+id_app_user_user_name).val()
                    , "first_name": $('#'+id_app_user_first_name).val()
                    , "last_name": $('#'+id_app_user_last_name).val()
                };
                appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
                //-
                search_area.append(divTable);
                search_area.show();
            } else {
                var vv ;
                vv = $('#'+id_app_user_user_name).val();
                vv = $('#'+id_app_user_first_name).val();
                vv = $('#'+id_app_user_last_name).val();
                divTable.tableMattoni().where("user_name", $('#'+id_app_user_user_name).val());
                divTable.tableMattoni().where("first_name", $('#'+id_app_user_first_name).val());
                divTable.tableMattoni().where("last_name", $('#'+id_app_user_last_name).val());
                divTable.tableMattoni().render();
            }
        });
    }
    return(rv);
};
