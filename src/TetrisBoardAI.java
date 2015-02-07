/***
 * 
 * TetrisBoardAI: Defines an simulated intelligence which will control the Tetris game
 * @author Arthur Wuterich
 * @date 5/13/13
 *
 */

public class TetrisBoardAI 
{
	// Grid calcs
	private Grid board = null;
	private Grid testBoard = null;
	
	private Block currentBlocks[] = null;
	private Block currentTestBlocks[] = null;
	private Point2I currentBlockPosition = null;
	
	private Point2I desiredBlockLocations[] = null;
	private int desiredBlockLocationsPos = 0;
	
	// Scoring weights
	private static final double NEIGHBOR_WEIGHT = 1.0;
	private static final double FRAME_NEIGHBOR_WEIGHT = 0.5;
	private static final double Y_POSITION_WEIGHT = 1.0;
	
	// Movement
	private Point2I lastTestBlockMove;
		
	public TetrisBoardAI( Grid board, Block[] blocks )
	{
		this.board = board;
		currentBlocks = blocks;
		currentTestBlocks = new Block[ blocks.length ];		
		currentBlockPosition = new Point2I();
		desiredBlockLocations = new Point2I[ 1000 ];
		lastTestBlockMove = new Point2I();
		
		for( int i = 0; i < 1000; i++ )
		{
			desiredBlockLocations[0] = new Point2I();
		}
	}
	
	// Will return the amount of neighbor blocks / frame
	private double GetNeighborsScore( Block blk )
	{
		double results = 0;
		
		if( blk.position.x+1 == testBoard.GetWidth() )
		{
			results += FRAME_NEIGHBOR_WEIGHT;
		}
		else if( testBoard.GetBlock( blk.position.x+1, blk.position.y ) != null )
		{
			results += NEIGHBOR_WEIGHT;
		}
		
		if( blk.position.x-1 < 0 )
		{
			results += FRAME_NEIGHBOR_WEIGHT;
		}
		else if( testBoard.GetBlock( blk.position.x+1, blk.position.y ) != null )
		{
			results += NEIGHBOR_WEIGHT;
		}
		
		if( blk.position.y+1 == testBoard.GetHeight() )
		{
			results += FRAME_NEIGHBOR_WEIGHT;
		}
		else if( testBoard.GetBlock( blk.position.x, blk.position.y+1 ) != null )
		{
			results += NEIGHBOR_WEIGHT;
		}
		
		if( blk.position.y-1 < 0 )
		{
			results += FRAME_NEIGHBOR_WEIGHT;
		}
		else if( testBoard.GetBlock( blk.position.x, blk.position.y-1 ) != null )
		{
			results += NEIGHBOR_WEIGHT;
		}		
		
		return results * NEIGHBOR_WEIGHT;
	}
	
	// Will return true if the block is positioned on a floor
	private boolean OnFloor( Block blk )
	{
		if( testBoard.GetHeight() >= blk.position.y+1 || testBoard.GetBlock( blk.position.x, blk.position.y+1) != null)
		{
			return true;
		}
		
		return false;
	}
	
	// Returns the value of complete line scores depending on the move
	private int CompleteLinesScore( Block[] blks )
	{
		int score = 0;
		
		for( int i = 0; i < testBoard.GetHeight(); i++ )
		{
			// Check each row for a continuous row of blocks
			int j = 0;
			for( ; j < testBoard.GetWidth(); j++ )
			{
				boolean nextRow = false;
				
				//Cycle through the current test blocks to see if they will complete the line
				for( int k = 0; k < blks.length; k++ )
				{
					if( blks[k].position.x == j && blks[k].position.y == i )
					{
						nextRow = true;
						break;
					}
				}
				
				if( nextRow )
					continue;
				
				// Check for natural blocks
				if( testBoard.GetBlock( j, i ) == null )
					break;
			}	
			
			// if j == testboard's width then the row is continuous
			if( j >= testBoard.GetWidth() )
			{
				score += i * testBoard.GetWidth();
			}
		}
		
		return score;
	}
	
