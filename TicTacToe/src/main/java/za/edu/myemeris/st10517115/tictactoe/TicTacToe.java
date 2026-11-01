/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package za.edu.myemeris.st10517115.tictactoe;


import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class TicTacToe extends JFrame {

    // ======================= GAME LOGIC (no Swing) =======================
    static class Board {
        enum Player { X, O }

        private static final int[][] LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},   // rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},   // columns
            {0, 4, 8}, {2, 4, 6}               // diagonals
        };

        private final Player[] cells = new Player[9];
        private Player current = Player.X;
        private int[] winLine;                 // null until someone wins

        /** Attempts a move. Returns false if the cell is taken or the game is over. */
        boolean play(int i) {
            if (cells[i] != null || isOver()) return false;
            cells[i] = current;
            winLine = findWin();
            if (winLine == null) current = (current == Player.X) ? Player.O : Player.X;
            return true;
        }

        private int[] findWin() {
            for (int[] l : LINES) {
                if (cells[l[0]] != null
                        && cells[l[0]] == cells[l[1]]
                        && cells[l[0]] == cells[l[2]]) return l;
            }
            return null;
        }

        boolean isFull() {
            for (Player p : cells) if (p == null) return false;
            return true;
        }

        boolean isOver()   { return winLine != null || isFull(); }
        boolean hasWinner(){ return winLine != null; }
        int[] getWinLine() { return winLine; }
        Player getCurrent(){ return current; }
        Player get(int i)  { return cells[i]; }

        void reset() {
            Arrays.fill(cells, null);
            current = Player.X;
            winLine = null;
        }
    }

    // ============================== UI ===================================
    private static final Color X_COLOR   = new Color(30, 90, 200);
    private static final Color O_COLOR   = new Color(210, 50, 50);
    private static final Color WIN_COLOR = new Color(144, 238, 144);
    private static final Font  CELL_FONT = new Font("Arial", Font.BOLD, 64);

    private final Board board = new Board();
    private final JButton[] buttons = new JButton[9];
    private final JLabel status = new JLabel("", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
    private int xWins, oWins, draws;

    public TicTacToe() {
        super("Tic-Tac-Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Status bar (top)
        status.setFont(new Font("Arial", Font.BOLD, 22));
        status.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(status, BorderLayout.NORTH);

        // 3x3 grid (centre)
        JPanel grid = new JPanel(new GridLayout(3, 3, 4, 4));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        for (int i = 0; i < 9; i++) {
            final int index = i;
            JButton b = new JButton();
            b.setFont(CELL_FONT);
            b.setFocusPainted(false);
            b.setOpaque(true);
            b.addActionListener(e -> handleMove(index));
            buttons[i] = b;
            grid.add(b);
        }
        add(grid, BorderLayout.CENTER);

        // Scoreboard + New Game (bottom)
        JButton newGame = new JButton("New Game");
        newGame.addActionListener(e -> {
            board.reset();
            refresh();
        });
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottom.add(scoreLabel, BorderLayout.CENTER);
        bottom.add(newGame, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(360, 440));
        setSize(420, 500);
        setLocationRelativeTo(null);
        refresh();
    }

    private void handleMove(int index) {
        Board.Player mover = board.getCurrent();
        if (!board.play(index)) return;

        // Update the score exactly once, when the game ends
        if (board.hasWinner()) {
            if (mover == Board.Player.X) xWins++; else oWins++;
        } else if (board.isFull()) {
            draws++;
        }
        refresh();
    }

    /** Redraws everything from the Board's state. */
    private void refresh() {
        Color defaultBg = UIManager.getColor("Button.background");
        for (int i = 0; i < 9; i++) {
            Board.Player p = board.get(i);
            buttons[i].setText(p == null ? "" : p.name());
            buttons[i].setForeground(p == Board.Player.X ? X_COLOR : O_COLOR);
            buttons[i].setBackground(defaultBg);
        }

        if (board.hasWinner()) {
            for (int i : board.getWinLine()) buttons[i].setBackground(WIN_COLOR);
            status.setText(board.get(board.getWinLine()[0]) + " wins!");
        } else if (board.isFull()) {
            status.setText("It's a draw!");
        } else {
            status.setText(board.getCurrent() + "'s turn");
        }

        scoreLabel.setText("X: " + xWins + "   O: " + oWins + "   Draws: " + draws);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe().setVisible(true));
    }
}
