
function app_configuration_main_menu() {
    //-
    //-
    //-
    app_main_menu_addLink(gRb("db.spacemr_menu_queries.title")
                          , "?page=app_spacemr_toolsqueries__app_spacemr_toolsqueries_queries");
    if (app_userHasPermissionOnAnyGroup("db_spacemr_space_read")) {
    }
    //-
    //-
    if (app_userHasPermissionOnAnyGroup("db_spacemr_space_read")) {
        app_main_menu_addLink(gRb("db.spacemr_space.list.title")
                              , "?page=app_spacemr_space__app_spacemr_space_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_people_read")) {
        app_main_menu_addLink(gRb("db.spacemr_people.list.title")
                              , "?page=app_spacemr_people__app_spacemr_people_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_space_type_read")) {
        app_main_menu_addLink(gRb("db.spacemr_space_type.list.title")
                              , "?page=app_spacemr_space_type__app_spacemr_space_type_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_space_people_type_read")) {
        app_main_menu_addLink(gRb("db.spacemr_space_people_type.list.title")
                              , "?page=app_spacemr_space_people_type__app_spacemr_space_people_type_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_space_people_read")) {
        app_main_menu_addLink(gRb("db.spacemr_space_people.list.title")
                              , "?page=app_spacemr_space_people__app_spacemr_space_people_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_inventario_read")) {
        app_main_menu_addLink(gRb("db.spacemr_inventario.list.title")
                              , "?page=app_spacemr_inventario__app_spacemr_inventario_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_space_map_read")) {
        app_main_menu_addLink(gRb("db.spacemr_space_map.list.title")
                              , "?page=app_spacemr_space_map__app_spacemr_space_map_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_space_user_presence_read")) {
        app_main_menu_addLink(gRb("db.spacemr_space_user_presence.list.title")
                              , "?page=app_spacemr_space_user_presence__app_spacemr_space_user_presence_list");
    }
    //-
    if (app_userHasPermission("db_spacemr_space_people_book_read")) {
        app_main_menu_addLink(gRb("db.spacemr_space_people_book.list.title")
                              , "?page=app_spacemr_space_people_book__app_spacemr_space_people_book_list");
    }
    
    //-
    //-
    //-
    //-
    //-
    if (app_userHasPermission("db_app_user_read")) {
        app_main_menu_addDivider(gRb("db.system.menu.system"));
    }
    //-
    //-
    //-
    //-
    //-
    // if (app_userHasPermission("db_app_group_role_user_read")) {
    //     app_main_menu_addLink(gRb("db.app_group_role_user.list.title")
    //                           , "?page=app_app_user__app_app_group_role_user_list");
    // }
    //-
    if (app_userHasPermission("db_app_user_read")) {
        app_main_menu_addLink(gRb("db.app_user.list.title")
                              , "?page=app_app_user__app_app_user_list");
    }
    //-
    if (app_userHasPermission("db_app_role_read")) {
        app_main_menu_addLink(gRb("db.app_role.list.title")
                              , "?page=app_app_user__app_app_role_list");
    }
    //-
    if (app_userHasPermission("db_app_permission_read")) {
        app_main_menu_addLink(gRb("db.app_permission.list.title")
                              , "?page=app_app_user__app_app_permission_list");
    }
    //-
    if (app_userHasPermission("db_app_group_read")) {
        app_main_menu_addLink(gRb("db.app_group.list.title")
                              , "?page=app_app_user__app_app_group_list");
    }
    //-
    if (app_userHasPermission("db_app_system_property_read")) {
        app_main_menu_addLink(gRb("db.app_system_property.list.title")
                              , "?page=app_app_system__app_app_system_property_list");
    }
    //-
    if (app_userHasPermission("db_app_file_read")) {
        app_main_menu_addLink(gRb("db.app_file.list.title")
                              , "?page=app_app_file__app_app_file_list");
    }
    //-
    if (app_userHasPermission("db_app_system_log_read")) {
        app_main_menu_addLink(gRb("db.app_system_log.list.title")
                              , "?page=app_app_system_log__app_app_system_log_list");
    }
    //-
    if (app_userHasPermission("db_app_notifica_read")) {
        app_main_menu_addLink(gRb("db.app_notifica.list.title")
                              , "?page=app_app_notifica__app_app_notifica_list");
    }
    //-
    app_main_menu_addDivider("");
    //-
    //-
    if (app_userHasPermission("db_app_sys_tests")) {
        app_main_menu_addLink("Tools"
                              , "?page=app_test__app_test_loadTestPage");
    }
    app_main_menu_addLink("About"
                          , "?page=app_about__app_about_loadAboutPage");
}