	// Calculates the best move for the AI
	public double CalculateBestMove()
	{
		// Reset move path
		for( int i = 0; i < desiredBlockLocations.length; i++ )
		{
			desiredBlockLocations[i] = null;
		}
		
		// Operation variables
		boolean nextPosition = false;
		double score = 0.0;
		double highestScore = 0.0;
		
		if( currentBlocks == null || currentBlocks[0] == null || currentBlocks[1] == null || currentBlocks[2] == null || currentBlocks[3] == null )
			return 0.0;
		
		// Get the differences between the blocks
		final Point2I b1Diff = new Point2I( currentBlocks[1].position.x - currentBlocks[0].position.x, currentBlocks[1].position.y - currentBlocks[0].position.y  );
		final Point2I b2Diff = new Point2I( currentBlocks[2].position.x - currentBlocks[0].position.x, currentBlocks[2].position.y - currentBlocks[0].position.y  );
		final Point2I b3Diff = new Point2I( currentBlocks[3].position.x - currentBlocks[0].position.x, currentBlocks[3].position.y - currentBlocks[0].position.y  );
		
		// Copy the movable blocks
		for( int i = 0; i < 4; i++ )
		{
			currentTestBlocks[i] = new Block( currentBlocks[i] );
		}
				
		// Create a new board ( might not be needed )
		testBoard = new Grid( board );
		
		// Cycle through all possible positions of the 0th piece
		for( int x = 0; x < testBoard.GetWidth(); x++ )
		{
			for( int y = 0; y < testBoard.GetHeight(); y++ )
			{
				nextPosition = false;
				score = 0.0;
				
				// Move the blocks into position
				currentTestBlocks[0].position.x = x;
				currentTestBlocks[0].position.y = y;
				
				currentTestBlocks[1].position.x = x + b1Diff.x;
				currentTestBlocks[1].position.y = y + b1Diff.y;
				
				currentTestBlocks[2].position.x = x + b2Diff.x;
				currentTestBlocks[2].position.y = y + b2Diff.y;
				
				currentTestBlocks[3].position.x = x + b3Diff.x;
				currentTestBlocks[3].position.y = y + b3Diff.y;	
				
				// Check to see if the blocks are intersecting any blocks or the frame
				for( int i = 0; i < currentBlocks.length; i++ )
				{
					if( currentTestBlocks[i].position.x < 0 || currentTestBlocks[i].position.x >= testBoard.GetWidth() ||
							currentTestBlocks[i].position.y < 0 || currentTestBlocks[i].position.y >= testBoard.GetHeight() ||
						testBoard.GetBlock( currentTestBlocks[i].position.x, currentTestBlocks[i].position.y) != null )
					{
						nextPosition = true;
						break;
					}
				}
				
				// Continue if the previous operation did not cycle through completely
				// - This will detect blocks outside of the grid and intersections
				if( nextPosition )
				{
					continue;
				}
				
				// Check that this configuration has either a frame floor or a block floor
				for( int i = 0; i < currentTestBlocks.length; i++ )
				{
					if( OnFloor( currentTestBlocks[i]) )
					{
						nextPosition = false;
						break; // Break out because the rest of the calculations don't matter
					}
					
					nextPosition = true;
				}
				
				// Continue if the previous operation did not detect a floor block / frame
				if( nextPosition )
				{
					continue;
				}
				
				// At this position we have a array of blocks which:
				//  - has a floor block / frame
				//  - does not intersect with any other block
				//  - has a valid position on the grid
				//  - ready for scoring
				
				// Score the blocks
				
				// Get the score for neighbors
				for( int i = 0; i < currentTestBlocks.length; i++ )
				{
					score += GetNeighborsScore( currentTestBlocks[i] );
				}
				
				// Get the score for positions
				for( int i = 0; i < currentTestBlocks.length; i++ )
				{
					score += currentTestBlocks[i].position.y * Y_POSITION_WEIGHT;
				}
				
				// Check for line clears
				score += CompleteLinesScore( currentTestBlocks );
				
				// Now we have a score for this move, check if the score just calculated is higher than the previous highest score
				if( score >= highestScore && SetMovePath() )
				{
					highestScore = score;
				}
			}			
		}
		
		return highestScore;
	}
	
	private void MoveTestBlocks( int x, int y )
	{
		lastTestBlockMove = new Point2I( x, y );
		for( int i = 0 ; i < currentTestBlocks.length; i++ )
		{
			currentTestBlocks[i].position.x += x;
			currentTestBlocks[i].position.y += y;
		}
	}
	
