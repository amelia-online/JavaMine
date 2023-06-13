package feb18;

import java.util.Random;

/**
 * Represents a single tile within a board.
 * @author Amelia Johnson (amelia-online)
 */
public class Tile {

    private Random rand = new Random();

    /**
     * The three states a tile can be in.
     */
    enum State {
        REVEALED,
        FLAGGED,
        HIDDEN,
    }

    private State state; // Current state of the tile.
    private int value; // The value the tile holds.
    private boolean isRevealed;
    private int row, col; // Its own position within the board.

    public Tile() {
        state = State.HIDDEN;
        value = 0;
        isRevealed = false;
        row = col = 0;
    }

    public Tile(State state, int value) {
        this.state = state;
        this.value = value;
        isRevealed = false;
        row = col = 0;
    }

    public Tile(int val) {
        this.value = val;
        state = State.HIDDEN;
        isRevealed = false;
        row = col = 0;
    }

    public boolean isFlagged() {
        return state == State.FLAGGED;
    }

    public int getRow() { return row; }

    public int getCol() { return col; }

    public void setRow(int row) { this.row = row; }

    public void setCol(int col) { this.col = col; }

    public State getState() { return state; }

    public int getValue() { return value; }

    public boolean isBomb() { return value == -1; } // a value of -1 indicates this tile is a mine.

    /**
     * Randomizes the state of the tile.
     */
    public void randomize() {
        int value = rand.nextInt(-1, 6);
        this.value = value;
        int choice = rand.nextInt(0, 3);
        switch (choice) {

            case 0:
                state = State.REVEALED;
                break;

            case 1:
                state = State.HIDDEN;
                break;

            case 2:
                state = State.FLAGGED;
                break;

            default:
                break;
        }
    }

    /**
     * If this tile is already flagged, 
     * its state becomes hidden. Otherwise, 
     * this tile will become flagged. You cannot
     * toggle an already revealed tile.
     */
    public void toggleFlag() {
        if (isRevealed)
            return;

        if (state == State.FLAGGED) 
            state = State.HIDDEN;
        else
            state = State.FLAGGED;
    }

    /**
     * Sets this tile's state to flagged no matter what.
     */
    public void flag() {
        state = State.FLAGGED;
    }

    /**
     * Reveals this tile.
     */
    public void reveal() {
        if (state == State.FLAGGED && value != -1)
            value = -2; // A value of -2 indicates a false flag. It prints as an 'X'/
        else if (state == State.FLAGGED && value == -1)
            value = -3; // A value of -3 indicates a correct flag.

        this.state = State.REVEALED;
        isRevealed = true;
    }

    public void setValue(int val) {
        value = val;
    }

    @Override
    public String toString() {
        switch(state) {
            
            case HIDDEN:
                return "[#]";

            case REVEALED:
                switch (value) {

                    case -2:
                        return "[\033[1m\033[91mX\033[39m]";

                    case -1:
                        return "[\033[1m\033[91mB\033[39m]";

                    case 0: // A value of zero indicates an empty space.
                        return " . ";
                        
                    default:
                        return "[\033[94m\033[1m" + value + "\033[39m]";

                }

            case FLAGGED:
                return "[\033[1m\033[92mF\033[39m]";

            default:
                return "[?]";
        }

    }

}