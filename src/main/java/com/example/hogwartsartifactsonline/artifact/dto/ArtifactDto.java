package com.example.hogwartsartifactsonline.artifact.dto;

import com.example.hogwartsartifactsonline.wizard.dto.WizardDto;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record ArtifactDto(
        String id,
        @NotEmpty(message = "name is required.")
        String name,
        @NotEmpty(message = "description is required.")
        String description,
        @NotEmpty(message = "imageUrl is required.")
        String imageUrl,
        WizardDto owner
) {

}
