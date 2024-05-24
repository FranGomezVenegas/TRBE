/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class EndpointsWithNoJsonModel {
    JSONObject summaryInfo;
    JSONArray endpointsWithPendingJsonModel;
    JSONArray summaryApisAndEndPointJsonModels;
    
    public JSONObject getSummaryInfo(){
        return this.summaryInfo;
    }        
    public EndpointsWithNoJsonModel(HttpServletRequest request, HttpServletResponse response){
        summaryInfo=new JSONObject();
        this.summaryInfo=new JSONObject();
        this.endpointsWithPendingJsonModel=new JSONArray();
        summaryApisAndEndPointJsonModels=new JSONArray();
        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);

        String[] fldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_API_SUMMARY_AND_JSONMODELS.getTableFields());        
        Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_API_SUMMARY_AND_JSONMODELS.getTableName(), 
                new String[]{TblsTrazitDocTrazit.viewApiActionsAndJsonModels.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{}, fldNames,
                new String[]{TblsTrazitDocTrazit.viewApiActionsAndJsonModels.PENDING_JSONMODEL.getName()+" desc ", TblsTrazitDocTrazit.viewApiActionsAndJsonModels.API_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
            return;
        }else{
            for (Object[] curRow: reqEndpointInfo){
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fldNames, curRow);
                summaryApisAndEndPointJsonModels.put(convertArrayRowToJSONObject);
            }
        }                                                                        
        fldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_ENDPOINTS_PENDING_JSONMODEL.getTableFields());        
        reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_TRAZIT.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_ENDPOINTS_PENDING_JSONMODEL.getTableName(), 
                new String[]{TblsTrazitDocTrazit.viewEndpointsPendingJsonModel.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{}, fldNames,
                new String[]{TblsTrazitDocTrazit.viewEndpointsPendingJsonModel.API_NAME.getName(), TblsTrazitDocTrazit.viewEndpointsPendingJsonModel.ENDPOINT_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
            return;
        }else{
            for (Object[] curRow: reqEndpointInfo){
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fldNames, curRow);
                endpointsWithPendingJsonModel.put(convertArrayRowToJSONObject);
            }
        }

        buildSummaryReport();        
    }
    
    public void buildSummaryReport(){
            String summaryDiagnoses = "";
            if (endpointsWithPendingJsonModel.isEmpty()) {
                summaryDiagnoses = "SUCCESS";
            } else {

                summaryDiagnoses = "WITH ERRORS, There are "+endpointsWithPendingJsonModel.length()+" endpoints with no json model assigned and required";
            }
            this.summaryInfo.put("00_summary",summaryDiagnoses);
            this.summaryInfo.put("01_total_endpoint_pending_of_json_model",endpointsWithPendingJsonModel.length());
            this.summaryInfo.put("01_list_of_endpoint_pending_of_json_model", endpointsWithPendingJsonModel);

            this.summaryInfo.put("01_total_apis",summaryApisAndEndPointJsonModels.length());
            this.summaryInfo.put("01_summary_of_jsonmodels_per_api", summaryApisAndEndPointJsonModels);
    }    
}
