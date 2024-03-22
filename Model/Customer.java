package Model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String id; // Format: c-xxxxxxx
    private String fullName;
    private InsuranceCard insuranceCard;
    private List<Claim> claims;
    private List<String> dependents; // Note: Fixed typo from "dependens" to "dependents"

    // Corrected constructor
    public Customer(String id, String fullName, InsuranceCard insuranceCard, List<Claim> claims, List<Customer> dependents) {
        this.id = id;
        this.fullName = fullName;
        this.insuranceCard = insuranceCard;
        this.claims = new ArrayList<>(); // Assign the provided list directly
        this.dependents = new ArrayList<>(); // Initialize the list
    }

    public List<String> getDependents() {
        return dependents;
    }

    public void setDependents(List<String> dependents) {
    this.dependents = dependents;
}

    public Customer(String insuredPerson) {
        this.fullName = insuredPerson;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public InsuranceCard getInsuranceCard() {
        return insuranceCard;
    }

    public void setInsuranceCard(InsuranceCard insuranceCard) {
        this.insuranceCard = insuranceCard;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", insuranceCardNumber='" + (insuranceCard != null ? insuranceCard.getCardNumber() : "N/A") + '\'' +
                ", claims=" + claims +
                ", dependents=" + dependents +
                '}';
    }
}