import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author Arthur Wuterich
 * @project Tetris
 * @date 02-27-2013
 *
 */

// Main Tetris game driver
@SuppressWarnings("serial")
public final class Tetris extends JFrame implements KeyListener, ActionListener
{
	// Constants
	private final int FPS = 60;
	private final int DEFAULT_SLOW_FALL_DELAY = 1000;
	private final int DEFAULT_FAST_FALL_DELAY = 50;
	private final int DEFAULT_MOVE_DELAY = 200;	
	private final String SCORE_PREFIX = "Score:\t";
	
	// Timers
	private Timer fpsTimer = new Timer();
	private Timer moveTimer = new Timer();
	private Timer fallTimer = new Timer();
	private Timer aiResetTimer = new Timer();
	
	// Delays
	private int fallDelay = DEFAULT_SLOW_FALL_DELAY;
	private int moveDelay = DEFAULT_MOVE_DELAY;
	
	// Frame components
	TetrisGrid mainGrid;
	TetrisGrid nextGrid;
	JLabel scoreLable;
	JButton resetButton;
	JButton playButton;
	JButton highScores;
	
	// Display high scores
	JFrame highScoreWindow;
	JTextArea highScoresText;
	
	// Add new high scores
	JFrame addHighScoreWindow;
	JTextPane scoreLabel;
	JTextPane highScoreNameAdd;
	JButton addScore;
	
	// Operational variables
	String highScoreText = "";
	boolean addScoreVisibility = false;
		
	// Game variables
	int score = 0;
	
	// Keys
	private boolean keys[] = new boolean[256];
	
	Tetris( int width, int height, int blockSize )
	{
		// Add the main grid to the frame and set it up
		mainGrid = new TetrisGrid( width-( blockSize * 3 + 60 ), height-100, blockSize );
		mainGrid.setLocation( 25, 25 );
		add( mainGrid );
		
		// Add the next grid to the main frame
		nextGrid = new TetrisGrid( blockSize * 3, blockSize * 4, blockSize );
		nextGrid.setLocation( width-( blockSize * 3 + 25 ), 25 );
		add( nextGrid );
		
		// Add score label to the frame
		scoreLable = new JLabel();
		scoreLable.setHorizontalAlignment( JLabel.LEFT );
		scoreLable.setVerticalAlignment( JLabel.TOP );		
		scoreLable.setSize( blockSize * 3, 28);
		scoreLable.setFont( new Font("Arial", Font.PLAIN, 12) );
		scoreLable.setForeground( new Color( 0xffffff ) );
		scoreLable.setBackground( new Color( 0x333333 ) );
		scoreLable.setOpaque(true);
		scoreLable.setLocation( width-( blockSize * 3 + 25 ), 35 + blockSize * 4 );
		scoreLable.setText( SCORE_PREFIX + score );
		this.add( scoreLable );
		
		resetButton = new JButton();
		resetButton.setText( "Reset" );
		resetButton.setSize( blockSize * 5, 25 );
		resetButton.setLocation( width-( blockSize * 3 + 25 ), 35 + blockSize * 4 + 33 );
		resetButton.addActionListener(this);
		this.add(resetButton);
		
		playButton = new JButton();
		playButton.setText( "Play" );
		playButton.setSize( blockSize * 5, 25 );
		playButton.setLocation( width-( blockSize * 3 + 25 ), 35 + blockSize * 4 + 63 );
		playButton.addActionListener(this);
		this.add(playButton);
		
		highScores = new JButton();
		highScores.setText( "HighScores" );
		highScores.setSize( blockSize * 5, 25 );
		highScores.setLocation( width-( blockSize * 3 + 25 ), 35 + blockSize * 4 + 92 );
		highScores.addActionListener(this);
		this.add( highScores );
		
		// High score display frame
		highScoreWindow = new JFrame();
		
		highScoresText = new JTextArea();
		highScoresText.setEditable( false );
		highScoreWindow.add( highScoresText );
		
		highScoreWindow.setTitle( "Tetris High Scores" );
		highScoreWindow.setResizable( false );
		highScoreWindow.setSize( 300, 500 );
		
		// Add high score frame
		addHighScoreWindow = new JFrame();
		addHighScoreWindow.setLayout( new GridLayout(3,1));
		
		// Styles for add score window
		SimpleAttributeSet aSet = new SimpleAttributeSet();  
        StyleConstants.setAlignment(aSet, StyleConstants.ALIGN_CENTER);   
        StyleConstants.setFontFamily(aSet, "lucida typewriter bold");  
        StyleConstants.setFontSize(aSet, 18);
		
		SimpleAttributeSet bSet = new SimpleAttributeSet();  
        StyleConstants.setAlignment(bSet, StyleConstants.ALIGN_CENTER);   
        StyleConstants.setFontFamily(bSet, "lucida typewriter bold");  
        StyleConstants.setFontSize(bSet, 24); 
                
		scoreLabel = new JTextPane();
		StyledDocument doc = scoreLabel.getStyledDocument();
		doc.setParagraphAttributes( 0, 104, bSet, false);
		scoreLabel.setEditable( false );
		scoreLabel.setBackground( new Color( 230, 230, 230) );
		addHighScoreWindow.add( scoreLabel );
		
		highScoreNameAdd = new JTextPane();
		doc = highScoreNameAdd.getStyledDocument();
		doc.setParagraphAttributes( 0, 104, aSet, false);		
		highScoreNameAdd.setText( "Player" );
		highScoreNameAdd.setBackground( Color.white );
		addHighScoreWindow.add( highScoreNameAdd );
		
		addScore = new JButton();
		addScore.setText( "Submit Score");
		addScore.addActionListener( this );
		addHighScoreWindow.add( addScore );
		
		addHighScoreWindow.setTitle( "Add High Score");
		addHighScoreWindow.setResizable( false );
		addHighScoreWindow.setSize( 250, 250 );
		addHighScoreWindow.setLocationRelativeTo(null);		
		
		
		// Configure the game frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle( "Tetris Clone" );
		setResizable( false );
		setLayout( null );
		setSize( width + blockSize * 2, height );
		setFocusable(true);		
		this.addKeyListener( this );
		setVisible( true );
		setLocationRelativeTo(null);
		
		// Sets the location of the high score window to be right next to the main window
		highScoreWindow.setLocation( this.getX() + width + blockSize * 2 + 10, this.getY() );		
	}
	
