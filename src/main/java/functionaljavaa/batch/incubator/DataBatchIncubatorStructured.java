/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.batch.incubator;

import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitConfig;
import com.labplanet.servicios.moduleenvmonit.TblsEnvMonitData;
import databases.Rdbms;
import databases.TblsData;
import functionaljavaa.audit.IncubBatchAudit;
import functionaljavaa.audit.SampleAudit;
import functionaljavaa.samplestructure.DataSampleIncubation;
import java.math.BigDecimal;
import java.util.Objects;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.session.ProcedureRequestSession;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class DataBatchIncubatorStructured {
    
    public static final String BATCHCONTENTSEPARATORSTRUCTUREDBATCH="<>";
    public static final String POSITIONVALUESEPARATORSTRUCTUREDBATCH="*";
    public static final String BATCHCONTENTEMPTYPOSITIONVALUE="-";
    
    static Boolean batchIsEmptyStructured(String batchName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName()});
        return (!LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(batchInfo[0][0]).toString())) && ("0".equals(LPNulls.replaceNull(batchInfo[0][0]).toString()));
    }
    
    public static Object[][] dbGetBatchArray(String batchName){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        return Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
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
    
    static Object[] createBatchStructured(String bName, Integer bTemplateId, Integer bTemplateVersion, String[] fldName, Object[] fldValue) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
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
        Object[][] templateDefInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsEnvMonitConfig.IncubBatch.TBL.getName(), 
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
        Object[] createBatchDiagn = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), fldName, fldValue);
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(createBatchDiagn[0].toString())) {
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.BatchAuditEvents.BATCH_CREATED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), bName, LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, ":"), null);
        }
        return createBatchDiagn;        
    }

    static Object[] batchAddSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override) {
        return batchAddSampleStructured(batchName, sampleId, pendingIncubationStage, row, col, override, false);
    }
    static Object[] batchAddSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override, Boolean byMovement) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        if ((row==null)||(col==null))return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "In a Structured batch position row and col are mandatory and by pos <*1*> and col <*2*> is a wrong coordinate.", new Object[]{LPNulls.replaceNull(row), LPNulls.replaceNull(col)});
        Object[] batchSampleIsAddable = batchSampleIsAddable(batchName, sampleId, pendingIncubationStage, row, col, override, byMovement);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchSampleIsAddable[0].toString())) return batchSampleIsAddable;

        String[] batchFldsToRetrieve= new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, 
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) return LPArray.array2dTo1d(batchInfo);
        Integer batchNumRows=(Integer) batchInfo[0][0];
        Integer batchNumCols=(Integer) batchInfo[0][1];
        Integer batchTotalObjects=LPNulls.replaceNull(batchInfo[0][3]).toString().length()==0 ? 0 : Integer.valueOf(batchInfo[0][3].toString());
        String batchContentStr=batchInfo[0][4].toString();
        
        String[][] batchContent2D=new String[0][0];        
        if ((batchContentStr==null) || (batchContentStr.length()==0)){
            batchContent2D=new String[batchNumRows][0];
            for (int i=0;i<batchNumCols;i++)
                batchContent2D=LPArray.convertObjectArrayToStringArray(LPArray.addColumnToArray2D(batchContent2D, BATCHCONTENTEMPTYPOSITIONVALUE));
        }else{
            String[] batchContent1D=batchContentStr.split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            batchContent2D=LPArray.array1dTo2d(batchContent1D, batchNumCols);
        }

        batchContent2D[row-1][col-1]=buildBatchPositionValue(sampleId, pendingIncubationStage);
        batchContentStr=LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), BATCHCONTENTSEPARATORSTRUCTUREDBATCH, "");        
        if (byMovement!=null && !byMovement) batchTotalObjects++;
        String[] updFieldName = new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[] updFieldValue = new Object[]{batchTotalObjects, batchContentStr};
        
        Object[] updateBatchContentDiagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                updFieldName, updFieldValue, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});
        
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchContentDiagn[0].toString())) {
            if (byMovement!=null && !byMovement) 
                IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_ADDED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
            else
                IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_MOVED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
        }
        String batchFldName = "";
        if (null == pendingIncubationStage) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, procInstanceName});
        } else switch (pendingIncubationStage) {
            case 1:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName();
                break;
            case 2:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName();
                break;
            default:
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, procInstanceName});
        }
        Object[] updateSampleInfo = Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), new String[]{batchFldName}, new Object[]{batchName}, new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSampleInfo[0].toString())) {
            SampleAudit smpAudit = new SampleAudit();       
            if (byMovement!=null && !byMovement) 
                smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.BATCH_SAMPLE_ADDED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
            else
                smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.BATCH_SAMPLE_MOVED_TO.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
        }
        return updateSampleInfo;
    }
    private static Object[] batchSampleIsAddable(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer row, Integer col, Boolean override, Boolean byMovement){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] batchFldsToRetrieve= new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_ROWS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName()
                    , TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, 
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) return LPArray.array2dTo1d(batchInfo);
        Integer batchNumRows=(Integer) batchInfo[0][0];
        Integer batchNumCols=(Integer) batchInfo[0][1];
        String batchContentStr=batchInfo[0][4].toString();
        
        String[][] batchContent2D=new String[0][0];        
        if (row>batchNumRows) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "This row, <*1*>, is greater than the batch total rows, <*2*> for batch <*3*> in procedure <*4*>"
                , new Object[]{row, batchNumRows, batchName, procInstanceName});
        if (col>batchNumCols) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "This col, <*1*>, is greater than the batch total columns, <*2*> for batch <*3*> in procedure <*4*>"
                , new Object[]{col, batchNumCols, batchName, procInstanceName});
        if ((batchContentStr==null) || (batchContentStr.length()==0)){
            batchContent2D=new String[batchNumRows][0];
            for (int i=0;i<batchNumCols;i++)
                batchContent2D=LPArray.convertObjectArrayToStringArray(LPArray.addColumnToArray2D(batchContent2D, BATCHCONTENTEMPTYPOSITIONVALUE));
        }else{
            String[] batchContent1D=batchContentStr.split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
            batchContent2D=LPArray.array1dTo2d(batchContent1D, batchNumCols);
        }
        
        String posicContent=batchContent2D[row-1][col-1];
        if ((LPNulls.replaceNull(posicContent).length()>0) && (!BATCHCONTENTEMPTYPOSITIONVALUE.equalsIgnoreCase(LPNulls.replaceNull(posicContent))) ){
            if (!override) return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The position <*1*>.<*2*> is occupied for batch <*3*> in procedure <*4*>"
                , new Object[]{row, col, batchName, procInstanceName});
        }        
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
    }

    static Object[] batchMoveSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Integer newRow, Integer newCol, Boolean override) {
        Object[] sampleAddable=batchSampleIsAddable(batchName, sampleId, pendingIncubationStage, newRow, newCol, override, true);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleAddable[0].toString())) return sampleAddable;
        Object[] moveDiagn=batchRemoveSampleStructured(batchName, sampleId, pendingIncubationStage, true);       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moveDiagn[0].toString())) return moveDiagn;
        return batchAddSampleStructured(batchName, sampleId, pendingIncubationStage, newRow, newCol, override, true);
    }
    
    static Object[] batchRemoveSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage) {
        return batchRemoveSampleStructured(batchName, sampleId, pendingIncubationStage, false);   
    }
    static Object[] batchRemoveSampleStructured(String batchName, Integer sampleId, Integer pendingIncubationStage, Boolean byMovement) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] batchFldsToRetrieve= new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_NUM_COLS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_POSITIONS.getName(), 
            TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] batchInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, 
                new Object[]{batchName}, batchFldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(batchInfo[0][0].toString())) return LPArray.array2dTo1d(batchInfo);
        Integer batchNumCols=(Integer) batchInfo[0][0];
        Integer batchTotalObjects=LPNulls.replaceNull(batchInfo[0][2]).toString().length()==0 ? 0 : Integer.valueOf(batchInfo[0][2].toString());
        String batchContentStr=batchInfo[0][3].toString();
        String positionValueToFind=buildBatchPositionValue(sampleId, pendingIncubationStage);

        if ((batchContentStr==null) || (batchContentStr.length()==0))
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The batch <*1*> is empty in procedure <*2*>", new Object[]{batchName, procInstanceName});
        
        String[] batchContent1D=batchContentStr.split(BATCHCONTENTSEPARATORSTRUCTUREDBATCH);
        Integer valuePosition=LPArray.valuePosicInArray(batchContent1D, positionValueToFind);
        if (valuePosition==-1)
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The sample <*1*> is not part of the batch <*2*> in procedure <*3*>", new Object[]{sampleId, batchName, procInstanceName});
        batchContent1D[valuePosition]="";  
        String[][] batchContent2D=LPArray.array1dTo2d(batchContent1D, batchNumCols);
        batchContentStr=LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), BATCHCONTENTSEPARATORSTRUCTUREDBATCH, "");        
        if (byMovement!=null && !byMovement) batchTotalObjects--;
        String[] updFieldName=new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_TOTAL_OBJECTS.getName(), TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[] updFieldValue=new Object[]{batchTotalObjects, batchContentStr};
        Object[] updateBatchContentDiagn=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), 
                updFieldName, updFieldValue, 
                new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName});       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(updateBatchContentDiagn[0].toString())) 
            return updateBatchContentDiagn;
        if (byMovement==null || !byMovement)         
            IncubBatchAudit.incubBatchAuditAdd(DataBatchIncubator.BatchAuditEvents.BATCH_SAMPLE_REMOVED.toString(), TblsEnvMonitData.IncubBatch.TBL.getName(), batchName, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
        
        String batchFldName = "";
        if (null == pendingIncubationStage) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, procInstanceName});
        } else switch (pendingIncubationStage) {
            case 1:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION_BATCH.getName();
                break;
            case 2:
                batchFldName = TblsEnvMonitData.Sample.FLD_INCUBATION2_BATCH.getName();
                break;
            default:
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " Incubation stage <*1*> is not 1 or 2 therefore not recognized for procedure <*2*>.", new Object[]{pendingIncubationStage, procInstanceName});
        }
        Object[] updateSampleInfo=Rdbms.updateRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.Sample.TBL.getName(), new String[]{batchFldName}, new Object[]{null}, new String[]{TblsEnvMonitData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{sampleId});
        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(updateSampleInfo[0].toString())) {
            SampleAudit smpAudit = new SampleAudit();       
            if (byMovement!=null && !byMovement) 
                smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.BATCH_SAMPLE_REMOVED.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
            else{
                updFieldName=LPArray.addValueToArray1D(updFieldName, "row");
                updFieldName=LPArray.addValueToArray1D(updFieldName, "col");                
                updFieldValue=LPArray.addValueToArray1D(updFieldValue, (valuePosition/batchNumCols)+1);
                updFieldValue=LPArray.addValueToArray1D(updFieldValue, (valuePosition%batchNumCols)+1);                
                smpAudit.sampleAuditAdd(SampleAudit.SampleAuditEvents.BATCH_SAMPLE_MOVED_FROM.toString(), TblsData.Sample.TBL.getName(), sampleId, sampleId, null, null, LPArray.joinTwo1DArraysInOneOf1DString(updFieldName, updFieldValue, ":"), null);
            }
        }
        return updateSampleInfo;
    }

    static Object[] batchSampleIncubStartedStructured() {
        return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "batchSampleIncubStartedStructured not implemented yet", null);
    }    
    static Object[] batchSampleIncubStartedStructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();

        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        if (batchSamples.length() == 0) {
            return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The batch <*1*> has no samples therefore cannot be started yet, procedure <*2*>", new Object[]{batchName, procInstanceName});
        }
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " batchSampleIncubStartedUnstructured cannot parse the info for the Sample <*1*> when there are more than 2 pieces of info. Batch Samples info is <*2*> for procedure <*3*>.", new Object[]{currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubStarted = DataSampleIncubation.setSampleStartIncubationDateTime(sampleId, incubStage, incubName, tempReading);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(setSampleIncubStarted[0].toString())) {
                return setSampleIncubStarted;
            }
        }
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "All sample set as incubation started", null);
    }
    
    static Object[] batchSampleIncubEndedStructured(String batchName, String incubName) {
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        
        String[] sampleInfoFieldsToRetrieve = new String[]{TblsEnvMonitData.IncubBatch.FLD_STRUCT_CONTENT.getName()};
        Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsEnvMonitData.IncubBatch.TBL.getName(), new String[]{TblsEnvMonitData.IncubBatch.FLD_NAME.getName()}, new Object[]{batchName}, sampleInfoFieldsToRetrieve);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(sampleInfo[0][0].toString())) {
            return LPArray.array2dTo1d(sampleInfo);
        }
        String batchSamples = LPNulls.replaceNull(sampleInfo[0][0]).toString();
        String[] batchSamplesArr = batchSamples.split("\\|");
        for (String currSample : batchSamplesArr) {
            String[] currSampleArr = currSample.split("\\*");
            if (currSampleArr.length != 2) {
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, " batchSampleIncubEndedUnstructured cannot parse the info for the Sample <*1*> when there are more than 2 pieces of info. Batch Samples info is <*2*> for procedure <*3*>.", new Object[]{currSample, batchSamples, procInstanceName});
            }
            Integer sampleId = Integer.valueOf(currSampleArr[0]);
            Integer incubStage = Integer.valueOf(currSampleArr[1]);
            BigDecimal tempReading = null;
            Object[] setSampleIncubEnded = DataSampleIncubation.setSampleEndIncubationDateTime(sampleId, incubStage, incubName, tempReading);
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
    public static String setLinesNameNOUSADO(String[] names, Integer numRows){
        //String[] linesName = new String[numRows];
        String valuesSeparator=BATCHCONTENTSEPARATORSTRUCTUREDBATCH;
        StringBuilder linesName=new StringBuilder();
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
                if (linesName.length()>0)linesName.append(valuesSeparator);
                linesName.append(currPrefixBuilder.toString()+alphabet[inumLetAlphabet]);
                inumLet++;
                inumLetAlphabet++;
            }            
        }else{
            for (String name : names) {
                if (linesName.length()>0)linesName.append(valuesSeparator);
                linesName.append(name);
            }            
//            if (linesName.length==names.length) linesName=names;
        }
        return linesName.toString(); //names;        
    }
    public static String setColumnsNameNOUSADO(String[] names, Integer numCols){
        //String[] columnsName=new String[numCols];
        String valuesSeparator=BATCHCONTENTSEPARATORSTRUCTUREDBATCH;
        StringBuilder columnsName=new StringBuilder();
        
        if (names==null){                                    
            Integer inumLet=1;
            //while (inumLet<=columnsName.length){                
            while (inumLet<=numCols){                
                if (columnsName.length()>0)columnsName.append(valuesSeparator);
                columnsName.append(inumLet);                
                //columnsName[inumLet-1]=inumLet.toString();
                inumLet++;
            }
        } else{
            for (String name : names) {
                if (columnsName.length()>0)columnsName.append(valuesSeparator);
                columnsName.append(name);                                
            }
//            if (columnsName.length==names.length) columnsName=names;            
        }
        return columnsName.toString(); //names;
    }    

}
