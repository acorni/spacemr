function app_spacemr_space_user_presence_doInitialize(callback) {
    log("spacemr_space_user_presence page initialization...");
    callback();
}
//-

function app_spacemr_space_user_presence_form_insert() {
    //-
    var spacemr_space_id  = getLocationParameterByName('spacemr_space_id');
    app_doRequestMappingRequest("spacemr_space_user_presence/spacemr_space_user_presence_insert_get_data"
                                , {
                                    spacemr_space_id: spacemr_space_id
                                }
                                , function(content) {
                                    app_spacemr_space_user_presence_form_insert_data(content);
                                });
}


function app_spacemr_space_user_presence_form_insert_data(content) {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    var alert_div = $("<div>");
    var alert_data = {}; alert_data.content = null;
    //-
    if (content.spacemr_space != undefined) {
        if (app_userHasPermission("db_spacemr_space_read")) {
            form.append(app_spacemr_space_tabs_get(content.spacemr_space.spacemr_space_id));
        }
    }
    //-
    var grid = app_ui_standard_getGrid(form);
    var topline = app_ui_standard_appendRow(grid);
    //-
    var v;
    var setReadOnly = function(id){
        grid.find("input[id='"+id+"']")
            .prop('readonly', 'readonly')
            .css('background-color', "lightgray")
        ;
    }
    //-
    //-
    app_ui_standard_appendFieldTimestamp(grid, "id_date_time", "date_time", gRb("db.spacemr_space_user_presence.date_time")
                                         ,appConvertTimestampToString((new Date()).getTime()))
        .change(function(){
            app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
        });
    ;
    if (app_userHasPermission("db_spacemr_space_user_presence_insert_choose_people_number")) {
        app_ui_standard_appendFieldInteger(grid, "id_people_number", "people_number", gRb("db.spacemr_space_user_presence.people_number.long"),"1")
            .change(function(){
                app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
            });
    } else {
        app_ui_standard_appendFieldHidden(grid, "id_people_number", "people_number", gRb("db.spacemr_space_user_presence.people_number.long"),"1")
            .change(function(){
                app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
            });
    }
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_user_presence.nota"),"");
    //-
    v = "";
    if (content.spacemr_space != undefined) v = content.spacemr_space.code;
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),v);
    //-
    v="";
    if (content.spacemr_space != undefined) v = content.spacemr_space.spacemr_space_id;
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", "hidden space",v);
    app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                          , "id_spacemr_space_code"
                                                          , "id_spacemr_space_id"
                                                          , function(){
                   app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
        }
                                                         );
    //-
    v="";
    if (content.current_user != undefined) v = content.current_user.user_name;
    app_ui_standard_appendFieldText(grid, "id_app_user_user_name", "app_user_user_name", gRb("db.app_user.user_name"),v);
    v="";
    if (content.current_user != undefined) v = content.current_user.first_name;
    app_ui_standard_appendFieldText(grid, "id_app_user_first_name", "app_user_first_name", gRb("db.app_user.first_name"),v);
    v="";
    if (content.current_user != undefined) v = content.current_user.last_name;
    app_ui_standard_appendFieldText(grid, "id_app_user_last_name", "app_user_last_name", gRb("db.app_user.last_name"),v);
    v="";
    if (content.current_user != undefined) v = content.current_user.app_user_id;
    app_ui_standard_appendFieldHidden(grid, "id_app_user_id", "app_user_id", "hidden label for app_user_id",v) ;
    // app_ui_standard_appendFieldInteger(grid, "id_app_user_id", "app_user_id", gRb("db.spacemr_space_user_presence.app_user_id"),"");
    //-
    //-
    if (app_userHasPermission("db_spacemr_space_user_presence_admin")
        && app_userHasPermission("db_app_user_read")
       ) {
        var ldap_search_button = false;
        if (app_userHasPermission("db_app_user_insert")
           && app_userHasPermission("spacemr_people_ldap_search") ) {
            var ask_and_insert_user_from_ldap = function() {
                var m = gRb("db.spacemr_space_user_presence.insert_user_from_ldap")
                    + " " + $("#id_app_user_user_name").val()
                    + " - " + $("#id_app_user_first_name").val()
                    + " - " + $("#id_app_user_last_name").val()
                if (confirm(m)) {
                    app_doRequestMappingRequest("app_user/app_user_insert"
                                                , {
                                                    user_name: $("#id_app_user_user_name").val()
                                                    , first_name: $("#id_app_user_first_name").val()
                                                    , last_name: $("#id_app_user_last_name").val()
                                                    , email: ""
                                                    , password: ""
                                                    , nota: "from app_spacemr_space_user_presence_form_insert"
                                                }
                                                , function(content) {
                                                    $("#id_app_user_id").val(content.app_user_id);
                                                    app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
                                                });
                    
                }
            }
            ldap_search_button =
                app_app_user_form_insertLdapButton(grid
                                                   , {
                                                       field_name_ids: {
                                                           user_name: '#id_app_user_user_name'
                                                           , first_name: '#id_app_user_first_name'
                                                           , last_name: '#id_app_user_last_name'
                                                           , email: false
                                                       }
                                                       , on_select_callback: ask_and_insert_user_from_ldap
                                                   });
            ldap_search_button.hide();
        }
        app_app_user_ui_standard_appendSearch_user(grid
                                                   , "id_app_user_user_name"
                                                   , "id_app_user_first_name"
                                                   , "id_app_user_last_name"
                                                   , "id_app_user_id"
                                                   , function(){
                                                       app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
                                                   }
                                                   , function() {
                                                       $("#id_app_user_id").val("");
                                                       ldap_search_button.show("fast");
                                                   }
                                                   , function(){
                                                       if (ldap_search_button) {
                                                           ldap_search_button.click();
                                                       }
                                                   }
                                                  );
    }
    //-
    //-
    if (!app_userHasPermission("db_spacemr_space_user_presence_admin")) {
        ["id_app_user_user_name"
         , "id_app_user_first_name"
         , "id_app_user_last_name"
        ].forEach(function(i){
            setReadOnly(i);
        })
    }
    if (!app_userHasPermission("db_spacemr_space_user_presence_insert_choose_date_time")) {
        setReadOnly("id_date_time");
    }
    //-
    topline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_space_user_presence_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_space_user_presence_form_insert_sendData();
                }
            })
    );
    if (app_userHasPermission("db_spacemr_space_user_presence_read")) {
        topline
            .append(" ")
            .append(
                app_ui_standard_button()
                    .append(
                        app_ui_clickableLink(
                            "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list&qparams={%22where%22:{%22this_user_name%22:true,%22from_today%22:true}}" )
                            .text(gRb("db.spacemr_space_user_presence.my_presences_of_today"))
                            .append(" ")
                    ) 
            );

    }

    if (app_userHasPermission("db_spacemr_space_user_presence_read")
        && app_userHasPermission("db_spacemr_space_read")
        && content.spacemr_space != undefined) {
        topline
            .append(" ")
            .append(
                app_ui_standard_button()
                    .append(
                        app_ui_clickableLink(
                            "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_calendar"
                                + "&spacemr_space_id="+content.spacemr_space.spacemr_space_id )
                            .text(gRb("db.spacemr_space_user_presence.calendar"))
                            .append(" ")
                            .append(app_getIcon("calendar_alt", 15).css("color","#ff5050") )
                    ) 
            );
    }
    if (app_userHasPermission("db_spacemr_space_read")
        && content.spacemr_space != undefined) {
        let ccd = new Date();
        ccd = new Date(ccd.getFullYear()
                           ,ccd.getMonth()
                           ,ccd.getDate()
                           ,0,0,0
                      );
        topline
            .append(" ")
            .append(
                app_ui_standard_button()
                    .append(
                        app_ui_clickableLink(
                            "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                + "&spacemr_space_child_id="+content.spacemr_space.spacemr_space_id
                                + "&spacemr_space_map_id="+content.spacemr_space.spacemr_space_in_map_id_default
                                + "&app_spacemr_space_people_book_map_current_date="+ccd.getTime()
                                + "&app_spacemr_space_people_view_mode=presences")
                            .text(gRb("db.spacemr_space_map.maps.people"))
                            .append(" ")
                            .append(app_getIcon("map", 15).css("color","#ff5050") )
                    ) 
            );
    }
    
    if (app_userHasPermission("db_spacemr_space_user_presence_qrcode") && content.spacemr_space != undefined) {
        topline
            .append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.sys.qrcode"))
                    .click(function(event) {
                        if ($("#qrcode").html() == "") {
                            var a = (""+window.location).substring(0,(""+window.location).indexOf("?page="));
			    var lsi = a+"?page=s__prsncdd&sp="+content.spacemr_space.spacemr_space_id+"";
                            var qrcode = new QRCode("qrcode");
                            qrcode.makeCode(lsi);
                            //-
                            let da=function(s) {
                                $("#qrcode").append($("<div>").append(s));
                            }
                            da(gRb("db.spacemr_space_user_presence..single"));
                            da((content.spacemr_space.code
                                + " - " + content.spacemr_space.description
                               ));
                            da(gRb("db.spacemr_space.number_of_seating") 
                               + ": " + content.spacemr_space.number_of_seating
                              );
                            da(gRb("db.spacemr_space.number_of_seating_booking") 
                               + ": " + content.spacemr_space.number_of_seating_booking
                              );
                            da(("" + lsi));
                            ;
                        } else {
                            $("#qrcode").html("");
                        }
                    })
            )
            .append(" ").append($("<span>").attr("id", "qrcode"))
        ;
    }
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_space_user_presence_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_space_user_presence_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_space_user_presence.insert.title"));
    //-
    topline.append(alert_div);
    setTimeout(function() {
        app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
    }, 20);
    // }
}


