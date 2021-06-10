import java.util.*;
import java.awt.*;        
import java.awt.event.*;
import javax.swing.*;

/*
 * This program is the driver for the game of Othello.
*/

public class Main extends JPanel 
{
    final static int BLACK = 1;          // Declare state of each square
    final static int WHITE = 2;
    final static int EMPTY = 0;
    final static int OFFBOARD = -1;

    Black black = new Black();          // The players
    White white = new White();

    private Game game = new Game();     // Game state
    private static int delay;

    private int turn = BLACK;
    private boolean black_done = false; 
    private boolean white_done = false;
    
    /**
     *  This constructor sets up the initial game configuration and delay
     */
    public Main() 
    { 
      this(0); 
    }

    /**
     *  This constructor sets up the initial game configuration
     *  @param    delay    number of milliseconds between player moves
     */
  public Main(int delay) {

    // Initialize the game state
    initGame(game);

    // Run the game with GUI - human vs. computer. 
      if (delay == 0) {
        setBackground(Color.RED);
        addMouseListener( new MouseAdapter() {
                public void mousePressed(MouseEvent evt) {
        // Find out which square was clicked
                    int x = evt.getX();
                    int y = evt.getY();
                    int screenWidth = getWidth();
                    int screenHeight = getHeight();
                    int column = (x*(game.WIDTH-2))/screenWidth+1;
                    int row = (y*(game.HEIGHT-2))/screenHeight+1;
                        
                    if (!game.legalMove(row,column,BLACK,true)) 
                        System.out.println("Not a legal move - try again!");
                    else {
                        game.board[row][column] = BLACK;
                        repaint();
                        black_done = true;
                        for (int i=1; i<game.HEIGHT-1; i++)
                            for (int j=1; j<game.WIDTH-1; j++)
                                if (game.legalMove(i,j,BLACK,false) )
                                    black_done=false;
                        whiteMove();
          }
        }
      });
    }
  }
  
   
    /** 
     *  Initialize the game state
     *  @param    game    the Game state
     */
    public void initGame(Game game) {

        turn = BLACK;
        //System.out.println("Turn is: " + turn);
        
         // Initialize off-board squares
        for (int i=0; i<game.WIDTH; i++) {     
            game.board[i][0] = OFFBOARD;
            game.board[i][game.WIDTH-1] = OFFBOARD;
            game.board[0][i] = OFFBOARD;
            game.board[game.HEIGHT-1][i] = OFFBOARD;
        }

        // Initialize game board to be empty except for initial setup
        for (int i=1; i<game.HEIGHT-1; i++)        
            for (int j=1; j<game.WIDTH-1; j++)
			   game.board[i][j] = EMPTY;
        
        game.board[game.HEIGHT/2-1][game.WIDTH/2-1] = WHITE;        
        game.board[game.HEIGHT/2][game.WIDTH/2-1] = BLACK;
        game.board[game.HEIGHT/2-1][game.WIDTH/2] = BLACK;
        game.board[game.HEIGHT/2][game.WIDTH/2] = WHITE;
    }

    /**
     *  Black and White take turns.
     */
    public void playerMove() {
        
        if (turn == BLACK) {
            blackMove();
            turn = WHITE;
        }
        else {
            whiteMove();
            turn = BLACK;
        }
    }

    /**
     *  Black takes a turn.
     */
    public void blackMove() {
       
        // Check if Black can move anywhere
        black_done = true;
        for (int i=1; i<game.HEIGHT-1; i++)
            for (int j=1; j<game.WIDTH-1; j++)
                if (game.legalMove(i,j,BLACK,false) ) 
                    black_done=false;

        game = black.strategy(game, black_done, BLACK);          
    } 

