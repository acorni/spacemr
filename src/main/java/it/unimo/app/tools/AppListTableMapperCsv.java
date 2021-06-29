package it.unimo.app.tools;

import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;

public class AppListTableMapperCsv extends AppListTableMapper {
   public AppListTableMapperCsv(String dbPrefixName
                                , HashMap<String,AppListTableMapper_header> customHeaders
                                , PrintWriter printWriter
                                , AppPermissionTools_userData userData
                                ) {
      super(dbPrefixName, customHeaders);
      _printWriter = printWriter;
      _userData = userData;
   }


   protected boolean init(ResultSet rs)  throws SQLException{
      boolean rv = super.init(rs);
      if (rv) {
         String comma = "";
         int columnCount = getColumnCount();
         StringBuffer line = new StringBuffer();
         for (int index = 0; index < columnCount; index++) {
            line.append(comma + getUserData().gRb(getRsLabel(index)));
            comma = csv_separator;
         }
         _printWriter.println(line.toString());
      }
      return(rv);
   }

   @Override
   public JSONArray mapRow(ResultSet rs, int rowNum) throws SQLException {
      init(rs);
      String comma = "";
      int columnCount = getColumnCount();
      StringBuffer line = new StringBuffer();
      for (int index = 0; index < columnCount; index++) {
         Object value      = rs.getObject(index+1);
         line.append(comma);
         if ( value == null ) {
            line.append("");
         } else {
            Object cellobj = getRsHeader(index).getValueCsv(value, _userData);
            line.append(cellobj.toString());
         }
         comma = csv_separator;
      }
      _printWriter.println(line.toString());
      return null;
   }


   public AppPermissionTools_userData getUserData() { 
      return(_userData);
   }

   private PrintWriter _printWriter = null;
   private AppPermissionTools_userData _userData;

   private static final String csv_separator = ";";
}
