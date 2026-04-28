package com.example.user_service.util;

import com.example.user_service.controller.UserController;
import com.example.user_service.dto.UserDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-04-2026
 * Description: the class that is an assembler for linking HAL-references to UserDTO-responses
 */
@Component
public class UserControllerAssembler implements RepresentationModelAssembler<UserDTO, EntityModel<UserDTO>> {

    @Override
    public EntityModel<UserDTO> toModel(UserDTO entity) {
        return EntityModel.of(entity,
                WebMvcLinkBuilder.linkTo(methodOn(UserController.class).createUser(entity)).withRel("create"),
                linkTo(methodOn(UserController.class).getUserById(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"),
                linkTo(methodOn(UserController.class).updateUser(entity.getId(), entity)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(entity.getId())).withRel("delete")
        );
    }
}
