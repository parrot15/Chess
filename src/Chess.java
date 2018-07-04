import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Chess extends JFrame {
    private static final long serialVersionUID = 1L;

    private final BufferedImage whitePawnGUI = ImageIO.read(new File("img/WhitePawn.png"));
    private final BufferedImage whiteKnightGUI = ImageIO.read(new File("img/WhiteKnight.png"));
    private final BufferedImage whiteBishopGUI = ImageIO.read(new File("img/WhiteBishop.png"));
    private final BufferedImage whiteRookGUI = ImageIO.read(new File("img/WhiteRook.png"));
    private final BufferedImage whiteQueenGUI = ImageIO.read(new File("img/WhiteQueen.png"));
    private final BufferedImage whiteKingGUI = ImageIO.read(new File("img/WhiteKing.png"));
    private final BufferedImage blackPawnGUI = ImageIO.read(new File("img/BlackPawn.png"));
    private final BufferedImage blackKnightGUI = ImageIO.read(new File("img/BlackKnight.png"));
    private final BufferedImage blackBishopGUI = ImageIO.read(new File("img/BlackBishop.png"));
    private final BufferedImage blackRookGUI = ImageIO.read(new File("img/BlackRook.png"));
    private final BufferedImage blackQueenGUI = ImageIO.read(new File("img/BlackQueen.png"));
    private final BufferedImage blackKingGUI = ImageIO.read(new File("img/BlackKing.png"));

    private JPanel[][] boardTiles = new JPanel[8][8];
    private String[][] piecePositions = new String[8][8];
    private char[][] movableTilesForWhiteKing = new char[8][8];  // x for tiles king can't move to, k for king tile
    private char[][] movableTilesForBlackKing = new char[8][8];

    private String lastSelectedPiece = null;
    private int lastSelectedRow = 0;
    private int lastSelectedCol = 0;

    // if isWhiteTurn is true then it's white's turn, if it's false then it's black's turn
//	private boolean isWhiteTurn = true;
    private boolean isWhiteTurn = true;

    private ArrayList<Move> allLegalMoves = new ArrayList<Move>();

    private boolean hasWhiteKingMoved = false;
    private boolean hasWhiteLeftRookMoved = false;
    private boolean hasWhiteRightRookMoved = false;
    private boolean hasBlackKingMoved = false;
    private boolean hasBlackLeftRookMoved = false;
    private boolean hasBlackRightRookMoved = false;

    public Chess() throws IOException {
        setTitle("Chess");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(128, 70, 27));  // darker brown
        JPanel basePanel = new JPanel();
        basePanel.setBackground(new Color(128, 70, 27));
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(128, 70, 27));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JPanel topLeftBufferPanel = new JPanel();
        topLeftBufferPanel.setBackground(new Color(128, 70, 27));
        topLeftBufferPanel.setPreferredSize(new Dimension(25, 25));
        topPanel.add(topLeftBufferPanel);

        JPanel scorePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(245, 245, 175));  // yellowish brown
                String text = isWhiteTurn ? "White's turn" : "Black's turn";
                g2d.drawString(text, (getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2,
                        (getHeight() - g2d.getFontMetrics().getHeight()) / 2 + g2d.getFontMetrics().getAscent());
            }
        };
        scorePanel.setBackground(new Color(128, 70, 27));
        scorePanel.setPreferredSize(new Dimension(620, 25));
        topPanel.add(scorePanel);

        JPanel topRightBufferPanel = new JPanel();
        topRightBufferPanel.setBackground(new Color(128, 70, 27));
        topRightBufferPanel.setPreferredSize(new Dimension(25, 25));
        topPanel.add(topRightBufferPanel);

        basePanel.add(topPanel);

        JPanel midPanel = new JPanel();
        midPanel.setBackground(new Color(128, 70, 27));
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));

        JPanel leftYAxisPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(245, 245, 175));  // yellowish brown
                for (int i = 0; i < 8; i++) {
                    String text = "87654321".substring(i, i + 1);
                    g2d.drawString(text, (getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2,
                            (boardTiles[0][0].getHeight() - g2d.getFontMetrics().getHeight()) / 2 +
                                    g2d.getFontMetrics().getAscent() + i * boardTiles[0][0].getHeight());
                }
            }
        };
        leftYAxisPanel.setBackground(new Color(128, 70, 27));
        leftYAxisPanel.setPreferredSize(new Dimension(25, 620));
        midPanel.add(leftYAxisPanel);

        JPanel gamePanel = new JPanel();
        gamePanel.setBackground(new Color(128, 70, 27));
        gamePanel.setLayout(new GridLayout(8, 8));
        gamePanel.setPreferredSize(new Dimension(620, 620));

//        setChessBoard();
        setTestingChessBoard();
