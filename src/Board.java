/*Assignment 4 COSC 3P71
Board.java contains the information on a generic board, that holds an array of chips
import java.util.Arrays;
import java.util.HashSet;
/*Assignment 1 COSC 3P71
Board.java contains the information on a generic board, that holds an array of chips
across several rows and columns. Methods included in this include getter methods for these
attributes, as well as an isEmpty method that checks whether a specific place on the board
contains a chip, as well as an add method that adds chips to a specific place on the board.
December 8, 2019
Curtis Honsberger 6630362
Liam Howes 5880331
This is entirely my own work.  */
import java.util.*;
public class Board {
    public Piece[][] board;
    public Board tempB;
    public boolean[][] tempTakes;
    public boolean playerTurn = true;
    public boolean gameOver = false;
    public boolean whiteInCheck = false;
    public boolean blackInCheck = false;
    public boolean checkCheckMate = false;
    public boolean dangerMove = false;
    public boolean maximizePlayer = false; //human goes first
    public Piece[] takenPieces = new Piece[32];//For sidebar
    public Board(){
        board = new Piece[8][8];
        board[7][0] = new Piece("W", "R"); //white rook
        board[7][1] = new Piece("W", "N"); //" knight
        board[7][2] = new Piece("W", "B"); //" bishop
        board[7][3] = new Piece("W", "Q"); //" queen
        board[7][4] = new Piece("W", "K"); //" king
        board[7][5] = new Piece("W", "B");
        board[7][6] = new Piece("W", "N");
        board[7][7] = new Piece("W", "R");
        board[6][0] = new Piece("W", "P"); //" pawn
        board[6][1] = new Piece("W", "P");
        board[6][2] = new Piece("W", "P");
        board[6][3] = new Piece("W", "P");
        board[6][4] = new Piece("W", "P");
        board[6][5] = new Piece("W", "P");
        board[6][6] = new Piece("W", "P");
        board[6][7] = new Piece("W", "P");

        board[0][0] = new Piece("B", "R"); //black rook
        board[0][1] = new Piece("B", "N"); //" knight
        board[0][2] = new Piece("B", "B"); //" bishop
        board[0][3] = new Piece("B", "Q"); //" queen
        board[0][4] = new Piece("B", "K"); //" king
        board[0][5] = new Piece("B", "B");
        board[0][6] = new Piece("B", "N");
        board[0][7] = new Piece("B", "R");
        board[1][0] = new Piece("B", "P"); //" pawn
        board[1][1] = new Piece("B", "P");
        board[1][2] = new Piece("B", "P");
        board[1][3] = new Piece("B", "P");
        board[1][4] = new Piece("B", "P");
        board[1][5] = new Piece("B", "P");
        board[1][6] = new Piece("B", "P");
        board[1][7] = new Piece("B", "P");
    }

    public Board(Piece[][] p) {
        this.board = new Piece[8][8];
        for (int i = 0; i < 64; i++) {
            this.board[i / 8][i % 8] = p[i / 8][i % 8];
        }
    }

    //Getter methods
    public Piece[][] getBoard() {
        return board;
    }

    public Piece[][] copyBoard(Piece[][] theBoard) {
        Piece[][] dup = new Piece[8][8];
        for (int i = 0; i < 8; i++) { //rows
            //columns
            System.arraycopy(theBoard[i], 0, dup[i], 0, 8);
        }
        return dup;
    }

    /*This method "looks" at a specific place in the array to see if a chip object
    has been created at that place, returning a boolean value if it has/hasn't*/
    public boolean isEmpty(int r, int c) {
        if (board[r][c] == null)
            return true;
        return false;
    }

    /*This method creates a Chip object to a parameter specified location in the board array
    IF the place is empty, and returns true if it was successfully added or returns false and does
    nothing*/
    public boolean add(int r, int c, Piece piece) {
        if (isEmpty(r, c)) {
            board[r][c] = piece;
            return true;
        }
        return false;
    }

