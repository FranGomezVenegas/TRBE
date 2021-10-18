/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.labplanet.servicios.app;


import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import databases.Rdbms;
import static databases.Rdbms.dbTableExists;
import databases.TblsCnfg;
import databases.TblsData;
import databases.TblsProcedure;
import databases.Token;
import functionaljavaa.datatransfer.FromInstanceToInstance;
import functionaljavaa.intervals.IntervalsUtilities;
import functionaljavaa.inventory.batch.DataBatchIncubator;
import functionaljavaa.materialspec.ConfigSpecRule;
//import functionaljavaa.parameter.Parameter;
import static functionaljavaa.platform.doc.EndPointsToRequirements.getDocInfoForEndPoint;
//import functionaljavaa.samplestructure.DataSampleAnalysis;
import functionaljavaa.testingscripts.LPTestingOutFormat;
import static functionaljavaa.testingscripts.LPTestingOutFormat.csvExtractFieldValueBigDecimal;
import functionaljavaa.testingscripts.TestingCoverage;
import functionaljavaa.user.UserAndRolesViews;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPDate;
import static lbplanet.utilities.LPDate.SecondsInDateRange;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import static trazit.session.ProcReqSessionAutomatisms.markAsExpiredTheExpiredObjects;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.parameter.Parameter;
import functionaljavaa.samplestructure.DataSampleRevisionTestingGroup;



/**
 *
 * @author Administrator
 */
public class TestingServer extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet testingServer</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet testingServer at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
            try{
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
            JsonArray build = Json.createArrayBuilder().add(Json.createObjectBuilder()//.add("repository", GlobalVariables.Schemas.APP.getName())
                    .add("table", TblsApp.Incident.TBL.getName()).build()).build();
            out.println(build); 
LPFilesTools.toCsvFromArray(true, "D:\\LP\\home\\toCsvFromArray.csv", new String[]{"bien bien", "bien"});            */
/*String procName="proc-deploy";
BusinessRules br=new BusinessRules(procName, 999);
String brName="sampleReviewer_canBeAnyTestingGroupReviewer";
String brValue=br.getProcedureBusinessRule(brName);
out.println(brName+" Business rule value="+brValue);
String ruleValue=Parameter.getBusinessRuleProcedureFile(procName, "procedure", brName);
out.println("ruleValue using Parameter.getBusinessRuleProcedureFile = "+ruleValue);
if (1==1) return;*/
Object[] isReviewByTestingGroupEnable=LPPlatform.isProcedureBusinessRuleEnable("proc-deploy", "procedure", 
        "sampleGenericAutoApproveEnabled");            
out.println(Arrays.toString(isReviewByTestingGroupEnable));
LocalDateTime startDate=LocalDateTime.now();        
int[] plusDays=new int[]{0, 1, 2, 200, 400};
for (int curPlusDays: plusDays){
    LocalDateTime endDate=LocalDateTime.now().plusDays(curPlusDays);
    out.println("Days difference="+String.valueOf(curPlusDays)+" "+SecondsInDateRange(startDate, endDate));
    long[] intervals=new long[]{-1, 0, 172000, 173000, 15280008, 18280008, 24560009, 34560009, 94560009};
    for (long interval:intervals){
        out.println("interval:"+interval+" "+Arrays.toString(IntervalsUtilities.isTheIntervalIntoTheDatesRange(interval, startDate, endDate)));
    }
}
Rdbms.stablishDBConection("labplanet");
String tblCreateScript2=TblsProcedure.SampleStageTimingIntervalDeviation.createTableScript("em-demo-a", new String[]{""});
Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});


if (1==1) return;  
Rdbms.closeRdbms();
Rdbms.stablishDBConection("labplanet");
markAsExpiredTheExpiredObjects("proc-deploy");
if (1==1) return;  
                Object[] isConnected = Rdbms.stablishDBConectionTester();
                //isConnected = Rdbms.getRdbms().startRdbms(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);      

out.println("Hello");
out.println(Rdbms.dbViewExists("em-demo-a", "data", "pr_scheduled_locations")[0].toString());
                Object[] dbTableExists = dbTableExists("em-demo-a-data", "sample");
