/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.enums;

/**
 *
 * @author User
 */
public class ActionsEndpointPair {

    private final String endpointIdentifier;
    private final String actionsClassName;

    public ActionsEndpointPair(String endpointIdentifier, String className) {
        this.endpointIdentifier = endpointIdentifier;
        this.actionsClassName = className;
    }

    public String getEndpoint() {
        return endpointIdentifier;
    }

    public String getAction() {
        return actionsClassName;
    }
 
}
