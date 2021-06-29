function app_spacemr_space_doInitialize(callback) {
    // log("spacemr_space page initialization...");
    callback();
}
//-


//-
function app_spacemr_space_form_insert() {
    var spacemr_space_in_id = getLocationParameterByName('spacemr_space_in_id');
    // console.log("spacemr_space_in_id: " + spacemr_space_in_id);
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space.insert.title"));
    //-
    var parms = {};
    if (spacemr_space_in_id != undefined
       && spacemr_space_in_id != "") {
        parms = { "spacemr_space_in_id": spacemr_space_in_id };
    }
    app_doRequestMappingRequest("spacemr_space/spacemr_space_insert_get_data"
                                , parms
                                , function(content) {
                                    app_spacemr_space_form_insert_data(content);
                                });
}

function app_spacemr_space_form_convert_groups_and_types(content) {
    var rv = {};
    var select_data_groups = [];
    { // converting groups
        select_data_groups
            .push({ "value":"", "label":""});
        var i,a,o;
        a = content.user_groups;
        for (i = 0; i < a.length; i++) {
            var o = a[i];
            select_data_groups
                .push({ "value":o
                        , "label":o});
        }
    }
    //-
    var select_data_space_type_ids = [];
    { // converting space_type_ids
        select_data_space_type_ids
            .push({ "value":"", "label":""});
        var i,a,o;
        a = content.space_types;
        for (i = 0; i < a.length; i++) {
            var o = a[i];
            select_data_space_type_ids
                .push({ "value": o.spacemr_space_type_id
                        , "label": o.name});
        }
    }
    //-
    rv.select_data_groups = select_data_groups;
    rv.select_data_space_type_ids = select_data_space_type_ids;
    return(rv);
}


function app_spacemr_space_form_insert_data(content) {
    //-
    //-
    // console.log(app_JSONStringify(content));
    //-
    var form = $('<form name="fm"/>');
    //-
    var spacemr_space_in = content.spacemr_space_in;
    if (spacemr_space_in != undefined) {
        form.append(app_spacemr_space_tabs(spacemr_space_in, content.ancestors));
    }
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var spacemr_space_super_id = getLocationParameterByName('spacemr_space_super_id');
    // if (spacemr_space_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("spacemr_space_super/spacemr_space_super_get"
    //                                 , { spacemr_space_super_id: spacemr_space_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_super_tabs(spacemr_space_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="spacemr_space_super_id" id="id_spacemr_space_super_id" type="hidden" />')
    //                 .val(spacemr_space_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    var converted = app_spacemr_space_form_convert_groups_and_types(content);
    var select_data_groups = converted.select_data_groups;
    var select_data_space_type_ids = converted.select_data_space_type_ids;
    //-
    app_ui_standard_appendFieldSelect(grid, "id_app_group_name", "app_group_name"
                                      , gRb("db.spacemr_space.app_group_name")
                                      ,((spacemr_space_in != undefined) ? spacemr_space_in.app_group_name : "")
                                      ,select_data_groups);
    //-
    app_ui_standard_appendFieldSelect(grid, "id_spacemr_space_type_id", "spacemr_space_type_id", gRb("db.spacemr_space.spacemr_space_type_name"),"",select_data_space_type_ids);
    //-
    app_ui_standard_appendFieldText(grid, "id_code", "code", gRb("db.spacemr_space.code"),"");
    //-    
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_in_code", "spacemr_space_in_code", gRb("db.spacemr_space.spacemr_space_in_code"), ((spacemr_space_in != undefined) ? spacemr_space_in.code : ""));
    app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                          , "id_spacemr_space_in_code"
                                                          , "id_spacemr_space_in_id"
                                                         );
    //-
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_in_id", "spacemr_space_in_id", "a dummy label for spacemr_space_in_id", ((spacemr_space_in != undefined) ? spacemr_space_in.spacemr_space_id : ""));
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space.description"),"");
    app_ui_standard_appendFieldDecimal(grid, "id_area_in_meters2", "area_in_meters2", gRb("db.spacemr_space.area_in_meters2"),"");
    app_ui_standard_appendFieldInteger(grid, "id_number_of_seating", "number_of_seating", gRb("db.spacemr_space.number_of_seating"),"");
    app_ui_standard_appendFieldInteger(grid, "id_number_of_seating_booking", "number_of_seating_booking", gRb("db.spacemr_space.number_of_seating_booking"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_user_white_list", "user_white_list", gRb("db.spacemr_space.user_white_list"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_user_presence_progressive_code_map", "user_presence_progressive_code_map", gRb("db.spacemr_space.user_presence_progressive_code_map"),"");
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space.nota"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_space_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_space_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_space.insert.title"));
    // }
}
//-
function app_spacemr_space_form_insert_validate() {
    var rv = $('<ul/>');
    // console.log("group: " + $('#id_app_group_name').val());
    // console.log("type: " + $('#id_spacemr_space_type_id').val());
    $('#id_app_group_name').val() != ""
        || (rv.append($("<li/>").text(gRb("db.spacemr_space.app_group_name") + " - " + gRb("db.sys.invalidValue"))));
    $('#id_spacemr_space_type_id').val() != ""
        && sys_number_integer_validation.test($('#id_spacemr_space_type_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space.spacemr_space_type_name") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_space_in_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space.spacemr_space_in_id") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_space_form_insert_sendData() {
    var data =  { 
         app_group_name:   $('#id_app_group_name').val()
         , spacemr_space_type_id:   $('#id_spacemr_space_type_id').val()
         , spacemr_space_in_id:   $('#id_spacemr_space_in_id').val()
         , code:   $('#id_code').val()
         , description:   $('#id_description').val()
         , number_of_seating:   $('#id_number_of_seating').val()
         , number_of_seating_booking:   $('#id_number_of_seating_booking').val()
         , area_in_meters2:   appConvertStringToDecimal($('#id_area_in_meters2').val())
         , user_white_list:   $('#id_user_white_list').val()
         , user_presence_progressive_code_map:   $('#id_user_presence_progressive_code_map').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_space/spacemr_space_insert"
                                , data
                                , function(content) {
                                    var spacemr_space_id = content.spacemr_space_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_space__app_spacemr_space_form_update"
                                            + "&spacemr_space_id="+spacemr_space_id);
                                });
    //-
}

function app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                               , input_code_value_field_id
                                                               , destination_value_field_id
                                                               , callback = null) {
    var rv = "";
    if (app_userHasPermission("db_spacemr_space_read")) {
        var search_button =
            app_ui_standard_button()
            .text(gRb("db.sys.search"));
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
                    .append(rv)
                    .append(search_button)
                    .append(" ")
                    .append(hide_button)
                    .append(search_area)
                   )
        ;
        var divTable=null;
        search_button.click(function(event){
            if (search_area.html() == "") {
                hide_button.show();
                //- setup search area
                divTable=$("<div>");
                var tableid = "table_spacemr_space_search";
                var buttonsCell = function(tr, mapIdName, row) {
                    var select =
                        $('<abbr>').attr('title',gRb("db.sys.doSelect")).append(
                            app_getIcon("check_circle", 15)
                                .click(function() {
                                    // alert("Hello " +row[mapIdName["spacemr_space_id"]] + " - " + row[mapIdName["spacemr_space_code"]]);
                                    $('#'+input_code_value_field_id).val(row[mapIdName["spacemr_space_code"]])
                                    $('#'+destination_value_field_id).val(row[mapIdName["spacemr_space_id"]]);
                                    search_area.hide(200,function(){ search_area.html(""); });
                                    hide_button.hide(200);
                                    if (callback != null) {
                                        callback();
                                    }
                                }
                                      )
                        );
                    // var edit =
                    //     $('<abbr>').attr('title',gRb("db.spacemr_space.update.title")).append(
                    //         app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update"
                    //                              + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
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
                                            , controller: "spacemr_space/spacemr_space_list"
                                            , buttonsCell: buttonsCell
                                            , content: {
                                                qparams: { } 
                                            }
                                            , enable_csv_output: true
                                           };
                tableMattoni_options.content.qparams.where = { "code":  $('#'+input_code_value_field_id).val() };
                appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
                //-
                search_area.append(divTable);
                search_area.show();
            } else {
                var vv = $('#'+input_code_value_field_id).val();
                divTable.tableMattoni().where("code", $('#'+input_code_value_field_id).val());
                divTable.tableMattoni().render();
            }
        });
    }
    return(rv);
};


