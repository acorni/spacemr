


var sys_dateFormatUi            = 'DD/MM/YYYY';
var sys_dateFormatUi_dtpkr_out  = 'DD/MM/YYYY';
var sys_dateFormatUi_locale     = "it";

var sys_dateFormatUi_pattern    = "([0-9]{2})\/([0-9]{2})\/([0-9]{4})";
var sys_dateFormatUi_validation = new RegExp("^"+sys_dateFormatUi_pattern+"$");

var sys_timestampFormatUi            = 'DD/MM/YYYY HH:mm:ss';
var sys_timestampFormatUi_dtpkr_out  = 'dd/mm/yy';
var sys_timestampFormatUi_pattern    = "([0-9]{2})\/([0-9]{2})\/([0-9]{4})( ([0-9]{2}):([0-9]{2})(:([0-9]{2}))?)?";
var sys_timestampFormatUi_validation = new RegExp("^"+sys_timestampFormatUi_pattern+"$");

var sys_number_integer_validation = new RegExp("^-?[0-9]*$");
var sys_number_decimal_validation = new RegExp("^-?[0-9\.]*(,[0-9]+)?$");
var sys_email_validation = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
var sys_number_integer_pattern = "-?[0-9]*";
var sys_number_decimal_pattern = "-?[0-9\.]*(,[0-9]+)?";

var sys_app_message = "";
var sys_app_message_timeToHide = 0;
var sys_app_message_messageSeconds = 15;

//- functions to call on login, each have to have a unique name
var sys_loginhooks = {};
var sys_scriptsfilesloaded = [];
var location_old = "";
var sys_session = {};
var sys_do_window_onhashchange_actions = true;

var sys_workflows = {};


var appRb = undefined;

function app_initialization_doInitialize(callback) {
    //-
    //- load base libraries
    //-
    $("#app_pagecontent_login").hide();
    app_initialization_doInitialize_go();
    moment.locale(sys_dateFormatUi_locale);
    //-
    if (callback != undefined) {
        callback();
    }
}

function app_initialization_doInitialize_go() {
    //- do something useful.
    //-
    //- navigation control
    //-
    window.onhashchange = function() {
        // console.log("url changed");
        if (sys_do_window_onhashchange_actions) {
            app_initialization_goToPage();
        } else {
            sys_do_window_onhashchange_actions=true;
        }
    }
    //-
    //-
    //-
    log( " ------ initialized -" );
    //-
    //-
    app_app_login_doGetLoggedUserInfo(function() {
        // app_loadScriptAndExecute("app_test", 'app_test_loadTestPage');
        app_initialization_goToPage();
    });
    //-
}

function app_getIcon(name, height) {
    var rv = "";
    if (sys_app_icons[name] == undefined) {
        rv = "Icon-unknow["+name+"]";
    } else {
        rv = $(sys_app_icons[name]).height(height);
    }
    return(rv);
};


var app_main_menu_last_onkeydown = null;

function app_main_menu_onkeydown(evt) {
    evt = evt || window.event;
    //console.log("evt.ctrlKey: "+evt.ctrlKey +" evt.keyCode: " + evt.keyCode);
    if (!evt.ctrlKey && evt.keyCode == 27) {
        app_main_menu_canvasOff_close();
    }
};

function app_main_menu_canvas_open() {
    document.getElementById("app_main_menu_canvasOff").style.width = "100%";
    document.getElementById("app_main_menu_canvasOff").style.opacity = "0.6";  
    document.getElementById("app_main_menu_canvas").style.width = "250px";
    app_main_menu_last_onkeydown = document.onkeydown;
    document.onkeydown = app_main_menu_onkeydown;
    $("#app_main_menu_search").focus();
}
function app_main_menu_canvasOff_close() {
    document.getElementById("app_main_menu_canvas").style.width = "0%";
    document.body.style.backgroundColor = "white";
    document.getElementById("app_main_menu_canvasOff").style.width = "0%";
    document.getElementById("app_main_menu_canvasOff").style.opacity = "0"; 
    document.onkeydown = app_main_menu_last_onkeydown;
}


function app_main_menu_addLink(label, link) {
    var a = 
        $('<a/>')
        .text(label)
        .attr('href' , "#" + link )
        .click(function(event){
            app_main_menu_canvasOff_close();
            app_initialization_setHashAndGoToPage(link);
        }) ;
    $("#app_main_menu_canvas").append(a)
};


function app_main_menu_addDivider(label) {
    var div = 
        $('<div/>')
        .text(label)
    ;
    $("#app_main_menu_canvas").append(div)
};

function app_main_menu_NascondiVociMenu() {
    var input, filter, div, a, i, value;
    input = document.getElementById("app_main_menu_search");
    filter = input.value.toUpperCase();
    div = document.getElementById("app_main_menu_canvas");
    a = div.getElementsByTagName("a");
    for (i = 0; i < a.length; i++) {
        value = a[i].text;
        if (value.toUpperCase().indexOf(filter) > -1) {
            a[i].style.display = "";
        } else {
            a[i].style.display = "none";
        }
    }
}

function app_initialization_setMainMenu() {
    //-
    //-
    // console.log($("#app_main_menu_button").height() - 12);
    //-
    $("#app_main_menu_button")
        .click(function() {
            app_main_menu_canvas_open();
        })
        .text(gRb("db.system.menu.menu"))
        .attr("title", "Alt-m")
        .append(" ")
        .append(app_getIcon("bars", 15));
    $("#app_main_menu_canvas")
        .html("")
        .append($('<span/>')
                .html("&times;")
                .attr('class', "app_main_menu_canvas_close")
                .attr('href' , "javascript:void(0)")
                .click(app_main_menu_canvasOff_close) 
               )
        .append($("<input class='w3-input' type='text' id='app_main_menu_search'>")
                .on("keyup", function() { app_main_menu_NascondiVociMenu(); } )
                .attr("placeholder", gRb("db.system.menu.search"))
               )
    ;
    //-
    $(document).bind('keydown', 'alt+m', function(e){
        // console.log("Key pressede which: " + e.which + " ctrl: " + e.ctrlKey + " altKey: " + e.altKey );
        //- alt-m
        if(e.which==77 && e.altKey) {
            app_main_menu_canvas_open();
        }
    });
    //-
    //-
    var mm = $('<div data-role="collapsibleset" data-theme="b" data-content-theme="a" data-collapsed-icon="arrow-r" data-expanded-icon="arrow-d" style="margin:0; width:250px;"/>');
    {
        var ul = $('<ul data-role="listview" data-filter="true" id="mainPanelListview" data-filter-placeholder="Search..." data-inset="true">');
        //-
	ul.click( function(){
	    // $('#mainPanel').panel("close");
	})
	//-
	//-
	//-
	//-
	//-
        app_configuration_main_menu();
	//-
	//-
	//-
        //-
	$('#mainPanel').html(ul);        
	$("#mainPanel").trigger('create');
        //-
    }
    //-
}


function app_initialization_setHash(newHash) {
    sys_do_window_onhashchange_actions = false;
    location.hash = newHash;
}

function app_initialization_setHashAndGoToPage(newHash) {
    app_tooltip_hide();
    location.hash = newHash;
    // app_initialization_goToPage();
}


function app_initialization_goToPage() {
    //-
    location_old  = location.hash;
    //- message
    if (sys_app_message == "") {
    } else {
        app_setMessage(sys_app_message);
        sys_app_message = "";
    }
    //-
    var pages = getLocationParameterByName('page');
    if (pages != undefined ) {
	var scriptAndFunctionName = pages.split("__",2);
	var scriptName   = scriptAndFunctionName[0];
	var functionName = scriptAndFunctionName[1];
	// log(" -- script: " + scriptName + " - funct: " + functionName);
	var re = /^[a-zA-Z][a-zA-Z0-9_]*$/;
	// log(re.toString());
	if (scriptName != undefined
            && functionName != undefined
            && re.test(scriptName) 
            && re.test(functionName)
           ) {
            //-
            app_initialization_goToPage_cleanPage();
	    app_loadScriptAndExecute(scriptName, functionName);
        } else {
            app_initialization_blank_page();
	}
    }
}

function app_initialization_goToPage_cleanPage() {
    $('.pika-title-yy').remove();
    $('.pika-single').remove();
}


function app_initialization_blank_page() {
    // log("blank page!");
    appSetPage($('<div>'), gRb("db.sys.app.name"));
}


function app_loadScriptAndExecute(scriptFileName, functionName) {
    //-
    // log("app_loadScriptAndExecute("+scriptFileName+","+functionName+")" );
    //-
    if (false) {
        //- no auto load of the modules
        window[functionName]();
    } else {
        app_loadScript(scriptFileName, function() {
            // log("functionName in cb: " + functionName);
            window[functionName]();
        });
    }
}

function app_loadScript(scriptFileName, callback) {
    //-
    var sys_scriptsfiles = new Array();
    if (sys_scriptsfilesloaded[scriptFileName] == undefined) {
        var fullScriptUrl = "js/"+scriptFileName+".js";
	log(" loading " + fullScriptUrl);
	$.getScript(fullScriptUrl , function() {
            var initFunctionName = scriptFileName+"_doInitialize";
	    // log(" ... calling functionName: " + initFunctionName)
            if (window[initFunctionName]){
	        window[initFunctionName](callback);
            }
	});
	sys_scriptsfilesloaded[scriptFileName] = 'ok';
    } else {
	callback();
    }
}


function doPopup(textToPopup) {
    app_setMessage(textToPopup);
}

function doPopupHtml(textToPopup) {
    app_setMessageHtml(textToPopup);
}

function doPopupPre(textToPopup) {
    app_setMessagePre(textToPopup);
}

function getLocationParameterByName(name) {
    // log("  getLocationParameterByName for " + name);
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.hash);
    // alert(results);
    var rv = (results == null) ? "" : decodeURIComponent(results[1].replace(/\+/g, " ")) 
    // alert(rv);
    return rv;
    // return results == null ? "" : results.length < 2 ? decodeURIComponent(results[1].replace(/\+/g, " "));
}