	private void RevertTestBlocksMove( )
	{
		MoveTestBlocks( -lastTestBlockMove.x, -lastTestBlockMove.y );
		lastTestBlockMove = new Point2I( 0, 0 );
	}
	
	private boolean CheckTestBlocksForCollisions()
	{
		for( int i = 0; i < currentTestBlocks.length; i++ )
		{
			if( currentTestBlocks[i].position.x < 0 || currentTestBlocks[i].position.x >= testBoard.GetWidth() ||
				currentTestBlocks[i].position.y < 0 || currentTestBlocks[i].position.y >= testBoard.GetHeight() ||
				testBoard.GetBlock( currentTestBlocks[i].position.x, currentTestBlocks[i].position.y) != null )
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean SetMovePath() 
	{
		desiredBlockLocations[desiredBlockLocationsPos++] = new Point2I( currentTestBlocks[0].position.x, currentTestBlocks[0].position.y );
		while( currentTestBlocks[0].position.y > currentBlocks[0].position.y )
		{
			// Check for movement directly up
			MoveTestBlocks( 0, -1 );
			
			// If there is a collision then there might need to be x movement
			if ( CheckTestBlocksForCollisions() )
			{
				// Check to the right for possible movements
				MoveTestBlocks( 1, 0 );
				if ( CheckTestBlocksForCollisions() )
				{
					RevertTestBlocksMove();
				}
				else
				{
					// The movement up right is successful; use it
					desiredBlockLocations[desiredBlockLocationsPos++] = new Point2I( currentTestBlocks[0].position.x, currentTestBlocks[0].position.y );
					continue;					
				}
		
				// Check to the left for possible movements
				MoveTestBlocks( -1, 0 );
				if ( CheckTestBlocksForCollisions() )
				{
					RevertTestBlocksMove();
				}
				else
				{
					// The movement up right is successful; use it
					desiredBlockLocations[desiredBlockLocationsPos++] = new Point2I( currentTestBlocks[0].position.x, currentTestBlocks[0].position.y );
					continue;					
				}
				
				// Conclude the position is unreachable
				return false;				
			}
			else
			{
				// The movement directly up is successful; use it
				desiredBlockLocations[desiredBlockLocationsPos++] = new Point2I( currentTestBlocks[0].position.x, currentTestBlocks[0].position.y );
				continue;
			}
		}
		
		return true;
	}
	
	private void PopDesiredElement()
	{
		if( desiredBlockLocationsPos == 0 )
			return;
		
		for( int i = 0; i < desiredBlockLocations.length; i++ )
		{
			if( desiredBlockLocations[i] != null )
			{
				desiredBlockLocations[i] = null;
				System.out.println( "popped element ");
				return;
			}
		}
	}

	public int GetDesiredX()
	{
		for( int i = 0; i < desiredBlockLocations.length; i++ )
		{
			if( desiredBlockLocations[i] != null )
				return desiredBlockLocations[i].x;
		}
		return 0;
	}
	
	public int GetDesiredY()
	{
		for( int i = 0; i < desiredBlockLocations.length; i++ )
		{
			if( desiredBlockLocations[i] != null )
				return desiredBlockLocations[i].y;
		}
		return 0;
	}
	
	// Will control the keys array to allow access to the controls
	public void KeyboardInput( boolean keyArray[] )
	{
		if( currentBlocks == null || currentBlocks[0] == null )
			return;
		
		// Update positions of blocks
		currentBlockPosition.x = currentBlocks[0].position.x;
		currentBlockPosition.y = currentBlocks[0].position.y;		
		
		// Reset key states
		keyArray[39] = false;
		keyArray[68] = false;		
		keyArray[37] = false;
		keyArray[65] = false;
		//keyArray[83] = true;
		//keyArray[40] = true;
		if( currentBlockPosition.x < GetDesiredX() )
		{
			keyArray[39] = true;
			keyArray[68] = true;
			keyArray[83] = false;
			keyArray[40] = false;
		}
		
		if( currentBlockPosition.x > GetDesiredX() )
		{
			keyArray[37] = true;
			keyArray[65] = true;
			keyArray[83] = false;
			keyArray[40] = false;			
		}
		
		if( currentBlockPosition.y > GetDesiredY() )
		{			
			PopDesiredElement();		
		}
		
		System.out.println( currentBlockPosition.y + " ( " + GetDesiredX() + ", " + GetDesiredY() + " )" );
	}

}
