public class TreatmentRooms {
    private static final int num_rooms = 2;
    private final Semaphore rooms = new Semaphore(num_rooms);

    // Doctor calls this: true if a room was acquired within ms milliseconds
    public boolean tryAcquire(long ms) {
        return rooms.tryP(ms);
    }

    // Doctor calls this after finishing treatment
    public void release() {
        rooms.V();
    }
}
