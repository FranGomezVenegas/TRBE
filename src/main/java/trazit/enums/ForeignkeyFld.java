/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

/**
 *
 * @author User
 */
public class ForeignkeyFld {
    private final String foreignKeyFld;
    private final String referencedSchema;
    private final String referencedTbl;
    private final String referencedFld;
    
    public ForeignkeyFld(String foreignKeyFld, String referencedSchema, String referencedTbl, String referencedFld){
        this.foreignKeyFld=foreignKeyFld;
        this.referencedSchema=referencedSchema;
        this.referencedTbl=referencedTbl;
        this.referencedFld=referencedFld;
    }
    public String getForeignKeyFld(){return foreignKeyFld;}
    public String getReferencedSchema(){return referencedSchema;}
    public String getReferencedTable(){return referencedTbl;}
    public String getReferencedField(){return referencedFld;}
    
}
