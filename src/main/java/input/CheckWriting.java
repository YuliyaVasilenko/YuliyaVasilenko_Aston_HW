package input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-03-2026
 * Description: this class is responsible for getting data from the console and verifying it
 */
public class CheckWriting implements Checker {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int exit = 0;
    private static final Logger logger = LoggerFactory.getLogger(CheckWriting.class);

    /**
     * @ Method Name: checkNumber
     * @ Description: checking if the string is the correct number (consisting of digits only)
     * and whether it is in the range from the min value to max value;
     * 'wordName' is the name of the variable being defined
     * @ param -> return: [java.lang.String, int, int] [wordName, numberOfMinOperation, numberOfMaxOperation] -> int
     */
    @Override
    public int checkNumber(String wordName, int numberOfMinOperation, int numberOfMaxOperation) {
        System.out.println("Please enter the " + wordName + " or press " + exit + " to exit to the menu");
        String wordUser = scanner.nextLine();
        while (!wordUser.equals(String.valueOf(exit))) {
            try {
                int numberUser = Integer.parseInt(wordUser);
                if (numberUser < numberOfMinOperation || numberUser > numberOfMaxOperation) {
                    throw new IllegalArgumentException();
                }
                logger.info("The number was written correct: {}", numberUser);
                return numberUser;
            } catch (NumberFormatException e) {
                logger.warn("The number was written incorrect, the number should be made up of digits only: {}", wordName);
                System.out.println("This is not a number, the number should be made up of digits only. "
                        + "Please write a correct number in range from "
                        + numberOfMinOperation + " to " + numberOfMaxOperation + ". To exit press " + exit);
                wordUser = scanner.nextLine();
            } catch (IllegalArgumentException e) {
                logger.warn("The number was written incorrect, the number not in range required: {}", wordName);
                System.out.println("Please write a correct number in range from "
                        + numberOfMinOperation + " to " + numberOfMaxOperation + ". To exit press " + exit);
                wordUser = scanner.nextLine();
            }
        }
        logger.warn("the exit to the menu was selected instead writing the number");
        return exit;
    }

    /**
     * @ Method Name: checkWord
     * @ Description: checking if the string is the correct word - not empty;
     * 'wordName' is the name of the variable being defined
     * @ param -> return: [java.lang.String] [wordName] -> java.lang.String
     */
    @Override
    public String checkWord(String wordName) {
        System.out.println("Please enter the " + wordName + " or press " + exit + " to exit to the menu");
        String wordUser = scanner.nextLine();
        while (wordUser.isBlank() && !wordUser.equals(String.valueOf(exit))) {
            logger.warn("The word was written incorrect, the word should not be empty: {}", wordName);
            System.out.println("Incorrect " + wordName + ", "
                    + wordName + " should not be empty, please write the " + wordName
                    + " again or press " + exit + " to exit to the menu");
            wordUser = scanner.nextLine();
        }
        if (wordUser.equals(String.valueOf(exit))) {
            logger.warn("the exit to the menu was selected instead writing the {}", wordName);
            wordUser = null;
        } else {
            logger.info("The number was written correct: {}", wordUser);
        }
        return wordUser;
    }

    /**
     * @ Method Name: checkEmail
     * @ Description: checking if the string is the correct email - not empty and looks like *@*.* (* is any number of characters)
     * @ param -> return: [] [] -> java.lang.String
     */
    public String checkEmail() {
        Pattern patternEmail = Pattern.compile("(.)+@(.)+\\.(.)+");
        Matcher matcherEmail;
        String email = checkWord("email");
        while (email != null) {
            matcherEmail = patternEmail.matcher(email);
            if (matcherEmail.find()) {
                logger.info("The email was written correct: {}", email);
                return email;
            } else {
                logger.warn("The word was written incorrect, the email should be empty like ***@***.***, but was: {}", email);
                System.out.println("Incorrect email, the email should be like ***@***.***. " +
                        "Please write the email again or press " + exit + " to exit to the menu");
                email = checkWord("email");
            }
        }
        return null;
    }
}
