/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

/**
 *
 * @author User
 */
public class SamplingPlanEntryItem {
    private String spEntryName;
    private String anaVariation;
    private Integer quantity;

    SamplingPlanEntryItem (String entry, String variation, Integer q){
        this.spEntryName=entry;
        this.anaVariation=variation;
        this.quantity=q;
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