function app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                                , id_spacemr_people_username
                                                                , id_spacemr_people_first_name
                                                                , id_spacemr_people_last_name
                                                                , destination_value_field_id) {
    var rv = "";
    if (app_userHasPermission("db_spacemr_people_read")) {
        var search_button =
            app_ui_standard_button()
            .text(gRb("db.sys.search"));
        var clear_button =
            app_ui_standard_button()
            .text(gRb("db.sys.clear"))
            .click(function(){
                $('#'+id_spacemr_people_username).val("")
                $('#'+id_spacemr_people_first_name).val("")
                $('#'+id_spacemr_people_last_name).val("")
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
            if (search_area.html() == "") {
                hide_button.show();
                //- setup search area
                divTable=$("<div>");
                var tableid = "table_spacemr_people_search";
                var buttonsCell = function(tr, mapIdName, row) {
                    var select =
                        $('<abbr>').attr('title',gRb("db.sys.doSelect")).append(
                            app_getIcon("check_circle", 15)
                                .click(function() {
                                    // alert("Hello " +row[mapIdName["spacemr_people_id"]] + " - " + row[mapIdName["spacemr_people_code"]]);
                                    $('#'+id_spacemr_people_username).val(row[mapIdName["username"]])
                                    $('#'+id_spacemr_people_first_name).val(row[mapIdName["first_name"]])
                                    $('#'+id_spacemr_people_last_name).val(row[mapIdName["last_name"]])
                                    //-
                                    $('#'+destination_value_field_id).val(row[mapIdName["spacemr_people_id"]]);
                                    search_area.hide(200,function(){ search_area.html(""); });
                                    hide_button.hide(200);
                                }
                                      )
                        );
                    // var edit =
                    //     $('<abbr>').attr('title',gRb("db.spacemr_people.update.title")).append(
                    //         app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update"
                    //                              + "&spacemr_people_id="+row[mapIdName["spacemr_people_id"]]
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
                                            , controller: "spacemr_people/spacemr_people_list"
                                            , buttonsCell: buttonsCell
                                            , content: {
                                                qparams: { } 
                                            }
                                            , enable_csv_output: true
                                           };
                tableMattoni_options.content.qparams.where = {
                    "username": $('#'+id_spacemr_people_username).val()
                    , "first_name": $('#'+id_spacemr_people_first_name).val()
                    , "last_name": $('#'+id_spacemr_people_last_name).val()
                };
                appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
                //-
                search_area.append(divTable);
                search_area.show();
            } else {
                var vv ;
                vv = $('#'+id_spacemr_people_username).val();
                vv = $('#'+id_spacemr_people_first_name).val();
                vv = $('#'+id_spacemr_people_last_name).val();
                divTable.tableMattoni().where("username", $('#'+id_spacemr_people_username).val());
                divTable.tableMattoni().where("first_name", $('#'+id_spacemr_people_first_name).val());
                divTable.tableMattoni().where("last_name", $('#'+id_spacemr_people_last_name).val());
                divTable.tableMattoni().render();
            }
        });
    }
    return(rv);
};


