//-
//-
//- short urls
//-
//-

function s_doInitialize(callback) {
    callback();
}


//-
//- spacemr_space_user_presence_insert
//-
function prsncdd(){
    var spacemr_space_id  = getLocationParameterByName('sp');
    app_doRequestMappingRequest("spacemr_space_user_presence/spacemr_space_user_presence_insert_get_data"
                                , {
                                    spacemr_space_id: spacemr_space_id
                                }
                                , function(content) {
                                    app_spacemr_space_user_presence_form_insert_data(content);
                                });
}
