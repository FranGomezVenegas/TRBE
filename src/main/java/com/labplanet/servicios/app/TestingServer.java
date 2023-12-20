/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;

import static trazit.globalvariables.GlobalVariables.VALIDATION_MODE_REPO;
import static com.labplanet.servicios.app.AppProcedureListAPI.PROC_NEW_EVENT_FLD_NAME;
import static com.labplanet.servicios.app.AppProcedureListAPI.newProcedureDefinition;
import module.monitoring.definition.TblsEnvMonitData.ViewsEnvMonData;
import databases.features.DbEncryption;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.Rdbms;
import static databases.Rdbms.dbTableExists;
import static databases.Rdbms.insertRecordInTableFromTable;
import databases.RdbmsObject;
import databases.SqlStatement;
import databases.SqlWhere;
import module.instrumentsmanagement.definition.TblsInstrumentsDataAudit;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsData.TablesData;
import databases.TblsProcedure;
import databases.TblsProcedureAudit.TablesProcedureAudit;
import trazit.procedureinstance.definition.definition.TblsReqs;
import databases.TblsTesting;
import databases.features.Token;
import functionaljavaa.analysis.UserMethod;
import functionaljavaa.certification.AnalysisMethodCertif;
import static functionaljavaa.certification.FrontendCertifObjsUtilities.certifObjCertifModeOwnUserAction;
import functionaljavaa.datatransfer.FromInstanceToInstance;
import functionaljavaa.intervals.IntervalsUtilities;
import functionaljavaa.inventory.batch.DataBatchIncubator;
import functionaljavaa.materialspec.ConfigSpecRule;
import functionaljavaa.parameter.Parameter;
import static functionaljavaa.platform.doc.EndPointsToRequirements.getDocInfoForEndPoint;
import functionaljavaa.samplestructure.DataSampleRevisionTestingGroup;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import static functionaljavaa.testingscripts.LPTestingOutFormat.csvExtractFieldValueBigDecimal;
import functionaljavaa.testingscripts.TestingCoverage;
import functionaljavaa.user.UserAndRolesViews;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static lbplanet.utilities.LPDatabase.FIELDS_NAMES_PROCEDURE_NAME;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import static trazit.session.ProcReqSessionAutomatisms.markAsExpiredTheExpiredObjects;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import module.instrumentsmanagement.definition.TblsInstrumentsData.ViewsInstrumentsData;
import module.inventorytrack.definition.TblsInvTrackingData.ViewsInvTrackingData;
import module.inventorytrack.logic.InvTrackingFrontendMasterData;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntViews;
import trazit.enums.deployrepository.DeployTables;
import static trazit.enums.deployrepository.DeployTables.createTableScript;
import static lbplanet.utilities.LPDate.secondsInDateRange;
import static lbplanet.utilities.LPLdap.LdapValidateUser;
import static lbplanet.utilities.LPLdap.createLdapNewUser;
import static module.instrumentsmanagement.logic.DataInstrumentsEvents.objectVariableSetValue;
import module.inventorytrack.logic.OperationMetricsConsumptionEntry;
import module.inventorytrack.logic.OperationMetricsConsumptionStock;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.simple.JSONArray;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntViewFields;
import static trazit.procedureinstance.definition.apis.ReqProcedureDefinitionQueries.getScriptWithSteps;
import trazit.procedureinstance.definition.logic.ClassReqProcedUserAndActionsForQueries;
import static trazit.platforminstance.logic.PlatformNewInstance.createCheckPlatformProcedure;
import trazit.session.InternalMessage;
import trazit.session.ProcedureRequestSession;
import trazit.thirdparties.sap.ExcelExporter;
import trazit.thirdparties.sap.Mosquitto;
import trazit.thirdparties.sap.PDFDataExtractor;
import java.util.Set;
import module.inspectionlot.rawmaterial.definition.TblsInspLotRMData.ViewsInspLotRMData;
import trazit.platforminstance.logic.CreatePlatform;
import static trazit.procedureinstance.definition.logic.ReqProcDefTestingCoverageSummary.procInstanceTestingInfo;
/**
 *
 * @author Administrator
 */
public class TestingServer extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request = LPHttp.requestPreparation(request);
        response = LPHttp.responsePreparation(response);
        try (PrintWriter out = response.getWriter()) {
            
        String moduleName="STOCKS";
        Integer moduleVersion=1;
        String procedureName="stock";
        Integer procedureVersion=1;
        String procInstanceName="stock";

        String dbName="demo_v0_9_2";
        Rdbms.stablishDBConection(dbName);
    
        procInstanceName="inspection_lot";                
        Object[] assignMethodToUser = AnalysisMethodCertif.newRecord("EP", 1, "admin", null, null);
        out.println(Arrays.toString(assignMethodToUser));
if (1==1)return;

        procInstanceName="inspection_lot";                
        out.println("************ ViewsAppProcData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW. Begin");
        String tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, procInstanceName, false, false, true, null);
        out.println(tblCreateScript);
Object[] prepUpQuery = Rdbms.prepUpQueryWithDiagn(procInstanceName, ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(), tblCreateScript, new Object[]{});
out.println(Arrays.toString(prepUpQuery));
out.println("************ ViewsAppProcData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW testing. End");
        out.println("************ ViewsAppProcData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW NO testing. Begin");
        tblCreateScript=EnumIntViews.getViewScriptCreation(ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, procInstanceName, false, false, false, null);
        out.println(tblCreateScript);
prepUpQuery = Rdbms.prepUpQueryWithDiagn(procInstanceName, ViewsInspLotRMData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW.getViewName(), tblCreateScript, new Object[]{});
out.println(Arrays.toString(prepUpQuery));
        out.println("************ ViewsAppProcData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW NO testing. End");
if (1==1)return;
    
    
                
Token token2 = new Token("eyJ1c2VyREIiOiJhZG1pbiIsImRhdGV0aW1lRm9ybWF0QXRQbGF0Zm9ybUxldmVsIjoiRElTQUJMRUQiLCJwcm9jc01vZHVsZU5hbWUiOiJzdG9jaypTVE9DS1N8aW5zdHJ1bWVudHMqSU5TVFJVTUVOVFN8bW9uX3dhdGVyKk1PTklUT1JJTkd8bWJfZW0qTU9OSVRPUklOR3xpbnNwZWN0aW9uX2xvdCpJTlNQRUNUSU9OX0xPVFMiLCJkYk5hbWUiOiJkZW1vX3YwXzlfMiIsInR5cCI6IkpXVCIsInVzZXJfcHJvY2VkdXJlX2hhc2hjb2RlcyI6InN0b2NrKjEqLTE5MzExMDk1MzZ8aW5zdHJ1bWVudHMqMSo1NzI0ODQyOTZ8bW9uX3dhdGVyKjEqMjA1MzgwNjg2NXxtYl9lbSoxKjE4NzkwNDY5OTN8aW5zcGVjdGlvbl9sb3QqMSoyOTk0NjE1MDAiLCJlU2lnbiI6ImZpcm1hZGVtbyIsInVzZXJEQlBhc3N3b3JkIjoidHJheml0NGV2ZXIiLCJ1c2VyTWFpbCI6ImluZm9AdHJheml0Lm5ldCIsInVzZXJfcHJvY2VkdXJlcyI6IltzdG9jaywgaW5zdHJ1bWVudHMsIG1vbl93YXRlciwgbWJfZW0sIGluc3BlY3Rpb25fbG90XSIsImFwcFNlc3Npb25JZCI6IjYwOCIsImFwcFNlc3Npb25TdGFydGVkRGF0ZSI6IkZyaSBEZWMgMDEgMTg6MjA6NTcgVVRDIDIwMjMiLCJ1c2VyUm9sZSI6InN1cGVydXNlciIsImFsZyI6IkhTMjU2IiwiaW50ZXJuYWxVc2VySUQiOiI0NTQ4OTIyMyJ9.eyJpc3MiOiJMYWJQTEFORVRkZXN0cmFuZ2lzSW5UaGVOaWdodCJ9.o_OJFAZCcncGaOW5fgTxSqMG9ff0H3bziooPmsxtmtw");                
newProcedureDefinition(token2, "inspection_lot", false);
if (1==1)return;                
                CreatePlatform nwPlatf= new CreatePlatform(dbName);

    //createModuleActionsAndQueries(moduleName, Integer , jFileContentObjModel, classesImplementingEndPoints);    
if (1==1)return;    
              //out.println(Mosquitto.sendMosquitto());
              out.println(Mosquitto.getFromMosquitto());

            procInstanceName="instruments";
            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VWtesting. Begin");
            out.println(EnumIntViews.getViewScriptCreation(ViewsInstrumentsData.NOT_DECOM_INSTR_EVENT_DATA_VW, procInstanceName, false, false, true, null));
            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW testing. End");
            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW NO testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(ViewsInstrumentsData.NOT_DECOM_INSTR_EVENT_DATA_VW, procInstanceName, false, false, false, null));
            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW NO testing. End");
    
if (1==1)return;            
    
    procInstanceTestingInfo( procInstanceName);
if (1==1)return;            

//        String procInstanceSource="lots_raw";
//        String procInstanceDestination="inspection lot";
        String procInstanceSource="mon_water";
        String procInstanceDestination="mon_water";

        
        EnumIntTables[] tblsArr= new EnumIntTables[]{TblsCnfg.TablesConfig.SPEC, TblsCnfg.TablesConfig.SPEC_RULES, TblsCnfg.TablesConfig.SPEC_LIMITS};
        //EnumIntTables[] tblsArr= new EnumIntTables[]{TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES, TblsInstrumentsConfig.TablesInstrumentsConfig.VARIABLES_SET};
        //EnumIntTables[] tblsArr= new EnumIntTables[]{TblsInvTrackingConfig.TablesInvTrackingConfig.INV_REFERENCE};
