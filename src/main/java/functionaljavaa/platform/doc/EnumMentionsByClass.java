package functionaljavaa.platform.doc;

import java.lang.reflect.Method;
import lbplanet.utilities.LPArray;
import trazit.enums.AnnotationDefinitions.UsesEnum;
import trazit.enums.AnnotationDefinitions.UsesEnums;

public class EnumMentionsByClass {

    public static String[] main(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className); //"DataInspectionLot"

            Method method;
            method = clazz.getMethod(methodName, String.class, Integer.class); //"applyBulkPlan"

            if (method.isAnnotationPresent(UsesEnums.class)) {
                UsesEnums usesEnums = method.getAnnotation(UsesEnums.class);

                String[] allEnums=new String[]{};
                for (UsesEnum enumAnnotation : usesEnums.value()) {
                    allEnums=LPArray.addValueToArray1D(allEnums, enumAnnotation.value());
                }
                return allEnums;
            }
            return new String[]{};
        } catch (ClassNotFoundException | NoSuchMethodException  e) {
            e.printStackTrace();
            return new String[]{};
        }
    }
}



 
