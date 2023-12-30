package functionaljavaa.certification;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.TblsCnfg;
import databases.TblsData;
import static functionaljavaa.certification.AnalysisMethodCertif.isUserCertificationEnabled;
import static functionaljavaa.user.UserProfile.getProcedureUsers;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class AnalysisMethodCertifQueries {
    private AnalysisMethodCertifQueries() {throw new IllegalStateException("Utility class");}
    public static Object[] analysisMethodCertifiedUsersList(String methodName, String[] fieldsToRetrieve, String[] fieldsToSort){
        return analysisMethodCertifiedUsersList(methodName, null, fieldsToRetrieve, fieldsToSort);
    }
    
    public static Object[] analysisMethodCertifiedUsersList(String methodName, Integer methodVersion, String[] fieldsToRetrieve, String[] fieldsToSort){
        if (fieldsToRetrieve==null)
            fieldsToRetrieve=new String[]{TblsData.CertifUserAnalysisMethod.USER_NAME.getName()};
        if (fieldsToSort==null)
            fieldsToSort=new String[]{TblsData.CertifUserAnalysisMethod.USER_NAME.getName()};
        AnalysisMethodCertif.uncertifyExpiredOnes();
        InternalMessage userCertificationEnabled = isUserCertificationEnabled();
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(userCertificationEnabled.getDiagnostic())){
            Object[][] returnObj=new Object[][]{{userCertificationEnabled}};
            return new Object[]{fieldsToRetrieve, returnObj};
        }
        String[] whereFldName=new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.LIGHT.getName()};
        Object[] whereFldValue=new Object[]{methodName, "GREEN"};
        return new Object[]{fieldsToRetrieve, QueryUtilitiesEnums.getTableData(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, 
            EnumIntTableFields.getTableFieldsFromString(TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD, fieldsToRetrieve),
            whereFldName, whereFldValue, fieldsToSort)};
        //return new Object[][]{{LPPlatform.LAB_FALSE.toString()}, {LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "notImplementedYet", null)}};
    }
    
    public static JSONArray methodsByUser(String procInstanceName) {
        Object[] procedureUsers = getProcedureUsers(procInstanceName, null);

        Object[][] methodsList = QueryUtilitiesEnums.getTableData(TblsCnfg.TablesConfig.METHODS,
EnumIntTableFields.getAllFieldNamesFromDatabase(TblsCnfg.TablesConfig.METHODS, procInstanceName),
            new String[]{TblsCnfg.Methods.CODE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
            new Object[]{""},
            new String[]{TblsCnfg.Methods.CODE.getName() + SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()}, procInstanceName);
        JSONArray jArr = new JSONArray();
        if (Boolean.TRUE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(methodsList[0][0].toString()))) {
            return jArr;
        }
        String[] roleActionsFldsArr = new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName(), TblsData.CertifUserAnalysisMethod.USER_NAME.getName(), TblsData.CertifUserAnalysisMethod.SOP_NAME.getName(), TblsData.CertifUserAnalysisMethod.STATUS.getName()};
        Object[][] methodUsersCertifInfo = Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getRepositoryName()), TblsData.TablesData.CERTIF_USER_ANALYSIS_METHOD.getTableName(),
                new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{""}, roleActionsFldsArr,
                new String[]{TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName()}, true);
        JSONArray rolesActionsOutput = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(methodUsersCertifInfo[0][0].toString()))) {            
            JSONArray header = new JSONArray();
            JSONObject fldDef = new JSONObject();
            fldDef.put("label", "Methods / Users");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName()+"_en");
            header.put(fldDef);
            fldDef = new JSONObject();
            fldDef.put("label", "Métodos / Usuarios");
            fldDef.put("is_translation", true);
            fldDef.put("name", TblsData.CertifUserAnalysisMethod.METHOD_NAME.getName()+"_es");
            header.put(fldDef);
            for (Object curUser : procedureUsers) {
                header.put(curUser.toString());
            }
            rolesActionsOutput.put(header);
            String prevExecuted = "";
            for (Object[] curMethod : methodsList) {
                JSONArray curActionRow = new JSONArray();
                curActionRow.put(curMethod[0].toString());
                curActionRow.put(curMethod[0].toString());
                for (int i=0; i<procedureUsers.length;i++) {
                    int[] valuePosicArray2D = LPArray.valuePosicArray2D(methodUsersCertifInfo, new Object[][]{
                        {0, curMethod[0].toString()}, {1, procedureUsers[i].toString()}});
                    if (valuePosicArray2D.length>0){
                        curActionRow.put(methodUsersCertifInfo[valuePosicArray2D[0]][3]);
                    }else{
                        curActionRow.put("");
                    }
                }
                rolesActionsOutput.put(curActionRow);
            }
        }
        return rolesActionsOutput;
    }
    
    
    
}
