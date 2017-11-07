import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Idea obtained from http://stackoverflow.com/questions/702415/how-to-know-if-other-threads-have-finished
 * @author Eddie
 */
public class NotifyingThread extends Thread {
  private final Set<ThreadCompleteListener> listeners
                   = new CopyOnWriteArraySet<ThreadCompleteListener>();
  public final void addListener(final ThreadCompleteListener listener) {
    listeners.add(listener);
  }
  public final void removeListener(final ThreadCompleteListener listener) {
    listeners.remove(listener);
  }
  private final void notifyListeners() {
    for (ThreadCompleteListener listener : listeners) {
      listener.threadCompleted(this);
    }
  }
  @Override
  public final void run() {
    try {
    	doRun();
    } finally {
    	Main.setInfoText("Done");
		Main.display.repaint();
    	notifyListeners();
    }
  }
  public final void step(){
	  if(Main.steptime != 0) Main.display.repaint();
	  try {
			Thread.sleep(Main.steptime);
		} catch (InterruptedException e) {
			
		}
  }
  public void doRun() {
	  
  }
}