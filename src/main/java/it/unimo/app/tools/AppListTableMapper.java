package it.unimo.app.tools;

// @author Alberto Corni[Al 20140504-13:00]
// inspired by 
//   https://gist.github.com/kdonald/2137988

 
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
 
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import it.unimo.app.controller.AppControllerTools;

/** 
    From a resultset
    extracts the information for the presentation in a table.
    These are formattad in two json array:
    - a header/metadata object, describes the meta informtion of each column
    - a rows oblect that returns the resultset data
    for
     select * from user;
    output example:
     {"headers":[
        {"name":"db.app_user.user_id","type":"integer"}
       ,{"name":"db.app_user.user_name","type":"string"}
       ,{"name":"db.app_user.first_name","type":"string"}
       ,{"name":"db.app_user.last_name","type":"string"}
       ,{"name":"db.app_user.email","type":"string"}
       ,{"name":"db.app_user.created","type":"string"}
       ,{"name":"db.app_user.nota","type":"longstring"}]
     ,"rows": [
       [1,"acorni","alberto","corni","alberto.corni@gmail.com","2014-05-05 11:26:58.0","notanota nota"]
      ,[2,"acorni1","alberto","corni","alberto.corni@gmail.com","2014-05-05 11:27:07.0","11"]
      ,[3,"acorni2","alberto","corni","alberto.corni@gmail.com","2014-05-05 11:27:16.0","artartrast"]
     ]}
 */ 
public class AppListTableMapper implements RowMapper<JSONArray> {

   public AppListTableMapper(String dbPrefixName) {
      init(dbPrefixName, new HashMap<String,AppListTableMapper_header>());
   }
   public AppListTableMapper(String dbPrefixName
                               , HashMap<String,AppListTableMapper_header> customHeaders
                               ) {
      init(dbPrefixName, customHeaders);
   }
   private void init(String dbPrefixName
                     , HashMap<String,AppListTableMapper_header> customHeaders
                     ) {
      _dbPrefixName  = dbPrefixName;
      _customHeaders = customHeaders;
   }


   public  JSONObject getJSON() { 
      JSONObject rv = new JSONObject();
      rv.put("headers",    _jSONHeaders);
      rv.put("rows",       _jSONRows);
      return rv; 
   }


   protected boolean init(ResultSet rs)  throws SQLException{
      boolean rv = false;
      if (_headers == null) {
         rv = true;
         ResultSetMetaData rsmd = rs.getMetaData();
         _columnCount           = rsmd.getColumnCount();
         _headers = new AppListTableMapper_header[_columnCount];
         _labels  = new String[_columnCount];
         for (int index = 0; index < _columnCount; index++) {
            JSONObject jsonHead = new JSONObject();
            String cname = JdbcUtils.lookupColumnName(rsmd, index+1);
            AppListTableMapper_header header = _customHeaders.get(cname);
            if (header == null) {
               header =
                  getHeader(Integer.valueOf(rsmd.getColumnType(index+1)));
            }
            if (header == null) {
               throw new IllegalArgumentException("Unmappable object type: " 
                                                  + rsmd.getColumnType(index+1)
                                                  + " on jdbc column "+cname+" ("+(index+1)+")"
                                                  );
            }
            jsonHead.put("name", cname );
            String label = _dbPrefixName + "." + cname;
            jsonHead.put("label" , label );
            jsonHead.put("type", header.getTypeCode());
            boolean hidden = header.getHidden();
            if (hidden) { jsonHead.put("hidden", true); }
            //-
            _jSONHeaders.put(jsonHead);
            _headers[index] = header;
            _labels[index]  = label;
         }
      }
      return(rv);
   }

   @Override
   public JSONArray mapRow(ResultSet rs, int rowNum) throws SQLException {
      init(rs);
      JSONArray jsonRow = new JSONArray();
      for (int index = 0; index < _columnCount; index++) {
         Object value      = rs.getObject(index+1);
         if ( value == null ) {
            jsonRow.put(JSONObject.NULL);
         } else {
            jsonRow.put(_headers[index].getValue(value));
         }
      }
      _jSONRows.put(jsonRow);
      return jsonRow;
   }

