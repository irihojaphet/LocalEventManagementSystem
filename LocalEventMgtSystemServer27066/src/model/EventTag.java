package model;

import java.io.Serializable;
import java.util.Set;
import jakarta.persistence.*;

/**
 * EventTag entity for Many-to-Many relationship with Events
 * 
 * @author 27066
 */
@Entity
@Table(name = "event_tags")
public class EventTag implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private int tagId;
    
    @Column(name = "tag_name", nullable = false, unique = true, length = 50)
    private String tagName;
    
    @Column(name = "description", length = 200)
    private String description;
    
    // Many-to-Many relationship with Events
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Event> events;

    public EventTag() {}

    public EventTag(String tagName, String description) {
        this.tagName = tagName;
        this.description = description;
    }

    // Getters and setters
    public int getTagId() { return tagId; }
    public void setTagId(int tagId) { this.tagId = tagId; }
    
    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Set<Event> getEvents() { return events; }
    public void setEvents(Set<Event> events) { this.events = events; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventTag eventTag = (EventTag) o;
        return tagId == eventTag.tagId;
    }
    
    @Override
    public int hashCode() {
        return tagId;
    }
}