function app_spacemr_space_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_spacemr_space_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            $('<abbr>').attr('title',gRb("db.spacemr_space.update.title")).append(
                app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update"
                                     + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                    )
                    .append(app_getIcon("edit", 15))
            );
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   .append(
                       app_userHasPermission("db_spacemr_space_people_read") ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_people..plural")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_people__app_spacemr_space_people_list"
                                                    + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                                   )
                                   .append(app_getIcon("user", 15))
                           )
                       ) : "")
                   .append(
                       app_userHasPermission("db_spacemr_space_map_read") && (row[mapIdName["spacemr_space_map_id_default"]] !== null ) ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space_map.update.map.title")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                    + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_map_id_default"]]
                                                   )
                                   .append(app_getIcon("map", 15))
                           )
                           
                       ) : "")
                   .append(
                       app_userHasPermission("db_spacemr_space_map_read") && (row[mapIdName["spacemr_space_in_map_id_default"]] !== null ) ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space_map.show.on.map")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                    + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_in_map_id_default"]]
                                                    + "&spacemr_space_child_id="+row[mapIdName["spacemr_space_id"]]
                                                   )
                                   .append(app_getIcon("map_black", 15))
                           )
                       ) : "" )
                   .append(
                       app_userHasPermission("db_spacemr_space_people_book_read")  ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space_people_book.calendar")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar"
                                                    + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                                   )
                                   .append(app_getIcon("calendar_alt", 15))
                           )
                       ) : "" )
                  );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "spacemr_space/spacemr_space_list"
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
    // var spacemr_space_super_id = getLocationParameterByName('spacemr_space_super_id');
    // if (spacemr_space_super_id != "") {
    //     app_doRequestMappingRequest("spacemr_space_super/spacemr_space_super_get"
    //                                 , { spacemr_space_super_id: spacemr_space_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_super_tabs(spacemr_space_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.spacemr_space_super_id = spacemr_space_super_id;
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
        if (app_userHasPermission("db_spacemr_space_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_spacemr_space__app_spacemr_space_form_insert"
                                                              // + "&spacemr_space_super_id=" + spacemr_space_super_id
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
        // app_where_append_integer(fieldcontain, gRb("db.spacemr_space.app_group_id"), "app_group_id", divTable);
        // app_where_append_integer(fieldcontain, gRb("db.spacemr_space.spacemr_space_type_id"), "spacemr_space_type_id", divTable);
        // app_where_append_integer(fieldcontain, gRb("db.spacemr_space.spacemr_space_in_id"), "spacemr_space_in_id", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space.code"), "code", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space.description"), "description", divTable);
        app_where_append_integer(fieldcontain, gRb("db.spacemr_space.number_of_seating"), "number_of_seating", divTable);
        app_where_append_integer(fieldcontain, gRb("db.spacemr_space.number_of_seating_booking"), "number_of_seating_booking", divTable);
        app_where_append_decimal(fieldcontain, gRb("db.spacemr_space.area_in_meters2"), "area_in_meters2", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space.user_white_list"), "user_white_list", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space.user_presence_progressive_code_map"), "user_presence_progressive_code_map", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space.nota"), "nota", divTable);
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
                    + "?page=app_spacemr_space__app_spacemr_space_list&qparams="+qp
                ;
                app_initialization_setHash(url);
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
        grid.append(fieldcontain);
        buttons_div.append(form);
    }
    //-
    appSetPage(page, gRb("db.spacemr_space.list.title"));
    //-
}
//-

