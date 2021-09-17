/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.testingscripts;

/**
 *
 * @author Administrator
 */
public class TestingAssertSummary {
        private Integer totalTests=0;
        private Integer totalSyntaxisMatch=0;
        private Integer totalSyntaxisUnMatch=0;
        private Integer totalSyntaxisUndefined=0;
        private Integer totalCodeMatch=0;
        private Integer totalCodeUnMatch=0;
        private Integer totalCodeUndefined=0;            
        
    /**
     *
     */
    public void increaseTotalTests(){this.totalTests++;}

    /**
     *
     */
    public void increasetotalLabPlanetBooleanUndefined(){this.totalSyntaxisUndefined++;}

    /**
     *
     */
    public void increasetotalLabPlanetBooleanMatch(){this.totalSyntaxisMatch++;}
        
    /**
     *
     */
    public void increasetotalLabPlanetBooleanUnMatch(){this.totalSyntaxisUnMatch++;}        

    /**
     *
     */
    public void increasetotalLabPlanetErrorCodeUndefined(){this.totalCodeUndefined++;}

    /**
     *
     */
    public void increasetotalLabPlanetErrorCodeMatch(){this.totalCodeMatch++;}
        
    /**
     *
     */
    public void increasetotalLabPlanetErrorCodeUnMatch(){this.totalCodeUnMatch++;}        
                
    /**
     *
     * @return
     */
    public Integer getTotalTests(){return this.totalTests;}
        
    /**
     *
     */
    public void notifyResults(){
           // Aun no implementado, debe notificar de los resultados por correo
        }

    /**
     * @return the totalSyntaxisMatch
     */
    public Integer getTotalSyntaxisMatch() {
        return totalSyntaxisMatch;
    }

    /**
     * @return the totalSyntaxisUnMatch
     */
    public Integer getTotalSyntaxisUnMatch() {
        return totalSyntaxisUnMatch;
    }

    /**
     * @return the totalSyntaxisUndefined
     */
    public Integer getTotalSyntaxisUndefined() {
        return totalSyntaxisUndefined;
    }

    /**
     * @return the totalCodeMatch
     */
    public Integer getTotalCodeMatch() {
        return totalCodeMatch;
    }

    /**
     * @return the totalCodeUnMatch
     */
    public Integer getTotalCodeUnMatch() {
        return totalCodeUnMatch;
    }

    /**
     * @return the totalCodeUndefined
     */
    public Integer getTotalCodeUndefined() {
        return totalCodeUndefined;
    }
}
