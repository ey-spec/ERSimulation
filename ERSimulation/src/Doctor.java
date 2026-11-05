import java.util.Random;

public class Doctor extends Thread {
    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRooms treatmentRooms;
    private final long timeoutMs;
    private final Random random = new Random();

    public Doctor(int id, WaitingRoom waitingRoom, TreatmentRooms treatmentRooms, long timeoutMs) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public String toString() {
        return "Doctor " + id;
    }

    @Override
    public void run() {
        while (true) {
            Patient p = waitingRoom.take(); // returns the patient if there exists.. if elements = 0 -> so the doctor sleeps in the elements waiting list till element.V() is called which means there is a patient in waiting room.
            if (p == null) break; // hospital closed

            Logger.log(this + " is assigned to " + p + ".");
            Logger.log(this + " is trying to acquire a treatment room.");

            if (treatmentRooms.tryAcquire(timeoutMs)) { // return boolean: True -> if there is a treatmentRooms.value > 0 //// False -> if remaining time is done and no treatmentRooms are available ////// in the remaining time the thread is waiting for a release that will make value > 0
                Logger.log(this + " acquired a treatment room.");
                Logger.log(this + " begins treating " + p + ".");

                try {
                    Thread.sleep(2000 + random.nextInt(3000));   // 2 to 5 seconds
                } catch (InterruptedException e) {
                }

                Logger.log(this + " finished treating " + p + ".");
                treatmentRooms.release();
                Logger.log(this + " released the treatment room.");
                Logger.log(this + " is now available.");
                p.leave();
            } else {
                Logger.log(this + " could not acquire a treatment room.");
                Logger.log(this + " is sending " + p + " back to the waiting room.");
                p.returnToWaitingRoom();             // does not block the doctor
                Logger.log(this + " is now available.");
            }
        }
    }
}