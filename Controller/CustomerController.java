package Controller;

import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;
import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    private ClaimController claimController;
    private static CustomerController instance;

    public static CustomerController getInstance(){
        if (instance == null) {
            instance = new CustomerController();
        }
        return instance;
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
}