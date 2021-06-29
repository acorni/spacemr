package it.unimo.app.om;
//-
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.Tools;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.multipart.MultipartFile;
//-
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import it.unimo.app.tools.AppSessionTools;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletOutputStream;
//-
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
   
public class App_file extends BaseObjectModel {
   //-
   //-
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private Tools tools;
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private AppSessionTools appSessionTools;
   //-
   @SuppressWarnings("unchecked")
   public int insert(JSONObject content) throws Exception {
      Vector columns = (Vector<String>)getColumnsWrite().clone();
      // columns.add( "foreign_reference_id");
      MapSqlParameterSource np = getMapSqlParameterSource(content, columns);
      KeyHolder keyHolder = new GeneratedKeyHolder();
      String qs = ""
         + "insert into app_file ("
         + getSqlStringForInsert_fields(columns)
         + ") values ("
         + getSqlStringForInsert_values(columns)
         + ")"
         ;
      // System.out.println(" qs: " + qs );
      //for(Object k: np.getValues().keySet() ) { System.out.println("k.["+k+"]:["+np.getValues().get(k)+"]");}
      namedParameterJdbcTemplate.update(qs , np, keyHolder);
      int id = keyHolder.getKey().intValue();
      //-
      return(id);
   }
   
   //-
   //-
   public JSONObject get(int app_file_id) throws Exception {
      return(get(app_file_id, getColumns()));
   }
   
