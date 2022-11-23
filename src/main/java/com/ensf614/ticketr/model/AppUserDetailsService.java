package com.ensf614.ticketr.model;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;

import com.ensf614.ticketr.data.DataStore;

public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private DataStore dataStore;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = dataStore.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with that email");
        }

        return new AppUserDetails(user);
    }
}