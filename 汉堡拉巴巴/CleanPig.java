import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CleanPig {

    private static final int SIZE = 8; // 游戏板大小
    private static final int MINES = 8; // 地雷数量
    private static JButton[][] buttons = new JButton[SIZE][SIZE];
    private static boolean[][] mines = new boolean[SIZE][SIZE];
    private static boolean[][] revealed = new boolean[SIZE][SIZE];

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("扫猪猪");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setLayout(new GridLayout(SIZE, SIZE));

            // 新增用户头像
            ImageIcon pigIcon = new ImageIcon("pig.png");
            frame.setIconImage(pigIcon.getImage());

            initializeBoard();
            placeMines();

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    JButton button = new JButton();
                    button.setPreferredSize(new Dimension(40, 40));
                    button.addActionListener(new ButtonClickListener(i, j, frame));
                    buttons[i][j] = button;
                    frame.add(button);
                }
            }

            frame.setVisible(true);
        });
    }

    private static void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                mines[i][j] = false;
                revealed[i][j] = false;
            }
        }
    }

    private static void placeMines() {
        Random rand = new Random();
        int minesPlaced = 0;

        while (minesPlaced < MINES) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);

            if (!mines[row][col]) {
                mines[row][col] = true;
                minesPlaced++;
            }
        }
    }

    private static class ButtonClickListener implements ActionListener {
        private final int row;
        private final int col;
        private final JFrame frame;

        public ButtonClickListener(int row, int col, JFrame frame) {
            this.row = row;
            this.col = col;
            this.frame = frame;

            
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();

            if (!revealed[row][col]) {
                revealed[row][col] = true;

                if (mines[row][col]) {
                    // 设置雷的图片大小
                    ImageIcon zhangIcon = new ImageIcon("zhang.png");
                    Image zhangImage = zhangIcon.getImage();
                    Image scaledZhangImage = zhangImage.getScaledInstance(40, 40, Image.SCALE_AREA_AVERAGING);
                    button.setIcon(new ImageIcon(scaledZhangImage));

                    // 显示"Game Over"图片
                    showGameOverPopup();

                } else {
                    int adjacentMines = countAdjacentMines(row, col);
                    if (adjacentMines > 0) {
                        button.setText(Integer.toString(adjacentMines));
                    } else {
                        revealEmptyCells(row, col);
                    }
                }
            }

            button.setEnabled(false);

            if (checkWin()) {
                JOptionPane.showMessageDialog(null, "小猪没有贪吃！小猪保持了好猪材！");
                resetGame();
            }
        }

        private int countAdjacentMines(int row, int col) {
            int count = 0;

            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < SIZE && j >= 0 && j < SIZE && mines[i][j]) {
                        count++;
                    }
                }
            }

            return count;
        }

        private void revealEmptyCells(int row, int col) {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < SIZE && j >= 0 && j < SIZE && !revealed[i][j]) {
                        revealed[i][j] = true;
                        int adjacentMines = countAdjacentMines(i, j);
                        buttons[i][j].setEnabled(false);
                        if (adjacentMines > 0) {
                            buttons[i][j].setText(Integer.toString(adjacentMines));
                        } else {
                            revealEmptyCells(i, j);
                        }
                    }
                }
            }
        }

        private boolean checkWin() {
            int revealedCount = 0;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (revealed[i][j]) {
                        revealedCount++;
                    }
                }
            }
            return revealedCount == SIZE * SIZE - MINES;
        }

        private void resetGame() {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    buttons[i][j].setEnabled(true);
                    buttons[i][j].setText("");
                    buttons[i][j].setIcon(null);
                    revealed[i][j] = false;
                    mines[i][j] = false;
                }
            }

            placeMines();
        }

        //用于弹出图片
        private void showGameOverPopup() {
            // 设置"Game Over"图片的大小
            ImageIcon fatIcon = new ImageIcon("fat.jpg");
            Image fatImage = fatIcon.getImage();
            Image scaledFatImage = fatImage.getScaledInstance(400, 400, Image.SCALE_AREA_AVERAGING);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledFatImage));

            // 创建一个新的JFrame显示"Game Over"图片
            JFrame gameOverFrame = new JFrame("Game Over");
            gameOverFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            gameOverFrame.add(imageLabel);
            gameOverFrame.setSize(new Dimension(scaledFatImage.getWidth(null), scaledFatImage.getHeight(null)));

            // 设置位置在游戏页面旁边
            Point gameLocation = frame.getLocation();
            gameOverFrame.setLocation((int) gameLocation.getX() + frame.getWidth(), (int) gameLocation.getY());

            // 添加窗口关闭监听器，用于在用户关闭窗口时重新开始游戏
            gameOverFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    resetGame();
                }
            });

            gameOverFrame.setVisible(true);
        }
    }
}
