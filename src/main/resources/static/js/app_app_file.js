function app_app_file_doInitialize(callback) {
    log("app_file page initialization...");
    callback();
}

//-
function app_app_file_form_insert() {
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    var tabsDiv = $("<div>");
    // var app_file_super_id = getLocationParameterByName('app_file_super_id');
    // if (app_file_super_id != "") {
    //     form.append(tabsDiv);
    //     app_doRequestMappingRequest("app_file_super/app_file_super_get"
    //                                 , { app_file_super_id: app_file_super_id}
    //                                 , function(content) {
    //                                     var app_file_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_file_super_tabs(app_file_super))
    //                                     ;
    //                                 });
    //     form.append($('<input name="app_file_super_id" id="id_app_file_super_id" type="hidden" />')
    //                 .val(app_file_super_id)
    //                );
    //-
    var grid = app_ui_standard_getGrid(form);
    //-
    //-
    app_ui_standard_appendFieldInteger(grid, "id_owner_object_type", "owner_object_type", gRb("db.app_file.owner_object_type"),"");
    app_ui_standard_appendFieldInteger(grid, "id_owner_object_id", "owner_object_id", gRb("db.app_file.owner_object_id"),"");
    app_ui_standard_appendFieldText(grid, "id_file_name", "file_name", gRb("db.app_file.file_name"),"");
    app_ui_standard_appendFieldInteger(grid, "id_file_size", "file_size", gRb("db.app_file.file_size"),"");
    //-
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    buttonline.append(" ").append(
        app_ui_standard_button()
            .text(gRb("db.sys.doInsert"))
            .click(function(event) {
                var validate = app_app_file_form_insert_validate();
                if (validate.children().length != 0) {
                    doPopupHtml(validate);
                } else {
                    app_app_file_form_insert_sendData();
                }
            })
    );
    //-
    appSetPage(form, gRb("db.app_file.insert.title"));
    // }
}
//-
function app_app_file_form_insert_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_owner_object_type').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_file.owner_object_type") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_owner_object_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_file.owner_object_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_file_size').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_file.file_size") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_app_file_form_insert_sendData() {
    var data =  { 
         owner_object_type:   $('#id_owner_object_type').val()
         , owner_object_id:   $('#id_owner_object_id').val()
         , file_name:   $('#id_file_name').val()
         , file_size:   $('#id_file_size').val()
    };
    //-
    app_doRequestMappingRequestSync("app_file/app_file_insert"
                                , data
                                , function(content) {
                                    var app_file_id = content.app_file_id;
                                    app_initialization_setHashAndGoToPage(
                                            "?page=app_app_file__app_app_file_form_update"
                                            + "&app_file_id="+app_file_id);
                                });
    //-
}
//-
function app_app_file_list() {
    appSetPage($("<p/>").text(gRb("db.sys.invalidValue")), gRb("db.app_file.list.title"));
    //-
    var page    = $("<div>");
    var tableid = "table_app_file_list";
    //-
    var buttonsCell = function(tr, mapIdName, row) {
        //-
        //- icons to render at beginning of every row
        //-
        var edit =
            $('<abbr>').attr('title',gRb("db.app_file.update.title")).append(
                app_ui_clickableLink("?page=app_app_file__app_app_file_form_update"
                                     + "&app_file_id="+row[mapIdName["app_file_id"]]
                                    )
                    .append(app_getIcon("edit", 15))
            );
        //-
        //- link to "super"
        //- link to "sub"
        //-
        tr.prepend($('<td>')
                   .append(edit)
                   // .append(app_file_super)
                   // .append(app_file_sub)
                   );
        // log(" buttonsCell: " + );
    }
    var tableMattoni_options = {tableid: tableid
                                , controller: "app_file/app_file_list"
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
        var qvalue = getLocationParameterByName("qparams");
        if (qvalue != "") {
            tableMattoni_options.content.qparams = JSON.parse(qvalue);
        }
    }
    // -- if this is the "list" of detail of the "super" master, add the super in the where clause
    // var app_file_super_id = getLocationParameterByName('app_file_super_id');
    // if (app_file_super_id != "") {
    //     app_doRequestMappingRequest("app_file_super/app_file_super_get"
    //                                 , { app_file_super_id: app_file_super_id}
    //                                 , function(content) {
    //                                     var app_file_super = content.obj;
    //                                     tabsDiv
    //                                         .append(app_app_file_super_tabs(app_file_super))
    //                                         .trigger('create')
    //                                     ;
    //                                 });
    //     tableMattoni_options.content.qparams.where.app_file_super_id = app_file_super_id;
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
        if (app_userHasPermission("db_app_file_insert")) {
            buttonline.append(" ").append(
                app_ui_standard_button()
                    .text(gRb("db.app_file.insert.title"))
                    .click(function(event) {
                        app_initialization_setHashAndGoToPage("?page=app_app_file__app_app_file_form_insert"
                                                              // + "&app_file_super_id=" + app_file_super_id
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
        app_where_append_integer(fieldcontain, gRb("db.app_file.owner_object_type"), "owner_object_type", divTable);
        app_where_append_integer(fieldcontain, gRb("db.app_file.owner_object_id"), "owner_object_id", divTable);
        app_where_append_string(fieldcontain, gRb("db.app_file.file_name"), "file_name", divTable);
        app_where_append_integer(fieldcontain, gRb("db.app_file.file_size"), "file_size", divTable);
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
    appSetPage(page, gRb("db.app_file.list.title"));
    //-
}
//-
function app_app_file_tabs(app_file) {
    var rv = $('<div class="w3-bar w3-theme-d4">');
    if (app_userHasPermission("db_app_file_read")) {
        var ls = "?page=app_app_file__app_app_file_form_update&app_file_id="+app_file.app_file_id+"";
        rv.append(
            app_ui_clickableLink(ls)
                .attr("class", "w3-bar-item w3-button")
                .text(gRb("db.app_file..single") + ": " + app_file.name)
        );
    }
    // if (app_userHasPermission("db_app_file_sub_read")) {
    //     var ls = "?page=app_app_file_sub__app_app_file_sub_list&app_file_id="+app_file.app_file_id+"";
    //     rv.append(
    //         app_ui_clickableLink(ls)
    //             .attr("class", "w3-bar-item w3-button")
    //             .text(gRb("db.app_file_sub.list.title"))
    //     );
    // }
    return(rv);
}
//-
function app_app_file_form_update() {
    var app_file_id = getLocationParameterByName('app_file_id');
    var form = $('<form name="fm"/>');
    form.submit(function(){ return(false);} );
    //-
    form.append(gRb("db.sys.retrievingData"));
    //-
    appSetPage(form, gRb("db.app_file.update.title"));
    //-
    app_doRequestMappingRequest("app_file/app_file_get"
                                , { app_file_id: app_file_id}
                                , function(content) {
                                    app_app_file_form_update_data(content);
                                });
}

//-
var app_app_file_form_update_old_data = undefined;
//-
function app_app_file_form_update_data(content) {
    var page = $('<div/>');
    var form = $('<form name="fm"/>');
    var logTable= undefined;
    form.submit(function(){ return(false);} );
    //-
    //- form.append($('<pre/>').append(app_JSONStringify(content)));
    //-
    var obj = content.obj;
    page.append(app_app_file_tabs(obj));
    //-
    var grid = app_ui_standard_getGrid(form);
    page.append(form);
    //-
    // app_ui_standard_appendFieldHidden(form, "id_app_file_id", "app_file_id", "label", obj.app_file_id);
    app_ui_standard_appendFieldHidden(form, "id_app_file_id", "app_file_id", "label", obj.app_file_id);
    app_ui_standard_appendFieldInteger(grid, "id_owner_object_type", "owner_object_type", gRb("db.app_file.owner_object_type"),obj.owner_object_type);
    app_ui_standard_appendFieldInteger(grid, "id_owner_object_id", "owner_object_id", gRb("db.app_file.owner_object_id"),obj.owner_object_id);
    app_ui_standard_appendFieldText(grid, "id_file_name", "file_name", gRb("db.app_file.file_name"),obj.file_name);
    app_ui_standard_appendFieldInteger(grid, "id_file_size", "file_size", gRb("db.app_file.file_size"),obj.file_size);
    //-
    //-
    if (obj.fileReadable) {
        var downloadArea = $("<div>")
            .append(
                $("<a/>")
                    .attr("href","spacemr_space_map/app_file_get_content?app_file_id="+obj.app_file_id)
                    .attr("target","_blank")
                    .append(gRb("db.app_file.update.download_file"))
            );
        app_ui_standard_appendElement(grid
                                      , ""
                                      , downloadArea
                                     );
    }
    //-
    if (app_userHasPermission("db_app_file_update")) {
        var uploadArea = $("<div>");
        app_ui_standard_appendElement(grid, gRb("db.app_file.update.upload_new_file"), uploadArea);
        //-
        //- upload fields usage
        //-
        var uconf = {
            id_prefix: "app_file"
            , upload_url: "app_file/app_file_update_upload?app_file_id="+obj.app_file_id
            , done: function(data){
                // console.log("hello done! " + app_JSONStringify(data));
                app_initialization_setHashAndGoToPage("?page=app_app_file__app_app_file_form_update"
                                                      + "&app_file_id="+obj.app_file_id);
            }
        }
        uploadArea.append("deve funzionare anche se i numeri sono NULL<br/>");
        uploadArea.append("System property: 'configuration_docs_directory'<br/>");
        uploadArea.append("Creare la cartella dati di default se manca??");
        uploadArea.append($('<div class="ui-block-b">')
                    .append(app_app_file_form_update_doUpdate_uploadForm(uconf))
                   );
    }
    //-
    var buttonline = app_ui_standard_appendRow(grid);
    if (app_userHasPermission("db_app_file_update")) {
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
                    app_app_file_form_update_doUpdate(onSuccess);
                })
        );
    }
    if (app_userHasPermission("db_app_file_delete")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.deleteThisRecord"))
                .click(function(event) {
                    var answer = confirm(gRb("db.sys.deleteThisRecordReally"));
                    if (answer) {
                        // log("ok do delete");
                        var data =  { 
                            app_file_id:   $('#id_app_file_id').val()
                        };
                        log(app_JSONStringify(data));
                        //-
                        app_doRequestMappingRequestSync("app_file/app_file_delete"
                                                        , data
                                                        , function(content) {
                                                            app_setMessageNextPage(gRb("db.sys.recordDeleted"));
                                                            app_initialization_setHashAndGoToPage("?page=app_app_file__app_app_file_list");
                                                        });
                        //-
                    }
                })
        );
    }
    if (app_userHasPermission("db_app_file_logs")) {
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.showLogs"))
                .click(function() {
                    if (logTable == undefined ) {
                        logTable =  
                        logTable =  $("<div>");
                        appGetWidget("custom.tableMattoni"
                                     , logTable
                                     , {tableid: "table_app_file_logs"
                                        , controller: "app_file/app_file_logs"
                                        , buttonsCell: function(tr, mapIdName, row) {
                                            tr.prepend($('<td>').append(""));
                                        }
                                        , content: {
                                            qparams: {
                                                app_file_id: obj.app_file_id
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
    appSetPage(page, gRb("db.app_file.update.title") + " " + obj.app_file_id);
    //-
    // app_test_showMobilePropertiesInfo();
    app_app_file_form_update_old_data = app_app_file_form_update_getData();
}


function app_app_file_form_update_doUpdate_uploadForm(uconf) {
    var prfx = uconf.id_prefix;
    var rv = $('<div id="'+prfx+'_file_upload_area" class="ui-corner-all ui-shadow ui-btn-inline">');
    rv
        .append($('<form>')
                .attr('name', prfx+'_file_upload_form')
                .attr('id', prfx+'_file_upload_form')
                .attr('enctype', 'multipart/form-data')
                .append($('<input type="file" name="file" />')
                        .css("margin-left", "6px")
                        .change(function(){
                            var file = this.files[0];
                            var name = file.name;
                            var size = file.size;
                            var type = file.type;
                            //Your validation
                            var m = "" + file.name
                                + ", "+gRb("db.app_file.update.size")+": " + file.size
                                + ", "+gRb("db.app_file.update.type")+": " + file.type
                            ;
                            $("#"+prfx+"_divprogress").text(m);
                        })
                       )
                .append($('<input type="button" class="ui-corner-all ui-shadow ui-btn ui-btn-inline" />')
                        .val(gRb("db.sys.submit"))
                        .click(function(){
                            app_app_file_form_update_doUpdate_doFileUpload(uconf);
                        })
                       )
                .append($('<input type="hidden" name="'+prfx+'_submitup" id="'+prfx+'_submitup"/>')
                        .val("submitup")
                       )
                .append($('<div id="'+prfx+'_divprogress">'))
               )
    ;
    return(rv);
}
function app_app_file_form_update_doUpdate_doFileUpload(uconf) {
    //-
    //- required uconf parameters
    //-   uconf.id_prefix  - prefix to pretend to the fields_ids
    //-   uconf.upload_url - the url serving the upload
    //-   uconf.done(data) - function to call on finish (error or not - check data.status)
    //-
    // alert(" hello " + uconf.id_prefix);
    //-
    var divprogress = $("#"+uconf.id_prefix+"_divprogress");
    var progress = $('<progress>');
    var formData = new FormData(document.getElementById(uconf.id_prefix+'_file_upload_form'));
    // formData.set("enctype", "multipart/form-data");
    // console.log("uconf.id_prefix+'_file': " + uconf.id_prefix+'_file');
    // console.log("document.getElementById(uconf.id_prefix+'_file').value: " + document.getElementById(uconf.id_prefix+'_file').value);
    // formData.append("file", document.getElementById(uconf.id_prefix+'_file').value);
    //-
    $.ajax({
        url: uconf.upload_url,
        type: 'POST',
        xhr: function() {
            var myXhr = $.ajaxSettings.xhr();
            if(myXhr.upload){ // Check if upload property exists
                // For handling the progress of the upload
                myXhr.upload
                    .addEventListener('progress'
                                      ,function(e){
                                          if(e.lengthComputable){
                                              progress.attr({value:e.loaded,max:e.total});
                                          }
                                      }
                                      ,false); 
            }
            return myXhr;
        },
        beforeSend: function() {
            divprogress.html(progress);
        },
        success: function() {
            divprogress.text(gRb("db.app_file.update.file_correctly_sent"));
        },
        error: function(){
            divprogress.text(gRb("db.app_file.update.error_uploading"));
        },
        data: formData,
        cache: false,
        contentType: false,
        processData: false
    })
        .done(function( data ) {
            if (uconf.done != undefined) {
               uconf.done(data);
            }
        });

}


//-
function app_app_file_form_update_doUpdate(onSuccessCallback) {
    var validate = app_app_file_form_update_validate();
    if (validate.children().length != 0) {
        doPopupHtml(validate);
    } else {
        var data = app_app_file_form_update_getData();
        // console.log(app_JSONStringify(app_app_file_form_update_old_data));
        // console.log(app_JSONStringify(data));
        if (app_JSONStringify(app_app_file_form_update_old_data) == app_JSONStringify(data)) {
            // alert("nothing to do");
        } else {
            // alert("changed!")
            app_doRequestMappingRequestSync("app_file/app_file_update"
                                        , data
                                        , function(content) {
                                            app_app_file_form_update_old_data = app_app_file_form_update_getData();
                                            app_setMessage(gRb("db.app_file.update.Updated"));
                                            onSuccessCallback();
                                        }
                                       );
            //-
        }
    }
    return(validate);
}

//-
function app_app_file_form_update_validate() {
    var rv = $('<ul/>');
    sys_number_integer_validation.test($('#id_owner_object_type').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_file.owner_object_type") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_owner_object_id').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_file.owner_object_id") + " - " + gRb("db.sys.invalidValue"))));
    sys_number_integer_validation.test($('#id_file_size').val()) 
        || (rv.append($("<li/>").text(gRb("db.app_file.file_size") + " - " + gRb("db.sys.invalidValue"))));
    return rv;
}
//-
function app_app_file_form_update_getData() {
    var data =  { 
         app_file_id:   $('#id_app_file_id').val()
         , owner_object_type:   $('#id_owner_object_type').val()
         , owner_object_id:   $('#id_owner_object_id').val()
         , file_name:   $('#id_file_name').val()
         , file_size:   $('#id_file_size').val()
    };
    //- log(app_JSONStringify(data));
    return(data);
}
