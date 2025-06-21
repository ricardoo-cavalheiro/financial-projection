package com.example.financial_app.converters;

import com.example.financial_app.domain.enums.PaymentTypeEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentTypeEnumConverter implements AttributeConverter<PaymentTypeEnum, String>  {
  @Override
  public String convertToDatabaseColumn(PaymentTypeEnum attribute) {
    return attribute == null ? null : attribute.toString();
  }

  @Override
  public PaymentTypeEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    for (PaymentTypeEnum type : PaymentTypeEnum.values()) {
        if (type.toString().equals(dbData)) return type;
    }
    throw new IllegalArgumentException("Unknown value: " + dbData);
  }
}
