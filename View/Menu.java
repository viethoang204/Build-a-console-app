package View;

import Controller.ClaimController;
import Controller.CustomerController;
import Model.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private ClaimController claimController = ClaimController.getInstance();
    private CustomerController customercontroller = CustomerController.getInstance();

    public Menu(){
    }

    public void view(){
        int choice;
        do {
            System.out.println("\033[1m===== HOME PAGE =====\033[0m");
            System.out.println("1. Claim Manager");
            System.out.println("2. Customer and Insurance Card Manager");
            System.out.println("3. Exit");
            System.out.println("3. Return");
            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());
            switch (choice){
                case 1: claimMenu(); break;
                case 2: customerAndCardMenu(); break;
            }
        } while (choice != 3);
    }

    private void customerAndCardMenu(){
        int choice;
        do {
            System.out.println("\033[1m===== Customer and Insurance Card Manager =====\033[0m");
            System.out.println("1. View All Customer");
            System.out.println("2. View All Customer's Insurance Card");
            System.out.println("3. Add Customer And His/Her Insurance Card");
            System.out.println("4. Delete Customer And His/Her Insurance Card");
            System.out.println("5. Return");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    this.printCustomerInfo(customercontroller.getListOfCustomers(), false);
                    break;
                case 2:
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

                        Customer insuredperson = null;
                        String customerId = ""; // Initialize customerId outside the loop
                        while (insuredperson == null) {
                            System.out.print("Enter insured person ID (c-xxxxxxx): ");
                            customerId = scanner.nextLine().trim();
                            insuredperson = claimController.getCustomerById(customerId);
                            if (insuredperson == null) {
                                System.out.println("Customer not found with the given ID. Please try again.");
                            }
                        }


                        InsuranceCard cardnumber = insuredperson.getInsuranceCard();
                        if (cardnumber == null) {
                            System.out.println("Insurance card not found with the given number.");
                            // Handle the case where the insurance card is not found as needed
                            return; // Or continue, based on your requirement
                        }
                        System.out.println("\033[1mInsurance card number of " + insuredperson.getFullName() + " is " + cardnumber.getCardNumber() + "\033[0m");

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
                        List<String> rawListOfDocuments = Arrays.asList(scanner.nextLine().split(","));
                        Set<String> documentSet = new LinkedHashSet<>(rawListOfDocuments);
                        List<String> listofdocuments = new ArrayList<>(documentSet);

                        Double amount = null;
                        while (amount == null) {
                            try {
                                System.out.print("Enter claim amount($): ");
                                amount = Double.parseDouble(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number format. Please try again.");
                            }
                        }

                        String status = "";
                        boolean isValidStatus = false;
                        while (!isValidStatus) {
                            System.out.print("Enter status (New, Processing, Done): ");
                            status = scanner.nextLine().trim().toLowerCase();
                            if (status.equals("new") || status.equals("processing") || status.equals("done")) {
                                isValidStatus = true; // Valid input; exit the loop.
                            } else {
                                System.out.println("Invalid status. Please enter 'New', 'Processing', or 'Done'.");
                            }
                        }
                        status = status.substring(0, 1).toUpperCase() + status.substring(1);

                        System.out.println("concac");
                        System.out.print("=====BANKING INFORMATION OF " + insuredperson.getFullName().toUpperCase() +"=====" + "\n");
                        System.out.print("Enter the bank name: ");
                        String bank = scanner.nextLine().toUpperCase();
                        String name_bank = insuredperson.getFullName().toUpperCase();
                        System.out.print("Enter the card number: ");
                        String number_bank = scanner.nextLine();
                        BankingInfo receiverbankinginfor = new BankingInfo(bank, name_bank, number_bank);

                        claimController.add(claimdate, insuredperson, cardnumber, examdate, listofdocuments, amount, status, receiverbankinginfor);
                    } catch (Exception e) {
                        System.out.println("An error occurred. Please try again");
                    }
                    break;
                case 3:
                    try {
                        this.printClaimInfo(claimController.getAll(), true);
                        System.out.print("Enter claim ID to remove (f-xxxxxxxxxx): ");
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
                case 4:
                    System.out.println("Returning...");
                case 5:
                    System.out.println("Returning...");
            }
        } while (choice!=4);
    }

    private void claimMenu(){
        int choice;
        do {
            System.out.println("\033[1m===== Claim Manager Menu =====\033[0m");
            System.out.println("1. View All Claim");
            System.out.println("2. Add Claim");
            System.out.println("3. Remove Claim");
            System.out.println("4. Return");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    this.printClaimInfo(claimController.getAll(), false);
                    break;
                case 2:
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

                        Customer insuredperson = null;
                        String customerId = ""; // Initialize customerId outside the loop
                        while (insuredperson == null) {
                            System.out.print("Enter insured person ID (c-xxxxxxx): ");
                            customerId = scanner.nextLine().trim();
                            insuredperson = claimController.getCustomerById(customerId);
                            if (insuredperson == null) {
                                System.out.println("Customer not found with the given ID. Please try again.");
                            }
                        }


                        InsuranceCard cardnumber = insuredperson.getInsuranceCard();
                        if (cardnumber == null) {
                            System.out.println("Insurance card not found with the given number.");
                            // Handle the case where the insurance card is not found as needed
                            return; // Or continue, based on your requirement
                        }
                        System.out.println("\033[1mInsurance card number of " + insuredperson.getFullName() + " is " + cardnumber.getCardNumber() + "\033[0m");

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
                        List<String> rawListOfDocuments = Arrays.asList(scanner.nextLine().split(","));
                        Set<String> documentSet = new LinkedHashSet<>(rawListOfDocuments);
                        List<String> listofdocuments = new ArrayList<>(documentSet);

                        Double amount = null;
                        while (amount == null) {
                            try {
                                System.out.print("Enter claim amount($): ");
                                amount = Double.parseDouble(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number format. Please try again.");
                            }
                        }

                        String status = "";
                        boolean isValidStatus = false;
                        while (!isValidStatus) {
                            System.out.print("Enter status (New, Processing, Done): ");
                            status = scanner.nextLine().trim().toLowerCase();
                            if (status.equals("new") || status.equals("processing") || status.equals("done")) {
                                isValidStatus = true; // Valid input; exit the loop.
                            } else {
                                System.out.println("Invalid status. Please enter 'New', 'Processing', or 'Done'.");
                            }
                        }
                        status = status.substring(0, 1).toUpperCase() + status.substring(1);

                        System.out.print("=====BANKING INFORMATION OF " + insuredperson.getFullName().toUpperCase() +"=====" + "\n");
                        System.out.print("Enter the bank name: ");
                        String bank = scanner.nextLine().toUpperCase();
                        String name_bank = insuredperson.getFullName().toUpperCase();
                        System.out.print("Enter the card number: ");
                        String number_bank = scanner.nextLine();
                        BankingInfo receiverbankinginfor = new BankingInfo(bank, name_bank, number_bank);

                        claimController.add(claimdate, insuredperson, cardnumber, examdate, listofdocuments, amount, status, receiverbankinginfor);
                    } catch (Exception e) {
                        System.out.println("An error occurred. Please try again");
                    }
                    break;
                case 3:
                    try {
                        this.printClaimInfo(claimController.getAll(), true);
                        System.out.print("Enter claim ID to remove (f-xxxx): ");
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
                case 4:
                    System.out.println("Returning...");
            }
        } while (choice!=4);
    }

    public void printClaimInfo(List<Claim> claims, boolean isPreview) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        // Column headers
        String[] headers = {
                "ID", "Claim Date", "Insured Person", "Card Number", "Exam Date",
                "List of Documents", "Claim Amount", "Status", "Receiver Banking Info"
        };

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        // Update column widths based on the data
        for (Claim claim : claims) {
            maxLengths[0] = Math.max(maxLengths[0], claim.getId().length());
            maxLengths[1] = Math.max(maxLengths[1], formatDate(claim.getClaimDate()).length());
            maxLengths[2] = Math.max(maxLengths[2], claim.getInsuredPerson().getFullName().length());
            maxLengths[3] = Math.max(maxLengths[3], claim.getCardNumber().getCardNumber().length());
            maxLengths[4] = Math.max(maxLengths[4], formatDate(claim.getExamDate()).length());
            maxLengths[5] = Math.max(maxLengths[5], String.join(", ", claim.getDocuments()).length());
            maxLengths[6] = Math.max(maxLengths[6], Double.toString(claim.getClaimAmount()).length());
            maxLengths[7] = Math.max(maxLengths[7], claim.getStatus().length());
            maxLengths[8] = Math.max(maxLengths[8], claim.getReceiverBankingInfo().printInfor().length());
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";  // +2 for padding on either side of the data
        }
        headerFormat += "%n";

        // Print the table
        if (isPreview) {
            System.out.println("\033[1m====== Preview the claim list =====\033[0m");
        } else {
            System.out.println("\033[1m====== Claim list =====\033[0m");
        }
        // Print the headers
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (Claim claim : claims) {
            String formattedAmount = decimalFormat.format(claim.getClaimAmount());

            System.out.printf(headerFormat,
                    claim.getId(),
                    formatDate(claim.getClaimDate()),
                    claim.getInsuredPerson().getFullName(),
                    claim.getCardNumber().getCardNumber(),
                    formatDate(claim.getExamDate()),
                    String.join(", ", claim.getDocuments()),
                    formattedAmount, // Use the DecimalFormat to format amount
                    claim.getStatus(),
                    claim.getReceiverBankingInfo().printInfor());
        }
    }


    public void printCustomerInfo(List<Customer> customers, boolean isPreview) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        // Column headers
        String[] headers = {
                "ID", "Full Name", "Insurance Card", "Title", "List Of Claims", "List Of Dependents"
        };

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        // Update column widths based on the data
        for (Customer customer : customers) {
            maxLengths[0] = Math.max(maxLengths[0], customer.getId().length());
            maxLengths[1] = Math.max(maxLengths[1], customer.getFullName().length());
            maxLengths[2] = Math.max(maxLengths[2], customer.getInsuranceCard().getCardNumber().length());
            // Adjust for the "no claim yet" message if no claims are present
            String claimsString = customer.getClaims().isEmpty() ? "no claim yet" : customer.getClaims().stream()
                    .map(Claim::getId)
                    .collect(Collectors.joining(", "));
            maxLengths[4] = Math.max(maxLengths[4], claimsString.length());
            if (customer instanceof PolicyHolder) {
                PolicyHolder policyHolder = (PolicyHolder) customer;
                if (!policyHolder.getDependents().isEmpty()) {
                    maxLengths[5] = Math.max(maxLengths[5], policyHolder.getDependents().stream()
                            .map(Dependent::getId) // Assuming you want to print full names
                            .collect(Collectors.joining(", ")).length());
                } else {
                    // No dependents, leave the length as is (could be the header length if no dependents at all)
                }
            } else {
                // Account for the length of the string "he/she is a dependent"
                maxLengths[4] = Math.max(maxLengths[4], "he/she is a dependent".length());
            }
            String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";
            maxLengths[3] = Math.max(maxLengths[3], title.length());
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the table
        if (isPreview) {
            System.out.println("\033[1m====== Preview the customer list =====\033[0m");
        } else {
            System.out.println("\033[1m====== Customer list =====\033[0m");
        }
        // Print the headers
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (Customer customer : customers) {
            String claimIds = customer.getClaims().isEmpty() ? "no claim yet" : String.join(", ", customer.getClaims().stream()
                    .map(Claim::getId)
                    .collect(Collectors.toList()));
            String dependentsText = "";
            if (customer instanceof PolicyHolder) {
                PolicyHolder policyHolder = (PolicyHolder) customer;
                dependentsText = policyHolder.getDependents().isEmpty() ? "no dependent yet" : String.join(", ", policyHolder.getDependents().stream()
                        .map(Customer::getId)
                        .collect(Collectors.toList()));
            }
//            else {
//                dependentsText = "he/she is a dependent";
//            }
            String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";

            System.out.printf(headerFormat,
                    customer.getId(),
                    customer.getFullName(),
                    customer.getInsuranceCard().getCardNumber(),
                    title,
                    claimIds,
                    dependentsText
                    );
        }
    }


    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }
}
