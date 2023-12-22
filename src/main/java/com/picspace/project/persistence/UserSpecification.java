package com.picspace.project.persistence;

import com.picspace.project.domain.FilterDTO;
import com.picspace.project.domain.User;
import com.picspace.project.persistence.entity.RoleEntity;
import com.picspace.project.persistence.entity.UserEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserSpecification {
    public static Specification<UserEntity> columnEqual(List<FilterDTO> filterDTOList ){
        return new Specification<UserEntity>() {
            @Override
            public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = filterDTOList.stream().map(filter -> criteriaBuilder.equal(root.get(filter.getColumnName()), filter.getColumnValue())).collect(Collectors.toList());
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
    public static Specification<UserEntity> excludeAdmins() {
        return new Specification<UserEntity>() {
            @Override
            public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Join<UserEntity, RoleEntity> rolesJoin = root.join("roles");
                return criteriaBuilder.notEqual(rolesJoin.get("name"), "ROLE_ADMIN");
            }
        };
    }




}
