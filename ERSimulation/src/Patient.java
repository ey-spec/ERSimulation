public class Patient extends Thread {
    private final int id;
    private final WaitingRoom waitingRoom;
    private final Semaphore done;

    public Patient(int id, WaitingRoom waitingRoom, Semaphore done) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.done = done;
    }

    @Override
    public void run() {
        Logger.log(this + " arrived.");
        waitingRoom.put(this);                       
    }

    // called by a doctor after a patient is sent back
    public void returnToWaitingRoom() {
        new Thread(() -> waitingRoom.put(this)).start();
    }

    // called by a doctor after treatment
    public void leave() {
        Logger.log(this + " left.");
        done.V();                                    
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}