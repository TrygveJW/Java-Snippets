package no.trygvejw.simpleUi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


/*
TODO:
    - singelton?
    - is the open Scanner system in an issue? it is not coled atm
    - default values shold be supported



 */


/**
 * Provides a simple way to get checked inputs from the user. Can query the user for string, doubles or ints
 * with the possibility for adding upper and lower bounds to the number query.
 * If an invalid input is detected the user is notified of what is wrong and queried again
 *
 * @author Trygve Woldseth
 * @version 1.0
 */
public class SimpleUserInput {

    private Scanner scanner;


    public SimpleUserInput() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Querys the user with the Qstring for a string input, if no input is provided
     * the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @return the string provided by th user
     */
    public String getStringInputt(String Qstring){
        boolean validInput = false;
        String returnStr = "";
        while (!validInput){
            System.out.print(Qstring);
            returnStr = scanner.nextLine();
            validInput = this.isValidStingInp(returnStr);
        }
        return returnStr;
    }

    /**
     * Querys the user with the Qstring for a string input, if no input is provided
     * null is returned, if allow empty is true, else the user is notified
     * @param Qstring the query to display to the user
     * @return the string provided by th user
     */
    public String getStringInputt(String Qstring, boolean allowEmpty){
        if (allowEmpty){
            System.out.print(Qstring);
            return scanner.nextLine();
        } else {
            return getStringInputt(Qstring);
        }
    }

    /**
     * Querys the user with the Qstring for a string input with a [y/n] appended;
     * If default true an ampty string wil result in a True beeing returned
     * @param Qstring the query to display to the user
     * @param defaultTrue if true an empty string wil return true if false empty wil return false
     * @return bol indicating whether the user answered y or n
     */
    public boolean getYesNoInput(String Qstring, boolean defaultTrue){
        boolean accept = defaultTrue;
        boolean validInput = false;

        String ynBox = (defaultTrue)? " [Y/n]: " : " [y/N]: ";



        while (!validInput){
            System.out.print(Qstring + ynBox);
            String returnStr = scanner.nextLine();

            if (!returnStr.isBlank()){
                if(returnStr.toLowerCase().equals("y")){
                    accept = true;
                    validInput = true;
                } else if(returnStr.toLowerCase().equals("n")){
                    accept = false;
                    validInput = true;
                } else {
                    System.out.println("invalid input");
                }
            } else {
                // input empty use default
                validInput = true;
            }
        }


        return accept;
    }

    /**
     * Querys the user with the Qstring for any number value, if no input or NAN input is provided
     * the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @return the number returned by the user
     */
    public double getDoubleInput(String Qstring){
        return getUserDoubleImpute(Qstring, "NONE", 0, 0);
    }

    /**
     * Querys the user with the Qstring for any number value, if no input, NAN input, or input smaller than min provided
     * the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @param min (inclusive) lower bound for accepted user inputs
     * @return the number provided by the user
     */
    public double getDoubleInput(String Qstring, double min){
        return getUserDoubleImpute(Qstring, "LOWER", min, 0);
    }

    /**
     * Querys the user with the Qstring for any number value, if no input, NAN input, or input out of range is provided
     * the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @param min (inclusive) lower bound for accepted user inputs
     * @param max (exclusive) upper bound for accepted user inputs
     * @return the number provided by the user
     * @throws IllegalArgumentException if the min input bound is greater than or equal to max
     */
    public double getDoubleInput(String Qstring, double min, double max){
        if (min >= max){
            throw new IllegalArgumentException("min input bound is greater than or equal to max");
        }
        return getUserDoubleImpute(Qstring, "UPPER_LOWER", min, max);
    }

    /**
     * Querys the user with the Qstring for a whole (integer) number value, if no input or NAN input is provided
     * the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @return the number returned by the user
     */
    public int getIntInput(String Qstring){
        return getUserIntInput(Qstring, "NONE", 0, 0);
    }

    /**
     * Querys the user with the Qstring for a whole (integer) number value, if no input, NAN input,
     * or input smaller than min provided the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @param min (inclusive) lower bound for accepted user inputs
     * @return the number provided by the user
     */
    public int getIntInput(String Qstring, int min){
        return getUserIntInput(Qstring, "LOWER", min, 0);
    }

    /**
     * Querys the user with the Qstring for a whole (integer) number value, if no input, NAN input,
     * or input out of range is provided the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @param min (inclusive) lower bound for accepted user inputs
     * @param max (exclusive) upper bound for accepted user inputs
     * @return the number provided by the user
     * @throws IllegalArgumentException if the min input bound is greater than or equal to max
     */
    public int getIntInput(String Qstring, int min, int max){
        if (min >= max){
            throw new IllegalArgumentException("min input bound is greater than or equal to max");
        }
        return getUserIntInput(Qstring, "UPPER_LOWER", min, max);
    }

