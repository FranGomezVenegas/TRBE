/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import module.inspectionlot.rawmaterial.definition.TblsInspLotRMConfig;
import databases.Rdbms;
import java.util.ArrayList;
import java.util.List;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public final class InventoryPlanEntry {
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
    public List<InventoryPlanEntryItem> getInvEntries() {
        return invEntries;
    }
    
/*    public enum SamplingPlanAlgorithms{FIX_NUM_SAMPLES, ONE_PER_EACH_QUANTITY, ONE_PER_EACH_CONTAINER, 
        Q_N_ROOT_PLUS_ONE_TRUNC, Q_N_ROOT_PLUS_ONE_UP, NUMCONT_N_ROOT_PLUS_ONE_TRUNC, NUMCONT_N_ROOT_PLUS_ONE_UP   
    }*/
    public enum invLocations{RETAIN}
    private Boolean hasErrors;
    private String[] errorsArr;
    private final JSONArray errorsjArr;
    private List<InventoryPlanEntryItem> invEntries ;

    public InventoryPlanEntry(String materialName, String specCode, Integer specCodeVersion, Double quant, Integer numCont) {        
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        this.errorsArr=new String[]{};
        this.errorsjArr=new JSONArray();
        this.hasErrors=false;
        List<InventoryPlanEntryItem> myList = new ArrayList<>();
        Object[][] materialInvPlanInfo=Rdbms.getRecordFieldsByFilter(procInstanceName, LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsInspLotRMConfig.TablesInspLotRMConfig.MATERIAL_INVENTORY_PLAN.getTableName(), 
            new String[]{TblsInspLotRMConfig.MaterialInventoryPlan.MATERIAL.getName()}, new Object[]{materialName}, 
            new String[]{TblsInspLotRMConfig.MaterialInventoryPlan.ENTRY_NAME.getName(), TblsInspLotRMConfig.MaterialInventoryPlan.ENTRY_TYPE.getName(), TblsInspLotRMConfig.MaterialInventoryPlan.QUANTITY.getName(), TblsInspLotRMConfig.MaterialInventoryPlan.QUANTITY_UOM.getName(),
                TblsInspLotRMConfig.MaterialInventoryPlan.REQUIRES_RECEPTION.getName(), TblsInspLotRMConfig.MaterialInventoryPlan.TRANSIT_LOCATION.getName()});
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(materialInvPlanInfo[0][0].toString())){
            this.hasErrors=true;
            this.errorsArr=LPArray.addValueToArray1D(this.getErrorsArr(), materialInvPlanInfo[0][materialInvPlanInfo[0].length-1].toString());
            this.getErrorsjArr().add(materialInvPlanInfo[0][materialInvPlanInfo[0].length-1].toString());
            return;
        }
        for (Object[] curMatInvPlan: materialInvPlanInfo){
            if(Boolean.FALSE.equals(invLocations.RETAIN.toString().equalsIgnoreCase(curMatInvPlan[1].toString()))){
                String errMsg="for "+curMatInvPlan[0]+" inventory type"+curMatInvPlan[1]+" not recognized.";
                this.errorsArr=LPArray.addValueToArray1D(this.getErrorsArr(), errMsg);
                this.hasErrors=true;
                this.getErrorsjArr().add(errMsg);
            }
            
/*            String algorithm=curMatInvPlan[1].toString();
            Integer numSamples=-1;
            SamplingPlanEntry.SamplingPlanAlgorithms algEntry = null;
            try{
                algEntry = SamplingPlanEntry.SamplingPlanAlgorithms.valueOf(algorithm.toUpperCase());
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
                            if (algorithm.equalsIgnoreCase(SamplingPlanEntry.SamplingPlanAlgorithms.ONE_PER_EACH_QUANTITY.toString())) numSamples=quant;  
                            else{
                                int nth = (int)Math.round(Math.pow(quant, 1.0 / 2.0));                                    
                                numSamples=Integer.valueOf(nth);
                                if (algorithm.equalsIgnoreCase(SamplingPlanEntry.SamplingPlanAlgorithms.Q_N_ROOT_PLUS_ONE_UP.toString()))
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
                            if (algorithm.equalsIgnoreCase(SamplingPlanEntry.SamplingPlanAlgorithms.ONE_PER_EACH_CONTAINER.toString())) numSamples=numCont;  
                            else{
                                int nth = (int)Math.round(Math.pow(numCont, 1.0 / 2.0));                                    
                                numSamples=Integer.valueOf(nth);
                                if (algorithm.equalsIgnoreCase(SamplingPlanEntry.SamplingPlanAlgorithms.NUMCONT_N_ROOT_PLUS_ONE_UP.toString()))
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
*/
                    InventoryPlanEntryItem ent=new InventoryPlanEntryItem(curMatInvPlan[0].toString(), curMatInvPlan[1].toString(), 
                        (Integer) curMatInvPlan[2], curMatInvPlan[3].toString(), (Boolean) curMatInvPlan[4], curMatInvPlan[5].toString());
                    myList.add(ent);                    
                }
/*            } catch (Exception ex) {
                this.hasErrors=true;
                errorMsg="Algorithm "+algorithm+" not recognized, should be one of "+Arrays.toString(SamplingPlanEntry.SamplingPlanAlgorithms.values());
                this.errorsArr=LPArray.addValueToArray1D(this.getErrorsArr(),errorMsg);
                this.getErrorsjArr().add(errorMsg);
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }*
        }*/
        this.invEntries=myList;
    }
    
}
