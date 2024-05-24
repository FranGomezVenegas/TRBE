/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.responserelatedobjects;

import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class RelatedObjects {
    
    public enum RelatedObjectsElementNames{
        SCHEMA("schema_name"), OBJECT_TYPE("object_type"), OBJECT("object_name");
        private RelatedObjectsElementNames(String labelName){
            this.labelName=labelName;            
        }    
        public String getLabelName(){
            return this.labelName;
        }           
        private final String labelName;
    }
    
    private static RelatedObjects mainStructureObject;
    private final JSONArray jArrMainObject;
    
    private RelatedObjects(){
        this.jArrMainObject=new JSONArray();
    }
    
    public static RelatedObjects getInstanceForActions() {
        if (mainStructureObject == null) {
            mainStructureObject = new RelatedObjects();
            return mainStructureObject;
        } else {
         return mainStructureObject;
        }                 
    }
    public void killInstance(){   
        RelatedObjects.mainStructureObject=null;        
    }
    public JSONArray getRelatedObject(){
        return jArrMainObject;
    }   

    public void addSimpleNode(String schema, String objectType, Object object){
        addSimpleNode(schema, objectType, object, null, null);
    }
    public void addSimpleNode(String schema, String objectType, Object object, String[] fldName, Object[] fldValue){
        JSONObject jObj=new JSONObject();
        jObj.put(RelatedObjectsElementNames.OBJECT_TYPE.getLabelName(),  objectType);
        jObj.put(RelatedObjectsElementNames.OBJECT.getLabelName(),  object);   
        if ( (fldName!=null && fldValue!=null) && (LPPlatform.LAB_TRUE.equalsIgnoreCase(LPArray.checkTwoArraysSameLength(fldValue, fldValue)[0].toString())) )
            for (int iFld=0;iFld<fldName.length;iFld++){
                jObj.put(fldName[iFld],  fldValue[iFld]);}
        
        mainStructureObject.jArrMainObject.put(jObj);
    }

    
}