function app_spacemr_space_user_presence_form_check_info(alert_div, alert_data) {
    //-
    // console.log(" -- in app_spacemr_space_user_presence_form_check_info");
    let data = {};
    data.app_user_id = $("#id_app_user_id").val();
    data.spacemr_space_id = $("#id_spacemr_space_id").val();
    let d = new Date(appConvertStringToTimestamp($('#id_date_time').val()));
    d = new Date(d.getFullYear(),d.getMonth(), d.getDate(),0,0,0 ).getTime();
    data.current_date = d;
    //-
    let content = alert_data.content;
    //-
    let update_data = false;
    if (content == null) {
        update_data = true;
        let data = {};
    } else {
        ["app_user_id", "spacemr_space_id", "current_date"].forEach(function(i){
            if (alert_data[i] != data[i]){
                update_data = true;
                // console.log(" -- canged: " +i);
                // console.log("   -- alert_data[i] " + alert_data[i]);
                // console.log("   -- data[i] " + data[i]);
            }
        })
    }
    if (update_data) {
        app_spacemr_space_user_presence_form_get_check_info(data, alert_div, alert_data);
    } else {
        let orange_insert = false;
        alert_div.html("");
        let write_message = function(message, bg_color='#ff8') {
            orange_insert = true;
            let m = $("<div>")
                .append($("<span>").css("font-weight", "bold").text(gRb("db.sys.warning")))
                .append(" ")
                .append(message)
                .css("background-color", bg_color )
            ;
            alert_div.append(m);
        }
        if (content.user_presence_count > 0) {
            write_message(gRb("db.spacemr_space_user_presence.message.presences_this_user"));
        }
        let people_number = parseInt($("#id_people_number").val());
        let local_user_can_insert = true;
        if (content.spacemr_space_ancestors_with_presence) {
            content.spacemr_space_ancestors_with_presence.forEach(function(sp){
                if (sp.number_of_seating_booking != null) {
                    // console.log(" sp.presents: " + sp.presents);
                    // console.log(" people_number: " + people_number);
                    // console.log(" sp.number_of_seating_booking: " + sp.number_of_seating_booking);
                    if (parseInt(sp.presents) + people_number > sp.number_of_seating_booking ) {
                        let questo_spazio = "";
                        if (sp.spacemr_space_id == data.spacemr_space_id) {
                            questo_spazio = " ("+gRb("db.spacemr_space.this_space")+") "
                        }
                        var ex = (parseInt(sp.presents) + people_number - sp.number_of_seating_booking)
                        let m = gRb("db.spacemr_space_user_presence.message.exceeds")
                            + " "
                            + sp.code + questo_spazio
                            + " "
                            + ex
                            + " "
                            + gRb("db.spacemr_space_user_presence.." + (
                                (ex > 1) ? "plural" : "single"                    ))
                        ;
                        write_message(m);
                        if (!app_userHasPermission("db_spacemr_space_user_presence_insert_over_booking")) {
                            local_user_can_insert = false;
                        }
                    }
                }
            })
        }
        if(orange_insert) {
            // console.log(" -- orange_insert ");
            $("button:contains('"+gRb("db.sys.doInsert")+"')")
                // .parent()
                .attr("style" , "background-color: orange !important")
                .css("color", "red")
                .css("font-weight", "bold")
            ;
        } else {
            $("button:contains('"+gRb("db.sys.doInsert")+"')")
                .attr("style" , "")
            ;
        }
        if (!(content.user_can_insert && local_user_can_insert)) {
            write_message(gRb("db.spacemr_space_user_presence.message.user_can_not_insert_a_presences"));
            $("button:contains('"+gRb("db.sys.doInsert")+"')").hide("fast");
	    app_setMessage(content.user_can_insert_reason);
        }
        // alert_div.append($("<pre>").text(app_JSONStringify(content)))
        // alert_div.append($("<div>").append("-- updating output"));
    }
}

