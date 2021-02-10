/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.queries;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Rdbms;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPKPIs;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class QueryUtilities {
    private QueryUtilities() {throw new IllegalStateException("Utility class");}
    
    public static String[] getFieldsListToRetrieve(String fldToRetrieve, String[] tableAllFields){
        String[] fieldsToRetrieve=tableAllFields;
        if (!(fldToRetrieve==null || fldToRetrieve.length()==0 || "ALL".equalsIgnoreCase(fldToRetrieve)))
            fieldsToRetrieve=fldToRetrieve.split("\\|");
        return fieldsToRetrieve;
    }
    public static Object[][] getTableData(String schema, String tableName, String fldToRetrieve, String[] tableAllFields, String[] whereFldName, Object[] whereFldValue, String[] orderBy){
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);        
        String[] fieldsToRetrieve=getFieldsListToRetrieve(fldToRetrieve, tableAllFields);
        Object[][] matCertifInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), schema), 
                tableName, whereFldName, whereFldValue, fieldsToRetrieve, orderBy);
        return matCertifInfo;
    }    
    
    public static JSONObject getKPIInfoFromRequest(HttpServletRequest request, String extraGrouperFieldName, String extraGrouperFieldValues){
        String[] programKPIGroupNameArr = new String[0];
        String programKPIGroupName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_OBJ_GROUP_NAME);   
        if (programKPIGroupName!=null) 
            programKPIGroupNameArr = programKPIGroupName.split("\\/");
        String[] programKPITableCategoryArr = new String[0];
        String programKPITableCategory = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TABLE_CATEGORY);   
        if (programKPITableCategory!=null) 
            programKPITableCategoryArr = programKPITableCategory.split("\\/");
        String[] programKPITableNameArr = new String[0];
        String programKPITableName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TABLE_NAME);   
        if (programKPITableName!=null) 
            programKPITableNameArr = programKPITableName.split("\\/");
        String[] programKPIWhereFieldsNameArr = new String[0];
        String programKPIWhereFieldsName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_NAME);   
        if (programKPIWhereFieldsName!=null) 
            programKPIWhereFieldsNameArr = programKPIWhereFieldsName.split("\\/");
        String[] programKPIWhereFieldsValueArr = new String[0];
        String programKPIWhereFieldsValue = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_WHERE_FIELDS_VALUE);   
        if (programKPIWhereFieldsValue!=null) 
            programKPIWhereFieldsValueArr = programKPIWhereFieldsValue.split("\\/");
        String[] programKPIRetrieveOrGroupingArr = new String[0];
        String programKPIRetrieveOrGrouping = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FIELDS_TO_RETRIEVE_OR_GROUPING);   
        if (programKPIRetrieveOrGrouping!=null) 
            programKPIRetrieveOrGroupingArr = programKPIRetrieveOrGrouping.split("\\/");
        String[] programKPIGroupedArr = new String[0];
        String programKPIGrouped = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_GROUPED);   
        if (programKPIGrouped!=null) 
            programKPIGroupedArr = programKPIGrouped.split("\\/");    
        JSONObject programkpIsObj = new JSONObject();
        if (programKPIWhereFieldsName!=null && programKPIWhereFieldsValue!=null){
            String[] curProgramKPIWhereFieldsNameArr = programKPIWhereFieldsName.split("\\/");
            String[] curProgramKPIWhereFieldsValueArr = programKPIWhereFieldsValue.split("\\/");
            for (int i=0;i<curProgramKPIWhereFieldsNameArr.length;i++){
                curProgramKPIWhereFieldsNameArr[i]=curProgramKPIWhereFieldsNameArr[i]+"|"+extraGrouperFieldName;
                curProgramKPIWhereFieldsValueArr[i]=curProgramKPIWhereFieldsValueArr[i]+"|"+extraGrouperFieldValues;
            }
            programkpIsObj = LPKPIs.getKPIs(programKPIGroupNameArr, programKPITableCategoryArr, programKPITableNameArr, 
                    curProgramKPIWhereFieldsNameArr, curProgramKPIWhereFieldsValueArr, programKPIRetrieveOrGroupingArr, programKPIGroupedArr);
        }
        return programkpIsObj;
    }
    
}