//TblsCnfg.TablesConfig.values(); //
//        TblsInspLotRMConfig.TablesInspLotRMConfig.values(); //
String curTblName="";      
    out.println("Source: "+procInstanceSource);
    out.println("Destination: "+procInstanceDestination);


for (EnumIntTables curTbl: tblsArr){  
    try{ 
    curTblName=curTbl.getTableName();
    out.println("Table Name: "+curTblName);
    procInstanceName="demo_v0_9_1";
    Rdbms.stablishDBConection(procInstanceName);
    SqlWhere sWhere=new SqlWhere();
    sWhere.addConstraint(curTbl.getTableFields()[0], SqlStatement.WHERECLAUSE_TYPES.IS_NOT_NULL, null, "");    
    Object[][] recordFieldsByFilter = Rdbms.getRecordFieldsByFilter(procInstanceSource, LPPlatform.buildSchemaName(procInstanceSource, "config"), curTbl, 
        sWhere, curTbl.getTableFields(), null, Boolean.TRUE);
    Rdbms.closeRdbms();
    if (Boolean.FALSE.equals(LPPlatform.LAB_FALSE.equalsIgnoreCase(recordFieldsByFilter[0][0].toString()))){
        procInstanceName="demo_v0_9_2";  
        Rdbms.stablishDBConection(procInstanceName);
        
        for (Object[] curRow: recordFieldsByFilter){
            String[] fldN=new String[]{};
            Object[] fldV=new Object[]{};
            for (int iFlds=0;iFlds<curRow.length;iFlds++){
                if (curRow[iFlds].toString().length()>0){
                    fldN=LPArray.addValueToArray1D(fldN, curTbl.getTableFields()[iFlds].getName());
                    fldV=LPArray.addValueToArray1D(fldV, curRow[iFlds]);                    
                }
            }
            RdbmsObject insertRecord = Rdbms.insertRecord(curTbl, fldN, fldV, procInstanceDestination);
            out.println(curTbl.getTableName()+": "+insertRecord.getRunSuccess()+"-"+insertRecord.getErrorMessageVariables()==null?"":Arrays.toString(insertRecord.getErrorMessageVariables()));
        }
        Rdbms.closeRdbms();
    }
    }catch(Exception e){
        out.println(curTblName+": "+e.getMessage());
    }

}   
if (1==1)return;         

        
for (int scriptId=1;scriptId<6;scriptId++){ 
    out.println("Source: "+procInstanceSource);
    out.println("Destination: "+procInstanceDestination);
    out.println("scriptId: "+scriptId);
        Rdbms.stablishDBConection("demo_v0_9_1");
            JSONObject myData=getScriptWithSteps(scriptId, procInstanceSource, null, null);
            Set<String> keys = myData.keySet();

            String[] fldN=new String[]{};
            Object[] fldV=new Object[]{};
            JSONArray stepsArr=new JSONArray();
            for(String key : keys) {
               Object value = myData.get(key);
              // out.println("Key: " + key + ", Value: " + value.toString());
               if ("steps".equalsIgnoreCase(key)){
                   stepsArr = (JSONArray) value;
               }else{
                   if (value.toString().length()>0){
                       if (TblsTesting.Script.DATE_CREATION.getName().equalsIgnoreCase(key)||
                           TblsTesting.Script.DATE_EXECUTION.getName().equalsIgnoreCase(key)||
                            TblsTesting.Script.TIME_STARTED.getName().equalsIgnoreCase(key)||
                            TblsTesting.Script.TIME_COMPLETED.getName().equalsIgnoreCase(key) )
                           value=LPDate.stringFormatToLocalDateTime(value.toString());
                           
                        fldN=LPArray.addValueToArray1D(fldN, key);
                        fldV=LPArray.addValueToArray1D(fldV, value);
                   }
               }
           }
        Rdbms.closeRdbms();
        
        Rdbms.stablishDBConection("demo_v0_9_2");
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsTesting.TablesTesting.SCRIPT, fldN, fldV, 
                    procInstanceDestination);
            out.println(TblsTesting.TablesTesting.SCRIPT.getTableName()+": "+insertRecord.getRunSuccess());
            RdbmsObject insertRecord2 = null;
            for (Object element : stepsArr) {
                JSONObject jsonObject = (JSONObject) element;
                keys = jsonObject.keySet();
                fldN=new String[]{};
                fldV=new Object[]{};
                // Iterate over each key-value pair in the JSONObject
                for (String key : keys) {
                    Object value = jsonObject.get(key);
                    System.out.println("Key: " + key + ", Value: " + value);
                    if (Boolean.FALSE.equals("eval_syntaxis_class".equalsIgnoreCase(key)||"eval_code_class".equalsIgnoreCase(key)||
                    "eval_code_icon".equalsIgnoreCase(key)||"eval_syntaxis_icon".equalsIgnoreCase(key) )){
                        if (value.toString().length()>0){
                           if (TblsTesting.ScriptSteps.DATE_EXECUTION.getName().equalsIgnoreCase(key)||
                                TblsTesting.ScriptSteps.TIME_STARTED.getName().equalsIgnoreCase(key)||
                                TblsTesting.ScriptSteps.TIME_COMPLETED.getName().equalsIgnoreCase(key) )
                               value=LPDate.stringFormatToLocalDateTime(value.toString());

                           fldN=LPArray.addValueToArray1D(fldN, key);
                           fldV=LPArray.addValueToArray1D(fldV, value);                        
                        }
                    }
                }
                insertRecord2 = Rdbms.insertRecord(TblsTesting.TablesTesting.SCRIPT_STEPS, fldN, fldV, 
                    procInstanceDestination);
                out.println(TblsTesting.TablesTesting.SCRIPT_STEPS.getTableName()+": "+insertRecord.getRunSuccess());
                
            }   
        Rdbms.closeRdbms();
}        
        if (1==1)return;  


        
        if (1==1)return;  
                            Object[] insertRecordInTableFromTable = insertRecordInTableFromTable(true, 
                                    getAllFieldNames(TblsReqs.TablesReqs.MODULE_MANUALS.getTableFields()),
                                        GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.MODULE_MANUALS.getTableName(), 
                                    new String[]{TblsReqs.ModuleManuals.MODULE_NAME.getName(), TblsReqs.ModuleManuals.MODULE_VERSION.getName()},
                                    new Object[]{moduleName, moduleVersion},
                                    GlobalVariables.Schemas.REQUIREMENTS.getName(), 
                                        TblsReqs.TablesReqs.PROC_MANUALS.getTableName(), getAllFieldNames(TblsReqs.TablesReqs.PROC_MANUALS.getTableFields())
                                ,new String[]{TblsReqs.ProcedureManuals.PROCEDURE_NAME.getName(), TblsReqs.ProcedureManuals.PROCEDURE_VERSION.getName(),
                                    TblsReqs.ProcedureManuals.PROC_INSTANCE_NAME.getName()}, 
                                new Object[]{procedureName, procedureVersion, procInstanceName}, null);
          
        createCheckPlatformProcedure("hola");
        out.println(OperationMetricsConsumptionStock.main());
        List<OperationMetricsConsumptionEntry> consumptionData = new ArrayList<>();
        // Populate consumptionData with historical consumption entries
        consumptionData.add(new OperationMetricsConsumptionEntry(LocalDate.of(2023, 1, 1), 200));
        consumptionData.add(new OperationMetricsConsumptionEntry(LocalDate.of(2023, 1, 2), 200));
        consumptionData.add(new OperationMetricsConsumptionEntry(LocalDate.of(2023, 1, 3), 200)); 
        double currentQuantity = 1000;
        double minStockAlert = 20;
        
       
        out.println("Current quantity is "+currentQuantity);
        out.println("min stock alert when quantity is "+minStockAlert);
        out.println("The last consumes are:");
        for (OperationMetricsConsumptionEntry entry : consumptionData) {
            out.println(entry.getDate()+ " - consume " + entry.getConsumedGrams() + " grams");
            //totalConsumedGrams += entry.getConsumedGrams();
        }
/*        String[] predConclusion=OperationMetricsConsumptionStock.main(currentQuantity, minStockAlert, consumptionData);
        for (String curStr: predConclusion){
            out.println(curStr);
        }*/
        //out.println("Weka determines...."+OperationMetricsConsumptionStock.mainWeka());
        for (String curStr: OperationMetricsConsumptionStock.mainWeka(currentQuantity, minStockAlert, consumptionData)){
            out.println(curStr);
        }
if (1==1)return;            

Rdbms.stablishDBConection("labplanet");
            JSONObject myData = getScriptWithSteps(11, "em-demo-a", null, null);
            out.println("automated upload ...");
          out.println(Mosquitto.sendMosquitto());
          
if (1==1)return;            
            Rdbms.stablishDBConection("labplanet");
            procInstanceName = "inv-draft";
            String[] fldsArr = new String[]{TblsReqs.ProcedureRoles.ROLE_NAME.getName()};
            Object[][] procRoles = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_ROLES.getTableName(),
                    new String[]{TblsReqs.ProcedureRoles.PROC_INSTANCE_NAME.getName()},
                    new Object[]{procInstanceName}, fldsArr,
                    new String[]{TblsReqs.ProcedureUserRoles.ROLE_NAME.getName()});
            out.println(ClassReqProcedUserAndActionsForQueries.actionsByRoles(procInstanceName, procRoles));
