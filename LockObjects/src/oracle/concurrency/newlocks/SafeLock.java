package oracle.concurrency.newlocks;

import static java.lang.System.out;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

/**
 * <p>
 *  <a href="http://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html">SafeLock</a>
 * </p>
 * @author Chris Wong
 *
 */
public class SafeLock {
	
	/**
	 * <p>
	 *  Friend inner class
	 * </p>
	 * @author Chris Wong
	 *
	 */
	static class Friend {
		private final String name;
		private final Lock lock = new ReentrantLock();
		
		/**
		 * Constructor with input name
		 * @param name friend name
		 */
		public Friend(String name){
			this.name = name;
		}
		
		/**
		 * Get friend name
		 * @return name friend name
		 */
		public String getName(){
			return this.name;
		}
		
		/**
		 * Impend bow
		 * @param bower the bower
		 * @return true if myLock and yourLock are both true
		 */
		public boolean impendingBow(Friend bower){
			Boolean myLock = false;
			Boolean yourLock = false;
			
			try {
				myLock = lock.tryLock();
				yourLock = bower.lock.tryLock();
			} finally {
				if(! (myLock && yourLock)){
					if(myLock){
						lock.unlock();
					}
					if(yourLock){
						bower.lock.unlock();
					}
				}
			}
			
			return myLock && yourLock;
		}
		
		/**
		 * Bow
		 * @param bower the bower 
		 */
		public void bow(Friend bower){
			if(impendingBow(bower)){
				try{
					out.format("%s: %s has bowed to me!%n", this.name, bower.getName());
				} finally{
					lock.unlock();
					bower.lock.unlock();
				}
			} else {
				out.format("%s: %s started to bow to me, but saw that I was already bowing to him. %n", this.name, bower.getName());
			}
		}
		
		/**
		 * Bow back
		 * @param bower the bower 
		 */
		public void bowBack(Friend bower){
			out.format("%s: %s has bowed back to me!%n", this.name, bower.getName());
		}
	}
	
	/**
	 * Bow loop infinitely
	 * @author Chris Wong
	 *
	 */
	static class BowLoop implements Runnable{
		private Friend bower;
		private Friend bowee;
		
		public BowLoop(Friend bower, Friend bowee){
			this.bower = bower;
			this.bowee = bowee;
		}
		
		@Override
		public void run() {
			Random random = new Random();
			
			for(;;){
				try {
					Thread.sleep(random.nextInt(10));
				} catch (InterruptedException exception){}
				bowee.bow(bower);
			}
			
		}
		
	}
	

	/**
	 * Main program
	 * @param args
	 */
	public static void main(String[] args) {
		final Friend alphonse = new Friend("Alphonse");
		final Friend gaston = new Friend("Gaston");
		
		new Thread(new BowLoop(alphonse, gaston)).start();
		new Thread(new BowLoop(gaston, alphonse)).start();
	}

}
