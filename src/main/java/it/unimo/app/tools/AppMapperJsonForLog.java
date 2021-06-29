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
 
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;


/** 
    Da un resultset
    estrae le informazioni per la visualizzazione
    in tabella.
    Queste sono formattate in due array json
     uno che descrive le meta informazioni di ogni colonna
     uno che per ogni riga riporta i dati
    output example:
 */ 
public class AppMapperJsonForLog implements RowMapper<JSONObject> {

   public  JSONArray getJSONArray() { 
      return _jSONRows; 
   }

   private int _columnCount = 0;
   private JSONArray _jSONRows = new JSONArray();
   private AppListTableMapper_header[]  _headers = null;
   private String[]                     _names = null;

   @Override
   public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
      if (_headers == null) {
         ResultSetMetaData rsmd = rs.getMetaData();
         _columnCount           = rsmd.getColumnCount();
         _headers = new AppListTableMapper_header[_columnCount];
         _names   = new String[_columnCount];
         for (int index = 0; index < _columnCount; index++) {
            String cname = JdbcUtils.lookupColumnName(rsmd, index+1);
            AppListTableMapper_header header = 
               AppListTableMapper.getHeaderForLog(Integer.valueOf(rsmd.getColumnType(index+1)));
            if (header == null) {
               throw new IllegalArgumentException("Unmappable object type: " 
                                                  + rsmd.getColumnType(index+1)
                                                  + " on jdbc column "+cname+" ("+(index+1)+")"
                                                  );
            }
            //-
            _headers[index] = header;
            _names[index]   = cname;
         }
      }
      JSONObject jsonRow = new JSONObject();
      for (int index = 0; index < _columnCount; index++) {
         Object value      = rs.getObject(index+1);
         if ( value == null ) {
            jsonRow.put(_names[index], JSONObject.NULL);
         } else {
            jsonRow.put(_names[index], _headers[index].getValue(value));
         }
      }
      _jSONRows.put(jsonRow);
      return jsonRow;
   }


   
}
