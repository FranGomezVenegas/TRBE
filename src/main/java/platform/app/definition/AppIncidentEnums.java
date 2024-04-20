/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.app.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import platform.app.apis.IncidentAPIactions;
import databases.TblsApp;
import javax.json.Json;
import javax.json.JsonArray;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class AppIncidentEnums {

    static final String COMMON_PARAMS = "incidentId|note";

    public enum IncidentAPIactionsEndpoints implements EnumIntEndpoints {
        /**
         *
         */
        NEW_INCIDENT("NEW_INCIDENT", "incidentTitle|incidentDetail", "", "incidentNewIncident_success",
                new LPAPIArguments[]{new LPAPIArguments("category", LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
                    new LPAPIArguments(IncidentAPIactions.ParamsList.INCIDENT_TITLE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments("incidentProcedure", LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
                    new LPAPIArguments("priority", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
                    new LPAPIArguments(IncidentAPIactions.ParamsList.INCIDENT_DETAIL.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 10)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.INCIDENT.getTableName()).build()).build(),
                 null, null),
        /*                new JSONArray()
                    .add(new JSONObject(GlobalAPIsParams.LBL_REPOSITORY,GlobalVariables.Schemas.APP.getName()))
                    .add(new JSONObject(GlobalAPIsParams.LBL_TABLE,TblsApp.TablesApp.INCIDENT.getTableName()))),*/
        CONFIRM_INCIDENT("CONFIRM_INCIDENT", COMMON_PARAMS, "", "incidentConfirmIncident_success",
                new LPAPIArguments[]{new LPAPIArguments(IncidentAPIactions.ParamsList.INCIDENT_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments(IncidentAPIactions.ParamsList.NOTE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.INCIDENT.getTableName()).build()).build(),
                 null, null),
        CLOSE_INCIDENT("CLOSE_INCIDENT", COMMON_PARAMS, "", "incidentClosedIncident_success",
                new LPAPIArguments[]{new LPAPIArguments(IncidentAPIactions.ParamsList.INCIDENT_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments(IncidentAPIactions.ParamsList.NOTE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.INCIDENT.getTableName()).build()).build(),
                 null, null),
        REOPEN_INCIDENT("REOPEN_INCIDENT", COMMON_PARAMS, "", "incidentReopenIncident_success",
                new LPAPIArguments[]{new LPAPIArguments(IncidentAPIactions.ParamsList.INCIDENT_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments(IncidentAPIactions.ParamsList.NOTE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.INCIDENT.getTableName()).build()).build(),
                 null, null),
        ADD_NOTE_INCIDENT("ADD_NOTE_INCIDENT", COMMON_PARAMS, "", "incidentAddNoteToIncident_success",
                new LPAPIArguments[]{new LPAPIArguments(IncidentAPIactions.ParamsList.INCIDENT_ID.getParamName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
                    new LPAPIArguments(IncidentAPIactions.ParamsList.NOTE.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
                    new LPAPIArguments(IncidentAPIactions.ParamsList.NEW_STATUS.getParamName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 7)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.INCIDENT.getTableName()).build()).build(),
                 null, null),;

        private IncidentAPIactionsEndpoints(String name, String mandatoryParams, String optionalParams, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.mandatoryParams = mandatoryParams;
            this.optionalParams = optionalParams;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }
        @Override public String getEntity() {return "incident";}
        @Override
        public String getName() {
            return this.name;
        }

        public String getMandatoryParams() {
            return this.mandatoryParams;
        }

        @Override
        public String getSuccessMessageCode() {
            return this.successMessageCode;
        }

        @Override
        public JsonArray getOutputObjectTypes() {
            return outputObjectTypes;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.APP_INCIDENTS_ACTIONS.getUrl();
        }

        /**
         * @return the arguments
         */
        @Override
        public LPAPIArguments[] getArguments() {
            return arguments;
        }
        private final String name;
        private final String mandatoryParams;
        private final String optionalParams;
        private final String successMessageCode;
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;

        @Override
        public String getDeveloperComment() {
            return this.devComment;
        }

        @Override
        public String getDeveloperCommentTag() {
            return this.devCommentTag;
        }
        private final String devComment;
        private final String devCommentTag;
    }

}
