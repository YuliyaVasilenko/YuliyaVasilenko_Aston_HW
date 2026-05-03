package com.example.user_service.assembler;

import com.example.user_service.controller.UserController;
import com.example.user_service.dto.UserDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * @author YuliyaVasilenko
 * @version 1.0.0
 * Date 26-04-2026
 * Description: this class binds HAL-links to the UserDTO model based on UserController methods
 */
@Component
public class UserControllerAssembler implements RepresentationModelAssembler<UserDTO, EntityModel<UserDTO>> {

    /**
     * @ Method Name: toModel
     * @ Description: creates links for userDTO based on UserController methods
     * @ param      : [com.example.user_service.dto.UserDTO]
     * @ return     : org.springframework.hateoas.EntityModel<com.example.user_service.dto.UserDTO>
     */
    @Override
    public EntityModel<UserDTO> toModel(UserDTO userDTO) {
        Link selfLink = linkTo(methodOn(UserController.class).getUserById(userDTO.getId())).withSelfRel();

        Link allUsersLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");

        Link updateLink = linkTo(methodOn(UserController.class).updateUser(userDTO.getId(), userDTO)).withRel("update");

        Link deleteLink = linkTo(methodOn(UserController.class).deleteUser(userDTO.getId())).withRel("delete");

        return EntityModel.of(userDTO, selfLink, allUsersLink, updateLink, deleteLink);
    }

    /**
     * @ Method Name: toCollectionModel
     * @ Description: creates links for List<EntityModel<UserDTO>> based on UserController methods
     * @ param      : [java.util.List<org.springframework.hateoas.EntityModel<com.example.user_service.dto.UserDTO>>]
     * @ return     : org.springframework.hateoas.CollectionModel<org.springframework
     * .hateoas.EntityModel<com.example.user_service.dto.UserDTO>>
     */
    public CollectionModel<EntityModel<UserDTO>> toCollectionModel(List<EntityModel<UserDTO>> userResources) {
        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();

        Link createLink = linkTo(methodOn(UserController.class).createUser(new UserDTO())).withRel("create");

        return CollectionModel.of(userResources, selfLink, createLink);
    }
}
