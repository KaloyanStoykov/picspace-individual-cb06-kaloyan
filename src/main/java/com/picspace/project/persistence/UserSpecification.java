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
        return (Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserEntity> subRoot = subquery.from(UserEntity.class);
            Join<UserEntity, RoleEntity> subRolesJoin = subRoot.join("roles");
            subquery.select(subRoot.get("id")) // Assuming 'id' is the primary key of UserEntity
                    .where(cb.equal(subRolesJoin.get("name"), "ROLE_ADMIN"));


            return cb.not(root.get("id").in(subquery));
        };
    }




}
