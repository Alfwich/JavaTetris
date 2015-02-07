import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JPanel;

/***
 * 
 * TetrisGrid: Defines a grid which is to be used as a Tetris grid
 * @author Arthur Wuterich
 * @date 5/13/13
 *
 */

@SuppressWarnings("serial")
public class TetrisGrid extends JPanel
{
	private static final int BLOCKS_TO_CONTROL = 4;
	protected Grid board;
	protected Block[] currentBlocks = new Block[BLOCKS_TO_CONTROL];
	private BLOCK_TYPE currentType = BLOCK_TYPE.SQUARE;
	
	private int blockSize = 32;
	
	private boolean pieceIsFalling = false;
	private boolean cantAddPieces = false;
	
	private int piecesRemoved = 0;
	
	public static enum BLOCK_TYPE { SQUARE, RHELBOW, LHELBOW, LHSTEP, RHSTEP, TEE, LINE };
	private static final Random rng = new Random();
	private static final BLOCK_TYPE[] BLOCK_TYPES = { BLOCK_TYPE.SQUARE, BLOCK_TYPE.RHELBOW, BLOCK_TYPE.LHELBOW, BLOCK_TYPE.LHSTEP, BLOCK_TYPE.RHSTEP, BLOCK_TYPE.TEE, BLOCK_TYPE.LINE };
		
	public TetrisGrid( int width, int height, int blockSize )
	{
		setLayout( null );
		setSize( width+1, height+1 );
		this.blockSize = blockSize;
		
		board = new Grid( (int) Math.floor( width / blockSize ), (int) Math.floor( height / blockSize ) );
	}
	
	private BLOCK_TYPE GetRandomBlockType()
	{
		return BLOCK_TYPES[ Math.abs( rng.nextInt() % BLOCK_TYPES.length ) ];
	}
			
	// Adds a random block piece
	public boolean AddBlockPiece()
	{
		currentType = GetRandomBlockType();
		return AddBlockPiece( currentType );
	}
	
