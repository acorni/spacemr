package it.unimo.app.controller;

import java.util.Date;
import java.util.Vector;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapperCsv;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Workflow;

import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.json.JSONObject;
import org.json.JSONArray;

import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppPermissionTools_userData;

/** 
   Controllers tools library
*/
public class AppControllerTableMattoni {



   public AppControllerTableMattoni(String labelPrefix
                                    , Vector<String> checkVector
                                    , HashMap<String, Object> qsargs
                                    , JSONObject qparams
                                    , StringBuffer qs
                                    , StringBuffer qswhere
                                    , StringBuffer qsorderBy
                                    , Tools tools
                                    , HttpSession session
                                    ) {
      _labelPrefix    = labelPrefix;
      _checkVector    = checkVector;
      _qsargs    = qsargs;
      _qparams   = qparams;
      _qs        = qs;
      _qsorderBy = qsorderBy;
      _qswhere   = qswhere;
      _qorderBy  = qparams.has("order")  ? qparams.getJSONArray("order") : null;
      _qwhere    = qparams.has("where")  ? qparams.getJSONObject("where") : null;
      _tools     = tools;
      _session   = session;
   }
   
   public JSONObject getQwhere(){
      return(_qwhere);
   };
   public Object getQwhereParamAsObject(String key){
      Object v = null;
      if (_qwhere.has(key)) {
         v = _qwhere.get(key).toString();
         if (v.equals("")) {
            v = null;
         }
      }
      return(v);
   };
   public void setCustomHeaderAndAddColumn(String column_name, String stanrdardHeaderId) {
      _customHeaders.put(column_name, AppListTableMapper.getHeader(stanrdardHeaderId));
      if (!_columns.contains(column_name) ) {
         _columns.add(column_name);
      }
   }
   public void setCustomHeader(String column_name, String stanrdardHeaderId) {
      _customHeaders.put(column_name, AppListTableMapper.getHeader(stanrdardHeaderId));
   }

   public AppListTableMapper getMapperJson() {
      AppListTableMapper rv = new AppListTableMapper(_labelPrefix, _customHeaders);
      return(rv);
   }

   public AppListTableMapper getMapperCsv(PrintWriter printWriter
                                          , AppPermissionTools_userData userData
                                          ) {
      AppListTableMapper rv = new AppListTableMapperCsv(_labelPrefix
                                                        , _customHeaders
                                                        , printWriter
                                                        , userData
                                                        );
      return(rv);
   }


   public void setOrderByString() {
      if ( _qorderBy != null) {
         // -----
         String comma = "";
         for(int i = 0 ; i < _qorderBy.length(); i++){
            JSONObject jcol = _qorderBy.getJSONObject(i);
            String columnName = jcol.getString("column");
            boolean desc      = jcol.has("desc");
            if (_checkVector.contains(columnName)) {
               _qsorderBy.append(comma + columnName + (desc ? " desc":" asc"));
               comma = ", ";
            }
         }
      }
   }

   public void addAnd(String statement) {
      _qswhere.append(""
                      + ((_qswhere.length()>0) ? "\n and " : "")
                      + " (" + statement + ")"
                      );
   }
   public void addQswhere(String key, Object value) {
      _qsargs.put(key, value);
   }