// JSON.pruned : a function to stringify any object without overflow
// example : var json = JSON.pruned({a:'e', c:[1,2,{d:{e:42, f:'deep'}}]})
// two additional optional parameters :
//   - the maximal depth (default : 6)
//   - the maximal length of arrays (default : 50)
// GitHub : https://github.com/Canop/JSON.prune
// This is based on Douglas Crockford's code ( https://github.com/douglascrockford/JSON-js/blob/master/json2.js )
// usage
//   var navJSON = JSON.pruned(navigator);
//-
(function () {
    'use strict';
    var DEFAULT_MAX_DEPTH = 8;
    var DEFAULT_ARRAY_MAX_LENGTH = 50;
    var seen; // Same variable used for all stringifications
    Date.prototype.toPrunedJSON = Date.prototype.toJSON;
    String.prototype.toPrunedJSON = String.prototype.toJSON;
    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        };
    function quote(string) {
        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string'
                ? c
                : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }
    function str(key, holder, depthDecr, arrayMaxLength) {
        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            partial,
            value = holder[key];
        if (value && typeof value === 'object' && typeof value.toPrunedJSON === 'function') {
            value = value.toPrunedJSON(key);
        }
        switch (typeof value) {
        case 'string':
            return quote(value);
        case 'number':
            return isFinite(value) ? String(value) : 'null';
        case 'boolean':
        case 'null':
            return String(value);
        case 'object':
            if (!value) {
                return 'null';
            }
            if (depthDecr<=0 ) {
                return '"-pruned-dept-"';
            } else if ( seen.indexOf(value)!==-1) {
                return '"-pruned-seen-"';
            }
            seen.push(value);
            partial = [];
            if (Object.prototype.toString.apply(value) === '[object Array]') {
                length = Math.min(value.length, arrayMaxLength);
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value, depthDecr-1, arrayMaxLength) || 'null';
                }
                v = partial.length === 0
                    ? '[]'
                    : '[' + partial.join(',') + ']';
                return v;
            }
            for (k in value) {
                if (Object.prototype.hasOwnProperty.call(value, k)) {
                    try {
                        v = str(k, value, depthDecr-1, arrayMaxLength);
                        if (v) partial.push(quote(k) + ':' + v);
                    } catch (e) { 
                        // this try/catch due to some "Accessing selectionEnd on an input element that cannot have a selection." on Chrome
                    }
                }
            }
            v = partial.length === 0
                ? '{}'
                : '{' + partial.join(',') + '}';
            return v;
        }
    }
    JSON.pruned = function (value, depthDecr, arrayMaxLength) {
        seen = [];
        depthDecr = depthDecr || DEFAULT_MAX_DEPTH;
        arrayMaxLength = arrayMaxLength || DEFAULT_ARRAY_MAX_LENGTH;
        return str('', {'': value}, depthDecr, arrayMaxLength);
    };
}());


function app_JSONStringify(obj,indent=2) {
    var rv = JSON.stringify(JSON.parse(JSON.pruned(obj)),null,indent);
    return(rv);
}

function app_doRequestMappingRequest(url, data, callBack) {
    // log(" -- in spacemr_inventario page initialization...");
    var datastr = JSON.stringify(data);
    var ajaxparams =  { 
        url: url
        ,type: "POST"
        ,data: { content : datastr }
        ,dataType: "Intelligent"
    };
    app_doRequestMappingRequest_base(ajaxparams,url, callBack);
}

function app_doRequestMappingRequestSync(url, data, callBack) {
    var datastr = JSON.stringify(data);
    var ajaxparams =  { 
        url: url
        ,type: "POST"
        ,data: { content : datastr }
        ,dataType: "Intelligent"
        ,async: false
    };
    app_doRequestMappingRequest_base(ajaxparams,url, callBack);
}

function app_doRequestMappingRequest_base(ajaxparams, url, callBack) {
    // console.log(" -- in spacemr_inventario page initialization_base...");
    $.ajax(ajaxparams)
        .always(function(msg) {
            // console.log(" -- in ajax spacemr_inventario page initialization_base...");
            // log(app_JSONStringify(msg));
            var errormessage = "";
            if (msg.readyState == 0) {
                errormessage = "Error connecting to the server url: " + url
                    + "\n status: " + app_JSONStringify(msg);
            } else {
                var context = null;
                try {
                    content = JSON.parse(msg.responseText);
                } catch (e) {
                    console.error("Parsing error:", e);
                    errormessage = ""
                        + " -- url --: " + url
                        + "\n -- error --: " + e
                        + "\nError parsing response: " + msg.responseText
                        + "\n -- callback --: " + callBack
                    ;
                    console.log(errormessage);
                    console.trace();
                }
                if (!status in content) {
                    errormessage = errormessage + "\n"
                        + " -- url --: " + url
                        + "\n -- error --  "
                        + "\nError - no status found in response -"
                        + "\n -- callback --: " + callBack
                    ;
                } else if (content.status == 500) {
                    errormessage = errormessage + "\n"
                        + " -- url --: " + url
                        + "\n -- error --: " + content.error
                        + "\nError parsing response: " + content.message
                        + "\n -- callback --: " + callBack
                    ;
                } else {
                    // log ("msg.responseText: " + msg.responseText);
                    // console.log("content.status: " + content.status);
                    if (content.status == "ok") {
                        callBack(content.content);
                    } else if (content.status.indexOf("permissionDenied") == 0) {
                        var auth_single_sign_on_protected_url =
                            sys_session.userData["auth_single_sign_on_protected_url"];
                        // if (single_sign_on) {
                        if (auth_single_sign_on_protected_url != undefined) {
                            // if anonymous
                            if (sys_session.userData.default_user){
				// var url = ""
				//     + auth_single_sign_on_protected_url
				//     + location.hash
				var url = ""
				    + auth_single_sign_on_protected_url
				    + "/" + btoa(location.hash)
				;
				window.location=url;
			    } else {
                                let m = gRb("db.app_permission.denied.message")
                                    + " " + content.status;
				app_setMessage(m);
			    }
			} else {
                            let m = gRb("db.app_permission.denied.message")
                                + " " + content.status;
			    app_setMessage(m);
                            app_app_login_form();
			}
			//-
                    } else {
                        errormessage = "Server status: " + content.status;
                    }
                }
            }
            if (errormessage.length > 0) {
                doPopup(errormessage);
            }
        })
    ;    
}

function appConvertTimestampToString(dateAsLong) {
    var d = new Date(dateAsLong);
    var rv = "";
    if (sys_number_integer_validation.test(""+dateAsLong)) {
        rv = moment(d).format(sys_timestampFormatUi);
        ;
    } 
    return rv;
}

function appConvertStringToTimestamp(theString) {
    var rv;
    if ("" === theString) {
        rv = "";
    } else {
        rv = 0;
        var parts = sys_timestampFormatUi_validation.exec(theString);
        // log(" parts: " + app_JSONStringify(parts));
        var day    = parts[1];
        var month  = parts[2]-1;
        var year   = parts[3];
        var hours  = (parts[5]==null) ? 0 : parts[5];
        var minutes= (parts[6]==null) ? 0 : parts[6];
        var seconds= (parts[8]==null) ? 0 : parts[8];
        var d = new Date(year, month, day, hours, minutes, seconds, 0);
        //-
        rv = d.getTime();
    }
    return rv;
}

function appConvertDecimalToString(theDecimal) {
    var rv = "";
    if (theDecimal == null
        || theDecimal == undefined
        || theDecimal == "" ) {
        rv = "";
    }else {
        var parts=theDecimal.toFixed(2).toString().split(".");
        rv  = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",") + (parts[1] ? "." + parts[1] : "");
        rv = rv.replace(/\./g,"__");
        rv = rv.replace(/,/g,".");
        rv = rv.replace(/__/g,",");
    }
    return rv;
}

function appConvertStringToDecimal(theString) {
    var rv = theString;
    rv = rv.replace(/\./g,"");
    rv = rv.replace(/,/g,".");
    return rv;
}

function appConvertBooleanToString(theBoolean) {
    var rv = null;
    if (theBoolean == true) {
        rv = gRb("db.sys.yes");
    } else {
        rv = gRb("db.sys.no");
    }
    return rv;
}

function appConvertStringToBoolean(theString) {
    var rv = false;
    if (theString == gRb("db.sys.yes")) {
        rv = true;
    }
    return rv;
}

function appConvertDateToString(dateAsLong) {
    var d = new Date(dateAsLong);
    var rv = "";
    if (sys_number_integer_validation.test(""+dateAsLong)) {
        rv = moment(d).format(sys_dateFormatUi);
        ;
    } 
    return rv;
}

function appSetPage(content, title) {
    // $("#app_pagecontent").hide();
    $("#app_pagecontent").html(content);
    $("#app_header_title").text(title);
    document.title = title;
}

function appGetPage() {
    return $("#app_pagecontent");
}


var sys_app_widgets = {};

function appCreateWidget(name, theWidget) {
    sys_app_widgets[name] = theWidget;
}

function appGetWidget(name, element, options) {
    var rv = sys_app_widgets[name];
    if (rv != undefined) {
        var orig = rv;
        rv = {};
        //- clone, cloning the widget object
        for (var attr in orig) {
            rv[attr] = orig[attr];
        }
        //- merging the default options object
        orig = rv.options;
        if (orig != undefined) {
            var newoption = {};
            for (var attr in orig) {
                newoption[attr] = orig[attr];
            }
            for (var attr in options) {
                newoption[attr] = options[attr];
            }
            rv.options = newoption;
        }
        rv.element = element;
        if (rv._create != null) {
            rv._create();
        }
        {
            var a = name.split(".");
            var n = a[a.length-1];
            // console.log(" ----- " + n);
            element[n] = function() {
                return(rv);
            };
        }
    } else {
        element.text("-- error - undefined widget ["+name+"]");
    }
    return(rv);
}