	// Adds a block piece to the grid
	public boolean AddBlockPiece( BLOCK_TYPE type )
	{
		if( pieceIsFalling || cantAddPieces || currentBlocks[0] != null )
			return false;
		
		int centerX = (int)Math.floor( board.GetWidth() / 2);
		currentType = type;
			
		switch( type )
		{
		case SQUARE:
			Block.SetGenerationColor( Color.yellow );
			currentBlocks[0] = new Block( centerX	, 0 );			
			currentBlocks[1] = new Block( centerX+1	, 0 );
			currentBlocks[2] = new Block( centerX+1	, 1 );
			currentBlocks[3] = new Block( centerX	, 1 );
			break;

		case LHELBOW:
			Block.SetGenerationColor( Color.blue );
			currentBlocks[0] = new Block( centerX+1	, 0 );
			currentBlocks[1] = new Block( centerX+1	, 1 );
			currentBlocks[2] = new Block( centerX+1	, 2 );
			currentBlocks[3] = new Block( centerX	, 2 );			
			break;			
			
		case RHELBOW:
			Block.SetGenerationColor( Color.orange );
			currentBlocks[0] = new Block( centerX	, 0 );
			currentBlocks[1] = new Block( centerX	, 2 );
			currentBlocks[2] = new Block( centerX	, 1 );
			currentBlocks[3] = new Block( centerX+1	, 2 );			
			break;
			
		case LHSTEP:
			Block.SetGenerationColor( Color.red );
			currentBlocks[0] = new Block( centerX+1	, 0 );			
			currentBlocks[1] = new Block( centerX+1	, 1 );
			currentBlocks[2] = new Block( centerX	, 1 );
			currentBlocks[3] = new Block( centerX	, 2 );
			break;
			
		case LINE:
			Block.SetGenerationColor( Color.cyan );
			currentBlocks[0] = new Block( centerX	, 0 );			
			currentBlocks[1] = new Block( centerX	, 1 );
			currentBlocks[2] = new Block( centerX	, 2 );
			currentBlocks[3] = new Block( centerX	, 3 );
			break;
			
		case RHSTEP:
			Block.SetGenerationColor( Color.green );
			currentBlocks[0] = new Block( centerX	, 0 );			
			currentBlocks[1] = new Block( centerX	, 1 );
			currentBlocks[2] = new Block( centerX+1	, 1 );
			currentBlocks[3] = new Block( centerX+1	, 2 );
			break;
			
		case TEE:
		default:
			Block.SetGenerationColor( new Color( 0x7D26CD ) );
			currentBlocks[0] = new Block( centerX-1	, 0 );			
			currentBlocks[1] = new Block( centerX	, 1 );
			currentBlocks[2] = new Block( centerX	, 0 );			
			currentBlocks[3] = new Block( centerX+1	, 0 );		
			break;
		}
		
		pieceIsFalling = true;
		
		// Add the new piece to the grid
		for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
		{
			if( board.GetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y) != null )
			{
				cantAddPieces = true;
				pieceIsFalling = false;
				break;
			}
			
			board.SetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y, currentBlocks[i] );
		}
				
		return !cantAddPieces;
	}
	
	// Returns true if the block below one of the current blocks is not null or
	// the y value of the blocks is equal to 0
	private boolean CheckForCollisions( int x, int y )
	{
		for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
		{	
			if( currentBlocks[i].position.y+y == board.GetHeight() )
				return true;
			
			if( currentBlocks[i].position.x + x < 0 || currentBlocks[i].position.x + x >= board.GetWidth() )
				return true;
			
			if( currentBlocks[i].position.y + y < 0 || currentBlocks[i].position.y + y >= board.GetHeight() )
				return true;
				
			Block testBlock = board.GetBlock( currentBlocks[i].position.x+x, currentBlocks[i].position.y+y);
			
			if( testBlock != null )
			{
				if( testBlock != currentBlocks[0] &&
					testBlock != currentBlocks[1] &&
					testBlock != currentBlocks[2] &&
					testBlock != currentBlocks[3] )
				return true;
			}
		}
		
		return false;
	}
	
	// Will rotate the current piece clockwise
	public void RotatePiece()
	{
		if( currentType == BLOCK_TYPE.SQUARE  )
			return;
		
		// Get the center point for the rotation
		Point2I centerPoint = new Point2I( currentBlocks[2].position.x, currentBlocks[2].position.y );
		
		// Remember the old points in case there is collision
		Point2I oldPoints[] = new Point2I[BLOCKS_TO_CONTROL];
		boolean resetPositions = false;
		
		// Set the blocks for the old position to empty, also save old positions
		for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
		{
			board.SetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y, null );
			oldPoints[i] = new Point2I(currentBlocks[i].position.x,currentBlocks[i].position.y);
		}
		
		for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
		{
			// Get the diffrence between the center block and the current block
			int xDiff = currentBlocks[i].position.x - centerPoint.x;
			int yDiff = currentBlocks[i].position.y - centerPoint.y;
			
			// Rotate the block
			currentBlocks[i].position.x = centerPoint.x - yDiff;
			currentBlocks[i].position.y = centerPoint.y + xDiff;
			
			// If the new position is either colliding with a block, or if out of the screen, reset the blocks
			if( currentBlocks[i].position.x < 0 || currentBlocks[i].position.x >= board.GetWidth() ||
			    currentBlocks[i].position.y < 0 || currentBlocks[i].position.y >= board.GetHeight() ||
			    board.GetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y ) != null   )
			{
				currentBlocks[i].position.x = oldPoints[i].x;
				currentBlocks[i].position.y = oldPoints[i].y;					
				resetPositions = true;
				break;
			}
			
			// Set the block in the block array
			board.SetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y, currentBlocks[i] );
		}			
		
		// If there is an error / collision reset the block positions
		if( resetPositions )
		{
			for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
			{
				board.SetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y, null );
				
				currentBlocks[i].position.x = oldPoints[i].x;
				currentBlocks[i].position.y = oldPoints[i].y;
				
				board.SetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y, currentBlocks[i] );
				//blocks[currentBlocks[i].position.x][currentBlocks[i].position.y] = currentBlocks[i];
			}			
		}
	}
	
	public boolean HasFallingPiece()
	{
		return pieceIsFalling;
	}
	
	public void ClearGrid()
	{
		
		board.Clear();
		
		for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
		{
			currentBlocks[i] = null;
		}			
		
		pieceIsFalling = false;
		cantAddPieces = false;
	}
	
	public BLOCK_TYPE GetCurrentBlockType()
	{
		return currentType;
	}
	
	public boolean AbleToAddBlocks()
	{
		return !cantAddPieces;
	}
	
	// Moves the pieces for the current controlled board
	public void MovePieces( int x, int y )
	{
		if( currentBlocks == null || !pieceIsFalling )
			return;
		
		// If there are collisions, stop
		if( CheckForCollisions( x, y ) )
		{
			if( x != 0)
				return;
			
			for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
			{
				currentBlocks[i] = null;
			}
			
			pieceIsFalling = false;
			RemoveCompleteLines();
			return;
		}
		
		// Clear the block grid of the current blocks
		for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
		{
			board.SetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y, null );
		}
		
		// Set the new positions to the current blocks
		for( int i = 0; i < BLOCKS_TO_CONTROL; i++ )
		{
			currentBlocks[i].position.x += x;			
			currentBlocks[i].position.y += y;
			board.SetBlock( currentBlocks[i].position.x, currentBlocks[i].position.y, currentBlocks[i] );
		}		
	}
	
	public int GetPiecesRemoved()
	{
		int tmp = piecesRemoved;
		piecesRemoved = 0;
		return tmp;
	}
	
	private void RemoveCompleteLines()
	{
		piecesRemoved = 0;
		for( int i = 0; i < board.GetHeight(); i++ )
		{
			// Check each row for a continuous row of blocks
			int j = 0;
			for( ; j < board.GetWidth(); j++ )
			{
				if( board.GetBlock( j, i ) == null )
					break;
			}	
			
			// if j == size.x then the row is continuous; remove the line and push all the blocks down
			if( j >= board.GetWidth() )
			{
				for( j = 0; j < board.GetWidth(); j++ )
				{
					board.SetBlock( j, i, null );
					piecesRemoved++;
				}
				
				for( int k = i-1; k > 0; k-- )
				{
					for( j = 0; j < board.GetWidth(); j++ )
					{
						board.SetBlock( j, k+1, board.GetBlock( j, k ) );						
					}
				}
				// Dec i to recheck the current row
				i--;
			}
		}
	}
	
	protected void paintComponent( Graphics g )
	{
		for( int i = 0; i < board.GetHeight(); i++ )
		{
			for( int j = 0; j < board.GetWidth(); j++)
			{				
				if( board.GetBlock( j, i ) == null )
				{
					g.setColor( new Color( 0x000000 ) );
					g.fillRect( j*blockSize, i*blockSize, blockSize, blockSize );					
					
					g.setColor( new Color( 0x333333 ) );
					g.fillRect( j*blockSize+1, i*blockSize+1, blockSize-2, blockSize-2 );
					
					g.setColor( new Color( 0x555555 ) );
					g.drawRect( j*blockSize, i*blockSize, blockSize, blockSize );					
				}
				else
				{
					g.setColor( board.GetBlock( j, i ).color );
					g.fillRect( j*blockSize, i*blockSize, blockSize, blockSize );
					
					g.setColor( new Color( 0, 0, 0, 192 ) );
					g.drawRect( j*blockSize, i*blockSize, blockSize, blockSize );
					
					g.setColor( new Color( 255, 255, 255, 192 ) );
					g.drawRect( j*(blockSize)+1, i*(blockSize)+1, (blockSize-1), (blockSize-1) );
					g.drawRect( j*(blockSize)+1, i*(blockSize)+1, (blockSize-1), (blockSize-1) );
					
					g.setColor( new Color( 255, 255, 255, 128 ) );
					g.drawRect( j*(blockSize)+2, i*(blockSize)+2, (blockSize-2), (blockSize-2) );
					
					g.setColor( new Color( 255, 255, 255, 64 ) );
					g.drawRect( j*(blockSize)+3, i*(blockSize)+3, (blockSize-3), (blockSize-3) );
				}
			}
		}
	}
}
