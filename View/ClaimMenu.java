/**
 * @author <Duong Viet Hoang - S3962514>
 */

package View;

import Controller.ClaimController;
import Controller.CustomerController;
import Model.BankingInfo;
import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimMenu {
    private MainMenu mainMenu;
    private static ClaimMenu instance;
    private final ClaimController claimController = ClaimController.getInstance();
    CustomerController customerController = CustomerController.getInstance();

    private final Scanner scanner = new Scanner(System.in);

    public static ClaimMenu getInstance() {
        if (instance == null) {
            instance = new ClaimMenu();
        }
        return instance;
    }

    public void setMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void claimMenu(){
        System.out.print("\n");
        int choice = 0;
        do {
            System.out.println("\033[1m======================== CLAIM MANAGER MENU ========================\033[0m");
            System.out.println("1. View All Claim");
            System.out.println("2. Add Claim");
            System.out.println("3. Remove Claim");
            System.out.println("4. Edit Claim");
            System.out.println("5. Save As File");
            System.out.println("6. Return");
            System.out.print("Enter your choice: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                continue; // Skip the rest of the loop and start over
            }

            switch (choice) {
                case 1:
                    System.out.print("\n");
                    do {
                        mainMenu.printClaimsInfo(claimController.getAll(), false);
                        System.out.println("1. View Detail Of A Claim");
                        System.out.println("2. Sorting");
                        System.out.println("3. Return");
                        System.out.print("Enter your choice: ");
                        try {
                            choice = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.nextLine(); // Consume the invalid input
                            continue; // Skip the rest of the loop and start over
                        }
                        switch (choice){
                            case 1: detailClaim(); break;
                            case 2: sortingClaim(); break;
                            case 3:
                                System.out.println("Returning...");
                        }
                    } while (choice != 3);
                    System.out.print("\n");
                    break;
                case 2:
                    System.out.print("\n");
                    try {
                        System.out.println("\033[1m============================= ADD CLAIM ===============================\033[0m");
                        List<Customer> customers = claimController.getListOfCustomers();

                        if (customers.isEmpty()) {
                            System.out.println("No customers in the system. You need to add a customer and his/her insurance card first.");
                            System.out.print("\n");
                            mainMenu.addCustomerAndCard();
                        } else {
                            String inSystem = "";
                            while (!inSystem.equals("y") && !inSystem.equals("n")) {
                                System.out.print("Is the customer for the claim already in the system? (y/n): ");
                                inSystem = scanner.nextLine().trim().toLowerCase();
                                if (!inSystem.equals("y") && !inSystem.equals("n")) {
                                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                                }
                            }

                            if (inSystem.equals("n")) {
                                System.out.println("You need to add the customer and his/her insurance card first.");
                                mainMenu.addCustomerAndCard();
                                // break; // Use this if you're inside a loop
                            }
                        }
                        mainMenu.printClaimsInfo(claimController.getAll(), true);
                        System.out.print("\n");
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

                        mainMenu.printCustomersAndCardsInfo(claimController.getListOfCustomers(), claimController.getListOfInsuranceCards(), true);

                        Customer insuredperson = null;
                        String customerId; // Initialize customerId outside the loop
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

                        System.out.print("Enter a list of document names without file prefix and extensions, separated by commas: ");
                        String input = scanner.nextLine();
                        List<String> listofdocuments;

                        if (input.isEmpty()) {
                            listofdocuments = new ArrayList<>();
                        } else {
                            List<String> rawListOfDocuments = Arrays.asList(input.split(","));
                            Set<String> documentSet = new LinkedHashSet<>(rawListOfDocuments);
                            listofdocuments = new ArrayList<>(documentSet);
                        }

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

                        System.out.print("===== BANKING INFORMATION OF " + insuredperson.getFullName().toUpperCase() +" =====" + "\n");
                        System.out.print("Enter the bank name: ");
                        String bank = scanner.nextLine().toUpperCase();
                        String name_bank = insuredperson.getFullName().toUpperCase();
                        System.out.print("Enter the card number: ");
                        String number_bank = scanner.nextLine();
                        BankingInfo receiverbankinginfor = new BankingInfo(bank, name_bank, number_bank);

                        // Create the new claim and add it to the controller
                        claimController.add(claimdate, insuredperson, cardnumber, examdate, listofdocuments, amount, status, receiverbankinginfor);

                        // Get the newly created claim
                        Claim newClaim = claimController.getOne(claimController.getListOfClaims().get(claimController.getListOfClaims().size() - 1).getId());

                        // Add the new claim to the customer's list of claims
                        insuredperson.setClaim(newClaim);
                        customerController.writeCustomersToFile();

                    } catch (Exception e) {
                        System.out.println("An error occurred. Please try again");
                    }
                    System.out.print("\n");
                    break;
                case 3:
                    System.out.print("\n");
                    try {
                        System.out.println("\033[1m=========================== REMOVE CLAIM ============================\033[0m");
                        mainMenu.printClaimsInfo(claimController.getAll(), true);
                        System.out.print("Enter claim ID to remove (f-xxxxxxxxxx): ");
                        String id = scanner.nextLine();
                        Claim claim = claimController.getOne(id);
                        if (claim != null && claimController.delete(id)) {
                            System.out.println("Removed claim " + id + " from the system");
                            Customer insuredperson = claim.getInsuredPerson();
                            if (insuredperson != null) {
                                List<Claim> customerClaims = insuredperson.getClaims();
                                for (int i = 0; i < customerClaims.size(); i++) {
                                    if (customerClaims.get(i).getId().equals(claim.getId())) {
                                        customerClaims.remove(i);
                                        System.out.println("Claim removed from insured person's list.");
                                        break;
                                    }
                                }
                            }
                            customerController.writeCustomersToFile();
                        } else {
                            System.out.println("Claim not found with the given ID.");
                        }
                    } catch (Exception e){
                        System.out.println("An error occurred, please try again.");
//                        e.printStackTrace();
                    }
                    System.out.print("\n");
                    break;
                case 4:
                    System.out.print("\n");
                    try {
                        System.out.println("\033[1m=========================== EDIT CLAIM =============================\033[0m");
                        mainMenu.printClaimsInfo(claimController.getAll(), true);

                        System.out.print("Enter claim ID to edit (f-xxxxxxxxxx): ");
                        String id = scanner.nextLine();
                        Claim claim = claimController.getOne(id);
                        if (claim == null) {
                            System.out.println("Claim not found with the given ID.");
                            claimMenu();
                        }

                        // Keep track of the old insured person
                        Customer oldInsuredPerson = claim.getInsuredPerson();

                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        System.out.print("Change claim date (dd-MM-yyyy) or press Enter to skip: ");
                        String claimDateInput = scanner.nextLine();
                        if (!claimDateInput.isEmpty()) {
                            try {
                                Date claimdate = formatter.parse(claimDateInput);
                                claim.setClaimDate(claimdate);
                            } catch (Exception e) {
                                System.out.println("Invalid date format. Please try again.");
                            }
                        }

                        mainMenu.printCustomersAndCardsInfo(claimController.getListOfCustomers(), claimController.getListOfInsuranceCards(), true);
                        System.out.print("Change insured person ID (c-xxxxxxx) for this claim or press Enter to skip: ");
                        String customerId = scanner.nextLine().trim();
                        if (!customerId.isEmpty()) {
                            Customer insuredperson = claimController.getCustomerById(customerId);
                            if (insuredperson != null) {
                                claim.setInsuredPerson(insuredperson);
                                if (oldInsuredPerson != null && !insuredperson.equals(oldInsuredPerson)) {
                                    List<Claim> oldInsuredPersonClaims = oldInsuredPerson.getClaims();
                                    for (int i = 0; i < oldInsuredPersonClaims.size(); i++) {
                                        if (oldInsuredPersonClaims.get(i).getId().equals(claim.getId())) {
                                            oldInsuredPersonClaims.remove(i);
                                            System.out.println("Claim removed from old insured person's list.");
                                            break; // Exit the loop after removing the claim
                                        }
                                    }
                                }
                                List<Claim> newInsuredPersonClaims = insuredperson.getClaims();
                                boolean claimAlreadyExists = newInsuredPersonClaims.stream().anyMatch(c -> c.getId().equals(claim.getId()));
                                if (!claimAlreadyExists) {
                                    newInsuredPersonClaims.add(claim);
                                    System.out.println("Claim added to new insured person's list.");
                                } else {
                                    System.out.println("Claim already exists in the new insured person's list.");
                                }
                                InsuranceCard cardnumber = insuredperson.getInsuranceCard();
                                System.out.println("\033[1mInsurance card number of " + insuredperson.getFullName() + " is " + cardnumber.getCardNumber() + "\033[0m");
                                claim.setCardNumber(cardnumber);
                                claim.getReceiverBankingInfo().setName(insuredperson.getFullName().toUpperCase());
                            }
                        }

                        System.out.print("Change exam date (dd-MM-yyyy) or press Enter to skip: ");
                        String examDateInput = scanner.nextLine();
                        if (!examDateInput.isEmpty()) {
                            try {
                                Date examdate = formatter.parse(examDateInput);
                                claim.setExamDate(examdate);
                            } catch (Exception e) {
                                System.out.println("Invalid date format. Please try again.");
                            }
                        }

                        boolean editingDocuments = true;
                        while (editingDocuments) {
                            System.out.print("\n");
                            System.out.println("\033[1m===== EDIT DOCUMENTS =====\033[0m");
                            System.out.println("Choose an option:");
                            System.out.println("1. Add a document");
                            System.out.println("2. Delete a document");
                            System.out.println("3. Skip");
                            System.out.print("Your choose: ");
                            String option = scanner.nextLine();

                            List<String> listofdocuments = new ArrayList<>(claim.getDocuments());
                            switch (option) {
                                case "1":
                                    for (String document : listofdocuments) {
                                        System.out.println(document);
                                    }
                                    System.out.print("Enter the name of the document to add (without full file prefix and extension): ");
                                    String documentToAdd = scanner.nextLine().trim(); // Trim to remove accidental whitespace
                                    if (!documentToAdd.isEmpty()) {
                                        // Construct the full document name
                                        documentToAdd = claim.getId() + "_" + claim.getCardNumber().getCardNumber() + "_" + documentToAdd + ".pdf";
                                        // Check if the document already exists to prevent duplicates
                                        boolean exists = listofdocuments.contains(documentToAdd);
                                        if (!exists) {
                                            listofdocuments.add(documentToAdd);
                                            System.out.println("Document added successfully.");
                                            claim.setDocuments(listofdocuments); // Update the claim with the new list of documents
                                        } else {
                                            System.out.println("Document already exists.");
                                        }
                                    } else {
                                        System.out.println("No document name entered.");
                                    }
                                    break;
                                case "2":
                                    for (String document : listofdocuments) {
                                        System.out.println(document);
                                    }
                                    System.out.print("Enter the name of the document to delete (full prefix and extension): ");
                                    String documentToDelete = scanner.nextLine();
                                    if (!documentToDelete.isEmpty()) {
                                        int documentIndex = -1;
                                        for (int i = 0; i < listofdocuments.size(); i++) {
                                            if (listofdocuments.get(i).equals(documentToDelete)) {
                                                documentIndex = i;
                                                break;
                                            }
                                        }
                                        if (documentIndex != -1) {
                                            listofdocuments.remove(documentIndex);
                                            System.out.println("Document removed successfully.");
                                        } else {
                                            System.out.println("Document not found: " + documentToDelete);
                                        }
                                        claim.setDocuments(listofdocuments);
                                    }
                                    break;
                                case "3":
                                    System.out.println("Skipping document editing.");
                                    editingDocuments = false; // menu will break the loop
                                    break;
                                default:
                                    System.out.println("Invalid option. Please choose 1 to add a document, 2 to delete a document, or 3 to skip.");
                                    break;
                            }
                        }

                        System.out.print("Change claim amount($) or press enter to Skip: ");
                        String amountInput = scanner.nextLine();
                        if (!amountInput.isEmpty()) {
                            try {
                                double amount = Double.parseDouble(amountInput);
                                claim.setClaimAmount(amount);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number format. Please try again.");
                            }
                        }

                        System.out.print("Change status (New, Processing, Done) or press Enter to skip: ");
                        String status = scanner.nextLine().trim().toLowerCase();
                        if (!status.isEmpty()) {
                            if (status.equals("new") || status.equals("processing") || status.equals("done")) {
                                status = status.substring(0, 1).toUpperCase() + status.substring(1);
                                claim.setStatus(status);
                            } else {
                                System.out.println("Invalid status. Please enter 'New', 'Processing', or 'Done' or Enter to skip.");
                            }
                        }

                        System.out.print("\n");
                        System.out.print("===== EDIT BANKING INFORMATION OF " + claim.getInsuredPerson().getFullName().toUpperCase() +" =====" + "\n");
                        System.out.print("Change the bank name or enter to skip: ");
                        String bank = scanner.nextLine().toUpperCase();
                        if (!bank.isEmpty()){
                            claim.getReceiverBankingInfo().setBank(bank);
                        }
                        System.out.print("Change the card number or enter to skip: ");
                        String number_bank = scanner.nextLine();
                        if (!number_bank.isEmpty()){
                            claim.getReceiverBankingInfo().setNumber(number_bank);
                        }

                        claimController.update(claim);
                        customerController.writeCustomersToFile();
                        System.out.println("Claim updated");
                    } catch (Exception e) {
                        System.out.println("An error occurred. Please try again");
                    }
                    System.out.print("\n");
                    break;
                case 5:
                    System.out.print("\n");
                    System.out.println("\033[1m=========================== SAVING CLAIM ============================\033[0m");
                    mainMenu.printClaimsInfo(claimController.getAll(), true);
                    do {
                        System.out.println("The claim table is currently sorted by the " + claimController.currentClaimOrder + " order");
                        System.out.println("Would you like to change the order before saving the file?");
                        System.out.println("1. Yes. moving to sorting menu");
                        System.out.println("2. No, save the file");
                        System.out.println("3. Return");
                        System.out.print("Enter your choice: ");
                        try {
                            choice = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            scanner.nextLine(); // Consume the invalid input
                            continue; // Skip the rest of the loop and start over
                        }
                        switch (choice){
                            case 1: sortingClaim(); break;
                            case 2: savingClaimMenu();break;
                            case 3:
                                System.out.println("Returning...");
                        }
                    } while (choice != 3);
                    System.out.print("\n");
                    break;
                case 6:
                    System.out.println("Returning...");
                    mainMenu.view();
                    break;
            }
        } while (choice!=6);
    }

    private void saveClaimListAsTable(List<Claim> claims) {
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
            maxLengths[1] = Math.max(maxLengths[1], claimController.formatDate(claim.getClaimDate()).length());
            String insuredPersonName = claim.getInsuredPerson() != null ? claim.getInsuredPerson().getFullName() : "no data";
            String cardNumber = claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data";
            maxLengths[2] = Math.max(maxLengths[2], insuredPersonName.length());
            maxLengths[3] = Math.max(maxLengths[3], cardNumber.length());
            maxLengths[4] = Math.max(maxLengths[4], claimController.formatDate(claim.getExamDate()).length());
            maxLengths[5] = Math.max(maxLengths[5], String.join(", ", claim.getDocuments()).length());
            maxLengths[6] = Math.max(maxLengths[6], decimalFormat.format(claim.getClaimAmount()).length());
            maxLengths[7] = Math.max(maxLengths[7], claim.getStatus().length());
            maxLengths[8] = Math.max(maxLengths[8], claim.getReceiverBankingInfo().printInfor().length());
        }

        // Create the header format with appropriate spacing
        String headerFormat = "|";
        String lineFormat = "+";
        String borderFormat = "+";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";  // +2 for padding
            lineFormat += new String(new char[width + 3]).replace('\0', '-') + "+";
            borderFormat += new String(new char[width + 3]).replace('\0', '-') + "+";
        }
        headerFormat += "%n";
        lineFormat = lineFormat.substring(0, lineFormat.length() - 1) + "%n";
        borderFormat = borderFormat.substring(0, borderFormat.length() - 1) + "%n";

        // Calculate the total width of the table including border characters
        int totalWidth = Arrays.stream(maxLengths).sum() + maxLengths.length * 3 + 1;

        // Calculate the width of the borders
        int borderWidth = maxLengths.length + 1;

        // Create the title with the correct padding to make it the same length as the table width including the borders
        String title = "CLAIM LIST";
        int titleWidth = title.length();
        int totalPadding = totalWidth - titleWidth;
        int paddingBefore = totalPadding / 2 + borderWidth / 2 + 3; // adding half the border width to the padding
        int paddingAfter = totalPadding - paddingBefore + borderWidth / 2 + 3;

        String titlePaddingBefore = new String(new char[paddingBefore]).replace('\0', '=');
        String titlePaddingAfter = new String(new char[paddingAfter]).replace('\0', '=');

        // Combine parts to create the full title
        String fullTitle = titlePaddingBefore + title + titlePaddingAfter;

        PrintWriter out;
        String filePath; // Declare filePath here

            // Prompt user for file name here
            System.out.print("Enter file name to save as TXT: ");
            String fileName = scanner.nextLine();
            filePath = "savedFile/" + fileName+ ".txt"; // Adjust directory as needed

            try {
                out = new PrintWriter(filePath);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
                return; // Exit the method if file not found
            }

        out.println(fullTitle);
        out.printf(borderFormat);
        out.printf(headerFormat, (Object[]) headers);
        out.printf(lineFormat);

        for (Claim claim : claims) {
            Object[] rowData = {
                    claim.getId(),
                    claimController.formatDate(claim.getClaimDate()),
                    claim.getInsuredPerson() != null ? claim.getInsuredPerson().getFullName() : "no data",
                    claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data",
                    claimController.formatDate(claim.getExamDate()),
                    String.join(", ", claim.getDocuments()),
                    decimalFormat.format(claim.getClaimAmount()),
                    claim.getStatus(),
                    claim.getReceiverBankingInfo().printInfor()
            };
            out.printf(headerFormat, rowData);
            out.printf(lineFormat);
        }

            System.out.println("File saved successfully at " + filePath);

        out.close();
    }

    private void saveClaimListAsTsv(List<Claim> claims) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        String delimiter = "\t";
        String lineEnd = "\r\n";
        String[] headers = {
                "ID", "Claim Date", "Insured Person", "Card Number", "Exam Date",
                "List of Documents", "Claim Amount", "Status", "Receiver Banking Info"
        };

        PrintWriter out;

            // Prompt user for file name here
            System.out.print("Enter file name to save as TSV: ");
            String fileName = scanner.nextLine();
            String filePath = "savedFile/" + fileName + ".tsv"; // Adjust directory as needed

            try {
                out = new PrintWriter(filePath);

                // Print the headers
                out.print(String.join(delimiter, headers) + lineEnd);

                // Print each data row
                for (Claim claim : claims) {
                    String[] rowData = {
                            claim.getId(),
                            claimController.formatDate(claim.getClaimDate()),
                            claim.getInsuredPerson() != null ? claim.getInsuredPerson().getFullName() : "no data",
                            claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data",
                            claimController.formatDate(claim.getExamDate()),
                            String.join(";", claim.getDocuments()),
                            decimalFormat.format(claim.getClaimAmount()),
                            claim.getStatus(),
                            claim.getReceiverBankingInfo().printInfor()
                    };
                    out.print(String.join(delimiter, rowData) + lineEnd);
                }
                System.out.println("File saved successfully at " + filePath);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
                return; // Exit the method if file not found
            } out.close();
    }

    private void saveClaimListAsCsv(List<Claim> claims) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        String delimiter = ",";
        String lineEnd = "\r\n";
        String[] headers = {
                "ID", "Claim Date", "Insured Person", "Card Number", "Exam Date",
                "List of Documents", "Claim Amount", "Status", "Receiver Banking Info"
        };

        PrintWriter out;

            // Prompt user for file name here
            System.out.print("Enter file name to save as CSV: ");
            String fileName = scanner.nextLine();
            String filePath = "savedFile/" + fileName + ".csv"; // Adjust directory as needed

            try {
                out = new PrintWriter(filePath);

                // Print the headers
                out.print(String.join(delimiter, headers) + lineEnd);

                // Print each data row
                for (Claim claim : claims) {
                    String[] rowData = {
                            claim.getId(),
                            claimController.formatDate(claim.getClaimDate()),
                            claim.getInsuredPerson() != null ? claim.getInsuredPerson().getFullName() : "no data",
                            claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data",
                            claimController.formatDate(claim.getExamDate()),
                            String.join(";", claim.getDocuments()),
                            decimalFormat.format(claim.getClaimAmount()),
                            claim.getStatus(),
                            claim.getReceiverBankingInfo().printInfor()
                    };

                    // Handle potential commas in the data
                    for (int i = 0; i < rowData.length; i++) {
                        rowData[i] = "\"" + rowData[i] + "\"";
                    }

                    out.print(String.join(delimiter, rowData) + lineEnd);
                }
                System.out.println("File saved successfully at " + filePath);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
                return; // Exit the method if file not found
        }
        out.close();
    }

    private void savingClaimMenu() {
        int choice = 0;
        do {
            System.out.println("1. Save as table format TXT");
            System.out.println("2. Save as TSV");
            System.out.println("3. Save as CSV");
            System.out.println("4. Return");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                continue; // Skip the rest of the loop and start over
            }
            switch (choice){
                case 1:
                    saveClaimListAsTable(claimController.getListOfClaims());
                    claimMenu();
                    break;
                case 2:
                    saveClaimListAsTsv(claimController.getListOfClaims());
                    claimMenu();
                    break;
                case 3:
                    saveClaimListAsCsv(claimController.getListOfClaims());
                    claimMenu();
                    break;
                case 4:
                    System.out.println("Returning...");
                    claimMenu();
            }
        } while (choice != 4);
    }

    private void sortingClaim() {
        System.out.print("\n");
        mainMenu.printClaimsInfo(claimController.getAll(), false);
        int choice = 0;
        do {
            System.out.println("SORTING BY:");
            System.out.println("1. Claim Date: Oldest to Newest");
            System.out.println("2. Claim Date: Newest to Oldest");
            System.out.println("3. Exam Date: Oldest to Newest");
            System.out.println("4. Exam Date: Newest to Oldest");
            System.out.println("5. Claim Amount: Lowest to Highest");
            System.out.println("6. Claim Amount: Highest to Lowest");
            System.out.println("7. Return");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                continue; // Skip the rest of the loop and start over
            }
            switch (choice){
                case 1:
                    claimController.sortClaimsByClaimDate(true);
                    mainMenu.printClaimsInfo(claimController.getAll(), false);
                    break;
                case 2:
                    claimController.sortClaimsByClaimDate(false);
                    mainMenu.printClaimsInfo(claimController.getAll(), false);
                    break;
                case 3:
                    claimController.sortClaimsByExamDate(true);
                    mainMenu.printClaimsInfo(claimController.getAll(), false);
                    break;
                case 4:
                    claimController.sortClaimsByExamDate(false);
                    mainMenu.printClaimsInfo(claimController.getAll(), false);
                    break;
                case 5:
                    claimController.sortClaimsByClaimAmount(true);
                    mainMenu.printClaimsInfo(claimController.getAll(), false);
                    break;
                case 6:
                    claimController.sortClaimsByClaimAmount(false);
                    mainMenu.printClaimsInfo(claimController.getAll(), false);
                    break;
                case 7:
                    System.out.println("Returning...");
            }
        } while (choice != 7);
    }

    private void detailClaim() {
        System.out.print("Enter claim ID(f-xxxxxxxxxx): ");
        String claimId = scanner.nextLine();
        Claim claim = claimController.getOne(claimId);
        if (claim == null) {
            System.out.println("Claim not found with the given ID.");
            return;
        }
        Customer insuredPerson = claim.getInsuredPerson();
        List<Customer> insuredPersonList = Collections.singletonList(insuredPerson);

        // Following menu, print the details of the claim
        System.out.print("\n");
        System.out.println("———————————————————————————— CLAIM DETAIL ———————————————————————————");
        System.out.println("ID: " + claim.getId());
        System.out.println("Claim Date: " + new SimpleDateFormat("dd-MM-yyyy").format(claim.getClaimDate()));
        if (claim.getInsuredPerson() != null) {
            System.out.println("Insured Person: " + claim.getInsuredPerson().getFullName());
        } else {
            System.out.println("Insured Person: No data");
        }

        if (claim.getCardNumber() != null) {
            System.out.println("Card Number: " + claim.getCardNumber().getCardNumber());
        } else {
            System.out.println("Card Number: No data");
        }
        System.out.println("Exam Date: " + new SimpleDateFormat("dd-MM-yyyy").format(claim.getExamDate()));
        System.out.println("List of Documents: " + claim.getDocuments().stream().collect(Collectors.joining(", ")));
        System.out.println("Claim Amount($): " + claim.getClaimAmount());
        System.out.println("Status: " + claim.getStatus());
        System.out.println("Receiver Banking Info: " + claim.getReceiverBankingInfo().printInfor());

        if (insuredPerson != null) {
            System.out.print("\n");
            System.out.println("—————— INSURED PERSON DETAIL OF " + insuredPerson.getFullName().toUpperCase() + " ——————");
            mainMenu.printACustomerInfo(insuredPersonList);
        } else {
            System.out.print("\n");
            System.out.println("————————————————————————————— INSURED PERSON DETAIL: No data ————————————————————————————");
        }

        if (claim.getCardNumber() != null) {
            mainMenu.printACardInfo(claim.getCardNumber(), insuredPerson != null ? insuredPerson.getFullName() : "No data");
        } else {
            System.out.println("\033[1m===== CARD INFO: No data =====\033[0m");
        }
        System.out.print("\n");
    }
}
