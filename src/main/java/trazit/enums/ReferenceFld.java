/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import com.labplanet.servicios.app.GlobalAPIsParams;

/**
 *
 * @author User
 */
public class ReferenceFld {
        
    private final String type;   
    private String repository;
    private String tableName;
    private String fieldName;
    private String values;
    private Boolean isForeignKey;
    
    public ReferenceFld(String repo, String tbl, String fld){
        this.type=GlobalAPIsParams.LBL_TABLE;
        this.repository=repo;
        this.tableName=tbl;
        this.fieldName=fld;
        this.isForeignKey=true;
    }
    public ReferenceFld(String values){
        this.type="values";
        this.values=values;
        this.isForeignKey=false;
        //CHECK (element_type IN ('lesson', 'quiz')
    }
    public String getRepository(){return repository;}
    public String getTableName(){return tableName;}
    public String getFieldName(){return fieldName;}
    public String getType(){return type;}
    public String getValues(){return values;}
    public Boolean getIsForeignKey(){return isForeignKey;}
    
    
}
