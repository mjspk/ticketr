package com.ensf614.ticketr.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component("user")

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String phone;
    private String password;
    private Card defaultCard;
    private Set<Role> roles = new HashSet<>();
    private String[] rolStrings;

    public User() {

    }

    public User(int id, String firstName, String lastName, String email, String address, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.password = password;
        rolStrings = new String[] {};
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;

        rolStrings = new String[roles.size()];
        int i = 0;
        for (Role role : roles) {
            rolStrings[i] = role.getName();
            i++;
        }

    }

    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public Card getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(Card defaultCard) {
        this.defaultCard = defaultCard;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = this.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public String[] getRolStrings() {
        return rolStrings;
    }

    public void setRolStrings(String[] rolStrings) {
        this.rolStrings = rolStrings;
    }

    public String getRolesString() {
        String rolesString = "";
        for (String role : rolStrings) {
            rolesString += role + ", ";
        }
        return rolesString;
    }

}