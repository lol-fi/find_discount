//snippet-sourcedescription:[S3BucketOps.java demonstrates how to create, list and delete S3 buckets.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-06]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright 2011-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.s3;
// snippet-start:[s3.java2.s3_bucket_ops.complete]
// snippet-start:[s3.java2.s3_bucket_ops.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesOfferingsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeReservedInstancesOfferingsResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.savingsplans.SavingsplansClient;
import software.amazon.awssdk.services.savingsplans.model.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


import org.json.simple.JSONArray;
import org.json.simple.parser.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;
// snippet-end:[s3.java2.s3_bucket_ops.import]
// snippet-start:[s3.java2.s3_bucket_ops.main]
public class FindDiscount{

    public static void printJsonObject(JSONObject jsonObj) {
        for (Object key : jsonObj.keySet()) {
            //based on you key types
            String keyStr = (String)key;
            Object keyvalue = jsonObj.get(keyStr);

            //Print key and value
            //System.out.println("key: "+ keyStr + " value: " + keyvalue + "\n");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");

            //for nested objects iteration if required
            if (keyvalue instanceof JSONObject)
                printJsonObject((JSONObject)keyvalue);
        }
    }
    public static void main(String[] args) throws IOException, ParseException {

        // snippet-start:[s3.java2.s3_bucket_ops.create_bucket]
        // snippet-start:[s3.java2.s3_bucket_ops.region]
       Ec2Client ec2 = Ec2Client.create();
       SavingsplansClient spc = SavingsplansClient.create();
       ArrayList<SavingsPlanOfferingRateFilterElement> spfeList = new ArrayList<>();
       try {
            DescribeReservedInstancesOfferingsRequest req = DescribeReservedInstancesOfferingsRequest.builder().availabilityZone("us-west-2").instanceType("m5.large").productDescription("Linux/UNIX").build();
            System.out.println(req.toString());
            SavingsPlanOfferingRateFilterElement spf1 = SavingsPlanOfferingRateFilterElement.builder().name("region").values("us-west-2").build();
            SavingsPlanOfferingRateFilterElement spf2 = SavingsPlanOfferingRateFilterElement.builder().name("instanceType").values("m5.large").build();
            SavingsPlanOfferingRateFilterElement spf3 = SavingsPlanOfferingRateFilterElement.builder().name("product").values("Linux/UNIX").build();
            System.out.println(spf1);
            System.out.println(spf2);
            DescribeSavingsPlansOfferingRatesRequest spreq = DescribeSavingsPlansOfferingRatesRequest.builder().filters(spf1, spf2, spf3).build();
            System.out.println(spreq);
            System.out.println("About to fire off a response");
            //DescribeSavingsPlansOfferingRatesResponse spres = spc.describeSavingsPlansOfferingRates(spreq);
            //System.out.println(spres);
            //DescribeReservedInstancesOfferingsResponse res = ec2.describeReservedInstancesOfferings(req);
            //System.out.println(res);
            System.out.println("Recvd a response, printed nothing");
        } catch (Ec2Exception e) {
            e.getStackTrace();
        }

        //can't currently make the right calls, so fake out data with json files grabbed
        //from cli interface
        try {
            Object obj = new JSONParser().parse(new FileReader("ri.json"));
            JSONObject jsonObj = (JSONObject) obj;
            //System.out.println(jsonObj);
            //printJsonObject(jsonObj);
            JSONArray c = (JSONArray) jsonObj.get("ReservedInstancesOfferings");
            System.out.println(c);
            for (int n = 0; n < c.size(); n++) {
                JSONObject curr = (JSONObject) c.get(n);
                System.out.println("Curr is: " +  curr);
                String id = (String) curr.get("ReservedInstancesOfferingId");
                System.out.println(id);
                //calculation isn't right - what is fixed price referring to?
                long duration = (long) curr.get("Duration");
                double fixedPrice = (double) curr.get("FixedPrice");
                System.out.println("duration: " + duration + "  fixed price" + fixedPrice);
                double hourly = fixedPrice/((duration/60)/60);
                System.out.println("hourly rate: " + hourly);
            }

        } catch (IOException fnf) {
            fnf.getStackTrace();
        }


        // snippet-end:[s3.java2.s3_bucket_ops.delete_bucket]
    }
}

// snippet-end:[s3.java2.s3_bucket_ops.main]
// snippet-end:[s3.java2.s3_bucket_ops.complete]
