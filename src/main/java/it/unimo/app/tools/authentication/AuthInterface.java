package it.unimo.app.tools.authentication;

import org.springframework.beans.factory.annotation.Autowired;

import org.json.JSONObject;
import org.json.JSONArray;

public interface AuthInterface {

   /** 
    * Returns null if the auth failed,
    * the user data as json if the auth success */
   public JSONObject doLogUser(String username, String password) throws Exception;


}
