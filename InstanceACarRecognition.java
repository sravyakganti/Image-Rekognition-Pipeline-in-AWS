import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
public class InstanceACarRecognition {

    private static final String S3_BUCKET_NAME = "njit-cs-643";
    private static final String SQS_QUEUE_URL = "carsinformation.fifo";
    private static final float MIN_CONFIDENCE = 90f;

    public static void main(String[] args) {
        // Initialize clients
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().build();
        AmazonSQS sqsClient = AmazonSQSClientBuilder.standard().build();

        // List and process images
        List<String> imageNames = listImagesFromS3(s3Client);
        processImages(rekognitionClient, sqsClient, imageNames);

        // Send completion signal
        sendCompletionMessage(sqsClient);
    }

    private static List<String> listImagesFromS3(AmazonS3 s3Client) {
        // Implement your code to list image names from the S3 bucket
        // ... Replace with your implementation
        return listOfImages; // Replace with your list of image names
    }

    private static void processImages(AmazonRekognition rekognitionClient, AmazonSQS sqsClient, List<String> imageNames) {
        for (String imageName : imageNames) {
            if (detectCar(rekognitionClient, imageName)) {
                sendMessageToSQS(sqsClient, imageName);
                break; // Only send the first image with a car detected
            }
        }
    }

    private static boolean detectCar(AmazonRekognition rekognitionClient, String imageName) {
        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new com.amazonaws.services.rekognition.model.Image()
                        .withS3Object(new com.amazonaws.services.rekognition.model.S3Object()
                                .withBucket(S3_BUCKET_NAME)
                                .withName(imageName)))
                .withMinConfidence(MIN_CONFIDENCE);

        DetectLabelsResult result = rekognitionClient.detectLabels(request);
        for (Label label : result.getLabels()) {
            if (label.getName().equals("Car")) {
                return true;
            }
        }
        return false;
    }

    private static void sendMessageToSQS(AmazonSQS sqsClient, String message) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(SQS_QUEUE_URL)
                .withMessageBody(message);
        sqsClient.sendMessage(sendMessageRequest);
    }

    private static void sendCompletionMessage(AmazonSQS sqsClient) {
        SendMessageRequest completionMessage = new SendMessageRequest()
                .withQueueUrl(SQS_QUEUE_URL)
                .withMessageBody("-1");
        sqsClient.sendMessage(completionMessage);
    }
}
