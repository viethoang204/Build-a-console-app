package View;

import Controller.ClaimController;
import Controller.CustomerController;
import Controller.InsuranceCardController;
import Model.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    private ClaimController claimController = ClaimController.getInstance();
    private CustomerController customerController = CustomerController.getInstance();
    private InsuranceCardController insuranceCardController = InsuranceCardController.getInstance();

    public Menu(){
    }

    public void view(){
        int choice = 0;
        do {
            System.out.println("\033[1m===== HOME PAGE =====\033[0m");
            System.out.println("1. Claim Manager");
            System.out.println("2. Customer Manager");
            System.out.println("3. Insurance Card Manager");
            System.out.println("4. Exit");
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
                case 1: claimMenu(); break;
                case 2: customerMenu(); break;
                case 3: cardMenu(); break;
            }
        } while (choice != 4);
    }

    private void claimMenu(){
//        claimController.loadClaimsFromFile();
        int choice = 0;
        do {
            System.out.println("\033[1m===== CLAIM MANAGER MENU =====\033[0m");
            System.out.println("1. View All Claim");
            System.out.println("2. Add Claim");
            System.out.println("3. Remove Claim");
            System.out.println("4. Update Claim");
            System.out.println("5. Return");
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
                    this.printClaimsInfo(claimController.getAll(), false);
                    do {
                        System.out.println("1. View Detail Of A Customer");
                        System.out.println("2. Sorting");
                        System.out.println("3. Export To File");
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
                            case 1: detailClaim(); break;
                            case 2:
                            case 3:
                            case 4:
                                System.out.println("Returning...");
                        }
                    } while (choice != 4);
                    break;
                case 2:
                    try {
                        this.printClaimsInfo(claimController.getAll(), true);
                        System.out.println("===== CREATE NEW CLAIM =====");
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

                        System.out.print("===== BANKING INFORMATION OF " + insuredperson.getFullName().toUpperCase() +" =====" + "\n");
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
                        this.printClaimsInfo(claimController.getAll(), true);
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
                    System.out.println("case 4.");
                    break;
                case 5:
                    System.out.println("Returning...");
            }
        } while (choice!=5);
    }

    private void cardMenu() {
        int choice = 0;
        do {
            System.out.println("\033[1m===== CARD MANAGER =====\033[0m");
            System.out.println("1. View All Insurance Card");
            System.out.println("2. Add Customer and his/her Insurance Card");
            System.out.println("3. Remove Insurance Card");
            System.out.println("4. Update Insurance Card");
            System.out.println("5. Return");
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
                    this.printCardsInfo(insuranceCardController.getListOfInsuranceCards(), false);
                    do {
                        System.out.println("1. View Detail Of A Insurance Card");
                        System.out.println("2. Sorting");
                        System.out.println("3. Export To File");
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
                        switch (choice) {
                            case 1:
                                detailInsuranceCard();
                                break;
                            case 2:
                                // Implementation for Sorting
                                break;
                            case 3:
                                // Implementation for Export To File
                                break;
                            case 4:
                                System.out.println("Returning...");
                                break;
                        }
                    } while (choice != 4);
                    break;
                case 2:
                    // Implementation for Add Customer and his/her Insurance Card
                    break;
                case 3:
                    try {
                        this.printCardsInfo(insuranceCardController.getAll(), true);
                        System.out.println("*** Notice: Deleting a card also removes their owner information ***");
                        System.out.println("*** Card Information will be erased from all related claims for management ***");
                        System.out.print("Enter customer ID to remove: ");
                        String id = scanner.nextLine();

                        String customerId = null;
                        for (Customer customer : claimController.getListOfCustomers()) {
                            if (customer.getInsuranceCard() != null && customer.getInsuranceCard().getCardNumber().equals(id.trim())) {
                                customerId = customer.getId();
                                break;
                            }
                        }

                        Customer customer = customerController.getOne(customerId);
                        if (customer != null) {
                            if (customer instanceof Dependent) {
//                                System.out.println("*** Note: Deleting a dependent's card will also remove them from the list of dependents of the associated policy holder. ***");
                                if (customerController.deleteCustomerDPD(customerId)) {
                                    System.out.println("Removed card " + id + " from the system");
                                } else {
                                    System.out.println("Invalid ID, please try again");
                                }
                            } else if (customer instanceof PolicyHolder) {
                                System.out.println("*** Note: Deleting a policyholder's card will also remove all of their dependents' cards ***");
                                if (customerController.deleteCustomerPLC(customerId)) {
                                    System.out.println("Removed card " + id + " from the system");
                                } else {
                                    System.out.println("Invalid ID, please try again");
                                }
                            }
                        } else {
                            System.out.println("Invalid ID, please try again");
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred, please try again.");
                    }
                    break;
                case 4:
                    // Implementation for Update Insurance Card
                    break;
                case 5:
                    System.out.println("Returning...");
                    break;
            }
        } while (choice != 5);
    }

    private void customerMenu(){
        int choice = 0;
        do {
            System.out.println("\033[1m===== CUSTOMER MANAGER =====\033[0m");
            System.out.println("1. View All Customer");
            System.out.println("2. Add Customer And His/Her Insurance Card");
            System.out.println("3. Remove Customer");
            System.out.println("4. Update Customer");
            System.out.println("5. Return");
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
                    this.printCustomersInfo(customerController.getListOfCustomers(), false);
                    do {
                        System.out.println("1. View Detail Of A Customer");
                        System.out.println("2. Sorting");
                        System.out.println("3. Export To File");
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
                            case 1: detailCustomer(); break;
                            case 2:
                            case 3:
                            case 4:
                                System.out.println("Returning...");
                        }
                    } while (choice != 4);
                    break;
                case 2:
                case 3:
                    try {
                        this.printCustomersInfo(customerController.getAll(), true);
                        System.out.println("*** Notice: Deleting a customer also removes their insurance card ***");
                        System.out.println("*** Customer details will be erased from all related claims for management ***");
                        System.out.print("Enter customer ID to remove (c-xxxxxxx): ");
                        String id = scanner.nextLine();
                        Customer customer = customerController.getOne(id);
                        if (customer != null) {
                            if (customer instanceof Dependent) {
                                System.out.println("*** Note: Deleting a dependent will also remove them from the list of dependents of the associated policy holder. ***");
                                if (customerController.deleteCustomerDPD(id)) {
                                    System.out.println("Removed dependent " + id + " from the system");
                                } else {
                                    System.out.println("Invalid ID, please try again");
                                }
                            } else if (customer instanceof PolicyHolder) {
                                System.out.println("*** Note: Deleting a policy holder will also remove all their dependents. ***");
                                if (customerController.deleteCustomerPLC(id)) {
                                    System.out.println("Removed policy holder " + id + " from the system");
                                } else {
                                    System.out.println("Invalid ID, please try again");
                                }
                            }
                        } else {
                            System.out.println("Invalid ID, please try again");
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred, please try again.");
                    }
                    break;
                case 4:
                case 5:
                    System.out.println("Returning...");
            }
        } while (choice!=5);
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

        // Following this, print the details of the claim
        System.out.println("\033[1m===== CLAIM DETAIL =====\033[0m");
        System.out.println("ID: " + claim.getId());
        System.out.println("Claim Date: " + new SimpleDateFormat("dd-MM-yyyy").format(claim.getClaimDate()));
        System.out.println("Insured Person: " + claim.getInsuredPerson().getFullName());
        System.out.println("Card Number: " + claim.getCardNumber().getCardNumber());
        System.out.println("Exam Date: " + new SimpleDateFormat("dd-MM-yyyy").format(claim.getExamDate()));
        System.out.println("List of Documents: " + claim.getDocuments().stream().collect(Collectors.joining(", ")));
        System.out.println("Claim Amount: " + claim.getClaimAmount());
        System.out.println("Status: " + claim.getStatus());
        System.out.println("Receiver Banking Info: " + claim.getReceiverBankingInfo().printInfor());

        System.out.println("\033[1m===== INSURED PERSON DETAIL OF " + insuredPerson.getFullName().toUpperCase() + " =====\033[0m");
        printACustomerInfo(insuredPersonList);
        printACardInfo(claim.getCardNumber(), insuredPerson.getFullName());
    }

    private void detailInsuranceCard() {
        System.out.print("Enter insurance card number: ");
        String cardNumber = scanner.nextLine();
        InsuranceCard card = insuranceCardController.getOne(cardNumber);
        if (card == null) {
            System.out.println("Insurance card not found with the given number.");
            return;
        }
        Customer cardHolder = getCustomerById(card.getCardHolder().getId());
        List<Customer> cardHolderList = Collections.singletonList(cardHolder);


        // Following this, print the details of the insurance card
        System.out.println("\033[1m===== INSURANCE CARD DETAIL =====\033[0m");
        System.out.println("Card Number: " + card.getCardNumber());
        System.out.println("Card Holder: " + card.getCardHolder().getId());
        System.out.println("Policy Owner: " + card.getPolicyOwner());
        System.out.println("Expiration Date: " + new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate()));

        System.out.println("\033[1m===== CUSTOMER DETAIL OF " + cardHolder.getFullName().toUpperCase() + " =====\033[0m");
        printACustomerInfo(cardHolderList);
    }

    private void detailCustomer() {
        System.out.print("Enter customer ID(c-xxxxxxx): ");
        String customerId = scanner.nextLine();
        Customer customer = customerController.getOne(customerId); // Ensure `customerController` is the correct instance variable name
        if (customer == null) {
            System.out.println("Customer not found with the given ID.");
            return;
        }
        System.out.println("\033[1m===== CUSTOMER DETAIL =====\033[0m");
        System.out.println("ID: " + customer.getId());
        System.out.println("Full Name: " + customer.getFullName());
        System.out.println("Title: " + (customer instanceof PolicyHolder ? "Policy Holder" : "Dependent"));
        System.out.println("Insurance Card: " + (customer.getInsuranceCard() != null ? customer.getInsuranceCard().getCardNumber() : "No card"));
        System.out.println("List Of Claims: " + (customer.getClaims().isEmpty() ? "no claim yet" : customer.getClaims().stream()
                .map(Claim::getId)
                .collect(Collectors.joining(", "))));
        System.out.println("List Of Dependents: " + (customer instanceof PolicyHolder ? ((PolicyHolder) customer).getDependents().stream()
                .map(Dependent::getId)
                .collect(Collectors.joining(", ")) : "N/A"));

        printACardInfo(customer.getInsuranceCard(), customer.getFullName());

        // Check if customer has claims and print details
        if (customer.getClaims().isEmpty()) {
            System.out.println("===== " +  customer.getFullName().toUpperCase() +" HAVE NO CLAIM YET"+ " =====");
        } else {
            printAClaimInfo(customer.getClaims(),customer.getFullName() ); // Pass the entire list of claims
        }

        // If customer is a PolicyHolder, print dependents information
        if (customer instanceof PolicyHolder) {
            if (customer instanceof PolicyHolder) {
                PolicyHolder policyHolder = (PolicyHolder) customer;
                printADependentInfo(policyHolder.getDependents(), customer.getFullName());
            }
        }
    }

    private void printACardInfo(InsuranceCard card, String customerName) {
        // Column headers
        String[] headers = {
                "Card Number", "Card Holder", "Policy Owner", "Expiration Date"
        };

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Update column widths based on the data
        maxLengths[0] = Math.max(maxLengths[0], card.getCardNumber().length());
        maxLengths[1] = Math.max(maxLengths[1], card.getCardHolder().getId().length());
        maxLengths[2] = Math.max(maxLengths[2], card.getPolicyOwner().length());
        maxLengths[3] = Math.max(maxLengths[3], dateFormat.format(card.getExpirationDate()).length());

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the table header
        System.out.println("\033[1m===== INSURANCE CARD DETAIL OF " + customerName.toUpperCase() + " =====\033[0m");
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data row for the single card
        System.out.printf(headerFormat,
                card.getCardNumber(),
                card.getCardHolder().getId(),
                card.getPolicyOwner(),
                dateFormat.format(card.getExpirationDate())
        );
    }

    public void printCardsInfo(List<InsuranceCard> cards, boolean isPreview) {
        // Column headers
        String[] headers = {
                "Card Number", "Card Holder", "Policy Owner", "Expiration Date"
        };

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Update column widths based on the data
        for (InsuranceCard card : cards) {
            maxLengths[0] = Math.max(maxLengths[0], card.getCardNumber().length());
            maxLengths[1] = Math.max(maxLengths[1], card.getCardHolder().getId().length());
            maxLengths[2] = Math.max(maxLengths[2], card.getPolicyOwner().length());
            maxLengths[3] = Math.max(maxLengths[3], dateFormat.format(card.getExpirationDate()).length());
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the table header
        if (isPreview) {
            System.out.println("\033[1m====== PREVIEW INSURANCE CARD LIST =====\033[0m");
        } else {
            System.out.println("\033[1m====== CARD LIST =====\033[0m");
        }
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (InsuranceCard card : cards) {
            System.out.printf(headerFormat,
                    card.getCardNumber(),
                    card.getCardHolder().getId(),
                    card.getPolicyOwner(),
                    dateFormat.format(card.getExpirationDate())
            );
        }
    }

    public void printADependentInfo(List<Dependent> dependents, String customerName) {
    if (dependents == null || dependents.isEmpty()) {
        System.out.println("List Of Dependents: no dependents yet");
        return;
    }

    // Column headers for dependents
    String[] headers = {
            "Dependent ID", "Full Name", "Insurance Card", "List Of Claims"
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
        String claimsString = dependent.getClaims().isEmpty() ? "no claim yet" : dependent.getClaims().stream()
                .map(Claim::getId)
                .collect(Collectors.joining(", "));
        maxLengths[3] = Math.max(maxLengths[3], claimsString.length());
    }

    // Create the header format with appropriate spacing
    String headerFormat = createHeaderFormat(maxLengths);

    // Print the table header for dependents
        System.out.println("\033[1m===== DEPENDENT DETAIL OF " + customerName.toUpperCase() + " =====\033[0m");
    System.out.printf(headerFormat, (Object[]) headers);

    // Print each dependent's detail
        for (Dependent dependent : dependents) {
            System.out.printf(headerFormat,
                    dependent.getId(),
                    dependent.getFullName(),
                    getInsuranceCardById(dependent.getId()),
                    dependent.getClaims().isEmpty() ? "no claim yet" : dependent.getClaims().stream()
                            .map(Claim::getId)
                            .collect(Collectors.joining(", "))
            );
        }
    }

    public String getInsuranceCardById(String id) {
        for (InsuranceCard card : customerController.getListOfInsuranceCards()) {
            if (card.getCardHolder().getId().equals(id.trim())) {
                return card.getCardNumber();
            }
        }
        return null;
    }

    public void printAClaimInfo(List<Claim> claims, String customerName) {
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

    // Calculate the maximum width for each column
    int[] maxLengths = calculateColumnWidths(headers, claims, decimalFormat);

    // Create the header format with appropriate spacing
    String headerFormat = createHeaderFormat(maxLengths);

    // Print the table header
        System.out.println("\033[1m===== CLAIM DETAIL OF " + customerName.toUpperCase() + " =====\033[0m");
        System.out.printf(headerFormat, (Object[]) headers);

    // Print each claim's detail
    for (Claim claim : claims) {
        printSingleClaimDetails(claim, headerFormat, decimalFormat); // print details for each claim
    }
}

    private int[] calculateColumnWidths(String[] headers, List<Claim> claims, DecimalFormat decimalFormat) {
        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        // Update column widths based on the claim data
        for (Claim claim : claims) {
            maxLengths[0] = Math.max(maxLengths[0], claim.getId().length());
            maxLengths[1] = Math.max(maxLengths[1], formatDate(claim.getClaimDate()).length());
            maxLengths[2] = Math.max(maxLengths[2], claim.getInsuredPerson().getFullName().length());
            maxLengths[3] = Math.max(maxLengths[3], claim.getCardNumber().getCardNumber().length());
            maxLengths[4] = Math.max(maxLengths[4], formatDate(claim.getExamDate()).length());
            maxLengths[5] = Math.max(maxLengths[5], String.join(", ", claim.getDocuments()).length());
            maxLengths[6] = Math.max(maxLengths[6], decimalFormat.format(claim.getClaimAmount()).length());
            maxLengths[7] = Math.max(maxLengths[7], claim.getStatus().length());
            maxLengths[8] = Math.max(maxLengths[8], claim.getReceiverBankingInfo().printInfor().length());
        }
        return maxLengths;
    }

    private void printSingleClaimDetails(Claim claim, String headerFormat, DecimalFormat decimalFormat) {
        // Print the claim data, same as before
        String formattedAmount = decimalFormat.format(claim.getClaimAmount());
        System.out.printf(headerFormat,
                claim.getId(),
                formatDate(claim.getClaimDate()),
                claim.getInsuredPerson().getFullName(),
                claim.getCardNumber().getCardNumber(),
                formatDate(claim.getExamDate()),
                String.join(", ", claim.getDocuments()),
                formattedAmount,
                claim.getStatus(),
                claim.getReceiverBankingInfo().printInfor());
    }

    private String createHeaderFormat(int[] maxLengths) {
        // Same as before
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";
        return headerFormat;
    }

    public void printClaimsInfo(List<Claim> claims, boolean isPreview) {
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
            String insuredPersonName = claim.getInsuredPerson() != null ? claim.getInsuredPerson().getFullName() : "no data";
            String cardNumber = claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data";
            maxLengths[2] = Math.max(maxLengths[2], insuredPersonName.length());
            maxLengths[3] = Math.max(maxLengths[3], cardNumber.length());
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
            System.out.println("\033[1m====== PREVIEW THE CLAIM LIST =====\033[0m");
        } else {
            System.out.println("\033[1m====== CLAIM LIST =====\033[0m");
        }
        // Print the headers
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (Claim claim : claims) {
            String formattedAmount = decimalFormat.format(claim.getClaimAmount());
            String insuredPersonName = claim.getInsuredPerson() != null ? claim.getInsuredPerson().getFullName() : "no data";
            String cardNumber = claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data";


            System.out.printf(headerFormat,
                    claim.getId(),
                    formatDate(claim.getClaimDate()),
                    insuredPersonName,
                    cardNumber,
                    formatDate(claim.getExamDate()),
                    String.join(", ", claim.getDocuments()),
                    formattedAmount, // Use the DecimalFormat to format amount
                    claim.getStatus(),
                    claim.getReceiverBankingInfo().printInfor());
        }
    }

    public Customer getCustomerById(String id) {
        for (Customer customer : customerController.getListOfCustomers()) {
            if (customer.getId().equals(id.trim())) {
                return customer;
            }
        }
        return null;
    }

    public void printACustomerInfo(List<Customer> customers) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);

        // Determine if we have any PolicyHolders to decide on including the dependents header
        boolean includeDependentsHeader = customers.stream().anyMatch(c -> c instanceof PolicyHolder);

        // Prepare headers based on customer type
        List<String> headersList = new ArrayList<>(Arrays.asList(
                "ID", "Full Name", "Title", "List Of Claims"
        ));
        if (includeDependentsHeader) {
            headersList.add("List Of Dependents");
        }
        String[] headers = headersList.toArray(new String[0]);

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        // Update column widths based on the data
        for (Customer customer : customers) {
            maxLengths[0] = Math.max(maxLengths[0], customer.getId().length());
            maxLengths[1] = Math.max(maxLengths[1], customer.getFullName().length());
            String claimsString = customer.getClaims().isEmpty() ? "no claim yet" : customer.getClaims().stream()
                    .map(Claim::getId)
                    .collect(Collectors.joining(", "));
            maxLengths[2] = Math.max(maxLengths[2], (customer instanceof PolicyHolder ? "Policy Holder" : "Dependent").length());
            maxLengths[3] = Math.max(maxLengths[3], claimsString.length());
            if (customer instanceof PolicyHolder && includeDependentsHeader) {
                PolicyHolder policyHolder = (PolicyHolder) customer;
                String dependentsString = policyHolder.getDependents().isEmpty() ? "no dependent yet" : policyHolder.getDependents().stream()
                        .map(Dependent::getId)
                        .collect(Collectors.joining(", "));
                maxLengths[4] = Math.max(maxLengths[4], dependentsString.length());
            }
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the headers
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (Customer customer : customers) {
            String claimIds = customer.getClaims().isEmpty() ? "no claim yet" : String.join(", ", customer.getClaims().stream()
                    .map(Claim::getId)
                    .collect(Collectors.toList()));
            String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";
            List<Object> dataRow = new ArrayList<>(Arrays.asList(
                    customer.getId(),
                    customer.getFullName(),
                    title,
                    claimIds
            ));
            if (customer instanceof PolicyHolder && includeDependentsHeader) {
                PolicyHolder policyHolder = (PolicyHolder) customer;
                String dependentsText = policyHolder.getDependents().isEmpty() ? "no dependent yet" : String.join(", ", policyHolder.getDependents().stream()
                        .map(Customer::getId)
                        .collect(Collectors.toList()));
                dataRow.add(dependentsText);
            }
            System.out.printf(headerFormat, dataRow.toArray());
        }
    }

    public void printCustomersInfo(List<Customer> customers, boolean isPreview) {
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
//            } else {
//                // Account for the length of the string "he/she is a dependent"
//                maxLengths[4] = Math.max(maxLengths[4], "he/she is a dependent".length());
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
            System.out.println("\033[1m====== PREVIEW THE CUSTOMER LIST =====\033[0m");
        } else {
            System.out.println("\033[1m====== CUSTOMER LIST =====\033[0m");
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
