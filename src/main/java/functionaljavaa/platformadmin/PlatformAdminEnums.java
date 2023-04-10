/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platformadmin;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.TblsApp;
import functionaljavaa.platform.doc.EndPointsToRequirements;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.http.HttpServletRequest;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class PlatformAdminEnums {

    public enum PlatformAdminAPIActionsEndpoints implements EnumIntEndpoints {
        ADD_WHITE_IP("ADD_WHITE_IP", "whiteIpAdded_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE1.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE2.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE3.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
            new LPAPIArguments(TblsApp.IPWhiteList.IP_VALUE4.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
            new LPAPIArguments(TblsApp.IPWhiteList.DESCRIPTION.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        ACTIVATE_WHITE_IP("ACTIVATE_WHITE_IP", "whiteIpActivated_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        DEACTIVATE_WHITE_IP("DEACTIVATE_WHITE_IP", "whiteIpDeactivated_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        UPDATE_WHITE_IP("UPDATE_WHITE_IP", "whiteIpUpdated_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPBlackList.ID.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE1.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE2.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE3.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE4.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
            new LPAPIArguments(TblsApp.IPWhiteList.DESCRIPTION.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 11)},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        REMOVE_WHITE_IP("REMOVE_WHITE_IP", "whiteIpRemoved_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        ADD_BLACK_IP("ADD_BLACK_IP", "blackIpAdded_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE1.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE2.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE3.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE4.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
            new LPAPIArguments(TblsApp.IPWhiteList.DESCRIPTION.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_BLACK_LIST.getTableName()).build()).build(),
                 null, null),
        ACTIVATE_BLACK_IP("ACTIVATE_BLACK_IP", "blackIpActivated_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        DEACTIVATE_BLACK_IP("DEACTIVATE_BLACK_IP", "blackIpDeactivated_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        UPDATE_BLACK_IP("UPDATE_BLACK_IP", "blackIpUpdated_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPBlackList.ID.getName(), LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE1.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE2.getName(), LPAPIArguments.ArgumentType.STRING.toString(), true, 8),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE3.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 9),
            new LPAPIArguments(TblsApp.IPBlackList.IP_VALUE4.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),
            new LPAPIArguments(TblsApp.IPWhiteList.DESCRIPTION.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 11),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        REMOVE_BLACK_IP("REMOVE_BLACK_IP", "blackIpRemoved_success", new LPAPIArguments[]{
            new LPAPIArguments(TblsApp.IPWhiteList.ID.getName(), LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.IP_WHITE_LIST.getTableName()).build()).build(),
                 null, null),
        UPDATE_USER_SHIFT("UPDATE_USER_SHIFT", "userShiftUpdated_success", new LPAPIArguments[]{
            new LPAPIArguments("newShift", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
            new LPAPIArguments("userName", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.USERS.getTableName()).build()).build(),
                 null, null),
        UPDATE_USER_MAIL("UPDATE_USER_MAIL", "userMailUpdated_success", new LPAPIArguments[]{
            new LPAPIArguments("newMail", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
            new LPAPIArguments("userName", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.USERS.getTableName()).build()).build(),
                 null, null),
        UPDATE_USER_ALIAS("UPDATE_USER_ALIAS", "userAliasUpdated_success", new LPAPIArguments[]{
            new LPAPIArguments("newAlias", LPAPIArguments.ArgumentType.STRING.toString(), true, 9),
            new LPAPIArguments("userName", LPAPIArguments.ArgumentType.STRING.toString(), false, 10),},
                Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.APP.getName())
                        .add(GlobalAPIsParams.LBL_TABLE, TblsApp.TablesApp.USERS.getTableName()).build()).build(),
                 null, null),;

        private PlatformAdminAPIActionsEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }

        @Override
        public String getName() {
            return this.name;
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
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.PLATFORM_ADMIN_ACTIONS.getUrl();
        }
        private final String name;
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

    public enum PlatformAdminAPIqueriesEndpoints implements EnumIntEndpoints {
        GET_API_LISTS("GET_API_LISTS", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_WHITE_IP_LIST("GET_WHITE_IP_LIST", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_BLACK_IP_LIST("GET_BLACK_IP_LIST", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_PLATFORM_ADMIN_ALL_INFO("GET_PLATFORM_ADMIN_ALL_INFO", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null),
        GET_PLATFORM_BUSINESS_RULES("GET_PLATFORM_BUSINESS_RULES", "", new LPAPIArguments[]{}, EndPointsToRequirements.endpointWithNoOutputObjects,
                 null, null);

        private PlatformAdminAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes, String devComment, String devCommentTag) {
            this.name = name;
            this.successMessageCode = successMessageCode;
            this.arguments = argums;
            this.outputObjectTypes = outputObjectTypes;
            this.devComment = LPNulls.replaceNull(devComment);
            this.devCommentTag = LPNulls.replaceNull(devCommentTag);
        }

        public HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex) {
            HashMap<HttpServletRequest, Object[]> hm = new HashMap<>();
            Object[] argValues = new Object[0];
            for (LPAPIArguments curArg : this.arguments) {
                argValues = LPArray.addValueToArray1D(argValues, curArg.getName() + ":" + getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }
            hm.put(request, argValues);
            return hm;
        }

        @Override
        public String getName() {
            return this.name;
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
        public LPAPIArguments[] getArguments() {
            return arguments;
        }

        @Override
        public String getApiUrl() {
            return GlobalVariables.ApiUrls.PLATFORM_ADMIN_QUERIES.getUrl();
        }
        private final String name;
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
