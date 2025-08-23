package com.it355pz2.entity.converter;

import com.it355pz2.entity.enums.ProficiencyLevel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ProficiencyLevelConverter implements AttributeConverter<ProficiencyLevel, String> {

    @Override
    public String convertToDatabaseColumn(ProficiencyLevel proficiencyLevel) {
        if (proficiencyLevel == null) {
            return null;
        }
        return proficiencyLevel.getLevel(); // Use the lowercase string value
    }

    @Override
    public ProficiencyLevel convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return ProficiencyLevel.BEGINNER; // Default value
        }
        return ProficiencyLevel.fromString(dbData); // Use our custom fromString method
    }
}