//		printChessBoard();

        for (int row = 0; row < boardTiles.length; row++) {
            for (int col = 0; col < boardTiles[row].length; col++) {
                final int tempRow = row;
                final int tempCol = col;
                boardTiles[row][col] = new JPanel() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        if (tempRow % 2 == 0) {
                            if (tempCol % 2 == 0) {
                                g2d.setColor(new Color(240, 217, 181));  // light brown
                            } else if (tempCol % 2 != 0) {
                                g2d.setColor(new Color(181, 136, 99));  // dark brown
                            }
                        } else if (tempRow % 2 != 0) {
                            if (tempCol % 2 != 0) {
                                g2d.setColor(new Color(240, 217, 181));  // light brown
                            } else if (tempCol % 2 == 0) {
                                g2d.setColor(new Color(181, 136, 99));  // dark brown
                            }
                        }
                        BufferedImage img = null;
                        if ("wp".equals(piecePositions[tempRow][tempCol])) {
                            img = whitePawnGUI;
                        } else if ("wn".equals(piecePositions[tempRow][tempCol])) {
                            img = whiteKnightGUI;
                        } else if ("wb".equals(piecePositions[tempRow][tempCol])) {
                            img = whiteBishopGUI;
                        } else if ("wr".equals(piecePositions[tempRow][tempCol])) {
                            img = whiteRookGUI;
                        } else if ("wq".equals(piecePositions[tempRow][tempCol])) {
                            img = whiteQueenGUI;
                        } else if ("wk".equals(piecePositions[tempRow][tempCol])) {
                            img = whiteKingGUI;
                        } else if ("bp".equals(piecePositions[tempRow][tempCol])) {
                            img = blackPawnGUI;
                        } else if ("bn".equals(piecePositions[tempRow][tempCol])) {
                            img = blackKnightGUI;
                        } else if ("bb".equals(piecePositions[tempRow][tempCol])) {
                            img = blackBishopGUI;
                        } else if ("br".equals(piecePositions[tempRow][tempCol])) {
                            img = blackRookGUI;
                        } else if ("bq".equals(piecePositions[tempRow][tempCol])) {
                            img = blackQueenGUI;
                        } else if ("bk".equals(piecePositions[tempRow][tempCol])) {
                            img = blackKingGUI;
                        }
                        if (img != null) {
                            double scale = Math.min((double) boardTiles[tempRow][tempCol].getWidth() / img.getWidth(),
                                    (double) boardTiles[tempRow][tempCol].getHeight() / img.getHeight());
                            double xPos = (boardTiles[tempRow][tempCol].getWidth() - img.getWidth()) / 2;
                            double yPos = (boardTiles[tempRow][tempCol].getHeight() - img.getHeight()) / 2;
                            if (img.getMinX() > (boardTiles[tempRow][tempCol].getWidth() - img.getWidth()) / 2 ||
                                    img.getMinY() > (boardTiles[tempRow][tempCol].getHeight() - img.getHeight()) / 2) {
                                xPos = (boardTiles[tempRow][tempCol].getWidth() - (scale * img.getWidth())) / 2;
                                yPos = (boardTiles[tempRow][tempCol].getHeight() - (scale * img.getHeight())) / 2;
                            }
                            AffineTransform fix = AffineTransform.getTranslateInstance(xPos, yPos);
                            fix.scale(scale, scale);
                            g2d.drawRenderedImage(img, fix);
                        }
                    }
                };
                if (row % 2 == 0) {
                    if (col % 2 == 0) {
                        boardTiles[row][col].setBackground(new Color(240, 217, 181));  // light brown
                        boardTiles[row][col].repaint();
                    } else if (col % 2 != 0) {
                        boardTiles[row][col].setBackground(new Color(181, 136, 99));  // dark brown
                        boardTiles[row][col].repaint();
                    }
                } else if (row % 2 != 0) {
                    if (col % 2 != 0) {
                        boardTiles[row][col].setBackground(new Color(240, 217, 181));  // light brown
                        boardTiles[row][col].repaint();
                    } else if (col % 2 == 0) {
                        boardTiles[row][col].setBackground(new Color(181, 136, 99));  // dark brown
                        boardTiles[row][col].repaint();
                    }
                }

                boardTiles[row][col].setLayout(new BorderLayout());
                boardTiles[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        System.out.println("row: " + tempRow + ", col: " + tempCol);
//						if ("wp".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("White pawn");
//							lastSelectedPiece = "wp";
//						} else if ("wn".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("White knight");
//							lastSelectedPiece = "wn";
//						} else if ("wb".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("White bishop");
//							lastSelectedPiece = "wb";
//						} else if ("wr".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("White rook");
//							lastSelectedPiece = "wr";
//						} else if ("wq".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("White queen");
//							lastSelectedPiece = "wq";
//						} else if ("wk".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("White king");
//							lastSelectedPiece = "wk";
//						} else if ("bp".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("Black pawn");
//							lastSelectedPiece = "bp";
//						} else if ("bn".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("Black knight");
//							lastSelectedPiece = "bn";
//						} else if ("bb".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("Black bishop");
//							lastSelectedPiece = "bb";
//						} else if ("br".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("Black rook");
//							lastSelectedPiece = "br";
//						} else if ("bq".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("Black queen");
//							lastSelectedPiece = "bq";
//						} else if ("bk".equals(piecePositions[tempRow][tempCol])) {
//							System.out.println("Black king");
//							lastSelectedPiece = "bk";
//						}

//						isWhiteTurn = !isWhiteTurn;
                        System.out.println(isWhiteTurn);
                        // maybe put this shit at the bottom

                        System.out.println("Last selected piece: " + lastSelectedPiece);
                        if (/*piecePositions[tempRow][tempCol] == null &&*/ lastSelectedPiece != null) {
                            if ((isWhiteTurn && lastSelectedPiece.charAt(0) == 'w') || (!isWhiteTurn &&
                                    lastSelectedPiece.charAt(0) == 'b')) {
                                System.out.println("Piece: " + lastSelectedPiece);
                                System.out.println("Current position: " + lastSelectedCol + ", " + lastSelectedRow);
                                System.out.println("Desired position: " + tempCol + ", " + tempRow);
//								System.out.println("Is move legal: " + isLegalMove(lastSelectedPiece, lastSelectedCol,
//										lastSelectedRow, tempCol, tempRow));
                                if (piecePositions[tempRow][tempCol] == null) {
                                    // moving piece to blank tile or tile occupied by ally
                                    System.out.println("Moving activated");
                                    if (isLegalMove(lastSelectedPiece, lastSelectedCol, lastSelectedRow, tempCol,
                                            tempRow)) {
                                        allLegalMoves.add(new Move(lastSelectedCol, lastSelectedRow, tempCol,
                                                tempRow));
                                        piecePositions[lastSelectedRow][lastSelectedCol] = null;
                                        piecePositions[tempRow][tempCol] = lastSelectedPiece;
                                        if (lastSelectedPiece.charAt(1) == 'p' && tempRow == 0) {
                                            System.out.println("Pawn transformation activated");
                                            choosePawnTransformation(lastSelectedPiece, tempRow, tempCol);
                                        }
                                        isWhiteTurn = !isWhiteTurn;
//										lastSelectedPiece = null;
                                        flipChessBoard();
                                    }
                                    lastSelectedPiece = null;
                                } else if (piecePositions[lastSelectedRow][lastSelectedCol].charAt(0) !=
                                        piecePositions[tempRow][tempCol].charAt(0)) {
                                    // moving piece to enemy tile (capturing)
                                    if (lastSelectedPiece.charAt(1) == 'p' && ((lastSelectedRow == tempRow + 1 &&
                                            lastSelectedCol == tempCol + 1) || (lastSelectedRow == tempRow + 1 &&
                                            lastSelectedCol == tempCol - 1))) {
                                        System.out.println("Pawn capturing activated");
                                        piecePositions[tempRow][tempCol] = lastSelectedPiece;
                                        piecePositions[lastSelectedRow][lastSelectedCol] = null;
                                        isWhiteTurn = !isWhiteTurn;
                                        lastSelectedPiece = null;
                                        flipChessBoard();
                                    } else if (lastSelectedPiece.charAt(1) == 'n' || lastSelectedPiece.charAt(1) == 'b'
                                            || lastSelectedPiece.charAt(1) == 'r' || lastSelectedPiece.charAt(1) == 'q'
                                            || lastSelectedPiece.charAt(1) == 'k') {
                                        if (isLegalMove(lastSelectedPiece, lastSelectedCol, lastSelectedRow, tempCol,
                                                tempRow)) {
                                            allLegalMoves.add(new Move(lastSelectedCol, lastSelectedRow, tempCol,
                                                    tempRow));
                                            System.out.println("Normal capturing activated");
                                            piecePositions[tempRow][tempCol] = lastSelectedPiece;
                                            piecePositions[lastSelectedRow][lastSelectedCol] = null;
                                            isWhiteTurn = !isWhiteTurn;
                                            lastSelectedPiece = null;
                                            flipChessBoard();
                                        }
                                    }
                                }
                            }
                        }
                        lastSelectedRow = tempRow;
                        lastSelectedCol = tempCol;
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException ex) {
//							ex.printStackTrace();
//						}
                        printChessBoard();
                        repaint();

                        if ("wp".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("White pawn");
                            lastSelectedPiece = "wp";
                        } else if ("wn".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("White knight");
                            lastSelectedPiece = "wn";
                        } else if ("wb".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("White bishop");
                            lastSelectedPiece = "wb";
                        } else if ("wr".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("White rook");
                            lastSelectedPiece = "wr";
                        } else if ("wq".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("White queen");
                            lastSelectedPiece = "wq";
                        } else if ("wk".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("White king");
                            lastSelectedPiece = "wk";
                        } else if ("bp".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("Black pawn");
                            lastSelectedPiece = "bp";
                        } else if ("bn".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("Black knight");
                            lastSelectedPiece = "bn";
                        } else if ("bb".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("Black bishop");
                            lastSelectedPiece = "bb";
                        } else if ("br".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("Black rook");
                            lastSelectedPiece = "br";
                        } else if ("bq".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("Black queen");
                            lastSelectedPiece = "bq";
                        } else if ("bk".equals(piecePositions[tempRow][tempCol])) {
                            System.out.println("Black king");
                            lastSelectedPiece = "bk";
                        }
                        // white and black king blocked and attacking tiles
                        movableTilesForWhiteKing = new char[8][8];
                        movableTilesForBlackKing = new char[8][8];
                        for (int i = 0; i < piecePositions.length; i++) {
                            for (int ii = 0; ii < piecePositions[i].length; ii++) {
                                if (piecePositions[i][ii] != null) {
                                    if (piecePositions[i][ii].charAt(1) != 'k') {
                                        if (isWhiteTurn && piecePositions[i][ii].charAt(0) == 'b') {
                                            movableTilesForWhiteKing[i][ii] = 'x';
//                                        movableTilesForBlackKing[i][ii] = 'x';
                                        } else if (!isWhiteTurn && piecePositions[i][ii].charAt(0) == 'w') {
//                                        movableTilesForWhiteKing[i][ii] = 'x';
                                            movableTilesForBlackKing[i][ii] = 'x';
                                        }
                                    } else {
                                        movableTilesForWhiteKing[i][ii] = 'x';
                                        movableTilesForBlackKing[i][ii] = 'x';
                                    }
                                }

                                // not working properly when pieces are captured
//                                if (piecePositions[i][ii] != null) {
//                                    if ((isWhiteTurn && piecePositions[i][ii].charAt(0) == 'b') ||
//                                            (!isWhiteTurn && piecePositions[i][ii].charAt(0) == 'w')) {
//                                        fillAttackedTiles(piecePositions[i][ii], i, ii);
//                                    }
//                                }
                                fillAttackedTiles(piecePositions[i][ii], i, ii);
//                                printMovableTilesForKings();
                            }
                        }
                        printMovableTilesForKings();
                        for (int i = 0; i < piecePositions.length; i++) {
                            for (int ii = 0; ii < piecePositions[i].length; ii++) {
                                if (piecePositions[i][ii] != null) {
                                    if (piecePositions[i][ii].charAt(1) == 'k' && ((isWhiteTurn &&
                                            piecePositions[i][ii].charAt(0) == 'w') || (!isWhiteTurn &&
                                            piecePositions[i][ii].charAt(0) == 'b'))) {
                                        System.out.println("Is " + (isWhiteTurn ? "white king" : "black king") +
                                                " under check: " + isKingUnderCheck(i, ii));
                                    }
                                    if (piecePositions[i][ii].charAt(1) == 'k' && ((isWhiteTurn &&
                                            piecePositions[i][ii].charAt(0) == 'w') || (!isWhiteTurn &&
                                            piecePositions[i][ii].charAt(0) == 'b'))) {
                                        System.out.println("Is " + (isWhiteTurn ? "white king" : "black king") +
                                                " in checkmate: " + isKingInCheckmate(i, ii));
                                    }
                                }
                            }
                        }
                        System.out.println("---------------------------------------------\n");
                    }
                });
            }
        }

        for (int row = 0; row < boardTiles.length; row++) {
            for (int col = 0; col < boardTiles[row].length/* col < boardTiles.length */; col++) {
                gamePanel.add(boardTiles[row][col]);
            }
        }
        midPanel.add(gamePanel);

        JPanel rightYAxisPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(245, 245, 175));  // yellowish brown
                for (int i = 0; i < 8; i++) {
                    String text = "87654321".substring(i, i + 1);
                    g2d.drawString(text, (getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2,
                            (boardTiles[0][0].getHeight() - g2d.getFontMetrics().getHeight()) / 2 +
                                g2d.getFontMetrics().getAscent() + i * boardTiles[0][0].getHeight());
                }
            }
        };
        rightYAxisPanel.setBackground(new Color(128, 70, 27));
        rightYAxisPanel.setPreferredSize(new Dimension(25, 620));
        midPanel.add(rightYAxisPanel);

        basePanel.add(midPanel);

        JPanel botPanel = new JPanel();
        botPanel.setBackground(new Color(128, 70, 27));
        botPanel.setLayout(new BoxLayout(botPanel, BoxLayout.X_AXIS));

        JPanel botLeftBufferPanel = new JPanel();
        botLeftBufferPanel.setBackground(new Color(128, 70, 27));
        botLeftBufferPanel.setPreferredSize(new Dimension(25, 25));
        botPanel.add(botLeftBufferPanel);

        JPanel xAxisPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(245, 245, 175));  // yellowish brown
                for (int i = 0; i < 8; i++) {
                    String text = "abcdefgh".substring(i, i + 1);
                    g2d.drawString(text, (boardTiles[0][0].getWidth() - g2d.getFontMetrics().stringWidth(text)) / 2
                            + i * boardTiles[0][0].getWidth(), (getHeight() - g2d.getFontMetrics().getHeight()) / 2
                            + g2d.getFontMetrics().getAscent());
                }
            }
        };
        xAxisPanel.setBackground(new Color(128, 70, 27));
        xAxisPanel.setPreferredSize(new Dimension(620, 25));
        botPanel.add(xAxisPanel);

        JPanel botRightBufferPanel = new JPanel();
        botRightBufferPanel.setBackground(new Color(128, 70, 27));
        botRightBufferPanel.setPreferredSize(new Dimension(25, 25));
        botPanel.add(botRightBufferPanel);

        basePanel.add(botPanel);

        getContentPane().add(basePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Chess();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void fillAttackedTiles(String piece, int currentPosX, int currentPosY) {
//        System.out.println("ACTIVATED");
        if (piece != null) {
            if (piece.charAt(0) == 'w' && !isWhiteTurn) {  // white
                if (piece.charAt(1) == 'p') {  // pawn
                    char[][] wrappedMovableTilesForBlackKing =
                            new char[movableTilesForBlackKing.length + 2][movableTilesForBlackKing.length + 2];
                    for (int row = 0; row < wrappedMovableTilesForBlackKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForBlackKing.length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForBlackKing.length - 2 &&
                                    col <= wrappedMovableTilesForBlackKing.length - 2) {
                                wrappedMovableTilesForBlackKing[row][col] = movableTilesForBlackKing[row - 1][col - 1];
                            }
                        }
                    }
                    wrappedMovableTilesForBlackKing[currentPosX + 1 + 1][currentPosY - 1 + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 1 + 1][currentPosY + 1 + 1] = 'x';

//                    printMatrix(wrappedMovableTilesForBlackKing);

                    movableTilesForBlackKing = new char[8][8];
                    for (int row = 0; row < wrappedMovableTilesForBlackKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForBlackKing[row].length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForBlackKing.length - 2 &&
                                    col <= wrappedMovableTilesForBlackKing.length - 2) {
                                movableTilesForBlackKing[row - 1][col - 1] = wrappedMovableTilesForBlackKing[row][col];
//                                printMovableTilesForKings();
                            }
                        }
                    }
                } else if (piece.charAt(1) == 'n') {  // knight
                    char[][] wrappedMovableTilesForBlackKing =
                            new char[movableTilesForBlackKing.length + 4][movableTilesForBlackKing.length + 4];
                    for (int row = 0; row < wrappedMovableTilesForBlackKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForBlackKing.length; col++) {
                            if (row >= 2 && col >= 2 && row <= wrappedMovableTilesForBlackKing.length - 3 &&
                                    col <= wrappedMovableTilesForBlackKing.length - 3) {
                                wrappedMovableTilesForBlackKing[row][col] = movableTilesForBlackKing[row - 2][col - 2];
                            }
                        }
                    }
                    wrappedMovableTilesForBlackKing[currentPosX + 1 + 2][currentPosY - 2 + 2] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 2 + 2][currentPosY - 1 + 2] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 2 + 2][currentPosY + 1 + 2] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 1 + 2][currentPosY + 2 + 2] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX - 1 + 2][currentPosY + 2 + 2] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX - 2 + 2][currentPosY + 1 + 2] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX - 2 + 2][currentPosY - 1 + 2] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX - 1 + 2][currentPosY - 2 + 2] = 'x';

