/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.procedureinstance.definition.definition;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.SqlStatementEnums;
import databases.TblsAppConfig;
import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.FIELDS_NAMES_MODULE_NAME;
import static lbplanet.utilities.LPDatabase.FIELDS_NAMES_MODULE_VERSION;
import static lbplanet.utilities.LPDatabase.FIELDS_NAMES_PROCEDURE_NAME;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.EnumIntTablesJoin;
import trazit.enums.EnumIntViewFields;
import trazit.enums.EnumIntViews;
import trazit.enums.FldBusinessRules;
import trazit.enums.ForeignkeyFld;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
import trazit.procedureinstance.deployment.logic.ProcedureDefinitionToInstanceSections.ReqSolutionTypes;

/**
 *
 * @author Administrator
 */
public class TblsReqs {

    public static final String FIELDS_NAMES_DESCRIPTION = "description";
    public static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.REQUIREMENTS.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = false;

    public enum TablesReqs implements EnumIntTables {
        MODULES(null, "modules", SCHEMA_NAME, false, Modules.values(), null,
                new String[]{Modules.MODULE_NAME.getName(), Modules.MODULE_VERSION.getName()}, null, "Modules Available"),
        MODULE_ACTIONS_N_QUERIES(null, "module_actions_and_queries", SCHEMA_NAME, false, ModuleActionsAndQueries.values(), null,
                new String[]{ModuleActionsAndQueries.MODULE_NAME.getName(), ModuleActionsAndQueries.MODULE_VERSION.getName(), ModuleActionsAndQueries.API_NAME.getName(), ModuleActionsAndQueries.ENDPOINT_NAME.getName()}, 
                new Object[]{new ForeignkeyFld(ModuleActionsAndQueries.MODULE_NAME.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_NAME.getName()),
                    new ForeignkeyFld(ModuleActionsAndQueries.MODULE_VERSION.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_VERSION.getName())},
                "Relation of actions and queries belonging to each module version in use"),
        MODULE_BUSINESS_RULES(null, "module_business_rules", SCHEMA_NAME, false, ModuleBusinessRules.values(), null,
                new String[]{ModuleBusinessRules.MODULE_NAME.getName(), ModuleBusinessRules.MODULE_VERSION.getName(), ModuleBusinessRules.API_NAME.getName(), ModuleBusinessRules.RULE_NAME.getName()}, 
                new Object[]{new ForeignkeyFld(ModuleActionsAndQueries.MODULE_NAME.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_NAME.getName()),
                    new ForeignkeyFld(ModuleActionsAndQueries.MODULE_VERSION.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_VERSION.getName())},
                "Relation of business rules belonging to each module version in use"),
        MODULE_ERROR_NOTIFICATIONS(null, "module_error_notifications", SCHEMA_NAME, false, ModuleErrorNotifications.values(), null,
                new String[]{ModuleErrorNotifications.MODULE_NAME.getName(), ModuleErrorNotifications.MODULE_VERSION.getName(), ModuleErrorNotifications.API_NAME.getName(), ModuleErrorNotifications.ERROR_CODE.getName()}, 
                new Object[]{new ForeignkeyFld(ModuleErrorNotifications.MODULE_NAME.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_NAME.getName()),
                    new ForeignkeyFld(ModuleErrorNotifications.MODULE_VERSION.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_VERSION.getName())},
                "Relation of error notifications belonging to each module version in use"),
        MODULE_SPECIAL_VIEWS(null, "module_special_views", SCHEMA_NAME, false, ModuleSpecialViews.values(), null,
                new String[]{ModuleSpecialViews.MODULE_NAME.getName(), ModuleSpecialViews.MODULE_VERSION.getName(), ModuleSpecialViews.VIEW_NAME.getName()}, 
                new Object[]{new ForeignkeyFld(ModuleSpecialViews.MODULE_NAME.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_NAME.getName()),
                    new ForeignkeyFld(ModuleSpecialViews.MODULE_VERSION.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_VERSION.getName())},
                "Relation of special views belonging to each module version in use"),
        MODULE_MANUALS(null, "module_manuals", SCHEMA_NAME, false, ModuleManuals.values(), null,
                new String[]{ModuleManuals.MODULE_NAME.getName(), ModuleManuals.MODULE_VERSION.getName(), ModuleManuals.MANUAL_NAME.getName(), ModuleManuals.MANUAL_VERSION.getName()}, 
                new Object[]{new ForeignkeyFld(ModuleManuals.MODULE_NAME.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_NAME.getName()),
                    new ForeignkeyFld(ModuleManuals.MODULE_VERSION.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_VERSION.getName())},
                "Relation of manuals belonging to each module version in use"),
        MODULE_TABLES_AND_VIEWS(null, "module_tables_and_views", SCHEMA_NAME, false, ModuleTablesAndViews.values(), null,
                new String[]{ModuleTablesAndViews.MODULE_NAME.getName(), ModuleTablesAndViews.MODULE_VERSION.getName(), ModuleTablesAndViews.SCHEMA_NAME.getName(), ModuleTablesAndViews.NAME.getName()}, 
                new Object[]{new ForeignkeyFld(ModuleTablesAndViews.MODULE_NAME.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_NAME.getName()),
                    new ForeignkeyFld(ModuleTablesAndViews.MODULE_VERSION.getName(), SCHEMA_NAME, TablesReqs.MODULES.getTableName(), Modules.MODULE_VERSION.getName())},
                "Relation of manuals belonging to each module version in use"),
        PROCEDURE_INFO(null, "procedure_info", SCHEMA_NAME, false, ProcedureInfo.values(), null,
                new String[]{ProcedureInfo.PROCEDURE_NAME.getName(), ProcedureInfo.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName()}, null, "This table provides the general info about the process instances"),
        PROCEDURE_ROLES(null, "procedure_roles", SCHEMA_NAME, false, ProcedureRoles.values(), null,
                new String[]{ProcedureRoles.PROCEDURE_NAME.getName(), ProcedureRoles.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureRoles.ROLE_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureRoles.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureRoles.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureRoles.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "Roles for a given process instance"),
        PROC_USERS(null, "procedure_users", SCHEMA_NAME, false, ProcedureUsers.values(), null,                
                new String[]{ProcedureUsers.PROCEDURE_NAME.getName(), ProcedureUsers.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUsers.USER_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureUsers.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureUsers.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureUsers.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "Users for a given process instance"),
        PROC_USER_ROLES(null, "procedure_user_role", SCHEMA_NAME, false, ProcedureUserRoles.values(), null,
                new String[]{ProcedureUserRoles.PROCEDURE_NAME.getName(), ProcedureUserRoles.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUserRoles.ROLE_NAME.getName(), ProcedureUserRoles.USER_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureUserRoles.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureUserRoles.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureUserRoles.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "User Roles for a given process instance"),
        PROCEDURE_SOP_META_DATA(null, "procedure_sop_meta_data", SCHEMA_NAME, false, ProcedureSopMetaData.values(),
                null,
                new String[]{ProcedureSopMetaData.PROCEDURE_NAME.getName(), ProcedureSopMetaData.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureSopMetaData.SOP_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureSopMetaData.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureSopMetaData.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureSopMetaData.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "SOPs for a given process instance"),
        PROCEDURE_USER_REQS(null, "procedure_user_requirements", SCHEMA_NAME, false, ProcedureUserRequirements.values(),"procedure_user_requirements_seq",
                new String[]{ProcedureUserRequirements.PROCEDURE_NAME.getName(), ProcedureUserRequirements.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUserRequirements.REQ_ID.getName()},
                new Object[]{new ForeignkeyFld(ProcedureUserRequirements.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureUserRequirements.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureUserRequirements.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "URS for a given process instance"),
        PROCEDURE_RISK_ASSESSMENT(null, "procedure_risk_assessment", SCHEMA_NAME, false, ProcedureRiskAssessment.values(), "procedure_risk_assessment_seq",
                new String[]{ProcedureRiskAssessment.PROCEDURE_NAME.getName(), ProcedureRiskAssessment.PROCEDURE_VERSION.getName(), ProcedureRiskAssessment.PROC_INSTANCE_NAME.getName(), ProcedureRiskAssessment.REQ_ID.getName(), ProcedureRiskAssessment.RISK_ID.getName()},
                new Object[]{new ForeignkeyFld(ProcedureRiskAssessment.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureRiskAssessment.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureRiskAssessment.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.PROC_INSTANCE_NAME.getName()),
                    new ForeignkeyFld(ProcedureRiskAssessment.REQ_ID.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.REQ_ID.getName())
                },
                "risk assessment for a given process instance"),
        PROCEDURE_REQ_SOLUTION(null, "procedure_req_solution", SCHEMA_NAME, false, ProcedureReqSolution.values(), "procedure_req_solution_seq",
                new String[]{ProcedureReqSolution.PROCEDURE_NAME.getName(), ProcedureReqSolution.PROCEDURE_VERSION.getName(), ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), ProcedureReqSolution.REQ_ID.getName(), ProcedureReqSolution.SOLUTION_ID.getName()},
                new Object[]{new ForeignkeyFld(ProcedureReqSolution.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureReqSolution.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.PROC_INSTANCE_NAME.getName()),
                    new ForeignkeyFld(ProcedureReqSolution.REQ_ID.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.REQ_ID.getName())
                }, "Requirement solution for a given process instance"),
        PROCEDURE_REQ_SOL_VIEW_TAB(null, "procedure_req_solution_view_tabs", SCHEMA_NAME, false, ProcedureReqSolutionViewTabs.values(), "sol_tab_id_seq",
                new String[]{ProcedureReqSolutionViewTabs.TAB_ID.getName()},
                new Object[]{new ForeignkeyFld(ProcedureReqSolution.SOLUTION_ID.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.REQ_ID.getName())
                }, "Definition for Tabs view"),
        PROCEDURE_REQ_SOL_VIEW_TBL_BUTTONS(null, "procedure_req_solution_view_tbl_w_buttons", SCHEMA_NAME, false, ProcedureReqSolutionViewTableWithButtons.values(), "sol_table_id_seq",
                new String[]{ProcedureReqSolutionViewTableWithButtons.TABLE_ID.getName()},
                new Object[]{new ForeignkeyFld(ProcedureReqSolutionViewTableWithButtons.SOLUTION_ID.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_USER_REQS.getTableName(), ProcedureUserRequirements.PROCEDURE_NAME.getName())
                    }, "Definition for Table with buttons"),
/*        PROCEDURE_USER_REQS_EVENTS(null, "procedure_user_requirements_events", SCHEMA_NAME, false, ProcedureReqSolution.values(),
                ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName() + "_" + ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName() + "_" + ProcedureUserRequirementsEvents.ID.getName(),
                new String[]{ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureUserRequirementsEvents.ID.getName()},
                new Object[]{new ForeignkeyFld(ProcedureUserRequirementsEvents.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureUserRequirementsEvents.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureUserRequirementsEvents.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "URS events for a given process instance"),*/
        
        PROC_MODULE_TABLES(null, "procedure_module_tables", SCHEMA_NAME, false, ProcedureModuleTables.values(), null,
                new String[]{ProcedureModuleTables.PROCEDURE_NAME.getName(), ProcedureModuleTables.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureModuleTables.SCHEMA_NAME.getName(), ProcedureModuleTables.TABLE_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureModuleTables.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureModuleTables.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureModuleTables.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "Roles for a given process instance"),
        PROC_BUS_RULES(null, "procedure_business_rules", SCHEMA_NAME, false, ProcedureBusinessRules.values(), null,
                new String[]{ProcedureBusinessRules.PROCEDURE_NAME.getName(), ProcedureBusinessRules.PROCEDURE_VERSION.getName(), ProcedureInfo.PROC_INSTANCE_NAME.getName(), ProcedureBusinessRules.FILE_SUFFIX.getName(), ProcedureBusinessRules.RULE_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureBusinessRules.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureBusinessRules.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureBusinessRules.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())},
                "Roles for a given process instance"),
        PROC_MASTER_DATA(null, "procedure_master_data", SCHEMA_NAME, false, ProcedureMasterData.values(), null,
                new String[]{ProcedureMasterData.PROCEDURE_NAME.getName(), ProcedureMasterData.PROCEDURE_VERSION.getName(), ProcedureFEModel.PROC_INSTANCE_NAME.getName(), ProcedureMasterData.OBJECT_TYPE.getName()},
                new Object[]{new ForeignkeyFld(ProcedureMasterData.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureMasterData.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureMasterData.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())
                },
                "Master Data for a given process instance"),
        PROC_FE_MODEL(null, "fe_proc_model", SCHEMA_NAME, false, ProcedureFEModel.values(), null,
                new String[]{ProcedureFEModel.PROCEDURE_NAME.getName(), ProcedureFEModel.PROCEDURE_VERSION.getName(), ProcedureFEModel.PROC_INSTANCE_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureFEModel.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureFEModel.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureFEModel.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureInfo.PROC_INSTANCE_NAME.getName())},
                "Frontend model definition for a given process instance"),
        PROC_MANUALS(null, "procedure_manuals", SCHEMA_NAME, false, ProcedureManuals.values(), null,
                new String[]{ProcedureManuals.PROCEDURE_NAME.getName(), ProcedureManuals.PROCEDURE_VERSION.getName(), ProcedureManuals.PROC_INSTANCE_NAME.getName(), ProcedureManuals.MANUAL_NAME.getName(), ProcedureManuals.MANUAL_VERSION.getName()},
                new Object[]{new ForeignkeyFld(ProcedureManuals.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureManuals.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureManuals.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureManuals.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureManuals.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureManuals.PROC_INSTANCE_NAME.getName())
                },
                "Roles for a given process instance"),
        PROCEDURE_FRONT_TESTING_WITNESS(null, "procedure_frontend_testing_witness", SCHEMA_NAME, false, ProcedureFrontendTestingWitness.values(), null,
                new String[]{ProcedureRoles.PROCEDURE_NAME.getName(), ProcedureFrontendTestingWitness.PROCEDURE_VERSION.getName(), ProcedureFrontendTestingWitness.PROC_INSTANCE_NAME.getName(), ProcedureFrontendTestingWitness.TEST_NAME.getName()},
                new Object[]{new ForeignkeyFld(ProcedureFrontendTestingWitness.PROCEDURE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureFrontendTestingWitness.PROCEDURE_NAME.getName()),
                    new ForeignkeyFld(ProcedureFrontendTestingWitness.PROCEDURE_VERSION.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureFrontendTestingWitness.PROCEDURE_VERSION.getName()),
                    new ForeignkeyFld(ProcedureFrontendTestingWitness.PROC_INSTANCE_NAME.getName(), SCHEMA_NAME, TablesReqs.PROCEDURE_INFO.getTableName(), ProcedureFrontendTestingWitness.PROC_INSTANCE_NAME.getName())
                },
                "Roles for a given process instance"),        
/*        JAVA_CLASS_DOC(null, "java_class_doc", SCHEMA_NAME, false, ProcedureFEModel.values(), "id",
                new String[]{"id"},
                null,
                "java_class_doc"),*/
        ;

        private TablesReqs(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds,
                String seqName, String[] primaryK, Object[] foreignK, String comment) {
            this.getTblBusinessRules = fldBusRules;
            this.tableName = dbTblName;
            this.repositoryName = repositoryName;
            this.isProcedure = isProcedure;
            this.tableFields = tblFlds;
            this.sequence = seqName;
            this.primarykey = primaryK;
            this.foreignkey = foreignK;
            this.tableComment = comment;
        }

        @Override
        public String getTableName() {
            return this.tableName;
        }

        @Override
        public String getTableComment() {
            return this.tableComment;
        }

        @Override
        public EnumIntTableFields[] getTableFields() {
            return this.tableFields;
        }

        @Override
        public String getRepositoryName() {
            return this.repositoryName;
        }

        @Override
        public String getSeqName() {
            return this.sequence;
        }

        @Override
        public String[] getPrimaryKey() {
            return this.primarykey;
        }

        @Override
        public Object[] getForeignKey() {
            return this.foreignkey;
        }

        @Override
        public Boolean getIsProcedureInstance() {
            return this.isProcedure;
        }

        @Override
        public FldBusinessRules[] getTblBusinessRules() {
            return this.getTblBusinessRules;
        }
        private final FldBusinessRules[] getTblBusinessRules;
        private final String tableName;
        private final String repositoryName;
        private final Boolean isProcedure;
        private final String sequence;
        private final EnumIntTableFields[] tableFields;
        private final String[] primarykey;
        private final Object[] foreignkey;
        private final String tableComment;
    }

    public enum ViewsReqs implements EnumIntViews {
        PROC_REQ_SOLUTION_ACTIONS("SELECT procinfo.module_name,\n" +
"	procinfo.module_version,\n" +
"    procinfo.procedure_name,\n" +
"	procinfo.procedure_version,\n" +
"	procinfo.proc_instance_name,\n" +
"	urs.req_id,\n" +
"	urs.parent_code,\n" +
"	urs.code,\n" +
"	urs.active,\n" +
"	urs.in_system,\n" +
"	urs.in_scope,	\n" +
"	reqs.solution_id,\n" + 
"    reqs.window_name AS window_name,\n" +
"    reqs.label_en AS window_label_en,\n" +
"    reqs.label_es AS window_label_es,\n" +
"    reqs.order_number,\n" +
"    reqs.roles,\n" +
"    reqs.type,\n" +
"    reqs.sop_name,\n" +
"    modact.endpoint_name,\n" +
"    modact.pretty_name_en,\n" +
"    modact.pretty_name_es,\n" +
"    modact.order_number AS mod_order_number,\n" +
"    modact.entity,\n" +
"	reqs.json_model\n" +
"     FROM requirements. procedure_req_solution reqs\n" +
"   	 JOIN requirements.procedure_user_requirements urs on urs.req_id=reqs.req_id\n" +
"     JOIN requirements.procedure_info procinfo ON reqs.proc_instance_name::text = procinfo.proc_instance_name::text\n" +
"     JOIN requirements.module_actions_and_queries modact ON reqs.window_action::text = modact.endpoint_name::text AND procinfo.module_name::text = modact.module_name::text\n" +
"    where (reqs.type='"+ReqSolutionTypes.WINDOW_BUTTON.getTagValue()+"' or reqs.type='"+ReqSolutionTypes.TABLE_ROW_BUTTON.getTagValue()+"' ); ",
                null, "proc_req_solution_actions", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsReqs.viewProcReqSolutionActions.values(), "proc_req_user_requirements_actions",
                new EnumIntTablesJoin[]{
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.PROCEDURE_USER_REQS, "urs", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME, TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME, TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureReqSolution.WINDOW_ACTION, TblsReqs.ModuleActionsAndQueries.ENDPOINT_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER), //            new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", false,
                //                new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.MODULE_NAME, TblsReqs.ModuleActionsAndQueries.MODULE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                }, " and procInfo.module_name = modAct.module_name", false),
        PROC_REQ_SOLUTION_WINDOWS("SELECT procinfo.module_name,\n" +
"    procinfo.module_version,\n" +
"    procinfo.procedure_name,\n" +
"    procinfo.procedure_version,\n" +
"    procinfo.proc_instance_name,\n" +
"    urs.req_id,\n" +
"    urs.parent_code,\n" +
"    urs.code,\n" +
"    urs.active,\n" +
"    urs.in_system,\n" +
"    urs.in_scope,\n" +
"    reqs.solution_id,\n" +
"    reqs.window_name,\n" +
"	reqs.window_type,\n" +
"    reqs.window_query,\n" +
"    reqs.label_en AS action_label_en,\n" +
"    reqs.label_es AS action_label_es,\n" +
"    reqs.order_number,\n" +
"    reqs.roles,\n" +
"    reqs.type,\n" +
"    reqs.sop_name,\n" +
"    modact.endpoint_name,\n" +
"    modact.pretty_name_en,\n" +
"    modact.pretty_name_es,\n" +
"    modact.order_number AS mod_order_number,\n" +
"    modact.entity,\n" +
"    reqs.json_model, reqs.twoicons_detail, reqs.add_refresh_button, reqs.grid_columns, reqs.endpoint_params, reqs.enable_context_menu, reqs.add_actions_to_context_menu, \n" +
"    reqs.view_title_en, reqs.view_title_es \n" +
"   FROM requirements.procedure_req_solution reqs\n" +
"     JOIN requirements.procedure_user_requirements urs ON urs.req_id = reqs.req_id\n" +
"     JOIN requirements.procedure_info procinfo ON reqs.proc_instance_name::text = procinfo.proc_instance_name::text\n" +
"     JOIN requirements.module_actions_and_queries modact ON reqs.window_query::text = modact.endpoint_name::text AND procinfo.module_name::text = modact.module_name::text\n" +
"  WHERE reqs.type::text = '"+ReqSolutionTypes.WINDOW.getTagValue()+"'::text;",
                null, "proc_req_solution_windows", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsReqs.viewProcReqSolutionViews.values(), "proc_req_user_requirements_actions",
                new EnumIntTablesJoin[]{
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.PROCEDURE_USER_REQS, "urs", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME, TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME, TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureReqSolution.WINDOW_ACTION, TblsReqs.ModuleActionsAndQueries.ENDPOINT_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER), //            new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", false,
                //                new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.MODULE_NAME, TblsReqs.ModuleActionsAndQueries.MODULE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                }, " and procInfo.module_name = modAct.module_name", false),
        PROC_REQ_SOLUTION_SPECIAL_VIEWS(" SELECT procinfo.module_name,\n" +
"    procinfo.module_version,\n" +
"    procinfo.procedure_name,\n" +
"    procinfo.procedure_version,\n" +
"    procinfo.proc_instance_name,\n" +
"    urs.req_id,\n" +
"    urs.parent_code,\n" +
"    urs.code,\n" +
"    urs.active,\n" +
"    urs.in_system,\n" +
"    urs.in_scope,\n" +
"    reqs.solution_id,\n" +
"    reqs.window_name,\n" +
"    reqs.window_type,\n" +
"    reqs.window_mode,\n" +
"    reqs.window_query,\n" +
"    reqs.label_en AS window_label_en,\n" +
"    reqs.label_es AS window_label_es,\n" +
"    reqs.order_number,\n" +
"    reqs.roles, reqs.twoicons_detail, reqs.content_type, \n" +
"    reqs.type,\n" +
"    reqs.sop_name,\n" +
"    modact.view_name,\n" +
"    modact.order_number AS mod_order_number,\n" +
"    modact.entity,\n" +
"    modact.json_model\n" +
"   FROM requirements.procedure_req_solution reqs\n" +
"     JOIN requirements.procedure_user_requirements urs ON urs.req_id = reqs.req_id\n" +
"     JOIN requirements.procedure_info procinfo ON reqs.proc_instance_name::text = procinfo.proc_instance_name::text\n" +
"     JOIN requirements.module_special_views modact ON reqs.special_view_name::text = modact.view_name::text \n" +
"	 AND procinfo.module_name::text = modact.module_name::text\n" +
"  WHERE reqs.type::text = '"+ReqSolutionTypes.SPECIAL_VIEW.getTagValue()+"'::text;",
                null, "proc_req_solution_special_views", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsReqs.viewProcReqSolutionSpecialViews.values(), "proc_req_user_requirements_actions",
                new EnumIntTablesJoin[]{
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.PROCEDURE_USER_REQS, "urs", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME, TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME, TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                    new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_REQ_SOLUTION, "reqs", TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", false,
                            new EnumIntTableFields[][]{{TblsReqs.ProcedureReqSolution.WINDOW_ACTION, TblsReqs.ModuleActionsAndQueries.ENDPOINT_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER), //            new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", false,
                //                new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.MODULE_NAME, TblsReqs.ModuleActionsAndQueries.MODULE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
                }, " and procInfo.module_name = modAct.module_name", false),
        BUSINESS_RULES_IN_SOLUTION("SELECT busRules.module_name, busRules.module_version, procinfo.procedure_name, procinfo.procedure_version, procinfo.proc_instance_name, busRules.rule_name, busRules.is_mandatory,\n" +
                        " 		busRules.api_name,	busRules.area, busRules.prerequite,\n" +
                	" busrules.values_list, busrules.tip_en, busrules.tip_es,\n" +
                        "    COALESCE(count(sol.business_rule), 0::bigint) AS present,\n" +
                        "    sstring_agg(COALESCE(sol.code::text, sol.parent_code::text), ', ') AS requirements_list \n" +
                        "   FROM requirements.module_business_rules busRules\n" +
                        "   JOIN requirements.procedure_info procinfo ON busrules.module_name::text = procinfo.module_name::text "+
                        "   LEFT JOIN (select reqsol.business_rule, usr.procedure_name, usr.procedure_version, usr.proc_instance_name, usr.parent_code, usr.code \n" +
                        "          		from requirements.procedure_req_solution reqsol, requirements.procedure_user_requirements usr\n" +
                        "			  where reqsol.req_id=usr.req_id AND upper(reqsol.window_element_type::text) =upper("+ReqSolutionTypes.BUSINESS_RULE.getTagValue()+")) sol\n" +
                        "	  ON busRules.rule_name::text = sol.business_rule::text and procinfo.proc_instance_name=sol.proc_instance_name\n" +
                        "  GROUP BY busRules.module_name, busRules.module_version, sol.procedure_name, sol.procedure_version, sol.proc_instance_name, busRules.rule_name, busRules.is_mandatory, busRules.api_name,	busRules.area, busRules.prerequite\n" +
                        "  ORDER BY (COALESCE(count(sol.business_rule), 0::bigint)), busRules.rule_name; ",
                null, "business_rules_in_solution", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsReqs.viewBusinessRulesInSolution.values(), "viewBusinessRulesInSolution",
                null, " and procInfo.module_name = modAct.module_name", false),
        ACTIONS_IN_SOLUTION("SELECT act.module_name, act.module_version, procinfo.procedure_name, procinfo.procedure_version, procinfo.proc_instance_name, act.entity, act.endpoint_name,\n" +
                        " 		act.api_name,	act.pretty_name_en, act.pretty_name_es,	act.query_for_button, act.extra_actions, act.arguments_array,\n" +
                        "    COALESCE(count(sol.window_action), 0::bigint) AS present,\n" +
                        "   string_agg(COALESCE(sol.code::text, sol.parent_code::text), ', ') AS requirements_list, act.output_object_types \n" +
                        "   FROM requirements.module_actions_and_queries act\n" +
                        "   JOIN requirements.procedure_info procinfo ON act.module_name::text = procinfo.module_name::text"+
                        "   LEFT JOIN (select reqsol.window_action, usr.code, usr.parent_code, usr.procedure_name, usr.procedure_version, usr.proc_instance_name \n" +
                        "          		from requirements.procedure_req_solution reqsol, requirements.procedure_user_requirements usr\n" +
                        "			  where reqsol.req_id=usr.req_id and upper(window_element_type)=upper("+ReqSolutionTypes.WINDOW_BUTTON.getTagValue()+")) sol\n" +
                        "	  ON act.endpoint_name::text = sol.window_action::text and procinfo.proc_instance_name=sol.proc_instance_name\n" +
                        "	WHERE upper(act.api_name::text) ~~ '%ACTION%'::text OR upper(act.api_name::text) ~~ '%QUER%'::text AND act.query_for_button = true" +
                        "  GROUP BY act.module_name, act.module_version, act.entity, act.endpoint_name, sol.procedure_name, sol.procedure_version, sol.proc_instance_name,\n" +
                        " 		act.api_name,	act.pretty_name_en, act.pretty_name_es\n" +
                        "   ORDER BY act.entity, act.api_name, (COALESCE(count(sol.window_action), 0::bigint)); ",
                null, "actions_in_solution", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsReqs.viewActionsInSolution.values(), "viewactionsInSolution",
                null, " and procInfo.module_name = modAct.module_name", false),
        QUERIES_IN_SOLUTION("SELECT act.module_name, act.module_version, procinfo.procedure_name, procinfo.procedure_version, procinfo.proc_instance_name, act.entity, act.endpoint_name,\n" +
                        " 		act.api_name,	act.pretty_name_en, act.pretty_name_es,	act.arguments_array,	\n" +
                        "    COALESCE(count(sol.window_query), 0::bigint) AS present,\n" +
                        "    string_agg(COALESCE(sol.code::text, sol.parent_code::text), ', ') AS requirements_list, act.output_object_types, act.json_model \n" +
                        "   FROM requirements.module_actions_and_queries act\n" +
                        "   LEFT JOIN (select reqsol.window_query, usr.procedure_name, usr.procedure_version, usr.proc_instance_name, usr.parent_code, usr.code \n" +
                        "          		from requirements.procedure_req_solution reqsol, requirements.procedure_user_requirements usr\n" +
                        "			  where reqsol.req_id=usr.req_id and upper(window_element_type)=upper("+ReqSolutionTypes.WINDOW.getTagValue()+")) sol\n" +
                        "	  ON act.endpoint_name::text = sol.window_query::text and procinfo.proc_instance_name=sol.proc_instance_name\n" +
                        "	WHERE upper(act.api_name) like '%QUER%'\n" +
                        "  GROUP BY act.module_name, act.module_version, sol.procedure_name, sol.procedure_version, sol.proc_instance_name, act.entity, act.endpoint_name,\n" +
                        " 		act.api_name,	act.pretty_name_en, act.pretty_name_es\n" +
                        "   ORDER BY (COALESCE(count(sol.window_query), 0::bigint)) desc, act.entity, act.api_name;",
                null, "queries_in_solution", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsReqs.viewQueriesInSolution.values(), "viewBusinessRulesInSolution",
                null, " and procInfo.module_name = modAct.module_name", false),
        SPECIAL_VIEWS_IN_SOLUTION("SELECT spvw.module_name,\n" +
                        "    spvw.module_version,\n" +
                        "    procinfo.procedure_name,\n" +
                        "    procinfo.procedure_version,\n" +
                        "    procinfo.proc_instance_name,\n" +
                        "    spvw.entity,\n" +
                        "    spvw.view_name,\n" +
                        "    spvw.pretty_name_en,\n" +
                        "    spvw.pretty_name_es,\n" +
                        "    COALESCE(count(sol.parent_code), 0::bigint) AS present,\n" +
                        "    string_agg(COALESCE(sol.code::text, sol.parent_code::text), ', ') AS requirements_list \n" +
                        "   FROM requirements.module_special_views spvw\n" +
                        "     JOIN requirements.procedure_info procinfo ON spvw.module_name::text = procinfo.module_name::text\n" +
                        "     LEFT JOIN ( SELECT reqsol.special_view_name,\n" +
                        "            usr.parent_code, usr.code,\n" +
                        "            usr.procedure_name,\n" +
                        "            usr.procedure_version,\n" +
                        "            usr.proc_instance_name\n" +
                        "           FROM requirements.procedure_req_solution reqsol,\n" +
                        "            requirements.procedure_user_requirements usr\n" +
                        "          WHERE reqsol.req_id = usr.req_id AND upper(reqsol.type::text) = 'SPECIAL VIEW'::text) sol \n" +
                        "		  	ON spvw.view_name::text = sol.special_view_name::text and procinfo.proc_instance_name=sol.proc_instance_name \n" +
                        "  GROUP BY spvw.module_name, spvw.module_version, spvw.entity, spvw.view_name, spvw.pretty_name_en, spvw.pretty_name_es, procinfo.procedure_name, procinfo.procedure_version, procinfo.proc_instance_name\n" +
                        "  ORDER BY (COALESCE(count(sol.special_view_name), 0::bigint)) DESC, spvw.entity, spvw.view_name;",
                null, "special_views_in_solution", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsReqs.viewSpecialViewsInSolution.values(), "viewSpecialViewsInSolution",
                null, " and procInfo.module_name = modAct.module_name", false),
        PROCEDURE_TABLES_AND_VIEWS_LOG(" SELECT 'table'::text AS object_type,\n" +
"    dt.procedure_name,\n" +
"    dt.procedure_version,\n" +
"    dt.proc_instance_name,\n" +
"        CASE\n" +
"            WHEN dc.table_schema IS NOT NULL THEN 'Deployed'::text\n" +
"            ELSE 'Not deployed'::text\n" +
"        END AS deployment_status,\n" +
"    dt.schema_name,\n" +
"    dt.name,\n" +
"    dt.active,\n" +
"    dt.is_view,\n" +
"    dt.field_name,\n" +
"    dt.fields_to_exclude,\n" +
"    dt.order_number,\n" +
"    dt.definition_en,\n" +
"    dt.definition_es,\n" +
"    dt.is_mandatory\n" +
"   FROM requirements.procedure_module_tables dt\n" +
"     LEFT JOIN information_schema.tables dc ON dc.table_schema::text = ((COALESCE(dt.proc_instance_name, ''::character varying)::text || '-'::text) || COALESCE(dt.schema_name, ''::character varying)::text) AND dt.name::text = dc.table_name::text AND dt.is_view = false\n" +
"UNION\n" +
" SELECT 'view'::text AS object_type,\n" +
"    dt.procedure_name,\n" +
"    dt.procedure_version,\n" +
"    dt.proc_instance_name,\n" +
"        CASE\n" +
"            WHEN dc.table_schema IS NOT NULL THEN 'Deployed'::text\n" +
"            ELSE 'Not deployed'::text\n" +
"        END AS deployment_status,\n" +
"    dt.schema_name,\n" +
"    dt.name,\n" +
"    dt.active,\n" +
"    dt.is_view,\n" +
"    dt.field_name,\n" +
"    dt.fields_to_exclude,\n" +
"    dt.order_number,\n" +
"    dt.definition_en,\n" +
"    dt.definition_es,\n" +
"    dt.is_mandatory\n" +
"   FROM requirements.procedure_module_tables dt\n" +
"     LEFT JOIN information_schema.views dc ON dc.table_schema::text = ((COALESCE(dt.proc_instance_name, ''::character varying)::text || '-'::text) || COALESCE(dt.schema_name, ''::character varying)::text) "
                + "AND dt.name::text = dc.table_name::text AND dt.is_view = true;"
                + "ALTER VIEW  #SCHEMA.#TBL  OWNER TO #OWNER;",
                null, "procedure_tables_and_views_deployment_log", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProcedureTablesAndViewsDeploymentLog.values(), "ProcedureTablesAndViewsDeploymentLog",
                new EnumIntTablesJoin[]{}, " ", false
        ),
        PROC_REQ_SOLUTION_VIEW(
" CREATE OR REPLACE VIEW requirements.procedure_req_solution_view\n" +
"as select\n" +
" 	req.parent_code,\n" +
"	req.code,\n" +
"    solution_id,\n" +
"    sol.req_id,\n" +
"    sol.procedure_name,\n" +
"    sol.procedure_version,\n" +
"    sol.proc_instance_name,\n" +
"    sol.order_number ,\n" +
"    name ,\n" +
"    lp_frontend_page_name ,\n" +
"    lp_frontend_page_filter ,\n" +
"    \"position\" ,\n" +
"    mode ,\n" +
"    type ,\n" +
"    label_en ,\n" +
"    label_es ,\n" +
"    sop ,\n" +
"    window_name ,\n" +
"    window_action ,\n" +
"    business_rule ,\n" +
"    business_rule_value ,\n" +
"    confirmation_dialog ,\n" +
"    confirmation_dialog_detail ,\n" +
"    roles ,\n" +
"    sop_name ,\n" +
"    training_req ,\n" +
"    training_name ,\n" +
"    uat_req ,\n" +
"    uat_name ,\n" +
"    req.active ,\n" +
"    req.in_scope ,\n" +
"    req.in_system ,\n" +
"    window_query ,\n" +
"    entity,\n" +
"    window_type,\n" +
"    window_mode,\n" +
"    business_rule_area,\n" +
"    special_view_json_model ,\n" +
"    special_view_name ,\n" +
"    json_model ,\n" +
"    query_for_button ,\n" +
"    extra_actions ,\n" +
"    twoicons_detail,\n" +
"    add_refresh_button ,\n" +
"    grid_columns ,\n" +
"    endpoint_params ,\n" +
"    enable_context_menu ,\n" +
"    add_actions_to_context_menu,\n" +
"    view_title_en ,\n" +
"    view_title_es ,\n" +
"    content_type \n" +
"	from requirements.procedure_req_solution sol, requirements.procedure_user_requirements req\n" +
"	where sol.req_id=req.req_id\n" +
"	;\n" +
"\n" +
"\n" +
"ALTER TABLE requirements.procedure_req_solution_view\n" +
"    OWNER to labplanet;",                
null, "procedure_req_solution_view", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, viewProcReqAndSolutionView.values(), "viewProcReqAndSolutionView",
                new EnumIntTablesJoin[]{}, " ", false                
        ),
        ; 
        private ViewsReqs(String viewScript, FldBusinessRules[] fldBusRules, String dbVwName, String repositoryName, Boolean isProcedure, EnumIntViewFields[] vwFlds,
                String comment, EnumIntTablesJoin[] tablesInView, String extraFilters, Boolean useFixViewScript) {
            this.getTblBusinessRules = fldBusRules;
            this.viewName = dbVwName;
            this.viewFields = vwFlds;
            this.repositoryName = repositoryName;
            this.isProcedure = isProcedure;
            this.viewComment = comment;
            this.viewScript = viewScript;
            this.tablesInTheView = tablesInView;
            this.extraFilters = extraFilters;
            this.useFixViewScript=useFixViewScript;
        }
    @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public String getViewCreatecript() {return this.viewScript;}
        @Override        public String getViewName() {return this.viewName;}
        @Override        public EnumIntViewFields[] getViewFields() {return this.viewFields;}
        @Override        public String getViewComment() {return this.viewComment;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
        @Override        public String getExtraFilters() {return this.extraFilters;}
        @Override        public Boolean getUsesFixScriptView() {return this.useFixViewScript;}
    
        private final FldBusinessRules[] getTblBusinessRules;
        private final String viewName;
        private final String repositoryName;
        private final Boolean isProcedure;
        private final EnumIntViewFields[] viewFields;
        private final String viewComment;
        private final String viewScript;
        private final EnumIntTablesJoin[] tablesInTheView;
        private final String extraFilters;
        private final Boolean useFixViewScript;
        @Override    public EnumIntTablesJoin[] getTablesRequiredInView() {
            return this.tablesInTheView;
        }
    }

    public enum Modules implements EnumIntTableFields {
        MODULE_NAME(FIELDS_NAMES_MODULE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION(FIELDS_NAMES_MODULE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        DESCRIPTION_EN(FIELDS_NAMES_DESCRIPTION+"_en", LPDatabase.string(), null, null, null, null),
        DESCRIPTION_ES(FIELDS_NAMES_DESCRIPTION+"_es", LPDatabase.string(), null, null, null, null),
        PRETTY_EN("pretty_name_en", LPDatabase.string(), null, null, null, null),        
        PRETTY_ES("pretty_name_es", LPDatabase.string(), null, null, null, null),
        PICTURE("picture", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        INFO_JSON("info_json", LPDatabase.json(), null, null, null, null),
        MODULE_SETTINGS("module_settings", LPDatabase.json(), null, null, null, null),
        ;
        private Modules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ModuleActionsAndQueries implements EnumIntTableFields {
        MODULE_NAME(FIELDS_NAMES_MODULE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION(FIELDS_NAMES_MODULE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        API_NAME("api_name", LPDatabase.stringNotNull(), null, null, null, null),
        ENDPOINT_NAME("endpoint_name", LPDatabase.stringNotNull(), null, null, null, null),
        ENTITY("entity", LPDatabase.string(), null, null, null, null),
        PRETTY_EN("pretty_name_en", LPDatabase.string(), null, null, null, null),
        PRETTY_ES("pretty_name_es", LPDatabase.string(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        JSON_MODEL("json_model", LPDatabase.json(), null, null, null, null),
        QUERY_FOR_BUTTON("query_for_button", LPDatabase.booleanFld(false), null, null, null, null),
        EXTRA_ACTIONS("extra_actions", LPDatabase.stringNotNull(), null, null, null, null),
        ARGUMENTS_ARRAY("arguments_array", LPDatabase.string(), null, null, null, null),
        OUTPUT_OBJECT_TYPES("output_object_types", LPDatabase.string(), null, null, null, null)
        ;
        private ModuleActionsAndQueries(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ModuleBusinessRules implements EnumIntTableFields {
        MODULE_NAME(FIELDS_NAMES_MODULE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION(FIELDS_NAMES_MODULE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        API_NAME("api_name", LPDatabase.stringNotNull(), null, null, null, null),
        AREA("area", LPDatabase.stringNotNull(), null, null, null, null),
        RULE_NAME("rule_name", LPDatabase.stringNotNull(), null, null, null, null),
        IS_MANDATORY("is_mandatory", LPDatabase.booleanFld(true), null, null, null, null),
        PREREQUISITE("prerequite",LPDatabase.string(), null, null, null, null),
        VALUES_LIST("values_list",LPDatabase.json(), null, null, null, null),        
        TIP_EN("tip_en",LPDatabase.string(), null, null, null, null),        
        TIP_ES("tip_es",LPDatabase.string(), null, null, null, null)
        ;
        private ModuleBusinessRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ModuleSpecialViews implements EnumIntTableFields {
        MODULE_NAME(FIELDS_NAMES_MODULE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION(FIELDS_NAMES_MODULE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        VIEW_NAME("view_name", LPDatabase.stringNotNull(), null, null, null, null),
        ENTITY("entity", LPDatabase.string(), null, null, null, null),
        JSON_MODEL("json_model", LPDatabase.json(), null, null, null, null),
        JSON_REQUIREMENTS("json_requirements", LPDatabase.json(), null, null, null, null),
        WINDOW_TYPE("window_type", LPDatabase.string(), null, null, null, null),
        PRETTY_EN("pretty_name_en", LPDatabase.string(), null, null, null, null),
        PRETTY_ES("pretty_name_es", LPDatabase.string(), null, null, null, null)
        ;
        private ModuleSpecialViews(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }
    
    public enum ModuleErrorNotifications implements EnumIntTableFields {
        MODULE_NAME(FIELDS_NAMES_MODULE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION(FIELDS_NAMES_MODULE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        API_NAME("api_name", LPDatabase.stringNotNull(), null, null, null, null),
        ERROR_CODE("error_code", LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private ModuleErrorNotifications(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }
    
    public enum ModuleManuals implements EnumIntTableFields {
        MODULE_NAME(LPDatabase.FIELDS_NAMES_MODULE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION(LPDatabase.FIELDS_NAMES_MODULE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        MANUAL_NAME("manual_name", LPDatabase.stringNotNull(), null, null, null, null),
        MANUAL_VERSION("manual_version", LPDatabase.integerNotNull(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        IS_VIDEO("is_video", LPDatabase.booleanFld(false), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        AREA("area", LPDatabase.string(), null, null, null, null),;

        private ModuleManuals(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }
    
    public enum ModuleTablesAndViews implements EnumIntTableFields {
        MODULE_NAME(LPDatabase.FIELDS_NAMES_MODULE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION(LPDatabase.FIELDS_NAMES_MODULE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        SCHEMA_NAME("schema_name", LPDatabase.stringNotNull(), null, null, null, null),
        NAME("name", LPDatabase.string(), null, null, null, null),
        IS_VIEW("is_view", LPDatabase.booleanFld(false), null, null, null, null),
        IS_MANDATORY("is_mandatory", LPDatabase.booleanFld(true), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        PURPOSE_EN("purpose_en", LPDatabase.string(), null, null, null, null),
        PURPOSE_ES("purpose_es", LPDatabase.string(), null, null, null, null),
        FIELDS_TO_INCLUDE("fields_to_include", LPDatabase.string(), null, null, null, null),
        FIELDS_TO_EXCLUDE("fields_to_exclude", LPDatabase.string(), null, null, null, null)
        ;
        private ModuleTablesAndViews(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureInfo implements EnumIntTableFields {
        PROCEDURE_NAME(FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION("procedure_version", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_HASH_CODE("procedure_hash_code", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_NAME("module_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION("module_version", LPDatabase.integer(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.stringNotNull(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.stringNotNull(), null, null, null, null),
        LOCKED_FOR_ACTIONS("locked_for_actions", LPDatabase.booleanFld(false), null, null, null, null),
        NAVIGATION_ICON_NAME("navigation_icon_name", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        MODULE_SETTINGS("module_settings", LPDatabase.json(), null, null, null, null)
        ;
        private ProcedureInfo(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureRoles implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.stringNotNull(), null, null, null, null),;

        private ProcedureRoles(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureSopMetaData implements EnumIntTableFields {
        SOP_NAME("sop_name", LPDatabase.stringNotNull(), null, null, null, null, true),
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null, true),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null, true),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null, true),        
        SOP_VERSION("sop_version", LPDatabase.integerNotNull(), null, null, null, null, true),
        SOP_REVISION("sop_revision", LPDatabase.integer(), null, null, null, null, true),
        CURRENT_STATUS("current_status", LPDatabase.string(), null, null, null, null, true),
        EXPIRES("expires", LPDatabase.booleanFld(false), null, null, null, null, false),
        HAS_CHILD("has_child", LPDatabase.booleanFld(false), null, null, null, null, false),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null, false),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null, false), // ....
        ;

        private ProcedureSopMetaData(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystemField) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
            this.isSystemField=isSystemField;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;
        private final Boolean isSystemField;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;
        
        @Override
        public Boolean isSystemField() {
            return this.isSystemField;
        }

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureUserRequirements implements EnumIntTableFields {
        REQ_ID("req_id", LPDatabase.integerNotNull(), null, null, null, null),
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        CODE("code", LPDatabase.string(), null, null, null, null),
        PARENT_CODE("parent_code", LPDatabase.string(), null, null, null, null),
/*        BRANCH_NEED("branch_need", LPDatabase.string(), null, null, null, null),
        NAME("name", LPDatabase.string(), null, null, null, null),
        WINDOW_ELEMENT_TYPE("window_element_type", LPDatabase.string(), null, null, null, null),
        WINDOW_NAME("window_name", LPDatabase.string(), null, null, null, null),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null),
        WINDOW_ACTION("window_action", LPDatabase.string(), null, null, null, null),
        BUSINESS_RULE("business_rule", LPDatabase.string(), null, null, null, null),
        BUSINESS_RULE_VALUE("business_rule_value", LPDatabase.string(), null, null, null, null),*/
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        IN_SCOPE("in_scope", LPDatabase.booleanFld(), null, null, null, null),
        IN_SYSTEM("in_system", LPDatabase.booleanFld(), null, null, null, null),
/*        CONFIRM_DIALOG("confirmation_dialog", LPDatabase.string(), null, null, null, null),
        CONFIRM_DIALOG_DETAIL("confirmation_dialog_detail", LPDatabase.string(), null, null, null, null),
        SOLUTION_TYPE("solution_type", LPDatabase.string(), null, null, null, null),
        ROLES("roles", LPDatabase.string(), null, null, null, null),
        SOP_NAME("sop_name", LPDatabase.string(), null, null, null, null),
        TRAINING_REQ("training_req", LPDatabase.booleanFld(), null, null, null, null),
        TRAINING_NAME("training_name", LPDatabase.string(), null, null, null, null),
        UAT_REQ("uat_req", LPDatabase.booleanFld(), null, null, null, null),
        UAT_NAME("uat_name", LPDatabase.string(), null, null, null, null), 
*/        
        /*        WIDGET("widget", LPDatabase.string(), null, null, null, null),
        WIDGET_VERSION("widget_version", LPDatabase.integer(), null, null, null, null),
        WIDGET_ACTION("widget_action", LPDatabase.string(), null, null, null, null),
        WIDGET_ACCESS_MODE("widget_access_mode", LPDatabase.string(), null, null, null, null),
        WIDGET_TYPE("widget_type", LPDatabase.string(), null, null, null, null),
        WIDGET_LABEL_EN("widget_label_en", LPDatabase.string(), null, null, null, null),
        WIDGET_LABEL_ES("widget_label_es", LPDatabase.string(), null, null, null, null),*/;

        private ProcedureUserRequirements(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureRiskAssessment implements EnumIntTableFields {
        RISK_ID("risk_id", LPDatabase.integerNotNull(), null, null, null, null),
        REQ_ID("req_id", LPDatabase.integerNotNull(), null, null, null, null),        
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        LEVEL("level", LPDatabase.string(), null, null, null, null),
        COMMENTS("comments", LPDatabase.string(), null, null, null, null), // ....
        HASTOBE_PART_OF_TESTING("has_to_be_part_of_testing", LPDatabase.booleanFld(false), null, null, null, null),
        EXPECTED_TESTS("EXPECTED_TESTS", LPDatabase.string(), null, null, null, null),
        OWNER("current_status", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        CREATION_DATE("created_date", LPDatabase.dateTime(), "to_char(" + "created_date" + ",'YYYY-MM-DD HH:MI')", null, null, null),
        CREATED_BY("created_by", LPDatabase.string(), null, new ReferenceFld(GlobalVariables.Schemas.CONFIG.getName(), TblsAppConfig.TablesAppConfig.PERSON.getTableName(), TblsAppConfig.Person.PERSON_ID.getName()), null, null),
        READY_FOR_REVISION("ready_for_revision", LPDatabase.booleanFld(false), null, null, null, null),
        SIGNED("signed", LPDatabase.booleanFld(false), null, null, null, null),
        ;

        private ProcedureRiskAssessment(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }
    
    public enum ProcedureReqSolution implements EnumIntTableFields {
        SOLUTION_ID("solution_id", LPDatabase.integerNotNull(), null, null, null, null, true),
        REQ_ID("req_id", LPDatabase.integerNotNull(), null, null, null, null, true),                
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null, true),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null, true),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null, true),        
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null, false),
        NAME("name", LPDatabase.string(), null, null, null, null, false),
        LP_FRONTEND_PAGE_NAME("lp_frontend_page_name", LPDatabase.string(), null, null, null, null, false),
        LP_FRONTEND_PAGE_FILTER("lp_frontend_page_filter", LPDatabase.string(), null, null, null, null, false),
        //PARENT_NAME("parent_name", LPDatabase.string(), null, null, null, null),
        POSITION("position", LPDatabase.string(), null, null, null, null, false),
        MODE("mode", LPDatabase.string(), null, null, null, null, false),
        TYPE("type", LPDatabase.string(), null, null, null, null, false),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null, false),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null, false),
        SOP("sop", LPDatabase.string(), null, null, null, null, false),
        //ESIGN_REQUIRED("esign_required", LPDatabase.booleanFld(), null, null, null, null),
        //USERCONFIRM_REQUIRED("userconfirm_required", LPDatabase.booleanFld(), null, null, null, null),
        //ICON_NAME("icon_name", LPDatabase.string(), null, null, null, null, false),
        //ICON_NAME_WHEN_NOT_CERTIFIED("icon_name_when_not_certified", LPDatabase.string(), null, null, null, null, false),
        //BRANCH_NEED("branch_need", LPDatabase.string(), null, null, null, null),        
        WINDOW_NAME("window_name", LPDatabase.string(), null, null, null, null, false),
        WINDOW_TYPE("window_type", LPDatabase.string(), null, null, null, null, false),
        WINDOW_MODE("window_mode", LPDatabase.string(), null, null, null, null, false),
        WINDOW_QUERY("window_query", LPDatabase.string(), null, null, null, null, false),
        ENTITY("entity", LPDatabase.string(), null, null, null, null, false),
        WINDOW_ACTION("window_action", LPDatabase.string(), null, null, null, null, false),
        BUSINESS_RULE("business_rule", LPDatabase.string(), null, null, null, null, false),
        BUSINESS_RULE_VALUE("business_rule_value", LPDatabase.string(), null, null, null, null, false),
        BUSINESS_RULE_AREA("business_rule_area", LPDatabase.string(), null, null, null, null, false),
        CONFIRM_DIALOG("confirmation_dialog", LPDatabase.string(), null, null, null, null, false),
        CONFIRM_DIALOG_DETAIL("confirmation_dialog_detail", LPDatabase.string(), null, null, null, null, false),
        //SOLUTION_TYPE("solution_type", LPDatabase.string(), null, null, null, null),
        ROLES("roles", LPDatabase.string(), null, null, null, null, false),
        SOP_NAME("sop_name", LPDatabase.string(), null, null, null, null, false),
        TRAINING_REQ("training_req", LPDatabase.booleanFld(), null, null, null, null, false),
        TRAINING_NAME("training_name", LPDatabase.string(), null, null, null, null, false),
        UAT_REQ("uat_req", LPDatabase.booleanFld(), null, null, null, null, false),
        UAT_NAME("uat_name", LPDatabase.string(), null, null, null, null, false),         
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null, false),
        IN_SCOPE("in_scope", LPDatabase.booleanFld(true), null, null, null, null, false),
        IN_SYSTEM("in_system", LPDatabase.booleanFld(true), null, null, null, null, false),
        JSON_MODEL("json_model", LPDatabase.json(), null, null, null, null, true),
        SPECIAL_VIEW_JSON_MODEL("special_view_json_model", LPDatabase.json(), null, null, null, null, true),
        SPECIAL_VIEW_NAME("special_view_name", LPDatabase.string(), null, null, null, null, false), 
        QUERY_FOR_BUTTON("query_for_button", LPDatabase.booleanFld(false), null, null, null, null, true),
        EXTRA_ACTIONS("extra_actions", LPDatabase.stringNotNull(), null, null, null, null, true),
        CONTENT_TYPE("content_type", LPDatabase.stringNotNull(), null, null, null, null, true),
        TWOICONS_DETAIL("twoicons_detail", LPDatabase.json(), null, null, null, null, true),
/*        ADD_REFRESH_BUTTON("add_refresh_button", LPDatabase.booleanFld(true), null, null, null, null, false),
        GRID_COLUMNS("grid_columns", LPDatabase.json(), null, null, null, null, true),
        ENDPOINT_PARAMS("endpoint_params", LPDatabase.json(), null, null, null, null, true),
        ENABLE_CONTEXT_MENU("enable_context_menu", LPDatabase.booleanFld(true), null, null, null, null, false),
        ADD_ACTIONS_TO_CONTEXT_MENU("add_actions_to_context_menu", LPDatabase.booleanFld(false), null, null, null, null, false),
        VIEW_TITLE_EN("view_title_en", LPDatabase.string(), null, null, null, null, false),
        VIEW_TITLE_ES("view_title_es", LPDatabase.string(), null, null, null, null, false)*/
        ;
        private ProcedureReqSolution(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override        public String getFieldComment() {            return this.fieldComment;        }
        private final FldBusinessRules[] fldBusinessRules;
        @Override        public FldBusinessRules[] getFldBusinessRules() {            return this.fldBusinessRules;        }
        private final Boolean isSystemFld;
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }


    public enum ProcedureReqSolutionViewTableWithButtons implements EnumIntTableFields {
        TABLE_ID("table_id", LPDatabase.integerNotNull(), null, null, null, null, true),                
        SOLUTION_ID("solution_id", LPDatabase.integerNotNull(), null, null, null, null, true),
        REQ_ID("req_id", LPDatabase.integerNotNull(), null, null, null, null, true),                
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null, true),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null, true),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null, true),        
        TAB_ID("tab_id", LPDatabase.integer(), null, null, null, null, true),                
        TYPE("type", LPDatabase.string(), null, null, null, null, false),
        LABEL_EN("label_en", LPDatabase.string(), null, null, null, null, false),
        LABEL_ES("label_es", LPDatabase.string(), null, null, null, null, false),
        WINDOW_NAME("window_name", LPDatabase.string(), null, null, null, null, false),
        WINDOW_TYPE("window_type", LPDatabase.string(), null, null, null, null, false),
        WINDOW_MODE("window_mode", LPDatabase.string(), null, null, null, null, false),
        WINDOW_QUERY("window_query", LPDatabase.string(), null, null, null, null, false),
        ENTITY("entity", LPDatabase.string(), null, null, null, null, false),
        QUERY_FOR_BUTTON("query_for_button", LPDatabase.booleanFld(false), null, null, null, null, true),
        EXTRA_ACTIONS("extra_actions", LPDatabase.stringNotNull(), null, null, null, null, true),
        TWOICONS_DETAIL("twoicons_detail", LPDatabase.json(), null, null, null, null, true),
        ADD_REFRESH_BUTTON("add_refresh_button", LPDatabase.booleanFld(true), null, null, null, null, false),
        GRID_COLUMNS("grid_columns", LPDatabase.json(), null, null, null, null, true),
        ENDPOINT_PARAMS("endpoint_params", LPDatabase.json(), null, null, null, null, true),
        ENDPOINT_PARAMS_SUBFILTER("endpoint_params_subfilter_designer", LPDatabase.string(), null, null, null, null, true),
        ENABLE_CONTEXT_MENU("enable_context_menu", LPDatabase.booleanFld(true), null, null, null, null, false),
        ADD_ACTIONS_TO_CONTEXT_MENU("add_actions_to_context_menu", LPDatabase.booleanFld(false), null, null, null, null, false),
        VIEW_TITLE_EN("view_title_en", LPDatabase.string(), null, null, null, null, false),
        VIEW_TITLE_ES("view_title_es", LPDatabase.string(), null, null, null, null, false),
        GRID_COLUMNS_DESIGNER("grid_columns_designer", LPDatabase.string(), null, null, null, null, true),
        TWOICONS_DETAIL_DESIGNER("twoicons_detail_designer", LPDatabase.string(), null, null, null, null, true),
        ;
        private ProcedureReqSolutionViewTableWithButtons(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override        public String getFieldComment() {            return this.fieldComment;        }
        private final FldBusinessRules[] fldBusinessRules;
        @Override        public FldBusinessRules[] getFldBusinessRules() {            return this.fldBusinessRules;        }
        private final Boolean isSystemFld;
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }

    public enum ProcedureReqSolutionViewTabs implements EnumIntTableFields {
        SOLUTION_ID("solution_id", LPDatabase.integerNotNull(), null, null, null, null, true),
        TAB_ID("tab_id", LPDatabase.integerNotNull(), null, null, null, null, true),                
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null, false),
        LABEL_EN("tab_label_en", LPDatabase.string(), null, null, null, null, false),
        LABEL_ES("tab_label_es", LPDatabase.string(), null, null, null, null, false),
        CONTENT_TYPE("content_type", LPDatabase.string(), null, null, null, null, false)
        ;
        private ProcedureReqSolutionViewTabs(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules, Boolean isSystFld) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
            this.isSystemFld=isSystFld;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override        public String getFieldComment() {            return this.fieldComment;        }
        private final FldBusinessRules[] fldBusinessRules;
        @Override        public FldBusinessRules[] getFldBusinessRules() {            return this.fldBusinessRules;        }
        private final Boolean isSystemFld;
        @Override        public Boolean isSystemField(){return this.isSystemFld;}
    }
    
    /**
     *
     */
    public enum ProcedureUserRoles implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        USER_NAME("user_name", LPDatabase.stringNotNull(), null, null, null, null),
        ROLE_NAME("role_name", LPDatabase.stringNotNull(), null, null, null, null), // ....
        ;

        private ProcedureUserRoles(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    /**
     *
     */
    public enum ProcedureUsers implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        USER_NAME("user_name", LPDatabase.stringNotNull(), null, null, null, null),
        FULL_NAME("full_name", LPDatabase.string(), null, null, null, null);

        private ProcedureUsers(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    /**
     *
     */
    public enum ProcedureModuleTables implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        SCHEMA_NAME("schema_name", LPDatabase.stringNotNull(), null, null, null, null),
        TABLE_NAME("name", LPDatabase.string(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        IS_VIEW("is_view", LPDatabase.booleanNotNull(false), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        IS_MANDATORY("is_mandatory", LPDatabase.booleanNotNull(true), null, null, null, null),
        FIELD_NAME(GlobalAPIsParams.LBL_FIELD_NAME, LPDatabase.string(), null, null, null, null),
        FIELDS_TO_EXCLUDE("fields_to_exclude", LPDatabase.string(), null, null, null, null),
        DEFINITION_EN("definition_en", LPDatabase.string(), null, null, null, null),
        DEFINITION_ES("definition_es", LPDatabase.string(), null, null, null, null)                
        ;
        private ProcedureModuleTables(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureBusinessRules implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        CATEGORY("category", LPDatabase.string(), null, null, null, null),
        EXPLANATION("explanation", LPDatabase.string(), null, null, null, null),
        VALUES_ALLOWED("values_allowed", LPDatabase.string(), null, null, null, null),
        MODULE_NAME("module_name", LPDatabase.stringNotNull(), null, null, null, null),
        MODULE_VERSION("module_version", LPDatabase.integerNotNull(), null, null, null, null),
        FILE_SUFFIX("file_suffix", LPDatabase.string(), null, null, null, null),
        RULE_NAME("rule_name", LPDatabase.string(), null, null, null, null),
        RULE_VALUE("rule_value", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),;

        private ProcedureBusinessRules(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureMasterData implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        OBJECT_TYPE("object_type", LPDatabase.string(), null, null, null, null),
        JSON_OBJ("json_obj", LPDatabase.string(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),;

        private ProcedureMasterData(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureFEModel implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(LPDatabase.FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        MODEL_JSON("model_json", LPDatabase.json(), null, null, null, null),
        MODEL_JSON_MOBILE("model_json_mobile", LPDatabase.json(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(), null, null, null, null),;

        private ProcedureFEModel(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureManuals implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        MANUAL_NAME("manual_name", LPDatabase.stringNotNull(), null, null, null, null),
        MANUAL_VERSION("manual_version", LPDatabase.integerNotNull(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        IS_VIDEO("is_video", LPDatabase.booleanFld(false), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        AREA("area", LPDatabase.string(), null, null, null, null),;

        private ProcedureManuals(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    /*
            new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", TblsReqs.TablesReqs.PROCEDURE_USER_REQS, "reqs", false,
                new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.PROC_INSTANCE_NAME, TblsReqs.ProcedureUserRequirements.PROC_INSTANCE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
            new EnumIntTablesJoin(TblsReqs.TablesReqs.PROCEDURE_INFO, "procInfo", TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", false,
                new EnumIntTableFields[][]{{TblsReqs.ProcedureInfo.MODULE_NAME, TblsReqs.ModuleActionsAndQueries.MODULE_NAME}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
            new EnumIntTablesJoin(TblsReqs.TablesReqs.MODULE_ACTIONS_N_QUERIES, "modAct", TblsReqs.TablesReqs.PROCEDURE_USER_REQS, "reqs", false,
                new EnumIntTableFields[][]{{TblsReqs.ModuleActionsAndQueries.ENDPOINT_NAME, TblsReqs.ProcedureUserRequirements.WINDOW_ACTION}}, "", SqlStatementEnums.JOIN_TYPES.INNER),
     */
    public enum viewProcReqSolutionActions implements EnumIntViewFields {
        MODULE_NAME("procInfo", ProcedureInfo.MODULE_NAME.getName(), "procInfo.module_name as module_name", ProcedureInfo.MODULE_NAME, null, null, null),
        MODULE_VERSION("procInfo", ProcedureInfo.MODULE_VERSION.getName(), "procInfo.module_verion as module_version", ProcedureInfo.MODULE_VERSION, null, null, null),
        PROCEDURE_NAME("procInfo", ProcedureReqSolution.PROCEDURE_NAME.getName(), "procInfo.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("procInfo", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "procInfo.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("procInfo", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "procInfo.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        REQ_ID("urs", ProcedureUserRequirements.REQ_ID.getName(), "urs.req_id as req_id", ProcedureUserRequirements.REQ_ID, null, null, null),
        PARENT_CODE("urs", ProcedureUserRequirements.PARENT_CODE.getName(), "urs.parent_code as parent_code", ProcedureUserRequirements.PARENT_CODE, null, null, null),
        CODE("urs", ProcedureUserRequirements.CODE.getName(), "urs.code as code", ProcedureUserRequirements.CODE, null, null, null),
        IN_SYSTEM("urs", ProcedureUserRequirements.IN_SYSTEM.getName(), "urs.in_system as in_system", ProcedureUserRequirements.IN_SYSTEM, null, null, null),
        ACTIVE("urs", ProcedureUserRequirements.ACTIVE.getName(), "urs.active as active", ProcedureUserRequirements.ACTIVE, null, null, null),
        IN_SCOPE("urs", ProcedureUserRequirements.IN_SCOPE.getName(), "urs.in_scope as in_scope", ProcedureUserRequirements.IN_SCOPE, null, null, null),
        SOLUTION_ID("reqs", ProcedureReqSolution.SOLUTION_ID.getName(), "reqs.solution_id as solution_id", ProcedureReqSolution.SOLUTION_ID, null, null, null),
        NAME("reqs", ProcedureReqSolution.NAME.getName(), "reqs.name as name", ProcedureReqSolution.NAME, null, null, null),
        WINDOW_ACTION("reqs", ProcedureReqSolution.WINDOW_ACTION.getName(), "reqs.window_action as window_action", ProcedureReqSolution.WINDOW_ACTION, null, null, null),
        WINDOW_NAME("reqs", ProcedureReqSolution.WINDOW_NAME.getName(), "reqs.window_name as window_name", ProcedureReqSolution.WINDOW_NAME, null, null, null),
        WINDOW_LABEL_EN("reqs", "window_label_en", "reqs.label_en as window_label_en", ProcedureReqSolution.LABEL_EN, null, null, null),
        WINDOW_LABEL_ES("reqs", "window_label_es", "reqs.label_es as window_label_es", ProcedureReqSolution.LABEL_ES, null, null, null),
        ORDER_NUMBER("reqs", ProcedureReqSolution.ORDER_NUMBER.getName(), "reqs.order_number as order_number", ProcedureUserRequirements.ORDER_NUMBER, null, null, null),
        ROLES("reqs", ProcedureReqSolution.ROLES.getName(), "reqs.roles as roles", ProcedureReqSolution.ROLES, null, null, null),
        TYPE("reqs", ProcedureReqSolution.TYPE.getName(), "reqs.type as type", ProcedureReqSolution.TYPE, null, null, null),
        SOP_NAME("reqs", "sop_name", "reqs.sop_name as sop_name", ProcedureReqSolution.SOP_NAME, null, null, null),
        ENDPOINT_NAME("modAct", ModuleActionsAndQueries.ENDPOINT_NAME.getName(), "modAct.endpoint_name as endpoint_name", ModuleActionsAndQueries.ENDPOINT_NAME, null, null, null),
        PRETTY_EN("modAct", ModuleActionsAndQueries.PRETTY_EN.getName(), "modAct.pretty_name_en as pretty_name_en", ModuleActionsAndQueries.PRETTY_EN, null, null, null),
        PRETTY_ES("modAct", ModuleActionsAndQueries.PRETTY_ES.getName(), "modAct.pretty_name_es as pretty_name_es", ModuleActionsAndQueries.PRETTY_ES, null, null, null),
        MOD_ORDER_NUMBER("modAct", "mod_order_number", "modAct.order_number as mod_order_number", ModuleActionsAndQueries.ORDER_NUMBER, null, null, null), 
        ENTITY("modAct", ModuleActionsAndQueries.ENTITY.getName(), "modAct.entity as entity", ModuleActionsAndQueries.ENTITY, null, null, null),
        JSON_MODEL("reqs", ModuleActionsAndQueries.JSON_MODEL.getName(), "reqs.json_model as json_model", ModuleActionsAndQueries.JSON_MODEL, null, null, null)
       
        /*        RAW_VALUE_NUM("raw_value_num", "CASE " +
"            WHEN isnumeric(sar.raw_value::text) THEN to_number(sar.raw_value::text, '9999'::text) " +
"            ELSE NULL::numeric END AS raw_value_num"
             , SampleAnalysisResult.REPLICA, null, null, null),
        PRETTY_VALUE("pretty_value", "sar.pretty_value", SampleAnalysisResult.PRETTY_VALUE, null, null, null),
        SAMPLE_ANALYSIS_STATUS("sample_analysis_status", "sa.status  AS sample_analysis_status", SampleAnalysis.STATUS, null, null, null),
         */;

        private viewProcReqSolutionActions(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
//            try{
//            this.fldName="";
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
            /*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
@Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }

    public enum viewProcReqAndSolutionView implements EnumIntViewFields {
        PARENT_CODE("req", ProcedureUserRequirements.PARENT_CODE.getName(), "req.parent_code as parent_code", ProcedureUserRequirements.PARENT_CODE, null, null, null),
        CODE("req", ProcedureUserRequirements.CODE.getName(), "req.code as code", ProcedureUserRequirements.CODE, null, null, null),
        REQ_ID("sol", ProcedureUserRequirements.REQ_ID.getName(), "sol.req_id as req_id", ProcedureUserRequirements.REQ_ID, null, null, null),
        PROCEDURE_NAME("sol", ProcedureReqSolution.PROCEDURE_NAME.getName(), "sol.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("sol", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "sol.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("sol", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "sol.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        ORDER_NUMBER("sol", ProcedureReqSolution.ORDER_NUMBER.getName(), "sol.order_number as order_number", ProcedureUserRequirements.ORDER_NUMBER, null, null, null),
        NAME("sol", ProcedureReqSolution.NAME.getName(), "sol.name as name", ProcedureReqSolution.NAME, null, null, null),
        LP_FRONTEND_PAGE_NAME("sol", ProcedureReqSolution.LP_FRONTEND_PAGE_NAME.getName(), "sol.lp_frontend_page_name as lp_frontend_page_name", ProcedureReqSolution.LP_FRONTEND_PAGE_NAME, null, null, null),
        LP_FRONTEND_PAGE_FILTER("sol", ProcedureReqSolution.LP_FRONTEND_PAGE_FILTER.getName(), "sol.lp_frontend_page_filter as lp_frontend_page_filter", ProcedureReqSolution.LP_FRONTEND_PAGE_FILTER, null, null, null),
        POSITION("sol", ProcedureReqSolution.POSITION.getName(), "sol.position as position", ProcedureReqSolution.POSITION, null, null, null),
        MODE("sol", ProcedureReqSolution.MODE.getName(), "sol.mode as mode", ProcedureReqSolution.MODE, null, null, null),
        TYPE("sol", ProcedureReqSolution.TYPE.getName(), "sol.type as type", ProcedureReqSolution.TYPE, null, null, null),
        WINDOW_LABEL_EN("sol", "window_label_en", "sol.window_label_en as window_label_en", ProcedureReqSolution.LABEL_EN, null, null, null),
        WINDOW_LABEL_ES("sol", "window_label_es", "sol.window_label_es as window_label_es", ProcedureReqSolution.LABEL_ES, null, null, null),
        SOP("sol", "sop", "sol.sop as sop", ProcedureReqSolution.SOP_NAME, null, null, null),
        WINDOW_NAME("sol", ProcedureReqSolution.WINDOW_NAME.getName(), "sol.window_name as window_name", ProcedureReqSolution.WINDOW_NAME, null, null, null),
        WINDOW_ACTION("sol", ProcedureReqSolution.WINDOW_ACTION.getName(), "sol.window_action as window_action", ProcedureReqSolution.WINDOW_ACTION, null, null, null),
        BUSINESS_RULE("sol", ProcedureReqSolution.BUSINESS_RULE.getName(), "sol.business_rule as business_rule", ProcedureReqSolution.BUSINESS_RULE, null, null, null),
        BUSINESS_RULE_VALUE("sol", ProcedureReqSolution.BUSINESS_RULE_VALUE.getName(), "sol.business_rule_value as business_rule_value", ProcedureReqSolution.BUSINESS_RULE_VALUE, null, null, null),
        BUSINESS_RULE_AREA("sol", ProcedureReqSolution.BUSINESS_RULE_AREA.getName(), "sol.business_rule_area as business_rule_area", ProcedureReqSolution.BUSINESS_RULE_AREA, null, null, null),
        CONFIRM_DIALOG("sol", ProcedureReqSolution.CONFIRM_DIALOG.getName(), "sol.confirmation_dialog as confirmation_dialog", ProcedureReqSolution.CONFIRM_DIALOG, null, null, null),
        CONFIRM_DIALOG_DETAIL("sol", ProcedureReqSolution.CONFIRM_DIALOG_DETAIL.getName(), "sol.confirmation_dialog_detail as confirmation_dialog_detail", ProcedureReqSolution.CONFIRM_DIALOG_DETAIL, null, null, null),
        ROLES("sol", ProcedureReqSolution.ROLES.getName(), "sol.roles as roles", ProcedureReqSolution.ROLES, null, null, null),
        SOP_NAME("sol", "sop_name", "sol.sop_name as sop_name", ProcedureReqSolution.SOP_NAME, null, null, null),
        TRAINING_REQ("sol", ProcedureReqSolution.TRAINING_REQ.getName(), "sol.training_req as training_req", ProcedureReqSolution.TRAINING_REQ, null, null, null),
        TRAINING_NAME("sol", ProcedureReqSolution.TRAINING_NAME.getName(), "sol.training_name as training_name", ProcedureReqSolution.TRAINING_NAME, null, null, null),
        UAT_REQ("sol", ProcedureReqSolution.UAT_REQ.getName(), "sol.uat_req as uat_req", ProcedureReqSolution.UAT_REQ, null, null, null),
        UAT_NAME("sol", ProcedureReqSolution.UAT_NAME.getName(), "sol.uat_name as uat_name", ProcedureReqSolution.UAT_NAME, null, null, null),
        ACTIVE("sol", ProcedureUserRequirements.ACTIVE.getName(), "sol.active as active", ProcedureUserRequirements.ACTIVE, null, null, null),
        IN_SCOPE("sol", ProcedureUserRequirements.IN_SCOPE.getName(), "sol.in_scope as in_scope", ProcedureUserRequirements.IN_SCOPE, null, null, null),
        IN_SYSTEM("sol", ProcedureUserRequirements.IN_SYSTEM.getName(), "sol.in_system as in_system", ProcedureUserRequirements.IN_SYSTEM, null, null, null),
        WINDOW_QUERY("sol", ProcedureReqSolution.WINDOW_QUERY.getName(), "sol.window_query as window_query", ProcedureReqSolution.WINDOW_QUERY, null, null, null),
        ENTITY("sol", ModuleActionsAndQueries.ENTITY.getName(), "sol.entity as entity", ModuleActionsAndQueries.ENTITY, null, null, null),
        WINDOW_TYPE("sol", ProcedureReqSolution.WINDOW_TYPE.getName(), "sol.window_type as window_type", ProcedureReqSolution.WINDOW_TYPE, null, null, null),
        WINDOW_MODE("sol", ProcedureReqSolution.WINDOW_MODE.getName(), "sol.window_mode as window_mode", ProcedureReqSolution.WINDOW_MODE, null, null, null),        

        JSON_MODEL("sol", ProcedureReqSolution.JSON_MODEL.getName(), "sol.json_model as json_model", ProcedureReqSolution.JSON_MODEL, null, null, null),
        SPECIAL_VIEW_JSON_MODEL("sol", ProcedureReqSolution.SPECIAL_VIEW_JSON_MODEL.getName(), "sol.special_view_json_model as special_view_json_model", ProcedureReqSolution.SPECIAL_VIEW_JSON_MODEL, null, null, null),
        SPECIAL_VIEW_NAME("sol", ProcedureReqSolution.SPECIAL_VIEW_NAME.getName(), "sol.special_view_name as special_view_name", ProcedureReqSolution.SPECIAL_VIEW_NAME, null, null, null),
        QUERY_FOR_BUTTON("sol", ProcedureReqSolution.QUERY_FOR_BUTTON.getName(), "sol.query_for_button as query_for_button", ProcedureReqSolution.QUERY_FOR_BUTTON, null, null, null),
        EXTRA_ACTIONS("sol", ProcedureReqSolution.EXTRA_ACTIONS.getName(), "sol.extra_actions as extra_actions", ProcedureReqSolution.EXTRA_ACTIONS, null, null, null),

        TWOICONS_DETAIL("sol", ProcedureReqSolution.TWOICONS_DETAIL.getName(), "sol.twoicons_detail as twoicons_detail", ProcedureReqSolution.TWOICONS_DETAIL, null, null, null),
        ADD_REFRESH_BUTTON("sol", ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON.getName(), "sol.add_refresh_button as add_refresh_button", ProcedureReqSolutionViewTableWithButtons.ADD_REFRESH_BUTTON, null, null, null),
        GRID_COLUMNS("sol", ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS.getName(), "sol.grid_columns as grid_columns", ProcedureReqSolutionViewTableWithButtons.GRID_COLUMNS, null, null, null),
        ENDPOINT_PARAMS("sol", ProcedureReqSolutionViewTableWithButtons.ENDPOINT_PARAMS.getName(), "sol.endpoint_params as endpoint_params", ProcedureReqSolutionViewTableWithButtons.ENDPOINT_PARAMS, null, null, null),
        ENABLE_CONTEXT_MENU("sol", ProcedureReqSolutionViewTableWithButtons.ENABLE_CONTEXT_MENU.getName(), "sol.enable_context_menu as enable_context_menu", ProcedureReqSolutionViewTableWithButtons.ENABLE_CONTEXT_MENU, null, null, null),
        ADD_ACTIONS_TO_CONTEXT_MENU("sol", ProcedureReqSolutionViewTableWithButtons.ADD_ACTIONS_TO_CONTEXT_MENU.getName(), "reqs.add_actions_to_context_menu as add_actions_to_context_menu", ProcedureReqSolutionViewTableWithButtons.ADD_ACTIONS_TO_CONTEXT_MENU, null, null, null),        
        VIEW_TITLE_EN("sol", ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_EN.getName(), "sol.view_title_en as view_title_en", ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_EN, null, null, null),
        VIEW_TITLE_ES("sol", ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_ES.getName(), "sol.view_title_es as view_title_es", ProcedureReqSolutionViewTableWithButtons.VIEW_TITLE_ES, null, null, null),
        CONTENT_TYPE("sol", ProcedureReqSolution.CONTENT_TYPE.getName(), "sol.content_type as content_type", ProcedureReqSolution.CONTENT_TYPE, null, null, null),
        ;

        private viewProcReqAndSolutionView(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
//            try{
//            this.fldName="";
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
            /*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }

    public enum viewProcReqSolutionViews implements EnumIntViewFields {
        MODULE_NAME("procInfo", ProcedureInfo.MODULE_NAME.getName(), "procInfo.module_name as module_name", ProcedureInfo.MODULE_NAME, null, null, null),
        MODULE_VERSION("procInfo", ProcedureInfo.MODULE_VERSION.getName(), "procInfo.module_verion as module_version", ProcedureInfo.MODULE_VERSION, null, null, null),
        PROCEDURE_NAME("procInfo", ProcedureReqSolution.PROCEDURE_NAME.getName(), "procInfo.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("procInfo", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "procInfo.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("procInfo", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "procInfo.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        REQ_ID("urs", ProcedureUserRequirements.REQ_ID.getName(), "urs.req_id as req_id", ProcedureUserRequirements.REQ_ID, null, null, null),
        PARENT_CODE("urs", ProcedureUserRequirements.PARENT_CODE.getName(), "urs.parent_code as parent_code", ProcedureUserRequirements.PARENT_CODE, null, null, null),
        CODE("urs", ProcedureUserRequirements.CODE.getName(), "urs.code as code", ProcedureUserRequirements.CODE, null, null, null),
        IN_SYSTEM("urs", ProcedureUserRequirements.IN_SYSTEM.getName(), "urs.in_system as in_system", ProcedureUserRequirements.IN_SYSTEM, null, null, null),
        ACTIVE("urs", ProcedureUserRequirements.ACTIVE.getName(), "urs.active as active", ProcedureUserRequirements.ACTIVE, null, null, null),
        IN_SCOPE("urs", ProcedureUserRequirements.IN_SCOPE.getName(), "urs.in_scope as in_scope", ProcedureUserRequirements.IN_SCOPE, null, null, null),
        SOLUTION_ID("reqs", ProcedureReqSolution.SOLUTION_ID.getName(), "reqs.solution_id as solution_id", ProcedureReqSolution.SOLUTION_ID, null, null, null),
        NAME("reqs", ProcedureReqSolution.NAME.getName(), "reqs.name as name", ProcedureReqSolution.NAME, null, null, null),
        WINDOW_QUERY("reqs", ProcedureReqSolution.WINDOW_QUERY.getName(), "reqs.window_query as window_query", ProcedureReqSolution.WINDOW_QUERY, null, null, null),
        WINDOW_NAME("reqs", ProcedureReqSolution.WINDOW_NAME.getName(), "reqs.window_name as window_name", ProcedureReqSolution.WINDOW_NAME, null, null, null),
        WINDOW_TYPE("reqs", ProcedureReqSolution.WINDOW_TYPE.getName(), "reqs.window_type as window_type", ProcedureReqSolution.WINDOW_TYPE, null, null, null),
        WINDOW_MODE("reqs", ProcedureReqSolution.WINDOW_MODE.getName(), "reqs.window_mode as window_mode", ProcedureReqSolution.WINDOW_MODE, null, null, null),
        WINDOW_LABEL_EN("reqs", "window_label_en", "reqs.window_label_en as window_label_en", ProcedureReqSolution.LABEL_EN, null, null, null),
        WINDOW_LABEL_ES("reqs", "window_label_es", "reqs.window_label_es as window_label_es", ProcedureReqSolution.LABEL_ES, null, null, null),
        ORDER_NUMBER("reqs", ProcedureReqSolution.ORDER_NUMBER.getName(), "reqs.order_number as order_number", ProcedureUserRequirements.ORDER_NUMBER, null, null, null),
        ROLES("reqs", ProcedureReqSolution.ROLES.getName(), "reqs.roles as roles", ProcedureReqSolution.ROLES, null, null, null),
        TYPE("reqs", ProcedureReqSolution.TYPE.getName(), "reqs.type as type", ProcedureReqSolution.TYPE, null, null, null),
        SOP_NAME("reqs", "sop_name", "reqs.sop_name as sop_name", ProcedureReqSolution.SOP_NAME, null, null, null),
        ENDPOINT_NAME("modAct", ModuleActionsAndQueries.ENDPOINT_NAME.getName(), "modAct.endpoint_name as endpoint_name", ModuleActionsAndQueries.ENDPOINT_NAME, null, null, null),
        PRETTY_EN("modAct", ModuleActionsAndQueries.PRETTY_EN.getName(), "modAct.pretty_name_en as pretty_name_en", ModuleActionsAndQueries.PRETTY_EN, null, null, null),
        PRETTY_ES("modAct", ModuleActionsAndQueries.PRETTY_ES.getName(), "modAct.pretty_name_es as pretty_name_es", ModuleActionsAndQueries.PRETTY_ES, null, null, null),
        MOD_ORDER_NUMBER("modAct", "mod_order_number", "modAct.order_number as mod_order_number", ModuleActionsAndQueries.ORDER_NUMBER, null, null, null), 
        ENTITY("modAct", ModuleActionsAndQueries.ENTITY.getName(), "modAct.entity as entity", ModuleActionsAndQueries.ENTITY, null, null, null),
        CONTENT_TYPE("reqs", ProcedureReqSolution.CONTENT_TYPE.getName(), "reqs.content_type as content_type", ProcedureReqSolution.CONTENT_TYPE, null, null, null),
        TWOICONS_DETAIL("reqs", ProcedureReqSolution.TWOICONS_DETAIL.getName(), "reqs.twoicons_detail as twoicons_detail", ProcedureReqSolution.TWOICONS_DETAIL, null, null, null),
        /*        JSON_MODEL("reqs", ModuleActionsAndQueries.JSON_MODEL.getName(), "reqs.json_model as json_model", ModuleActionsAndQueries.JSON_MODEL, null, null, null),
        
        ADD_REFRESH_BUTTON("reqs", ProcedureReqSolution.ADD_REFRESH_BUTTON.getName(), "reqs.add_refresh_button as add_refresh_button", ProcedureReqSolution.ADD_REFRESH_BUTTON, null, null, null),
        GRID_COLUMNS("reqs", ProcedureReqSolution.GRID_COLUMNS.getName(), "reqs.grid_columns as grid_columns", ProcedureReqSolution.GRID_COLUMNS, null, null, null),
        ENDPOINT_PARAMS("reqs", ProcedureReqSolution.ENDPOINT_PARAMS.getName(), "reqs.endpoint_params as endpoint_params", ProcedureReqSolution.ENDPOINT_PARAMS, null, null, null),
        ENABLE_CONTEXT_MENU("reqs", ProcedureReqSolution.ENABLE_CONTEXT_MENU.getName(), "reqs.enable_context_menu as enable_context_menu", ProcedureReqSolution.ENABLE_CONTEXT_MENU, null, null, null),
        ADD_ACTIONS_TO_CONTEXT_MENU("reqs", ProcedureReqSolution.ADD_ACTIONS_TO_CONTEXT_MENU.getName(), "reqs.add_actions_to_context_menu as add_actions_to_context_menu", ProcedureReqSolution.ADD_ACTIONS_TO_CONTEXT_MENU, null, null, null),
        VIEW_TITLE_EN("reqs", ProcedureReqSolution.VIEW_TITLE_EN.getName(), "reqs.view_title_en as view_title_en", ProcedureReqSolution.VIEW_TITLE_EN, null, null, null),
        VIEW_TITLE_ES("reqs", ProcedureReqSolution.VIEW_TITLE_ES.getName(), "reqs.view_title_es as view_title_es", ProcedureReqSolution.VIEW_TITLE_ES, null, null, null)
*/        
        
        /*        RAW_VALUE_NUM("raw_value_num", "CASE " +
"            WHEN isnumeric(sar.raw_value::text) THEN to_number(sar.raw_value::text, '9999'::text) " +
"            ELSE NULL::numeric END AS raw_value_num"
             , SampleAnalysisResult.REPLICA, null, null, null),
        PRETTY_VALUE("pretty_value", "sar.pretty_value", SampleAnalysisResult.PRETTY_VALUE, null, null, null),
        SAMPLE_ANALYSIS_STATUS("sample_analysis_status", "sa.status  AS sample_analysis_status", SampleAnalysis.STATUS, null, null, null),
         */
        ;

        private viewProcReqSolutionViews(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
//            try{
//            this.fldName="";
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
            /*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }

    public enum viewProcReqSolutionSpecialViews implements EnumIntViewFields {
        MODULE_NAME("procInfo", ProcedureInfo.MODULE_NAME.getName(), "procInfo.module_name as module_name", ProcedureInfo.MODULE_NAME, null, null, null),
        MODULE_VERSION("procInfo", ProcedureInfo.MODULE_VERSION.getName(), "procInfo.module_verion as module_version", ProcedureInfo.MODULE_VERSION, null, null, null),
        PROCEDURE_NAME("procInfo", ProcedureReqSolution.PROCEDURE_NAME.getName(), "procInfo.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("procInfo", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "procInfo.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("procInfo", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "procInfo.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        REQ_ID("urs", ProcedureUserRequirements.REQ_ID.getName(), "urs.req_id as req_id", ProcedureUserRequirements.REQ_ID, null, null, null),
        PARENT_CODE("urs", ProcedureUserRequirements.PARENT_CODE.getName(), "urs.parent_code as parent_code", ProcedureUserRequirements.PARENT_CODE, null, null, null),
        CODE("urs", ProcedureUserRequirements.CODE.getName(), "urs.code as code", ProcedureUserRequirements.CODE, null, null, null),
        IN_SYSTEM("urs", ProcedureUserRequirements.IN_SYSTEM.getName(), "urs.in_system as in_system", ProcedureUserRequirements.IN_SYSTEM, null, null, null),
        ACTIVE("urs", ProcedureUserRequirements.ACTIVE.getName(), "urs.active as active", ProcedureUserRequirements.ACTIVE, null, null, null),
        IN_SCOPE("urs", ProcedureUserRequirements.IN_SCOPE.getName(), "urs.in_scope as in_scope", ProcedureUserRequirements.IN_SCOPE, null, null, null),
        SOLUTION_ID("reqs", ProcedureReqSolution.SOLUTION_ID.getName(), "reqs.solution_id as solution_id", ProcedureReqSolution.SOLUTION_ID, null, null, null),
        NAME("reqs", ProcedureReqSolution.NAME.getName(), "reqs.name as name", ProcedureReqSolution.NAME, null, null, null),
        WINDOW_QUERY("reqs", ProcedureReqSolution.WINDOW_QUERY.getName(), "reqs.window_query as window_query", ProcedureReqSolution.WINDOW_QUERY, null, null, null),
        WINDOW_NAME("reqs", ProcedureReqSolution.WINDOW_NAME.getName(), "reqs.window_name as window_name", ProcedureReqSolution.WINDOW_NAME, null, null, null),
        WINDOW_TYPE("reqs", ProcedureReqSolution.WINDOW_TYPE.getName(), "reqs.window_type as window_type", ProcedureReqSolution.WINDOW_TYPE, null, null, null),
        WINDOW_MODE("reqs", ProcedureReqSolution.WINDOW_MODE.getName(), "reqs.window_mode as window_mode", ProcedureReqSolution.WINDOW_MODE, null, null, null),
        WINDOW_LABEL_EN("reqs", "window_label_en", "reqs.window_label_en as window_label_en", ProcedureReqSolution.LABEL_EN, null, null, null),
        WINDOW_LABEL_ES("reqs", "window_label_es", "reqs.window_label_es as window_label_es", ProcedureReqSolution.LABEL_ES, null, null, null),
        ORDER_NUMBER("reqs", ProcedureReqSolution.ORDER_NUMBER.getName(), "reqs.order_number as order_number", ProcedureUserRequirements.ORDER_NUMBER, null, null, null),
        ROLES("reqs", ProcedureReqSolution.ROLES.getName(), "reqs.roles as roles", ProcedureReqSolution.ROLES, null, null, null),
        TYPE("reqs", ProcedureReqSolution.TYPE.getName(), "reqs.type as type", ProcedureReqSolution.TYPE, null, null, null),
        SOP_NAME("reqs", "sop_name", "reqs.sop_name as sop_name", ProcedureReqSolution.SOP_NAME, null, null, null),
        VIEW_NAME("modAct", ModuleSpecialViews.VIEW_NAME.getName(), "modAct.endpoint_name as endpoint_name", ModuleSpecialViews.VIEW_NAME, null, null, null),
        MOD_ORDER_NUMBER("modAct", "mod_order_number", "modAct.order_number as mod_order_number", ModuleActionsAndQueries.ORDER_NUMBER, null, null, null), 
        ENTITY("modAct", ModuleActionsAndQueries.ENTITY.getName(), "modAct.entity as entity", ModuleActionsAndQueries.ENTITY, null, null, null),
        JSON_MODEL("modact", ModuleActionsAndQueries.JSON_MODEL.getName(), "modact.json_model as json_model", ModuleActionsAndQueries.JSON_MODEL, null, null, null),
        CONTENT_TYPE("reqs", ProcedureReqSolution.CONTENT_TYPE.getName(), "reqs.content_type as content_type", ProcedureReqSolution.CONTENT_TYPE, null, null, null),
        TWOICONS_DETAIL("reqs", ProcedureReqSolution.TWOICONS_DETAIL.getName(), "reqs.twoicons_detail as twoicons_detail", ProcedureReqSolution.TWOICONS_DETAIL, null, null, null),
       
        /*        RAW_VALUE_NUM("raw_value_num", "CASE " +
"            WHEN isnumeric(sar.raw_value::text) THEN to_number(sar.raw_value::text, '9999'::text) " +
"            ELSE NULL::numeric END AS raw_value_num"
             , SampleAnalysisResult.REPLICA, null, null, null),
        PRETTY_VALUE("pretty_value", "sar.pretty_value", SampleAnalysisResult.PRETTY_VALUE, null, null, null),
        SAMPLE_ANALYSIS_STATUS("sample_analysis_status", "sa.status  AS sample_analysis_status", SampleAnalysis.STATUS, null, null, null),
         */;

        private viewProcReqSolutionSpecialViews(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
//            try{
//            this.fldName="";
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
            /*            }catch(Exception e){
                String s= e.getMessage();
                //String s2=name;
                this.fldName="";
            }*/
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }

    public enum viewBusinessRulesInSolution implements EnumIntViewFields {
        MODULE_NAME("busRules", ModuleBusinessRules.MODULE_NAME.getName(), "busRules.module_name as module_name", ModuleBusinessRules.MODULE_NAME, null, null, null),
        MODULE_VERSION("busRules", ModuleBusinessRules.MODULE_VERSION.getName(), "busRules.module_version as module_version", ModuleBusinessRules.MODULE_VERSION, null, null, null),
        PROCEDURE_NAME("procInfo", ProcedureReqSolution.PROCEDURE_NAME.getName(), "procInfo.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("procInfo", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "procInfo.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("procInfo", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "procInfo.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        RULE_NAME("busRules", ModuleBusinessRules.RULE_NAME.getName(), "busRules.rule_name as rule_name", ModuleBusinessRules.RULE_NAME, null, null, null),
        IS_MANDATORY("busRules", ModuleBusinessRules.IS_MANDATORY.getName(), "busRules.is_mandatory as is_mandatory", ModuleBusinessRules.IS_MANDATORY, null, null, null),
        API_NAME("busRules", ModuleBusinessRules.API_NAME.getName(), "busRules.api_name as api_name", ModuleBusinessRules.API_NAME, null, null, null),
        AREA("busRules", ModuleBusinessRules.AREA.getName(), "busRules.area as area", ModuleBusinessRules.AREA, null, null, null),
        PREREQUISITE("busRules", ModuleBusinessRules.PREREQUISITE.getName(), "busRules.prerequite as prerequite", ModuleBusinessRules.PREREQUISITE, null, null, null),
        VALUES_LIST("busRules", ModuleBusinessRules.VALUES_LIST.getName(), "busRules.values_list as values_list", ModuleBusinessRules.VALUES_LIST, null, null, null),
        TIP_EN("busRules", ModuleBusinessRules.TIP_EN.getName(), "busRules.tip_en as tip_en", ModuleBusinessRules.TIP_EN, null, null, null),
        TIP_ES("busRules", ModuleBusinessRules.TIP_ES.getName(), "busRules.tip_es as tip_es", ModuleBusinessRules.TIP_ES, null, null, null),
        PRESENT("sol", "present", "sol.present as present", ModuleBusinessRules.MODULE_VERSION, null, null, null),
        REQUIREMENTS_LIST("sol", "requirements_list", "sol.requirements_list as requirements_list", ModuleBusinessRules.PREREQUISITE, null, null, null),
        ;
        private viewBusinessRulesInSolution(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }

    public enum viewActionsInSolution implements EnumIntViewFields {
        MODULE_NAME("act", ModuleActionsAndQueries.MODULE_NAME.getName(), "act.module_name as module_name", ModuleActionsAndQueries.MODULE_NAME, null, null, null),
        MODULE_VERSION("act", ModuleActionsAndQueries.MODULE_VERSION.getName(), "act.module_version as module_version", ModuleActionsAndQueries.MODULE_VERSION, null, null, null),
        PROCEDURE_NAME("procinfo", ProcedureReqSolution.PROCEDURE_NAME.getName(), "procinfo.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("procinfo", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "procinfo.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("procinfo", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "procinfo.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        ENTITY("act",TblsReqs.ModuleActionsAndQueries.ENTITY.getName(), "act.entity as entity", ModuleActionsAndQueries.ENTITY, null, null, null),
        API_NAME("act", ModuleActionsAndQueries.API_NAME.getName(), "act.api_name as api_name", ModuleActionsAndQueries.API_NAME, null, null, null),
        OUTPUT_OBJECT_TYPES("act", ModuleActionsAndQueries.OUTPUT_OBJECT_TYPES.getName(), "act.output_object_types as output_object_types", ModuleActionsAndQueries.OUTPUT_OBJECT_TYPES, null, null, null),
        ENDPOINT_NAME("act", ModuleActionsAndQueries.ENDPOINT_NAME.getName(), "act.endpoint_name as endpoint_name", ModuleActionsAndQueries.ENDPOINT_NAME, null, null, null),
        PRETTY_EN("act", ModuleActionsAndQueries.PRETTY_EN.getName(), "act.pretty_name_en as pretty_name_en", ModuleActionsAndQueries.PRETTY_EN, null, null, null),
        PRETTY_ES("act", ModuleActionsAndQueries.PRETTY_ES.getName(), "act.pretty_name_eS as pretty_name_eS", ModuleActionsAndQueries.PRETTY_ES, null, null, null),
        QUERY_FOR_BUTTON("act", ModuleActionsAndQueries.QUERY_FOR_BUTTON.getName(), "act.query_for_button as query_for_button", ModuleActionsAndQueries.QUERY_FOR_BUTTON, null, null, null),
        EXTRA_ACTIONS("act", ModuleActionsAndQueries.EXTRA_ACTIONS.getName(), "act.extra_actions as extra_actions", ModuleActionsAndQueries.EXTRA_ACTIONS, null, null, null),
        PRESENT("sol", "present", "sol.present as present", ModuleBusinessRules.MODULE_VERSION, null, null, null),
        REQUIREMENTS_LIST("sol", "requirements_list", "sol.requirements_list as requirements_list", ModuleBusinessRules.PREREQUISITE, null, null, null),
        ARGUMENTS_ARRAY("act", ModuleActionsAndQueries.ARGUMENTS_ARRAY.getName(), "act.arguments_array as arguments_array", ModuleActionsAndQueries.ARGUMENTS_ARRAY, null, null, null),
        JSON_MODEL("act", ModuleActionsAndQueries.JSON_MODEL.getName(), "act.json_model as json_model", ModuleActionsAndQueries.JSON_MODEL, null, null, null)
        ;
        private viewActionsInSolution(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }
    public enum viewQueriesInSolution implements EnumIntViewFields {
        MODULE_NAME("act", ModuleActionsAndQueries.MODULE_NAME.getName(), "act.module_name as module_name", ModuleActionsAndQueries.MODULE_NAME, null, null, null),
        MODULE_VERSION("act", ModuleActionsAndQueries.MODULE_VERSION.getName(), "act.module_version as module_version", ModuleActionsAndQueries.MODULE_VERSION, null, null, null),
        PROCEDURE_NAME("procinfo", ProcedureReqSolution.PROCEDURE_NAME.getName(), "procinfo.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("procinfo", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "procinfo.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("procinfo", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "procinfo.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        ENTITY("act",TblsReqs.ModuleActionsAndQueries.ENTITY.getName(), "act.entity as entity", ModuleActionsAndQueries.ENTITY, null, null, null),
        API_NAME("act", ModuleActionsAndQueries.API_NAME.getName(), "act.api_name as api_name", ModuleActionsAndQueries.API_NAME, null, null, null),
        ENDPOINT_NAME("act", ModuleActionsAndQueries.ENDPOINT_NAME.getName(), "act.endpoint_name as endpoint_name", ModuleActionsAndQueries.ENDPOINT_NAME, null, null, null),
        OUTPUT_OBJECT_TYPES("act", ModuleActionsAndQueries.OUTPUT_OBJECT_TYPES.getName(), "act.output_object_types as output_object_types", ModuleActionsAndQueries.OUTPUT_OBJECT_TYPES, null, null, null),
        PRETTY_EN("act", ModuleActionsAndQueries.PRETTY_EN.getName(), "act.pretty_name_en as pretty_name_en", ModuleActionsAndQueries.PRETTY_EN, null, null, null),
        PRETTY_ES("act", ModuleActionsAndQueries.PRETTY_ES.getName(), "act.pretty_name_eS as pretty_name_eS", ModuleActionsAndQueries.PRETTY_ES, null, null, null),
        PRESENT("sol", "present", "sol.present as present", ModuleBusinessRules.MODULE_VERSION, null, null, null),
        REQUIREMENTS_LIST("sol", "requirements_list", "sol.requirements_list as requirements_list", ModuleBusinessRules.PREREQUISITE, null, null, null),
        ARGUMENTS_ARRAY("act", ModuleActionsAndQueries.ARGUMENTS_ARRAY.getName(), "act.arguments_array as arguments_array", ModuleActionsAndQueries.ARGUMENTS_ARRAY, null, null, null)
        ;
        private viewQueriesInSolution(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }
    public enum viewSpecialViewsInSolution implements EnumIntViewFields {
        MODULE_NAME("spvw", ModuleSpecialViews.MODULE_NAME.getName(), "spvw.module_name as module_name", ModuleSpecialViews.MODULE_NAME, null, null, null),
        MODULE_VERSION("spvw", ModuleSpecialViews.MODULE_VERSION.getName(), "spvw.module_version as module_version", ModuleSpecialViews.MODULE_VERSION, null, null, null),
        PROCEDURE_NAME("procinfo", ProcedureReqSolution.PROCEDURE_NAME.getName(), "procinfo.procedure_name as procedure_name", ProcedureUserRequirements.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("procinfo", ProcedureReqSolution.PROCEDURE_VERSION.getName(), "procinfo.procedure_version as procedure_version", ProcedureUserRequirements.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("procinfo", ProcedureReqSolution.PROC_INSTANCE_NAME.getName(), "procinfo.proc_instance_name as proc_instance_name", ProcedureUserRequirements.PROC_INSTANCE_NAME, null, null, null),
        ENTITY("spvw",TblsReqs.ModuleSpecialViews.ENTITY.getName(), "spvw.entity as entity", ModuleSpecialViews.ENTITY, null, null, null),
        VIEW_NAME("spvw", ModuleSpecialViews.VIEW_NAME.getName(), "spvw.api_name as api_name", ModuleSpecialViews.VIEW_NAME, null, null, null),
        PRETTY_EN("spvw", ModuleSpecialViews.PRETTY_EN.getName(), "spvw.pretty_name_en as pretty_name_en", ModuleSpecialViews.PRETTY_EN, null, null, null),
        PRETTY_ES("spvw", ModuleSpecialViews.PRETTY_ES.getName(), "spvw.pretty_name_eS as pretty_name_eS", ModuleSpecialViews.PRETTY_ES, null, null, null),
        PRESENT("sol", "present", "sol.present as present", ModuleBusinessRules.MODULE_VERSION, null, null, null),
        REQUIREMENTS_LIST("sol", "requirements_list", "sol.requirements_list as requirements_list", ModuleBusinessRules.PREREQUISITE, null, null, null),
        ;
        private viewSpecialViewsInSolution(String tblAliasInView, String name, String vwAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
            this.fldName = name;
            this.fldAliasInView = vwAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView=tblAliasInView;
        }
        private final String fldName;
        private final String tblAliasInView;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        @Override public String getTblAliasInView() {return this.tblAliasInView;}
        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }
    }

    public enum ProcedureFrontendTestingWitness implements EnumIntTableFields {
        PROCEDURE_NAME(LPDatabase.FIELDS_NAMES_PROCEDURE_NAME, LPDatabase.stringNotNull(), null, null, null, null),
        PROCEDURE_VERSION(LPDatabase.FIELDS_NAMES_PROCEDURE_VERSION, LPDatabase.integerNotNull(), null, null, null, null),
        PROC_INSTANCE_NAME("proc_instance_name", LPDatabase.stringNotNull(), null, null, null, null),
        TEST_NAME("test_name", LPDatabase.stringNotNull(), null, null, null, null),
        DESCRIPTION(FIELDS_NAMES_DESCRIPTION, LPDatabase.string(), null, null, null, null),
        REPORT_URL("report_url", LPDatabase.string(), null, null, null, null),
        LAST_EXEC_EVAL("last_execution_eval", LPDatabase.string(), null, null, null, null),
        LAST_EXEC("last_execution", LPDatabase.dateTime(), null, null, null, null),
        ACCEPTED("accepted", LPDatabase.booleanFld(false), null, null, null, null),
        ORDER_NUMBER("order_number", LPDatabase.real(), null, null, null, null),
        ACTIVE("active", LPDatabase.booleanFld(true), null, null, null, null),
        AREA("area", LPDatabase.string(), null, null, null, null),;        
        ;
        private ProcedureFrontendTestingWitness(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules) {
            this.fieldName = dbObjName;
            this.fieldType = dbObjType;
            this.fieldMask = fieldMask;
            this.reference = refer;
            this.fieldComment = comment;
            this.fldBusinessRules = fldBusRules;
        }
        private final String fieldName;

        @Override
        public String getName() {
            return this.fieldName;
        }
        private final String fieldType;

        @Override
        public String getFieldType() {
            return this.fieldType;
        }
        private final String fieldMask;

        @Override
        public String getFieldMask() {
            return this.fieldMask;
        }
        private final ReferenceFld reference;

        @Override
        public ReferenceFld getReferenceTable() {
            return this.reference;
        }
        private final String fieldComment;

        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        private final FldBusinessRules[] fldBusinessRules;

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }
    }

    public enum ProcedureTablesAndViewsDeploymentLog implements EnumIntViewFields {
        PROCEDURE_NAME("", ProcedureModuleTables.PROCEDURE_NAME.getName(), "object_type", ProcedureModuleTables.PROCEDURE_NAME, null, null, null),
        PROCEDURE_VERSION("", ProcedureModuleTables.PROCEDURE_VERSION.getName(), "sar.test_id", ProcedureModuleTables.PROCEDURE_VERSION, null, null, null),
        PROC_INSTANCE_NAME("", ProcedureModuleTables.PROC_INSTANCE_NAME.getName(), "sar.sample_id", ProcedureModuleTables.PROC_INSTANCE_NAME, null, null, null),
        SCHEMA_NAME("", ProcedureModuleTables.SCHEMA_NAME.getName(), ProcedureModuleTables.SCHEMA_NAME.getName(), ProcedureModuleTables.SCHEMA_NAME, null, null, null),
        TABLE_NAME("", ProcedureModuleTables.TABLE_NAME.getName(), ProcedureModuleTables.TABLE_NAME.getName(), ProcedureModuleTables.TABLE_NAME, null, null, null),
        IS_VIEW("", ProcedureModuleTables.IS_VIEW.getName(), ProcedureModuleTables.IS_VIEW.getName(), ProcedureModuleTables.IS_VIEW, null, null, null),
        ACTIVE("", ProcedureModuleTables.SCHEMA_NAME.getName(), ProcedureModuleTables.SCHEMA_NAME.getName(), ProcedureModuleTables.ACTIVE, null, null, null),
        IS_MANDATORY("", "is_mandatory", ProcedureModuleTables.IS_MANDATORY.getName(), ProcedureModuleTables.IS_MANDATORY, null, null, null),
        FIELD_NAME("", ProcedureModuleTables.FIELD_NAME.getName(), ProcedureModuleTables.FIELD_NAME.getName(), ProcedureModuleTables.FIELD_NAME, null, null, null),
        FIELDS_TO_EXCLUDE("", ProcedureModuleTables.FIELDS_TO_EXCLUDE.getName(), ProcedureModuleTables.FIELDS_TO_EXCLUDE.getName(), ProcedureModuleTables.FIELDS_TO_EXCLUDE, null, null, null),
        DEFINITION_EN("", ProcedureModuleTables.DEFINITION_EN.getName(), ProcedureModuleTables.DEFINITION_EN.getName(), ProcedureModuleTables.DEFINITION_EN, null, null, null),
        DEFINITION_ES("", ProcedureModuleTables.DEFINITION_ES.getName(), ProcedureModuleTables.DEFINITION_ES.getName(), ProcedureModuleTables.DEFINITION_ES, null, null, null),
        OBJECT_TYPE("", "object_type", "object_type", ProcedureModuleTables.PROCEDURE_NAME, null, null, null),
        DEPLOYMENT_STATUS("", "deployment_status", "deployment_status", ProcedureModuleTables.PROCEDURE_NAME, null, null, null),
        ;
        private ProcedureTablesAndViewsDeploymentLog(String tblAliasInView, String name, String vwFldAliasName, EnumIntTableFields fldObj, String fldMask, String comment, FldBusinessRules[] busRules) {
            this.fldName = name;
            this.fldAliasInView = vwFldAliasName;
            this.fldMask = fldMask;
            this.fldComment = comment;
            this.fldBusinessRules = busRules;
            this.fldObj = fldObj;
            this.tblAliasInView = tblAliasInView;
        }
        private final String fldName;
        private final String fldAliasInView;
        private final EnumIntTableFields fldObj;
        private final String fldMask;
        private final String fldComment;
        private final FldBusinessRules[] fldBusinessRules;
        private final String tblAliasInView;

        @Override
        public String getName() {
            return fldName;
        }

        @Override
        public String getFldViewAliasName() {
            return this.fldAliasInView;
        }

        @Override
        public String getFieldMask() {
            return this.fldMask;
        }

        @Override
        public String getFieldComment() {
            return this.fldComment;
        }

        @Override
        public FldBusinessRules[] getFldBusinessRules() {
            return this.fldBusinessRules;
        }

        @Override
        public EnumIntTableFields getTableField() {
            return this.fldObj;
        }

        @Override
        public String getTblAliasInView() {
            return this.tblAliasInView;
        }
    }
    
}
