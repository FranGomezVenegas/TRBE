/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module.projectrnd.definition;

import databases.Rdbms;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DataMethodDevSeq {
    private final String sequenceName;
    private String projectName;
    private Boolean isLocked;
    private String lockedReason;
    private String[] formulaFieldNames;
    private Object[] formulaFieldValues;
    private Boolean hasError;
    private InternalMessage errorDetail;
    private String[] ingredientsFieldNames;
    private Object[] ingredientsFieldValues;
    
    public DataMethodDevSeq(String sequenceName, String analyticalParameter, String projectName) {
        ProcedureRequestSession procReqSession = ProcedureRequestSession.getInstanceForActions(null, null, null);
        String procInstanceName = procReqSession.getProcedureInstance();        
        Object[][] projectRnDinfo = Rdbms.getRecordFieldsByFilter(procInstanceName,  LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.DATA.getName()), TblsProjectRnDData.TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE.getTableName(),
                new String[]{TblsProjectRnDData.MethodDevelopmentSequence.NAME.getName(), TblsProjectRnDData.MethodDevelopmentSequence.ANALYTICAL_PARAMETER.getName(), TblsProjectRnDData.MethodDevelopmentSequence.PROJECT.getName()},
                new Object[]{sequenceName, analyticalParameter, projectName}, getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(projectRnDinfo[0][0].toString())) {
            this.sequenceName = null;
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, Rdbms.RdbmsErrorTrapping.RDBMS_RECORD_NOT_FOUND, new Object[]{sequenceName, TblsProjectRnDData.TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE.getTableName(), LPPlatform.buildSchemaName(procReqSession.getProcedureInstance(), GlobalVariables.Schemas.DATA.getName())}, sequenceName);
        } else {
            this.hasError = false;
            this.formulaFieldNames = getAllFieldNames(TblsProjectRnDData.TablesProjectRnDData.METHOD_DEVELOPMENT_SEQUENCE.getTableFields());
            this.formulaFieldValues = projectRnDinfo[0];
            this.sequenceName = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.MethodDevelopmentSequence.NAME.getName())]).toString();
            this.isLocked = Boolean.valueOf(LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.MethodDevelopmentSequence.IS_LOCKED.getName())]).toString());
            if (this.isLocked == null) {
                this.isLocked = false;
            }
            this.lockedReason = LPNulls.replaceNull(projectRnDinfo[0][LPArray.valuePosicInArray(formulaFieldNames, TblsProjectRnDData.MethodDevelopmentSequence.LOCKED_REASON.getName())]).toString();
        }
    }   
        public String getSequenceName() {
            return sequenceName;
        }
        public String getProjectName() {
            return projectName;
        }
        public Boolean getHasError() {
            return hasError;
        }
        
        public InternalMessage getErrorDetail() {
            return errorDetail;
        }        
        
}
