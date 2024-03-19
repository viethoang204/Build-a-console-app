package View;

import Controller.ClaimManagerController;
import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private ClaimManagerController claimManagerController = ClaimManagerController.getInstance();

    public Menu(){
        claimManagerController.loadUsersFromFile();
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
                    try{
                        this.displayClaimPreview();
                        System.out.println("=====Create new claim=====");
                        System.out.print("Enter claim date (dd-MM-yyyy): ");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        Date claimdate = formatter.parse(scanner.nextLine());
                        System.out.print("Enter insured person: ");
                        Customer insuredperson = new Customer(scanner.nextLine());
                        System.out.print("Enter card number: ");
                        InsuranceCard cardnumber = new InsuranceCard(scanner.nextLine());
                        System.out.print("Enter exam date (dd-MM-yyyy): ");
                        Date examdate = formatter.parse(scanner.nextLine());
                        System.out.print("Enter list of documents (comma separated): ");
                        List<String> listofdocuments = Arrays.asList(scanner.nextLine().split(","));
                        System.out.print("Enter claim amount: ");
                        double amount = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter status: ");
                        String status = scanner.nextLine();
                        System.out.print("Enter receiver banking info: ");
                        String receiverbankinginfor = scanner.nextLine();
                        claimManagerController.add(claimdate, insuredperson, cardnumber, examdate, listofdocuments, amount, status, receiverbankinginfor);
                    } catch (Exception e){
                        System.out.println("An error occurred. Please try again");
                    }
                    break;
                case 3:
                    this.printClaimTable(claimManagerController.getAll());
                    break;
                case 4:
                    System.out.println("Returning...");
            }
        } while (choice!=4);
    }

    public void printClaimTable(List<Claim> claims) {
    // Define the table header
    System.out.printf("%-10s %-20s %-30s %-30s %-15s %-15s %-15s %-15s %-15s%n",
            "ID", "Claim Date", "Insured Person", "Card Number", "Exam Date", "List of Documents", "Claim Amount", "Status", "Receiver Banking Info");

    // Loop through the list of users and print each user as a row in the table
    for (Claim user : claims) {
        System.out.printf("%-10s %-20s %-30s %-30s %-15s %-15s %-15s %-15s %-15s%n",
                user.getId(),
                user.getClaimDate(),
                user.getInsuredPerson(),
                user.getCardNumber(),
                user.getExamDate(),
                user.getDocuments(),
                user.getClaimAmount(),
                user.getStatus(),
                user.getReceiverBankingInfo());
    }
}

    private void displayClaimPreview() {
        List<Claim> claims = claimManagerController.getAll();

        if (claims.isEmpty()) {
            System.out.println("No claims available.");
        } else {
            System.out.println("===== Claim Preview =====");
            System.out.printf("%-5s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%n", "ID", "Claim Date", "Insured Person", "Card Number", "Exam Date", "List of Documents", "Claim Amount", "Status", "Receiver Banking Info");

            for (Claim claim : claims) {
                System.out.printf("%-5s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%-15s%n", claim.getId(), claim.getClaimDate(), claim.getInsuredPerson(), claim.getCardNumber(), claim.getExamDate(), claim.getDocuments(), claim.getClaimAmount(), claim.getStatus(), claim.getReceiverBankingInfo());
            }
        }
    }
}
