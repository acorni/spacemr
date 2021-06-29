function app_spacemr_space_people_doInitialize(callback) {
    log("spacemr_space_people page initialization...");
    callback();
}

//-
function app_spacemr_space_people_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space_people.insert.title"));
    //-
    var parms = {};
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    var spacemr_people_id = getLocationParameterByName('spacemr_people_id');
    if (spacemr_space_id != "") {
        app_doRequestMappingRequest("spacemr_space/spacemr_space_get"
                                    , { spacemr_space_id: spacemr_space_id}
                                    , function(content_space) {
                                        app_doRequestMappingRequest("spacemr_space_people/spacemr_space_people_insert_get_data"
                                                                    , parms
                                                                    , function(content) {
                                                                        content.spacemr_space = content_space.obj;
                                                                        content.spacemr_space_ancestors = content_space.ancestors;
                                                                        app_spacemr_space_people_form_insert_data(content);
                                                                    });
                                    });                            
    } else if (spacemr_people_id != "") {
        app_doRequestMappingRequest("spacemr_people/spacemr_people_get"
                                    , { spacemr_people_id: spacemr_people_id}
                                    , function(content_people) {
                                        app_doRequestMappingRequest("spacemr_space_people/spacemr_space_people_insert_get_data"
                                                                    , parms
                                                                    , function(content) {
                                                                        content.spacemr_people = content_people.obj;
                                                                        app_spacemr_space_people_form_insert_data(content);
                                                                    });
                                    });                            
    } else {
        app_doRequestMappingRequest("spacemr_space_people/spacemr_space_people_insert_get_data"
                                    , parms
                                    , function(content) {
                                        app_spacemr_space_people_form_insert_data(content);
                                    });
    }
    
    /* 
 */
}

function app_spacemr_space_people_form_insert_data(content) {
    //-
    // console.log(app_JSONStringify(content));
    //-
    var form = $('<form name="fm"/>');
    //-
    //-
    form.submit(function(){ return(false);} );
    if (content.spacemr_space != undefined) {
        form.append(app_spacemr_space_tabs(content.spacemr_space, content.spacemr_space_ancestors));
    }
    if (content.spacemr_people != undefined) {
        form.append(app_spacemr_people_tabs(content.spacemr_people));
        console.log(app_JSONStringify(content.spacemr_people))
    }
    // var spacemr_space_people_super_id = getLocationParameterByName('spacemr_space_people_super_id');
    // if (spacemr_space_people_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("spacemr_space_people_super/spacemr_space_people_super_get"
    //                                 , { spacemr_space_people_super_id: spacemr_space_people_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_people_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_people_super_tabs(spacemr_space_people_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="spacemr_space_people_super_id" id="id_spacemr_space_people_super_id" type="hidden" />')
    //                 .val(spacemr_space_people_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    var select_data_space_people_types = [];
    { // converting space_people_type
        select_data_space_people_types
            .push({ "value":"", "label":""});
        var i,a,o;
        a = content.spacemr_space_people_types;
        for (i = 0; i < a.length; i++) {
            var o = a[i];
            select_data_space_people_types
                .push({ "value":o.spacemr_space_people_type_id
                        , "label":o.name});
        }
    }
    
    //-
    app_ui_standard_appendFieldSelect(grid, "id_spacemr_space_people_type_id", "spacemr_space_people_type_id"
                                      , gRb("db.spacemr_space_people.spacemr_space_people_type_name")
                                      , ""
                                      ,select_data_space_people_types);

    //-
    //-
    //-
    var v;
    v = "";
    if (content.spacemr_space != undefined) v = content.spacemr_space.code;
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),v);
    //-
    v="";
    if (content.spacemr_space != undefined) v = content.spacemr_space.spacemr_space_id;
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", gRb("db.spacemr_space_people.spacemr_space_id"),v);
    app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                          , "id_spacemr_space_code"
                                                          , "id_spacemr_space_id"
                                                         );
    //-
    //-
    //-
    v="";
    if (content.spacemr_people != undefined) v = content.spacemr_people.username;
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_username", "spacemr_people_username", gRb("db.spacemr_space_people.spacemr_people_username"),v);
    v="";
    if (content.spacemr_people != undefined) v = content.spacemr_people.first_name;
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_first_name", "spacemr_people_first_name", gRb("db.spacemr_space_people.spacemr_people_first_name"),v);
    v="";
    if (content.spacemr_people != undefined) v = content.spacemr_people.last_name;
    app_ui_standard_appendFieldText(grid, "id_spacemr_people_last_name", "spacemr_people_last_name", gRb("db.spacemr_space_people.spacemr_people_last_name"),v);
    v="";
    if (content.spacemr_people != undefined) v = content.spacemr_people.spacemr_people_id;
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_people_id", "spacemr_people_id", "hidden label for spacemr_people_id",v);
    app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                           , "id_spacemr_people_username"
                                                           , "id_spacemr_people_first_name"
                                                           , "id_spacemr_people_last_name"
                                                           , "id_spacemr_people_id"
                                                         );
    //-
    //-
    app_ui_standard_appendFieldDate(grid, "id_date_from", "date_from", gRb("db.spacemr_space_people.date_from"),"");
    app_ui_standard_appendFieldDate(grid, "id_date_to", "date_to", gRb("db.spacemr_space_people.date_to"),"");
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space_people.description"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_people.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_space_people_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_space_people_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_space_people.insert.title"));
    // }
}