    //Looks at the current board (depending on the player's turn) and updates the variables whiteInCheck,blackInCheck, and gameOver based on
    //Whether a player is in check, checkmate, stalemate or none of these (regular play)
    public void checkGameState(){
        //Iterate through all the tiles on the board
        for(int i=0;i<64;i++){
            boolean[][] takes = new boolean[8][8];
            if(checkCheckMate){
                dangerMove=true;
            }
            takes = getPieceTakes((i / 8),(i % 8));
            //If there is a piece there, and the piece is black
            if(board[i / 8][(i % 8)]!=null&&((board[i / 8][(i % 8)].getColour().equals("B")))){
                for(int j=0;j<64;j++){
                    if(takes[j/8][j%8]&&board[j/8][j%8].getRank().equals("K")&&board[j/8][j%8].getColour().equals("W")){
                        whiteInCheck=true;
                        if(!checkCheckMate&&!anyMoves()){
                            System.out.println("Well white you really fucked up now. Game over");
                            gameOver = true;
                        }
                        else {
                            checkCheckMate=false;
                        }
                        return;
                    }
                }
            }
            //Same thing but for white pieces
            else if (board[i / 8][(i % 8)] != null && (board[i / 8][(i % 8)].getColour().equals("W"))) {
                for (int j = 0; j < 64; j++) {
                    if (takes[j / 8][j % 8] && board[j / 8][j % 8].getRank().equals("K") && board[j / 8][j % 8].getColour().equals("B")) {
                        blackInCheck = true;
                        if(!checkCheckMate&&!anyMoves()){
                            System.out.println("Well black you really fucked up now. Game over");
                            gameOver = true;
                        }
                        else {
                            checkCheckMate=false;
                        }
                        return;
                    }
                }
            }
        }
        if(!checkCheckMate&&!anyMoves()){
            System.out.println("Well I can't think of anything creative to say for stalemate.Game over");
            gameOver=true;
            return;
        }
        whiteInCheck=false;
        blackInCheck=false;
    }
    public boolean anyMoves(){
        for(int i=0;i<64;i++){
            boolean[][] anyMoves = new boolean[8][8];
            boolean[][] anyTakes = new boolean[8][8];
            if (board[i / 8][(i % 8)] != null && ((!playerTurn && board[i / 8][(i % 8)].getColour().equals("B")) || (playerTurn && board[i / 8][(i % 8)].getColour().equals("W")))) {
                anyMoves = getPieceMoves((i / 8), (i % 8));
                anyTakes = getPieceTakes((i / 8), (i % 8));
                for (int j = 0; j < 64; j++) {
                    if (anyMoves[j / 8][(j % 8)] || anyTakes[j / 8][(j % 8)]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //getPieceTakes returns a boolean array of all the board tiles that the piece can move to in order to capture an opponent's piece
    public boolean[][] getPieceTakes(int row, int column) {
        if (board[row][column] == null) {
            return null;
        }
        boolean[][] takes = new boolean[8][8];
        if (board[row][column].rank.equals("P")) { //if the piece is a...
            if (board[row][column].colour.equals("B")) {//black pawn
                if(row == 7){
                    board[row][column] = null;
                    board[row][column] = new Piece("B", "Q");//if pawn gets to other side: upgrade it to a queen
                    getPieceTakes(row,column);//and give the take-set for a queen
                }
                if (row + 1 > 7 || column + 1 > 7) {
                    //do nothing
                } else if (board[row + 1][column + 1] != null && board[row + 1][column + 1].colour.equals("W")) {
                    takes[row + 1][column + 1] = true;
                }
                if (row + 1 > 7 || column - 1 < 0) {
                    //do nothing
                } else if (board[row + 1][column - 1] != null && board[row + 1][column - 1].colour.equals("W")) {
                    takes[row + 1][column - 1] = true;
                }
            }
            else if(board[row][column].colour.equals("W")){//white pawn
                if(row == 0){
                    board[row][column] = null;
                    board[row][column] = new Piece("W", "Q");//if pawn gets to other side: upgrade it to a queen
                    getPieceTakes(row,column);//and give the take-set for a queen
                }
                if(row-1<0||column+1>7) {
                    //do nothing
                }else if (board[row-1][column+1]!=null&&board[row-1][column+1].colour.equals("B")){
                    takes[row-1][column+1] = true;
                }
                if(row-1<0||column-1<0) {
                    //do nothing
                }else if (board[row-1][column-1]!=null&&board[row-1][column-1].colour.equals("B")){
                    takes[row-1][column-1] = true;
                }
            }
        }
        else if(board[row][column].rank.equals("R")){//rook
            takes = getLinesTakes(takes, row, column);
        }
        else if(board[row][column].rank.equals("N")){//knight
            takes = getKnightTakes(takes, row, column);
        }
        else if(board[row][column].rank.equals("B")){//bishop
            takes = getDiagonalTakes(takes, row, column);
        }
        else if(board[row][column].rank.equals("Q")){//queen
            takes = getDiagonalTakes(takes, row, column);
            takes = getLinesTakes(takes, row, column);
        }
        else if(board[row][column].rank.equals("K")){//king
            takes = getKingTakes(takes, row, column);
        }
        if(whiteInCheck||playerTurn) {
            if(dangerMove){
                whiteInCheck=false;
                dangerMove=false;
                return takes;
            }
            for(int i=0;i<64;i++){
                if(takes[i/8][i%8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.checkCheckMate=true;
                    tempB.checkGameState();
                    if (tempB.whiteInCheck)
                        takes[i / 8][i % 8] = false;
                }
            }
        }
        if(whiteInCheck&&!checkCheckMate){
            for(int i=0;i<64;i++){
                if(takes[i/8][i%8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.checkCheckMate=true;
                    tempB.dangerMove=true;
                    tempTakes = tempB.getPieceTakes(i/8,i%8);
                    for(int j=0;j<64;j++) {
                        if (tempTakes[j / 8][j % 8] && tempB.board[j / 8][j % 8].getRank().equals("K")) {
                            takes[i / 8][i % 8] = false;
                        }
                    }
                }
            }
        }
        if(blackInCheck||!playerTurn) {
            if(dangerMove){
                blackInCheck=false;
                dangerMove=false;
                return takes;
            }
            for(int i=0;i<64;i++){
                if(takes[i/8][i%8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.checkCheckMate=true;
                    tempB.checkGameState();
                    if (tempB.blackInCheck)
                        takes[i / 8][i % 8] = false;
                }
            }
        }
        if(blackInCheck&&!checkCheckMate){
            for(int i=0;i<64;i++){
                if(takes[i/8][i%8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.playerTurn = false;
                    tempB.checkCheckMate=true;
                    tempB.dangerMove=true;
                    tempTakes = tempB.getPieceTakes(i/8,i%8);
                    for(int j=0;j<64;j++) {
                        if (tempTakes[j / 8][j % 8] && tempB.board[j / 8][j % 8].getRank().equals("K")) {
                            takes[i / 8][i % 8] = false;
                        }
                    }
                }
            }
        }

        return takes;
    }

    //Breaks down the non-capturing moves a piece at a given co-ordinate can make (returns null if no piece is found at the co-ordinate)
    public boolean[][] getPieceMoves(int row, int column) {
        if (board[row][column] == null) {
            return null;
        }

        boolean[][] moves = new boolean[8][8];
        //moves[row][column] = true;

        if (board[row][column].rank.equals("P")) { //if the piece is a...
            if (board[row][column].colour.equals("B")) {//black pawn
                if(row == 7){
                    board[row][column] = null;
                    board[row][column] = new Piece("B", "Q");//if pawn gets to other side: upgrade it to a queen
                    getPieceMoves(row,column);//and give the move-set for a queen
                }
                if (board[row + 1][column] == null) {
                    moves[row + 1][column] = true;
                    if (row == 1 && board[row + 2][column] == null) {
                        moves[row + 2][column] = true;
                    }
                }
            } else if (board[row][column].colour.equals("W")) {//white pawn
                if(row == 0){
                    board[row][column] = null;
                    board[row][column] = new Piece("W","Q");//if pawn gets to other side: upgrade it to a queen
                    getPieceMoves(row,column);//and give the move-set for a queen
                }
                if (board[row - 1][column] == null) {
                    moves[row - 1][column] = true;
                    if (row == 6 && board[row - 2][column] == null) {
                        moves[row - 2][column] = true;
                    }
                }
            }
        } else if (board[row][column].rank.equals("R")) {//rook
            moves = getLinesMoves(moves, row, column);
            if(castlingWhiteL&&row==7&&column==0){
                /* if there is nothing in between rook and king*/
                moves[row][column+4] = true;
            }
            if(castlingWhiteR&&row==7&&column==7){
                /* if there is nothing in between rook and king*/
                moves[row][column-3] = true;
            }
        } else if (board[row][column].rank.equals("N")) {//knight
            moves = getKnightMoves(moves, row, column);
        } else if (board[row][column].rank.equals("B")) {//bishop
            moves = getDiagonalMoves(moves, row, column);
        } else if (board[row][column].rank.equals("Q")) {//queen
            moves = getDiagonalMoves(moves, row, column);
            moves = getLinesMoves(moves, row, column);
        }
        else if(board[row][column].rank.equals("K")) {//king



            moves = getKingMoves(moves, row, column);
        }
        if(whiteInCheck||playerTurn) {
            for (int i = 0; i < 64; i++) {
                if (moves[i / 8][i % 8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.checkCheckMate = true;
                    tempB.checkGameState();
                    if (tempB.whiteInCheck) {
                        moves[i / 8][i % 8] = false;
                    }
                }
            }
        }
        if(whiteInCheck&&!checkCheckMate){
            for(int i=0;i<64;i++){
                if(moves[i/8][i%8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.checkCheckMate=true;
                    tempB.dangerMove=true;
                    tempTakes = tempB.getPieceTakes(i/8,i%8);
                    for(int j=0;j<64;j++) {
                        if (tempTakes[j / 8][j % 8] && tempB.board[j / 8][j % 8].getRank().equals("K")) {
                            moves[i / 8][i % 8] = false;
                        }
                    }
                }
            }
        }
        if(blackInCheck||!playerTurn) {
            for (int i = 0; i < 64; i++) {
                if (moves[i / 8][i % 8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.checkCheckMate = true;
                    tempB.checkGameState();
                    if (tempB.blackInCheck) {
                        moves[i / 8][i % 8] = false;
                    }
                }
            }
        }
        if(blackInCheck&&!checkCheckMate){
            for(int i=0;i<64;i++){
                if(moves[i/8][i%8]) {
                    tempB = new Board(board);
                    tempB.board[i / 8][i % 8] = board[row][column];
                    tempB.board[row][column] = null;
                    tempB.playerTurn=false;
                    tempB.checkCheckMate=true;
                    tempB.dangerMove=true;
                    tempTakes = tempB.getPieceTakes(i/8,i%8);
                    for(int j=0;j<64;j++) {
                        if (tempTakes[j / 8][j % 8] && tempB.board[j / 8][j % 8].getRank().equals("K")) {
                            moves[i / 8][i % 8] = false;
                        }
                    }
                }
            }
        }
        return moves;
    }

    public void clearMoves(boolean[][] moves) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                moves[i][j] = false;
            }
        }
    }

    public boolean[][] getKingTakes(boolean[][] moves, int row, int column) {
        String c = board[row][column].colour;
        if (row + 1 > 7 || column + 1 > 7 || board[row + 1][column + 1] == null) {//off the board or empty tile
            //do nothing
        } else if (!c.equals(board[row + 1][column + 1].colour)) { //down right
            moves[row + 1][column + 1] = true;
        }
        if (row + 1 > 7 || column - 1 < 0 || board[row + 1][column - 1] == null) {
            //do nothing
        } else if (!c.equals(board[row + 1][column - 1].colour)) { //down left
            moves[row + 1][column - 1] = true;
        }
        if (row - 1 < 0 || column + 1 > 7 || board[row - 1][column + 1] == null) {
            //do nothing
        } else if (!c.equals(board[row - 1][column + 1].colour)) { //up right
            moves[row - 1][column + 1] = true;
        }
        if (row - 1 < 0 || column - 1 < 0 || board[row - 1][column - 1] == null) {
            //do nothing
        } else if (!c.equals(board[row - 1][column - 1].colour)) { //up left
            moves[row - 1][column - 1] = true;
        }
        if (row - 1 < 0 || board[row - 1][column] == null) {
            //do nothing
        } else if (!c.equals(board[row - 1][column].colour)) { //up
            moves[row - 1][column] = true;
        }
        if (row + 1 > 7 || board[row + 1][column] == null) {
            //do nothing
        } else if (!c.equals(board[row + 1][column].colour)) { //down
            moves[row + 1][column] = true;
        }
        if (column + 1 > 7 || board[row][column + 1] == null) {
            //do nothing
        } else if (!c.equals(board[row][column + 1].colour)) { //right
            moves[row][column + 1] = true;
        }
        if (column - 1 < 0 || board[row][column - 1] == null) {
            //do nothing
        } else if (!c.equals(board[row][column - 1].colour)) { //left
            moves[row][column - 1] = true;
        }
        return moves;
    }

    public boolean[][] getKnightTakes(boolean[][] moves, int row, int column) {
        String c = board[row][column].colour;
        if (row - 1 < 0 || column + 2 > 7 || board[row - 1][column + 2] == null) {//off the board or empty tile
            //do nothing
        } else if (!c.equals(board[row - 1][column + 2].colour)) { //right right up
            moves[row - 1][column + 2] = true;
        }
        if (row + 1 > 7 || column + 2 > 7 || board[row + 1][column + 2] == null) {
            //do nothing
        } else if (!c.equals(board[row + 1][column + 2].colour)) { //right right down
            moves[row + 1][column + 2] = true;
        }
        if (row - 2 < 0 || column + 1 > 7 || board[row - 2][column + 1] == null) {
            //do nothing
        } else if (!c.equals(board[row - 2][column + 1].colour)) { //right up up
            moves[row - 2][column + 1] = true;
        }
        if (row + 2 > 7 || column + 1 > 7 || board[row + 2][column + 1] == null) {
            //do nothing
        } else if (!c.equals(board[row + 2][column + 1].colour)) { //right down down
            moves[row + 2][column + 1] = true;
        }
        if (row - 1 < 0 || column - 2 < 0 || board[row - 1][column - 2] == null) {
            //do nothing
        } else if (!c.equals(board[row - 1][column - 2].colour)) { //left left up
            moves[row-1][column-2] = true;
        }
        if (row + 1 > 7 || column - 2 < 0 || board[row + 1][column - 2] == null) {
            //do nothing
        } else if (!c.equals(board[row + 1][column - 2].colour)) { //left left down
            moves[row + 1][column - 2] = true;
        }
        if (row - 2 < 0 || column - 1 < 0 || board[row - 2][column - 1] == null) {
            //do nothing
        } else if (!c.equals(board[row - 2][column - 1].colour)) { //left up up
            moves[row - 2][column - 1] = true;
        }
        if (row + 2 > 7 || column - 1 < 0 || board[row + 2][column - 1] == null) {
            //do nothing
        } else if (!c.equals(board[row + 2][column - 1].colour)) { //left down down
            moves[row + 2][column - 1] = true;
        }
        return moves;
    }

    public boolean[][] getDiagonalTakes(boolean[][] moves, int row, int column) {
        String c = board[row][column].colour;
        for (int i = 1; i < 8; i++) {
            if (row + i > 7 || column + i > 7 || board[row + i][column + i] == null) {
            } //if out of bounds, or empty tile: do nothing
            else if (board[row + i][column + i].colour.equals(c)) {
                break;//if the tile is taken by the player's own piece: break
            } else if (!c.equals(board[row + i][column + i].colour)) {//down right
                moves[row + i][column + i] = true;
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row + i > 7 || column - i < 0 || board[row + i][column - i] == null) {
            } else if (board[row + i][column - i].colour.equals(c)) {
                break;
            } else if (!c.equals(board[row + i][column - i].colour)) {//down left
                moves[row + i][column - i] = true;
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row - i < 0 || column + i > 7 || board[row - i][column + i] == null) {
            } else if (board[row - i][column + i].colour.equals(c)) {
                break;
            } else if (!c.equals(board[row - i][column + i].colour)) {//up right
                moves[row - i][column + i] = true;
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row - i < 0 || column - i < 0 || board[row - i][column - i] == null) {
            } else if (board[row - i][column - i].colour.equals(c)) {
                break;
            } else if (!c.equals(board[row - i][column - i].colour)) {//up left
                moves[row - i][column - i] = true;
                break;
            }
        }
        return moves;
    }

    public boolean[][] getLinesTakes(boolean[][] moves, int row, int column) {
        String c = board[row][column].colour;
        for (int i = 1; i < 8; i++) {//rows
            if (row + i > 7 || board[row + i][column] == null) {
                //if tile is out of bounds, or empty: do nothing
            } else if (board[row + i][column].colour.equals(c)) {
                break; //if tile is taken by player's own piece: break.
            } else if (!c.equals(board[row + i][column].colour)) {//down
                moves[row + i][column] = true;
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row - i < 0 || board[row - i][column] == null) {
            } else if (board[row - i][column].colour.equals(c)) {
                break;
            } else if (!c.equals(board[row - i][column].colour)) {//up
                moves[row - i][column] = true;
                break;
            }
        }
        for (int i = 1; i < 8; i++) {//columns
            if (column + i > 7 || board[row][column + i] == null) {
            } else if (board[row][column + i].colour.equals(c)) {
                break;
            } else if (!c.equals(board[row][column + i].colour)) {//right
                moves[row][column + i] = true;
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (column - i < 0 || board[row][column - i] == null) {
            } else if (board[row][column - i].colour.equals(c)) {
                break;
            } else if (!c.equals(board[row][column - i].colour)) {//left
                moves[row][column - i] = true;
                break;
            }
        }
        return moves;
    }

    public boolean[][] getKingMoves(boolean[][] moves, int row, int column) {
        if (row + 1 > 7 || column + 1 > 7 || board[row + 1][column + 1] != null) {//check for conditions that will return an array out of bounds exception
            //do nothing
        } else if (board[row + 1][column + 1] == null) { //down right
            moves[row + 1][column + 1] = true;
        }
        if (row + 1 > 7 || column - 1 < 0 || board[row + 1][column - 1] != null) {
            //do nothing
        } else if (board[row + 1][column - 1] == null) { //down left
            moves[row + 1][column - 1] = true;
        }
        if (row - 1 < 0 || column + 1 > 7 || board[row - 1][column + 1] != null) {
            //do nothing
        } else if (board[row - 1][column + 1] == null) { //up right
            moves[row - 1][column + 1] = true;
        }
        if (row - 1 < 0 || column - 1 < 0 || board[row - 1][column - 1] != null) {
            //do nothing
        } else if (board[row - 1][column - 1] == null) { //up left
            moves[row - 1][column - 1] = true;
        }
        if (row - 1 < 0 || board[row - 1][column] != null) {
            //do nothing
        } else if (board[row - 1][column] == null) { //up
            moves[row - 1][column] = true;
        }
        if (row + 1 > 7 || board[row + 1][column] != null) {
            //do nothing
        } else if (board[row + 1][column] == null) { //down
            moves[row + 1][column] = true;
        }
        if (column + 1 > 7 || board[row][column + 1] != null) {
            //do nothing
        } else if (board[row][column + 1] == null) { //right
            moves[row][column + 1] = true;
        }
        if (column - 1 < 0 || board[row][column - 1] != null) {
            //do nothing
        } else if (board[row][column - 1] == null) { //left
            moves[row][column - 1] = true;
        }
        return moves;
    }

    public boolean[][] getKnightMoves(boolean[][] moves, int row, int column) {
        if (row - 1 < 0 || column + 2 > 7 || board[row - 1][column + 2] != null) {//check for conditions that will return an array out of bounds exception
            //do nothing
        } else if (board[row - 1][column + 2] == null) { //right right up
            moves[row - 1][column + 2] = true;
        }
        if (row + 1 > 7 || column + 2 > 7 || board[row + 1][column + 2] != null) {
            //do nothing
        } else if (board[row + 1][column + 2] == null) { //right right down
            moves[row + 1][column + 2] = true;
        }
        if (row - 2 < 0 || column + 1 > 7 || board[row - 2][column + 1] != null) {
            //do nothing
        } else if (board[row - 2][column + 1] == null) { //right up up
            moves[row - 2][column + 1] = true;
        }
        if (row + 2 > 7 || column + 1 > 7 || board[row + 2][column + 1] != null) {
            //do nothing
        } else if (board[row + 2][column + 1] == null) { //right down down
            moves[row + 2][column + 1] = true;
        }
        if (row - 1 < 0 || column - 2 < 0 || board[row - 1][column - 2] != null) {
            //do nothing
        } else if (board[row - 1][column - 2] == null) { //left left up
            moves[row-1][column-2] = true;
        }
        if (row + 1 > 7 || column - 2 < 0 || board[row + 1][column - 2] != null) {
            //do nothing
        } else if (board[row + 1][column - 2] == null) { //left left down
            moves[row + 1][column - 2] = true;
        }
        if (row - 2 < 0 || column - 1 < 0 || board[row - 2][column - 1] != null) {
            //do nothing
        } else if (board[row - 2][column - 1] == null) { //left up up
            moves[row - 2][column - 1] = true;
        }
        if (row + 2 > 7 || column - 1 < 0 || board[row + 2][column - 1] != null) {
            //do nothing
        } else if (board[row + 2][column - 1] == null) { //left down down
            moves[row + 2][column - 1] = true;
        }
        return moves;
    }

    public boolean[][] getDiagonalMoves(boolean[][] moves, int row, int column) {
        for (int i = 1; i < 8; i++) {
            if (row + i > 7 || column + i > 7 || board[row + i][column + i] != null) {
                break;
            } else if (board[row + i][column + i] == null) {//down right
                moves[row + i][column + i] = true;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row + i > 7 || column - i < 0 || board[row + i][column - i] != null) {
                break;
            } else if (board[row + i][column - i] == null) {//down left
                moves[row + i][column - i] = true;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row - i < 0 || column + i > 7 || board[row - i][column + i] != null) {
                break;
            } else if (board[row - i][column + i] == null) {//up right
                moves[row - i][column + i] = true;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row - i < 0 || column - i < 0 || board[row - i][column - i] != null) {
                break;
            } else if (board[row - i][column - i] == null) {//up left
                moves[row - i][column - i] = true;
            }
        }
        return moves;
    }

    public boolean[][] getLinesMoves(boolean[][] moves, int row, int column) {
        for (int i = 1; i < 8; i++) {//rows
            if (row + i > 7 || board[row + i][column] != null) { //if spot is taken, break loop because pieces can't skip over others
                break;
            } else if (board[row + i][column] == null) {//down
                moves[row + i][column] = true;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (row - i < 0 || board[row - i][column] != null) {
                break;
            } else if (board[row - i][column] == null) {//up
                moves[row - i][column] = true;
            }
        }
        for (int i = 1; i < 8; i++) {//columns
            if (column + i > 7 || board[row][column + i] != null) {
                break;
            } else if (board[row][column + i] == null) {//right
                moves[row][column + i] = true;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (column - i < 0 || board[row][column - i] != null) {
                break;
            } else if (board[row][column - i] == null) {//left
                moves[row][column - i] = true;
            }
        }
        return moves;
    }

    public int getScore(String c, Board theBoard) {//String c = piece colour you get the score for
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (theBoard.board[i][j]!=null && theBoard.board[i][j].colour.equals(c)) {//if the piece is an ally piece
                    switch (theBoard.board[i][j].rank) {
                        case "P"://pawn
                            score += 1;
                        case "N"://knight
                            score += 3;
                        case "B"://bishop
                            score += 3;
                        case "R"://rook
                            score += 5;
                        case "Q"://queen
                            score += 9;
                        case "K"://king
                            score += 10000;//a number that all the other pieces can never reach
                    }
                } else if (theBoard.board[i][j]!=null&&!c.equals(theBoard.board[i][j].colour)) {//if the piece is an enemy piece
                    switch (theBoard.board[i][j].rank) {
                        case "P"://pawn
                            score -= 1;
                        case "N"://knight
                            score -= 3;
                        case "B"://bishop
                            score -= 3;
                        case "R"://rook
                            score -= 5;
                        case "Q"://queen
                            score -= 9;
                        case "K"://king
                            score -= 10000;//a number that all the other pieces can never reach
                    }
                }
            }//end column loop
        }//end row loop
        return score;
    }

    public int[] minimax(Board bored, int depth, boolean maxPlayer, int alpha, int beta) {
        int[] array = new int[5];//[destination row, destination column, minimax score, starting row, starting column]
        if (depth == 0) {
            array[2] = getScore("B", bored);//heuristic
            System.out.println(array[2]);
            return array;
        }
        if (gameOver) {//terminal node
            if (whiteInCheck) {//if black(AI) wins
                array[2] = 1000000;
                return array;
            } else if (blackInCheck) {//if white wins (AI loses)
                array[2] = -1000000;
                return array;
            }
        }
        if (maxPlayer) {
            System.out.println("max black");
            int value = Integer.MIN_VALUE;
            //for every possible move for BLACK PIECES at current board state:
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (bored.board[i][j]!=null && bored.board[i][j].colour.equals("B")) {
                        boolean[][] moves = bored.getPieceMoves(i, j);
                        boolean[][] takes = bored.getPieceTakes(i, j);
                        for (int k = 0; k < 8; k++) {
                            for (int l = 0; l < 8; l++) {
                                if (moves[k][l] || takes[k][l]) {
                                    //make a copy of the board for running minimax so it doesn't change the board state until a move is determined
                                    Board temp;
                                    temp = new Board(bored.board);
                                    temp.board[k][l] = bored.board[i][j];
                                    temp.board[i][j] = null;
                                    /////////////////////////////////////////
                                    int new_value = Math.max(value, minimax(temp, depth - 1, false, alpha, beta)[2]);
                                    if(new_value>value){
                                        value = new_value;
                                        array[0] = k;
                                        array[1] = l;
                                        array[3] = i;
                                        array[4] = j;
                                    }
                                    alpha=Math.max(alpha, value); //alpha
                                    if(alpha>=beta) break;//don't explore subtree
                                }
                            }
                        }
                    }
                }
            }
            array[2] = value;
            return array;
        } else/*minimizing player*/ {
            System.out.println("min white");
            int value = Integer.MAX_VALUE;
            //for every possible move for WHITE PIECES at current board state:
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (bored.board[i][j]!=null && bored.board[i][j].colour.equals("W")) {
                        boolean[][] moves = bored.getPieceMoves(i, j);
                        boolean[][] takes = bored.getPieceTakes(i, j);
                        for (int k = 0; k < 8; k++) {
                            for (int l = 0; l < 8; l++) {
                                if (moves[k][l] || takes[k][l]) {
                                    //make a copy of the board for running minimax so it doesn't change the board state until a move is determined
                                    Board temp;
                                    temp = new Board(bored.board);
                                    temp.board[k][l] = bored.board[i][j];
                                    temp.board[i][j] = null;
                                    /////////////////////////////////////////
                                    int new_value = Math.min(value, minimax(temp, depth - 1, true, alpha, beta)[2]);
                                    if(new_value<value){
                                        value = new_value;
                                        array[0] = k;
                                        array[1] = l;
                                        array[3] = i;
                                        array[4] = j;
                                    }
                                    beta=Math.min(beta, value);//beta
                                    if(alpha>=beta) break; //don't explore subtree
                                }
                            }
                        }
                    }
                }
            }
            array[2] = value;
            return array;
        }
    }
    public void updatePieceTakes(Piece p){
        int pos = 0;
        if(p.colour.equals("B"))
            pos += 16;
        while(takenPieces[pos]!=null){
            pos++;
        }
        takenPieces[pos]=p;
    }
    public void customSetup(){
        for(int i=0;i<64;i++){
            if(board[i / 8][i % 8]!=null)
                updatePieceTakes(board[i / 8][i % 8]);
            board[i / 8][i % 8] = null;
        }
    }
}