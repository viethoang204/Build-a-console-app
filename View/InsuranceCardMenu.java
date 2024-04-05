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
import java.text.SimpleDateFormat;
import java.util.*;

public class InsuranceCardMenu {
    private MainMenu mainMenu;
    private static InsuranceCardMenu instance;

    private final InsuranceCardController insuranceCardController = InsuranceCardController.getInstance();

    private final CustomerController customerController = CustomerController.getInstance();

    private final ClaimController claimController = ClaimController.getInstance();

    private final Scanner scanner = new Scanner(System.in);

    public static InsuranceCardMenu getInstance() {
        if (instance == null) {
            instance = new InsuranceCardMenu();
        }
        return instance;
    }

    public void setMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void cardMenu() {
        System.out.print("\n");
        int choice = 0;
        do {
            System.out.println("\033[1m=========================== CARD MANAGER ===========================\033[0m");
            System.out.println("1. View All Insurance Card");
            System.out.println("2. Add Insurance Card");
            System.out.println("3. Remove Insurance Card");
            System.out.println("4. Edit Insurance Card");
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
                        this.printCardsInfo(insuranceCardController.getListOfInsuranceCards(), false);
                        System.out.println("1. View Detail Of A Insurance Card");
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
                        switch (choice) {
                            case 1:
                                detailInsuranceCard();
                                break;
                            case 2:
                                sortingCard();
                                break;
                            case 3:
                                System.out.println("Returning...");
                                break;
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
                        System.out.println("\033[1m======================= REMOVE INSUREANCE CARD ======================\033[0m");
                        this.printCardsInfo(insuranceCardController.getAll(), true);
                        System.out.println("*** Note: Deleting a card also removes their owner information ***");
                        System.out.println("*** Card Information will be erased from all related claims for management ***");
                        System.out.print("Enter customer ID to remove (10 digits): ");
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
                                if (customerController.deleteCustomerDPD(customerId)) {
                                    System.out.println("Removed card " + id + " from the system and all related dependents'card.");
                                } else {
                                    System.out.println("Invalid ID, please try again");
                                }
                            } else if (customer instanceof PolicyHolder) {
                                System.out.println("*** Note: This is policyholder's card, Deleting this card will also remove all of their dependents' cards ***");

                                // Prompt for confirmation with loop for validation
                                String confirmation;
                                boolean validInput = false;
                                do {
                                    System.out.print("Are you sure you want to delete the policyholder's card and all of their dependents' cards? (y/n): ");
                                    confirmation = scanner.nextLine().toLowerCase(); // Convert input to lowercase
                                    if (confirmation.equals("y") || confirmation.equals("n")) {
                                        validInput = true;
                                    } else {
                                        System.out.println("Invalid input. Please enter 'y' or 'n'.");
                                    }
                                } while (!validInput);

                                if (confirmation.equals("y")) {
                                    if (customerController.deleteCustomerPLC(customerId)) {
                                        System.out.println("Removed card " + id + " from the system");
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
                        System.out.println("\033[1m========================= EDIT INSURANCE CARD ========================\033[0m");
                        this.printCardsInfo(insuranceCardController.getAll(), true);

                        System.out.print("Enter card ID to edit (10 digits): ");
                        String id = scanner.nextLine();
                        InsuranceCard insuranceCard = insuranceCardController.getOne(id);
                        if (insuranceCard == null) {
                            System.out.println("Card not found with the given ID.");
                            cardMenu();
                        }

                        // Check if the card belongs to a PolicyHolder or a Dependent
                        Customer cardHolder = insuranceCard.getCardHolder();
                        if (cardHolder instanceof PolicyHolder) {
                            System.out.print("Enter new policy owner name or press Enter to skip: ");
                            String newPolicyOwner = scanner.nextLine();
                            if (!newPolicyOwner.isEmpty()) {
                                insuranceCard.setPolicyOwner(newPolicyOwner);

                                List<Dependent> dependents = null;
                                for (Customer customer : customerController.getListOfCustomers()) {
                                    if (customer instanceof PolicyHolder && customer.getInsuranceCard().getCardNumber().equals(insuranceCard.getCardNumber())) {
                                        dependents = ((PolicyHolder) customer).getDependents();
                                    }
                                }
                                // Update the policy owner for all dependents of the policyholder
                                for (Dependent dependent : dependents) {
                                    for (InsuranceCard insuranceCard1 : insuranceCardController.getListOfInsuranceCards()) {
                                        if (insuranceCard1.getCardHolder().getId().equals(dependent.getId())) {
                                            insuranceCard1.setPolicyOwner(newPolicyOwner);
                                        }
                                    }
                                }
                                System.out.println("Updated policy owner of all dependents belong to this policy holder");
                            }
                        } else if (cardHolder instanceof Dependent) {
                            System.out.println("Dependent is not allowed to edit policy owner");
                        }

                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        Date claimdate = null;
                        while (claimdate == null) {
                            System.out.print("Enter expiration date (dd-MM-yyyy) or press Enter to skip: ");
                            String claimDateInput = scanner.nextLine();
                            if (!claimDateInput.isEmpty()) {
                                try {
                                    claimdate = formatter.parse(claimDateInput);
                                    insuranceCard.setExpirationDate(claimdate);

                                    // Update the expiration date in the claim list
                                    for (Claim claim : claimController.getListOfClaims()) {
                                        if (claim.getCardNumber().getCardNumber().equals(insuranceCard.getCardNumber())) {
                                            claim.getCardNumber().setExpirationDate(claimdate);
                                        }
                                    }

                                    // Update the expiration date in the customer list
                                    for (Customer customer : customerController.getListOfCustomers()) {
                                        if (customer.getInsuranceCard().getCardNumber().equals(insuranceCard.getCardNumber())) {
                                            customer.getInsuranceCard().setExpirationDate(claimdate);
                                        }
                                    }

                                } catch (Exception e) {
                                    System.out.println("Invalid date format. Please try again.");
                                }
                            } else {
                                break;
                            }
                        }

                        claimController.writeInsuranceCardoFile();
                        System.out.println("Insurance card updated");
                    } catch (Exception e) {
                        System.out.println("An error occurred. Please try again");
                    }
                    System.out.print("\n");
                    break;
                case 5:
                    System.out.print("\n");
                    System.out.println("\033[1m======================= SAVING INSURANCE CARD =======================\033[0m");
                    this.printCardsInfo(insuranceCardController.getAll(), true);
                    do {
                        System.out.println("The insurance card table is currently sorted by the " + insuranceCardController.currentCardOrder + " order");
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
                            case 1: sortingCard(); break;
                            case 2: savingCardMenu();break;
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
        } while (choice != 6);
    }

    private void savingCardMenu() {
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
                    saveCardListAsTable(claimController.getListOfInsuranceCards(), true);
                    cardMenu();
                    break;
                case 2:
                    saveCardListAsTsv(claimController.getListOfInsuranceCards(), true);
                    cardMenu();
                    break;
                case 3:
                    saveCardListAsCsv(claimController.getListOfInsuranceCards(), true);
                    cardMenu();
                    break;
                case 4:
                    System.out.println("Returning...");
                    cardMenu();
            }
        } while (choice != 4);
    }

    private void sortingCard() {
        System.out.print("\n");
        this.printCardsInfo(insuranceCardController.getAll(), false);
        int choice = 0;
        do {
            System.out.println("SORTING BY:");
            System.out.println("1. Expiration Date: earliest to latest");
            System.out.println("2. Expiration Date: latest to earliest");
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
                    insuranceCardController.sortInsuranceCardByDate(true);
                    this.printCardsInfo(claimController.getListOfInsuranceCards(), false);
                    break;
                case 2:
                    insuranceCardController.sortInsuranceCardByDate(false);
                    this.printCardsInfo(claimController.getListOfInsuranceCards(), false);
                    break;
                case 3:
                    System.out.println("Returning...");
            }
        } while (choice != 3);
    }

