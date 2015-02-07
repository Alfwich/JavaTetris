/***
 * 
 * Grid:	Defines a block grid
 * @author Arthur Wuterich
 * @date 5/13/13
 *
 */


public class Grid 
{
	private Block[][] blocks;
	private Point2I size = new Point2I();
	
	// Default
	public Grid( int x, int y )
	{
		size.x = x;
		size.y = y;
		blocks = new Block[size.x][size.y];		
	}
	
	// Copy
	// Provides a shallow copy of the blocks
	public Grid( Grid g )
	{	
		this( g.size.x, g.size.y );
		
		for( int y = 0; y < g.size.y; y++ )
		{
			for( int x = 0; x < g.size.x; x++ )
			{
				Block copyBlock = g.GetBlock( x, y );
				SetBlock( x, y, copyBlock );
			}			
		}
	}	
	
	// Checks that the (x,y) are contained within the grid
	private boolean CheckBounds( int x, int y )
	{
		// Return false if the array is out of bounds
		if( x < 0 || x >= size.x || y < 0 || y > size.y )
		{
			return true;
		}
		
		return false;
	}
	
	public Block GetBlock( int x, int y )
	{
		if( CheckBounds( x, y ) )
		{
			return null;
		}
		
		return blocks[x][y];
	}
	
	public boolean SetBlock( int x, int y, Block blk )
	{
		if( CheckBounds( x, y ) )
		{
			return false;
		}
		
		blocks[x][y] = blk;
		return true;
	}
	
	public Point2I GetSize()
	{
		return new Point2I( size.x, size.y );
	}
	
	public int GetWidth()
	{
		return size.x;
	}
	
	public int GetHeight()
	{
		return size.y;
	}
	
	public void Clear()
	{
		for( int y = 0; y < size.y; y++ )
		{
			for( int x = 0; x < size.x; x++ )
			{
				SetBlock( x, y, null );
			}			
		}		
	}
	
	
	

}
