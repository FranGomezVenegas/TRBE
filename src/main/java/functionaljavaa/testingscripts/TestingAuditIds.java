/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

//import databases.Rdbms;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class TestingAuditIds {
    private TestingAuditIds() { jsonContent=new JSONArray();}  
    

    private static TestingAuditIds auditInfo;
    private JSONArray jsonContent;
    private Integer minAuditId;
    private Integer maxAuditId;

    public static synchronized TestingAuditIds getInstance(){
        if (auditInfo==null){
            auditInfo=new TestingAuditIds();
        }
        return auditInfo;
    }
    public void killIt(){
        auditInfo=null;
        jsonContent=new JSONArray();
    }
    public Integer getMinAudit(){return this.minAuditId;}
    public Integer getMaxAudit(){return this.maxAuditId;}
    public JSONArray getJsonContent(){return jsonContent;}
    public void addObject(String schemaName, String tableName, Integer auditId, String[] fldName, Object[] fldValue){
        JSONObject jObj=new JSONObject();
        jObj.put("schemaName", schemaName);
        jObj.put("tableName", tableName);
        jObj.put("auditId", auditId);
        jObj.put("fldName", Arrays.toString(fldName));
        jObj.put("fldValue", Arrays.toString(fldValue));
        jObj.put("fieldsPretty", Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR)));
        if (this.jsonContent.isEmpty())
            this.minAuditId=auditId;
        this.maxAuditId=auditId;
        this.jsonContent.add(jObj);
    }
   
}
