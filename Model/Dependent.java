package Model;

import java.util.ArrayList;

public class Dependent extends Customer {
    private PolicyHolder policyHolder;

    public Dependent(String id, String fullName, InsuranceCard insuranceCard, PolicyHolder policyHolder) {
        super(id, fullName, insuranceCard, new ArrayList<>(), new ArrayList<>());
        this.policyHolder = policyHolder;
    }

    public PolicyHolder getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(PolicyHolder policyHolder) {
        this.policyHolder = policyHolder;
    }
}