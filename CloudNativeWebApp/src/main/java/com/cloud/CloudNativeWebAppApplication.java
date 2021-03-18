package com.cloud;

import com.cloud.config.FileStorageProperties;
import com.cloud.config.DataSourceConfig;
import com.cloud.service.FileStorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import software.amazon.awssdk.regions.Region;


import java.io.IOException;


@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class CloudNativeWebAppApplication {

	public static void main(String[] args) throws IOException {
		DataSourceConfig.username = System.getenv("user");
		DataSourceConfig.password = System.getenv("password");
		DataSourceConfig.hostname = System.getenv("host");
		//csye6225-f20.cbcz4zpbrrbg.us-east-1.rds.amazonaws.com
		FileStorageService.S3_BUCKET_NAME =System.getenv("bucketname");
		FileStorageService.region = Region.US_EAST_1;
		SpringApplication.run(CloudNativeWebAppApplication.class, args);
	}


}
