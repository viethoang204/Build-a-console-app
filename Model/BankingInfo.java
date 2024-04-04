/**
 * @author <Duong Viet Hoang - S3962514>
 */

package Model;

public class BankingInfo {
    private String bank;
    private String name;
    private String number;

    public BankingInfo(String bank, String name, String number) {
        this.bank = bank;
        this.name = name;
        this.number = number;
    }

    public String printInfor() {
        String infor_bank = bank + "-" + name + "-" + number;
        return infor_bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}