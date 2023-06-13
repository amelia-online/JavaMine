package feb18;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import feb18.Tile.State;

/**
 * A board is a collection of tiles that operate 
 * on Minesweeper game logic.
 * @author Amelia Johnson (amelia-online)
 */
public class Board {
    
    private Tile[][] board;
    private int rows, cols;
    private int numBombs;
    private int remBombs;

    // ANSI escapes for colorful text in the terminal.
    private final String BOLD = "\033[1m";
    private final String DEFAULT = "\033[39m";
    private final String RESET = "\033[0m";
    private final String GREEN = "\033[92m";
    private final String RED = "\033[91m";
    private final String BLUE = "\033[94m";
    private final String[] COLORS = { RED, BLUE, GREEN };

    private void execANSI(String color) {
        System.out.print(color);
        System.out.flush();
    }

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new Tile[rows][cols];
        numBombs = rows*cols / 7;
        remBombs = numBombs;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Tile();
                board[i][j].setRow(i);
                board[i][j].setCol(j);
            }
    }

    public void setNumBombs(int bombs) {
        this.numBombs = bombs;
    }

    public int getGameStatus() {
        if (remBombs == 0)
            return 1; // Win
        else if (remBombs == -1)
            return -1; // Loss
        else
            return 0; // Running
    }

    public int getNumBombs() { return numBombs; }

    public void setTileValue(int row, int col, int value) {
        if (value < -1)
            return;

        board[row][col].setValue(value);
    }

    public int getRows() { return rows; }

    public int getCols() { return cols; }

    /**
     * Reveals the tile at the specified row and column of 
     * the board.
     * 
     * If a mine is revealed, the game ends.
     * 
     * @param row The row on which resides the tile to reveal.
     * @param col The column on which resides the tile to reveal.
     */
    public void interact(int row, int col) {
        if (row < 0 || row > rows || col < 0 || col > cols)
            return;

        Tile tile = board[row][col];

        if (tile.getValue() > 0 && tile.getState() == State.REVEALED) {
            revealAdjacent(tile.getRow(), tile.getCol(), new ArrayList<Tile>());
        } else if (tile.getValue() == -1) { // Player reveals a mine.
            tile.reveal();
            tile.setValue(-2);
            remBombs = -1; // Sets the game status to loss.
            reveal(); // Reveal the board.
            return;
        }
        else if (tile.getValue() > 0) {
            tile.reveal();
        }
        else { // In this case, the value of the tile is zero.. An empty space.
            tile.reveal();
            expandArea(row, col, new ArrayList<Tile>()); // Find neighboring empty spaces and reveal them too.
        }   
    }

    public void flag(int row, int col) {
        if (row < 0 || row > rows || col < 0 || col > cols)
            return;

        // Keeping track of player's progress.
        if (board[row][col].getValue() == -1 && !board[row][col].isFlagged())
            remBombs--;
        else if (board[row][col].getValue() == -1 && board[row][col].isFlagged())
            remBombs++;

        board[row][col].toggleFlag();
    }

    /**
     * Given a tile position, this method recursively reveals
     * adjacent empty tiles and tiles that border the empty tiles
     * and aren't mines. Does not reveal correctly flagged tiles.
     * @param row The row to begin.
     * @param col The column to begin.
     * @param traversed Keeps track of already seen tiles. Enter a new empty list. 
     */
    private void expandArea(int row, int col, ArrayList<Tile> traversed) {
        ArrayList<Tile> adjacent = (ArrayList<Tile>)getAdjacentTiles(row, col);
        for (Tile t : adjacent) {
            if (traversed.contains(t))
                continue;
            traversed.add(t);
            if (t.getValue() > 0) // Normal tile.
                t.reveal();
            else if (t.getValue() == 0) { // Empty tile.
                t.reveal();
                expandArea(t.getRow(), t.getCol(), traversed);
            } else if (t.getState() == State.FLAGGED || t.getValue() == -1) // Flagged.
                continue;
        }
    }

    /**
     * Determines if a flagged tile has the
     * correct number of adjacent tiles flagged
     * according to the original tile's value.
     * Tile cannot be a mine or be revealed.
     * @param row The row of the tile.
     * @param col The column of the tile.
     * @return See above.
     */
    private boolean meetsRequirements(int row, int col) {
        Tile tile = board[row][col];
        if (tile.getValue() < 0 || tile.getState() != State.REVEALED)
            return false;
        ArrayList<Tile> adjacent = (ArrayList<Tile>)getAdjacentTiles(row, col);
        int numFlagged = 0;
        for (Tile t : adjacent)
            if (t.getValue() == -1 && t.getState() == State.FLAGGED)
                ++numFlagged;
        return numFlagged >= tile.getValue();
    }   

    /**
     * Assuming the tile meets the requirements as 
     * shown in `meetsRequirements`, this function
     * reveals a tile's neighbors, and calls
     * `expandArea` if an empty tile is encountered.
     * @param row The row of the start tile.
     * @param col The column of the start tile.
     * @param traversed A list of already traversed tiles.
     */
    private void revealAdjacent(int row, int col, ArrayList<Tile> traversed) {
        if (!meetsRequirements(row, col))
            return;

        ArrayList<Tile> adjacent = (ArrayList<Tile>)getAdjacentTiles(row, col);
        for (Tile t : adjacent) {
            if (traversed.contains(t))
                continue;
            traversed.add(t);

            int val = t.getValue();

            if (val > 0) {
                t.reveal();
            } else if (val == -1 && t.getState() != State.FLAGGED) {
                t.setValue(-2);
                remBombs = -1;
                break;
            } else if (val == 0) {
                t.reveal();
                expandArea(t.getRow(), t.getCol(), traversed);
            } else if (val == -1 && t.getState() == State.FLAGGED)
                continue;
            
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cols; i++) {
           if ((i + "").length() > 1)
                System.out.print(i + " ");
            else
                System.out.print(" " + i + " ");

        }
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                switch (board[i][j].getState()) {

                    case HIDDEN:
                        System.out.print("[#]");
                        break;

                    case FLAGGED:
                        System.out.print("[");
                        execANSI(BOLD);
                        execANSI(GREEN);
                        System.out.print("F");
                        execANSI(DEFAULT);
                        System.out.print("]");
                        break;  

                    case REVEALED:

                        if (board[i][j].getValue() == 0) {
                            System.out.print(" . ");
                            break;
                        }
                        
                        System.out.print("[");
                        switch (board[i][j].getValue()) {

                            case -3:
                                execANSI(BOLD);
                                execANSI(GREEN);
                                System.out.print("*");
                                execANSI(DEFAULT);
                                break;

                            case -2:
                            execANSI(BOLD);
                            execANSI(RED);
                            System.out.print("X");
                            execANSI(DEFAULT);
                            break;

                            case -1:
                                execANSI(BOLD);
                                execANSI(RED);
                                System.out.print("B");
                                execANSI(DEFAULT);
                                break;

                            default:
                                execANSI(BOLD);
                                execANSI(COLORS[board[i][j].getValue() % 3]);
                                System.out.print(board[i][j].getValue());
                                execANSI(DEFAULT);
                                break;
                        }
                        System.out.print("]");
                        break;
        
                    default:
                        break;

                }
                execANSI(RESET);
            }
            System.out.print(" " + i);
            System.out.println("");
        }
        return builder.toString();
    }

    /**
     * Determines if a row-column coordinate
     * is within the board.
     * @param row The row to check.
     * @param col The column to check.
     * @return True if the coordinate is within the board, false otherwise.
     */
    public boolean isWithinBoard(int row, int col) {
        return (row >= 0 && row < rows) && (col >= 0 && col < cols);
    }

    /**
     * Given a row-column coordinate, this
     * function returns the immediately
     * adjacent tiles to that tile.
     * @param row The row of the tile.
     * @param col The column of the tile.
     * @return See above.
     */
    public List<Tile> getAdjacentTiles(int row, int col) {
        ArrayList<Tile> result = new ArrayList<>();
        if (!isWithinBoard(row, col))
            return result;

        // Gets above and below tile.
        for (int i = -1; i < 2; i++) {
            if (isWithinBoard(row+i, col-1))
                result.add(board[row+i][col-1]);
            if (isWithinBoard(row+i, col+1))
                result.add(board[row+i][col+1]);
        }

        // Gets the sides of the tile.
        if (isWithinBoard(row+1, col))
            result.add(board[row+1][col]);
        if (isWithinBoard(row-1, col))
            result.add(board[row-1][col]);

        return result;
    }

    /*
     * Randomizes the entire board.
     */
    public void randomize() {
        for (Tile[] tiles : board)
            for (Tile tile : tiles)
                tile.randomize();
    }

    /**
     * Sets up the board for a game by 
     * placing all the mines.
     */
    public void setup() {
        Random rng = new Random();
        int bombs = numBombs;
        while (bombs > 0) {
            int row = rng.nextInt(0, rows);
            int col = rng.nextInt(0, cols);

            if (board[row][col].getValue() == -1)
                continue;

            board[row][col].setValue(-1);
            bombs--;
        }

        // Goes through every tile and checks how many mines are
        // adjacent to it.
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                {
                    if (board[i][j].getValue() == -1)
                        continue;
                    ArrayList<Tile> adjacent = (ArrayList<Tile>)getAdjacentTiles(i, j);
                    int bombsCount = countBombs(adjacent);
                    board[i][j].setValue(bombsCount);
                }
    }

    /**
     * Counts the number of mines in a list of tiles.
     * @param tiles The list to check.
     * @return the number of mines in a list of tiles.
     */
    private int countBombs(ArrayList<Tile> tiles) {
        int result = 0;
        for (Tile t : tiles)
            if (t.getValue() == -1)
                result++;
        return result;
    }

    /**
     * Reveals every tile on the board.
     */
    public void reveal() {
        for(Tile[] tiles: board)
            for (Tile t : tiles)
                t.reveal();
    }

}
