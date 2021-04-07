package com.cloud;

import com.cloud.config.FileStorageProperties;
import com.cloud.config.DataSourceConfig;
import com.cloud.service.FileStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SNSClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SNSException;
import software.amazon.awssdk.services.sqs.SQSClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.auth.InstanceProfileCredentialsProvider;
import com.cloud.config.pathVariableConfig;
import java.util.List;

import java.io.IOException;


@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class CloudNativeWebAppApplication {

	public static void main(String[] args) throws IOException {
		DataSourceConfig.username = "root";
		DataSourceConfig.password = "liukeyu521";
		DataSourceConfig.hostname = "csye6225-f20.cbcz4zpbrrbg.us-east-1.rds.amazonaws.com";
		FileStorageService.S3_BUCKET_NAME ="webapps32";
		FileStorageService.region = Region.US_EAST_1;
		pathVariableConfig.queueUrl= "https://sqs.us-east-1.amazonaws.com/359410113455/myQueue.fifo";
		pathVariableConfig.region= Region.US_EAST_2;
		pathVariableConfig.topicArn = "arn:aws:sns:us-east-1:359410113455:myTopic.fifo";

		Runnable r = () -> {
			SQSClient sqsClient = SQSClient.builder()
					.credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
					.region(pathVariableConfig.region)
					.build();
			SNSClient snsClient = SNSClient.builder()
					.credentialsProvider(InstanceProfileCredentialsProvider.builder().build())
					.region(pathVariableConfig.region)
					.build();
			while(true){
				try {
					ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
							.queueUrl(pathVariableConfig.queueUrl)
							.maxNumberOfMessages(10)
							.visibilityTimeout(30)
							.waitTimeSeconds(0)
							.build();
					List<Message> messages= sqsClient.receiveMessage(receiveMessageRequest).messages();
					if(messages.isEmpty()) continue;

					for(Message message:messages){
						//publish messages to SNS topic
						PublishRequest request = PublishRequest.builder()
								.message(message.body())
								.topicArn(pathVariableConfig.topicArn)
								.build();

						PublishResponse result = snsClient.publish(request);
						//System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

					}

					for (Message message : messages) {
						DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
								.queueUrl(pathVariableConfig.queueUrl)
								.receiptHandle(message.receiptHandle())
								.build();
						sqsClient.deleteMessage(deleteMessageRequest);
					}

				} catch (SNSException e) {
					System.err.println(e);
					//System.exit(1);
				} catch (SQSException e) {
					try {
						Thread.sleep(3000);
					}
					catch (Exception a) {

					}
				}

			}
		};

		new Thread(r).start();
		SpringApplication.run(CloudNativeWebAppApplication.class, args);
	}


}
