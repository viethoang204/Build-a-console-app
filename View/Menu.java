package View;

import Controller.ClaimController;
import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private ClaimController claimController = ClaimController.getInstance();

    public Menu(){
        claimController.loadClaimsFromFile();
    }

    public void view(){
        int choice;
        do {
            System.out.println("\n================================================= HOMEPAGE =================================================");
            System.out.println("1. Claim Manager");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());
            switch (choice){
                case 1: claimMenu(); break;
            }
        } while (choice != 2);
    }

    private void claimMenu(){
        int choice;
        do {
            System.out.println("===== Admin Menu =====");
            System.out.println("1. Add Claim");
            System.out.println("2. Remove Claim");
            System.out.println("3. View All Claim");
            System.out.println("4. Return");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    try {
    this.printClaimInfo(claimController.getAll(), true);
    System.out.println("=====Create new claim=====");
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    Date claimdate = null;
    while (claimdate == null) {
        try {
            System.out.print("Enter claim date (dd-MM-yyyy): ");
            claimdate = formatter.parse(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    System.out.print("Enter insured person: ");
    Customer insuredperson = new Customer(scanner.nextLine());
    insuredperson.setFullName(scanner.nextLine());

    System.out.print("Enter card number: ");
    InsuranceCard cardnumber = new InsuranceCard(scanner.nextLine());
    cardnumber.setCardNumber(scanner.nextLine());

    Date examdate = null;
    while (examdate == null) {
        try {
            System.out.print("Enter exam date (dd-MM-yyyy): ");
            examdate = formatter.parse(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid date format. Please try again.");
        }
    }

    System.out.print("Enter list of documents (comma separated): ");
    List<String> listofdocuments = Arrays.asList(scanner.nextLine().split(","));

    Double amount = null;
    while (amount == null) {
        try {
            System.out.print("Enter claim amount($): ");
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please try again.");
        }
    }

    System.out.print("Enter status: ");
    String status = scanner.nextLine();

    System.out.print("Enter receiver banking info: ");
    String receiverbankinginfor = scanner.nextLine();

    claimController.add(claimdate, insuredperson, cardnumber, examdate, listofdocuments, amount, status, receiverbankinginfor);
} catch (Exception e) {
    System.out.println("An error occurred. Please try again");
}
                case 2:
                    try {
                        this.printClaimInfo(claimController.getAll(), true);
                        System.out.print("Enter claim ID to remove (c-<number>): ");
                        String id = scanner.nextLine();
                        if (claimController.delete(id)) {
                            System.out.println("Removed claim " + id + " from the system");
                        } else {
                            System.out.println("Invalid ID, please try again");
                        }
                    }catch (Exception e){
                        System.out.println("An error occurred, please try again.");
                    }
                    break;
                case 3:
                    this.printClaimInfo(claimController.getAll(), false);
                    break;
                case 4:
                    System.out.println("Returning...");
            }
        } while (choice!=4);
    }

//    public void printClaimTable(List<Claim> claims) {
//        // Define the table header
//        System.out.printf("%-10s %-20s %-30s %-30s %-15s %-15s %-15s %-15s %-15s%n",
//                "ID", "Claim Date", "Insured Person", "Card Number", "Exam Date", "List of Documents", "Claim Amount", "Status", "Receiver Banking Info");
//
//        // Loop through the list of users and print each user as a row in the table
//        for (Claim user : claims) {
//            System.out.printf("%-10s %-20s %-30s %-30s %-15s %-15s %-15s %-15s %-15s%n",
//                    user.getId(),
//                    formatDate(user.getClaimDate()),
//                    user.getInsuredPerson(),
//                    user.getCardNumber(),
//                    formatDate(user.getExamDate()),
//                    user.getDocuments(),
//                    user.getClaimAmount(),
//                    user.getStatus(),
//                    user.getReceiverBankingInfo());
//        }
//    }
//
//    private void displayClaimPreview() {
//        List<Claim> claims = claimController.getAll();
//
//        if (claims.isEmpty()) {
//            System.out.println("No claims available.");
//        } else {
//            System.out.println("===== Claim Preview =====");
//            System.out.printf("%-5s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%n",
//                    "ID",
//                    "Claim Date",
//                    "Insured Person",
//                    "Card Number",
//                    "Exam Date",
//                    "List of Documents",
//                    "Claim Amount",
//                    "Status",
//                    "Receiver Banking Info");
//
//            for (Claim claim : claims) {
//                String formattedClaimDate = formatDate(claim.getClaimDate());
//                String formattedExamDate = formatDate(claim.getExamDate());
//                System.out.printf("%-5s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%n",
//                        claim.getId(),
//                        formattedClaimDate,
//                        claim.getInsuredPerson(),
//                        claim.getCardNumber(),
//                        formattedExamDate,
//                        claim.getDocuments(),
//                        claim.getClaimAmount(),
//                        claim.getStatus(),
//                        claim.getReceiverBankingInfo());
//            }
//        }
//    }
    // Method to format Date to String

    public void printClaimInfo(List<Claim> claims, boolean isPreview) {
    String headerFormat = "%-10s%-20s%-30s%-30s%-20s%-15s%-15s%-15s%-15s%n";

    if (isPreview) {
        System.out.println("================================================= Preview the claim list =================================================");
    }

    System.out.printf(headerFormat, "ID", "Claim Date", "Insured Person", "Card Number", "Exam Date", "List of Documents", "Claim Amount", "Status", "Receiver Banking Info");

    for (Claim claim : claims) {
        System.out.printf(headerFormat,
                claim.getId(),
                formatDate(claim.getClaimDate()),
                claim.getInsuredPerson(),
                claim.getCardNumber(),
                formatDate(claim.getExamDate()),
                String.join(", ", claim.getDocuments()),
                claim.getClaimAmount(),
                claim.getStatus(),
                claim.getReceiverBankingInfo());
    }
}

    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }
}
