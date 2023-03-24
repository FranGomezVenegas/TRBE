/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;

/**
 *
 * @author User
 */
public interface EnumIntEndpoints {
    String getApiUrl();
    String getName();
    String getSuccessMessageCode();
    JsonArray getOutputObjectTypes();
    LPAPIArguments[] getArguments();
    String getDeveloperComment();
    String getDeveloperCommentTag();
}



