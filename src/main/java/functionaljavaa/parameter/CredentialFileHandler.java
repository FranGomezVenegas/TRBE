/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package functionaljavaa.parameter;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lbplanet.utilities.LPNulls;

public class CredentialFileHandler {

    public static String encrypt(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void main(String[] args) throws Exception {
        // Generate a secret key (you should store this securely)
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 128-bit key size
        SecretKey secretKey = keyGenerator.generateKey();
        
        // User credentials
        String adminUsername = "admin";
        String fakeProcessUsername = "demo";
        String password = "trazit4ever";
        String adminpersonId="adminz";
        String adminEsign="sign"+adminUsername;
        String fakepersonId="demoz";
        String fakeEsign="sign"+fakeProcessUsername;
        String defaultMail="techsupport@trazit.net";
        String fakeProcName = "check-platform";                
        
        // Encrypt the credentials
        adminUsername = encrypt(adminUsername, secretKey);
        fakeProcessUsername = encrypt(fakeProcessUsername, secretKey);
        password = encrypt(password, secretKey);
        adminpersonId = encrypt(adminpersonId, secretKey);
        adminEsign = encrypt(adminEsign, secretKey);
        fakepersonId = encrypt(fakepersonId, secretKey);
        fakeEsign = encrypt(fakeEsign, secretKey);
        defaultMail = encrypt(defaultMail, secretKey);
        fakeProcName = encrypt(fakeProcName, secretKey);

        // Specify the path for the credentials file
        String propFileName="D:/LP/LabPLANETAPI_2021/src/main/resources/parameter/config/adminAndFakeCred";
        Parameter parm = new Parameter();
        StringBuilder sB=new StringBuilder(0);
        sB.append("adminUsername:").append(LPNulls.replaceNull(adminUsername)).append("\n");
        sB.append("adminEsign:").append(LPNulls.replaceNull(adminEsign)).append("\n");
        sB.append("adminpersonId:").append(LPNulls.replaceNull(adminpersonId)).append("\n");
        sB.append("fakeProcessUsername:").append(LPNulls.replaceNull(fakeProcessUsername)).append("\n");
        sB.append("fakeEsign:").append(LPNulls.replaceNull(fakeEsign)).append("\n");
        sB.append("fakepersonId:").append(LPNulls.replaceNull(fakepersonId)).append("\n");
        sB.append("defaultMail:").append(LPNulls.replaceNull(defaultMail)).append("\n");
        sB.append("password:").append(LPNulls.replaceNull(password)).append("\n");
        
        byte[] encodedKey = secretKey.getEncoded();
        String byteArrayToHex = byteArrayToHex(encodedKey);
        sB.append("secretKey:").append(LPNulls.replaceNull(byteArrayToHex)).append("\n");
        
        System.out.println(sB.toString());
    }
        private static String byteArrayToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
    
}
