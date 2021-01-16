/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import com.labplanet.servicios.moduleinspectionlotrm.TblsInspLotRMConfig;
import databases.Rdbms;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */

public class SamplingPlanEntry {

    /**
     * @return the hasErrors
     */
    public Boolean getHasErrors() {
        return hasErrors;
    }

    /**
     * @return the errorsArr
     */
    public String[] getErrorsArr() {
        return errorsArr;
    }

    /**
     * @return the errorsjArr
     */
    public JSONArray getErrorsjArr() {
        return errorsjArr;
    }

    /**
     * @return the spEntries
     */
    public List<SamplingPlanEntryItem> getSpEntries() {
        return spEntries;
    }
    
    public enum SamplingPlanAlgorithms{FIX_NUM_SAMPLES, ONE_PER_EACH_QUANTITY, ONE_PER_EACH_CONTAINER, 
        Q_N_ROOT_PLUS_ONE_TRUNC, Q_N_ROOT_PLUS_ONE_UP, NUMCONT_N_ROOT_PLUS_ONE_TRUNC, NUMCONT_N_ROOT_PLUS_ONE_UP
    
    }
    private Boolean hasErrors;
    private String[] errorsArr;
    private JSONArray errorsjArr;
    private List<SamplingPlanEntryItem> spEntries ;
    
    
    public SamplingPlanEntry(String materialName, String specCode, Integer specCodeVersion, Integer quant, Integer numCont) {        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        this.errorsArr=new String[]{};
        this.errorsjArr=new JSONArray();
        this.hasErrors=false;
        List<SamplingPlanEntryItem> myList = new ArrayList<>();
        Object[][] materialSampPlanInfo=Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG), TblsInspLotRMConfig.MaterialSamplingPlan.TBL.getName(), 
            new String[]{TblsInspLotRMConfig.MaterialSamplingPlan.FLD_MATERIAL.getName()}, new Object[]{materialName}, 
            new String[]{TblsInspLotRMConfig.MaterialSamplingPlan.FLD_ENTRY_NAME.getName(), TblsInspLotRMConfig.MaterialSamplingPlan.FLD_ANALYSIS_VARIATION.getName(), TblsInspLotRMConfig.MaterialSamplingPlan.FLD_ALGORITHM.getName(), TblsInspLotRMConfig.MaterialSamplingPlan.FLD_FIX_SAMPLES_NUM.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialSampPlanInfo[0][0].toString())){
            this.hasErrors=true;
            this.errorsArr=LPArray.addValueToArray1D(this.getErrorsArr(), materialSampPlanInfo[0][materialSampPlanInfo[0].length-1].toString());
            this.getErrorsjArr().add(materialSampPlanInfo[0][materialSampPlanInfo[0].length-1].toString());
            return;
        }
        for (Object[] curMatSampPlan: materialSampPlanInfo){
            String errorMsg="";
            String algorithm=curMatSampPlan[2].toString();
            Integer numSamples=-1;
            SamplingPlanAlgorithms algEntry = null;
            try{
                algEntry = SamplingPlanAlgorithms.valueOf(algorithm.toUpperCase());
                switch (algEntry){
                    case FIX_NUM_SAMPLES:
                        if (curMatSampPlan[3]==null || curMatSampPlan[3].toString().length()==0)
                            errorMsg="For the algorithm"+algEntry+" and entry "+curMatSampPlan[0].toString()+" no number of fix samples was specified";
                        else{
                            try{
                                numSamples=Integer.valueOf(curMatSampPlan[3].toString());
                            }catch(NumberFormatException e){
                                errorMsg="For the algorithm"+algEntry+" and entry "+curMatSampPlan[0].toString()+" occurred the error "+e.getMessage();
                            }
                        }
                        break;
                    case ONE_PER_EACH_QUANTITY:
                    case Q_N_ROOT_PLUS_ONE_TRUNC:
                    case Q_N_ROOT_PLUS_ONE_UP:
                        if (quant==null || quant.toString().length()==0)
                            errorMsg="For the algorithm"+algEntry+" and entry "+curMatSampPlan[0].toString()+" the lot quantity is required but not specified";
                        else{
                            if (algorithm.equalsIgnoreCase(SamplingPlanAlgorithms.ONE_PER_EACH_QUANTITY.toString())) numSamples=quant;  
                            else{
                                int nth = (int)Math.round(Math.pow(quant, 1.0 / 2.0));                                    
                                numSamples=Integer.valueOf(nth);
                                if (algorithm.equalsIgnoreCase(SamplingPlanAlgorithms.Q_N_ROOT_PLUS_ONE_UP.toString()))
                                    numSamples++;
                            }
                        }
                        break;
                    case ONE_PER_EACH_CONTAINER:
                    case NUMCONT_N_ROOT_PLUS_ONE_TRUNC:
                    case NUMCONT_N_ROOT_PLUS_ONE_UP:
                        if (numCont==null || numCont.toString().length()==0)
                            errorMsg="For the algorithm"+algEntry+" and entry "+curMatSampPlan[0].toString()+" the lot number of Containers is required but not specified";                            
                        else{
                            if (algorithm.equalsIgnoreCase(SamplingPlanAlgorithms.ONE_PER_EACH_CONTAINER.toString())) numSamples=numCont;  
                            else{
                                int nth = (int)Math.round(Math.pow(numCont, 1.0 / 2.0));                                    
                                numSamples=Integer.valueOf(nth);
                                if (algorithm.equalsIgnoreCase(SamplingPlanAlgorithms.NUMCONT_N_ROOT_PLUS_ONE_UP.toString()))
                                    numSamples++;
                            }
                        }
                        break;
                }
                if (errorMsg.length()>0){
                    this.hasErrors=true;
                    this.errorsArr=LPArray.addValueToArray1D(this.getErrorsArr(), errorMsg); 
                    this.getErrorsjArr().add(errorMsg);
                }else{
                    SamplingPlanEntryItem ent=new SamplingPlanEntryItem(curMatSampPlan[0].toString(), 
                        curMatSampPlan[1].toString(), numSamples);
                    myList.add(ent);                    
                }
            } catch (Exception ex) {
                this.hasErrors=true;
                errorMsg="Algorithm "+algorithm+" not recognized, should be one of "+Arrays.toString(SamplingPlanAlgorithms.values());
                this.errorsArr=LPArray.addValueToArray1D(this.getErrorsArr(),errorMsg);
                this.getErrorsjArr().add(errorMsg);
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.spEntries=myList;
    }

    
}
