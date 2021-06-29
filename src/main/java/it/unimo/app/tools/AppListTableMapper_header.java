package it.unimo.app.tools;

// @author Alberto Corni[Al 20140504-13:00]
// inspired by 
//   https://gist.github.com/kdonald/2137988

 
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
 
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
 
public abstract class AppListTableMapper_header {
 
   public AppListTableMapper_header(String typeCode) {
      _typeCode = typeCode;
   }

   public AppListTableMapper_header() {
   }

   public String getTypeCode() {
      return(_typeCode);
   }

   public  boolean getHidden(){
      return _hidden;
   }
   public AppListTableMapper_header setHidden(boolean v){
      _hidden = v;
      return(this);
   }
   public AppListTableMapper_header setHidden(){
      _hidden = true;
      return(this);
   }

   public abstract Object getValue(Object object) throws SQLException;

   public String getValueCsv(Object object
                             , AppPermissionTools_userData userData
                             ) throws SQLException {
      String rv = null;
      Object o = getValue(object);
      if (o != null) {
         rv = o.toString();
      }
      return(rv);
   }

   public String toString() {
      return("AppListTableMapper_header - " + _typeCode + " hidden: " + _hidden);
   }
   
   private String _typeCode   = null;
   private boolean _hidden    = false;

}
