function app_spacemr_space_people_type_doInitialize(callback) {
    log("spacemr_space_people_type page initialization...");
    callback();
}
//-
function app_spacemr_space_people_type_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var spacemr_space_people_type_super_id = getLocationParameterByName('spacemr_space_people_type_super_id');
    // if (spacemr_space_people_type_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("spacemr_space_people_type_super/spacemr_space_people_type_super_get"
    //                                 , { spacemr_space_people_type_super_id: spacemr_space_people_type_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_people_type_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_people_type_super_tabs(spacemr_space_people_type_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="spacemr_space_people_type_super_id" id="id_spacemr_space_people_type_super_id" type="hidden" />')
    //                 .val(spacemr_space_people_type_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.spacemr_space_people_type.name"),"");
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space_people_type.description"),"");
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_is_a_seat", "fg_is_a_seat", gRb("db.spacemr_space_people_type.fg_is_a_seat"),false);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_people_type.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_space_people_type_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_space_people_type_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_space_people_type.insert.title"));
    // }
}
//-
function app_spacemr_space_people_type_form_insert_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_spacemr_space_people_type_form_insert_sendData() {
    var data =  { 
         name:   $('#id_name').val()
         , description:   $('#id_description').val()
         , fg_is_a_seat:   $('#id_fg_is_a_seat').prop("checked")
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_space_people_type/spacemr_space_people_type_insert"
                                , data
                                , function(content) {
                                    var spacemr_space_people_type_id = content.spacemr_space_people_type_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_space_people_type__app_spacemr_space_people_type_form_update"
                                            + "&spacemr_space_people_type_id="+spacemr_space_people_type_id);
                                });
    //-
}
//-
function app_spacemr_space_people_type_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space_people_type.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_spacemr_space_people_type_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            $('<abbr>').attr('title',gRb("db.spacemr_space_people_type.update.title")).append(
                app_ui_clickableLink("?page=app_spacemr_space_people_type__app_spacemr_space_people_type_form_update"
                                     + "&spacemr_space_people_type_id="+row[mapIdName["spacemr_space_people_type_id"]]
                                    )
                    .append(app_getIcon("edit", 15))
            );
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(spacemr_space_people_type_super)
                   // .append(spacemr_space_people_type_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "spacemr_space_people_type/spacemr_space_people_type_list"
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
    // var spacemr_space_people_type_super_id = getLocationParameterByName('spacemr_space_people_type_super_id');
    // if (spacemr_space_people_type_super_id != "") {
    //     app_doRequestMappingRequest("spacemr_space_people_type_super/spacemr_space_people_type_super_get"
    //                                 , { spacemr_space_people_type_super_id: spacemr_space_people_type_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_people_type_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_people_type_super_tabs(spacemr_space_people_type_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.spacemr_space_people_type_super_id = spacemr_space_people_type_super_id;
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
        if (app_userHasPermission("db_spacemr_space_people_type_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space_people_type.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_spacemr_space_people_type__app_spacemr_space_people_type_form_insert"
                                                              // + "&spacemr_space_people_type_super_id=" + spacemr_space_people_type_super_id
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
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_type.name"), "name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_type.description"), "description", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.spacemr_space_people_type.fg_is_a_seat"), "fg_is_a_seat", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_type.nota"), "nota", divTable);
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
    appSetPage(page, gRb("db.spacemr_space_people_type.list.title"));
    //-
}
//-
function app_spacemr_space_people_type_tabs(spacemr_space_people_type) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_spacemr_space_people_type_read")) {
        var ls = "?page=app_spacemr_space_people_type__app_spacemr_space_people_type_form_update&spacemr_space_people_type_id="+spacemr_space_people_type.spacemr_space_people_type_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_people_type..single") + ": " + spacemr_space_people_type.name)
        );
    }
    // if (app_userHasPermission("db_spacemr_space_people_type_sub_read")) {
    //     var la = "?page=app_spacemr_space_people_type_sub__app_spacemr_space_people_type_sub_list&spacemr_space_people_type_id="+spacemr_space_people_type.spacemr_space_people_type_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.spacemr_space_people_type_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rv);
}
//-
function app_spacemr_space_people_type_form_update() {
    var spacemr_space_people_type_id = getLocationParameterByName('spacemr_space_people_type_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space_people_type.update.title"));
    //-
    app_doRequestMappingRequest("spacemr_space_people_type/spacemr_space_people_type_get"
                                , { spacemr_space_people_type_id: spacemr_space_people_type_id}
                                , function(content) {
                                    app_spacemr_space_people_type_form_update_data(content);
                                });
}

//-
var app_spacemr_space_people_type_form_update_old_data = undefined;
//-
function app_spacemr_space_people_type_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_spacemr_space_people_type_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_spacemr_space_people_type_id", "spacemr_space_people_type_id", "label", obj.spacemr_space_people_type_id);
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_people_type_id", "spacemr_space_people_type_id", "label", obj.spacemr_space_people_type_id);
    app_ui_standard_appendFieldText(grid, "id_name", "name", gRb("db.spacemr_space_people_type.name"),obj.name);
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space_people_type.description"),obj.description);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_is_a_seat", "fg_is_a_seat", gRb("db.spacemr_space_people_type.fg_is_a_seat"),obj.fg_is_a_seat);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_people_type.nota"),obj.nota);
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_people_type_update")) {
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
                    app_spacemr_space_people_type_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_people_type_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            spacemr_space_people_type_id:   $('#id_spacemr_space_people_type_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_space_people_type/spacemr_space_people_type_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_space_people_type__app_spacemr_space_people_type_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_people_type_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_space_people_type_logs"
                                        , controller: "spacemr_space_people_type/spacemr_space_people_type_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_space_people_type_id: obj.spacemr_space_people_type_id
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
    appSetPage(page, gRb("db.spacemr_space_people_type.update.title") + " " + obj.name);
    //-
    // app_test_showMobilePropertiesInfo();
    app_spacemr_space_people_type_form_update_old_data = app_spacemr_space_people_type_form_update_getData();
}

//-
function app_spacemr_space_people_type_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_space_people_type_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_space_people_type_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_space_people_type_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_space_people_type_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("spacemr_space_people_type/spacemr_space_people_type_update"
                                        , data
                                        , function(content) {
                                            app_spacemr_space_people_type_form_update_old_data = app_spacemr_space_people_type_form_update_getData();
                                            app_setMessage(gRb("db.spacemr_space_people_type.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_space_people_type_form_update_validate() {
    var rv = $('<ul/>');
    return rv;
}
//-
function app_spacemr_space_people_type_form_update_getData() {
    var data =  { 
         spacemr_space_people_type_id:   $('#id_spacemr_space_people_type_id').val()
         , name:   $('#id_name').val()
         , description:   $('#id_description').val()
         , fg_is_a_seat:   $('#id_fg_is_a_seat').prop("checked")
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}
