package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Customer {
    private String id;
    private String fullName;
    private InsuranceCard insuranceCard;
    private List<Claim> claims;

    // Corrected constructor
    public Customer(String id, String fullName, InsuranceCard insuranceCard, List<Claim> claims) {
        this.id = id;
        this.fullName = fullName;
        this.insuranceCard = insuranceCard;
        this.claims = claims;}

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
                ", insuranceCardNumber='" + (insuranceCard != null ? insuranceCard : "N/A") + '\'' +
                ", claims=" + claims +
                '}';
    }


}