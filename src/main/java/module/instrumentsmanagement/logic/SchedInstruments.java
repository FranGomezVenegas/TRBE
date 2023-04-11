/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.instrumentsmanagement.logic;

import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlWhere;
import databases.features.Token;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import module.instrumentsmanagement.definition.TblsInstrumentsData;
import module.instrumentsmanagement.definition.TblsInstrumentsData.ViewsInstrumentsData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntViewFields;
import trazit.globalvariables.GlobalVariables;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class SchedInstruments {

    private SchedInstruments() {
        throw new IllegalStateException("Utility class");
    }

    public static void instrumentsSchedProcesses(Token token, String procInstanceName) {
        String moduleNameFromProcInstance = token.getModuleNameFromProcInstance(procInstanceName);
        if (Boolean.FALSE.equals(GlobalVariables.TrazitModules.INSTRUMENTS.name().equalsIgnoreCase(moduleNameFromProcInstance))) {
            return;
        }
        logNextEventWhenExpiredOrClose(procInstanceName, false);
    }

    public static JSONArray logNextEventWhenExpiredOrClose(String procInstanceName, Boolean readOnly) {
        Object[] dbViewExists = Rdbms.dbViewExists(procInstanceName, ViewsInstrumentsData.CALIB_PM_EXPIRED_OR_EXPIRING.getRepositoryName(), ViewsInstrumentsData.CALIB_PM_EXPIRED_OR_EXPIRING.getViewName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbViewExists[0].toString())) {
            new JSONArray();
        }

        EnumIntViewFields[] fieldsToRetrieveObj = EnumIntViewFields.getViewFieldsFromString(ViewsInstrumentsData.CALIB_PM_EXPIRED_OR_EXPIRING, "ALL", procInstanceName);
        String[] fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(fieldsToRetrieveObj);
        SqlWhere sW = new SqlWhere();
        sW.addConstraint(TblsInstrumentsData.CalibPmExpiredOrExpiring.EVENTS_IN_PROGRESS,
                SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{0}, null);
        Object[][] eventsInfo = QueryUtilitiesEnums.getViewData(ViewsInstrumentsData.CALIB_PM_EXPIRED_OR_EXPIRING,
                fieldsToRetrieveObj, sW, new String[]{TblsInstrumentsData.CalibPmExpiredOrExpiring.NAME.getName()},
                procInstanceName);
        JSONArray jArr = new JSONArray();
        if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(eventsInfo[0][0].toString()))) {
            Integer fldEventTypePosic = EnumIntViewFields.getFldPosicInArray(fieldsToRetrieveObj, TblsInstrumentsData.CalibPmExpiredOrExpiring.TYPE.getName());
            Integer fldInstrNamePosic = EnumIntViewFields.getFldPosicInArray(fieldsToRetrieveObj, TblsInstrumentsData.CalibPmExpiredOrExpiring.NAME.getName());
            if (fldEventTypePosic == -1 || fldInstrNamePosic == -1) {
                return new JSONArray();
            }

            for (Object[] currInstrEv : eventsInfo) {
                String curInstName = currInstrEv[fldInstrNamePosic].toString();
                DataInstruments instr = new DataInstruments(curInstName);
                InternalMessage newEventLog = null;
                if (Boolean.TRUE.equals(instr.getHasError())) {
                    newEventLog = null;
                } else {
                    if (Boolean.FALSE.equals(readOnly)) {
                        switch (currInstrEv[fldEventTypePosic].toString().toUpperCase()) {
                            case "CALIBRATION":
                                newEventLog = instr.startCalibration(true);
                                break;
                            case "CALIBRATION_OFFSET":
                                newEventLog = instr.startCalibration(true);
                                break;
                            case "PM":
                                newEventLog = instr.startPrevMaint(true);
                                break;
                            case "PM_OFFSET":
                                newEventLog = instr.startPrevMaint(true);
                                break;
                            default:
                                break;
                        }
                    }
                }
                JSONObject jObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currInstrEv);
                jArr.add(jObj);
            }
        }
        return jArr;
    }
}
