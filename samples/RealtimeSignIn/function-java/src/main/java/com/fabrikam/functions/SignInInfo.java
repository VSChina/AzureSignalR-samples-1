package com.fabrikam.functions;

import java.util.UUID;
import com.microsoft.azure.storage.table.TableServiceEntity;

public class SignInInfo extends TableServiceEntity {
    public SignInInfo() {
    }

    public SignInInfo(String os, String browser) {
        this.rowKey = UUID.randomUUID().toString();
        this.partitionKey = "SignIn";
        this.os = os;
        this.browser = browser;
    }

    String os;
    String browser;

    public String getOS() {
        return this.os;
    }

    public void setOS(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }
}