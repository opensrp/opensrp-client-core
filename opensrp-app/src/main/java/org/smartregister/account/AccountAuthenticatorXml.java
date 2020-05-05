package org.smartregister.account;

/**
 * Created by ndegwamartin on 08/05/2020.
 */
public class AccountAuthenticatorXml {
    private String accountType;
    private String accountName;
    private int icon;

    public String getAccountType() {
        return accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public int getIcon() {
        return icon;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