   protected AppListTableMapper_header getRsHeader(int i) {
      return(_headers[i]);
   }
   protected void setRsHeader(int i, AppListTableMapper_header header) {
      _headers[i] = header;
   }

   protected String getRsLabel(int i) {
      return(_labels[i]);
   }

   protected int getColumnCount() {
      return(_columnCount);
   }


   //-
   //- object properties
   //-

   private String    _dbPrefixName = "";
   private HashMap<String,AppListTableMapper_header>  _customHeaders = null;

   private JSONArray _jSONRows    = new JSONArray();
   private JSONArray _jSONHeaders = new JSONArray();

   private AppListTableMapper_header[]  _headers = null;
   private String[]                     _labels  = null;
   private int _columnCount = 0;
   
   //-
   //- static properties
   //-

   private static HashMap<Integer,AppListTableMapper_header> _hedersAssociationType = 
      new HashMap<Integer,AppListTableMapper_header>();
   private static HashMap<Integer,AppListTableMapper_header> _hedersAssociationTypeForLog = 
      new HashMap<Integer,AppListTableMapper_header>();
   private static HashMap<String,AppListTableMapper_header>  _hedersAssociationName = 
      new HashMap<String,AppListTableMapper_header>();

   public static void addCustomHeader(String headerName
                                      , AppListTableMapper_header header
                                      ) throws Exception {
      _hedersAssociationName.put(headerName, header);
   }

   public static void addCustomHeaderFromExistingHeader(String nameOld
                                                        , String nameNew
                                                        ) throws Exception {
      AppListTableMapper_header hOld =
         (AppListTableMapper_header)_hedersAssociationName.get(nameOld);
      AppListTableMapper_header hNew =
         hOld.getClass().getDeclaredConstructor(String.class).newInstance(nameNew);
      _hedersAssociationName.put(nameNew, hNew);
   }
   