String procInstanceName="em-demo-a";
    Object[] dbSchemaAndTestingSchemaTablesAndFieldsIsMirror = Rdbms.dbSchemaAndTestingSchemaTablesAndFieldsIsMirror(procInstanceName, GlobalVariables.Schemas.DATA.getName(), GlobalVariables.Schemas.DATA_TESTING.getName());
    Object[][] mismatches= (Object[][]) dbSchemaAndTestingSchemaTablesAndFieldsIsMirror[0];
    if (!LPPlatform.LAB_TRUE.equalsIgnoreCase(mismatches[0][0].toString())){
        out.println("the schemas "+procInstanceName+"-"+GlobalVariables.Schemas.DATA.getName()
            +" and "+procInstanceName+"-"+GlobalVariables.Schemas.DATA_TESTING.getName()+" are not mirror, see below the mismatches ("+dbSchemaAndTestingSchemaTablesAndFieldsIsMirror.length+"):");    
        out.print("<table><tr><th>Table</th><th>Field</th></tr></table>");
        for (Object[] curRow:mismatches){
            out.print("TBL: "+curRow[0].toString()+"+FLD: "+curRow[1].toString()+";    ");
        }
        return;
    }
Rdbms.closeRdbms();
if (1==1) return;            
Rdbms.stablishDBConection("tst");
               
String functionCr=" CREATE OR REPLACE FUNCTION public.isnumeric(text) RETURNS boolean LANGUAGE plpgsql";
functionCr=functionCr+" IMMUTABLE STRICT ";
functionCr=functionCr+" AS $function$ DECLARE x NUMERIC; BEGIN x = $1::NUMERIC; RETURN TRUE; EXCEPTION WHEN others THEN RETURN FALSE; END; $function$ ";

//Rdbms.prepRdQuery(functionCr, null);
Rdbms.prepUpQuery(functionCr, null);
//Rdbms.updateRecordFieldsByFilter(functionCr, functionCr, updateFieldNames, updateFieldValues, whereFieldNames, whereFieldValues)
if (1==1) return;            

Rdbms.stablishDBConection("labplanet");

            markAsExpiredTheExpiredObjects("proc-deploy");            
if (1==1) return;            
            }catch(Exception e){
                out.println(e.getMessage());
                return;
            }
ConfigSpecRule mSpec = new ConfigSpecRule();  
Object[]  resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(
        csvExtractFieldValueBigDecimal(-2.5), csvExtractFieldValueBigDecimal(5), csvExtractFieldValueBigDecimal(5), null);
out.println(Arrays.toString(resSpecEvaluation));
resSpecEvaluation = mSpec.specLimitIsCorrectQuantitative(
        csvExtractFieldValueBigDecimal(-2.5),csvExtractFieldValueBigDecimal(5.1), csvExtractFieldValueBigDecimal(5), null);
out.println(Arrays.toString(resSpecEvaluation));
if (1==1) return;
            String procInstanceName="em-air-allv2";
            Rdbms.stablishDBConection("labplanet");
            
            
        String tblCreateScript2=TblsData.ViewSampleTestingGroup.createTableScript("proc-deploy", new String[]{""});
        Rdbms.prepRdQuery(tblCreateScript2, new Object[]{});


        TestingCoverage tstCov=new TestingCoverage(procInstanceName, 1);    
        tstCov.calculateCoverage();
//            String tblCreateScript = TblsTesting.ScriptsCoverage.createTableScript(procInstanceName, new String[]{""});
//        Rdbms.prepRdQuery(tblCreateScript, new Object[]{});
        //jsonObj.put("TblsTesting.ScriptsCoverage", tblCreateScript);
            
