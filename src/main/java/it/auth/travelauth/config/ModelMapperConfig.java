package it.auth.travelauth.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.auth.travelauth.domain.dto.response.UserRegistrationResponseDto;
import it.auth.travelauth.domain.entity.User;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(User.class, UserRegistrationResponseDto.class).addMappings(mapper -> {
            mapper.map(User::getUsername, UserRegistrationResponseDto::setUsername);
            mapper.map(src -> src.getUserProfile().getFirstName(), UserRegistrationResponseDto::setFirstName);
            mapper.map(src -> src.getUserProfile().getLastName(), UserRegistrationResponseDto::setLastName);
            mapper.map(src -> src.getUserProfile().getEmail(), UserRegistrationResponseDto::setEmail);
        });

        return modelMapper;
    }
}