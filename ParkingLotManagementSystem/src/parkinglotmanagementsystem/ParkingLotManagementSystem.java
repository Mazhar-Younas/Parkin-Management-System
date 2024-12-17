/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package parkinglotmanagementsystem;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalTime;

class ParkingSlot {
    int slotNumber;
    public String vehicleNumber;
     String entryTime;
    boolean isOccupied;
    ParkingSlot next;

    public ParkingSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.vehicleNumber = null;
        this.isOccupied = false;
        this.next = null;
    }
}

class ParkingLot {
    private static ParkingLot instance;
    ParkingSlot head;
    int totalSlots;
    Queue<String> waitingQueue;

    public ParkingLot(int totalSlots) {
        this.totalSlots = totalSlots;
        this.waitingQueue = new LinkedList<>();
        initializeSlots();
    }
   // Method to get the singleton instance
    public static ParkingLot getInstance(int totalSlots) {
        if (instance == null) {
            instance = new ParkingLot(totalSlots);
        }
        return instance;
    }
    // Initialize parking slots
    public void initializeSlots() {
        head = new ParkingSlot(1);
        ParkingSlot current = head;
        for (int i = 2; i <= totalSlots; i++) {
            current.next = new ParkingSlot(i);
            current = current.next;
        }
    }

    // Park a vehicle
public void parkVehicle(String vehicleNumber, String entryTimeString) {
    ParkingSlot freeSlot = findFreeSlot();
    if (freeSlot != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime entryTime = LocalTime.parse(entryTimeString, formatter);

        freeSlot.vehicleNumber = vehicleNumber;
        freeSlot.entryTime = entryTimeString; // Store as string for easier use later
        freeSlot.isOccupied = true;

        System.out.println("Vehicle " + vehicleNumber + " parked at slot " + freeSlot.slotNumber + " at " + entryTime);
    } else {
        System.out.println("Parking full! Adding vehicle " + vehicleNumber + " to waiting queue.");
        waitingQueue.add(vehicleNumber);
    }
}



    // Exit a vehicle
public void exitVehicle(String vehicleNumber, String exitTimeString, int ratePerHour) {
    ParkingSlot slot = findVehicle(vehicleNumber);
    if (slot != null) {
        long fee = calculateFee(slot.entryTime, exitTimeString, ratePerHour);

        System.out.println("Vehicle " + vehicleNumber + " exited from slot " + slot.slotNumber + " at " + exitTimeString);
        System.out.println("Total fee: " + fee + " rupees");

        slot.vehicleNumber = null;
        slot.entryTime = null;
        slot.isOccupied = false;

        // Assign next vehicle from waiting queue if available
        if (!waitingQueue.isEmpty()) {
            String nextVehicle = waitingQueue.poll();
            parkVehicle(nextVehicle, exitTimeString); // Use the same time as new entry time
        }
    } else {
        System.out.println("Vehicle " + vehicleNumber + " not found!");
    }
}



    // Display parking lot status
    public void displayStatus() {
        System.out.println("\nParking Lot Status:");
        ParkingSlot current = head;
        while (current != null) {
            String status = current.isOccupied ? "Occupied by " + current.vehicleNumber : "Available";
            System.out.println("Slot " + current.slotNumber + ": " + status);
            current = current.next;
        }
        System.out.println("Vehicles in waiting queue: " + waitingQueue);
    }

