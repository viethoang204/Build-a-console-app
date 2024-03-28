package Model;

import Controller.ClaimController;

import java.util.ArrayList;
import java.util.List;

public class Dependent extends Customer {

    public Dependent(String id, String fullName, InsuranceCard insuranceCard, List<Claim> claims) {
        super(id, fullName, insuranceCard, claims);
    }

    // create getPolicyHolder method
    public PolicyHolder getPolicyHolder() {
        // iterate over the list of customers
        for (Customer customer : new ArrayList<>(ClaimController.getInstance().getListOfCustomers())) {
            // check if the customer is a policyholder
            if (customer instanceof PolicyHolder) {
                // cast the customer to a policyholder
                PolicyHolder policyHolder = (PolicyHolder) customer;
                // iterate over the list of dependents
                for (Dependent dependent : policyHolder.getDependents()) {
                    // check if the dependent is the same as this dependent
                    if (dependent.getId().equals(this.getId())) {
                        // return the policyholder
                        return policyHolder;
                    }
                }
            }
        }
        return null;
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