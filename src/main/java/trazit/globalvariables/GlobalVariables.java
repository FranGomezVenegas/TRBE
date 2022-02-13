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
    
    public enum Schemas{APP("app"), APP_AUDIT("app-audit"), APP_BUSINESS_RULES("app-business-rules"),
        APP_CONFIG("config"), APP_TESTING("app-testing"), 
        APP_PROC_CONFIG("app-proc-config"), APP_PROC_DATA("app-proc-data"), APP_PROC_DATA_AUDIT("app-proc-data-audit"),
        CONFIG("config"), CONFIG_AUDIT("config-audit"), REQUIREMENTS("requirements"),
        DATA("data"), DATA_AUDIT("data-audit"), TESTING("testing"), DATA_TESTING("data_testing"), DATA_AUDIT_TESTING("data-audit_testing"),
        PROCEDURE("procedure"), PROCEDURE_CONFIG("procedure-config"), PROCEDURE_TESTING("procedure_testing"), PROCEDURE_AUDIT("procedure-audit"), PROCEDURE_AUDIT_TESTING("procedure-audit_testing"), MODULES_TRAZIT_TRAZIT("trazit")
        ;
        Schemas(String nm){
            this.name=nm;
        }
        public String getName() {
            return name;
        }
        private final String name;        
    }

    public static final String DEFAULTLANGUAGE="en";
    public static final String LANGUAGE_ALL_LANGUAGES="ALL";
    public enum Languages{EN("en"), ES("es")
        ;
        Languages(String nm){
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