    // Find the first free slot
    public ParkingSlot findFreeSlot() {
        ParkingSlot current = head;
        while (current != null) {
            if (!current.isOccupied) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // Find a slot by vehicle number
    public ParkingSlot findVehicle(String vehicleNumber) {
        ParkingSlot current = head;
        while (current != null) {
            if (current.isOccupied && current.vehicleNumber.equals(vehicleNumber)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // Calculate parking fee
//private long calculateFee(String entryTimeString, String exitTimeString, int ratePerHour) {
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//    LocalTime entryTime = LocalTime.parse(entryTimeString, formatter);
//    LocalTime exitTime = LocalTime.parse(exitTimeString, formatter);
//
//        if (exitTime.isBefore(entryTime)) {
//        exitTime = exitTime.plusHours(24);
//    }
//
//    // Calculate the duration in minutes
//    long durationInMinutes = Duration.between(entryTime, exitTime).toMinutes();
//
//    // Calculate the fee proportionally
//    double durationInHours =durationInMinutes / 60.0; // Convert minutes to hours
//    double fee = durationInHours * ratePerHour;
//
//    // Round to the nearest whole number (for simplicity in currency)
//    return Math.max(0, Math.round(fee));
//}
public long calculateFee(String entryTimeString, String exitTimeString, int ratePerHour) {
    try {
        if (ratePerHour <= 0) {
            System.out.println("Invalid rate per hour: " + ratePerHour);
            return 0;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime entryTime = LocalTime.parse(entryTimeString, formatter);
        LocalTime exitTime = LocalTime.parse(exitTimeString, formatter);

        // Create LocalDateTime objects with a placeholder date (e.g., 2023-11-25)
        LocalDateTime entryDateTime = LocalDateTime.of(2023, 11, 25, entryTime.getHour(), entryTime.getMinute());
        LocalDateTime exitDateTime = LocalDateTime.of(2023, 11, 25, exitTime.getHour(), exitTime.getMinute());
        // Debug: Print parsed times
        System.out.println("Entry Time: " + entryDateTime);
        System.out.println("Exit Time: " + exitDateTime);

        // Adjust for midnight crossover
        if (exitDateTime.isBefore(entryDateTime)) {
             exitDateTime = exitDateTime.plusDays(1);
            System.out.println("Adjusted Exit Time: " + exitTime);
        }

        // Calculate duration
        long durationInMinutes = Duration.between(entryDateTime, exitDateTime).toMinutes();
        double durationInHours = durationInMinutes / 60.0;

        // Debug: Print duration
        System.out.println("Duration in minutes: " + durationInMinutes);
        System.out.println("Duration in hours: " + durationInHours);

        // Calculate fee
        double fee = durationInHours * ratePerHour;

        // Debug: Print fee before rounding
        System.out.println("Calculated Fee (before rounding): " + fee);

        return Math.max(0, Math.round(fee));

    } catch (Exception e) {
        System.out.println("Error calculating fee: " + e.getMessage());
        return 0;
    }
}


}


public class ParkingLotManagementSystem {
    public static void main(String[] args) {
       // Scanner scanner = new Scanner(System.in);

       // System.out.print("Enter total number of parking slots: ");
        //int totalSlots = scanner.nextInt();
        ParkingLot parkingLot = new ParkingLot(20);

//        int choice;
//        do {
//            System.out.println("\nMenu:");
//            System.out.println("1. Park a Vehicle");
//            System.out.println("2. Exit a Vehicle");
//            System.out.println("3. Display Parking Lot Status");
//            System.out.println("4. Exit System");
//            System.out.print("Enter your choice: ");
//            choice = scanner.nextInt();
//            scanner.nextLine(); // Consume newline
//
//            switch (choice) {
//               case 1:
//                 System.out.print("Enter vehicle number: ");
//                 String vehicleNumber = scanner.nextLine();
//                 System.out.print("Enter entry time (HH:mm): ");
//                 String entryTimeString = scanner.nextLine();
//                 parkingLot.parkVehicle(vehicleNumber, entryTimeString);
//                 break;
//
//                
//
//                case 2:
//                 System.out.print("Enter vehicle number: ");
//                 vehicleNumber = scanner.nextLine();
//                 System.out.print("Enter exit time (HH:mm): ");
//                 String exitTimeString = scanner.nextLine();
//                 System.out.print("Enter rate per hour: ");
//                int ratePerHour = scanner.nextInt();
//                parkingLot.exitVehicle(vehicleNumber, exitTimeString, ratePerHour);
//                break;
//                 
//
//                case 3:
//                    parkingLot.displayStatus();
//                    break;
//
//                case 4:
//                    System.out.println("Exiting system. Goodbye!");
//                    break;
//
//                default:
//                    System.out.println("Invalid choice! Please try again.");
//            }
//        } while (choice != 4);
//
//        scanner.close();
    }
}
