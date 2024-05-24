/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import java.util.ArrayList;
import org.json.JSONArray;

/**
 *
 * @author User
 */
public interface EnumIntBusinessRules {
    String getTagName();   
    String getAreaName();
    JSONArray getValuesList();
    Boolean getAllowMultiValue();
    char getMultiValueSeparator();
    Boolean getIsOptional();    
    ArrayList<String[]> getPreReqs();    
/*
    String getHola(String s);

    private static int functionFoo(EnumIntBusinessRules enumeration) {
        return enumeration.hashCode();
    }
*/    
}


