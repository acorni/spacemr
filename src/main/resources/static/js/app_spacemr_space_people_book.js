
var app_spacemr_space_people_book_current_people = undefined;
var app_spacemr_space_people_book_last_inserted_data = undefined;
var app_spacemr_space_people_book_list_show_transactions_options = undefined;
var app_spacemr_space_people_book_list_show_transactions_statuses = undefined;
var app_spacemr_space_people_book_calendar_current_date = undefined;

function app_spacemr_space_people_book_doInitialize(callback) {
    log(" -- in spacemr_space_people_book page initialization...");
    var initfunction = function(callback) {
        app_doRequestMappingRequest("spacemr_space_people_book/spacemr_space_people_book_workflows"
                                    , { }
                                    , function(content) {
                                        // console.log(" -- setting workflows");
                                        // console.log(" -- Got workflow: " + app_JSONStringify(content));
                                        var workflows = content.workflows;
                                        var keys = Object.keys(workflows);
                                        for (var i = 0; i < keys.length; i++) {
                                            var k = keys[i];
                                            console.log(" -- new wf: " + k);
                                            app_workflow_set(k, workflows[k]);
                                            // sys_workflows[k] = workflows[k];
                                        }
                                        //-
                                        app_spacemr_space_people_book_current_people =
                                            content.current_people;
                                        //-
                                        if (callback != undefined) {
                                            callback();
                                        }
                                    }
                                   );
    }
    sys_loginhooks.app_spacemr_space_people_book_doInitialize = initfunction;
    initfunction(callback);
    //-
    //- custom headers
    //-
    {
        s = "longstring_spacemr_space_people_book_transactions"
        mapper = {};
        mapper.hidden = false;
        mapper.name   = s;
        mapper.draw   = function(value, tr, mapIdName, row) {
            var rv="";
            if (app_userHasPermission("db_spacemr_space_people_book_admin")
                || ( app_userHasPermission("db_spacemr_space_people_book_update")
                     && app_spacemr_space_people_book_current_people.username == row[mapIdName["responsible_username_hidden"]]
                   )) {
                app_spacemr_space_people_book_list_show_transactions_options = true;
                app_spacemr_space_people_book_list_show_transactions_statuses[row[mapIdName["stato_hidden"]]] = true;
                var wf = app_workflow_get("spacemr_space_people_book_workflow");
                rv = app_list_appendFieldWorkflow("id_stato_"+row[mapIdName["spacemr_space_people_book_id"]], "stato_"+row[mapIdName["spacemr_space_people_book_id"]], "db.spacemr_space_people_book.stato",row[mapIdName["stato_hidden"]],wf);
            }
            return(rv);
        };
        app_appListTable_mappers[s] = mapper;
    }
    {
        s = "string_spacemr_space_people_book_repetition"
        mapper = {};
        mapper.hidden = false;
        mapper.name   = s;
        mapper.draw   = function(value, tr, mapIdName, row) {
            // var rv="Hello " + value;
            if(value == 'd') {
                rv = gRb("db.spacemr_space_people_book.repetition.d");
            } else {
                if (mapIdName["date_from"] == undefined) {
                    rv = gRb("db.spacemr_space_people_book.repetition." + value);
                } else {
                    let day = (new Date(row[mapIdName["date_from"]])).getDay();
                    rv = gRb("db.sys.calendar.weekday."+day);
                }
            }
            return(rv);
        };
        app_appListTable_mappers[s] = mapper;
    }
    {
        s = "longstring_spacemr_space_people_book_links"
        mapper = {};
        mapper.hidden = false;
        mapper.name   = s;
        mapper.draw   = function(value, tr, mapIdName, row) {
            if (value == null) {
                value = "";
                //} else {
                //    if (value.length > 254) {
                //        value = value.substring(0,251)+"...";
                //    }
            }
            value = value.replace(/"spacemr_space_id":"(\d+)"/
                                  , '<a href="#?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id=$1" onclick="app_spacemr_icr_space_onclick($1)">"spacemr_space_id":"$1"</a>'
                                 );
            value = value.replace(/"spacemr_space_id":(\d+)/
                                  , '<a href="#?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id=$1" onclick="app_spacemr_icr_space_onclick($1)">"spacemr_space_id":$1</a>'
                                 );
            value = value.replace(/"spacemr_people_id":"(\d+)"/
                                  , '<a href="#?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id=$1" onclick="app_spacemr_icr_people_onclick($1)">"spacemr_people_id":"$1"</a>'
                                 );
            value = value.replace(/"spacemr_people_id":(\d+)/
                                  , '<a href="#?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id=$1" onclick="app_spacemr_icr_people_onclick($1)">"spacemr_people_id":$1</a>'
                                 );
            return("" + value);
        };
        app_appListTable_mappers[s] = mapper;
    }
}


function app_spacemr_space_people_book_form_repetition_values() {
    var rv = [];
    [ "d" , "w"].forEach(function(id){
        rv.push({ "value": id
                  , "label": gRb("db.spacemr_space_people_book.repetition."+id)});
    });
    return(rv);
}

//-
function app_spacemr_space_people_book_form_insert() {
    //-
    var spacemr_space_id  = getLocationParameterByName('spacemr_space_id');
    var spacemr_people_id = getLocationParameterByName('spacemr_people_id');
    app_doRequestMappingRequest("spacemr_space_people_book/spacemr_space_people_book_insert_get_data"
                                , {
                                    spacemr_space_id: spacemr_space_id
                                    , spacemr_people_id: spacemr_people_id
                                }
                                , function(content) {
                                    app_spacemr_space_people_book_form_insert_data(content);
                                });
}

function app_spacemr_space_people_book_form_insert_data(content) {
    var form = $('<form name="fm"/>');
    var spacemr_space_id  = getLocationParameterByName('spacemr_space_id');
    var current_date = getLocationParameterByName('current_date');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var spacemr_space_people_book_super_id = getLocationParameterByName('spacemr_space_people_book_super_id');
    // if (spacemr_space_people_book_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("spacemr_space_people_book_super/spacemr_space_people_book_super_get"
    //                                 , { spacemr_space_people_book_super_id: spacemr_space_people_book_super_id}
    //                                 , function(content) {
    //                                     var spacemr_space_people_book_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_spacemr_space_people_book_super_tabs(spacemr_space_people_book_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="spacemr_space_people_book_super_id" id="id_spacemr_space_people_book_super_id" type="hidden" />')
    //                 .val(spacemr_space_people_book_super_id)
    //                );
    //-
    var wf = app_workflow_get("spacemr_space_people_book_workflow");
    if (wf == undefined) {
        // alert("workflow spacemr_space_people_book_workflow undefined");
        app_spacemr_space_people_book_doInitialize(app_spacemr_space_people_book_form_insert);
        return;
    }
    if (content.spacemr_space != undefined) {
        form.append(app_spacemr_space_tabs_get(content.spacemr_space.spacemr_space_id));
    }
    //-
    var current_people = app_spacemr_space_people_book_current_people;
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    var v;
    //-
    var setReadOnly = function(id){
        grid.find("input[id='"+id+"']")
            .prop('readonly', 'readonly')
            .css('background-color', "lightgray")
        ;
    }
    //-
    //-
    v="";
    v = current_people.username;
    app_ui_standard_appendFieldText(grid, "id_spacemr_responsible_username", "spacemr_responsible_username", gRb("db.spacemr_space_people_book.spacemr_responsible_username"),v);
    v="";
    v = current_people.first_name;
    app_ui_standard_appendFieldText(grid, "id_spacemr_responsible_first_name", "spacemr_responsible_first_name", gRb("db.spacemr_space_people_book.spacemr_responsible_first_name"),v);
    v="";
    v = current_people.last_name;
    app_ui_standard_appendFieldText(grid, "id_spacemr_responsible_last_name", "spacemr_responsible_last_name", gRb("db.spacemr_space_people_book.spacemr_responsible_last_name"),v);
    v="";
    v = current_people.spacemr_people_id;
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_responsible_id", "spacemr_responsible_id", "hidden label for spacemr_responsible_id",v) ;
    //-
    //-
    if (app_userHasPermission("db_spacemr_space_people_book_admin")) {
        app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                               , "id_spacemr_responsible_username"
                                                               , "id_spacemr_responsible_first_name"
                                                               , "id_spacemr_responsible_last_name"
                                                               , "id_spacemr_responsible_id"
                                                              );
    } else {
        [ "id_spacemr_responsible_username"
          , "id_spacemr_responsible_first_name"
          , "id_spacemr_responsible_last_name"
          , "id_spacemr_responsible_id"
        ].forEach(function(id){
            setReadOnly(id);
        });
    }
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
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_people_id", "spacemr_people_id", "hidden label for spacemr_people_id",v) ;
    //-
    //-
    app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                           , "id_spacemr_people_username"
                                                           , "id_spacemr_people_first_name"
                                                           , "id_spacemr_people_last_name"
                                                           , "id_spacemr_people_id"
                                                          );
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
                                                         );
    //-
    v=""
    if (app_spacemr_space_people_book_last_inserted_data != undefined) v = app_spacemr_space_people_book_last_inserted_data.reason;
    app_ui_standard_appendFieldText(grid, "id_reason", "reason", gRb("db.spacemr_space_people_book.reason"),v);
    app_ui_standard_appendFieldInteger(grid, "id_people_number", "people_number", gRb("db.spacemr_space_people_book.people_number.long"),"1");
    //-
    {
        v="";
        if (current_date != "") {
            v = appConvertDateToString(parseInt(current_date));
        } else {
            if (app_spacemr_space_people_book_last_inserted_data != undefined) {
                v = appConvertDateToString(app_spacemr_space_people_book_last_inserted_data.date_from);
            }
        }
        app_ui_standard_appendFieldDate(grid, "id_date_from", "date_from", gRb("db.spacemr_space_people_book.date_from"),v)
            .change(app_ui_standard_appendField_custom_book_repetition_update_gui)
        ;
    }
    //-
    v="";
    if (current_date != "") {
        v = appConvertDateToString(parseInt(current_date));
    } else {
        if (app_spacemr_space_people_book_last_inserted_data != undefined) {
            v = appConvertDateToString(app_spacemr_space_people_book_last_inserted_data.date_to);
        }
    }
    app_ui_standard_appendFieldDate(grid, "id_date_to", "date_to", gRb("db.spacemr_space_people_book.date_to"),v)
        .change(app_ui_standard_appendField_custom_book_repetition_update_gui)
    ;
    //-
    v="d";
    if (app_spacemr_space_people_book_last_inserted_data != undefined) v = app_spacemr_space_people_book_last_inserted_data.repetition;
    app_ui_standard_appendField_custom_book_repetition(grid, "id_repetition", "repetition"
                                                       , gRb("db.spacemr_space_people_book.repetition")
                                                       , v
                                                      );
    //-
    v=""
    if (app_spacemr_space_people_book_last_inserted_data != undefined) v = app_spacemr_space_people_book_last_inserted_data.nota;
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_people_book.nota"),v);
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_spacemr_space_people_book_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_spacemr_space_people_book_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.spacemr_space_people_book.insert.title"));
    // }
}

