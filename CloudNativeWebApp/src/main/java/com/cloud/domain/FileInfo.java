package com.cloud.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
public class FileInfo {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String file_id;
    private String file_name;
    private String s3_object_name;
    private String created_date;
    private String user_id;
    private String url;
    private String upload_date;
    private String fileContentType;
    private String lastAccessTime;
    private String lastModifiedTime;
    private Long size;
    private String Md5Hash;
}
