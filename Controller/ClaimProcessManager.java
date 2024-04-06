/**
 * @author <Duong Viet Hoang - S3962514>
 */

package Controller;

import Model.BankingInfo;
import Model.Claim;
import Model.Customer;
import Model.InsuranceCard;

import java.util.Date;
import java.util.List;

public interface ClaimProcessManager {
    void add(Date claimdate, Customer insuredperson, InsuranceCard cardnumber, Date examdate, List<String> listofdocuments, double amount, String status, BankingInfo receiverbankinginfor);
    void update(Claim claim);
    boolean delete(String claimId);
    Claim getOne(String claimId);
    List<Claim> getAll();
}
