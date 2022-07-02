package com.goals.course.authenticator.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Data
@Accessors(chain = true)
@Table("roles")
public class Role implements GrantedAuthority {

    @Id
    @Column
    private UUID id;

    @Column
    private String title;

    @Override
    public String getAuthority() {
        return title;
    }
}
