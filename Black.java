public class Black {

    /** 
     *  This method calls the appropriate strategy.
     * @return   game    the resulting state of the game
     */
    public Game strategy(Game game, boolean done, int color) {
        
        return possibleStrategy(game,done,color);
    }

    /**
     *  @return possible moves
     */
    public Game possibleStrategy(Game game, boolean done, int color) {

        int row = (int)(Math.random()*(game.HEIGHT-2)) + 1;
        int column = (int)(Math.random()*(game.WIDTH-2)) + 1;
        
        while (!done && !game.legalMove(row,column,color,true)) {
            row = (int)(Math.random()*(game.HEIGHT-2)) + 1;
            column = (int)(Math.random()*(game.WIDTH-2)) + 1;
        }
        
        if (!done)
            game.board[row][column] = color;

        return game;
    }
}
