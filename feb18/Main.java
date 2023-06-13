package feb18;

import java.io.IOException;
import java.util.Scanner;

/**
 * A simple program that sets up a game of Minesweeper
 * presented in an ASCII command-line format.
 * @author Amelia Johnson (amelia-online)
 */
public class Main {
    public static void main(String[] args) {

        play();
       
    }

    /**
     * Simple game loop. Allows the user to input a move
     * and repeats until the game is either won or lost.
     */
    public static void play() {

        Scanner scanner = new Scanner(System.in);

        Board board = new Board(20, 20);

        board.setup();

        while (board.getGameStatus() == 0) {
            // Clearing screen.
            System.out.print("\033[2J");
            System.out.flush();

            System.out.print("\033[H");
            System.out.flush();

            // Get command.
            System.out.println(board);
            System.out.print("Enter 'f' for flag or 'r' for reveal: ");

            String cmd = scanner.nextLine();
            switch(cmd) {

                case "f": // flag
                    System.out.print("Enter row and col: ");
                    String loc = scanner.nextLine();
                    String[] nums = loc.split(" ");
                    try {
                        int row = Integer.parseInt(nums[0]);
                        int col = Integer.parseInt(nums[1]);
                        board.flag(row, col);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    break;

                case "r": // reveal
                    System.out.print("Enter row and col: ");
                    String location = scanner.nextLine();
                    String[] nums2 = location.split(" ");
                    try {
                        int irow = Integer.parseInt(nums2[0]);
                        int icol = Integer.parseInt(nums2[1]);
                        board.interact(irow, icol);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    break;

                default:
                    continue;

            }
        }

        System.out.print("\033[2J");
        System.out.flush();

        System.out.print("\033[H");
        System.out.flush();

        board.reveal();
        System.out.println(board);

        if (board.getGameStatus() == 1)
            System.out.println("\033[1m\033[92mWin.\033[39m\033[0m"); // User has won.
        else
            System.out.println("\033[1m\033[91mLoss.\033[39m\033[0m"); // User has lost.

        scanner.close();
    }
}
