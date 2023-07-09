package com.asusoftware.socialapp.post.model;

import com.asusoftware.socialapp.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Post")
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "description", nullable = false, length = 30)
    private String description;

   // private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

   // @OneToMany(mappedBy="post")
   // private List<User> likes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relationship with post likes
    @ManyToMany
    @JoinTable(
            name = "user_post_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<User> userLikes = new HashSet<>();

}
