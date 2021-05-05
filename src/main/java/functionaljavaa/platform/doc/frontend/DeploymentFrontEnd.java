/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.platform.doc.frontend;

import databases.Rdbms;
import databases.TblsTrazitDocModules;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class DeploymentFrontEnd {
    
    public static JSONObject createFiles(File mainPath, String newFileCamelLower, String newFileCamel, String newFileProcName, String newAliasUnderscore, String moduleName, Integer moduleVersion){
        JSONObject jObj=new JSONObject();
        String[] fieldsToGet=TblsTrazitDocModules.ModuleFrontend.getAllFieldNames();
        Object[][] moduleFiles = Rdbms.getRecordFieldsByFilter("modules", TblsTrazitDocModules.ModuleFrontend.TBL.getName(), 
                new String[]{TblsTrazitDocModules.ModuleFrontend.FLD_MODULE_NAME.getName(), TblsTrazitDocModules.ModuleFrontend.FLD_MODULE_VERSION.getName()},
                new Object[]{moduleName, moduleVersion}, 
                fieldsToGet);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(moduleFiles[0][0].toString())){
            jObj.put("summary", "noFiles for "+moduleName+" "+moduleVersion.toString());
            return jObj;             
        }
        for (Object[] curFile: moduleFiles){            
            try {
                File otherDirs = new File(mainPath.getPath()+File.separator+curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_DESTINATION_PATH.getName())]);
                //otherDirs.createNewFile(curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_FILE_NAME.getName())]);
                otherDirs.mkdirs();
                String curFilePath=mainPath.getAbsolutePath()+File.separator+curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_DESTINATION_PATH.getName())]+File.separator + curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_FILE_NAME.getName())];
                String sourceCode=curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_SOURCE_CODE.getName())].toString();
                String currentFileCamelLower=curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_CAMEL_LOWER.getName())].toString();
                String currentFileCamel=curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_CAMEL.getName())].toString();
                String currentFileProcName=curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_PROC_NAME.getName())].toString();
                String currentAliasUnderscore=curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_ALIASES_UNDERSCORE.getName())].toString();
                if (currentFileProcName!=null && currentFileProcName.length()>0)
                    curFilePath=curFilePath.replaceAll(currentFileProcName, newFileProcName);
                   //curFilePath.replace(currentFileProcName, newFileProcName);
                File newFile = new File(curFilePath);
                boolean createNewFile = newFile.createNewFile();
                FileWriter myWriter = new FileWriter(curFilePath, StandardCharsets.UTF_8);
                if (currentFileCamelLower!=null && currentFileCamelLower.length()>0)
                   sourceCode=sourceCode.replaceAll(currentFileCamelLower, newFileCamelLower);
                if (currentFileCamel!=null && currentFileCamel.length()>0)
                   sourceCode=sourceCode.replaceAll(currentFileCamel, newFileCamel);
                if (currentFileProcName!=null && currentFileProcName.length()>0)
                   sourceCode=sourceCode.replaceAll(currentFileProcName, newFileProcName);                
                if (currentAliasUnderscore!=null && currentAliasUnderscore.length()>0)
                   sourceCode=sourceCode.replaceAll(currentAliasUnderscore, newAliasUnderscore);                
                
                myWriter.write(sourceCode);
                myWriter.close();                
                   
                jObj.put(curFile[LPArray.valuePosicInArray(fieldsToGet, TblsTrazitDocModules.ModuleFrontend.FLD_FILE_NAME.getName())]+" created?", createNewFile);
            } catch (IOException ex) {
                Logger.getLogger(DeploymentFrontEnd.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        jObj.put("summary", "nothing done yet");
        return jObj;
    }
    
}
