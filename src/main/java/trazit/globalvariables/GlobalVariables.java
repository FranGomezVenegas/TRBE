/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.globalvariables;

/**
 *
 * @author User
 */
public class GlobalVariables {
    
    public enum Schemas{APP("app"), APP_AUDIT("app-audit"), CONFIG("config"), CONFIG_AUDIT("config-audit"), REQUIREMENTS("requirements"),
        DATA("data"), DATA_AUDIT("data-audit"), TESTING("testing"), DATA_TESTING("data_testing"), DATA_AUDIT_TESTING("data-audit_testing"),
        PROCEDURE("procedure"), PROCEDURE_AUDIT("procedure-audit")
        ;
        Schemas(String nm){
            this.name=nm;
        }
        public String getName() {
            return name;
        }
        private final String name;        
    }

    public enum ServletsResponse{SUCCESS("/ResponseSuccess", "response"), ERROR("/ResponseError", "errorDetail");
        ServletsResponse(String svlt, String attr){
            this.attributeName=attr;
            this.servletName=svlt;
        }
        public String getAttributeName() {
            return attributeName;
        }
        public String getServletName() {
            return servletName;
        }     
        private final String servletName;        
        private final String attributeName;        
    }
    
}
