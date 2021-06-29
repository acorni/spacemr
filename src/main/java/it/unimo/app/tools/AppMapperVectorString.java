package it.unimo.app.tools;

// @author Alberto Corni [Al 20140618-21:53]

 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
 
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;


/** 
 */ 
public class AppMapperVectorString implements RowMapper<String> {

   public Vector<String> getVector() { 
      return _rv; 
   }

   @Override
   public String mapRow(ResultSet rs, int rowNum) throws SQLException {
      String s = rs.getString(1);
      _rv.add(s);
      return s;
   }

   private Vector<String> _rv = new Vector<String>();

   
}
