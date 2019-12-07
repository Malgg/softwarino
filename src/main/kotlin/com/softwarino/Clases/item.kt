package com.softwarino.Clases

import javax.persistence.*

@Entity
@Table(name = "item")
class item (
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        val id_item: Long? = null,
        val name: String,
        val tipo: String,
        val atributo: Int
)