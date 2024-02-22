package com.example.hogwartsartifactsonline.artifact;

import com.example.hogwartsartifactsonline.artifact.dto.ArtifactDto;
import com.example.hogwartsartifactsonline.system.StatusCode;
import com.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Turn off Spring Security
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        artifacts = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Artifact a = new Artifact();
            a.setId("125080860174490419" + i);
            a.setName(i + " Invisibility Cloak");
            a.setDescription("[" + i + "] " + "An invisibility cloak is used to make the wearer invisible.");
            a.setImageUrl("ImageUrl");
            artifacts.add(a);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindArtifactByIdSuccess() throws Exception {
        // Given
        given(artifactService.findById("1250808601744904190")).willReturn(artifacts.get(0));

        // When and then
        mockMvc.perform(get(baseUrl + "/artifacts/1250808601744904190").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904190"))
                .andExpect(jsonPath("$.data.name").value("0 Invisibility Cloak"));
    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {
        // Given
        given(artifactService.findById("1250808601744904190"))
                .willThrow(new ObjectNotFoundException("artifact", "1250808601744904190"));

        // When and then
        mockMvc.perform(get(baseUrl + "/artifacts/1250808601744904190").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904190 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {
        // Given
        given(artifactService.findAll()).willReturn(artifacts);

        // When and then
        mockMvc.perform(get(baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1250808601744904190"))
                .andExpect(jsonPath("$.data[0].name").value("0 Invisibility Cloak"))
                .andExpect(jsonPath("$.data[1].id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data[1].name").value("1 Invisibility Cloak"));
    }

    @Test
    void testAddArtifactSuccess() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto(
                null,
                "Remembrall",
                "A Remembrall was a magical large marble-sized glass ball that contained smoke which turned red when its owner or user had forgotten something. It turned clear once whatever was forgotten was remembered.",
                "ImageUrl",
                null
        );

        String json = objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact = new Artifact();
        savedArtifact.setId("1250808601744904197");
        savedArtifact.setName("Remembrall");
        savedArtifact.setDescription("A Remembrall was a magical large marble-sized glass ball that contained smoke which turned red when its owner or user had forgotten something. It turned clear once whatever was forgotten was remembered.");
        savedArtifact.setImageUrl("ImageUrl");

        given(artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);

        // When and then
        mockMvc.perform(post(baseUrl + "/artifacts").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto(
                "1250808601744904192",
                "Invisibility Cloak",
                "A new description.",
                "ImageUrl",
                null
        );

        String json = objectMapper.writeValueAsString(artifactDto);

        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setId("1250808601744904192");
        updatedArtifact.setName("Invisibility Cloak");
        updatedArtifact.setDescription("A new description.");
        updatedArtifact.setImageUrl("ImageUrl");

        given(artifactService.update(eq("1250808601744904192"), Mockito.any(Artifact.class))).willReturn(updatedArtifact);

        // When and then
        mockMvc.perform(put(baseUrl + "/artifacts/1250808601744904192").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactErrorWithNonExistentId() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto(
                "1250808601744904192",
                "Invisibility Cloak",
                "A new description.",
                "ImageUrl",
                null
        );

        String json = objectMapper.writeValueAsString(artifactDto);

        given(artifactService.update(eq("1250808601744904192"), Mockito.any(Artifact.class))).willThrow(new ObjectNotFoundException("artifact", "1250808601744904192"));

        // When and then
        mockMvc.perform(put(baseUrl + "/artifacts/1250808601744904192").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904192 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactSuccess() throws Exception {
        // Given
        doNothing().when(artifactService).delete("1250808601744904191");

        // When and then
        mockMvc.perform(delete(baseUrl + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("artifact", "1250808601744904191")).when(artifactService).delete("1250808601744904191");

        // When and then
        mockMvc.perform(delete(baseUrl + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}