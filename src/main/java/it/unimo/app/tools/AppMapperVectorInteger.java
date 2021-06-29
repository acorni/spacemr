package it.unimo.app.tools;

// @author Alberto Corni [Al 20140618-21:53]

 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
 
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;


/** 
 */ 
public class AppMapperVectorInteger implements RowMapper<Integer> {

   public Vector<Integer> getVector() { 
      return _rv; 
   }

   @Override
   public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
      Integer s = Integer.valueOf(rs.getInt(1));
      _rv.add(s);
      return s;
   }

   private Vector<Integer> _rv = new Vector<Integer>();

   
}
