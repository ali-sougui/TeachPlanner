package org.univ.projet_tutore.teachPlanner.controller;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.univ.projet_tutore.teachPlanner.model.TypeSalle;

@Converter(autoApply = true)
public class TypeSalleConverter implements AttributeConverter<TypeSalle, String> {

    @Override
    public String convertToDatabaseColumn(TypeSalle typeSalle) {
        if (typeSalle == null) {
            return null;
        }
        return typeSalle.name();
    }

    @Override
    public TypeSalle convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return TypeSalle.valueOf(dbData);
    }
}