appCreateWidget( "custom.tableMattoni", {
    options: {  
        // identifier for this widget
        tableid: "none"
	, // spring url that serve this witdget
        controller: "app_user/app_user_list"
	,  // function that creates e.g. the button to select each row
        buttonsCell: function(tr, mapIdName, row) { 	
		  tr.prepend($('<td>').append("row")); }
	, // if defined customize the controls on this widget 
        // by default in the head there is the "columnSelector"
        buttonsHead: undefined
        , // content: 
        //   - the data to render, 
        //   - che columns to display, the filters, ...
        //   - the number of row to display
        //   - the number of the current page
        //   - the number of page that can be displayed
        //   ...
        // this is the main object exchanget with the java controller
        content: {}
        , enable_csv_output: false
        , action_on_noRecords: false
    },
    _sckb: undefined
    , _columnsSelector: function(tr, mapIdName, headers) {
        var wid = this;
        var columnsSelectorButtonSpace = $('<div>').text("");
        var qparams = wid.qparams();
        var sname = "sortablecheckboxesforcolumns";
        var columnsSelectorButton_click =
            function(event) {
                var input_pageSize = 
                    $('<input type="text"/>')
                    .attr("id", sname + "_pageSize")
                    .val(qparams.pageSize)
                    .attr("pattern", sys_number_integer_pattern)
                ;
                // if (columnsSelectorButtonSpace.children().length == 0) {
                let out_div = $("<div>");
                if (wid.sckb == undefined) {
                    var legend_sckb = $('<div>');
                    if (app_userHasPermission("db_app_table_mattoni_save")) {
                        legend_sckb
                            .append($('<a>')
                                    .click(function(event) {
                                        var conf = {};
                                        conf.pageSize = wid.input_pageSize.val();
                                        conf.columns  = wid.sckb.values();
                                        app_doRequestMappingRequest("app_user/app_user_property_set_table_mattoni_defaults"
                                                                    , { tableid: wid.options.tableid
                                                                        , conf: conf
                                                                      }
                                                                    , function(content) {
                                                                        doPopup("saved as default");
                                                                    });
                                    })
                                    .append(" ")
                                    .append(app_getIcon("save", 15))
                                    .append(gRb("db.sys.save"))
                                    .attr("title", gRb("db.app.tableMattoni.save_as_user_preference"))
                                   );
                    }
                    if (app_userHasPermission("db_app_table_mattoni_admin")) {
                        legend_sckb
                            .append($('<a>')
                                    .click(function(event) {
                                        var conf = {};
                                        conf.pageSize = wid.input_pageSize.val();
                                        conf.columns  = wid.sckb.values();
                                        app_doRequestMappingRequest("app_system/app_system_property_set_table_mattoni_defaults"
                                                                    , { tableid: wid.options.tableid
                                                                        , conf: conf
                                                                      }
                                                                    , function(content) {
                                                                        doPopup("saved as default");
                                                                    });
                                    })
                                    .append(" ")
                                    .append(app_getIcon("database", 15))
                                    .append(gRb("db.app.tableMattoni.save_as_application_default"))
                                    .attr("title", gRb("db.app.tableMattoni.save_as_application_default_title"))
                                   );
                        legend_sckb
                            .append($('<a>')
                                    .click(function(event) {
                                        var conf = {};
                                        conf.pageSize = wid.input_pageSize.val();
                                        conf.columns  = wid.sckb.values();
                                        var s = app_JSONStringify(conf);
                                        s = s.replace(/"/g, "\\\"");
                                        s = s.replace(/\n/g, "");
                                        s = ',{"table_mattoni__'+wid.options.tableid+'", "", "'+s+'" }';
                                        out_div.text(s);
                                    })
                                    .append(" ")
                                    .append(app_getIcon("info_circle", 15))
                                    .append("info")
                                   )
                            .append($('<a>')
                                    .click(function(event) {
                                        var conf = {};
                                        var s = app_JSONStringify(wid.qparams());
                                        out_div.text("&qparams="+encodeURI(s));
                                    })
                                    .append(" ")
                                    .append(app_getIcon("info_circle", 15))
                                    .append("qparam")
                                   );
                        ;
                        legend_sckb.append(out_div);
                    }
                    if (wid.options.enable_csv_output) {
                        var postDiv = $('<span>');
                        legend_sckb.append(postDiv);
                        // var params = jQuery.extend(true, {}, wid.qparams());
                        var params = jQuery.extend({}, wid.qparams());
                        params['outputtype'] = 'csv';
                        // log(app_JSONStringify(params));
                        // console.log(app_JSONStringify(wid.qparams()));
                        var inputParams = 
                            $('<input>')
                            .attr('type', 'hidden')
                            .attr('name', 'content')
                            .val("")
                        ;
                        var form = 
                            $('<form>')
                            .submit(function() {
                                params.columns = wid.sckb.values();
                                var pageSize = $("#"+sname + "_pageSize").val();
                                // alert("pageSize:" + pageSize);
                                params.pageSize = pageSize;
                                inputParams.val(JSON.stringify(params));
                                return true;
                            })
                            .attr('method', 'post')
                            .attr('action', wid.options.controller + "/data.csv")
                            .attr('target', 'csv')
                            .append(inputParams)
                            .append()
                            .append($('<input>')
                                    .attr('type', 'submit')
                                    .attr('name', 'doSubmit')
                                    .val("Get CSV")
                                   )
                        ;
                        postDiv.append(form);
                    }
   	            var sckb = 
                        appGetWidget("custom.sortablecheckboxes", $("<div>"), {
                            legend: gRb("db.sys.selectColumns")
                            , name: sname
                            , values: qparams.columns
                            , valuesAll: qparams.allcolumns
                            , labels: {}
                            , labelPrefix: qparams.labelPrefix
                        });
                    columnsSelectorButtonSpace.hide(0);
                    columnsSelectorButtonSpace
                        .append($('<label>').text(gRb("db.sys.selectColumns.pageSize"))
                                .attr("for", sname + "_pageSize"))
                        .append($("<form>")
                                .append(input_pageSize)
                                .keypress(function (event) {
                                    if (event.keyCode == 10 || event.keyCode == 13) {
                                        columnsSelectorButton_click();
                                    }
                                })
                               )
                        .append(sckb.element)
                        .append(legend_sckb)
                    ;
                    columnsSelectorButtonSpace.show(150);
                    wid.sckb = sckb;
                    wid.input_pageSize = input_pageSize;
                } else {
                    qparams.columns = wid.sckb.values();
                    var pageSize = $("#"+sname + "_pageSize").val();
                    qparams.pageSize = pageSize;
                    //-
                    columnsSelectorButtonSpace.hide(150, function() {
                        columnsSelectorButtonSpace.html("");
                        wid.sckb = undefined;
	                wid.render();
                    });
                }
        }
        ;
        var columnsSelectorButton =
            $('<a>')
            .css("text-align", "left")
            .append(app_getIcon("bars", 15))
            .click(columnsSelectorButton_click )
        ;
	tr.prepend($("<th/>")
                   .css("text-align", "left").css("font-weight","normal")
                   .append(columnsSelectorButton)
                   .append(columnsSelectorButtonSpace)
                  );
    },
    _create: function() {
        var wid = this;
        
	wid.element.text("Rendering..");
	wid.render();
    },
    qparams: function( ) {
        var wid = this;
        if ( (wid.options.content === undefined) ) {
            wid.options.content = {};
        }
        if ( (wid.options.content.qparams === undefined) ) {
            wid.options.content.qparams = {};
        }
        return(wid.options.content.qparams);
    },
    custom_mappers: function( ) {
        var wid = this;
        if ( (wid.options.custom_mappers === undefined) ) {
            wid.options.custom_mappers = {};
        }
        return(wid.options.custom_mappers);
    },
    where: function( key, value ) {
        var wid = this;
        var qparams = wid.qparams();
        if ( (qparams.where === undefined) ) {
            qparams.where = {};
        }
        var where = qparams.where;
        if ( value === undefined ) {
            // No value passed, act as a getter.
            return where[key];
        } else {
            // Value passed, act as a setter.
            where[key] = value;
        }
    },
    where_fields_hook_push: function(hookFunction) {
        var wid = this;
        if (wid.options.where_fields_hook === undefined ) {
            wid.options.where_fields_hook = [];
        }
        wid.options.where_fields_hook.push(hookFunction);
    },
    where_fields_hooks_run: function() {
        var wid = this;
        if (wid.options.where_fields_hook === undefined ) {
            wid.options.where_fields_hook = [];
        } else {
            for(var i=0;i<wid.options.where_fields_hook.length; i++) {
                (wid.options.where_fields_hook[i])();
            }
            
        }
    },
    order: function( key ) {
        var wid = this;
        var qparams = wid.qparams();
        if ( (qparams.order === undefined) ) {
            qparams.order = [];
        }
        var order = qparams.order;
        //-
        var goon = true;
        if (goon && order.length > 0) {
            var o0 = order[0];
            if (o0.column == key) {
                if (o0.desc === undefined) {
                    o0.desc = true;
                } else {
                    delete o0.desc;
                }
            } else {
                order.unshift({ column: key });
                if (order.length > 3) {
                    order.pop();
                }
            }
        } else {
            order.push({ column: key });
        }
        wid.render();
    },
    render_addHook: function(hookFunction) {
        var wid = this;
        if (wid.options.render_hooks === undefined ) {
            wid.options.render_hooks = [];
        }
        wid.options.render_hooks.push(hookFunction);
    },
    render_addPostHook: function(hookFunction) {
        var wid = this;
        if (wid.options.render_post_hooks === undefined ) {
            wid.options.render_post_hooks = [];
        }
        wid.options.render_post_hooks.push(hookFunction);
    },
    render: function( ) {
        var wid = this;
	// wid.element.text("rendering..");
        wid.qparams().tableid = wid.options.tableid;
        //-
        if (wid.options.render_hooks === undefined ) {
        } else {
            for(var i=0;i<wid.options.render_hooks.length; i++) {
                (wid.options.render_hooks[i])();
            }
        }
        //-
        // console.log("wid.qparams().tableid: " + wid.qparams().tableid);
	app_doRequestMappingRequest(wid.options.controller
                                    , wid.qparams()
                                    , function(content) { wid.render_draw(content) }
				   );
    },
    render_draw: function(content) {
        var wid = this;
	wid.options.content = content;
        //-
        var id  = wid.options.tableid;
	var tableSpace = app_ui_standard_getGrid_centralElement();
        //  data-mode="columntoggle" 
	var table = 
            $('<table data-role="table" class="w3-striped w3-hoverable w3-responsive"/>')
            .attr("id",id+"_table")
            .append();
	// table.append($('<tr/>').append($('<td/>').text('help')));
	// log("content:" + app_JSONStringify(content));
	var tr = $("<tr/>");
	var i = 0;
	var mappers = [];
	var headers = content.list.headers;
	var mapIdName = {};
	var buttonsCell = wid.options.buttonsCell;
	var buttonsHead = wid.options.buttonsHead;
        if (content.list.rows.length <= 0) {
            tableSpace.append($('<div style="width: 100%; overflow: auto">')
                              .append(gRb("db.sys.tableMattoni.noRecords")));
            if (wid.options.action_on_noRecords) {
                wid.options.action_on_noRecords();
            }
        } else {
	    content.columns = [];
	    jQuery.each(headers, function(){
                var h = this;
                // log("col: " + h.name);
                if (!h.hidden) {
		    tr.append($("<th/>")
			      .attr("data-priority", "" + Math.min(i+1, 6))
			      .append($("<a>")
                                      .append(gRb(h.label)
                                              //  + " ("+h.type+")"
                                             )
                                     ).click(function() {
                                         wid.order(h.name);
                                     })
                             );
		    content.columns[content.columns.length] = h.name;
                }
                var custom_mappers = wid.custom_mappers();
                var mapper = custom_mappers[h.name];
                if (mapper == undefined) {
                    mapper = app_appListTable_mappers[h.type];
                    if (mapper == undefined) {
		        var m = " Unknow mapper for type ["+h.type+"]";
		        log(m);
		        alert(m);
                    }
                }
                mappers.push(mapper);
	        mapIdName[h.name] = i;
                i = i+1;
	    });
            if (buttonsHead == undefined) {
                wid._columnsSelector(tr, mapIdName, headers)
            } else {
	        buttonsHead(tr, mapIdName, headers);
            }
	    table.append($("<thead/>").append(tr));
	    //-
	    // log("mappers:" + app_JSONStringify(mappers));
	    //-
	    var tbody = $("<tbody/>")
	    jQuery.each(content.list.rows, function(){
                var row = this;
                tr = $("<tr/>");
                for (i = 0; i < row.length; i += 1) {
                    var cell = row[i];
		    if (!headers[i].hidden) {
                        var td = $("<td/>");
		        var d = mappers[i].draw(cell, tr, mapIdName, row, td);
                        tr.append(td);
                        td.append(d);
		    }
                }
	        buttonsCell(tr, mapIdName, row);
                tbody.append(tr);
	    });
	    table.append(tbody);
            tableSpace.append($('<div style="width: 100%; overflow: auto">')
                              .append(table));
        }
        //-
        //- paging
        //-
        {
            var pages = wid.options.content.qparams.pages;
            if (pages > 1) {
                var pageNumber = wid.options.content.qparams.pageNumber;
                var pagingSpace = $("<div>");
                pagingSpace.append("pages: " );
                // for ( var i=1; i <= pages ; i++) {
                //     pagingSpace.append(" " + i);
                // }
                var r=10;
                var generateA = function(pageNumber) {
                    var rv = $("<a>").text(pageNumber).click(function(){
                        wid.options.content.qparams.pageNumber = pageNumber;
                        wid.render();
                    } );
                    return rv;
                }
                var pf = pageNumber - r;
                var pl = pageNumber + r;
                if (pf <= 1) {
                    pf = 1;
                } else {
                    pagingSpace.append(generateA(1));
                    pagingSpace.append(" ... ");
                }
                if (pl >=  pages) {
                    pl = pages;
                }
                for ( var i=pf; i <= pl ; i++) {
                    if (i == pageNumber) {
                        pagingSpace.append(" ")
                            .append($("<span class='w3-theme-d1'>").text(i));
                    } else {
                        pagingSpace.append(" ");
                        pagingSpace.append(generateA(i));
                    }
                }
                if (pl <  pages) {
                    pagingSpace.append(" ... ");
                    pagingSpace.append(generateA(pages));
                }
                tableSpace.append(pagingSpace)
            }
        }
	wid.element
            .html(app_ui_standard_getGrid_wrapperElement().append(tableSpace));
	// var columns = appListTable_refresh_draw_columnHide(id, content);
	// $("#"+id)
        //     .html(table)
        //     .append(columns);
	// ;
	// $("#"+id).trigger('create');
	// wid.element.trigger('create');
        if (wid.options.render_post_hooks === undefined ) {
        } else {
            for(var i=0;i<wid.options.render_post_hooks.length; i++) {
                (wid.options.render_post_hooks[i])();
            }
        }
    },
});


