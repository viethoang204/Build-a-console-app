package Controller;

import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimManagerController implements ClaimProcessManager {
    private static ClaimManagerController instance;

    public static ClaimManagerController getInstance(){
        if (instance == null) {
            instance = new ClaimManagerController();
        }
        return instance;
    }
    private ArrayList<Claim> listOfClaims = new ArrayList<Claim>();

    @Override
    public void add(Date claimdate, Customer insuredperson, InsuranceCard cardnumber, Date examdate, List<String> listofdocuments, double amount, String status, String receiverbankinginfor) {
        Claim claim = new Claim("c-"+generateUniqueClaimID(),claimdate, insuredperson, cardnumber, examdate, listofdocuments, amount, status, receiverbankinginfor);
        this.listOfClaims.add(claim);
        writeClaimsToFile();
    }

    public void loadUsersFromFile() {
    try {
        ArrayList<Claim> claim = new ArrayList<Claim>();
        Scanner fileScanner = new Scanner(new File("dataFile/claims.txt"));
        fileScanner.nextLine();
        while (fileScanner.hasNext()) {
            String[] data; // Create an array to store one claim's information
            String line = fileScanner.nextLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line, ",");

            // Separate the line's information by comma
            String ID = stringTokenizer.nextToken();
            String claimDate = stringTokenizer.nextToken();
            String insuredPerson = stringTokenizer.nextToken();
            String cardNumber = stringTokenizer.nextToken();
            String examDate = stringTokenizer.nextToken();
            String documents = stringTokenizer.nextToken();
            String amount = stringTokenizer.nextToken();
            String status = stringTokenizer.nextToken();
            String receiverBankingInfo = stringTokenizer.nextToken();
            claim.add(new Claim(
                    ID,
                    new SimpleDateFormat("yyyy-MM-dd").parse(claimDate),
                    new Customer(insuredPerson),
                    new InsuranceCard(cardNumber),
                    new SimpleDateFormat("yyyy-MM-dd").parse(examDate), 
                    Arrays.asList(documents.split(";")),
                    Double.parseDouble(amount),
                    status,
                    receiverBankingInfo));

        }
        this.listOfClaims = claim;
        System.out.println("Users loaded from dataFile/users.txt");
    } catch (Exception e){
        System.out.println("Error: "+e.getMessage());
    }
}

    public void writeClaimsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("dataFile/claims.txt"))) {
            // Write the CSV header
            writer.println("ID,ClaimDate,InsuredPerson,CardNumber,ExamDate,ListofDocuments,Amount,Status,ReceiverBankingInfor");

            // Write user records
            for (Claim claim : listOfClaims) {
                String port; String role;
                writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        claim.getId(),
                        claim.getClaimDate(),
                        claim.getInsuredPerson(),
                        claim.getCardNumber(),
                        claim.getExamDate(),
                        claim.getDocuments(),
                        claim.getClaimAmount(),
                        claim.getStatus(),
                        claim.getReceiverBankingInfo()
                ));
            }
            System.out.println("Users have been written to " + "dataFile/claims.txt");
        } catch (IOException e) {
            e.printStackTrace();
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
    public void delete(String claimId) {

    }

    @Override
    public Claim getOne(String claimId) {
        return null;
    }

    @Override
    public List<Claim> getAll() {
        return this.listOfClaims.stream().filter(claim -> claim instanceof Claim).collect(Collectors.toCollection(ArrayList::new));
    }

}