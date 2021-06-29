package it.unimo.app.tools;

import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;
import java.util.Date;
import java.util.Properties;
import java.io.StringReader;
import java.io.StringWriter;

import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.PasswordAuthentication;


import org.springframework.beans.factory.annotation.Autowired;

import it.unimo.app.om.App_system_property;
import it.unimo.app.om.App_log;
import it.unimo.app.tools.Tools;

import freemarker.template.Template;
import freemarker.template.Configuration;
   
public class Spacemr_email_manager {
   
   @Autowired
   private Tools tools;

   @Autowired
   private App_system_property app_system_property;

   @Autowired
   private App_log app_log;
   
   public void runUsage() {
      String s = ""
         + "\n spacemr_email_manager:"
         + "\n  send <comma_separtated_to_list>  <subject> <text>"
         ;
      System.out.println(s);
   }

   /** 
    * command line parameter parser */
   public void run(String args[]) throws Exception {
      int ipos = 1;
      if (args.length < 2) {
         runUsage();
      } else {
         String option = args[ipos++];
         if (option.equals("test")) {
            //-
            //-  cd /dati/toolsZippati/projects/spacemr; time gradle bootRun -Pargs="spacemr_email_manager test"
            //-
            System.out.println("-- hello! Spacemr_email_manager test.");
            System.out.println("args:");
            for (;ipos<args.length;ipos++) {
               System.out.println("   " + ipos + ": " + args[ipos]);
            }
         } else if (option.equals("send")) {
            // cd /dati/toolsZippati/projects/spacemr; time gradle bootRun -Pargs="spacemr_email_manager send alberto.corni@unimore.it this_is_the_subject this_is_the_text"
            // //-
            // //-
            String to      = args[ipos++];
            String subject = args[ipos++];
            String text    = args[ipos++];
            //-
            System.out.println(" I shold send ["+subject+"]["+text+"] to ["+to+"]");
            //-
            try {
               doSendEmail(to, subject, text);
            } catch (MessagingException mex) {
               System.out.println("send failed, exception: " + mex);
            }
            System.out.println(" done.");
            //-
         } else if (option.equals("sendHtml")) {
            // cd /dati/toolsZippati/projects/spacemr; time gradle bootRun -Pargs="spacemr_email_manager sendHtml alberto.corni@unimore.it this_is_the_subject '<h1>Hello</h1>this_is_the_text'"
            // //-
            // //-
            String to      = args[ipos++];
            String subject = args[ipos++];
            String text    = args[ipos++];
            //-
            System.out.println(" I shold send ["+subject+"]["+text+"] to ["+to+"]");
            //-
            try {
               doSendEmailHtml(to, subject, text);
            } catch (MessagingException mex) {
               System.out.println("send failed, exception: " + mex);
            }
            System.out.println(" done.");
            //-
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }


   public void doSendEmail(String to, String subject, String text
                           ) throws Exception {
      MimeMessage msg = getTextMessage(to,subject, text);
      Transport.send(msg);
   }
   
   public void doSendEmailHtml(String to, String subject, String text
                           ) throws Exception {
      MimeMessage msg = getHtmlMessage(to,subject, text);
      Transport.send(msg);
   }

   public MimeMessage getTextMessage(String to, String subject, String text
                              ) throws Exception {
      MimeMessage msg = getMessage(to,subject);
      msg.setText(text);
      return(msg);
   }

   public MimeMessage getHtmlMessage(String to, String subject, String html_text
                              ) throws Exception {
      MimeMessage msg = getMessage(to,subject);
      msg.setContent(html_text, "text/html; charset=utf-8");
      // msg.setText(html_text);
      return(msg);
   }

   
   public MimeMessage getMessage(String to, String subject
                              ) throws Exception {
      Properties props =
         tools
         .propertiesFromString(app_system_property
                               .getAsString("sys_email_configuration"));
      //-
      //-
      String from     = props.getProperty("mail.smtp.from_email");
      String username = props.getProperty("mail.smtp.username");
      String password = props.getProperty("mail.smtp.password");
      //-
      // System.out.println("props:\n" + tools.propertiesToString(props)+"\n----");
      //-
      Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
         });
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(from);
      msg.setRecipients(Message.RecipientType.TO, to);
      msg.setSubject(subject);
      msg.setSentDate(new Date());
      return(msg);
   }

   
   public String applyTemplate(String template
                               , HashMap<String, Object> root
                               ) throws Exception {
      Template t = new Template("name", new StringReader(template),
                                new Configuration());
      StringWriter stringWriter = new StringWriter();
      t.process(root, stringWriter);
      String rv = stringWriter.toString();
      return(rv);
   }

   
}
