public class WaitingRoom {
    private final Patient[] buffer;           // the seats (circular array)
    private final int capacity;
    private int inptr = 0;                    // next free slot to write
    private int outptr = 0;                   // next slot to read

    private final Semaphore spaces;           // free seats
    private final Semaphore elements = new Semaphore(0);  // patients inside
    private final Semaphore mutex = new Semaphore(1);     // protects inptr/outptr

    private volatile boolean closed = false;  // set at closing

    public WaitingRoom(int capacity) {
        this.capacity = capacity;
        this.buffer = new Patient[capacity];
        this.spaces = new Semaphore(capacity);
    }

    // Producer side: called by a patient
    public void put(Patient p) {
        if (!spaces.tryP(0)) {                // is there a seat?
            Logger.log("The waiting room is full.");
            Logger.log(p + " is waiting outside for an available seat.");
            spaces.P();                       // sleep here until a seat frees
        }
        mutex.P();
        buffer[inptr] = p;
        inptr = (inptr + 1) % capacity;
        mutex.V();
        Logger.log(p + " entered the waiting room.");
        elements.V();                         // announce: one more patient
    }

    // Consumer side: called by a doctor. Returns null at closing.
    public Patient take() {
        elements.P();                         // idle here until a patient exists
        if (closed) return null;
        mutex.P();
        Patient p = buffer[outptr];
        buffer[outptr] = null;
        outptr = (outptr + 1) % capacity;
        mutex.V();
        spaces.V();                           // a seat is free now
        return p;
    }

    // Called once by Hospital when all patients are done
    public void close(int numDoctors) {
        closed = true;
        for (int i = 0; i < numDoctors; i++) {
            elements.V();                     // wake each idle doctor
        }
    }
}