if (1==1)return;
            String[] fieldsToRetrieve = EnumIntViewFields.getAllFieldNames(TblsData.ViewUserAndAnalysisMethodCertificationView.values());
            fieldsToRetrieve = LPArray.addValueToArray1D(fieldsToRetrieve, FIELDS_NAMES_PROCEDURE_NAME);

            String[] allUserProcedurePrefix = new String[]{"mp-release1"};
            Object[][] userAnaMethCertifByProcess = UserMethod.getUserAnalysisMethodCerttifByProcess(
                    new String[]{TblsData.ViewUserAndAnalysisMethodCertificationView.USER_NAME.getName()}, new Object[]{"lots"}, fieldsToRetrieve, allUserProcedurePrefix);
            if (userAnaMethCertifByProcess == null) {
                return;
            }
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(LPNulls.replaceNull(userAnaMethCertifByProcess[0][0]).toString())) {
                LPFrontEnd.responseError(allUserProcedurePrefix);
                Rdbms.closeRdbms();
                return;
            }
            JSONArray myAnaMethCertif = new JSONArray();
            JSONObject myAnaMethCertifList = new JSONObject();
            JSONArray myAnaMethCertifListArr = new JSONArray();

            Integer procedureFldPosic = LPArray.valuePosicInArray(fieldsToRetrieve, TblsData.ViewUserAndAnalysisMethodCertificationView.PROCEDURE.getName());

            for (Object[] curCertif : userAnaMethCertifByProcess) {
                JSONObject anaMethodJObj = new JSONObject();
                if (procedureFldPosic > -1) {
                    curCertif[procedureFldPosic] = curCertif[procedureFldPosic].toString().replace("-config", "").replace("\"", "");
                }
                anaMethodJObj = LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, curCertif);
                anaMethodJObj.put(GlobalAPIsParams.REQUEST_PARAM_CERTIF_OBJECTS_LEVEL, certifObjCertifModeOwnUserAction(fieldsToRetrieve, curCertif));
                myAnaMethCertif.add(anaMethodJObj);
            }
            myAnaMethCertifList.put("my_analysis_method_certifications", myAnaMethCertif);
            myAnaMethCertifListArr.add(myAnaMethCertifList);

            if (1 == 1) {
                return;
            }
            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME, "app-proc");
            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_DB_NAME, "labplanet");
            request.setAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, "ENTER_EVENT_RESULT");

            ProcedureRequestSession instanceForActions = ProcedureRequestSession.getInstanceForActions(request, response, false);
            if (instanceForActions.getHasErrors()) {
                out.println(instanceForActions.getErrorMessage());
                instanceForActions.killIt();
                return;
            }
            String url2 = "https://example.com/path/to/example.pdf";
            String text = "";
            String pdfPath = "D:/LP/Interfaces/HPLC_VALIDACIONES_FRAN_382.pdf";
            File pdfFile = new File(pdfPath);

            PDDocument document = PDDocument.load(pdfFile);

            PDFTextStripper textStripper = new PDFTextStripper();
            text = textStripper.getText(document);

            InputStream requestBody = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
            InternalMessage actionDiagnoses = objectVariableSetValue("qwer", 261, "¿Es Correcto?", "-");
            out.println(actionDiagnoses.getDiagnostic() + " " + actionDiagnoses.getMessageCodeObj().getErrorCode());
            instanceForActions.killIt();
            if (1 == 1) {
                return;
            }
            if (1 == 2) {
                try {
                    out.println("***** " + "getCarbohydratesByHPLCPDF" + " *****");
                    out.println(PDFDataExtractor.getCarbohydratesByHPLCPDF());
                    out.println("***** " + "getDataFromPDF" + " *****");
                    out.println(PDFDataExtractor.getDataFromPDF());
                    out.println("***** " + "getAmoxicilinaPDF" + " *****");
                    out.println(PDFDataExtractor.getAmoxicilinaPDF());

                } catch (Exception e) {
                    out.print(e.getMessage());
                    return;
                }

                String[][] data1 = {{"Name", "Age", "Gender"}, {"John", "25", "Male"}, {"Mary", "30", "Female"}};
                String[][] data2 = {{"ID", "Salary"}, {"001", "5000"}, {"002", "6000"}, {"003", "7000"}};
                String filename = "M:/LW-LIMS-V6-PROCAPS/trazit_excel_output.xlsx";

                ExcelExporter.exportToExcel(data1, data2, filename);
            }
//out.println(connectToOpenLDAP("demo"));
            Rdbms.stablishDBConection("labplanet");
            out.println(EnumIntViews.getViewScriptCreation(TblsReqs.ViewsReqs.PROC_REQ_SOLUTION_ACTIONS, "", false, false, false, null));
            out.println("************ ViewsReqs.PROC_REQ_USER_REQUIREMENTS_ACTIONS NO testing. End");

            if (1 == 1) {
                return;
            }
            String newUser = "carmsantos";
            out.println(newUser + ": " + createLdapNewUser("TRAZIT", newUser, "dc=example,dc=org"));
            out.println(newUser + ": " + LdapValidateUser("TRAZIT", newUser, "demo", "dc=example,dc=org"));
            out.println("fgomez " + LdapValidateUser("TRAZIT", "fgomez", "demo", "dc=example,dc=org"));
            out.println("uName " + LdapValidateUser("TRAZIT", "uName", "demo", "dc=example,dc=org"));
            if (1 == 1) {
                return;
            }
            if (1 == 2) {
                Rdbms.stablishDBConection("labplanet");
                InvTrackingFrontendMasterData mdObj = new InvTrackingFrontendMasterData();
                JSONObject masterDataJsonObject = mdObj.getMasterDataJsonObject("inv-draft");
                out.println(masterDataJsonObject.toJSONString());
                out.println("************ ViewsInvTrackingData.AVAILABLE_LOTS_PER_REFERENCE testing. Begin");
                out.println(EnumIntViews.getViewScriptCreation(ViewsInvTrackingData.AVAILABLE_LOTS_PER_REFERENCE, "inv-draft", false, false, true, null));
                out.println("************ ViewsInvTrackingData.AVAILABLE_LOTS_PER_REFERENCE testing. End");
                out.println("************ ViewsInvTrackingData.AVAILABLE_LOTS_PER_REFERENCE NO testing. Begin");
                out.println(EnumIntViews.getViewScriptCreation(ViewsInvTrackingData.AVAILABLE_LOTS_PER_REFERENCE, "inv-draft", false, false, false, null));
                out.println("************ ViewsInvTrackingData.AVAILABLE_LOTS_PER_REFERENCE NO testing. End");

                if (1 == 2) {
                    return;
                }
            }

//lbplanet.utilities.LPMailing.sendMailViaSSL("prueba SSL", "SSL esto es una prueba", new String[]{"info.fran.gomez@gmail.com"}, 
//null, null, null);
            out.println(lbplanet.utilities.LPMailing.mailAI("localhost", "25"));
            out.println(lbplanet.utilities.LPMailing.mailAI("localhost", "587"));
            out.println(lbplanet.utilities.LPMailing.mailAI("localhost", "465"));
            out.println(lbplanet.utilities.LPMailing.mailAI("localhost", "2525"));
            out.println(lbplanet.utilities.LPMailing.mailAI("mail.trazit.net", "25"));
            out.println(lbplanet.utilities.LPMailing.mailAI("mail.trazit.net", "587"));
            out.println(lbplanet.utilities.LPMailing.mailAI("mail.trazit.net", "465"));
            out.println(lbplanet.utilities.LPMailing.mailAI("mail.trazit.net", "2525"));
            if (1 == 1) {
                return;
            }

//Map<String, ? extends ServletRegistration> registrations = servletContext.getServletRegistrations();        
//@Override
            //public int hashCode() {
            //return 
