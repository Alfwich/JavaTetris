import java.awt.Color;
import java.util.Random;

/***
 * 
 * Block:	Defines a block object of position and color
 * 			Also holds static block generation state
 * @author Arthur Wuterich
 * @date 5/13/13
 *
 */

public final class Block
{
	// Generation color of blocks
	private static Color CurrentGenerationColor = Color.red;
	private static final Random rng = new Random();
	
	// Potential colors for the block
	private static final Color[] colors = {
											Color.RED, Color.GREEN, Color.BLUE, 
											Color.YELLOW, Color.ORANGE, Color.MAGENTA, 
											Color.CYAN, Color.BLACK, Color.PINK };
	
	// Block data
	protected Color color = new Color( 0xFFFFFF );
	protected Point2I position = new Point2I();
	
	public Block( int x, int y )
	{
		position.x = x;
		position.y = y;
		color = CurrentGenerationColor;
	}
	
	// Copy
	public Block( Block blk )
	{
		position.x = blk.position.x;
		position.y = blk.position.y;
		color = blk.color;
	}	
	
	public static void SetGenerationColor( Color clr )
	{
		CurrentGenerationColor = clr;
	}
	
	public static void SetRandomGenerationColor()
	{
		CurrentGenerationColor = GetRandomColor();
	}
	
	private static Color GetRandomColor()
	{
		return colors[ Math.abs( rng.nextInt() % ( colors.length ) )];
	}
		
	public void SetBlockColor( Color color )
	{
		this.color = color;
	}
}
