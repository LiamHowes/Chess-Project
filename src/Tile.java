public class Tile {
    private int id;
    public Piece p;

    Tile(int i){
        this.id = i;
    }
    public Piece getPiece(){
        return p;
    }

}
