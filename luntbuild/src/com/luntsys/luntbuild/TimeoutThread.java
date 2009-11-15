package com.luntsys.luntbuild;

import java.util.Date;

/** Stop a thread after a given timeout has elapsed
 * <P>
 * A simple timeout class.  You give it a thread to watch and a timeout
 * in milliseconds.  After the timeout has elapsed, the thread is killed
 * with a Thread.stop().  If the thread finishes successfully before then,
 * you can cancel the timeout with a done() call; you can also re-use the
 * timeout on the same thread with the reset() call.
 * <P>
 * <A HREF="/resources/classes/Acme/TimeKiller.java">Fetch the software.</A><BR>
 * <A HREF="/resources/classes/Acme.tar.gz">Fetch the entire Acme package.</A>
 * 
 * @author lubosp
 */

public class TimeoutThread implements Runnable {

	private final Thread targetThread;
	private long millis;
	private final Thread watcherThread;
	private boolean loop;
	private boolean enabled;

	/**
	 * Constructor. Give it a thread to watch, and a timeout in milliseconds.
	 * After the timeout has elapsed, the thread gets killed. If you want
	 * to cancel the kill, just call done().
	 * 
	 * @param targetThread
	 * @param millis
	 */
	public TimeoutThread(Thread targetThread, long millis) {
		this.targetThread = targetThread;
		this.millis = millis;
		watcherThread = new Thread(this);
		enabled = true;
		watcherThread.start();
		// Hack - pause a bit to let the watcher thread get started.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Constructor, current thread.
	 * 
	 * @param millis
	 */
	public TimeoutThread(long millis) {
		this(Thread.currentThread(), millis);
	}

	/**
	 * Call this when the target thread has finished.
	 */
	public synchronized void done() {
		loop = false;
		enabled = false;
		notify();
	}

	/**
	 * Call this to restart the wait from zero.
	 */
	public synchronized void reset() {
		loop = true;
		notify();
	}

	/**
	 * Call this to restart the wait from zero with a different timeout value.
	 * 
	 * @param millis
	 */
	public synchronized void reset(long millis) {
		this.millis = millis;
		reset();
	}

	/*
	 * The watcher thread - from the Runnable interface.
	 * This has to be pretty anal to avoid monitor lockup, lost threads, etc.
	 * 
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run() {
		Thread me = Thread.currentThread();
		me.setPriority(Thread.MAX_PRIORITY);
		if (enabled) {
			do {
				loop = false;
				try {
					wait(millis);
				} catch (InterruptedException e) {
				}
			} while (enabled && loop);
		}
		// The call stop() is depreciated, but Java =< 1.4 doesn't offer anything better
		if (enabled && targetThread.isAlive())
			targetThread.stop();
	}

	// Test main
	public static void main(String[] args) {
		System.out.println((new Date()) + "  Setting ten-second timeout...");
		TimeoutThread tk = new TimeoutThread(10000);
		try {
			double f = 1.;
			System.out.println((new Date())
					+ "  Starting twenty-second pause...");
			for(double i = 0; i < 1.0E99; i++) f = f * i;
			//Thread.sleep(20000);
			System.out.println((new Date())
					+ "  Another twenty-second pause...");
			for(double i = 0; i < 1.0E99; i++) f = f * i;
			//Thread.sleep(20000);
			tk.done();
		} catch (Exception e) {
			System.out.println((new Date()) + "  Caught Exception");
		} catch (ThreadDeath td) {
			System.out.println((new Date()) + "  Caught ThreadDeath");
		}
		System.out.println((new Date()) + "  Oops - pauses finished!");
	}


}
