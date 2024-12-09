package com.adit.backend.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.adit.backend.domain.event.entity.Event;
import com.adit.backend.domain.place.entity.UserPlace;
import com.adit.backend.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String socialType;

    private String socialId;
    private String token;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPlace> userPlaces = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRelationship> sentFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRelationship> receivedFriendRequests = new ArrayList<>();

    // 연관관계 메서드
    public void addEvent(Event event) {
        this.events.add(event);
        event.setUser(this);
    }

    public void addUserPlace(UserPlace userPlace) {
        this.userPlaces.add(userPlace);
        userPlace.setUser(this);
    }

    public void addFriendRequest(FriendRelationship friendRelationship) {
        this.sentFriendRequests.add(friendRelationship);
        friendRelationship.setFromUser(this);
    }
}
