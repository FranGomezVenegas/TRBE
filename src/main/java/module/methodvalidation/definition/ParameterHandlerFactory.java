/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module.methodvalidation.definition;

public class ParameterHandlerFactory {
    public static MethodParamLinealityHandler getHandler(String paramName) {
        switch (paramName.toLowerCase()) {
            case "lineality":
                return new MethodParamLinealityHandler();
            case "repeatibility":
                //return new RepeatibilityHandler();
            default:
                return null;
        }
    }
}