//                    printMatrix(wrappedMovableTilesForBlackKing);
                    movableTilesForBlackKing = new char[8][8];
                    for (int row = 0; row < wrappedMovableTilesForBlackKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForBlackKing[row].length; col++) {
                            if (row >= 2 && col >= 2 && row <= wrappedMovableTilesForBlackKing.length - 3 &&
                                    col <= wrappedMovableTilesForBlackKing.length - 3) {
                                movableTilesForBlackKing[row - 2][col - 2] = wrappedMovableTilesForBlackKing[row][col];
//                                printMovableTilesForKings();
                            }
                        }
                    }
                } else if (piece.charAt(1) == 'b') {  // bishop
                    for (int i = 1; currentPosX - i >= 0 && currentPosY + i < 8; i++) {  // diagonal northeast
                        if (piecePositions[currentPosX - i][currentPosY + i] != null) {
                            break;
//                            if (piecePositions[currentPosX - i][currentPosY + i].charAt(1) == 'k') {
//                                movableTilesForBlackKing[currentPosX - i][currentPosY + i] = 'x';
//                            } else {
//                                break;
//                            }
                        } else {
                            movableTilesForBlackKing[currentPosX - i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY + i < 8; i++) {  // diagonal southeast
                        if (piecePositions[currentPosX + i][currentPosY + i] != null) {
                            break;
//                            if (piecePositions[currentPosX + i][currentPosY + i].charAt(1) == 'k') {
//                                movableTilesForBlackKing[currentPosX + i][currentPosY + i] = 'x';
//                            } else {
//                                break;
//                            }
                        } else {
                            movableTilesForBlackKing[currentPosX + i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY - i >= 0; i++) {  // diagonal southwest
                        if (piecePositions[currentPosX + i][currentPosY - i] != null) {
                            break;
//                            if (piecePositions[currentPosX + i][currentPosY - i].charAt(1) == 'k') {
//                                movableTilesForBlackKing[currentPosX + i][currentPosY - i] = 'x';
//                            } else {
//                                break;
//                            }
                        } else {
                            movableTilesForBlackKing[currentPosX + i][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0 && currentPosY - i >= 0; i++) {  // diagonal northwest
                        if (piecePositions[currentPosX - i][currentPosY - i] != null) {
                            break;
//                            if (piecePositions[currentPosX - i][currentPosY - i].charAt(1) == 'k') {
//                                movableTilesForBlackKing[currentPosX - i][currentPosY - i] = 'x';
//                            } else {
//                                break;
//                            }
                        } else {
                            movableTilesForBlackKing[currentPosX - i][currentPosY - i] = 'x';
                        }
                    }
                } else if (piece.charAt(1) == 'r') {  // rook
                    for (int i = 1; currentPosX - i >= 0; i++) {  // vertical north
                        if (piecePositions[currentPosX - i][currentPosY] != null) {
                            if (piecePositions[currentPosX - i][currentPosY].charAt(1) == 'k') {
                                movableTilesForBlackKing[currentPosX - i][currentPosY] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForBlackKing[currentPosX - i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY + i < 8; i++) {  // horizontal east
                        if (piecePositions[currentPosX][currentPosY + i] != null) {
                            if (piecePositions[currentPosX][currentPosY + i].charAt(1) == 'k') {
                                movableTilesForBlackKing[currentPosX][currentPosY + i] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForBlackKing[currentPosX][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8; i++) {  // vertical south
                        if (piecePositions[currentPosX + i][currentPosY] != null) {
                            if (piecePositions[currentPosX + i][currentPosY].charAt(1) == 'k') {
                                movableTilesForBlackKing[currentPosX + i][currentPosY] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForBlackKing[currentPosX + i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY - i >= 0; i++) {  // horizontal west
                        if (piecePositions[currentPosX][currentPosY - i] != null) {
                            if (piecePositions[currentPosX][currentPosY - i].charAt(1) == 'k') {
                                movableTilesForBlackKing[currentPosX][currentPosY - i] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForBlackKing[currentPosX][currentPosY - i] = 'x';
                        }
                    }
                } else if (piece.charAt(1) == 'q') {  // queen
                    for (int i = 1; currentPosX - i >= 0; i++) {  // vertical north
                        if (piecePositions[currentPosX - i][currentPosY] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX - i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0 && currentPosY + i < 8; i++) {  // diagonal northeast
                        if (piecePositions[currentPosX - i][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX - i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY + i < 8; i++) {  // horizontal east
                        if (piecePositions[currentPosX][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY + i < 8; i++) {  // diagonal southeast
                        if (piecePositions[currentPosX + i][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX + i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8; i++) {  // vertical south
                        if (piecePositions[currentPosX + i][currentPosY] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX + i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY - i >= 0; i++) {  // diagonal southwest
                        if (piecePositions[currentPosX + i][currentPosY - i] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX + i][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY - i >= 0; i++) {  // horizontal west
                        if (piecePositions[currentPosX][currentPosY - i] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0 && currentPosY - i >= 0; i++) {  // diagonal northwest
                        if (piecePositions[currentPosX - i][currentPosY - i] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX - i][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0; i++) {  // vertical north
                        if (piecePositions[currentPosX - i][currentPosY] != null) {
                            break;
                        } else {
                            movableTilesForBlackKing[currentPosX - i][currentPosY] = 'x';
                        }
                    }
                } else if (piece.charAt(1) == 'k') {  // king
                    char[][] wrappedMovableTilesForBlackKing =
                            new char[movableTilesForBlackKing.length + 2][movableTilesForBlackKing.length + 2];
                    for (int row = 0; row < wrappedMovableTilesForBlackKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForBlackKing.length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForBlackKing.length - 2 &&
                                    col <= wrappedMovableTilesForBlackKing.length - 2) {
                                wrappedMovableTilesForBlackKing[row][col] = movableTilesForBlackKing[row - 1][col - 1];
                            }
                        }
                    }

                    wrappedMovableTilesForBlackKing[currentPosX - 1 + 1][currentPosY + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX - 1 + 1][currentPosY + 1 + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 1][currentPosY + 1 + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 1 + 1][currentPosY + 1 + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 1 + 1][currentPosY + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 1 + 1][currentPosY - 1 + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX + 1][currentPosY - 1 + 1] = 'x';
                    wrappedMovableTilesForBlackKing[currentPosX - 1 + 1][currentPosY - 1 + 1] = 'x';

//                    printMatrix(wrappedMovableTilesForBlackKing);
                    movableTilesForBlackKing = new char[8][8];
                    for (int row = 0; row < wrappedMovableTilesForBlackKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForBlackKing[row].length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForBlackKing.length - 2 &&
                                    col <= wrappedMovableTilesForBlackKing.length - 2) {
                                movableTilesForBlackKing[row - 1][col - 1] = wrappedMovableTilesForBlackKing[row][col];
//                                printMovableTilesForKings();
                            }
                        }
                    }
                }
            } else if (piece.charAt(0) == 'b' && isWhiteTurn) {  // black
                if (piece.charAt(1) == 'p') {  // pawn
                    // pawn is causing IndexOutOfBoundsException

                    // wrapping
                    // maybe wrap movableTilesForWhiteKing and movableTilesForBlackKing at the beginning of
                    // fillAttackedTiles()
                    char[][] wrappedMovableTilesForWhiteKing =
                            new char[movableTilesForWhiteKing.length + 2][movableTilesForWhiteKing.length + 2];
                    for (int row = 0; row < wrappedMovableTilesForWhiteKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForWhiteKing.length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForWhiteKing.length - 2 &&
                                    col <= wrappedMovableTilesForWhiteKing.length - 2) {
                                wrappedMovableTilesForWhiteKing[row][col] = movableTilesForWhiteKing[row - 1][col - 1];
                            }
                        }
                    }
                    wrappedMovableTilesForWhiteKing[currentPosX + 1 + 1][currentPosY - 1 + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 1 + 1][currentPosY + 1 + 1] = 'x';

//                    printMatrix(wrappedMovableTilesForWhiteKing);
                    movableTilesForWhiteKing = new char[8][8];
                    for (int row = 0; row < wrappedMovableTilesForWhiteKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForWhiteKing[row].length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForWhiteKing.length - 2 &&
                                    col <= wrappedMovableTilesForWhiteKing.length - 2) {
                                movableTilesForWhiteKing[row - 1][col - 1] = wrappedMovableTilesForWhiteKing[row][col];
//                                printMovableTilesForKings();
                            }
                        }
                    }
                } else if (piece.charAt(1) == 'n') {  // knight
                    // knight is also causing IndexOutOfBoundsException
                    char[][] wrappedMovableTilesForWhiteKing =
                            new char[movableTilesForWhiteKing.length + 4][movableTilesForWhiteKing.length + 4];
                    for (int row = 0; row < wrappedMovableTilesForWhiteKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForWhiteKing.length; col++) {
                            if (row >= 2 && col >= 2 && row <= wrappedMovableTilesForWhiteKing.length - 3 &&
                                    col <= wrappedMovableTilesForWhiteKing.length - 3) {
                                wrappedMovableTilesForWhiteKing[row][col] = movableTilesForWhiteKing[row - 2][col - 2];
                            }
                        }
                    }
                    wrappedMovableTilesForWhiteKing[currentPosX + 1 + 2][currentPosY - 2 + 2] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 2 + 2][currentPosY - 1 + 2] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 2 + 2][currentPosY + 1 + 2] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 1 + 2][currentPosY + 2 + 2] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX - 1 + 2][currentPosY + 2 + 2] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX - 2 + 2][currentPosY + 1 + 2] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX - 2 + 2][currentPosY - 1 + 2] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX - 1 + 2][currentPosY - 2 + 2] = 'x';

//                    printMatrix(wrappedMovableTilesForWhiteKing);
                    movableTilesForWhiteKing = new char[8][8];
                    for (int row = 0; row < wrappedMovableTilesForWhiteKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForWhiteKing[row].length; col++) {
                            if (row >= 2 && col >= 2 && row <= wrappedMovableTilesForWhiteKing.length - 3 &&
                                    col <= wrappedMovableTilesForWhiteKing.length - 3) {
                                movableTilesForWhiteKing[row - 2][col - 2] = wrappedMovableTilesForWhiteKing[row][col];
//                                printMovableTilesForKings();
                            }
                        }
                    }
                } else if (piece.charAt(1) == 'b') {  // bishop
                    for (int i = 1; currentPosX - i >= 0 && currentPosY + i < 8; i++) {  // diagonal northeast
                        if (piecePositions[currentPosX - i][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX - i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY + i < 8; i++) {  // diagonal southeast
                        if (piecePositions[currentPosX + i][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX + i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY - i >= 0; i++) {  // diagonal southwest
                        if (piecePositions[currentPosX + i][currentPosY - i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX + i][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0 && currentPosY - i >= 0; i++) {  // diagonal northwest
                        if (piecePositions[currentPosX - i][currentPosY - i] != null && piecePositions[currentPosX -
                                i][currentPosX - i].charAt(1) != 'k') {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX - i][currentPosY - i] = 'x';
                        }
                    }
                } else if (piece.charAt(1) == 'r') {  // rook
                    for (int i = 1; currentPosX - i >= 0; i++) {  // vertical north
                        if (piecePositions[currentPosX - i][currentPosY] != null) {
                            if (piecePositions[currentPosX - i][currentPosY].charAt(1) == 'k') {
                                movableTilesForWhiteKing[currentPosX - i][currentPosY] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForWhiteKing[currentPosX - i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY + i < 8; i++) {  // horizontal east
                        if (piecePositions[currentPosX][currentPosY + i] != null) {
                            if (piecePositions[currentPosX][currentPosY + i].charAt(1) == 'k') {
                                movableTilesForWhiteKing[currentPosX][currentPosY + i] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForWhiteKing[currentPosX][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8; i++) {  // vertical south
                        if (piecePositions[currentPosX + i][currentPosY] != null) {
                            if (piecePositions[currentPosX + i][currentPosY].charAt(1) == 'k') {
                                movableTilesForWhiteKing[currentPosX + i][currentPosY] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForWhiteKing[currentPosX + i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY - i >= 0; i++) {  // horizontal west
                        if (piecePositions[currentPosX][currentPosY - i] != null) {
                            if (piecePositions[currentPosX][currentPosY - i].charAt(1) == 'k') {
                                movableTilesForWhiteKing[currentPosX][currentPosY - i] = 'x';
                            } else {
                                break;
                            }
                        } else {
                            movableTilesForWhiteKing[currentPosX][currentPosY - i] = 'x';
                        }
                    }
                } else if (piece.charAt(1) == 'q') {  // queen
                    for (int i = 1; currentPosX - i >= 0; i++) {  // vertical north
                        if (piecePositions[currentPosX - i][currentPosY] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX - i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0 && currentPosY + i < 8; i++) {  // diagonal northeast
                        if (piecePositions[currentPosX - i][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX - i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY + i < 8; i++) {  // horizontal east
                        if (piecePositions[currentPosX][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY + i < 8; i++) {  // diagonal southeast
                        if (piecePositions[currentPosX + i][currentPosY + i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX + i][currentPosY + i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8; i++) {  // vertical south
                        if (piecePositions[currentPosX + i][currentPosY] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX + i][currentPosY] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX + i < 8 && currentPosY - i >= 0; i++) {  // diagonal southwest
                        if (piecePositions[currentPosX + i][currentPosY - i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX + i][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosY - i >= 0; i++) {  // horizontal west
                        if (piecePositions[currentPosX][currentPosY - i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0 && currentPosY - i >= 0; i++) {  // diagonal northwest
                        if (piecePositions[currentPosX - i][currentPosY - i] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX - i][currentPosY - i] = 'x';
                        }
                    }
                    for (int i = 1; currentPosX - i >= 0; i++) {  // vertical north
                        if (piecePositions[currentPosX - i][currentPosY] != null) {
                            break;
                        } else {
                            movableTilesForWhiteKing[currentPosX - i][currentPosY] = 'x';
                        }
                    }
                } else if (piece.charAt(1) == 'k') {  // king
                    // king is causing IndexOutOfBoundsException as well
                    char[][] wrappedMovableTilesForWhiteKing =
                            new char[movableTilesForWhiteKing.length + 2][movableTilesForWhiteKing.length + 2];
                    for (int row = 0; row < wrappedMovableTilesForWhiteKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForWhiteKing.length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForWhiteKing.length - 2 &&
                                    col <= wrappedMovableTilesForWhiteKing.length - 2) {
                                wrappedMovableTilesForWhiteKing[row][col] = movableTilesForWhiteKing[row - 1][col - 1];
                            }
                        }
                    }

                    wrappedMovableTilesForWhiteKing[currentPosX - 1 + 1][currentPosY + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX - 1 + 1][currentPosY + 1 + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 1][currentPosY + 1 + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 1 + 1][currentPosY + 1 + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 1 + 1][currentPosY + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 1 + 1][currentPosY - 1 + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX + 1][currentPosY - 1 + 1] = 'x';
                    wrappedMovableTilesForWhiteKing[currentPosX - 1 + 1][currentPosY - 1 + 1] = 'x';

//                    printMatrix(wrappedMovableTilesForWhiteKing);
                    movableTilesForWhiteKing = new char[8][8];
                    for (int row = 0; row < wrappedMovableTilesForWhiteKing.length; row++) {
                        for (int col = 0; col < wrappedMovableTilesForWhiteKing[row].length; col++) {
                            if (row >= 1 && col >= 1 && row <= wrappedMovableTilesForWhiteKing.length - 2 &&
                                    col <= wrappedMovableTilesForWhiteKing.length - 2) {
                                movableTilesForWhiteKing[row - 1][col - 1] = wrappedMovableTilesForWhiteKing[row][col];
//                                printMovableTilesForKings();
                            }
                        }
                    }
                }
            }
        }
//        System.out.println("----------------------------------------------------------");
//        printMovableTilesForKings();
    }

    public void printMatrix(char[][] mat) {
        String out = "";
        for (int i = 0; i < mat.length; i++) {
            for (int ii = 0; ii < mat[i].length; ii++) {
                out += mat[i][ii] + " ";
            }
            out += "\n";
        }
        System.out.println(out);
    }

    public boolean isLegalMove(String piece, int currentPosX, int currentPosY, int desiredPosX, int desiredPosY) {
        if (piece.charAt(1) == 'p') {  // pawn
            if (currentPosY == 6 && currentPosX == desiredPosX && currentPosY == desiredPosY + 2 &&
                    isVectorFree(currentPosX, currentPosY, desiredPosX, desiredPosY)) {
                return true;
            } else if (currentPosX == desiredPosX && currentPosY == desiredPosY + 1) {
                return true;
            }
        } else if (piece.charAt(1) == 'n') {  // knight
            if ((currentPosX == desiredPosX - 1 && currentPosY == desiredPosY + 2) || (currentPosX == desiredPosX - 2
                    && currentPosY == desiredPosY + 1) || (currentPosX == desiredPosX - 2 && currentPosY ==
                    desiredPosY - 1) || (currentPosX == desiredPosX - 1 && currentPosY == desiredPosY - 2) ||
                    (currentPosX == desiredPosX + 1 && currentPosY == desiredPosY - 2) || (currentPosX ==
                    desiredPosX + 2 && currentPosY == desiredPosY - 1) || (currentPosX == desiredPosX + 2 &&
                    currentPosY == desiredPosY + 1) || (currentPosX == desiredPosX + 1 && currentPosY ==
                    desiredPosY + 2)) {
                return true;
            }
        } else if (piece.charAt(1) == 'b') {  // bishop
            if (Math.abs(currentPosX - desiredPosX) == Math.abs(currentPosY - desiredPosY) && isVectorFree(currentPosX,
                    currentPosY, desiredPosX, desiredPosY)) {
                return true;
            }
        } else if (piece.charAt(1) == 'r') {  // rook
            if ((currentPosX == desiredPosX || currentPosY == desiredPosY) && isVectorFree(currentPosX, currentPosY,
                    desiredPosX, desiredPosY)) {
                return true;
            }
        } else if (piece.charAt(1) == 'q') {  // queen
            for (int i = 1; i < 8; i++) {
                if ((Math.abs(currentPosX - desiredPosX) == Math.abs(currentPosY - desiredPosY) || (currentPosX ==
                        desiredPosX || currentPosY == desiredPosY)) && isVectorFree(currentPosX, currentPosY,
                        desiredPosX, desiredPosY)) {
                    return true;
                }
            }
        } else if (piece.charAt(1) == 'k') {  // king
            if ((currentPosX == desiredPosX && currentPosY == desiredPosY + 1) || (currentPosX == desiredPosX - 1 &&
                    currentPosY == desiredPosY + 1) || (currentPosX == desiredPosX - 1 && currentPosY == desiredPosY)
                    || (currentPosX == desiredPosX - 1 && currentPosY == desiredPosY - 1) || (currentPosX ==
                    desiredPosX && currentPosY == desiredPosY - 1) || (currentPosX == desiredPosX + 1 && currentPosY ==
                    desiredPosY - 1) || (currentPosX == desiredPosX + 1 && currentPosY == desiredPosY) || (currentPosX
                    == desiredPosX + 1 && currentPosY == desiredPosY + 1)) {
                return true;
            }
        }
        return false;
    }

//	public boolean isCastleLegal(String piece, int ) {
//		return false;
//	}

    public boolean isKingUnderCheck(int kingX, int kingY) {
        if (isWhiteTurn) {
            return movableTilesForWhiteKing[kingX][kingY] == 'x';
        } else {
            return movableTilesForBlackKing[kingX][kingY] == 'x';
        }
    }

    public boolean isKingInCheckmate(int kingX, int kingY) {
        /* Do every possible legal move for each of the pieces with the same alliance of the king that
        isKingInCheckmate() is being called for.

        If the king is still under check and surrounded by x's after every possible legal move, then return true,
        otherwise return false */

        //        int legalCount = 0;
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX - 1, kingY)) {
//            legalCount++;
//        }
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX - 1,
//                kingY + 1)) {
//            legalCount++;
//        }
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX, kingY + 1)) {
//            legalCount++;
//        }
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX + 1,
//                kingY + 1)) {
//            legalCount++;
//        }
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX + 1, kingY)) {
//            legalCount++;
//        }
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX + 1,
//                kingY - 1)) {
//            legalCount++;
//        }
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX, kingY - 1)) {
//            legalCount++;
//        }
//        if (isLegalMove(piecePositions[kingX][kingY], kingX, kingY, kingX - 1,
//                kingY - 1)) {
//            legalCount++;
//        }
//        if (legalCount < 8) {
//            return false;
//        }

//        if (piecePositions[lastSelectedRow][lastSelectedCol].charAt(0) !=
//                piecePositions[tempRow][tempCol].charAt(0)) {
//            // moving piece to enemy tile (capturing)
//            if (lastSelectedPiece.charAt(1) == 'p' && ((lastSelectedRow == tempRow + 1 &&
//                    lastSelectedCol == tempCol + 1) || (lastSelectedRow == tempRow + 1 &&
//                    lastSelectedCol == tempCol - 1))) {
//                System.out.println("Pawn capturing activated");
//                piecePositions[tempRow][tempCol] = lastSelectedPiece;
//                piecePositions[lastSelectedRow][lastSelectedCol] = null;
//                isWhiteTurn = !isWhiteTurn;
//                lastSelectedPiece = null;
//                flipChessBoard();
//            } else if (lastSelectedPiece.charAt(1) == 'n' || lastSelectedPiece.charAt(1) == 'b'
//                    || lastSelectedPiece.charAt(1) == 'r' || lastSelectedPiece.charAt(1) == 'q'
//                    || lastSelectedPiece.charAt(1) == 'k') {
//                if (isLegalMove(lastSelectedPiece, lastSelectedCol, lastSelectedRow, tempCol,
//                        tempRow)) {
//                    System.out.println("Normal capturing activated");
//                    piecePositions[tempRow][tempCol] = lastSelectedPiece;
//                    piecePositions[lastSelectedRow][lastSelectedCol] = null;
//                    isWhiteTurn = !isWhiteTurn;
//                    lastSelectedPiece = null;
//                    flipChessBoard();
//                }
//            }
//        }

//        ArrayList<String[][]> alteredCopies = new ArrayList<String[][]>();
        // get all legal moves

//        System.out.println(allLegalMoves);
//        // loop through all legal moves
//        for (int i = 0; i < allLegalMoves.size(); i++) {
//            String[][] alteredCopy = new String[8][8];
//            // alter the copy
//            for (int row = 0; row < piecePositions.length; row++) {
//                for (int col = 0; col < piecePositions[row].length; col++) {
//                    if (row == allLegalMoves.get(i).getCurrentX() && col == allLegalMoves.get(i).getCurrentY()) {
//                        alteredCopy[row][col] = piecePositions[allLegalMoves.get(i).desiredX][allLegalMoves.get(i).
//                                desiredY];
//                    } else {
//                        alteredCopy[row][col] = piecePositions[row][col];
//                    }
//                }
//            }
////            if ()
//        }
//        // make copies of the current state of the board and alter those copies, then check the results
//        for (int row = 0; row < piecePositions.length; row++) {
//            for (int col = 0; col < piecePositions[row].length; col++) {
//                if (piecePositions[row][col].charAt(0) == piecePositions[kingX][kingY].charAt(0)) {
//
//                }
//            }
//        }
        return false;
    }

    private class Board {

    }

    private class Move {
        int currentX, currentY, desiredX, desiredY;

        public Move(int currentX, int currentY, int desiredX, int desiredY) {
            this.currentX = currentX;
            this.currentY = currentY;
            this.desiredX = desiredX;
            this.desiredY = desiredY;
        }

        public int getCurrentX() {
            return currentX;
        }

        public int getCurrentY() {
            return currentY;
        }

        public int getDesiredXX() {
            return desiredX;
        }

        public int getDesiredYX() {
            return desiredY;
        }

        @Override
        public String toString() {
            return "Current: (" + currentX + ", " + currentY + ")\t(" + desiredX + ", " + desiredY + ")";
        }
    }

    public boolean isCapturable() {
        return true;
    }

//	public boolean isStalemate() {
//		return false;
//	}

    // make wrapMatrix method

    public boolean isVectorFree(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 > y2) {  // vertical north
            for (int i = y1 - 1; i > y2; i--) {
                if (piecePositions[i][x1] != null) {
                    return false;
                }
            }
        } else if (x1 < x2 && y1 == y2) {  // horizontal east
            for (int i = x1 + 1; i < x2; i++) {
                if (piecePositions[y1][i] != null) {
                    return false;
                }
            }
        } else if (x1 == x2 && y1 < y2) {  // vertical south
            for (int i = y1 + 1; i < y2; i++) {
                if (piecePositions[i][x1] != null) {
                    return false;
                }
            }
        } else if (x1 > x2 && y1 == y2) {  // horizontal west
            for (int i = x1 - 1; i > x2; i--) {
                if (piecePositions[y1][i] != null) {
                    return false;
                }
            }
        } else if (x1 < x2 && y1 > y2) {  // diagonal northeast
            for (int i = 1; i < x2 - x1; i++) {
                if (piecePositions[y1 - i][x1 + i] != null) {
                    return false;
                }
            }
        } else if (x1 < x2 && y1 < y2) {  // diagonal southeast
            for (int i = 1; i < x2 - x1; i++) {
                if (piecePositions[y1 + i][x1 + i] != null) {
                    return false;
                }
            }
        } else if (x1 > x2 && y1 < y2) {  // diagonal southwest
            for (int i = 1; i < x1 - x2; i++) {
                if (piecePositions[y1 + i][x1 - i] != null) {
                    return false;
                }
            }
        } else if (x1 > x2 && y1 > y2) {  // diagonal northwest
            for (int i = 1; i < x1 - x2; i++) {
                if (piecePositions[y1 - i][x1 - i] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void choosePawnTransformation(String piece, int row, int col) {
        JDialog transformationDialog = new JDialog(this, "Choose piece");
        transformationDialog.setBackground(new Color(128, 70, 27));
        transformationDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        transformationDialog.setResizable(false);
        transformationDialog.setLayout(new BorderLayout());

        JPanel basePanel = new JPanel();
        basePanel.setBackground(new Color(128, 70, 27));
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));

        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(new Color(128, 70, 27));
        messagePanel.setPreferredSize(new Dimension(180, 30));
        JLabel title = new JLabel("Transform pawn into:");
        title.setForeground(new Color(245, 245, 175));
        messagePanel.add(title);
        basePanel.add(messagePanel);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(128, 70, 27));
        buttonPanel.setPreferredSize(new Dimension(180, 180));
        buttonPanel.setLayout(new GridLayout(2, 2));

        JButton queen = new JButton();
        if (piece.charAt(0) == 'w') {
            queen.setIcon(new ImageIcon(whiteQueenGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else if (piece.charAt(0) == 'b') {
            queen.setIcon(new ImageIcon(blackQueenGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        }
        queen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (piece.charAt(0) == 'w') {
                    piecePositions[row][col] = "wq";
                } else if (piece.charAt(0) == 'b') {
                    piecePositions[row][col] = "bq";
                }
                transformationDialog.dispose();
            }
        });
        buttonPanel.add(queen);

        JButton rook = new JButton();
        if (piece.charAt(0) == 'w') {
            rook.setIcon(new ImageIcon(whiteRookGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else if (piece.charAt(0) == 'b') {
            rook.setIcon(new ImageIcon(blackRookGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        }
        rook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (piece.charAt(0) == 'w') {
                    piecePositions[row][col] = "wr";
                } else if (piece.charAt(0) == 'b') {
                    piecePositions[row][col] = "br";
                }
                transformationDialog.dispose();
            }
        });
        buttonPanel.add(rook);

        JButton bishop = new JButton();
        if (piece.charAt(0) == 'w') {
            bishop.setIcon(new ImageIcon(whiteBishopGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else if (piece.charAt(0) == 'b') {
            bishop.setIcon(new ImageIcon(blackBishopGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        }
        bishop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (piece.charAt(0) == 'w') {
                    piecePositions[row][col] = "wb";
                } else if (piece.charAt(0) == 'b') {
                    piecePositions[row][col] = "bb";
                }
                transformationDialog.dispose();
            }
        });
        buttonPanel.add(bishop);

        JButton knight = new JButton();
        if (piece.charAt(0) == 'w') {
            knight.setIcon(new ImageIcon(whiteKnightGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        } else if (piece.charAt(0) == 'b') {
            knight.setIcon(new ImageIcon(blackKnightGUI.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        }
        knight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (piece.charAt(0) == 'w') {
                    piecePositions[row][col] = "wn";
                } else if (piece.charAt(0) == 'b') {
                    piecePositions[row][col] = "bn";
                }
                transformationDialog.dispose();
            }
        });
        buttonPanel.add(knight);

        basePanel.add(buttonPanel);

        transformationDialog.getContentPane().add(basePanel);
        transformationDialog.pack();
        transformationDialog.setModal(true);
        transformationDialog.setLocationRelativeTo(this);
        transformationDialog.setVisible(true);
    }

    public void setTestingChessBoard() {
//        piecePositions[0][0] = "wr";
//        piecePositions[5][5] = "wk";
//        piecePositions[3][3] = "bk";

//        piecePositions[7][7] = "wk";

//        piecePositions[2][2] = "bb";
        piecePositions[4][4] = "bb";

        piecePositions[2][2] = "wk";
        piecePositions[6][2] = "br";
        piecePositions[6][6] = "bp";
        piecePositions[2][6] = "bq";

//        piecePositions[5][2] = "bk";
//        piecePositions[5][5] = "wq";

//        piecePositions[7][0] = "wr";
//        piecePositions[7][7] = "wr";
//        piecePositions[4][4] = "bk";
//        piecePositions[1][1] = "wk";
//        piecePositions[3][3] = "bq";

//        piecePositions[2][0] = "br";
//        piecePositions[4][3] = "wk";
//        piecePositions[6][7] = "br";
//        piecePositions[7][7] = "bk";

//        piecePositions[6][0] = "wp";
//        piecePositions[6][1] = "wp";
//        piecePositions[6][2] = "wp";
//        piecePositions[6][3] = "wp";
//        piecePositions[6][4] = "wp";
//        piecePositions[6][5] = "wp";
//        piecePositions[6][6] = "wp";
//        piecePositions[6][7] = "wp";
//
//        piecePositions[1][0] = "bp";
//        piecePositions[1][1] = "bp";
//        piecePositions[1][2] = "bp";
//        piecePositions[1][3] = "bp";
//        piecePositions[1][4] = "bp";
//        piecePositions[1][5] = "bp";
//        piecePositions[1][6] = "bp";
//        piecePositions[1][7] = "bp";
    }

    public void setChessBoard() {
//        // white pieces
//        piecePositions[6][0] = "bp";
//        piecePositions[6][1] = "bp";
//        piecePositions[6][2] = "bp";
//        piecePositions[6][3] = "bp";
//        piecePositions[6][4] = "bp";
//        piecePositions[6][5] = "bp";
//        piecePositions[6][6] = "bp";
//        piecePositions[6][7] = "bp";
//
//        piecePositions[7][1] = "bn";
//        piecePositions[7][6] = "bn";
//
//        piecePositions[7][2] = "bb";
//        piecePositions[7][5] = "bb";
//
//        piecePositions[7][0] = "br";
//        piecePositions[7][7] = "br";
//
//        piecePositions[7][3] = "bq";
//        piecePositions[7][4] = "bk";
//
//        // black pieces
//        piecePositions[1][0] = "wp";
//        piecePositions[1][1] = "wp";
//        piecePositions[1][2] = "wp";
//        piecePositions[1][3] = "wp";
//        piecePositions[1][4] = "wp";
//        piecePositions[1][5] = "wp";
//        piecePositions[1][6] = "wp";
//        piecePositions[1][7] = "wp";
//
//        piecePositions[0][1] = "wn";
//        piecePositions[0][6] = "wn";
//
//        piecePositions[0][2] = "wb";
//        piecePositions[0][5] = "wb";
//
//        piecePositions[0][0] = "wr";
//        piecePositions[0][7] = "wr";
//
//        piecePositions[0][3] = "wq";
//        piecePositions[0][4] = "wk";


        // white pieces
        piecePositions[6][0] = "wp";
        piecePositions[6][1] = "wp";
        piecePositions[6][2] = "wp";
        piecePositions[6][3] = "wp";
        piecePositions[6][4] = "wp";
        piecePositions[6][5] = "wp";
        piecePositions[6][6] = "wp";
        piecePositions[6][7] = "wp";

        piecePositions[7][1] = "wn";
        piecePositions[7][6] = "wn";

        piecePositions[7][2] = "wb";
        piecePositions[7][5] = "wb";

        piecePositions[7][0] = "wr";
        piecePositions[7][7] = "wr";

        piecePositions[7][3] = "wq";
        piecePositions[7][4] = "wk";

        // black pieces
        piecePositions[1][0] = "bp";
        piecePositions[1][1] = "bp";
        piecePositions[1][2] = "bp";
        piecePositions[1][3] = "bp";
        piecePositions[1][4] = "bp";
        piecePositions[1][5] = "bp";
        piecePositions[1][6] = "bp";
        piecePositions[1][7] = "bp";

        piecePositions[0][1] = "bn";
        piecePositions[0][6] = "bn";

        piecePositions[0][2] = "bb";
        piecePositions[0][5] = "bb";

        piecePositions[0][0] = "br";
        piecePositions[0][7] = "br";

        piecePositions[0][3] = "bq";
        piecePositions[0][4] = "bk";
    }

    public void flipChessBoard() {
        String temp;
        // flip pieces vertically across chess board
        for (int i = 0; i < piecePositions.length / 2; i++) {
            for (int j = 0; j < piecePositions[i].length; j++) {
                temp = piecePositions[i][j];
                piecePositions[i][j] = piecePositions[piecePositions.length - 1 - i][j];
                piecePositions[piecePositions.length - 1 - i][j] = temp;
            }
        }
        // flip pieces horizontally across chess board
        for (int i = 0; i < piecePositions.length; i++) {
            for (int j = 0; j < piecePositions[i].length / 2; j++) {
                temp = piecePositions[i][j];
                piecePositions[i][j] = piecePositions[i][piecePositions.length - 1 - j];
                piecePositions[i][piecePositions.length - 1 - j] = temp;
            }
        }
    }

    public void printChessBoard() {
        String output = "";
        for (int row = 0; row < piecePositions.length; row++) {
            for (int col = 0; col < piecePositions[row].length; col++) {
                output += piecePositions[row][col] == null ? piecePositions[row][col] + " " : " " +
                        piecePositions[row][col] + "  ";
            }
            output += "\n";
        }
        System.out.println(output);
    }

    public void printMovableTilesForKings() {
//        String output = "White king movable tiles:\n";
//        for (int row = 0; row < movableTilesForWhiteKing.length; row++) {
//            for (int col = 0; col < movableTilesForWhiteKing[row].length; col++) {
//                output += movableTilesForWhiteKing[row][col] + " ";
//            }
//            output += "\n";
//        }
//        output += "\nBlack King movable tiles:\n";
//        for (int row = 0; row < movableTilesForBlackKing.length; row++) {
//            for (int col = 0; col < movableTilesForBlackKing[row].length; col++) {
//                output += movableTilesForBlackKing[row][col] + " ";
//            }
//            output += "\n";
//        }
//        System.out.println(output);
        String output = "";
        if (isWhiteTurn) {
            output = "White king movable tiles:\n";
            for (int row = 0; row < movableTilesForWhiteKing.length; row++) {
                for (int col = 0; col < movableTilesForWhiteKing[row].length; col++) {
                    output += movableTilesForWhiteKing[row][col] + " ";
                }
                output += "\n";
            }
        } else if (!isWhiteTurn) {
            output += "\nBlack King movable tiles:\n";
            for (int row = 0; row < movableTilesForBlackKing.length; row++) {
                for (int col = 0; col < movableTilesForBlackKing[row].length; col++) {
                    output += movableTilesForBlackKing[row][col] + " ";
                }
                output += "\n";
            }
        }
        System.out.println(output);
    }
}