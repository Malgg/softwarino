package com.softwarino.Clases

import javax.persistence.*

@Entity
@Table(name = "player")
class player (
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id_player : Long? = null,
        val username: String,
        val password: String,
        @OneToOne(cascade = [CascadeType.ALL])
        @JoinColumn(name = "person_id", referencedColumnName = "id_person")
        val person_id: person
)