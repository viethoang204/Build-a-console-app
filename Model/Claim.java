package Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Claim {
    private String id; // Format: f-xxxxxxxxxx
    private Date claimDate;
    private Customer insuredPerson;
    private InsuranceCard cardNumber;
    private Date examDate;
    private List<String> documents;
    private double claimAmount;
    private String status; // New, Processing, Done
    private String receiverBankingInfo; // Bank – Name – Number

    public InsuranceCard getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(InsuranceCard cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Claim(String id, Date claimDate, Customer insuredPerson, InsuranceCard cardNumber, Date examDate, List<String> documents, double claimAmount, String status, String receiverBankingInfo) {
        this.id = id;
        this.claimDate = claimDate;
        this.insuredPerson = insuredPerson;
        this.cardNumber = cardNumber;
        this.examDate = examDate;
        this.documents = documents;
//        this.documents = new ArrayList<>();
        this.claimAmount = claimAmount;
        this.status = status;
        this.receiverBankingInfo = receiverBankingInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(Date claimDate) {
        this.claimDate = claimDate;
    }

    public Customer getInsuredPerson() {
        return insuredPerson;
    }

    public void setInsuredPerson(Customer insuredPerson) {
        this.insuredPerson = insuredPerson;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiverBankingInfo() {
        return receiverBankingInfo;
    }

    public void setReceiverBankingInfo(String receiverBankingInfo) {
        this.receiverBankingInfo = receiverBankingInfo;
    }
    // Convert claim data to a single string for file saving

//    public String toFileString() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String claimDateStr = dateFormat.format(claimDate);
//        String examDateStr = dateFormat.format(examDate);
//        String documentsStr = String.join(";", documents); // Using semicolon to separate documents
//
//        // Assuming Customer and InsuranceCard classes have a toString method that returns a string representation
//        // For example, customer's ID or name, and card's number. If not, you might need to adjust this part.
//        String insuredPersonStr = insuredPerson.toString();
//        String cardNumberStr = cardNumber.toString();
//
//        return String.join(",",
//                id,
//                claimDateStr,
//                insuredPersonStr,
//                cardNumberStr,
//                examDateStr,
//                documentsStr,
//                String.valueOf(claimAmount),
//                status,
//                receiverBankingInfo);
//    }
}