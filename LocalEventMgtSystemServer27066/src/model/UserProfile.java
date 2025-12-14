package model;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * UserProfile entity for One-to-One relationship with User
 * 
 * @author 27066
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private int profileId;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "preferences", columnDefinition = "TEXT")
    private String preferences;
    
    @Column(name = "notification_enabled")
    private boolean notificationEnabled = true;
    
    @Column(name = "email_notifications")
    private boolean emailNotifications = true;
    
    @Column(name = "sms_notifications")
    private boolean smsNotifications = false;

    public UserProfile() {}

    public UserProfile(User user) {
        this.user = user;
    }

    // Getters and setters
    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
    
    public boolean isNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
    
    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }
    
    public boolean isSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(boolean smsNotifications) { this.smsNotifications = smsNotifications; }
}

