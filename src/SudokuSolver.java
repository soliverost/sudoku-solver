import java.io.*;

/**
 * This is a class that reads a .csv file with a classic 9x9 sudoku board 
 * with zeroes as empty spaces. Then, it completes the board according 
 * to sudoku rules and ouputs the result in a .csv file.
 * 
 * @author soliverost
 */
public class SudokuSolver {

	final static int BOARD_SIZE = 9;

	/**
	 * @param args
	 * argument 0 is the name of a .csv file containing a incomplete sudoku board
	 * argument 1 is the desired name for the output file
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
		
		if(args.length != 2) {
			System.out.println("Please include: inputFile.csv outputName.csv");
			return;
		}

		// Read the input file an check for correctness
		if(!readInput(args[0], board)) 
			return;
		
		// Print input board to screen
		System.out.println("Input board:");
		printBoard(board);
		System.out.println(" ");

		// Solve the board and print if a solution was found
		if (solveBoardBacktracking(board)) {
			System.out.println("Solved board:");
			printBoard(board);
			outputSolution(board, args[1]);
		} else
			System.out.println("No Solution found");

	} 
	
	/**** Functions to deal with input/output *****/

	private static boolean readInput(String fileName, int[][] board)
			throws FileNotFoundException, IOException 
	{

		File file = new File(fileName);		
		
		if(!file.exists()){
			System.out.println("Coulnd't find the input file");
			return false;
		}
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		int row = 0;

		while ((line = in.readLine()) != null) {

			if (row >= BOARD_SIZE) {
				System.out.println("Error with input. Board size must be 9x9");
				in.close();
				return false;
			}

			String[] inputInString = line.split(",");

			if (inputInString.length != BOARD_SIZE) {
				System.out.println("Error with input. Board size must be 9x9");
				in.close();
				return false;
			}

			for (int col = 0; col < inputInString.length; col++) {
				try {
					// Check if the number is between 0-9
					int num = Integer.parseInt(inputInString[col]);
					if(num >= 0 && num <= 9)
						board[row][col] = num;
					else {
						System.out.println("Error with input. Numbers must be between 0-9");
						in.close();
						return false;
					}
						
				} catch (NumberFormatException e) {
					System.out.println("Error with input. Only numbers are allowed");
				}

			}

			row++;

		}
		in.close();			
		
		return true;
	}
	
	public static void printBoard(int[][] board) {
		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < 9; col++) {
				System.out.print(board[row][col] + " ");
			}
			System.out.print("\n");
		}
	}

	public static void outputSolution(int[][] board, String fileName) {
		try {
			File file = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));

			for (int row = 0; row < BOARD_SIZE; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					if (col != BOARD_SIZE - 1)
						output.write(board[row][col] + ",");
					else
						output.write(board[row][col] + "\n");
				}
			}
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**** Functions to solve the Board *****/

	public static boolean isRowValid(int value, int rowPos, int colPos,
			int[][] board) {
		for (int row = 0; row < 9; row++) {
			if (value == board[row][colPos] && row != rowPos)
				return false;
		}
		return true;
	}

	public static boolean isColValid(int value, int rowPos, int colPos,
			int[][] board) {
		for (int col = 0; col < 9; col++) {
			if (value == board[rowPos][col] && col != colPos)
				return false;
		}
		return true;
	}

	public static boolean isSquareValid(int value, int rowPos, int colPos,
			int[][] board) {
		// Find the start of the row and the start of the column
		int rowStart = rowPos - rowPos % 3;
		int colStart = colPos - colPos % 3;

		for (int row = rowStart; row < rowStart + 3; row++) {
			for (int col = colStart; col < colStart + 3; col++) {
				if (value == board[row][col])
					return false;
			}
		}
		return true;
	}

	public static boolean isValid(int value, int rowPos, int colPos,
			int[][] board) {

		boolean rowValid = isRowValid(value, rowPos, colPos, board);
		boolean colValid = isColValid(value, rowPos, colPos, board);
		boolean squareValid = isSquareValid(value, rowPos, colPos, board);

		if (rowValid && colValid && squareValid)
			return true;
		else
			return false;

	}

	public static Position getNextEmpty(int[][] board) {

		Position emptyPos = null;

		for (int row = 0; row < BOARD_SIZE; row++) {
			for (int col = 0; col < BOARD_SIZE; col++) {
				if (board[row][col] == 0) {
					emptyPos =  new Position(row,col);
					return emptyPos;
				}
			}
		}

		return emptyPos;
	}

	public static boolean solveBoardBacktracking(int[][] board) {

		// Find the first empty square
		Position emptyPos = getNextEmpty(board);
		
		// We didn't find an empty square = success!
		if (emptyPos == null) {
			return true;
		}

		int row = emptyPos.getRow();
		int col = emptyPos.getCol();

		// Try all the possible digits
		for (int num = 1; num <= 9; num++) {

			if (isValid(num, row, col, board)) {
				board[row][col] = num;

				if (solveBoardBacktracking(board))
					return true;

				// We didn't find a solution so make it empty again
				board[row][col] = 0;
			}

		}

		return false;

	}


	/****** AUXILIARY CLASS ****/
	
	/**
	 * Class to keep track of a specific position on the board.
	 * Includes the row number and the column number
	 * @author solivero
	 */
	private static class Position {
		private int row;
		private int col;
		
		public Position(int row, int col){
			this.row = row;
			this.col = col;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

	}

}// end of class

