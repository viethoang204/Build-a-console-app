package Model;

import java.util.ArrayList;
import java.util.List;

public class Dependent extends Customer {

    public Dependent(String id, String fullName, InsuranceCard insuranceCard, List<Claim> claims) {
        super(id, fullName, insuranceCard, claims);
    }

    public String toString() {
        return "Dependent{" +
                "id='" + super.getId() + '\'' +
                ", fullName='" + super.getFullName() + '\'' +
                ", insuranceCardNumber='" + (super.getInsuranceCard() != null ? super.getInsuranceCard() : "N/A") + '\'' +
                ", claims=" + super.getClaims() +
                '}';
    }
}