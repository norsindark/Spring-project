//package com.medimarket.api.users;
//
//import com.medimarket.api.advice.UniqueEmail;
//import com.medimarket.api.users.UserRepository;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public boolean isValid(String email, ConstraintValidatorContext context) {
//        return !userRepository.existsByEmail(email);
//    }
//}