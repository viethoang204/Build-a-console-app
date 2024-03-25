package Controller;

import Model.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimController implements ClaimProcessManager {

    private static ClaimController instance;
    private ArrayList<Claim> listOfClaims;
    private ArrayList<Customer> listOfCustomers;
    private ArrayList<InsuranceCard> listOfInsuranceCards;

    private ClaimController() {
        this.listOfClaims = new ArrayList<>();
        this.listOfCustomers = new ArrayList<>();
        this.listOfInsuranceCards = new ArrayList<>();
        loadInsuranceCardsFromFile();
        loadCustomsFromFile();
        loadClaimsFromFile();
    }

    public static ClaimController getInstance(){
        if (instance == null) {
            instance = new ClaimController();
        }
        return instance;
    }

    public InsuranceCard getInsuranceCardById(String cardNumber) {
        for (InsuranceCard card : listOfInsuranceCards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null;
    }

    public Dependent getCustomerDById(String id) {
        for (Customer customer : listOfCustomers) {
            if (customer instanceof Dependent && customer.getId().equals(id.trim())) {
                return (Dependent) customer;
            }
        }
        return null;
    }

    public Customer getCustomerById(String id) {
        for (Customer customer : listOfCustomers) {
            if (customer.getId().equals(id.trim())) {
                return customer;
            }
        }
        return null;
    }

    @Override
    public Claim getOne(String claimId) {
        for (Claim claim : listOfClaims) {
            if (claim.getId().equals(claimId)) {
                return claim;
            }
        }
        return null;
    }

    @Override
    public void add(Date claimdate, Customer insuredperson, InsuranceCard cardnumber, Date examdate, List<String> rawListOfDocuments, double amount, String status, BankingInfo receiverbankinginfor) {
        // Generate a unique claim ID
        String claimId = generateUniqueClaimID();

        // Card number from the insurance card object
        String cardNumber = cardnumber.getCardNumber();

        // Initially format document names
        List<String> initiallyFormattedListOfDocuments = rawListOfDocuments.stream()
                .map(documentName -> claimId + "_" + cardNumber + "_" + documentName.trim() + ".pdf")
                .collect(Collectors.toList());

        // Remove duplicates after formatting
        List<String> formattedListOfDocuments = new ArrayList<>(new LinkedHashSet<>(initiallyFormattedListOfDocuments));

        // Create a new claim with the deduplicated list of document names
        Claim claim = new Claim(claimId, claimdate, insuredperson, cardnumber, examdate, formattedListOfDocuments, amount, status, receiverbankinginfor);
        this.listOfClaims.add(claim);
        writeClaimsToFile();
    }

    @Override
    public void update(Claim claim) {
    }

    @Override
    public List<Claim> getAll() {
        return new ArrayList<>(this.listOfClaims);
    }

    @Override
    public boolean delete(String claimID){
        if (listOfClaims.removeIf(claim -> claim.getId().equals(claimID))) {
            writeClaimsToFile();
            return true;
        }
        return false;
    }

    public void loadCustomsFromFile() {
        loadClaimsFromFile();
        try {
            ArrayList<Customer> customers = new ArrayList<Customer>();
            Scanner fileScanner = new Scanner(new File("dataFile/customers.txt"));
            fileScanner.nextLine(); // Bỏ qua tiêu đề
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(","); // Sử dụng split thay vì StringTokenizer để dễ dàng xác định số phần tử
                String id = parts[0];
                String fullName = parts[1];
                InsuranceCard cardNumber = getInsuranceCardById(parts[2].trim());
                String[] claimsArray = parts[3].substring(1, parts[3].length() - 1).split(";"); // Loại bỏ dấu ngoặc và split

                List<Claim> listOfClaims = new ArrayList<>();
                for (String claimId : claimsArray) {
                    if (!claimId.isEmpty()) {
                        Claim claim = getOne(claimId.trim());
                        if (claim != null) {
                            listOfClaims.add(claim);
                        }
                    }
                }

                if (parts.length == 5) { // Đây là PolicyHolder
                    String[] dependentsArray = parts[4].substring(1, parts[4].length() - 1).split(";");
                    List<Dependent> listOfDependents = new ArrayList<>();
                    for (String dependentId : dependentsArray) {
                        if (!dependentId.isEmpty()) {
                            Dependent dependent = getCustomerDById(dependentId.trim());
                            if (dependent != null) {
                                listOfDependents.add(dependent);
                            }
                        }
                    }
                    // Tạo một bản sao mới của listOfDependents để sử dụng cho PolicyHolder này
                    customers.add(new PolicyHolder(id, fullName, cardNumber, listOfClaims, listOfDependents));
                } else { // Đây là Dependent
                    customers.add(new Dependent(id, fullName, cardNumber, listOfClaims));
                }
            }
            this.listOfCustomers = customers;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
            Customer insuredPerson = getCustomerById(stringTokenizer.nextToken().trim());
            InsuranceCard cardNumber = getInsuranceCardById(stringTokenizer.nextToken().trim());
            String examDate = stringTokenizer.nextToken();
            String documents = stringTokenizer.nextToken().replace("[", "").replace("]", "");
            String amount = stringTokenizer.nextToken();
            String status = stringTokenizer.nextToken();
            String[] bankingInfoParts = stringTokenizer.nextToken().split("-");
            if (bankingInfoParts.length == 3) { // Ensure there are exactly 3 parts
                String bank = bankingInfoParts[0].trim();
                String name = bankingInfoParts[1].trim();
                String number = bankingInfoParts[2].trim();
                BankingInfo receiverBankingInfo = new BankingInfo(bank, name, number);

                // Split documents by comma and space
                String[] documentsArray = documents.split(", ");

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
            } else {
                System.out.println("Banking Information is invalid");
            }
        }
        this.listOfClaims = claim;
    } catch (Exception e){
        System.out.println("Error: "+e.getMessage());
    }
}

    public void loadInsuranceCardsFromFile() {
        loadCustomsFromFile();
        try {
            ArrayList<InsuranceCard> cards = new ArrayList<>();
            Scanner fileScanner = new Scanner(new File("dataFile/insuranceCards.txt"));
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
                String cardNumber = stringTokenizer.nextToken();
                Customer cardHolder = getCustomerById(stringTokenizer.nextToken().trim());
                String policyOwner = stringTokenizer.nextToken();
                String expirationDateStr = stringTokenizer.nextToken();
                Date expirationDate = new SimpleDateFormat("dd-MM-yyyy").parse(expirationDateStr);

                cards.add(new InsuranceCard(cardNumber, cardHolder, policyOwner, expirationDate));
            }
            this.listOfInsuranceCards = cards;
//            for (InsuranceCard card : listOfInsuranceCards) {
//                System.out.println(card.toString());
//            }
//            System.out.println("Insurance cards loaded from dataFile/insuranceCards.txt");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private synchronized String generateUniqueClaimID() {
        int maxAssignedNumber = 0;
        for (Claim claim : listOfClaims) {
            String containerID = claim.getId();
            if (containerID.startsWith("f-")) {
                try {
                    // Correctly parsing the numeric part of the ID after "f-"
                    int number = Integer.parseInt(containerID.substring(2));
                    maxAssignedNumber = Math.max(maxAssignedNumber, number);
                } catch (NumberFormatException e) {
                    // Handle parsing error if necessary
                }
            }
        }

        // Increment to get the next number
        int nextNumber = maxAssignedNumber + 1;
        // Generate the next ID, adjusting for length if necessary
        String nextID = String.format("f-%010d", nextNumber);

        return nextID;
    }

    public void writeClaimsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("dataFile/claims.txt"))) {
            // Write the CSV header
            writer.println("ID,Claim Date,Insured Person,Card Number,Exam Date,List of Documents,Amount,Status,Receiver Banking Infor");

            // Write claim records
            for (Claim claim : listOfClaims) {
                String formattedClaimDate = DateUtils.formatDate(claim.getClaimDate()); // Use utility method to format date
                String formattedExamDate = DateUtils.formatDate(claim.getExamDate()); // Use utility method to format date
                // Joining documents with semicolon and wrapping with square brackets
                String documents = String.join(";", claim.getDocuments()) ;

                writer.println(String.format("%s,%s,%s,%s,%s,[%s],%s,%s,%s",
                        claim.getId(),
                        formattedClaimDate, // Use formatted claim date
                        claim.getInsuredPerson().getId(), // Correcting assumption comment for clarity
                        claim.getCardNumber().getCardNumber(), // Assuming getCardNumber returns an InsuranceCard object with getCardNumber()
                        formattedExamDate, // Use formatted exam date
                        documents, // Documents string already includes square brackets
                        claim.getClaimAmount(),
                        claim.getStatus(),
                        claim.getReceiverBankingInfo().printInfor()
                ));
            }
//            System.out.println("Claim has been written to " + "dataFile/claims.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCustomersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("dataFile/customers.txt"))) {
            // Write the CSV header
            writer.println("ID,Full Name,Insurance Card,List Of Claims,List Of Dependents");

            // Write claim records
            for (Customer customer : listOfCustomers) {

                writer.println(String.format("%s,%s,%s,[%s],[%s]",
                        customer.getId(),
                        customer.getFullName(),
                        customer.getInsuranceCard().getCardNumber(),
                        customer.getClaims().stream().map(Claim::getId).collect(Collectors.joining(";")),
                        customer instanceof PolicyHolder ? ((PolicyHolder) customer).getListOfDependents().stream().map(Dependent::getId).collect(Collectors.joining(";")) : ""
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInsuranceCardoFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("dataFile/insuranceCards.txt"))) {
            // Write the CSV header
            writer.println("Card Number,Card Holder,Policy Owner,Expiration Date");

            // Write claim records
            for (InsuranceCard insurancecard : listOfInsuranceCards) {
                writer.println(String.format("%s,%s,%s,%s",
                        insurancecard.getCardNumber(),
                        insurancecard.getCardHolder().getId(),
                        insurancecard.getPolicyOwner(),
                        DateUtils.formatDate(insurancecard.getExpirationDate())
                ));
            }
//            System.out.println("Insurance card has been written to " + "insuranceCards.txt.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Claim> getListOfClaims() {
        return listOfClaims;
    }

    public ArrayList<Customer> getListOfCustomers() {
        return listOfCustomers;
    }

    public ArrayList<InsuranceCard> getListOfInsuranceCards() {
        return listOfInsuranceCards;
    }


    public class DateUtils {
        public static String formatDate(Date date) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            return formatter.format(date);
        }
    }

}