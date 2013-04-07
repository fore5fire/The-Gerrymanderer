
/*
Timer allows you to repeatedly call a method at a given speed, for a given amount of time.


IMPORTANT:

This class calls methods in the background, using threading. There are certain rules for 
threaded programs that if broken will cause your program to crash, variables to lose data, 
and otherwise give you massive headaches and spontaneous errors.

Rule 1:

Always know what thread of execution a method is running in. A method runs in the thread
it was called in. If a method is run in a background, you must make be sure it doesn't use
another threads variables unless they follow rules 2 and 3.


Rule 2:

Don't modify a primitive (int, boolean, double, etc.) or pointer in more than one thread unless it
is marked volatile.

Example:

private double i;			// This is not thread-safe. Only use with a single thread.

public volatile int j; 		// This can be used with multiple threads but isn't as fast

public volatile Object o; 	// You can modify this pointer to point to a new object, but don't
 							// modify the object it points to (see rule 3)

Rule 3 (Simpler version):

Don't modify an object in more than one thread unless it is in synchronized code. You can synchronize
code easily by marking a method as synchronized. If you only directly modify an object from
synchronized methods all in the same class, then you can use those methods in any thread.

Example:
	public synchronized void set(int num, int[] array, int index) {

		array[index] = num;
	}

	public synchronized void get(int[] array, index) {
	
		return array[index];
	}
	
	
	
Rule 3 (Faster, more powerful version)

See bottom
*/

import java.lang.reflect.Method; 

public class Timer implements Runnable {

	public Timer(double frequency, double duration, Object target, String selector) {
		this.frequency = frequency;
		this.duration = duration;
		this.target = target;
		try {
			method = target.getClass().getMethod(selector);
		}
		catch (Exception e) {
			try {
				method = target.getClass().getMethod(selector, this.getClass());
			}
			catch (Exception e2) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
	}
	
	private final double frequency;
	private final double duration;
	private Method method;
	private Object target;
	private Thread thread = new Thread(this);
	private volatile double startTime;
	private volatile double endTime;
	private volatile boolean running;
	
	public void stop() {
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public double frequency() {
		return frequency;
	}
	
	public double duration() {
		return duration;
	}
	
	private void call() {
		if (method.getParameterTypes().length > 0) {
			try {
				method.invoke(target, this);
			}
			catch (Exception e) {
	
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}
		else {
			try {
				method.invoke(target);
				return;
			}
			catch (Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}
	}
	
	public void start() {
		if (!thread.isAlive()) {
			running = true;
			thread.start();
		}
	}
	
	
	// This is the start of our new thread. When this method ends the thread dies
	
	public void run() {
		startTime = getTime();
		endTime = startTime + duration;
		double timePerLoop = 1 / frequency;
		double sleepTime;
		double lastTime = startTime;
		double currentTime;
		boolean stillWaiting;
		while ((duration < 0 || percentComplete() < 100) && running) {
			do {
				currentTime = getTime();
				try {
					sleepTime = timePerLoop - (currentTime - lastTime);
					if (sleepTime > 0) {
						Thread.sleep((long)(sleepTime * 1000));
					}
					stillWaiting = false;
					lastTime = getTime();
				} catch (InterruptedException e) {
					stillWaiting = true;
				}
			} while (stillWaiting);
			
			if (percentComplete() < 100 && running)
				call();
		}
		if (running) {
			call();
		}
		running = false;
		thread = new Thread(this);
	}
	
	public double percentComplete() {
		
		if (thread.isAlive()) {
			
			if (duration < 0) {
				return -1;
			}
			else {
				double percent = (getTime() - startTime) / (endTime - startTime) * 100;
				if (percent > 100) {
					return 100;
				}
				else {
					return percent;
				}
			}
		}
		else {
			return 100;
		}
	}
	
	public static double getTime() {
		return System.nanoTime() / 1000000000.0;
	}
}

/* 

Rule 3 (Faster, more powerful version)


You can also synchronize code by making a synchronized block. This requires that you specify an object
to hold the synchronization lock. In the example below, this allows multiple different arrays to be
acted on simultaneously, where in the previous example only one could be modified at a time. Also, you
can make the program faster by synchronizing only the code that needs to be synchronized.
 

	public void set(int num, int[] array, int index) {
	
		synchronized (array) {
		
			array[index] = num;
		}
	}
	
	public int get(int[] array, int index) {
	
		synchronized (array) {
			
			return array[index];
		}
	}
	
	
Note that certain parts of this tutorial have been oversimplified. 

For more info see http://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html




	
	
*/