	// Will sleep for the required ms to simulate a FPS
	// - the fpsTimer will calculate the time between rendering and adjust the sleep duration
	private void LimitFrames() throws InterruptedException
	{
		int milliForFps = (int) Math.round( 1000.0 / FPS );
		
		if( milliForFps - fpsTimer.GetTicks() < 0 )
		{
			fpsTimer.Start();
			return;
		}
		
		Thread.sleep( milliForFps - fpsTimer.GetTicks() );
		fpsTimer.Start();
	}
	
	// Returns the high score string from sql database
	public static String GetHighScoreString( String url )
	{
		
		String HTML = "";
		
		try 
		{
			// Read the high score html from web address
			URLConnection connection = new URL( url ).openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader( connection.getInputStream() ) );
	        String inputLine;
	        
	        while ((inputLine = in.readLine()) != null)
	        {
	        	HTML += inputLine + "\n";
	        }
	        
	        in.close();
	        
	        // Split on <br> tags and reformat with line breaks
	        String[] scores = HTML.split("<br>");
	        HTML = "";
	        for( String s : scores)
	        {
	        	HTML += s + "\n";
	        }
	               
		} 
		catch (MalformedURLException e) 
		{
			System.out.println( "Failed to open URL!" );
			return "";
		} 
		catch (IOException e) 
		{
			System.out.println( "Failed to open URL!" );
			return "";
		}
		
