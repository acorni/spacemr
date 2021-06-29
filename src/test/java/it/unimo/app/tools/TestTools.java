package it.unimo.app.tools;

import java.util.*;
import java.sql.*;
import java.io.*;

/** 
 * Contains methods useful for testing */
public class TestTools {

   public static String getTestPathFromClass(Class c
                                             ) throws Exception {
      String rv =
         "src/test/java/"
         + c.getName().replace('.','/');
      return(rv);
   }
   
} 