function app_spacemr_space_user_presence_form_get_check_info(data, alert_div, alert_data) {
    //-
    var spacemr_space_id  = getLocationParameterByName('spacemr_space_id');
    app_doRequestMappingRequest("spacemr_space_user_presence/spacemr_space_user_presence_get_check_info"
                                , data
                                , function(content) {
                                    alert_data.app_user_id      = data.app_user_id;
                                    alert_data.spacemr_space_id = data.spacemr_space_id;
                                    alert_data.current_date     = data.current_date;
                                    alert_data.content = content;
                                    app_spacemr_space_user_presence_form_check_info(alert_div, alert_data);
                                });
}

//-
function app_spacemr_space_user_presence_form_insert_validate() {
    var rv = $('<ul/>');
    (sys_number_integer_validation.test($('#id_app_user_id').val())
     && ($('#id_app_user_id').val() != ""))
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_user_presence.please_select_app_user_id") + " - " + gRb("db.sys.invalidValue"))));
    ($('#id_spacemr_space_id').val() != "" && sys_number_integer_validation.test($('#id_spacemr_space_id').val()))
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people.spacemr_space_code") + " - " + gRb("db.sys.invalidValue") + ", " + gRb("db.sys.please.search.and.select"))));
    sys_timestampFormatUi_validation.test($('#id_date_time').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_user_presence.date_time") + " - " + gRb("db.sys.invalidValue") )));
    if (app_userHasPermission("db_spacemr_space_user_presence_insert_choose_people_number")) {
        sys_number_integer_validation.test($('#id_people_number').val()) 
            || (rv.append($("<li/>").text(gRb("db.spacemr_space_user_presence.people_number") + " - " + gRb("db.sys.invalidValue"))));
    }
    return rv;
}
//-
function app_spacemr_space_user_presence_form_insert_sendData() {
    var data =  { 
         app_user_id:   $('#id_app_user_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , date_time:   appConvertStringToTimestamp($('#id_date_time').val())
         , people_number:   $('#id_people_number').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_space_user_presence/spacemr_space_user_presence_insert"
                                , data
                                , function(content) {
                                    var spacemr_space_user_presence_id = content.spacemr_space_user_presence_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_update"
                                            + "&spacemr_space_user_presence_id="+spacemr_space_user_presence_id);
                                    app_setMessage(gRb("db.spacemr_space_user_presence.added"));
                                });
    //-
}

//-
function app_spacemr_space_user_presence_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space_user_presence.list.title"));
    //-
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    if ( spacemr_space_id == "") spacemr_space_id = undefined;
    var app_user_id = getLocationParameterByName('app_user_id');
    if ( app_user_id == "") app_user_id = undefined;
    var app_user_user_name = getLocationParameterByName('app_user_user_name');
    if ( app_user_user_name == "") app_user_user_name = undefined;
    //-
    var page    = $("<div>");
    if ( spacemr_space_id != undefined) {
        page.append(app_spacemr_space_tabs_get(spacemr_space_id));
    }
    var tableid = "table_spacemr_space_user_presence_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_update"
                                 + "&spacemr_space_user_presence_id="+row[mapIdName["spacemr_space_user_presence_id"]]
                                )
            .attr('title',gRb("db.spacemr_space_user_presence.update.title"))
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(spacemr_space_user_presence_super)
                   // .append(spacemr_space_user_presence_sub)
                   .append(
                       app_userHasPermission("db_spacemr_space_read") ? (
                           app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update"
                                                + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                               )
                               .attr('title',gRb("db.spacemr_space..single"))
                               .append(app_getIcon("home", 15))
                       ) : ""
                   )
                   .append(
                       app_userHasPermission("db_app_user_read") ? (
                           app_ui_clickableLink("?page=app_app_user__app_app_user_form_update"
                                                + "&app_user_id="+row[mapIdName["app_user_id"]]
                                               )
                               .attr('title',gRb("db.app_user..single"))
                               .append(app_getIcon("user", 15))
                       ) : ""
                   )
                   .append(
                       app_userHasPermission("db_spacemr_space_user_presence_read")
                           && app_userHasPermission("db_spacemr_space_read") ? (
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                    + "&spacemr_space_child_id="+row[mapIdName["spacemr_space_id"]]
                                                    + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_in_map_id_default"]]
                                                    // + "&app_spacemr_space_people_view_mode=bookings"
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
                                , controller: "spacemr_space_user_presence/spacemr_space_user_presence_list"
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
            if (tableMattoni_options.content.qparams.where.from_today) {
                delete tableMattoni_options.content.qparams.where['from_today'];
                let d = new Date();
                tableMattoni_options.content.qparams.where.date_time__from =
                    new Date(d.getFullYear(),d.getMonth(), d.getDate(),0,0,0 ).getTime();
            }
            if (tableMattoni_options.content.qparams.where.this_user_name) {
                //console.log("this_user_name! " + sys_session.userData.user_name);
                if (sys_session.userData.user_name != 'anonymous'){
                    tableMattoni_options.content.qparams.where.app_user_user_name =
                        sys_session.userData.user_name;
                    tableMattoni_options.content.qparams.where._user_name = false;
                }
            }
        }
    }
    // -- if this is the "list" of detail of the "super" master, add the super in the where clause
    // var spacemr_space_user_presence_super_id = getLocationParameterByName('spacemr_space_user_presence_super_id');
    // if (spacemr_space_user_presence_super_id != "") {
    //     app_doRequestMappingRequest("spacemr_space_user_presence_super/spacemr_space_user_presence_super_get"
    //                                 , { spacemr_space_user_presence_super_id: spacemr_space_user_presence_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_user_presence_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_user_presence_super_tabs(spacemr_space_user_presence_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.spacemr_space_user_presence_super_id = spacemr_space_user_presence_super_id;
    // };
    //-
    if ( spacemr_space_id != undefined) {
        tableMattoni_options.content.qparams.where.spacemr_space_id = spacemr_space_id;
        tableMattoni_options.content.qparams.where.recursive_space = true;
    }
    if ( app_user_id != undefined) {
        tableMattoni_options.content.qparams.where.app_user_id = app_user_id;
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
        if (app_userHasPermission("db_spacemr_space_user_presence_insert")) {
            let l = "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_insert";
            if ( spacemr_space_id != undefined) {
                l = l + "&spacemr_space_id="+spacemr_space_id
            }
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space_user_presence.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage(l)
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
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_user_presence.spacemr_space_code"), "spacemr_space_code", divTable);
        if (app_userHasPermission("db_spacemr_space_user_presence_admin")
            || app_userHasPermission("db_spacemr_space_user_presence_read_all") ) {
            app_where_append_string(fieldcontain, gRb("db.spacemr_space_user_presence.app_user_user_name"), "app_user_user_name", divTable);
            app_where_append_string(fieldcontain, gRb("db.spacemr_space_user_presence.app_user_first_name"), "app_user_first_name", divTable);
            app_where_append_string(fieldcontain, gRb("db.spacemr_space_user_presence.app_user_last_name"), "app_user_last_name", divTable);
            app_where_append_checkbox(fieldcontain,   gRb("db.spacemr_space_user_presence.this_user_name"), "this_user_name", divTable);
        }
        //-
        app_where_append_timestamp(fieldcontain, gRb("db.spacemr_space_user_presence.date_time"), "date_time", divTable);
        app_where_append_integer(fieldcontain, gRb("db.spacemr_space_user_presence.people_number"), "people_number", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_user_presence.nota"), "nota", divTable);
        app_where_append_date_single(fieldcontain, gRb("db.spacemr_space_user_presence.current_date"), "current_date", divTable);
        //-
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
                    + "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list"
                    + "&qparams="+qp
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
    appSetPage(page, gRb("db.spacemr_space_user_presence.list.title"));
    //-
}
//-
function app_spacemr_space_user_presence_tabs(spacemr_space_user_presence) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_spacemr_space_user_presence_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_update&spacemr_space_user_presence_id="+spacemr_space_user_presence.spacemr_space_user_presence_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_user_presence..single") + ": "
                      + spacemr_space_user_presence.app_user_user_name
                      + " - "
                      + spacemr_space_user_presence.spacemr_space_code)
        );
    }
    if (app_userHasPermission("db_app_user_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_app_user__app_app_user_form_update&app_user_id="+spacemr_space_user_presence.app_user_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_user..single") + ": "
                      + spacemr_space_user_presence.app_user_user_name)
        );
    }
    // if (app_userHasPermission("db_spacemr_space_user_presence_sub_read")) {
    //     var ls = "?page=app_spacemr_space_user_presence_sub__app_spacemr_space_user_presence_sub_list&spacemr_space_user_presence_id="+spacemr_space_user_presence.spacemr_space_user_presence_id+"";
    //     rv.append(
    //         app_ui_clickableLink(ls)
    //             .attr("class", "w3-bar-item w3-button")
    //             .text(gRb("db.spacemr_space_user_presence_sub.list.title"))
    //     );
    // }
    return(rv);
}
//-
function app_spacemr_space_user_presence_form_update() {
    var spacemr_space_user_presence_id = getLocationParameterByName('spacemr_space_user_presence_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space_user_presence.update.title"));
    //-
    app_doRequestMappingRequest("spacemr_space_user_presence/spacemr_space_user_presence_get"
                                , { spacemr_space_user_presence_id: spacemr_space_user_presence_id}
                                , function(content) {
                                    app_spacemr_space_user_presence_form_update_data(content);
                                });
}

//-
var app_spacemr_space_user_presence_form_update_old_data = undefined;
//-
function app_spacemr_space_user_presence_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    if (app_userHasPermission("db_spacemr_space_read")) {
        page.append(app_spacemr_space_tabs_get(obj.spacemr_space_id));
    }
    page.append(app_spacemr_space_user_presence_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    var topline = app_ui_standard_appendRow(grid);
    var pass_icon_imageContainer =  app_ui_standard_appendRow(grid);
    //-
    //- logs
    var logs = $("<div>"); grid.append(logs);
    //-
    page.append(form);
    //-
    var setReadOnly = function(id){
        grid.find("input[id='"+id+"']")
            .prop('readonly', 'readonly')
            .css('background-color', "lightgray")
        ;
    }
    //-
    var updateFunction = function(event) {
        var onSuccess = function() {
            if (logTable != undefined ) {
                setTimeout(function() {
                    logTable.tableMattoni().render();
                }, 1000);
            }
        }
        app_spacemr_space_user_presence_form_update_doUpdate(onSuccess);
    };
    //-
    
    let pass_icon_toggler = function() {
        if(pass_icon_imageContainer.html() == "") {
            let icons = [ "bars", "calendar_alt", "check", "circle", "circle_empty", "cut", "database", "home", "lens_search", "map_black", "plus-square", "save", "undo", "user_group", "trash_can", "user", "table"
            ];
            let colors = ["#ffb4b4", "#fff3b4", "#cbffb4", "#b4fffd", "#b4c7ff", "#ddb4ff", "#ffb4e7"];
            var w = window.innerWidth;
            if (window.innerHeight < w ) {
                w = window.innerHeight;
            }
            w = w * 0.9;
            let getRandomInt = function(max) {
                return Math.floor(Math.random() * Math.floor(max));
            }
	    let day = null;
	    {
		//- must be normalized because of the  time zone and 	daylight saving time
		// console.log("$('#id_date_time').val(): " + $('#id_date_time').val());
		let t  = appConvertStringToTimestamp($('#id_date_time').val());
		// console.log("t: " + t);
		let ts = appConvertTimestampToString(t).substring(0,10);
		// console.log("ts: " + ts);
		day =
		    Math.floor(appConvertStringToTimestamp(ts) / (1000 * 60 * 60 * 24));
	    }
            // console.log(" -- day: " + day);
            let i_icon  = icons[day % icons.length];
            let i_color = colors[day % colors.length];
            var svg =
                app_getIcon(i_icon, w)
                .width(w)
                .css("color",i_color)
            ;
            pass_icon_imageContainer
                .html(svg)
                .css("text-align","center")
            ;
            let textd = $("<div>").hide();
            setTimeout((function() {
                let p = svg.position();
                textd
                    .addClass("w3-theme-dark-color")
                    .append($("<div>").append($('#id_app_user_first_name').val()
                                              + " " + $('#id_app_user_last_name').val()))
                    .append($("<div>").append($('#id_spacemr_space_code').val()))
                    .append($("<div>").append(gRb("db.spacemr_space_user_presence.space_day_numeration.short") +": " + obj.space_day_numeration))
                ;
                var f = function(){
                    var o = content.spacemr_space.user_presence_progressive_code_map;
                    if (!o ) {
                        return;
                    }
                    if ((o.trim() == "")) {
                        return;
                    }
                    try {
                        o = JSON.parse(o);
                        o = o.user_presence_progressive_code_map;
                        o = o[obj.space_day_numeration - 1];
                        if (o) {
                            textd
                                .append($("<div>").append(gRb("db.spacemr_space.user_presence_progressive_code_map..short") +": " + o))
                        }
                    } catch (error) {
                        return;
                    }
                }
                f();
                //-
                textd
                    .css("position", "absolute")
                    .css("display", "block")
                    .css("left", p.left + 'px')
                    .css("top",  (p.top + (w/20) ) + 'px')
                    .css("width", w+'px')
                    .css("heigth", w+'px')
                    // .css("border","black solid 1px")
                    .css("font-size","10vw")
                    .css("text-align","center")
                    // .css("font-weight","bold")
                ;
                textd.show();
            })
                       ,  0.2 );
            pass_icon_imageContainer.append(textd);
        } else {
            pass_icon_imageContainer.html("");
        }
    };
    
    
    
    // var topbuttonline = app_ui_standard_appendRow(grid);
    // if (app_userHasPermission("db_spacemr_space_user_presence_update")) {
    //     topbuttonline.append(" ").append(
    //         app_ui_standard_button()
    //             .text(gRb("db.sys.update"))
    //             .click(updateFunction)
    //     );
    // }
    //-
    // app_ui_standard_appendFieldHidden(form, "id_spacemr_space_user_presence_id", "spacemr_space_user_presence_id", "label", obj.spacemr_space_user_presence_id);
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_user_presence_id", "spacemr_space_user_presence_id", "label", obj.spacemr_space_user_presence_id);
    //-
    app_ui_standard_appendFieldText(grid, "id_app_user_user_name", "app_user_user_name", gRb("db.app_user.user_name"),obj.app_user_user_name);
    app_ui_standard_appendFieldText(grid, "id_app_user_first_name", "app_user_first_name", gRb("db.app_user.first_name"),obj.app_user_first_name);
    app_ui_standard_appendFieldText(grid, "id_app_user_last_name", "app_user_last_name", gRb("db.app_user.last_name"),obj.app_user_last_name);
    app_ui_standard_appendFieldHidden(grid, "id_app_user_id", "app_user_id", "hidden label for app_user_id",obj.app_user_id) ;
    // app_ui_standard_appendFieldInteger(grid, "id_app_user_id", "app_user_id", gRb("db.spacemr_space_user_presence.app_user_id"),"");
    //-
    //-
    if (app_userHasPermission("db_spacemr_space_user_presence_admin")
       ) {
        app_app_user_ui_standard_appendSearch_user(grid
                                                   , "id_app_user_user_name"
                                                   , "id_app_user_first_name"
                                                   , "id_app_user_last_name"
                                                   , "id_app_user_id"
                                                  );
    }
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),obj.spacemr_space_code);
    //-
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", "hidden space",obj.spacemr_space_id);
    if (app_userHasPermission("db_spacemr_space_user_presence_admin")){
        app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                              , "id_spacemr_space_code"
                                                              , "id_spacemr_space_id"
                                                             );
    }    
    app_ui_standard_appendFieldTimestamp(grid, "id_date_time", "date_time", gRb("db.spacemr_space_user_presence.date_time"),appConvertTimestampToString(obj.date_time));
    app_ui_standard_appendFieldInteger(grid, "id_people_number", "people_number", gRb("db.spacemr_space_user_presence.people_number"),obj.people_number);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_user_presence.nota"),obj.nota);
    //-
    if (!app_userHasPermission("db_spacemr_space_user_presence_admin")) {
        ["id_app_user_user_name"
         , "id_app_user_first_name"
         , "id_app_user_last_name"
         , "id_spacemr_space_code"
         , "id_date_time"
         , "id_people_number"
        ].forEach(function(i){
            setReadOnly(i);
        })
    }
    //-
    if (app_userHasPermission("db_spacemr_space_user_presence_update")) {
        topline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    {
        topline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.spacemr_space_user_presence.pass_icon"))
                .click(pass_icon_toggler)
        );
    }
    if (app_userHasPermission("db_spacemr_space_user_presence_read")
        && app_userHasPermission("db_spacemr_space_read")
       ) {
        topline
            .append(" ")
            .append(
                app_ui_standard_button()
                    .append(
                        app_ui_clickableLink(
                            "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_calendar"
                                + "&spacemr_space_id="+obj.spacemr_space_id )
                            .text(gRb("db.spacemr_space_user_presence.calendar"))
                            .append(" ")
                            .append(app_getIcon("calendar_alt", 15).css("color","#ff5050") )
                    ) 
            );
    }
    if (app_userHasPermission("db_spacemr_space_read")) {
        let ccd = new Date();
        ccd = new Date(ccd.getFullYear()
                           ,ccd.getMonth()
                           ,ccd.getDate()
                           ,0,0,0
                      );
        topline
            .append(" ")
            .append(
                app_ui_standard_button()
                    .append(
                        app_ui_clickableLink(
                            "?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                + "&spacemr_space_child_id="+obj.spacemr_space_id
                                + "&spacemr_space_map_id="+obj.spacemr_space_in_map_id_default
                                + "&app_spacemr_space_people_book_map_current_date="+ccd.getTime()
                                + "&app_spacemr_space_people_view_mode=presences")
                            .text(gRb("db.spacemr_space_map.maps.people"))
                            .append(" ")
                            .append(app_getIcon("map", 15).css("color","#ff5050") )
                    ) 
            );
    }
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_user_presence_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    if (app_userHasPermission("db_spacemr_space_user_presence_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            spacemr_space_user_presence_id:   $('#id_spacemr_space_user_presence_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_space_user_presence/spacemr_space_user_presence_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_user_presence_logs")) {
        topline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_space_user_presence_logs"
                                        , controller: "spacemr_space_user_presence/spacemr_space_user_presence_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_space_user_presence_id: obj.spacemr_space_user_presence_id
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
    appSetPage(page, gRb("db.spacemr_space_user_presence.update.title") + " " + obj.spacemr_space_user_presence_id);
    //-
    app_spacemr_space_user_presence_form_update_old_data = app_spacemr_space_user_presence_form_update_getData();
    //-
    if (!app_userHasPermission("db_spacemr_space_user_presence_hide_pass_icon")) {
        pass_icon_toggler();
    }
}

