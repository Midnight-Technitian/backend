package dev.glabay.features.user;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-30
 */
public class CustomUserDetails implements UserDetails {

    private final UserProfile userProfile;

    private final SimpleGrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");
    private final SimpleGrantedAuthority ROLE_MANAGER = new SimpleGrantedAuthority("ROLE_MANAGEMENT");
    private final SimpleGrantedAuthority ROLE_DEVELOPER = new SimpleGrantedAuthority("ROLE_DEVELOPER");
    private final SimpleGrantedAuthority ROLE_ADMINISTRATOR = new SimpleGrantedAuthority("ROLE_ADMIN");

    public CustomUserDetails(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        // Everyone gets the default USER role
        authorities.add(ROLE_USER);

        return authorities;
    }

    @Override
    public String getPassword() {
        return userProfile.getEncryptedPassword();
    }

    @Override
    @NullMarked
    public String getUsername() {
        return userProfile.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
