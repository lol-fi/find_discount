# find_discount

This repository is based on the examples in [aws-doc-sdk-examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/s3). You can run DiscountFinder by cloning the repo and running ./run_example.sh DiscountFinder from the command line

I started by following the instructions in the AWS SDK for Java 2.0 Developer Guide to install the SDK and set up a Maven build. I continued to Creating Service Clients to get a feel for how to use to the SDK. However, it wasn't happy with my pom file. I wasn't sure where to go from here, so I grabbed the Getting Started sample code and tried to run it, following the steps in the Readme. 

No luck - so I grabbed the examples repo on github from the Code Samples section and was able to follow the instructions to get S3BucketOps from s3 to work. This seemed like relatively safe operation that I wouldn't get charged for (since it just creates a bucket and deletes it), and a good way to confirm that my SDK was working and installed properly, and classpath is set correctly. Great! So I moved over to the ec2 folder - however - I got many Class Not Found errors in the stock example code. The next move was to add ec2 to the s3 pom and try to import the needed ec2.service.models, just to make sure I could grab them and (for example) create an Ec2 client, make a DescribeReservedInstanceOfferingsRequest and get a DescribeReservedInstanceOfferingsResult. However, I must still be making some pom errors because it's not happy with this.

Next step was do something even simpler - grab the aws cli and try to make those requests! After some fiddling, I started getting results. This was very helpful to see what kind of results to expect, what the available filters and parameters are, and get familiar with the aws API. I went ahead and made sure I could do the same with savingsplans to describe-savings-plans-offering-rates. This was also helpful to see that savingsplans will also have to be added to the pom, and will be separate from ec2.

My next steps are start again with a new maven build, add the necessary items to the pom, grab the right models, make the savings plan and reserved instances requests with the right parameters, and then compare rates between SP offerings which are billed in rate/hr with a given commitment time and RI which are fixed price/duration. 

I moved back to getting a new class within S3 on the example code going, and was able to successfully change the pom file and import the required ec2 packages, and create a DescribeInstanceOfferingsRequest with the correct information, however, when I create a DescribeInstanceOfferingsResponse and use ec2.describeReserveInstanceOfferings(request), it simply hangs.

