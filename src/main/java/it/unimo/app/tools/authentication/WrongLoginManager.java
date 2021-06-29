package it.unimo.app.tools.authentication;

// jdk
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Date;
import java.util.Vector;
import java.util.Iterator;

/** 
 * fail2ban like login manager 
 * if you use mod_proxy please set the 
 * system property  "auth_x-forwarded-for"
 */
public class WrongLoginManager {

   public boolean isIpBanned(String ip) {
      long now = (new Date()).getTime();
      return(isIpBanned(ip, now));
   }

   /**
    * for testing */
   public boolean isIpBanned(String ip, long now) {
      boolean rv = false;
      clean(now);
      //-
      IpInfo ipinfo = _ipmap.get(ip);
      if (ipinfo != null) {
         if (ipinfo.unbanTime != null) {
            //-
            //- unban checks are done in "clean"
            //-
            rv = true;
         }
      }
      // System.out.println(" isIpBanned for ["+ip+"]: "+rv);
      return(rv);
   }
   
   public void wrongLogin(String ip) {
      long now = (new Date()).getTime();
      wrongLogin(ip, now);
   }

   /**
    * for testing */
   public void wrongLogin(String ip, long now) {
      // System.out.println(" wrongLogin for ["+ip+"]");
      clean(now);
      wrongLogin_syncronized(ip, now);
   }

   /** override this for customize logging  */
   public void doLog(String ban_message) {
      System.err.println(" > " + ban_message);
   }
   
   private synchronized void wrongLogin_syncronized(String ip, long now) {
      IpInfo ipinfo = _ipmap.get(ip);
      if(ipinfo == null) {
         ipinfo = new IpInfo();
         _ipmap.put(ip, ipinfo);
      }
      ipinfo.timestamps.add(Long.valueOf​(now));
      //-
      //- clean removed time-outed timestamps
      //-
      if (ipinfo.timestamps.size() >= max_wrong_access) {
         //-
         //- ban!
         //-
         ipinfo.unbanTime = Long.valueOf(now + banned_interval_milliseconds);
         doLog("banned ip.["+ip+"]");
      }
   }

   private synchronized void clean(long now) {
      long remove_threshold = now - max_wrong_access_time_interval_milliseconds;
      for(Object ipo: _ipmap.keySet().toArray()) {
         String ip = (String)ipo;
         //-
         IpInfo ipinfo = _ipmap.get(ip);
         //-
         while(ipinfo.timestamps.size() > 0
               && ipinfo.timestamps.getFirst().longValue() < remove_threshold
               ) {
            ipinfo.timestamps.remove();
         }
         //-
         if (ipinfo.unbanTime != null) {
            if (now > ipinfo.unbanTime.longValue()) {
               ipinfo.unbanTime = null;
               doLog("unban ip.["+ip+"]");
            }
         }
         //-
         if (ipinfo.unbanTime == null
             && (ipinfo.timestamps.size() == 0)
             ) {
            //- remove this ip from
            _ipmap.remove(ip);
         }
      }
   }


   public String toString() {
      StringBuffer rv = new StringBuffer();
      for(Object ipo: _ipmap.keySet().toArray()) {
         String ip = (String)ipo;
         IpInfo ipinfo = _ipmap.get(ip);
         rv.append("\n ["+ip+"] "
                   + "("+ (ipinfo.unbanTime == null?"---":"BAN") +")")
            ;
         //-
         Iterator<Long> iterator = ipinfo.timestamps.listIterator(0);
         while (iterator.hasNext()){
            rv.append(" " + iterator.next().longValue());
         }
      }
      return(rv.toString());
   }
   
   private class IpInfo {
      //-
      //- list of the last wrong login as timestamps (milliseconds)
      //-  file:///dati/toolsZippati/docs/manuali/java/jdk-1_6/docs/api/index.html
      public LinkedList<Long> timestamps = new LinkedList<Long>();
      //-
      public Long unbanTime = null;
   }

   /** 
    * Key: String username, value: HttpSession session */
   private HashMap<String, IpInfo> _ipmap = new HashMap<String, IpInfo>();

   private static int  max_wrong_access = 6;
   private static long max_wrong_access_time_interval_milliseconds = 6 * 60 * 1000;
   private static long banned_interval_milliseconds = 10 * 60 * 1000;
   
}
