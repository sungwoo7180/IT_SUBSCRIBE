package com.sw.journal.journalcrawlerpublisher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sw.journal.journalcrawlerpublisher.domain.Category;
import com.sw.journal.journalcrawlerpublisher.domain.Article;
import com.sw.journal.journalcrawlerpublisher.domain.Tag;
import com.sw.journal.journalcrawlerpublisher.domain.Image;

import com.sw.journal.journalcrawlerpublisher.service.ImageService;
import com.sw.journal.journalcrawlerpublisher.service.TagService;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
// 기사를 우리 사이트에 게시할 때 필요한 데이터를 전송하기 위한 DTO
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ArticleWithTagsDTO {
    private Long id; // 기사 id
    private String title; // 기사 제목
    private String content; // 기사 내용
    private LocalDateTime postDate; // 기사
    private Category category; // 기사 카테고리
    private String source; // 기사 출처
    private List<Tag> tags; // 기사 태그
    private List<String> imgUrls; // 기사 이미지 URL 리스트

    // 서비스에서 태그와 이미지를 가져와서 DTO 를 생성
    public static ArticleWithTagsDTO from(Article article, TagService tagService, ImageService imageService) {
        List<Tag> tags = tagService.findByArticle(article);
        List<String> imgUrls = imageService.findByArticle(article).stream()
                .map(Image::getImgUrl)
                .collect(Collectors.toList());
        return from(article, tags, imgUrls);
    }

    // 이미 태그와 이미지가 있을 때 DTO 를 생성
    public static ArticleWithTagsDTO from(Article article, List<Tag> tags, List<String> imgUrls) {
        ArticleWithTagsDTO dto = new ArticleWithTagsDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setPostDate(article.getPostDate());
        dto.setCategory(article.getCategory());
        dto.setSource(article.getSource());
        dto.setTags(tags);
        dto.setImgUrls(imgUrls);
        return dto;
    }
}