appCreateWidget( "custom.sortablecheckboxes", {
    options: {
        legend: "widget header legend"
        , name: "sortablecheckboxes"
        , values: ["a"
                   , "e"
                   , "r"
                   , "c"
                  ]
        , valuesAll: [ "a"
                       , "b"
                       , "c"
                       , "d"
                       , "e"
                     ]
        // , labels: { a: "label a"
        //           , b: "label b longer text"
        //           , c: "label c"
        //           , d: "label for d"
        //           , e: "this is the 'e' label"
        //           }
        // , labelPrefix: ""
        // , change: function() { alert("changed")}
    },
    _create: function() {
        var wid       = this;
        wid.options.valueNameMap = {};
        var values    = wid.options.values; 
        var valuesAll = wid.options.valuesAll; 
        var labels       = wid.options.labels;
        var labelPrefix  = wid.options.labelPrefix;
        var getLabel;
        // console.log(app_JSONStringify(wid.options));
        // console.log(labels);
        if (labels != undefined && Object.keys(labels).length > 0) {
            // console.log(" labels is defined!");
            getLabel = function(value) {
                var rv = labels[value];
                if (rv == undefined) {
                    rv = "l!" + value;
                }
                return (rv);
            }
        } else if (labelPrefix != undefined) {
            getLabel = function(value) {
                var rv = gRb(labelPrefix +"." + value);
                return (rv);
            }
        } else  {
            getLabel = function(value) {
                return (value);
            }
        }
        var fieldset = $('<fieldset>');
        let info_area = $("<div>");
        if (this.options.legend!=undefined) {
            fieldset.append($('<legend>')
                            .html(this.options.legend)
                            .append(" ")
                            .append(app_getIcon("info_circle", 15)
                                    .click(function(){
                                        if(info_area.text() == "") {
                                            info_area.append("Info: ");
                                            let textarea =
                                                $("<textarea class='w3-round'>")
                                                .val(app_JSONStringify(wid.values()))
                                            ;
                                            info_area.append(textarea);
                                            info_area
                                                .append(
                                                    $("<span>")
                                                        .attr('title',gRb("db.sys.update"))
                                                        .append(
                                                            app_getIcon("check", 15)
                                                                .click(function(){
                                                                    wid.options.values =
                                                                        JSON.parse(textarea.val())
                                                                    wid.element.html("");
                                                                    wid._create();
                                                                })
                                                        )
                                                       );
                                            info_area.show(50);
                                        } else {
                                            info_area.hide(50, function(){
                                                info_area.text("")
                                            });
                                        }
                                    })
                                   )
                           );
        }
        fieldset.append(info_area);
        var i;
        // log(app_JSONStringify(values));
        for (i = 0; i < values.length; i++) {
            var value = values[i];
            // log(" in values: " + value + " i: " + i + "  inAll: " + valuesAll.indexOf( value ));
            if (valuesAll.indexOf( value ) >= 0) {
                // log(" true to " + value + " i: " + i);
                wid._addCheckbox(fieldset, getLabel(value), value, true);
            }
        };
        for (i = 0; i < valuesAll.length; i++) {
            var value = valuesAll[i];
            // log(" in values: " + value + " i: " + i + "  inAll: " + values.indexOf( value ));
            if (values.indexOf( value ) < 0) {
                // log(" false to " + value + " i: " + i);
                wid._addCheckbox(fieldset, getLabel(value), value, false);
            }
        };
        fieldset.on("mouseleave", function(evt){
            if (wid.dragdropSelected != null) {
                wid.dragdropSelected.removeClass("w3-theme-d3")
            }
            wid.dragdropOnMouseOver = null;
            wid.dragdropSelected = null;
        });
        wid.element.append($('<form>').append(fieldset));
	wid.element.trigger('create');
    },
    // Create a private method.
    _addCheckbox: function( fieldset, label, name, checked) {
        var wid = this;
        wid.options.valueNameMap[""+name] = name;
        var id = this.options.name + "_" + name;
        var i = $('<input>')
            .attr("name", name)
            .attr("type","checkbox")
            .attr("id", id)
            .prop("checked", checked)
            .change(function(){
                if (wid.options.change != undefined) {
                    wid.options.change(wid);
                }
            })
        ;
        var l = $('<label>')
            .text(label)
            .attr("for", id)
        ;
        wid.dragdropOnMouseOver = null;
        wid.dragdropSelected = null;
        wid.dragdropSelected_mouseDown  = false;
        var changed = true;
        fieldset.append($("<div>")
                        .append(i)
                        .append(l)
                        .click(function(evt) {
                            if(wid.dragdropSelected_mouseDown ) {
                                wid.dragdropSelected_mouseDown = false;
                                evt.preventDefault();
                            }
                        })
                        .on("mousedown", function(evt){
                            evt.preventDefault();
                            $(this).addClass("w3-theme-d3")
                            // console.log("hello " + $(this).find("input").attr("name") );
                            wid.dragdropSelected = $(this);
                            var theOldElement = $(this);
                            wid.dragdropOnMouseOver = function(theNewElement) {
                                // console.log(theNewElement.position());
                                // console.log("old:  " + theOldElement.find("input").attr("name")
                                //             + " new: " + theNewElement.find("input").attr("name")
                                //            );
                                if (theOldElement.parent().parent().children(':first-child').children().find("input").attr("name") == theNewElement.find("input").attr("name")) {
                                    theOldElement.insertBefore(theNewElement);
                                } else {
                                    theOldElement.insertAfter(theNewElement);
                                }
                                wid.dragdropSelected_mouseDown = true;
                                changed=true;
                            }
                        })
                        .on("mouseup", function(evt){
                            evt.preventDefault();
                            // console.log("stop hello");
                            if (wid.dragdropSelected != null) {
                                wid.dragdropSelected.removeClass("w3-theme-d3")
                            }
                            wid.dragdropOnMouseOver = null;
                            wid.dragdropSelected = null;
                            if(changed) {
                                if (wid.options.change != undefined) {
                                    wid.options.change(wid);
                                }
                                changed=false;
                            }
                        })
                        .on("mouseover", function(evt){
                            if (wid.dragdropOnMouseOver != null
                               && wid.dragdropSelected != null ) {
                                if( $(this).find("input").attr("name")
                                    != wid.dragdropSelected.find("input").attr("name")) {
                                    wid.dragdropOnMouseOver($(this));
                                }
                            }
                        })
                       )
        ;
    },
    //-
    values: function() {
        var rv = [];
        var wid = this;
        var inputs = wid.element.find("input");
        // log (app_JSONStringify(inputs));
        inputs.each(function() {
            var checkbox = $(this);
            if (checkbox.prop("checked")) {
                rv.push(wid.options.valueNameMap[checkbox.attr("name")]);
            }
        });
        return rv;
        // return this.options.values;
    },
});