    /**
     *  White takes a turn.
     */
    public void whiteMove() {

        // Check if White can move
        white_done = true;
        for (int i=1; i<game.HEIGHT-1; i++)
            for (int j=1; j<game.WIDTH-1; j++)
                if (game.legalMove(i,j,WHITE,false) )
                    white_done=false;
        
        game = white.strategy(game, white_done, WHITE);
    }

   /**
    *  Draw the board and the current state of the game. 
    *  @param g the graphics context of the game
    */
   public void paintComponent(Graphics g) {
      
       super.paintComponent(g);  // Fill panel with background color
       
       int width = getWidth();
       int height = getHeight();
       int xoff = width/(game.WIDTH-2);
       int yoff = height/(game.HEIGHT-2);

       int bCount=0;                     
       int wCount=0;                        

       // Draw the lines on the board
       g.setColor(Color.BLACK);
       for (int i=1; i<=game.HEIGHT-2; i++) {        
           g.drawLine(i*xoff, 0, i*xoff, height);
           g.drawLine(0, i*yoff, width, i*yoff);
       }

       // Draw discs on the board and show the legal moves
       for (int i=1; i<game.HEIGHT-1; i++) {        
           for (int j=1; j<game.WIDTH-1; j++) {
               // Draw the discs
               if (game.board[i][j] == BLACK) {       
                   g.setColor(Color.BLACK);
                   g.fillOval((j*yoff)-yoff+7,(i*xoff)-xoff+7,50,50); 
                   bCount++;
               }
               else if (game.board[i][j] == WHITE) {  
                   g.setColor(Color.WHITE);
                   g.fillOval((j*yoff)-yoff+7,(i*xoff)-xoff+7,50,50);
                   wCount++;
               }
               // Show the legal moves for the current player
               if (turn == BLACK && game.legalMove(i,j,BLACK,false)) {
                   g.setColor(Color.BLACK);
                   g.fillOval((j*yoff+29)-yoff,(i*xoff+29)-xoff,6,6);
               }
               // If other player cannot move, current player cleans up
               if (turn == WHITE && game.legalMove(i,j,WHITE,false)) {
                   g.setColor(Color.WHITE);
                   g.fillOval((j*yoff+29)-yoff,(i*xoff+29)-xoff,6,6);
               }
           }
       }
 
       // Check if there are any more moves to make
       boolean done = true;
       for (int i=1; i<game.HEIGHT-1; i++)
           for (int j=1; j<game.WIDTH-1; j++)
               if ((game.legalMove(i,j,BLACK,false)) ||
                   (game.legalMove(i,j,WHITE,false)))
                   done=false;

      // Shows the current state of game
       g.setColor(Color.GREEN);
       g.setFont(new Font("Roman Baseline", Font.BOLD, 14));
       if (done) {
           if (wCount > bCount)
               g.drawString("White won with " + wCount + " discs.",10,20);
           else if (bCount > wCount)
               g.drawString("Black won with " + bCount + " discs.",10,20);
           else g.drawString("Tied game",10,20);
       }
       else {     
           if (wCount > bCount)
               g.drawString("White is winning with " + wCount+" discs",10,20);
           else if (bCount > wCount)
               g.drawString("Black is winning with " + bCount+" discs",10,20);
           else g.drawString("Currently tied",10,20);
       }
      
   }

    /**
     * The main program.
     * @param    args    command line arguments (ignored)
     */
    public static void main(String [] args) {
		
    Main content;

    if (args.length == 1) {   
            try {
                delay = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.out.println("Command line arg must be an integer");
                System.exit(0);
            }
            content = new Main(delay);
            if (delay >= 0) {
                JFrame window = new JFrame("Othello Game");
                window.setContentPane(content);
                window.setSize(530,557);
                window.setLocation(100,100);
                window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                window.setVisible(true);
            }
        }
        else {
            content = new Main();
            JFrame window = new JFrame("Othello Game");
            window.setContentPane(content);
            window.setSize(530,557);
            window.setLocation(100,100);
            window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            window.setVisible(true);
        }
    }
}
