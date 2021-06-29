package it.unimo.app.controller;
//-
import it.unimo.app.tools.DbTools;
import it.unimo.app.om.*;
import it.unimo.app.tools.Tools;
import it.unimo.app.tools.AppMapperJson;
import it.unimo.app.tools.AppListTableMapper;
import it.unimo.app.tools.AppListTableMapper_header;
import it.unimo.app.tools.AppSessionTools;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


//-
/*
   cd /dati/bin/workspace/tmp/mvc; gradle run
*/
@Controller
public class Spacemr_space_mapController {
   @Autowired
   private AppControllerTools appControllerTools;
   @Autowired
   private JdbcTemplate jdbcTemplate;
   @Autowired
   private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
   @Autowired
   private App_log app_log;
   @Autowired
   private AppSessionTools appSessionTools;
   @Autowired
   private App_file app_file;
   @Autowired
   private Tools tools;
   @Autowired
   private Spacemr_space_map spacemr_space_map;
   // @Autowired
   // private Spacemr_space_map_super spacemr_space_map_super;
   //-
   /**
    *  http://localhost:8080/spacemr_space_map/spacemr_space_map_insert
    */
   @Transactional
   @RequestMapping(value="spacemr_space_map/spacemr_space_map_insert")
   public void spacemr_space_map_insert(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_insert")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      if (content.optBoolean("fg_default_map", false)) {
         MapSqlParameterSource np = new MapSqlParameterSource();
         np.addValue("spacemr_space_id", content.getInt("spacemr_space_id"));
         namedParameterJdbcTemplate
            .update(
                    "update spacemr_space_map set fg_default_map = 0"
                    + " where spacemr_space_id = :spacemr_space_id"
                    , np
                    );
      }
      //-
      int id = spacemr_space_map.insert(content);
      app_log.writeLog(spacemr_space_map, id, appSessionTools.getUser_name(session),content);
      status = "ok";
      rv.put("spacemr_space_map_id",  id);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   //-
   /**
    */
   @RequestMapping(value="spacemr_space_map/spacemr_space_map_list/**")
   public void spacemr_space_map_list(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject qparams = new JSONObject(sqparams);
      // System.out.println(" \n--- qparams"
      //                    + " \n" + sqparams
      //                    + " \n" + qparams.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      HashMap<String, Object> qsargs = new HashMap<String, Object>();
      StringBuffer                qs = new StringBuffer();
      StringBuffer           qswhere = new StringBuffer();
      StringBuffer         qsorderBy = new StringBuffer();
      String              outputtype = qparams.optString("outputtype","json");
      //-
      //-
      @SuppressWarnings("unchecked")
      Vector<String> checkVector    = (Vector<String>)spacemr_space_map.getColumnsForList().clone();
      //-
      AppControllerTableMattoni qtm = 
         new AppControllerTableMattoni("db.spacemr_space_map"
                                       , checkVector
                                       , qsargs
                                       , qparams
                                       , qs
                                       , qswhere
                                       , qsorderBy
                                       , tools
                                       , session
                                       );
      //-
      qtm.setCustomHeaderAndAddColumn("spacemr_space_map_id", "id");
      qtm.setCustomHeaderAndAddColumn("spacemr_space_id", "id");
      //-
      //-
      AppListTableMapper mapper = null;
      if (outputtype.equals("csv")) {
         mapper = qtm.getMapperCsv(response.getWriter(), appSessionTools.getUserData(session));
         response.setContentType("text/csv");
      } else {
         mapper = qtm.getMapperJson();
      }
      //-
      //- Query composition
      //-
      qs.append("select ");
      //-
      //- columns
      qtm.checkColumns(spacemr_space_map.getComputedColumnsMap(), "spacemr_space_map");
      //-
      //- where
      //-
      {
         qtm.setWhereIntegerEqual("spacemr_space_id",  "spacemr_space_map.spacemr_space_id");
         qtm.setWhereStringLike("description",  "spacemr_space_map.description");
         qtm.setWhereBoolean("fg_default_map",  "spacemr_space_map.fg_default_map");
         qtm.setWhereStringLike("info",  "spacemr_space_map.info");
         qtm.setWhereStringLike("nota",  "spacemr_space_map.nota");
      }
      //-
      //-
      //- order:[ { column: user_name, desc: true}, { column: first_name, desc: true} ]
      //-
      //-
      qtm.setOrderByString();
      //-
      //- query assemblation
      //-
      qs.append("\n from spacemr_space_map"
                + "   left join spacemr_space  on spacemr_space_map.spacemr_space_id  = spacemr_space.spacemr_space_id"
                );
      // qs.append("\n ,    spacemr_space_map_super");
      // qtm.addAnd(" spacemr_space_map_super.spacemr_space_map_super_id = spacemr_space_map.spacemr_space_map_super_id");
      //-
      //-
      if (qswhere.length() > 0) { qs.append("\n where " + qswhere); }
      //-
      //-
      //- ok the query is ready
      //-
      //-
      // System.out.println(" --- qs: " + qs + "\n qsargs: " + qsargs);
      //-
      //-
      //- paging and order by clause
      //-
      //-
      qtm.doPaging(namedParameterJdbcTemplate);
      //-
      //-
      //- query execution
      //-
      //-
      namedParameterJdbcTemplate.query(qs.toString(), qsargs, mapper);
      //-
      if (!outputtype.equals("csv")) {
         rv.put("list",       mapper.getJSON());
         rv.put("qparams",    qparams);
         //-
         status = "ok";
         //-
         appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
      }
   }
   
