/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import databases.Rdbms;
import databases.TblsApp;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.directory.Attributes;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import java.security.*;
import java.util.Base64;

/**
 *
 * @author User
 */
public class LPLdap {

    public static DirContext connectToOpenLDAP(String LdapName) {

        EnumIntTableFields[] allFieldNames = TblsApp.TablesApp.LDAP_SETTINGS.getTableFields();
        Object[][] ldapInfo = Rdbms.getRecordFieldsByFilter("", GlobalVariables.Schemas.APP.getName(), TblsApp.TablesApp.LDAP_SETTINGS.getTableName(),
                new String[]{TblsApp.LdapSetting.NAME.getName()}, new Object[]{LdapName}, getAllFieldNames(allFieldNames));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(ldapInfo[0][0].toString())) {
            return null;
        }

        Integer fldPosicInArray = EnumIntTableFields.getFldPosicInArray(allFieldNames, TblsApp.LdapSetting.URL.getName());
        Integer fld2PosicInArray = EnumIntTableFields.getFldPosicInArray(allFieldNames, TblsApp.LdapSetting.PORT.getName());
        // Set up environment properties for the initial context
        String ldapUrl = LPNulls.replaceNull(ldapInfo[0][fldPosicInArray]).toString() + ":" + LPNulls.replaceNull(ldapInfo[0][fld2PosicInArray]).toString();
        fldPosicInArray = EnumIntTableFields.getFldPosicInArray(allFieldNames, TblsApp.LdapSetting.LDAP_SEC_PRINCIPAL.getName());
        String ldapDn = LPNulls.replaceNull(ldapInfo[0][fldPosicInArray]).toString();
        fldPosicInArray = EnumIntTableFields.getFldPosicInArray(allFieldNames, TblsApp.LdapSetting.LDAP_ADMIN_PW.getName());
        String ldapPassword = LPNulls.replaceNull(ldapInfo[0][fldPosicInArray]).toString();

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        fldPosicInArray = EnumIntTableFields.getFldPosicInArray(allFieldNames, TblsApp.LdapSetting.LDAP_SEC_AUTHENTICATION.getName());
        env.put(Context.SECURITY_AUTHENTICATION, LPNulls.replaceNull(ldapInfo[0][fldPosicInArray]).toString());
        env.put(Context.SECURITY_PRINCIPAL, ldapDn);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        try {
            return new InitialDirContext(env);
        } catch (NamingException ex) {
            Logger.getLogger(LPLdap.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static String createLdapNewUser(String LdapName, String uName, String baseDn) {
        DirContext ctx = connectToOpenLDAP(LdapName);
        if (ctx == null) {
            return "Connection to LDAP not possible";
        }
        String userDn = "cn=" + uName + "," + baseDn;
        Attributes attrs = new BasicAttributes();
        attrs.put("objectClass", "inetOrgPerson");
        attrs.put("cn", uName);
        attrs.put("sn", uName);
        attrs.put("userPassword", encodePassword("demo"));

        try {
            DirContext createSubcontext = ctx.createSubcontext(userDn, attrs);
            ctx.close();
            return "user created";
        } catch (NamingException e) {
            try {
                ctx.close();
            } catch (NamingException ex) {
                Logger.getLogger(LPLdap.class.getName()).log(Level.SEVERE, null, ex);
            }
            return e.getMessage();
        }
    }

private static String encodePassword(String password) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(digest);
        return "{SHA}" + encoded;
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        return null;
    }
}    
    public static boolean LdapValidateUser(String LdapName, String username, String pwdToCheck, String baseDn) {
        DirContext ctx = connectToOpenLDAP(LdapName);
        if (ctx == null) {
            return false; 
        }
        Hashtable<String, String> env = new Hashtable<>();
        try {

            // Specify the search filter and attribute
            String searchFilter = "(cn=" + username + ")";
            String[] searchAttributes = {"userPassword"};

            // Search for the user entry
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setReturningAttributes(searchAttributes);
            NamingEnumeration<SearchResult> results = ctx.search(baseDn, searchFilter, controls);

            // Check the user password
            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                byte[] userPasswordBytes = (byte[]) attrs.get("userPassword").get();
                return validatePassword(userPasswordBytes, pwdToCheck);
            }
            ctx.close();
            return false;
        } catch (NamingException e) {
            return false;
        }
    }

    private static boolean validatePassword(byte[] userPasswordBytes, String password) {
        String userPasswordDecoded = new String(userPasswordBytes);
        String passwordScheme = userPasswordDecoded.substring(0, userPasswordDecoded.indexOf("}") + 1);
        String encodedPassword = userPasswordDecoded.substring(passwordScheme.length());

        switch (passwordScheme) {
            case "{MD5}": {
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    return false;
                }
                md.update(password.getBytes());
                byte[] hashedPassword = md.digest();
                String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);
                return encodedHash.equals(encodedPassword);
            }
            case "{SHA}": {
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    return false;
                }
                md.update(password.getBytes());
                byte[] hashedPassword = md.digest();
                String encodedHash = Base64.getEncoder().encodeToString(hashedPassword);
                return encodedHash.equals(encodedPassword);
            }
            default:
                // Unsupported password scheme
                return false;
        }
    }
}
