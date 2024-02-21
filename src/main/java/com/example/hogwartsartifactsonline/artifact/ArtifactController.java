package com.example.hogwartsartifactsonline.artifact;

import com.example.hogwartsartifactsonline.artifact.converter.ArtifactDtoToArtifactConverter;
import com.example.hogwartsartifactsonline.artifact.converter.ArtifactToArtifactDtoConverter;
import com.example.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import com.example.hogwartsartifactsonline.system.Result;
import com.example.hogwartsartifactsonline.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;
    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    public ArtifactController(ArtifactService artifactService, ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter, ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    }

    @GetMapping
    public Result findAllArtifacts() {
        List<Artifact> foundArtifacts = artifactService.findAll();
        List<ArtifactDto> artifactDtos = foundArtifacts.stream()
                .map(artifactToArtifactDtoConverter::convert)
                .collect(Collectors.toList());

        return new Result(true, StatusCode.SUCCESS, "Find All Success", artifactDtos);
    }

    @PostMapping
    public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
        Artifact newArtifact = artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact savedArtifact = artifactService.save(newArtifact);
        ArtifactDto savedArtifactDto = artifactToArtifactDtoConverter.convert(savedArtifact);

        return new Result(true, StatusCode.SUCCESS, "Add Success", savedArtifactDto);
    }

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId) {
        Artifact foundArtifact = artifactService.findById(artifactId);
        ArtifactDto artifactDto = artifactToArtifactDtoConverter.convert(foundArtifact);

        return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDto);
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifact(
            @PathVariable String artifactId,
            @Valid @RequestBody ArtifactDto artifactDto
    ) {
        Artifact update = artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact updatedArtifact = artifactService.update(artifactId, update);
        ArtifactDto updatedArtifactDto = artifactToArtifactDtoConverter.convert(updatedArtifact);

        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedArtifactDto);
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable String artifactId) {
        artifactService.delete(artifactId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
