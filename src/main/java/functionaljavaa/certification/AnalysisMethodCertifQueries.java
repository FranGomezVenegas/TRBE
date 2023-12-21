package functionaljavaa.certification;

import databases.TblsData;
import static functionaljavaa.certification.AnalysisMethodCertif.isUserCertificationEnabled;
import lbplanet.utilities.LPPlatform;
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
    
    
}