//-
//-
appCreateWidget( "custom.checkBoxGroup", {
    options: {widgetId: "oneCheckBoxGroup"
	      , list: [ { label: "label",  id: 0, checked: true}]
              , legend: "custom.checkBoxGroup legend"
	     },
    _create: function() {
        var wid = this;
        var list     = wid.options.list;
        var widgetId = wid.options.widgetId;
        var fieldset = $('<fieldset data-role="controlgroup">');
        wid.element.attr("id",widgetId);
        fieldset.append($('<legend>').text(wid.options.legend));
        list.forEach(function(entry) {
            var input = 
                $("<input type='checkbox'>")
                .attr("id",widgetId+"__"+entry.id)
                .prop('checked', entry.checked)
            ;
            action = function() {
                entry.checked = input.prop('checked');
                // log(app_JSONStringify(entry));
            };
            // input.blur(action);
            input.click(action);
            //-
	    fieldset.append(input);
	    fieldset.append($("<label>")
                            .attr("for",widgetId+"__"+entry.id)
                            .text(entry.label)
                           );
        });
	wid.element.append(fieldset);
	wid.element.trigger('create');
    },
    getList: function() {
        var wid = this;
        var list     = wid.options.list;
        return(list);
    },

});


var app_appListTable_mappers = {};
(function appListTable_initMappers() {
    // log("init mappers");
    var mapper;
    var s;
    //-
    //-
    //- http://phpjs.org/functions/number_format/
    //- http://www.teamdf.com/web/jquery-number-format/178
    //-
    s = "integer";
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row, td) {
        td.css("text-align", "right");
        if (value == null
           || value == undefined ) {
            value = "";
        }else {
            value=""+value;
        }
        return(value);
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "decimal";
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row, td) {
        td.css("text-align", "right");
        value=""+appConvertDecimalToString(value);
        return(value);
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "double";
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row, td) {
        td.css("text-align", "right");
        return(""+appConvertDecimalToString(value));
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "real";
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row, td) {
        td.css("text-align", "right");
        return(""+appConvertDecimalToString(value));
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "boolean";
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
        return(""+appConvertBooleanToString(value));
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "boolean_hidden";
    mapper = {};
    mapper.hidden = true;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
        return(""+appConvertBooleanToString(value));
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "id";
    mapper = {};
    mapper.hidden = true;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
        return(""+value);
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "string";
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
        if (value == null
           || value == undefined ) {
            value = "";
        }
        return("" +  value);
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "string_hidden";
    mapper = {};
    mapper.hidden = true;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
        if (value == null) {
            value = "";
        }
        return("" + value);
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "longstring"
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
        let rv=$("<p>");
        if (value == null) {
            rv.text("");
        } else {
            if (value.length < 100) {
                rv.text(value);
            } else {
                rv.text(value.substring(0,100)+"...");
                app_tooltip_set_click_recursive(rv
                                                , function(evt, tooltip_div, refresh_callback){
                                                    tooltip_div
                                                        .append($("<div>")
                                                                .text(value)
                                                               )
                                                    setTimeout(app_tooltip_center, 100);
                                                }
                                                , null) ;
                
                // rv.attr("title", value);
            }
        }
        return(rv);
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "timestamp"
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
	var s  = "" + value;
	var rv = "";
	if (sys_number_integer_validation.test(s)) {
	    // rv = "" + new Date(value);
            rv = appConvertTimestampToString(value);
	} 
        return("" + rv);
    };
    app_appListTable_mappers[s] = mapper;
    //-
    s = "date"
    mapper = {};
    mapper.hidden = false;
    mapper.name   = s;
    mapper.draw   = function(value, tr, mapIdName, row) {
	var s  = "" + value;
	var rv = "";
	if (sys_number_integer_validation.test(s)) {
	    // rv = "" + new Date(value);
            rv = appConvertDateToString(value);
	} 
        return("" + rv);
    };
    app_appListTable_mappers[s] = mapper;
    //-
})();


function app_where_append_string(fieldcontain, label, key, divTable) {
    var field = app_ui_standard_appendFieldText(fieldcontain, key, key, label,"")
        .keyup(function(){
            divTable.tableMattoni().where(key,$(this).val());
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field.val(divTable.tableMattoni().where(key));
    });
}

function app_where_append_integer(fieldcontain, label, key, divTable) {
    var localcontain = $('<div>');
    var localcontainf = $('<span style="display:inline-block;margin-right:10px;">');
    var localcontaint = $('<span style="display:inline-block;">');
    var key_f = key + "__from";
    var field_f = app_ui_standard_appendFieldInteger(localcontainf, key_f, key_f, label + " - " + gRb("db.sys.whereFrom"),"")
        .keyup(function(){
            divTable.tableMattoni().where(key_f,$(this).val());
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_f.val(divTable.tableMattoni().where(key_f));
    });
    //-
    var key_t = key + "__to";
    var field_t = app_ui_standard_appendFieldInteger(localcontaint, key_t, key_t, label + " - " + gRb("db.sys.whereTo"),"")
        .keyup(function(){
            divTable.tableMattoni().where(key_t,$(this).val());
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_t.val(divTable.tableMattoni().where(key_t));
    });
    //-
    localcontain.append(localcontainf, localcontaint)
    fieldcontain.append(localcontain);
}


function app_where_append_integer_equal(fieldcontain, label, key, divTable) {
    var field = app_ui_standard_appendFieldInteger(fieldcontain, key, key, label,"")
        .keyup(function(){
            divTable.tableMattoni().where(key,$(this).val());
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field.val(divTable.tableMattoni().where(key));
    });
}


function app_where_append_decimal(fieldcontain, label, key, divTable) {
    var localcontain = $('<div>');
    var localcontainf = $('<span style="display:inline-block;margin-right:10px;">');
    var localcontaint = $('<span style="display:inline-block;">');
    var key_f = key + "__from";
    var field_f = app_ui_standard_appendFieldDecimal(localcontainf, key_f, key_f, label + " - " + gRb("db.sys.whereFrom"),"")
        .keyup(function(){
            divTable.tableMattoni().where(key_f,$(this).val());
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_f.val(divTable.tableMattoni().where(key_f));
    });
    //-
    var key_t = key + "__to";
    var field_t = app_ui_standard_appendFieldDecimal(localcontaint, key_t, key_t, label + " - " + gRb("db.sys.whereTo"),"")
        .keyup(function(){
            divTable.tableMattoni().where(key_t,$(this).val());
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_t.val(divTable.tableMattoni().where(key_t));
    });
    //-
    localcontain.append(localcontainf, localcontaint)
    fieldcontain.append(localcontain);
}

function app_where_append_checkbox(fieldcontain, label, key, divTable) {
    var field = app_ui_standard_appendFieldCheckBox(fieldcontain, key, key, label,"")
        .keyup(function(){
            divTable.tableMattoni().where(key,$(this).prop("checked"));
        })
        .click(function() {
            divTable.tableMattoni().where(key,$(this).prop("checked"));
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field.prop("checked", divTable.tableMattoni().where(key));
    });
}

function app_where_append_boolean(fieldcontain, label, key, divTable) {
    var localcontain = $('<div>');
    var localcontainf = $('<span style="display:inline-block;margin-right:10px;">');
    var localcontaint = $('<span style="display:inline-block;">');
    var key_f = key + "__true";
    var field_f = app_ui_standard_appendFieldCheckBox(localcontainf, key_f, key_f, label + " - " + gRb("db.sys.selectValueIsTrue"),"")
        .keyup(function(){
            divTable.tableMattoni().where(key_f,$(this).prop("checked"));
        })
        .click(function() {
            divTable.tableMattoni().where(key_f,$(this).prop("checked"));
        })
    ;
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_f.prop("checked", divTable.tableMattoni().where(key_f));
    });
    //-
    var key_t = key + "__false";
    var field_t = app_ui_standard_appendFieldCheckBox(localcontaint, key_t, key_t, label + " - " + gRb("db.sys.selectValueIsFalse"),"")
        .keyup(function(){
            divTable.tableMattoni().where(key_t,$(this).prop("checked"));
        })
        .click(function() {
            divTable.tableMattoni().where(key_t,$(this).prop("checked"));
        })
    ;
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_t.prop("checked", divTable.tableMattoni().where(key_t));
    });
    //-
    localcontain.append(localcontainf, localcontaint)
    fieldcontain.append(localcontain);
}