   public void setWhereStringLike(String key
                                   , String sqlkeyname
                                  ) {
      if (_qwhere != null) {
         String v = null;
         if (_qwhere.has(key)) {
            v = _qwhere.getString(key);
            if (v.equals("")) {
               v = null;
            }
         }
         if (v != null) {
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " like :wc__"+key + ")"
                            );
            _qsargs.put("wc__"+key, _tools.sql_convertToLikeClause(v));
            // System.out.println(" ---v: " + _tools.sql_convertToLikeClause(v));
         } 
      }
   }

   public void setWhereInIds(String key
                             , String sqlkeyname
                             ) {
      if (_qwhere != null) {
         JSONArray v = null;
         if (_qwhere.has(key)) {
            v = _qwhere.getJSONArray(key);
            if (v.length() > 0) {
               StringBuffer qs = new StringBuffer();
               String comma = "";
               for (int i = 0; i < v.length(); i++) {
                  Integer val = new Integer(v.getInt(i));
                  String keyName = "wc__"+key+"_"+i;
                  qs.append(comma + ":"+keyName );
                  _qsargs.put(keyName, val);
                  comma = ", ";
               }
               addAnd(" "+sqlkeyname+" in ("+qs.toString()+")");
            }
         }
      }
   }

   
   public void setWhereStringEqual(String key
                                   , String sqlkeyname
                                   ) {
      if (_qwhere != null) {
         String v = null;
         if (_qwhere.has(key)) {
            v = _qwhere.getString(key);
            if (v.equals("")) {
               v = null;
            }
         }
         if (v != null) {
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " = :wc__"+key + ")"
                            );
            _qsargs.put("wc__"+key, v);
         } 
      }
   }

   public void setWhereInteger(String key
                               , String sqlkeyname
                               ) {
      if (_qwhere != null) {
         String v = null;
         String k = null;
         //-
         //-
         k = key + "__from";
         v =  null;
         if (_qwhere.has(k)) {
            v = _qwhere.get(k).toString();
            if (v.equals("")) {
               v = null;
            }
         }
         if (v != null) {
            Integer d = Integer.valueOf(v);
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " >= :wc__"+k+ ")"
                            );
            _qsargs.put("wc__"+k, d);
            // System.out.println(" --- "+k+": " + d);
         } 
         //-
         //-
         k = key + "__to";
         v =  null;
         if (_qwhere.has(k)) {
            v = _qwhere.get(k).toString();
            if (v.equals("")) {
               v = null;
            }
         }
         if (v != null) {
            Integer d = Integer.valueOf(v);
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " <= :wc__"+k+ ")"
                            );
            _qsargs.put("wc__"+k, d);
            // System.out.println(" --- "+k+": " + d);
         } 
      }
   }


   public void setWhereIntegerEqual(String key
                                    , String sqlkeyname
                                    ) {
      if (_qwhere != null) {
         String v = null;
         //-
         //-
         if (_qwhere.has(key)) {
            v = _qwhere.get(key).toString();
            if (v.equals("")) {
               v = null;
            }
         }
         if (v != null) {
            Integer d = Integer.valueOf(v);
            _qswhere.append("\n"
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " = :wc__"+key+ ")"
                            );
            _qsargs.put("wc__"+key, d);
            // System.out.println(" --- "+key+": " + d);
         } 
      }
   }

   public void setWhereDecimal(String key
                               , String sqlkeyname
                               ) {
      if (_qwhere != null) {
         BigDecimal d = null;
         String k = null;
         //-
         //-
         k = key + "__from";
         d =  _tools.jsonObject_getBigDecimal(_qwhere,k);
         if (d != null) {
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " >= :wc__"+k+ ")"
                            );
            _qsargs.put("wc__"+k, d);
            // System.out.println(" --- "+k+": " + d);
         } 
         //-
         //-
         k = key + "__to";
         d =  _tools.jsonObject_getBigDecimal(_qwhere,k);
         if (d != null) {
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " <= :wc__"+k+ ")"
                            );
            _qsargs.put("wc__"+k, d);
            // System.out.println(" --- "+k+": " + d);
         } 
      }
   }

   public void setWhereBoolean(String key
                               , String sqlkeyname
                               ) {
      if (_qwhere != null) {
         String k = null;
         Boolean t = null;
         Boolean f = null;
         //-
         //-
         k = key + "__true";
         t =  _tools.jsonObject_getBoolean(_qwhere,k);
         k = key + "__false";
         f =  _tools.jsonObject_getBoolean(_qwhere,k);
         //-
         if (t != null && t.booleanValue()) {
            if (f != null && f.booleanValue()) {
               _qswhere.append(""
                               + ((_qswhere.length()>0) ? "\n and " : "")
                               + " (" 
                               + " (" + sqlkeyname + " = true )"
                               + " or "
                               + " (" + sqlkeyname + " = false )"
                               +  ")"
                               );
            } else {
               _qswhere.append(""
                               + ((_qswhere.length()>0) ? "\n and " : "")
                               + " (" + sqlkeyname + " = true )"
                               );
            }
         } else {
            if (f != null && f.booleanValue()) {
               _qswhere.append(""
                               + ((_qswhere.length()>0) ? "\n and " : "")
                               + " (" + sqlkeyname + " = false )"
                               );
            }
         }
         //-
      }
   }

   public void setWhereDate(String key
                            , String sqlkeyname
                            ) {
      if (_qwhere != null) {
         String v = null;
         String k = null;
         //-
         //-
         k = key + "__from";
         v =  null;
         if (_qwhere.has(k)) {
            v = _qwhere.get(k).toString();
            if (v.equals("")) {
               v = null;
            }
         }
         if (v != null) {
            Date d = new Date(Long.parseLong(v));
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " >= :wc__"+k+ ")"
                            );
            _qsargs.put("wc__"+k, d);
            // System.out.println(" --- "+k+": " + d);
         } 
         //-
         //-
         k = key + "__to";
         v =  null;
         if (_qwhere.has(k)) {
            v = _qwhere.get(k).toString();
            if (v.equals("")) {
               v = null;
            }
         }
         if (v != null) {
            Date d = new Date(Long.parseLong(v));
            _qswhere.append(""
                            + ((_qswhere.length()>0) ? "\n and " : "")
                            + " (" + sqlkeyname + " <= :wc__"+k+ ")"
                            );
            _qsargs.put("wc__"+k, d);
            // System.out.println(" --- "+k+": " + d);
         } 
      }
   }


   public void setWhereDateNull(String key
                                , String sqlkeyname
                                ) {
      if (_qwhere != null) {
         String v = null;
         String k = null;
         boolean isnull = false;
         //-
         k = key + "__null";
         v =  null;
         //-
         if (_qwhere.has(k)) {
            k = key + "__null";
            isnull =  _tools.jsonObject_getBoolean(_qwhere,k);
         }
         if (isnull) {
               _qswhere.append(""
                               + ((_qswhere.length()>0) ? "\n and " : "")
                               + " (" + sqlkeyname + " is null )"
                               );
         } else {
            k = key + "__from";
            v =  null;
            if (_qwhere.has(k)) {
               v = _qwhere.get(k).toString();
               if (v.equals("")) {
                  v = null;
               }
            }
            if (v != null) {
               Date d = new Date(Long.parseLong(v));
               _qswhere.append(""
                               + ((_qswhere.length()>0) ? "\n and " : "")
                               + " (" + sqlkeyname + " >= :wc__"+k+ ")"
                               );
               _qsargs.put("wc__"+k, d);
               // System.out.println(" --- "+k+": " + d);
            } 
            //-
            //-
            k = key + "__to";
            v =  null;
            if (_qwhere.has(k)) {
               v = _qwhere.get(k).toString();
               if (v.equals("")) {
                  v = null;
               }
            }
            if (v != null) {
               Date d = new Date(Long.parseLong(v));
               _qswhere.append(""
                               + ((_qswhere.length()>0) ? "\n and " : "")
                               + " (" + sqlkeyname + " <= :wc__"+k+ ")"
                               );
               _qsargs.put("wc__"+k, d);
               // System.out.println(" --- "+k+": " + d);
            } 
         }
      }
   }


   public void setWhereWorkflow(String key
                                , String sqlkeyname
                                , Workflow workflow
                                ) {
      if (_qwhere != null) {
         // System.out.println("qwhere: " + _qwhere.toString(2));
         String comma = "";
         StringBuffer qor = new StringBuffer();
         for (String k: workflow.getStatusListAsArray()) {
            String kid = key + "__" + k;
            Boolean v  =  _tools.jsonObject_getBoolean(_qwhere,kid);
            if (v!=null && v.booleanValue()) {
               qor.append(comma + " " + sqlkeyname + " = :wc__"+kid+ " ");
               _qsargs.put("wc__"+kid, k);
               comma = " or ";
            }
         }
         if (qor.length() > 0) {
            _qswhere.append(((_qswhere.length()>0) ? "\n and " : "") + "( "+qor.toString()+" ) ");
         }
         //-
         //-
      }
   }

   
   public void doPaging(NamedParameterJdbcTemplate namedParameterJdbcTemplate
                        ) throws Exception {
      int pageRecords = namedParameterJdbcTemplate
         .queryForObject("select count(*) "
                         + "from ("+_qs.toString()+") as q__"
                         , _qsargs, Integer.class);
      //-
      int pageNumber = _qparams.has("pageNumber") ? _qparams.getInt("pageNumber") : 1;
      int pageSize   = _qparams.has("pageSize")   ? _qparams.getInt("pageSize")   : _defaultPageSize;
      int pages      = (pageRecords / pageSize) + ((pageRecords % pageSize == 0) ? 0 : 1);
      if (pageNumber > pages) { pageNumber = pages; };
      if (pageNumber < 1)     { pageNumber = 1; };
      //-
      _qparams.put("pageNumber", pageNumber);
      _qparams.put("pages",      pages );
      _qparams.put("pageSize",   pageSize);
      //-
      //- order by clause
      //-
      if (_qsorderBy.length() > 0) { _qs.append("\n order by  " + _qsorderBy); }
      //-
      //-
      _qs.append("\n limit :limit_offset, :limit_pagesize");
      //-
      _qsargs.put("limit_offset",   Integer.valueOf(pageSize * (pageNumber - 1)));
      _qsargs.put("limit_pagesize", Integer.valueOf(pageSize));
   }

   public void checkColumns() throws Exception {
      checkColumns(null);
   }

   public void checkColumns(HashMap<String,String> computedColumns
                            ) throws Exception {
      checkColumns(computedColumns, null);
   }

   public void checkColumns(HashMap<String,String> computedColumns
                            , String sql_prefix
                            ) throws Exception {
      {
         //-
         //- all columns
         //-
         JSONArray allcolumns = new JSONArray();
         for (String c: _checkVector) {
            allcolumns.put(c);
         }
         //-
         _qparams.put("allcolumns",  allcolumns);
         _qparams.put("labelPrefix", _labelPrefix);
      }
      //-
      //-
      //-
      JSONArray  qparamsColumns    = _qparams.has("columns")?_qparams.getJSONArray("columns"):null;
      JSONArray  qparamsColumnsNew = new JSONArray();
      //-
      if (qparamsColumns == null) {
         JSONObject defaults = 
            _tools.getTableMattoniDefaults(_qparams.getString("tableid"), _session);
         if (defaults!=null) {
            if (defaults.has("pageSize")) {
               _defaultPageSize = defaults.getInt("pageSize");
            }
            JSONArray  dcolumns = defaults.getJSONArray("columns");
            for (int i = 0; i < dcolumns.length(); i++) {
               String c = dcolumns.getString(i);
               if (_checkVector.contains(c)) {
                  if (!_columns.contains(c) ) {
                     _columns.add(c);
                  }
                  qparamsColumnsNew.put(c);
               }
            }
         }
      } else {
         for (int i = 0; i < qparamsColumns.length(); i++) {
            String c = qparamsColumns.getString(i);
            if (_checkVector.contains(c)) {
               if (!_columns.contains(c) ) {
                  _columns.add(c);
               }
               qparamsColumnsNew.put(c);
            }
         }
      }
      _qparams.put("columns",  qparamsColumnsNew);
      {
         String comma = "";
         for (String c: _columns) {
            String cn = c;
            boolean add_sql_prefix = true;
            if (computedColumns!=null){
               cn = computedColumns.get(c);
               if (cn == null) { 
                  cn = c; 
               } else {
                  cn = cn + " as " + c; 
                  add_sql_prefix = false;
               }
            }
            if (add_sql_prefix && sql_prefix != null) {
               cn = sql_prefix + "." + c + " as " + c; 
            }
            _qs.append("\n " +comma+ cn);
            comma = ", ";
         }
      }
   }

   /**
    *  */
   public static SimpleDateFormat getMvcDateFormat() {
      return(_MVC_DATE_FORMATTER);
   }

   public static SimpleDateFormat getMvcTimestampFormat() {
      return(_MVC_TIMESTAMP_FORMATTER);
   }
   

   private static SimpleDateFormat _MVC_DATE_FORMATTER =
      new SimpleDateFormat("yyyyMMdd");


   private static SimpleDateFormat _MVC_TIMESTAMP_FORMATTER =
      new SimpleDateFormat("yyyyMMddHHmmss");

   private Tools                   _tools;
   private HttpSession             _session;
   private JSONObject              _qparams;
   private StringBuffer            _qsorderBy;
   private JSONArray               _qorderBy;
   private Vector<String>          _checkVector;
   private JSONObject              _qwhere;
   private int                     _defaultPageSize = 50;
   private HashMap<String, Object> _qsargs;
   private StringBuffer            _qswhere;
   private String _labelPrefix;
   private Vector<String> _columns = new Vector<String>();
   private StringBuffer   _qs;
   private HashMap<String,AppListTableMapper_header>  _customHeaders =
      new HashMap<String,AppListTableMapper_header>();

}

