package com.example.DOTORY.post.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    private String title;
    private String content;
    private List<String> imageUrls;


}
