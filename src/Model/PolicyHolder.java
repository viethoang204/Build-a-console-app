package Model;

import java.util.ArrayList;
import java.util.List;

public class PolicyHolder extends Customer {
    private List<Dependent> dependents;

    public PolicyHolder(String id, String fullName, InsuranceCard insuranceCard) {
        super(id, fullName, insuranceCard);
        this.dependents = new ArrayList<>();
    }

    public List<Dependent> getDependents() {
        return dependents;
    }

    public void setDependents(List<Dependent> dependents) {
        this.dependents = dependents;
    }
}

