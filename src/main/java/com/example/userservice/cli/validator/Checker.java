package com.example.userservice.cli.validator;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 28-03-2026
 * Description: this interface is responsible for verifying the data
 */
public interface Checker {
    /**
     * @ Method Name: checkNumber
     * @ Description: checking if the string is the correct number (consisting of digits only)
     * and whether it is in the range from the min value to max value;
     * 'wordName' is the name of the variable being defined
     * @ param      : [java.lang.String, int, int]
     * @ return     : int
     */
    int checkNumber(String wordName, int numberOfMinOperation, int numberOfMaxOperation);

    /**
     * @ Method Name: checkWord
     * @ Description: checking if the string is the correct word - not empty;
     * 'wordName' is the name of the variable being defined
     * @ param      : [java.lang.String]
     * @ return     : java.lang.String
     */
    String checkWord(String wordName);
}
