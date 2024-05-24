/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.enums;

import functionaljavaa.responserelatedobjects.RelatedObjects;
import org.json.JSONArray;
import org.json.simple.JSONObject;

public interface EnumIntQueriesObj {
    Object[] getMessageDynamicData();
    RelatedObjects getRelatedObj();
    Boolean getEndpointExists();
    Object[] getDiagnostic();
    Boolean getFunctionFound();
    Boolean getIsSuccess();
    JSONObject getResponseSuccessJObj();
    JSONArray getResponseSuccessJArr();
    Object[] getResponseError();
}
