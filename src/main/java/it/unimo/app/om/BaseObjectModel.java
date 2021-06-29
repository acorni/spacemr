package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import java.util.Date;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//-
public abstract class BaseObjectModel {
   //-
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

   public abstract Vector<String> getColumns();

   // public abstract int insert(JSONObject content) throws Exception;

   // public abstract JSONObject get(int id) throws Exception;

   public abstract JSONObject getLog(int id) throws Exception;

   // public abstract void update(JSONObject content) throws Exception;

   // public abstract void delete(int id) throws Exception;

   public abstract MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                                  , Vector<String> columns
                                                                  ) throws Exception ;

   public Vector<String> getLogColumns() throws Exception {
      return(getColumns());
   }

   public String getSqlStringForUpdate(Vector<String> fields) {
      StringBuffer sb = new StringBuffer();
      String comma = "";
      for(String f: fields) {
         sb.append("\n "+comma+ " "+ f+" = :"+f);
         comma = ", ";
      }
      return(sb.toString());
   }


   public String getSqlStringForInsert_fields(Vector<String> fields) {
      String comma = "";
      StringBuffer sb = new StringBuffer();
      for(String f: fields) {
         sb.append("\n "+comma+f);
         comma = ", ";
      }
      return(sb.toString());
   }

   public String getSqlStringForInsert_values(Vector<String> fields) {
      String comma = "";
      StringBuffer sb = new StringBuffer();
      for(String f: fields) {
         sb.append("\n "+comma+":"+f);
         comma = ", ";
      }
      return(sb.toString());
   }
}
