package View;

import Controller.ClaimManager;
import Model.Claim;
import java.util.Scanner;

public class Menu {
    private ClaimManager claimManager;

    public Menu() {
        this.claimManager = new ClaimManager();
    }

    public void runMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Add claim");
            System.out.println("2. Update claim");
            System.out.println("3. Delete claim");
            System.out.println("4. Get one claim");
            System.out.println("5. Get all claims");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    // Call method to add claim
                    Claim claimToAdd = new Claim(); // You need to create a new Claim object here
                    claimManager.add(claimToAdd);
                    break;
                case 2:
                    // Call method to update claim
                    Claim claimToUpdate = new Claim(); // You need to create a new Claim object here
                    claimManager.update(claimToUpdate);
                    break;
                case 3:
                    // Call method to delete claim
                    String claimIdToDelete = ""; // You need to get the claim ID here
                    claimManager.delete(claimIdToDelete);
                    break;
                case 4:
                    // Call method to get one claim
                    String claimIdToGet = ""; // You need to get the claim ID here
                    Claim claim = claimManager.getOne(claimIdToGet);
                    break;
                case 5:
                    // Call method to get all claims
                    claimManager.getAll();
                    break;
                case 6:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please enter a number between 1 and 6.");
            }
        }
    }
}