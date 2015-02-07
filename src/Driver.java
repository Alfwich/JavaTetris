/***
 * 
 * Driver:	Test chassis for the Tertris game
 * @author Arthur Wuterich
 * @date 5/13/13
 *
 */


public class Driver 
{
	public static void main(String[] args) throws InterruptedException 
	{
		// Standard grid
		Tetris game = new Tetris( 384, 648, 24 );
		
		// Send control into the game main loop	
		game.main();
		System.exit( 0 );
	}

}
