package it.unimo.app.tools.spacemr_inventarioImport;

import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;
import java.util.HashMap;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;

//-ldap
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.unimo.app.om.App_system_property;
import it.unimo.app.om.Spacemr_people;
import it.unimo.app.om.Spacemr_space;
import it.unimo.app.om.Spacemr_inventario;
import it.unimo.app.om.App_log;
import it.unimo.app.tools.Tools;

   
public class Spacemr_inventarioImport_unimoreUgov {
   
   @Autowired
   private JdbcTemplate jdbcTemplate;
   
   @Autowired
   private Tools tools;

   @Autowired
   private App_system_property app_system_property;

   @Autowired
   private Spacemr_people spacemr_people;
   @Autowired
   private Spacemr_space spacemr_space;
   @Autowired
   private Spacemr_inventario spacemr_inventario;

   @Autowired
   private App_log app_log;
   
   public void runUsage() {
      String s = ""
         + "\n spacemr_inventarioImport_unimoreUgov:"
         + "\n  [column_50] doImport <fileName.csv> - import from the UGOV excel file CSV by character '~'"
         + "\n column_50 indicate the format of the csv file"
         ;
      System.out.println(s);
   }

   /** 
    * command line parameter parser */
   public void run(String args[]) throws Exception {
      int ipos = 1;
      boolean column_50 = false;
      if (args.length < 2) {
         runUsage();
      } else {
         String option = args[ipos++];
         if (option.equals("column_50")) {
            column_50 = true;
            option = args[ipos++];
         }
         if (option.equals("test")) {
            //- cd /dati/toolsZippati/projects/spacemr; time gradle run -Pargs="spacemr_inventarioImport_unimoreUgov test arg1 arg2 etc..." 
            System.out.println("-- hello! test.");
            System.out.println("args:");
            for (;ipos<args.length;ipos++) {
               System.out.println("   " + ipos + ": " + args[ipos]);
            }
            
         } else if (option.equals("doImport")) {
            //-
            /* 
             delete from spacemr_inventario;
             delete from app_log where object_type = 1060;
             */
            //-
            //- cd /dati/toolsZippati/projects/spacemr; time gradle run -Pargs="spacemr_inventarioImport_unimoreUgov doImport docs/inventario-completo-20190530.csv" 
            //-
            //-
            String fileName = args[ipos++];
            //-
            System.out.println("Checking ["+fileName+"]");
            String errors = doImport(fileName, true, column_50);
            if (1 == 1 && "".equals(errors)) {
               System.out.println("Importing from ["+fileName+"]");
               errors = doImport(fileName, false, column_50);
               if (!"".equals(errors)) {
                  System.out.println("\n#### errors! ####\n\n" + errors);
               }
            } else {
               System.out.println("Errors before importing:\n"+errors
                                  +"\n\nNo records has been imported.");
            }
            //-
         } else {
            runUsage();
         }
      }
      System.out.println("");
   }

