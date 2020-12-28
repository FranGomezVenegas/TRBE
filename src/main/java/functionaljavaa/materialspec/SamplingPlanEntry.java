/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

import java.util.ArrayList;
import java.util.List;
import lbplanet.utilities.LPArray;

/**
 *
 * @author User
 */
public class SamplingPlanEntry {
    private final String spEntryName;
    private final Integer quantity;
    private final String anaVariation;
    
    SamplingPlanEntry(String entry, Integer q, String variation){
        this.spEntryName=entry;
        this.quantity=q;
        this.anaVariation=variation;
    }
    
    public static List<SamplingPlanEntry> getSamplingPlanInfo() {
        List<SamplingPlanEntry> myList = new ArrayList<SamplingPlanEntry>();
        //myList = new List<SamplingPlanEntry>;
        SamplingPlanEntry ent=new SamplingPlanEntry("FQ", 2, "FQ");
        myList.add(ent);
        ent=new SamplingPlanEntry("MB", 3, "MB");
        myList.add(ent);
        return myList;
    }

    /**
     * @return the spEntryName
     */
    public String getSpEntryName() {
        return spEntryName;
    }

    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @return the anaVariation
     */
    public String getAnaVariation() {
        if (anaVariation!=null && anaVariation.length()>0) return anaVariation;
        else return spEntryName;
    }
    
}