function app_spacemr_space_tabs_get(spacemr_space_id, set_spacemr_space=undefined) {
    var rv = $('<span>');
    // console.log("app_spacemr_space_tabs_get - spacemr_space_id: " + spacemr_space_id);
    app_doRequestMappingRequest("spacemr_space/spacemr_space_get"
                                , { spacemr_space_id: spacemr_space_id}
                                , function(content) {
                                    rv.append(app_spacemr_space_tabs(content.obj, content.ancestors));
                                    if( set_spacemr_space != undefined) {
                                        set_spacemr_space(content.obj);
                                    }
                                });
    return(rv);
}
function app_spacemr_space_tabs(spacemr_space, spacemr_space_ancestors) {
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
        .append("S")
        .click(toggle_this_menu)
    ;
    // alert("window.innerWidth: " + window.innerWidth);
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
    if (app_userHasPermissionOnAnyGroup("db_spacemr_space_read")) {
        //-
        var ls = "?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space..single") + ": " + spacemr_space.code)
        );
        //-
        //- ancestors
        //-
        {
            var ancspan=$("<span>").attr("class", "w3-bar-item w3-button");
            var v = spacemr_space_ancestors;
            var separator = "";
            for (var i = 0; i < v.length; i++) {
                let child=null;
                if (i+1<v.length) {
                    child=v[i+1].spacemr_space_id;
                } else {
                    child=spacemr_space.spacemr_space_id;
                }
                var ss = v[i];
                var ls = "?page=app_spacemr_space__app_spacemr_space_form_update_childs&spacemr_space_id="+ss.spacemr_space_id+"";
                var o = app_ui_clickableLink(ls).text(ss.code);
                if(ss.spacemr_space_map_id_default !== null){
                    var ls1 = "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people&spacemr_space_map_id="+ss.spacemr_space_map_id_default+"&spacemr_space_child_id="+child;
                    o.append(" ");
                    o.append(app_ui_clickableLink(ls1)
                             .attr('title',gRb("db.spacemr_space_map.maps.people"))
                             .append(app_getIcon("map", 15)));
                }
                o.append(
                    app_userHasPermission("db_spacemr_space_people_book_read")  ? (
                        app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar"
                                             + "&spacemr_space_id="+ss.spacemr_space_id
                                            )
                            .attr('title',gRb("db.spacemr_space_people_book.calendar"))
                            .append(app_getIcon("calendar_alt", 15))
                    ) : "" );
                o.append(
                    app_userHasPermission("db_spacemr_space_user_presence_read")  ? (
                        app_ui_clickableLink("?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_calendar"
                                             + "&spacemr_space_id="+ss.spacemr_space_id
                                            )
                            .attr('title',gRb("db.spacemr_space_user_presence.calendar"))
                            .append(app_getIcon("calendar_alt", 15).css("color","#ff5050"))
                    ) : "" );
                ancspan
                    .append(separator)
                    .append(o)
                ;
                separator = " > "
            }
            rv.append(ancspan);
        }
        //-
        //- space_people_book
        //-
        if (app_userHasPermission("db_spacemr_space_people_book_read")) {
            var ls = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
            var ls1 = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
            var l2 = app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_people_book..plural"))
                .append(" ")
                .append(app_ui_clickableLink(ls1).append(app_getIcon("calendar_alt", 15))
                        .attr('title',gRb("db.spacemr_space_people_book.calendar"))
                       );
            rv.append(l2);
            if (app_userHasPermission("db_spacemr_space_people_book_insert")) {
                var ls3 = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_insert&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
                l2.append(app_ui_clickableLink(ls3).append(app_getIcon("plus", 15))
                                .attr('title',gRb("db.spacemr_space_people_book.insert.title"))
                         );
            }
            rv;
        }
        //-
        //- space_user_presence
        //-
        if (app_userHasPermission("db_spacemr_space_user_presence_read")) {
            var ls = "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
            var ls1 = "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_calendar&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
            var l2 = app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_user_presence..plural"))
                .append(" ")
                .append(app_ui_clickableLink(ls1).append(app_getIcon("calendar_alt", 15))
                        .css("color","#ff5050")
                        .attr('title',gRb("db.spacemr_space_user_presence.calendar"))
                       );
            rv.append(l2);
            if (app_userHasPermission("db_spacemr_space_user_presence_insert")) {
                var ls3 = "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_insert&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
                l2.append(app_ui_clickableLink(ls3).append(app_getIcon("plus", 15))
                                .css("color","#ff5050")
                                .attr('title',gRb("db.spacemr_space_user_presence.insert.title"))
                         );
            }
            rv;
        }
        //-
        //- childs
        //-
        var ls = "?page=app_spacemr_space__app_spacemr_space_form_update_childs&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space.update.childs.title"))
        );
        //-
        //- people
        //-
        if (app_userHasPermission("db_spacemr_space_people_read")) {
            var ls = "?page=app_spacemr_space_people__app_spacemr_space_people_list&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
            rv.append(
                app_ui_clickableLink(ls)
                    .attr("class", "w3-bar-item w3-button")
                    .text(gRb("db.spacemr_people..plural"))
            );
        }
        //-
        //- map
        //-
        if (app_userHasPermission("db_spacemr_space_map_read")) {
            var ls = "?page=app_spacemr_space_map__app_spacemr_space_map_list&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
            rv.append(
                app_ui_clickableLink(ls)
                    .attr("class", "w3-bar-item w3-button")
                    .text(gRb("db.spacemr_space_map..short"))
                    .append(
                        (spacemr_space.spacemr_space_map_id_default !== null ) ? (
                            $('<abbr>').attr('title',gRb("db.spacemr_space_map.maps.people")).append(
                                app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                     + "&spacemr_space_map_id="+spacemr_space.spacemr_space_map_id_default
                                                    )
                                    .append(app_getIcon("map", 15))
                            )                            
                        ) : ""                )
            )
            ;
        }
        //-
        //- inventario
        //-
        if (app_userHasPermission("db_spacemr_inventario_read")) {
            var ls = "?page=app_spacemr_inventario__app_spacemr_inventario_list&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
            rv.append(
                app_ui_clickableLink(ls)
                    .attr("class", "w3-bar-item w3-button")
                    .text(gRb("db.spacemr_inventario..short"))
            );
        }
    }
    // if (app_userHasPermission("db_spacemr_space_sub_read")) {
    //     var la = "?page=app_spacemr_space_sub__app_spacemr_space_sub_list&spacemr_space_id="+spacemr_space.spacemr_space_id+"";
    //     rv.append($('<a href="#" class="ui-btn ui-corner-all">')
    //               .text(gRb("db.spacemr_space_sub.list.title"))
    //               .attr("href", "#" + la)
    //               .click(function(event){
    //                   app_initialization_setHashAndGoToPage(la);
    //               })
    //              );
    // }
    return(rrv);
}



