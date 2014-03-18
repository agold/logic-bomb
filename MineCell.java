import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class MineCell extends JButton {
    private boolean isMine;
    private boolean flagged;
    private Vector<MineCell> adjacentCells;

    public MineCell() {
        super();
        setText("");
        setBackground(new Color(23,81,232));
        setForeground(Color.RED);
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, 16));
        setMargin(new Insets(0, 0, 0, 0));
        isMine = false;
        flagged = false;
        adjacentCells = new Vector<MineCell>();
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean m) {
        isMine = m;
        if (isMine) {
            //setText("x");
        }
    }

    public void addAdjacentCell(MineCell c) {
        adjacentCells.add(c);
    }

    public int adjacentMines() {
        int count = 0;
        for (MineCell c : adjacentCells) {
            if (c.isMine()) {
                count++;
            }
        }
        return count;
    }

    public int activateCell() {
        int cellsCleared = 0;
        if (!flagged && isEnabled()) {
            setEnabled(false);
            setBackground(Color.LIGHT_GRAY);

            if (isMine) {
                setText("X");
                return -1;
            }
            else {
                cellsCleared++;
                if (adjacentMines() > 0) {
                    setText(String.valueOf(adjacentMines()));
                }
                else {
                    for (MineCell c : adjacentCells) {
                        if (c.isEnabled()) {
                            int tmp = c.activateCell();
                            if (tmp == -1)
                                return -1;
                            else
                                cellsCleared += tmp;
                        }
                    }
                }
            }
        }
        return cellsCleared;
    }

    public int activateAdjacentCells() {
        int numFlagged = 0;
        int cellsCleared = 0;
        for (MineCell c : adjacentCells) {
            if (c.isFlagged()) {
                numFlagged++;
            }
        }
        if (numFlagged == adjacentMines()) {
            for (MineCell c : adjacentCells) {
                if (!c.isFlagged() && c.isEnabled()) {
                    int tmp = c.activateCell();
                    if (tmp == -1)
                        return -1;
                    else
                        cellsCleared += tmp;
                }
            }
        }
        return cellsCleared;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void flag() {
        setText("O");
        flagged = true;
    }

    public void unflag() {
        setText("");
        flagged = false;
    }
}
