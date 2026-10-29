public class Semaphore {
    private int value;                       // number of free places

    public Semaphore(int initial) {
        this.value = initial;
    }

    // take a place, wait if none is free
    public synchronized void P() {
        boolean interrupted = false;
        while (value <= 0) {                 // no places
            try {
                wait();                      // releases the lock while waiting
            } catch (InterruptedException e) {
            }
        }
        value--;
    }

    // give a place back, wake one waiter
    public synchronized void V() {
        value++;
        notify();
    }

    // like P(), but gives up after milliseconds
    public synchronized boolean tryP(long ms) {
        long deadline = System.currentTimeMillis() + ms;
        while (value <= 0) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) return false;    // timed out, nothing was taken
            try {
                wait(remaining);
            } catch (InterruptedException e) {
            }
        }
        value--;
        return true;
    }
}
