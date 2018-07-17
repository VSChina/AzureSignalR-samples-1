package com.fabrikam.functions;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import org.json.JSONObject;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import com.fabrikam.functions.SignInTable.SignInStats;
import com.mashape.unirest.http.Unirest;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/hello". Two ways to invoke it using
     * "curl" command in bash: 1. curl -d "HTTP Body" {your host}/api/hello 2. curl
     * {your host}/api/hello?name=HTTP%20Query
     */
    @FunctionName("signin")
    public HttpResponseMessage<String> signin(
            @HttpTrigger(name = "req", methods = { "get",
                    "post" }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            SignInTable table = new SignInTable(System.getenv("TableConnectionString"));
            String endpoint = System.getenv("AzureSignalREndpoint");
            String serverToken = System.getenv("AzureSignalRServerToken");
            String clientToken = System.getenv("AzureSignalRClientToken");

            // add sign-in record
            table.Add(request.getQueryParameters().get("os"), request.getQueryParameters().get("broswer"));

            // calculate statistics
            SignInStats stats = table.GetStats();

            // broadcast through SignalR
            Unirest.post(String.format("https://%s:5002/api/v1-preview/hub/signin", endpoint))
                    .header("authorization", String.format("Bearer %s", serverToken))
                    .header("accept", "application/json").header("content-type", "application/json")
                    .header("cache-control", "no-cache").header("postman-token", "d4aedd2f-4572-6415-c62a-f9d772c37af6")
                    .body(String.format("{\"Target\":\"updateSignInStats\",\n\"Arguments\":[%s, %s, %s]}",
                            stats.totalNumber, new JSONObject(stats.byOS), new JSONObject(stats.byBrowser)))
                    .asString();

            JSONObject obj = new JSONObject();
            JSONObject objAuthInfo = new JSONObject();
            objAuthInfo.put("serviceUrl", String.format("https://%s:5001/client/?hub=signin", endpoint));
            objAuthInfo.put("accessToken", clientToken);
            obj.put("authInfo", objAuthInfo);
            JSONObject objStats = new JSONObject();
            objStats.put("totalNumber", stats.totalNumber);
            objStats.put("byOS", stats.byOS);
            objStats.put("byBrowser", stats.byBrowser);
            obj.put("stats", objStats);
            return request.createResponse(200, obj.toString());
        } catch (Exception e) {
            return request.createResponse(500, e.toString());
        }
    }
}