    private void detailInsuranceCard() {
        System.out.print("Enter insurance card number (10 digits): ");
        String cardNumber = scanner.nextLine();
        InsuranceCard card = insuranceCardController.getOne(cardNumber);
        if (card == null) {
            System.out.println("Insurance card not found with the given number.");
            return;
        }
        Customer cardHolder = claimController.getCustomerById(card.getCardHolder().getId());
        List<Customer> cardHolderList = Collections.singletonList(cardHolder);


        // Following this, print the details of the insurance card
        System.out.print("\n");
        System.out.println("———————————————————————— INSURANCE CARD DETAIL ——————————————————————");
        System.out.println("Card Number: " + card.getCardNumber());
        System.out.println("Card Holder: " + card.getCardHolder().getFullName());
        System.out.println("Policy Owner: " + card.getPolicyOwner());
        System.out.println("Expiration Date: " + new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate()));

        System.out.print("\n");
        System.out.println("——————— CUSTOMER DETAIL OF " + cardHolder.getFullName().toUpperCase() + " ———————");
        mainMenu.printACustomerInfo(cardHolderList);
        System.out.print("\n");
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
            maxLengths[1] = Math.max(maxLengths[1], card.getCardHolder().getFullName().length());
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
            System.out.println("———————————————————— PREVIEW INSURANCE CARD LIST ————————————————————");
        } else {
            System.out.println("\033[1m============================= VIEW CARD =============================\033[0m");
        }
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (InsuranceCard card : cards) {
            System.out.printf(headerFormat,
                    card.getCardNumber(),
                    card.getCardHolder().getFullName(),
                    card.getPolicyOwner(),
                    dateFormat.format(card.getExpirationDate())
            );
        }
        if (isPreview) {
            System.out.println("—————————————————————————————————————————————————————————————————————");
        } else {
            System.out.println("\033[1m=====================================================================\033[0m");
        }
    }

    public void saveCardListAsTsv(List<InsuranceCard> insuranceCards, boolean saveFile) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        String delimiter = "\t";
        String lineEnd = "\r\n";
        String[] headers = {"Card Number", "Card Holder", "Policy Owner", "Expiration Date"};

        if (saveFile) {
            System.out.print("Enter file name to save as TSV: ");
            Scanner scanner = new Scanner(System.in);
            String fileName = scanner.nextLine();
            String filePath = "savedFile/" + fileName + ".tsv"; // Adjust directory as needed

            try (PrintWriter out = new PrintWriter(filePath)) {
                // Print the headers
                out.println(String.join(delimiter, headers));

                // Print each data row
                for (InsuranceCard card : insuranceCards) {
                    String policyOwner = card.getPolicyOwner() != null ? card.getPolicyOwner() : "N/A";
                    out.print(card.getCardNumber() + delimiter);
                    out.print(card.getCardHolder().getFullName() + delimiter);
                    out.print(policyOwner + delimiter);
                    out.print(new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate()) + lineEnd);
                }
                System.out.println("File saved successfully at " + filePath);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
            }
        } else {
            try (PrintWriter out = new PrintWriter(System.out)) {
                // Print the headers
                out.println(String.join(delimiter, headers));

                // Print each data row for console output
                for (InsuranceCard card : insuranceCards) {
                    String policyOwner = card.getPolicyOwner() != null ? card.getPolicyOwner() : "N/A";
                    out.print(card.getCardNumber() + delimiter);
                    out.print(card.getCardHolder().getFullName() + delimiter);
                    out.print(policyOwner + delimiter);
                    out.print(new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate()) + lineEnd);
                }
            }
        }
    }

    public void saveCardListAsCsv(List<InsuranceCard> insuranceCards, boolean saveFile) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);
        String delimiter = ",";
        String lineEnd = "\r\n";
        String[] headers = {"Card Number", "Card Holder", "Policy Owner", "Expiration Date"};

        if (saveFile) {
            System.out.print("Enter file name to save as CSV: ");
            Scanner scanner = new Scanner(System.in);
            String fileName = scanner.nextLine();
            String filePath = "savedFile/" + fileName + ".csv"; // Adjust directory as needed

            try (PrintWriter out = new PrintWriter(filePath)) {
                // Print the headers
                out.println(String.join(delimiter, headers));

                // Print each data row
                for (InsuranceCard card : insuranceCards) {
                    String policyOwner = card.getPolicyOwner() != null ? card.getPolicyOwner() : "N/A";
                    out.printf("\"%s\"%s\"%s\"%s\"%s\"%s\"%s\"%s",
                            card.getCardNumber(),
                            delimiter,
                            card.getCardHolder().getFullName(),
                            delimiter,
                            policyOwner,
                            delimiter,
                            new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate()),
                            lineEnd);
                }
                System.out.println("File saved successfully at " + filePath);
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
            }
        } else {
            try (PrintWriter out = new PrintWriter(System.out)) {
                // Print the headers for console output
                out.println(String.join(delimiter, headers));

                // Print each data row for console output
                for (InsuranceCard card : insuranceCards) {
                    String policyOwner = card.getPolicyOwner() != null ? card.getPolicyOwner() : "N/A";
                    out.printf("\"%s\"%s\"%s\"%s\"%s\"%s\"%s\"%s",
                            card.getCardNumber(),
                            delimiter,
                            card.getCardHolder().getFullName(),
                            delimiter,
                            policyOwner,
                            delimiter,
                            new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate()),
                            lineEnd);
                }
            }
        }
    }

    public void saveCardListAsTable(List<InsuranceCard> insuranceCards, boolean saveFile) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);

        // Column headers
        String[] headers = {"Card Number","Card Holder","Policy Owner","Expiration Date"};

        // Initialize column widths to header lengths
        int[] maxLengths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            maxLengths[i] = headers[i].length();
        }

        // Update column widths based on the data
        for (InsuranceCard card : insuranceCards) {
            maxLengths[0] = Math.max(maxLengths[0], card.getCardNumber().length());
            maxLengths[1] = Math.max(maxLengths[1], card.getCardHolder().getFullName().length());
            String policyOwner = card.getPolicyOwner() != null ? card.getPolicyOwner() : "N/A";
            maxLengths[2] = Math.max(maxLengths[2], policyOwner.length());
            maxLengths[3] = Math.max(maxLengths[3], new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate()).length());
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
        String title = "CARD LIST";
        int titleWidth = title.length();
        int totalPadding = totalWidth - titleWidth;
        int paddingBefore = totalPadding / 2 + borderWidth / 2; // adding half the border width to the padding
        int paddingAfter = totalPadding - paddingBefore + borderWidth / 2 + 1;

        String titlePaddingBefore = new String(new char[paddingBefore]).replace('\0', '=');
        String titlePaddingAfter = new String(new char[paddingAfter]).replace('\0', '=');

        // Combine parts to create the full title
        String fullTitle = titlePaddingBefore + title + titlePaddingAfter;

        PrintWriter out = null;
        String filePath = null; // Declare filePath here
        if (saveFile) {
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
        } else {
            out = new PrintWriter(System.out);
        }
        out.println(fullTitle);
        out.printf(borderFormat);
        out.printf(headerFormat, (Object[]) headers);
        out.printf(lineFormat);

        for (InsuranceCard card : insuranceCards) {
            String policyOwner = card.getPolicyOwner() != null ? card.getPolicyOwner() : "N/A";
            Object[] rowData = {
                    card.getCardNumber(),
                    card.getCardHolder().getFullName(),
                    policyOwner,
                    new SimpleDateFormat("dd-MM-yyyy").format(card.getExpirationDate())
            };
            out.printf(headerFormat, rowData);
            out.printf(lineFormat);
        }
        if (saveFile) {
            System.out.println("File saved successfully at " + filePath);
        }
        out.close();
    }
}
