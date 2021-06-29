function app_app_system_log_doInitialize(callback) {
    log("app_system_log page initialization...");
    callback();
}
//-
function app_app_system_log_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_system_log_super_id = getLocationParameterByName('app_system_log_super_id');
    // if (app_system_log_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_system_log_super/app_system_log_super_get"
    //                                 , { app_system_log_super_id: app_system_log_super_id}
    //                                 , function(content) {
    //                                     var app_system_log_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_system_log_super_tabs(app_system_log_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_system_log_super_id" id="id_app_system_log_super_id" type="hidden" />')
    //                 .val(app_system_log_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldTimestamp(grid, "id_date", "date", gRb("db.app_system_log.date"),"");
    app_ui_standard_appendFieldText(grid, "id_user_name", "user_name", gRb("db.app_system_log.user_name"),"");
    app_ui_standard_appendFieldText(grid, "id_category", "category", gRb("db.app_system_log.category"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_value", "value", gRb("db.app_system_log.value"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_app_system_log_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_system_log_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_system_log.insert.title"));
    // }
}
//-
function app_app_system_log_form_insert_validate() {
    var rv = $('<ul/>');
    sys_timestampFormatUi_validation.test($('#id_date').val())
        || (rv.append($("<li/>").text(gRb("db.app_system_log.date") + " - " + gRb("db.sys.invalidValue") )));
    return rv;
}
//-
function app_app_system_log_form_insert_sendData() {
    var data =  { 
         date:   appConvertStringToTimestamp($('#id_date').val())
         , user_name:   $('#id_user_name').val()
         , category:   $('#id_category').val()
         , value:   $('#id_value').val()
    };
    //-
    app_doRequestMappingRequestSync("app_system_log/app_system_log_insert"
                                , data
                                , function(content) {
                                    var app_system_log_id = content.app_system_log_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_system_log__app_app_system_log_form_update"
                                            + "&app_system_log_id="+app_system_log_id);
                                });
    //-
}
//-
function app_app_system_log_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_system_log.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_system_log_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            $('<abbr>').attr('title',gRb("db.app_system_log.update.title")).append(
                app_ui_clickableLink("?page=app_app_system_log__app_app_system_log_form_update"
                                     + "&app_system_log_id="+row[mapIdName["app_system_log_id"]]
                                    )
                    .append(app_getIcon("edit", 15))
            );
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(app_system_log_super)
                   // .append(app_system_log_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_system_log/app_system_log_list"
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
    // var app_system_log_super_id = getLocationParameterByName('app_system_log_super_id');
    // if (app_system_log_super_id != "") {
    //     app_doRequestMappingRequest("app_system_log_super/app_system_log_super_get"
    //                                 , { app_system_log_super_id: app_system_log_super_id}
    //                                 , function(content) {
    //                                     var app_system_log_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_system_log_super_tabs(app_system_log_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_system_log_super_id = app_system_log_super_id;
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
        if (app_userHasPermission("db_app_system_log_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_system_log.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_system_log__app_app_system_log_form_insert"
                                                              // + "&app_system_log_super_id=" + app_system_log_super_id
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
        app_where_append_timestamp(fieldcontain, gRb("db.app_system_log.date"), "date", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_system_log.user_name"), "user_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_system_log.category"), "category", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_system_log.value"), "value", divTable);
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
    appSetPage(page, gRb("db.app_system_log.list.title"));
    //-
}
//-
function app_app_system_log_tabs(app_system_log) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_system_log_read")) {
        var ls = "?page=app_app_system_log__app_app_system_log_form_update&app_system_log_id="+app_system_log.app_system_log_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_system_log..single") + ": " + app_system_log.category + " - "+ appConvertTimestampToString(app_system_log.date))
        );
    }
    // if (app_userHasPermission("db_app_system_log_sub_read")) {
    //     var ls = "?page=app_app_system_log_sub__app_app_system_log_sub_list&app_system_log_id="+app_system_log.app_system_log_id+"";
    //     rv.append(
    //         app_ui_clickableLink(ls)
    //             .attr("class", "w3-bar-item w3-button")
    //             .text(gRb("db.app_system_log_sub.list.title"))
    //     );
    // }
    return(rv);
}
//-
function app_app_system_log_form_update() {
    var app_system_log_id = getLocationParameterByName('app_system_log_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_system_log.update.title"));
    //-
    app_doRequestMappingRequest("app_system_log/app_system_log_get"
                                , { app_system_log_id: app_system_log_id}
                                , function(content) {
                                    app_app_system_log_form_update_data(content);
                                });
}

//-
var app_app_system_log_form_update_old_data = undefined;
//-
function app_app_system_log_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_system_log_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_app_system_log_id", "app_system_log_id", "label", obj.app_system_log_id);
    app_ui_standard_appendFieldHidden(form, "id_app_system_log_id", "app_system_log_id", "label", obj.app_system_log_id);
    app_ui_standard_appendFieldTimestamp(grid, "id_date", "date", gRb("db.app_system_log.date"),appConvertTimestampToString(obj.date));
    app_ui_standard_appendFieldText(grid, "id_user_name", "user_name", gRb("db.app_system_log.user_name"),obj.user_name);
    app_ui_standard_appendFieldText(grid, "id_category", "category", gRb("db.app_system_log.category"),obj.category);
    app_ui_standard_appendFieldTextArea(grid, "id_value", "value", gRb("db.app_system_log.value"),obj.value);
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_system_log_update")) {
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
                    app_app_system_log_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_app_system_log_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_system_log_id:   $('#id_app_system_log_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_system_log/app_system_log_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_system_log__app_app_system_log_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_system_log_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_app_system_log_logs"
                                        , controller: "app_system_log/app_system_log_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                app_system_log_id: obj.app_system_log_id
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
    appSetPage(page, gRb("db.app_system_log.update.title") + " " + obj.category + " - "+ appConvertTimestampToString(obj.date));
    //-
    // app_test_showMobilePropertiesInfo();
    app_app_system_log_form_update_old_data = app_app_system_log_form_update_getData();
}

//-
function app_app_system_log_form_update_doUpdate(onSuccessCallback) {
    var validate = app_app_system_log_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_system_log_form_update_getData();
        // console.log(app_JSONStringify(app_app_system_log_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_system_log_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_system_log/app_system_log_update"
                                        , data
                                        , function(content) {
                                            app_app_system_log_form_update_old_data = app_app_system_log_form_update_getData();
                                            app_setMessage(gRb("db.app_system_log.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_app_system_log_form_update_validate() {
    var rv = $('<ul/>');
    sys_timestampFormatUi_validation.test($('#id_date').val())
        || (rv.append($("<li/>").text(gRb("db.app_system_log.date") + " - " + gRb("db.sys.invalidValue") )));
    return rv;
}
//-
function app_app_system_log_form_update_getData() {
    var data =  { 
         app_system_log_id:   $('#id_app_system_log_id').val()
         , date:   appConvertStringToTimestamp($('#id_date').val())
         , user_name:   $('#id_user_name').val()
         , category:   $('#id_category').val()
         , value:   $('#id_value').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}