function app_where_append_workflow(fieldcontain, label_key, key, divTable, wf) {
    if (wf == undefined) {
        var rv = "error Workflow not initialized";
        app_ui_standard_appendRow(grid)
            .append($('<label>').attr("for", key).text(label))
            .append(rv)
        ;
        return(rv);
    }
    var label = gRb(label_key);
    //-
    // fieldcontain.append($("<pre>").append(app_JSONStringify(wf.statusList)));
    // fieldcontain.append($("<pre>").append(function(statusList){
    //     var rv = ""; statusList.forEach(function(k){
    //         rv = rv + "\n    , \"" + label_key+"__"+k + "\" : \""+k+"\"";
    //     }); return(rv);
    // }(wf.statusList)));
    var statusList=wf.statusList;
    //-
    var boxContainer = $("<span>");
    //-
    statusList.forEach(function(k) {
        var f=function(k){
            var key_v = key + "__" + k;
            var field_v =
                $('<input type="checkbox"/>').attr('name', key_v).attr('id', key_v)
                .keyup(function(){
                    divTable.tableMattoni().where(key_v,$(this).prop("checked"));
                })
                .click(function() {
                    divTable.tableMattoni().where(key_v,$(this).prop("checked"));
                })
            ;
            divTable.tableMattoni().where_fields_hook_push(function(){
                field_v.prop("checked", divTable.tableMattoni().where(key_v));
            });
            field_v.css('background-color', 'red');
            boxContainer
                .append(" ")
                .append(
                    $("<span>")
                        .attr('title',wf.statusIndex[k].description)
                        .css('background-color', wf.statusIndex[k].color)
                        .append(field_v)
                        .append(k)
                )
            ;
        };
        f(k);
    });
    app_ui_standard_appendRow(fieldcontain)
        .append($('<label>').attr("for", key).text(label))
        .append(boxContainer)
    ;
}


function app_where_set_workflow_defaults(qparams, key, wf){
        if (wf.defaultSearchStatuses != undefined) {
            // console.log(" -- not undefined in app_where_set_workflow_defaults");
            wf.defaultSearchStatuses.forEach(function(k){
                // console.log(" -- " + key+"__"+k);
                qparams.where[key+"__"+k] = true;
            });
        }
    }


function app_where_append_selectMenu(fieldcontain, label, key, divTable, values) {
    var input;
    //-
    //-
    fieldcontain.append($('<div class="ui-block-a">').text(label));
    var fieldset=$('<fieldset data-role="controlgroup">');
    values.forEach(function(value) {
        fieldset.append($('<label for="id_'+key+'__'+value.value+'">')
                        .text(value.description));
        fieldset.append($('<input name="'+key+'__'+value.value+'" id="id_'+key+'__'+value.value+'" type="checkbox">')
                        .keyup(function() {
                            divTable.tableMattoni("where",key+"__"+value.value,$(this).prop("checked"));
                        })
                        .click(function() {
                            divTable.tableMattoni("where",key+"__"+value.value,$(this).prop("checked"));
                        })
                       );
    });
    fieldcontain.append($('<div class="ui-block-b">').append(fieldset));
    //-
    //-
}

function app_where_append_date(fieldcontain, label, key, divTable) {
    var localcontain = $('<div>');
    var localcontainf = $('<span style="display:inline-block;margin-right:10px;">');
    var localcontaint = $('<span style="display:inline-block;">');
    var key_f = key + "__from";
    var field_f = app_ui_standard_appendFieldDate(localcontainf, key_f, key_f, label + " - " + gRb("db.sys.whereFrom"),"")
        .change(function(){            
            divTable.tableMattoni().where(key_f,appConvertStringToTimestamp($(this).val()));
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_f.val(appConvertTimestampToString(divTable.tableMattoni().where(key_f)));
    });
    //-
    var key_t = key + "__to";
    var field_t = app_ui_standard_appendFieldDate(localcontaint, key_t, key_t, label + " - " + gRb("db.sys.whereTo"),"")
        .change(function(){
            divTable.tableMattoni().where(key_t,appConvertStringToTimestamp($(this).val()));
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_t.val(appConvertTimestampToString(divTable.tableMattoni().where(key_t)));
    });
    //-
    localcontain.append(localcontainf, localcontaint)
    fieldcontain.append(localcontain);
}

function app_where_append_date_single(fieldcontain, label, key, divTable) {
    var field = app_ui_standard_appendFieldDate(fieldcontain, key, key, label,"")
        .change(function(){            
            divTable.tableMattoni().where(key,appConvertStringToTimestamp($(this).val()));
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field.val(appConvertTimestampToString(divTable.tableMattoni().where(key)));
    });
    //-
}


function app_where_append_timestamp(fieldcontain, label, key, divTable) {
    var localcontain = $('<div>');
    var localcontainf = $('<span style="display:inline-block;margin-right:10px;">');
    var localcontaint = $('<span style="display:inline-block;">');
    var key_f = key + "__from";
    var field_f = app_ui_standard_appendFieldTimestamp(localcontainf, key_f, key_f, label + " - " + gRb("db.sys.whereFrom"),"")
        .change(function(){            
            divTable.tableMattoni().where(key_f,appConvertStringToTimestamp($(this).val()));
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_f.val(appConvertTimestampToString(divTable.tableMattoni().where(key_f)));
    });
    //-
    var key_t = key + "__to";
    var field_t = app_ui_standard_appendFieldTimestamp(localcontaint, key_t, key_t, label + " - " + gRb("db.sys.whereTo"),"")
        .change(function(){
            divTable.tableMattoni().where(key_t,appConvertStringToTimestamp($(this).val()));
        });
    divTable.tableMattoni().where_fields_hook_push(function(){
        field_t.val(appConvertTimestampToString(divTable.tableMattoni().where(key_t)));
    });
    //-
    localcontain.append(localcontainf, localcontaint)
    fieldcontain.append(localcontain);
}


function appStringPrefixToLength(theString, charAsPrefix, length) {
    var rv = theString;
    while (rv.length < length) {
	rv = charAsPrefix + rv;
    }
    return rv;
}

function app_setMessage(message) {
    app_setMessageHtml($('<div>').text(message));
}
function app_setMessagePre(message) {
    app_setMessageHtml($('<pre>').text(message));
}
function app_setMessageHtml(message) {
    $('#app_messages').html(message);
    //-
    $('#app_messages').click(function(){ sys_app_message_timeToHide = 0;
                                         app_setMessage_hide(); });
    //-
    $('#app_messages').show(50);
    //-
    sys_app_message_timeToHide = (new Date()).getTime() + (sys_app_message_messageSeconds * 1000);
    setTimeout(app_setMessage_hide,sys_app_message_messageSeconds * 1000);
}
function app_setMessage_hide() {
    log(" hiding sys_app_message_timeToHide: " + (new Date()).getTime());
    if ((new Date()).getTime() >= sys_app_message_timeToHide) {
        $('#app_messages').hide(50);
    }
}
function app_setMessageNextPage(message) {
    sys_app_message = message;
}

function gRb(value) {
    if (appRb == undefined) {
        appRb = sys_session.userData.resource_bundle;
    }
    var rv = appRb[value];
    if (rv == undefined) {
        rv = "k?" + value;
    }
    return rv;
}

//- check if a user can or not do something
function app_userHasPermission(permission, group) {
    var rv = false;
    //-
    if (group == undefined) {
        group = "root";
    }
    // log("sys_session:"+app_JSONStringify(sys_session.userData));
    var p = sys_session.userData.permissions[group];
    if(p != undefined) {
        // console.log("p"+app_JSONStringify(p));
        // console.log("p["+permission+"]"+p[permission]);
        rv = ($.inArray(permission, p)>=0);
    }
    if (!rv && group != "root") {
        rv = app_userHasPermission(permission, "root");
    }
    // log("-- check for " + permission + ": " + rv);
    return(rv);
}

//- check if a user can or not do something
function app_userHasPermissionOnAnyGroup(permission) {
    var rv = false;
    //-
    for (var group in sys_session.userData.permissions) {
        // console.log("  group: " + group);
        var p = sys_session.userData.permissions[group];
        rv = rv || ($.inArray(permission, p)>=0);
    }    
    return(rv);
}

function app_ui_standard_getGrid_centralElement() {
    var rv = $('<div class="w3-panel w3-white w3-card-4 w3-round">');
    return(rv);
}
function app_ui_standard_getGrid_wrapperElement() {
    var rv = $('<div class="w3-container w3-content" style=" width: 98%;">');
    return(rv);
}
function app_ui_standard_getGrid(page) {
    var rv = app_ui_standard_getGrid_centralElement();
    page.append(app_ui_standard_getGrid_wrapperElement().append(rv));
    return(rv);
};
function app_ui_standard_appendRow(grid) {
    var rv = $('<p/>');
    grid.append(rv);
    return rv;
}
function app_ui_standard_select(id, name, values, value) {
    //- values is an array  [{"value": "t1", "label":"t1l"}, {"value": "t2", "label":"t2l"}]
    var rv = $('<select class="">').attr("id", id).attr("name", name);
    var i;
    for (i = 0; i < values.length; i++) {
        var r = values[i];
        // console.log(r.value + " " + r.label);
        var o = $("<option>")
                  .attr("value", r.value)
                  .append(r.label)
        ;
        if (r.value == value) {
            o.attr("selected", true);
        }
        rv.append(o);
    }    
    return(rv);
}
function app_ui_standard_appendFieldSelect(grid, id, name, label, value, values) {
    var rv = app_ui_standard_select(id, name, values, value);
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).text(label))
        .append(" ")
        .append(rv)
    ;
    return(rv);
}

