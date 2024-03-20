package Model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String id; // Format: c-xxxxxxx
    private String fullName;
    private InsuranceCard insuranceCard;
    private List<Claim> claims;
    private List<Claim> dependens;

    public List<Claim> getDependens() {
        return dependens;
    }

    public void setDependens(List<Claim> dependens) {
        this.dependens = dependens;
    }

    public Customer(String id, String fullName, InsuranceCard insuranceCard,List<String> claims,List<String> dependens) {
        this.id = id;
        this.fullName = fullName;
        this.insuranceCard = insuranceCard;
        this.claims = new ArrayList<>();
        this.dependens = new ArrayList<>();
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