//-
function app_spacemr_space_form_update() {
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space.update.title"));
    //-
    app_doRequestMappingRequest("spacemr_space/spacemr_space_get_form"
                                , { spacemr_space_id: spacemr_space_id}
                                , function(content) {
                                    app_spacemr_space_form_update_data(content);
                                });
}

//-
var app_spacemr_space_form_update_old_data = undefined;
//-
function app_spacemr_space_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    var columns = content.columns;
    page.append(app_spacemr_space_tabs(obj, content.ancestors));
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
        app_spacemr_space_form_update_doUpdate(onSuccess);
    };
    //-
    var topbuttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_update")) {
        topbuttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    //- logs
    var logs = $("<div>"); grid.append(logs);
    //-
    var converted = app_spacemr_space_form_convert_groups_and_types(content);
    var select_data_groups = converted.select_data_groups;
    var select_data_space_type_ids = converted.select_data_space_type_ids;
    //-
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_id", "spacemr_space_id", "label", obj.spacemr_space_id);
    //-
    app_ui_standard_appendFieldSelect(grid, "id_app_group_name", "app_group_name", gRb("db.spacemr_space.app_group_name"),obj.app_group_name,select_data_groups);
    app_ui_standard_appendFieldSelect(grid, "id_spacemr_space_type_id", "spacemr_space_type_id", gRb("db.spacemr_space.spacemr_space_type_name"),obj.spacemr_space_type_id,select_data_space_type_ids);
    app_ui_standard_appendFieldText(grid, "id_code", "code", gRb("db.spacemr_space.code"),obj.code);
    //-
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_in_code", "spacemr_space_in_code", gRb("db.spacemr_space.spacemr_space_in_code"), obj.spacemr_space_in_code);
    app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                          , "id_spacemr_space_in_code"
                                                          , "id_spacemr_space_in_id");
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_in_id", "spacemr_space_in_id","label for spacemr_space_in_id", obj.spacemr_space_in_id);
    app_ui_standard_appendFieldText(grid, "id_description", "description", gRb("db.spacemr_space.description"),obj.description);
    app_ui_standard_appendFieldDecimal(grid, "id_area_in_meters2", "area_in_meters2", gRb("db.spacemr_space.area_in_meters2"),obj.area_in_meters2);
    app_ui_standard_appendFieldInteger(grid, "id_number_of_seating", "number_of_seating", gRb("db.spacemr_space.number_of_seating"),obj.number_of_seating);
    app_ui_standard_appendFieldInteger(grid, "id_number_of_seating_booking", "number_of_seating_booking", gRb("db.spacemr_space.number_of_seating_booking"),obj.number_of_seating_booking);
    columns.includes("user_white_list") && app_ui_standard_appendFieldTextArea(grid, "id_user_white_list", "user_white_list", gRb("db.spacemr_space.user_white_list"),obj.user_white_list)
        .attr("wrap", "off");
    columns.includes("user_presence_progressive_code_map") && app_ui_standard_appendFieldTextArea(grid, "id_user_presence_progressive_code_map", "user_presence_progressive_code_map", gRb("db.spacemr_space.user_presence_progressive_code_map"),obj.user_presence_progressive_code_map);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space.nota"),obj.nota);
    //-
    //-
    //-




    {
        let id = "id_user_presence_progressive_code_map";
        let span = $('<span />');
        let csvgenout = $("<div>").hide();
        let csvgenout_text = $("<div>");
        let csvgenout_in   = 
            $("<textarea class='w3-input w3-border w3-round w3-theme-light'>")
            .val(`input.example
1_11
1_9
1_7
2_5
2_17
`)
        ;
        let csvgenfunction = function() {
            csvgenout_text.html("");
            var rv = [];
            csvgenout_in.val().split("\n").forEach(function(line,i){
                line = line.trim();
                if (line != "") {
                    rv.push(line);
                }
            });
            var fv = $("#id_user_presence_progressive_code_map").val();
            var replace_in_field = false;
            if (fv != "") {
                try {
                    fv = JSON.parse(fv);
                    replace_in_field = true;
                } catch (error) {
                    app_setMessage("Error parsing JSON: " + error);
                }
            }
            if (!replace_in_field) {
                fv = {};
            }
            fv["user_presence_progressive_code_map"] = rv;
            $("#id_user_presence_progressive_code_map").val(JSON.stringify(fv,null,2));
            app_setMessage(gRb("db.spacemr_space.tools.lines_to_json.json_updated"));
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
            .append(" -- ")
            .append(app_getIcon("database", 15))
            .append(gRb("db.spacemr_space.tools.lines_to_json"))
            .click(csvgenclick);
        grid.find("label[id='"+id+"_label']")
            .after( csvgen ) ;
        grid.find("textarea[id='"+id+"']")
            .after( csvgenout ) ;
    }


    {
        let id = "id_user_presence_progressive_code_map";
        let span = $('<span />');
        let checkout = $("<div>").hide();
        let checkout_text = 
            $("<textarea class='w3-input w3-border w3-round w3-theme-light'>");
        let checkfunction = function() {
            checkout_text.val("");
            var fv = $("#id_user_presence_progressive_code_map").val();
            if (fv == "") {
                return
            }
            try {
                fv = JSON.parse(fv);
            } catch (error) {
                checkout_text.val("Error parsing JSON: " + error);
                return;
            }
            if (fv.user_presence_progressive_code_map) {
                var rv = "";
                var i = 0;
                while (i<fv.user_presence_progressive_code_map.length) {
                    var s = fv.user_presence_progressive_code_map[i];
                    i = i + 1;
                    rv = rv + "\n" + i + "\t" + s; 
                }
                checkout_text.val(rv);
            }
        };
        let checkclick = function(){
            if (checkout.html() == "") {
                checkout.show("fast");
                checkout.append(
                    app_ui_standard_appendRow(checkout)
                        .append(
                            app_ui_standard_button()
                                .click(function(event){
                                    checkfunction();
                                })
                                .append(app_getIcon("redo", 15))
                                .append(gRb("db.sys.compute"))
                        )
                )
                checkout.append(checkout_text);
                checkfunction();
            } else {
                checkout.hide("fast");
                checkout.html("");
            }
        };
        let check = $("<span>")
            .append(" -- ")
            .append(app_getIcon("check", 15))
            .append(gRb("db.sys.check"))
            .click(checkclick);
        grid.find("label[id='"+id+"_label']")
            .after( check ) ;
        grid.find("textarea[id='"+id+"']")
            .after( checkout ) ;
    }



    {
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
            csvgenout_text.html('"users": "'+rv+'"');
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
            .append(" -- ")
            .append(app_getIcon("database", 15))
            .append(gRb("db.spacemr_space.tools.lines_to_csv"))
            .click(csvgenclick);
        grid.find("label[id='"+id+"_label']")
            .after( csvgen ) ;
        grid.find("textarea[id='"+id+"']")
            .after( csvgenout ) ;
    }
    //-
    //-
    //-



    if (content.spacemr_space_id_user_white_list_help){
        let id = "id_user_white_list";
        let span = $('<span />');
        let helpout = $("<div>").hide();
        let helpout_text = $("<div>");
        let helpclick = function(){
            if (helpout.html() == "") {
                helpout.html(content.spacemr_space_id_user_white_list_help);
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
        grid.find("label[id='"+id+"_label']")
            .after( help ) ;
        grid.find("textarea[id='"+id+"']")
            .after( helpout ) ;
    }
    //-

    
    if (content.spacemr_space_id_user_white_list_plugins){
        eval(content.spacemr_space_id_user_white_list_plugins);
    }
    //-

    {
        let id = "id_user_white_list";
        let span = $('<span />');
        let checkout = $("<div>").hide();
        let checkout_text = $("<div>");
        let checkfunction = function() {
            checkout_text.html("");
            // checkout_text.append("" + Date.now());
            app_doRequestMappingRequest("spacemr_space/spacemr_space_update_check_user_white_list"
                                        , { spacemr_space_id: $('#id_spacemr_space_id').val()
                                            , user_white_list:   $('#id_user_white_list').val()
                                            , code: $('#id_code').val()
                                          }
                                        , function(content) {
                                            if("errors" in content) {
                                                checkout_text
                                                    .append(gRb("db.sys.errors"))
                                                    .append($("<pre>")
                                                            .text(content.errors)
                                                           )
                                            }
                                            checkout_text
                                            .append(gRb("db.spacemr_space.tools.text_description"))
                                                .append( $("<textarea class='w3-input w3-border w3-round w3-theme-light'>")
                                                         .val(content.text_description)
                                                       )
                                            .append(gRb("db.spacemr_space.tools.involved_users"))
                                                .append( $("<textarea class='w3-input w3-border w3-round w3-theme-light'>")
                                                         .val(JSON.stringify(content.all_involved_users,null,2))
                                                       )
                                        });
        };
        let checkclick = function(){
            if (checkout.html() == "") {
                checkout.show("fast");
                checkout.append(
                    app_ui_standard_appendRow(checkout)
                        .append(
                            app_ui_standard_button()
                                .click(function(event){
                                    checkfunction();
                                })
                                .append(app_getIcon("redo", 15))
                                .append(gRb("db.sys.compute"))
                        )
                )
                checkout.append(checkout_text);
                checkfunction();
            } else {
                checkout.hide("fast");
                checkout.html("");
            }
        };
        let check = $("<span>")
            .append(" -- ")
            .append(app_getIcon("check", 15))
            .append(gRb("db.sys.check"))
            .click(checkclick);
        grid.find("label[id='"+id+"_label']")
            .after( check ) ;
        grid.find("textarea[id='"+id+"']")
            .after( checkout ) ;
    }
    
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_update", obj.app_group_name)) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    if (app_userHasPermission("db_spacemr_space_delete", obj.app_group_name)) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            spacemr_space_id:   $('#id_spacemr_space_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_space/spacemr_space_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_space__app_spacemr_space_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_logs", obj.app_group_name)) {
        topbuttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_space_logs"
                                        , controller: "spacemr_space/spacemr_space_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_space_id: obj.spacemr_space_id
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
    if (app_userHasPermission("db_spacemr_space_insert", obj.app_group_name)) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.spacemr_space.update.insertcopying"))
                .click(
                    function(event) {
                        var data = app_spacemr_space_form_update_getData();
                        if (app_JSONStringify(app_spacemr_space_form_update_old_data) == app_JSONStringify(data)) {
                            app_setMessage(gRb("db.spacemr_space.update.insertcopying.noChanges"));
                            
                        } else
                            var answer = confirm(gRb("db.spacemr_space.update.insertcopying.confirm"));
                        if (answer) {
                            var validate = app_spacemr_space_form_insert_validate();
                            if (validate.children().length != 0) {
                                doPopupHtml(validate);
                            } else {
                                app_spacemr_space_form_insert_sendData();
                            }
                        }
                    }
                )
        );
    }
    //-
    //-
    //-
    //-
    //-
    appSetPage(page, gRb("db.spacemr_space.update.title") + " " + obj.code);
    //-
    // app_test_showMobilePropertiesInfo();
    app_spacemr_space_form_update_old_data = app_spacemr_space_form_update_getData();
}

