import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardGUI{

    private final JFrame gameBoard;
    private BoardDisplay board;
    private JLabel isInCheck;
    public Board b = new Board();
    private boolean[][] selectionMoves = null;
    private boolean[][] selectionTakes = null;
    private int selectionID = -1;
    private Piece selectionPiece;
    public int difficulty=3; //difficulty is an int corresponding directly to the search depth of the enemy AI
    public int[] minMaxTemp;

    public BoardGUI() {
        this.gameBoard = new JFrame("40 Pizzas in 30 days");
        this.gameBoard.setLayout(new BorderLayout());
        final JMenuBar menu = new JMenuBar();
        populateMenuBar(menu);
        isInCheck = new JLabel(); //tells you if you or enemy is in check
        isInCheck.setBorder(BorderFactory.createEtchedBorder());
        isInCheck.setPreferredSize(new Dimension(600,25));
        gameBoard.add(isInCheck, BorderLayout.SOUTH);
        this.gameBoard.setJMenuBar(menu);
        this.gameBoard.setSize(600, 625);
        this.board = new BoardDisplay();
        this.gameBoard.add(this.board, (BorderLayout.CENTER));
        gameBoard.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//you can exit with file->Exit or by hitting x on window
        this.gameBoard.setVisible(true);

    }

    private void populateMenuBar(final JMenuBar tableMenuBar) {
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createDifficultyMenu());
    }

    private JMenu createFileMenu() {
        final JMenu filesMenu = new JMenu("File");
        filesMenu.setMnemonic(KeyEvent.VK_F);

        final JMenuItem newgame = new JMenuItem("New Game", KeyEvent.VK_O);
        newgame.addActionListener(e-> {
            gameBoard.dispose();//start a new game (opens new window, closes old one)
            new BoardGUI();
        });
        final JMenuItem exitGame = new JMenuItem("Exit", KeyEvent.VK_O);
        exitGame.addActionListener(e -> System.exit(0));

        filesMenu.add(newgame);
        filesMenu.add(exitGame);
        return filesMenu;
    }
    private JMenu createDifficultyMenu() {
        final JMenu difficultyMenu = new JMenu("Difficulty");
        difficultyMenu.setMnemonic(KeyEvent.VK_F);
        ButtonGroup group = new ButtonGroup();

        final JMenuItem easy = new JRadioButtonMenuItem("Beginner");
        difficultyMenu.add(easy);
        easy.addItemListener(e -> {
            if(e.getStateChange()==ItemEvent.SELECTED){
                difficulty = 1;
            }
        });
        final JMenuItem medium = new JRadioButtonMenuItem("Intermediate");
        difficultyMenu.add(medium);
        medium.addItemListener(e -> {
            if(e.getStateChange()==ItemEvent.SELECTED){
                difficulty = 2;
            }
        });
        final JMenuItem hard = new JRadioButtonMenuItem("Advanced");
        difficultyMenu.add(hard);
        hard.setSelected(true);
        hard.addItemListener(e -> {
            if(e.getStateChange()==ItemEvent.SELECTED){
                difficulty = 3;
            }
        });
        group.add(easy);
        group.add(medium);
        group.add(hard);
        return difficultyMenu;
    }

    private void updateBoard(){
        this.gameBoard.remove(this.board);
        this.board = new BoardDisplay();
        this.gameBoard.add(this.board, (BorderLayout.CENTER));
        this.gameBoard.setVisible(true);
    }

    //The background layer; mostly just holds layout/setup
    private class BoardDisplay extends JPanel{
        final List<TileDisplay> boardTiles;

        BoardDisplay() {
            //Draws tiles
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                final TileDisplay tile = new TileDisplay(this,i);
                this.boardTiles.add(tile);
                add(tile);
            }
            setPreferredSize(new Dimension(400,350));
            validate();
        }
    }
    public class TileDisplay extends JPanel{
        private final int id;
        TileDisplay(final BoardDisplay bd, final int i){
            super(new GridBagLayout());

            this.id = i;
            setPreferredSize(new Dimension(10,10));
            assignTileColor();
            if((id==selectionID)||selectionMoves!=null&&selectionMoves[id/8][id%8]){
                assignTileHighlight();
            }
            if(b.board[id/8][(id%8)]!=null) {
                assignTile(b.board[id / 8][(id % 8)].toString());
            }
            if(selectionTakes!=null&&selectionTakes[id/8][id%8])
                assignTileHighlightTake();
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Column:"+(id%8)+" Row:"+(id/8)%8);
                    System.out.println(b.board[id/8][id%8]);
                    if(SwingUtilities.isLeftMouseButton(e)&&!b.gameOver&&b.playerTurn){
                        //No tile selected, selection is a valid piece
                        if(selectionPiece==null&&b.board[id/8][id%8]!=null&&((b.board[id/8][id%8].colour.equals("W")&&b.playerTurn)||(b.board[id/8][id%8].colour.equals("B")&&!b.playerTurn))) {
                            System.out.println("Piece selected");
                            selectionPiece = b.board[id / 8][id % 8];
                            selectionID = id;
                            selectionMoves = b.getPieceMoves((id / 8), (id % 8));
                            selectionTakes = b.getPieceTakes((id / 8), (id % 8));
                            //Tile selected, valid move spot
                        } else if(selectionPiece!=null&&selectionMoves[id/8][id%8]){
                            //player turn
                            b.board[id/8][id%8] = selectionPiece;
                            b.board[selectionID/8][selectionID%8]=null;
                            selectionPiece=null;
                            selectionID=-1;
                            selectionMoves=null;
                            selectionTakes=null;
                            b.playerTurn = !b.playerTurn; //end player turn
                            b.checkGameState();
                            if(b.blackInCheck&&b.gameOver){
                                isInCheck.setText("Well black you really fucked up now. Game over");
                            }
                            else if(b.blackInCheck){
                                isInCheck.setText("Black you're about to get fucked!");
                            }
                            else if(b.gameOver){
                                isInCheck.setText("Well I can't think of anything creative to say for stalemate.Game over");
                            }
                            else{
                                isInCheck.setText(" ");
                            }
                            updateBoard();
                            validate();
                            //AI turn
                            if(!b.gameOver) {
                                minMaxTemp = b.minimax(b,difficulty,true,Integer.MIN_VALUE,Integer.MAX_VALUE);
                                if( b.board[minMaxTemp[0]][minMaxTemp[1]]!=null){//If its a take, update it for the sidebar
                                    b.updatePieceTakes( b.board[minMaxTemp[0]][minMaxTemp[1]]);
                                }
                                b.board[minMaxTemp[0]][minMaxTemp[1]] = b.board[minMaxTemp[3]][minMaxTemp[4]];
                                b.board[minMaxTemp[3]][minMaxTemp[4]] = null;
                                b.playerTurn = !b.playerTurn; //end AI turn
                                b.checkGameState();
                                if(b.whiteInCheck&&b.gameOver){
                                    isInCheck.setText("Well white you really fucked up now. Game over");
                                }
                                else if(b.whiteInCheck){
                                    isInCheck.setText("White you're about to get fucked!");
                                }
                                else if(b.gameOver){
                                    isInCheck.setText("Well I can't think of anything creative to say for stalemate.Game over");
                                }
                                else{
                                    isInCheck.setText(" ");
                                }
                            }
                            updateBoard();
                            validate();
	                            minMaxTemp = b.minimax(b,difficulty,true,Integer.MIN_VALUE,Integer.MAX_VALUE);
	                            b.board[minMaxTemp[0]][minMaxTemp[1]] = b.board[minMaxTemp[3]][minMaxTemp[4]];
	                            b.board[minMaxTemp[3]][minMaxTemp[4]] = null;
	                            b.playerTurn = !b.playerTurn; //end AI turn
	                            b.checkGameState();
                                if(b.whiteInCheck&&b.gameOver){
                                    isInCheck.setText("Well white you really fucked up now. Game over");
                                }
	                            else if(b.whiteInCheck){
	                                isInCheck.setText("White you're about to get fucked!");
	                            }
                                else if(b.gameOver){
                                    isInCheck.setText("Well I can't think of anything creative to say for stalemate.Game over");
                                }
	                            else{
                                    isInCheck.setText(" ");
                                }
                            }
                        }
                        else if(selectionPiece!=null&&selectionTakes[id/8][id%8]){
                            b.board[id/8][id%8] = selectionPiece;
                            //Add piece to sidebar?
                            b.board[selectionID/8][selectionID%8]=null;
                            selectionPiece=null;
                            selectionID=-1;
                            selectionMoves=null;
                            selectionTakes=null;
                            b.playerTurn = !b.playerTurn;
                            b.checkGameState();
                            if(b.blackInCheck&&b.gameOver){
                                isInCheck.setText("Well black you really fucked up now. Game over");
                            }
                            else if(b.blackInCheck){
                                isInCheck.setText("Black you're about to get fucked!");
                            }
                            else if(b.gameOver){
                                isInCheck.setText("Well I can't think of anything creative to say for stalemate.Game over");
                            }
                            else{
                                isInCheck.setText(" ");
                            }
                            updateBoard();
                            validate();
                            //AI turn
                            if(!b.gameOver) {
                                minMaxTemp = b.minimax(b,difficulty,true,Integer.MIN_VALUE,Integer.MAX_VALUE);
                                if( b.board[minMaxTemp[0]][minMaxTemp[1]]!=null){//If its a take, update it for the sidebar
                                    b.updatePieceTakes( b.board[minMaxTemp[0]][minMaxTemp[1]]);
                                }
                                b.board[minMaxTemp[0]][minMaxTemp[1]] = b.board[minMaxTemp[3]][minMaxTemp[4]];
                                b.board[minMaxTemp[3]][minMaxTemp[4]] = null;
                                b.playerTurn = !b.playerTurn;
                                b.checkGameState();
	                            minMaxTemp = b.minimax(b,difficulty,true,Integer.MIN_VALUE,Integer.MAX_VALUE);
	                            b.board[minMaxTemp[0]][minMaxTemp[1]] = b.board[minMaxTemp[3]][minMaxTemp[4]];
	                            b.board[minMaxTemp[3]][minMaxTemp[4]] = null;
	                            b.playerTurn = !b.playerTurn;
	                            b.checkGameState();
                                if(b.whiteInCheck&&b.gameOver){
                                    isInCheck.setText("Well white you really fucked up now. Game over");
                                }
                                else if(b.whiteInCheck){
                                    isInCheck.setText("White you're about to get fucked");
                                }
                                else if(b.gameOver){
                                    isInCheck.setText("Well I can't think of anything creative to say for stalemate.Game over");
                                }
                                else{
                                    isInCheck.setText(" ");
                                }
                            }
                        }
                        //Tile is selected and reclicked (cancels move)
                        else if(selectionPiece!=null&&selectionID==id){
                            System.out.println("deselect piece");
                            selectionPiece=null;
                            selectionID=-1;
                            selectionMoves=null;
                            selectionTakes=null;
                            //Tile selected, non-valid move spot
                        } else if(selectionPiece!=null&& !selectionMoves[id / 8][id % 8]){
                            System.out.println("can't move here");
                            selectionPiece=null;
                            selectionID=-1;
                            selectionMoves=null;
                            selectionTakes=null;
                        }
                        updateBoard();
                        validate();
                    //Right click will always cancels selection
                    if(SwingUtilities.isRightMouseButton(e)&&!b.gameOver&&b.playerTurn){
                        selectionPiece=null;
                        selectionID=-1;
                        selectionMoves=null;
                        selectionTakes=null;
                        updateBoard();
                        validate();
                    }
            }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            validate();
        }
        private void assignTileColor(){
            if((id/8)%2==0) {
                if (id % 2 == 0)
                    setBackground(Color.decode("#FFFACD"));
                else
                    setBackground(Color.decode("#593E1A"));
            }
            else{
                if (id % 2 == 0)
                    setBackground(Color.decode("#593E1A"));
                else
                    setBackground(Color.decode("#FFFACD"));
            }
        }
        //Assigns a piece icon to the given tile
        private void assignTile(final String p){
            this.removeAll();
            if(p!=null) {
                try {
                    final BufferedImage piece = ImageIO.read(new File("src\\art\\" + p + ".gif"));
                    add(new JLabel((new ImageIcon(piece))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private void assignTileHighlight() {
            if(getBackground().equals(Color.decode("#593E1A"))){
                setBackground(Color.decode("#2b5b1a"));
            }
            else if(getBackground().equals(Color.decode("#FFFACD"))){
                setBackground(Color.decode("#53fa55"));
            }
        }
        private void assignTileHighlightTake(){
            if(getBackground().equals(Color.decode("#593E1A"))){
                setBackground(Color.decode("#5b1a1a"));
            }
            else if(getBackground().equals(Color.decode("#FFFACD"))){
                setBackground(Color.decode("#ff6364"));
            }
        }

    }}