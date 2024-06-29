import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import java.util.List;
public class InstanceBTextRecongition {

    private static final String S3_BUCKET_NAME = "your-s3-bucket-name"; // Replace with your actual bucket name
    private static final String SQS_QUEUE_URL = "your-sqs-queue-url"; // Replace with your actual queue URL

    public static void main(String[] args) {
        // Initialize clients
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().build();
        AmazonSQS sqsClient = AmazonSQSClientBuilder.standard().build();

        // Main processing loop
        while (true) {
            String messageBody = receiveMessageFromSQS(sqsClient);
            if (messageBody.equals("-1")) {
                break; // Completion signal received
            }
            processImage(s3Client, rekognitionClient, messageBody);
        }
    }

    private static String receiveMessageFromSQS(AmazonSQS sqsClient) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(SQS_QUEUE_URL)
                .withMaxNumberOfMessages(1);

        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).getMessages();
        if (messages.isEmpty()) {
            return null; // No messages received
        }
        return messages.get(0).getBody();
    }

    private static void processImage(AmazonS3 s3Client, AmazonRekognition rekognitionClient, String messageBody) {
        // Download the image from S3
        // Implement your code to download the image using S3

        // Perform text recognition using Rekognition
        DetectTextRequest textRequest = new DetectTextRequest()
                .withImage(new com.amazonaws.services.rekognition.model.Image()
                        .withS3Object(new com.amazonaws.services.rekognition.model.S3Object()
                                .withBucket(S3_BUCKET_NAME)
                                .withName(messageBody)));

        DetectTextResult textResult = rekognitionClient.detectText(textRequest);

        // Analyze and print recognized text
        for (com.amazonaws.services.rekognition.model.TextDetection textDetection : textResult.getTextDetections()) {
            String recognizedText = textDetection.getDetectedText();
            System.out.println("Image Index: " + messageBody + ", Recognized Text: " + recognizedText);
        }
    }
}
