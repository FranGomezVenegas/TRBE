/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package module.clinicalstudies.definition;

import lbplanet.utilities.LPDatabase;
import static lbplanet.utilities.LPDatabase.dateTime;
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.enums.FldBusinessRules;
import trazit.enums.ReferenceFld;
import trazit.globalvariables.GlobalVariables;
/**
 *
 * @author User
 */
public class TblsGenomaData {
    
    private static final java.lang.String SCHEMA_NAME = GlobalVariables.Schemas.DATA.getName();
    private static final Boolean IS_PRODEDURE_INSTANCE = true;
    public enum TablesGenomaData implements EnumIntTables{        
        PROJECT(null, "project", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, Project.values(), null, new String[]{Project.NAME.getName()}, null, "Project table"),
        PROJECT_USERS(null, "project_users", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, ProjectUsers.values(), ProjectUsers.ID.getName(), new String[]{ProjectUsers.ID.getName()}, null, "ProjectUsers table"),
        STUDY(null, "study", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, TblsGenomaData.Study.values(), null, new String[]{TblsGenomaData.Study.NAME.getName()}, null, "Study table"),
        STUDY_USERS(null, "study_users", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyUsers.values(), StudyUsers.ID.getName(), new String[]{StudyUsers.ID.getName()}, null, "StudyUsers table"),
        STUDY_INDIVIDUAL(null, "study_individual", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyIndividual.values(), StudyIndividual.INDIVIDUAL_ID.getName(), new String[]{StudyIndividual.INDIVIDUAL_ID.getName()}, null, "StudyIndividual table"),
        STUDY_FAMILY_INDIVIDUAL(null, "study_family_individual", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyFamilyIndividual.values(), StudyFamilyIndividual.ID.getName(), new String[]{StudyFamilyIndividual.ID.getName()}, null, "StudyFamilyIndividual table"),
        STUDY_INDIVIDUAL_SAMPLE(null, "study_individual_sample", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyIndividualSample.values(), StudyIndividualSample.SAMPLE_ID.getName(), new String[]{StudyIndividualSample.SAMPLE_ID.getName()}, null, "StudyIndividualSample table"),
        STUDY_INDIVIDUAL_CONSENT(null, "study_individual_consent", SCHEMA_NAME, true, StudyIndividualConsent.values(), StudyIndividualConsent.ID.getName(),
            new String[]{StudyIndividualConsent.ID.getName()}, null, "StudyIndividualConsent"),
        STUDY_SAMPLES_SET(null, "study_samples_set", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudySamplesSet.values(), null, new String[]{StudySamplesSet.NAME.getName()}, null, "StudySamplesSet table"),
        STUDY_FAMILY(null, "study_family", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyFamily.values(), null, new String[]{StudyFamily.NAME.getName()}, null, "StudyFamily table"),
        STUDY_COHORT(null, "study_cohort", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyCohort.values(), null, new String[]{StudyCohort.NAME.getName()}, null, "StudyCohort table"),
        STUDY_COHORT_INDIVIDUAL(null, "study_cohort_individual", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyCohortIndividual.values(), StudyCohortIndividual.ID.getName(), new String[]{StudyCohortIndividual.ID.getName()}, null, "StudyCohortIndividual table"),
        STUDY_VARIABLE_VALUES(null, "study_variable_values", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyVariableValues.values(), StudyVariableValues.ID.getName(), new String[]{StudyVariableValues.ID.getName()}, null, "StudyVariableValues table"),
        STUDY_OBJECTS_FILES(null, "study_objects_files", SCHEMA_NAME, IS_PRODEDURE_INSTANCE, StudyObjectsFiles.values(), StudyObjectsFiles.FILE_ID.getName(), new String[]{StudyObjectsFiles.FILE_ID.getName()}, null, "study_objects_files table"),
        ;
        private TablesGenomaData(FldBusinessRules[] fldBusRules, String dbTblName, String repositoryName, Boolean isProcedure, EnumIntTableFields[] tblFlds, 
                String seqName, String[] primaryK, Object[] foreignK, String comment){
            this.getTblBusinessRules=fldBusRules;
            this.tableName=dbTblName;
            this.tableFields=tblFlds;
            this.repositoryName=repositoryName;
            this.isProcedure=isProcedure;
            this.sequence=seqName;
            this.primarykey=primaryK;
            this.foreignkey=foreignK;
            this.tableComment=comment;
        }
        @Override        public String getTableName() {return this.tableName;}
        @Override        public String getTableComment() {return this.tableComment;}
        @Override        public EnumIntTableFields[] getTableFields() {return this.tableFields;}
        @Override        public String getRepositoryName() {return this.repositoryName;}
        @Override        public String getSeqName() {return this.sequence;}
        @Override        public String[] getPrimaryKey() {return this.primarykey;}
        @Override        public Object[] getForeignKey() {return this.foreignkey;}
        @Override        public Boolean getIsProcedureInstance() {return this.isProcedure;}
        @Override        public FldBusinessRules[] getTblBusinessRules() {return this.getTblBusinessRules;}
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
    public enum Project implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        STARTED_ON("started_on", LPDatabase.dateTime(), null, null, null, null),
        ENDED_ON("ended_on", LPDatabase.dateTime(), null, null, null, null),
        ;
        private Project(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    
    public enum ProjectUsers implements EnumIntTableFields{
        ID("row_id",  LPDatabase.integerNotNull(), null, null, null, null),
        PROJECT("project",  LPDatabase.stringNotNull(100), null, null, null, null),
        PERSON("person",  LPDatabase.stringNotNull(100), null, null, null, null),
        ROLES("roles", LPDatabase.stringNotNull(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        ;
        private ProjectUsers(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum Study implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(100), null, null, null, null),
        PROJECT("project",  LPDatabase.stringNotNull(100), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        STARTED_ON("started_on", LPDatabase.dateTime(), null, null, null, null),
        ENDED_ON("ended_on", LPDatabase.dateTime(), null, null, null, null),
        ;
        private Study(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum StudyUsers implements EnumIntTableFields{
        ID("row_id",  LPDatabase.integerNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        PERSON("person",  LPDatabase.stringNotNull(100), null, null, null, null),
        ROLES("roles", LPDatabase.stringNotNull(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        ;
        private StudyUsers(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    
    public enum StudyIndividual implements EnumIntTableFields{
        INDIVIDUAL_ID("individual_id",  LPDatabase.integerNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        INDIV_NAME("individual_name", LPDatabase.string(), null, null, null, null),
        ;
        private StudyIndividual(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }

    public enum StudyFamilyIndividual implements EnumIntTableFields{
        ID("id",  LPDatabase.integerNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        FAMILY_NAME("family_name",  LPDatabase.stringNotNull(100), null, null, null, null),
        INDIVIDUAL_ID("individual_id",  LPDatabase.integerNotNull(), null, null, null, null),
        LINKED_ON("linked_on", LPDatabase.dateTime(), null, null, null, null),
        ;
        private StudyFamilyIndividual(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum StudyIndividualSample implements EnumIntTableFields{
        SAMPLE_ID("sample_id",  LPDatabase.integerNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        INDIVIDUAL_ID("individual_id",  LPDatabase.integerNotNull(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        ;
        private StudyIndividualSample(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum StudySamplesSet implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        COMPLETED("completed", LPDatabase.booleanFld(false), null, null, null, null),
        UNSTRUCT_CONTENT("samples_content", LPDatabase.string(), null, null, null, null),
        ;
        private StudySamplesSet(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum StudyFamily implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        COMPLETED("completed", LPDatabase.booleanFld(false), null, null, null, null),
        UNSTRUCT_CONTENT("unstruct_content", LPDatabase.string(), null, null, null, null),
        ;
        private StudyFamily(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum StudyCohort implements EnumIntTableFields{
        NAME("name",  LPDatabase.stringNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        TYPE("type", LPDatabase.string(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        DEACTIVATED_ON( LPDatabase.FIELDS_NAMES_DEACTIVATED_ON, LPDatabase.dateTime(), null, null, null, null),
        DEACTIVATED_BY( LPDatabase.FIELDS_NAMES_DEACTIVATED_BY, LPDatabase.string(), null, null, null, null),
        COMPLETED("completed", LPDatabase.booleanFld(false), null, null, null, null),
        UNSTRUCT_CONTENT("unstruct_content", LPDatabase.string(), null, null, null, null),
        ;
        private StudyCohort(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum StudyCohortIndividual implements EnumIntTableFields{
        ID("id",  LPDatabase.integerNotNull(), null, null, null, null),
        STUDY("study",  LPDatabase.stringNotNull(100), null, null, null, null),
        COHORT_NAME("cohort_name",  LPDatabase.stringNotNull(100), null, null, null, null),
        INDIVIDUAL_ID("individual_id",  LPDatabase.integerNotNull(), null, null, null, null),
        LINKED_ON("linked_on", LPDatabase.dateTime(), null, null, null, null),
        ;
        private StudyCohortIndividual(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    
    public enum StudyVariableValues implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        OWNER_TABLE("owner_table", LPDatabase.stringNotNull(), null, null, null, null),
        OWNER_ID("owner_id", LPDatabase.stringNotNull(), null, null, null, null),
        STUDY("study", LPDatabase.stringNotNull(), null, null, null, null),
        INDIVIDUAL("individual", LPDatabase.integer(), null, null, null, null),
        FAMILY("family", LPDatabase.string(), null, null, null, null),
        SAMPLE("sample", LPDatabase.integer(), null, null, null, null),
        DESCRIPTION("description", LPDatabase.string(), null, null, null, null),
        VARIABLE_SET("variable_set", LPDatabase.stringNotNull(), null, null, null, null),
        NAME("name", LPDatabase.stringNotNull(), null, null, null, null),
        VALUE("value", LPDatabase.string(), null, null, null, null),
        ACTIVE( LPDatabase.FIELDS_NAMES_ACTIVE, LPDatabase.booleanFld(), null, null, null, null),
        PARAM_TYPE("param_type", LPDatabase.string(), null, null, null, null),
        REQUIRED("required", LPDatabase.string(), null, null, null, null),
        ALLOWED_VALUES("allowed_values", LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, dateTime(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        ;
        private StudyVariableValues(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    public enum StudyObjectsFiles implements EnumIntTableFields{
        FILE_ID("file_id", LPDatabase.integerNotNull(), null, null, null, null),
        STUDY("study", LPDatabase.stringNotNull(), null, null, null, null),
        OWNER_TABLE("owner_table", LPDatabase.stringNotNull(), null, null, null, null),
        OWNER_ID("owner_id", LPDatabase.stringNotNull(), null, null, null, null),
        DOC_NAME("doc_name", LPDatabase.stringNotNull(), null, null, null, null),
        ADDED_ON("added_on", dateTime(), null, null, null, null),
        ADDED_BY("added_by", LPDatabase.string(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.stringNotNull(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.stringNotNull(), null, null, null, null),
        AUTHOR("author", LPDatabase.stringNotNull(), null, null, null, null),
        ;
        private StudyObjectsFiles(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName;
        private final String fieldType;
        private final String fieldMask;
        private final ReferenceFld reference;
        private final String fieldComment;
        private final FldBusinessRules[] fldBusinessRules;

        @Override        public String getName(){return this.fieldName;}
        @Override        public String getFieldType() {return this.fieldType;}
        @Override        public String getFieldMask() {return this.fieldMask;}
        @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        @Override        public String getFieldComment(){return this.fieldComment;}
        @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }
    
    public enum StudyIndividualConsent implements EnumIntTableFields{
        ID("id", LPDatabase.integerNotNull(), null, null, null, null),
        STUDY("study", LPDatabase.stringNotNull(), null, null, null, null),
        INDIVIDUAL_ID("individual_id",  LPDatabase.integerNotNull(), null, null, null, null),
        CREATED_BY( LPDatabase.FIELDS_NAMES_CREATED_BY, LPDatabase.string(), null, null, null, null),
        CREATED_ON( LPDatabase.FIELDS_NAMES_CREATED_ON, LPDatabase.dateTime(), null, null, null, null),
        FILE_LINK("file_link", LPDatabase.string(), null, null, null, null),
        DB_FILE("db_file", LPDatabase.embeddedFile(), null, null, null, null),
        BRIEF_SUMMARY("brief_summary", LPDatabase.string(), null, null, null, null),
        REMOVED("removed", LPDatabase.booleanFld(false), null, null, null, null),
        ;
        private StudyIndividualConsent(String dbObjName, String dbObjType, String fieldMask, ReferenceFld refer, String comment,
                FldBusinessRules[] fldBusRules){
            this.fieldName=dbObjName;
            this.fieldType=dbObjType;
            this.fieldMask=fieldMask;
            this.reference=refer;
            this.fieldComment=comment;
            this.fldBusinessRules=fldBusRules;
        }
        private final String fieldName; @Override        public String getName(){return this.fieldName;}
        private final String fieldType; @Override        public String getFieldType() {return this.fieldType;}
        private final String fieldMask; @Override        public String getFieldMask() {return this.fieldMask;}
        private final ReferenceFld reference; @Override        public ReferenceFld getReferenceTable() {return this.reference;}
        private final String fieldComment;    @Override        public String getFieldComment(){return this.fieldComment;}
        private final FldBusinessRules[] fldBusinessRules;     @Override        public FldBusinessRules[] getFldBusinessRules(){return this.fldBusinessRules;}
    }                
    
}
