package com.softwarino.Clases

import javax.persistence.*

@Entity
@Table(name = "person")
class person (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id_person: Long? = null,
        val health: Int,
        val armor: Int,
        val damage: Int,
        val oro: Int,
        val hunger: Int,
        val thirst: Int
)