   static {
      AppListTableMapper_header h = null;
      String s = null;
      //-
      s = "integer";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((Integer) value);
            }
         };
      _hedersAssociationType.put(Integer.valueOf(Types.INTEGER) , h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.INTEGER) , h);
      _hedersAssociationType.put(Integer.valueOf(Types.SMALLINT), h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.SMALLINT), h);
      _hedersAssociationType.put(Integer.valueOf(Types.TINYINT),  h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.TINYINT),  h);
      //-
      s = "integer";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((Long) value);
            }
         };
      _hedersAssociationType.put(Integer.valueOf(Types.BIGINT),   h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.BIGINT),   h);
      //-
      s = "decimal";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((BigDecimal) value);
            }
            public String getValueCsv(Object value
                                      , AppPermissionTools_userData userData
                                      ) throws SQLException {
               String rv = userData.getDecimalFormat().format((BigDecimal)value);
               return(rv);
            }
         };
      _hedersAssociationType.put(Integer.valueOf(Types.DECIMAL) , h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.DECIMAL) , h);
      //-
      s = "real";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((Float) value);
            }
            public String getValueCsv(Object value
                                      , AppPermissionTools_userData userData
                                      ) throws SQLException {
               String rv = userData.getDecimalFormat().format((Float)value);
               return(rv);
            }
         };
      _hedersAssociationType.put(Integer.valueOf(Types.REAL) , h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.REAL) , h);
      //-
      s = "double";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((Double) value);
            }
            public String getValueCsv(Object value
                                      , AppPermissionTools_userData userData
                                      ) throws SQLException {
               String rv = userData.getDecimalFormat().format((Double)value);
               return(rv);
            }
         };
      _hedersAssociationType.put(Integer.valueOf(Types.DOUBLE) , h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.DOUBLE) , h);
      //-
      s = "boolean";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               // System.out.println(" ---- boolean - " + value);
               return((Boolean) value);
            }
         };
      _hedersAssociationType.put(Integer.valueOf(Types.BIT) , h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.BIT) , h);
      //-
      //-
      s = "string";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               // System.out.println(" ---- string - " + value);
               return((String)value);
            }
            @Override
            public String getValueCsv(Object value
                                      , AppPermissionTools_userData userData
                                      ) throws SQLException {
               String rv = null;
               Object o = getValue(value);
               if (o!=null) {
                  rv = o.toString();
                  rv = Tools.stringReplaceInString(rv, "\"","\"\"");
                  rv = "\""+rv+"\"";
               }
               return(rv);
            }
         } ;
      _hedersAssociationName.put(s, h);
      _hedersAssociationType.put(Integer.valueOf(Types.VARCHAR), h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.VARCHAR), h);
      //-
      //-
      //-
      s = "longstring";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               String rv = (String)value;
               return(rv);
            }
            @Override
            public String getValueCsv(Object value
                                      , AppPermissionTools_userData userData
                                      ) throws SQLException {
               String rv = null;
               Object o = getValue(value);
               if (o!=null) {
                  rv = o.toString();
                  rv = Tools.stringReplaceInString(rv, "\"","\"\"");
                  rv = "\""+rv+"\"";
               }
               return(rv);
            }
         };
      _hedersAssociationName.put(s, h);
      _hedersAssociationType.put(Integer.valueOf(Types.LONGVARCHAR), h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.LONGVARCHAR), h);
      _hedersAssociationType.put(Integer.valueOf(Types.CLOB),        h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.CLOB),        h);
      //-
      //-
      s = "time";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return(Long.valueOf(((Date)value).getTime()));
            }
         };
      _hedersAssociationName.put(s, h);
      _hedersAssociationType.put(Integer.valueOf(Types.TIME), h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.TIME), h);
      //-
      //-
      s = "timestamp";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return(Long.valueOf(((Date)value).getTime()));
            }
            @Override
            public String getValueCsv(Object value
                                      , AppPermissionTools_userData userData
                                      ) throws SQLException {
               String rv = null;
               if (value!=null) {
                  rv = _SYSTEM_TIMESTAMP_FORMATTER.format((Date)value);
               }
               return(rv);
            }
         };
      _hedersAssociationName.put(s, h);
      _hedersAssociationType.put(Integer.valueOf(Types.TIMESTAMP), h);
      //-
      //-
      s = "date";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return(Long.valueOf(((Date)value).getTime()));
            }
            @Override
            public String getValueCsv(Object value
                                      , AppPermissionTools_userData userData
                                      ) throws SQLException {
               String rv = null;
               if (value!=null) {
                  rv = _SYSTEM_DATE_FORMATTER.format((Date)value);
               }
               return(rv);
            }
         };
      _hedersAssociationName.put(s, h);
      _hedersAssociationType.put(Integer.valueOf(Types.DATE), h);
      //-
      //-
      s = "timestampForLog";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return(AppControllerTools.getMvcTimestampFormat().format((Date)value));
            }
         };
      _hedersAssociationName.put(s, h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.TIMESTAMP), h);
      //-
      //-
      s = "dateForLog";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return(AppControllerTools.getMvcDateFormat().format((Date)value));
            }
         };
      _hedersAssociationName.put(s, h);
      _hedersAssociationTypeForLog.put(Integer.valueOf(Types.DATE), h);
      //-
      //-
      //-
      //- custom
      //-
      //-
      //-
      s = "id";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((Integer)value);
            }
         };
      h.setHidden(true);
      _hedersAssociationName.put(s, h);
      //-
      //-
      s = "string_hidden";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((String)value);
            }
         } ;
      h.setHidden();
      _hedersAssociationName.put(s, h);
      //-
      //-
      s = "boolean_hidden";
      h = new AppListTableMapper_header(s) {
            @Override
            public Object getValue(Object value) throws SQLException {
               return((Boolean)value);
            }
         } ;
      h.setHidden();
      _hedersAssociationName.put(s, h);
      //-
      //-
      //-
   }

   public static AppListTableMapper_header getHeader(Integer jdbcTypeCode) {
      return(_hedersAssociationType.get(jdbcTypeCode));
   }

   public static AppListTableMapper_header getHeaderForLog(Integer jdbcTypeCode) {
      return(_hedersAssociationTypeForLog.get(jdbcTypeCode));
   }

   public static AppListTableMapper_header getHeader(String typecode) {
      return(_hedersAssociationName.get(typecode));
   }

   private static SimpleDateFormat _SYSTEM_TIMESTAMP_FORMATTER =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static SimpleDateFormat _SYSTEM_DATE_FORMATTER =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   
}
