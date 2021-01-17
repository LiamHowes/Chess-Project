/*Assignment 1 COSC 3P71
Chip.java contains the information on a chip used in a Connect 4 Game (namely,
its color). Methods included this class set and get the colour, as well as compare 
it to another chip to see if the two are equal, or "matching".
September 30, 2019
Curtis Honsberger 6630362 
This is entirely my own work. */
public class Piece {
   public String colour;
   public String rank;
   
   //Constructor
   public Piece(String c, String r) {
      colour = c;
      rank = r;
   }
   
   //Getter and setter methods
   public String getColour() {
      return colour;
   }
   public void setColour(String c) {
      colour = c;
   }
   public String getRank(){return rank;}
   /*This method gets the colour from another chip and returns a boolean value if
   the two chips are the same color*/
   public boolean equals(Piece c) {
      if(colour.equals(c.getColour()))
         return true;
      return false;
   }
   
   //Prints the first character of the chip's colour
   public String toString() {
      String s = "" + colour.charAt(0)+rank.charAt(0);
      return s;
   }
}