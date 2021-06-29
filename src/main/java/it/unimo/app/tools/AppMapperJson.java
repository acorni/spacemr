package it.unimo.app.tools;

// @author Alberto Corni[Al 20140504-13:00]
// inspired by 
//   https://gist.github.com/kdonald/2137988

 
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
    From a short ResultSet
    creates a JSONArray.
    Usually I use it to extract a single Object (getFirstRow).
    Works in two steps:
    - read the ResultSetMetaData and formatters setup.
    - read the ResultSet rows and creation of the JSON Objects.
    example:
    - the code
       jdbcTemplate.query("select * "
                          + " from app_group"
                          , (Object[])null
                          , mapper);
       System.out.println(" -- mapper.getFirstRow():\n" 
                          + mapper.getFirstRow().toString(2));
       System.out.println(" -- mapper.getJSONArray():\n" 
                          + mapper.getJSONArray().toString(2));
    - the output
       -- mapper.getFirstRow():
       {
         "name": "ingmo",
         "app_group_id": 1,
         "nota": "aaa"
       }
       -- mapper.getJSONArray():
       [
         {
           "name": "ingmo",
           "app_group_id": 1,
           "nota": "aaa"
       },
         {
           "name": "root",
           "app_group_id": 2,
          "nota": "Added by app system initializer on Mon Jun 16 10:49:29 CEST 2014"
       },
         {
           "name": "ditta1",
           "app_group_id": 3,
           "nota": "Nessuna nota"
       },
         {
           "name": "ditta2",
           "app_group_id": 4,
         "nota": ""
         }
       ]
 */ 
public class AppMapperJson implements RowMapper<JSONObject> {

   public  JSONObject getJSON(String nameToGiveToTheSet) { 
      JSONObject rv = new JSONObject();
      rv.put(nameToGiveToTheSet,    _jSONRows);
      return rv; 
   }

   public  JSONObject getJSON() { 
      return getJSON("rows"); 
   }

   public  JSONArray getJSONArray() { 
      return _jSONRows; 
   }

   public  JSONObject getFirstRow() { 
      JSONObject rv = null;
      if (_jSONRows.length() > 0) {
         rv = _jSONRows.getJSONObject(0);
      }
      return rv; 
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
               AppListTableMapper.getHeader(Integer.valueOf(rsmd.getColumnType(index+1)));
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
         // System.out.println(" cname.["+_names[index]+"] ["+value+"] ["+_headers[index]+"]" );
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
