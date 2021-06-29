<#--  
  usage:
      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable app_user app_user" 
      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable app_permission app_user" 
      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable app_role app_user" 
      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable app_group app_user" 
      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable app_log app_user" 
      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable app_system_property" 
      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable app_user_property" 

      cd /dati/bin/workspace/spacemr; \
        gradle run -Pargs="toolDb describeTable spacemr_questionario spacemr_questionario" 
  parameters:
    -Pargs="toolDb describeTable app_user app_user user_name"
    -Pargs="toolDb describeTable tableName mvcPrefix"
    that means
-->

<#assign tname   = table.getName()>
<#assign tnameid = tname+"_id">
<#assign tnameU  = tools.stringUpcaseFirst(tname) >
<#assign mvcPrefixU = tools.stringUpcaseFirst(mvcPrefix) >

Table content:
<#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  ${cname} ${ctype}
</#foreach>


------------
src/main/resources/language/language_it.json

    //-
    //-
    //-
    , "db.${tname}..single"        : "${tnameU}"
    , "db.${tname}..plural"        : "${tnameU}s"
    , "db.${tname}.insert.title"   : "Nuova ${tnameU}"
    , "db.${tname}.update.title"   : "Modifica ${tnameU}"
    , "db.${tname}.update.Updated" : "${tnameU} Aggiornato"
    , "db.${tname}.list.title"     : "Elenco ${tnameU}s"
    //-
<#assign comma=""><#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#else>
    , "db.${tname}.${cname}"  : "${tools.stringUpcaseFirst(cname)}"
  </#if>
</#foreach>

------------------------
build/resources/main/static/js/app_configuration.js

    //-
    if (app_userHasPermission("db_${tname}_read")) {
        app_main_menu_addLink(gRb("db.${tname}.list.title")
                              , "?page=app_${mvcPrefix}__app_${tname}_list");
    }

------------
build/resources/main/static/index.html
    <script src="./js/app_${mvcPrefix}.js"></script>

------------
build/resources/main/static/js/app_${mvcPrefix}.js

function app_${mvcPrefix}_doInitialize(callback) {
    log("${tname} page initialization...");
    callback();
}
//-
function app_${tname}_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var ${tname}_super_id = getLocationParameterByName('${tname}_super_id');
    // if (${tname}_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("${tname}_super/${tname}_super_get"
    //                                 , { ${tname}_super_id: ${tname}_super_id}
    //                                 , function(content) {
    //                                     var ${tname}_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_${tname}_super_tabs(${tname}_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="${tname}_super_id" id="id_${tname}_super_id" type="hidden" />')
    //                 .val(${tname}_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
