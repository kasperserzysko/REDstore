package com.kasperserzysko.data.repositories.specifications;

import com.kasperserzysko.data.models.Game;
import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
public class GameSpecification {

    private final static DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static Specification<Game> priceLessOrEqualsThan(float price){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
    }
    public static Specification<Game> priceMoreOrEqualsThan(float price){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price);
    }
    public static Specification<Game> titleLike(String title){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }
    public static Specification<Game> releaseAfter(String date){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), LocalDate.parse(date, europeanDateFormatter));
    }
    public static Specification<Game> releaseBefore(String date){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("releaseDate"), LocalDate.parse(date, europeanDateFormatter));
    }
    public static Specification<Game> inTags(Tag[] tags){
        return (root, query, criteriaBuilder) ->
         root.join("tags").in(tags);
    }
    public static Specification<Game> inGenres(Genre[] genres){
        return (root, query, criteriaBuilder) ->
                root.join("genres").in(genres);
    }
}
