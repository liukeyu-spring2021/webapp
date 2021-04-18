Webapp is a Books Management System (BMS) web application, which includes:
- create/update/delete users with user information(name, email, password, etc)
- authorized users are able to create/update/delete books and images of books
- users will receive emails if the books under their name are created, updated or deleted
- use DynamoDB to store the message published on SNS and sent to users by SES to avoid sending duplicate emails
- auto scaling groups to scale out/in according to CPU utilization
- connections between client and load balancer by AWS certificate manager with certification from CA / EC2 and rds are secured by AWS certificate manager with certification from AWS
- All components with contains sensitive date like EBS and RDS are encrypted by key in AWS KMS
- use https to visit domain(https://(prod/dev).6225csyekeyuliu.me)
- Webapp and lambda function will create log/metric in cloudwatch

The webapp can:
- trigger ci to build and test the application
- deploy webapp automatically on aws using codedeploy
- do load test using JMeter

Endpoints:
https://app.swaggerhub.com/apis-docs/csye6225/spring2021/assignment-04

Related repo:
- [infrastructure](https://github.com/liukeyu-spring2021/prod.git)
- [serverless](https://github.com/liukeyu-spring2021/serverless.git)
- [ami](https://github.com/liukeyu-spring2021/ami)
- [each version](https://github.com/KeyuLiu-NEU/webapp-1.git)

secrets setting:
- `cicd` user in prod or dev account
