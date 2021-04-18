Build and Deploy instructions:

1. Open project by IntelliJ IDEA or other IDEs
2. Create Database in MySQL
3. Modify the application.propoties with your database info
4. Run the application


Prerequisites:

1. Install Java
2. Install MySql
3. ./mvnw spring-boot:run
4. ./mvnw :install


Stack used:

- Java
- Spring-boot
- awssdk package
- Maven

Components on AWS used:
- EC2 Instances, Security Groups, AMI, Auto Scaling, Load balaner
- Rds
- DynamoDB
- S3
- CloudWatch
- VPC
- CodeDeploy
- Route53
- Lambda
- SNS
- SES
- Certificate Manager

Tools:
- Github actions
- JMeter





- ![Overall Design Diagram](https://github.com/liukeyu-spring2021/webapp/tree/main/doc/Diagram.png?raw=true "Title")

See more details:

- Design:  [doc/design.md](https://github.com/liukeyu-spring2021/webapp/tree/main/doc/design.md)
- Version: [doc/version.md](https://github.com/liukeyu-spring2021/webapp/tree/main/doc/version.md)
- Command: [doc/command.md](https://github.com/liukeyu-spring2021/webapp/tree/maindoc/command.md)