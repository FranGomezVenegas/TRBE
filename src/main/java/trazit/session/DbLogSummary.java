/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.session;

/**
 *
 * @author User
 */
public class DbLogSummary {

    private Integer numInserts;
    private Integer numUpdates;
    private Integer numRemoves;
    private Boolean anyFailure;
    
    public DbLogSummary(){
        this.numInserts=0;
        this.numRemoves=0;
        this.numUpdates=0;
        this.anyFailure=false;
    }
    
    public void addInsert(){this.numInserts=this.numInserts+1;}
    public void addUpdate(){this.numUpdates=this.numUpdates+1;}
    public void addRemove(){this.numRemoves=this.numRemoves+1;}
    public void setFailure(){        this.anyFailure=true;    }
    
    public Boolean hadAnyFailure(){        return this.anyFailure;    }
    public Boolean hasDbAlterActions(){        return this.numInserts+this.numRemoves+this.numUpdates!=0;    }
    public Integer getNumInserts() {        return numInserts;    }
    public Integer getNumUpdates() {        return numUpdates;    }
    public Integer getNumRemoves() {        return numRemoves;    }
}
