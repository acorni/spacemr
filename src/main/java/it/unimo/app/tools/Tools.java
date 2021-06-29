package it.unimo.app.tools;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import org.apache.commons.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import it.unimo.app.om.App_system_property;
import it.unimo.app.om.App_user;

public class Tools {
   

   public String base64RandomBytes(int numberOfBytes) {
      byte[] random = new byte[numberOfBytes];
      new Random().nextBytes(random);
      String rv = base64Encode(random);
      return(rv);
   }

   public String base64Encode(byte[] bytes) {
      byte[] encodedBytes = Base64.encodeBase64(bytes);
      return(new String(encodedBytes));
   }

   public byte[] base64Decode(String s) {
      byte[] encodedBytes = s.getBytes();
      byte[] rv = Base64.decodeBase64(encodedBytes);
      return(rv);
   }

   public String stringMd5ToBase64(String s) throws Exception {
      byte[] bytesOfMessage = s.getBytes("UTF-8");
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] thedigest = md.digest(bytesOfMessage);
      return(base64Encode(thedigest));
   }

   /**
    * Normalize the date removing time.  */
   public static java.util.Date date_floor_on_day(java.util.Date d) throws Exception {
      java.util.Date rv = _date_floor_on_day.parse(_date_floor_on_day.format(d));
      return(rv);
   }
   private static SimpleDateFormat _date_floor_on_day =
      new SimpleDateFormat("yyyy-MM-dd");

   /** 
    * encodes a string according to the HTTP specification
    * <br />
    * Characters other than those in the "reserved" and "unsafe" 
    * sets (see section 3.2) are equivalent to their ""%" HEX HEX" encodings.
    * <br />
    * Null values are encoded with the string "%nn"
    *
    * @Author Alberto Corni
    */
   public String encodeHttpString(String value) {
      String rv = "";
      // System.out.println("Encoding [" + value + "]");
      if (value!=null) {
         int l = value.length();
         char[] a = value.toCharArray();
         StringBuffer b = new StringBuffer();
         char c;
         int intc;
         String s;
         for (int i = 0; i < l; i++) {
            c = a[i];
            if ((c >= 'a' &&  c <= 'z') ||
                (c >= 'A' &&  c <= 'Z') ||
                (c >= '0' &&  c <= '9') ||
                (c == '.' ) ||
                (c == '_' )
                ) {
               b.append(c);
            } else {
               intc = c;
               s = Integer.toHexString(intc);
               b.append("%" + (s.length() > 1 ? "" : "0") +  s);
            }
         }
         rv = b.toString();
      } else {
         rv="%nn";
      }
      return(rv);
   }

   public java.util.Date jsonObject_getDate(JSONObject content, String key) {
      // System.out.println(" content.optString("+key+"): " + content.optString(key,""));
      java.util.Date rv = null;
      if (content.has(key)) {
         if (content.optString(key,"").length() > 0){
            rv = new java.util.Date(content.getLong(key));
         }
      }
      return(rv);
   }

   public Integer jsonObject_getInteger(JSONObject content, String key) {
      Integer rv = null;
      if (content.has(key)) {
         if (!content.isNull(key)) {
            Object o = content.get(key);
            if (o instanceof String) {
               if (content.optString(key,"").length() > 0){
                  rv = Integer.valueOf(content.getString(key));
               } 
            } else {
               rv = Integer.valueOf(content.getInt(key));
            }
         }
      }
      return(rv);
   }

   public BigDecimal jsonObject_getBigDecimal(JSONObject content, String key) {
      BigDecimal rv = null;
      if (content.has(key)) {
         if (!content.isNull(key)) {
            Object o = content.get(key);
            if (o instanceof String) {
               if (!content.getString(key).equals("")) {
                  String sk = content.getString(key);
                  // System.out.println(" -------- sk: "+ sk);
                  rv = new BigDecimal(sk);
               } 
            } else {
               if (!"".equals(o.toString())) {
                  rv = new BigDecimal(content.getDouble(key));
               }
            }
         }
      }
      return(rv);
   }

   public Boolean jsonObject_getBoolean(JSONObject content, String key) {
      Boolean rv = null;
      if (content.has(key)) {
         rv = Boolean.valueOf(content.optBoolean(key));
      }
      return(rv);
   }
   private static Object jsonChanges_normalize(JSONObject o, String key) {
      Object rv = (o.isNull(key) ? "" : o.get(key));
      if (rv instanceof Boolean) {
         if (((Boolean)rv).booleanValue()) {
            // do nothing
         } else {
            rv = "";
         }
      } else if (rv instanceof BigDecimal) {
         rv = _decimalFormat.format((BigDecimal)rv);
      }
      return(rv);
   }
   /**
    * Simple flat json comparison.
    * Returns null if no changes occurred.
    *   n  new object
    *   o  old object
    * this returns all changes to rebuild the old
    * given the new (backtrack from current value).
    */
   public static JSONObject jsonChanges(JSONObject n
                                        , JSONObject o
                                        ) throws Exception {
      JSONObject rv = new JSONObject();
      JSONObject rv_changed = new JSONObject();
      JSONArray  rv_added   = new JSONArray();
      JSONObject rv_removed = new JSONObject();
      String namesn[] = JSONObject.getNames(n);
      if (namesn == null) { namesn = new String[0]; }
      String nameso[] = JSONObject.getNames(o);
      if (nameso == null) { nameso = new String[0]; }
      Arrays.sort(namesn);
      Arrays.sort(nameso);
      int i_n = 0;
      int i_o = 0;
      // System.out.println("n: " + n.toString(2));
      // System.out.println("o: " + o.toString(2));
      while(i_n<namesn.length && i_o<nameso.length){
         int c = namesn[i_n].compareTo(nameso[i_o]);
         // System.out.println(" - compare "+namesn[i_n]+" " + nameso[i_o]);
         if (c==0) {
            // System.out.println(" match " + namesn[i_n]);
            Object vo = jsonChanges_normalize(o,nameso[i_o]);
            Object vn = jsonChanges_normalize(n,namesn[i_n]);
            // System.out.println("  -- "+namesn[i_n]+": values ["+vn+"]["+vo+"]");
            if (vo.toString().equals(vn.toString())) {
               // are equals - no action
               // System.out.println("      equals");
            } else {
               // System.out.println("  -- "+namesn[i_n]+": values ["+vn+"]["+vo+"]");
               // System.out.println("     ["+vn.getClass().getName()+"]["+vo.getClass().getName()+"]");
               rv_changed.put(nameso[i_o], vo);
            }
            i_n++;
            i_o++;
         } else if(c>0) {
            // System.out.println(" deleted " + nameso[i_o]);
            rv_removed.put(nameso[i_o], o.get(nameso[i_o]));
            i_o++;
         } else { // c<0
            // System.out.println(" added " + namesn[i_n]);
            rv_added.put(namesn[i_n]);
            i_n++;
         }
      }
      while(i_n<namesn.length){
         // System.out.println(" added " + namesn[i_n]);
         rv_added.put(namesn[i_n]);
         i_n++;
      }
      while(i_o<nameso.length){
         // System.out.println(" deleted " + nameso[i_o]);
         rv_removed.put(nameso[i_o], o.get(nameso[i_o]));
         i_o++;
      }
      if (rv_changed.length() > 0) { rv.put("changed", rv_changed); }
      if (rv_added.length() > 0)   { rv.put("added",   rv_added); }
      if (rv_removed.length() > 0) { rv.put("removed", rv_removed); }
      if (rv.length() == 0){
         rv = null;
      }
      return(rv);
   }

   /** 
    * creates a light copy of the src object,
    * it is created a new JSONObject
    * and all the first level objects references are copied
    */
   public JSONObject jsonObject_clone(JSONObject src) {
      JSONObject rv = new JSONObject();
      for (String k: JSONObject.getNames(src)) {
         rv.put(k, src.opt(k));
      }
      return(rv);
   }

   /**
    * Used to conver a space separatd strings in string separated
    * by "%" (as SQL "like clause" requires)
    * <br>
    * example:
    * <br>
    * "this string become" is converted in "%this%string%become%"
    */
   public String sql_convertToLikeClause(String strin) {
      String rv = null;
      if (strin != null) {
         StringBuffer sb = new StringBuffer();
         if (strin.trim().length() == 0) {
            sb.append("%");
         } else {
            Vector<String> vpredicates = splitString(strin, ' ');
            for (String s: vpredicates){
               sb.append(s + "%");
            }
         }
         rv = sb.toString();
      }
      return(rv);
   }
   
   public String sqlListOfColumnsToSelectList(Vector<String> columns) {
      StringBuffer sb = new StringBuffer();
      String comma = "";
      for(String c: columns) {
         sb.append(comma);
         sb.append(c);
         comma = ", ";
      }
      return(sb.toString());
   }

   public String sqlListOfColumnsToSelectList(Vector<String> columns
                                              , HashMap<String,String> computedColums
                                              ) {
      return(sqlListOfColumnsToSelectList(columns
                                          , computedColums
                                          , ""
                                          ));
   }

   public String sqlListOfColumnsToSelectList(Vector<String> columns
                                              , HashMap<String,String> computedColums
                                              , String tableAliasName
                                              ) {
      if (!tableAliasName.equals("")) {
         tableAliasName = tableAliasName + ".";
      }
      StringBuffer sb = new StringBuffer();
      String comma = "";
      for(String c: columns) {
         String cn = computedColums.get(c);
         if (cn == null) {
            cn = tableAliasName + c + " as " + c;
         } else {
            cn = cn + " as " + c;
         }
         sb.append(comma);
         sb.append(cn);
         comma = ", ";
      }
      return(sb.toString());
   }

   /** 
    * 
    cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="tools test user"
    cd /dati/toolsZippati/projects/spacemr; gradle run -Pargs="tools test user" | emacs_filter_maven.pl
    */
   public void doTest() {
      String s = ""
         + "\n hello:"
         ;
      System.out.println(s);
   }

   public JSONObject getTableMattoniDefaults(String tableid 
                                             , HttpSession session
                                             ) throws Exception {
      String propertyName = "table_mattoni__"+tableid;
      int app_user_id = appSessionTools.getUserData(session).getApp_user_id();
      JSONObject rv = app_user.getUserPropertyAsJSONObject(app_user_id, propertyName);
      if (rv == null) {
         rv = app_system_property.getAsJSONObject(propertyName);
      }
      return(rv);
   }

   public String string_loadFromResources(String filePath) throws Exception {
      StringBuffer rv = new StringBuffer();
      byte buf[] = new byte[500];
      int buf_l;
      Resource res = applicationContext.getResource("classpath:"+filePath);
      // System.out.println(" *********************************************** res: " + res);
      // System.out.println(" res.getClass().getName(): " + res.getClass().getName());
      // ----
      InputStream fis = res.getInputStream();
      if (fis == null) {
         String m = "Resource not found.["+filePath+"]"
            + "\nPlease check classpath."
            + "\nClassLoader: " 
            + Tools.class.getClassLoader().getClass().getName()
            + "\n"
            ;
         throw(new Exception(m));
      }
      // System.out.println(" filePath: " + filePath);
      // System.out.println(" fis: " + fis);
      while ((buf_l =  fis.read(buf)) >= 0) {
         rv.append(new String(buf,0, buf_l));
      }
      fis.close();
      return(rv.toString());
   }

   /**
    */
   public static String
      string_loadFromClasspath(String fileName
                               ) throws FileNotFoundException
                                        , IOException {
      String rv = null;
      String classPath = System.getProperty("java.class.path",".");
      Vector dirs = splitString(classPath, File.pathSeparatorChar );
      for (Iterator i=dirs.iterator(); (rv == null) && i.hasNext(); ) {
         String dir = (String)i.next();
         // System.out.println(" dir: " + dir);
         File f = new File(dir + File.separator + fileName);
         System.out.println(" file: " + dir + File.separator + fileName);
         if (f.exists()) {
            InputStreamReader fis;
            StringBuffer rvsb;
            char buf[] = new char[500];
            int buf_l;
            // ----
            rvsb = new StringBuffer();
            fis = new InputStreamReader(new FileInputStream(f));
            while ((buf_l =  fis.read(buf)) >= 0) {
               rvsb.append(new String(buf,0,buf_l));
            }
            fis.close();
            rv = rvsb.toString();
         }
      }
      return(rv);
   }

   public static boolean object_equalsWithNull(Object o1, Object o2)  {
      boolean rv = false;
      if (o1 == null) {
         if (o2 == null) {
            rv = true;
         }
      } else {
         rv = o1.equals(o2);
      }
      return(rv);
   }

   
   public static String getStringTimeStamp() {
      return(_SYS_DATE_FORMATTER.format(new java.util.Date()));
   }
   
   public static String getStringTimeStamp(java.util.Date date) {
      return(_SYS_DATE_FORMATTER.format(date));
   }
   public static String getItalianDateAsString(java.util.Date date) {
      return(_ITALIAN_DATE_FORMATTER.format(date));
   }

   /**
    * Upcase first char
    */
   public  String stringUpcaseFirst(String s) {
      String rv = null;
      if (s != null) {
         rv = s;
         if (s.length() > 0) {
            char first = Character.toUpperCase(s.charAt(0));
            rv = first + s.substring(1);
         }
      }
      return (rv);
   }

   /**
    * Return the stack trace of an exception in a string.
    * @author Alberto Corni (May 2000)
    */
   public static String stringStackTrace(Exception e) {
      String rv;
      // -----
      ByteArrayOutputStream ostream = new ByteArrayOutputStream();
      PrintWriter p = new PrintWriter(ostream);
      e.printStackTrace(p);
      p.flush();
      rv = ostream.toString();
      //
      return(rv);
   }

   /**
    * Split a string separated by the "separator" char in single strings.
    *
    * @param  s the string to split
    * @param  separator the character separator of the various strings
    * @return a vector of strings
    */
   public static Vector<String> splitString(String s, char separator) {
      Vector<String> rv = new Vector<String>();
      // ----
      if (s != null && s.length() > 0) {
         String c = "" + separator;
         String sub;
         int start = 0;
         int end   = s.indexOf(c, start);
         while (end >= 0) {
            sub = s.substring(start, end);
            rv.add(sub);
            start = end + 1;
            if (start < s.length()) {
               end   = s.indexOf(c, start);
            } else {
               end = -1;
            }
         }
         //-
         // Last occurence
         //-
         if (start > s.length()) {
            start = s.length();
         }
         sub = s.substring(start);
         rv.add(sub);
      }
      return (rv);
   }

   /**
    * in a strings, replace a given pattern with one other.
    */
   public static  String stringReplaceInString(String sin,
                                               String sold,
                                               String snew) {
      int i=0;
      int lold=sold.length();
      int lnew=snew.length();
      // -----
      if (sin != null) {
          sin = new String(sin);
          i = sin.indexOf(sold);
          while (i >= 0) {
             sin = sin.substring(0,i) + snew + sin.substring(i+lold);
             i = sin.indexOf(sold, i+lnew);
             // System.out.println(" -- i." + i + " sin." + sin);
          }
      }
      return sin;
   }

   /**
    * Reads a file and put it in a string using a StringBuffer.
    * @param  fileName name of the file to be read.
    * @return the string with the file content.
    */
   public static  String string_loadFromFile(String fileName
                                             ) throws FileNotFoundException, IOException {
      return(string_loadFromFile(fileName, StandardCharsets.UTF_8));
   }

   public static  String string_loadFromFile(File file
                                             ) throws FileNotFoundException, IOException {
      return(string_loadFromFile(file, StandardCharsets.UTF_8));
   }

   /**
    * Reads a file and put it in a string using a StringBuffer.
    * @param  fileName name of the file to be read.
    * @return the string with the file content.
    */
   public static  String string_loadFromFile(String fileName
                                             , Charset charset
                                             ) throws FileNotFoundException, IOException {
      return(string_loadFromFile(new File(fileName), charset));
   }



   /**
    * Reads a file and put it in a string using a StringBuffer.
    * @param  fileName name of the file to be read.
    * @return the string with the file content.
    */
   public static  String string_loadFromFile(File file
                                             , Charset charset
                                             ) throws FileNotFoundException, IOException {
      InputStreamReader fis;
      StringBuffer rv;
      char buf[] = new char[500];
      int buf_l;
      // ----
      rv = new StringBuffer();
      fis = new InputStreamReader(new FileInputStream(file), charset);
      while ((buf_l =  fis.read(buf)) >= 0) {
         rv.append(new String(buf,0,buf_l));
      }
      fis.close();
      return (rv.toString());
   }


   /**
    * Write the content of a string in a file, overwriting it.
    * @param fileName name of the file to be written.
    * @param s        file content.
    */
   public static  void string_writeToFile(String fileName, String s
                                          ) throws Exception {
      string_writeToFile(fileName, s, false);
   }

   /**
    * Write the content of a string in a file.
    * @param fileName name of the file to be written.
    * @param s        file content.
    * @param append   If true, bytes will be written to the end of the
    *                 file rather than the beginning.
    */
   public static void string_writeToFile(String fileName
                                         , String s
                                         , boolean append) throws Exception {
      FileOutputStream fos;
      int buf_l;
      // ----
      fos = new FileOutputStream(fileName, append);
      fos.write(s.getBytes());
      fos.close();
   }


   public static JSONArray vector_of_string_to_sjonarray(Vector<String>v
                                                         ) throws Exception {
      JSONArray rv = new JSONArray();
      for (String s: v) {
         rv.put(s);
      }
      return(rv);
   }
      
   /**
    * Split a string separated by the "separator" char in single strings.
    * <br>
    * Is used to implement documentation. A documentation Object
    * is characterized by a unique number that identifies it uniquely.
    * <br>
    * Moreover such documentation is associated with a directory on a
    * the filesystem containing a set of documents that is the
    * "documentation".
    * <br>
    * This algorithm is used to reduce the number of entry in a single
    * directory and spread a high number of entries in a hierarchical
    * directory structure. Each directory has non more than 200 entries.
    * <br>
    * Using base 100, this algorithm produces the following output:
    *	<pre>
    *	in [1] out.[p01]
    *	in [10] out.[p10]
    *	in [100] out.[01\p00]
    *	in [1000] out.[10\p00]
    *	in [99999] out.[09\99\p99]
    *	in [999999] out.[99\99\p99]
    *	</pre>
    *
    * @param  inNumber  the number to create the path from
    * @return a string starting with "/"
    */
   public static  String getPathFromInt(int inNumber, int base) {
      int i = inNumber;
      //
      String rv = "";
      boolean fg_first = true;
      while (i>0) {
         String s = "" + (i%base);
         if (i%base < 10) { s = "0" + s; }
         i = i / base;
         if (fg_first) {
            fg_first = false;
            rv= "p" + s + rv;
         } else {
            rv= s + File.separator + rv;
         }
      }
      return (rv);
   }

   
   /**
    * Parser for mime data in the following format:
    * <pre>
    *  Content-Disposition: form-data; name="filecontent"; filename="pre.doc"
    * </pre>
    * <pre>
    *      +--------+    +-----------------------+
    *     *|        V    V                       |
    *      |   +----------+          ' '         |;
    *      +---| ignore   |-------------------+  |
    *          +----------+                   |  |
    *               ^                         V  |
    *               |                    +----------+
    *               |"                   | name     |----+
    *      +-----+  |                    +----------+    |
    *     *|     V  |                         |    ^     |
    *      |   +----------+      +----+       |=   +-----+
    *      +---| value    |<-----|tmp2|-------+
    *          +----------+   "  +----+
    * </pre>
    */
   @SuppressWarnings("unchecked")
   public static HashMap parseMimeHeader(InputStream is
                                         , boolean debug
                                         ) throws Exception {
      //-
      final int st_ignore = 1;
      final int st_name   = 2;
      final int st_value  = 3;
      final int st_tmp2   = 4;
      //-
      int status = st_ignore;
      int b;
      HashMap rv = new HashMap();
      //-
      StringBuffer value = new StringBuffer();
      StringBuffer name  = null;
      char c;
      //-
      while ((b = is.read()) >= 0 ) {
         c = (char)b;
         switch (status) {
         case st_ignore:
            if (c == ' ') {
               name  = new StringBuffer();
               status = st_name;
            } else {
               // do nothing
            }
            break;
         case st_name:
            if (c == '=') {
               status = st_tmp2;
            } else if (c == ';') {
               status = st_ignore;
            } else if (c ==10) {
               // ignore
            } else if (c ==13) {
               // ignore
            } else {
               name.append(c);
            }
            break;
         case st_tmp2:
            if (c == '"') {
               status = st_value;
            }
            break;
         case st_value:
            if (c == '"') {
               status = st_ignore;
               String vals = value.toString();
               if (debug) {
                  System.out.println(" found.["+name+"]["+vals+"]");
               }
               rv.put(name.toString(), vals);
               value = new StringBuffer();
               name  = new StringBuffer();
            } else if (c ==10) {
               // ignore
            } else if (c ==13) {
               // ignore
            } else {
               value.append(c);
            }
            break;
         }
      }
      is.close();
      return(rv);
   }

   /**
    * Dumps a property on a string */
   public static String propertiesToString(Properties p) throws Exception {
      ByteArrayOutputStream ostream = new ByteArrayOutputStream();
      p.store(ostream, "");
      String s = ostream.toString();
      return(s);
   }

   /**
    * Restore a property from a string */
   public static Properties propertiesFromString(String s) throws Exception {
      ByteArrayInputStream istream = new ByteArrayInputStream(s.getBytes());
      Properties rv = new Properties();
      rv.load(istream);
      istream.close();
      return(rv);
   }

   public void runUsage() {
      String s = ""
         + "\n tool usage:"
         + "\n  run tool <option> [args]"
         + "\n supported options:"
         + "\n  test - various app tool tests "
         ;
      System.out.println(s);
   }

   /** 
    * command line parameter parser */
   public void run(String args[]) throws Exception {
      int ipos = 1;
      String option = args[ipos++];
      if (args.length < 2) {
         runUsage();
      } else {
         if (option.equals("test")) {
            System.out.println(" hello! test.");
            doTest();
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }
   
   private static DecimalFormat _decimalFormat = new DecimalFormat("0.##");
   static {
      _decimalFormat.setDecimalSeparatorAlwaysShown(false);
   }
   private static SimpleDateFormat _SYS_DATE_FORMATTER =
      new SimpleDateFormat("yyyyMMdd:HHmmss");
   //-
   private static SimpleDateFormat _ITALIAN_DATE_FORMATTER =
      new SimpleDateFormat("dd/MM/yyyy");
   
   @Autowired
   private App_system_property app_system_property;
   @Autowired
   private App_user app_user;
   @Autowired
   private ApplicationContext applicationContext;

   @Autowired
   private AppSessionTools appSessionTools;


}
