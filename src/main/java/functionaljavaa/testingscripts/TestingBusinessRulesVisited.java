/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

//import databases.Rdbms;
import lbplanet.utilities.LPJson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class TestingBusinessRulesVisited {
    public TestingBusinessRulesVisited() { jsonContent=new JSONArray();}  
    

    private static TestingBusinessRulesVisited busRuleInfo;
    private JSONArray jsonContent;

    public static synchronized TestingBusinessRulesVisited getInstance(){
        if (busRuleInfo==null){
            busRuleInfo=new TestingBusinessRulesVisited();
        }
        return busRuleInfo;
    }
    public void killIt(){
        busRuleInfo=null;
        jsonContent=new JSONArray();
    }
    public JSONArray getJsonContent(){return jsonContent;}
    
    public void addObject(String schemaName, String suffix, String className, String ruleName, String ruleValue){
        JSONObject jObj=new JSONObject();
        jObj.put("schemaName", schemaName);
        jObj.put("suffix", suffix);
        jObj.put("className", className);
        jObj.put("ruleName", ruleName);
        jObj.put("ruleValue", ruleValue);
        if (Boolean.FALSE.equals(LPJson.containsForSimple(this.jsonContent, jObj)))
            this.jsonContent.add(jObj);
    }
    
}