//-
function app_spacemr_space_people_book_form_insert_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_spacemr_people_id').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people_book.spacemr_people_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_spacemr_responsible_id').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people_book.spacemr_responsible_id") + " - " + gRb("db.sys.invalidValue"))));
    ($('#id_spacemr_space_id').val() != "" && sys_number_integer_validation.test($('#id_spacemr_space_id').val()))
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people.spacemr_space_code") + " - " + gRb("db.sys.invalidValue") + ", " + gRb("db.sys.please.search.and.select"))));
    sys_dateFormatUi_validation.test($('#id_date_from').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people_book.date_from") + " - " + gRb("db.sys.invalidValue") )));
    sys_dateFormatUi_validation.test($('#id_date_to').val())
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people_book.date_to") + " - " + gRb("db.sys.invalidValue") )));
    (appConvertStringToTimestamp($('#id_date_to').val()) >= appConvertStringToTimestamp($('#id_date_from').val()))
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people_book.msg.date_to_greater_than_from")  )));
    ($('#id_reason').val() != "")
        || (rv.append($("<li/>").text(gRb("db.spacemr_space_people_book.msg.reason_can_not_be_empty")  )));
    {
        let people_number = $('#id_people_number').val();
        let doError = true;
        if ( sys_number_integer_validation.test(people_number)) {
            people_number = parseInt(people_number);
            if (people_number>=0) {
                doError = false;
            }
        }
        if(doError){
            rv.append($("<li/>").text(gRb("db.spacemr_space_people_book.people_number") + " - " + gRb("db.sys.invalidValue")))
        }
    }
    return rv;
}
//-
function app_spacemr_space_people_book_form_insert_sendData() {
    var data =  {
         spacemr_people_id:   $('#id_spacemr_people_id').val()
         , spacemr_responsible_id:   $('#id_spacemr_responsible_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , reason:   $('#id_reason').val()
         , people_number:   $('#id_people_number').val()
         , date_from:   appConvertStringToTimestamp($('#id_date_from').val())
         , date_to:   appConvertStringToTimestamp($('#id_date_to').val())
         , repetition: $('input[name=repetition]:checked').val()
         , nota:   $('#id_nota').val()
    };
    //-
    app_doRequestMappingRequestSync("spacemr_space_people_book/spacemr_space_people_book_insert"
                                    , data
                                    , function(content) {
                                        var spacemr_space_people_book_id = content.spacemr_space_people_book_id;
                                        data.spacemr_space_people_book_id = spacemr_space_people_book_id;
                                        app_spacemr_space_people_book_last_inserted_data = data;
                                        app_initialization_setHashAndGoToPage(
                                            "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_update"
                                                + "&spacemr_space_people_book_id="+spacemr_space_people_book_id);
                                    });
    //-
}



function app_spacemr_space_people_book_list_set_render_hooks(divTable, tableMattoni_options, wf, refresh_callback) {
    //-
    //- table hooks
    //-
    var render_hooks = [];
    render_hooks.push(function(){
        app_spacemr_space_people_book_list_show_transactions_options = false;
        app_spacemr_space_people_book_list_show_transactions_statuses = {};
    });
    tableMattoni_options.render_hooks = render_hooks;
    //-
    let update_log = $("<span id='update_log'>");
    //-
    var update_hook = function(event) {
        // $("input[radio][class^='lafw_']:checked")
        let re_id    = /(\w+)_([0-9]+)__(\w+)/;
        let re_class = /lafw__(\w+)/;
        let transactions = [];
        $("input[type=radio][class^='lafw_']:checked")
            .each(function(i, radio){
                let t = {};
                // console.log(`${i}: ${radio.id}, ${radio.className} - ${radio}`);
                let match=re_id.exec(radio.id);
                t.name       = match[1];
                t.spacemr_space_people_book_id    = match[2];
                t.stato = match[3];
                match=re_class.exec(radio.className);
                t.stato_old = match[1];
                //-
                // console.log(' -- ' + app_JSONStringify(t));
                if(t.stato_old != t.stato) {
                    t.nota=$("#"+t.name+"_"+t.spacemr_space_people_book_id+"__nota").val();
                    transactions.push(t);
                }
                //-
                //-
                // console.log(" -- radio: " + radio);
                // console.log(" -- radio: "
                //             + radio.attr('id')
                //             + " - "
                //             + radio.attr('class')
                //            );
            });
        if (transactions.length > 0) {
            // console.log(' -- ' + app_JSONStringify(transactions));
            let iterator_tot = transactions.length;
            let iterator_i = 0;
            let update_update_log = function() {
                update_log.text(" updated "+iterator_i+"/"+iterator_tot+"");
                if (iterator_i == iterator_tot) {
                    if (refresh_callback == null) {
                        divTable.tableMattoni().render();
                    } else {
                        refresh_callback();
                    }
                }
            }
            update_update_log();
            let i = 0;
            while(i<iterator_tot) {
                let t = transactions[i];
                // console.log(' -- updating ' + app_JSONStringify(t));
                app_doRequestMappingRequest("spacemr_space_people_book/spacemr_space_people_book_do_transaction"
                                            , t
                                            , function(content) {
                                                iterator_i = iterator_i + 1;
                                                update_update_log();
                                            }
                                           );
                i = i + 1;
            }
        }
    }
    //-
    var render_post_hooks = [];
    render_post_hooks.push(function(){
        if (app_spacemr_space_people_book_list_show_transactions_options){
            var space_people_book_transactionBar = $("#space_people_book_transactionBar");
            space_people_book_transactionBar.html("");
            if (app_spacemr_space_people_book_list_show_transactions_options) {
                space_people_book_transactionBar
                    .append(" "+gRb("db.spacemr_space_people_book.transactions")+": ")
                    .append(app_list_appendFieldWorkflow_transactions("id_stato_"
                                                                      , "stato_"
                                                                      , "db.spacemr_space_people_book.stato"
                                                                      , app_spacemr_space_people_book_list_show_transactions_statuses
                                                                      , wf
                                                                      , update_hook
                                                                      , update_log
                                                                     ))
            }
        }
    });
    tableMattoni_options.render_post_hooks = render_post_hooks;
    //-
}

function app_ui_standard_appendField_custom_book_repetition_update_gui(){
    $(".db_spacemr_space_people_book_date_from").text("...");
    $(".db_spacemr_space_people_book_weekday").text("...");
    $(".db_spacemr_space_people_book_date_to").text("...");
    //-
    let df = appConvertStringToTimestamp($('#id_date_from').val());
    let dt = appConvertStringToTimestamp($('#id_date_to').val());
    if (df != "") {
        // console.log("df." + df);
        $(".db_spacemr_space_people_book_date_from").text($('#id_date_from').val());
        $(".db_spacemr_space_people_book_weekday").text(gRb("db.sys.calendar.weekday."+ (new Date(df)).getDay()));
        if (dt != "") {
            // console.log("dt." + dt);
            if (dt < df) {
                $('#id_date_to').val($('#id_date_from').val());
            }
        } else {
            $('#id_date_to').val($('#id_date_from').val());
        }
    }
    if (dt != "") {
        $(".db_spacemr_space_people_book_date_to").text($('#id_date_to').val());
    }
};
function app_ui_standard_appendField_custom_book_repetition(grid, id, name, label, value) {
    var rv = $("<div>");
    //-
    let value_name = "d";
    rv.append($("<div>")
              .append(
                  $('<input type="radio" value="">')
                      .attr("name", name)
                      .attr("id",   name+"__"+value_name)
                      .attr('value', value_name)
                      .prop("checked", (value_name == value))
                      .val(value_name)
              )
              .append(" " + gRb("db.spacemr_space_people_book.repetition."+value_name)
                      + " - " + gRb("db.spacemr_space_people_book.repetition_everyDay")
                      + " " + gRb("db.sys.date_from")
                      + " "
                     )
              .append($("<span class='db_spacemr_space_people_book_date_from'>").text("..."))
              .append(" " + gRb("db.sys.date_to")
                      + " "
                     )
              .append($("<span class='db_spacemr_space_people_book_date_to'>").text("..."))
             );
    //-
    value_name = "w";
    rv.append($("<div>")
              .append(
                  $('<input type="radio" value="">')
                      .attr("name", name)
                      .attr("id",   name+"__"+value_name)
                      .attr('value', value_name)
                      .prop("checked", (value_name == value))
                      .val(value_name)
              )
              .append(" " + gRb("db.spacemr_space_people_book.repetition."+value_name)
                      + " - " + gRb("db.spacemr_space_people_book.repetition_everyWeekday")
                      + " ")
              .append($("<span class='db_spacemr_space_people_book_weekday'>")
                      .text("...")
                      .css("font-weight","bold")
                     )
              .append(" " + gRb("db.sys.date_from")
                      + " "
                     )
              .append($("<span class='db_spacemr_space_people_book_date_from'>").text("..."))
              .append(" " + gRb("db.sys.date_to")
                      + " "
                     )
              .append($("<span class='db_spacemr_space_people_book_date_to'>").text("..."))
             );
    //-
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).attr("id", id+"_label").text(label))
        .append(rv)
    ;
    setTimeout(app_ui_standard_appendField_custom_book_repetition_update_gui, 200);
    return(rv);
}