   public String doImport(String fileName, Boolean checkOnly, boolean column_50) throws Exception {
      //- returns the errors
      // 1514/I.D12
      //- [Al 20210326-09:19]
      //- Lisa e' una brava persona, e UGOV e' un po ingannevole,
      //-  UGOV permette di esportare l'inventario in 2 formati
      //-    52 colonne con i campi numero_buono_scarico, data_buono_scarico
      //-    50 colonne senza tali campi
      //- per rendermi la vita piu' interessante
      //- a volte Lisa esporta il file in un formato
      //- altre volte nell'altro formato
      //- indipendentemente da quante volte glielo dico o scrivo.
      //- per cui il parametro column_50
      //-
      
      //-
      HashMap<String,String> exDips = new HashMap<String,String>();
      exDips.put("I.D11","Ex.DIMA");
      exDips.put("I.D12","Ex.DII");
      exDips.put("I.D13","Ex.DIMEC");
      exDips.put("I.MOINGE","Ex.FAC-MOINGE");
      //-
      Pattern exDips_pattern =
         Pattern.compile(".* ([0-9]+)/(I.[0-9A-Z]+)$");
      //-
      final String system_user = "sys-inventarioUgov" ;
      DecimalFormat decimalFormat = new DecimalFormat("0.##");
      decimalFormat.setDecimalSeparatorAlwaysShown(false);
      //-
      //-
      StringBuffer rv = new StringBuffer();
      BufferedReader br = null;
      HashSet<String> missing_users = new HashSet<String>();
      HashSet<String> missing_spaces = new HashSet<String>();
      System.out.println("Working Directory = " + System.getProperty("user.dir"));
      try {
         File file = new File(fileName);
         // System.out.println(" file: " + file.canRead());
         // System.out.println(" file: " + file.getAbsolutePath());
         // System.out.println(" file.getpath: " + file.getPath());
         // System.out.println(" file.getAbsolutePath(): " + file.getAbsolutePath());
         // System.out.println(" file: " + file.getName());
         // System.out.println(" file.isFile: " + file.isFile());
         br = new BufferedReader(new FileReader(file));
      } catch (Exception e) {
         rv.append("error opening file ["+fileName+"]\n"+
                   tools.stringStackTrace(e)
                   );
      }
      int lineNumber = 0;
      if (rv.length()==0) {
         String line = "";
         //- skip headers lines
         while (lineNumber < 7 && (line = br.readLine()) != null) {
            lineNumber++;
         }
         //-
         //- reading headers
         if (0 == 1) {
            Vector<String> v = tools.splitString(line, '~');
            HashMap<String,Integer> headers = new HashMap<String,Integer>();
            for (int vi=0; vi<v.size(); vi++) {
               System.out.println(vi+ ": " + v.get(vi));
            }
         }
         int number_of_columns = 52;
         if (column_50) {
            number_of_columns = 50;
         }
         while (lineNumber < 300000 && (line = br.readLine()) != null) {
            lineNumber++;
            JSONObject si = new JSONObject();
            try {
               Vector<String> v = tools.splitString(line, '~');
               while (v.size()<number_of_columns) {
                  String lineOld = line;
                  line = br.readLine();
                  lineNumber++;
                  line = lineOld + "\n" + line;
                  v = tools.splitString(line, '~');
               }
               int column_i = 0;
               String column_inventario = v.get(column_i++);
               String column_descrizione_inventario = v.get(column_i++);
               String column_codice_responsabile = v.get(column_i++);
               String column_responsabile_inventario = v.get(column_i++);
               String column_numero_inventario = v.get(column_i++);
               String column_sub_inventario = v.get(column_i++);
               String column_num_inventario_ateneo = v.get(column_i++);
               String column_numero_buono = v.get(column_i++);
               String column_tipo_buono = v.get(column_i++);
               String column_codice_tipo_carico_scarico = v.get(column_i++);
               String column_descrizione_tipo_carico_scarico = v.get(column_i++);
               String column_data_carico = v.get(column_i++);
               String column_tipo_campo_attivita = v.get(column_i++);
               String column_descrizione_bene = v.get(column_i++);
               String column_valore_convenzionale = v.get(column_i++);
               String column_esercizio_bene_migrato = v.get(column_i++);
               String column_numero_carico_bene_migrato = v.get(column_i++);
               String column_data_carico_migrato = v.get(column_i++);
               //-
               //- i seguenti due campi a volte non sono presenti
               String column_numero_buono_scarico = "";
               String column_data_buono_scarico = "";
               if (!column_50) {
                  //  check also the line:      while (v.size()<50) {
                  column_numero_buono_scarico = v.get(column_i++);
                  column_data_buono_scarico = v.get(column_i++);
               }
               //-
               String column_codice_categoria = v.get(column_i++);
               String column_descrizione_categoria = v.get(column_i++);
               String column_codice_immobilizzazione = v.get(column_i++);
               String column_descrizione_immobilizzazione = v.get(column_i++);
               String column_codice_spazio = v.get(column_i++);
               String column_descrizione_spazio = v.get(column_i++);
               String column_locale = v.get(column_i++);
               String column_codice_resp_spazio = v.get(column_i++);
               String column_responsabile_spazio = v.get(column_i++);
               String column_codice_possessore = v.get(column_i++);
               String column_possessore = v.get(column_i++);
               String column_codice_fornitore = v.get(column_i++);
               String column_denominazione_fornitore = v.get(column_i++);
               String column_anno_di_fabbricazione = v.get(column_i++);
               String column_numero_seriale = v.get(column_i++);
               String column_cib = v.get(column_i++);
               String column_marca = v.get(column_i++);
               String column_numero_targa = v.get(column_i++);
               String column_condizione_bene = v.get(column_i++);
               String column_garanzia_da = v.get(column_i++);
               String column_garanzia_a = v.get(column_i++);
               String column_uo_di_riferimento = v.get(column_i++);
               String column_note = v.get(column_i++);
               String column_nome_tipo_dg = v.get(column_i++);
               String column_data_dg = v.get(column_i++);
               String column_numero_dg = v.get(column_i++);
               String column_data_registrazione_dg = v.get(column_i++);
               String column_numero_registrazione_dg = v.get(column_i++);
               String column_edificio_collegato_spazio = v.get(column_i++);
               String column_aliquota_ammortamento_ordinario = v.get(column_i++);
               String column_aliquota_ammortamento_fiscale = v.get(column_i++);
               String column_dg_contributo_impianto = v.get(column_i++);
               String column_percentuale_contributo_impianto = v.get(column_i++);
               //-
               if ( 1 != 1) {
                  String s = ""
                     + "\n column_inventario: " + column_inventario
                     + "\n column_descrizione_inventario: " + column_descrizione_inventario
                     + "\n column_codice_responsabile: " + column_codice_responsabile
                     + "\n column_responsabile_inventario: " + column_responsabile_inventario
                     + "\n column_numero_inventario: " + column_numero_inventario
                     + "\n column_sub_inventario: " + column_sub_inventario
                     + "\n column_num_inventario_ateneo: " + column_num_inventario_ateneo
                     + "\n column_numero_buono: " + column_numero_buono
                     + "\n column_tipo_buono: " + column_tipo_buono
                     + "\n column_codice_tipo_carico_scarico: " + column_codice_tipo_carico_scarico
                     + "\n column_descrizione_tipo_carico_scarico: " + column_descrizione_tipo_carico_scarico
                     + "\n column_data_carico: " + column_data_carico
                     + "\n column_tipo_campo_attivita: " + column_tipo_campo_attivita
                     + "\n column_descrizione_bene: " + column_descrizione_bene
                     + "\n column_valore_convenzionale: " + column_valore_convenzionale
                     + "\n column_esercizio_bene_migrato: " + column_esercizio_bene_migrato
                     + "\n column_numero_carico_bene_migrato: " + column_numero_carico_bene_migrato
                     + "\n column_data_carico_migrato: " + column_data_carico_migrato
                     + "\n column_numero_buono_scarico: " + column_numero_buono_scarico
                     + "\n column_data_buono_scarico: " + column_data_buono_scarico
                     + "\n column_codice_categoria: " + column_codice_categoria
                     + "\n column_descrizione_categoria: " + column_descrizione_categoria
                     + "\n column_codice_immobilizzazione: " + column_codice_immobilizzazione
                     + "\n column_descrizione_immobilizzazione: " + column_descrizione_immobilizzazione
                     + "\n column_codice_spazio: " + column_codice_spazio
                     + "\n column_descrizione_spazio: " + column_descrizione_spazio
                     + "\n column_locale: " + column_locale
                     + "\n column_codice_resp_spazio: " + column_codice_resp_spazio
                     + "\n column_responsabile_spazio: " + column_responsabile_spazio
                     + "\n column_codice_possessore: " + column_codice_possessore
                     + "\n column_possessore: " + column_possessore
                     + "\n column_codice_fornitore: " + column_codice_fornitore
                     + "\n column_denominazione_fornitore: " + column_denominazione_fornitore
                     + "\n column_anno_di_fabbricazione: " + column_anno_di_fabbricazione
                     + "\n column_numero_seriale: " + column_numero_seriale
                     + "\n column_cib: " + column_cib
                     + "\n column_marca: " + column_marca
                     + "\n column_numero_targa: " + column_numero_targa
                     + "\n column_condizione_bene: " + column_condizione_bene
                     + "\n column_garanzia_da: " + column_garanzia_da
                     + "\n column_garanzia_a: " + column_garanzia_a
                     + "\n column_uo_di_riferimento: " + column_uo_di_riferimento
                     + "\n column_note: " + column_note
                     + "\n column_nome_tipo_dg: " + column_nome_tipo_dg
                     + "\n column_data_dg: " + column_data_dg
                     + "\n column_numero_dg: " + column_numero_dg
                     + "\n column_data_registrazione_dg: " + column_data_registrazione_dg
                     + "\n column_numero_registrazione_dg: " + column_numero_registrazione_dg
                     + "\n column_edificio_collegato_spazio: " + column_edificio_collegato_spazio
                     + "\n column_aliquota_ammortamento_ordinario: " + column_aliquota_ammortamento_ordinario
                     + "\n column_aliquota_ammortamento_fiscale: " + column_aliquota_ammortamento_fiscale
                     + "\n column_dg_contributo_impianto: " + column_dg_contributo_impianto
                     + "\n column_percentuale_contributo_impianto: " + column_percentuale_contributo_impianto
                     ;
                  System.out.println("line: " + s);
               }
               
               //-
               //-
               //-
               // System.out.println(lineNumber
               //                    // " - " + line
               //                    + " column_numero_inventario: "
               //                    + column_numero_inventario);
               //-
               //-
               String nota = "";
               {
                  String userCognomeNome = column_possessore;
                  JSONObject user = null;
                  if (userCognomeNome.indexOf(';')>=0) {
                     Vector<String> v1 = tools.splitString(userCognomeNome, ';');
                     userCognomeNome = v1.get(0);
                     nota = nota + ("".equals(nota)?"":", ") + v1.get(1);
                  }
                  if ("".equals(userCognomeNome)
                      || "direttore_dief".equals(userCognomeNome)
                      || "DIR.004 DIR. DIP. di INGEGNERIA \"Enzo Ferrari\"".equals(userCognomeNome)
                      || "\"DIR.004 DIR. DIP. di INGEGNERIA \"\"Enzo Ferrari\"\"\"".equals(userCognomeNome)
                      || "\"DIRETTORE DIP. INGEGNERIA \"\"ENZO FERRARI\"\" (INFO CAMPO \"\"COD.FISCALE\"\" UTILE SOLO PER SCOPI TECNICI) EFFETTIVO ECONOMO: ALESSANDRO CAPRA CF CPRLSN61E05A944G\"".equals(userCognomeNome)
                      || "\"DIRETTORE DIP. INGEGNERIA \"\"ENZO FERRARI\"\" (INFO CAMPO \"\"COD.FISCALE\"\" UTILE SOLO PER SCOPI TECNICI) EFFETTIVO ECONOMO: MASSIMO BORGHI CF BRGMSM56R16F257N\"".equals(userCognomeNome)
                      || "DIRETTORE DIP. INGEGNERIA \"ENZO FERRARI\" (INFO CAMPO \"COD.FISCALE\" UTILE SOLO PER SCOPI TECNICI) EFFETTIVO ECONOMO: ALESSANDRO CAPRA CF CPRLSN61E05A944G".equals(userCognomeNome)
                      || "DIRETTORE DIP. INGEGNERIA \"ENZO FERRARI\" (INFO CAMPO \"COD.FISCALE\" UTILE SOLO PER SCOPI TECNICI) EFFETTIVO ECONOMO: MASSIMO BORGHI CF BRGMSM56R16F257N".equals(userCognomeNome)
                      ){
                     user = spacemr_people.getByUsername("direttore_dief");
                     if (user == null) {
                        missing_users.add("direttore_dief");
                     } else {
                        si.put("spacemr_people_id", user.getInt("spacemr_people_id"));
                     }
                  } else {
                     user =  spacemr_people.getByLastFirstName(userCognomeNome);
                     if (user == null) {
                        missing_users.add(userCognomeNome);
                     } else {
                        si.put("spacemr_people_id", user.getInt("spacemr_people_id"));
                     }
                  }
               }
               //- 
               //- space     
               //- 
               {
                  String codiceSpazio = column_codice_spazio;
                  if (codiceSpazio.indexOf(';')>=0) {
                     Vector<String> v1 = tools.splitString(codiceSpazio, ';');
                     codiceSpazio = v1.get(0);
                     nota = nota + ("".equals(nota)?"":", ") + v1.get(1);
                  }
                  if (codiceSpazio == null
                      || "".equals(codiceSpazio)
                      ){
                     codiceSpazio = "MO-25-01-014";
                  } else {
                     if (codiceSpazio != null
                         && codiceSpazio.length()>1) {
                        codiceSpazio = codiceSpazio.substring(2);
                     }
                     if (
                         "MO-25-AM-001".equals(codiceSpazio)
                         || "MO-25-SP-002".equals(codiceSpazio)
                         || "ND-00-00-000".equals(codiceSpazio)
                         || "MO-25-02-034".equals(codiceSpazio)
                         ) {
                        codiceSpazio = "MO-25-01-014";
                     } else  if ("MO-28-00-027".equals(codiceSpazio)) {
                        codiceSpazio = "MO-28-00-027f";
                     }
                  }
                  JSONObject space = null;
                  space =  spacemr_space.getByCode(codiceSpazio);
                     if (space == null) {
                        missing_spaces.add(codiceSpazio);
                     } else {
                        si.put("spacemr_space_id", space.getInt("spacemr_space_id"));
                     }
               }
               //-
               //-
               si.put("fg_validato"              , false);
               si.put("nota"                     , nota);
               si.put("codice_inventario_unimore", column_inventario);
               si.put("inventario_numero"        , column_numero_inventario);
               si.put("inventario_numero_sub"    , column_sub_inventario);
               si.put("tipo_carico_scarico"      , column_codice_tipo_carico_scarico);
               // System.out.println(" column_data_carico: ["+column_data_carico+"]");
               Date carico_data     =            _DATE_FORMAT.parse(column_data_carico);
               si.put("carico_data"              , carico_data.getTime());
               si.put("attivita_tipo"            , column_tipo_campo_attivita);
               si.put("descrizione"              , column_descrizione_bene);
               si.put("categoria_inventario"     , column_codice_categoria);
               // System.out.println(" --- "+lineNumber+" column_valore_convenzionale: " + column_valore_convenzionale);
               //-
               //-
               //- inventario_etichetta
               //-
               //-
               {
                  String exNota = column_note;
                  // System.out.println(" exNota: " + exNota);
                  String inventario_etichetta = "DIEF " + si.getString("inventario_numero");
                  if (!"".equals(si.getString("inventario_numero_sub"))){
                     inventario_etichetta =
                        inventario_etichetta + "/" + si.getString("inventario_numero_sub");
                  }
                  Matcher m = exDips_pattern.matcher(exNota);
                  if (m.matches()) {
                     String dip = exDips.get(m.group(2));
                     if (dip == null) {
                        dip = m.group(2);
                     }
                     inventario_etichetta = inventario_etichetta +
                        ", " + dip + "/" + m.group(1);
                     // System.out.println(inventario_etichetta);
                  }
                  si.put("inventario_etichetta"     , inventario_etichetta);
               }
               //-
               //- valore
               //-
               {
                  String s = column_valore_convenzionale;
                  s = tools.stringReplaceInString(s,",","");
                  BigDecimal valore = new BigDecimal(s);
                  s = decimalFormat.format(valore);
                  // System.out.println(" valore: from.["+column_valore_convenzionale+"] to ["+s+"]");
                  si.put("valore", s);
               }
               si.put("fornitore"           , column_denominazione_fornitore);
               //-
               //- scarico, stato e validato
               //-
               /* 
                  select spacemr_inventario_id
                       , old_values
                    from spacemr_inventario
                   where old_values like '%"stato":"smaltito"%'
                   ;
                */
               {
                  si.put("scarico_numero_buono", column_numero_buono_scarico);
                  String stato = "da_controllare";
                  if (column_data_buono_scarico.length() > 0){
                     Date scarico_data = _DATE_FORMAT.parse(column_data_buono_scarico);
                     si.put("scarico_data"          , scarico_data.getTime());
                     stato = "smaltito";
                     si.put("fg_validato", true);
                     // System.out.println(" smaltito   inventario_etichetta: " + si.getString("inventario_etichetta"));
                  } else {
                     si.put("scarico_data"          , (Long)null);
                  }
                  si.put("stato" , stato);
               }
               //-
               //-
               //- 
               // System.out.println("si.toString(1): " + si.toString(1));
               //-
               //-
               //-
               boolean persitent_write = true;
               if (!checkOnly) {
                  //-
                  //- retrieve the object from DB
                  //-
                  JSONObject inventario =
                     spacemr_inventario
                     .getByCodiceInventario(si.getString("codice_inventario_unimore")
                                            , si.getString("inventario_numero")
                                            , si.getString("inventario_numero_sub")
                                            );
                  //-
                  si.put("old_values",si.toString());
                  si.put("old_values_changes","");
                  //-
                  if (inventario != null) {
                     boolean doUpdate = false;
                     //-
                     //- update rules
                     //-
                     //- 
                     if (!inventario.getString("old_values")
                         .equals(si.getString("old_values"))) {
                        //-
                        //- aggiorno se ci sono stati cambiamenti in  UGOV
                        //-
                        //- fa si che 
                        //- - se il bene e' stato scaricato viene marcato come scaricato
                        //- - se e' stato spostato o altro viene marcato come "da controllare"
                        //-
                        //-
                        doUpdate = true;
                     }
                     if (doUpdate) {
                        System.out.print("---- UPDATING ---- " + si.getString("descrizione") + "...");
                        if (persitent_write) {
                           int id = inventario.getInt("spacemr_inventario_id");
                           app_log.writeLog(spacemr_inventario, id, system_user, si);
                           si.put("spacemr_inventario_id", id);
                           spacemr_inventario.update(si);
                           System.out.println(" updated.");
                        }
                     }
                  } else {
                     System.out.print("---- Insert ---- " + si.getString("descrizione") + "...");
                     if (persitent_write) {
                        int id = spacemr_inventario.insert(si);
                        app_log.writeLog(spacemr_inventario, id, system_user, si);
                     }
                     System.out.println(" added.");
                  }
               }
            } catch(Exception e) {
               rv.append(tools.stringStackTrace(e)
                         + "\n in line "+lineNumber+": "
                         + "\n line: "+line 
                         + "\n for\n" + si.toString(1)
                         );
            }
         }
      }
      if (missing_users.size()>0) {
         rv.append("Utenti mancanti -begin-:\n");
         for (String s: missing_users) {
            rv.append(s+"\n");
         }
         rv.append("-end-.\n");
      }
      if (missing_spaces.size()>0) {
         rv.append("Spazi mancanti -begin-:\n");
         for (String s: missing_spaces) {
            rv.append(s+"\n");
         }
         rv.append("-end-.\n");
      }
      //-
      return(rv.toString());
   }


   private static SimpleDateFormat _DATE_FORMAT =
      new SimpleDateFormat("dd/MM/yyyy");
   
}
