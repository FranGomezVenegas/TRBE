/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import functionaljavaa.parameter.Parameter;

/**
 *
 * @author User
 */
public class LPAPIEndPointdocumentation {
    String configFileName;
    String configFileTag;
    String documentName;
    String documentChapterName;
    Integer documentChapterId;   
    public LPAPIEndPointdocumentation(String configFileName, String configFileTag, String docName, Integer chapterId, String chapterName){
        this.configFileName=configFileName;
        this.configFileTag=configFileTag;
        this.documentName=docName;
        this.documentChapterId=chapterId;
        this.documentChapterName=chapterName;
    }
    public String getBriefDescription(String language){
        return Parameter.getMessageCodeValue(Parameter.PropertyFilesType.ENDPOINTDOCUMENTATION.getAppConfigParamName(), configFileName, null, configFileTag, language);
    }
    public String getDocumentName(){return this.documentName;}    
    public Integer getDocChapterId(){return this.documentChapterId;}
    public String getDocChapterName(){return this.documentChapterName;}
        
}
