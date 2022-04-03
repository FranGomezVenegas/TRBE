/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databases;

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
import trazit.session.ApiMessageReturn;

/**
 *
 * @author User
 */
public class DbEncryption {
    private static final String ENCRYPTION_KEY = "Bar12345Bar12345";
    /**
     *
     * @param schemaName
     * @param tableName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object[] encryptTableFieldArray(String schemaName, String tableName, String[] fieldName, Object[] fieldValue){
if (1==1) return fieldValue;
        String key = ENCRYPTION_KEY; // 128 bit key
        //? Should be by procInstanceName? config or data???
        String fieldsEncrypted = Parameter.getBusinessRuleProcedureFile(schemaName.replace("\"", ""), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getAreaName(), LPArray.LpArrayBusinessRules.ENCRYPTED_PREFIX.getTagName());        

        for (int iFields=0;iFields<fieldName.length;iFields++){
            if (fieldsEncrypted.contains(fieldName[iFields])){
                try{
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
    
    /**
     *
     * @param schemaName
     * @param tableName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object[][] decryptTableFieldArray(String schemaName, String tableName, String[] fieldName, Object[][] fieldValue){
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
        
}
