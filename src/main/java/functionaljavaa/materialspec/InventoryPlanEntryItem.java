/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.materialspec;

public class InventoryPlanEntryItem {
    private String invEntryName;
    private String invEntryType;
    private Integer quantity;
    private String qUom;
    private Boolean receptionRequired;
    private String transitLocation;

    InventoryPlanEntryItem (String entry, String type, Integer q, String qUom, Boolean receptionReq, String transitLoc){
        this.invEntryName=entry;
        this.invEntryType=type;
        this.quantity=q;
        this.qUom=qUom;
        this.receptionRequired=receptionReq;
        this.transitLocation=transitLoc;
    }
    /**
     * @return the invEntryName
     */
    public String getInvEntryName() {
        return invEntryName;
    }
    public String getInvEntryType() {
        if (invEntryType!=null && invEntryType.length()>0) return invEntryType;
        else return "";
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getQuantityUom() {
        return qUom;
    }
    public Boolean getReceptionRequired() {
        if (receptionRequired!=null && receptionRequired.toString().length()>0) return receptionRequired;
        else return false;
    }
    public String getTransitLocation() {
        return transitLocation;
    }    
}