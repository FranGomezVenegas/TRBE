/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functionaljavaa.platform.doc;

import databases.Rdbms;
import databases.SqlStatement;
import databases.TblsTrazitDocTrazit;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.platform.doc.EndPointsToRequirements.formatListForEmail;
import static functionaljavaa.platform.doc.EndPointsToRequirements.jsonArrayToList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPMailing;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class DevObjectsInModules {
    JSONObject summaryInfo;
    JSONArray objsNotInModuleArr;
    JSONArray allApisArr;
    JSONArray allBusRulesArr;
    JSONArray allErrorFamArr;
    JSONArray allAudEvArr;
    JSONArray allApisNotFoundArr;
    JSONArray allBusRulesNotFoundArr;
    JSONArray allErrorFamNotFoundArr;
    JSONArray allAudEvNotFoundArr;
    
    public JSONObject getSummaryInfo(){
        return this.summaryInfo;
    }    
    public DevObjectsInModules(HttpServletRequest request, HttpServletResponse response){    
        this.summaryInfo=new JSONObject();
        this.objsNotInModuleArr=new JSONArray();
        allApisArr=new JSONArray();
        allBusRulesArr=new JSONArray();
        allErrorFamArr=new JSONArray();
        allAudEvArr=new JSONArray();
        allApisNotFoundArr=new JSONArray();
        allBusRulesNotFoundArr=new JSONArray();
        allErrorFamNotFoundArr=new JSONArray();
        allAudEvNotFoundArr=new JSONArray();
        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);         
        String dbTrazitModules=prop.getString(Rdbms.DbConnectionParams.DBMODULES.getParamValue());
        Rdbms.getRdbms().startRdbms(dbTrazitModules);

        String[] fldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_APIS_PER_MODULE.getTableFields());        
        Object[][] reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_MODULES.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_APIS_PER_MODULE.getTableName(), 
                new String[]{TblsTrazitDocTrazit.viewApisPerModule.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{}, fldNames,
                new String[]{TblsTrazitDocTrazit.viewApisPerModule.COUNT_PRESENT.getName(), TblsTrazitDocTrazit.viewApisPerModule.API_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
            return;
        }else{
            for (Object[] curRow: reqEndpointInfo){
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fldNames, curRow);
                allApisArr.put(convertArrayRowToJSONObject);
            }
            evaluateInfo(fldNames, reqEndpointInfo, "APIS");
        }
        fldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_AUDIT_EVENT_ENTITES_PER_MODULE.getTableFields());        
        reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_MODULES.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_AUDIT_EVENT_ENTITES_PER_MODULE.getTableName(), 
                new String[]{TblsTrazitDocTrazit.viewAuditEventEntitiesPerModule.ENTITY.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{}, fldNames,
                new String[]{TblsTrazitDocTrazit.viewAuditEventEntitiesPerModule.COUNT_PRESENT.getName(), TblsTrazitDocTrazit.viewAuditEventEntitiesPerModule.ENTITY.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
            return;
        }else{
            for (Object[] curRow: reqEndpointInfo){
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fldNames, curRow);
                allAudEvArr.put(convertArrayRowToJSONObject);
            }
            evaluateInfo(fldNames, reqEndpointInfo, "AUDIT");
        }
        fldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_BUSINESS_RULES_PER_MODULE.getTableFields());        
        reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_MODULES.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_BUSINESS_RULES_PER_MODULE.getTableName(), 
                new String[]{TblsTrazitDocTrazit.viewBusinessRulesPerModule.API_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{}, fldNames,
                new String[]{TblsTrazitDocTrazit.viewBusinessRulesPerModule.COUNT_PRESENT.getName(), TblsTrazitDocTrazit.viewBusinessRulesPerModule.API_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
            return;
        }else{
            for (Object[] curRow: reqEndpointInfo){
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fldNames, curRow);
                allBusRulesArr.put(convertArrayRowToJSONObject);
            }
            evaluateInfo(fldNames, reqEndpointInfo, "BUSINESS");
        }
        fldNames = EnumIntTableFields.getAllFieldNames(TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_ERROR_FAMILIES_PER_MODULE.getTableFields());        
        reqEndpointInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.MODULES_TRAZIT_MODULES.getName(), TblsTrazitDocTrazit.TablesTrazitDocTrazit.VIEW_ERROR_FAMILIES_PER_MODULE.getTableName(), 
                new String[]{TblsTrazitDocTrazit.viewErrorFamiliesPerModule.FAMILY_NAME.getName()+" "+SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL.getSqlClause()},
                new Object[]{}, fldNames,
                new String[]{TblsTrazitDocTrazit.viewErrorFamiliesPerModule.COUNT_PRESENT.getName(), TblsTrazitDocTrazit.viewErrorFamiliesPerModule.FAMILY_NAME.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(reqEndpointInfo[0][0].toString())){
            return;
        }else{
            for (Object[] curRow: reqEndpointInfo){
                JSONObject convertArrayRowToJSONObject = LPJson.convertArrayRowToJSONObject(fldNames, curRow);
                allErrorFamArr.put(convertArrayRowToJSONObject);
            }
            evaluateInfo(fldNames, reqEndpointInfo, "ERRORS");
        }


        buildSummaryReport(request);
    }
    
    public void evaluateInfo(String[] fldNames, Object[][] reqEndpointInfo, String objType){
        Integer countPresentFldPosic=LPArray.valuePosicInArray(fldNames, TblsTrazitDocTrazit.viewErrorFamiliesPerModule.COUNT_PRESENT.getName());
        if (countPresentFldPosic==-1){return;}
        if (Boolean.FALSE.equals("0".equalsIgnoreCase(reqEndpointInfo[0][countPresentFldPosic].toString()))){return;}
        for (Object[] curRow: reqEndpointInfo){
            if (Boolean.TRUE.equals("0".equalsIgnoreCase(curRow[countPresentFldPosic].toString()))){
                JSONObject missingObj=new JSONObject();
                for (int i=0;i<fldNames.length;i++){
                    missingObj.put(fldNames[i], curRow[i]);
                }                
                switch (objType){
                    case "ERRORS":
                        this.allErrorFamNotFoundArr.put(missingObj);
                        break;
                    case "BUSINESS":
                        this.allBusRulesNotFoundArr.put(missingObj);
                        break;
                    case "AUDIT":
                        this.allAudEvNotFoundArr.put(missingObj);
                        break;
                    case "APIS":
                        this.allApisNotFoundArr.put(missingObj);
                        break;
                    default: 
                        return;
                }
                this.objsNotInModuleArr.put(missingObj);                
            }else{
                return;
            }
        }
    }
    
    public void buildSummaryReport(HttpServletRequest request){
            String summaryDiagnoses = "";
            if (objsNotInModuleArr.isEmpty()) {
                summaryDiagnoses = "SUCCESS";
            } else {

                summaryDiagnoses = "WITH ERRORS, objects not assigned to any module";
            }
            JSONObject summaryDiagnObj=new JSONObject();
            summaryDiagnObj.put("summary", summaryDiagnoses);
            JSONArray summaryDiagnArr=new JSONArray();
            if (allApisNotFoundArr.length()>0)
                summaryDiagnArr.put("For endpoints, There are "+allApisNotFoundArr.length()+" apis not assigned to any module");
            if (allBusRulesNotFoundArr.length()>0)
                summaryDiagnArr.put("For Business Rules, There are "+allBusRulesNotFoundArr.length()+" business rules apis not assigned to any module");
            if (allErrorFamNotFoundArr.length()>0)
                summaryDiagnArr.put("For Error notifications, There are "+allErrorFamNotFoundArr.length()+" families not assigned to any module");
            if (allAudEvNotFoundArr.length()>0)
                summaryDiagnArr.put("For Audit events, There are "+allAudEvNotFoundArr.length()+" entities not assigned to any module");

            summaryDiagnObj.put("detail", summaryDiagnArr);

            this.summaryInfo.put("00_summary",summaryDiagnObj);
            this.summaryInfo.put("01_total_endpoint_apis",allApisArr.length());
            this.summaryInfo.put("01_list_of_endpoint_apis", allApisArr);
            this.summaryInfo.put("02_total_business_rules_apis",allBusRulesArr.length());
            this.summaryInfo.put("02_list_of_business_rules_apis", allBusRulesArr);
            this.summaryInfo.put("03_total_error_families",allErrorFamArr.length());
            this.summaryInfo.put("03_list_of_error_families", allErrorFamArr);
            this.summaryInfo.put("04_total_audit_event_entities",allAudEvArr.length());
            this.summaryInfo.put("04_list_of_audit_event_entities", allAudEvArr);

            this.summaryInfo.put("01_total_endpoint_apis_with_no_module",allApisNotFoundArr.length());
            this.summaryInfo.put("01_list_of_endpoint_apis_with_no_module", allApisNotFoundArr);
            this.summaryInfo.put("02_total_business_rules_apis_with_no_module",allBusRulesNotFoundArr.length());
            this.summaryInfo.put("02_list_of_business_rules_apis_with_no_module", allBusRulesNotFoundArr);
            this.summaryInfo.put("03_total_error_families_with_no_module",allErrorFamNotFoundArr.length());
            this.summaryInfo.put("03_list_of_error_families_with_no_module", allErrorFamNotFoundArr);
            this.summaryInfo.put("04_total_audit_event_entities_with_no_module",allAudEvNotFoundArr.length());
            this.summaryInfo.put("04_list_of_audit_event_entities_with_no_module", allAudEvNotFoundArr);
            
            this.summaryInfo.put("03_total_all_objects_with_no_module",objsNotInModuleArr.length());
            this.summaryInfo.put("03_list_of_all_objects_with_no_module", objsNotInModuleArr);
            Boolean sendMail = Boolean.valueOf(request.getParameter("sendMail"));        
            if (sendMail){
                try {
                    StringBuilder mailBody=new StringBuilder(0);
                    mailBody.append("<h2>Endpoint APIS with no module: "+allApisNotFoundArr.length()+" from  a total of "+allApisArr.length()+"</h2><br>");
                    mailBody.append("<h2>Business Rules with no module: "+allBusRulesNotFoundArr.length()+" from  a total of "+allBusRulesArr.length()+"</h2><br>");
                    mailBody.append("<h2>Error notification families with no module: "+allErrorFamNotFoundArr.length()+" from  a total of "+allErrorFamArr.length()+"</h2><br>");
                    mailBody.append("<h2>Audit events entities with no module: "+allAudEvNotFoundArr.length()+" from  a total of "+allAudEvArr.length()+"</h2><br>");
                    
                    mailBody.append("<b>Endpoint APIS with no module ones are:</b> <br>"+formatListForEmail(jsonArrayToList(allApisNotFoundArr))+"<br><br>");
                    mailBody.append("<b>Business Rules with no module ones are:</b> <br>"+formatListForEmail(jsonArrayToList(allBusRulesNotFoundArr))+"<br><br>");
                    mailBody.append("<b>Error notification with no module ones are:</b> <br>"+formatListForEmail(jsonArrayToList(allErrorFamNotFoundArr))+"<br><br>");
                    mailBody.append("<b>Audit events with no module ones are:</b> <br>"+formatListForEmail(jsonArrayToList(allAudEvNotFoundArr))+"<br><br>");
                    LPMailing newMail = new LPMailing();
                    newMail.sendEmail(
                            new String[]{"info.fran.gomez@gmail.com", "fgomez@trazit.net", "ibelmonte@trazit.net",
                                "cdesantos@trazit.net", "promera@trazit.net"},            
                            "Business Rules declaration: "+summaryDiagnoses, mailBody.toString(),null, summaryInfo);
                } catch (MessagingException ex) {
                    Logger.getLogger(DevObjectsInModules.class.getName()).log(Level.SEVERE, null, ex);
                }
            }             
        
    }
}
