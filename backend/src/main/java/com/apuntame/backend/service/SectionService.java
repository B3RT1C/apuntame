package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.Section;
import com.apuntame.backend.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section createSection(Section section) {
        validateSection(section);
        return sectionRepository.save(section);
    }

    public Section getSectionById(Integer id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.SECTION_NOT_FOUND, id)));
    }

    public Section updateSection(Integer id, Section sectionDetails) {
        Section section = getSectionById(id);

        if (sectionDetails.getName() != null && !sectionDetails.getName().trim().isEmpty()) {
            section.setName(sectionDetails.getName());
        }

        return sectionRepository.save(section);
    }

    public void deleteSection(Integer id) {
        if (!sectionRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.SECTION_NOT_FOUND, id));
        }
        sectionRepository.deleteById(id);
    }

    private void validateSection(Section section) {
        if (section.getName() == null || section.getName().trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessages.SECTION_NAME_EMPTY);
        }
    }
}
