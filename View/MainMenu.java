/**
 * @author <Duong Viet Hoang - S3962514>
 */

package View;

import Controller.ClaimController;
import Controller.CustomerController;
import Controller.InsuranceCardController;
import Model.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MainMenu {
    private final ClaimMenu claimMenu;
    private final InsuranceCardMenu insuranceCardMenu;
    private final CustomerMenu customerMenu;
    private final Scanner scanner = new Scanner(System.in);
    private final ClaimController claimController = ClaimController.getInstance();
    private final CustomerController customerController = CustomerController.getInstance();
    private final InsuranceCardController insuranceCardController = InsuranceCardController.getInstance();

    public MainMenu() {
        claimMenu = ClaimMenu.getInstance();
        customerMenu = CustomerMenu.getInstance();
        insuranceCardMenu = InsuranceCardMenu.getInstance();
        claimMenu.setMenu(this);
        customerMenu.setMenu(this);
        insuranceCardMenu.setMenu(this);
    }

    public void view(){
        System.out.print("\n");
        int choice = 0;
        do {
            System.out.println("\033[1m============================ HOME MENU =============================\033[0m");
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
                case 1: claimMenu.claimMenu(); break;
                case 2: customerMenu.customerMenu(); break;
                case 3: insuranceCardMenu.cardMenu(); break;
            }
        } while (choice != 4);
    }

    private boolean cardExists(String cardnumber) {
        return InsuranceCardController.getInstance().getListOfInsuranceCards().stream()
                .anyMatch(card -> card.getCardNumber().equals(cardnumber));
    }

    private String generateRandomCardNumber() {
        return new Random().ints(10, 0, 10)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
    }

    public void addCustomerAndCard() {
        try {
            System.out.println("\033[1m=================== ADD CUSTOMER AND INSURANCE CARD =================\033[0m");
            this.printCustomersAndCardsInfo(claimController.getListOfCustomers(), claimController.getListOfInsuranceCards(), true);
            System.out.print("\n");
            System.out.println("—————————— FILL IN THE INFORMATION OF THE CUSTOMER'S CARD ———————————");

            List<Customer> customers = customerController.getAll();

            String customerType = "";

            if (customers.isEmpty()) {
                System.out.println("No customers in the system. You need to add a Policy Holder first.");
                customerType = "P";
            } else {
                System.out.println("*** Noted: If you are creating a dependent, please ensure that his/her policy holder is already in the system. ***");
                System.out.println("*** If the policy holder is not in the system, please add the policy holder first ***");
                while (!customerType.equals("D") && !customerType.equals("P")) {
                    System.out.print("Is the customer a Dependent or a Policy Holder? (Enter d for Dependent, p for Policy Holder): ");
                    customerType = scanner.nextLine().trim().toUpperCase();
                    if (!customerType.equals("D") && !customerType.equals("P")) {
                        System.out.println("Invalid input. Please enter d for Dependent or p for Policy Holder.");
                    }
                }
            }

            String fullname = "";
            while (fullname.isEmpty()) {
                System.out.print("Enter full name: ");
                fullname = scanner.nextLine();
            }

            if (customerType.equals("P")) {
                System.out.print("\n");
                System.out.println("——————————————— FILL IN THE INFORMATION OF THE CARD —————————————————");
                String cardnumber = null;
                System.out.println("Choose an option:");
                System.out.println("1. Manually input card number");
                System.out.println("2. Generate card number randomly");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        while (cardnumber == null || cardnumber.isEmpty()|| cardExists(cardnumber)) {
                            System.out.print("Enter card number (10 digits): ");
                            cardnumber = scanner.nextLine();
                            if (cardnumber.isEmpty()) {
                                System.out.println("Card number cannot be empty. Please try again.");
                            } else if (cardnumber.length() < 10) {
                                System.out.println("Card number must be at least 10 digits. Please try again.");
                                cardnumber = null; // Reset cardnumber to null to continue the loop
                            } else if (cardExists(cardnumber)) {
                                System.out.println("Card number already exists. Please try again.");
                                cardnumber = null; // Reset cardnumber to null to continue the loop
                            }
                        }
                        break;
                    case 2:
                        do {
                            cardnumber = generateRandomCardNumber();
                        } while (cardExists(cardnumber));
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }

                String policyowner = null;
                while (policyowner == null || policyowner.isEmpty()) {
                    System.out.print("Enter policy owner: ");
                    policyowner = scanner.nextLine();
                    if (policyowner.isEmpty()) {
                        System.out.println("Policy owner cannot be empty. Please try again.");
                    }
                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date expirationdate = null;
                while (expirationdate == null) {
                    System.out.print("Enter expiration date (dd-MM-yyyy): ");
                    String dateInput = scanner.nextLine();
                    try {
                        expirationdate = formatter.parse(dateInput);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please use dd-MM-yyyy.");
                    }
                }

                List<Dependent> listofdependents = new ArrayList<>();

                // Create an InsuranceCard object
                InsuranceCard insuranceCard = insuranceCardController.add(cardnumber, null, policyowner, expirationdate);

                // Pass the InsuranceCard object when creating the PolicyHolder
                PolicyHolder policyHolder = customerController.addPolicyHolder(fullname, insuranceCard, new ArrayList<>(), listofdependents);
                insuranceCard.setCardHolder(policyHolder);
                insuranceCardController.writeInsuranceCardToFile();

                System.out.println("Customer " + fullname + " and insurance card " + cardnumber + " added successfully.");

            } else if (customerType.equals("D")) {
                this.printPolicyHolders(customerController.getAll(), true);
                System.out.print("Enter the ID of the policy holder for this dependent (c-xxxxxxx): ");
                String policyHolderId = scanner.nextLine();
                PolicyHolder policyHolder = (PolicyHolder) customerController.getOne(policyHolderId);
                if (policyHolder == null) {
                    System.out.println("Policy holder not found with the given ID.");
                    return;
                }
                System.out.print("\n");
                System.out.println("——————————————— FILL IN THE INFORMATION OF THE CARD —————————————————");
                String cardnumber = null;
                System.out.println("Choose an option:");
                System.out.println("1. Manually input card number");
                System.out.println("2. Randomly generate card number");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        while (cardnumber == null || cardnumber.isEmpty() || cardExists(cardnumber)) {
                            System.out.print("Enter card number (10 digits): ");
                            cardnumber = scanner.nextLine();
                            if (cardnumber.isEmpty()) {
                                System.out.println("Card number cannot be empty. Please try again.");
                            } else if (cardnumber.length() != 10) {
                                System.out.println("Card number must be exactly 10 digits. Please try again.");
                                cardnumber = null; // Reset cardnumber to null to continue the loop
                            } else if (cardExists(cardnumber)) {
                                System.out.println("Card number already exists. Please try again.");
                                cardnumber = null; // Reset cardnumber to null to continue the loop
                            }
                        }
                        break;
                    case 2:
                        do {
                            cardnumber = generateRandomCardNumber();
                        } while (cardExists(cardnumber));
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }

                System.out.println("\033[1mPolicy owner of this insurance card is: \033[0m" + policyHolder.getInsuranceCard().getPolicyOwner());

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date expirationdate = null;
                while (expirationdate == null) {
                    System.out.print("Enter expiration date (dd-MM-yyyy): ");
                    String dateInput = scanner.nextLine();
                    try {
                        expirationdate = formatter.parse(dateInput);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format. Please use dd-MM-yyyy.");
                    }
                }

                String policyowner = policyHolder.getInsuranceCard().getPolicyOwner();
                InsuranceCard insuranceCard = insuranceCardController.add(cardnumber, null, policyowner, expirationdate);

                // Pass the InsuranceCard object when creating the Dependent
                Dependent dependent = customerController.addDependent(fullname, insuranceCard, new ArrayList<>());

                // Add the dependent to the list of dependents of the policyholder
                policyHolder.addDependent(dependent);
                customerController.writeCustomersToFile();

                insuranceCard.setCardHolder(dependent);
                insuranceCardController.writeInsuranceCardToFile();

                System.out.println("Customer " + fullname + " and insurance card " + cardnumber + " added successfully.");
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("An error occurred. Please try again.");
        }

    }

    public void printACardInfo(InsuranceCard card, String customerName) {
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
        maxLengths[1] = Math.max(maxLengths[1], card.getCardHolder().getFullName().length());
        maxLengths[2] = Math.max(maxLengths[2], card.getPolicyOwner().length());
        maxLengths[3] = Math.max(maxLengths[3], dateFormat.format(card.getExpirationDate()).length());

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        System.out.print("\n");
        System.out.println("—————— INSURANCE CARD DETAIL OF " + customerName.toUpperCase() + " ——————");
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data row for the single card
        System.out.printf(headerFormat,
                card.getCardNumber(),
                card.getCardHolder().getFullName(),
                card.getPolicyOwner(),
                dateFormat.format(card.getExpirationDate())
        );
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
            maxLengths[1] = Math.max(maxLengths[1], claimController.formatDate(claim.getClaimDate()).length());
            String insuredPersonName = claim.getInsuredPerson() != null ? claim.getInsuredPerson().getFullName() : "no data";
            String cardNumber = claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data";
            maxLengths[2] = Math.max(maxLengths[2], insuredPersonName.length());
            maxLengths[3] = Math.max(maxLengths[3], cardNumber.length());
            maxLengths[4] = Math.max(maxLengths[4], claimController.formatDate(claim.getExamDate()).length());
            maxLengths[5] = Math.max(maxLengths[5], String.join(", ", claim.getDocuments()).length());
            maxLengths[6] = Math.max(maxLengths[6], Double.toString(claim.getClaimAmount()).length());
            maxLengths[7] = Math.max(maxLengths[7], claim.getStatus().length());
            maxLengths[8] = Math.max(maxLengths[8], claim.getReceiverBankingInfo().printInfor().length());
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the table
        if (isPreview) {
            System.out.println("—————————————————————— PREVIEW THE CLAIM LIST ———————————————————————");
        } else {
            System.out.println("\033[1m============================ VIEW CLAIM ==============================\033[0m");
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
                    claimController.formatDate(claim.getClaimDate()),
                    insuredPersonName,
                    cardNumber,
                    claimController.formatDate(claim.getExamDate()),
                    String.join(", ", claim.getDocuments()),
                    formattedAmount,
                    claim.getStatus(),
                    claim.getReceiverBankingInfo().printInfor());
        }
        if (isPreview) {
            System.out.println("—————————————————————————————————————————————————————————————————————");
        } else {
            System.out.println("\033[1m======================================================================\033[0m");
        }
    }

    public void printACustomerInfo(List<Customer> customers) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.'); // Ensure the decimal separator is dot and not comma
        DecimalFormat decimalFormat = new DecimalFormat("0.#", symbols);
        decimalFormat.setMaximumFractionDigits(2);

        // Prepare headers based on customer type
        List<String> headersList = new ArrayList<>(Arrays.asList(
                "ID", "Full Name", "Title"
        ));
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
            maxLengths[2] = Math.max(maxLengths[2], (customer instanceof PolicyHolder ? "Policy Holder" : "Dependent").length());
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
            String title = customer instanceof PolicyHolder ? "Policy Holder" : "Dependent";
            List<Object> dataRow = new ArrayList<>(Arrays.asList(
                    customer.getId(),
                    customer.getFullName(),
                    title
            ));
            System.out.printf(headerFormat, dataRow.toArray());
        }
    }

    public void printPolicyHolders(List<Customer> customers, boolean isPreview) {
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
            if (customer instanceof PolicyHolder) {
                maxLengths[0] = Math.max(maxLengths[0], customer.getId().length());
                maxLengths[1] = Math.max(maxLengths[1], customer.getFullName().length());
                maxLengths[2] = Math.max(maxLengths[2], customer.getInsuranceCard().getCardNumber().length());
                String title = "Policy Holder";
                maxLengths[3] = Math.max(maxLengths[3], title.length());
            }
        }

        // Create the header format with appropriate spacing
        String headerFormat = "";
        for (int width : maxLengths) {
            headerFormat += " %-"+ (width + 2) +"s|";
        }
        headerFormat += "%n";

        // Print the table
        if (isPreview) {
            System.out.println("————————————————————— PREVIEW POLICY HOLDER LIST —————————————————————");
        } else {
            System.out.println("\033[1m====== POLICY HOLDER LIST =====\033[0m");
        }
        // Print the headers
        System.out.printf(headerFormat, (Object[]) headers);

        // Print the data rows
        for (Customer customer : customers) {
            if (customer instanceof PolicyHolder) {
                String title = "Policy Holder";
                System.out.printf(headerFormat,
                        customer.getId(),
                        customer.getFullName(),
                        customer.getInsuranceCard().getCardNumber(),
                        title
                );
            }
        }
        if (isPreview) {
            System.out.println("——————————————————————————————————————————————————————————————————————");
        } else {
            System.out.println("\033[1m===============================\033[0m");
        }
    }

    public void printCustomersAndCardsInfo(List<Customer> customers, List<InsuranceCard> cards, boolean isPreview) {
        if (customers.size() != cards.size()) {
            System.out.println("The lists do not match in size, unable to print in a 1-1 relationship.");
            return;
        }

        // Assuming the customers and cards are in the same order and correspond to each other.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Initialize maximum lengths
        int maxCustomerIdLength = "Customer ID".length();
        int maxFullNameLength = "Customer Full Name".length();
        int maxCardNumberLength = "Card Number".length();
        int maxTitleLength = "Title".length();
        int maxPolicyOwnerLength = "Policy Owner".length();
        int maxExpirationDateLength = "Expiration Date".length();

        // Find maximum lengths
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            InsuranceCard card = cards.get(i);

            maxCustomerIdLength = Math.max(maxCustomerIdLength, customer.getId().length());
            maxFullNameLength = Math.max(maxFullNameLength, customer.getFullName().length());
            maxCardNumberLength = Math.max(maxCardNumberLength, customer.getInsuranceCard().getCardNumber().length());
            maxTitleLength = Math.max(maxTitleLength, (customer instanceof PolicyHolder) ? "Policy Holder".length() : "Dependent".length());
            maxPolicyOwnerLength = Math.max(maxPolicyOwnerLength, card.getPolicyOwner().length());
            maxExpirationDateLength = Math.max(maxExpirationDateLength, dateFormat.format(card.getExpirationDate()).length());
        }

        // Build the format strings dynamically
        String format = String.format("%%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds%%n",
                maxCustomerIdLength, maxFullNameLength, maxCardNumberLength, maxTitleLength,
                maxPolicyOwnerLength, maxExpirationDateLength);

        // Print table header
        if (isPreview) {
            System.out.println("—————————————— PREVIEW CUSTOMER AND INSURANCE CARD INFO —————————————");
        } else {
            System.out.println("\033[1m====== CUSTOMER AND INSURANCE CARD INFO =====\033[0m");
        }

        // Print headers
        System.out.printf(format, "Customer ID", "Customer Full Name", "Card Number", "Title", "Policy Owner", "Expiration Date");

        // Print each row
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            InsuranceCard card = cards.get(i);

            System.out.printf(format,
                    customer.getId(),
                    customer.getFullName(),
                    customer.getInsuranceCard().getCardNumber(),
                    (customer instanceof PolicyHolder) ? "Policy Holder" : "Dependent",
                    card.getPolicyOwner(),
                    dateFormat.format(card.getExpirationDate()));
        }
        if (isPreview) {
            System.out.println("—————————————————————————————————————————————————————————————————————");
        } else {
            System.out.println("\033[1m=============================================\033[0m");
        }
    }
}
