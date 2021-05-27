/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

//import databases.Rdbms;
import java.util.Arrays;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class TestingBusinessRulesVisited {
    private TestingBusinessRulesVisited() { jsonContent=new JSONArray();}  
    

    private static TestingBusinessRulesVisited auditInfo;
    private JSONArray jsonContent;

    public static synchronized TestingBusinessRulesVisited getInstance(){
        if (auditInfo==null){
            auditInfo=new TestingBusinessRulesVisited();
        }
        return auditInfo;
    }
    public void killIt(){
        auditInfo=null;
        jsonContent=new JSONArray();
    }
    public JSONArray getJsonContent(){return jsonContent;}
    public void AddObject(String schemaName, String tableName, Integer auditId, String[] fldName, Object[] fldValue){
        JSONObject jObj=new JSONObject();
        jObj.put("schemaName", schemaName);
        jObj.put("tableName", tableName);
        jObj.put("auditId", auditId);
        jObj.put("fldName", Arrays.toString(fldName));
        jObj.put("fldValue", Arrays.toString(fldValue));
        jObj.put("fieldsPretty", Arrays.toString(LPArray.joinTwo1DArraysInOneOf1DString(fldName, fldValue, ":")));
        this.jsonContent.add(jObj);
    }
    
}