//-
function app_spacemr_space_user_presence_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_space_user_presence_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_space_user_presence_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_space_user_presence_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_space_user_presence_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("spacemr_space_user_presence/spacemr_space_user_presence_update"
                                        , data
                                        , function(content) {
                                            app_spacemr_space_user_presence_form_update_old_data = app_spacemr_space_user_presence_form_update_getData();
                                            app_setMessage(gRb("db.spacemr_space_user_presence.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_space_user_presence_form_update_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_app_user_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_user_presence.app_user_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_space_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_user_presence.spacemr_space_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_timestampFormatUi_validation.test($('#id_date_time').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_user_presence.date_time") + " - " + gRb("db.sys.invalidValue") )));
    sys_number_integer_validation.test($('#id_people_number').val()) 
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_user_presence.people_number") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_spacemr_space_user_presence_form_update_getData() {
    var data =  { 
         spacemr_space_user_presence_id:   $('#id_spacemr_space_user_presence_id').val()
         , app_user_id:   $('#id_app_user_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , date_time:   appConvertStringToTimestamp($('#id_date_time').val())
         , people_number:   $('#id_people_number').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}


function app_spacemr_space_user_presences_getVerifier() {
    //- presence_verifier given
    //-   a tree of spaces
    //-   and a set of presences on such spaces
    //- helps to recursively
    //    compute the occupation of such spaces
    //-   compare the number_of_seating_presence with the computed occupation.
    let rv = {
        init: function(content) {
            //- content has the format
            //-
            let wid = this;
            //-
            let spacemr_space_user_presences = content.spacemr_space_user_presences;
            //-
            //- indexing spaces
            //-
            {
                let result_set = content.spacemr_spaces;
                let headers = result_set.headers;
                let rows    = result_set.rows;
                let objects={};
                for (let ir = 0; ir < rows.length; ir++) {
                    let obj = {};
                    let row = rows[ir];
                    for (let ih = 0; ih < headers.length; ih++) {
                        obj[headers[ih].name] = row[ih];
                        obj.childs = [];
                    }
                    objects[obj.spacemr_space_id] = obj;
                }
                //-
                wid.spaces = objects;
                // console.log(app_JSONStringify(wid.spaces));
                //-
                //- looking for root, the object who have no parent
                //-
                var keys = Object.keys(objects);
                for (let i = 0; i < keys.length; i++) {
                    let obj = objects[keys[i]];
                    if (objects[obj.spacemr_space_in_id] == undefined) {
                        // console.log(" --- found root: " + app_JSONStringify(obj));
                        wid.space_root = obj;
                    } else {
                        objects[obj.spacemr_space_in_id].childs.push(obj);
                    }
                }
            }
            // console.log(" -- the tree:\n" + app_JSONStringify(wid.space_root));
            //-
            //- processing presences
            //-
            {
                let result_set = content.spacemr_space_user_presences;
                let headers = result_set.headers;
                let rows    = result_set.rows;
                let objects=[];
                for (let ir = 0; ir < rows.length; ir++) {
                    let obj = {};
                    let row = rows[ir];
                    for (let ih = 0; ih < headers.length; ih++) {
                        obj[headers[ih].name] = row[ih];
                    }
                    // obj.date_from = new Date(obj.date_from);
                    // obj.date_to   = new Date(obj.date_to);
                    obj.day = new Date(obj.date_from).getDay();
                    obj.ids = obj.ids.split(",");
                    objects.push(obj);
                }
                //-
                wid.presences = objects;
                // console.log(" --- all presences:\n" + app_JSONStringify(wid.presences));
            }
            // console.log(" -- verifier is initialized");
        }
        , compute_occupation(current_date) {
            let wid = this;
            let ccd_day   = current_date.getDay();
            let ccd_time      = current_date.getTime();
            let ccd_time_end  = current_date.getTime() + (24 * 60 * 60 * 1000) - 1000;
            let spaces    = wid.spaces;
            let presences = wid.presences;
            // console.log(" --- all presences length:" + presences.length);
            // console.log(" --- all presences2:\n" + app_JSONStringify(presences));
            //-
            //- clean data structure
            //-
            var keys = Object.keys(spaces);
            for (let i = 0; i < keys.length; i++) {
                let space = spaces[keys[i]];
                space.presences       = [];
                space.presences_count = 0;
                space.exceeds = false;
            }
            //-
            // console.log(" -- in compute_occupation computing leaf presences");
            //-
            for (let i = 0; i < presences.length; i++) {
                let row = presences[i];
                // console.log(" -- ccd_time: "+ccd_time+"\nprocessing presence: " + app_JSONStringify(row));
                if (row.date_time >= ccd_time && row.date_time <= ccd_time_end ) {
                    let space = spaces[row.spacemr_space_id];
                    // console.log(" -- ccd_time: "+ccd_time+"\nadding  presence: " + app_JSONStringify(row));
                    space.presences.push(row);
                    space.presences_count = space.presences_count + row.people_number;
                }
            }
            //-
            // console.log(" -- in compute_occupation roll_up recursively");
            //-
            let collect_tree = function(obj) {
                let sum = obj.presences_count;
                // console.log(" -- in collect_tree for " + obj.code);
                for (let i = 0; i < obj.childs.length; i++) {
                    let child = obj.childs[i];
                    // console.log(" -- child for " + obj.code + ": " + child.code);
                    collect_tree(child);
                    sum = sum + child.presences_count;
                    obj.presences = obj.presences.concat(child.presences);
                    if (child.exceeds) {
                        obj.exceeds = true;
                    };
                }
                obj.presences_count = sum;
                //-
                if (obj.number_of_seating_booking != null
                    && obj.presences_count > obj.number_of_seating_booking) {
                    obj.exceeds = true;
                }
                // console.log(" -- obj.exceeds: " + obj.exceeds + " count: "+obj.presences_count+" seats: " + obj.number_of_seating_booking );
            }
            //-
            collect_tree(wid.space_root);
            // console.log(app_JSONStringify(wid.space_root));
        }


        , tooltip_presences(element, spacemr_space_id, stato, refresh_callback, ccd, menu_function) {
            let wid = this;
            let obj = wid.spaces[spacemr_space_id];
            // console.log(" -- obj: ", app_JSONStringify(obj));
            // console.log(" -- presences: ", app_JSONStringify(obj));
            let spacemr_space_user_presence_ids = [];
            let presences = obj.presences;
            for (let i = 0; i < presences.length; i++) {
                let presence = presences[i];
                // console.log(" --- ", app_JSONStringify(presence));
                if ("present" == stato) {
                    spacemr_space_user_presence_ids = spacemr_space_user_presence_ids.concat(presence.ids);
                }
            }
            // console.log("spacemr_space_user_presence_ids", spacemr_space_user_presence_ids);
            app_tooltip_set_click(element, function(evt, tooltip_div){
                //-
                //- this a popup is like
                //-   app_spacemr_space_user_presence_list
                //-
                //- start tooltip_generation
                //-
                let page = tooltip_div;
                var tableid = "table_spacemr_space_user_presence_list_tooltip";
                //-
                let ccd_time = ccd.getTime();
                var buttonsCell = function(tr, mapIdName, row) {
                    //-
                    tr.prepend($('<td>')
                               .append(
                                   app_userHasPermission("db_spacemr_space_user_presence_read") ? (
                                       app_ui_clickableLink("?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_calendar"
                                                            + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                                           )
                                           .attr('title',gRb("db.spacemr_space_user_presence.calendar"))
                                           .append(
                                               app_getIcon("calendar_alt", 15).css("color","#ff5050")
                                           )
                                   ) : ""
                               )
                               .append(
                                   app_ui_clickableLink("?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_form_update"
                                                        + "&spacemr_space_user_presence_id="+row[mapIdName["spacemr_space_user_presence_id"]]
                                                       )
                                       .append(
                                           app_getIcon("edit", 15).css("color", "green")
                                       )
                               )
                               .append(
                                   app_userHasPermission("db_spacemr_space_user_presence_read")
                                       && app_userHasPermission("db_spacemr_space_read") ? (
                                           app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                                + "&spacemr_space_child_id="+row[mapIdName["spacemr_space_id"]]
                                                                + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_in_map_id_default"]]
                                                                + "&app_spacemr_space_people_book_map_current_date="+ccd_time
                                                                + "&app_spacemr_space_people_view_mode=presences"
                                                               )
                                               .attr('title',gRb("db.spacemr_space_map.maps.people"))
                                               .append(
                                                   app_getIcon("map", 15)
                                               )
                                       ) : ""
                               )
                              );
                }
                var tableMattoni_options = {tableid: tableid
                                            , controller: "spacemr_space_user_presence/spacemr_space_user_presence_list"
                                            , buttonsCell: buttonsCell
                                            , content: {
                                                qparams: { }
                                            }
                                            , enable_csv_output: true
                                            , render_post_hooks: [
                                                function(){
                                                    app_tooltip_center();
                                                }
                                            ]
                                           };
                tableMattoni_options.content.qparams.where = {
                    spacemr_space_user_presence_ids: spacemr_space_user_presence_ids
                };
                //-
                //-
                //-
                //- the div containing the table
                var divTable=$("<div>");
                appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
                //-
                var form = $('<form name="fm">');
		if (menu_function != null) {
                    let grid = app_ui_standard_getGrid(form);
                    var buttonline = app_ui_standard_appendRow(grid);
		    menu_function(buttonline);
		}
                page.append(form);
                page.append(divTable);
                //-
                //- end tooltip_generation
                //-
            });
        }
        
    }
    return(rv);
}


