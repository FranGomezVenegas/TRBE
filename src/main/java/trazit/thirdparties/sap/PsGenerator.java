/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.thirdparties.sap;
// pwGenerator required import org.apache.commons.text.RandomStringGenerator;

public class PsGenerator {
    public static void main(String[] args) {
        int passwordLength = 12; // Set the desired password length
        boolean useLetters = true;
        boolean useNumbers = true;
        boolean useSpecialCharacters = true;

// pwGenerator required         RandomStringGenerator generator = new RandomStringGenerator.Builder()
// pwGenerator required                 .withinRange('0', 'z')
// pwGenerator required                 .filteredBy(Character::isLetter, Character::isDigit) // Include letters and digits
// pwGenerator required                 .build();

// pwGenerator required         String generatedPassword = generator.generate(passwordLength);

// pwGenerator required         System.out.println("Generated Password: " + generatedPassword);
    }
}


