import java.util.Scanner;

public class Hospital {
    private static final long ROOM_TIMOUT_MS = 3000;

    private static int readPositiveInt(Scanner sc, String prompt) {
        System.out.print(prompt);
        while (true) {
            if (!sc.hasNextInt()) {
                sc.next();
                System.out.print("Invalid.. Please enter valid number (Positive Number) : ");
                continue;
            }
            int input = sc.nextInt();
            if (input > 0) {
                return input;
            }
            System.out.print("Invalid.. Please enter valid number (Positive Number) : ");
        }

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int waitingRoomCapacity = readPositiveInt(sc, "Enter Waiting Room Capacity: ");
        int numberOfDoctors = readPositiveInt(sc, "Enter Number Of Doctors: ");
        int numberOfPatients = readPositiveInt(sc, "Enter Number Of Patients: ");

        WaitingRoom waitingRoom = new WaitingRoom(waitingRoomCapacity);
        TreatmentRooms treatmentRooms = new TreatmentRooms();
        Semaphore done = new Semaphore(0);

        Doctor[] doctors = new Doctor[numberOfDoctors];
        for (int i = 0; i < numberOfDoctors; i++) {
            doctors[i] = new Doctor(i, waitingRoom, treatmentRooms, ROOM_TIMOUT_MS);
        }

        Patient[] patients = new Patient[numberOfPatients];
        for (int i = 0; i < numberOfPatients; i++) {
            patients[i] = new Patient(i, waitingRoom, done);
        }

        StringBuilder list = new StringBuilder();
        for (Patient p : patients) {
            list.append(p).append(" ");
        }

        Logger.log("Patients arriving:");
        Logger.log(list.toString().trim());

        for (Doctor d : doctors) {
            d.start();
        }
        for (Patient p : patients) {
            p.start();
            try {

                Thread.sleep(300); // small gap so arrivals look spread out
            } catch (InterruptedException e) {
            }
        }

        for (int i = 0; i < numberOfPatients; i++) {
            done.P();
        }

        waitingRoom.close(numberOfDoctors);
        for (Doctor d : doctors) {
            try {

                d.join();
            } catch (InterruptedException e) {

            }
        }

        Logger.log("All patients have been treated.");
        Logger.log("Simulation completed.");
    }
}
