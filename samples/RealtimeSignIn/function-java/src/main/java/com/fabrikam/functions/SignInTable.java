package com.fabrikam.functions;

import java.util.Hashtable;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;

public class SignInTable {
    private CloudTable table;

    public SignInTable(String connectionString) throws Exception {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
        CloudTableClient tableClient = storageAccount.createCloudTableClient();
        this.table = tableClient.getTableReference("uainfo");
    }

    public void Add(String os, String browser) throws Exception {
        SignInInfo newInfo = new SignInInfo(os, browser);
        TableOperation insert = TableOperation.insert(newInfo);
        this.table.execute(insert);
    }

    public SignInStats GetStats() {
        SignInStats stats = new SignInStats();
        TableQuery<SignInInfo> query = new TableQuery<SignInInfo>(SignInInfo.class);
        for (SignInInfo entity : this.table.execute(query)) {
            stats.totalNumber++;
            if (entity.getOS() != null) {
                if (!stats.byOS.containsKey(entity.getOS())) {
                    stats.byOS.put(entity.getOS(), 0);
                }
                stats.byOS.put(entity.getOS(), stats.byOS.get(entity.getOS()) + 1);
            }
            if (entity.getBrowser() != null) {
                if (!stats.byBrowser.containsKey(entity.getBrowser())) {
                    stats.byBrowser.put(entity.getBrowser(), 0);
                }
                stats.byBrowser.put(entity.getBrowser(), stats.byBrowser.get(entity.getBrowser()) + 1);
            }
        }
        return stats;
    }

    public class SignInStats {
        public Integer totalNumber;

        public Hashtable<String, Integer> byOS;

        public Hashtable<String, Integer> byBrowser;

        public SignInStats() {
            this.totalNumber = 0;
            this.byOS = new Hashtable<String, Integer>();
            this.byBrowser = new Hashtable<String, Integer>();
        }
    }
}