   //-
   public JSONObject get(int app_file_id, Vector<String> columns) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = "app_file_id, " 
         + tools.sqlListOfColumnsToSelectList(columns
                                              , getComputedColumnsMap()
                                              , "app_file");
      jdbcTemplate.query("select "
                         + fieldsToGet
                         + "  from app_file"
                         // + "     , app_file_super"
                         + " where app_file_id = ?"
                         // + "   and app_file_super.app_file_super_id = app_file.app_file_super_id"
                         , new Object[] { Integer.valueOf(app_file_id) }
                         , mapper);
      JSONObject rv = mapper.getFirstRow();
      //-
      {
         String fileSystemFileName = getFileFullPathName(app_file_id);
         File   fileSystemFile     = new File(fileSystemFileName);
         rv.put("fileReadable", fileSystemFile.canRead());
      }
      //-
      return(rv);
   }
   
   //-
   public JSONObject getLog(int app_file_id) throws Exception {
      AppMapperJson mapper = new AppMapperJson();
      jdbcTemplate.query("select * from app_file where app_file_id = ?"
                         , new Object[] { Integer.valueOf(app_file_id) }
                         , mapper);
      JSONObject rv = mapper.getJSONArray().getJSONObject(0);
      return(rv);
   }
   //-
   public void update(JSONObject content) throws Exception {
      MapSqlParameterSource np = getMapSqlParameterSource(content, getColumnsWrite());
      np.addValue("app_file_id", content.getInt("app_file_id"));
      String qs = 
         "update app_file set "
         + getSqlStringForUpdate(getColumnsWrite())
         + " where app_file_id = :app_file_id"
         ;
      namedParameterJdbcTemplate.update(qs, np );
   }
   
   //-
   public void delete(int app_file_id) throws Exception {
      //- unlink file
      File file = new File(getFileFullPathName(app_file_id));
      if (file.canRead()) {
         file.delete();
      }
      //- logs
      app_log.deleteLogs(this, app_file_id);
      MapSqlParameterSource np = new MapSqlParameterSource();
      np.addValue("app_file_id", app_file_id);
      namedParameterJdbcTemplate
         .update(
                 "delete from app_file  "
                 + " where app_file_id = :app_file_id"
                 , np
                 );
   }


   public String getFileFullPathName(int app_file_id) throws Exception {
      String rv = ""
         + app_system_property.getAsString("configuration_docs_directory")
         + File.separator 
         + tools.getPathFromInt(app_file_id, 100)
         ;
      return(rv);
   }



   //-
   public MapSqlParameterSource getMapSqlParameterSource(JSONObject content
                                                         , Vector<String> columns
                                                         ) throws Exception {
      MapSqlParameterSource np = new MapSqlParameterSource();
      for (String k: columns) {
         switch (k) {
         case "app_file_id":
            break;
         case "owner_object_type":
            np.addValue("owner_object_type", tools.jsonObject_getInteger(content, "owner_object_type"));
            break;
         case "owner_object_id":
            np.addValue("owner_object_id", tools.jsonObject_getInteger(content, "owner_object_id"));
            break;
         case "file_name":
            np.addValue("file_name", content.optString("file_name", null));
            break;
         case "file_size":
            np.addValue("file_size", tools.jsonObject_getInteger(content, "file_size"));
            break;
         default:
            throw new Exception("column name ["+k+"] not found");
         }
      }
      return(np);
   }
   //-

   /** Get all files id related to a given object  */
   public JSONArray getFiles(BaseObjectModel om, int owner_object_id) throws Exception {
      //-
      int owner_object_type = app_log.getClassNumber(om.getClass());
      //-
      AppMapperJson mapper = new AppMapperJson();
      String fieldsToGet = " "; 
      jdbcTemplate.query("select app_file_id from app_file"
                         + " where owner_object_type = ?"
                         + "   and owner_object_id = ?"
                         , new Object[] { 
                            Integer.valueOf(owner_object_type) 
                            , Integer.valueOf(owner_object_id) 
                         }
                         , mapper);
      JSONArray arr = mapper.getJSONArray();
      return(arr);
   }

   public void deleteFiles(BaseObjectModel om, int owner_object_id) throws Exception {
      //-
      JSONArray arr = getFiles(om, owner_object_id);
      for (int i = 0; i < arr.length(); i++) {
         JSONObject obj = arr.getJSONObject(i);
         // System.out.println("obj.getInt(app_file_id): " + obj.getInt("app_file_id"));
         delete(obj.getInt("app_file_id"));
      }
   }


   public JSONObject uploadNewAppFile(BaseObjectModel owner_object
                                      , int owner_object_id
                                      , MultipartFile file_to_upload
                                      , HttpSession        session
                                      ) throws Exception {
      //-
      JSONObject obj = new JSONObject();
      //-
      int owner_object_type = app_log.getClassNumber(owner_object.getClass());
      obj.put("owner_object_type", owner_object_type);
      obj.put("owner_object_id", owner_object_id);
      //-
      // insert per avere un id
      //-
      int app_file_id = insert(obj);
      obj.put("app_file_id", app_file_id);
      obj.put("file_size", file_to_upload.getSize());
      obj.put("file_name", file_to_upload.getOriginalFilename());
      //-
      String uploadDestination      = getFileFullPathName(app_file_id);
      file_to_upload.transferTo(new File(uploadDestination));
      //-
      app_log.writeLog(this, app_file_id, appSessionTools.getUser_name(session), obj);
      //-
      update(obj);
      //-
      return(obj);
   }

   public JSONObject uploadAppFile(int app_file_id
                                   , MultipartFile file_to_upload
                                   , HttpSession   session
                                   ) throws Exception {
      //-
      JSONObject app_file_obj = get(app_file_id);
      //-
      // upload the file
      //-
      String uploadDestination      = getFileFullPathName(app_file_id);
      //-
      app_file_obj.put("file_size", file_to_upload.getSize());
      app_file_obj.put("file_name", file_to_upload.getOriginalFilename());
      file_to_upload.transferTo(new File(uploadDestination));
      //-
      app_log.writeLog(this, app_file_id, appSessionTools.getUser_name(session), app_file_obj);
      update(app_file_obj);
      //-
      return(app_file_obj);
   }

   



   public void download(int app_file_id
                        , HttpServletRequest  request
                        , HttpServletResponse response
                        ) throws Exception {
      JSONObject obj = get(app_file_id);
      String file_name = obj.optString("file_name", null);
      //-
      //- default content type
      String contentType = "application/bin";
      if (file_name != null ){
         String ct = request
            .getServletContext()
            .getMimeType(file_name)
            ;
         if (ct != null) {
            contentType = ct;
         }
         response.setContentType(contentType);
      }
      //-
      String fileSystemFileName = getFileFullPathName(app_file_id);
      File   fileSystemFile     = new File(fileSystemFileName);
      response.setContentLength((int)fileSystemFile.length()); 
      //-
      //-
      FileInputStream     fis = new FileInputStream(fileSystemFile);
      ServletOutputStream sos = response.getOutputStream();
      //-
      int amount;
      int bufsize = 1024;
      byte[] b = new byte[bufsize];
      while((amount = fis.read(b,0,bufsize)) != -1){
         sos.write(b,0,amount);
      }
      fis.close();
   }


   public Vector<String> getColumns() {
      return(_columns);
   }
   public Vector<String> getColumnsWrite() {
      return(_columnsWrite);
   }
   public Vector<String> getColumnsForList() {
      return(_columnsForList);
   }
   public HashMap<String,String> getComputedColumnsMap() {
      return(computedColumnsMap);
   }
   private static final Vector<String> _columns = new Vector<String>();
   private static final Vector<String> _columnsHiddenInList = new Vector<String>();
   private static final Vector<String> _columnsForList = new Vector<String>();
   private static final Vector<String> _columnsWrite = new Vector<String>();
   private static final HashMap<String,String> computedColumnsMap = new HashMap<String,String>();
   private static final String[] _columnsWrite_a = {
      "owner_object_type"
      , "owner_object_id"
      , "file_name"
      , "file_size"
   };
   private static final String[] _columns_toGet_a = {
      // "app_file_super_name"
   };
   private static final String[] _columns_hidden_in_list = {
      // "app_file_super_id"
   };
   static  {
      for (String c: _columnsWrite_a) {
         _columnsWrite.add(c);
         _columns.add(c);
      }
      for (String c: _columns_toGet_a) {
         _columns.add(c);
      }
      for (String c: _columns_hidden_in_list) {
         _columnsHiddenInList.add(c);
      }
      for (String c: _columns) {
         if (!_columnsHiddenInList.contains(c)) {
            _columnsForList.add(c);
         }
      }
   }
   static  {
      // computedColumnsMap.put("app_file_super_name", "app_file_super.name");
      // computedColumnsMap
      //    .put("user_first_name"
      //         , ""
      //         + "(select first_name from app_user where app_user.user_name = app_file.user_name)"
      //         );
   }
}
