/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import lbplanet.utilities.LPArray;
import trazit.enums.EnumIntMessages;

/**
 *
 * @author User
 */
public class ResponseMessages {

    /**
     * @return the isSuccess
     */
    public Boolean getIsSuccess() {
        return isSuccess;
    }

    /**
     * @param isSuccess the isSuccess to set
     */
    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    
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
    private Boolean isSuccess;
    
    private ResponseMessages(){
        this.messages=new Object[][]{{}};
        this.mainMessage=new Object[][]{{}};
        this.isSuccess=false;
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
        LPArray.addValueToArray1D(array2dTo1d, new Object[]{});
        mainStructureObject.messages=LPArray.array1dTo2d(array2dTo1d, 3);
    }
    public void addMainForSuccess(String className, String messageCode, Object[] msgCodeVariables){
        this.setIsSuccess((Boolean) true);
        mainStructureObject.mainMessage=LPArray.array1dTo2d(new Object[]{className, messageCode, msgCodeVariables}, 3);
    }
    public void addMainForError(String messageCode, Object[] msgCodeVariables){
        this.setIsSuccess((Boolean) false);
        mainStructureObject.mainMessage=LPArray.array1dTo2d(new Object[]{messageCode, msgCodeVariables, new Object[]{}}, 3);
    }
    public void addMainForError(EnumIntMessages messageCode, Object[] msgCodeVariables){
        this.setIsSuccess((Boolean) false);
        mainStructureObject.mainMessage=LPArray.array1dTo2d(new Object[]{messageCode.getErrorCode(), msgCodeVariables, new Object[]{}}, 3);
    }
    
}
