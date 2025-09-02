package com.example.instagram.user.domain;

import com.example.instagram.user.constant.UserErrorConstant;
import com.example.instagram.user.exception.NotAgreedRequireAgreement;

import java.util.Arrays;
import java.util.List;

public enum AgreementType {

    TERMS_OF_SERVICE("이용약관", true),
    DATA_POLICY("데이터정책", true),
    LOCATION_BASED_SERVICE("위치기반 기능", true);

    private final String description;
    private final boolean required;
    
    AgreementType(String description, boolean required) {
        this.description = description;
        this.required = required;
    }
    
    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public static AgreementType getAgreementType(String type){
        return Arrays.stream(AgreementType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow();
    }

    public static List<AgreementType> getRequiredTypes() {
        return Arrays.stream(values())
                .filter(AgreementType::isRequired)
                .toList();
    }

    public static void validateRequiredAgreements(List<AgreementType> agreements) {
        List<AgreementType> requiredTypes = AgreementType.getRequiredTypes();

        List<AgreementType> notAgreed = requiredTypes.stream()
                .filter(type -> !agreements.contains(type))
                .toList();

        if(!notAgreed.isEmpty()){
            throw new NotAgreedRequireAgreement(notAgreed);
        }
    }


    public static boolean exists(String value) {
        return Arrays.stream(AgreementType.values())
                .anyMatch(type -> type.name().equalsIgnoreCase(value));
    }
}