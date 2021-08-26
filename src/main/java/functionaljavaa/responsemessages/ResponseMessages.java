/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.responsemessages;

import lbplanet.utilities.LPArray;

/**
 *
 * @author User
 */
public class ResponseMessages {
    
    public enum RelatedObjectsElementNames{
        SCHEMA("schema_name"), TABLE("table_name"), OBJECT_TYPE("object_type"), OBJECT("object_name");
        private RelatedObjectsElementNames(String labelName){
            this.labelName=labelName;            
        }    
        public String getLabelName(){
            return this.labelName;
        }           
        private final String labelName;
    }
    
    private static ResponseMessages mainStructureObject;
    private Object[][] messages;
    private Object[][] mainMessage;
    
    private ResponseMessages(){
        this.messages=new Object[][]{{}};
        this.mainMessage=new Object[][]{{}};
    }
    
    public static ResponseMessages getInstance() {
        if (mainStructureObject == null) {
            mainStructureObject = new ResponseMessages();
            return mainStructureObject;
        } else {
         return mainStructureObject;
        }                 
    }
    public void killInstance(){   
        ResponseMessages.mainStructureObject=null;        
    }
    public Object[][] getMessages(){
        return messages;
    }   
    public Object[][] getMainMessage(){
        return mainMessage;
    }   

    public void add(String messageCode, Object[] msgCodeVariables){
        Object[] array2dTo1d = LPArray.array2dTo1d(messages);
        LPArray.addValueToArray1D(array2dTo1d, messageCode);
        LPArray.addValueToArray1D(array2dTo1d, msgCodeVariables);
        mainStructureObject.messages=LPArray.array1dTo2d(array2dTo1d, 2);
    }
    public void addMain(String messageCode, Object[] msgCodeVariables){
        mainStructureObject.mainMessage=LPArray.array1dTo2d(new Object[]{messageCode, msgCodeVariables}, 2);
    }
    
}