    /**
     * Querys the user with the Qstring for a date of the given dateFormat format if no input or wrong format input
     * is provided the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @param dateFormat a dateformat object with the format required from the user
     * @return a Date object with the date provided by the user
     */
    public Date getDateInput(String Qstring, SimpleDateFormat dateFormat){
        boolean validInput = false;
        Date returnDate = null;
        // makes the parser throw an exception if the format is wrong
        dateFormat.setLenient(false);
        while (!validInput) {
            System.out.print(Qstring);
            String inpDateStr = scanner.nextLine();

            if (isValidDate(inpDateStr, dateFormat)) {
                try {
                    returnDate = dateFormat.parse(inpDateStr);
                    validInput = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return returnDate;
    }



    // -- private method's -- //


    /**
     * Querys the user with the Qstring for any number value, if no input, NAN input, or input out of range is provided
     * the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @param constriction UPPER, UPPER_LOWER or NONE depending on what kind of constriction is wanted for the input
     * @param min (inclusive) lower bound for accepted user inputs
     * @param max (exclusive) upper bound for accepted user inputs
     * @return the number provided by the user
     */
    private double getUserDoubleImpute(String Qstring, String constriction, double min, double max){
        boolean validInput = false;
        double returnDouble = 0;

        while (!validInput) {
            System.out.print(Qstring);
            String tmp = scanner.nextLine().replace(" ", "").replace(",", ".");
            validInput = this.isValidDoubleInc(tmp);
            if (validInput){
                returnDouble = Double.valueOf(tmp);
                if (constriction.equals("UPPER_LOWER")){
                    validInput = returnDouble >= min && returnDouble < max;
                    if (!validInput){
                        System.out.printf("input is not within the given range (%f - %f)\n", min , max);
                    }
                } else if (constriction.equals("LOWER")){
                    validInput = returnDouble >= min;
                    if (!validInput){
                        System.out.printf("input is not within the given range (%3f >)\n", min);
                    }
                }
            }
        }
        return returnDouble;
    }


    /**
     * Querys the user with the Qstring for a whole (integer) number value, if no input, NAN input,
     * or input out of range is provided the user is notified and queried again.
     * @param Qstring the query to display to the user
     * @param constriction UPPER, UPPER_LOWER or NONE depending on what kind of constriction is wanted for the input
     * @param min (inclusive) lower bound for accepted user inputs
     * @param max (exclusive) upper bound for accepted user inputs
     * @return the number provided by the user
     */
    private int getUserIntInput(String Qstring, String constriction, int min, int max){
        boolean validInput = false;
        int returnInt = 0;
        while (!validInput) {
            System.out.print(Qstring);
            String inpInt = scanner.nextLine().replace(" ", "");
            validInput = this.isValidIntInp(inpInt);
            if (validInput){
                returnInt = Integer.valueOf(inpInt);
                if (constriction.equals("UPPER_LOWER")){
                    validInput = returnInt >= min && returnInt < max;
                    if (!validInput){
                        System.out.printf("input is not within the given range (%d - %d)\n", min , max);
                    }
                } else if (constriction.equals("LOWER")){
                    validInput = returnInt >= min;
                    if (!validInput){
                        System.out.printf("input is not within the given range (%3d >)\n", min);
                    }
                }
            }
        }
        return returnInt;
    }

    /**
     * Checks if the provided inp sting is not of lenght 0 notifies the user if it is.
     * @param input the string to check
     * @return false if the string is 0 long true if not
     */
    private boolean isAnyInput(String input){
        boolean isInputt = false;
        if (input.length() > 0){
            isInputt = true;
        } else {
            System.out.println("No Inputt Provided");
        }
        return isInputt;
    }

    /**
     * Verifies if the provided inp sting has a valid date format notifies the user if not.
     * @param input the string to check
     * @param dateFormat the dateFormat to check the string against
     * @return true if valid false if not
     */
    private boolean isValidDate(String input, SimpleDateFormat dateFormat) {
        boolean validInput = false;

        if (isAnyInput(input)){
            try {
                dateFormat.parse(input);
                validInput = true;
            } catch (ParseException e) {
                System.out.println("Invalid date the required format is: " + dateFormat.toPattern());

            }
        }
        return validInput;
    }

    /**
     * Checks if the provided inp sting is not of lenght 0 notifies the user if it is.
     * @param input the string to check
     * @return false if the string is 0 long true if not
     */
    private boolean isValidStingInp(String input){
        return isAnyInput(input);
    }

    /**
     * Checks if the provided inp string is a valid double value notefies the user if not.
     * @param input the string to check
     * @return true if valid false if not
     */
    private boolean isValidDoubleInc(String input){
        boolean isInput = isAnyInput(input);
        boolean valid = false;
        if (isInput){
            try {
                Double.valueOf(input);
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("input is not a number");
            }
        }

        return valid;
    }

    /**
     * Checks if the provided inp string is a valid whole (integer) number value notefies the user if not.
     * @param input the string to check
     * @return true if valid false if not
     */
    private boolean isValidIntInp(String input){
        boolean isInput = isAnyInput(input);
        boolean valid = false;
        if (isInput){
            try {
                Integer.valueOf(input);
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("input is not a whole number");
            }
        }

        return valid;
    }
}
