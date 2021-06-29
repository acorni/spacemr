function app_app_notifica_doInitialize(callback) {
    log("app_notifica page initialization...");
    callback();
}
//-
function app_app_notifica_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_notifica_super_id = getLocationParameterByName('app_notifica_super_id');
    // if (app_notifica_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_notifica_super/app_notifica_super_get"
    //                                 , { app_notifica_super_id: app_notifica_super_id}
    //                                 , function(content) {
    //                                     var app_notifica_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_notifica_super_tabs(app_notifica_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_notifica_super_id" id="id_app_notifica_super_id" type="hidden" />')
    //                 .val(app_notifica_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldTimestamp(grid, "id_date", "date", gRb("db.app_notifica.date"),appConvertTimestampToString((new Date()).getTime()));
    app_ui_standard_appendFieldText(grid, "id_bean_name", "bean_name", gRb("db.app_notifica.bean_name"),"");
    app_ui_standard_appendFieldInteger(grid, "id_object_id", "object_id", gRb("db.app_notifica.object_id"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_json_info", "json_info", gRb("db.app_notifica.json_info"),"");
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_serving", "fg_serving", gRb("db.app_notifica.fg_serving"),false);
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_app_notifica_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_notifica_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_notifica.insert.title"));
    // }
}
//-
function app_app_notifica_form_insert_validate() {
    var rv = $('<ul/>');
    sys_timestampFormatUi_validation.test($('#id_date').val())
        || (rv.append($("<li/>").text(gRb("db.app_notifica.date") + " - " + gRb("db.sys.invalidValue") )));
    sys_number_integer_validation.test($('#id_object_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_notifica.object_id") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_app_notifica_form_insert_sendData() {
    var data =  { 
         date:   appConvertStringToTimestamp($('#id_date').val())
         , bean_name:   $('#id_bean_name').val()
         , object_id:   $('#id_object_id').val()
         , json_info:   $('#id_json_info').val()
         , fg_serving:   $('#id_fg_serving').prop("checked")
    };
    //-
    app_doRequestMappingRequestSync("app_notifica/app_notifica_insert"
                                , data
                                , function(content) {
                                    var app_notifica_id = content.app_notifica_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_notifica__app_app_notifica_form_update"
                                            + "&app_notifica_id="+app_notifica_id);
                                });
    //-
}
//-
function app_app_notifica_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_notifica.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_notifica_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_app_notifica__app_app_notifica_form_update"
                                 + "&app_notifica_id="+row[mapIdName["app_notifica_id"]]
                                )
            .attr('title',gRb("db.app_notifica.update.title"))
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(app_notifica_super)
                   // .append(app_notifica_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_notifica/app_notifica_list"
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
    // -- if this is the "list" of detail of the "super" master, add the super in the where clause
    // var app_notifica_super_id = getLocationParameterByName('app_notifica_super_id');
    // if (app_notifica_super_id != "") {
    //     app_doRequestMappingRequest("app_notifica_super/app_notifica_super_get"
    //                                 , { app_notifica_super_id: app_notifica_super_id}
    //                                 , function(content) {
    //                                     var app_notifica_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_notifica_super_tabs(app_notifica_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_notifica_super_id = app_notifica_super_id;
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
        if (app_userHasPermission("db_app_notifica_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_notifica.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_notifica__app_app_notifica_form_insert"
                                                              // + "&app_notifica_super_id=" + app_notifica_super_id
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
        app_where_append_timestamp(fieldcontain, gRb("db.app_notifica.date"), "date", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_notifica.bean_name"), "bean_name", divTable);
        app_where_append_integer(fieldcontain, gRb("db.app_notifica.object_id"), "object_id", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_notifica.json_info"), "json_info", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.app_notifica.fg_serving"), "fg_serving", divTable);
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
                    + "?page=app_app_notifica__app_app_notifica_list"
                    + "&qparams="+qp
                ;
                app_initialization_setHash(url);
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
        //-
        grid.append(fieldcontain);
        page.append(form);
    }
    //-
    appSetPage(page, gRb("db.app_notifica.list.title"));
    //-
}
//-
function app_app_notifica_tabs(app_notifica) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_notifica_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_app_notifica__app_app_notifica_form_update&app_notifica_id="+app_notifica.app_notifica_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_notifica..single") + ": " + app_notifica.name)
        );
    }
    // if (app_userHasPermission("db_app_notifica_sub_read")) {
    //     var ls = "?page=app_app_notifica_sub__app_app_notifica_sub_list&app_notifica_id="+app_notifica.app_notifica_id+"";
    //     rv.append(
    //         app_ui_clickableLink(ls)
    //             .attr("class", "w3-bar-item w3-button")
    //             .text(gRb("db.app_notifica_sub.list.title"))
    //     );
    // }
    return(rv);
}
//-
function app_app_notifica_form_update() {
    var app_notifica_id = getLocationParameterByName('app_notifica_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_notifica.update.title"));
    //-
    app_doRequestMappingRequest("app_notifica/app_notifica_get"
                                , { app_notifica_id: app_notifica_id}
                                , function(content) {
                                    app_app_notifica_form_update_data(content);
                                });
}

//-
var app_app_notifica_form_update_old_data = undefined;
//-
function app_app_notifica_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_notifica_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    //-
    var updateFunction = function(event) {
        var onSuccess = function() {
            if (logTable != undefined ) {
                setTimeout(function() {
                    logTable.tableMattoni().render();
                }, 1000);
            }
        }
        app_app_notifica_form_update_doUpdate(onSuccess);
    };
    // var topbuttonline = app_ui_standard_appendRow(grid);
    // if (app_userHasPermission("db_app_notifica_update")) {
    //     topbuttonline.append(" ").append(
    //         app_ui_standard_button()
    //             .text(gRb("db.sys.update"))
    //             .click(updateFunction)
    //     );
    // }
    //-
    // app_ui_standard_appendFieldHidden(form, "id_app_notifica_id", "app_notifica_id", "label", obj.app_notifica_id);
    app_ui_standard_appendFieldHidden(form, "id_app_notifica_id", "app_notifica_id", "label", obj.app_notifica_id);
    app_ui_standard_appendFieldTimestamp(grid, "id_date", "date", gRb("db.app_notifica.date"),appConvertTimestampToString(obj.date));
    app_ui_standard_appendFieldText(grid, "id_bean_name", "bean_name", gRb("db.app_notifica.bean_name"),obj.bean_name);
    app_ui_standard_appendFieldInteger(grid, "id_object_id", "object_id", gRb("db.app_notifica.object_id"),obj.object_id);
    app_ui_standard_appendFieldTextArea(grid, "id_json_info", "json_info", gRb("db.app_notifica.json_info"),obj.json_info);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_serving", "fg_serving", gRb("db.app_notifica.fg_serving"),obj.fg_serving);
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_notifica_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    if (app_userHasPermission("db_app_notifica_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_notifica_id:   $('#id_app_notifica_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_notifica/app_notifica_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_notifica__app_app_notifica_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_notifica_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_app_notifica_logs"
                                        , controller: "app_notifica/app_notifica_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                app_notifica_id: obj.app_notifica_id
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
    appSetPage(page, gRb("db.app_notifica.update.title") + " " + obj.app_notifica_id);
    //-
    app_app_notifica_form_update_old_data = app_app_notifica_form_update_getData();
}

//-
function app_app_notifica_form_update_doUpdate(onSuccessCallback) {
    var validate = app_app_notifica_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_notifica_form_update_getData();
        // console.log(app_JSONStringify(app_app_notifica_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_notifica_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_notifica/app_notifica_update"
                                        , data
                                        , function(content) {
                                            app_app_notifica_form_update_old_data = app_app_notifica_form_update_getData();
                                            app_setMessage(gRb("db.app_notifica.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_app_notifica_form_update_validate() {
    var rv = $('<ul/>');
    sys_timestampFormatUi_validation.test($('#id_date').val())
        || (rv.append($("<li/>").text(gRb("db.app_notifica.date") + " - " + gRb("db.sys.invalidValue") )));
    sys_number_integer_validation.test($('#id_object_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_notifica.object_id") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_app_notifica_form_update_getData() {
    var data =  { 
         app_notifica_id:   $('#id_app_notifica_id').val()
         , date:   appConvertStringToTimestamp($('#id_date').val())
         , bean_name:   $('#id_bean_name').val()
         , object_id:   $('#id_object_id').val()
         , json_info:   $('#id_json_info').val()
         , fg_serving:   $('#id_fg_serving').prop("checked")
    };
    //- log(app_JSONStringify(data));
    return(data);
}
