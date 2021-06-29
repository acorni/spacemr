package it.unimo.app.tools;

// @author Alberto Corni [Al 20180808-19:02]

 
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
 
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;


/** 
 */ 
public class AppMapperJSONArray implements RowMapper<Object> {

   public JSONArray getJSONArray() { 
      return _rv; 
   }

   @Override
   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
      Object o = rs.getObject(1);
      _rv.put(o);
      return o;
   }
   private JSONArray _rv = new JSONArray();

   
}