if (1==1) return;
            
            Object[] tableContent = FromInstanceToInstance.tableContent("config", TblsCnfg.UnitsOfMeasurement.TBL.getName(), "labplanet", "modules_trazit");
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
//        GlobalVariables.Schemas.REQUIREMENTS.getName(), TblsReqs.ProcedureUserRequirementsEvents.TBL.getName(), 
//        new String[]{TblsReqs.ProcedureUserRequirementsEvents.FLD_PROCEDURE_NAME.getName(), TblsReqs.ProcedureUserRequirementsEvents.FLD_PROCEDURE_VERSION.getName(), TblsReqs.ProcedureUserRequirementsEvents.FLD_SCHEMA_PREFIX.getName()},
//        new Object[]{"proc-deploy", 1, "proc-deploy"},
//        LPPlatform.buildSchemaName("proc-deploy", GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.ProcedureEvents.TBL.getName(), TblsProcedure.ProcedureEvents.getAllFieldNames());
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
String[][] endpointArr=new String[2][2];
endpointArr[0][0]="SampleAPIfrontendEndpoints";
endpointArr[0][1]="GET_METHOD_CERTIFIED_USERS_LIST";
endpointArr[1][0]="SampleAPIfrontendEndpoints";
endpointArr[1][1]="KPIS";
for (String[] curEntry: endpointArr){
    String apiName=curEntry[0];//"SampleAPIfrontendEndpoints";
    String endPointName=curEntry[1];//"GET_METHOD_CERTIFIED_USERS_LIST";
    Object[] docInfoForEndPoint = getDocInfoForEndPoint(apiName, endPointName);    
    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(docInfoForEndPoint[0].toString()))
        out.println("No doc info for "+apiName+"."+endPointName);         
    else
        out.println(apiName+"."+endPointName+" >> "+Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString((String[]) docInfoForEndPoint[0], (Object[]) docInfoForEndPoint[1], LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)));
}
if (1==1) return;
procInstanceName="em-demo-a";
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
String sch="\"em-demo-a-data_audit";
String schManip=sch;
if (sch.contains("data")){
    if (schManip.endsWith("\"")) schManip=schManip.substring(0, schManip.length()-1)+"_testing\"";
    else schManip=sch+"_testing";
}
out.println(sch);
out.println(schManip);
if (1==1) return;
Rdbms.stablishDBConectionTester();
Object[] createAppUser = UserAndRolesViews.createAppUser("obustos", null, null);
out.println(Arrays.toString(createAppUser));         
if (1==1) return;

String batchContentStr="";     
Integer batchNumRows=5;
Integer batchNumCols=3;
out.println(batchContentStr);
//add       
Integer sampleId=-1;
Integer pendingIncubationStage=-1;
Integer row=-1;
Integer col=-1;
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
            isConnected=Rdbms.stablishDBConectionTester();
            //isConnected = Rdbms.getRdbms().startRdbms(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW);      

