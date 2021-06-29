
let app_spacemr_space_people_book_map_current_date = 
    new Date((new Date()).getFullYear(),(new Date()).getMonth(), (new Date()).getDate(),0,0,0 );

let app_spacemr_space_people_view_mode = "people";
let app_spacemr_space_people_menuInSpaces_status = 'mapModeMenu'

let app_spacemr_space_people_book_map_leftMenu_hidden = true;

function app_spacemr_space_map_doInitialize(callback) {
    log("spacemr_space_map page initialization...");
    if (app_userHasPermission("db_spacemr_space_people_book_read")) {
        app_spacemr_space_people_book_doInitialize(function(){
            callback();
        });
    } else {
        callback();
    }
}

//-
function app_spacemr_space_map_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    form.append(app_spacemr_space_tabs_get(spacemr_space_id));
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", gRb("db.spacemr_space_people.spacemr_space_id"),spacemr_space_id);
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space_map.description"),"");
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_default_map", "fg_default_map", gRb("db.spacemr_space_map.fg_default_map"),false);
    app_ui_standard_appendFieldTextArea(grid, "id_info", "info", gRb("db.spacemr_space_map.info"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_map.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_space_map_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_space_map_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_space_map.insert.title"));
    // }
}
//-
function app_spacemr_space_map_form_insert_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_spacemr_space_id').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_map.spacemr_space_id") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_space_map_form_insert_sendData() {
    var data =  {
         spacemr_space_id:   $('#id_spacemr_space_id').val()
         , description:   $('#id_description').val()
         , fg_default_map:   $('#id_fg_default_map').prop("checked")
         , info:   $('#id_info').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_space_map/spacemr_space_map_insert"
                                , data
                                , function(content) {
                                    var spacemr_space_map_id = content.spacemr_space_map_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_space_map__app_spacemr_space_map_form_update"
                                            + "&spacemr_space_map_id="+spacemr_space_map_id);
                                });
    //-
}

//-
function app_spacemr_space_map_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space_map.list.title"));
    //-
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    if ( spacemr_space_id == "") spacemr_space_id = undefined;
    //-
    var page    = $("<div>");
    if ( spacemr_space_id != undefined) {
        page.append(app_spacemr_space_tabs_get(spacemr_space_id));
    }
    var tableid = "table_spacemr_space_map_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            $('<abbr>').attr('title',gRb("db.spacemr_space_map.maps.people")).append(
                app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                     + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_map_id"]]
                                    )
                    .append(app_getIcon("map", 15))
            )
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   .append(
                       $('<abbr>').attr('title',gRb("db.spacemr_space_map.update.title")).append(
                           app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_form_update"
                                                + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_map_id"]]
                                               )
                               .append(app_getIcon("edit", 15))
                       )
                   )
                   .append(
                       app_userHasPermission("db_spacemr_space_read") ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space..single")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_list"
                                                    + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                                   )
                                   .append(app_getIcon("home", 15)))
                       ) : ""
                   )
                   // .append(spacemr_space_map_super)
                   // .append(spacemr_space_map_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "spacemr_space_map/spacemr_space_map_list"
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
    // var spacemr_space_map_super_id = getLocationParameterByName('spacemr_space_map_super_id');
    // if (spacemr_space_map_super_id != "") {
    //     app_doRequestMappingRequest("spacemr_space_map_super/spacemr_space_map_super_get"
    //                                 , { spacemr_space_map_super_id: spacemr_space_map_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_map_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_map_super_tabs(spacemr_space_map_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.spacemr_space_map_super_id = spacemr_space_map_super_id;
    // };
    if ( spacemr_space_id != undefined) {
        tableMattoni_options.content.qparams.where.spacemr_space_id = spacemr_space_id;
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
        if (app_userHasPermission("db_spacemr_space_map_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space_map.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_spacemr_space_map__app_spacemr_space_map_form_insert"
                                                              + "&spacemr_space_id=" + spacemr_space_id
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
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_map.spacemr_space_code"), "spacemr_space_code", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_map.description"), "description", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.spacemr_space_map.fg_default_map"), "fg_default_map", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_map.info"), "info", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_map.nota"), "nota", divTable);
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
                    + "?page=app_spacemr_space_map__app_spacemr_space_map_list"
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
    appSetPage(page, gRb("db.spacemr_space_map.list.title"));
    //-
}
//-
function app_spacemr_space_map_tabs(spacemr_space_map) {
    var rrv = $('<span>');
    var rv = $('<div class="w3-bar w3-theme-d4">');
    var rvh = $('<span style="display: none;">');
    rrv.append(rvh)
        .append(rv)
    ;
    var toggle_this_menu = function(){
        rv.toggle("fast");
        rvh.toggle("fast");
    };
    rvh.append(app_getIcon("bars", 15))
        .append("DM")
        .click(toggle_this_menu)
    ;
    // console.log("window.innerWidth: " + window.innerWidth);
    if (window.innerWidth < 600) {
        rvh.attr("style", "");
        rv.attr("style", "display: none;");
    }
    rv.append($("<span>")
                .attr("class", "w3-bar-item w3-button")
                .append(app_getIcon("caret_left", 15))
                .click(toggle_this_menu)
               )
    ;
    if (app_userHasPermission("db_spacemr_space_map_update")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_form_update&spacemr_space_map_id="+spacemr_space_map.spacemr_space_map_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_map.update.title")
                      + ": " + spacemr_space_map.description)
        );
    }
    if (app_userHasPermission("db_spacemr_space_map_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people&spacemr_space_map_id="+spacemr_space_map.spacemr_space_map_id)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_map.maps.people"))
                .append(" ")
                .append(app_getIcon("map", 15))
        );
    }
   if (app_userHasPermission("db_spacemr_space_map_update")) {
         rv.append(
            app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_form_update_map&spacemr_space_map_id="+spacemr_space_map.spacemr_space_map_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_map.update.map.title"))
                .append(" ")
                .append(app_getIcon("edit", 15))
        );
    }
    // if (app_userHasPermission("db_spacemr_space_read")) {
    //     rv.append(
    //         app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id="+spacemr_space_map.spacemr_space_id+"")
    //             .attr("class", "w3-bar-item w3-button")
    //             .text(gRb("db.spacemr_space..single") + ": " + spacemr_space_map.spacemr_space_code)
    //     );
    // }
    return(rrv);
}

//-
function app_spacemr_space_map_form_update() {
    var spacemr_space_map_id = getLocationParameterByName('spacemr_space_map_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space_map.update.title"));
    //-
    app_doRequestMappingRequest("spacemr_space_map/spacemr_space_map_get"
                                , { spacemr_space_map_id: spacemr_space_map_id}
                                , function(content) {
                                    app_spacemr_space_map_form_update_data(content);
                                });
}
//-
var app_spacemr_space_map_form_update_old_data = undefined;
//-
function app_spacemr_space_map_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_spacemr_space_tabs_get(obj.spacemr_space_id));
    page.append(app_spacemr_space_map_tabs(obj));
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
        app_spacemr_space_map_form_update_doUpdate(onSuccess);
    };
    // var topbuttonline = app_ui_standard_appendRow(grid);
    // if (app_userHasPermission("db_spacemr_space_map_update")) {
    //     topbuttonline.append(" ").append(
    //         app_ui_standard_button()
    //             .text(gRb("db.sys.update"))
    //             .click(updateFunction)
    //     );
    // }
    //-
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_map_id", "spacemr_space_map_id", "label", obj.spacemr_space_map_id);
    // app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),obj.spacemr_space_code);
    // app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
    //                                                       , "id_spacemr_space_code"
    //                                                       , "id_spacemr_space_id"
    //                                                      );
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", gRb("db.spacemr_space_people.spacemr_space_id"),obj.spacemr_space_id);
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space_map.description"),obj.description);
    app_ui_standard_appendFieldCheckBox(grid, "id_fg_default_map", "fg_default_map", gRb("db.spacemr_space_map.fg_default_map"),obj.fg_default_map);
    app_ui_standard_appendFieldTextArea(grid, "id_info", "info", gRb("db.spacemr_space_map.info"),obj.info);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_map.nota"),obj.nota);
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_map_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    if (app_userHasPermission("db_spacemr_space_map_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  {
                            spacemr_space_map_id:   $('#id_spacemr_space_map_id').val()
                        };
                        // log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_space_map/spacemr_space_map_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_space_map__app_spacemr_space_map_list&spacemr_space_id="+obj.spacemr_space_id);
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_map_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_space_map_logs"
                                        , controller: "spacemr_space_map/spacemr_space_map_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_space_map_id: obj.spacemr_space_map_id
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
    appSetPage(page, gRb("db.spacemr_space_map.update.title") + " " + obj.description);
    //-
    // app_test_showMobilePropertiesInfo();
    app_spacemr_space_map_form_update_old_data = app_spacemr_space_map_form_update_getData();
}

