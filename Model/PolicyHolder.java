package Model;

import java.util.ArrayList;
import java.util.List;

public class PolicyHolder extends Customer {
    private List<Dependent> dependents;

    public PolicyHolder(String id, String fullName, InsuranceCard insuranceCard) {
        super(id, fullName, insuranceCard, new ArrayList<>(), new ArrayList<>());
        this.dependents = new ArrayList<>();
    }

    public List<Dependent> getDependentMembers() {
        return dependents;
    }

    public void setDependentMembers(List<Dependent> dependents) {
        this.dependents = dependents;
    }
}