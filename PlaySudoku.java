package sudokuTry;

import java.io.*;
import java.util.*;

public class PlaySudoku {
	static String rows = "123456789";
	static String cols = "ABCDEFGHI";
	public static void printForGame(Map<String, String> solution) {
        for (int i = 0; i < 9; ++i) {
            if (i % 3 == 0)
                System.out.println(" -----------------------");
            for (int j = 0; j < 9; ++j) {
                if (j % 3 == 0) System.out.print("| ");
                System.out.print(solution.get(""+rows.charAt(i)+cols.charAt(j)).equals("0")
                                 ? "_"
                                 : solution.get(""+rows.charAt(i)+cols.charAt(j)));
                System.out.print(' ');
            }
            System.out.println("|");
        }
        System.out.println(" -----------------------");
    }
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			try{
				System.out.println("----Welcome to Sudoku Solver----\n\n");
				System.out.println("Select your option : \n1\t:\tGet solution to a puzzle\n0\t:\tExit\nYour Choice  :  ");
				String input = br.readLine();
				while(input.length() == 0){
					System.out.println("Enter valid choice from above menu : ");
					input = br.readLine();
				}
				int value = Integer.parseInt(input);
				if(value==0){
					System.out.println("Thank you and good bye..!");
					System.exit(1);
				}
				else if(value==1){
					System.out.println("Please enter the path of Sudoku Puzzle file (*.csv format)");
					String fileName = br.readLine();
					sudokuSolver a = new sudokuSolver(fileName);
					fileName = fileName.replace(".csv", "Solution.csv");
					long startTime = System.nanoTime();
					System.out.println("Original Sudoku : ");
					printForGame(a.sudokuGrid);
					a.solutionFinder(a.sudokuGrid);
					long solutionTime = System.nanoTime() - startTime;
					System.out.println("\nThe solution to the given sudoku file is...\n\n");
					printForGame(a.finalSolution);
					System.out.println("The computation time for solving the puzzle was "+ solutionTime*1e-9);
					a.exportCSV(fileName);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
