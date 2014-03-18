import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class LogicBombBoard {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel board;

    private JLabel timeElapsed;
    private JLabel minesRemaining;

    private int width;
    private int height;
    private int mines;
    private int minesFlagged;
    private int totalCellsCleared;
    private boolean gameStarted;
    private long startedAt;
    private Timer timer;
    MineCell[] mineCells;

    public LogicBombBoard(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;
        this.minesFlagged = 0;
        totalCellsCleared = 0;
        mineCells = new MineCell[width*height];
        gameStarted = false;

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted) {
                    long elapsedTime = System.currentTimeMillis() - startedAt;
                    timeElapsed.setText(String.valueOf((int) elapsedTime/1000));
                }
            }
        });
        timer.start();

        layoutMainPanel();
        makeMainFrame();
    }

    public void makeMainFrame() {
        mainFrame = new JFrame("Logic Bomb");
        mainFrame.getContentPane().add(mainPanel);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        mainFrame.setJMenuBar(makeMenu());
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

    }

    public JMenuBar makeMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);

        menuBar.add(gameMenu);

        JMenuItem changeSize = new JMenuItem("Change board",
                KeyEvent.VK_C);
        changeSize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        changeSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JDialog dialog = new JDialog();
                dialog.setTitle("Change board");
                JPanel dialogPanel = new JPanel();
                dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS));

                dialog.setModal(true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                JPanel p1 = new JPanel();
                final JTextField widthField = new JTextField(String.valueOf(width));
                widthField.setPreferredSize(new Dimension(100, 20));
                p1.add(new JLabel("Width: "));
                p1.add(widthField);
                dialogPanel.add(p1);

                JPanel p2 = new JPanel();
                final JTextField heightField = new JTextField(String.valueOf(height));
                heightField.setPreferredSize(new Dimension(100, 20));
                p2.add(new JLabel("Height: "));
                p2.add(heightField);
                dialogPanel.add(p2);

                JPanel p3 = new JPanel();
                final JTextField minesField = new JTextField(String.valueOf(mines));
                minesField.setPreferredSize(new Dimension(100, 20));
                p3.add(new JLabel("Mines: "));
                p3.add(minesField);
                dialogPanel.add(p3);

                JPanel p4 = new JPanel();
                JButton okButton = new JButton("Change");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        resetBoard(Integer.parseInt(widthField.getText()),
                                   Integer.parseInt(heightField.getText()),
                                   Integer.parseInt(minesField.getText()));
                        dialog.dispose();
                    }
                });
                p4.add(Box.createHorizontalGlue());
                p4.add(okButton);
                p4.add(Box.createHorizontalGlue());
                dialogPanel.add(p4);

                dialog.setContentPane(dialogPanel);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        gameMenu.add(changeSize);

        return menuBar;
    }

    // Implementing Fisher–Yates shuffle
    private void shuffleArray(boolean[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            boolean a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    private boolean[] makeMineField(int width, int height, int mines) {
        boolean[] field = new boolean[width*height];
        for (int i = 0; i < width*height; i++) {
            if (i < mines) {
                field[i] = true;
            }
            else {
                field[i] = false;
            }
        }
        shuffleArray(field);
        return field;
    }

    private int rowColToIndex(int row, int col, int width) {
        return row*width+col;
    }

    public int getMinesRemaining() {
        return mines - minesFlagged;
    }

    private void flagCell(int i) {
        if (!gameStarted) {
            gameStarted = true;
            startedAt = System.currentTimeMillis();
        }

        MineCell cell = mineCells[i];
        if (cell.isFlagged()) {
            cell.unflag();
            minesFlagged--;
        }
        else if (getMinesRemaining() > 0 && cell.isEnabled()) {
            cell.flag();
            minesFlagged++;
        }
        minesRemaining.setText(String.valueOf(getMinesRemaining()));
    }

    private void activateCell(int i) {
        if (!gameStarted) {
            gameStarted = true;
            startedAt = System.currentTimeMillis();
        }

        MineCell cell = mineCells[i];
        int cellsCleared;
        cellsCleared = cell.activateCell();
        if (cellsCleared == -1)
            gameOver();
        else {
            totalCellsCleared += cellsCleared;
//            System.out.println(cellsCleared + " " + totalCellsCleared);
            if (totalCellsCleared == width*height-mines)
                gameWon();
        }


    }

    private void activateAdjacentCells(int i) {
        if (!gameStarted) {
            gameStarted = true;
            startedAt = System.currentTimeMillis();
        }

        MineCell cell = mineCells[i];
        int cellsCleared;
        cellsCleared = cell.activateAdjacentCells();
        if (cellsCleared == -1)
            gameOver();
        else {
            totalCellsCleared += cellsCleared;
//            System.out.println(cellsCleared + " " + totalCellsCleared);
            if (totalCellsCleared == width*height-mines)
                gameWon();
        }
    }

    private void gameOver() {
        System.out.println("Game over");
        gameStarted = false;
        JOptionPane.showMessageDialog(mainFrame,
                "Sorry, game over.",
                "Game over",
                JOptionPane.PLAIN_MESSAGE);
        resetBoard(width, height, mines);
    }

    private void gameWon() {
        System.out.println("Game won");
        long elapsedTime = System.currentTimeMillis() - startedAt;
        gameStarted = false;
        JOptionPane.showMessageDialog(mainFrame,
                    "Congratulations! You won in " + String.valueOf((int) elapsedTime/1000) + " seconds.",
                    "Congratulations!",
                    JOptionPane.PLAIN_MESSAGE);
        resetBoard(width, height, mines);
    }

    private void resetBoard(int w, int h, int m) {
        width = w;
        height = h;
        mines = m;
        mainFrame.getContentPane().removeAll();
        minesFlagged = 0;
        totalCellsCleared = 0;
        mineCells = new MineCell[width*height];
        gameStarted = false;

        layoutMainPanel();
        mainFrame.add(mainPanel);
        mainFrame.pack();
    }

    private void layoutMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        board = new JPanel();
        board.setLayout(new GridLayout(this.height, this.width));
        boolean[] mineField = makeMineField(width, height, mines);

        UIManager.put("Button.disabledText", Color.BLACK);

        for (int i = 0; i < this.height*this.width; i++) {
            MineCell b = new MineCell();
            b.setIsMine(mineField[i]);
            b.setMinimumSize(new Dimension(25, 25));
            b.setPreferredSize(new Dimension(25, 25));
            final int index = i;
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                        activateCell(index);
                    }
                    else if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                        flagCell(index);
                    }
                    else if (e.getClickCount() == 2) {
                        activateAdjacentCells(index);
                    }
                }
            });

            board.add(b);
            mineCells[i] = b;
        }

        for (int i = 0; i < this.height*this.width; i++) {
            int col = i % width;
            int row = (i - col) / width;

            int c, r;
            if (col > 0) {
                // Add cell to the left
                c = col - 1;
                r = row;
                mineCells[i].addAdjacentCell(mineCells[r*width+c]);

                if (row > 0) {
                    // Add cell to the left-top
                    r = row - 1;
                    mineCells[i].addAdjacentCell(mineCells[r*width+c]);
                }
                if (row < height-1) {
                    // Add cell to the left-bottom
                    r = row + 1;
                    mineCells[i].addAdjacentCell(mineCells[r*width+c]);
                }
            }

            if (col < width-1) {
                // Add cell to the right
                c = col + 1;
                r = row;
                mineCells[i].addAdjacentCell(mineCells[r*width+c]);

                if (row > 0) {
                    // Add cell to the right-top
                    r = row - 1;
                    mineCells[i].addAdjacentCell(mineCells[r*width+c]);
                }
                if (row < height-1) {
                    // Add cell to the right-bottom
                    r = row + 1;
                    mineCells[i].addAdjacentCell(mineCells[r*width+c]);
                }
            }
            if (row > 0) {
                // Add cell to the top
                c = col;
                r = row - 1;
                mineCells[i].addAdjacentCell(mineCells[r*width+c]);
            }
            if (row < height-1) {
                // Add cell to the bottom
                c = col;
                r = row + 1;
                mineCells[i].addAdjacentCell(mineCells[r*width+c]);
            }
        }


        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BorderLayout());
        statsPanel.add(Box.createRigidArea(new Dimension(50, 30)), BorderLayout.WEST);

        JPanel centerStats = new JPanel(new BorderLayout());

        JPanel timePanel = new JPanel(new BorderLayout());
        timeElapsed = new JLabel("0");
        timePanel.add(timeElapsed, BorderLayout.WEST);
        timePanel.add(new JLabel(" Seconds"), BorderLayout.EAST);

        JPanel minesPanel = new JPanel(new BorderLayout());
        minesRemaining = new JLabel(String.valueOf(this.mines));
        minesPanel.add(minesRemaining, BorderLayout.WEST);
        minesPanel.add(new JLabel(" Mines"), BorderLayout.EAST);

        centerStats.add(timePanel, BorderLayout.WEST);
        centerStats.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        centerStats.add(minesPanel, BorderLayout.EAST);

        statsPanel.add(centerStats, BorderLayout.CENTER);
        statsPanel.add(Box.createRigidArea(new Dimension(50, 30)), BorderLayout.EAST);

        mainPanel.add(statsPanel);
        mainPanel.add(board, BorderLayout.NORTH);

    }

}
