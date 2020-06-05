/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.batch.incubator;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.samplestructure.DataSampleIncubation;
import java.math.BigDecimal;
import java.util.Objects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class DataBatchIncubatorStructured {
    
    public static final String BATCHCONTENTSEPARATORSTRUCTUREDBATCH="<>";
    public static final String POSITIONVALUESEPARATORSTRUCTUREDBATCH="*";
    public static final String BATCHCONTENTEMPTYPOSITIONVALUE="-";
    
    static Boolean batchIsEmptyStructured(String schemaPrefix, Token token, String batchName){
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName()});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(batchInfo[0][0]).toString())){
            if ("0".equals(LPNulls.replaceNull(batchInfo[0][0]).toString())) return true;
        }
        return false;
    }
    
    public static Object[][] dbGetBatchArray(String schemaPrefix, String batchName){
        return Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_ROWS_NAME.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_COLS_NAME.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName()});
        
/*        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsByFilter[0][0].toString())){                    
            try {
                SerialArray rowNames = (SerialArray) recordFieldsByFilter[0][8];
                Object[] rowNamesArr = (Object[]) rowNames.getArray();
                SerialArray colNames = (SerialArray) recordFieldsByFilter[0][8];
                Object[] colNamesArr = (Object[]) colNames.getArray();                
                
                return new BatchArray(
                        LPNulls.replaceNull(recordFieldsByFilter[0][1].toString()),
                        (Integer) recordFieldsByFilter[0][2],  batchName,
                        "",
                        //(String) recordFieldsByFilter[0][3]==null ? "" : recordFieldsByFilter[0][3].toString(),
                        //Integer.valueOf(LPNulls.replaceNull((String)recordFieldsByFilter[0][4])),
                        (Integer) recordFieldsByFilter[0][4],
                        (Integer) recordFieldsByFilter[0][5],
                        (Object[]) rowNamesArr,
                        (Object[]) colNamesArr );
            } catch (SerialException ex) {
                Logger.getLogger(BatchArray.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;*/
    }
    
    static Object[] createBatchStructured(String schemaPrefix, Token token, String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue) {
        if(fldName==null)fldName=new String[0];
        if(fldValue==null)fldValue=new Object[0];
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateId);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName())] = bTemplateId;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bTemplateVersion);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_INCUB_BATCH_CONFIG_VERSION.getName())] = bTemplateVersion;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_NAME.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_NAME.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, bName);
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_NAME.getName())] = bName;
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, DataBatchIncubator.BatchIncubatorType.STRUCTURED.toString());
        } else {
            fldValue[LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_TYPE.getName())] = DataBatchIncubator.BatchIncubatorType.STRUCTURED.toString();
        }
        String[] templateFldsToPropagate= new String[]{TblsEnvMonitConfig.IncubBatch.FLD_STRUCT_NUM_ROWS.getName(), TblsEnvMonitConfig.IncubBatch.FLD_STRUCT_NUM_COLS.getName()
                    , TblsEnvMonitConfig.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitConfig.IncubBatch.FLD_STRUCT_ROWS_NAME.getName(), TblsEnvMonitConfig.IncubBatch.FLD_STRUCT_COLS_NAME.getName()};
        Object[][] templateDefInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_CONFIG), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_CONFIG_ID.getName(), TblsEnvMonitConfig.IncubBatch.FLD_INCUB_BATCH_VERSION.getName()}, 
                new Object[]{bTemplateId, bTemplateVersion}, templateFldsToPropagate);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(templateDefInfo[0][0].toString())) return LPArray.array2dTo1d(templateDefInfo);
        for (int i=0; i<templateFldsToPropagate.length;i++){
            fldName=LPArray.addValueToArray1D(fldName, templateFldsToPropagate[i]);
            fldValue=LPArray.addValueToArray1D(fldValue, templateDefInfo[0][i]);
        }
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_ACTIVE.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, true);
        }         
        if (LPArray.valuePosicInArray(fldName, TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName()) == -1) {
            fldName = LPArray.addValueToArray1D(fldName, TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName());
            fldValue = LPArray.addValueToArray1D(fldValue, 0);
        }                 
        Object[] createBatchDiagn = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), fldName, fldValue);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(createBatchDiagn[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(schemaPrefix, token, DataBatchIncubator.BatchAuditEvents.BATCH_CREATED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), bName, bName, LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, ":"), null);
        }
        return createBatchDiagn;        
    }

    static Object[] batchAddSampleStructured(String schemaPrefix, Token token, String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override) {
        return batchAddSampleStructured(schemaPrefix, token, batchName, sampleId, pendingIncubationStage, row, col, override, false);
    }
    static Object[] batchAddSampleStructured(String schemaPrefix, Token token, String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override, Boolean byMovement) {
        if ((row==null)||(col==null))return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "In a Structured batch position row and col are mandatory and by pos <*1*> and col <*2*> is a wrong coordinate.", new Object[]{LPNulls.replaceNull(row), LPNulls.replaceNull(col)});
        String[] batchFldsToRetrieve= new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, 
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) return LPArray.array2dTo1d(batchInfo);
        Integer batchNumRows=(Integer) batchInfo[0][0];
        Integer batchNumCols=(Integer) batchInfo[0][1];
        Integer batchTotalObjects=LPNulls.replaceNull(batchInfo[0][3]).toString().length()==0 ? 0 : Integer.valueOf(batchInfo[0][3].toString());
        String batchContentStr=batchInfo[0][4].toString();
        
        if (row>batchNumRows) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "This row, <*1*>, is greater than the batch total rows, <*2*> for batch <*3*> in procedure <*4*>"
                , new Object[]{row, batchNumRows, batchName, schemaPrefix});
        if (col>batchNumCols) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "This col, <*1*>, is greater than the batch total columns, <*2*> for batch <*3*> in procedure <*4*>"
                , new Object[]{col, batchNumCols, batchName, schemaPrefix});
        
        String[][] batchContent2D=new String[0][0];        
        if ((batchContentStr==null) || (batchContentStr.length()==0)){
            batchContent2D=new String[batchNumRows][0];
            for (int i=0;i<batchNumCols;i++)
                batchContent2D=LPArray.convertObjectArrayToStringArray(LPArray.addColumnToArray2D(batchContent2D, BATCHCONTENTEMPTYPOSITIONVALUE));
//            batchContent2D=new String[batchNumRows][batchNumCols];
//            batchContent1D=LPArray.array2dTo1d(batchContent2D);
        }else{
            String[] batchContent1D=batchContentStr.split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            batchContent2D=LPArray.array1dTo2d(batchContent1D, batchNumCols);
        }
        String posicContent=batchContent2D[row-1][col-1];
        //posicContent=LPNulls.replaceNull(posicContent);
        if ((LPNulls.replaceNull(posicContent).length()>0) && (!BATCHCONTENTEMPTYPOSITIONVALUE.equalsIgnoreCase(LPNulls.replaceNull(posicContent))) ){
            if (!override) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The position <*1*>.<*2*> is occupied for batch <*3*> in procedure <*4*>"
                , new Object[]{row, col, batchName, schemaPrefix});
            else{
                
            }
        }
        batchContent2D[row-1][col-1]=buildBatchPositionValue(sampleId, pendingIncubationStage);
        batchContentStr=LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), BATCHCONTENTSEPARATORSTRUCTUREDBATCH, "");        
        if (byMovement!=null && !byMovement) batchTotalObjects++;
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchTotalObjects, batchContentStr};
        
        Object[] updateBatchContentDiagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                updFieldName, updFieldValue, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});
