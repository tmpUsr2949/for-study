package com.example.hogwartsartifactsonline.wizard;

import com.example.hogwartsartifactsonline.artifact.Artifact;
import com.example.hogwartsartifactsonline.artifact.ArtifactRepository;
import com.example.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard findById(Integer wizardId) {
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
    }

    public Wizard save(Wizard newWizard) {
        return this.wizardRepository.save(newWizard);
    }

    // We are not updating a wizard's artifacts through this method, we only update their name.
    public Wizard update(Integer wizardId, Wizard update) {
        return this.wizardRepository.findById(wizardId)
                .map(oldWizard -> {
                    oldWizard.setName(update.getName());
                    return this.wizardRepository.save(oldWizard);
                })
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
    }

    public void delete(Integer wizardId) {
        Wizard wizardToBeDeleted = this.wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));

        // Before deletion, we will unassign this wizard's owned artifacts.
        wizardToBeDeleted.removeAllArtifacts();
        this.wizardRepository.deleteById(wizardId);
    }

    public void assignArtifact(Integer wizardId, String artifactId) {
        // Find this artifact by id from DB.
        Artifact artifactToBeAssigned = artifactRepository
                .findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));

        // Find this wizard by id from DB.
        Wizard wizard = wizardRepository
                .findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));

        // Artifact assignment
        // We need to see if the artifact is already owned by some wizard.
        if (artifactToBeAssigned.getOwner() != null) {
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }

        wizard.addArtifact(artifactToBeAssigned);
    }

}