package Controller;

import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;

import java.util.Date;
import java.util.List;

public interface ClaimProcessManager {
    void add(Date claimdate, Customer insuredperson, InsuranceCard cardnumber, Date examdate, List<String> listofdocuments, double amount, String status, String receiverbankinginfor);
    void update(Claim claim);
    void delete(String claimId);
    Claim getOne(String claimId);
    List<Claim> getAll();
}
