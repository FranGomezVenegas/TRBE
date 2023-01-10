/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.certification;

import databases.TblsCnfg;
import databases.TblsData;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class FrontendCertifObjsUtilities {
    
    public enum CertifModes{
        CERTIFUSER_READ_AND_UNDERSTOOD("CERTIFUSER_READ_AND_UNDERSTOOD", "Read and understood", "Leido y entendido"),
        CERTIFUSER_REVIEWER_ONLY("CERTIFUSER_REVIEWER_ONLY", "Pending Approval", "Pendiente Aprobación"),         
        CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER("CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER", "Understood and sending to reviewer", "Entendido y Envío a Aprobar"),
        CERTIFUSER_REVIEWER_SIGN("CERTIFUSER_REVIEWER_SIGN", "Pending Approval", "Pendiente Aprobación"),        
        CERTIFUSER_TRAINING_REQUIRED("CERTIFUSER_TRAINING_REQUIRED", "Training required", "Requiere entrenamiento"),
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
    public static JSONObject certifObjCertifReviewerPendingSign(String[] objFldNames, Object[] objFldValues){
        JSONObject mainObj=new JSONObject();
        Integer valuePosicInArray = LPArray.valuePosicInArray(objFldNames, TblsData.ViewUserAndAnalysisMethodCertificationView.LIGHT.getName());
        if ( valuePosicInArray>1 && "GREEN".equalsIgnoreCase(LPNulls.replaceNull(objFldValues[valuePosicInArray].toString())) ){
            mainObj.put("action_visible", false);
            return mainObj;
        }
        valuePosicInArray = LPArray.valuePosicInArray(objFldNames, TblsCnfg.SopMetaData.CERTIFICATION_MODE.getName());
        if (valuePosicInArray==-1){
            mainObj.put("action_visible", false);
            mainObj.put("error", TblsCnfg.SopMetaData.CERTIFICATION_MODE.getName()+" field not found to determine the mode, please review definition");
            return mainObj;
        }
        mainObj.put("user_name", objFldValues[LPArray.valuePosicInArray(objFldNames, TblsData.ViewUserAndAnalysisMethodCertificationView.USER_NAME.getName())]);
        valuePosicInArray = LPArray.valuePosicInArray(objFldNames, TblsData.ViewUserAndMetaDataSopView.PENDING_REVIEW.getName());
        if (valuePosicInArray==-1||!Boolean.TRUE.toString().equalsIgnoreCase(LPNulls.replaceNull(objFldValues[valuePosicInArray]).toString())){
            mainObj.put("action_visible", true);
            mainObj.put("action_enabled", false);    
            
            mainObj.put("label_en", "Still not pending for approval");
            mainObj.put("label_es", "Aún no pendiente de aprobar");
        }else{
            mainObj.put("action_visible", true);
            mainObj.put("action_enabled", true);       
            mainObj.put("endpoint_name", CertifModes.CERTIFUSER_REVIEWER_SIGN.getEndpointName());
            mainObj.put("label_en", CertifModes.CERTIFUSER_REVIEWER_SIGN.getLabelEn());
            mainObj.put("label_es", CertifModes.CERTIFUSER_REVIEWER_SIGN.getLabelEs());
        }
        return mainObj;
    }
    
    public static JSONObject certifObjCertifModeOwnUserAction(String[] objFldNames, Object[] objFldValues){
        JSONObject mainObj=new JSONObject();
        Integer valuePosicInArray = LPArray.valuePosicInArray(objFldNames, TblsData.ViewUserAndAnalysisMethodCertificationView.LIGHT.getName());
        if ( valuePosicInArray>1 && "GREEN".equalsIgnoreCase(LPNulls.replaceNull(objFldValues[valuePosicInArray].toString())) ){
            mainObj.put("action_visible", false);
            return mainObj;
        }
        valuePosicInArray = LPArray.valuePosicInArray(objFldNames, TblsCnfg.SopMetaData.CERTIFICATION_MODE.getName());
        if (valuePosicInArray==-1){
            mainObj.put("action_visible", false);
            mainObj.put("error", TblsCnfg.SopMetaData.CERTIFICATION_MODE.getName()+" field not found to determine the mode, please review definition");
            return mainObj;
        }
        String certifMode =LPNulls.replaceNull(objFldValues[valuePosicInArray]).toString();
        CertifModes cMode=null;
        try{
            valuePosicInArray = LPArray.valuePosicInArray(objFldNames, TblsData.ViewUserAndMetaDataSopView.PENDING_REVIEW.getName());
            if (valuePosicInArray>-1&&Boolean.TRUE.toString().equalsIgnoreCase(LPNulls.replaceNull(objFldValues[valuePosicInArray]).toString())){
                mainObj.put("action_visible", false);
                mainObj.put("action_enabled", false);
                mainObj.put("label_en", CertifModes.CERTIFUSER_REVIEWER_SIGN.getLabelEn());
                mainObj.put("label_es", CertifModes.CERTIFUSER_REVIEWER_SIGN.getLabelEs());                
            }else{
                cMode = CertifModes.valueOf(certifMode.toUpperCase());
                if ( (CertifModes.CERTIFUSER_READ_AND_UNDERSTOOD.toString().equalsIgnoreCase(certifMode)) || (CertifModes.CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER.toString().equalsIgnoreCase(certifMode))){
                    mainObj.put("action_visible", true);
                    mainObj.put("action_enabled", true);
                }else if (CertifModes.CERTIFUSER_UNDERSTOOD_AND_SENDTOREVIEWER.toString().equalsIgnoreCase(certifMode)){
                    mainObj.put("action_visible", true);

                    mainObj.put("action_enabled", true);
                }else{
                    mainObj.put("action_visible", true);
                    mainObj.put("action_enabled", false);
                }
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
