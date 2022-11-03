package functionaljavaa.testingscripts;

/**
 *
 * @author Fran Gomez
 */
public class TestingMainInfo {

    private Integer dbLogErrorIdStart;
    private Integer dbLogErrorIdEnd;
    private Integer propertiesErrorIdStart;
    private Integer propertiesErrorIdEnd;
    
    public void TestingMainInfo(){      
        this.dbLogErrorIdStart=-999;
        this.dbLogErrorIdEnd=-999;
        this.propertiesErrorIdStart=-999;
        this.propertiesErrorIdEnd=-999;
    }
    
    public void setDbLogErrorStart(Integer id){this.dbLogErrorIdStart=id;}
    public void setDbLogErrorEnd(Integer id){this.dbLogErrorIdEnd=id;}
    public void setPropertiesErrorStart(Integer id){this.propertiesErrorIdStart=id;}
    public void setPropertiesEnd(Integer id){this.propertiesErrorIdEnd=id;}
    
    public Integer getDbLogErrorStart(){return this.dbLogErrorIdStart;}
    public Integer getDbLogErrorEnd(){return this.dbLogErrorIdEnd;}
    public Integer getPropertiesErrorStart(){return this.propertiesErrorIdStart;}
    public Integer getPropertiesEnd(){return this.propertiesErrorIdEnd;}
    
}
