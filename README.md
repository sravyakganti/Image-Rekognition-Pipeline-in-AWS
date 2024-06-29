Login in to AWS Management Console
How to create two instances of EC2: 
1. Select "Launch instance" by clicking. 
2.We can create an EC2 instance by entering its name. Choose Amazon Linux 2 AMI 
3.Decide to use the t2.micro instance type. T2 instances are a type of general-purpose, low-cost instance that can conduct CPU operations at a baseline level and, if necessary, can spike over it. 
4.Generate the Key-Pair value and gets downloaded and name it as Cloud.pem
5.Choose "Create security group" from the Network parameters menu, then review the parameters below. 
Permit SSH communication from 
Permit internet transmission via HTTPs. 
Permit internet HTTP traffic. 
6.Choose My IP rather than Anywhere if you want to send traffic exclusively from your IP address.
Steps for assigning IAM roles to the instances:
1. After creating our instances, visit the EC2 Instances site to see them. If any of our instances are available for usage right now, choose one, click the "Instance state" dropdown menu, and choose "Start instance."
2. Select each instance individually after they have begun to run and select "Actions" > "Security" > "Modify IAM role."
3. The IAM role's "LabInstanceProfile" menu item is accessible. i.e., S3, SQS, and Recognition may not be accessible.
4. If the IAM role does not have access to S3, SQS, or Rekognition, go to IAM > Roles > LabRole and grant the following privileges as policies.
5. Verify that we have the executable JAR files for all of these programs before we begin. Compressed versions of the.class files are included in these JAR files, together with any pertinent picture or audio files and directories.

Establish an S3 bucket: 
1. Open the AWS Management Console and navigate to the S3 service. 
2.Press the "Create bucket" button. 
3.After deciding on a distinctive bucket name, pick the US East-1 (North Virginia) region. 
4.Click "Create bucket" after leaving the other settings as they are. 

Establish a SQS Queue: 
1.Open the AWS Management Console and navigate to the SQS service. 
2.Press the "Create queue" button. 
3.Give your queue a name, and leave the other settings unaltered. 
4."Create queue" should be clicked.
Configure AWS Credentials:
1.Obtain your AWS access key ID and secret access key from the IAM service in the Management Console.
2.Create a file named credentials on both EC2 instances in the ~/.aws directory.
3.Paste the following content into the file, replacing the placeholders with your actual credentials.

Creating the Java Programs: 
InstanceA (Car Recongition): InstanceACarRecognition.java
To develop the code, open a Java project on your local computer.
Add the required libraries, such as the AWS SDK
Implement the following functionalities: 
1.Using the S3 client, read the picture names from the S3 bucket. 
2.Using the S3 client, download every image. 
3.Utilize the Rekognition client to find labels within the picture. 
4.Verify if "Car" is identified with greater than 90% confidence. 
5.Send the image index (filename) to the SQS queue if an automobile is found. 
6.Once every image has been processed, notify the queue with a termination signal (-1).

InstanceB (Text Recognition): InstanceBTextRecognition.java

Make a different Java project specifically for the text recognition. 
Add the libraries that are included in Instance A. 

Implement the following functionalities: 
1.Messages from the SQS queue are received. 
2.Verify whether the message is the -1 termination signal. If so, close the software. 
3.If not, take off the filename and image index from the message. 
4.Using the picture index, download the image from S3. 
5.For text detection in the image, use the Rekognition client. 
6.From the result, extract the text that was detected. 
7.Examine only those images where the text has been extracted and "Car" has been detected (from Instance A).

Starting the applications:
1.Transfer the Java programs (JAR files) to the instances on EC2.
2.Use SSH to establish a connection with each instance, then open the directory holding the JAR file. 
3.Start the program on Instance A by typing java -jar car-recognition.jar.
Use Instance B to run the application:
Run java -jar text-recognition.jar.
To ensure that messages are transmitted and received, keep an eye on the SQS queue.
After completion, review the results in the output.txt file on Instance B.
