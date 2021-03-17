package com.cloud.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import javax.persistence.ElementCollection;
import java.util.List;

@Data
public class BookInfo {
    private String title;
    private String author;
    private String isbn;
    private String published_date;
//    @ElementCollection
//    private List<CategoryInfo> categories;
}