<#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#elseif ctype == "INTEGER" >
    app_ui_standard_appendFieldInteger(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#elseif ctype == "DECIMAL" >
    app_ui_standard_appendFieldDecimal(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#elseif ctype == "REAL" >
    app_ui_standard_appendFieldDecimal(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#elseif ctype == "DOUBLE" >
    app_ui_standard_appendFieldDecimal(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#elseif ctype == "BIT" >
    app_ui_standard_appendFieldCheckBox(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),false);
  <#elseif ctype == "VARCHAR" >
    app_ui_standard_appendFieldText(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#elseif ctype == "DATE" >
    app_ui_standard_appendFieldDate(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#elseif ctype == "TIMESTAMP" >
    app_ui_standard_appendFieldTimestamp(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#elseif ctype == "LONGVARCHAR" >
    app_ui_standard_appendFieldTextArea(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),"");
  <#else>
    // ------------------------------------------------
    // unknown type ${ctype} for column ${cname}
    // ------------------------------------------------
  </#if>
</#foreach>
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_${tname}_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_${tname}_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.${tname}.insert.title"));
    // }
}
//-
function app_${tname}_form_insert_validate() {
    var rv = $('<ul/>');
<#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#elseif ctype == "INTEGER" >
    sys_number_integer_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue"))));
  <#elseif ctype == "DECIMAL" >
    sys_number_decimal_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "REAL" >
    sys_number_decimal_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "DOUBLE" >
    sys_number_decimal_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "BIT" >
  <#elseif ctype == "VARCHAR" >
  <#elseif ctype == "DATE" >
    sys_dateFormatUi_validation.test($('#id_${cname}').val())
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue"))));
  <#elseif ctype == "TIMESTAMP" >
    sys_timestampFormatUi_validation.test($('#id_${cname}').val())
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "LONGVARCHAR" >
  <#else>
    // ------------------------------------------------
    // unknown type ${ctype} for column ${cname}
    // ------------------------------------------------
  </#if>
</#foreach>
    return rv;
}
//-
function app_${tname}_form_insert_sendData() {
    var data =  { 
<#assign comma=""><#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#else>
  <#if ctype == "INTEGER" >
         ${comma}${cname}:   $('#id_${cname}').val()
  <#elseif ctype == "DECIMAL" >
         ${comma}${cname}:   appConvertStringToDecimal($('#id_${cname}').val())
  <#elseif ctype == "REAL" >
         ${comma}${cname}:   appConvertStringToDecimal($('#id_${cname}').val())
  <#elseif ctype == "DOUBLE" >
         ${comma}${cname}:   appConvertStringToDecimal($('#id_${cname}').val())
  <#elseif ctype == "BIT" >
         ${comma}${cname}:   $('#id_${cname}').prop("checked")
  <#elseif ctype == "VARCHAR" >
         ${comma}${cname}:   $('#id_${cname}').val()
  <#elseif ctype == "DATE" >
         ${comma}${cname}:   appConvertStringToTimestamp($('#id_${cname}').val())
  <#elseif ctype == "TIMESTAMP" >
         ${comma}${cname}:   appConvertStringToTimestamp($('#id_${cname}').val())
  <#elseif ctype == "LONGVARCHAR" >
         ${comma}${cname}:   $('#id_${cname}').val()
  <#else>
    // ------------------------------------------------
    // unknown type ${ctype} for column ${cname}
    // ------------------------------------------------
  </#if><#assign comma=", ">
  </#if>
</#foreach>
    };
    //-
    app_doRequestMappingRequestSync("${mvcPrefix}/${tname}_insert"
                                , data
                                , function(content) {
                                    var ${tname}_id = content.${tname}_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_${mvcPrefix}__app_${tname}_form_update"
                                            + "&${tname}_id="+${tname}_id);
                                });
    //-
}
//-
function app_${tname}_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.${tname}.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_${tname}_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            app_ui_clickableLink("?page=app_${mvcPrefix}__app_${tname}_form_update"
                                 + "&${tname}_id="+row[mapIdName["${tname}_id"]]
                                )
            .attr('title',gRb("db.${tname}.update.title"))
            .append(app_getIcon("edit", 15))
        ;
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(${tname}_super)
                   // .append(${tname}_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "${mvcPrefix}/${tname}_list"
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
    // var ${tname}_super_id = getLocationParameterByName('${tname}_super_id');
    // if (${tname}_super_id != "") {
    //     app_doRequestMappingRequest("${tname}_super/${tname}_super_get"
    //                                 , { ${tname}_super_id: ${tname}_super_id}
    //                                 , function(content) {
    //                                     var ${tname}_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_${tname}_super_tabs(${tname}_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.${tname}_super_id = ${tname}_super_id;
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
        if (app_userHasPermission("db_${tname}_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.${tname}.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_${mvcPrefix}__app_${tname}_form_insert"
                                                              // + "&${tname}_super_id=" + ${tname}_super_id
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
<#assign comma=""><#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#else>
  <#if ctype == "INTEGER" >
        app_where_append_integer(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "DECIMAL" >
        app_where_append_decimal(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "REAL" >
        app_where_append_decimal(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "DOUBLE" >
        app_where_append_decimal(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "BIT" >
        app_where_append_boolean(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "VARCHAR" >
        app_where_append_string(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "DATE" >
        app_where_append_date(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "TIMESTAMP" >
        app_where_append_timestamp(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#elseif ctype == "LONGVARCHAR" >
        app_where_append_string(fieldcontain, gRb("db.${tname}.${cname}"), "${cname}", divTable);
  <#else>
    // ------------------------------------------------
    // unknown type ${ctype} for column ${cname}
    // ------------------------------------------------
  </#if><#assign comma=", ">
  </#if>
</#foreach>
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
                    + "?page=app_${mvcPrefix}__app_${tname}_list"
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
    appSetPage(page, gRb("db.${tname}.list.title"));
    //-
}
//-
function app_${tname}_tabs(${tname}) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_${tname}_read")) {
        rv.append(
            app_ui_clickableLink("?page=app_${tname}__app_${tname}_form_update&${tname}_id="+${tname}.${tname}_id+"")
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.${tname}..single") + ": " + ${tname}.name)
        );
    }
    // if (app_userHasPermission("db_${tname}_sub_read")) {
    //     var ls = "?page=app_${tname}_sub__app_${tname}_sub_list&${tname}_id="+${tname}.${tname}_id+"";
    //     rv.append(
    //         app_ui_clickableLink(ls)
    //             .attr("class", "w3-bar-item w3-button")
    //             .text(gRb("db.${tname}_sub.list.title"))
    //     );
    // }
    return(rv);
}
//-
function app_${tname}_form_update() {
    var ${tname}_id = getLocationParameterByName('${tname}_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.${tname}.update.title"));
    //-
    app_doRequestMappingRequest("${mvcPrefix}/${tname}_get"
                                , { ${tname}_id: ${tname}_id}
                                , function(content) {
                                    app_${tname}_form_update_data(content);
                                });
}

//-
var app_${tname}_form_update_old_data = undefined;
//-
function app_${tname}_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_${tname}_tabs(obj));
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
        app_${tname}_form_update_doUpdate(onSuccess);
    };
    // var topbuttonline = app_ui_standard_appendRow(grid);
    // if (app_userHasPermission("db_${tname}_update")) {
    //     topbuttonline.append(" ").append(
    //         app_ui_standard_button()
    //             .text(gRb("db.sys.update"))
    //             .click(updateFunction)
    //     );
    // }
    //-
    // app_ui_standard_appendFieldHidden(form, "id_${tname}_id", "${tname}_id", "label", obj.${tname}_id);
<#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
    app_ui_standard_appendFieldHidden(form, "id_${cname}", "${cname}", "label", obj.${cname});
  <#elseif ctype == "INTEGER" >
    app_ui_standard_appendFieldInteger(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),obj.${cname});
  <#elseif ctype == "DECIMAL" >
    app_ui_standard_appendFieldDecimal(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),obj.${cname});
  <#elseif ctype == "REAL" >
    app_ui_standard_appendFieldDecimal(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),obj.${cname});
  <#elseif ctype == "DOUBLE" >
    app_ui_standard_appendFieldDecimal(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),obj.${cname});
  <#elseif ctype == "BIT" >
    app_ui_standard_appendFieldCheckBox(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),obj.${cname});
  <#elseif ctype == "VARCHAR" >
    app_ui_standard_appendFieldText(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),obj.${cname});
  <#elseif ctype == "DATE" >
    app_ui_standard_appendFieldDate(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),appConvertDateToString(obj.${cname}));
  <#elseif ctype == "TIMESTAMP" >
    app_ui_standard_appendFieldTimestamp(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),appConvertTimestampToString(obj.${cname}));
  <#elseif ctype == "LONGVARCHAR" >
    app_ui_standard_appendFieldTextArea(grid, "id_${cname}", "${cname}", gRb("db.${tname}.${cname}"),obj.${cname});
  <#else>
    // ------------------------------------------------
    // unknown type ${ctype} for column ${cname}
    // ------------------------------------------------
  </#if>
</#foreach>
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_${tname}_update")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.update"))
                .click(updateFunction)
        );
    }
    if (app_userHasPermission("db_${tname}_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            ${tname}_id:   $('#id_${tname}_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("${mvcPrefix}/${tname}_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_${mvcPrefix}__app_${tname}_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_${tname}_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_${tname}_logs"
                                        , controller: "${mvcPrefix}/${tname}_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                ${tname}_id: obj.${tname}_id
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
    appSetPage(page, gRb("db.${tname}.update.title") + " " + obj.${tnameid});
    //-
    app_${tname}_form_update_old_data = app_${tname}_form_update_getData();
}

//-
function app_${tname}_form_update_doUpdate(onSuccessCallback) {
    var validate = app_${tname}_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_${tname}_form_update_getData();
        // console.log(app_JSONStringify(app_${tname}_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_${tname}_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("${mvcPrefix}/${tname}_update"
                                        , data
                                        , function(content) {
                                            app_${tname}_form_update_old_data = app_${tname}_form_update_getData();
                                            app_setMessage(gRb("db.${tname}.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_${tname}_form_update_validate() {
    var rv = $('<ul/>');
<#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#elseif ctype == "INTEGER" >
    sys_number_integer_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue"))));
  <#elseif ctype == "DECIMAL" >
    sys_number_decimal_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "REAL" >
    sys_number_decimal_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "DOUBLE" >
    sys_number_decimal_validation.test($('#id_${cname}').val()) 
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "BIT" >
  <#elseif ctype == "VARCHAR" >
  <#elseif ctype == "DATE" >
    sys_dateFormatUi_validation.test($('#id_${cname}').val())
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue"))));
  <#elseif ctype == "TIMESTAMP" >
    sys_timestampFormatUi_validation.test($('#id_${cname}').val())
        || (rv.append($("<li/>").text(gRb("db.${tname}.${cname}") + " - " + gRb("db.sys.invalidValue") )));
  <#elseif ctype == "LONGVARCHAR" >
  <#else>
    // ------------------------------------------------
    // unknown type ${ctype} for column ${cname}
    // ------------------------------------------------
  </#if>
</#foreach>
    return rv;
}
//-
function app_${tname}_form_update_getData() {
    var data =  { 
<#assign comma=""><#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if ctype == "INTEGER" >
         ${comma}${cname}:   $('#id_${cname}').val()
  <#elseif ctype == "DECIMAL" >
         ${comma}${cname}:   appConvertStringToDecimal($('#id_${cname}').val())
  <#elseif ctype == "REAL" >
         ${comma}${cname}:   appConvertStringToDecimal($('#id_${cname}').val())
  <#elseif ctype == "DOUBLE" >
         ${comma}${cname}:   appConvertStringToDecimal($('#id_${cname}').val())
  <#elseif ctype == "BIT" >
         ${comma}${cname}:   $('#id_${cname}').prop("checked")
  <#elseif ctype == "VARCHAR" >
         ${comma}${cname}:   $('#id_${cname}').val()
  <#elseif ctype == "DATE" >
         ${comma}${cname}:   appConvertStringToTimestamp($('#id_${cname}').val())
  <#elseif ctype == "TIMESTAMP" >
         ${comma}${cname}:   appConvertStringToTimestamp($('#id_${cname}').val())
  <#elseif ctype == "LONGVARCHAR" >
         ${comma}${cname}:   $('#id_${cname}').val()
  <#else>
    // ------------------------------------------------
    // unknown type ${ctype} for column ${cname}
    // ------------------------------------------------
  </#if><#assign comma=", ">
</#foreach>
    };
    //- log(app_JSONStringify(data));
    return(data);
}

------------
src/main/java/it/unimo/app/controller/${mvcPrefixU}Controller.java

package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
//-
/*
   cd /dati/bin/workspace/tmp/mvc; gradle run
*/
@Controller
public class ${mvcPrefixU}Controller {
   @Autowired
   private AppControllerTools appControllerTools;
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private AppSessionTools appSessionTools;
   @Autowired
   private Tools tools;
   @Autowired
   private ${tnameU} ${tname};
   // @Autowired
   // private ${tnameU}_super ${tname}_super;
   //-
   /**
    *  http://localhost:8080/${mvcPrefix}/${tname}_insert
    */
   @Transactional
   @RequestMapping(value="${mvcPrefix}/${tname}_insert")
   public void ${tname}_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_${tname}_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int id = ${tname}.insert(content);
      app_log.writeLog(${tname}, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("${tname}_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    */
   @RequestMapping(value="${mvcPrefix}/${tname}_list/**")
   public void ${tname}_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_${tname}_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      String              outputtype = qparams.optString("outputtype","json");
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)${tname}.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.${tname}"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("${tname}_id", "id");
      //-
      //-
      AppListTableMapper mapper = null;
      if (outputtype.equals("csv")) {
         mapper = qtm.getMapperCsv(response.getWriter(), appSessionTools.getUserData(session));
         response.setContentType("text/csv");
      } else {
         mapper = qtm.getMapperJson();
      }
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns(${tname}.getComputedColumnsMap(), "${tname}");
      //-
      //- where
      //-
      {
         // qtm.setWhereIntegerEqual("${tname}_super_id",  "${tname}.${tname}_super_id");
<#assign comma=""><#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#else>
  <#if ctype == "INTEGER" >
         qtm.setWhereInteger("${cname}",  "${tname}.${cname}");
  <#elseif ctype == "DECIMAL" >
         qtm.setWhereDecimal("${cname}",  "${tname}.${cname}");
  <#elseif ctype == "REAL" >
         qtm.setWhereDecimal("${cname}",  "${tname}.${cname}");
  <#elseif ctype == "DOUBLE" >
         qtm.setWhereDecimal("${cname}",  "${tname}.${cname}");
  <#elseif ctype == "BIT" >
         qtm.setWhereBoolean("${cname}",  "${tname}.${cname}");
  <#elseif ctype == "VARCHAR" >
         qtm.setWhereStringLike("${cname}",  "${tname}.${cname}");
  <#elseif ctype == "DATE" >
         qtm.setWhereDate(      "${cname}",  "${tname}.${cname}");
  <#elseif ctype == "TIMESTAMP" >
         qtm.setWhereDate(      "${cname}",  "${tname}.${cname}");
  <#elseif ctype == "LONGVARCHAR" >
         qtm.setWhereStringLike("${cname}",  "${tname}.${cname}");
  <#else>
         // ------------------------------------------------
         // unknown type ${ctype} for column ${cname}
         // ------------------------------------------------
  </#if><#assign comma=", ">
  </#if>
</#foreach>
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from ${tname}");
      // qs.append("\n ,    ${tname}_super");
      // qtm.addAnd(" ${tname}_super.${tname}_super_id = ${tname}.${tname}_super_id");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      if (!outputtype.equals("csv")) {
         rv.put("list",       mapper.getJSON());
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   //-
   /**
    *  http://localhost:8080/${mvcPrefix}/${tname}_get?content={"${tname}_id", "1"}
    */
   @RequestMapping(value="${mvcPrefix}/${tname}_get")
   public void ${tname}_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_${tname}_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int ${tname}_id = content.getInt("${tname}_id");
      JSONObject obj = ${tname}.get(${tname}_id);
      rv.put("obj",obj);
      // JSONObject ${tname}_superj = ${tname}_super.get(obj.getInt("${tname}_super_id"));
      // rv.put("${tname}_super",${tname}_superj);
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   @Transactional
   @RequestMapping(value="${mvcPrefix}/${tname}_update")
   public void ${tname}_update(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_${tname}_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int ${tname}_id = content.getInt("${tname}_id");
      //-
      app_log.writeLog(${tname}, ${tname}_id, appSessionTools.getUser_name(session),content);
      //-
      ${tname}.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/${mvcPrefix}/${tname}_delete
    */
   @Transactional
   @RequestMapping(value="${mvcPrefix}/${tname}_delete")
   public void ${tname}_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_${tname}_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int ${tname}_id = content.getInt("${tname}_id");
      ${tname}.delete(${tname}_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    *  http://localhost:8080/${mvcPrefix}/${tname}_logs
    */
   @RequestMapping(value="${mvcPrefix}/${tname}_logs")
   public void ${tname}_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_${tname}_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("${tname}_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , ${tname}
                       , id
                       );
   }
}



------------
src/main/java/it/unimo/app/om/${tnameU}.java
package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppMapperJsonForLog;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
//-
public class ${tnameU} extends BaseObjectModel {
   //-
   //-
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private Tools tools;
   //-
   @SuppressWarnings("unchecked")
   public int insert(JSONObject content) throws Exception {
      Vector columns = (Vector<String>)getColumnsWrite().clone();
      // columns.add( "foreign_reference_id");
      MapSqlParameterSource np = getMapSqlParameterSource(content, columns);
      KeyHolder keyHolder = new GeneratedKeyHolder();
      String qs = ""
         + "insert into ${tname} ("
         + getSqlStringForInsert_fields(columns)
         + ") values ("
         + getSqlStringForInsert_values(columns)
         + ")"
         ;
      // System.out.println(" qs: " + qs );
      //for(Object k: np.getValues().keySet() ) { System.out.println("k.["+k+"]:["+np.getValues().get(k)+"]");}
      namedParameterJdbcTemplate.update(qs , np, keyHolder);
      int id = keyHolder.getKey().intValue();
      //-
      return(id);
   }
   //-
   //-
   public JSONObject get(int ${tname}_id) throws Exception {
      return(get(${tname}_id, getColumns()));
   }
   //-
   public JSONObject get(int ${tname}_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "${tname}_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "${tname}");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from ${tname}"
                         // + "     , ${tname}_super"
                         + " where ${tname}_id = ?"
                         // + "   and ${tname}_super.${tname}_super_id = ${tname}.${tname}_super_id"
                         , new Object[] { Integer.valueOf(${tname}_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      return(rv);
   }
   //-
   public JSONObject getLog(int ${tname}_id) throws Exception {
      AppMapperJsonForLog mapper = new AppMapperJsonForLog();
      jdbcTemplate.query("select * from ${tname} where ${tname}_id = ?"
                         , new Object[] { Integer.valueOf(${tname}_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("${tname}_id", content.getInt("${tname}_id"));
      String qs = 
         "update ${tname} set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where ${tname}_id = :${tname}_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   //-
   public void delete(int ${tname}_id) throws Exception {
      app_log.deleteLogs(this, ${tname}_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("${tname}_id", ${tname}_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from ${tname}  "
                 + " where ${tname}_id = :${tname}_id"
                 , np
                 );
   }
   //-
   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) throws Exception {
      MapSqlParameterSource np = new MapSqlParameterSource();
      for (String k: columns) {
         switch (k) {
<#assign comma=""><#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
         case "${cname}":
  <#if cname == tnameid >
  <#else>
  <#if ctype == "INTEGER" >
            np.addValue("${cname}", tools.jsonObject_getInteger(content, "${cname}"));
  <#elseif ctype == "DECIMAL" >
            np.addValue("${cname}", tools.jsonObject_getBigDecimal(content, "${cname}"));
  <#elseif ctype == "REAL" >
            np.addValue("${cname}", tools.jsonObject_getBigDecimal(content, "${cname}"));
  <#elseif ctype == "DOUBLE" >
            np.addValue("${cname}", tools.jsonObject_getBigDecimal(content, "${cname}"));
  <#elseif ctype == "BIT" >
            np.addValue("${cname}", tools.jsonObject_getBoolean(content, "${cname}"));
  <#elseif ctype == "VARCHAR" >
            np.addValue("${cname}", content.optString("${cname}", null));
  <#elseif ctype == "DATE" >
            np.addValue("${cname}", tools.jsonObject_getDate(content, "${cname}"));
  <#elseif ctype == "TIMESTAMP" >
            np.addValue("${cname}", tools.jsonObject_getDate(content, "${cname}"));
  <#elseif ctype == "LONGVARCHAR" >
            np.addValue("${cname}", content.getString("${cname}"));
  <#else>
            // ------------------------------------------------
            // unknown type ${ctype} for column ${cname}
            // ------------------------------------------------
  </#if><#assign comma=", ">
  </#if>
            break;
</#foreach>
         default:
            throw new Exception("column name ["+k+"] not found");
         }
      }
      return(np);
   }
   //-
   public Vector<String> getColumns() {
      return(_columns);
   }
   public Vector<String> getColumnsWrite() {
      return(_columnsWrite);
   }
   public Vector<String> getColumnsForList() {
      return(_columnsForList);
   }
   public HashMap<String,String> getComputedColumnsMap() {
      return(computedColumnsMap);
   }
   private static final Vector<String> _columns = new Vector<String>();
   private static final Vector<String> _columnsHiddenInList = new Vector<String>();
   private static final Vector<String> _columnsForList = new Vector<String>();
   private static final Vector<String> _columnsWrite = new Vector<String>();
   private static final HashMap<String,String> computedColumnsMap = new HashMap<String,String>();
   private static final String[] _columnsWrite_a = {
<#assign comma=""><#foreach c in table.getColumns()><#assign ctype=c.getSqlType()><#assign cname=c.getName()>
  <#if cname == tnameid >
  <#else>
      ${comma}"${cname}"<#assign comma=", ">
  </#if>
</#foreach>
   };
   private static final String[] _columns_toGet_a = {
      // "${tname}_super_name"
   };
   private static final String[] _columns_hidden_in_list = {
      // "${tname}_super_id"
   };
   static  {
      for (String c: _columnsWrite_a) {
         _columnsWrite.add(c);
         _columns.add(c);
      }
      for (String c: _columns_toGet_a) {
         _columns.add(c);
      }
      for (String c: _columns_hidden_in_list) {
         _columnsHiddenInList.add(c);
      }
      for (String c: _columns) {
         if (!_columnsHiddenInList.contains(c)) {
            _columnsForList.add(c);
         }
      }
   }
   static  {
      // computedColumnsMap.put("${tname}_super_name", "${tname}_super.name");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = ${tname}.user_name)"
      //         );
   }
}




--------------------------
src/main/java/it/unimo/app/Application.java
   @Bean
   public ${tnameU} ${tname}() throws Exception {
      ${tnameU} rv = new ${tnameU}();
      return rv;
   }

--------------------------
src/main/java/it/unimo/app/om/App_log.java
      registerClass(${tnameU}.class, 1xxx);

-------------------------- permissions
src/main/java/it/unimo/app/om/App_permission.java
      , "db_${tname}_read"
      , "db_${tname}_insert"
      , "db_${tname}_delete"
      , "db_${tname}_update"
      , "db_${tname}_logs"

-------------------------- on run set default mattoni
src/main/java/it/unimo/app/om/App_system_property.java
      ,{"table_mattoni__table_${tname}_logs", "", "{  \"pageSize\": \"50\",  \"columns\": [    \"date\",    \"user_name\",    \"parameter_values\"  ]}" }

<#--  
 reference file is:
  ~/lavoro/tmp/DbTools_templateTable.ftl
-->
