package it.unimo.app.tools;


import java.util.*;
import java.sql.*;
import java.io.*;

import javax.sql.DataSource;

import java.lang.reflect.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class DbToolsTable {

   public DbToolsTable(String name, Connection c) throws Exception {
      setName(name);
      setColumnDefinitions(c);
   }

   public String getName() {
      return(_name);
   }

   public void setName(String name) {
      _name = name;
   }


   /** 
    * Adds a column to this table */
   public void addColumn(DbToolsColumn col) {
      _columns.add(col);
   }

   /** 
    * ... */
   @SuppressWarnings("unchecked")
   public void setColumnDefinitions(Connection c) throws Exception {
      _columns = new Vector();
      // System.out.println(" - loading ["+getName()+"]");
      {
         //-
         //- Columns
         //-
         Statement stm = c.createStatement();
         ResultSet rs = stm.executeQuery("SELECT * FROM " + getName());
         setColumnDefinitions(rs);
         rs.close();
         stm.close();
      }
   }


   /** 
    * ... */
   @SuppressWarnings("unchecked")
   public void setColumnDefinitions(ResultSet rs) throws Exception {
      ResultSetMetaData rsMd = rs.getMetaData();
      _columns = new Vector();
      for (int j=1;j<=rsMd.getColumnCount();j++){
         DbToolsColumn col = 
            new  DbToolsColumn(rsMd.getColumnName(j)
                               , rsMd.getColumnType(j)
                               , rsMd.getColumnDisplaySize(j)
                               , rsMd.getPrecision(j)
                               , rsMd.getScale(j)
                               , rsMd.isAutoIncrement(j)
                               , rsMd.isNullable(j)
                               );
	 _columns.add(col);
      }
   }

   /** 
    * ... */
   public Vector<DbToolsColumn> getColumns() {
      return(_columns);
   }

   /** 
    * ... */
   public DbToolsColumn getColumn(String columnName) {
      DbToolsColumn rv = null;
      //-
      for (DbToolsColumn col: getColumns()){
         String name  = col.getName();
         if (columnName.equals(name)) {
            rv=col;
         }
      }
      return(rv);
   }


   /** 
    * Returns a string representation of this object
    */
   public String toString() {
      String rv = "Table.["+getName()+"].[";
      //-
      boolean fg_first = true;
      for (DbToolsColumn col: getColumns()){
         if (fg_first) {
            fg_first = false;
         } else {
            rv = rv + ", ";
         }
         rv = rv + col;
      }
      rv= rv + "]";
      return(rv);
   }


   private String _name     = "";
   private String _nameOrig = "";
   private Vector<DbToolsColumn> _columns = new Vector<DbToolsColumn>();

}
