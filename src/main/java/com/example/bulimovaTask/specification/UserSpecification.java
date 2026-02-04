package com.example.bulimovaTask.specification;

import com.example.bulimovaTask.entity.Role;
import com.example.bulimovaTask.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserSpecification {
    public static Specification<User> hasId(Long id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<User> usernameLike(String username) {
        return (root, query, cb) ->
                username == null ? null :
                        cb.like(cb.lower(root.get("username")),
                                "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> emailLike(String email) {
        return (root, query, cb) ->
                email == null ? null :
                        cb.like(cb.lower(root.get("email")),
                                "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(Role role) {
        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("role"), role);
    }

    public static Specification<User> createdFrom(LocalDateTime from) {
        return (root, query, cb) ->
                from == null ? null :
                        cb.greaterThanOrEqualTo(root.get("createDate"), from);
    }

    public static Specification<User> createdTo(LocalDateTime to) {
        return (root, query, cb) ->
                to == null ? null :
                        cb.lessThanOrEqualTo(root.get("createDate"), to);
    }

    public static Specification<User> updatedFrom(LocalDateTime from) {
        return (root, query, cb) ->
                from == null ? null :
                        cb.greaterThanOrEqualTo(root.get("updateDate"), from);
    }

    public static Specification<User> updatedTo(LocalDateTime to) {
        return (root, query, cb) ->
                to == null ? null :
                        cb.lessThanOrEqualTo(root.get("updateDate"), to);
    }
}
