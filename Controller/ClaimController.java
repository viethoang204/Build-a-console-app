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

    public Customer getCustomerById(String id) {
        for (Customer customer : listOfCustomers) { // Assuming listOfCustomers is a list of Customer objects
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null; // Return null if no customer with the given ID is found
    }

    public InsuranceCard getInsuranceCardByNumber(String cardNumber) {
        for (InsuranceCard card : listOfInsuranceCards) { // Assuming listOfInsuranceCards is a list of InsuranceCard objects
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null; // Return null if no card with the given number is found
    }
    public void loadCustomsFromFile() {
    try {
        ArrayList<Customer> customer = new ArrayList<Customer>();
        Scanner fileScanner = new Scanner(new File("dataFile/customers.txt"));
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
            String ID = stringTokenizer.nextToken();
            String fullName = stringTokenizer.nextToken();
            String insuranceCard = stringTokenizer.nextToken();
            String listOfClaims = stringTokenizer.nextToken();
            String listOfDependents = stringTokenizer.nextToken();

            // Convert insuranceCard from String to InsuranceCard
            InsuranceCard insuranceCardObj = getInsuranceCardByNumber(insuranceCard);

            // Convert listOfClaims and listOfDependents from String to List<String>
            List<String> listOfClaimsList = Arrays.asList(listOfClaims.split(";"));
            List<String> listOfDependentsList = Arrays.asList(listOfDependents.split(";"));

            customer.add(new Customer(ID,
                    fullName,
                    insuranceCardObj,
                    listOfClaimsList,
                    listOfDependentsList));
        }
        this.listOfCustomers = customer;
        System.out.println("Customers loaded from dataFile/customers.txt");
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
//        for (Claim claim1 : listOfClaims) {
//            System.out.println(claim1);
//        }

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

//    public void saveClaimsToFile() {
//        File file = new File("dataFile/claims.txt");
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//            for (Claim claim : listOfClaims) {
//                writer.write(claim.toFileString());
//                writer.newLine();
//            }
//            System.out.println("Claims have been saved to " + file.getPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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