//-
function app_spacemr_space_people_form_insert_validate() {
    var rv = app_spacemr_space_people_form_update_validate();
    return rv;
}
//-
function app_spacemr_space_people_form_insert_sendData() {
    var data =  { 
         spacemr_space_people_type_id:   $('#id_spacemr_space_people_type_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , spacemr_people_id:   $('#id_spacemr_people_id').val()
         , date_from:   appConvertStringToTimestamp($('#id_date_from').val())
         , date_to:   appConvertStringToTimestamp($('#id_date_to').val())
         , description:   $('#id_description').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_space_people/spacemr_space_people_insert"
                                , data
                                , function(content) {
                                    var spacemr_space_people_id = content.spacemr_space_people_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_space_people__app_spacemr_space_people_form_update"
                                            + "&spacemr_space_people_id="+spacemr_space_people_id);
                                });
    //-
}
//-

function app_spacemr_space_people_list(map_ids) {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space_people.list.title"));
    //-
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    if ( spacemr_space_id == "") spacemr_space_id = undefined;
    var spacemr_people_id = getLocationParameterByName('spacemr_people_id');
    if ( spacemr_people_id == "") spacemr_people_id = undefined;
    var spacemr_people_username = getLocationParameterByName('spacemr_people_username');
    if ( spacemr_people_username == "") spacemr_people_username = undefined;
    //-
    if (map_ids != undefined) {
        spacemr_people_username =
            ( map_ids.spacemr_people_username == undefined ? spacemr_people_username : map_ids.spacemr_people_username);
    }
    //-
    var page    = $("<div>");
    if ( spacemr_space_id != undefined) {
        page.append(app_spacemr_space_tabs_get(spacemr_space_id));
    }
    if ( spacemr_people_id != undefined) {
        page.append(app_spacemr_people_tabs_get(spacemr_people_id));
    }
    var tableid = "table_spacemr_space_people_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            $('<abbr>').attr('title',gRb("db.spacemr_space_people.update.title")).append(
                app_ui_clickableLink("?page=app_spacemr_space_people__app_spacemr_space_people_form_update"
                                     + "&spacemr_space_people_id="+row[mapIdName["spacemr_space_people_id"]]
                                    )
                    .append(app_getIcon("edit", 15))
            );
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(spacemr_space_people_super)
                   // .append(spacemr_space_people_sub)
                   .append(
                       app_userHasPermission("db_spacemr_people_read") ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_people..single")).append(
                               app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update"
                                                    + "&spacemr_people_id="+row[mapIdName["spacemr_people_id"]]
                                                   )
                                   .append(app_getIcon("user", 15)))
                       ) : ""
                   ) 
                   .append(
                       app_userHasPermission("db_spacemr_space_read") ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space..single")).append(
                               app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update"
                                                    + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                                   )
                                   .append(app_getIcon("home", 15)))
                       ) : ""
                   ) 
                   .append(
                       app_userHasPermission("db_spacemr_space_map_read") && (row[mapIdName["spacemr_space_in_map_id_default"]] !== null ) ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space_map.show.on.map")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                    + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_in_map_id_default"]]
                                                    + "&spacemr_space_child_id="+row[mapIdName["spacemr_space_id"]]
                                                   )
                                   .append(app_getIcon("map_black", 15))
                           )
                           
                       ) : ""
                   )
                  );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "spacemr_space_people/spacemr_space_people_list"
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
        if (app_userHasPermission("db_spacemr_space_people_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space_people.insert.title"))
                    .click(function(event) {
                        var ln="?page=app_spacemr_space_people__app_spacemr_space_people_form_insert";
                        if ( spacemr_space_id != undefined) {
                            ln=ln+"&spacemr_space_id="+spacemr_space_id;
                        }
                        if ( spacemr_people_id != undefined) {
                            ln=ln+"&spacemr_people_id="+spacemr_people_id;
                        }
                        app_initialization_setHashAndGoToPage(ln);
                    }
                          )
            );
        }
        //-
        //- where clause
        //-
        fieldcontain.append($("<h3>").text(gRb("db.sys.ricercaPerParametri")));
        //-
        app_where_append_checkbox(fieldcontain, gRb("db.spacemr_space_people.spacemr_people_spaces_history"), "spacemr_people_spaces_history", divTable)
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people.spacemr_people_username"), "spacemr_people_username", divTable);
        app_where_append_boolean(fieldcontain, gRb("db.spacemr_space_people.spacemr_people_fg_is_a_seat"), "fg_is_a_seat", divTable)
        // app_where_append_integer_equal(fieldcontain, gRb("db.spacemr_space_people.spacemr_space_id"), "spacemr_space_id", divTable);
        // app_where_append_integer_equal(fieldcontain, gRb("db.spacemr_space_people.spacemr_people_id"), "spacemr_people_id", divTable);
        app_where_append_date(fieldcontain, gRb("db.spacemr_space_people.date_from"), "date_from", divTable);
        app_where_append_date(fieldcontain, gRb("db.spacemr_space_people.date_to"), "date_to", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people.description"), "description", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people.nota"), "nota", divTable);
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
                    + "?page=app_spacemr_space_people__app_spacemr_space_people_list"
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
    appSetPage(page, gRb("db.spacemr_space_people.list.title"));
    //-
}
//-
function app_spacemr_space_people_tabs(spacemr_space_people) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_spacemr_space_people_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space_people__app_spacemr_space_people_form_update&spacemr_space_people_id="+spacemr_space_people.spacemr_space_people_id)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_people..single.short") + ": "
                     ))
        ;
    }
    if (app_userHasPermission("db_spacemr_people_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id="+spacemr_space_people.spacemr_people_id)
                .attr("class", "w3-bar-item w3-button")
                .text(
                    spacemr_space_people.spacemr_people_first_name
                        + " "  + spacemr_space_people.spacemr_people_last_name 
                        + " - " + spacemr_space_people.spacemr_people_username 
                        + ", "
                ))
        ;
    }
    if (app_userHasPermission("db_spacemr_space_people_type_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space_people_type__app_spacemr_space_people_type_form_update&spacemr_space_people_type_id="+spacemr_space_people.spacemr_space_people_type_id)
                    .attr("class", "w3-bar-item w3-button")
                    .text(
                        spacemr_space_people.spacemr_space_people_type_name + ", "
                    ))
                   ;
    }
    if (app_userHasPermission("db_spacemr_app_spacemr_space_people_read")) {
        rv.append(
                app_ui_clickableLink("?page=app_spacemr_space_people__app_spacemr_space_people_list&spacemr_space_id="+spacemr_space_people.spacemr_space_id)
                    .attr("class", "w3-bar-item w3-button")
                    .text(
                        spacemr_space_people.spacemr_space_code
                    ))
        ;
    }
    // if (app_userHasPermission("db_spacemr_space_people_sub_read")) {
    //     var la = "?page=app_spacemr_space_people_sub__app_spacemr_space_people_sub_list&spacemr_space_people_id="+spacemr_space_people.spacemr_space_people_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.spacemr_space_people_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rv);
}
//-
function app_spacemr_space_people_form_update() {
    var spacemr_space_people_id = getLocationParameterByName('spacemr_space_people_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space_people.update.title"));
    //-
    app_doRequestMappingRequest("spacemr_space_people/spacemr_space_people_get"
                                , { spacemr_space_people_id: spacemr_space_people_id}
                                , function(content) {
                                    app_doRequestMappingRequest("spacemr_space_people/spacemr_space_people_insert_get_data"
                                                                , { }
                                                                , function(content1) {
                                                                    app_spacemr_space_people_form_update_data(content, content1.spacemr_space_people_types);
                                                                });
                                    
                                });
}

//-
var app_spacemr_space_people_form_update_old_data = undefined;
//-
function app_spacemr_space_people_form_update_data(content, spacemr_space_people_types) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    var obj = content.obj;
    page.append(app_spacemr_space_people_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    var select_data_space_people_types = [];
    { // converting space_people_type
        select_data_space_people_types
            .push({ "value":"", "label":""});
        var i,a,o;
        a = spacemr_space_people_types;
        for (i = 0; i < a.length; i++) {
            var o = a[i];
            select_data_space_people_types
                .push({ "value":o.spacemr_space_people_type_id
                        , "label":o.name});
        }
    }
    //-
    //-
    //-
    // app_ui_standard_appendFieldHidden(form, "id_spacemr_space_people_id", "spacemr_space_people_id", "label", obj.spacemr_space_people_id);
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_people_id", "spacemr_space_people_id", "label", obj.spacemr_space_people_id);
    // app_ui_standard_appendFieldInteger(grid, "id_spacemr_space_people_type_id", "spacemr_space_people_type_id", gRb("db.spacemr_space_people.spacemr_space_people_type_id"),obj.spacemr_space_people_type_id);
    app_ui_standard_appendFieldSelect(grid, "id_spacemr_space_people_type_id", "spacemr_space_people_type_id"
                                      , gRb("db.spacemr_space_people.spacemr_space_people_type_name")
                                      , obj.spacemr_space_people_type_id
                                      ,select_data_space_people_types);
    
    //-
    //-
    //-
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),obj.spacemr_space_code);
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", gRb("db.spacemr_space_people.spacemr_space_id"),obj.spacemr_space_id);
    app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                          , "id_spacemr_space_code"
                                                          , "id_spacemr_space_id"
                                                         );
    //-
    //-
    //-
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
    //-
    //-
    app_ui_standard_appendFieldDate(grid, "id_date_from", "date_from", gRb("db.spacemr_space_people.date_from"),appConvertDateToString(obj.date_from));
    app_ui_standard_appendFieldDate(grid, "id_date_to", "date_to", gRb("db.spacemr_space_people.date_to"),appConvertDateToString(obj.date_to));
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space_people.description"),obj.description);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_people.nota"),obj.nota);
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_people_update")) {
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
                    app_spacemr_space_people_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_people_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            spacemr_space_people_id:   $('#id_spacemr_space_people_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_space_people/spacemr_space_people_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_space_people__app_spacemr_space_people_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_people_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_space_people_logs"
                                        , controller: "spacemr_space_people/spacemr_space_people_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_space_people_id: obj.spacemr_space_people_id
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
    //-
    if (app_userHasPermission("db_spacemr_space_people_insert", obj.app_group_name)) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.spacemr_space_people.update.insertcopying"))
                .click(
                    function(event) {
                        var data = app_spacemr_space_people_form_update_getData();
                        if (app_JSONStringify(app_spacemr_space_people_form_update_old_data) == app_JSONStringify(data)) {
                            app_setMessage(gRb("db.spacemr_space_people.update.insertcopying.noChanges"));
                            
                        } else
                            var answer = confirm(gRb("db.spacemr_space_people.update.insertcopying.confirm"));
                        if (answer) {
                            var validate = app_spacemr_space_people_form_insert_validate();
                            if (validate.children().length != 0) {
                                doPopupHtml(validate);
                            } else {
                                app_spacemr_space_people_form_insert_sendData();
                            }
                        }
                    }
                )
        );
    }
    //-
    //-
    //- logs
    var logs = $("<div>"); page.append(logs);
    //-
    appSetPage(page, gRb("db.spacemr_space_people.update.title")
               + ": " + obj.spacemr_people_username
               + ", " + obj.spacemr_space_people_type_name
               + ", " + obj.spacemr_space_code);
    //-
    // app_test_showMobilePropertiesInfo();
    app_spacemr_space_people_form_update_old_data = app_spacemr_space_people_form_update_getData();
}