//-
function app_spacemr_space_user_presence_form_calendar() {
    //-
    //-
    var spacemr_space_id  = getLocationParameterByName('spacemr_space_id');
    var app_user_id = getLocationParameterByName('app_user_id');
    var spacemr_responsible_id = getLocationParameterByName('spacemr_responsible_id');
    //-
    var page    = $("<div>");
    //-
    //-
    //-
    //-
    //-
    //- defining the object managing the calendar
    //-
    //-
    //-
    let calendar_manager = app_spacemr_space_people_book_calendar_manager_basic();
    //-
    //- customizing the calendar_manager
    //-
    {
        calendar_manager.spacemr_space_id = spacemr_space_id;   // current space
        calendar_manager.get_calendar_data = function() {
            let wid = this;
            wid.map_id_default = undefined;
            if (app_userHasPermission("db_spacemr_space_map_read")
                && (wid.spacemr_space != undefined )
                && (wid.spacemr_space_map_id_default !== null )) {
                wid.map_id_default = wid.spacemr_space.spacemr_space_map_id_default;
                if (wid.map_id_default == undefined ) {
                    wid.map_id_default = wid.spacemr_space.spacemr_space_in_map_id_default;
                }
            }
            //-
            wid.presences_verifier = app_spacemr_space_user_presences_getVerifier();
            //-
            app_doRequestMappingRequest("spacemr_space_user_presence/spacemr_space_user_presence_list_sitting"
                                        , { "spacemr_space_id": wid.spacemr_space_id
                                            , "ccd_initial":    wid.ccd_initial.getTime()
                                            , "ccd_final":      wid.ccd_final.getTime() + wid.oneDayInMs - 1000
                                          }
                                        , function(content) {
                                            // console.log(" -- spacemr_space_user_presence_list_sitting");
                                            // console.log(app_JSONStringify(content));
                                            wid.presences_verifier.init(content);
                                            wid.render_data();
                                        });
            //-
            //- days of the month rendering
            //-
        };
        calendar_manager.render_data_single_day = function(d, ccd, ccd_date, ccd_time, ccd_day){
            let wid = this;
            //-
            wid.presences_verifier.compute_occupation(ccd);
            //-
            let qp = {
                "where":{"spacemr_space_id": wid.spacemr_space_id
                         , "current_date":    ccd_time
                         , "recursive_space": true
                        }
                ,"order": [ { "column": "stato" } ]
                ,"tableid":"spacemr_space_user_presence_list"}
            ;
            d.append(
                app_ui_clickableLink("?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list"
                                     + "&spacemr_space_id="+wid.spacemr_space_id
                                     + "&qparams="+encodeURI(app_JSONStringify(qp))
                                    )
                    .append(ccd_date)
            );
            //-
            //-
            //-
            let dc = $('<div>');
            // dc.css('background-color', "white")
            dc.css('margin', "2px")
            d.append(dc);
            // console.log(" -- " + appConvertDateToString(ccd_time));
            //-
            // dc.append("here!");
            let statusIndex = {};
            let add_statusIndex = function (name, people_number){
                let value = statusIndex[name];
                if ( value == undefined) {
                    value = people_number;
                } else {
                    value = value + people_number;
                }
                statusIndex[name] = value;
            }
            let presences = wid.presences_verifier.space_root.presences;
            jQuery.each(presences, function(presencei, presence){
                add_statusIndex("present", presence.people_number);
            });
            // console.log(" -- presences: " + app_JSONStringify(presences));
            // console.log(" -- root: " + app_JSONStringify(wid.presences_verifier.space_root));
            // console.log(" -- statusIndex: " + app_JSONStringify(statusIndex));
            jQuery.each(["present"], function(statoi, stato){
                let n = statusIndex[stato];
                // console.log(" -- stato: " + app_JSONStringify(statusIndex));
                if (n != undefined) {
                    let icon =app_getIcon("circle", 15).css("color", "green");
                    if (wid.map_id_default!=undefined){
			menu_function = function(tooltip_panel){
			    let menu = $("<div>");
			    tooltip_panel.append(menu);
			    menu.append(
				app_ui_standard_button()
				    .append(
					app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
							     + "&spacemr_space_map_id="+wid.map_id_default
							     + "&app_spacemr_space_people_book_map_current_date="+ccd_time
							     + "&app_spacemr_space_people_view_mode=presences"
							    )
					    .append(gRb("db.spacemr_space_map.show.on.map"))
					    .append(" ")
					    .append(app_getIcon("map_black", 15))
				    )
			    );
			};
                        wid.presences_verifier.tooltip_presences(icon, wid.spacemr_space_id, stato
                                                                 , function() {
                                                                     wid.redraw();
                                                                 }, ccd, menu_function);
                    }
                    dc.append($("<div>")
                              .append(icon)
                              .append(
                                  "" + n + (window.innerWidth < 900 ? "" : " - " + gRb("db.spacemr_space_people_book.status.plural."+stato)))
                             )
                    ;
                }
            });
            // if (ccd_date == 15) {
            //     console.log(" --- date: "+ccd_date+"\n --- root: " + app_JSONStringify(wid.presences_verifier.space_root) + "\n exceeds: " + wid.presences_verifier.space_root.exceeds);
            // }
            if (wid.presences_verifier.space_root.exceeds) {
                dc.append($("<div>")
                          .css("background-color","red")
                          .append(gRb("db.spacemr_space_people_book.sys.exceeds"))
                         );
            }
            //-
            //-
        }
    }
    //-
    let do_redraw = true;
    if ( spacemr_space_id != "") {
        do_redraw = false;
        page.append(app_spacemr_space_tabs_get(spacemr_space_id
                                               , function(obj){
                                                   calendar_manager.spacemr_space = obj;
                                                   calendar_manager.redraw();
                                               }));
    }
    if ( app_user_id != "") {
        page.append(app_app_user_tabs_get(app_user_id));
    }
    //-
    let date_grid = $("<div>");
    page.append(date_grid);
    let grid =
        app_ui_standard_getGrid(page);
    let calendar_grid =
        $("<div>")
    ;
    grid.append(calendar_grid);
    calendar_manager.init(date_grid, calendar_grid);
    //-
    if (do_redraw) {
        calendar_manager.redraw();
    }
    //-
    //-
    appSetPage(page, gRb("db.spacemr_space_user_presence.calendar"));
    //-
}


