/**
 * Sudoku.java
 * 
 * Implementation of a class that represents a Sudoku puzzle and solves
 * it using recursive backtracking.
 *
 * Zuizz Saeed
 */

import java.io.*;   // allows us to read from a file
import java.util.*;

public class Sudoku {    
    // The current contents of the cells of the puzzle. 
    private int[][] grid;
    
    /*
     * Indicates whether the value in a given cell is fixed 
     * (i.e., part of the initial configuration).
     * valIsFixed[r][c] is true if the value in the cell 
     * at row r, column c is fixed, and false otherwise.
     */
    private boolean[][] valIsFixed;
    
    /*
     * This 3-D array allows us to determine if a given subgrid 
     * (i.e., a given 3x3 region of the puzzle) already contains a given value.
     * We use 2 indices to identify a given subgrid:
     *
     *    (0,0)   (0,1)   (0,2)
     *
     *    (1,0)   (1,1)   (1,2)
     * 
     *    (2,0)   (2,1)   (2,2)
     * 
     * For example, subgridHasVal[0][2][5] will be true if the subgrid in the 
     * upper right-hand corner already has a 5 in it, and false otherwise.
     */
    private boolean[][][] subgridHasVal;
    
    /*** add your additional fields here ***/
    
    /*
     * These matrices allow us to determine if a given
     * row or column already contains a given value.
     * For example, rowHasVal[3][4] will be true if row 3
     * already has a 4 in it.
     */
    private boolean[][] rowHasVal;
    private boolean[][] colHasVal;
    
    /* 
     * Constructs a new Puzzle object, which initially
     * has all empty cells.
     */
    public Sudoku() {
        this.grid = new int[9][9];
        this.valIsFixed = new boolean[9][9];     
        
        /* 
         * Note that the third dimension of the array is 10,
         * because we need to be able to use the possible values 
         * (1 through 9) as indices.
         */
        this.subgridHasVal = new boolean[9][9][10];        
      
        this.rowHasVal = new boolean[9][10];
        this.colHasVal = new boolean[9][10];
    }
    
    /*
     * Place the specified value in the cell with the specified coordinates, 
     * and update the state of the puzzle accordingly.
     */
    public void placeVal(int val, int row, int col) {
        this.grid[row][col] = val;
        this.subgridHasVal[row/3][col/3][val] = true;
        
        this.rowHasVal[row][val] = true;
        this.colHasVal[col][val] = true;
    }
        
    /*
     * remove the specified value from the cell with the specified coordinates, 
     * and update the state of the puzzle accordingly.
     */
    public void removeVal(int val, int row, int col) {
        this.grid[row][col] = 0;
        this.subgridHasVal[row/3][col/3][val] = false;
        
        this.rowHasVal[row][val] = false;
        this.colHasVal[col][val] = false;
    }  
        
    /*
     * read in the initial configuration of the puzzle from the specified 
     * Scanner, and use that config to initialize the state of the puzzle.  
     * The configuration should consist of one line for each row, with the
     * values in the row specified as integers separated by spaces.
     * A value of 0 should be used to indicate an empty cell.
     * 
     * You should not change this method.
     */
    public void readConfig(Scanner input) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = input.nextInt();
                this.placeVal(val, r, c);
                if (val != 0) {
                    this.valIsFixed[r][c] = true;
                }
            }
            input.nextLine();
        }
    }
                
    /*
     * Displays the current state of the puzzle.
     * You should not change this method.
     */        
    public void printGrid() {
        for (int r = 0; r < 9; r++) {
            this.printRowSeparator();
            for (int c = 0; c < 9; c++) {
                System.out.print("|");
                if (this.grid[r][c] == 0)
                    System.out.print("   ");
                else
                    System.out.print(" " + this.grid[r][c] + " ");
            }
            System.out.println("|");
        }
        this.printRowSeparator();
    }
        
    // A private helper method used by display() 
    // to print a line separating two rows of the puzzle.
    private static void printRowSeparator() {
        for (int i = 0; i < 9; i++)
            System.out.print("----");
        System.out.println("-");
    }
    
    /*
     * Determines if it is okay to place the value val in the cell with 
     * the specified coordinates, based on the current contents of the puzzle.
     * Returns true if the placement is valid, and false otherwise.
     */
    public boolean isValid(int val, int row, int col) {
        return (!this.valIsFixed[row][col]
          && !this.rowHasVal[row][val]
          && !this.colHasVal[col][val]
          && !this.subgridHasVal[row/3][col/3][val]);       
    }
            
    /*
     * This is the key recursive-backtracking method.
     * Returns true if a solution has already been found, and false otherwise.
     * 
     * There are different ways to use parameters in this method.
     * In the approach shown below, the parameter n is the number of 
     * the cell that a given invocation of the method is responsible for, 
     * with the cells numbered as follows:
     * 
     *     0  1  2  3  4  5  6  7  8
     *     9 10 11 12 13 14 15 16 17
     *    18 ...
     */
    private boolean solveRB(int n) {
        // we've found a solution!
        if (n >= 9*9)
            return true;
                
        // Determine the row and column of the nth cell.
        int row = n / 9;
        int col = n % 9;
                
        // skip fixed values
        if (this.valIsFixed[row][col]) {
            return this.solveRB(n + 1);
        }
                
        // Try all possible values for the current cell.
        for (int val = 1; val <= 9; val++) {
            if (this.isValid(val, row, col)) {
                this.placeVal(val, row, col);
                
                // Make a recursive call to move onto the next cell.
                // If the call returns true, the solution has already been
                // found, so we keep returning true.
                if (this.solveRB(n + 1)) {
                    return true;
                }
                                
                // If we get here, we've backtracked, so remove the
                // value placed above and try the next possible value.
                this.removeVal(val, row, col);
            }
        }
                
        // backtrack!
        return false;
    } 
    
    /*
     * public "wrapper" method for solveRB().
     * Makes the initial call to solveRB, and returns whatever it returns.
     */
    public boolean solve() { 
        boolean foundSol = this.solveRB(0);
        return foundSol;
    }
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Sudoku puzzle = new Sudoku();
        
        System.out.print("Enter the name of the puzzle file: ");
        String filename = scan.nextLine();
        
        try {
            Scanner input = new Scanner(new File(filename));
            puzzle.readConfig(input);
        } catch (IOException e) {
            System.out.println("error accessing file " + filename);
            System.out.println(e);
            System.exit(1);
        }
        
        System.out.println();
        System.out.println("Here is the initial puzzle: ");
        puzzle.printGrid();
        System.out.println();
        
        if (puzzle.solve()) {
            System.out.println("Here is the solution: ");
        } else {
            System.out.println("No solution could be found.");
            System.out.println("Here is the current state of the puzzle:");
        }
        puzzle.printGrid();  
    }    
}
