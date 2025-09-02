package com.example.instagram.user.exception;

import com.example.instagram.common.exception.GlobalException;
import com.example.instagram.user.constant.UserErrorConstant;
import com.example.instagram.user.domain.AgreementType;
import lombok.Getter;

import java.util.List;

@Getter
public class NotAgreedRequireAgreement extends GlobalException {
    
    private final List<AgreementType> notAgreedTypes;
    
    public NotAgreedRequireAgreement(List<AgreementType> notAgreedTypes) {
        super(UserErrorConstant.NOT_AGREED);
        this.notAgreedTypes = notAgreedTypes;
    }
}