/*        String addType = ""; 
        if ( (row<=batchNumRows()) && (col<=batchNumCols())) {                               
            if (batchPosic[row-1][col-1] == null){
                ++this.numTotalObjects;
                addType = "Added Succesfully";
            } else {
                addType = "Content overrided";}
            batchPosic[row-1][col-1] = objId;
            return addType;
        } else {
            return "This position is out of the batch dimension"; }
*/        
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchContentDiagn[0].toString())) {
            if (byMovement!=null && !byMovement) 
                IncubBatchAudit.incubBatchAuditAdd(schemaPrefix, token, DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_ADDED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, sampleId.toString(), LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
            else
                IncubBatchAudit.incubBatchAuditAdd(schemaPrefix, token, DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_MOVED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, sampleId.toString(), LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
        }
        String batchFldName = "";
        if (null == pendingIncubationStage) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, schemaPrefix});
        } else switch (pendingIncubationStage) {
            case 1:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName();
                break;
            case 2:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName();
                break;
            default:
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, schemaPrefix});
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), new String[]{batchFldName}, new Object[]{batchName}, new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
    }

    static Object[] batchMoveSampleStructured(String schemaPrefix, Token token, String batchName, Integer sampleId, Integer pendingIncubationStage, Integer newRow, Integer newCol, Boolean override) {
        Object[] moveDiagn=batchAddSampleStructured(schemaPrefix, token, batchName, sampleId, pendingIncubationStage, newRow, newCol, override, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moveDiagn[0].toString())) return moveDiagn;
        return batchRemoveSampleStructured(schemaPrefix, token, batchName, sampleId, pendingIncubationStage, true);       
    }
    
    static Object[] batchRemoveSampleStructured(String schemaPrefix, Token token, String batchName, Integer sampleId, Integer pendingIncubationStage) {
        return batchRemoveSampleStructured(schemaPrefix, token, batchName, sampleId, pendingIncubationStage, false);   
    }
    static Object[] batchRemoveSampleStructured(String schemaPrefix, Token token, String batchName, Integer sampleId, Integer pendingIncubationStage, Boolean byMovement) {
        String[] batchFldsToRetrieve= new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), 
            TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, 
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) return LPArray.array2dTo1d(batchInfo);
        Integer batchNumCols=(Integer) batchInfo[0][0];
        Integer batchTotalObjects=LPNulls.replaceNull(batchInfo[0][2]).toString().length()==0 ? 0 : Integer.valueOf(batchInfo[0][2].toString());
        String batchContentStr=batchInfo[0][3].toString();
        String positionValueToFind=buildBatchPositionValue(sampleId, pendingIncubationStage);

        if ((batchContentStr==null) || (batchContentStr.length()==0))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The batch <*1*> is empty in procedure <*2*>", new Object[]{batchName, schemaPrefix});
        
        String[] batchContent1D=batchContentStr.split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
        Integer valuePosition=LPArray.valuePosicInArray(batchContent1D, positionValueToFind);
        if (valuePosition==-1)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The sample <*1*> is not part of the batch <*1*> in procedure <*2*>", new Object[]{sampleId, batchName, schemaPrefix});
        batchContent1D[valuePosition]="";  
        String[][] batchContent2D=LPArray.array1dTo2d(batchContent1D, batchNumCols);
        batchContentStr=LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), BATCHCONTENTSEPARATORSTRUCTUREDBATCH, "");        
        batchTotalObjects--;
        String[] updFieldName=new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[] updFieldValue=new Object[]{batchTotalObjects, batchContentStr};
        Object[] updateBatchContentDiagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                updFieldName, updFieldValue, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchContentDiagn[0].toString())) 
            return updateBatchContentDiagn;
        IncubBatchAudit.incubBatchAuditAdd(schemaPrefix, token, DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_REMOVED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, sampleId.toString(), LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
        
        String batchFldName = "";
        if (null == pendingIncubationStage) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, schemaPrefix});
        } else switch (pendingIncubationStage) {
            case 1:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName();
                break;
            case 2:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName();
                break;
            default:
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, schemaPrefix});
        }
        return Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.Sample.TBL.getName(), new String[]{batchFldName}, new Object[]{null}, new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
    }

    static Object[] batchSampleIncubStartedStructured() {
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "batchRemoveSampleStructured not implemented yet", null);
    }    
    static Object[] batchSampleIncubStartedStructured(String schemaPrefix, Token token, String batchName, String incubName) {
        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() == 0) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The batch <*1*> has no samples therefore cannot be started yet, procedure <*2*>", new Object[]{batchName, schemaPrefix});
        }
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " batchSampleIncubStartedUnstructured cannot parse the info for the Sample <*1*> when there are more than 2 pieces of info. Batch Samples info is <*2*> for procedure <*3*>.", new Object[]{currSample, batchSamples, schemaPrefix});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubStarted = DataSampleIncubation.setSampleStartIncubationDateTime(schemaPrefix, token, sampleId, incubStage, incubName, tempReading);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubStarted[0].toString())) {
                return setSampleIncubStarted;
            }
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "All sample set as incubation started", null);
    }
    
    static Object[] batchSampleIncubEndedStructured(String schemaPrefix, Token token, String batchName, String incubName) {
        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaPrefix, LPPlatform.SCHEMA_DATA), TblsEnvMonitData.IncubBatch.TBL.getName(), new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " batchSampleIncubEndedUnstructured cannot parse the info for the Sample <*1*> when there are more than 2 pieces of info. Batch Samples info is <*2*> for procedure <*3*>.", new Object[]{currSample, batchSamples, schemaPrefix});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubEnded = DataSampleIncubation.setSampleEndIncubationDateTime(schemaPrefix, token, sampleId, incubStage, incubName, tempReading);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubEnded[0].toString())) {
                return setSampleIncubEnded;
            }
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "All sample set as incubation ended", null);
    }   
    static Object[] batchSampleIncubEndedStructured() {
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "batchRemoveSampleStructured not implemented yet", null);
    }
    
    public static String buildBatchPositionValue(Integer sampleId, Integer pendingIncubationStage){
        if (pendingIncubationStage==null)return sampleId.toString()+POSITIONVALUESEPARATORSTRUCTUREDBATCH;
        return sampleId.toString()+POSITIONVALUESEPARATORSTRUCTUREDBATCH+pendingIncubationStage.toString();
    }
    private static String setLinesName(String[] names, Integer numRows){
        //String[] linesName = new String[numRows];
        String valuesSeparator=BATCHCONTENTSEPARATORSTRUCTUREDBATCH;
        String linesName="";
        if (names==null){
            char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();            
            //Integer numLet=alphabet.length;
            Integer inumLet=0;
            Integer inumLetAlphabet=0;
            StringBuilder currPrefixBuilder =new StringBuilder(0);
            //while (inumLet<linesName.length){
            while (inumLet<numRows){
                if (Objects.equals(inumLet, alphabet.length)){
                    currPrefixBuilder.append("A");
                    inumLetAlphabet=0;
                }
                //linesName[inumLet]=currPrefixBuilder.toString()+alphabet[inumLetAlphabet];
                if (linesName.length()>0)linesName=linesName+valuesSeparator;
                linesName=linesName+currPrefixBuilder.toString()+alphabet[inumLetAlphabet];
                inumLet++;
                inumLetAlphabet++;
            }            
        }else{
            for (String name : names) {
                if (linesName.length()>0)linesName=linesName+valuesSeparator;
                linesName = linesName + name;
            }            
//            if (linesName.length==names.length) linesName=names;
        }
        return linesName; //names;        
    }
    private static String setColumnsName(String[] names, Integer numCols){
        //String[] columnsName=new String[numCols];
        String valuesSeparator=BATCHCONTENTSEPARATORSTRUCTUREDBATCH;
        String columnsName="";
        
        if (names==null){                                    
            Integer inumLet=1;
            //while (inumLet<=columnsName.length){                
            while (inumLet<=numCols){                
                if (columnsName.length()>0)columnsName=columnsName+valuesSeparator;
                columnsName=columnsName+inumLet;                
                //columnsName[inumLet-1]=inumLet.toString();
                inumLet++;
            }
        } else{
            for (String name : names) {
                if (columnsName.length()>0)columnsName=columnsName+valuesSeparator;
                columnsName = columnsName + name;                                
            }
//            if (columnsName.length==names.length) columnsName=names;            
        }
        return columnsName; //names;
    }    

}
