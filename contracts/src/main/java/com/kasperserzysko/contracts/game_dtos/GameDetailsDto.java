package com.kasperserzysko.contracts.game_dtos;

import com.kasperserzysko.data.models.enums.Genre;
import com.kasperserzysko.data.models.enums.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class GameDetailsDto implements Serializable {

    @NotNull(message = "Field: title can't be null")
    @NotBlank(message = "Field: title can't be blank")
    private String title;

    private String description;

    private String releaseDate;

    @NotNull(message = "Field: price can't be null")
    private float price;

    private Set<Tag> tags;

    @NotNull(message = "Field: genres can't be null")
    private Set<Genre> genres;
}
