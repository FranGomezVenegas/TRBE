/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases.features;

import databases.TblsAppConfig;
import databases.TblsAppConfig.TblFldsEncrypt;
import functionaljavaa.parameter.Parameter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.TrazitUtiilitiesEnums;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DbEncryption {
    static final String ENCRYPTION_KEY = "Bar12345Bar12345";
    /**
     *
     * @param schemaName
     * @param tableName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object[] encryptTableFieldArray(String schemaName, String tableName, String[] fieldName, Object[] fieldValue){
//if (1==1) return fieldValue;
        Boolean tableHasEncryptedFlds = tableHasEncryptedFlds(null, schemaName, tableName);
        if (!tableHasEncryptedFlds) return fieldValue;
if (1==1) return fieldValue;
        String key = ENCRYPTION_KEY; // 128 bit key
        //? Should be by procInstanceName? config or data???
        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        

        for (int iFields=0;iFields<fieldName.length;iFields++){
            if (fieldsEncrypted.contains(fieldName[iFields])){
                try{
                    encryptValue(fieldValue[iFields].toString());
                    String text = fieldValue[iFields].toString();
                    // Create key and cipher
                    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    // encrypt the text
                    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                    byte[] encrypted = cipher.doFinal(text.getBytes());

                    StringBuilder sb = new StringBuilder(0);
                    for (byte b: encrypted) {
                        sb.append((char)b);
                    }
                    // the encrypted String
                    String enc = sb.toString();
                    fieldValue[iFields] = enc;
                }
                catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
                    Object[] errorDetailVariables = new Object[0];
                    errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, e.getMessage());
                    return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, errorDetailVariables);
                }
            }
        }        
        return fieldValue;        
    }  
/*    public static Object[] encryptTableFieldArray(String schemaName, String tableName, String[] fieldName, Object[] fieldValue){
if (1==1) return fieldValue;
        String key = ENCRYPTION_KEY; // 128 bit key
        //? Should be by procInstanceName? config or data???
        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        

        for (int iFields=0;iFields<fieldName.length;iFields++){
            if (fieldsEncrypted.contains(fieldName[iFields])){
                Object[] encryptValue = encryptValue(fieldValue[iFields].toString());
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(encryptValue[0].toString()))
                    fieldValue[iFields] = encryptValue[1];
                else
                    fieldValue[iFields] = 
            }
        }        
        return fieldValue;        
    }  */
    public static Object[] encryptValue(Object val){
        String key = ENCRYPTION_KEY; // 128 bit key
        try{
            String text = val.toString();
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());

            StringBuilder sb = new StringBuilder(0);
            for (byte b: encrypted) {
                sb.append((char)b);
            }
            // the encrypted String
            String enc = sb.toString();
            return new Object[]{LPPlatform.LAB_TRUE, enc};
        }
        catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
            Object[] errorDetailVariables = new Object[0];
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, e.getMessage());
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, errorDetailVariables);
        }        
    }
    public static Object[] decryptValue(String val){
        String key = ENCRYPTION_KEY; // 128 bit key
        String keyStr="AES";
        try{                    
            // Create key and cipher for decryption
            Key aesKey = new SecretKeySpec(key.getBytes(), keyStr);
            Cipher cipher = Cipher.getInstance(keyStr);
            byte[] bb = new byte[val.length()];
            for (int i=0; i<val.length(); i++) {
                bb[i] = (byte) val.charAt(i);
            }
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(bb));
            return new Object[]{LPPlatform.LAB_TRUE, decrypted};            
        }
        catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
            Object[] errorDetailVariables = new Object[0];
            errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, e.getMessage());
            return new Object[]{LPPlatform.LAB_FALSE, e.getMessage(), val}; 
            //return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, TrazitUtiilitiesEnums.TrazitUtilitiesErrorTrapping.ERRORTRAPPING_EXCEPTION, errorDetailVariables);
        }    
    }
    
    /**
     *
     * @param schemaName
     * @param tableName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object[][] decryptTableFieldArray(String schemaName, String tableName, String[] fieldName, Object[][] fieldValue){
//if (1==1) return fieldValue;
        Boolean tableHasEncryptedFlds = tableHasEncryptedFlds(null, schemaName, tableName);
        if (!tableHasEncryptedFlds) return fieldValue;
if (1==1) return fieldValue;
        String key = ENCRYPTION_KEY; //"Bar12345Bar12345"; // 128 bit key
        String keyStr="AES";
        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        
        for (int iFields=0;iFields<fieldName.length;iFields++){
            if (fieldsEncrypted.contains(fieldName[iFields])){
                    for (Object[] fieldValue1 : fieldValue) {
                        String enc = fieldValue1[iFields].toString();
                        if (enc!=null){
                            try{                    
                                // Create key and cipher for decryption
                                Key aesKey = new SecretKeySpec(key.getBytes(), keyStr);
                                Cipher cipher = Cipher.getInstance(keyStr);
                                byte[] bb = new byte[enc.length()];
                                for (int i=0; i<enc.length(); i++) {
                                    bb[i] = (byte) enc.charAt(i);
                                }
                                // decrypt the text
                                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                                String decrypted = new String(cipher.doFinal(bb));
                                fieldValue1[iFields] = decrypted;
                            }
                            catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
                                fieldValue1[iFields] = fieldValue1[iFields].toString();
                            }    
                    }        
                }
            }
        }        
        return fieldValue;        
    }    

    /**
     *
     * @param schemaName
     * @param tableName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object[] decryptTableFieldArray(String schemaName, String tableName, String[] fieldName, Object[] fieldValue){
//if (1==1) return fieldValue;
        Boolean tableHasEncryptedFlds = tableHasEncryptedFlds(null, schemaName, tableName);
        if (!tableHasEncryptedFlds) return fieldValue;
if (1==1) return fieldValue;
        String key = ENCRYPTION_KEY;
        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        
        for (int iFields=0;iFields<fieldName.length;iFields++){
            if (fieldsEncrypted.contains(fieldName[iFields])){
                try{                                        
                    String enc = fieldValue[iFields].toString();
                    // Create key and cipher
                    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    // for decryption
                    byte[] bb = new byte[enc.length()];
                    for (int i=0; i<enc.length(); i++) {
                        bb[i] = (byte) enc.charAt(i);
                    }
                    // decrypt the text
                    cipher.init(Cipher.DECRYPT_MODE, aesKey);
                    String decrypted = new String(cipher.doFinal(bb));
                    fieldValue[iFields] = decrypted;
                }
                catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
                    fieldValue[iFields] = fieldValue[iFields].toString();
                }
            }
        }        
        return fieldValue;        
    }    
       
    public static Object[] getEncryptFields(String dbName, Boolean isApp, String procInstanceName){
        ProcedureRequestSession instanceForQueries = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        Object[] mainObj=new Object[2];
        mainObj[0]=getAllFieldNames(TblsAppConfig.TablesAppConfig.TBL_FLD_ENCRYPT.getTableFields());
        if (!"demoplatform".equalsIgnoreCase(dbName))
            return mainObj;
        Object[][] fldsArr=null;
        if (isApp)
            fldsArr=new Object[][]{{"config", "person", "person_id", true},
                {"app", "users", "person_name", true},
                {"app", "ip_black_list", "ip_value1", true},
                {"app", "ip_black_list", "ip_value2", true},
            //    {"app", "ip_black_list", "active", true},
            };
        //else
        //    fldsArr=new Object[][]{{}};
        mainObj[1]=fldsArr;
        return mainObj;
    }

    public static Boolean tableHasEncryptedFlds(Boolean isApp, String schemaN, String tblN){
        if (isApp==null){
            if (isEncryptedTableFld(true, schemaN, tblN, null)) return true;
            return isEncryptedTableFld(false, schemaN, tblN, null);
        }
        return isEncryptedTableFld(isApp, schemaN, tblN, null);
    }
    public static Boolean isEncryptedTableFld(Boolean isApp, String schemaN, String tblN, String fldN){
        if (schemaN==null || tblN==null) return false;
        ProcedureRequestSession instanceForQueries = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        Object[] encrFieldsObj=null;
        if (isApp)
            encrFieldsObj=instanceForQueries.getAppEncryptFields();
        else
            encrFieldsObj=instanceForQueries.getProcedureEncryptFields();
        if (encrFieldsObj==null) return false;
        Object[][] encrFlds=(Object[][])encrFieldsObj[1];
        if (encrFlds==null) return false;
        Integer schColPosic=LPArray.valuePosicInArray((String[])encrFieldsObj[0], TblFldsEncrypt.SCHEMA_NAME.getName());
        if (schColPosic==-1) return false;
        Integer tblColPosic=LPArray.valuePosicInArray((String[])encrFieldsObj[0], TblFldsEncrypt.TABLE_NAME.getName());
        if (tblColPosic==-1) return false;
        Integer fldColPosic=LPArray.valuePosicInArray((String[])encrFieldsObj[0], TblFldsEncrypt.FIELD_NAME.getName());
        if (fldColPosic==-1) return false;
        Object[] schColArr = LPArray.getColumnFromArray2D(encrFlds, schColPosic);
        Object[] tblColArr = LPArray.getColumnFromArray2D(encrFlds, tblColPosic);
        String[] joinTwo1DArraysInOneOf1DString = LPArray.joinTwo1DArraysInOneOf1DString(schColArr, tblColArr, "");
        if (fldN==null)
            return LPArray.valueInArray(joinTwo1DArraysInOneOf1DString, schemaN+tblN);
        Object[] fldColArr = LPArray.getColumnFromArray2D(encrFlds, fldColPosic);
        joinTwo1DArraysInOneOf1DString = LPArray.joinTwo1DArraysInOneOf1DString(joinTwo1DArraysInOneOf1DString, fldColArr, "");
        return LPArray.valueInArray(joinTwo1DArraysInOneOf1DString, schemaN+tblN+fldN);
    }
}
