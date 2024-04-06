/**
 * @author <Duong Viet Hoang - S3962514>
 */

package Model;

import java.util.List;

public class PolicyHolder extends Customer {
    private List<Dependent> dependents;

    public PolicyHolder(String id, String fullName, InsuranceCard insuranceCard, List<Claim> claims, List<Dependent> listOfDependents) {
        super(id, fullName, insuranceCard, claims);
        this.dependents = listOfDependents;
    }

    public void addDependent(Dependent dependent) {
        this.dependents.add(dependent);
    }

    public List<Dependent> getDependents() {
        return dependents;
    }

    @Override
    public String toString() {
    String listOfDependents = (this.dependents != null) ? this.dependents.toString() : "N/A";
    return "PolicyHolder{" +
            "id='" + super.getId() + '\'' +
            ", fullName='" + super.getFullName() + '\'' +
            ", insuranceCardNumber='" + (super.getInsuranceCard() != null ? super.getInsuranceCard() : "N/A") + '\'' +
            ", claims=" + super.getClaims() +
            ", dependents=" + listOfDependents +
            '}';

}

}