function app_ui_standard_appendFieldCheckBox(grid, id, name, label, value) {
    var rv =
        $('<input type="checkbox" class="w3-check"/>').attr('name', name).attr('id', id)
        .prop("checked", value);
    app_ui_standard_appendRow(grid)
        .append(rv)
        .append(" ")
        .append($('<label>').attr("for", id).text(label))
    ;
    return(rv);
};
function app_ui_standard_appendFieldDate(grid, id, name, label, value) {

    var input =
        $('<input class="w3-input w3-border w3-round" type="text"/>')
        .attr('name', name).attr('id', id).val(value)
    ;
    input.pikaday({format: sys_dateFormatUi
                   , firstDay: 1
                   , i18n: { months: moment.localeData()._months
                             , weekdays: moment.localeData()._weekdays
                             , weekdaysShort: moment.localeData()._weekdaysShort }
                  });
    var rv = input;
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).text(label))
        .append(rv)
    ;
    return(rv);
};
function app_ui_standard_appendFieldTimestamp(grid, id, name, label, value) {
    var rv = $('<input class="w3-input w3-border w3-round" type="text"/>').attr('name', name).attr('id', id).attr("pattern", sys_timestampFormatUi_pattern).val(value);
    app_ui_standard_appendRow(grid)
        .append($('<abbr>').attr('title',gRb("db.sys.format_timestamp"))
                .append($('<label>').attr("for", id).text(label))
               )
        .append(rv)
    ;
    return(rv);
};
function app_ui_standard_appendFieldDecimal(grid, id, name, label, value) {
    var rv = $('<input class="w3-input w3-border w3-round" type="text"/>').attr('name', name).attr('id', id).attr("pattern", sys_number_decimal_pattern).val(appConvertDecimalToString(value));
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).text(label))
        .append(rv)
    ;
    return(rv);
};
function app_ui_standard_appendFieldHidden(form, id, name, label ,value) {
    if ( value == undefined) {
        value = "";
    }
    var rv = $('<input type="hidden"/>').attr('name', name).attr('id', id).val(value);
    form.append(rv);
    return(rv);
};
function app_ui_standard_appendFieldInteger(grid, id, name, label, value) {
    var rv = $('<input class="w3-input w3-border w3-round" type="text"/>').attr('name', name).attr('id', id).attr("pattern", sys_number_integer_pattern).val(value);
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).text(label))
        .append(rv)
    ;
    return(rv);
};
function app_ui_standard_appendFieldPassword(grid, id, name, label, value) {
    var rv = $('<input class="w3-input w3-border w3-round" type="password"/>').attr('name', name).attr('id', id).val(value);
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).text(label))
        .append(rv)
    ;
    return(rv);
};
function app_ui_standard_appendElement(grid, label, theElement) {
   app_ui_standard_appendRow(grid)
        .append($('<div>').text(label))
        .append(theElement)
    ;
    return(theElement);
 }
function app_ui_standard_appendFieldText(grid, id, name, label, value) {
    var rv = $('<input class="w3-input w3-border w3-round" type="text"/>').attr('name', name).attr('id', id).val(value);
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).text(label))
        .append(rv)
    ;
    return(rv);
};
function app_ui_standard_appendFieldTextArea(grid, id, name, label, value) {
    var rv = $('<textarea class="w3-input w3-border w3-round"/>')
        .attr('name', name).attr('id', id).val(value);
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).attr("id", id+"_label").text(label))
        .append(rv)
    ;
    return(rv);
};


function app_ui_standard_appendFieldWorkflow(grid, id, name, label_key, value, wf) {
    var rv = $("<span>");
    if (!wf.statusList.includes(value)){
        value = wf.defaultStatus;
    }
    //-
    var insertStatus = function(rv, newstatusName, checked) {
        var newstatus = wf.statusIndex[newstatusName];
        rv
            .append(" ")
            .append(
                $("<span>")
                    .attr('title',newstatus.description)
                    .css('background-color', newstatus.color)
                    .append(
                        $('<input type="radio" value="">')
                            .attr("name", name)
                            .attr("id",   name+"__"+newstatusName)
                            .attr('value', newstatusName)
                            .prop("checked", checked)
                            .val(newstatusName)
                    )
                    .append($("<label>")
                            .attr("for", name+"__"+newstatusName)
                            .append(gRb(label_key + "__" + newstatusName))
                           )
            )
        ;
    }
    var steps = wf.statusIndex[value].steps;
    insertStatus(rv, value, true);
    if (steps != undefined) {
        rv.append(" " + gRb("db.sys.workflow.cambia.stato")+": ");
        steps.forEach(function(newstatusName) {
            stepInfo=wf.statusIndex[value].stepsIndex[newstatusName];
            // console.log(app_JSONStringify(stepInfo));
            if (app_userHasPermission(stepInfo.permission)) {
                insertStatus(rv, newstatusName, false);
            }
        });
    }
    //--qui--
    let info_out = $("<div class='w3-input w3-border w3-round'>").hide();
    let info = $("<span>")
        .append(" ")
        .append(app_getIcon("info_circle", 15))
        .attr("title", gRb("db.sys.workflow.info.about"))
        .click(function(){
            if (info_out.text() == "") {
                info_out.append($("<p>").text("Workflow:"));
                info_out.append($("<p>").text("Default status:"));
                {
                    let name = wf.defaultStatus;
                    let stato = wf.statusIndex[name];
                    let ss = $("<div>");
                    ss.append(app_getIcon("circle", 15).css("color",stato.color));
                    ss.append(stato.name);
                    info_out.append(ss);
                }
                info_out.append($("<p>").text("Status transactions:"));
                for (let i = 0; i < wf.statusList.length; i++) {
                    let name = wf.statusList[i];
                    let stato = wf.statusIndex[name];
                    let ss = $("<div>");
                    ss.append(app_getIcon("circle", 15).css("color",stato.color));
                    ss.append(stato.name);
                    info_out.append(ss);
                    for (let is = 0; is < stato.steps.length; is++) {
                        let sname = stato.steps[is];
                        let sstato = wf.statusIndex[sname];
                        let ssi = stato.stepsIndex[sname];
                        let sss = $("<div>");
                        sss.append(" ... ");
                        sss.append(app_getIcon("circle", 15).css("color",stato.color));
                        sss.append(stato.name);
                        sss.append(app_getIcon("arrow_right", 15));
                        sss.append(app_getIcon("circle", 15).css("color",sstato.color));
                        sss.append(sstato.name);
                        sss.append(" -- " + ssi.description);
                        sss.append(" -- " + ssi.permission);
                        info_out.append(sss);
                    }
                }
                info_out.append($("<pre>").text(app_JSONStringify(wf)));
                info_out.show(50);
            } else {
                info_out.hide(50, function(){info_out.text("")});
            }
        })
    ;
    //-
    rv.append(info);
    rv.append(info_out);
    /* 
        {
        var workflowContainer = $("<div>");
        ul.append($("<li/>")
                  .append($("<a/>").click(function() {
                      if(workflowContainer.html() == "") {
                          var form = $("<form>");
                          var fwinput = $("<input type='text' size='30'>");
                          fwinput.val("spacemr_space_people_book_workflow");
                          var out = $("<div>");
                          var go = $("<span>")
                              .text("GO")
                              .click(function(){
                                  let wf = app_workflow_get(fwinput.val());
                                  alert(wf);
                                  out.text(wf);
                              })
                          ;
                          //-
                          //-
                          form.append(fwinput)
                              .append(go)
                              .append(out)
                          ;
                          workflowContainer.html(form);
                      } else {
                          workflowContainer.html("");
                      }
	          }).text("display a Workflow "))
                  .append(workflowContainer));
    }
 */

    //-
    app_ui_standard_appendRow(grid)
        .append($('<label>').attr("for", id).text(gRb(label_key)))
        .append(rv)
    ;
    return(rv);
};


function app_list_appendFieldWorkflow(id, name, label_key, value, wf) {
    var rv = $("<span>");
    if (!wf.statusList.includes(value)){
        value = wf.defaultStatus;
    }
    //-
    var insertStatus = function(rv, oldstatusName, newstatusName, checked) {
        var newstatus = wf.statusIndex[newstatusName];
        rv
            .append(" ")
            .append(
                $("<span>")
                    .attr('title',newstatus.description)
                    .css('background-color', newstatus.color)
                    .append(
                        $('<input type="radio" value="">')
                            .attr("name", name)
                            .attr("id",   name+"__"+newstatusName)
                            .attr('value', newstatusName)
                            .prop("checked", checked)
                            .val(newstatusName)
                            .addClass("lafw__"+oldstatusName)
                    )
            )
        ;
    }
    var steps = wf.statusIndex[value].steps;
    insertStatus(rv, value, value, true);
    if (steps != undefined) {
        rv.append(" -> ");
        steps.forEach(function(newstatusName) {
            stepInfo=wf.statusIndex[value].stepsIndex[newstatusName];
            // console.log(app_JSONStringify(stepInfo));
            if (app_userHasPermission(stepInfo.permission)) {
                insertStatus(rv, value, newstatusName, false);
            }
        });
    }
    rv
        .append(" ")
        .append(
            $('<input type="text" size="8" value="">')
                .attr("name", name+"__nota")
                .attr("id",   name+"__nota")
                .addClass("lafw__nota__"+value)
                .attr('title',gRb("db.spacemr_space_people_book.nota"))
        )
    ;

    //-
    return(rv);
};



function app_list_appendFieldWorkflow_transactions(id, name, label_key
                                                   , transactions_statuses, wf
                                                   , update_hook
                                                   , update_log) {
    var rv = $("<div>")
        .css('display', "inline-block")
        .addClass('w3-round')
        .addClass('w3-panel')
        .addClass('w3-card')
    ;
    //-
    //-
    var statusList=wf.statusList;
    statusList.forEach(function(stato) {
        if (transactions_statuses[stato] != undefined){
            var rvstato = $("<div>");
            rv.append(rvstato);
            var insertStatus = function(rvstato, oldstatusName, newstatusName, thisName, checked) {
                var newstatus = wf.statusIndex[newstatusName];
                rvstato
                    .append(" ")
                    .append(
                        $("<span>")
                            .attr('title',newstatus.description)
                            .css('background-color', newstatus.color)
                            .append(
                                $('<input type="radio" value="">')
                                    .attr("name", thisName)
                                    .attr("id",   thisName+"__"+newstatusName)
                                    .attr('value', newstatusName)
                                    .prop("checked", checked)
                                    .val(newstatusName)
                                    .click(function(){
                                        $("input[value='"+newstatusName+"'].lafw__"+oldstatusName)
                                            .prop("checked", true);
                                        
                                        // .addClass("lafw__"+oldstatusName)
                                    })
                            )
                    )
                ;
            }
            var steps = wf.statusIndex[stato].steps;
            insertStatus(rvstato, stato, stato, name+"__"+stato, true);
            if (steps != undefined) {
                rvstato.append(" -> ");
                steps.forEach(function(newstatusName) {
                    stepInfo=wf.statusIndex[stato].stepsIndex[newstatusName];
                    // console.log(app_JSONStringify(stepInfo));
                    if (app_userHasPermission(stepInfo.permission)) {
                        insertStatus(rvstato, stato, newstatusName, name+"__"+stato, false);
                    }
                });
            }
            rvstato
                .append(" ")
                .append(
                    $('<input type="text" size="20" value="">')
                        .attr("name", stato+"__nota")
                        .attr("id",   stato+"__nota")
                        .addClass("lafw__nota")
                        .attr('title',gRb("db.spacemr_space_people_book.nota"))
                        .change(function(){
                            console.log("-- " + "input.lafw__nota__"+stato);
                            $("input.lafw__nota__"+stato)
                                .val($("#"+stato+"__nota").val());
                            // .addClass("lafw__"+oldstatusName)
                        })
                );
        }
    });
    rv
        .append(
            app_ui_standard_button()
                .text(gRb("db.sys.workflow.esegui.cambiamenti")
                      + " ")
                .click(update_hook)
        )
        .append(update_log)
    ;
    //-
    return(rv);
};


