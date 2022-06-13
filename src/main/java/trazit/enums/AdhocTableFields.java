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
public class AdhocTableFields implements EnumIntTableFields{
    AdhocTableFields(String dbObjName){
        this.fieldName=dbObjName;
        this.fieldType=null;
        this.fieldMask=null;
        this.reference=null;
        this.fieldComment=null;
        this.fldBusinessRules=null;
    }
    private final String fieldName;
    private final String fieldType;
    private final String fieldMask;
    private final ReferenceFld reference;
    private final String fieldComment;
    private final FldBusinessRules[] fldBusinessRules;

    @Override        public String getName(){return this.fieldName;}
    @Override        public String getFieldType() {return this.fieldType;}
    @Override        public String getFieldMask() {return this.fieldMask;}
    @Override        public ReferenceFld getReferenceTable() {return this.reference;}
    @Override        public String getFieldComment(){return this.fieldComment;}
    @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    
}
