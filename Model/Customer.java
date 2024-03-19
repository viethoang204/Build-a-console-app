package Model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    protected String id; // Format: c-xxxxxxx
    protected String fullName;
    protected InsuranceCard insuranceCard;
    protected List<Claim> claims;

    public Customer(String id, String fullName, InsuranceCard insuranceCard) {
        this.id = id;
        this.fullName = fullName;
        this.insuranceCard = insuranceCard;
        this.claims = new ArrayList<>();
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
}