//-
function app_spacemr_space_map_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_space_map_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_space_map_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_space_map_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_space_map_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("spacemr_space_map/spacemr_space_map_update"
                                        , data
                                        , function(content) {
                                            app_spacemr_space_map_form_update_old_data = app_spacemr_space_map_form_update_getData();
                                            app_setMessage(gRb("db.spacemr_space_map.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_space_map_form_update_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_spacemr_space_id').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_map.spacemr_space_id") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_space_map_form_update_getData() {
    var data =  {
         spacemr_space_map_id:   $('#id_spacemr_space_map_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , description:   $('#id_description').val()
         , fg_default_map:   $('#id_fg_default_map').prop("checked")
         , info:   $('#id_info').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}



function app_spacemr_space_map_get_map_manager() {
    var map_manager = {
        leftMenuWidth: 200
        , options: {
            svg_width: 10000
            , svg_height: 5000
        }
        , inSpaces: []
        , inSpacesIdx: {}
	, default_font_size: 100
	, aux: {}   // auxiliary information used for a single draw
        //-
        , initInSpaces: function() {
            var wid       = this;
            let initInSpaces_processContent = function(content) {
                //-
                wid.inSpaces  = [];
                content.list.rows.forEach(function(row) {
                    var space={
                        spacemr_space_id: row[0]
                        , spacemr_space_code: row[1]
                        , spacemr_space_map_id_default: row[2]
                        , code: row[4]
                        , description: row[5]
                        , spacemr_space_type_name: row[6]
                        , number_of_seating: row[7]
                        , number_of_seating_booking: row[8]
                        , spacemr_space_people_names: ( row[9] === null ? "" : row[9])
                    };
                    wid.inSpaces.push(space);
                    wid.inSpacesIdx[space.spacemr_space_id] = space;
                    //-
                    //-
                    var space_info = wid.infoj.space_info[space.spacemr_space_id];
                    if (space_info != undefined) {
                        wid.spaces_addArea(space.spacemr_space_id, space_info);
                    }                
                })
                //-
                //-
                //-
                wid.drawMenuInSpaces();            
            }
            app_doRequestMappingRequest("spacemr_space/spacemr_space_list"
                                        , {"where":{"spacemr_space_in_id": wid.spacemr_space_id}
                                           ,"pageSize": "1000"
                                           ,   "columns": [
                                               "code",
                                               "description",
                                               "spacemr_space_type_name",
                                               "number_of_seating",
                                               "number_of_seating_booking",
                                               "spacemr_space_people_names"
                                           ]
                                           ,"order": [
                                               {
                                                   "column": "code"
                                               }
                                           ]
                                           ,"tableid":"table_spacemr_space_list"}
                                        , initInSpaces_processContent
                                       );
        }
        , drawMenuInSpaces_tabs: function(menuInSpaces) {
            var wid       = this;
            var menu_statuses = [
                {
                    name: "spaceList"
                    , label: gRb("db.spacemr_space..plural")
                    , action: function() {
                        wid.drawMenuInSpaces_spaceList(menuInSpaces);
                    }
                }
            ];
            return(menu_statuses);
        }
        , getMenuInSpaces: function(content) {
            var menuInSpaces = $("#menuInSpaces");
            return(menuInSpaces);
        }
        , drawMenuInSpaces: function(content) {            
            var wid       = this;
            if(wid._menuInSpaces_status == undefined) {
                wid._menuInSpaces_status = "spaceList";
            }
            var menuInSpaces = wid.getMenuInSpaces();
            var menu_statuses = wid.drawMenuInSpaces_tabs(menuInSpaces)
            var comma="";
            var head = $("<div>");
            menuInSpaces.html(head);
            menu_statuses.forEach(function(status){
                head.append(comma); comma = ", ";
                tab_name = $("<span>")
                    .text(status.label)
                    .click(function(){
                        wid._menuInSpaces_status = status.name;
                        wid.drawMenuInSpaces();
                    });
                head.append(tab_name);
                if (status.name == wid._menuInSpaces_status ) {
                    status.action();
                    tab_name.css("font-weight", "bold");
                }
            });
        }
        , drawMenuInSpaces_spaceList: function(menuInSpaces) {
            var wid       = this;
            //-
            wid.inSpaces.forEach(function(space){
                var line = $("<div>")
                    .attr("id", "pmenuInSpace_" + space.spacemr_space_id)
                    .click(function(){
                        wid.setStatus_none();
                        wid.spaces_selectSpaceArea_select(space.spacemr_space_id);
                    })
                ;
                if ($("#pspace_" + space.spacemr_space_id).length) {
                    line.append($('<abbr>')
                                .attr('title','seleziona sulla mappa')
                                .append(
                                    app_getIcon("circle", 15)
                                )
                               )
                    ;
                } else {
                    line.append($('<abbr>')
                                .attr('title', 'segna sulla mappa')
                                .append(
                                    app_getIcon("circle_empty", 15)
                                        .click(function(event){
                                            event.preventDefault();
                                            event.stopPropagation();
                                            wid.setStatus_drawNewSpaceArea(space.spacemr_space_id);
                                        })
                                     )
                               )
                    ;
                }
                line.append(
                        $('<abbr>')
                            .attr('title',
                                  "" + space.spacemr_space_type_name
                                  + ", " + space.description
                                  + ", " + space.spacemr_space_people_names
                                 ).append(space.code)
                    )
                ;
                menuInSpaces.append(line);
            });
        }
        , save_getJsonObject_getAPoints: function(svg_points){
            var points = [];
            svg_points.split(",").forEach(function(point){
                point.split(" ").forEach(function(coord){
                    points.push(parseFloat(coord));
                });
            });
            return(points);
        }
        , save_getJsonObject_getSpacearea: function(spacemr_space_id){
            var wid       = this;
            var spacearea = undefined;
            var o = $("#pspace_" + spacemr_space_id);
            if (o.length) {
                spacearea = {};
                var pointss = o.attr("points");
                var apoints = wid.save_getJsonObject_getAPoints(pointss);
                spacearea.apoints = apoints;
                //-
                var txt = $("#pspacetext_" + spacemr_space_id);
                if (txt.length) {
                    var startPoint = {x: apoints[0], y: apoints[1]};
                    var textinfo = {};
                    var regex = /translate\((.*),(.*)\) rotate\((.*)\).*/;
                    var transform = txt.attr("transform");
                    var found = transform.match(regex);
                    if (found != null) {
                        textinfo.dx = found[1] - startPoint.x;
                        textinfo.dy = found[2] - startPoint.y;
                        textinfo.rotate = found[3];
                        spacearea.textinfo = textinfo;
                        // console.log("  found " + app_JSONStringify(textinfo));
                    }
                }
            }
            return(spacearea);
        }
        , getEventCoordinates(event){
            var wid       = this;
            wid.svg_pt.x = event.clientX;
            wid.svg_pt.y = event.clientY;
            // The cursor point, translated into svg coordinates
            var cursorpt = wid.svg_pt.matrixTransform(wid.svg.getScreenCTM().inverse());
            return(cursorpt);
        }
        , setStatus_none: function() {
            //-
            //- by default the whole map is moved, drag and move.
            //-
            var wid       = this;
            var startPoint = null;
            var svg_frameBars = { };
            //-
            // wid.messageArea.text("");
            wid.eventListener_mousedown  = function(event) {
                event.preventDefault();
                event.stopPropagation();
                //- initializing references for moving the map / scroll bars.
                startPoint = { x: event.clientX , y: event.clientY };
                svg_frameBars = { x: wid.svg_frame.scrollLeft()
                                  , y: wid.svg_frame.scrollTop() };
                // console.log(" -- startPoint set to ("+startPoint.x+","+startPoint.y+")");
                // console.log(" -- svg_frameBars: " + app_JSONStringify(svg_frameBars));
            };
            wid.eventListener_mousemove  = function(event) {
                event.preventDefault();
                event.stopPropagation();
                if (startPoint != null) {
                    var endPoint = { x: event.clientX , y: event.clientY };
                    var dx = endPoint.x - startPoint.x;
                    var dy = endPoint.y - startPoint.y;
                    // console.log(" -- start: " + app_JSONStringify(startPoint)
                    //             + " end: " + app_JSONStringify(endPoint));
                    // console.log("moving x."+dx+" y."+dy);
                    wid.svg_frame.scrollLeft(svg_frameBars.x - dx);
                    wid.svg_frame.scrollTop(svg_frameBars.y - dy);
                }
            };
            wid.eventListener_mouseup    = function(event) {
                event.preventDefault();
                event.stopPropagation();
                startPoint = null;
            };
            wid.eventListener_mouseleave = function(event) {
                event.preventDefault();
                event.stopPropagation();
                startPoint = null;
            };
            if (wid.svg_image != undefined) {
                wid.svg_image.css("cursor","default");
                wid.spaces_selectSpaceArea_unselect();
                wid.eventListener_spacearea_click = function(event, spacemr_space_id) {
                    wid.spaces_selectSpaceArea_select(spacemr_space_id);
                };
            }
        }        
        , spaces_selectSpaceArea_select(spacemr_space_id) {
            var wid       = this;
            if (wid.selected_spacemr_space_id != undefined) {
                wid.spaces_selectSpaceArea_unselect();
            }
            if ($("#pspace_"+spacemr_space_id).length) {
                $("#pmenuInSpace_"+spacemr_space_id).css("background-color", "blue");
                $("#pspace_"+spacemr_space_id)
                    .attr('fill', 'blue')
                    .mousedown(function(event){
                        event.preventDefault();
                        event.stopPropagation();
			alert("" + $("#pspacetext_"+spacemr_space_id).text());
                    })
                ;
                $("#pspacetext_"+spacemr_space_id)
                    .attr('fill', 'white')
                    .mousedown(function(event){
			alert("" + $("#pspacetext_"+spacemr_space_id).text());
                    })
                ;
                //-
                //-
                wid.selected_spacemr_space_id = spacemr_space_id;
                //-
            }
        }
        , spaces_selectSpaceArea_unselect_extension() {
            var wid       = this;
        }
        , spaces_selectSpaceArea_unselect() {
            var wid       = this;
            if (wid.selected_spacemr_space_id != undefined) {
                $("#text_rotate_button").remove();
                $(".tmp_node").remove();
                $("#pmenuInSpace_"+wid.selected_spacemr_space_id).css("background-color", "");
                //-
                var spacearea = wid.save_getJsonObject_getSpacearea(wid.selected_spacemr_space_id);
                wid.spaces_deleteArea(wid.selected_spacemr_space_id);
                if (spacearea != undefined) {
                    wid.spaces_addArea(wid.selected_spacemr_space_id, spacearea);
                }
                //-
                //-
                wid.selected_spacemr_space_id = undefined;
                //-
                wid.spaces_selectSpaceArea_unselect_extension();
            }
        }
        , spaces_addArea(spacemr_space_id, spacearea) {
            var wid       = this;
            var apoints   = spacearea.apoints;
            //-
            var r = document.createElementNS(wid.svgNS,'polygon');
            r.setAttribute('id','pspace_' + spacemr_space_id);
            //-
            var startPoint = {x: apoints[0], y: apoints[1]};
            //-
            var p = "" + apoints[0] + " " + apoints[1];
            var i = 2;
            for (i = 2; i < apoints.length; i = i + 2) {
                p = p + "," + apoints[i] + " " + apoints[i+1];
            }
            r.setAttributeNS(null, 'points', p);
            r.setAttributeNS(null, 'fill', 'azure');
            r.setAttributeNS(null, 'fill-opacity', 0.8);
            r.setAttributeNS(null, 'stroke', 'black');
            r.setAttributeNS(null, 'stroke-width', 5);
            wid.svg.appendChild(r);
            r.onclick = function(event) {
                wid.eventListener_spacearea_click(event, spacemr_space_id);
            };
            //-
            var space = wid.inSpacesIdx[spacemr_space_id];
            // console.log(app_JSONStringify(space,1));
            wid.spaces_addArea_writeText(spacemr_space_id, spacearea, space);
            //-
        }
        , spaces_addArea_writeText_svg(svgText, space, spacearea) {
            var wid       = this;
            var txt  = space.code;
            var text = document.createTextNode(txt);
            svgText.appendChild(text);
        }
        , spaces_addArea_writeText(spacemr_space_id, spacearea, space) {
            var wid       = this;
            var textinfo = spacearea.textinfo;
            if (textinfo == undefined) {
                textinfo = {};
                textinfo.dx = 10;
                textinfo.dy = 100;
                textinfo.rotate = 0;
            }
            var startPoint = {x: spacearea.apoints[0], y: spacearea.apoints[1]};
            //-
            var t = document.createElementNS(wid.svgNS,'text');
            t.setAttribute('id','pspacetext_' + spacemr_space_id);
            var t_transform = ""
                + "translate("+(startPoint.x + textinfo.dx) +","+(startPoint.y + textinfo.dy)+")"
                + " rotate("+textinfo.rotate+")"
            ;
            t.setAttributeNS(null, 'transform', t_transform);
            t.setAttributeNS(null,"font-size", wid.default_font_size)
            wid.spaces_addArea_writeText_svg(t, space, spacearea);
            //-
            wid.svg.appendChild(t);
        }
        , spaces_deleteArea(spacemr_space_id) {
            $('#pspace_' + spacemr_space_id).remove();
            $('#pspacetext_' + spacemr_space_id).remove();
        }
        , eventListener_spacearea_click(event, spacemr_space_id) {
            event.preventDefault();
            event.stopPropagation();
            // console.log(" -- click on space " + spacemr_space_id);
        }
        , eventListener_mousedown(event) {
            event.preventDefault();
            event.stopPropagation();
            // console.log("no mousedown eventListener");
        }
        , eventListener_mouseup(event) {
            event.preventDefault();
            event.stopPropagation();
            // console.log("no mouseup eventListener");
        }
        , eventListener_mousemove(event) {
            event.preventDefault();
            event.stopPropagation();
            // console.log("no mousemove eventListener");
        }
        , eventListener_mouseleave(event) {
            event.preventDefault();
            event.stopPropagation();
            // console.log("no mouseleave eventListener");
        }
        , toggleLeftMenu: function(action){
            var wid       = this;
            var leftMenu  = wid.leftMenu;
            var svg_image = wid.svg_image;
            var svg_frame = wid.svg_frame;
            var imageArea = wid.imageArea;
            var fsetsizes = function(width) {
                height = width * wid.options.svg_height / wid.options.svg_width;
                //-
                svg_frame.css("width",  width);
                svg_frame.css("height", height);
                leftMenu.css("height", height);
                // console.log(" leftMenu.height: " + height);
                // console.log(" leftMenu.height(): " + leftMenu.height());
            }
            var fshow = function() {
                leftMenu.show();
                app_spacemr_space_people_book_map_leftMenu_hidden = false;
                width  = imageArea.width() - wid.leftMenuWidth - 4;
                fsetsizes(width)
            }
            var fhide=function() {
                leftMenu.hide();
                app_spacemr_space_people_book_map_leftMenu_hidden = true;
                width  = imageArea.width();
                fsetsizes(width)
            }
            if (action == "toggle") {
                if (leftMenu.is(":hidden")) {
                    fshow();
                } else {
                    fhide();
                }
            } else if (action == "windows_resize") {
                if (leftMenu.is(":hidden")) {
                    fhide();
                } else {
                    fshow();
                }
            }
        }
        , drawImageArea_buttons: function(){
            var wid       = this;
	}
        , drawImageArea: function(){
            var wid       = this;
            var files     = wid.files;
            app_tooltip_hide();
            wid.aux = {};
            wid.imageArea.html("");
            if (files.length > 0) {
                files.forEach(function(fileJson) {
                    // wid.imageArea.append("\n"+fileJson.app_file_id+"\n");
                    //-
                    //-
                    $( window ).resize(function() {
                        wid.toggleLeftMenu("windows_resize");
                    });
                    //-
                    var menuButton =
                        $("<span id='menuButton'>")
                        .append(app_getIcon("bars", 15))
                        // .append(gRb("db.system.menu.menu") + " ")
                        .append("M" + " ")
                        .click(function() {
                            wid.toggleLeftMenu("toggle");
                        })
                    ;
                    wid.imageArea.append(menuButton);
                    //-
		    wid.drawImageArea_buttons()
                    //-
                    wid.messageArea =
                        $("<span id='id_messageArea'>")
                        .text("")
                        .css("border-radius", "8px")
                        .css("background-color", "#FFF0F0")
                        .css("padding", "2px")
                        .css("border", "1px solid gray")
                    ;
                    wid.imageArea.append(wid.messageArea)
                    //-
                    var leftMenu =
                        $('<span id="leftMenu">')
                        // .hide()
                        .css("width", wid.leftMenuWidth)
                        .css("float", "left")
                        .css("padding", "3px")
                        .css("border", "1px solid gray")
                        .css("overflow", "auto")
                        .css("background-color", "#F0F0F0")
                        .css("border-radius", "8px")
                    ;
                    wid.leftMenu        = leftMenu;
                    if (app_spacemr_space_people_book_map_leftMenu_hidden) {
                        leftMenu.hide();
                    }
                    wid.imageArea.append(leftMenu);
                    {
                        var menuInSpaces = $("<div id='menuInSpaces'>");
                        leftMenu.append(menuInSpaces);
                        wid.initInSpaces();
                    }
                    var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
                    var svg_pt  = svg.createSVGPoint();
                    wid.svg    = svg;
                    wid.svg_pt = svg_pt;
                    svg.setAttribute('viewBox','0 0 '+(wid.options.svg_width)+' '+(wid.options.svg_height));
                    svg.setAttribute('preserveAspectRatio','xMinYMin');
                    var svgNS = svg.namespaceURI;
                    wid.svgNS = svgNS;
                    // {
                    //     var rect = document.createElementNS(svgNS,'rect');
                    //     rect.setAttribute('x',5);
                    //     rect.setAttribute('y',5);
                    //     rect.setAttribute('width',wid.options.svg_width - 5);
                    //     rect.setAttribute('height',wid.options.svg_height - 5);
                    //     rect.setAttribute('style','fill:none; stroke:black; stroke-width:2');
                    //     svg.appendChild(rect);
                    // }
                    {
                        var svgimg = document.createElementNS('http://www.w3.org/2000/svg','image');
                        var gv = function(name, defaul) {
                            var rv = defaul;
                            if ( name in wid.options) {
                                rv = wid.options[name];
                            }
                            return(rv);
                        }
                        // console.log("wid.options.svg_height + gv(svgimg_dheight,0): " + (wid.options.svg_height + gv("svgimg_dheight",0)));
                        svgimg.setAttribute('x',gv("svgimg_dx",0));
                        svgimg.setAttribute('y',gv("svgimg_dy",0));
                        svgimg.setAttribute('width',(parseInt(wid.options.svg_width) + parseInt(gv("svgimg_dwidth",0))));
                        svgimg.setAttribute('height',(parseInt(wid.options.svg_height) + parseInt(gv("svgimg_dheight",0))));
                        svgimg.setAttributeNS('http://www.w3.org/1999/xlink','href',"spacemr_space_map/app_file_get_content?app_file_id="+fileJson.app_file_id);
                        svg.appendChild(svgimg);
                    }
                    // {
                    //     var circle = document.createElementNS(svgNS,'circle');
                    //     circle.setAttribute('cx',70);
                    //     circle.setAttribute('cy',80);
                    //     circle.setAttribute('r',30);
                    //     circle.setAttribute('fill','yellow');
                    //     svg.appendChild(circle);
                    // }
                    //-
                    var svg_image = $(svg).attr("id","svg_image");
                    wid.svg_image = svg_image;
                    //-
                    svg.addEventListener('mousedown', function(event) {
                        wid.eventListener_mousedown(event);
                    });
                    svg.addEventListener('mouseup', function(event) {
                        wid.eventListener_mouseup(event)
                    });
                    svg.addEventListener('mousemove', function(event) {
                        wid.lastposition = wid.getEventCoordinates(event);
                        wid.eventListener_mousemove(event);
                    });
                    svg.addEventListener('mouseleave', function(event) {
                        wid.eventListener_mouseleave(event);
                    });
                    //-
                    // svg_image.click(function(event){
                    //     // {
                    //     //     var e = event.target;
                    //     //     var dim = e.getBoundingClientRect();
                    //     //     var x = event.clientX - dim.left;
                    //     //     var y = event.clientY - dim.top;
                    //     //     console.log(" ----- click event on svg  x: "+x+" y:"+y);
                    //     // }
                    //     //-
                    //     {
                    //         wid.svg_pt.x = event.clientX;
                    //         wid.svg_pt.y = event.clientY;
                    //         // The cursor point, translated into svg coordinates
                    //         var cursorpt =
                    //             wid.svg_pt.matrixTransform(wid.svg.getScreenCTM().inverse());
                    //         var m = "position (" + cursorpt.x + ", " + cursorpt.y + ")";
                    //         console.log(m);
                    //         wid.messageArea.text(m);
                    //     }
                    // })
                    svg.addEventListener('wheel', function(event) {
                        //-
                        //- svg zoom
                        //-
                        event.preventDefault();
                        event.stopPropagation();
                        //-
                        if (event.deltaY < 0) {
                            wid.svg_image.css("width", wid.svg_image.width() * 1.05);
                        }
                        if (event.deltaY > 0) {
                            wid.svg_image.css("width", wid.svg_image.width() * 0.95);
                        }
                        //- scrollTop, scrollLeft;
                        var point = wid.lastposition ; // wid.getEventCoordinates(event);
                        var o =
                            (wid.svg_image.width() * point.x) / wid.options.svg_width
                            - (wid.svg_frame.width() / 2)
                        ;
                        wid.svg_frame.scrollLeft(o);
                        // console.log("scrollLeft: " + o);
                        //-
                        o =
                            (wid.svg_image.height() * point.y) / wid.options.svg_height
                            - (wid.svg_frame.height() / 2)
                        ;
                        wid.svg_frame.scrollTop(o);
                        // console.log("scrollTop: " + o);
                    });
                    // $(document).ready(function(){
                    //     $('#svg_image').bind('mousewheel', function(e){
                    //         if(e.originalEvent.wheelDelta /120 > 0) {
                    //             console.log('scrolling up !');
                    //         }
                    //         else{
                    //             console.log('scrolling down !');
                    //         }
                    //     });
                    // });
                    //wid.svg_image.find("circle").click(function(){
                    //    alert("hello circle");
                    //});
                    var svg_frame =
                        $("<div>")
                        .append(wid.svg_image)
                        .css('width','100%')
                        .css("float", "right")
                        .css("overflow", "auto")
                    ;
                    wid.svg_frame = svg_frame;
                    wid.imageArea.append(svg_frame);
                    // wid.imageArea.append($("<a>")
                    //                 .attr("href", "spacemr_space_map/app_file_get_content?app_file_id="+fileJson.app_file_id)
                    //                 .append($("<img>").attr("src", "spacemr_space_map/app_file_get_content?app_file_id="+fileJson.app_file_id)
                    //                        )
                    //                );
                    setTimeout(function(){
                        wid.toggleLeftMenu("windows_resize");
                    }, 100);
                })
            } else {
                var area      = wid.imageArea;
                wid.uploadBackgroungImage(area);
            }
            wid.setStatus_none();
        }
        , uploadBackgroungImage: function(area){
            var wid       = this;
            area.append($("<div>").text(gRb("db.spacemr_space_map.update.background_image_not_defined")));
        }
        , setImageArea: function(imageArea){
            var wid       = this;
            wid.imageArea = imageArea;
        }
        , initialize: function(files, obj){
            var wid       = this;
	    var info      = obj.info;
            //-
            var infoj     = undefined;
            if (info != undefined && info != "") {
                infoj = JSON.parse(info);
                wid.options =  infoj.options;
            } else {
                infoj = {};
                infoj.space_info = {}
            }
            wid.spacemr_space_id = obj.spacemr_space_id;
            wid.spacemr_space_map = obj;
	    //-
            wid.infoj = infoj;
            wid.files = files;
	    //-
            //-
        }
    };
    return(map_manager);
}

function app_spacemr_space_map_view_map_people_username() {
    //-
    //- redirect
    //-
    //- http://localhost:8080/#?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people_username&spacemr_people_username=acorni
    //-
    // var link =
    //     "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
    //     + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_map_id"]]
    // ;
    // app_initialization_setHashAndGoToPage(link);
    var spacemr_space_id        = getLocationParameterByName('spacemr_space_id');
    var spacemr_people_username = getLocationParameterByName('spacemr_people_username');
    if (spacemr_people_username != "") {
        app_doRequestMappingRequest("spacemr_space_people/spacemr_space_people_list"
                                    , {
                                        "where": {
                                            "spacemr_people_username": spacemr_people_username
                                            , "fg_is_a_seat__true": true
                                            , "fg_is_a_seat__false":false
                                        }
                                        ,"pageSize": "1000"
                                        , "labelPrefix": "db.spacemr_space_people"
                                        , "columns": []
                                        , "tableid": "table_spacemr_space_people_list"
                                    }
                                    , function(content) {
                                        var rows = content.list.rows;
                                        let headers = content.list.headers;
                                        var i;
                                        var doSingle  = true;
                                        var urlToJump = null;
                                        var doJump    = true;
                                        //-
                                        let rowIndex={}
                                        for (let ih = 0; ih < headers.length; ih++) {
                                            rowIndex[headers[ih].name] = ih;
                                        }
                                        // console.log(" - rowIndex: " + app_JSONStringify(rowIndex,1));
                                        if (rows.length < 1) {
                                            var div = $('<div/>');
                                            //-
                                            div.append(gRb("db.spacemr_space_people.search.notfound.username"));
                                            div.append(" " + spacemr_people_username);
                                            //-
                                            appSetPage(div, gRb("db.spacemr_space_people.search.notfound.username"));
                                        } else {
                                            for (i = 0; i < rows.length && doSingle; i = i + 1) {
                                                var row = rows[i];
                                                var spacemr_space_id = row[rowIndex['spacemr_space_id']];
                                                var spacemr_people_id = row[rowIndex['spacemr_people_id']];
                                                var spacemr_space_in_map_id_default = row[rowIndex['spacemr_space_in_map_id_default']];
                                                if (spacemr_space_id != null
                                                    && spacemr_space_in_map_id_default != null) {
                                                    var map_ids = {
                                                        spacemr_space_map_id: spacemr_space_in_map_id_default
                                                        , spacemr_space_child_id: spacemr_space_id
                                                    };
                                                    if (urlToJump == null) {
                                                        // console.log(" -- -app_spacemr_space_map_view_map_people ");
                                                        urlToJump = function(){
                                                            app_spacemr_space_map_view_map_people(map_ids);
                                                        };
                                                        // + "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                        // + "&spacemr_space_map_id="+spacemr_space_in_map_id_default
                                                        // + "&spacemr_space_child_id="+spacemr_space_id
                                                        // ;
                                                        doJump = false;
                                                    } else {
                                                        console.log(" -- -app_spacemr_space_people_list ");
                                                        urlToJump = function() {
                                                            app_spacemr_space_people_list(map_ids);
                                                        };
                                                        // + "?page=app_spacemr_space_people__app_spacemr_space_people_list"
                                                        // + "&spacemr_people_username="+spacemr_people_username
                                                        // ;
                                                        doJump = false;
                                                        doSingle=false;
                                                    }
                                                }
                                            }
                                            if (urlToJump == null) {
                                                var link = ""
                                                    + "?page=app_spacemr_people__app_spacemr_people_form_update"
                                                    + "&spacemr_people_username="+spacemr_people_username
                                                ;
                                                app_initialization_setHashAndGoToPage(link);
                                            } else {
                                                if (doJump) {
                                                    app_initialization_setHashAndGoToPage(urlToJump);
                                                } else {
                                                    urlToJump();
                                                }
                                            }
                                        }
                                    });
    }
}


function app_spacemr_space_map_view_map_people_get_exension() {
    function rh(headers_index,row,name){
	var rv = headers_index[name];
	if (rv != undefined) {
	    rv = row[rv];
	} else {
	    rv = null;
	}
	return(rv);
    }
    function rjson(headers_index,row,name) {
	var rv = rh(headers_index, row,  name);
	if (rv === null) {
	    rv = [];
	} else {
	    rv = JSON.parse(rv);
	}
	return(rv);
    }
    //-
    let rv = {
        modes: {}
        , _menuInSpaces_status: app_spacemr_space_people_menuInSpaces_status
        , modes_init: function(){
            var wid       = this;
            //-
            var drawMode = function(list, name, drawThis, wid){
                var text = name;
                if (name == app_spacemr_space_people_view_mode) {
                    text = $("<b>")
                        .append(text);
                }
                text = $("<div>").append(text);
                text
                    .click(function(){
                        app_spacemr_space_people_view_mode = name;
                        drawThis(list);
                        wid.drawImageArea();
                    })
                let li = $('<li/>');
                li.append(text)
                list.append(li);
                return(li);
            };
	    //-
            let rv = {
                
                people: {
                    initInSpaces_query: wid.initInSpaces_query
                    , initInSpaces_spaceFromQuery: wid.initInSpaces_spaceFromQuery
                    , left_menu_draw_mode: drawMode
                    , spaces_addArea_writeText_svg: function(svgText, space, spacearea, addTspan, wid2) {
                        let peoples=space.spacemr_space_people_json;
                        peoples.sort(function(a,b){let rv = a.last_name.localeCompare(b.last_name);
                                                         if(rv == 0) rv = a.first_name.localeCompare(b.first_name)
                                                         return(rv)})
                        peoples.forEach(function(people){
                            var the_text =
                                people.first_name + " " + people.last_name
                            ;
                            tspan = addTspan(the_text);
                            if (app_userHasPermission("db_spacemr_space_people_book_insert")) {
                                tspan.onclick = function(event){
                                    event.preventDefault();
                                    event.stopPropagation();
                                    let wf = app_workflow_get("spacemr_space_people_book_workflow");
                                    //if (app_userHasPermission("db_spacemr_space_people_book_admin")
                                    //    || (app_userHasPermission("db_spacemr_space_people_book_insert")
                                    //        && wf.aux.bookers_roles.includes(app_spacemr_space_people_book_current_people.role))
                                    //   ) {
		                    var ls = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_insert&spacemr_people_id="+people.spacemr_people_id+"&spacemr_space_id="+space.spacemr_space_id+"";
		                    app_initialization_setHashAndGoToPage(ls);
                                    //} else {
		                    //    var ls = "?page=app_spacemr_space_people__app_spacemr_people_form_update&spacemr_people_id="+people.spacemr_people_id+"";
		                    //    app_initialization_setHashAndGoToPage(ls);
                                    //}
                                };
                            }
                        });
                    }
                }

                , area: {
                    initInSpaces_query: wid.initInSpaces_query
                    , initInSpaces_spaceFromQuery: wid.initInSpaces_spaceFromQuery
                    , left_menu_draw_mode: drawMode
                    , spaces_addArea_writeText_svg: function(svgText, space, spacearea, addTspan, wid2) {
                        // let s = "";
                        // if (space.number_of_seating_booking != null) {
                        // }
                        tspan = addTspan(space.description);
                        let s = " / " + space.number_of_seating_booking
                        tspan = addTspan(gRb("db.spacemr_space.number_of_seating")
                                         +": "+ space.spacemr_space_people_count
                                         + " / " + space.number_of_seating
                                         + s
                                        );
                        tspan = addTspan(gRb("db.spacemr_space.area_in_meters2")
                                         +": "+ space.area_in_meters2);
                    }
                }
                
                , resp: {
                    initInSpaces_query: wid.initInSpaces_query
                    , initInSpaces_spaceFromQuery: wid.initInSpaces_spaceFromQuery
                    , left_menu_draw_mode: drawMode
                    , spaces_addArea_writeText_svg: function(svgText, space, spacearea, addTspan, wid2) {
                        //-
                        peoples=space.spacemr_space_people_non_sitting_json;
                        peoples.sort(function(a,b){let rv = a.last_name.localeCompare(b.last_name);
                                                         if(rv == 0) rv = a.first_name.localeCompare(b.first_name)
                                                         return(rv)})
                        peoples.forEach(function(people){
                            tspan = addTspan(people.spacemr_space_people_type_name + ": ");
                            var the_text =
                                people.first_name + " " + people.last_name
                            ;
                            tspan = addTspan(the_text);
                            if (app_userHasPermission("db_spacemr_space_people_read")) {
                                tspan.onclick = function(event){
                                    event.preventDefault();
                                    event.stopPropagation();
		                    var ls = "?page=app_spacemr_space_people__app_spacemr_space_people_list&spacemr_people_id="+people.spacemr_people_id+"";
		                    app_initialization_setHashAndGoToPage(ls);
                                };
                            }
                        });
                    }
                }
                
                , bookings: {
                    // initInSpaces_query: wid.initInSpaces_query
                    initInSpaces_query: function(wid2){
                        let rv = undefined;
                        var current_date = app_spacemr_space_people_book_map_current_date;
                        var current_time = current_date.getTime();
                        {
                            var wf = app_workflow_get("spacemr_space_people_book_workflow");
                            if (wf == undefined) {
                                // console.log("-- workflow spacemr_space_people_book_workflow undefined")
                                app_spacemr_space_people_book_doInitialize( function() {
                                    wid2.initInSpaces();
                                });
                                return;
                            }
                        }
                        if (wid2.aux.bookings_verifier == undefined || wid2.aux.bookings_verifier_reinit) {
                            wid2.aux.bookings_verifier = app_spacemr_space_people_book_getVerifier();
                            app_doRequestMappingRequest("spacemr_space_people_book/spacemr_space_people_book_list_sitting"
                                                        , { "spacemr_space_id": wid2.spacemr_space_id
                                                            , "current_date":   current_time
                                                          }
                                                        , function(content) {
                                                            let wf = app_workflow_get("spacemr_space_people_book_workflow");
                                                            wid2.aux.bookings_verifier.init(content, wf);
                                                            wid2.aux.bookings_verifier.compute_occupation(current_date);
                                                            wid2.initInSpaces();
                                                            wid2.aux.bookings_verifier_reinit = false;
                                                        });
                        } else {
                            wid2.aux.bookings_verifier_reinit = true;
                            rv = {
                                "where":{"spacemr_space_in_id": wid2.spacemr_space_id
                                         , "current_date": current_time
                                        }
                                ,"pageSize": "1000"
                                ,   "columns": [
                                    "code",
				    "description",
				    "spacemr_space_type_name",
                                    "number_of_seating",
                                    "number_of_seating_booking",
				    "spacemr_space_people_json",
				    "spacemr_space_people_non_sitting_json",
                                    "area_in_meters2",
                                    "spacemr_space_people_count",
                                    "bookings"
                                ]
                                ,"order": [
                                    {
                                        "column": "code"
                                    }
                                ]
                                ,"tableid":"table_spacemr_space_list"
                            }
                        }
                        return(rv);
                    }
                    , initInSpaces_spaceFromQuery: function(wid2, row, headers_index) {
			let h=headers_index;
			var space={
			    spacemr_space_id: rh(h,row,"spacemr_space_id")
			    , spacemr_space_code: rh(h,row,"spacemr_space_code")
			    , spacemr_space_map_id_default: rh(h,row,"spacemr_space_map_id_default")
			    , code: rh(h,row,"code")
			    , description: rh(h,row,"description")
			    , spacemr_space_type_name: rh(h,row,"spacemr_space_type_name")
			    , number_of_seating: rh(h,row,"number_of_seating")
			    , number_of_seating_booking: rh(h,row,"number_of_seating_booking")
			    , spacemr_space_people_json: rjson(h,row,"spacemr_space_people_json")
			    , spacemr_space_people_non_sitting_json: rjson(h,row,"spacemr_space_people_non_sitting_json")
			    , area_in_meters2: rh(h,row,"area_in_meters2")
			    , spacemr_space_people_count: rh(h,row,"spacemr_space_people_count")
                            , bookings: rjson(h,row,"bookings")
			};
                        return(space);
                    }
                    , spaces_addArea_writeText_svg: function(svgText, space, spacearea, addTspan, wid2) {
                        // tspan = addTspan(" area(m2): " + space.area_in_meters2);
                        let bookings = space.bookings;
                        if (bookings != null) {
                            let wf = app_workflow_get("spacemr_space_people_book_workflow");
                            if(bookings.length > 0) {
                                var addColoredText = function(line, text, color) {
                                    var textinfo = spacearea.textinfo;
                                    if (textinfo == undefined) {
                                        textinfo = {};
                                        textinfo.dx = 10;
                                        textinfo.dy = 100;
                                        textinfo.rotate = 0;
                                    }
                                    var startPoint = {x: spacearea.apoints[0], y: spacearea.apoints[1]};
                                    var t = document.createElementNS(wid2.svgNS,'text');
                                    var t_transform = ""
                                        + "translate("+(startPoint.x + textinfo.dx) +","+(startPoint.y + textinfo.dy)+")"
                                        + " rotate("+textinfo.rotate+")"
                                    ;
                                    t.setAttributeNS(null, 'transform', t_transform);
                                    t.setAttributeNS(null,"fill", color);
                                    t.setAttributeNS(null,"font-size", wid2.default_font_size);
                                    //-
                                    let dy = 0;
                                    let addTspan = function(the_text) {
                                        //-
                                        var tspan = document.createElementNS(wid2.svgNS,'tspan');
                                        tspan.setAttributeNS(null, "x",  0);
                                        tspan.setAttributeNS(null, "dy", dy);
                                        dy = wid2.default_font_size;
                                        tspan.appendChild(document.createTextNode(the_text));
                                        t.appendChild(tspan);
                                        return(tspan);
                                    }
                                    let i=0;
                                    while (i<=line) {
                                        addTspan(".\n");
                                        i++;
                                    }
                                    let rv = addTspan(text);
                                    wid2.svg.appendChild(t);
                                    return(rv);
                                }
                                // console.log(" - bookings: " + app_JSONStringify(bookings,1));
                                //-qui-
                                let bindex = {};
                                bookings.forEach(function(st){
                                    // console.log(app_JSONStringify(st,1));
                                    let k = Object.keys(st)[0];
                                    bindex[k] = st[k];
                                });
                                // console.log(" - bindex: " + app_JSONStringify(bindex,1));
                                let line = 0;
                                wf.statusList.forEach(function(stato){
                                    if (wf.defaultSearchStatuses == undefined
                                        || wf.defaultSearchStatuses.includes(stato)) {
                                        var st = bindex[stato];
                                        if (st  != undefined) {
                                            var circle = '\u2B24';
                                            tspan  = addTspan(st);
                                            tspanc = addColoredText(line, st+" - "+stato+circle, wf.statusIndex[stato].color);
                                            wid2.aux.bookings_verifier.tooltip_bookings($(tspanc), space.spacemr_space_id, stato
                                                                                        , function(){
                                                                                            wid2.drawImageArea();
                                                                                        }, app_spacemr_space_people_book_map_current_date
											, null);
                                            line = line + 1;
                                        }
                                    }
                                });
                                if(wid2.aux.bookings_verifier.spaces[space.spacemr_space_id].exceeds) {
                                    let s = gRb("db.spacemr_space_people_book.sys.exceeds");
                                    tspanc = addColoredText(line, "\u2588\u2588\u2588\u2588\u2588\u2588\u2588", "red");
                                    tspanc = addColoredText(line, s, "black");
                                    let exceeds_spaces = wid2.aux.bookings_verifier.spaces[space.spacemr_space_id].exceeds_spaces;
                                    let df = function(evt, tooltip_div){
                                        tooltip_div.append($("<textarea>")
				                         .css("width", "200pt")
				                         .css("height", "200pt")
				                         .val(JSON.stringify(exceeds_spaces,null,2))
			       	                        );
                                    }
                                    app_tooltip_set_click($(tspanc), df, null);
                                    line = line + 1;
                                }
                            }
                        }
                    }
                    , left_menu_draw_mode: function(list, name, drawThis, wid){
                        let li = drawMode(list, name, drawThis, wid);
                        if (name == app_spacemr_space_people_view_mode) {
                            let current_date = app_spacemr_space_people_book_map_current_date;
                            //-
                            let date_grid = $("<div>");
                            date_grid
                                .append($("<span>")
                                        .append(app_getIcon("arrow_left", 15))
                                        .attr('title',gRb("db.spacemr_space_people_book.update.title"))
                                        .click(function(){
                                            let oneDayInMs = 24 * 60 * 60 * 1000;
                                            app_spacemr_space_people_book_map_current_date = 
                                                new Date(app_spacemr_space_people_book_map_current_date.getTime()-oneDayInMs);
                                            wid.drawImageArea();
                                        }) 
                                       )
                                .append($("<span>")
                                        .append(app_getIcon("arrow_right", 15))
                                        .attr('title',gRb("db.spacemr_space_people_book.update.title"))
                                        .click(function(){
                                            let oneDayInMs = 24 * 60 * 60 * 1000;
                                            app_spacemr_space_people_book_map_current_date = 
                                                new Date(app_spacemr_space_people_book_map_current_date.getTime()+oneDayInMs);
                                            wid.drawImageArea();
                                        }) 
                                       )
                                .append(" ")
                                .append(""
                                        + current_date.getDate()
                                        + " " + gRb("db.sys.calendar.month."+current_date.getMonth())
                                        + " " + current_date.getFullYear())
                            ;
                            {
                                let value = appConvertDateToString(current_date.getTime());
                                let name  = "spacemr_space_people_book__current_date";
                                let id    = "id_"+name;
                                let form = $("<form>")
                                    .css('display', "inline-block")
                                    .submit(function(){ return(false);} );
                                let input = $('<input class="w3-input w3-border w3-round" size="9" type="text"/>')
                                    .attr('name', name).attr('id', id).val(value)
                                    .change(function(){
                                        let d   = new Date(appConvertStringToTimestamp(input.val()));
                                        let ccd = new Date(current_date.getFullYear(),current_date.getMonth(), current_date.getDate(),0,0,0 );
                                        if (d.getTime() != ccd.getTime()) {
                                            app_spacemr_space_people_book_map_current_date = 
                                                new Date(d.getFullYear(),d.getMonth(), d.getDate(),0,0,0 );
                                            wid.drawImageArea();
                                        }
                                        //-
                                    })
                                ;
                                input.pikaday({format: sys_dateFormatUi
                                               , firstDay: 1
                                               , i18n: { months: moment.localeData()._months
                                                         , weekdays: moment.localeData()._weekdays
                                                         , weekdaysShort: moment.localeData()._weekdaysShort }
                                              });
                                let rv = input;
                                app_ui_standard_appendRow(form)
                                    .append(rv)
                                ;
                                date_grid
                                    .append(" ")
                                    .append(
                                        app_ui_standard_button()
                                            .text(gRb("db.sys.refresh"))
                                            .click(function(event) {
                                                wid.initInSpaces();
                                            }
                                                  )
                                    );
                                date_grid
                                    .append(" ")
                                    .append(form);
                            }
                            li.append(date_grid);
                        }
                    }
                }

                , presences: {
                    // initInSpaces_query: wid.initInSpaces_query
                    initInSpaces_query: function(wid2){
                        let rv = undefined;
                        var current_date = app_spacemr_space_people_book_map_current_date;
                        var current_time = current_date.getTime();
                        if (wid2.aux.presences_verifier == undefined || wid2.aux.presences_verifier_reinit) {
                            wid2.aux.presences_verifier = app_spacemr_space_user_presences_getVerifier();
                            app_doRequestMappingRequest("spacemr_space_user_presence/spacemr_space_user_presence_list_sitting"
                                                        , { "spacemr_space_id": wid2.spacemr_space_id
                                                            , "current_date":   current_time
                                                          }
                                                        , function(content) {
                                                            wid2.aux.presences_verifier.init(content);
                                                            wid2.aux.presences_verifier.compute_occupation(current_date);
                                                            wid2.initInSpaces();
                                                            wid2.aux.presences_verifier_reinit = false;
                                                        });
                        } else {
                            wid2.aux.presences_verifier_reinit = true;
                            rv = {
                                "where":{"spacemr_space_in_id": wid2.spacemr_space_id
                                         , "current_date": current_time
                                        }
                                ,"pageSize": "1000"
                                ,   "columns": [
                                    "code",
				    "description",
				    "spacemr_space_type_name",
                                    "number_of_seating",
                                    "number_of_seating_booking",
				    "spacemr_space_people_json",
				    "spacemr_space_people_non_sitting_json",
                                    "area_in_meters2",
                                    "spacemr_space_people_count",
                                    "presences"
                                ]
                                ,"order": [
                                    {
                                        "column": "code"
                                    }
                                ]
                                ,"tableid":"table_spacemr_space_list"
                            }
                        }
                        return(rv);
                    }
                    , initInSpaces_spaceFromQuery: function(wid2, row, headers_index) {
			let h=headers_index;
			var space={
			    spacemr_space_id: rh(h,row,"spacemr_space_id")
			    , spacemr_space_code: rh(h,row,"spacemr_space_code")
			    , spacemr_space_map_id_default: rh(h,row,"spacemr_space_map_id_default")
			    , code: rh(h,row,"code")
			    , description: rh(h,row,"description")
			    , spacemr_space_type_name: rh(h,row,"spacemr_space_type_name")
			    , number_of_seating: rh(h,row,"number_of_seating")
			    , number_of_seating_booking: rh(h,row,"number_of_seating_booking")
			    , spacemr_space_people_json: rjson(h,row,"spacemr_space_people_json")
			    , spacemr_space_people_non_sitting_json: rjson(h,row,"spacemr_space_people_non_sitting_json")
			    , area_in_meters2: rh(h,row,"area_in_meters2")
			    , spacemr_space_people_count: rh(h,row,"spacemr_space_people_count")
                            , presences: rjson(h,row,"presences")
			};
                        return(space);
		        }
                    , spaces_addArea_writeText_svg: function(svgText, space, spacearea, addTspan, wid2) {
                        // tspan = addTspan(" area(m2): " + space.area_in_meters2);
                        let presences = space.presences;
                        if (presences != null) {
                            if(presences.length > 0) {
                                var addColoredText = function(line, text, color) {
                                    var textinfo = spacearea.textinfo;
                                    if (textinfo == undefined) {
                                        textinfo = {};
                                        textinfo.dx = 10;
                                        textinfo.dy = 100;
                                        textinfo.rotate = 0;
                                    }
                                    var startPoint = {x: spacearea.apoints[0], y: spacearea.apoints[1]};
                                    var t = document.createElementNS(wid2.svgNS,'text');
                                    var t_transform = ""
                                        + "translate("+(startPoint.x + textinfo.dx) +","+(startPoint.y + textinfo.dy)+")"
                                        + " rotate("+textinfo.rotate+")"
                                    ;
                                    t.setAttributeNS(null, 'transform', t_transform);
                                    t.setAttributeNS(null,"fill", color);
                                    t.setAttributeNS(null,"font-size", wid2.default_font_size);
                                    //-
                                    let dy = 0;
                                    let addTspan = function(the_text) {
                                        //-
                                        var tspan = document.createElementNS(wid2.svgNS,'tspan');
                                        tspan.setAttributeNS(null, "x",  0);
                                        tspan.setAttributeNS(null, "dy", dy);
                                        dy = wid2.default_font_size;
                                        tspan.appendChild(document.createTextNode(the_text));
                                        t.appendChild(tspan);
                                        return(tspan);
                                    }
                                    let i=0;
                                    while (i<=line) {
                                        addTspan(".\n");
                                        i++;
                                    }
                                    let rv = addTspan(text);
                                    wid2.svg.appendChild(t);
                                    return(rv);
                                }
                                // console.log(" - presences: " + app_JSONStringify(presences,1));
                                let bindex = {};
                                presences.forEach(function(st){
                                    // console.log(app_JSONStringify(st,1));
                                    let k = Object.keys(st)[0];
                                    bindex[k] = st[k];
                                });
                                // console.log(" - bindex: " + app_JSONStringify(bindex,1));
                                let line = 0;
                                ["present"].forEach(function(stato){
                                    var st = bindex[stato];
                                    if (st  != undefined) {
                                        var circle = '\u2B24';
                                        tspan  = addTspan(st);
                                        tspanc = addColoredText(line, st+" - "+stato+circle, "green");
                                        wid2.aux.presences_verifier.tooltip_presences($(tspanc), space.spacemr_space_id, stato
                                                                                      , function(){
                                                                                          wid2.drawImageArea();
                                                                                      }, app_spacemr_space_people_book_map_current_date, null);
                                        line = line + 1;
                                    }
                                });
                                if(wid2.aux.presences_verifier.spaces[space.spacemr_space_id].exceeds) {
                                    let s = gRb("db.spacemr_space_people_book.sys.exceeds");
                                    tspanc = addColoredText(line, "\u2588\u2588\u2588\u2588\u2588\u2588\u2588", "red");
                                    tspanc = addColoredText(line, s, "black");
                                    line = line + 1;
                                }
                            }
                        }
                    }
                    , left_menu_draw_mode: function(list, name, drawThis, wid){
                        let li = drawMode(list, name, drawThis, wid);
                        if (name == app_spacemr_space_people_view_mode) {
                            let current_date = app_spacemr_space_people_book_map_current_date;
                            //-
                            let date_grid = $("<div>");
                            date_grid
                                .append($("<span>")
                                        .append(app_getIcon("arrow_left", 15))
                                        .attr('title',gRb("db.spacemr_space_people_book.update.title"))
                                        .click(function(){
                                            let oneDayInMs = 24 * 60 * 60 * 1000;
                                            app_spacemr_space_people_book_map_current_date = 
                                                new Date(app_spacemr_space_people_book_map_current_date.getTime()-oneDayInMs);
                                            wid.drawImageArea();
                                        }) 
                                       )
                                .append($("<span>")
                                        .append(app_getIcon("arrow_right", 15))
                                        .attr('title',gRb("db.spacemr_space_people_book.update.title"))
                                        .click(function(){
                                            let oneDayInMs = 24 * 60 * 60 * 1000;
                                            app_spacemr_space_people_book_map_current_date = 
                                                new Date(app_spacemr_space_people_book_map_current_date.getTime()+oneDayInMs);
                                            wid.drawImageArea();
                                        }) 
                                       )
                                .append(" ")
                                .append(""
                                        + current_date.getDate()
                                        + " " + gRb("db.sys.calendar.month."+current_date.getMonth())
                                        + " " + current_date.getFullYear())
                            ;
                            {
                                let value = appConvertDateToString(current_date.getTime());
                                let name  = "spacemr_space_people_book__current_date";
                                let id    = "id_"+name;
                                let form = $("<form>")
                                    .css('display', "inline-block")
                                    .submit(function(){ return(false);} );
                                let input = $('<input class="w3-input w3-border w3-round" size="9" type="text"/>')
                                    .attr('name', name).attr('id', id).val(value)
                                    .change(function(){
                                        let d   = new Date(appConvertStringToTimestamp(input.val()));
                                        let ccd = new Date(current_date.getFullYear(),current_date.getMonth(), current_date.getDate(),0,0,0 );
                                        if (d.getTime() != ccd.getTime()) {
                                            app_spacemr_space_people_book_map_current_date = 
                                                new Date(d.getFullYear(),d.getMonth(), d.getDate(),0,0,0 );
                                            wid.drawImageArea();
                                        }
                                        //-
                                    })
                                ;
                                input.pikaday({format: sys_dateFormatUi
                                               , firstDay: 1
                                               , i18n: { months: moment.localeData()._months
                                                         , weekdays: moment.localeData()._weekdays
                                                         , weekdaysShort: moment.localeData()._weekdaysShort }
                                              });
                                let rv = input;
                                app_ui_standard_appendRow(form)
                                    .append(rv)
                                ;
                                date_grid
                                    .append(" ")
                                    .append(
                                        app_ui_standard_button()
                                            .text(gRb("db.sys.refresh"))
                                            .click(function(event) {
                                                wid.initInSpaces();
                                            }
                                                  )
                                    );
                                date_grid
                                    .append(" ")
                                    .append(form);
                            }
                            li.append(date_grid);
                            li.append(
                                app_tooltip_set_click(
                                    app_ui_standard_button()
                                      .text(gRb("db.spacemr_space_map.map-data-to-csv.short"))
                                        .attr("title",gRb("db.spacemr_space_map.map-data-to-csv"))
                                    , function(evt, tooltip_div){
                                        let form = $("<form>");
                                        let out_ta   =
                                            $("<textarea class='w3-input w3-border w3-round w3-theme-light'>")
                                            .val("");
                                        form.append(out_ta);
                                        let out = "presences\tcode\n";
                                        wid.inSpaces.forEach(function(space){
                                            if (space.presences) {
                                                if (space.presences[0].present) {
                                                    out = out
                                                        + space.presences[0].present
                                                        +"\t"+space.code
                                                        +"\n"
                                                    ;
                                                }
                                            }
                                        });
                                        out_ta.val(out);
					tooltip_div
                                            .append(form)
                                        // .append(app_JSONStringify(wid))
					// .append("this has the same interface of app_tooltip_set")
                                        ;
                                        // wid.drawImageArea();
				    }
                                )
                            );
                        }
                    }
                }

                , inventarios: {
                    // initInSpaces_query: wid.initInSpaces_query
                    initInSpaces_query: function(wid2){
                        let rv = undefined;
                        var current_date = app_spacemr_space_people_book_map_current_date;
                        var current_time = current_date.getTime();
                        {
                            var wf = app_workflow_get("spacemr_inventario_workflow");
                            if (wf == undefined) {
                                // console.log("-- workflow spacemr_space_people_book_workflow undefined")
                                app_spacemr_inventario_doInitialize( function() {
                                    wid2.initInSpaces();
                                });
                                return;
                            }
                        }
                        if (wid2.aux.inventarios_verifier == undefined || wid2.aux.inventarios_verifier_reinit) {
                            wid2.aux.inventarios_verifier = app_spacemr_inventario_getVerifier();
                            let wf = app_workflow_get("spacemr_inventario_workflow");
                            wid2.aux.inventarios_verifier.init(wf);
                            wid2.initInSpaces();
                            wid2.aux.inventarios_verifier_reinit = false;
                        } else {
                            wid2.aux.inventarios_verifier_reinit = true;
                            rv = {
                                "where":{"spacemr_space_in_id": wid2.spacemr_space_id
                                        }
                                ,"pageSize": "1000"
                                ,   "columns": [
                                    "code",
                                    "description",
                                    "spacemr_space_type_name",
                                    "number_of_seating",
                                    "number_of_seating_booking",
                                    "spacemr_space_people_json",
                                    "spacemr_space_people_non_sitting_json",
                                    "area_in_meters2",
                                    "spacemr_space_people_count",
                                    "inventarios"
                                ]
                                ,"order": [
                                    {
                                        "column": "code"
                                    }
                                ]
                                ,"tableid":"table_spacemr_space_list"
                            }
                        }
                        return(rv);
                    }
                    , initInSpaces_spaceFromQuery: function(wid2, row, headers_index) {
                        let h=headers_index;
                        var space={
                            spacemr_space_id: rh(h,row,"spacemr_space_id")
                            , spacemr_space_code: rh(h,row,"spacemr_space_code")
                            , spacemr_space_map_id_default: rh(h,row,"spacemr_space_map_id_default")
                            , code: rh(h,row,"code")
                            , description: rh(h,row,"description")
                            , spacemr_space_type_name: rh(h,row,"spacemr_space_type_name")
                            , number_of_seating: rh(h,row,"number_of_seating")
                            , number_of_seating_booking: rh(h,row,"number_of_seating_booking")
                            , spacemr_space_people_json: rjson(h,row,"spacemr_space_people_json")
                            , spacemr_space_people_non_sitting_json: rjson(h,row,"spacemr_space_people_non_sitting_json")
                            , area_in_meters2: rh(h,row,"area_in_meters2")
                            , spacemr_space_people_count: rh(h,row,"spacemr_space_people_count")
                            , inventarios: rjson(h,row,"inventarios")
                        };
                        return(space);
                    }
                    , spaces_addArea_writeText_svg: function(svgText, space, spacearea, addTspan, wid2) {
                        // tspan = addTspan(" area(m2): " + space.area_in_meters2);
                        let inventarios = space.inventarios;
                        if (inventarios != null) {
                            let wf = app_workflow_get("spacemr_inventario_workflow");
                            if(inventarios.length > 0) {
                                var addColoredText = function(line, text, color) {
                                    var textinfo = spacearea.textinfo;
                                    if (textinfo == undefined) {
                                        textinfo = {};
                                        textinfo.dx = 10;
                                        textinfo.dy = 100;
                                        textinfo.rotate = 0;
                                    }
                                    var startPoint = {x: spacearea.apoints[0], y: spacearea.apoints[1]};
                                    var t = document.createElementNS(wid2.svgNS,'text');
                                    var t_transform = ""
                                        + "translate("+(startPoint.x + textinfo.dx) +","+(startPoint.y + textinfo.dy)+")"
                                        + " rotate("+textinfo.rotate+")"
                                    ;
                                    t.setAttributeNS(null, 'transform', t_transform);
                                    t.setAttributeNS(null,"fill", color);
                                    t.setAttributeNS(null,"font-size", wid2.default_font_size);
                                    //-
                                    let dy = 0;
                                    let addTspan = function(the_text) {
                                        //-
                                        var tspan = document.createElementNS(wid2.svgNS,'tspan');
                                        tspan.setAttributeNS(null, "x",  0);
                                        tspan.setAttributeNS(null, "dy", dy);
                                        dy = wid2.default_font_size;
                                        tspan.appendChild(document.createTextNode(the_text));
                                        t.appendChild(tspan);
                                        return(tspan);
                                    }
                                    let i=0;
                                    while (i<=line) {
                                        addTspan(".\n");
                                        i++;
                                    }
                                    let rv = addTspan(text);
                                    wid2.svg.appendChild(t);
                                    return(rv);
                                }
                                // console.log(" - inventarios: " + app_JSONStringify(inventarios,1));
                                //-qui-
                                let bindex = {};
                                inventarios.forEach(function(st){
                                    // console.log(app_JSONStringify(st,1));
                                    let k = Object.keys(st)[0];
                                    bindex[k] = st[k];
                                });
                                // console.log(" - bindex: " + app_JSONStringify(bindex,1));
                                let line = 0;
                                wf.statusList.forEach(function(stato){
                                    if (wf.defaultSearchStatuses == undefined
                                        || wf.defaultSearchStatuses.includes(stato)) {
                                        var st = bindex[stato];
                                        if (st  != undefined) {
                                            var circle = '\u2B24';
                                            tspan  = addTspan(st);
                                            tspanc = addColoredText(line, st+" - "+stato+circle, wf.statusIndex[stato].color);
                                            wid2.aux.inventarios_verifier.tooltip_inventarios($(tspanc), space.spacemr_space_id, stato
                                                                                        , function(){
                                                                                            wid2.drawImageArea();
                                                                                        }, null
                                                                                        , null);
                                            line = line + 1;
                                        }
                                    }
                                });
                            }
                        }
                    }
                    , left_menu_draw_mode: function(list, name, drawThis, wid){
                        let li = drawMode(list, name, drawThis, wid);
                    }
                }

            }
            wid.modes = rv;
        }
        , initInSpaces_query: function(wid){
            var rv = {
                "where":{"spacemr_space_in_id": wid.spacemr_space_id}
                ,"pageSize": "1000"
                ,   "columns": [
                    "code",
                    "description",
                    "spacemr_space_type_name",
                    "number_of_seating",
                    "number_of_seating_booking",
                    "spacemr_space_people_json",
                    "spacemr_space_people_non_sitting_json",
                    "area_in_meters2",
                    "spacemr_space_people_count"
                ]
                ,"order": [
                    {
                        "column": "code"
                    }
                ]
                ,"tableid":"table_spacemr_space_list"
            }
            return(rv);
        }
        , initInSpaces_spaceFromQuery: function(wid, row, headers_index) {
	    let h=headers_index;
            var space={
                spacemr_space_id: rh(h,row,"spacemr_space_id")
                , spacemr_space_code: rh(h,row,"spacemr_space_code")
                , spacemr_space_map_id_default: rh(h,row,"spacemr_space_map_id_default")
                , code: rh(h,row,"code")
                , description: rh(h,row,"description")
                , spacemr_space_type_name: rh(h,row,"spacemr_space_type_name")
                , number_of_seating: rh(h,row,"number_of_seating")
                , number_of_seating_booking: rh(h,row,"number_of_seating_booking")
                , spacemr_space_people_json: rjson(h,row,"spacemr_space_people_json")
                , spacemr_space_people_non_sitting_json: rjson(h,row,"spacemr_space_people_non_sitting_json")
                , area_in_meters2: rh(h,row,"area_in_meters2")
                , spacemr_space_people_count: rh(h,row,"spacemr_space_people_count")
            };
            return(space);
            }
        , initInSpaces: function() {
            var wid       = this;
            //-
            let initInSpaces_processContent = function(content) {
                //-
                var headers_index = {};
                {
                    var headers = content.list.headers;
                    for (var i=0;i<headers.length; i++) {
                        headers_index[headers[i].name] = i;
                    }
                }
                wid.inSpaces  = [];
                content.list.rows.forEach(function(row) {
                    var space =
                        wid.modes[app_spacemr_space_people_view_mode]
                        .initInSpaces_spaceFromQuery(wid, row, headers_index);
                    wid.inSpaces.push(space);
                    wid.inSpacesIdx[space.spacemr_space_id] = space;
                    //-
                    //-
                    var space_info = wid.infoj.space_info[space.spacemr_space_id];
                    if (space_info != undefined) {
                        wid.spaces_addArea(space.spacemr_space_id, space_info);
                    }                
                })
                //-
                wid.drawMenuInSpaces();
                if (wid.spacemr_space_child_id!="") {
                    setTimeout(function() {
                        //- hook for post-rendering actions
                        wid.spaces_selectSpaceArea_select(wid.spacemr_space_child_id);
                    }, 100);
                }
            }
            // ;
            let initInSpaces_query = wid.modes[app_spacemr_space_people_view_mode].initInSpaces_query(wid);
            if (initInSpaces_query != null) {
                app_doRequestMappingRequest("spacemr_space/spacemr_space_list"
                                            , initInSpaces_query
                                            , initInSpaces_processContent
                                           );
            }
        }
        , spaces_selectSpaceArea_select(spacemr_space_id) {
            var wid       = this;
            if (wid.selected_spacemr_space_id != undefined) {
                wid.spaces_selectSpaceArea_unselect();
            }
            var mousedownfunction = function(event) {
                event.preventDefault();
                event.stopPropagation();
                //-
                var space = wid.inSpacesIdx[spacemr_space_id];
                let ls = null;
                if (space.spacemr_space_map_id_default) {
		    ls = "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people&spacemr_space_map_id="+space.spacemr_space_map_id_default+"";
                //} else if (app_userHasPermission("db_spacemr_space_people_read")) {
		//    ls = "?page=app_spacemr_space__app_spacemr_space_people_list&spacemr_space_id="+space.spacemr_space_id+"";
                } else {
                    wid.space_menu_tooltip(event, space);
                }
                if (ls != null) {
		    app_initialization_setHashAndGoToPage(ls);
                }
            }
            if ($("#pspace_"+spacemr_space_id).length) {
                $("#pmenuInSpace_"+spacemr_space_id).css("background-color", "#d1ffb0");
                $("#pspace_"+spacemr_space_id)
                    .attr('fill', '#d1ffb0')
                    .mousedown(mousedownfunction)
                ;
                $("#pspacetext_"+spacemr_space_id)
                    .attr('fill', '#600000')
                // .mousedown(mousedownfunction)
                ;
                //-
                //-
                wid.selected_spacemr_space_id = spacemr_space_id;
                //-
            }
        }
        , space_menu_tooltip: function (evt, space) {
            var wid       = this;
            let tooltip_function = function(evt, tooltip_div){
                {
                    //- head
                    let d=$("<div>");
		    if (app_userHasPermission("db_spacemr_space_read")) {
                        let edit =
                            app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update"
                                                 + "&spacemr_space_id="+space.spacemr_space_id
                                                )
                            .attr('title',gRb("db.spacemr_space.update.title"))
                            .append(app_getIcon("edit", 15));
                        d.append(edit);
                        d.append(" ");
                    }
                    d.append(space.code);
                    tooltip_div.append(d);
                };
                //-
                //- insert
                if (!(space.spacemr_space_map_id_default) ) {
                    let insert_div = $("<div>");
                    tooltip_div.append(insert_div);
                    if (app_userHasPermission("db_spacemr_space_user_presence_insert")) {
			var lsi = "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_insert&spacemr_space_id="+space.spacemr_space_id+"";
                        var tab = app_ui_clickableLink(lsi)
			    .attr("class", "w3-bar-item w3-button")
			    .text(gRb("db.spacemr_space_user_presence.insert.title"))
                            .append(" ")
                            .append(app_ui_clickableLink(lsi)
                                    .append(app_getIcon("plus-square", 15)
                                            .css("color","#ff5050") ) ) ;
			insert_div.append(tab);
		    }
                    if (app_userHasPermission("db_spacemr_space_people_book_insert")) {
			var lsi = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_insert&spacemr_space_id="+space.spacemr_space_id+"";
                        var tab = app_ui_clickableLink(lsi)
			    .attr("class", "w3-bar-item w3-button")
			    .text(gRb("db.spacemr_space_people_book.insert.title"))
                            .append(" ")
                            .append(app_ui_clickableLink(lsi)
                                    .append(app_getIcon("plus-square", 15) ) ) ;
			insert_div.append(tab);
		    }
                }
                //-
                //- buttons
                let buttons_div = $("<div>");
                tooltip_div.append(buttons_div);
                //-
		if (app_userHasPermission("db_spacemr_space_user_presence_read")) {
		    var ls = "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list&spacemr_space_id="+space.spacemr_space_id+"";
                    var lsc = "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_calendar&spacemr_space_id="+space.spacemr_space_id+"";
                    var lsm = "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                        +"&app_spacemr_space_people_view_mode=presences"
                        +"&spacemr_space_child_id="+wid.spacemr_space_child_id
                        +"&spacemr_space_map_id="+wid.spacemr_space_map_id;
                    // alert (" " + wid.spacemr_space_map_id + " " + wid.spacemr_space_child_id);
                    var tab = app_ui_clickableLink(ls)
			.attr("class", "w3-bar-item w3-button")
			.text(gRb("db.spacemr_space_user_presence..plural"))
                        .append(" ")
                        .append(app_ui_clickableLink(lsm).append(app_getIcon("map", 15))
                                .css("color","#ff5050")
                               )
                        .append(app_ui_clickableLink(lsc).append(app_getIcon("calendar_alt", 15))
                                .css("color","#ff5050")
                                .attr('title',gRb("db.spacemr_space_user_presence.calendar"))
                               )
                    ;
		    buttons_div.append(tab);
		}
		if (app_userHasPermission("db_spacemr_space_people_book_read")) {
		    var ls = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list&spacemr_space_id="+space.spacemr_space_id+"";
		    var ls1 = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar&spacemr_space_id="+space.spacemr_space_id+"";
                    var lsm1 = "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                        +"&app_spacemr_space_people_view_mode=bookings"
                        +"&spacemr_space_child_id="+wid.spacemr_space_child_id
                        +"&spacemr_space_map_id="+wid.spacemr_space_map_id;
		    buttons_div
			.append(
			    app_ui_clickableLink(ls)
				.attr("class", "w3-bar-item w3-button")
				.text(gRb("db.spacemr_space_people_book..plural"))
				.append(" ")
                                .append(app_ui_clickableLink(lsm1).append(app_getIcon("map", 15)))
				.append(app_ui_clickableLink(ls1).append(app_getIcon("calendar_alt", 15))
				       )
			);
		}
                //-
                //-
                //-
		let mya=function(e) {
		    let rv = $("<div>");
		    tooltip_div.append(rv.append(e));
		    return(rv);
		}
		//-
		//- persone
		//-
                if (app_userHasPermission("db_spacemr_space_people_read")) {
		    var ls = "?page=app_spacemr_space_people__app_spacemr_space_people_list&spacemr_space_id="+space.spacemr_space_id+"";
		    tooltip_div.append(
		    	app_ui_clickableLink(ls)
		    	    .attr("class", "w3-bar-item w3-button")
		    	    .text(gRb("db.spacemr_people..plural"))
		    );
                }
		//-
		//- inventario
		//-
		if (app_userHasPermission("db_spacemr_inventario_read")) {
		    var ls = "?page=app_spacemr_inventario__app_spacemr_inventario_list&spacemr_space_id="+space.spacemr_space_id+"";
                    var lsmi = "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                        +"&app_spacemr_space_people_view_mode=inventarios"
                        +"&spacemr_space_child_id="+wid.spacemr_space_child_id
                        +"&spacemr_space_map_id="+wid.spacemr_space_map_id;
		    tooltip_div.append(
			app_ui_clickableLink(ls)
			    .attr("class", "w3-bar-item w3-button")
			    .text(gRb("db.spacemr_inventario..short"))
                            .append(" ")
                            .append(app_ui_clickableLink(lsmi).append(app_getIcon("map", 15))
                                    .css("color","#ff5050")
                                   )
		    );
		}
		//-
		//- responsibles 
		let peoples=space.spacemr_space_people_non_sitting_json;
		peoples.sort(function(a,b){let rv = a.spacemr_space_people_type_name.localeCompare(b.spacemr_space_people_type_name);
                                           if(rv == 0) rv = a.last_name.localeCompare(b.last_name)
                                           if(rv == 0) rv = a.first_name.localeCompare(b.first_name)
                                           return(rv)})
		peoples.forEach(function(people){
		    var the_text =
			people.spacemr_space_people_type_name + ": "+ people.first_name + " " + people.last_name
		    ;
		    mya(the_text)
			.css("font-style", "italic");
		    ;
		});
		//-
		//- peoples
		//-
		mya(gRb("db.spacemr_people..plural"))
		    .css("font-style", "italic");
		peoples=space.spacemr_space_people_json;
		peoples.sort(function(a,b){let rv = a.last_name.localeCompare(b.last_name);
                                           if(rv == 0) rv = a.first_name.localeCompare(b.first_name)
                                           return(rv)})
		peoples.forEach(function(people){
		    var the_text =
			people.first_name + " " + people.last_name
		    ;
		    mya(the_text);
		})
		//-
		//- info
		//-
		mya(space.description);
		let s = " / " + space.number_of_seating_booking
		mya(gRb("db.spacemr_space.number_of_seating")
		    +": "+ space.spacemr_space_people_count
		    + " / " + space.number_of_seating
		    + s
		   );
		mya(gRb("db.spacemr_space.area_in_meters2")
		    +": "+ space.area_in_meters2);
		// tooltip_div.append($("<pre>").append(app_JSONStringify(space))) ;
		setTimeout(app_tooltip_center, 20);
	    }
            app_tooltip_set_on_click(evt, tooltip_function);
        }

        
        , spaces_addArea_writeText_svg(svgText, space, spacearea) {
            var wid       = this;
	    // var dy_step = 0; // wid.default_font_size;
	    //-
	    //-
	    //-
            let dy=0;
            let addTspan = function(the_text) {
                // console.log(" -- addTspan: " + the_text);
                //-
                var tspan = document.createElementNS(wid.svgNS,'tspan');
                tspan.setAttributeNS(null, "x",  0);
                tspan.setAttributeNS(null, "dy", dy);
                dy=wid.default_font_size;
                // dy=dy+dy_step;
                tspan.appendChild(document.createTextNode(the_text));
                svgText.appendChild(tspan);
                return(tspan);
            }
            var tspan = addTspan(space.code);
            
            
            $(tspan).click(function(evt) {
                wid.space_menu_tooltip(evt, space);
            });
            // tspan.onclick = function(event){
            //     event.preventDefault();
            //     event.stopPropagation();
            //     // console.log(space.spacemr_space_map_id_default);
            //     var ls = "";
            //     if (space.spacemr_space_map_id_default) {
	    //         ls = "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people&spacemr_space_map_id="+space.spacemr_space_map_id_default+"";
	    //         app_initialization_setHashAndGoToPage(ls);
            //     } else {
            //         let wf = app_workflow_get("spacemr_space_people_book_workflow");
            //         if (app_userHasPermission("db_spacemr_space_people_book_admin")
            //             || (app_userHasPermission("db_spacemr_space_people_book_insert")
            //                 && wf.aux.bookers_roles.includes(app_spacemr_space_people_book_current_people.role))) {
	    //             ls = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_insert&spacemr_space_id="+space.spacemr_space_id+"";
	    //             app_initialization_setHashAndGoToPage(ls);
            //         } else {
	    //             ls = "?page=app_spacemr_space__app_spacemr_space_people_list&spacemr_space_id="+space.spacemr_space_id+"";
	    //             app_initialization_setHashAndGoToPage(ls);
            //         }
            //     }
            // };
            var mode = app_spacemr_space_people_view_mode;
            wid.modes[mode].spaces_addArea_writeText_svg(svgText, space, spacearea, addTspan, wid);
            //-
            svgText.setAttribute('fill','black');
        }
        , drawMenuInSpaces_tabs: function(menuInSpaces) {
            var wid       = this;
            var menu_statuses = [
                {
                    name: "mapModeMenu"
                    , label: "Modo"
                    , action: function() {
                        app_spacemr_space_people_menuInSpaces_status = wid._menuInSpaces_status;
                        var list = $("<ul>");
                        function drawThis(list){
                            //-
                            list.html("")
                            let li = null;
                            let fa = [];
                            if (app_userHasPermission("db_spacemr_space_people_read", wid.spacemr_space_map.app_group_name)) {
                                fa.push("people");
                                fa.push("resp");
                            }
                            fa.push("area");
                            if (app_userHasPermission("db_spacemr_space_people_book_read", wid.spacemr_space_map.app_group_name))
                                fa.push("bookings");
                            if (app_userHasPermission("db_spacemr_space_user_presence_read", wid.spacemr_space_map.app_group_name))
                                fa.push("presences");
                            if (app_userHasPermission("db_spacemr_inventario_read", wid.spacemr_space_map.app_group_name))
                                fa.push("inventarios");
                            //-
                            fa.forEach(function(mode, index){
                                wid.modes[mode].left_menu_draw_mode(list, mode, drawThis, wid);
                            });
                        };
                        drawThis(list);
                        menuInSpaces.append(list);
                    }
                }
                , {
                    name: "spaceList"
                    , label: gRb("db.spacemr_space..plural")
                    , action: function() {
                        app_spacemr_space_people_menuInSpaces_status = wid._menuInSpaces_status;
                        wid.drawMenuInSpaces_spaceList(menuInSpaces);
                    }
                }
            ];
            return(menu_statuses);
        }
    };
    rv.modes_init();
    return(rv);
}

function app_spacemr_space_map_view_map_people(map_ids) {
    // console.log(" map_ids: " + map_ids);
    var spacemr_space_map_id = getLocationParameterByName('spacemr_space_map_id');
    var spacemr_space_child_id = getLocationParameterByName('spacemr_space_child_id');
    if (map_ids != undefined) {
        spacemr_space_map_id   = map_ids.spacemr_space_map_id;
        spacemr_space_child_id = map_ids.spacemr_space_child_id;
    }
    {
        let d = getLocationParameterByName("spacemr_space_child_id");
        if (d != "") {
            spacemr_space_child_id = d;
        }
    }
    {
        let d = getLocationParameterByName("app_spacemr_space_people_book_map_current_date");
        if (d != "") {
            app_spacemr_space_people_book_map_current_date = new Date(parseInt(d));
        }
    }
    {
        let d = getLocationParameterByName("app_spacemr_space_people_view_mode");
        if (d != "") {
            app_spacemr_space_people_view_mode = d;
        }
    }
    //-
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-                    
    appSetPage(form, gRb("db.spacemr_space_map.update.map.title"));
    //-
    var map_editor_extension = app_spacemr_space_map_view_map_people_get_exension();
    map_editor_extension.spacemr_space_map_id   = spacemr_space_map_id;
    map_editor_extension.spacemr_space_child_id = spacemr_space_child_id;
    // map_editor_extension = { };
    //-
    app_doRequestMappingRequest("spacemr_space_map/spacemr_space_map_get"
                                , { spacemr_space_map_id: spacemr_space_map_id}
                                , function(content) {
				    var map_editor = app_spacemr_space_map_view_map_data(content, map_editor_extension);
                                });
}

function app_spacemr_space_map_view_map_data(content, map_editor_extension) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    // console.log(app_JSONStringify(content,1));
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj   = content.obj;
    var files = content.files;
    //-
    page.append(app_spacemr_space_tabs_get(obj.spacemr_space_id));
    page.append(app_spacemr_space_map_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    //-
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_map_id", "spacemr_space_map_id", "label", obj.spacemr_space_map_id);
    app_ui_standard_appendFieldHidden(grid, "id_obj", "obj", "mappa", app_JSONStringify(obj,0));
    //-
    //-
    var imageArea =
        $("<div id='imageArea'>")
        .css("border", "1px")
        .css("overwlow", "auto")
    ;
    grid.append(imageArea);
    //-
    //-
    var map_editor = app_spacemr_space_map_get_map_manager();
    var map_editor = Object.assign({}, map_editor, map_editor_extension);
    //-
    map_editor.setImageArea(imageArea);
    map_editor.initialize(files, obj);
    map_editor.drawImageArea();
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    //-
    //-
    appSetPage(page, obj.description);
    //-
    return(map_editor);
}



//-
var app_spacemr_space_map_form_update_old_data = undefined;
//-
//-
function app_spacemr_space_map_form_update_map() {
    var spacemr_space_map_id = getLocationParameterByName('spacemr_space_map_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-                    
    appSetPage(form, gRb("db.spacemr_space_map.update.map.title"));
    //-
    app_doRequestMappingRequest("spacemr_space_map/spacemr_space_map_get"
                                , { spacemr_space_map_id: spacemr_space_map_id}
                                , function(content) {
                                    app_spacemr_space_map_form_update_map_data(content);
                                });
}
//-
var app_spacemr_space_map_form_update_old_data = undefined;
//-

function app_spacemr_space_map_form_update_map_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    // console.log(app_JSONStringify(content,1));
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj   = content.obj;
    var files = content.files;
    //-
    page.append(app_spacemr_space_tabs_get(obj.spacemr_space_id));
    page.append(app_spacemr_space_map_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    //-
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_map_id", "spacemr_space_map_id", "label", obj.spacemr_space_map_id);
    app_ui_standard_appendFieldHidden(grid, "id_obj", "obj", "mappa", app_JSONStringify(obj,0));
    //-
    //-
    var imageArea =
        $("<div id='imageArea'>")
        .css("border", "1px")
        .css("overwlow", "auto")
    ;
    grid.append(imageArea);
    //-
    //-
    var map_manager = app_spacemr_space_map_get_map_manager();
    var map_editor_extension = {
        spacemr_space_map_id: obj.spacemr_space_map_id
        , undoRedoArray: []
        , undoRedoArray_position: undefined
        , save_getJsonObject: function(){
            var wid       = this;
            var rv = {};
            rv.options = wid.options;
            var space_info = {};
            rv.space_info = space_info;
            wid.inSpaces.forEach(function(space){
                var spacearea = wid.save_getJsonObject_getSpacearea(space.spacemr_space_id);
                if (spacearea != undefined) {
                    space_info[space.spacemr_space_id] = spacearea;
                }
            });
            // console.log("save_getJsonObject: " + app_JSONStringify(rv));
            return(rv);
        }
        , setStatus_spaceAreaText_move: function(event, spacemr_space_id) {
            var wid       = this;
            //-
            var startPoint = wid.getEventCoordinates(event);
            //-
            var selectedText = $("#pspacetext_"+wid.selected_spacemr_space_id);
            selectedText.hide(0);
            //-
            var text = document.createTextNode(selectedText.text());
            //-
            var transform = selectedText.attr("transform");
            //-
            var regex = /translate\((.*),(.*)\) rotate\((.*)\).*/;
            var found = transform.match(regex);
            var startTextinfo = {};
            startTextinfo.x=parseFloat(found[1]);
            startTextinfo.y=parseFloat(found[2]);
            startTextinfo.rotate=found[3];
            //-
            var text_tmp = document.createElementNS(wid.svgNS,'text');
            text_tmp.setAttribute('id','text_tmp');
            text_tmp.setAttributeNS(null,'transform', transform);
            text_tmp.setAttributeNS(null,"font-size", wid.default_font_size);
            text_tmp.setAttributeNS(null,"fill", selectedText.attr("fill"));
            text_tmp.appendChild(text);
            wid.svg.appendChild(text_tmp);
            //-
            //-
            wid.eventListener_mouseleave = function(event) {
                $('#text_tmp').remove();
                wid.setStatus_none();
            };
            wid.eventListener_mousemove = function(event) {
                //-
                var endPoint = wid.getEventCoordinates(event);
                var x = startTextinfo.x + endPoint.x - startPoint.x;
                var y = startTextinfo.y + endPoint.y - startPoint.y;
                var atransform =
                    "translate("+x+","+y+") rotate("+startTextinfo.rotate+")";
                // console.log("moving x."+x+" y."+y);
                text_tmp.setAttributeNS(null,'transform', atransform);;
            };
            wid.eventListener_mouseup = function(event) {
                $('#text_tmp').remove();
                var endPoint = wid.getEventCoordinates(event);
                var x = startTextinfo.x + endPoint.x - startPoint.x;
                var y = startTextinfo.y + endPoint.y - startPoint.y;
                //-
                wid.uiAction_moveText(spacemr_space_id, x, y);
                //-
                wid.setStatus_none();
                wid.spaces_selectSpaceArea_select(spacemr_space_id);
            };
        }
        , setStatus_spaceAreaPoint_move: function(event, spacemr_space_id, apointi) {
            var wid       = this;
            //-
            var startPoint = wid.getEventCoordinates(event);
            //-
            var selectedArea = $("#pspace_"+wid.selected_spacemr_space_id);
            var apoints      = wid.save_getJsonObject_getAPoints(selectedArea.attr("points"));
            //-
            var tmp_node = $("#tmp_node_" + apointi);
            var tmp_node_pos = {};
            tmp_node_pos.x=parseFloat(tmp_node.attr('x'));
            tmp_node_pos.y=parseFloat(tmp_node.attr('y'));
            //-
            $('#pspacetext_' + spacemr_space_id).hide(0);
            $('#pspace_' + spacemr_space_id).hide(0);
            $("#text_rotate_button").remove();
            //-
            var rect_tmp   = document.createElementNS(wid.svgNS,'polygon');
            rect_tmp.setAttribute('id','rect_tmp');
            rect_tmp.setAttributeNS(null, 'points', selectedArea.attr("points"));
            rect_tmp.setAttributeNS(null, 'fill', 'blue');
            rect_tmp.setAttributeNS(null, 'fill-opacity', 0.8);
            rect_tmp.setAttributeNS(null, 'stroke', 'black');
            rect_tmp.setAttributeNS(null, 'stroke-width', 5);
            wid.svg.appendChild(rect_tmp);
            //-
            rect_tmp = $(rect_tmp);
            //-
            wid.eventListener_mouseleave = function(event) {
                $('#rect_tmp').remove();
                wid.setStatus_none();
            };
            wid.eventListener_mousemove = function(event) {
                //-
                var endPoint = wid.getEventCoordinates(event);
                var dx = endPoint.x - startPoint.x;
                var dy = endPoint.y - startPoint.y;
                // console.log("moving x."+dx+" y."+dy);
                var comma = "";
                var p = "";
                for (i = 0; i < apoints.length; i = i + 2) {
                    if (i == apointi) {
                        p = p + comma + (apoints[i]+dx) + " " + (apoints[i+1]+dy);
                    } else {
                        p = p + comma + (apoints[i]) + " " + (apoints[i+1]);
                    }
                    comma = ",";
                }
                tmp_node.attr('x', tmp_node_pos.x + dx);
                tmp_node.attr('y', tmp_node_pos.y + dy);
                rect_tmp.attr("points", p);
            };
            wid.eventListener_mouseup = function(event) {
                var endPoint = wid.getEventCoordinates(event);
                var dx = endPoint.x - startPoint.x;
                var dy = endPoint.y - startPoint.y;
                //-
                wid.uiAction_moveAreaNode(spacemr_space_id, dx, dy, apointi);
                //-
                wid.setStatus_none();
                wid.spaces_selectSpaceArea_select(spacemr_space_id);
                $('#rect_tmp').remove();
            };
        }
        , setStatus_spaceArea_move: function(event, spacemr_space_id) {
            var wid       = this;
            var startPoint = wid.getEventCoordinates(event);
            //-
            var selectedArea = $("#pspace_"+wid.selected_spacemr_space_id);
            var apoints      = wid.save_getJsonObject_getAPoints(selectedArea.attr("points"));
            //-
            var rect_tmp   = document.createElementNS(wid.svgNS,'polygon');
            rect_tmp.setAttribute('id','rect_tmp');
            rect_tmp.setAttributeNS(null, 'points', selectedArea.attr("points"));
            rect_tmp.setAttributeNS(null, 'fill', 'blue');
            rect_tmp.setAttributeNS(null, 'fill-opacity', 0.8);
            rect_tmp.setAttributeNS(null, 'stroke', 'black');
            rect_tmp.setAttributeNS(null, 'stroke-width', 5);
            wid.svg.appendChild(rect_tmp);
            //-
            rect_tmp = $(rect_tmp);
            //-
            $('#pspacetext_' + spacemr_space_id).hide(0);
            $('#pspace_' + spacemr_space_id).hide(0);
            $("#text_rotate_button").remove();
            $(".tmp_node").remove();
            //-
            //-
            wid.eventListener_mouseleave = function(event) {
                $('#rect_tmp').remove();
                wid.setStatus_none();
            };
            wid.eventListener_mousemove = function(event) {
                //-
                var endPoint = wid.getEventCoordinates(event);
                var dx = endPoint.x - startPoint.x;
                var dy = endPoint.y - startPoint.y;
                // console.log("moving x."+dx+" y."+dy);
                var p = "" + (apoints[0]+dx) + " " + (apoints[1]+dy);
                var i = 2;
                for (i = 2; i < apoints.length; i = i + 2) {
                    p = p + "," + (apoints[i]+dx) + " " + (apoints[i+1]+dy);
                }
                rect_tmp.attr("points", p);
            };
            wid.eventListener_mouseup = function(event) {
                var endPoint = wid.getEventCoordinates(event);
                var dx = endPoint.x - startPoint.x;
                var dy = endPoint.y - startPoint.y;
                //-
                wid.uiAction_moveArea(spacemr_space_id, dx, dy);
                //-
                wid.setStatus_none();
                wid.spaces_selectSpaceArea_select(spacemr_space_id);
                $('#rect_tmp').remove();
            };
        }        
        , setStatus_drawNewSpaceArea: function(spacemr_space_id) {
            var wid       = this;
            wid.messageArea.text("Disegnare un rettangolo trascinando un punto sulla mappa " + spacemr_space_id);
            wid.svg_image.css("cursor","cell");
            var startPoint = {};
            var rect_tmp   = {};
            wid.eventListener_mousedown = function(event) {
                event.preventDefault();
                event.stopPropagation();
                startPoint = wid.getEventCoordinates(event);
                //-
                var rect = document.createElementNS(wid.svgNS,'rect');
                rect.setAttribute('id','rect_tmp');
                rect.setAttributeNS(null, 'x', startPoint.x);
                rect.setAttributeNS(null, 'y', startPoint.y);
                rect.setAttributeNS(null, 'width',20);
                rect.setAttributeNS(null, 'height',20);
                rect.setAttributeNS(null, 'fill', 'blue');
                rect.setAttributeNS(null, 'stroke', 'black');
                wid.svg.appendChild(rect);
                //-
                var endEventHandler = function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    wid.messageArea.text("");
                    var endPoint = wid.getEventCoordinates(event);
                    if ((endPoint.x - startPoint.x) <= 0
                        || (endPoint.y - startPoint.y) <= 0) {
                        //- nothing to do
                    } else {
                        wid.uiAction_addNewArea(spacemr_space_id, startPoint, endPoint);
                    }
                    $("#rect_tmp").remove();
                    wid.setStatus_none();
                }
                wid.eventListener_mousemove = function(event){
                    event.preventDefault();
                    event.stopPropagation();
                    var endPoint = wid.getEventCoordinates(event);
                    rect.setAttribute('width', endPoint.x - startPoint.x);
                    rect.setAttribute('height', endPoint.y - startPoint.y);
                }
                wid.eventListener_mouseup    = endEventHandler;
                wid.eventListener_mouseleave = endEventHandler;
            };
        }
        , spaces_selectSpaceArea_select_extension(spacemr_space_id) {
            var wid       = this;
            //-
        }
        , spaces_selectSpaceArea_select(spacemr_space_id) {
            var wid       = this;
            if (wid.selected_spacemr_space_id != undefined) {
                wid.spaces_selectSpaceArea_unselect();
            }
            if ($("#pspace_"+spacemr_space_id).length) {
                $("#pmenuInSpace_"+spacemr_space_id).css("background-color", "blue");
                $("#pspace_"+spacemr_space_id)
                    .attr('fill', 'blue')
                    .mousedown(function(event){
                        event.preventDefault();
                        event.stopPropagation();
                        wid.setStatus_spaceArea_move(event, spacemr_space_id);
                    })
                ;
                $("#pspacetext_"+spacemr_space_id)
                    .attr('fill', 'red')
                    .mousedown(function(event){
                        event.preventDefault();
                        event.stopPropagation();
                        wid.setStatus_spaceAreaText_move(event, spacemr_space_id);
                    })
                ;
                //-
                var selectedArea = $("#pspace_"+spacemr_space_id);
                var apoints = wid.save_getJsonObject_getAPoints(selectedArea.attr("points"));
                var startPoint = {x: apoints[0], y: apoints[1]};
                //-
                var text_rotate_button = app_getIcon("rotate_counterclockwise", 15);
                var innersvg = document.createElementNS(wid.svgNS,'svg');
                innersvg.innerHTML = text_rotate_button.html();
                innersvg.setAttribute('id','text_rotate_button');
                innersvg.setAttribute('wiewBox',text_rotate_button.attr("viewBox"));
                innersvg.setAttribute('style',text_rotate_button.attr("style"));
                innersvg.setAttribute('transform'
                                      ,"translate("+(startPoint.x + 5)+" "+(startPoint.y + 5)+")"
                                      + " scale(0.3 0.3)"
                                     );
                wid.svg.appendChild(innersvg);
                $("#text_rotate_button")
                    .css("color", "#CCCCCC")
                    .click(function(){
                        wid.uiAction_rotateText(spacemr_space_id);
                    })
                ;                
                var create_rect = function(x,y,id,nodeSize){
                    var rect = document.createElementNS(wid.svgNS,'rect');
                    rect.setAttribute("class", "tmp_node");
                    rect.setAttribute('id', id);
                    rect.setAttribute('x', x - nodeSize);
                    rect.setAttribute('y', y - nodeSize);
                    rect.setAttribute('width', 2 * nodeSize);
                    rect.setAttribute('height',2 * nodeSize);
                    rect.setAttribute('fill','yellow');
                    rect.setAttribute('style','stroke:black; stroke-width:1');
                    wid.svg.appendChild(rect);
                    return(rect);
                };
                //-
                var add_add_point = function(ax,ay,bx,by,i){
                    var x = (ax + bx) / 2;
                    var y = (ay + by) / 2;
                    var rect = create_rect(x,y,"tmp_add_node_" + i,20);
                    rect.setAttribute('fill','red');
                    var f=function(apointi){
                        var rv = function(event){
                            event.preventDefault();
                            event.stopPropagation();
                            wid.uiAction_addAreaNode(spacemr_space_id, apointi);
                        }
                        return(rv);
                    }
                    rect.onmousedown = f(i);
                };
                var lastPoint = { x:0, y:0 };
                for (i = 0; i < apoints.length; i = i + 2) {
                    var x = apoints[i];
                    var y = apoints[i+1];
                    var rect = create_rect(x,y,"tmp_node_" + i,20);
                    var f=function(apointi){
                        var rv = function(event){
                            event.preventDefault();
                            event.stopPropagation();
                            // console.log(" onmousedown " + apointi);
                            wid.setStatus_spaceAreaPoint_move(event, spacemr_space_id, apointi);
                        }
                        return(rv);
                    }
                    rect.onmousedown = f(i);
                    var fd=function(apointi){
                        var rv = function(event){
                            event.preventDefault();
                            event.stopPropagation();
                            wid.uiAction_deleteAreaNode(spacemr_space_id, apointi);
                        }
                        return(rv);
                    }
                    rect.oncontextmenu  = fd(i);
                    //-
                    if (i > 0) {
                        add_add_point(x,y,lastPoint.x,lastPoint.y,i);
                    }
                    lastPoint.x = x;
                    lastPoint.y = y;
                }
                add_add_point(lastPoint.x,lastPoint.y, apoints[0], apoints[1],0);
                //-
                wid.selected_spacemr_space_id = spacemr_space_id;
                //-
                wid.button_area_remove.show(0);
                //-
            }
        }
        , uiAction_addNewArea(spacemr_space_id, startPoint, endPoint) {
            var wid       = this;
            var apoints    = [
                startPoint.x, startPoint.y
                , startPoint.x, endPoint.y
                , endPoint.x, endPoint.y
                , endPoint.x, startPoint.y
            ];
            var spacearea = {};
            spacearea.apoints = apoints;
            var f_do = function() {
                wid.spaces_addArea(spacemr_space_id, spacearea);
                wid.messageArea.text("added area for " + wid.inSpacesIdx[spacemr_space_id].code);
                wid.drawMenuInSpaces();
            }
            var f_undo = function() {
                wid.spaces_deleteArea(spacemr_space_id);
                wid.drawMenuInSpaces();
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_removeSelectedArea(spacemr_space_id) {
            var wid       = this;
            var spacearea = wid.save_getJsonObject_getSpacearea(spacemr_space_id);
            var f_do = function() {
                wid.spaces_deleteArea(spacemr_space_id);
                wid.drawMenuInSpaces();
            }
            var f_undo = function() {
                wid.spaces_addArea(spacemr_space_id, spacearea);
                wid.drawMenuInSpaces();
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_moveArea(spacemr_space_id, dx, dy) {
            var wid       = this;
            var spacearea = wid.save_getJsonObject_getSpacearea(wid.selected_spacemr_space_id);
            var apoints_old = spacearea.apoints;
            var apoints_new = [];
            var i = 0;
            for (i = 0; i < apoints_old.length; i = i + 2) {
                apoints_new.push(apoints_old[i]+dx);
                apoints_new.push(apoints_old[i+1]+dy);
            }
            var f_do = function() {
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_new;
                wid.spaces_addArea(spacemr_space_id, spacearea);
            }
            var f_undo = function() {
                wid.spaces_selectSpaceArea_unselect();
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_old;
                wid.spaces_addArea(spacemr_space_id, spacearea);
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_addAreaNode(spacemr_space_id, apointi) {
            var wid       = this;
            var spacearea = wid.save_getJsonObject_getSpacearea(wid.selected_spacemr_space_id);
            var apoints_old = spacearea.apoints;
            var apoints_new = [];
            var i = 0;
            for (i = 0; i < apoints_old.length; i = i + 2) {
                if (i == apointi) {
                    var nexti = i-2;
                    if (nexti < 0) {
                        nexti = (apoints_old.length - 2);
                    }
                    apoints_new.push((apoints_old[i]+apoints_old[nexti])/2);
                    apoints_new.push((apoints_old[i+1]+apoints_old[nexti+1])/2);
                }
                apoints_new.push(apoints_old[i]);
                apoints_new.push(apoints_old[i+1]);
            }
            var f_do = function() {
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_new;
                wid.spaces_addArea(spacemr_space_id, spacearea);
                $(".tmp_node").remove();
            }
            var f_undo = function() {
                wid.spaces_selectSpaceArea_unselect();
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_old;
                wid.spaces_addArea(spacemr_space_id, spacearea);
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_deleteAreaNode(spacemr_space_id, apointi) {
            var wid       = this;
            var spacearea = wid.save_getJsonObject_getSpacearea(wid.selected_spacemr_space_id);
            var apoints_old = spacearea.apoints;
            var apoints_new = [];
            var i = 0;
            for (i = 0; i < apoints_old.length; i = i + 2) {
                if (i != apointi) {
                    apoints_new.push(apoints_old[i]);
                    apoints_new.push(apoints_old[i+1]);
                }
            }
            var f_do = function() {
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_new;
                wid.spaces_addArea(spacemr_space_id, spacearea);
                $(".tmp_node").remove();
            }
            var f_undo = function() {
                wid.spaces_selectSpaceArea_unselect();
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_old;
                wid.spaces_addArea(spacemr_space_id, spacearea);
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_moveAreaNode(spacemr_space_id, dx, dy, apointi) {
            var wid       = this;
            var spacearea = wid.save_getJsonObject_getSpacearea(wid.selected_spacemr_space_id);
            var apoints_old = spacearea.apoints;
            var apoints_new = [];
            var i = 0;
            for (i = 0; i < apoints_old.length; i = i + 2) {
                if (i == apointi) {
                    apoints_new.push(apoints_old[i]+dx);
                    apoints_new.push(apoints_old[i+1]+dy);
                } else {
                    apoints_new.push(apoints_old[i]);
                    apoints_new.push(apoints_old[i+1]);
                }
            }
            var f_do = function() {
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_new;
                wid.spaces_addArea(spacemr_space_id, spacearea);
            }
            var f_undo = function() {
                wid.spaces_selectSpaceArea_unselect();
                wid.spaces_deleteArea(spacemr_space_id);
                spacearea.apoints = apoints_old;
                wid.spaces_addArea(spacemr_space_id, spacearea);
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_moveText(spacemr_space_id, x, y) {
            var wid       = this;
            var selectedText = $("#pspacetext_"+wid.selected_spacemr_space_id);
            var transform = "" + selectedText.attr("transform");
            var regex = /translate\((.*),(.*)\) rotate\((.*)\).*/;
            var found = transform.match(regex);
            var rotate=found[3];
            var atransform =
                "translate("+x+","+y+") rotate("+rotate+")";
            selectedText.show(0);
            var f_do = function() {
                $("#pspacetext_"+wid.selected_spacemr_space_id).attr('transform', atransform);
            }
            var f_undo = function() {
                $("#pspacetext_"+wid.selected_spacemr_space_id).attr('transform', transform);
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_rotateText(spacemr_space_id) {
            var wid       = this;
            var txt = $("#pspacetext_" + spacemr_space_id);
            var transform = txt.attr("transform");
            var regex = /translate\((.*)\) rotate\((.*)\).*/;
            var found = transform.match(regex);
            var position = found[1];
            var rotate   = found[2];
            $('#pspacetext_' + spacemr_space_id).attr("transform",transform);
            var f_do = function() {
                // console.log( "rotate: " + rotate + " " + ((rotate - 90) % 360));
                var atransform = "translate("+position+") rotate("+((rotate - 90) % 360)+")";
                $('#pspacetext_' + spacemr_space_id).attr("transform",atransform);
            }
            var f_undo = function() {
                var atransform = "translate("+position+") rotate("+rotate+")";
                $('#pspacetext_' + spacemr_space_id).attr("transform",atransform);
            }
            wid.uiAction_undoAddAction(f_do, f_undo);
            f_do();
        }
        , uiAction_undoAddAction(f_do, f_undo) {
            var wid       = this;
            wid.uiAction_undoDoPrintUndoStatus("AddAction - in");
            if (wid.undoRedoArray_position != undefined) {
                $("#button_redo")
                    .css("background-color", "#CCCCCC")
                ;
                wid.undoRedoArray = wid.undoRedoArray.slice(0, wid.undoRedoArray_position);
                wid.undoRedoArray_position = undefined;
            }
            wid.undoRedoArray.push({f_do: f_do, f_undo: f_undo});
            $("#button_undo")
                .css("background-color", "white")
            ;
            wid.uiAction_undoDoPrintUndoStatus("AddAction - out");
        }
        , uiAction_undoDoPrintUndoStatus(msg) {
            var wid       = this;
            // console.log(msg + ": pos: " + wid.undoRedoArray_position + " len: " + wid.undoRedoArray.length);
        }
        , uiAction_undoDoUndo() {
            var wid       = this;
            wid.uiAction_undoDoPrintUndoStatus("uiAction_undoDoUndo - in");
            if (wid.undoRedoArray.length >= 0 &&
                (wid.undoRedoArray_position == undefined ||
                 wid.undoRedoArray_position > 0
                )) {
                if (wid.undoRedoArray_position == undefined) {
                    wid.undoRedoArray_position = wid.undoRedoArray.length;
                    $("#button_redo")
                        .css("background-color", "white")
                    ;
                }
                wid.undoRedoArray_position = wid.undoRedoArray_position - 1;
                wid.undoRedoArray[wid.undoRedoArray_position].f_undo();
                if(wid.undoRedoArray_position <= 0) {
                    $("#button_undo")
                        .css("background-color", "#CCCCCC")
                    ;
                }
            }
            wid.uiAction_undoDoPrintUndoStatus("uiAction_undoDoUndo - out");
        }
        , uiAction_undoDoRedo() {
            var wid       = this;
            wid.uiAction_undoDoPrintUndoStatus("uiAction_undoDoRedo - in");
            if (wid.undoRedoArray_position != undefined
                && wid.undoRedoArray_position >= 0) {
                wid.undoRedoArray[wid.undoRedoArray_position].f_do();
                wid.undoRedoArray_position = wid.undoRedoArray_position + 1;
                if (wid.undoRedoArray_position >= wid.undoRedoArray.length) {
                    wid.undoRedoArray_position = undefined;
                    $("#button_redo")
                        .css("background-color", "#CCCCCC")
                    ;
                }
                $("#button_undo")
                    .css("background-color", "white")
                ;
            }
            wid.uiAction_undoDoPrintUndoStatus("uiAction_undoDoRedo - out");
        }
        , drawImageArea_buttons: function(){
            var wid       = this;
            var button_undo =
                $("<span id='button_undo'>")
                .append(app_getIcon("undo", 15))
                .attr("class", "w3-btn w3-round")
                .css("background-color", "#CCCCCC")
                .append("undo" + " ")
                .click(function() {
                    wid.uiAction_undoDoUndo();
                })
            ;
            wid.imageArea.append(button_undo);
            wid.button_undo = button_undo;
            //-
            var button_redo =
                $("<span id='button_redo'>")
                .attr("class", "w3-btn w3-round")
                .css("background-color", "#CCCCCC")
                .append(app_getIcon("redo", 15))
                .append("redo" + " ")
                .click(function() {
                    wid.uiAction_undoDoRedo();
                })
            ;
            wid.imageArea.append(button_redo);
            wid.button_redo = button_redo;
            //-
            var button_area_remove =
                $("<span id='button_area_remove'>")
                .attr("class", "w3-btn w3-round")
                .append(app_getIcon("times_circle", 15))
                .append("area" + " ")
                .click(function() {
                    var idToRemove = wid.selected_spacemr_space_id;
                    wid.spaces_selectSpaceArea_unselect();
                    wid.uiAction_removeSelectedArea(idToRemove);
                })
                .css( "display", "none" )
            ;
            wid.imageArea.append(button_area_remove);
            wid.button_area_remove = button_area_remove;
            //-
            var button_info =
                $("<span id='button_info'>")
                .attr("class", "w3-btn w3-round")
                .append(app_getIcon("info_circle", 15))
                .click(function() {
                    var json = wid.save_getJsonObject();
                    alert(app_JSONStringify(json));
                })
            ;
            wid.imageArea.append(button_info);
            wid.button_info = button_info;
	}
        , spaces_selectSpaceArea_unselect_extension() {
            var wid       = this;
            wid.button_area_remove.hide(0);
        }
        , drawMenuInSpaces_tabs: function(menuInSpaces) {
            var wid       = this;
            var menu_statuses = [
                {
                    name: "spaceList"
                    , label: gRb("db.spacemr_space..plural")
                    , action: function() {
                        wid.drawMenuInSpaces_spaceList(menuInSpaces);
                    }
                }
                , {
                    name: "backgroundSmageSettings"
                    , label: "Image"
                    , action: function() {
                        // svgimg_dx
                        var gv = function(name, defaul) {
                            var rv = defaul;
                            if ( name in wid.options) {
                                rv = wid.options[name];
                            }
                            return(rv);
                        }
                        var form = $("<form>");
                        form.submit(function(){ return(false);} );
                        var getInputField = function getInputField(name){
                            var infield = 
                                $('<input class="w3-input w3-border w3-round" type="text"/>')
                                .attr("pattern", sys_number_integer_pattern)
                                .val(gv(name,0))
                                .change(function(){
                                    wid.options[name] = infield.val();
                                    wid.drawImageArea();
                                });
                            var rv =
                                $("<div>")
                                .append(name + ": ")
                                .append(infield)
                            ;
                            return(rv);
                        };
                        form
                            .append(getInputField("svgimg_dx"))
                            .append(getInputField("svgimg_dy"))
                            .append(getInputField("svgimg_dwidth"))
                            .append(getInputField("svgimg_dheight"))
                        ;
                        wid.uploadBackgroungImage(menuInSpaces);
                        menuInSpaces.append(form);
                    }
                }
            ];
            return(menu_statuses);
        }
        , uploadBackgroungImage: function(area){
            var wid       = this;
            if (app_userHasPermission("db_spacemr_space_map_update")) {
                area.append($("<div>").text(gRb("db.spacemr_space_map.update.load_background_image")));
                var uconf = {
                    id_prefix: "app_file"
                    , upload_url: "spacemr_space_map/app_file_insert_upload?spacemr_space_map_id="+wid.spacemr_space_map_id
                    , done: function(data){
                        location.reload();
                    }
                }
                area.append($('<div class="ui-block-b">')
                                     .append(app_app_file_form_update_doUpdate_uploadForm(uconf))
                                    );
            }
        }        
    };
    var map_editor = Object.assign({}, map_manager, map_editor_extension);
    //-
    map_editor.setImageArea(imageArea);
    map_editor.initialize(files, obj);
    map_editor.drawImageArea();
    //-
    //-
    var updateFunction = function(event) {
        var infoj = map_editor.save_getJsonObject();
        var info = app_JSONStringify(infoj,0);
        var old_obj = JSON.parse($("#id_obj").val());
        var old_info = old_obj.info;
        // console.log("info: " + info);
        // console.log("old_info: " + old_info);
        if (old_info != info) {
            var data = {
                spacemr_space_map_id: old_obj.spacemr_space_map_id
                , info: info
            };
            app_doRequestMappingRequestSync("spacemr_space_map/spacemr_space_map_update_info"
                                            , data
                                            , function(content) {
                                                app_setMessage(gRb("db.spacemr_space_map.update.Updated"));
                                                old_obj.info = info;
                                                $("#id_obj").val(app_JSONStringify(old_obj,0));
                                            }
                                           );
        }
    };
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_map_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    //-
    //-
    appSetPage(page, gRb("db.spacemr_space_map.update.map.title") + " " + obj.description);
    //-
}
