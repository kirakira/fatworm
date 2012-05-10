package fatworm.query;

import java.util.Collection;

import fatworm.dataentity.DataEntity;
/**
 * The interface will be implemented by each query scan.
 * There is a Scan class for each relational
 * algebra operator.
 */

public interface Scan {
   
   /**
    * Positions the scan before its first record.
    */
   public void     beforeFirst();
   
   /**
    * Moves the scan to the next record.
    * @return false if there is no next record
    */
   public boolean  next();
   
   
   // /**
   //  * Returns the value of the specified field in the current record.
   //  * The value is expressed as a Constant.
   //  * @param fldname the name of the field
   //  * @return the value of that field, expressed as a Constant.
   //  */
   // public Constant getVal(String fldname);
   
   /**
    * Returns the value of the specified integer field 
    * in the current record.
    * @param fldname the name of the field
    * @return the field's integer value in the current record
    */
   public DataEntity   getField(String fldname);
   
   /**
    * Returns true if the scan has the specified field.
    * @param fldname the name of the field
    * @return true if the scan has that field
    */
   public boolean  hasField(String fldname);

   /**
    * Returns true if the scan has the specified column, which may be table.field or field or FUNC(colname).
    * @param colname the name of the field
    * @return true if the scan has that field
    */
   public boolean  hasColumn(String colname);

   /**
    * Returns fields of the table.
    * @return a collection contains the field.
    */
   public Collection<String>  fields();

   /**
    * Returns columns of the table. table.field or FUNC(table.field).
    * @return a collection contains the columns.
    */
   public Collection<String>  columns();

    /**
     *Return the first column of the record, using for "in", "any", "all" comparision.
     */
    
    public DataEntity getFirstColumn();
}
