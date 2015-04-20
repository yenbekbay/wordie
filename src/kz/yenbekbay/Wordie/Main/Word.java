/**
 * This class stores the formed word and
 * handles modifying and fetching it.
 */

package kz.yenbekbay.Wordie.Main;

import kz.yenbekbay.Wordie.Box.Ball;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Word {
    /** Array of characters in the formed word */
    public ArrayList<Character> letters;

    /**
     * Default constructor.
     */
    public Word() {
        letters = new ArrayList<Character>();
    }

    /**
     * Adds the given character to the array.
     * @param letter character to add
     */
    public void addLetter(char letter) {
        letters.add(letter);
    }

    /**
     * Removes the last character in the array.
     */
    public void removeLastLetter() {
        letters.remove(letters.size()-1);
    }

    /**
     * Gets the string of characters in the array.
     * @return String the formed word
     */
    public String getString () {
        StringBuffer b = new StringBuffer();
        for(Character letter : letters){
            b.append(letter);
        }
        return b.toString();
    }

    /**
     * Checks if the formed word exists in the dictionary.
     * @return boolean whether the word is valid or not
     */
    public boolean isValid() {
        try {
            // Open the file as a buffered reader
            BufferedReader bf = new BufferedReader(new FileReader(GameFrame.dir + "/resources/words.txt"));
            // declare a string to hold our current line.
            String line;
            // Loop through each line, stashing the line into our line variable.
            while ((line = bf.readLine()) != null) {
                if (line.equals(getString().toLowerCase())) {
                    return true;
                }
            }
            // Close the file after done searching
            bf.close();
        }
        catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
        return  false;
    }

    /**
     * Clears the array of characters.
     */
    public void reset() {
        letters.clear();
    }

    /**
     * Estimates the number of points the formed word will give depending on the length and the colors.
     * @return int number of points
     */
    public int calculatePoints(ArrayList<Ball> balls) {
        int points = letters.size() * 10;
        boolean oneColor = true;
        for (int i = 0; i < balls.size() - 1; i++) { // if the balls were of the same color double the points
            if (balls.get(i).color != balls.get(i+1).color)
                oneColor = false;
        }
        if (oneColor) points *= 2;
        return points;
    }
}