function app_spacemr_space_form_user_presence_qrcode() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space_user_presence.form.qrcode"));
    //-
    var page    = $("<div>");
    //-
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    page.append(app_spacemr_space_tabs_get(spacemr_space_id));
    //-
    var tableid = "table_spacemr_space_list";
    //-
    //- 
    let data=JSON.parse(`{
       "allcolumns": [
         "code",
         "description",
         "area_in_meters2",
         "number_of_seating",
         "number_of_seating_booking",
         "nota",
         "spacemr_space_type_name",
         "app_group_name",
         "spacemr_space_in_code",
         "spacemr_space_contained_spaces_count",
         "spacemr_space_people_count",
         "spacemr_space_people_count_delta",
         "spacemr_space_people_names",
         "spacemr_space_people_json",
         "spacemr_space_people_non_sitting_json",
         "bookings",
         "presences"
       ],
       "pageNumber": 1,
       "pages": 3,
       "labelPrefix": "db.spacemr_space",
       "columns": [
         "code",
         "description",
         "spacemr_space_type_name",
         "area_in_meters2",
         "number_of_seating",
         "number_of_seating_booking"
       ],
       "tableid": "table_spacemr_space_list",
       "pageSize": "500",
       "where": {
         "spacemr_space_in_id": ""
       }
       , "order":[{"column":"code"}]
      }`);
    data.where.spacemr_space_in_id=spacemr_space_id;
    //-
    var tabsDiv = $("<div>");
    // tabsDiv.append($("<pre>").append(app_JSONStringify(data)));
    page.append(tabsDiv);
    //-
    app_doRequestMappingRequest("spacemr_space/spacemr_space_list"
                                , data
                                , function(content) {
                                    // tabsDiv.append($("<pre>").append(app_JSONStringify(content)));
                                    //-
                                    $('body').css('background-color', 'blue !important');
                                    //-
                                    let ri=app_applisttablemapper_get_row_index(content);
                                    var a = (""+window.location)
                                        .substring(0,(""+window.location).indexOf("?page="));
                                    //-
                                    //-
                                    content.list.rows.forEach(function(row){
                                        // tabsDiv.append(
                                        //     $("<pre>")
                                        //         .append("\n -- spacemr_space_id: " + row[ri.spacemr_space_id])
                                        //         .append("\n code: " + row[ri.code])
                                        // )
                                        let qrcode_name = "qrcode_"+row[ri.spacemr_space_id];
                                        let qrcode_div = $("<div>")
                                            .css("display", "inline-block")
                                            .css("border-style", "1px")
                                            .css("width", "320")
                                            .css("height", "350")
                                            .css("text-align", "center")
                                            .css("align", "center")
                                            .css("padding", "10px")
                                            .css("margin", "0px")
                                            .css("vertical-align","middle")
                                        ;
                                        qrcode_div.append($("<div>")
                                                          .attr("id", qrcode_name));
                                        tabsDiv.append(qrcode_div);
                                        //- 
                                        //-
			                var lsi = a+"?page=s__prsncdd&sp="+row[ri.spacemr_space_id]+"";
                                        setTimeout(function() {
                                            var qrcode = new QRCode(qrcode_name);
                                            qrcode.makeCode(lsi);
                                            let da=function(s) {
                                                let rv = $("<div>");
                                                qrcode_div.append(rv.append(s));
                                                return(rv)
                                            }
                                            $("#"+qrcode_name+" img")
                                                .css("margin-left", "auto")
                                                .css("margin-right", "auto")
                                                .css("diplay", "block")
                                            ;
                                            da(gRb("db.spacemr_space_user_presence..single"))
                                                .css("font-weight", "bold");
                                            let s = row[ri.code]
                                                     + " - " + row[ri.description]
                                                     ;
                                            let comma = ", ";
                                            if (row[ri.number_of_seating] != null) {
                                                s = s + comma + gRb("db.spacemr_space.number_of_seating")
                                                    + ": " + row[ri.number_of_seating]
                                                ;
                                                comma=", "
                                            }
                                            if (row[ri.number_of_seating_booking] != null) {
                                                s = s + comma + gRb("db.spacemr_space.number_of_seating_booking") 
                                                    + ": " + row[ri.number_of_seating_booking]
                                                ;
                                                comma=", "
                                            }
                                            da(s);
                                            //-
                                            qrcode_div
                                                .click(function(evt) {
                                                    qrcode_div.hide("slow");
                                                })
                                        }, 20);
                                        //-
                                    });
                                    
                                });
    //-
    //- the div containing the table
    var divTable=$("<div>");
    page.append(divTable);
    //-
    //-
    appSetPage(page, gRb("db.spacemr_space.update.childs.title"));
    //-
}