		return HTML;
	}
	
	// Really bad sanitizing function *** REFACTOR ***
	private static String Sanitize( String s )
	{
		s = s.replace( '"', ' ');
		s = s.replace( '\'', ' ');
		s = s.replace( ';', ' ');
		s = s.replace( ',', ' ');
		
		return s;
	}
	// Will submit a high score to sql database
	public static void SubmitHighScore( String name, int score )
	{
		try 
		{
			// Connect to website
			HttpURLConnection connection = (HttpURLConnection)new URL( "http://aw-games.com/misc/setHighScore.php" ).openConnection();
			
			// Post data about high score and name
			String post_data = "n=" + URLEncoder.encode( Sanitize( name ), "UTF-8" ) + "&s=" + URLEncoder.encode( Integer.toString( score ), "UTF-8" );
			
			// Flags to allow writing and sending post data
			connection.setDoOutput( true );
			connection.setRequestMethod( "POST" );
			connection.setFixedLengthStreamingMode(post_data.getBytes().length);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			// Send the data
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.print(post_data);
			out.close();		
		} 
		catch (MalformedURLException e) 
		{
			System.out.println( "Failed to open URL!" );
			return;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return;
		}
	}	
	
	public void main() throws InterruptedException 
	{
		// Prime the next grid with a new block
		nextGrid.AddBlockPiece();
						
		// Main loop
		while( true )
		{
			// Falling
			if( fallTimer.GetTicks() > fallDelay )
			{
				mainGrid.MovePieces( 0, 1 );
				fallTimer.Start();
			}
			
			// Add a new block if there is non
			if( mainGrid.AddBlockPiece( nextGrid.GetCurrentBlockType() ) )
			{
				score += mainGrid.GetPiecesRemoved();
				scoreLable.setText( SCORE_PREFIX + score );
				nextGrid.ClearGrid();
				nextGrid.AddBlockPiece();
				addScoreVisibility = false;					
			}
			else if( !mainGrid.AbleToAddBlocks() && !addScoreVisibility )
			{
				
				scoreLabel.setText( "Add score\n" + score );
				addScoreVisibility = true;
				addHighScoreWindow.setVisible( addScoreVisibility );
			}
														
			// Redraw grid
			repaint();
						
			// Exit key
			if( keys[81] )
			{
				break;
			}
			
			// Movement
			if( moveTimer.GetTicks() > moveDelay )
			{
				// D / RightArrow
				if( keys[39]||keys[68] )
				{
					mainGrid.MovePieces( 1, 0 );
					moveTimer.Start();
				}
				
				// A / LeftArrow				
				if( keys[37]||keys[65] )
				{
					mainGrid.MovePieces( -1, 0 );
					moveTimer.Start();
				}

				// W / UpArrow
				if( keys[87]||keys[38] )
				{
					mainGrid.RotatePiece();
					moveTimer.Start();					
				}
			}
			
			// S / DownArrow
			// Makes the falling blocks move faster
			if( keys[83]||keys[40] )
			{
				fallDelay = DEFAULT_FAST_FALL_DELAY;
			}
			else
			{
				fallDelay = DEFAULT_SLOW_FALL_DELAY;
			}
			
			// Will prevent the scene from being rendered too frequently ( maintains a fps )
			LimitFrames();
		}
		
		// Exit the application
		System.exit( 0 );
	}
	
	@Override
	public void keyPressed( KeyEvent e ) 
	{
		// Update key array
		if( e.getKeyCode() >= 256)
			return;
		
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased( KeyEvent e ) 
	{
		// Update key array
		if( e.getKeyCode() >= 256)
			return;
		
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped( KeyEvent e ) 
	{
	}

	// Resets the game
	public void actionPerformed( ActionEvent e ) 
	{
		// Reset the game
		if( e.getSource() == resetButton )
		{
			mainGrid.ClearGrid();
			nextGrid.ClearGrid();
			nextGrid.AddBlockPiece();
			score = 0;
			scoreLable.setText( SCORE_PREFIX + score );
			fallTimer.Start();
			moveTimer.Start();
			aiResetTimer.Start();
			this.requestFocus();
		}
		
		// Start game play
		if( e.getSource() == playButton )
		{
			if( fallTimer.IsStarted() )
			{
				fallTimer.Stop();
				moveTimer.Stop();
				aiResetTimer.Stop();
				mainGrid.ClearGrid();
				playButton.setText( "Play" );
				this.requestFocus();
			}
			else
			{
				fallTimer.Start();
				moveTimer.Start();
				aiResetTimer.Start();
				playButton.setText( "End" );
				this.requestFocus();
			}
		}
		
		// Show high score information
		if( e.getSource() == highScores )
		{
			highScoreText = GetHighScoreString( "http://aw-games.com/misc/getHighScores.php" );
			highScoresText.setText( "High Scores:\n" + highScoreText );
			highScoreWindow.setVisible( true );
			this.requestFocus();
		}
		
		// Add high score to the high scores
		if( e.getSource() == addScore )
		{
			SubmitHighScore( highScoreNameAdd.getText() , score );
			addHighScoreWindow.setVisible( false );
			
			highScoreText = GetHighScoreString( "http://aw-games.com/misc/getHighScores.php" );
			highScoresText.setText( "High Scores:\n" + highScoreText );
			highScoreWindow.setVisible( true );
			this.requestFocus();
		}
	}	
	
}