//         Objects.hash(nombre,apellidos);
            //}
            //int hashCode=LPDate.getCurrentTimeStamp().hashCode();
            //int hashCode = this.getClass().hashCode();
            //out.println(hashCode);
            String valueToDecrypt = "noencrypted with spaces";
            out.println("Value: " + valueToDecrypt);
            Object[] decryptVal = DbEncryption.decryptValue(valueToDecrypt);
            out.println("Decrypted back: " + decryptVal[decryptVal.length - 1].toString());
            lbplanet.utilities.LPMailing.sendMailViaSSL("prueba SSL", "SSL esto es una prueba", new String[]{"info.fran.gomez@gmail.com"},
                    null, null, null);
            out.println("SSL Mail sent without attachment");
            lbplanet.utilities.LPMailing.sendMailViaSSL("prueba SSL", "SSL esto es una prueba", new String[]{"info.fran.gomez@gmail.com"},
                    null, null, new String[]{"d:/FE Refactoring LP.xlsx"});
            out.println("SSL Mail sent with attachment");

            lbplanet.utilities.LPMailing.sendMailViaTLS("prueba", "esto es una prueba desde Trazit",
                    new String[]{"info.fran.gomez@gmail.com", "fgomez@trazit.net", "jmallorqui@trazit.net"},
                    null, null, new String[]{});
            out.println("TLS Mail sent");
            lbplanet.utilities.LPMailing.sendMailViaTLS("prueba", "esto es una prueba",
                    new String[]{"info.fran.gomez@gmail.com", "fgomez@trazit.net", "jmallorqui@trazit.net"},
                    null, null, new String[]{}); //"d:/FE Refactoring LP.xlsx", "D:/LP-Documentacion/hexagon-white-blue-light.jpg"});
            out.println("TLS Mail sent");

            if (1 == 1) {
                return;
            }
            Rdbms.stablishDBConection("labplanet");

            JSONObject jObj = new JSONObject();
            jObj.put("hola", "adios");
            String[] fieldNames = new String[]{FIELDS_NAMES_PROCEDURE_NAME, "procedure_version", "proc_instance_name", "model_json"};
            Object[] fieldValues = new Object[]{"demo", 1, "demo", jObj};
            RdbmsObject insertRecord = Rdbms.insertRecord(TblsReqs.TablesReqs.PROC_FE_MODEL, fieldNames, fieldValues, PROC_NEW_EVENT_FLD_NAME);
            out.println(Arrays.toString(insertRecord.getApiMessage()));
            Token token = null;

            if (1 == 1) {
                return;
            }
            JSONObject procedure = new JSONObject();
            JSONObject procEventJson = new JSONObject();
            String[] procEventFldNameArray = PROC_NEW_EVENT_FLD_NAME.split("\\|");
            Rdbms.stablishDBConection("labplanet");
            out.println("************ ViewsData.SAMPLE_COC_NAMES_VIEW testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.SAMPLE_COC_NAMES_VIEW, "sample-coa-rel1", false, false, true, null));
            out.println("************ ViewsData.SAMPLE_COC_NAMES_VIEW testing. End");
            out.println("************ ViewsData.SAMPLE_COC_NAMES_VIEW NO testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.SAMPLE_COC_NAMES_VIEW, "sample-coa-rel1", false, false, false, null));
            out.println("************ ViewsData.SAMPLE_COC_NAMES_VIEW NO testing. End");
            /*out.println("************ ViewsData.SAMPLE_COC testing. Begin");  
out.println(createTableScript(TablesData.SAMPLE_COC, "sample-coa-rel1_testing", true, true));
out.println("************ ViewsData.SAMPLE_COC testing. End");  
out.println("************ ViewsData.SAMPLE_COC NO testing. Begin");  
out.println(createTableScript(TablesData.SAMPLE_COC, "sample-coa-rel1", true, true));
out.println("************ ViewsData.SAMPLE_COC NO testing. End");  
            

out.println("************ ViewsData.USER_AND_META_DATA_SOP_VIEW testing. Begin");  
out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.USER_AND_META_DATA_SOP_VIEW, "em-demo-a",false, false, true));
out.println("************ ViewsData.USER_AND_META_DATA_SOP_VIEW testing. End");  
out.println("************ ViewsData.USER_AND_META_DATA_SOP_VIEW NO testing. Begin");  
out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.USER_AND_META_DATA_SOP_VIEW, "em-demo-a", false, false, false));
out.println("************ ViewsData.USER_AND_META_DATA_SOP_VIEW NO testing. End");  
             */
            out.println("************ ViewsData.USER_AND_ANALYSISMETHOD_CERTIF_VIEW testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.USER_AND_ANALYSISMETHOD_CERTIF_VIEW, "proc-deploy", false, false, true, null));
            out.println("************ ViewsData.USER_AND_ANALYSISMETHOD_CERTIF_VIEW testing. End");
            out.println("************ ViewsData.USER_AND_ANALYSISMETHOD_CERTIF_VIEW NO testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.USER_AND_ANALYSISMETHOD_CERTIF_VIEW, "proc-deploy", false, false, false, null));
            out.println("************ ViewsData.USER_AND_ANALYSISMETHOD_CERTIF_VIEW NO testing. End");

            out.println("************ ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "em-demo-a", false, false, true, null));
            out.println("************ ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW testing. End");
            out.println("************ ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW NO testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW, "em-demo-a", false, false, false, null));
            out.println("************ ViewsData.SAMPLE_ANALYSIS_RESULT_WITH_SPEC_LIMITS_VIEW NO testing. End");

            out.println("************ ViewsData.SAMPLE_TESTING_GROUP_VIEW testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.SAMPLE_TESTING_GROUP_VIEW, "proc-deploy", false, false, true, null));
            out.println("************ ViewsData.SAMPLE_TESTING_GROUP_VIEW testing. End");
            out.println("************ ViewsData.SAMPLE_TESTING_GROUP_VIEW NO testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(TblsData.ViewsData.SAMPLE_TESTING_GROUP_VIEW, "proc-deploy", false, false, false, null));
            out.println("************ ViewsData.SAMPLE_TESTING_GROUP_VIEW NO testing. End");

            out.println("************ ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, "em-demo-a", false, false, true, null));
            out.println("************ ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW testing. End");
            out.println("************ ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW NO testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW, "em-demo-a", false, false, false, null));
            out.println("************ ViewsEnvMonData.SAMPLE_MICROORGANISM_LIST_VIEW NO testing. End");

            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VWtesting. Begin");
            out.println(EnumIntViews.getViewScriptCreation(ViewsInstrumentsData.NOT_DECOM_INSTR_EVENT_DATA_VW, "app-proc", false, false, true, null));
            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW testing. End");
            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW NO testing. Begin");
            out.println(EnumIntViews.getViewScriptCreation(ViewsInstrumentsData.NOT_DECOM_INSTR_EVENT_DATA_VW, "app-proc", false, false, false, null));
            out.println("************ ViewsAppProcData.NOT_DECOM_INSTR_EVENT_DATA_VW NO testing. End");

            if (1 == 1) {
                return;
            }

            /*        EnumIntTables[] tblsTesting = new EnumIntTables[]{TblsProcedure.TablesProcedure.PERSON_PROFILE, TblsProcedure.TablesProcedure.PROCEDURE_INFO,
            TblsProcedure.TablesProcedure.PROCEDURE_BUSINESS_RULE, TblsProcedure.TablesProcedure.PROCEDURE_EVENTS,
            , TblsTesting.TablesTesting.SCRIPT_STEPS,
            TblsTesting.TablesTesting.SCRIPT_BUS_RULES, TblsTesting.TablesTesting.SCRIPTS_COVERAGE, TblsTesting.TablesTesting.SCRIPT_SAVE_POINT,
            TblsCnfg.TablesConfig.SOP_META_DATA, TblsCnfg.TablesConfig.ZZZ_DB_ERROR, TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR,
            TablesData.USER_SOP,
            TablesProcedureAudit.PROC_HASH_CODES, TblsDataAudit.TablesDataAudit.SESSION};
        for (EnumIntTables curTbl: tblsTesting){
        }*/
            EnumIntTables curTbl = TblsTesting.TablesTesting.SCRIPT;
            String tblCreateScript3 = DeployTables.createTableScript(curTbl, "sample-coa-rel1", true, true, null);
            Rdbms.prepUpQueryWithDiagn(curTbl.getRepositoryName(), curTbl.getTableName(), tblCreateScript3, new Object[]{});

            if (1 == 1) {
                return;
            }
            Object[][] procEvent = Rdbms.getRecordFieldsByFilter("proc-deploy-procedure", TblsProcedure.TablesProcedure.PROCEDURE_VIEWS.getTableName(),
                    new String[]{TblsProcedure.ProcedureViews.NAME.getName(), TblsProcedure.ProcedureViews.ROLE_NAME.getName(), TblsProcedure.ProcedureViews.TYPE.getName() + " " + SqlStatement.WHERECLAUSE_TYPES.IN.getSqlClause()},
                    new String[]{"ER-FQ", "coordinator", AppProcedureListAPI.elementType.SIMPLE.toString().toLowerCase() + "|" + AppProcedureListAPI.elementType.TWOICONS.toString().toLowerCase()},
                    procEventFldNameArray, new String[]{TblsProcedure.ProcedureViews.ORDER_NUMBER.getName(), TblsProcedure.ProcedureViews.TYPE.getName(), TblsProcedure.ProcedureViews.POSITION.getName()});
            JSONObject procEventSopDetail = new JSONObject();
            for (Object[] procEvent1 : procEvent) {
                procEventJson = LPJson.convertArrayRowToJSONObject(procEventFldNameArray, procEvent1, null);
                procEventSopDetail = AppProcedureListAPI.procEventSops("9", "proc-deploy", procedure, procEventJson,
                        procEventFldNameArray, procEvent1,
                        Boolean.valueOf(LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING))));
            }
            if (1 == 1) {
                return;
            }
            out.println("Time to encrypt data ....");
            Object[] valsToEncrytp = new Object[]{"hola", new BigDecimal("123.2"), LPDate.getCurrentTimeStamp()};
            for (Object valueToEncrypt : valsToEncrytp) {
                out.println("Value: " + valueToEncrypt);
                Object[] encryptValue = DbEncryption.encryptValue(valueToEncrypt);
                out.println("Encrypted: " + encryptValue[encryptValue.length - 1].toString());
                Object[] decryptValue = DbEncryption.decryptValue(encryptValue[encryptValue.length - 1].toString());
                out.println("Decrypted back: " + decryptValue[decryptValue.length - 1].toString());
                out.println("");
            }
            Rdbms.stablishDBConection("demoplatform");
            tblCreateScript = createTableScript(TablesProcedureAudit.PROC_HASH_CODES,
                    "em-air-spr1", false, true, null);
            Rdbms.prepUpQueryWithDiagn(TablesProcedureAudit.PROC_HASH_CODES.getRepositoryName(),
                    TablesProcedureAudit.PROC_HASH_CODES.getTableName(),
                    tblCreateScript, new Object[]{});
            Rdbms.closeRdbms();
            if (1 == 1) {
                return;
            }
            Parameter parm = new Parameter();
            String filePrefix = "goleef1";
            String propFileName = Parameter.PropertyFilesType.ERROR_TRAPING.toString();
            ResourceBundle errorTrapFileEs = null;
            try {
                errorTrapFileEs = ResourceBundle.getBundle("parameter.LabPLANET." + filePrefix + "_es");
                out.println(filePrefix + " file found!");
                parm.addTagInPropertiesFile(propFileName, filePrefix, "demo", LPNulls.replaceNull("val"));
            } catch (Exception e) {
                parm.createPropertiesFile(propFileName, filePrefix + "_es");
                try {
                    String fileDir = parm.getFileDirByPropertyFileType(propFileName);
                    errorTrapFileEs = ResourceBundle.getBundle(fileDir + "." + filePrefix + "_es");
                } catch (Exception ex) {
                    out.println(ex.getMessage());
                    return;
                }
                out.println(filePrefix + " file created and found!");
            }

            if (1 == 1) {
                return;
            }

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet testingServer</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet testingServer at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
            try {
                Rdbms.stablishDBConection("labplanet");
                String schemaName = "em-demo-a";
                out.println(" " + TablesData.SAMPLE.getTableName() + " >> " + createTableScript(TablesData.SAMPLE, schemaName, false, true, null));
                out.println("");
                schemaName = "proc-deploy";
                out.println(schemaName + " " + TablesData.SAMPLE.getTableName() + " >> " + createTableScript(TablesData.SAMPLE, schemaName, false, true, null));
                out.println("");
                out.println(schemaName + " " + TablesData.PRODUCT.getTableName() + " >> " + createTableScript(TablesData.PRODUCT, schemaName, false, true, null));
                out.println("");
                /*            Rdbms.stablishDBConection("labplanet"); 
            schemaName="proc-deploy";
            Object[][] recordFieldsByFilter = getRecordFieldsByFilter(LPPlatform.buildSchemaName(schemaName, TablesData.SAMPLE.getRepositoryName()), TablesData.SAMPLE.getTableName(), 
                new EnumIntTableFields[]{SampleFlds.STATUS}, new Object[]{"REVIEWED"},
                new EnumIntTableFields[]{SampleFlds.LOGGED_ON, SampleFldsLOGGED_BY, SampleFldsSAMPLER, SampleFldsSAMPLE_ID, SampleFldsSTATUS},
                null, true);
            out.println(recordFieldsByFilter.length);
            out.println(recordFieldsByFilter[0][0].toString());
            out.println(recordFieldsByFilter[0][1].toString());
            out.println(recordFieldsByFilter[0][2].toString());
            out.println(recordFieldsByFilter[0][3].toString());
            out.println(recordFieldsByFilter[0][4].toString());*/
                if (1 == 1) {
                    return;
                }
            } catch (Exception e) {
                String errMsg = e.getMessage();
                out.println(errMsg);
            }
            try (io.github.classgraph.ScanResult scanResult = new ClassGraph().enableAllInfo()//.acceptPackages("com.xyz")
                    .scan()) {
                ClassInfoList classesImplementing = scanResult.getClassesImplementing("trazit.enums.EnumIntBusinessRules");
                ClassInfoList allEnums = scanResult.getAllEnums();
                out.println("business rules counter: " + classesImplementing.size());
                for (int i = 0; i < classesImplementing.size(); i++) {
                    out.println("enum name:" + classesImplementing.get(i).getName());
                    ClassInfo getMine = classesImplementing.get(i);
                    List<Object> enumConstantObjects = getMine.getEnumConstantObjects();
                    for (i = 0; i < enumConstantObjects.size(); i++) {
                        String name = ((EnumIntBusinessRules) enumConstantObjects.get(i)).getTagName();
                        out.println(name);
                        name = ((EnumIntBusinessRules) enumConstantObjects.get(i)).getAreaName();
                        out.println(name);
                        out.println(enumConstantObjects.get(i).getClass().getName());
                    }
                    /*                    
                    String methName="values";
                    out.println("has method "+methName+"? "+getMine.hasMethod(methName));
                    Class [] carr = new Class[]{};
                    Method method = (Method) getMine.getClass().getMethod("values").invoke(null);*/
//                    Field f = getMine.getClass().getDeclaredField("$VALUES");
//                    out.println(f);
//                    out.println(Modifier.toString(f.getModifiers()));
//                    f.setAccessible(true);
//                    Object o = f.get(null);
                    //return (E[]) o;                    
//                    Method method = null;
//                        Class<?>[] paramTypes = {String.class};
                    //method = this.getClass().getDeclaredMethod(aMethod, paramTypes);
//                        method=getMine.getClass().getMethod(methName, paramTypes);
//                    String[] parameters = new String[]{"hhhh"};
//                    method.invoke(this, paramTypes);

                    //MethodInfoList methodInfo = getMine.getMethodInfo("values");
                    //out.println(methodInfo.
                    //getMine.getClass().getMethod(name, parameterTypes)
                }
                ClassInfoList widgetClasses = scanResult.getClassesImplementing("com.xyz.Widget");
                List<String> widgetClassNames = widgetClasses.getNames();
                // ...
            } catch (Exception e) {
                out.println(e.getMessage());
                ScanResult.closeAll();
                return;
            }
            ScanResult.closeAll();
            if (1 == 1) {
                out.println("ended line 159");
                return;
            }
            ServiceLoader<EnumIntBusinessRules> loader = ServiceLoader.load(EnumIntBusinessRules.class);
            for (EnumIntBusinessRules implClass : loader) {
                out.println(implClass.getClass().getSimpleName()); // prints Dog, Cat
            }
            try {

                if (1 == 1) {
                    return;
                }
                lbplanet.utilities.LPMailing.sendMailViaTLS("prueba", "esto es una prueba desde Trazit", new String[]{"info.fran.gomez@gmail.com", "joel.sada.nillni@gmail.com"},
                        null, null, new String[]{});
                lbplanet.utilities.LPMailing.sendMailViaTLS("prueba", "esto es una prueba", new String[]{"info.fran.gomez@gmail.com"},
                        null, null, new String[]{}); //"d:/FE Refactoring LP.xlsx", "D:/LP-Documentacion/hexagon-white-blue-light.jpg"});
//lbplanet.utilities.LPMailing.sendMailViaSSL("prueba SSL", "SSL esto es una prueba", new String[]{"info.fran.gomez@gmail.com"}, 
//        null, null, new String[]{"d:/FE Refactoring LP.xlsx"});
//lbplanet.utilities.LPMailing.otroMailViaSSL();

                /*             javax.json.JsonObject empObject = Json.createObjectBuilder().add("empName", "Jai")
                                 .add("empAge", "25")
                                 .add("empSalary", "40000")
                                 .add("empAddress",
                                      Json.createObjectBuilder().add("street", "IDPL Colony")
                                                           .add("city", "Hyderabad")
                                                           .add("pinCode", "500072")
                                                           .build()
                                     )
                                 .add("phoneNumber",
                                      Json.createArrayBuilder().add("9959984000")
                                                               .add("7702144400")
                                                               .build()
                                      )
                                 .build();
             out.println(empObject); 
            JsonArray build = Json.createArrayBuilder().add(Json.createObjectBuilder()//.add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                    .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.INCIDENT.getTableName()).build()).build();
            out.println(build); 

                
    LPFilesTools.toCsvFromArray(true, "D:\\LP\\home\\toCsvFromArray.csv", new String[]{"bien bien", "bien"});            */
                String tblCreateScript2 = createTableScript(TblsInstrumentsDataAudit.TablesInstrumentsDataAudit.INSTRUMENTS, false, true, null);
                Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});

                if (1 == 1) {
                    return;
                }
                String procInstanceName2 = "proc-deploy";//"em-demo-a";
                Rdbms.stablishDBConection("labplanet");
                TestingCoverage tstCov = null;
                tstCov = new TestingCoverage(procInstanceName2, 1);
                tstCov = null;
                /*tstCov=new TestingCoverage(procInstanceName2, 2);    
tstCov=null;
tstCov=new TestingCoverage(procInstanceName2, 3);    
tstCov=null;
tstCov=new TestingCoverage(procInstanceName2, 4);    
tstCov=null;
                 */
                Object[] isSampleTestingGroupGenericAutoApproveEnabled = LPPlatform.isProcedureBusinessRuleEnable(procInstanceName2, DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_GENERICAUTOAPPROVEENABLED.getAreaName(), DataSampleRevisionTestingGroup.DataSampleRevisionTestingGroupBusinessRules.SAMPLETESTINGBYGROUP_GENERICAUTOAPPROVEENABLED.getTagName());
                out.println(Arrays.toString(isSampleTestingGroupGenericAutoApproveEnabled));

                LocalDateTime startDate = LocalDateTime.now();
                LocalDateTime endDate = LocalDateTime.now().plusSeconds(2).plusNanos(514);
                out.println("Seconds difference=" + " " + secondsInDateRange(startDate, endDate, true));
                if (1 == 1) {
                    return;
                }
                /*String procName="proc-deploy";
BusinessRules br=new BusinessRules(procName, 999);
String brName="sampleReviewer_canBeAnyTestingGroupReviewer";
String brValue=br.getProcedureBusinessRule(brName);
out.println(brName+" Business rule value="+brValue);
String ruleValue=Parameter.getBusinessRuleProcedureFile(procName, "procedure", brName);
out.println("ruleValue using Parameter.getBusinessRuleProcedureFile = "+ruleValue);
if (1==1) return;*/

                Object[] isReviewByTestingGroupEnable = LPPlatform.isProcedureBusinessRuleEnable("proc-deploy", "procedure",
                        "sampleGenericAutoApproveEnabled");
                out.println(Arrays.toString(isReviewByTestingGroupEnable));
                startDate = LocalDateTime.now();
                int[] plusDays = new int[]{0, 1, 2, 200, 400};
                for (int curPlusDays : plusDays) {
                    endDate = LocalDateTime.now().plusDays(curPlusDays);
                    out.println("Days difference=" + curPlusDays + " " + secondsInDateRange(startDate, endDate, false));
                    long[] intervals = new long[]{-1, 0, 172000, 173000, 15280008, 18280008, 24560009, 34560009, 94560009};
                    for (long interval : intervals) {
                        out.println("interval:" + interval + " " + Arrays.toString(IntervalsUtilities.isTheIntervalIntoTheDatesRange(interval, startDate, endDate)));
                    }
                }
                Rdbms.stablishDBConection("labplanet");
                tblCreateScript2 = createTableScript(TblsProcedure.TablesProcedure.SAMPLE_STAGE_TIMING_INTERVAL_DEVIATION, "em-demo-a", false, true, null);
                Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});

                if (1 == 1) {
                    return;
                }
                Rdbms.closeRdbms();
                Rdbms.stablishDBConection("labplanet");
                markAsExpiredTheExpiredObjects("proc-deploy");
                if (1 == 1) {
                    return;
                }
                Rdbms.stablishDBConectionTester();
                //isConnected = Rdbms.getRdbms().startRdbms(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);      

                out.println("Hello");
                out.println(Rdbms.dbViewExists("em-demo-a", "em-demo-a", "data", "pr_scheduled_locations")[0].toString());
                dbTableExists("em-demo-a", "em-demo-a-data", "sample");
                procInstanceName = "em-demo-a";
                Object[] dbSchemaAndTestingSchemaTablesAndFieldsIsMirror = Rdbms.dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_TESTING.getName());
                Object[][] mismatches = (Object[][]) dbSchemaAndTestingSchemaTablesAndFieldsIsMirror[0];
                if (Boolean.FALSE.equals(LPPlatform.LAB_TRUE.equalsIgnoreCase(mismatches[0][0].toString()))) {
                    out.println("the schemas " + procInstanceName + "-" + GlobalVariables.Schemas.DATA.getName()
                            + " and " + procInstanceName + "-" + GlobalVariables.Schemas.DATA_TESTING.getName() + " are not mirror, see below the mismatches (" + dbSchemaAndTestingSchemaTablesAndFieldsIsMirror.length + "):");
                    out.print("<table><tr><th>Table</th><th>Field</th></tr></table>");
                    for (Object[] curRow : mismatches) {
                        out.print("TBL: " + curRow[0].toString() + "+FLD: " + curRow[1].toString() + ";    ");
                    }
                    return;
                }
                Rdbms.closeRdbms();
                if (1 == 1) {
                    return;
                }
                Rdbms.stablishDBConection("tst");

                String functionCr = " CREATE OR REPLACE FUNCTION public.isnumeric(text) RETURNS boolean LANGUAGE plpgsql";
                functionCr = functionCr + " IMMUTABLE STRICT ";
                functionCr = functionCr + " AS $function$ DECLARE x NUMERIC; BEGIN x = $1::NUMERIC; RETURN TRUE; EXCEPTION WHEN others THEN RETURN FALSE; END; $function$ ";

//Rdbms.prepRdQuery(functionCr, null);
                Rdbms.prepUpQuery(functionCr, null);
//Rdbms.updateRecordFieldsByFilter(functionCr, functionCr, updateFieldNames, updateFieldValues, whereFieldNames, whereFieldValues)
                if (1 == 1) {
                    return;
                }

                Rdbms.stablishDBConection("labplanet");

                markAsExpiredTheExpiredObjects("proc-deploy");
                if (1 == 1) {
                    return;
                }
            } catch (Exception e) {
                out.println(e.getMessage());
                return;
            }
            ConfigSpecRule mSpec = new ConfigSpecRule();
            Object[] resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(
                    csvExtractFieldValueBigDecimal(-2.5), csvExtractFieldValueBigDecimal(5), csvExtractFieldValueBigDecimal(5), null);
            out.println(Arrays.toString(resSpecEvaluation));
            resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(
                    csvExtractFieldValueBigDecimal(-2.5), csvExtractFieldValueBigDecimal(5.1), csvExtractFieldValueBigDecimal(5), null);
            out.println(Arrays.toString(resSpecEvaluation));
            if (1 == 1) {
                return;
            }
            procInstanceName = "em-air-allv2";
            Rdbms.stablishDBConection("labplanet");

//        String tblCreateScript2=TblsData.ViewSampleTestingGroup.createTableScript("proc-deploy", new String[]{""});
//        Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
//            String tblCreateScript = TblsTesting.ScriptsCoverage.createTableScript(procInstanceName, new String[]{""});
//        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            //jsonObj.put("TblsTesting.ScriptsCoverage", tblCreateScript);
            if (1 == 1) {
                return;
            }

            FromInstanceToInstance.tableContent(TblsCnfg.TablesConfig.UOM, "labplanet", "modules_trazit");
//        String tblCreateScript2=TblsProcedureAudit.Investigation.createTableScript("em-demo-a", new String[]{""});
//        Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});

//            Object[][] lblContent = new Object[][]{{"TEXT", "Ejemplo", 1, 1, 14}, {"BARCODE39", "123", 1, 1, 14, 1, 1}};
//            ZPL.zplLabel("", 5, lblContent);            
//Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable("proc-deploy", "procedure", DataSampleRevisionTestingGroup.TestingGroupFileProperties.sampleTestingByGroup_ReviewByTestingGroup.toString());            
            //String tblCreateScript2=TblsData.SampleRevisionTestingGroup.createTableScript("proc-deploy", new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
//EndPointsToRequirements.EndpointDefinition();
//        String tblCreateScript2=TblsCnfgAudit.Spec.createTableScript("proc-deploy", new String[]{""});
//        Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
//        String tblCreateScript2=TblsCnfg.zzzDbErrorLog.createTableScript("config", new String[]{""});
//        Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
//        String tblCreateScript2=TblsApp.VideoTutorial.createTableScript(new String[]{""});
//        Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
//if  (1==1)      return;
//lbplanet.utilities.LPMailing.sendMailViaTLS("prueba", "esto es una prueba", new String[]{"info.fran.gomez@gmail.com"}, 
//        null, null, new String[]{"d:/FE Refactoring LP.xlsx", "D:/LP-Documentacion/hexagon-white-blue-light.jpg"});
//lbplanet.utilities.LPMailing.sendMailViaSSL("prueba SSL", "SSL esto es una prueba", new String[]{"info.fran.gomez@gmail.com"}, 
//        null, null, new String[]{"d:/FE Refactoring LP.xlsx"});
//lbplanet.utilities.LPMailing.otroMailViaSSL();
//LPFilesTools.fromCsvToArray("D:\\LP\\testingRepository-20200203\\spec_limits.csv", '.');
//LPFilesTools.toCsvFromArray(true, "D:\\LP\\home\\toCsvFromArray.csv", new String[]{"bien bien", "bien"});
//TblsReqs.ProcedureUserRequirements.
//            List<String[]> fromCsvToArray = LPFilesTools.fromCsvToArray("D:\\LP\\testingRepository-20200203\\spec_limits.csv", '.');
//Rdbms.stablishDBConectionTester();
//insertRecordInTableFromTable(true, TblsReqs.ProcedureUserRequirementsEvents.getAllFieldNames(),
//        GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.TablesReqs.PROCEDURE_USER_REQS_EVENTS.getTableName(), 
//        new String[]{TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirementsEvents.SCHEMA_PREFIX.getName()},
//        new Object[]{"proc-deploy", 1, "proc-deploy"},
//        LPPlatform.buildSchemaName("proc-deploy", GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROCEDURE_EVENTS.getTableName(), TblsProcedure.ProcedureEvents.getAllFieldNames());
//            if (1==1) return;            
//            out.println(SomeEnumClass.getCell(1));
//            out.println(OtherEnumClass.getCell(1));
//            MyEnum m = null;            
//            out.println("Fran "+m.getByIndexFran(1));
/*
            Integer[] misEnterosArr = new Integer[5];
            Integer valorABuscar=12;
            misEnterosArr[0]=1;
            misEnterosArr[1]=12;
            misEnterosArr[2]=41;
            misEnterosArr[3]=44;
            misEnterosArr[4]=87;
            String dameLaPosicion=null;
            int indice=0;
            Integer indiceEnArray=null;
            Boolean noOut=true;
            Integer tamañoArr=misEnterosArr.length;
            while(noOut){
            indiceEnArray=Integer.valueOf(tamañoArr/2);
            if (misEnterosArr[indiceEnArray]==valorABuscar){
            dameLaPosicion=indiceEnArray.toString();
            noOut=false;
            }else{
            if (misEnterosArr[indiceEnArray]<valorABuscar) tamañoArr
            }
            }
            //for (Integer currInt: misEnterosArr){
            //    if (currInt==valorABuscar) {
            //        dameLaPosicion=String.valueOf(indice);
            //        break;
            //    }
            //    indice++;
            //}
            if (dameLaPosicion==null) dameLaPosicion="No existe";
            //dameLaPosicion == null ? "No existe" : String.valueOf(indice);
            String msgStr="El valor "+valorABuscar.toString()+" está en la posición "+dameLaPosicion;
            out.println(msgStr);
            if (1==1) return;
            if (1==1) return;
             */
            Rdbms.stablishDBConectionTester();
//String scriptToAddCertifToAnyObject = CertifGlobalVariables.getScriptToAddCertifToAnyObject("mp-release1", "data", "user_sop");
//Rdbms.prepRdQuery(scriptToAddCertifToAnyObject, new Object[]{});
            String[][] endpointArr = new String[2][2];
            endpointArr[0][0] = "SampleAPIfrontendEndpoints";
            endpointArr[0][1] = "GET_METHOD_CERTIFIED_USERS_LIST";
            endpointArr[1][0] = "SampleAPIfrontendEndpoints";
            endpointArr[1][1] = "KPIS";
            for (String[] curEntry : endpointArr) {
                String apiName = curEntry[0];//"SampleAPIfrontendEndpoints";
                String endPointName = curEntry[1];//"GET_METHOD_CERTIFIED_USERS_LIST";
                Object[] docInfoForEndPoint = getDocInfoForEndPoint(apiName, endPointName);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(docInfoForEndPoint[0].toString())) {
                    out.println("No doc info for " + apiName + "." + endPointName);
                } else {
                    out.println(apiName + "." + endPointName + " >> " + Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString((String[]) docInfoForEndPoint[0], (Object[]) docInfoForEndPoint[1], LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)));
                }
            }
            if (1 == 1) {
                return;
            }
            procInstanceName = "em-demo-a";
            /*
        String tblCreateScript=TblsTesting.Script.createTableScript(procInstanceName, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
        tblCreateScript=TblsTesting.ScriptSteps.createTableScript(procInstanceName, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
        
        String tblCreateScript2=TblsApp.Incident.createTableScript(new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});

        tblCreateScript2=TblsAppAudit.Session.createTableScript("", new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});

procInstanceName="genoma-1";
        tblCreateScript2=TblsCnfg.SopMetaData.createTableScript(procInstanceName, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
        
        tblCreateScript2=TblsData.UserSop.createTableScript(procInstanceName, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});

        tblCreateScript2=TblsData.ViewUserAndMetaDataSopView.createTableScript(procInstanceName, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});
             */
            String sch = "\"em-demo-a-data_audit";
            String schManip = sch;
            if (sch.contains("data")) {
                if (schManip.endsWith("\"")) {
                    schManip = schManip.substring(0, schManip.length() - 1) + "_testing\"";
                } else {
                    schManip = VALIDATION_MODE_REPO + sch ;
                }
            }
            out.println(sch);
            out.println(schManip);
            if (1 == 1) {
                return;
            }
            Rdbms.stablishDBConectionTester();
            Object[] createAppUser = UserAndRolesViews.createAppUser("obustos", null, null);
            out.println(Arrays.toString(createAppUser));
            if (1 == 1) {
                return;
            }

            String batchContentStr = "";
            Integer batchNumRows = 5;
            Integer batchNumCols = 3;
            out.println(batchContentStr);
//add       
            Integer sampleId = -1;
            Integer pendingIncubationStage = -1;
            Integer row = -1;
            Integer col = -1;
            /*
Object[][] objectsToAdd=new Object[][]{{12,1,1,2},{55,2,5,1},{53,2,5,3}};
    for (Object[] curObj: objectsToAdd){
        sampleId=(Integer) curObj[0];
        pendingIncubationStage=(Integer) curObj[1];
        row=(Integer) curObj[2];
        col=(Integer) curObj[3];
        if ((batchContentStr==null) || (batchContentStr.length()==0)){
            batchContent2D=new String[batchNumRows][0];
            for (int i=0;i<batchNumCols;i++)
                batchContent2D=LPArray.convertObjectArrayToStringArray(LPArray.addColumnToArray2D(batchContent2D, "EMPTY"));
                //batchContent2D=LPArray.setColumnValueToArray2D(batchContent2D, i, " ");
            batchContent1D=LPArray.array2dTo1d(batchContent2D);
        }else{
            batchContent1D=batchContentStr.split(batchContentSeparatorStructuredBatch);
            batchContent2D=LPArray.array1dTo2d(batchContent1D, batchNumCols);            
        }
        batchContent2D[row-1][col-1]=DataBatchIncubatorStructured.buildBatchPositionValue(sampleId, pendingIncubationStage);
        batchContentStr=LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), batchContentSeparatorStructuredBatch, "");        
        out.println(batchContentStr);
        out.println("batchContent1D:"+batchContent1D.length);        
        out.println("batchContent2D:"+batchContent2D.length+"x"+batchContent2D[0].length);        
    }


//remove 
row=5;
col=3;
String positionValueToFind=sampleId.toString()+"*"+pendingIncubationStage.toString();
        batchContent1D=batchContentStr.split(batchContentSeparatorStructuredBatch);
        Integer valuePosition=LPArray.valuePosicInArray(batchContent1D, positionValueToFind);
        if (valuePosition==-1)
            out.println("The sample "+sampleId.toString()+" is not part of the batch or not in position "+row.toString()+"x"+col.toString());
            //Object[] m=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "The sample <*1*> is not part of the batch <*1*> in procedure <*2*>", null);
        out.println(batchContentStr);
        batchContent1D[valuePosition]="";  
        batchContent2D=LPArray.array1dTo2d(batchContent1D, batchNumCols);

        batchContentStr=LPArray.convertArrayToString(LPArray.array2dTo1d(batchContent2D), batchContentSeparatorStructuredBatch, "");        
out.println("batchContent1D:"+batchContent1D.length);        
out.println("batchContent2D:"+batchContent2D.length+"x"+batchContent2D[0].length);        
out.println(batchContentStr);
out.println("FIN");        
//if (1==1) return;
             */
            Object[] isConnected = new Object[0];
            isConnected = Rdbms.stablishDBConectionTester();
            //isConnected = Rdbms.getRdbms().startRdbms(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);      

            out.println("Hello");
            out.println(Rdbms.dbViewExists("em-demo-a", "em-demo-a", "data", "pr_scheduled_locations")[0].toString());
            out.println(Rdbms.dbViewExists("requirements", "requirements", "", "pr_scheduled_locations")[0].toString());
            out.println(Rdbms.dbViewExists("em-demo-a", "em-demo-a", "data", "padsasr_scheduled_locationssss")[0].toString());
            out.println("Bye");
//if (1==1) return;

            if (Boolean.TRUE.equals(isConnected[0])) {
                out.println("Connected to the db !:)");
            } else {
                out.println("NOT Connected to the db :( " + Arrays.toString(isConnected));
            }
            token = new Token("eyJ1c2VyREIiOiJtYXJjIiwiZVNpZ24iOiJ2YWxlZSIsInVzZXJEQlBhc3N3b3JkIjoiYXVsaW4iLCJ1c2VyX3Byb2NlZHVyZXMiOiJbZW0tZGVtby1hXSIsInR5cCI6IkpXVCIsImFwcFNlc3Npb25JZCI6IjExNTQxIiwiYXBwU2Vzc2lvblN0YXJ0ZWREYXRlIjoiVHVlIFNlcCAwOCAyMjoyNDoxNyBDRVNUIDIwMjAiLCJ1c2VyUm9sZSI6ImNvb3JkaW5hdG9yIiwiYWxnIjoiSFMyNTYiLCJpbnRlcm5hbFVzZXJJRCI6IjEyIn0.eyJpc3MiOiJMYWJQTEFORVRkZXN0cmFuZ2lzSW5UaGVOaWdodCJ9.IOclIuaUZ-OhyTif3Mmmt4r9m8F9PYC2ECZ4coX3Jno");
            out.println("Today in Date format: " + LPDate.getTimeStampLocalDate().toString());
            //out.println("Today in DateTime format: "+LPDate.getDateTimeLocalDate().toString());
/*            
            sampleId=138;

            
            DataModuleSampleAnalysis smpAna = new DataModuleSampleAnalysis();   
            DataSample smp = new DataSample(smpAna);   
            Object[] dataSample = smp.setSamplingDate(sampleId);
            out.println("log for setSamplingDate: "+Arrays.toString(dataSample));
            
            out.println("Testing Files Path: "+LPTestingOutFormat.TESTING_FILES_PATH);
            out.println(LPDate.getTimeStampLocalDate().toString());
            String procName="process-us";
            
            out.println("Extract portion, extraer 5 sobre una cantidad de 4: "+Arrays.toString(LPMath.extractPortion(procName, 
                    BigDecimal.valueOf(4), "MG",1, BigDecimal.valueOf(5), "MG", 2)));
            out.println("Extract portion, extraer 4 sobre una cantidad de 4: "+Arrays.toString(LPMath.extractPortion(procName, 
                    BigDecimal.valueOf(4), "MG",1, BigDecimal.valueOf(4), "MG", 2)));
            
            out.println("Statuses en inglés: "+Arrays.toString(DataSampleUtilities.getSchemaSampleStatusList(procName)));
            out.println("Statuses en castellano: "+Arrays.toString(DataSampleUtilities.getSchemaSampleStatusList(procName, "es")));
            DataSampleAnalysisBusinessRules
            out.println("First sample analysis status for process-us is: "+Parameter.getBusinessRuleProcedureFile("process-us", DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSFIRST.getAreaName(), DataSampleAnalysis.DataSampleAnalysisBusinessRules.SAMPLEANALYSIS_STATUSFIRST.getTagName()));
            
            String[] errObject = new String[]{"Servlet sampleAPI at " + request.getServletPath()};          
            
            String[] sampleFieldName = new String[]{TblsData.Sample.SPEC_CODE_VERSION.getName()};
            String[][] specFields = new String[][]{{TblsData.Sample.SPEC_CODE.getName(), ""}, {TblsData.Sample.SPEC_CODE_VERSION.getName(), ""}, {TblsData.Sample.SPEC_VARIATION_NAME.getName(), ""}};
            String[] specMissingFields = new String[0];
            for (String[] curValue: specFields){
                Integer posicField = LPArray.valuePosicInArray(sampleFieldName, curValue[0]);
                if (posicField == -1){specMissingFields = LPArray.addValueToArray1D(specMissingFields, curValue);
                }else{curValue[1] = "H";}                
            }
            for (String[] curField: specFields){
                out.println("specFields "+Arrays.toString(curField));    
            }
            Object[] firstArray=new Object[]{"A", "B"};
            Object[] secondArray=new Object[]{"1", "2", 3};
                String[] myJoinedArray=LPArray.joinTwo1DArraysInOneOf1DString(firstArray, secondArray, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
                out.println("joining two arrays of "+Arrays.toString(firstArray)+" and "+Arrays.toString(secondArray)+" with the separator "+LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+" I obtained "+Arrays.toString(myJoinedArray));
            firstArray=new Object[]{"A", "B","c", "Z"};
                myJoinedArray=LPArray.joinTwo1DArraysInOneOf1DString(firstArray, secondArray, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
                out.println("joining two arrays of "+Arrays.toString(firstArray)+" and "+Arrays.toString(secondArray)+" with the separator "+LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR+" I obtained "+Arrays.toString(myJoinedArray));
            
            out.println("The name for the table Session in db is "+ TblsApp.AppSession.valueOf("TBL").getName());
            out.println("The name for the field Session_id in db is "+TblsApp.AppSession.valueOf("SESSION_ID").getName());
            String myTableName=TblsApp.TablesApp.USERS.getTableName();
            out.println("The table name with NO valueOf() is "+myTableName);
            myTableName=TblsApp.Users.valueOf("TBL").getName();
            out.println("The table name WITH valueOf() is "+myTableName);
            
            
            String procInstanceNameSampleInfo="oil-pl1";
            Integer selSample=134;
            Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceNameSampleInfo, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                    new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{selSample}, 
                    new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.STATUS.getName()});
            out.println("Info from "+procInstanceNameSampleInfo+".sample "+selSample.toString()+": "+Arrays.toString(sampleInfo[0]));

            procInstanceNameSampleInfo="em-demo-a";
            selSample=160;
            sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceNameSampleInfo, GlobalVariables.Schemas.DATA.getName()), TblsData.TablesData.SAMPLE.getTableName(), 
                    new String[]{TblsData.Sample.SAMPLE_ID.getName()}, new Object[]{selSample}, 
                    new String[]{TblsData.Sample.SAMPLE_ID.getName(), TblsData.Sample.STATUS.getName(), TblsData.Sample.STATUS_PREVIOUS.getName()});
            out.println("Info from "+procInstanceNameSampleInfo+".sample "+selSample.toString()+": "+Arrays.toString(sampleInfo[0]));
        
        JSONObject jsonObj = new JSONObject();
             */
 /*
        String schemaNamePrefix="em-demo-a";
        tblCreateScript=TblsEnvMonitConfig.InstrIncubator.createTableScript(schemaNamePrefix, new String[]{""});
        tblCreateScript=TblsEnvMonitConfig.IncubBatch.createTableScript(schemaNamePrefix, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsEnvMonitConfig.InstrIncubator", tblCreateScript);

        tblCreateScript=TblsEnvMonitDataAudit.IncubBatch.createTableScript(schemaNamePrefix, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsEnvMonitConfig.InstrIncubator", tblCreateScript);

        tblCreateScript=TblsEnvMonitProcedure.IncubatorTempReadingViolations.createTableScript(schemaNamePrefix, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsEnvMonitProcedure.IncubatorTempReadingViolations", tblCreateScript);
        out.println("TblsEnvMonitProcedure.IncubatorTempReadingViolations ");
             */
 /*        tblCreateScript=TblsEnvMonitData.ViewSampleMicroorganismList.createTableScript(schemaNamePrefix, new String[]{""});        
        //tblCreateScript=TblsEnvMonitData.IncubBatch.createTableScript(schemaNamePrefix, new String[]{""});
        //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});        
        //jsonObj.put("TblsEnvMonitData.InstrIncubatorNoteBook", tblCreateScript);
        
        tblCreateScript=TblsData.ViewUserAndMetaDataSopView.createTableScript(schemaNamePrefix, new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsData.ViewUserAndMetaDataSopView", tblCreateScript);

        tblCreateScript=TblsApp.HolidaysCalendarDate.createTableScript(new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        jsonObj.put("TblsApp.HolidaysCalendarDate", tblCreateScript);
             */

            //           DbObjects.createPlatformSchemas();
//            DbObjects.createDBTables("", new String[]{});
            //EnvMonitSchemaDefinition.createDBTables(schemaNamePrefix, new String[]{});
//            tblCreateScript=TblsCnfg.zzzPropertiesMissing.createTableScript(schemaNamePrefix, new String[]{""});
//            Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
//            tblCreateScript=TblsEnvMonitConfig.ProgramCalendarDate.createTableScript(schemaNamePrefix, new String[]{""});
//            Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
//            tblCreateScript=TblsEnvMonitConfig.ProgramCalendarRecursiveEntries.createTableScript(schemaNamePrefix, new String[]{""});
//            Rdbms.prepRdQuery(tblCreateScript, new Object[]{});

            /*String procName="LlenadoVialesFA2018";
Integer calendarId=5;
String locationName="E01";
String[] fieldName=new String[]{"day_of_week"};
Object[] fieldValue=new Object[]{"TUESDAYS"};
Object[] addRecDiag=new Object[]{};
String schemaNamePrefix="";
addRecDiag=ConfigProgramCalendar.addRecursiveScheduleForLocation(schemaNamePrefix, procName, calendarId, locationName, 
        fieldName, fieldValue);
//out.println("Adding recursive schedule for location E01 on TUESDAYS: "+Arrays.toString(addRecDiag));
 locationName="E02";
 fieldName=new String[]{"day_of_week"};
 fieldValue=new Object[]{"FRIDAYS"};
 //addRecDiag=ConfigProgramCalendar.addRecursiveScheduleForLocation(schemaNamePrefix, procName, calendarId, locationName, 
 //       fieldName, fieldValue);
out.println("Adding recursive schedule for location E01 on FRIDAYS: "+Arrays.toString(addRecDiag));
String holidaysCalendar="España Comunidad X 2019";
             */
//Object[] addHolidayCal=ConfigProgramCalendar.importHolidaysCalendarSchedule(schemaNamePrefix, procName, calendarId, holidaysCalendar);
//out.println("Adding holidays calendar for "+holidaysCalendar+": "+Arrays.toString(addHolidayCal));
            //ProcedureDefinitionToInstance.createDBProcessSchemas("samples");
            //ProcedureDefinitionToInstance.createDBProcessTables("pepe", "", new String[]{});
            //String schemaNamePrefix="";
            //String tblCreateScript=TblsCnfg.zzzDbErrorLog.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
            //schemaNamePrefix="em-demo-a";
            //String tblCreateScript=TblsData.ViewSampleAnalysisResultWithSpecLimits.createTableScript(schemaNamePrefix, new String[]{""});
            //Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
//             schemaNamePrefix="em-demo-a";
            //tblCreateScript=TblsEnvMonitData.ProductionLot.createTableScript(schemaNamePrefix, new String[]{""});
//            Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
//Structured Batches. Begin
            Object[] diagn = new Object[0];
            out.println("Start testing for Structured Batches");
            procInstanceName = "em-demo-a";
            String previousBatchName = "Testeo Estructurada10";
            String batchName = "Testeo Estructurada12";
            Integer batchTemplate = 2;
            Integer batchTemplateVersion = 1;
            Integer[] smpls = new Integer[]{1000, 1001, 1002, 1003, 1009, 1011, 1012, 1013, 1014};
            /*
    diagn=DataBatchIncubator.batchRemoveSample(previousBatchName, batchTemplate, 1, smpls[8]);
    out.println("batchRemoveSample"+diagn[0]+diagn[diagn.length-1]);    
    
    for (Integer curSmp: smpls){
        diagn=DataBatchIncubator.batchRemoveSample(previousBatchName, batchTemplate, 1, curSmp);
        out.println("batchRemoveSample"+diagn[0]+diagn[diagn.length-1]);
    }
             */
//    diagn=DataBatchIncubator.removeBatch(batchName);
//    out.println("removeBatch"+diagn[0]+diagn[diagn.length-1]);

            diagn = DataBatchIncubator.createBatch(batchName, batchTemplate, batchTemplateVersion, null, null);
            out.println("createBatch" + diagn[0] + diagn[diagn.length - 1]);

            /*    
    diagn=DataBatchIncubator.batchRemoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[0]);
    out.println("batchRemoveSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchRemoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[1]);
    out.println("batchRemoveSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchRemoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[2]);
    out.println("batchRemoveSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchRemoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[3]);
    out.println("batchRemoveSample"+diagn[0]+diagn[diagn.length-1]);
             */
            diagn = DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[4], 2, 1, true);
            out.println("batchAddSample" + diagn[0] + diagn[diagn.length - 1]);
            diagn = DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[5], 1, 2, false);
            out.println("batchAddSample" + diagn[0] + diagn[diagn.length - 1]);
            diagn = DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[6], 5, 5, false);
            out.println("batchAddSample" + diagn[0] + diagn[diagn.length - 1]);
            diagn = DataBatchIncubator.batchRemoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[5]);
            out.println("batchRemoveSample" + diagn[0] + diagn[diagn.length - 1]);
            diagn = DataBatchIncubator.batchMoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[5], 5, 5, false);
            out.println("batchMoveSample" + diagn[0] + diagn[diagn.length - 1]);
            diagn = DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[7], 3, 3, true);
            out.println("batchAddSample" + diagn[0] + diagn[diagn.length - 1]);
            diagn = DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[8], 2, 3, true);
            out.println("batchAddSample" + diagn[0] + diagn[diagn.length - 1]);
            diagn = DataBatchIncubator.batchMoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[8], 3, 3, true);
            out.println("batchMoveSample" + diagn[0] + diagn[diagn.length - 1]);

