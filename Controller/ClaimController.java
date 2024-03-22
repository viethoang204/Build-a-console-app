package Controller;

import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimController implements ClaimProcessManager {

    private static ClaimController instance;
    private ArrayList<Claim> listOfClaims = new ArrayList<Claim>(); // Fixed: Removed duplicate declaration
    private ArrayList<Customer> listOfCustomers; // Declare listOfCustomers
    private ArrayList<InsuranceCard> listOfInsuranceCards; // Declare listOfInsuranceCards

    private ClaimController() {
        this.listOfCustomers = new ArrayList<>(); // Initialize listOfCustomers
        this.listOfInsuranceCards = new ArrayList<>(); // Initialize listOfInsuranceCards

        loadCustomsFromFile();
        loadInsuranceCardsFromFile();
    }

    public static ClaimController getInstance(){
        if (instance == null) {
            instance = new ClaimController();
        }
        return instance;
    }

    @Override
    public void add(Date claimdate, Customer insuredperson, InsuranceCard cardnumber, Date examdate, List<String> listofdocuments, double amount, String status, String receiverbankinginfor) {
        Claim claim = new Claim("c-"+generateUniqueClaimID(),claimdate, insuredperson, cardnumber, examdate, listofdocuments, amount, status, receiverbankinginfor);
        this.listOfClaims.add(claim);
        writeClaimsToFile();
    }

    public String getCustomerNameById(String id) {
        for (Customer customer : listOfCustomers) {
            if (customer.getId().equals(id)) {
                return customer.getFullName().trim(); // Trim to remove leading/trailing spaces
            }
        }
        return "Customer Not Found"; // Or some other default value indicating no match
    }

    public void loadInsuranceCardsFromFile() {
        try {
            ArrayList<InsuranceCard> cards = new ArrayList<>();
            Scanner fileScanner = new Scanner(new File("dataFile/insuranceCards.txt"));
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                String cardNumber = stringTokenizer.nextToken();
                String cardHolder = getCustomerNameById(stringTokenizer.nextToken().trim());
                String policyOwner = stringTokenizer.nextToken();
                String expirationDateStr = stringTokenizer.nextToken();
                Date expirationDate = new SimpleDateFormat("dd-MM-yyyy").parse(expirationDateStr);

                cards.add(new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate));
            }
            this.listOfInsuranceCards = cards;
            for (InsuranceCard card : listOfInsuranceCards) {
                System.out.println(card.toString());
            }
            System.out.println("Insurance cards loaded from dataFile/insuranceCards.txt");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public InsuranceCard getInsuranceCardByNumber(String cardNumber) {
        for (InsuranceCard card : listOfInsuranceCards) { // Assuming listOfInsuranceCards is a list of InsuranceCard objects
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null; // Return null if no card with the given number is found
    }

    public String getCardNumberByCustomerId(String customerId) {
        for (InsuranceCard insurancecard : listOfInsuranceCards) {
            if (insurancecard.getCardNumber().equals(customerId)) {
                return insurancecard.getCardNumber().trim();
            }
        }
        return "Customer Not Found";
    }

    public Claim getClaimById(String id) {
        for (Claim claim : listOfClaims) {
            if (claim.getId().equals(id)) {
                return claim;
            }
        }
        return null;
    }

    public Customer getCustomerById(String id) {
        for (Customer customer : listOfCustomers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public List<String> getCustomerIdsByIds(List<String> ids) {
        List<String> foundCustomerIds = new ArrayList<>();
        for (String id : ids) {
            for (Customer customer : listOfCustomers) {
                if (customer.getId().equals(id)) {
                    foundCustomerIds.add(customer.getId());
                    break;
                }
            }
        }
        return foundCustomerIds;
    }

    public void loadCustomsFromFile() {
        try {
            loadInsuranceCardsFromFile();
            ArrayList<Customer> customers = new ArrayList<Customer>();
            Scanner fileScanner = new Scanner(new File("dataFile/customers.txt"));
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                String ID = stringTokenizer.nextToken();
                String fullName = stringTokenizer.nextToken();
                String insuranceCard = getCardNumberByCustomerId(stringTokenizer.nextToken().trim());
                InsuranceCard insuranceCardObj = getInsuranceCardByNumber(insuranceCard);
                customers.add(new Customer(ID, fullName, insuranceCardObj, new ArrayList<>(), new ArrayList<>()));
            }
            this.listOfCustomers = customers;

            // Reset the scanner to start of the file
            fileScanner = new Scanner(new File("dataFile/customers.txt"));
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                String ID = stringTokenizer.nextToken();
                stringTokenizer.nextToken(); // Skip fullName
                stringTokenizer.nextToken(); // Skip insuranceCard
                String listOfClaims = stringTokenizer.nextToken();
                String listOfDependents = stringTokenizer.nextToken().replace("[", "").replace("]", "").trim();

                List<String> listOfClaimsList = Arrays.asList(listOfClaims.split(";"));
                List<String> listOfDependentsList = Arrays.asList(listOfDependents.split(";"));

                Customer currentCustomer = null;
                for (Customer customer : listOfCustomers) {
                    if (customer.getId().equals(ID)) {
                        currentCustomer = customer;
                        break;
                    }
                }
                currentCustomer.setClaims(listOfClaimsList.stream().map(this::getClaimById).collect(Collectors.toList()));
                currentCustomer.setDependents(!listOfDependentsList.isEmpty() ? getCustomerIdsByIds(listOfDependentsList) : new ArrayList<>());
            }

            for (Customer customer : listOfCustomers) {
                System.out.println(customer.toString());
            }
            System.out.println("Customers loaded from customers.txt");
        } catch (Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    public void loadClaimsFromFile() {
        try {
            ArrayList<Claim> claim = new ArrayList<Claim>();
            Scanner fileScanner = new Scanner(new File("dataFile/claims.txt"));
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                String ID = stringTokenizer.nextToken();
                String claimDate = stringTokenizer.nextToken();
                String insuredPersonId = stringTokenizer.nextToken();
                String cardNumberStr = stringTokenizer.nextToken();
                String examDate = stringTokenizer.nextToken();
                String documents = stringTokenizer.nextToken().replace("[", "").replace("]", "");
                String amount = stringTokenizer.nextToken();
                String status = stringTokenizer.nextToken();
                String receiverBankingInfo = stringTokenizer.nextToken();

                // Split documents by comma and space
                String[] documentsArray = documents.split(", ");

                // Retrieve or create Customer and InsuranceCard objects
                Customer insuredPerson = getCustomerById(insuredPersonId);
                InsuranceCard cardNumber = getInsuranceCardByNumber(cardNumberStr);

                claim.add(new Claim(
                        ID,
                        new SimpleDateFormat("dd-MM-yyyy").parse(claimDate),
                        insuredPerson,
                        cardNumber,
                        new SimpleDateFormat("dd-MM-yyyy").parse(examDate),
                        Arrays.asList(documentsArray),
                        Double.parseDouble(amount),
                        status,
                        receiverBankingInfo));

            }
            this.listOfClaims = claim;
            for (Claim claim1 : listOfClaims) {
                System.out.println(claim1.toString());
            }
            System.out.println("Claims loaded from dataFile/claims.txt");
        } catch (Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private synchronized String generateUniqueClaimID() {
        int maxAssignedNumber = 0;
        for (Claim claim : listOfClaims) {
            String containerID = claim.getId();
            if (containerID.startsWith("c-")) {
                try {
                    int number = Integer.parseInt(containerID.substring(2));
                    maxAssignedNumber = Math.max(maxAssignedNumber, number);
                } catch (NumberFormatException e) {
                }
            }
        }
        return String.valueOf(maxAssignedNumber+1);
    }

    @Override
    public void update(Claim claim) {

    }

    @Override
    public Claim getOne(String claimId) {
        return null;
    }

    @Override
    public List<Claim> getAll() {
        return this.listOfClaims.stream().filter(claim -> claim instanceof Claim).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean delete(String portID){
        if (listOfClaims.removeIf(port -> port.getId().equals(portID))) {
            writeClaimsToFile();
            return true;
        }
        return false;
    }

    public void writeClaimsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("dataFile/claims.txt"))) {
            // Write the CSV header
            writer.println("ID,ClaimDate,InsuredPerson,CardNumber,ExamDate,ListofDocuments,Amount,Status,ReceiverBankingInfor");

            // Write claim records
            for (Claim claim : listOfClaims) {
                String formattedClaimDate = DateUtils.formatDate(claim.getClaimDate()); // Use utility method to format date
                String formattedExamDate = DateUtils.formatDate(claim.getExamDate()); // Use utility method to format date
                String documents = String.join(";", claim.getDocuments());

                writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        claim.getId(),
                        formattedClaimDate, // Use formatted claim date
                        claim.getInsuredPerson().getFullName(), // Assuming getInsuredPerson returns a Customer object with getFullName()
                        claim.getCardNumber().getCardNumber(), // Assuming getCardNumber returns an InsuranceCard object with getCardNumber()
                        formattedExamDate, // Use formatted exam date
                        documents, // Joined list of documents with semicolon
                        claim.getClaimAmount(),
                        claim.getStatus(),
                        claim.getReceiverBankingInfo()
                ));
            }
            System.out.println("Claims have been written to " + "dataFile/claims.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class DateUtils {
        public static String formatDate(Date date) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            return formatter.format(date);
        }
    }

}