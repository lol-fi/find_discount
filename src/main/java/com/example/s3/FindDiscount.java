//snippet-sourcedescription:[S3BucketOps.java demonstrates how to create, list and delete S3 buckets.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-06]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * This program makes calls to the AWS Ec2 and savingsplans APIs to
 * get all offerings of Reserved Instances and Savings Plans with
 * Linux/UNIX, m5.large in teh us-west-2 region. It then determines
 * the offering with the lowest hourly cost
 */

package com.example.s3;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesOfferingsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesOfferingsResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.savingsplans.SavingsplansClient;
import software.amazon.awssdk.services.savingsplans.model.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


import org.json.simple.JSONArray;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;

public class FindDiscount{

    public static void main(String[] args) throws IOException, ParseException {

        Map<String, Double> offerings = new HashMap<>();
       Ec2Client ec2 = Ec2Client.create();
       SavingsplansClient spc = SavingsplansClient.create();
       ArrayList<SavingsPlanOfferingRateFilterElement> spfeList = new ArrayList<>();
       try {
            DescribeReservedInstancesOfferingsRequest req = DescribeReservedInstancesOfferingsRequest.builder().availabilityZone("us-west-2").instanceType("m5.large").productDescription("Linux/UNIX").build();
            SavingsPlanOfferingRateFilterElement spf1 = SavingsPlanOfferingRateFilterElement.builder().name("region").values("us-west-2").build();
            SavingsPlanOfferingRateFilterElement spf2 = SavingsPlanOfferingRateFilterElement.builder().name("instanceType").values("m5.large").build();
            SavingsPlanOfferingRateFilterElement spf3 = SavingsPlanOfferingRateFilterElement.builder().name("product").values("Linux/UNIX").build();
            DescribeSavingsPlansOfferingRatesRequest spreq = DescribeSavingsPlansOfferingRatesRequest.builder().filters(spf1, spf2, spf3).build();
            //DescribeSavingsPlansOfferingRatesResponse spres = spc.describeSavingsPlansOfferingRates(spreq);
            //System.out.println(spres);
            //DescribeReservedInstancesOfferingsResponse res = ec2.describeReservedInstancesOfferings(req);
            //System.out.println(res);
        } catch (Ec2Exception e) {
            e.getStackTrace();
        }

        //can't currently make the right calls, so fake out data with json files grabbed
        //from cli interface
        try {
            Object obj = new JSONParser().parse(new FileReader("ri.json"));
            JSONObject jsonObj = (JSONObject) obj;
            JSONArray c = (JSONArray) jsonObj.get("ReservedInstancesOfferings");
            for (int n = 0; n < c.size(); n++) {
                JSONObject curr = (JSONObject) c.get(n);
                String id = (String) curr.get("ReservedInstancesOfferingId");
                long duration = (long) curr.get("Duration");
                double fixedPrice = (double) curr.get("FixedPrice");
                JSONArray recurring = (JSONArray) curr.get("RecurringCharges");
                JSONObject rcharges = (JSONObject) recurring.get(0);
                double hourlyrecurring = (double) rcharges.get("Amount");
                double hourly = (fixedPrice/((duration/60)/60)) + hourlyrecurring;
                offerings.put(id, hourly);
            }
        } catch (IOException fnf) {
            fnf.getStackTrace();
        }
        try {
            Object obj = new JSONParser().parse(new FileReader("sp.json"));
            JSONObject jsonObj = (JSONObject) obj;
            JSONArray c = (JSONArray) jsonObj.get("searchResults");
            for (int n = 0; n < c.size(); n++) {
                JSONObject curr = (JSONObject) c.get(n);
                JSONObject spInstance = (JSONObject) curr.get("savingsPlanOffering");
                String id = (String) spInstance.get("offeringId");
                String rate = (String) curr.get("rate");
                double drate = Double.parseDouble(rate);
                offerings.put(id, drate);
            }
        } catch (IOException fnf) {
            fnf.getStackTrace();
        }

        String key = Collections.min(offerings.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Best offering ID: " + key);

        ec2.close();
        spc.close();

    }
}
