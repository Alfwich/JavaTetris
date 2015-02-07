/**
 * Timer: Saves the current milliseconds from the System class, and will give time elapsed information
 * @author Arthur Wuterich
 * @date 3/03/2013
 * 
 */

public class Timer 
{
	private long tick;
	private long pausedTick;
	
	private boolean running;
	private boolean paused;
	
	public Timer()
	{
		running = false;
		paused = false;
		
		tick = 0;
		pausedTick = 0;
	}
	
	// Starts the timer saving the current milliseconds
	public void Start()
	{
		tick = System.currentTimeMillis();
		pausedTick = 0;
		running = true;
		paused = false;
	}
	
	public boolean IsStarted()
	{
		return running;
	}
	
	// Returns time elapsed since start if running, the current paused time if paused, or 0 if not running
	public long GetTicks()
	{
		if( running )
			return System.currentTimeMillis() - tick;
		
		if( paused )
			return pausedTick;
		
		return 0L;
	}
	
	// Stops the timer
	public void Stop()
	{
		tick = 0;
		running = false;
	}
	
	// Pauses the timer saving the current tick
	public void Pause()
	{
		if( !running )
			return;
		
		running = false;
		paused = true;
		pausedTick = tick;
	}
	
	// Unpauses the timer
	public void Unpause()
	{
		if( !paused )
			return;
		
		tick += System.currentTimeMillis() - pausedTick;
		paused = false;
		running = true;
	}

}
