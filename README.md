# find_discount

This repository is based on the examples in [aws-doc-sdk-examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/s3). You can run DiscountFinder by cloning the repo and running ./run_example.sh DiscountFinder from the command line

I started by following the instructions in the AWS SDK for Java 2.0 Developer Guide to install the SDK and set up a Maven build. I continued to Creating Service Clients to get a feel for how to use to the SDK. However, it wasn't happy with my pom file. I wasn't sure where to go from here, so I grabbed the Getting Started sample code and tried to run it, following the steps in the Readme. 

No luck - so I grabbed the examples repo on github from the Code Samples section and was able to follow the instructions to get S3BucketOps from s3 to work. This seemed like relatively safe operation that I wouldn't get charged for (since it just creates a bucket and deletes it), and a good way to confirm that my SDK was working and installed properly, and classpath is set correctly. Great! So I moved over to the ec2 folder - however - I got many Class Not Found errors in the stock example code. The next move was to add ec2 to the s3 pom and try to import the needed ec2.service.models, just to make sure I could grab them and (for example) create an Ec2 client, make a DescribeReservedInstanceOfferingsRequest and get a DescribeReservedInstanceOfferingsResult. However, I must still be making some pom errors because it's not happy with this.

Next step was do something even simpler - grab the aws cli and try to make those requests! After some fiddling, I started getting results. This was very helpful to see what kind of results to expect, what the available filters and parameters are, and get familiar with the aws API. I went ahead and made sure I could do the same with savingsplans to describe-savings-plans-offering-rates. This was also helpful to see that savingsplans will also have to be added to the pom, and will be separate from ec2.

I moved back to getting a new class within S3 on the example code going, and was able to successfully change the pom file and import the required ec2 packages, and create a DescribeInstanceOfferingsRequest with the correct information, however, when I create a DescribeInstanceOfferingsResponse and use ec2.describeReserveInstanceOfferings(request), it simply hangs.

However, because I was able to use the cli api to get responses and get a list of offerings, so I just ran those api calls from the cli, and redirected the output to a file. Since the output is in JSON, I was then able to read the files into my Java program, parse the JSON input and use JSON objects to get the required information from the field.

Savings Plans prices are given at an hourly rate for a duration. Since there were no duration requirements in the given problem, I made the assumption the we are okay with either 1 year or 3 year commitments, as long as we can get the lowest price. I put each offering into a hash map with the offering id as the key and the hourly price as the value.

Reserved Instance offerings are given at a fixed cost, plus some hourly rate (the fixed cost can be 0 or the hourly rate can be 0, depending on whether it's all upfront or no upfront, or they can both have non-zero values if it is partial upfront). To calculate the hourly rate in order to compare these costs to savings plans, I calculated the hourly rate that is being charged on the upfront fixed price, then added the additional hourly cost. I put each offering into a hash map with the offering id as the key and the hourly price as a value, as with the savings plan. 

Finally, I found the minimum value in the hash map. The offer that is the key for this value is the best price based on the given requirements.

What still needs to be done in this program is to make the api call to get the Reserved Instances and Savings Plans offerings from the Java api, and use the object returned to make calculations instead of using the stand-in values from the JSON files from the cli api call. The current offerings likely change, so while this was a good way to get past a point of being stuck, the Java api will need to be used. The object returned by the DescribeReservedInstanceOfferingsResponse and DescribeSavingsPlansOfferingsRates will have the same fields as the JSON, and the calculations will still be the same. 

After completing the API call, adding tests can be worthwhile.
Some test examples are:
Validate that I am only receiving offerings that are LINUX/Unix
Validate that I am only receiving offerings that are in us-west-2
Validate that I am only receiving offerings that are m5.large
Validate that we get the correct error if we are not able to complete the api call
Validate correct response in the case where there are no offerings that meet the
criteria