//Structured Batches. End
            out.println("Before creating the token");
            String myToken2 = token.createToken(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW, "3", "Admin", "", "", "", null, "");
            out.println("Token created: " + myToken2);

            out.println("Reading web text file");
            StringBuilder exampleUrl = new StringBuilder(0).append("http://");
            exampleUrl.append("51.").append("75.");
            exampleUrl.append("202.").append("142").append(":8").append("888").append("/myfiles/txtfile.txt");
            final URL url = new URL(exampleUrl.toString());
            final StringBuilder sb = new StringBuilder(0);

            final char[] buf = new char[4096];

            final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT);

            try (
                    final InputStream in = url.openStream();
                    final InputStreamReader reader = new InputStreamReader(in, decoder);) {
                int nrChars;
                while ((nrChars = reader.read(buf)) != -1) {
                    sb.append(buf, 0, nrChars);
                }
            }

            final String test = sb.toString();
            out.println("Test File Content: <br>" + test);

            String csvFileName = "noDBSchema_config_SpecQualitativeRuleGeneratorChecker.txt";

            String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH + csvFileName;
            String csvFileSeparator = LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;

            Object[][] csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator);
            out.println("csv File Content: <br>" + LPTestingOutFormat.convertArrayInHtmlTable(csvFileContent));

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null, null);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
