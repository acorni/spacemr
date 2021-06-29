
package it.unimo.app.om;

//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppMapperJsonForLog;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import java.util.Date;
import java.util.Vector;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
//-
public class App_role_permission  {

   //-
   public JSONObject getPermissionForRole_sortablecheckboxes(int app_role_id) throws Exception {
      JSONObject rv = new JSONObject();
      
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query(""
                         + "\n select p.app_permission_id id, p.name name, rp1.app_role_id app_role_id"
                         + "\n   from app_permission p left join  "
                         + "\n   ( select rp.app_permission_id, rp.app_role_id "
                         + "\n       from app_role_permission rp "
                         + "\n      where rp.app_role_id = ? "
                         + "\n   ) as rp1 "
                         + "\n   on p.app_permission_id = rp1.app_permission_id "
                         + "\n  order by p.name; "
                         , mapper, app_role_id );
         /* 
            select p.app_permission_id, p.name, rp1.app_role_id
              from app_permission p left join 
              ( select rp.app_permission_id, rp.app_role_id
                  from app_role_permission rp
                 where rp.app_role_id = 2
              ) as rp1
              on p.app_permission_id = rp1.app_permission_id
             order by p.name;

            select p.app_permission_id
              from app_permission p
             order by p.name;
         */
         JSONArray a = mapper.getJSONArray();
         JSONArray values = new JSONArray();
         JSONArray valueAll = new JSONArray();
         JSONObject labels   = new JSONObject();
         // System.out.println(a.toString(2));
         for (int i=0; i<a.length();i++) {
            JSONObject r = a.getJSONObject(i);
            labels.put(""+r.getInt("id"), r.get("name"));
            valueAll.put(r.get("id"));
            // System.out.println(" [" + r.get("app_role_id") + "]");
            if ( !r.isNull("app_role_id")) {
               values.put(r.get("id"));
            }
         }
         rv.put("values", values);
         rv.put("valuesAll", valueAll);
         rv.put("labels", labels);
         return(rv);
   }
   
   public JSONArray getPermissionForRole(int app_role_id) throws Exception {
      PermissionJsonMapper mapper = new PermissionJsonMapper();
      jdbcTemplate
         .query(""
                + "\n   select p.name, p.app_permission_id, pr.app_permission_id"
                + "\n     from app_permission p "
                + "\n      left join ("
                + "\n       select pr1.app_permission_id"
                + "\n            , pr1.app_role_id "
                + "\n         from app_role_permission pr1"
                + "\n        where pr1.app_role_id = ?"
                + "\n      ) pr"
                + "\n       on pr.app_permission_id = p.app_permission_id"
                + "\n    where pr.app_role_id is null  "
                + "\n       or pr.app_role_id = ?"
                , new Object[] { Integer.valueOf(app_role_id), Integer.valueOf(app_role_id) }
                , mapper);
      JSONArray rv = mapper.getJSON();
      return(rv);
   }
   ;
   
   //-
   public JSONArray getPermissionForRoleForLog(int app_role_id) throws Exception {
      PermissionJsonMapperForLog mapper = new PermissionJsonMapperForLog();
      jdbcTemplate
         .query(""
                + "\n   select p.name, pr.app_permission_id"
                + "\n     from app_permission p"
                + "\n        , app_role_permission pr"
                + "\n    where pr.app_permission_id = p.app_permission_id"
                + "\n      and pr.app_role_id = ?"
                , new Object[] { Integer.valueOf(app_role_id) }
                , mapper);
      JSONArray rv = mapper.getJSON();
      return(rv);
   }

   //-
   @SuppressWarnings("unchecked")
   public void setPermissionForRole(int app_role_id
                                    , JSONArray listnew
                                    ) throws Exception {
      Vector<Integer> vAll      = new Vector<Integer>();
      Vector<Integer> vOld      = new Vector<Integer>();
      Vector<Integer> vNew      = new Vector<Integer>();
      Vector<Integer> vToDelete = new Vector<Integer>();
      Vector<Integer> vToAdd = new Vector<Integer>();
      //-
      JSONArray listcurrent = getPermissionForRole(app_role_id);
      //-
      //- vAll and vOld
      {
         for (int i=0; i<listcurrent.length(); i++) {
            JSONObject item = listcurrent.getJSONObject(i);
            Integer      id = Integer.valueOf(item.getInt("id"));
            boolean checked = item.getBoolean("checked");
            //-
            vAll.add(id);
            if (checked) { vOld.add(id); };
         }      
      }
      //-
      //- vNew
      {
         for (int i=0; i<listnew.length(); i++) {
            int id = listnew.getInt(i);
            vNew.add(id); 
         }      
      }
      //-
      // System.out.println(" vOld: " + vOld);
      // System.out.println(" vNew: " + vNew);
      // System.out.println(" vAll: " + vAll);
      //-
      vToDelete  = ((Vector<Integer>)vOld.clone()); vToDelete.removeAll(vNew);
      vToAdd     = ((Vector<Integer>)vNew.clone()); vToAdd.removeAll(vOld);
      //-
      // System.out.println(" vToDelete: " + vToDelete);
      // System.out.println(" vToAdd: " + vToAdd);
      //-
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_role_id", app_role_id);
      for(Integer i: vToAdd) {
         np.addValue("app_permission_id", i);
         // System.out.println(" insert: app_role_id: " + app_role_id 
         //                    + " app_permission_id: " + i);
         namedParameterJdbcTemplate
            .update(
                    "insert into app_role_permission ("
                    + " app_role_id, app_permission_id"
                    + ") values ("
                    + " :app_role_id, :app_permission_id"
                    + ")"
                    , np
                    );
      }
      for(Integer i: vToDelete) {
         np.addValue("app_permission_id", i);
         // System.out.println(" delete: app_role_id: " + app_role_id 
         //                    + " app_permission_id: " + i);
         namedParameterJdbcTemplate
            .update(
                    "delete from app_role_permission "
                    + " where app_role_id = :app_role_id"
                    + "   and app_permission_id = :app_permission_id"
                    , np
                    );
      }
   }
   ;

   public void deletePermissionsForRole(int app_role_id) throws Exception {
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_role_id", app_role_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_role_permission "
                 + " where app_role_id = :app_role_id"
                 , np
                 );
   }

   private class PermissionJsonMapper implements RowMapper<JSONObject> {
      public JSONArray getJSON() { 
         return _jSONRows;
      }
      private JSONArray _jSONRows = new JSONArray();
      public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
         JSONObject jsonRow = new JSONObject();
         String  permission    = rs.getString(1);
         int     pid           = rs.getInt(2);
         Object  rid           = rs.getObject(3);
         boolean checked       = (rid != null);
         jsonRow.put("label", permission);
         jsonRow.put("id",   pid);
         jsonRow.put("checked", checked);
         _jSONRows.put(jsonRow);
         return jsonRow;
      }
   }

   private class PermissionJsonMapperForLog implements RowMapper<JSONObject> {
      public JSONArray getJSON() { 
         return _jSONRows;
      }
      private JSONArray _jSONRows = new JSONArray();
      public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
         String  permission    = rs.getString(1);
         _jSONRows.put(permission);
         return null;
      }
   }


   //-
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   //-
   public static final String[] obj_fields = {
      "name"
      , "nota"
   };
   //-
   private static final Vector<String> obj_fieldsc = new Vector<String>();
   static  {
      for (String c: obj_fields) {
         obj_fieldsc.add(c);
      }
   }
}
