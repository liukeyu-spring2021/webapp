package com.cloud.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Book {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String published_date;
    private String book_created;
    private String userId;
    @OneToMany
    private List<FileInfo> book_images;
//    @OneToMany
//    private List<CategoryInfo> categories;
//    @OneToMany
//    private List<AnswerInfo> answers;

}
