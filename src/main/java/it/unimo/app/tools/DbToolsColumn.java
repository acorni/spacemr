package it.unimo.app.tools;


import java.util.*;
import java.sql.*;
import java.io.*;

import javax.sql.DataSource;

import java.lang.reflect.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class DbToolsColumn {

   /** 
    * ... */
   public DbToolsColumn(String name
                        , int typeId
                        , int columnDisplaySize
                        , int precision
                        , int scale
                        , boolean isAutoIncrement
                        , int nullable
                        ) throws Exception {
      setName(name);
      setTypeId(typeId);
      _columnDisplaySize = columnDisplaySize;
      _precision         = precision;
      _scale             = scale;
      _isAutoIncrement   = isAutoIncrement;
      _nullable          = nullable;
   }



   public String getName() {
      return(_name);
   }

   public void setName(String name) {
      _name = name;
   }

   public String getJavaType() {
      return(selectJavaType(getTypeId()));
   }

   public String getSqlType() {
      return(selectSqlType(getTypeId()));
   }

   public int getTypeId() {
      return(_typeId);
   }

   public void setTypeId(int typeId) throws Exception {
      _typeId = typeId;
   }


   public String toString() {
      String rv = "Col.["+getName()+"]";
      return(rv);
   }

   public int getColumnDisplaySize(){
      return(_columnDisplaySize);
   }
   public void setColumnDisplaySize(int v ){
      _columnDisplaySize = v;
   }
   public int getPrecision(){
      return(_precision);
   }
   public void setPrecision(int v ){
      _precision = v;
   }
   public int getScale(){
      return(_scale);
   }
   public void setScale(int v ){
      _scale = v;
   }
   public boolean getIsAutoIncrement(){
      return(_isAutoIncrement);
   }
   public void setIsAutoIncrement(boolean v ){
      _isAutoIncrement = v;
   }
   public int getNullable(){
      return(_nullable);
   }
   public void setNullable(int v ){
      _nullable = v;
   }


   /** 
    * Returns the string that represents the
    * name (but the prefix import/export) of the
    * function to use to handle the givent type
    */
   private static String selectJavaType(int colType)  {
      String rv = null;
      switch (colType) {
         //-
         //-
         //- Lob, blob (by inpustream!)
      case Types.BLOB:
      case Types.BINARY:
      case Types.VARBINARY:
      case Types.JAVA_OBJECT:
      case Types.LONGVARBINARY:
         //- rv = "AsBinaryStream";
         rv = "String";
         break;
         //-
         //- Big Characters fields
      case Types.CLOB:
      case Types.LONGVARCHAR:
         rv = "String";
         //- rv = "AsAsciiStream";
         break;
         //-
         //- Long
      case Types.BIGINT:
         rv = "int";
         break;
         //-
         //- Int
      case Types.INTEGER:
      case Types.SMALLINT:
      case Types.TINYINT:
         rv = "int";
         break;
         //-
         //- Booleans
      case Types.BIT:
         rv = "boolean";
         break;
         //-
         //- Strings
      case Types.CHAR:
      case Types.VARCHAR:
         rv = "String";
         break;
         //-
         //- Timestamp
      case Types.TIME:
      case Types.TIMESTAMP:
         rv = "Timestamp";
         break;
         //-
         //- Date
      case Types.DATE:
         rv = "Date";
         break;
         //-
         //- java.math.BigDecimal
      case Types.DECIMAL:
      case Types.DOUBLE:
      case Types.FLOAT:
      case Types.NUMERIC:
      case Types.REAL:
         rv = "double";
         break;
         //-
         //- Unsupported
         //-
      case Types.ARRAY:
         rv = " - unsupported type ARRAY";
         break;
      case Types.DISTINCT:
         rv = " - unsupported type DISTINCT";
         break;
      case Types.NULL:
         rv = " - unsupported type NULL";
         break;
      case Types.OTHER:
         rv = " - unsupported type OTHER";
         break;
      case Types.REF:
         rv = " - unsupported type REF";
         break;
      case Types.STRUCT:
         rv = " - unsupported type STRUCT";
         break;
      default:
         //- rv = "UNSUPPORTED";
         //- return NULL!
         rv = " - unsupported type with code " + colType ;
         break;
      }
      return(rv);
   }


   /** 
    * Returns the string that represents the
    * name (but the prefix import/export) of the
    * function to use to handle the givent type
    */
   private static String selectSqlType(int colType)  {
      String rv = null;
      switch (colType) {
      case Types.BLOB:
         rv = "BLOB";
         break;
      case Types.BINARY:
         rv = "BINARY";
         break;
      case Types.VARBINARY:
         rv = "VARBINARY";
         break;
      case Types.JAVA_OBJECT:
         rv = "JAVA_OBJECT";
         break;
      case Types.LONGVARBINARY:
         rv = "LONGVARBINARY";
         break;
         //-
         //- Big Characters fields
      case Types.CLOB:
         rv = "CLOB";
         break;
      case Types.LONGVARCHAR:
         rv = "LONGVARCHAR";
         break;
      case Types.BIGINT:
         rv = "BIGINT";
         break;
      case Types.INTEGER:
         rv = "INTEGER";
         break;
      case Types.SMALLINT:
         rv = "SMALLINT";
         break;
      case Types.TINYINT:
         rv = "TINYINT";
         break;
      case Types.BIT:
         rv = "BIT";
         break;
      case Types.CHAR:
         rv = "CHAR";
         break;
      case Types.VARCHAR:
         rv = "VARCHAR";
         break;
      case Types.TIME:
         rv = "TIME";
         break;
      case Types.TIMESTAMP:
         rv = "TIMESTAMP";
         break;
      case Types.DATE:
         rv = "DATE";
         break;
      case Types.DECIMAL:
         rv = "DECIMAL";
         break;
      case Types.DOUBLE:
         rv = "DOUBLE";
         break;
      case Types.FLOAT:
         rv = "FLOAT";
         break;
      case Types.NUMERIC:
         rv = "NUMERIC";
         break;
      case Types.REAL:
         rv = "REAL";
         break;
      case Types.ARRAY:
         rv = "ARRAY";
         break;
      case Types.DISTINCT:
         rv = "DISTINCT";
         break;
      case Types.NULL:
         rv = "NULL";
         break;
      case Types.OTHER:
         rv = "OTHER";
         break;
      case Types.REF:
         rv = "REF";
         break;
      case Types.STRUCT:
         rv = "STRUCT";
         break;
      default:
         rv = "UNSUPPORTED";
         break;
      }
      return(rv);
   }



   private String  _name   = "";
   private int     _typeId;
   private int     _columnDisplaySize = -1;
   private int     _precision = -1;
   private int     _scale = -1;
   private boolean _isAutoIncrement = false;
   private int     _nullable = -1;

}