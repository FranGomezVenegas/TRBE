/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

//import databases.Rdbms;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class TestingMessageCodeVisited {
    private TestingMessageCodeVisited() { jsonContent=new JSONArray();}  
    

    private static TestingMessageCodeVisited msgCodeInfo;
    private JSONArray jsonContent;

    public static synchronized TestingMessageCodeVisited getInstance(){
        if (msgCodeInfo==null){
            msgCodeInfo=new TestingMessageCodeVisited();
        }
        return msgCodeInfo;
    }
    public void killIt(){
        msgCodeInfo=null;
        jsonContent=new JSONArray();
    }
    public JSONArray getJsonContent(){return jsonContent;}
    public void AddObject(String procName, String suffix, String messageCode, String messageValue, String className){
        JSONObject jObj=new JSONObject();
        jObj.put("procName", procName);
        jObj.put("suffix", suffix);
        jObj.put("messageCode", messageCode);
        jObj.put("messageValue", messageValue);
        jObj.put("className", LPNulls.replaceNull(className));
        this.jsonContent.add(jObj);
    }
    
}
