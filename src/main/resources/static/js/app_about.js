function app_about_doInitialize(callback) {
    log("test page initialization...");
    callback();
}

function app_about_loadAboutPage() {
    var page = $("<div>");
    var grid = app_ui_standard_getGrid(page);
    //-
    var d = $("<div>");
    //-
    let fformat = function(ul, o){
        ul .append($("<li>")
                   .append(o.t)
                   .append(" - ")
                   .append($("<a>").attr("href",o.u).append(o.u))
                  )
    };
    var app_about_custom = $("<div>").css("margin-top", "40px");
    //-
    d.append($("<div>").append("Applicazione Spazi - DIEF").css("font-weight","bold"));
    d.append(app_about_custom);
    app_doRequestMappingRequest("app_user/app_get_system_property_app_about_custom"
                                , {}
                                , function(content) {
                                    app_about_custom.html(content.app_about_custom);
                                });
    d.append($("<div>").append("Author").css("margin-top", "40px"));
    {
        var ul = $("<ul>");
        d.append(ul);
        let o = {"t":"Alberto Corni", "u": "http://personale.unimore.it/rubrica/dettaglio/acorni"}
        fformat(ul,o);
    }
    d.append($("<div>").append("Thanks to").css("margin-top", "40px"));
    {
        var ul = $("<ul>");
        d.append(ul);
        let o = {"t":"Elena Fabbri - for support", "u": "http://personale.unimore.it/rubrica/dettaglio/efabbri"}
        fformat(ul,o);
    }
    d.append($("<div>").append("Credits").css("margin-top", "20px"));
    {
        let v = [
            {"t":"DIEF Dipartimento Ingegneria Enzo Ferrari", "u": "http://www.ingmo.unimore.it/"}
            ,{"t":"Java - GNU General Public License (GNU GPL) version 2 with a linking exception", "u": "https://openjdk.java.net/"}
            ,{"t":"JQuery - JavaScript library - MIT license", "u": "https://jquery.org/license/"}
            ,{"t":"Spring - Java develpmente Framework - Apache License 2.0", "u":"https://spring.io/"}
            ,{"t":"Moment - dates and times in JavaScript - MIT license", "u":"https://momentjs.com/"}
            ,{"t":"pikaday - refreshing JavaScript Datepicker - MIT license", "u":"https://pikaday.com/"}
            ,{"t":"hikariCP - JDBC Connection Pooling - Apache License 2.0", "u":"https://github.com/brettwooldridge/HikariCP/"}
            ,{"t":"freemarker - template language - Apache License 2.0", "u":"https://freemarker.apache.org/"}
            ,{"t":"fontawesome - svg images - CC BY 4.0 License", "u":"https://fontawesome.com/"}
            ,{"t":"logback - logging framework - EPL v1.0 and the LGPL 2.1", "u":"http://logback.qos.ch/"}
            ,{"t":"com.sun.mail - email client -  Common Development and Distribution License (CDDL) v1.1 and GNU General Public License (GPL) v2 with Classpath Exception", "u":"https://javaee.github.io/javamail/JavaMail-License/"}
            ,{"t":"QRCode.js - javascript QRCode generator - MIT License", "u":"https://davidshimjs.github.io/qrcodejs/"}
        ];
        var ul = $("<ul>");
        d.append(ul);
        //-
        v.forEach(o =>fformat(ul, o));
    }
    //-
    //-
    grid.append(d);
    //-
    appSetPage(page, "About");
    // }
}