   /**
    *  http://localhost:8080/spacemr_space_map/spacemr_space_map_get?content={"spacemr_space_map_id", "1"}
    */
   @RequestMapping(value="spacemr_space_map/spacemr_space_map_get")
   public void spacemr_space_map_get(HttpSession session
                            , HttpServletResponse response
                            , @RequestParam("content") String scontent
                            ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int spacemr_space_map_id = content.getInt("spacemr_space_map_id");
      JSONObject obj = spacemr_space_map.get(spacemr_space_map_id);
      //-
      //- files
      JSONArray files = app_file.getFiles(spacemr_space_map, spacemr_space_map_id);
      //-
      rv.put("files",files);
      rv.put("obj",obj);
      rv.put("spacemr_space_map_parameters",spacemr_space_map_get_session_parameters(session));
      status = "ok";
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

//    /**
//     *  http://localhost:8080/spacemr_space_map/spacemr_space_map_set_session_parameter?content={"parameter_name": "map_people__view_mode", "parameter_value", "area"}
//     */
//    @RequestMapping(value="spacemr_space_map/spacemr_space_map_set_session_parameter")
//    public void spacemr_space_map_setSessionParameter(HttpSession session
//                                                      , HttpServletResponse response
//                                                      , @RequestParam("content") String scontent
//                                                      ) throws Exception {
//       String status = null;
//       JSONObject rv = new JSONObject();
//       if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_read")) != null) {
//          status = "permissionDenied " + status;
//          appControllerTools.appDoRequestMappingResponse(response, status, rv);
//          return;
//       }
//       //-
//       JSONObject content = new JSONObject(scontent);
//       // System.out.println(" \n---"
//       //                    + " \n" + scontent
//       //                    + " \n" + content.toString(2)
//       //                    + " \n" + "---"
//       //                    );
//       //-
//       String parameter_name  = content.getString("parameter_name");
//       String parameter_value = content.getString("parameter_value");
//       //-
//       JSONObject session_parameters =
//          spacemr_space_map_get_session_parameters(session);
//       session_parameters.put(parameter_name, parameter_value);
//       //-
//       status = "ok";
//       //-
//       appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
//    }

   private JSONObject spacemr_space_map_get_session_parameters(HttpSession session
                                                               ) throws Exception {
      JSONObject session_parameters = 
         (JSONObject)session.getAttribute("spacemr_space_map_parameters");
      if (session_parameters == null) {
         session_parameters = new JSONObject();
         session.setAttribute("spacemr_space_map_parameters", session_parameters);
      }
      return(session_parameters);
   }
   
   @Transactional
   @RequestMapping(value="spacemr_space_map/spacemr_space_map_update")
   public void spacemr_space_map_update(HttpSession session
                                        , HttpServletResponse response
                                        , @RequestParam("content") String scontent
                                        ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int spacemr_space_map_id = content.getInt("spacemr_space_map_id");
      //-
      app_log.writeLog(spacemr_space_map, spacemr_space_map_id, appSessionTools.getUser_name(session),content);
      //-
      if (content.optBoolean("fg_default_map", false)) {
         MapSqlParameterSource np = new MapSqlParameterSource();
         np.addValue("spacemr_space_id", content.getInt("spacemr_space_id"));
         namedParameterJdbcTemplate
            .update(
                    "update spacemr_space_map set fg_default_map = 0"
                    + " where spacemr_space_id = :spacemr_space_id"
                    , np
                    );
      }
      spacemr_space_map.update(content);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }



   @Transactional
   @RequestMapping(value="spacemr_space_map/spacemr_space_map_update_info")
   public void spacemr_space_map_update_info(HttpSession session
                                             , HttpServletResponse response
                                             , @RequestParam("content") String scontent
                                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      //-
      int spacemr_space_map_id = content.getInt("spacemr_space_map_id");
      String info              = content.getString("info");
      //-
      app_log.writeLog(spacemr_space_map, spacemr_space_map_id, appSessionTools.getUser_name(session),content);
      //-
      JSONObject obj = spacemr_space_map.get(spacemr_space_map_id);
      obj.put("info", info);
      spacemr_space_map.update(obj);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   /**
    *  http://localhost:8080/spacemr_space_map/spacemr_space_map_delete
    */
   @Transactional
   @RequestMapping(value="spacemr_space_map/spacemr_space_map_delete")
   public void spacemr_space_map_delete(HttpSession session
                               , HttpServletResponse response
                               , @RequestParam("content") String scontent
                               ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_delete")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject content = new JSONObject(scontent);
      // System.out.println(" \n---"
      //                    + " \n" + scontent
      //                    + " \n" + content.toString(2)
      //                    + " \n" + "---"
      //                    );
      //-
      int spacemr_space_map_id = content.getInt("spacemr_space_map_id");
      spacemr_space_map.delete(spacemr_space_map_id);
      status = "ok";
      //-
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }
   
   //-
   /**
    *  http://localhost:8080/spacemr_space_map/spacemr_space_map_logs
    */
   @RequestMapping(value="spacemr_space_map/spacemr_space_map_logs")
   public void spacemr_space_map_logs(HttpSession session
                             , HttpServletResponse response
                             , @RequestParam("content") String sqparams
                             ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_logs")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      JSONObject qparams = new JSONObject(sqparams);
      int id = qparams.getInt("spacemr_space_map_id");
      //-
      app_log.log_list(session
                       , response
                       , qparams
                       , spacemr_space_map
                       , id
                       );
   }

   @RequestMapping(value="spacemr_space_map/app_file_get_content*")
   public void app_file_get_content(HttpSession session
                                    , HttpServletRequest  request
                                    , HttpServletResponse response
                                    , @RequestParam("app_file_id") int app_file_id
                                    ) throws Exception {
      String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_read")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      app_file.download(app_file_id, request, response);
   }

/* 
    https://www.baeldung.com/spring-file-upload
    https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/multipart/4. Uploading a File
To upload our file, we can build a simple form in which we use an HTML input tag with type='file'.
Regardless of the upload handling configuration, we have chosen, we need to set the encoding attribute of the form to multipart/form-data. This lets the browser know how to encode the form:
<form:form method="POST" action="/spring-mvc-xml/uploadFile" enctype="multipart/form-data">
    <table>
        <tr>
            <td><form:label path="file">Select a file to upload</form:label></td>
            <td><input type="file" name="file" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form>

@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
public void submit(@RequestParam("file") MultipartFile file) {
    file.transferTo(File dest);
    file.getSize();
}
 */

   
   @RequestMapping(value="spacemr_space_map/app_file_insert_upload", method = RequestMethod.POST)
   public void app_file_insert_upload(HttpSession session
                                      , HttpServletResponse response
                                      , @RequestParam("file") MultipartFile file_to_upload
                                      , @RequestParam("spacemr_space_map_id") int spacemr_space_map_id
                                      ) throws Exception {

     String status = null;
      JSONObject rv = new JSONObject();
      if ((status = appSessionTools.hasPermission(session, "db_spacemr_space_map_update")) != null) {
         status = "permissionDenied " + status;
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONObject obj = spacemr_space_map.get(spacemr_space_map_id);
      if (obj == null) {
         status = "object spacemr_space_map ["+spacemr_space_map_id+"] not found";
         appControllerTools.appDoRequestMappingResponse(response, status, rv);
         return;
      }
      //-
      JSONArray files = app_file.getFiles(spacemr_space_map, spacemr_space_map_id);
      if (files.length()>0) {
         int app_file_id = files.getJSONObject(0).getInt("app_file_id");
         app_file.uploadAppFile(app_file_id, file_to_upload, session);
      } else {
         //- create a new one
         JSONObject newFile =
            app_file.uploadNewAppFile(spacemr_space_map, spacemr_space_map_id
                                      , file_to_upload
                                      , session);
      }
      files = app_file.getFiles(spacemr_space_map, spacemr_space_map_id);
      rv.put("files",files);
      //-
      appControllerTools.appDoRequestMappingResponse(response, status, rv) ;
   }

   
}