//-
function app_spacemr_space_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_space_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_space_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_space_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_space_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("spacemr_space/spacemr_space_update"
                                        , data
                                        , function(content) {
                                            app_spacemr_space_form_update_old_data = app_spacemr_space_form_update_getData();
                                            app_setMessage(gRb("db.spacemr_space.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_space_form_update_validate() {
    var rv = $('<ul/>');
    // console.log("group: " + $('#id_app_group_name').val());
    // console.log("type: " + $('#id_spacemr_space_type_id').val());
    $('#id_app_group_name').val() != ""
        || (rv.append($("<li/>").text(gRb("db.spacemr_space.app_group_name") + " - " + gRb("db.sys.invalidValue"))));
    $('#id_spacemr_space_type_id').val() != ""
        && sys_number_integer_validation.test($('#id_spacemr_space_type_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space.spacemr_space_type_name") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_space_in_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space.spacemr_space_in_id") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_space_form_update_getData() {
    var data =  { 
         spacemr_space_id:   $('#id_spacemr_space_id').val()
         , app_group_name:   $('#id_app_group_name').val()
         , spacemr_space_type_id:   $('#id_spacemr_space_type_id').val()
         , spacemr_space_in_id:   $('#id_spacemr_space_in_id').val()
         , code:   $('#id_code').val()
         , description:   $('#id_description').val()
         , area_in_meters2:   appConvertStringToDecimal($('#id_area_in_meters2').val())
         , number_of_seating:   $('#id_number_of_seating').val()
         , number_of_seating_booking:   $('#id_number_of_seating_booking').val()
         , user_white_list:   $('#id_user_white_list').val()
         , user_presence_progressive_code_map:   $('#id_user_presence_progressive_code_map').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}

 
function app_spacemr_space_form_update_childs() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space.update.childs.title"));
    //-
    var page    = $("<div>");
    //-
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    page.append(app_spacemr_space_tabs_get(spacemr_space_id));
    //-
    var tableid = "table_spacemr_space_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update"
                                 + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                )
            .attr('title',gRb("db.spacemr_space.update.title"))
            .append(app_getIcon("edit", 15));
        var edit_in =
            app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update_childs"
                                 + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                )
            .attr('title',gRb("db.spacemr_space.update.childs.title"))
            .append(app_getIcon("bars", 15));
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   .append(app_ui_clickableLink("?page=app_spacemr_space_people__app_spacemr_space_people_list"
                                                + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                               )
                               .attr('title',gRb("db.spacemr_people..plural"))
                               .append(app_getIcon("user", 15)))
                   .append(edit_in)
                   .append(
                       app_userHasPermission("db_spacemr_space_map_read") && (row[mapIdName["spacemr_space_map_id_default"]] !== null ) ? (
                           $('<abbr>').attr('title',gRb("db.spacemr_space_map.maps.people")).append(
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                    + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_map_id_default"]]
                                                   )
                                   .append(app_getIcon("map", 15))
                           )                           
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
                                , controller: "spacemr_space/spacemr_space_list"
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
    tableMattoni_options.content.qparams.where = {};
    tableMattoni_options.content.qparams.where.spacemr_space_in_id = spacemr_space_id;
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
        var workline   = app_ui_standard_appendRow(grid);
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.refresh"))
                .click(function(event) {
                    // console.log(app_JSONStringify(divTable.tableMattoni().options));
                    divTable.tableMattoni().render();
                }
                      )
        );
        if (app_userHasPermission("db_spacemr_space_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_spacemr_space__app_spacemr_space_form_insert"
                                                              + "&spacemr_space_in_id=" + spacemr_space_id
                                                             )
                    }
                          )
            );
        }
        if (app_userHasPermission("db_spacemr_space_insert_mass")) {
            var create_mass_insert_form = function(content) {
                var obj = content.obj;
                //-
                var converted = app_spacemr_space_form_convert_groups_and_types(content);
                var select_data_groups = converted.select_data_groups;
                var select_data_space_type_ids = converted.select_data_space_type_ids;
                //-
                app_ui_standard_appendFieldTextArea(workline
                                                    , "space_insert_mass_text"
                                                    , "space_insert_mass_text"
                                                    , gRb("db.spacemr_space.update.insertcopying_mass_text")
                                                    , "# format"
                                                    + "\n# <codice>: <descrizione>"
                                                    + "\n#"
                                                   );
                //-
                app_ui_standard_appendFieldSelect(workline, "id_app_group_name", "app_group_name", gRb("db.spacemr_space.app_group_name"),obj.app_group_name,select_data_groups);
                app_ui_standard_appendFieldSelect(workline, "id_spacemr_space_type_id", "spacemr_space_type_id", gRb("db.spacemr_space.spacemr_space_type_name"),obj.spacemr_space_type_id,select_data_space_type_ids);
                workline.append(
                    app_ui_standard_button()
                        .text(gRb("db.spacemr_space.update.insertcopying_mass_go"))
                        .click(
                            function(event) {
                                var status = $("#space_insert_mass_status");
                                var text = $("#space_insert_mass_text").val();
                                //-
                                var lines =  text.match(/[^\r\n]+/g);;
                                status.append(" lines.length: " + lines.length + "\n");
                                for (var i=0; i<lines.length; i++) {
                                    var line = lines[i].trim();
                                    if (line == "" || line.startsWith("#")) {
                                        //- ignored
                                    } else {
                                        var p_colon = line.indexOf(":");
                                        if (p_colon >= 0) {
                                            var code = line.substring(0,p_colon).trim();
                                            var description = line.substring(p_colon+1).trim();
                                            status.append("inserting ["+code+"]["+description+"]...\n");
                                            //-
                                            var data =  { 
                                                app_group_name:   $('#id_app_group_name').val()
                                                , spacemr_space_type_id:   $('#id_spacemr_space_type_id').val()
                                                , spacemr_space_in_id:   ""+obj.spacemr_space_id
                                                , code:   code
                                                , description:   description
                                                , number_of_seating:   0
                                                , number_of_seating_booking:   0
                                                , area_in_meters2:   0
                                                , nota:   ""
                                            };
                                            //-
                                            app_doRequestMappingRequestSync("spacemr_space/spacemr_space_insert"
                                                                            , data
                                                                            , function(content) {
                                                                                var spacemr_space_id = content.spacemr_space_id;
                                                                                status.append("added ["+code+"], please refresh this page\n");
                                                                            });
                                        }
                                    }
                                }
                            }
                        )
                );
                workline.append($("<pre id='space_insert_mass_status'>"));
            }
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space.update.insertcopying_mass"))
                    .click(
                        function(event) {
                            if (workline.text() == "") {
                                var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
                                app_doRequestMappingRequest("spacemr_space/spacemr_space_get"
                                                            , { spacemr_space_id: spacemr_space_id}
                                                            , function(content) {
                                                                create_mass_insert_form(content);
                                                            });
                            } else {
                                workline.text("");
                            }
                        }
                    )
            );
        }
        if (app_userHasPermission("db_spacemr_space_user_presence_qrcode")) {
            let button =
                app_ui_clickableLink("?page=app_spacemr_space_user_presence__app_spacemr_space_form_user_presence_qrcode"
                                     + "&spacemr_space_id="+spacemr_space_id
                                    )
                .append(
                    app_ui_standard_button()
                        .text(gRb("db.sys.qrcode"))
                );
            buttonline.append(" ").append(button);
        }
        //-
        //- where clause
        //-
        //-
        page.append(form);
        {
            //-
            //- navigation and filter hooks
            //-
            divTable.tableMattoni().render_addHook(function(){
                var qp=app_JSONStringify(divTable.tableMattoni().qparams(),0).replace(/%/g,"%25");
                var url = ""
                    + "?page=app_spacemr_space__app_spacemr_space_form_update_childs&spacemr_space_id="+spacemr_space_id+"&qparams="+qp
                ;
                app_initialization_setHash(url);
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
    }
    //-
    appSetPage(page, gRb("db.spacemr_space.update.childs.title"));
    //-
}

//-
