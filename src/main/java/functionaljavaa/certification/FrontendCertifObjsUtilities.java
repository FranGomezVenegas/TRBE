/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.certification;

import databases.TblsCnfg;
import lbplanet.utilities.LPArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class FrontendCertifObjsUtilities {
    
    public enum CertifModes{
        READ_AND_UNDERSTOOD("READ_AND_UNDERSTOOD", "Read and understood", "Leido y entendido"),
        UNDERSTOOD_AND_SENDTOREVIEWER("UNDERSTOOD_AND_SENDTOREVIEWER", "Understood", "Entendido"),
        TRAINING_REQUIRED("TRAINING_REQUIRED", "Training required", "Requiere entrenamiento"),
        ;
        private CertifModes(String endpointName, String labelEn, String labelEs){
            this.endpointName=endpointName;
            this.labelEn=labelEn;
            this.labelEs=labelEs;
        }
        private final String endpointName;             
        private final String labelEn;
        private final String labelEs;

        /**
         * @return the endpointName
         */
        public String getEndpointName() {
            return endpointName;
        }

        /**
         * @return the labelEn
         */
        public String getLabelEn() {
            return labelEn;
        }

        /**
         * @return the labelEs
         */
        public String getLabelEs() {
            return labelEs;
        }
    }    
    
    public static JSONObject certifObjCertifModeOwnUserAction(String[] objFldNames, Object[] objFldValues){
        JSONObject mainObj=new JSONObject();
        Integer valuePosicInArray = LPArray.valuePosicInArray(objFldNames, TblsCnfg.SopMetaData.FLD_CERTIFICATION_MODE.getName());
        if (valuePosicInArray==-1) return mainObj;
        String certifMode =objFldValues[valuePosicInArray].toString();
        CertifModes cMode=null;
        try{
            cMode = CertifModes.valueOf(certifMode.toUpperCase());
            if ( (CertifModes.READ_AND_UNDERSTOOD.toString().equalsIgnoreCase(certifMode)) || (CertifModes.UNDERSTOOD_AND_SENDTOREVIEWER.toString().equalsIgnoreCase(certifMode))){
                mainObj.put("action_visible", true);
                mainObj.put("action_enabled", true);
            }else{
                mainObj.put("action_visible", true);
                mainObj.put("action_enabled", false);
            }
            mainObj.put("endpoint_name", certifMode);
            mainObj.put("label_en", cMode.getLabelEn());
            mainObj.put("label_es", cMode.getLabelEs());
            
        }catch(Exception e){
            mainObj.put("action_visible", false);
        }        
        return mainObj;
    }
    
}
