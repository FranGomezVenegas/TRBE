/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases.features;

import databases.TblsAppConfig;
import static databases.features.DbEncryption.encryptValue;
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
import trazit.enums.EnumIntTableFields;
import trazit.enums.EnumIntTables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class DbEncryptionObject {
    public static Object[] decryptTableFieldArray(EnumIntTables tblObj, EnumIntTableFields[] fieldsToRetrieve, Object[] fieldValue){
//if (1==1) return fieldValue;
        Boolean tableHasEncryptedFlds = tableHasEncryptedFlds(tblObj);
        if (!tableHasEncryptedFlds) return fieldValue;
//if (1==1) return fieldValue;
        String key = DbEncryption.ENCRYPTION_KEY; //"Bar12345Bar12345"; // 128 bit key
        String keyStr="AES/ECB/NoPadding";
//        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        
        for (int iFields=0;iFields<fieldsToRetrieve.length;iFields++){
            //if (fieldsEncrypted.contains(fieldName[iFields])){
            if (isEncryptedTableFld(tblObj, fieldsToRetrieve[iFields])){
                String enc = fieldValue[iFields].toString();
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
                        fieldValue[iFields] = decrypted;
                    }
                    catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
                        fieldValue[iFields] = fieldValue[iFields].toString();
                    }    
                }
            }
        }        
        return fieldValue;        
    }    

    public static Object[][] decryptTableFieldArray(EnumIntTables tblObj, EnumIntTableFields[] fieldsToRetrieve, Object[][] fieldValue){
//if (1==1) return fieldValue;
        Boolean tableHasEncryptedFlds = tableHasEncryptedFlds(tblObj);
        if (!tableHasEncryptedFlds) return fieldValue;
//if (1==1) return fieldValue;
        String key = DbEncryption.ENCRYPTION_KEY; //"Bar12345Bar12345"; // 128 bit key
        String keyStr="AES/ECB/NoPadding";
//        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        
        for (int iFields=0;iFields<fieldsToRetrieve.length;iFields++){
            //if (fieldsEncrypted.contains(fieldName[iFields])){
            if (isEncryptedTableFld(tblObj, fieldsToRetrieve[iFields])){
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
    public static Object[] encryptTableFieldArray(EnumIntTables tblObj, EnumIntTableFields[] fieldsToRetrieve, Object[] fieldValue){
//if (1==1) return fieldValue;
        Boolean tableHasEncryptedFlds = tableHasEncryptedFlds(tblObj);
        if (!tableHasEncryptedFlds) return fieldValue;
//if (1==1) return fieldValue;
        String key = DbEncryption.ENCRYPTION_KEY; // 128 bit key
        //? Should be by procInstanceName? config or data???
//        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        

        for (int iFields=0;iFields<fieldsToRetrieve.length;iFields++){
            //if (fieldsEncrypted.contains(fieldName[iFields])){
            if (isEncryptedTableFld(tblObj, fieldsToRetrieve[iFields])){
                try{
                    encryptValue(fieldValue[iFields].toString());
                    String text = fieldValue[iFields].toString();
                    // Create key and cipher
                    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
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
    public static Boolean tableHasEncryptedFlds(EnumIntTables tblObj){
        return isEncryptedTableFld(tblObj, null);
    }
    public static Boolean isEncryptedTableFld(EnumIntTables tblObj, EnumIntTableFields fldNObj){
        if (tblObj.getRepositoryName()==null || tblObj.getTableName()==null) return false;
        ProcedureRequestSession instanceForQueries = ProcedureRequestSession.getInstanceForQueries(null, null, false);
        Object[] encrFieldsObj=null;
        if (!tblObj.getIsProcedureInstance())
            encrFieldsObj=instanceForQueries.getAppEncryptFields();
        else
            encrFieldsObj=instanceForQueries.getProcedureEncryptFields();
        if (encrFieldsObj==null) return false;
        Object[][] encrFlds=(Object[][])encrFieldsObj[1];
        if (encrFlds==null) return false;
        Integer schColPosic=LPArray.valuePosicInArray((String[])encrFieldsObj[0], TblsAppConfig.TblFldsEncrypt.SCHEMA_NAME.getName());
        if (schColPosic==-1) return false;
        Integer tblColPosic=LPArray.valuePosicInArray((String[])encrFieldsObj[0], TblsAppConfig.TblFldsEncrypt.TABLE_NAME.getName());
        if (tblColPosic==-1) return false;
        Integer fldColPosic=LPArray.valuePosicInArray((String[])encrFieldsObj[0], TblsAppConfig.TblFldsEncrypt.FIELD_NAME.getName());
        if (fldColPosic==-1) return false;
        Object[] schColArr = LPArray.getColumnFromArray2D(encrFlds, schColPosic);
        Object[] tblColArr = LPArray.getColumnFromArray2D(encrFlds, tblColPosic);
        String[] joinTwo1DArraysInOneOf1DString = LPArray.joinTwo1DArraysInOneOf1DString(schColArr, tblColArr, "");
        if (fldNObj==null || fldNObj.getName()==null)
            return LPArray.valueInArray(joinTwo1DArraysInOneOf1DString, tblObj.getRepositoryName()+tblObj.getTableName());
        Object[] fldColArr = LPArray.getColumnFromArray2D(encrFlds, fldColPosic);
        joinTwo1DArraysInOneOf1DString = LPArray.joinTwo1DArraysInOneOf1DString(joinTwo1DArraysInOneOf1DString, fldColArr, "");
        return LPArray.valueInArray(joinTwo1DArraysInOneOf1DString, tblObj.getRepositoryName()+tblObj.getTableName()+fldNObj.getName());
    }
}
