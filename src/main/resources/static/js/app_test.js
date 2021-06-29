function app_test_doInitialize(callback) {
    log("test page initialization...");
    callback();
}


(function( $ ){

  $.fn.fitText = function( kompressor, options ) {

    // Setup options
    var compressor = kompressor || 1,
        settings = $.extend({
          'minFontSize' : Number.NEGATIVE_INFINITY,
          'maxFontSize' : Number.POSITIVE_INFINITY
        }, options);

    return this.each(function(){

      // Store the object
      var $this = $(this);

      // Resizer() resizes items based on the object width divided by the compressor * 10
      var resizer = function () {
        $this.css('font-size', Math.max(Math.min($this.width() / (compressor*10), parseFloat(settings.maxFontSize)), parseFloat(settings.minFontSize)));
      };

      // Call once to set.
      resizer();

      // Call on resize. Opera debounces their resize by default.
      $(window).on('resize.fittext orientationchange.fittext', resizer);

    });

  };

})( jQuery );


function app_test_loadTestPage() {
    var divPage = $("<div>");
    divPage.append("tests singoli:")
    var ul = $("<ul/>");
    //-
    {
        var iconContainer = $("<div>");
        ul.append($("<li/>")
                  .append($("<a/>").click(function() {
                      if(iconContainer.html() == "") {
                          var icons = $("<div>");
                          for (var icon_name in sys_app_icons) {
                              icons
                                  .append(app_getIcon(icon_name, 15))
                                  .append(icon_name)
                                  .append($("<br>"))
                              ;
                          }
                          iconContainer.html(icons);
                      } else {
                          iconContainer.html("");
                      }
	          }).text("Icons list"))
                  .append(iconContainer));
    }
    
    {
        var iconContainer = $("<div>");
        ul.append($("<li/>")
                  .append($("<a/>").click(function() {
                      if(iconContainer.html() == "") {
                          var c = $("<div>");
                          var auth_single_sign_on_protected_url =
                              sys_session.userData["auth_single_sign_on_protected_url"];
                          // if (single_sign_on) {
                          if (auth_single_sign_on_protected_url == undefined) {
                              c.append("no auth_single_sign_on_protected_url");
                          } else {
                              c.append("good. " + auth_single_sign_on_protected_url);
                              var form = $("<form>");
                              var tobeEncodedHash = $("<input size=160>");
                              var encode_out = $("<input size=160>");
                              var decode_out = $("<input size=160>");
                              var comments   = $("<div>");
                              tobeEncodedHash.val(location.hash);
                              var encode=$("<span>")
                                  .append(" encode")
                                  .click(function(){
                                      encode_out.val(btoa(tobeEncodedHash.val()));
                                  })
                              ;
                              var decode=$("<span>")
                                  .append(" decode")
                                  .click(function(){
                                      decode_out.val(atob(encode_out.val()));
                                      if (tobeEncodedHash.val() == decode_out.val()) {
                                          comments.html("match ok.");
                                      } else {
                                          comments.html("NO MATCH!");
                                      }
                                  })
                              ;
                              var url_encode=$("<span>")
                                  .append(" url_encode")
                                  .click(function(){
                                      var u = auth_single_sign_on_protected_url
                                          + "/" + btoa(tobeEncodedHash.val());
                                      encode_out.val(u);
                                      ;
                                      comments.html("")
                                          .append($("<a>")
                                                  .attr("href",u)
                                                  .text(u)
                                                 )
                                      ;
                                  })
                              ;
                              var url_decode=$("<span>")
                                  .append(" url_decode")
                                  .click(function(){
                                      var u = encode_out.val();
                                      var pos = u.indexOf(auth_single_sign_on_protected_url);
                                      pos = pos + auth_single_sign_on_protected_url.length + 1;
                                      u = atob(u.substring(pos));
                                      decode_out.val(u);
                                      if (tobeEncodedHash.val() == decode_out.val()) {
                                          comments.html("match ok.");
                                      } else {
                                          comments.html("NO MATCH!");
                                      }
                                  })
                              ;
                              form.append("tobeEncodedHash: ")
                                  .append(tobeEncodedHash)
                                  .append(encode)
                                  .append(url_encode)
                                  .append($("<br>"))
                                  .append(encode_out)
                                  .append(decode)
                                  .append(url_decode)
                                  .append($("<br>"))
                                  .append(decode_out)
                                  .append(comments)
                              ;
                              c.append(form);
                          }
                          iconContainer.html(c);
                      } else {
                          iconContainer.html("");
                      }
	          }).text("auth_single_sign_on_protected_url url-encoding test"))
                  .append(iconContainer));
    }
    {
        var backgroundImageContainer = $("<div>");
        ul.append($("<li/>")
                  .append($("<a/>").click(function() {
                      if(backgroundImageContainer.html() == "") {
                          let icons = [ "bars", "calendar_alt", "check", "circle", "circle_empty", "cut", "database", "home", "lens_search", "map_black", "plus-square", "save", "undo", "user_group", "trash_can", "user"
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
                          let i_icon  = icons[getRandomInt(icons.length)];
                          let i_color = colors[getRandomInt(colors.length)];
                          var svg =
                              app_getIcon(i_icon, w)
                              .width(w)
                              .css("color",i_color)
                          ;
                          backgroundImageContainer.html(svg);
                          let textd = $("<div>").hide();
                          setTimeout((function() {
                              let p = svg.position();
                              textd
                                  .append($("<div>").append("QuestoNome"))
                                  .append($("<div>").append("QuestoCognome"))
                                  .append($("<div>").append("Progressivo"))
                                  .append($("<div>").append("icon: " + i_icon))
                                  .append($("<div>").append("color: " + i_color))
                                  .append($("<div>").append("left "+p.left + 'px'))
                                  .append($("<div>").append("top  " + p.top + 'px'))
                                  .addClass("w3-theme-dark-color")
                              // .css("color","gray")
                              ;
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
                                  // .css("font-size","25vw")
                                  // .css("font-weight","bold")
                              ;
                              textd.show();
                          })
                              ,  0.2 );
                          backgroundImageContainer.append(textd);
                      } else {
                          backgroundImageContainer.html("");
                      }
	          }).text("Genera un'immagine di background"))
                  .append(backgroundImageContainer));
    }
    
    {
        var headersContainer = $("<div>");
        ul.append($("<li/>")
                  .append($("<a/>").click(function() {
                      if(headersContainer.html() == "") {
                          headersContainer.html("Peforming headers request...");
                          app_doRequestMappingRequest("app_system_test/app_system_test_get_request_headers"
                                                      , { }
                                                      , function(content) {
                                                          headersContainer.html("headers:");
                                                          let p=$("<pre>");
                                                          p.append(app_JSONStringify(content));
                                                          headersContainer.append(p);
                                                      });
                      } else {
                          headersContainer.html("");
                      }
                  }).text("Test received server Headers (for shibboleth)"))
                  .append(headersContainer));
    }
    {
        var mdiv = $("<div>");
        var testOpen = function() {
            if (mdiv.text() == "") {
                let i_url =
                    $("<textarea type='text' rows='1' cols='100'>")
                    .val("spacemr_space_people/spacemr_space_people_has_current_spaces");
                let i_content = 
                    $("<textarea type='text' rows='10' cols='100'>")
                    .val('["acorni","sonia","71401"]');
                let i_out = 
                    $("<textarea type='text' rows='10' cols='100'>")
                    .val('');
                let i_submit = 
                    $("<input type='submit'>")
                    .val('submit')
                    .click(function(e){
                        e.preventDefault();
                        i_out.val("sending...");
                        // alert("submit");
                        var datastr = i_content.val();
                        var ajaxparams =  { 
                            url: i_url.val()
                            ,type: "POST"
                            ,data: { content : datastr }
                            ,dataType: "Intelligent"
                            ,async: false
                        };
                        $.ajax(ajaxparams)
                            .always(function(msg) {
                                if (msg.readyState == 0) {
                                    var errormessage = "";
                                    errormessage = "Error connecting to the server url: " + url
                                        + "\n status: " + app_JSONStringify(msg);
                                    i_out.val("Error:\n"+errormessage);
                                } else {
                                    i_out.val(msg.responseText);
                                }
                            })
                        ;    
                    });
                ;
                let fm =
                    $("<form>")
                    .append("submit-info:")
                    .append(i_submit)
                    .append($("<div>")
                            .append("url: ")
                            .append(i_url))
                    .append($("<div>")
                            .append("content: ")
                            .append(i_content))
                    .append($("<div>")
                            .append("out: ")
                            .append(i_out))
                mdiv.html(fm);
            } else {
                mdiv.text("");
            }
        }
        ul.append($("<li/>")
                  .append($("<a/>")
                          .text("Test a submit")
                          .click(function(e) {
                              e.preventDefault();
                              testOpen();
                          }
                                ))
                  .append(mdiv)
                 );
    }
    {
        ul.append($("<li/>")
                  .append( app_tooltip_set($("<span/>")
                                           , function(evt, tooltip_div){
                                               tooltip_div
                                                   .append("this is the text to display");
                                           })
                           .text("app_tooltip_set-1")
                         )
                  .append( app_tooltip_set($("<span/>")
                                           , function(evt, tooltip_div){
                                               tooltip_div
                                                   .append("second text to display")
                                                   .append($("<div>")
                                                           .text("click_me_text")
                                                           .click(function(){
                                                               tooltip_div
                                                                   .append($("<div>")
                                                                           .text(" -click"))
                                                           })
                                                          )
                                               let recursive_tooltip_div = $("<div>")
                                                   .append("try a recursive tooltip");
                                               tooltip_div.append(recursive_tooltip_div);
                                               app_tooltip_set_click_recursive(
                                                   recursive_tooltip_div
                                                   , function(evt, tooltip_div, refresh_callback){
                                                       tooltip_div
                                                           .append("I am a recursive tooltip!");
                                                   }
                                               );
                                               ;
                                           })
                           .text(", app_tooltip_set-2")
                         )
                  .append( app_tooltip_set_click($("<span/>")
						 , function(evt, tooltip_div){
						     tooltip_div
							 .append("this has the same interface of app_tooltip_set");
						 })
                           .text(", app_tooltip_set_click")
                         )
                  .append($("<span>")
                          .text(", app_tooltip_set_on_click")
                          .click(function(evt) {
                              app_tooltip_set_on_click(evt
                                                       , function(evt, tooltip_div, refresh_callback){
                                                           tooltip_div
                                                               .append("different signature - hello on click")
                                                               .append($("<div>")
                                                                       .text("click_me_text")
                                                                       .click(function(){
                                                                           tooltip_div
                                                                               .append($("<div>")
                                                                                       .text(" -click"))
                                                                       })
                                                                      )
                                                           let recursive_tooltip_div = $("<div>")
                                                               .append("try a recursive tooltip");
                                                           tooltip_div.append(recursive_tooltip_div);
                                                           app_tooltip_set_click_recursive(
                                                               recursive_tooltip_div
                                                               , function(evt, tooltip_div, refresh_callback){
                                                                   tooltip_div
                                                                       .append("I am a recursive tooltip!");
                                                               }
                                                           );
                                                       }
                                                       , null) 
                          })
                         )
                 );
        
    }
    ul.append($("<li/>").append($("<a/>").click(function(){
        $( this ).hide( "slow" );
        setTimeout((function(obj) { obj.show("slow"); })( $(this) ),  300 );
    }).text("generico - hide and show")));
    {
        //- datepicker - pickaday tests
        var do_fill = function(container) {
            var f = $("<form>");
            console.log(moment().format(sys_dateFormatUi));
            //-
            //-
            //-
            {
                var input = $("<input type='text'>").val(moment().format(sys_dateFormatUi));
                input.pikaday({format: sys_dateFormatUi
                               , i18n: { months: moment.localeData()._months
                                         , weekdays: moment.localeData()._weekdays
                                         , weekdaysShort: moment.localeData()._weekdaysShort }
                              });
                f.append($("<div>").append("date: ").append(input));
            }
            {
                var input = $("<input type='text'>").val(moment().format(sys_timestampFormatUi));
                input.pikaday({format: sys_timestampFormatUi
                               , i18n: { months: moment.localeData()._months
                                         , weekdays: moment.localeData()._weekdays
                                         , weekdaysShort: moment.localeData()._weekdaysShort }
                               , onSelect: function() {
                                   console.log(" val: " + input.val());
                                   // picker non mi permette di accedere ai valori
                                   // dell'ora.
                                   input.val(moment((this.getDate().getTime())).format(sys_timestampFormatUi));
                               }
                              });
                // input.change(function() {
                //     console.log(" change - " + input.val());
                //     var old = input.val();
                //     var nuovo = moment.format(moment(old) + input.app_status.partOfDay);
                //     input.val(nuovo);
                // })
                f.append($("<div>")
                         .append("dateTime: ").append(input)
                         .append(" now: " + moment().format(sys_timestampFormatUi) )
                        );
                f.append($("<b>").append(" per ora NON gestisco i TIMESTAMP con datetime - si perde l'ora! " ))
            }
            //-
            //-
            s.append(f);
        };
        var s = $("<div/>");
        ul.append($("<li/>")
                  .append($("<div/>")
                          .text("datepick tests")
                          .click(function(){
                              if(s.children().length > 0) {
                                  s.html("");
                              } else {
                                  do_fill(s);
                              }
                          }))
                  .append(s)
                 );

    }
    ul.append($("<li/>").append($("<a/>").text("getApplicationDirectory")
                                .click(function(){
                                    app_doRequestMappingRequest("hello/hello_get_application_directory"
                                                                , { }
                                                                , function(content) {
	                                                            app_setMessage(app_JSONStringify(content));
                                                                });
                                })));
    ul.append($("<li/>").append($("<a/>").click(function(){
        $("#app_footer_text").text("test - " + new Date());
    }).text("set footer")));
    ul.append($("<li/>").append($("<a/>").click(function(){
        app_setMessage("message test - " + new Date());
    }).text("set message")));
    ul.append($("<li/>").append($("<a/>").click(function(){
        $( this ).hide( "slow" );
        setTimeout((function(obj) { obj.show("slow"); })( $(this) ),  300 );
    }).text("Hide and show")));
    ul.append($("<li/>").append($("<a/>").click(function(){
        // forse devo usare il search di jquery??
	$(this).text("Eseguito! Prova a fare click su un link in questa pagina...");
	var i = 0;
        $('a').each(function(e)  {
            this.iindex = i;
            $(this).off('click');
            $(this).click('click', function(e){
                    e.preventDefault();
                    alert( '\nI am link #' + i 
			   +'\nthis iindex #' + this.iindex 
			   + '\n ---------------- '
			   +'\nthis: ' + app_JSONStringify(this) 
			   + '\n ---------------- '
			   + '\nevent: ' + app_JSONStringify(e)
			 );
		});
	    i++;
        })
    }).text("addEventListener e proprieta degli '<a>'")));
    //-
    ul.append($("<li/>").append($("<a/>").click(function() {
	$.getJSON( "sys/sessioninfo", function( data ) {
	    var s = JSON.stringify(data, "", "  ");
	    doPopupPre(s);
	}); }).text("mvc demo - Session Info")))
    ul.append($("<li/>").append($("<a/>").click(function(){
	log('ciao2'); $(this).text($(this).text() + " - log written.");
    }).text("javascript demo - Log Ciao2")))
    ul.append($("<li/>").append($("<a/>").click(function(){doPopup('textToPopup2')}).text("jquery mobile Popup 2")))
    ul.append($("<li/>").append($("<a/>").click(function(){doPopupPre('textToPopup2')}).text("jquery mobile Popup Pre")))
    ul.append($("<li/>").append($("<a/>").click(app_test_doGetLocationInfo).text("javascript Location Info ")))
    ul.append($("<li/>").append($("<a/>").click(function() {
	app_loadScriptAndExecute("app_test", 'app_test_doGetLocationInfo');}).text("app_loadScriptAndExecute test")))
    ul.append($("<li/>").append($("<a/>").click(function() {
	document.location = 'http://web.ing.unimo.it/';}).text("set location to http://web.ing.unimo.it/")))
    ul.append($("<li/>").append($("<a/>").click(function() {
	document.location.search = 'page=pippo';}).text("set location.search to page=pippo")))
    ul.append($("<li/>").append($("<a/>").click(function() {
	app_test_showMobilePropertiesInfo('#div-showMobilePropertiesInfo')})
				.text("show $.mobile Properties Info")
			       )
	      .append("<div id='div-showMobilePropertiesInfo'/>")
	     );
    ul.append($("<li/>").append($("<a/>").click(function() {
        appGetPage()
            .append($("<p/>")
                    .text("a new row.")
                   ); })
				.text("append a row to the 'page'")
			       )
	      .append("<div id='div-showMobilePropertiesInfo'/>")
	     );
    {
        var d = '22/12/1970';
        var dsecs = appConvertStringToTimestamp(d);
        ul.append($("<li/>").append("app appConvertStringToTimestamp - ["+d+"]: " + dsecs + " - ["+new Date(dsecs)+"]"));
    }    
    //-
    //-
    divPage.append(ul);
    divPage.append("Pagine di test");
    ul = $("<ul/>");
    //-
    ul.append($("<li/>").append($("<a/>").click(function(){
        app_initialization_setHashAndGoToPage("?page=app_test__app_test_loadTestPage_widgets");
    }).text("Widgets tests")));
    divPage.append(ul);
    //-
    // app_test_showMobilePropertiesInfo();
    appSetPage(divPage, "test page");
}


function app_test_loadTestPage_widgets() {
    var divPage = $("<div>");
    //-
    divPage.append("tests singoli:")
    var ul = $("<ul/>");
    //-
    ul.append($("<li/>").append($("<a/>").click(function(){
	if ($(this).parent().find("div").length > 0) {
	    $(this).parent().find("div").remove();
	    $(this).text("widget - sortablecheckboxes");
	} else {
            $(this).text( 'widget - press to "destroy"' );
	    var div = $("<div>");
	    //-
	    div.append("sortablecheckboxes1");
	    var sckb = appGetWidget("custom.sortablecheckboxes", $("<div>")
                                    , {
                                        legend: "drag and drop widget header legend"
                                        , name: "sortablecheckboxes_dd"
                                        , values: ["a"
                                                   , "c"
                                                   , "e"
                                                   , "r"
                                                  ]
                                        , valuesAll: [ "a"
                                                       , "b"
                                                       , "c"
                                                       , "d"
                                                       , "e"
                                                       , "f"
                                                     ]
                                        , labels: { a: "label a"
                                                    , b: "label b longer text"
                                                    , c: "label c"
                                                    , d: "label for d"
                                                    , e: "this is the 'e' label"
                                                  }
                                        , labelPrefix: "dad_prefix"
                                        , change: function(wid) {
                                            $("#out1").text(wid.values());
                                        }
                                    });
	    div.append(sckb.element);
	    //-
	    $(this).after(div);
	    //-
            var out=$('<div>');
            div.append($("<a>").text("show values").click(function(){
                out.text(app_JSONStringify(sckb.values()));
            }));
            div.append(out);
            var out1 = $('<div id="out1">').text("out");
            div.append(out1);
	    //-
	}
    }).text("widget - sortablecheckboxes")));
    
    //-
    ul.append($("<li/>").append($("<a/>").click(function(){
	if ($(this).parent().find("div").length > 0) {
	    $(this).parent().find("div").remove();
	    $(this).text("widget - sortablecheckboxes - with no label lookup");
	} else {
            $(this).text( 'widget - press to "destroy"' );
	    var div = $("<div>");
	    //-
	    div.append("sortablecheckboxes1");
	    var sckb = appGetWidget("custom.sortablecheckboxes", $("<div>")
                                    , {
                                        legend: "widget header legend"
                                        , name: "sortablecheckboxes_dd"
                                        , values: ["a"
                                                   , "c"
                                                   , "e"
                                                   , "r"
                                                  ]
                                        , valuesAll: [ "a"
                                                       , "b"
                                                       , "c"
                                                       , "d"
                                                       , "e"
                                                       , "f"
                                                     ]
                                    });
	    div.append(sckb.element);
	    //-
	    $(this).after(div);
	    //-
            var out=$('<div>');
            div.append($("<a>").text("show values").click(function(){
                out.text(app_JSONStringify(sckb.values()));
            }));
            div.append(out);
	    //-
	}
    }).text("widget - sortablecheckboxes - with no label lookup")));

    //-
    //-
    ul.append($("<li/>").append($("<a/>").click(function(){
        $( this ).hide( "slow" );
        setTimeout((function(obj) { obj.show("slow"); })( $(this) ),  300 );
    }).text("hide and show")));
    //-
    //-
    divPage.append(ul);
    //-
    appSetPage(divPage, "test page widgets");
}




function app_test_showMobilePropertiesInfo(id) {
    //-
    //-
    //- display $.mobile properties
    //-
    //-
    if ( ("" + $(id).html()).length > 0) {
	$(id).html("");
    } else {
    var table=$('<table data-role="table" id="table-showMobilePropertiesInfo" class="ui-responsive table-stroke"/>');
    table.append($('<thead/>')
		 .append($('<th/>').text("Property name"))
		 .append($('<th/>').text("value"))
		);
    $.each( $.mobile, function( key, val ) {
	var valo;
	if (typeof val === 'object' && val !== null) {
	    valo = $('<td/>').append($('<pre/>').text("" + app_JSONStringify(val)));
	} else {
	    valo = $('<td/>').text(""+val);
	}
	table.append($('<tr/>')
		     .append($('<td/>').text("$.mobile." + key))
		     .append(valo)
		    );
    });
	$(id).html(table);
    }
}


function app_test_doGetLocationInfo() {
    log("here.. app_test_doQueryUrlTests");
    var l = {
	'location': ""+document.location
	,'hash': document.location.hash
	,'hostname': document.location.hostname
	,'href': document.location.href
	,'origin': document.location.origin
	,'pathname': document.location.pathname
	,'port': document.location.port
	,'protocol': document.location.protocol
	,'search': document.location.search
    };
    doPopupPre(app_JSONStringify(l));
}



