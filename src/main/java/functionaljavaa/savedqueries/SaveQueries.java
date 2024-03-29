/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.savedqueries;

import databases.Rdbms;
import databases.RdbmsObject;
import databases.TblsData;
import databases.features.Token;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import trazit.session.ProcedureRequestSession;
import trazit.session.ApiMessageReturn;
/**
 *
 * @author User
 */
public class SaveQueries {

    enum SavedQueriesAPIErrorMessages{
        AAA_FILE_NAME("errorTrapping"),
    //    INCIDENT_CURRENTLY_NOT_ACTIVE("incidentCurrentlyNotActive"),
    //    INCIDENT_ALREADY_ACTIVE("incidentAlreadyActive"),
        ;
        private SavedQueriesAPIErrorMessages(String sname){
            name=sname;
        } 
        public String getErrorCode(){
            return this.name;
        }
        private final String name;
    }

    public static Object[] newSavedQuery(String name, String definition, String[] fldNames, Object[] fldValues){ 
        Token token=ProcedureRequestSession.getInstanceForActions(null, null, null).getToken();
        try {
            JSONParser parser = new JSONParser(); 
            JSONObject json = (JSONObject) parser.parse(definition);
        } catch (ParseException ex) {
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "Definition field not recognized as Json object, error: <*1*>. Field value: <*2*>",new Object[]{ex.toString(), definition});
        }
        
        String[] updFieldName=new String[]{TblsData.SavedQueries.NAME.getName(), TblsData.SavedQueries.DEFINITION.getName(), TblsData.SavedQueries.OWNER.getName()};
        Object[] updFieldValue=new Object[]{name, definition, token.getPersonName()};
        
        if ( fldNames!=null && fldValues!=null && fldNames[0].length()>0 && fldValues[0].toString().length()>0){
            updFieldName=LPArray.addValueToArray1D(updFieldName, fldNames);
            updFieldValue=LPArray.addValueToArray1D(updFieldValue, fldValues);
        }
        
        RdbmsObject insertDiagn = Rdbms.insertRecordInTable(TblsData.TablesData.SAVED_QUERIES, updFieldName, updFieldValue);
        if (Boolean.FALSE.equals(insertDiagn.getRunSuccess())) return insertDiagn.getApiMessage();
        
        return LPArray.addValueToArray1D(insertDiagn.getApiMessage(),insertDiagn.getNewRowId());
    }

}
