package module.methodvalidation.logic;

import module.methodvalidation.definition.MethodParamLinealityHandler;
import module.methodvalidation.definition.ParameterHandlerFactory;
import org.json.simple.JSONObject;
import trazit.session.ProcedureRequestSession;

public class DataMethValQueriesPerParameter {

    public static JSONObject methodValidationData(String curProjName, String paramName, String sequenceName, String analyticalParameter) {
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        String procInstanceName = procReqInstance.getProcedureInstance();
        
        MethodParamLinealityHandler handler = ParameterHandlerFactory.getHandler(analyticalParameter);
        if (handler==null){
            JSONObject errorLog=new JSONObject();
            errorLog.put("project", curProjName);
            errorLog.put("paramName", paramName);
            errorLog.put("sequenceName", sequenceName);
            errorLog.put("analyticalParameter", analyticalParameter);
            errorLog.put("error found", "No analytical Parameter detected");
            return errorLog;
        }
        return handler.paramDataForQuery(curProjName, paramName, sequenceName, analyticalParameter, procInstanceName);
    }
}
