function app_app_system_doInitialize(callback) {
    log("system page initialization...");
    callback();
}

//-
function app_app_system_property_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_system_property_super_id = getLocationParameterByName('app_system_property_super_id');
    // if (app_system_property_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_system_property_super/app_system_property_super_get"
    //                                 , { app_system_property_super_id: app_system_property_super_id}
    //                                 , function(content) {
    //                                     var app_system_property_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_system_property_super_tabs(app_system_property_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_system_property_super_id" id="id_app_system_property_super_id" type="hidden" />')
    //                 .val(app_system_property_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_system_property.name"),"");
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.app_system_property.description"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_value", "value", gRb("db.app_system_property.value"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_value_default", "value_default", gRb("db.app_system_property.value_default"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_system_property.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_app_system_property_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_system_property_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_system_property.insert.title"));
    // }
}
//-
function app_app_system_property_form_insert_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_system_property_form_insert_sendData() {
    var data =  { 
         name:   $('#id_name').val()
         , description:   $('#id_description').val()
         , value:   $('#id_value').val()
         , value_default:   $('#id_value_default').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("app_system/app_system_property_insert"
                                , data
                                , function(content) {
                                    var app_system_property_id = content.app_system_property_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_system__app_app_system_property_form_update"
                                            + "&app_system_property_id="+app_system_property_id);
                                });
    //-
}
//-
function app_app_system_property_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_system_property.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_system_property_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_app_system__app_app_system_property_form_update"
                                 + "&app_system_property_id="+row[mapIdName["app_system_property_id"]]
                                )
            .attr('title',gRb("db.app_system_property.update.title"))
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(app_system_property_super)
                   // .append(app_system_property_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_system/app_system_property_list"
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
    // var app_system_property_super_id = getLocationParameterByName('app_system_property_super_id');
    // if (app_system_property_super_id != "") {
    //     app_doRequestMappingRequest("app_system_property_super/app_system_property_super_get"
    //                                 , { app_system_property_super_id: app_system_property_super_id}
    //                                 , function(content) {
    //                                     var app_system_property_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_system_property_super_tabs(app_system_property_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_system_property_super_id = app_system_property_super_id;
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
        if (app_userHasPermission("db_app_system_property_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_system_property.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_system__app_app_system_property_form_insert"
                                                              // + "&app_system_property_super_id=" + app_system_property_super_id
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
        app_where_append_string(fieldcontain, gRb("db.app_system_property.name"), "name", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_system_property.description"), "description", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_system_property.value"), "value", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_system_property.value_default"), "value_default", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_system_property.nota"), "nota", divTable);
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
                    + "?page=app_app_system__app_app_system_property_list"
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
    appSetPage(page, gRb("db.app_system_property.list.title"));
    //-
}
//-
function app_app_system_property_tabs(app_system_property) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_system_property_read")) {
        var ls = "?page=app_app_system_property__app_app_system_property_form_update&app_system_property_id="+app_system_property.app_system_property_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_system_property..single") + ": " + app_system_property.name)
        );
    }
    // if (app_userHasPermission("db_app_system_property_sub_read")) {
    //     var la = "?page=app_app_system_property_sub__app_app_system_property_sub_list&app_system_property_id="+app_system_property.app_system_property_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.app_system_property_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rv);
}
//-
function app_app_system_property_form_update() {
    var app_system_property_id = getLocationParameterByName('app_system_property_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append($('<h3/>').text(gRb("db.app_system_property.update.title")));
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_system_property.update.title"));
    //-
    app_doRequestMappingRequest("app_system/app_system_property_get"
                                , { app_system_property_id: app_system_property_id}
                                , function(content) {
                                    app_app_system_property_form_update_data(content);
                                });
}

//-
var app_app_system_property_form_update_old_data = undefined;
//-
function app_app_system_property_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_system_property_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_app_system_property_id", "app_system_property_id", "label", obj.app_system_property_id);
    app_ui_standard_appendFieldHidden(form, "id_app_system_property_id", "app_system_property_id", "label", obj.app_system_property_id);
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.app_system_property.name"),obj.name);
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.app_system_property.description"),obj.description);
    app_ui_standard_appendFieldTextArea(grid, "id_value", "value", gRb("db.app_system_property.value"),obj.value);
    app_ui_standard_appendFieldTextArea(grid, "id_value_default", "value_default", gRb("db.app_system_property.value_default"),obj.value_default);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.app_system_property.nota"),obj.nota);
    //-
    var addPrettyPrint=function(id){
        grid.find("label[id='"+id+"_label']")
            .append(
                $('<span/>')
                    .text(" ("+gRb("db.app_system_property.t.json_pretty_print")+")")
                    .click(function(event){
                        event.preventDefault();
                        event.stopPropagation();
                        var t = $("#"+id);
                        var s = t.val();
                        console.log("--"+s+"--");
                        var j = JSON.parse(s);
                        t.val(app_JSONStringify(j));
                    }) 
            )
        ;
    }
    addPrettyPrint("id_value");
    addPrettyPrint("id_value_default");
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_system_property_update")) {
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
                    app_app_system_property_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_app_system_property_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_system_property_id:   $('#id_app_system_property_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_system/app_system_property_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_system__app_app_system_property_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_system_property_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_app_system_property_logs"
                                        , controller: "app_system/app_system_property_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                app_system_property_id: obj.app_system_property_id
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
    appSetPage(page, gRb("db.app_system_property.update.title") + " " + obj.app_system_property_id);
    //-
    // app_test_showMobilePropertiesInfo();
    app_app_system_property_form_update_old_data = app_app_system_property_form_update_getData();
}

//-
function app_app_system_property_form_update_doUpdate(onSuccessCallback) {
    var validate = app_app_system_property_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_system_property_form_update_getData();
        // console.log(app_JSONStringify(app_app_system_property_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_system_property_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_system/app_system_property_update"
                                        , data
                                        , function(content) {
                                            app_app_system_property_form_update_old_data = app_app_system_property_form_update_getData();
                                            app_setMessage(gRb("db.app_system_property.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_app_system_property_form_update_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_app_system_property_form_update_getData() {
    var data =  { 
         app_system_property_id:   $('#id_app_system_property_id').val()
         , name:   $('#id_name').val()
         , description:   $('#id_description').val()
         , value:   $('#id_value').val()
         , value_default:   $('#id_value_default').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}
