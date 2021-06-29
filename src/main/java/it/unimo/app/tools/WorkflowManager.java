package it.unimo.app.tools;

import java.io.*;
import java.util.HashMap;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpSession;

import it.unimo.app.om.App_system_property;
import it.unimo.app.tools.AppSessionTools;


// import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowManager {

   public Workflow getWorkflow(String systemPropertiesId
                               , AppSessionTools appSessionTools
                               , HttpSession session) throws Exception {
      @SuppressWarnings("unchecked")
      HashMap <String, Workflow> sessionWorkflowCache = 
         (HashMap <String, Workflow>)session.getAttribute("sessionWorkflowCache");
      if (sessionWorkflowCache == null) {
         sessionWorkflowCache = new HashMap <String, Workflow>();
         session.setAttribute("sessionWorkflowCache", sessionWorkflowCache);
      }
      Workflow rv = sessionWorkflowCache.get(systemPropertiesId);
      if (rv == null) {
         JSONObject wfjson = app_system_property.getAsJSONObject(systemPropertiesId);
         if (wfjson != null) {
            rv = new Workflow(wfjson, appSessionTools, session);
            sessionWorkflowCache.put(systemPropertiesId, rv);
         }
      }
      return(rv);
   }
   


   @Autowired
   private App_system_property app_system_property;
   
}
