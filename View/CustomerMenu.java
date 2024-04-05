/**
 * @author <Duong Viet Hoang - S3962514>
 */

package View;

import Controller.ClaimController;
import Controller.CustomerController;
import Controller.InsuranceCardController;
import Model.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerMenu {
    private MainMenu mainMenu;
    private static CustomerMenu instance;

    private final CustomerController customerController = CustomerController.getInstance();

    private final ClaimController claimController = ClaimController.getInstance();

    private final InsuranceCardController insuranceCardController = InsuranceCardController.getInstance();

    private final Scanner scanner = new Scanner(System.in);

    public static CustomerMenu getInstance() {
        if (instance == null) {
            instance = new CustomerMenu();
        }
        return instance;
    }

    public void setMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void customerMenu(){
        System.out.print("\n");
        int choice = 0;
        do {
            System.out.println("\033[1m========================= CUSTOMER MANAGER =========================\033[0m");
            System.out.println("1. View All Customer");
            System.out.println("2. Add Customer");
            System.out.println("3. Remove Customer");
            System.out.println("4. Edit Customer");
            System.out.println("5. Save as file");
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

                        this.printCustomersInfo(customerController.getListOfCustomers(), false);
                        System.out.println("1. View Detail Of A Customer (Claims list + Dependents list)");
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
                            case 1: detailCustomer(); break;
                            case 2: sortingCustomer(); break;
                            case 3:
                                System.out.println("Returning...");
                        }
                    } while (choice != 3);
                    System.out.print("\n");
                    break;
                case 2:
                    System.out.print("\n");
                    mainMenu.addCustomerAndCard();
                    System.out.print("\n");
                    break;
                case 3:
                    System.out.print("\n");
                    try {
                        System.out.println("\033[1m========================== REMOVE CUSTOMER ==========================\033[0m");
                        this.printCustomersInfo(customerController.getAll(), true);
                        System.out.println("*** Notice: Deleting a customer also removes their insurance card ***");
                        System.out.println("*** Customer details will be erased from all related claims for management ***");
                        System.out.print("Enter customer ID to remove (c-xxxxxxx): ");
                        Scanner scanner = new Scanner(System.in);
                        String id = scanner.nextLine();
                        Customer customer = customerController.getOne(id);
                        if (customer != null) {
                            if (customer instanceof Dependent) {
                                System.out.println("*** Note: Deleting this dependent will also remove them from the list of dependents of the associated policy holder. ***");
                                if (confirmDeletion("Are you sure you want to delete this dependent? (y/n): ")) {
                                    if (customerController.deleteCustomerDPD(id)) {
                                        System.out.println("Removed dependent " + id + " from the system");
                                    } else {
                                        System.out.println("Invalid ID, please try again");
                                    }
                                }
                            } else if (customer instanceof PolicyHolder) {
                                System.out.println("*** Note: Deleting this policy holder will also remove all their dependents. ***");
                                if (confirmDeletion("Are you sure you want to delete the policy holder and all their dependents? (y/n): ")) {
                                    if (customerController.deleteCustomerPLC(id)) {
                                        System.out.println("Removed policy holder " + id + " and all dependents from the system");
                                    } else {
                                        System.out.println("Invalid ID, please try again");
                                    }
                                } else {
                                    System.out.println("Deletion canceled by user.");
                                }
                            }
                        } else {
                            System.out.println("Invalid ID, please try again");
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred, please try again.");
                    }
                    System.out.print("\n");
                    break;
                case 4:
                    System.out.print("\n");
                    try {
                        System.out.println("\033[1m=========================== EDIT CUSTOMER ===========================\033[0m");
                        this.printCustomersInfo(customerController.getAll(), true);

                        System.out.print("Enter customer ID to edit (c-xxxxxxx): ");
                        String id = scanner.nextLine();
                        Customer customer = customerController.getOne(id);
                        if (customer == null) {
                            System.out.println("Card not found with the given ID.");
                            customerMenu();
                        }

                        System.out.print("Enter full name or press enter to skip: ");
                        String fullname = scanner.nextLine();
                        if (!fullname.isEmpty()){
                            if (customer != null) {
                                customer.setFullName(fullname);
                            } else {
                                System.out.println("Customer not found.");
                            }
                            // Update full name in claim list
                            for (Claim claim : claimController.getListOfClaims()) {
                                if (claim.getInsuredPerson() != null && claim.getInsuredPerson().getId().equals(id)) {
                                    claim.getInsuredPerson().setFullName(fullname);
                                }
                            }
                            // Update full name in insurance card list
                            for (InsuranceCard card : insuranceCardController.getListOfInsuranceCards()) {
                                if (card.getCardHolder() != null && card.getCardHolder().getId().equals(id)) {
                                    card.getCardHolder().setFullName(fullname);
                                }
                            }
                        }

                        claimController.writeCustomersToFile();
                        System.out.println("Customer and related information updated");
                    } catch (Exception e) {
                        System.out.println("An error occurred. Please try again");
                    }
                    System.out.print("\n");
                    break;
                case 5:
                    System.out.print("\n");
                    System.out.println("\033[1m========================= SAVING CUSTOMER ==========================\033[0m");
                    this.printCustomersInfo(customerController.getAll(), true);
                    do {
                        System.out.println("The customer table is currently sorted by the " + customerController.currentCustomerOrder + " order");
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
                            case 1: sortingCustomer(); break;
                            case 2: savingCustomerMenu();break;
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

    private void savingCustomerMenu() {
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
                    saveCustomerListAsTable(claimController.getListOfCustomers());
                    customerMenu();
                    break;
                case 2:
                    saveCustomerListAsTsv(claimController.getListOfCustomers());
                    customerMenu();
                    break;
                case 3:
                    saveCustomerListAsCsv(claimController.getListOfCustomers());
                    customerMenu();
                    break;
                case 4:
                    System.out.println("Returning...");
                    customerMenu();
            }
        } while (choice != 4);
    }

    private boolean confirmDeletion(String message) {
        Scanner scanner = new Scanner(System.in);
        String confirmation;
        do {
            System.out.print(message);
            confirmation = scanner.nextLine().toLowerCase();
            if (!confirmation.equals("y") && !confirmation.equals("n")) {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        } while (!confirmation.equals("y") && !confirmation.equals("n"));
        return confirmation.equals("y");
    }

    private void sortingCustomer() {
        System.out.print("\n");
        this.printCustomersInfo(customerController.getAll(), false);
        int choice = 0;
        do {
            System.out.println("SORTING BY:");
            System.out.println("1. Claim Count: least to most");
            System.out.println("2. Claim Count: most to least");
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
                case 1:
                    customerController.sortCustomerByNumberOfClaim(true);
                    this.printCustomersInfo(claimController.getListOfCustomers(), false);
                    break;
                case 2:
                    customerController.sortCustomerByNumberOfClaim(false);
                    this.printCustomersInfo(claimController.getListOfCustomers(), false);
                    break;
                case 3:
                    System.out.println("Returning...");
            }
        } while (choice != 3);
    }

    private void detailCustomer() {
        System.out.print("Enter customer ID(c-xxxxxxx): ");
        String customerId = scanner.nextLine();
        Customer customer = customerController.getOne(customerId); // Ensure `customerController` is the correct instance variable name
        if (customer == null) {
            System.out.println("Customer not found with the given ID.");
            return;
        }
        System.out.print("\n");
        System.out.println("——————————————————————————— CUSTOMER DETAIL ————————————————————————");
        System.out.println("ID: " + customer.getId());
        System.out.println("Full Name: " + customer.getFullName());
        System.out.println("Title: " + (customer instanceof PolicyHolder ? "Policy Holder" : "Dependent"));
        System.out.println("Insurance Card: " + (customer.getInsuranceCard() != null ? customer.getInsuranceCard().getCardNumber() : "No card"));
        System.out.println("List Of Claims: " + (customer.getClaims().isEmpty() ? "no claim yet" : customer.getClaims().stream()
                .map(Claim::getId)
                .collect(Collectors.joining(", "))));
        System.out.println("List Of Dependents: " + (customer instanceof PolicyHolder ? ((PolicyHolder) customer).getDependents().stream()
                .map(Dependent::getFullName)
                .collect(Collectors.joining(", ")) : "N/A"));

        mainMenu.printACardInfo(customer.getInsuranceCard(), customer.getFullName());

        // Check if customer has claims and print details
        if (customer.getClaims().isEmpty()) {
            System.out.println("\033[1m===== " +  customer.getFullName().toUpperCase() +" HAVE NO CLAIM YET"+ " =====\033[0m");
        } else {
            printAClaimInfo(customer.getClaims(),customer.getFullName() ); // Pass the entire list of claims
        }

        // If customer is a PolicyHolder, print dependents information
        if (customer instanceof PolicyHolder) {
            PolicyHolder policyHolder = (PolicyHolder) customer;
            printADependentInfo(policyHolder.getDependents(), customer.getFullName());
        }

        System.out.print("\n");
    }

    private void printADependentInfo(List<Dependent> dependents, String customerName) {
        if (dependents == null || dependents.isEmpty()) {
            System.out.println("List Of Dependents: no dependents yet");
            return;
        }

        // Column headers for dependents
        String[] headers = {
                "Dependent ID", "Full Name", "Insurance Card"
        };

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        // Update column widths based on the dependent data
        for (Dependent dependent : dependents) {
            maxLengths[0] = Math.max(maxLengths[0], dependent.getId().length());
            maxLengths[1] = Math.max(maxLengths[1], dependent.getFullName().length());
            String cardNumber = dependent.getInsuranceCard() != null ? dependent.getInsuranceCard().getCardNumber() : "No card";
            maxLengths[2] = Math.max(maxLengths[2], cardNumber.length());
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the table header for dependents
        System.out.print("\n");
        System.out.println("—————————— DEPENDENT LIST OF " + customerName.toUpperCase() + " —————————");
        System.out.printf(headerFormat, (Object[]) headers);

        // Print each dependent's detail
        for (Dependent dependent : dependents) {
            System.out.printf(headerFormat,
                    dependent.getId(),
                    dependent.getFullName(),
                    claimController.getInsuranceCardNumberById(dependent.getId())
            );
        }
    }

    private void printAClaimInfo(List<Claim> claims, String customerName) {
        if (claims == null || claims.isEmpty()) {
            System.out.println("No claims found.");
            return;
        }

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

        // Update column widths based on the claim data
        for (Claim claim : claims) {
            maxLengths[0] = Math.max(maxLengths[0], claim.getId().length());
            maxLengths[1] = Math.max(maxLengths[1], claimController.formatDate(claim.getClaimDate()).length());
            if (claim.getInsuredPerson() != null) {
                maxLengths[2] = Math.max(maxLengths[2], claim.getInsuredPerson().getFullName().length());
            } else {
                System.out.println("No data");
            }
            if (claim.getCardNumber() != null) {
                maxLengths[3] = Math.max(maxLengths[3], claim.getCardNumber().getCardNumber().length());
            } else {
                System.out.println("No data");
            }
            maxLengths[4] = Math.max(maxLengths[4], claimController.formatDate(claim.getExamDate()).length());
            maxLengths[5] = Math.max(maxLengths[5], String.join(", ", claim.getDocuments()).length());
            maxLengths[6] = Math.max(maxLengths[6], decimalFormat.format(claim.getClaimAmount()).length());
            maxLengths[7] = Math.max(maxLengths[7], claim.getStatus().length());
            maxLengths[8] = Math.max(maxLengths[8], claim.getReceiverBankingInfo().printInfor().length());
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the table header
        System.out.print("\n");
        System.out.println("———————————— CLAIM DETAIL OF " + customerName.toUpperCase() + " —————————");
        System.out.printf(headerFormat, (Object[]) headers);

        // Print each claim's detail
        for (Claim claim : claims) {
            // Print the claim data, same as before
            String formattedAmount = decimalFormat.format(claim.getClaimAmount());
            System.out.printf(headerFormat,
                    claim.getId(),
                    claimController.formatDate(claim.getClaimDate()),
                    (claim.getInsuredPerson() != null) ? claim.getInsuredPerson().getFullName() : "No data",
                    (claim.getCardNumber() != null) ? claim.getCardNumber().getCardNumber() : "No data",
                    claimController.formatDate(claim.getExamDate()),
                    String.join(", ", claim.getDocuments()),
                    formattedAmount,
                    claim.getStatus(),
                    claim.getReceiverBankingInfo().printInfor());
        }
    }

    private void printCustomersInfo(List<Customer> customers, boolean isPreview) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        // Column headers
        String[] headers = {
                "ID", "Full Name", "Insurance Card", "Title"
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
            System.out.println("———————————————————— PREVIEW THE CUSTOMER LIST —————————————————————");
        } else {
            System.out.println("\033[1m========================== VIEW CUSTOMER ============================\033[0m");
        }
        // Print the headers
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (Customer customer : customers) {

            String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";

            System.out.printf(headerFormat,
                    customer.getId(),
                    customer.getFullName(),
                    customer.getInsuranceCard().getCardNumber(),
                    title
            );
        }
        if (isPreview) {

            System.out.println("————————————————————————————————————————————————————————————————————");
        } else {
            System.out.println("\033[1m=====================================================================\033[0m");
        }
    }

    private void saveCustomerListAsCsv(List<Customer> customers) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        String delimiter = ",";
        String lineEnd = "\r\n";
        String[] headers = {
                "ID", "Full Name", "Insurance Card", "Title"
        };


            System.out.print("Enter file name to save as CSV: ");
            String fileName = scanner.nextLine();
            String filePath = "savedFile/" + fileName + ".csv"; // Adjust directory as needed

            try (PrintWriter out = new PrintWriter(filePath)) {
                // Print the headers, enclosing each header in quotes
                out.println(String.join(delimiter, Arrays.stream(headers).map(header -> "\"" + header + "\"").toArray(String[]::new)));

                // Print each data row
                for (Customer customer : customers) {
                    String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";
                    String insuranceCardNumber = customer.getInsuranceCard() != null ? customer.getInsuranceCard().getCardNumber() : "N/A";
                    // Enclose each field in quotes and join with delimiter
                    out.printf("\"%s\"%s\"%s\"%s\"%s\"%s\"%s\"%s",
                            customer.getId(),
                            delimiter,
                            customer.getFullName(),
                            delimiter,
                            insuranceCardNumber,
                            delimiter,
                            title,
                            lineEnd
                    );
                }
                System.out.println("File saved successfully at " + filePath);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
            }
    }

    private void saveCustomerListAsTable(List<Customer> customers) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);

        // Column headers
        String[] headers = {"ID", "Full Name", "Insurance Card", "Title"};

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        // Update column widths based on the data
        for (Customer customer : customers) {
            maxLengths[0] = Math.max(maxLengths[0], customer.getId().length());
            maxLengths[1] = Math.max(maxLengths[1], customer.getFullName().length());
            String insuranceCardNumber = customer.getInsuranceCard() != null ? customer.getInsuranceCard().getCardNumber() : "N/A";
            maxLengths[2] = Math.max(maxLengths[2], insuranceCardNumber.length());
            String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";
            maxLengths[3] = Math.max(maxLengths[3], title.length());
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
        int paddingBefore = totalPadding / 2 + borderWidth / 2; // adding half the border width to the padding
        int paddingAfter = totalPadding - paddingBefore + borderWidth / 2 + 1;

        String titlePaddingBefore = new String(new char[paddingBefore]).replace('\0', '=');
        String titlePaddingAfter = new String(new char[paddingAfter]).replace('\0', '=');

        // Combine parts to create the full title
        String fullTitle = titlePaddingBefore + title + titlePaddingAfter;

        PrintWriter out = null;

            // Prompt user for file name here
            System.out.print("Enter file name to save as TXT: ");
            String fileName = scanner.nextLine();
            String filePath = "savedFile/" + fileName+ ".txt"; // Adjust directory as needed

            try {
                out = new PrintWriter(filePath);

                out.println(fullTitle);
                out.printf(borderFormat);
                out.printf(headerFormat, (Object[]) headers);
                out.printf(lineFormat);

                for (Customer customer : customers) {
                    Object[] rowData = {
                            customer.getId(),
                            customer.getFullName(),
                            customer.getInsuranceCard() != null ? customer.getInsuranceCard().getCardNumber() : "N/A",
                            customer instanceof PolicyHolder ? "Policy Holder" : "Dependent"
                    };
                    out.printf(headerFormat, rowData);
                    out.printf(lineFormat);
                }
                System.out.println("File saved successfully at " + filePath);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
                return; // Exit the method if file not found
            }
        out.close();
    }

    private void saveCustomerListAsTsv(List<Customer> customers) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        String delimiter = "\t";
        String lineEnd = "\r\n";
        String[] headers = {
                "ID", "Full Name", "Insurance Card", "Title" // Assuming "Insured Person" was a mistake and removed it
        };


            System.out.print("Enter file name to save as TSV: ");
            String fileName = scanner.nextLine();
            String filePath = "savedFile/" + fileName + ".tsv"; // Adjust directory as needed

            try (PrintWriter out = new PrintWriter(filePath)) {
                // Print the headers
                out.println(String.join(delimiter, headers));

                // Print each data row
                for (Customer customer : customers) {
                    String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";
                    String insuranceCardNumber = customer.getInsuranceCard() != null ? customer.getInsuranceCard().getCardNumber() : "N/A";
                    out.printf("%s%s%s%s%s%s%s%s",
                            customer.getId(),
                            delimiter,
                            customer.getFullName(),
                            delimiter,
                            insuranceCardNumber,
                            delimiter,
                            title,
                            lineEnd
                    );
                }
                System.out.println("File saved successfully at " + filePath);

            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
            }
    }
}
