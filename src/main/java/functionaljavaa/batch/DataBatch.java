/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.batch;

import lbplanet.utilities.LPPlatform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class DataBatch {
    String classVersion = "0.1";
    String tableName = "batch_java";
    /**
     *
     * @param schemaName
     * @param transac
     * @param batchArray
     * @return
     */
    public String zdbCreateBatchArray( String schemaName, String transac, BatchArray batchArray){        

        //Integer td[][]= {{4, 17, 28, 38, 43, 58, 69, 77, 83}, {4, 12, 24, 35, 48, 55, 62, 73, 87}, {11,15, 22, 36, 46, 60, 67, 80, 84}};
        List<String> singleDArray = new ArrayList<>();
        for (String[] array :batchArray.batchPosic) {         
              singleDArray.addAll(Arrays.asList(array));
        }       
        return LPPlatform.LAB_FALSE+"notImplementedYet";
        
/*        Object[] insertRecordInTable = Rdbms.insertRecordInTable(schemaName, tableName, 
                                                new String[]{"name, template, template_version, array_num_rows,"
                                                    + "array_num_cols, array_total_positions, array_total_objects"},
                                                new Object [] {batchArray.getBatchName(), batchArray.getBatchTemplate(), batchArray.getBatchTemplateVersion(), batchArray.getNumRows(),
                                                    + batchArray.getNumCols(), batchArray.getNumTotalObjects(), batchArray.getNumTotalObjects()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(insertRecordInTable[0].toString())){return insertRecordInTable[0].toString();}
        return LPPlatform.LAB_TRUE+"Added to the database";*/
        
    }

    /**
     *
     * @param schemaName
     * @param batchArray
     * @return
     */
    public Object[] zdbCreateBatchArray( String schemaName, BatchArray batchArray)
    {
        //Integer td[][]= {{4, 17, 28, 38, 43, 58, 69, 77, 83}, {4, 12, 24, 35, 48, 55, 62, 73, 87}, {11,15, 22, 36, 46, 60, 67, 80, 84}};
        List<String> singleDArray = new ArrayList<>();
        for (String[] array :batchArray.batchPosic) {         
              singleDArray.addAll(Arrays.asList(array));
        } 
        return new Object[]{LPPlatform.LAB_FALSE, "notImplementedYet", null};
/*        return Rdbms.insertRecordInTable(schemaName, tableName, 
                                                new String[]{"name, template, template_version, array_num_rows,"
                                                    + "array_num_cols, array_total_positions, array_total_objects"},
                                                new Object [] {batchArray.getBatchName(), batchArray.getBatchTemplate(), batchArray.getBatchTemplateVersion(), batchArray.getNumRows(),
                                                    + batchArray.getNumCols(), batchArray.getNumTotalObjects(), batchArray.getNumTotalObjects()});    
        */
    }
    
    /**
     *
     * @param schemaName
     * @param batchName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public Integer zdbUpdateBatchArray( String schemaName, String batchName, String fieldName, String fieldValue) {
        
        Integer pk = -999;
       
//        Object[] updateRecordFieldsByFilter = Rdbms.updateRecordFieldsByFilter(schemaName, tableName, 
//                new String[]{fieldName}, new Object[]{fieldValue}, 
//                new String[]{"name"}, new Object[]{batchName});
//        pk = Integer.parseInt(updateRecordFieldsByFilter[6].toString());
        return pk; 
    }    
}