//-
function app_spacemr_space_people_book_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.spacemr_space_people_book.list.title"));
    var wf = app_workflow_get("spacemr_space_people_book_workflow");
    if (wf == undefined) {
        // alert("workflow spacemr_space_people_book_workflow undefined");
        app_spacemr_space_people_book_doInitialize(app_spacemr_space_people_book_list);
        return;
    }
    //-
    var spacemr_space_id = getLocationParameterByName('spacemr_space_id');
    if ( spacemr_space_id == "") spacemr_space_id = undefined;
    var spacemr_people_id = getLocationParameterByName('spacemr_people_id');
    if ( spacemr_people_id == "") spacemr_people_id = undefined;
    var spacemr_people_username = getLocationParameterByName('spacemr_people_username');
    if ( spacemr_people_username == "") spacemr_people_username = undefined;
    //-
    //-
    var page    = $("<div>");
    if ( spacemr_space_id != undefined) {
        page.append(app_spacemr_space_tabs_get(spacemr_space_id));
    }
    if ( spacemr_people_id != undefined) {
        page.append(app_spacemr_people_tabs_get(spacemr_people_id));
    }
    var tableid = "table_spacemr_space_people_book_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_update"
                                 + "&spacemr_space_people_book_id="+row[mapIdName["spacemr_space_people_book_id"]]
                                )
            .attr('title',gRb("db.spacemr_space_people_book.update.title"))
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        // console.log('mapIdName["stato_hidden"]: ' + mapIdName["stato_hidden"])
        // console.log('row[mapIdName["stato_hidden"]]: ' + row[mapIdName["stato_hidden"]])
        // console.log('wf.statusIndex[row[mapIdName["stato_hidden"]]].color: ' + wf.statusIndex[row[mapIdName["stato_hidden"]]].color)
        //-
        tr.prepend($('<td>')
                   // .css("width", "30px")
                   // .css("overflow", "hidden")
                   // .css("display", "inline-block")
                   // .css("white-space", "nowrap")
                   .append(edit)
                   // .append(
                   //     app_userHasPermission("db_spacemr_people_read") ? (
                   //             app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update"
                   //                                  + "&spacemr_people_id="+row[mapIdName["spacemr_people_id"]]
                   //                                 )
                   //                     .attr('title',gRb("db.spacemr_people..single"))
                   //                     .append(app_getIcon("user", 15))
                   //     ) : ""
                   // )
                   // .append(
                   //     app_userHasPermission("db_spacemr_people_read") ? (
                   //             app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update"
                   //                                  + "&spacemr_people_id="+row[mapIdName["spacemr_responsible_id"]]
                   //                                 )
                   //                     .attr('title',gRb("db.spacemr_space_people_book.responsible"))
                   //                 .append(app_getIcon("user", 15).css("color", "#0000DD")) // ""
                   //     ) : ""
                   // )
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
                       app_userHasPermission("db_spacemr_space_people_book_read") ? (
                           app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar"
                                                + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                               )
                               .attr('title',gRb("db.spacemr_space_people_book.calendar"))
                               .append(
                                   app_getIcon("calendar_alt", 15)
                               )
                       ) : ""
                   )
                   .append(
                       app_userHasPermission("db_spacemr_space_people_book_read")
                           && app_userHasPermission("db_spacemr_space_read") ? (
                               app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                    + "&spacemr_space_child_id="+row[mapIdName["spacemr_space_id"]]
                                                    + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_in_map_id_default"]]
                                                    + "&app_spacemr_space_people_view_mode=bookings"
                                                   )
                               .attr('title',gRb("db.spacemr_space_map.maps.people"))
                               .append(
                                   app_getIcon("map", 15)
                               )
                       ) : ""
                   )
                   .append(
                       app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_update"
                                            + "&spacemr_space_people_book_id="+row[mapIdName["spacemr_space_people_book_id"]]
                                           )
                           .attr('title',wf.statusIndex[row[mapIdName["stato_hidden"]]].description)
                           .append(
                               app_getIcon("circle", 15).css("color", wf.statusIndex[row[mapIdName["stato_hidden"]]].color)
                           )
                   )
                   // .append(spacemr_space_people_book_super)
                   // .append(spacemr_space_people_book_sub)
                  );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "spacemr_space_people_book/spacemr_space_people_book_list"
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
    app_where_set_workflow_defaults(tableMattoni_options.content.qparams, "stato", wf);
    {
        //-
        //- navigation and filter hooks
        //-
        var qvalue = getLocationParameterByName("qparams");
        if (qvalue != "") {
            tableMattoni_options.content.qparams = JSON.parse(qvalue);
            if (tableMattoni_options.content.qparams.where.from_today) {
                let d = new Date();
                tableMattoni_options.content.qparams.where.ccd_initial =
                    new Date(d.getFullYear(),d.getMonth(), d.getDate(),0,0,0 ).getTime();
            }
            if (tableMattoni_options.content.qparams.where.this_username) {
                //console.log("this_username! " + sys_session.userData.user_name);
                if (sys_session.userData.user_name != 'anonymous'){
                    tableMattoni_options.content.qparams.where.username =
                        sys_session.userData.user_name;
                    tableMattoni_options.content.qparams.where.this_username = false;
                }
            }
        }
    }
    if ( spacemr_space_id != undefined) {
        tableMattoni_options.content.qparams.where.spacemr_space_id = spacemr_space_id;
        tableMattoni_options.content.qparams.where.recursive_space = true;
    }
    if ( spacemr_people_id != undefined) {
        tableMattoni_options.content.qparams.where.spacemr_people_id = spacemr_people_id;
    }
    //-
    //- the div containing the table
    var divTable=$("<div>");
    //-
    app_spacemr_space_people_book_list_set_render_hooks(divTable, tableMattoni_options, wf, null);
    appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
    page.append(divTable);
    //-
    {
        //-
        //- area containing insert button and "show/hide"  filters
        //-
        var form = $('<form name="fm">');
        let grid = app_ui_standard_getGrid(form);
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
        //-
        // console.log("app_spacemr_space_people_book_current_people: " + app_JSONStringify(app_spacemr_space_people_book_current_people));
        // console.log("wf.aux: " + app_JSONStringify(wf.aux));
        // console.log("if: " );
        if (app_userHasPermission("db_spacemr_space_people_book_admin")
            || (app_userHasPermission("db_spacemr_space_people_book_insert")
                && wf.aux.bookers_roles.includes(app_spacemr_space_people_book_current_people.role))
           ) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.spacemr_space_people_book.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_insert"
                                                              // + "&spacemr_space_people_book_super_id=" + spacemr_space_people_book_super_id
                                                             )
                    }
                          )
            );
        }
        //-
        //- where clause
        //-
        //-
        fieldcontain.append($("<h3>").text(gRb("db.sys.ricercaPerParametri")));
        //-
        app_where_append_workflow(fieldcontain, "db.spacemr_space_people_book.stato", "stato", divTable, wf);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_space_code"), "spacemr_space_code", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.username"), "username", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_people_username"), "spacemr_people_username", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_people_first_name"), "spacemr_people_first_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_people_last_name"), "spacemr_people_last_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_people_role"), "spacemr_people_role", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_responsible_username"), "spacemr_responsible_username", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_responsible_first_name"), "spacemr_responsible_first_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_responsible_last_name"), "spacemr_responsible_last_name", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.spacemr_responsible_role"), "spacemr_responsible_role", divTable);
        app_where_append_date_single(fieldcontain,   gRb("db.spacemr_space_people_book.current_date"), "current_date", divTable);
        app_where_append_date_single(fieldcontain,   gRb("db.spacemr_space_people_book.ccd_initial"), "ccd_initial", divTable);
        app_where_append_date_single(fieldcontain,   gRb("db.spacemr_space_people_book.ccd_final"), "ccd_final", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.reason"), "reason", divTable);
        app_where_append_integer(fieldcontain, gRb("db.spacemr_space_people_book.people_number"), "people_number", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.stato"), "stato", divTable);
        app_where_append_string(fieldcontain, gRb("db.spacemr_space_people_book.nota"), "nota", divTable);
        app_where_append_date(fieldcontain,   gRb("db.spacemr_space_people_book.date_from"), "date_from", divTable);
        app_where_append_date(fieldcontain,   gRb("db.spacemr_space_people_book.date_to"), "date_to", divTable);
        app_where_append_checkbox(fieldcontain,   gRb("db.spacemr_space_people_book.this_username"), "this_username", divTable);
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
        buttonline.append(" ").append($("<span id='space_people_book_transactionBar'>"));
        {
            //-
            //- navigation and filter hooks
            //-
            divTable.tableMattoni().render_addHook(function(){
                var qp=app_JSONStringify(divTable.tableMattoni().qparams(),0).replace(/%/g,"%25");
                var url = ""
                    + "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list"
                    + "&qparams="+qp
                ;
                // console.log(url);
                app_initialization_setHash(url);
                //-
            });
            divTable.tableMattoni().where_fields_hooks_run();
        }
        //-
        grid.append(fieldcontain);
        buttons_div.append(form);
    }
    //-
    appSetPage(page, gRb("db.spacemr_space_people_book.list.title"));
    //-
}
//-
function app_spacemr_space_people_book_tabs(spacemr_space_people_book) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_spacemr_space_people_book_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_update&spacemr_space_people_book_id="+spacemr_space_people_book.spacemr_space_people_book_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_people_book..single") )
        );
    }
    if (app_userHasPermission("db_spacemr_people_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id="+spacemr_space_people_book.spacemr_people_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_people..single") + ": " + spacemr_space_people_book.spacemr_people_username)
        );
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_people__app_spacemr_people_form_update&spacemr_people_id="+spacemr_space_people_book.spacemr_responsible_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space_people_book.responsible") + ": " + spacemr_space_people_book.spacemr_responsible_username)
        );
    }
    if (app_userHasPermission("db_spacemr_space_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_spacemr_space__app_spacemr_space_form_update&spacemr_space_id="+spacemr_space_people_book.spacemr_space_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.spacemr_space..single") + ": " + spacemr_space_people_book.spacemr_space_code)
        );
    }
    return(rv);
}
//-
function app_spacemr_space_people_book_form_update() {
    var wf = app_workflow_get("spacemr_space_people_book_workflow");
    if (wf == undefined) {
        // console.log("-- workflow spacemr_space_people_book_workflow undefined")
        app_spacemr_space_people_book_doInitialize(app_spacemr_space_people_book_form_update);
        return;
    }
    var spacemr_space_people_book_id = getLocationParameterByName('spacemr_space_people_book_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.spacemr_space_people_book.update.title"));
    //-
    app_doRequestMappingRequest("spacemr_space_people_book/spacemr_space_people_book_get"
                                , { spacemr_space_people_book_id: spacemr_space_people_book_id}
                                , function(content) {
                                    app_spacemr_space_people_book_form_update_data(content);
                                });
}

//-
var app_spacemr_space_people_book_form_update_old_data = undefined;
//-
function app_spacemr_space_people_book_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm" id="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_spacemr_space_tabs_get(obj.spacemr_space_id));
    page.append(app_spacemr_space_people_book_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    var wf = app_workflow_get("spacemr_space_people_book_workflow");
    //-
    var updateFunction = function(event) {
        var onSuccess = function() {
            if (logTable != undefined ) {
                setTimeout(function() {
                    logTable.tableMattoni().render();
                }, 1000);
            }
        }
        app_spacemr_space_people_book_form_update_doUpdate(onSuccess);
    };
    var topbuttonline = app_ui_standard_appendRow(grid);
    //-
    var logs = $("<div>"); grid.append(logs);
    //-
    if (app_userHasPermission("db_spacemr_space_people_book_admin")
        || ( app_userHasPermission("db_spacemr_space_people_book_update")
             && app_spacemr_space_people_book_current_people.username == content.spacemr_responsible.username
           )) {
        topbuttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
        topbuttonline.append(" ").append(
            app_ui_standard_button()
                .append(app_getIcon("calendar_alt", 15))
                .append(" ")
                .append(gRb("db.spacemr_space_people_book.calendar.of.this.space"))
                .click(function(){
                    event.preventDefault();
                    event.stopPropagation();
                    var ls1 = "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar&spacemr_space_id="+obj.spacemr_space_id+"";
                    app_initialization_setHashAndGoToPage(ls1);
                })
        );
    }
    //-
    // app_ui_standard_appendFieldHidden(form, "id_spacemr_space_people_book_id", "spacemr_space_people_book_id", "label", obj.spacemr_space_people_book_id);
    app_ui_standard_appendFieldHidden(form, "id_spacemr_space_people_book_id", "spacemr_space_people_book_id", "label", obj.spacemr_space_people_book_id);
    //-
    //-
    app_ui_standard_appendFieldWorkflow(grid, "id_stato", "stato", "db.spacemr_space_people_book.stato",obj.stato,wf);
    //-
    //-
    v="";
    if (content.spacemr_responsible != undefined) v = content.spacemr_responsible.username;
    app_ui_standard_appendFieldText(grid, "id_spacemr_responsible_username", "spacemr_responsible_username", gRb("db.spacemr_space_people_book.spacemr_responsible_username"),v);
    v="";
    if (content.spacemr_responsible != undefined) v = content.spacemr_responsible.first_name;
    app_ui_standard_appendFieldText(grid, "id_spacemr_responsible_first_name", "spacemr_responsible_first_name", gRb("db.spacemr_space_people_book.spacemr_responsible_first_name"),v);
    v="";
    if (content.spacemr_responsible != undefined) v = content.spacemr_responsible.last_name;
    app_ui_standard_appendFieldText(grid, "id_spacemr_responsible_last_name", "spacemr_responsible_last_name", gRb("db.spacemr_space_people_book.spacemr_responsible_last_name"),v);
    v = obj.spacemr_responsible_id;
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_responsible_id", "spacemr_responsible_id", "hidden label for spacemr_responsible_id",v) ;
    //-
    if (wf.statusIndex[obj.stato].per_field_permission_canWrite["spacemr_responsible_id"]) {
        app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                               , "id_spacemr_responsible_username"
                                                               , "id_spacemr_responsible_first_name"
                                                               , "id_spacemr_responsible_last_name"
                                                               , "id_spacemr_responsible_id"
                                                              );
    }
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
    v=obj.spacemr_people_id;
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_people_id", "spacemr_people_id", "hidden label for spacemr_people_id",v) ;
    //-
    //-
    if (wf.statusIndex[obj.stato].per_field_permission_canWrite["spacemr_people_id"]) {
        app_spacemr_space_app_ui_standard_appendSearch_persona(grid
                                                               , "id_spacemr_people_username"
                                                               , "id_spacemr_people_first_name"
                                                               , "id_spacemr_people_last_name"
                                                               , "id_spacemr_people_id"
                                                              );
    }
    //-
    //-
    v = "";
    if (content.spacemr_space != undefined) v = content.spacemr_space.code;
    app_ui_standard_appendFieldText(grid, "id_spacemr_space_code", "spacemr_space_code", gRb("db.spacemr_space_people.spacemr_space_code"),v);
    //-
    v=obj.spacemr_space_id;
    app_ui_standard_appendFieldHidden(grid, "id_spacemr_space_id", "spacemr_space_id", "hidden space",v);
    if (wf.statusIndex[obj.stato].per_field_permission_canWrite["spacemr_space_id"]) {
        app_spacemr_space_app_ui_standard_appendSearch_spazio(grid
                                                              , "id_spacemr_space_code"
                                                              , "id_spacemr_space_id"
                                                             );
    }
    //-
    app_ui_standard_appendFieldText(grid, "id_reason", "reason", gRb("db.spacemr_space_people_book.reason"),obj.reason);
    app_ui_standard_appendFieldInteger(grid, "id_people_number", "people_number", gRb("db.spacemr_space_people_book.people_number.long"),obj.people_number);
    app_ui_standard_appendFieldDate(grid, "id_date_from", "date_from", gRb("db.spacemr_space_people_book.date_from"),appConvertDateToString(obj.date_from))
        .change(app_ui_standard_appendField_custom_book_repetition_update_gui)
    ;
    app_ui_standard_appendFieldDate(grid, "id_date_to", "date_to", gRb("db.spacemr_space_people_book.date_to"),appConvertDateToString(obj.date_to))
        .change(app_ui_standard_appendField_custom_book_repetition_update_gui)
    ;
    app_ui_standard_appendField_custom_book_repetition(grid, "id_repetition", "repetition"
                                                       , gRb("db.spacemr_space_people_book.repetition")
                                                       , obj.repetition
                                                      );
    // app_ui_standard_appendFieldText(grid, "id_stato", "stato", gRb("db.spacemr_space_people_book.stato"),obj.stato);
    app_ui_standard_appendFieldTextArea(grid, "id_nota", "nota", gRb("db.spacemr_space_people_book.nota"),obj.nota);
    //-
    app_workflow_apply_per_field_permisson(wf, obj.stato, grid);
    //-
    var buttonlinestatus = app_ui_standard_appendRow(grid);
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_spacemr_space_people_book_admin")
        || ( app_userHasPermission("db_spacemr_space_people_book_update")
             && app_spacemr_space_people_book_current_people.username == content.spacemr_responsible.username
           )) {
        {
            let stato = wf.statusIndex[obj.stato];
            for (let is = 0; is < stato.steps.length; is++) {
                let sname = stato.steps[is];
                let sstato = wf.statusIndex[sname];
                let ssi = stato.stepsIndex[sname];
                buttonlinestatus.append(" ").append(
                    app_ui_standard_button()
                        .append(gRb("db.sys.update") + " ")
                        .append(app_getIcon("arrow_right", 15))
                        .append(app_getIcon("circle", 15).css("color",sstato.color))
                        .append(sstato.name)
                        .attr('title',gRb("db.sys.workflow.change.status.and.update"))
                        .click(function(){
                            var answer = confirm(gRb("db.spacemr_space_people_book.confirm.change_status_to") + " " + sstato.name + "?");
                            if (answer) {
                                $('#stato__' + sstato.name).prop('checked',true);
                                updateFunction();
                            }
                        })
                );
            }
        }
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    if (app_userHasPermission("db_spacemr_space_people_book_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  {
                            spacemr_space_people_book_id:   $('#id_spacemr_space_people_book_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("spacemr_space_people_book/spacemr_space_people_book_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_spacemr_space_people_book_admin")
        || ( app_userHasPermission("db_spacemr_space_people_book_insert")
             && app_spacemr_space_people_book_current_people.username == content.spacemr_responsible.username
           )) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.spacemr_space_people_book.update.insertcopying"))
                .click(
                    function(event) {
                        var data = app_spacemr_space_people_book_form_update_getData();
                        if (app_JSONStringify(app_spacemr_space_people_book_form_update_old_data) == app_JSONStringify(data)) {
                            app_setMessage(gRb("db.spacemr_space_people_book.update.insertcopying.noChanges"));
                        } else
                            var answer = confirm(gRb("db.spacemr_space_people_book.update.insertcopying.confirm"));
                        if (answer) {
                            var validate = app_spacemr_space_people_book_form_insert_validate();
                            if (validate.children().length != 0) {
                                doPopupHtml(validate);
                            } else {
                                app_spacemr_space_people_book_form_insert_sendData();
                            }
                        }
                    }
                )
        );
    }
    if (app_userHasPermission("db_spacemr_space_people_book_logs")) {
        topbuttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_spacemr_space_people_book_logs"
                                        , controller: "spacemr_space_people_book/spacemr_space_people_book_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                spacemr_space_people_book_id: obj.spacemr_space_people_book_id
                                                , order:[{ column:"date", desc: true}]
                                            }
                                        }
                                       });
                        logTable.hide();
                        logs.html(logTable);
                        setTimeout(function() {
                            logTable.show("fast");
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
    appSetPage(page, gRb("db.spacemr_space_people_book.update.title") + " " + obj.spacemr_space_people_book_id);
    //-
    app_spacemr_space_people_book_form_update_old_data = app_spacemr_space_people_book_form_update_getData();
}

//-
function app_spacemr_space_people_book_form_update_doUpdate(onSuccessCallback) {
    var validate = app_spacemr_space_people_book_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_spacemr_space_people_book_form_update_getData();
        // console.log(app_JSONStringify(app_spacemr_space_people_book_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_spacemr_space_people_book_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            // data.stato = "pippero";
            app_doRequestMappingRequestSync("spacemr_space_people_book/spacemr_space_people_book_update"
                                        , data
                                        , function(content) {
                                            app_setMessage(gRb("db.spacemr_space_people_book.update.Updated"));
                                            // console.log(" -- stato da "+app_spacemr_space_people_book_form_update_old_data.stato+" a " + data.stato+"")
                                            if (app_spacemr_space_people_book_form_update_old_data.stato == data.stato) {
                                                app_spacemr_space_people_book_form_update_old_data = data;
                                                onSuccessCallback();
                                            } else {
                                                //- status has changed
                                                app_doRequestMappingRequest("spacemr_space_people_book/spacemr_space_people_book_get"
                                                                            , { spacemr_space_people_book_id: data.spacemr_space_people_book_id}
                                                                            , function(content) {
                                                                                app_spacemr_space_people_book_form_update_data(content);
                                                                            });
                                            }
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_spacemr_space_people_book_form_update_validate() {
    var rv = app_spacemr_space_people_book_form_insert_validate();
    return rv;
}
//-
function app_spacemr_space_people_book_form_update_getData() {
    var data =  {
         spacemr_space_people_book_id:   $('#id_spacemr_space_people_book_id').val()
         , spacemr_people_id:   $('#id_spacemr_people_id').val()
         , spacemr_responsible_id:   $('#id_spacemr_responsible_id').val()
         , spacemr_space_id:   $('#id_spacemr_space_id').val()
         , reason:   $('#id_reason').val()
         , people_number:   $('#id_people_number').val()
         , date_from:   appConvertStringToTimestamp($('#id_date_from').val())
         , date_to:   appConvertStringToTimestamp($('#id_date_to').val())
         , stato:               $("input[name='stato']:checked").val()
         , repetition: $('input[name=repetition]:checked').val()
         , nota:   $('#id_nota').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}

function app_spacemr_space_people_book_getVerifier() {
    //- booking_verifier given
    //-   a tree of spaces
    //-   and a set of bookings on such spaces
    //- helps to recursively
    //    compute the occupation of such spaces
    //-   compare the number_of_seating_booking with the computed occupation.
    let rv = {
        init: function(content, wf) {
            //- content has the format
            // {
            //   "spacemr_space_people_books": {
            //     "headers": [
            //       {
            //         "name": "spacemr_space_people_book_id",
            //         "label": "obj.spacemr_space_people_book_id",
            //         "type": "integer"
            //       },
            //       {
            //         "name": "spacemr_space_id",
            //         "label": "obj.spacemr_space_id",
            //         "type": "integer"
            //       },
            //       {
            //         "name": "code",
            //         "label": "obj.code",
            //         "type": "string"
            //       },
            //       {
            //         "name": "date_from",
            //         "label": "obj.date_from",
            //         "type": "date"
            //       },
            //       {
            //         "name": "date_to",
            //         "label": "obj.date_to",
            //         "type": "date"
            //       },
            //       {
            //         "name": "repetition",
            //         "label": "obj.repetition",
            //         "type": "string"
            //       },
            //       {
            //         "name": "people_number",
            //         "label": "obj.people_number",
            //         "type": "integer"
            //       }
            //     ],
            //     "rows": [
            //       [
            //         24,
            //         4,
            //         "MO-27-01-020",
            //         1593640800000,
            //         1593640800000,
            //         "d"
            //       ],
            //       [
            //         29,
            //         5,
            //         "MO-27-01-a20",
            //         1594072800000,
            //         1598824800000,
            //         "w",
            //         1
            //       ], ...
            //     ]
            //   },
            //   "spacemr_spaces": {
            //     "headers": [
            //       {
            //         "name": "spacemr_space_id",
            //         "label": "obj.spacemr_space_id",
            //         "type": "integer"
            //       },
            //       {
            //         "name": "code",
            //         "label": "obj.code",
            //         "type": "string"
            //       },
            //       {
            //         "name": "number_of_seating",
            //         "label": "obj.number_of_seating",
            //         "type": "integer"
            //       },
            //       {
            //         "name": "number_of_seating_booking",
            //         "label": "obj.number_of_seating_booking",
            //         "type": "integer"
            //       },
            //       {
            //         "name": "spacemr_space_in_id",
            //         "label": "obj.spacemr_space_in_id",
            //         "type": "integer"
            //       }
            //     ],
            //     "rows": [
            //       [
            //         3,
            //         "MO-27",
            //         null,
            //         null,
            //         1
            //       ],
            //       [
            //         20,
            //         "MO-27-01",
            //         null,
            //         null,
            //         3
            //       ], ...
            //     ]
            //   }
            // }
            //-
            let wid = this;
            //-
            wid.wf = wf;
            let spacemr_space_people_books = content.spacemr_space_people_books;
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
            //- processing bookings
            //-
            {
                let result_set = content.spacemr_space_people_books;
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
                    objects.push(obj);
                }
                //-
                wid.bookings = objects;
                // console.log(" --- all bookings:\n" + app_JSONStringify(wid.bookings));
            }
            // console.log(" -- verifier is initialized");
        }
        , compute_occupation(current_date) {
            let wid = this;
            let wf = wid.wf;
            let ccd_day   = current_date.getDay();
            let ccd_time  = current_date.getTime();
            // console.log(" -- in compute_occupation d: "+current_date+" wf: " + wf);
            let spaces   = wid.spaces;
            let bookings = wid.bookings;
            // console.log(" --- all bookings length:" + bookings.length);
            // console.log(" --- all bookings2:\n" + app_JSONStringify(bookings));
            //-
            //- clean data structure
            //-
            var keys = Object.keys(spaces);
            for (let i = 0; i < keys.length; i++) {
                let space = spaces[keys[i]];
                space.bookings       = [];
                space.bookings_count = 0;
                space.exceeds = false;
                space.exceeds_spaces = [];
            }
            //-
            // console.log(" -- in compute_occupation computing leaf bookings");
            // console.log(" -- wf: " + app_JSONStringify(wf));
            //-
            for (let i = 0; i < bookings.length; i++) {
                let row = bookings[i];
                // console.log(" -- ccd_time: "+ccd_time+"\nprocessing booking: " + app_JSONStringify(row));
                if (
                    (!row.date_from || ccd_time >= row.date_from)
                        && (!row.date_to || ccd_time <= row.date_to )
                        && (row.repetition == 'd' || row.day == ccd_day)
                ) {
                    let space = spaces[row.spacemr_space_id];
                    // console.log(" -- ccd_time: "+ccd_time+"\nadding  booking: " + app_JSONStringify(row));
                    space.bookings.push(row);
                    if (wf.aux.bookings_counted_statuses.includes(row.stato)) {
                        space.bookings_count = space.bookings_count + row.people_number;
                    }
                }
            }
            //-
            // console.log(" -- in compute_occupation roll_up recursively");
            //-
            let collect_tree = function(obj) {
                let sum = obj.bookings_count;
                // console.log(" -- in collect_tree for " + obj.code);
                for (let i = 0; i < obj.childs.length; i++) {
                    let child = obj.childs[i];
                    // console.log(" -- child for " + obj.code + ": " + child.code);
                    collect_tree(child);
                    sum = sum + child.bookings_count;
                    obj.bookings = obj.bookings.concat(child.bookings);
                    if (child.exceeds) {
                        obj.exceeds = true;
                        obj.exceeds_spaces = obj.exceeds_spaces.concat(child.exceeds_spaces);
                    };
                }
                obj.bookings_count = sum;
                //-
                if (obj.number_of_seating_booking != null
                    && obj.bookings_count > obj.number_of_seating_booking) {
                    obj.exceeds = true;
                    obj.exceeds_spaces.push(obj.code);
                }
                // console.log(" -- obj.exceeds: " + obj.exceeds + " count: "+obj.bookings_count+" seats: " + obj.number_of_seating_booking );
            }
            //-
            collect_tree(wid.space_root);
            // console.log(app_JSONStringify(wid.space_root));
        }
        , tooltip_bookings(element, spacemr_space_id, stato, refresh_callback, ccd, menu_function) {
            let wid = this;
            let wf = wid.wf;
            let obj = wid.spaces[spacemr_space_id];
            let spacemr_space_people_book_ids = [];
            let bookings = obj.bookings;
            for (let i = 0; i < bookings.length; i++) {
                let booking = bookings[i];
                if (booking.stato == stato) {
                    spacemr_space_people_book_ids.push(booking.spacemr_space_people_book_id);
                }
            }
            app_tooltip_set_click(element, function(evt, tooltip_div){
                //-
                //- this a popup is like
                //-   app_spacemr_space_people_book_list
                //-
                //- start tooltip_generation
                //-
                let page = tooltip_div;
                var tableid = "table_spacemr_space_people_book_list_tooltip";
                //-
                let ccd_time = ccd.getTime();
                var buttonsCell = function(tr, mapIdName, row) {
                    //-
                    let cut_function = function(evt, tooltip_div){
                        //-
                        //- booking-cut popup management
                        //-
                        var form = $('<form name="fm_cut">');
                        let spacemr_space_people_book_id = row[mapIdName['spacemr_space_people_book_id']];
                        tooltip_div.append(form);
                        let grid = app_ui_standard_getGrid(form);
                        var buttonline = app_ui_standard_appendRow(grid);
                        buttonline
                            .append(gRb("db.spacemr_space_people_book.cut.pop.pre"))
                            .append($("<div>")
                                    .css("font-weight","bold")
                                    .append(gRb("db.sys.calendar.weekday."+ccd.getDay()))
                                    .append(" " + appConvertDateToString(ccd_time))
                                   )
                            .append($("<div>")
                                    .append(
                                        app_ui_standard_button()
                                            .text(gRb("db.spacemr_space_people_book.cut.removeNow"))
                                            .click(function(event) {
                                                let v = confirm(gRb("db.spacemr_space_people_book.cut.continue"));
                                                if (v) {
                                                    var data =  {
                                                        spacemr_space_people_book_id:   spacemr_space_people_book_id
                                                        , current_date: ccd_time
                                                    };
                                                    app_doRequestMappingRequestSync("spacemr_space_people_book/spacemr_space_people_book_cut_date"
                                                                                    , data
                                                                                    , function(content) {
                                                                                        refresh_callback();
                                                                                    });
                                                }
                                            }
                                                  )
                                    )
                                   );
                        buttonline
                            .append(gRb("db.spacemr_space_people_book.cut.pop.pre1"))
                            .append($("<div>").text(" "))
                        ;
                        var booking_indented = $("<div>").css("textIndent","20px");
                        [
                           "spacemr_space_code",
                           "spacemr_responsible_last_name",
                           "spacemr_people_last_name",
                           "reason",
                           "date_from",
                           "date_to",
                           "repetition",
                           "people_number",
                           "nota"
                        ].forEach(function(id){
                            let div1 = $("<div>");
                            div1
                                .append(gRb("db.spacemr_space_people_book."+id))
                                .append(": ")
                            if (id.startsWith("date_")) {
                                div1.append(appConvertDateToString(row[mapIdName[id]]))
                            } else if (id.startsWith("repetition")) {
                                if (row[mapIdName[id]] == 'd') {
                                    div1.append(gRb("db.spacemr_space_people_book.repetition.d"))
                                } else {
                                    div1
                                        .append(gRb("db.spacemr_space_people_book.repetition_everyWeekday"))
                                        .append(gRb("db.sys.calendar.weekday."+(new Date(row[mapIdName["date_from"]])).getDay()))
                                }
                            } else {
                                div1.append(document.createTextNode(row[mapIdName[id]]))
                            }
                            ;
                            booking_indented.append(div1);
                        });
                        buttonline
                            .append(booking_indented)
                        ;
                        if(ccd_time == row[mapIdName["date_from"]]
                          || ccd_time == row[mapIdName["date_from"]]) {
                            buttonline.append("")
                        } else {
                            buttonline
                                .append($("<div>")
                                        .css("font-weight","bold")
                                        .append(gRb("db.spacemr_space_people_book.cut.will_divide"))
                                       )
                        }
                        // //-qui-
                        // buttonline
                        //     .append($("<div>")
                        //             .append($("<div>").text("spacemr_space_people_book_id" + row[mapIdName["spacemr_space_people_book_id"]]))
                        //             .append($("<pre>").text("mapIdName: " + app_JSONStringify(mapIdName)))
                        //             .append($("<div>").text("row: " + app_JSONStringify(row)))
                        //             .append($("<div>").text("date_from: " + row[mapIdName["date_from"]]))
                        //             .append($("<div>").text("date_to: " + row[mapIdName["date_to"]]))
                        //
                        //            );
                        /*
                           mapIdName: {
                           "spacemr_space_people_book_id": 0,
                           "spacemr_space_id": 1,
                           "spacemr_people_id": 2,
                           "spacemr_responsible_id": 3,
                           "stato_hidden": 4,
                           "responsible_username_hidden": 5,
                           "transactions": 6,
                           "spacemr_space_code": 7,
                           "spacemr_responsible_last_name": 8,
                           "spacemr_people_last_name": 9,
                           "reason": 10,
                           "date_from": 11,
                           "date_to": 12,
                           "repetition": 13,
                           "people_number": 14,
                           "nota": 15
                           }
                        */
                        //-
                        setTimeout(app_tooltip_center, 20);
                    };
                    //-
                    let split_in_weekly = false;
                    let split_in_weekly_function = null;
                    if (row[mapIdName["repetition"]] == 'd') {
                        // console.log(""
                        //             + row[mapIdName["spacemr_space_people_book_id"]]
                        //             + " - " + row[mapIdName["date_from"]]
                        //             + " - " + row[mapIdName["date_to"]]
                        //             + " - " + (row[mapIdName["date_to"]] - row[mapIdName["date_from"]]) / 1000 / 60 / 60 / 24 / 7
                        //
                        //            );
                        if ((row[mapIdName["date_to"]] > row[mapIdName["date_from"]] + (1000 * 60 * 60 * (3 + 24 * 7))) ) {
                            split_in_weekly = true;
                            split_in_weekly_function = function(evt, tooltip_div){
                                //-
                                //- booking-split_in_weekly popup management
                                //-
                                var form = $('<form name="fm_split_in_weekly">');
                                let spacemr_space_people_book_id = row[mapIdName['spacemr_space_people_book_id']];
                                tooltip_div.append(form);
                                let grid = app_ui_standard_getGrid(form);
                                var buttonline = app_ui_standard_appendRow(grid);
                                buttonline
                                    .append(gRb("db.spacemr_space_people_book.split_in_weekly.pop.pre"))
                                    .append($("<div>")
                                            .append(
                                                app_ui_standard_button()
                                                    .text(gRb("db.spacemr_space_people_book.split_in_weekly.splitNow"))
                                                    .click(function(event) {
                                                        let v = confirm(gRb("db.spacemr_space_people_book.split_in_weekly.continue"));
                                                        if (v) {
                                                            var data =  {
                                                                spacemr_space_people_book_id:   spacemr_space_people_book_id
                                                            };
                                                            app_doRequestMappingRequestSync("spacemr_space_people_book/spacemr_space_people_book_split_in_weekly"
                                                                                            , data
                                                                                            , function(content) {
                                                                                                refresh_callback();
                                                                                            });
                                                        }
                                                    }
                                                          )
                                            )
                                           );
                                buttonline
                                    .append(gRb("db.spacemr_space_people_book.split_in_weekly.pop.pre1"))
                                    .append($("<div>").text(" "))
                                ;
                                var booking_indented = $("<div>").css("textIndent","20px");
                                [
                                    "spacemr_space_code",
                                    "spacemr_responsible_last_name",
                                    "spacemr_people_last_name",
                                    "reason",
                                    "date_from",
                                    "date_to",
                                    "repetition",
                                    "people_number",
                                    "nota"
                                ].forEach(function(id){
                                    let div1 = $("<div>");
                                    div1
                                        .append(gRb("db.spacemr_space_people_book."+id))
                                        .append(": ")
                                    if (id.startsWith("date_")) {
                                        div1.append(appConvertDateToString(row[mapIdName[id]]))
                                    } else if (id.startsWith("repetition")) {
                                        if (row[mapIdName[id]] == 'd') {
                                            div1.append(gRb("db.spacemr_space_people_book.repetition.d"))
                                        } else {
                                            div1
                                                .append(gRb("db.spacemr_space_people_book.repetition_everyWeekday"))
                                                .append(gRb("db.sys.calendar.weekday."+(new Date(row[mapIdName["date_from"]])).getDay()))
                                        }
                                    } else {
                                        div1.append(document.createTextNode(row[mapIdName[id]]))
                                    }
                                    ;
                                    booking_indented.append(div1);
                                });
                                buttonline
                                    .append(booking_indented)
                                ;
                                //-
                                setTimeout(app_tooltip_center, 20);
                            };


                        }
                    }
                    //-
                    tr.prepend($('<td>')
                               .append(
                                   app_userHasPermission("db_spacemr_space_people_book_read") ? (
                                       app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_calendar"
                                                            + "&spacemr_space_id="+row[mapIdName["spacemr_space_id"]]
                                                           )
                                           .attr('title',gRb("db.spacemr_space_people_book.calendar"))
                                           .append(
                                               app_getIcon("calendar_alt", 15)
                                           )
                                   ) : ""
                               )
                               .append(
                                   app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_update"
                                                        + "&spacemr_space_people_book_id="+row[mapIdName["spacemr_space_people_book_id"]]
                                                       )
                                       .attr('title',wf.statusIndex[row[mapIdName["stato_hidden"]]].description)
                                       .append(
                                           app_getIcon("edit", 15).css("color", wf.statusIndex[row[mapIdName["stato_hidden"]]].color)
                                       )
                               )
                               .append(
                                   app_userHasPermission("db_spacemr_space_people_book_read")
                                       && app_userHasPermission("db_spacemr_space_read") ? (
                                           app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                                + "&spacemr_space_child_id="+row[mapIdName["spacemr_space_id"]]
                                                                + "&spacemr_space_map_id="+row[mapIdName["spacemr_space_in_map_id_default"]]
                                                                + "&app_spacemr_space_people_book_map_current_date="+ccd_time
                                                                + "&app_spacemr_space_people_view_mode=bookings"
                                                               )
                                               .attr('title',gRb("db.spacemr_space_map.maps.people"))
                                               .append(
                                                   app_getIcon("map", 15)
                                               )
                                       ) : ""
                               )
                               .append(
                                   app_userHasPermission("db_spacemr_space_people_book_admin")
                                       || ( app_userHasPermission("db_spacemr_space_people_book_update")
                                            && app_spacemr_space_people_book_current_people.username == row[mapIdName['responsible_username_hidden']]
                                          )
                                       ?
                                       (
                                           (row[mapIdName["date_from"]] == row[mapIdName["date_to"]]) ? "" :
                                               $("<a>")
                                               .attr('title', gRb("db.spacemr_space_people_book.cut.title"))
                                               .append(
                                                   app_getIcon("cut", 15)
                                                       .click(function(evt){
                                                           app_tooltip_set_on_click(evt
                                                                                    , cut_function
                                                                                    , null)
                                                       })
                                               )
                                       )
                                   : ""
                               )
                               .append(
                                   split_in_weekly &&
                                   (app_userHasPermission("db_spacemr_space_people_book_admin")
                                       || ( app_userHasPermission("db_spacemr_space_people_book_update")
                                            && app_spacemr_space_people_book_current_people.username == row[mapIdName['responsible_username_hidden']]
                                          ))
                                       ?
                                       (
                                           (row[mapIdName["date_from"]] == row[mapIdName["date_to"]]) ? "" :
                                               $("<a>")
                                               .attr('title', gRb("db.spacemr_space_people_book.split_in_weekly.title"))
                                               .append(
                                                   app_getIcon("bars-vertical", 15)
                                                       .click(function(evt){
                                                           app_tooltip_set_on_click(evt
                                                                                    , split_in_weekly_function
                                                                                    , null)
                                                       })
                                               )
                                       )
                                   : ""
                               )
                              );
                }
                var tableMattoni_options = {tableid: tableid
                                            , controller: "spacemr_space_people_book/spacemr_space_people_book_list"
                                            , buttonsCell: buttonsCell
                                            , content: {
                                                qparams: { }
                                            }
                                            , enable_csv_output: true
                                           };
                tableMattoni_options.content.qparams.where = {
                    spacemr_space_people_book_ids: spacemr_space_people_book_ids
                };
                //-
                //-
                app_where_set_workflow_defaults(tableMattoni_options.content.qparams, "stato", wf);
                //-
                //- the div containing the table
                var divTable=$("<div>");
                app_spacemr_space_people_book_list_set_render_hooks(divTable, tableMattoni_options, wf, refresh_callback);
                appGetWidget("custom.tableMattoni", divTable, tableMattoni_options);
                //-
                var form = $('<form name="fm">');
                let grid = app_ui_standard_getGrid(form);
                var buttonline = app_ui_standard_appendRow(grid);
		if (menu_function != null) {
		    menu_function(buttonline);
		}
                buttonline.append(" ").append($("<span id='space_people_book_transactionBar'>"));
                page.append(form);
                page.append(divTable);
                tableMattoni_options.render_post_hooks.push(function(){
                    app_tooltip_center();
                })
                //-
                //- end tooltip_generation
                //-
            });
        }
    }
    return(rv);
}


function app_spacemr_space_people_book_calendar_manager_basic() {
    let calendar_manager = {
        current_date: undefined     // current date
        , calendar_grid: undefined  // where render the calendar
        , date_grid: undefined      // where render the calendar top controls
        , ccd_initial: undefined    // first Date of the calendar
        , ccd_final: undefined      // last Date of the calendar
        , book:           {} // all the bookings in the period
        , book_byDayWeek: {} // all the bookings in the period
        , oneDayInMs: (24 * 60 * 60 * 1000)
        , date_offset: 1 //- il primo giorno della settimana e' lunedi'
        //-
        , init: function(date_grid, calendar_grid) {
            let wid = this;
            wid.calendar_grid = calendar_grid;
            wid.date_grid = date_grid;
            calendar_grid
                .css('display', 'grid')
                .css('grid-gap', '1px')
                .css('grid-gap', '1px')
                .css('grid-template-columns', '1fr 1fr 1fr 1fr 1fr 1fr 1fr')
                .css('background-color', "lightgray")
                .css('padding-left', "2px")
                .css('padding-right', "2px")
            ;
        }
        , redraw: function() {
            let wid = this;
            app_tooltip_hide();
            if (app_spacemr_space_people_book_calendar_current_date == undefined) {
                let d = new Date();
                app_spacemr_space_people_book_calendar_current_date =
                    new Date(d.getFullYear(),d.getMonth(), 1,0,0,0 );
            }
            wid.current_date = app_spacemr_space_people_book_calendar_current_date;
            wid.draw_current_date_modifiers();
            wid.calendar_grid.html("");
            // let i = 0;
            // for (i = 0; i < 6; i++) {
            //     let d = $("<div>");
            //     calendar_grid.append(d);
            // }
            for (i = 0; i < 7; i++) {
                let d = $("<div>")
                    .append(gRb("db.sys.calendar.weekday."+((i+wid.date_offset)%7)))
                    .css("font-weight", "bold")
                    .css('background-color', "white")
                ;
                wid.calendar_grid.append(d);
            }
            //-
            //- current calendar date
            //- compute for the first day to show.
            wid.compute_ccd_initial_final();
            //-
            wid.get_calendar_data();
        }
        , get_calendar_data: function() {
            let wid = this;
            wid.render_data();
        }
        , render_data: function() {
            let wid = this;
            let ccd = wid.ccd_initial;
            //console.log(app_JSONStringify(content));
            //-
            //-
            let today = null;
            {
                let d = new Date();
                today = new Date(d.getFullYear(),d.getMonth(), d.getDate(),0,0,0 );
            }
            let watchdog = 50;
            while(watchdog > 0
                  && (
                      ccd.getTime() < wid.current_date.getTime()
                          || ccd.getMonth() == wid.current_date.getMonth()
                  )
                 ) {
                watchdog = watchdog - 1;
                let  i_week = 0;
                for (i_week = 0; i_week < 7; i_week++) {
                    let ccd_date = ccd.getDate();
                    let ccd_time = ccd.getTime();
                    let ccd_day  = ccd.getDay();
                    //-
                    let d = $('<div>');
                    //-
                    if (ccd.getTime() == today.getTime()) {
                        d.css('background-color', "#fef9e7")
                    } else if (ccd.getMonth() == wid.current_date.getMonth()) {
                        d.css('background-color', "white")
                    } else {
                        d.css('background-color', "lightgray")
                    }
                    //-
                    //-
                    //-
                    //-
                    wid.render_data_single_day(d, ccd, ccd_date, ccd_time, ccd_day);
                    //-
                    //-
                    wid.calendar_grid.append(d);
                    //-
                    //-
                    //- console.log("ccd_date: " + ccd_date
                    //-             +", ccd_day: " + ccd_day
                    //-             + ", ccd_hour: " + ccd.getHours()
                    //-            );
                    //- console.log("increment 1 day.");
                    //- Daylight saving time fix
                    ccd = new Date( ccd.getTime() + wid.oneDayInMs + 2 * 1000 * 60 * 60);
                    ccd = new Date(ccd.getFullYear()
                                   ,ccd.getMonth()
                                   ,ccd.getDate()
                                   ,0,0,0
                                  );
                }
            }
        }
        , render_data_single_day: function(day_div, ccd, ccd_date, ccd_time, ccd_day){
            let wid = this;
            day_div.append(ccd_date);
        }
        , compute_ccd_initial_final: function() {
            let wid = this;
            //-
            //- initial
            //-
            let y = wid.current_date.getFullYear();
            let m = wid.current_date.getMonth();
            let ccd_initial = new Date(y,m, 1,0,0,0 );
            let o = (ccd_initial.getDay() - wid.date_offset + 7) % 7;
            // console.log("ccd_initial.getDay: " + ccd_initial.getDay() );
            // console.log("o: " + o );
            ccd_initial = new Date(ccd_initial.getTime() - o * wid.oneDayInMs);
            let value = appConvertDateToString(ccd_initial.getTime());
            // console.log(" - start " + value );
            //-
            //- final
            //-
            if (m<11){
                m = m + 1;
            } else {
                m = 0;
                y = y + 1;
            }
            let ccd_final = new Date(y,m, 1,0,0,0 );
            ccd_final = new Date(ccd_final.getTime() - wid.oneDayInMs);
            o = ((wid.date_offset - 1) - ccd_final.getDay() + 7) % 7;
            // console.log("ccd_final.getDay: " + ccd_final.getDay() );
            // console.log("o: " + o );
            ccd_final = new Date(ccd_final.getTime() + o * wid.oneDayInMs);
            value = appConvertDateToString(ccd_final.getTime());
            // console.log(" - end " + value );
            //-
            //-
            wid.ccd_initial = ccd_initial;
            wid.ccd_final   = ccd_final;
        }
        , draw_current_date_modifiers: function() {
            let wid = this;
            date_grid = wid.date_grid;
            date_grid.html("");
            date_grid
                .append($("<span>")
                        .append(app_getIcon("arrow_left", 15))
                        .attr('title',gRb("db.spacemr_space_people_book.update.title"))
                        .click(function(){
                            let y = wid.current_date.getFullYear();
                            let m = wid.current_date.getMonth();
                            if (m>0){
                                m = m - 1;
                            } else {
                                m = 11;
                                y = y - 1;
                            }
                            app_spacemr_space_people_book_calendar_current_date =
                                new Date(y,m, 1,0,0,0 );
                            wid.redraw();
                        })
                       )
                .append($("<span>")
                        .append(app_getIcon("arrow_right", 15))
                        .attr('title',gRb("db.spacemr_space_people_book.update.title"))
                        .click(function(){
                            let y = wid.current_date.getFullYear();
                            let m = wid.current_date.getMonth();
                            if (m<11){
                                m = m + 1;
                            } else {
                                m = 0;
                                y = y + 1;
                            }
                            app_spacemr_space_people_book_calendar_current_date =
                                new Date(y,m, 1,0,0,0 );
                            wid.redraw();
                        })
                       )
                .append(" ")
                .append(gRb("db.sys.calendar.month."+wid.current_date.getMonth())
                        + " " + wid.current_date.getFullYear())
            ;
            {
                let value = appConvertDateToString(wid.current_date.getTime());
                let name  = "spacemr_space_people_book__current_date";
                let id    = "id_"+name;
                let form = $("<form>")
                    .css('display', "inline-block")
                    .submit(function(){ return(false);} );
                let input = $('<input class="w3-input w3-border w3-round" size="9" type="text"/>')
                    .attr('name', name).attr('id', id).val(value)
                    .change(function(){
                        let d   = new Date(appConvertStringToTimestamp(input.val()));
                        let ccd = new Date(wid.current_date.getFullYear(),wid.current_date.getMonth(), 1,0,0,0 );
                        if (d.getTime() != ccd.getTime()) {
                            app_spacemr_space_people_book_calendar_current_date =
                                new Date(d.getFullYear(),d.getMonth(), 1,0,0,0 );
                            wid.redraw();
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
                                wid.redraw();
                            }
                                  )
                    );
                date_grid
                    .append(" ")
                    .append(form);
            }
        }
    };
    return calendar_manager;
}


//-
function app_spacemr_space_people_book_form_calendar() {
    //-
    var wf = app_workflow_get("spacemr_space_people_book_workflow");
    if (wf == undefined) {
        // alert("workflow spacemr_space_people_book_workflow undefined");
        app_spacemr_space_people_book_doInitialize(app_spacemr_space_people_book_form_calendar);
        return;
    }
    //-
    var spacemr_space_id  = getLocationParameterByName('spacemr_space_id');
    var spacemr_people_id = getLocationParameterByName('spacemr_people_id');
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
        calendar_manager.wf = wf;
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
            wid.bookings_verifier = app_spacemr_space_people_book_getVerifier();
            //-
            app_doRequestMappingRequest("spacemr_space_people_book/spacemr_space_people_book_list_sitting"
                                        , { "spacemr_space_id": wid.spacemr_space_id
                                            , "ccd_initial":    wid.ccd_initial.getTime()
                                            , "ccd_final":      wid.ccd_final.getTime()
                                          }
                                        , function(content) {
                                            // console.log(" -- spacemr_space_people_book_list_sitting");
                                            // console.log(app_JSONStringify(content));
                                            // wid.render_bookings(content);
                                            wid.bookings_verifier.init(content, wid.wf);
                                            wid.render_data();
                                        });
            //-
            //- days of the month rendering
            //-
        };
        calendar_manager.render_data_single_day = function(d, ccd, ccd_date, ccd_time, ccd_day){
            let wid = this;
            //-
            wid.bookings_verifier.compute_occupation(ccd);
            //-
            let qp = {
                "where":{"spacemr_space_id": wid.spacemr_space_id
                         , "current_date":    ccd_time
                         , "recursive_space": true
                        }
                ,"order": [ { "column": "stato" } ]
                ,"tableid":"spacemr_space_people_book_list"}
            ;
            let ccd_date_div = $("<div>")
                .append(ccd_date);
            d.append( ccd_date_div );
            let ccd_date_div_menu_function = function(evt, tooltip_div, refresh_callback) {
                http://localhost:8080/#?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_insert&spacemr_space_id=7  ;
                //-
                //- add bookings
                //-
                tooltip_div.append(
                    app_ui_standard_button()
                        .append(
                            app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_form_insert"
                                                 + "&spacemr_space_id="+wid.spacemr_space_id
                                                 + "&current_date="+ccd_time
                                                )
                                .text(gRb("db.spacemr_space_people_book.insert.title"))
                        )
                ).append(" ");
                //-
                //- show on map
                //-
                if (wid.map_id_default!=undefined){
                    tooltip_div.append(
		        app_ui_standard_button()
                            .append(
                                app_ui_clickableLink("?page=app_spacemr_space_map__app_spacemr_space_map_view_map_people"
                                                     + "&spacemr_space_map_id="+wid.map_id_default
                                                     + "&app_spacemr_space_people_book_map_current_date="+ccd_time
                                                     + "&spacemr_space_child_id="+wid.spacemr_space_id
                                                     + "&app_spacemr_space_people_view_mode=bookings"
                                                    )
                                    .append(gRb("db.spacemr_space_map.show.on.map"))
                            )
                        //-
                    ).append(" ");
                }
                //-
                //- bookings
                //-
                tooltip_div.append(
		    app_ui_standard_button()
                        .append(
                            app_ui_clickableLink("?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list"
                                                 + "&spacemr_space_id="+wid.spacemr_space_id
                                                 + "&qparams="+encodeURI(app_JSONStringify(qp))
                                                )
                                .append(gRb("db.spacemr_space_people_book..plural"))
                        )
                ).append(" ");
                ;
            };
            app_tooltip_set_click(ccd_date_div, ccd_date_div_menu_function, null);
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
            let bookings = wid.bookings_verifier.space_root.bookings;
            jQuery.each(bookings, function(bookingi, booking){
                add_statusIndex(booking.stato, booking.people_number);
            });
            // console.log(" -- bookings: " + app_JSONStringify(bookings));
            // console.log(" -- root: " + app_JSONStringify(wid.bookings_verifier.space_root));
            // console.log(" -- statusIndex: " + app_JSONStringify(statusIndex));
            jQuery.each(wid.wf.statusList, function(statoi, stato){
                let n = statusIndex[stato];
                // console.log(" -- stato: " + app_JSONStringify(statusIndex));
                if (n != undefined) {
                    if (wid.wf.defaultSearchStatuses == undefined
                        || wid.wf.defaultSearchStatuses.includes(stato)) {
			let menu_function = null
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
							 + "&app_spacemr_space_people_view_mode=bookings"
							)
					.append(gRb("db.spacemr_space_map.show.on.map"))
					.append(" ")
					.append(app_getIcon("map_black", 15))
					)
				);
			    };
                        }

                        let icon =app_getIcon("circle", 15).css("color", wid.wf.statusIndex[stato].color);
                        wid.bookings_verifier.tooltip_bookings(icon, wid.spacemr_space_id, stato
                                                               , function() {
                                                                   wid.redraw();
                                                               }, ccd
							       , menu_function
							      );
                        dc.append($("<div>")
                                  .append(icon)
                                  .append(
                                      "" + n + (window.innerWidth < 900 ? "" : " - " + stato))
                                 )
                        ;
                    }
                }
            });
            // if (ccd_date == 15) {
            //     console.log(" --- date: "+ccd_date+"\n --- root: " + app_JSONStringify(wid.bookings_verifier.space_root) + "\n exceeds: " + wid.bookings_verifier.space_root.exceeds);
            // }
            if (wid.bookings_verifier.space_root.exceeds) {
                let d =
                    $("<div>")
                    .css("background-color","red")
                    .append(gRb("db.spacemr_space_people_book.sys.exceeds"))
                    ;
                dc.append(d);
                let exceeds_spaces = wid.bookings_verifier.space_root.exceeds_spaces;
                let df = function(evt, tooltip_div){
                    tooltip_div.append($("<textarea>")
				       .css("width", "200pt")
				       .css("height", "200pt")
				       .val(JSON.stringify(exceeds_spaces,null,2))
			       	      );
                }
                app_tooltip_set_click_recursive(d, df, null);
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
    if ( spacemr_people_id != "") {
        page.append(app_spacemr_people_tabs_get(spacemr_people_id));
    }
    if ( spacemr_responsible_id != "") {
        page.append(app_spacemr_people_tabs_get(spacemr_responsible_id));
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
    appSetPage(page, gRb("db.spacemr_space_people_book.calendar"));
    //-
}
