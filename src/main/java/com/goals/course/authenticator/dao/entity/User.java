package com.goals.course.authenticator.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Table("users")
public class User implements UserDetails {
    @Id
    @Column
    private UUID id;

    @Column
    private String username;

    @Column
    @JsonIgnore
    private String password;

    @Column
    @JsonIgnore
    private String firstName;

    @Column
    @JsonIgnore
    private String lastName;

    @Column
    private boolean enabled;

    @Column
    private List<Role> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    @SuppressWarnings("java:S4144")
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    @SuppressWarnings("java:S4144")
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

}
