function app_app_login_doInitialize(callback) {
    // log("app_user page initialization...");
    callback();
}


function app_app_login_form() {
    //-
    //-
    var do_login_on_keypressed = function(event){
            if(event.which == 13) {
                do_login();
            }
    };
    //-
    //-
    $("#app_pagecontent_login").show("fast");
    //
    $("#id_user_name_label").text(gRb("db.app_user.user_name"));
    $("#id_password_label").text(gRb("db.app_user.password"));
    $("#id_log_in_button").val(gRb("db.sys.doLogIn"));
    //-
    // $("#id_fm").attr("action", url);
    //-
    var thePage = $('<div>');
    //-
    //-
    appSetPage(thePage, gRb("db.app_user.login.title"));
    // app_setMessage("");
    //-
    $("#id_user_name").focus();
    // app_test_showMobilePropertiesInfo();
}

function app_app_login_setUser() {
    if (sys_session.userData.default_user) {
        $('#app_login_area')
            .html($('<a/>')
                  .click(function(event){
                      let auth_single_sign_on_protected_url =
                          sys_session.userData["auth_single_sign_on_protected_url"];
                      if (auth_single_sign_on_protected_url != undefined) {
			  var url = ""
			      + auth_single_sign_on_protected_url
			      + location.hash
			  ;
			  window.location=url;
                      } else {
                          app_app_login_form();
                      }
                  })
                  .text("login"))

    } else {
        $('#app_login_area')
            .html($('<a/>')
                  .click(function(event){
                      app_app_login_doLogout();
                  })
                  .text("logout " 
                        + sys_session.userData.user_name
                       ))
        ;        
    }
    app_initialization_setMainMenu();
    //-
    //-
    if (sys_session.userData.app_first_login_note) {
        var page = $('<div/>');
        var grid = app_ui_standard_getGrid(page);
        grid.append(sys_session.userData.app_first_login_note);
        $('#app_first_login_note').html(page);
        var buttonline = app_ui_standard_appendRow(grid);
        buttonline.append(" ").append(
            app_ui_standard_button()
                .text(gRb("db.sys.acceptance"))
                .click(function() {
                    app_doRequestMappingRequest("app_user/app_user_set_app_first_login_note_acceptance"
                                                , { }
                                                , function(content) {
                                                    $('#app_first_login_note').hide("slow");
                                                });
                    
                })
        );
    }
    //-
    //- login setups (to run also on first page load)
    //-
    // log("   ---  in app_app_login_setUser")
    Object.keys(sys_loginhooks).forEach(function(key) {
        // log("   --- app_app_login_setUser sys_loginhooks " + key)
        var value = sys_loginhooks[key];
        value();
        // iteration code
    });
    //-
}

function app_app_login_doLogout() {
    app_doRequestMappingRequest("app_user/app_user_logout"
                                , {}
                                , function(content) {
                                    app_initialization_setHashAndGoToPage("");
                                    app_app_login_doGetLoggedUserInfo()
                                });
}

function app_app_login_doGetLoggedUserInfo(callback) {
    var data =  {};
    app_doRequestMappingRequest("app_user/app_user_getloggeduserinfo"
                                , data
                                , function(content) {
                                    // log(app_JSONStringify(content))
                                    sys_session.userData = content.userData;
                                    app_app_login_setUser();
                                    if (callback != undefined) {
                                        callback();
                                    }
                                });
}
