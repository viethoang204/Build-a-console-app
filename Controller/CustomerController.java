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

    public Customer getOne(String id){
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

    public boolean deleteCustomer(String id){
        if (claimController.getListOfCustomers().removeIf(customer -> customer.getId().equals(id))) {
            claimController.writeCustomersToFile();
            return true;
        };
        return false;
    }


}