//-
function app_spacemr_space_people_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_space_people_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_space_people_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_space_people_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_space_people_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("spacemr_space_people/spacemr_space_people_update"
                                        , data
                                        , function(content) {
                                            app_spacemr_space_people_form_update_old_data = app_spacemr_space_people_form_update_getData();
                                            app_setMessage(gRb("db.spacemr_space_people.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_space_people_form_update_validate() {
    var rv = $('<ul/>');
    ($('#id_spacemr_space_people_type_id').val() != "" && sys_number_integer_validation.test($('#id_spacemr_space_people_type_id').val())) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people.spacemr_space_people_type_name") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_space_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people.spacemr_space_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_people_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people.spacemr_people_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_dateFormatUi_validation.test($('#id_date_from').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people.date_from") + " - " + gRb("db.sys.invalidValue"))));
    ($('#id_date_to').val() == "") || sys_dateFormatUi_validation.test($('#id_date_to').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people.date_to") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_space_people_form_update_getData() {
    var data =  { 
         spacemr_space_people_id:   $('#id_spacemr_space_people_id').val()
         , spacemr_space_people_type_id:   $('#id_spacemr_space_people_type_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , spacemr_people_id:   $('#id_spacemr_people_id').val()
         , date_from:   appConvertStringToTimestamp($('#id_date_from').val())
         , date_to:   appConvertStringToTimestamp($('#id_date_to').val())
         , description:   $('#id_description').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}