out.println("Hello");
out.println(Rdbms.dbViewExists("em-demo-a", "data", "pr_scheduled_locations")[0].toString());
out.println(Rdbms.dbViewExists("requirements", "", "pr_scheduled_locations")[0].toString());
out.println(Rdbms.dbViewExists("em-demo-a", "data", "padsasr_scheduled_locationssss")[0].toString());
out.println("Bye");
//if (1==1) return;

            


            if ((Boolean) isConnected[0]){out.println("Connected to the db !:)");
            }else{out.println("NOT Connected to the db :( "+ Arrays.toString(isConnected));}
            Token token = new Token("eyJ1c2VyREIiOiJtYXJjIiwiZVNpZ24iOiJ2YWxlZSIsInVzZXJEQlBhc3N3b3JkIjoiYXVsaW4iLCJ1c2VyX3Byb2NlZHVyZXMiOiJbZW0tZGVtby1hXSIsInR5cCI6IkpXVCIsImFwcFNlc3Npb25JZCI6IjExNTQxIiwiYXBwU2Vzc2lvblN0YXJ0ZWREYXRlIjoiVHVlIFNlcCAwOCAyMjoyNDoxNyBDRVNUIDIwMjAiLCJ1c2VyUm9sZSI6ImNvb3JkaW5hdG9yIiwiYWxnIjoiSFMyNTYiLCJpbnRlcm5hbFVzZXJJRCI6IjEyIn0.eyJpc3MiOiJMYWJQTEFORVRkZXN0cmFuZ2lzSW5UaGVOaWdodCJ9.IOclIuaUZ-OhyTif3Mmmt4r9m8F9PYC2ECZ4coX3Jno");
            out.println("Today in Date format: "+LPDate.getTimeStampLocalDate().toString());
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
            
            String[] sampleFieldName = new String[]{TblsData.Sample.FLD_SPEC_CODE_VERSION.getName()};
            String[][] specFields = new String[][]{{TblsData.Sample.FLD_SPEC_CODE.getName(), ""}, {TblsData.Sample.FLD_SPEC_CODE_VERSION.getName(), ""}, {TblsData.Sample.FLD_SPEC_VARIATION_NAME.getName(), ""}};
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
            out.println("The name for the field Session_id in db is "+TblsApp.AppSession.valueOf("FLD_SESSION_ID").getName());
            String myTableName=TblsApp.Users.TBL.getName();
            out.println("The table name with NO valueOf() is "+myTableName);
            myTableName=TblsApp.Users.valueOf("TBL").getName();
            out.println("The table name WITH valueOf() is "+myTableName);
            
            
            String procInstanceNameSampleInfo="oil-pl1";
            Integer selSample=134;
            Object[][] sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceNameSampleInfo, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{selSample}, 
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_STATUS.getName()});
            out.println("Info from "+procInstanceNameSampleInfo+".sample "+selSample.toString()+": "+Arrays.toString(sampleInfo[0]));

            procInstanceNameSampleInfo="em-demo-a";
            selSample=160;
            sampleInfo = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceNameSampleInfo, GlobalVariables.Schemas.DATA.getName()), TblsData.Sample.TBL.getName(), 
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName()}, new Object[]{selSample}, 
                    new String[]{TblsData.Sample.FLD_SAMPLE_ID.getName(), TblsData.Sample.FLD_STATUS.getName(), TblsData.Sample.FLD_STATUS_PREVIOUS.getName()});
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
    Object[] diagn=new Object[0];
    out.println("Start testing for Structured Batches");
    procInstanceName="em-demo-a";
    String previousBatchName="Testeo Estructurada10";
    String batchName="Testeo Estructurada12";
    Integer batchTemplate=2;
    Integer batchTemplateVersion=1;
    Integer[] smpls = new Integer[]{1000, 1001,1002, 1003, 1009, 1011, 1012, 1013, 1014};
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
    
    diagn=DataBatchIncubator.createBatch(batchName, batchTemplate, batchTemplateVersion, null, null);
    out.println("createBatch"+diagn[0]+diagn[diagn.length-1]);

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

    diagn=DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[4], 2, 1, true);
    out.println("batchAddSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[5], 1, 2, false);
    out.println("batchAddSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[6], 5, 5, false);
    out.println("batchAddSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchRemoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[5]);
    out.println("batchRemoveSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchMoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[5], 5, 5, false);
    out.println("batchMoveSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[7], 3, 3, true);
    out.println("batchAddSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchAddSample(batchName, batchTemplate, batchTemplateVersion, smpls[8], 2, 3, true);
    out.println("batchAddSample"+diagn[0]+diagn[diagn.length-1]);
    diagn=DataBatchIncubator.batchMoveSample(batchName, batchTemplate, batchTemplateVersion, smpls[8], 3, 3, true);
    out.println("batchMoveSample"+diagn[0]+diagn[diagn.length-1]);

//Structured Batches. End
            
            out.println("Before creating the token");
            String myToken = token.createToken(LPTestingOutFormat.TESTING_USER, LPTestingOutFormat.TESTING_PW, "3", "Admin", "", "", "", null);
            out.println("Token created: "+myToken);
            
            out.println("Reading web text file");
            String exampleUrl = "http://51.75.202.142:8888/myfiles/txtfile.txt";
            final URL url = new URL(exampleUrl);
            final StringBuilder sb = new StringBuilder(0);

            final char[] buf = new char[4096];

            final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT);

            try (
                final InputStream in = url.openStream();
                final InputStreamReader reader = new InputStreamReader(in, decoder);
            ) {
                int nrChars;
                while ((nrChars = reader.read(buf)) != -1)
                    sb.append(buf, 0, nrChars);
            }

            final String test = sb.toString();
            out.println("Test File Content: <br>"+test);
            
        String csvFileName = "noDBSchema_config_SpecQualitativeRuleGeneratorChecker.txt"; 
                             
        String csvPathName = LPTestingOutFormat.TESTING_FILES_PATH+csvFileName; 
        String csvFileSeparator=LPTestingOutFormat.TESTING_FILES_FIELD_SEPARATOR;
        
        Object[][] csvFileContent = LPArray.convertCSVinArray(csvPathName, csvFileSeparator);
        out.println("csv File Content: <br>"+LPTestingOutFormat.convertArrayInHtmlTable(csvFileContent));
        
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  {
        try{
        processRequest(request, response);
        }catch(ServletException|IOException e){
            LPFrontEnd.servletReturnResponseError(request, response, e.getMessage(), new Object[]{}, null);
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

