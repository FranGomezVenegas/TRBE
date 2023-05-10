/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.thirdparties.sap;
/*
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.astm.Animl;
import org.astm.AnimlDocument;
import org.astm.AnimlException;
import org.astm.AnimlReader;
import org.astm.AnimlWriter;
import org.astm.CD;
import org.astm.CVT;
import org.astm.P;
import org.astm.Profile;
import org.astm.QTY;
import org.astm.R;
import org.astm.Scalar;
import org.astm.Unit;
*/
public class AnimlExample {
/*
    public static void main(String[] args) {
        try {
            // create a new ANIML document
            AnimlDocument animl = new AnimlDocument();

            // create a new profile element
            Profile profile = new Profile();
            profile.setManufacturer("My Company");
            profile.setModelNumber("1234");
            profile.setSerialNumber("5678");
            profile.setSoftwareVersion("1.0");
            animl.setProfile(profile);

            // create a new result element
            R result = new R();
            result.setName("Test Result");
            result.setTimestamp("2023-05-03T14:30:00Z");

            // create a new parameter element
            P parameter = new P();
            parameter.setName("Parameter 1");
            parameter.setUnit(Unit.PERCENT);
            parameter.setQTY(new QTY(new Scalar(5.0), CD.HIGH));

            // add the parameter to the result
            result.addParameter(parameter);

            // add the result to the ANIML document
            animl.addResult(result);

            // write the ANIML document to a file
            AnimlWriter writer = new AnimlWriter();
            writer.write(animl, new File("result.animl"));

            // read the ANIML document from the file
            AnimlReader reader = new AnimlReader();
            AnimlDocument animl2 = reader.read(new File("result.animl"));

            // get the results from the ANIML document
            List<R> results = animl2.getResults();

            // print the name and value of each result
            for (R r : results) {
                System.out.println("Result: " + r.getName());
                List<P> parameters = r.getParameters();
                for (P p : parameters) {
                    System.out.println("Parameter: " + p.getName() + " = " + p.getQTY().getValue().getValue() + " " + p.getUnit().value());
                }
            }

        } catch (AnimlException | IOException e) {
            e.printStackTrace();
        }
    }
*/
}
