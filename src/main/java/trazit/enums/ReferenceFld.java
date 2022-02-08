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
public class ReferenceFld {
        
    private String repository;
    private String tableName;
    private String fieldName;
    
    public ReferenceFld(String repo, String tbl, String fld){
        this.repository=repo;
        this.tableName=tbl;
        this.fieldName=fld;
    }
    public String getRepository(){return repository;}
    public String getTableName(){return tableName;}
    public String getFieldName(){return fieldName;}
    
}
