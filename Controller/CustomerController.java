package Controller;

import Model.*;

import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    private ClaimController claimController;
    private static CustomerController instance;

    public static CustomerController getInstance() {
        if (instance == null) {
            instance = new CustomerController();
        }
        return instance;
    }

    public Customer getOne(String id) {
        return claimController.getListOfCustomers().stream().filter(customer -> customer.getId().equals(id)).findFirst().orElse(null);
    }

    public CustomerController() {
        this.claimController = ClaimController.getInstance();
    }

    public ArrayList<Claim> getListOfClaims() {
        return claimController.getListOfClaims();
    }

    public ArrayList<Customer> getListOfCustomers() {
        return claimController.getListOfCustomers();
    }

    public ArrayList<InsuranceCard> getListOfInsuranceCards() {
        return claimController.getListOfInsuranceCards();
    }

    public List<Customer> getAll() {
        return this.getListOfCustomers();
    }

    public boolean deleteCustomerPLC(String id) {
        Customer customer = this.getOne(id);
        if (customer != null) {
            InsuranceCard card = customer.getInsuranceCard();
            if (card != null) {
                InsuranceCardController.getInstance().delete(card.getCardNumber());
            }
            if (claimController.getListOfCustomers().removeIf(c -> c.getId().equals(id))) {
                // Iterate over the list of claims
                for (Claim claim : new ArrayList<>(claimController.getListOfClaims())) {
                    // Check if the claim's insured person or card number matches the customer or card that is being deleted
                    if (claim.getInsuredPerson() != null && claim.getInsuredPerson().getId().equals(customer.getId())) {
                        // Set the insured person to null
                        claim.setInsuredPerson(null);
                    }
                    if (claim.getCardNumber() != null && claim.getCardNumber().getCardNumber().equals(card.getCardNumber())) {
                        // Set the card number to null
                        claim.setCardNumber(null);
                    }
                }

                // If the customer is a PolicyHolder, delete all their dependents
                if (customer instanceof PolicyHolder) {
                    PolicyHolder policyHolder = (PolicyHolder) customer;
                    List<Dependent> dependentsCopy = new ArrayList<>(policyHolder.getDependents());
                    for (Dependent dependent : dependentsCopy) {
                        deleteCustomerPLC(dependent.getId());
                    }
                }

                if (claimController.getListOfCustomers().removeIf(c -> c.getId().equals(id))) {
                    // Iterate over the list of claims
                    for (Claim claim : claimController.getListOfClaims()) {
                        // Check if the claim's insured person or card number matches the customer or card that is being deleted
                        if (claim.getInsuredPerson().getId().equals(customer.getId()) || claim.getCardNumber().getCardNumber().equals(card.getCardNumber())) {
                            // Set the insured person and card number to null
                            claim.setInsuredPerson(null);
                            claim.setCardNumber(null);
                        }
                    }
                }

//                 If the customer is a Dependent, remove them from their PolicyHolder's list of dependents
                claimController.writeCustomersToFile();
                claimController.writeClaimsToFile1();
                return true;
            }
        }
        return false;
    }

    public boolean deleteCustomerDPD(String id) {
        Customer customer = this.getOne(id);
        if (customer != null) {
            InsuranceCard card = customer.getInsuranceCard();
            if (card != null) {
                InsuranceCardController.getInstance().delete(card.getCardNumber());
            }
            if (claimController.getListOfCustomers().removeIf(c -> c.getId().equals(id))) {
                // Iterate over the list of claims
                for (Claim claim : new ArrayList<>(claimController.getListOfClaims())) {
                    // Check if the claim's insured person or card number matches the customer or card that is being deleted
                    if (claim.getInsuredPerson() != null && claim.getInsuredPerson().getId().equals(customer.getId())) {
                        // Set the insured person to null
                        claim.setInsuredPerson(null);
                    }
                    if (claim.getCardNumber() != null && claim.getCardNumber().getCardNumber().equals(card.getCardNumber())) {
                        // Set the card number to null
                        claim.setCardNumber(null);
                    }
                }
//                 If the customer is a Dependent, remove them from their PolicyHolder's list of dependents
                if (customer instanceof Dependent) {
                    Dependent dependent = (Dependent) customer;
                    PolicyHolder policyHolder = dependent.getPolicyHolder();
                    policyHolder.getDependents().removeIf(d -> d.getId().equals(dependent.getId()));
                }

                if (claimController.getListOfCustomers().removeIf(c -> c.getId().equals(id))) {
                    // Iterate over the list of claims
                    for (Claim claim : claimController.getListOfClaims()) {
                        // Check if the claim's insured person or card number matches the customer or card that is being deleted
                        if (claim.getInsuredPerson().getId().equals(customer.getId()) || claim.getCardNumber().getCardNumber().equals(card.getCardNumber())) {
                            // Set the insured person and card number to null
                            claim.setInsuredPerson(null);
                            claim.setCardNumber(null);
                        }
                    }
                }
                claimController.writeCustomersToFile();
                claimController.writeClaimsToFile1();
                return true;
            }
        }
        return false;
    }
    public PolicyHolder addPolicyHolder(String fullName, InsuranceCard insuranceCard, List<Claim> claims, List<Dependent> dependents) {
        String id = generateUniqueCustomerID();
        PolicyHolder policyHolder = new PolicyHolder(id, fullName, insuranceCard, claims, dependents);
        this.getListOfCustomers().add(policyHolder);
        claimController.writeCustomersToFile();
        return policyHolder;
    }

    public Dependent addDependent(String fullName, InsuranceCard insuranceCard, List<Claim> claims) {
        String id = generateUniqueCustomerID();
        Dependent dependent = new Dependent(id, fullName, insuranceCard, claims);
        this.getListOfCustomers().add(dependent);
        claimController.writeCustomersToFile();
        return dependent;
    }

    private synchronized String generateUniqueCustomerID() {
        int maxAssignedNumber = 0;
        for (Customer customer : claimController.getListOfCustomers()) {
            String containerID = customer.getId();
            if (containerID.startsWith("c-")) {
                try {
                    // Correctly parsing the numeric part of the ID after "c-"
                    int number = Integer.parseInt(containerID.substring(2));
                    maxAssignedNumber = Math.max(maxAssignedNumber, number);
                } catch (NumberFormatException e) {
                    // Handle parsing error if necessary
                }
            }
        }

        // Increment to get the next number
        int nextNumber = maxAssignedNumber + 1;
        // Generate the next ID, adjusting for length if necessary
        String nextID = String.format("c-%07d", nextNumber);

        return nextID;
    }

}