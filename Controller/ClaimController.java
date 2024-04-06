/**
 * @author <Duong Viet Hoang - S3962514>
 */

package Controller;

import Model.*;

import java.util.Comparator;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimController implements ClaimProcessManager {
    public String currentClaimOrder = "default";
    private static ClaimController instance;
    private ArrayList<Claim> listOfClaims;
    private ArrayList<Customer> listOfCustomers;
    private ArrayList<InsuranceCard> listOfInsuranceCards;
    private CustomerController customerController;

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
    public void update(Claim updatedClaim) {
        List<Claim> claims = getAll();
        for (int i = 0; i < claims.size(); i++) {
            if (claims.get(i).getId().equals(updatedClaim.getId())) {
                claims.set(i, updatedClaim);
                break;
            }
        }
        writeClaimsToFile();
    }

    @Override
    public List<Claim> getAll() {
        return new ArrayList<>(this.listOfClaims);
    }

    @Override
    public boolean delete(String claimId){
        if (listOfClaims.removeIf(claim -> claim.getId().equals(claimId))) {
            writeClaimsToFile();
            return true;
        }
        return false;
    }

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

    public String getInsuranceCardNumberById(String id) {
        for (InsuranceCard card : listOfInsuranceCards) {
            if (card.getCardHolder().getId().equals(id.trim())) {
                return card.getCardNumber();
            }
        }
        return null;
    }

    public void loadCustomsFromFile() {
        loadClaimsFromFile();
        try {
            ArrayList<Customer> customers = new ArrayList<Customer>();
            Scanner fileScanner = new Scanner(new File("dataFile/customers.txt"));
            fileScanner.nextLine();
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                String id = parts[0];
                String fullName = parts[1];
                InsuranceCard cardNumber = getInsuranceCardById(parts[2].trim());
                String[] claimsArray = parts[3].substring(1, parts[3].length() - 1).split(";");

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
            ArrayList<Claim> claims = new ArrayList<>();
            Scanner fileScanner = new Scanner(new File("dataFile/claims.txt"));
            fileScanner.nextLine(); // Skip header line
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    String ID = parts[0].trim();
                    String claimDate = parts[1].trim();
                    String insuredPersonId = parts[2].trim();
                    String cardNumber = parts[3].trim();
                    String examDate = parts[4].trim();
                    String documents = parts[5].trim().replace("[", "").replace("]", "");
                    double amount = Double.parseDouble(parts[6].trim());
                    String status = parts[7].trim();
                    String receiverBankingInfoStr = parts[8].trim();

                    Customer insuredPerson = null;
                    InsuranceCard insuranceCard = null;

                    // If Insured Person and Card Number are not provided, set them to null
                    if (!insuredPersonId.equals("null") && !cardNumber.equals("null")) {
                        insuredPerson = getCustomerById(insuredPersonId);
                        insuranceCard = getInsuranceCardById(cardNumber);
                    }

                    String[] bankingInfoParts = receiverBankingInfoStr.split("-");
                    if (bankingInfoParts.length == 3) {
                        String bank = bankingInfoParts[0].trim();
                        String name = bankingInfoParts[1].trim();
                        String number = bankingInfoParts[2].trim();
                        BankingInfo receiverBankingInfo = new BankingInfo(bank, name, number);

                        // Split documents by semicolon and remove leading/trailing whitespace
                        String[] documentsArray = documents.split(";");
                        for (int i = 0; i < documentsArray.length; i++) {
                            documentsArray[i] = documentsArray[i].trim();
                        }

                        claims.add(new Claim(
                                ID,
                                new SimpleDateFormat("dd-MM-yyyy").parse(claimDate),
                                insuredPerson,
                                insuranceCard,
                                new SimpleDateFormat("dd-MM-yyyy").parse(examDate),
                                Arrays.asList(documentsArray),
                                amount,
                                status,
                                receiverBankingInfo));
                    } else {
                        System.out.println("Banking Information is invalid");
                    }
                } else {
                    System.out.println("Invalid claim record: " + line);
                }
            }
            this.listOfClaims = claims;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
                String formattedClaimDate = formatDate(claim.getClaimDate()); // Use utility method to format date
                String formattedExamDate = formatDate(claim.getExamDate()); // Use utility method to format date
                // Joining documents with semicolon and wrapping with square brackets
                String documents = String.join(";", claim.getDocuments()) ;

                // Check if insured person and card number are null
                String insuredPersonId = claim.getInsuredPerson() != null ? claim.getInsuredPerson().getId() : "no data";
                String cardNumber = claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : "no data";

                writer.println(String.format("%s,%s,%s,%s,%s,[%s],%s,%s,%s",
                        claim.getId(),
                        formattedClaimDate, // Use formatted claim date
                        insuredPersonId,
                        cardNumber,
                        formattedExamDate, // Use formatted exam date
                        documents, // Documents string already includes square brackets
                        claim.getClaimAmount(),
                        claim.getStatus(),
                        claim.getReceiverBankingInfo().printInfor()
                ));
            }

        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public void writeClaimsToFile1() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("dataFile/claims.txt"))) {
            // Write the CSV header
            writer.println("ID,Claim Date,Insured Person,Card Number,Exam Date,List of Documents,Amount,Status,Receiver Banking Infor");

            // Write claim records
            for (Claim claim : listOfClaims) {
                String formattedClaimDate = formatDate(claim.getClaimDate()); // Use utility method to format date
                String formattedExamDate = formatDate(claim.getExamDate()); // Use utility method to format date
                // Joining documents with semicolon and wrapping with square brackets
                String documents = String.join(";", claim.getDocuments()) ;

                // Check if insured person and card number are null
                String insuredPersonId = claim.getInsuredPerson() != null ? claim.getInsuredPerson().getId() : null;
                String cardNumber = claim.getCardNumber() != null ? claim.getCardNumber().getCardNumber() : null;

                writer.println(String.format("%s,%s,%s,%s,%s,[%s],%s,%s,%s",
                        claim.getId(),
                        formattedClaimDate, // Use formatted claim date
                        insuredPersonId,
                        cardNumber,
                        formattedExamDate, // Use formatted exam date
                        documents, // Documents string already includes square brackets
                        claim.getClaimAmount(),
                        claim.getStatus(),
                        claim.getReceiverBankingInfo().printInfor()
                ));
            }

        } catch (IOException e) {
//            e.printStackTrace();
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

    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    public void sortClaimsByClaimDate(boolean ascending) {
        if (ascending) {
            // Sort claims in ascending order by date (earlier dates at the top)
            listOfClaims.sort(Comparator.comparing(Claim::getClaimDate));
            currentClaimOrder = "claim date from oldest to newest";
        } else {
            // Sort claims in descending order by date (later dates at the top)
            listOfClaims.sort(Comparator.comparing(Claim::getClaimDate).reversed());
            currentClaimOrder = "claim date from newest to oldest";
        }
    }

    public void sortClaimsByExamDate(boolean ascending) {
        if (ascending) {
            listOfClaims.sort(Comparator.comparing(Claim::getExamDate));
            currentClaimOrder = "exam date from oldest to newest";
        } else {
            // Sort claims in descending order by date (later dates at the top)
            listOfClaims.sort(Comparator.comparing(Claim::getExamDate).reversed());
            currentClaimOrder = "exam date from newest to oldest";
        }
    }

    public void sortClaimsByClaimAmount(boolean ascending) {
        if (ascending) {
            listOfClaims.sort(Comparator.comparing(Claim::getClaimAmount));
            currentClaimOrder = "claim amount from lowest oldest to highest";
        } else {
            // Sort claims in descending order by date (later dates at the top)
            listOfClaims.sort(Comparator.comparing(Claim::getClaimAmount).reversed());
            currentClaimOrder = "claim amount from lowest highest to oldest";
        }
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

}