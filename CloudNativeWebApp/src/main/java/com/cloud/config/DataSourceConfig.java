
package com.cloud.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
@Configuration
public class DataSourceConfig {
    public String username = "root";
    public String password = "liukeyu521";
    public String hostname = "csye6225-f20.cbcz4zpbrrbg.us-east-1.rds.amazonaws.com";

    public static String Username;
    public static String Password;
    public static String Hostname;
    @Bean
    public DataSource dataSource() {
        String url="jdbc:mysql://"+hostname+":3306/"+"db_cloud?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true";

        //jdbc:mysql://${MYSQL_HOST:csye6225-spring2020.c6ldbcss2x5a.us-east-1.rds.amazonaws.com}:3306/db_cloud?createDatabaseIfNotExist=true
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        //dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
//        dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}