function app_ui_standard_appendFieldButton(grid, label) {
    var rv = app_ui_standard_button().text(label);
    app_ui_standard_appendRow(grid)
        .append(rv)
    ;
    return(rv);
};

function app_ui_standard_button() {
    var rv = $('<button class="w3-btn w3-padding w3-light-grey w3-round" type="button"/>');
    return(rv);
};

function app_ui_clickableLink(link) {
    var a = 
        $('<a/>')
        .attr('href' , "#" + link )
        .click(function(event){
            event.preventDefault();
            event.stopPropagation();
            app_initialization_setHashAndGoToPage(link);
        }) ;
    return(a);
};


function app_workflow_set(name, wf) {
    sys_workflows[name] = wf;
    // console.log(" -- app_workflow_set ["+name+"] " ); // + app_JSONStringify(wf)
    sys_workflows[name] = wf;
};
function app_workflow_get(name) {
    // console.log(" -- app_workflow_get ["+name+"] " ); // + app_JSONStringify(wf)
    return(sys_workflows[name]);
};
function app_workflow_apply_per_field_permisson(wf, status, grid) {
    //- fields by default are in read-write
    //- changes are done only for read-only and hidden fields.
    var canWrite = wf.statusIndex[status].per_field_permission_canWrite;
    var canRead  = wf.statusIndex[status].per_field_permission_canRead;
    var fieldList = wf.fieldList;
    fieldList.forEach(function(fieldName){
        // console.log(" --- field: " + fieldName);
        if (!canWrite[fieldName]) {
            if (canRead[fieldName]) {
                console.log(" --- setting readOnly: " + fieldName);
                grid.find("input[name='"+fieldName+"']").prop('readonly', 'readonly');
                grid.find("input[name='"+fieldName+"']").css('background-color', "lightgray");
                grid.find("textarea[name='"+fieldName+"']").prop('readonly', 'readonly');
                grid.find("textarea[name='"+fieldName+"']").css('background-color', "lightgray");
            } else {
                //- hide
                // console.log(" .. hiding " + fieldName);
                grid.find("input[name='"+fieldName+"']").closest("p").css('display','none');
                grid.find("textarea[name='"+fieldName+"']").closest("p").css('display','none');
            }
        }
    });
}


function app_tooltip_center() {
    let app_tooltip = $("#app_tooltip");
    if(app_tooltip.html() != "") {
        // console.log( " -- position: " + app_JSONStringify(app_tooltip.position()) );
        // console.log( " -- width: " + app_tooltip.width() );
        // console.log( " -- window.innerWidth: " + window.innerWidth );
        let position = app_tooltip.position().left;
        let width = app_tooltip.width();
        if (position > 10 && (position + width) > window.innerWidth) {
            // console.log(" -- try to move");
            position = window.innerWidth - width;
            if (position < 10) {
                position = 10;
            }
            app_tooltip.css("left", position + 'px');
        }
        //if (window.innerWidth < 600) {
        // }
    }
}
function app_tooltip_show(evt, content) {
    //- accepts jquery objects content
    /* 
       tspanc.addEventListener("mouseenter"
       , function(evt){
       app_tooltip_show(evt, "here I am");
       }); 
       tspanc.addEventListener("mouseleave"
       , function(evt){
       app_tooltip_hide();
       }); 
     */
    let app_tooltip = $("#app_tooltip");
    app_tooltip.html("");
    app_tooltip.append(content);
    app_tooltip.css("display", "block");
    app_tooltip.css("left", evt.pageX + 10 + 'px');
    app_tooltip.css("top",  evt.pageY + 10 + 'px');
    app_tooltip.show();
}
function app_tooltip_hide() {
    $("#app_tooltip")
        // .off("mouseleave click mouseover")
        .hide()
        .html("")
    ;
}

function app_applisttablemapper_get_row_index(content) {
    //- indexes columns of a standard query result
    let rv={};
    var rows = content.list.rows;
    let headers = content.list.headers;
    for (let ih = 0; ih < headers.length; ih++) {
        rv[headers[ih].name] = ih;
    }
    return(rv);
}

function app_tooltip_set_click_recursive(element, content_function, refresh_callback = null) {
    //-
    //-
    //- where content_function has the signature
    //-    content_function(evt, tooltip_div, refresh_callback)
    //- and
    //-    refresh_callback is a function called to redraw the page
    //-                     if the tooltip changed some data.
    //- see app_test.js for an usage example
    //-
    let the_tooltip = null;
    element.click(function(evt){
        // console.log(" evt.pageX " + app_JSONStringify(evt));
        evt.preventDefault();
        if (the_tooltip == null) {
            the_tooltip = $("<div>")
                .css("position","absolute")
                .css("background","white")
                .css("border", "1px solid black")
                .css("border-radius", "5px")
                .css("padding", "5px")
                .hide()
            ;
            the_tooltip.insertAfter( element );
            let tooltip_div = $("<div>");
            let close_me = $("<div>");
            tooltip_div.append(close_me);
            close_me
	        .append(app_getIcon("window-close",15))
	        .click(function(){
                    the_tooltip.remove();
                    the_tooltip = null;
                })
	        .attr("title", gRb("db.sys.close"))
            ;
            // the_tooltip.html("");
            content_function(evt, tooltip_div, refresh_callback);
            the_tooltip.append(tooltip_div);
            the_tooltip.css("display", "block");
            // console.log(" evt.pageX-1 " + (evt.pageX + 10 + 'px'));
            the_tooltip.css("left", evt.clienetX + 'px');
            the_tooltip.css("top",  evt.clienetY + 'px');
            if(the_tooltip.html() != "") {
                // console.log( " -- position: " + app_JSONStringify(the_tooltip.position()) );
                // console.log( " -- width: " + the_tooltip.width() );
                // console.log( " -- window.innerWidth: " + window.innerWidth );
                let position = the_tooltip.position().left;
                let width = the_tooltip.width();
                if (position > 10 && (position + width) > window.innerWidth) {
                    // console.log(" -- try to move");
                    position = window.innerWidth - width;
                    if (position < 10) {
                        position = 10;
                    }
                    the_tooltip.css("left", position + 'px');
                }
                //if (window.innerWidth < 600) {
                // }
            }
            the_tooltip.show(150);
        } else {
            the_tooltip.remove();
            the_tooltip = null;
        }
    });
    return(element);
}
function app_tooltip_set_on_click(evt, content_function, refresh_callback = null) {
    //-
    //- where content_function has the signature
    //-    content_function(evt, tooltip_div, refresh_callback)
    //- and
    //-    refresh_callback is a function called to redraw the page
    //-                     if the tooltip changed some data.
    //- see app_test.js for an usage example
    let tooltip_div = $("<div>");
    let close_me = $("<div>");
    tooltip_div.append(close_me);
    close_me
	.append(app_getIcon("window-close",15))
	.click(app_tooltip_hide)
	.attr("title", gRb("db.sys.close"))
    ;
    content_function(evt, tooltip_div, refresh_callback);
    app_tooltip_show(evt, tooltip_div);
    // tooltip_div.mouseenter(function(){
    //     app_tooltip_hide_on_mouseleave = false;
    //     tooltip_div.mouseleave(app_tooltip_hide);
    //     // console.log(" -- mouseenter on tooltip_div");
    // });
}
function app_tooltip_set_click(element, content_function, refresh_callback = null) {
    //-
    //-
    //- where content_function has the signature
    //-    content_function(evt, tooltip_div, refresh_callback)
    //- and
    //-    refresh_callback is a function called to redraw the page
    //-                     if the tooltip changed some data.
    //- see app_test.js for an usage example
    //-
    element.click(function(evt){
        evt.preventDefault();
	app_tooltip_set_on_click(evt, content_function, refresh_callback);
    });
    return(element);
}
function app_tooltip_set(element, content_function, refresh_callback = null) {
    //-
    //- where content_function has the signature
    //-    content_function(evt, tooltip_div, refresh_callback)
    //- and
    //-    refresh_callback is a function called to redraw the page
    //-                     if the tooltip changed some data.
    //- see app_test.js for an usage example
    //-
    let app_tooltip_hide_on_mouseleave = true;
    element.mouseenter(function(evt){
        let tooltip_div = $("<div>");
	let close_me = $("<div>");
	tooltip_div.append(close_me);
	close_me
	    .append(app_getIcon("window-close",15))
	    .click(app_tooltip_hide)
	    .attr("title", gRb("db.sys.close"))
	;
        content_function(evt, tooltip_div, refresh_callback);
        app_tooltip_show(evt, tooltip_div);
        tooltip_div.mouseenter(function(){
            app_tooltip_hide_on_mouseleave = false;
            tooltip_div.mouseleave(app_tooltip_hide);
            // console.log(" -- mouseenter on tooltip_div");
        });
        // console.log(" -- mouseenter on original element");
    });
    element.mouseleave(function(evt){
        // console.log(" -- mouseleave on original element");
        setInterval(function() {
            // console.log(" -- mouseleave on setInterval");
            if (app_tooltip_hide_on_mouseleave) {
                // app_tooltip_hide();
            }
        }
                    , 100
        );
    });
    // console.log(" -- tooltip set");
    return(element);
}

console.log(true);
function log(msg) {
    // $('#app_log').append($('<p>').text(msg));
    console.log(msg);
}

$('#app_messages').hide();

