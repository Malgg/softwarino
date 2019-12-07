package com.softwarino.Clases

import javax.persistence.*

@Entity
@Table(name = "inventory")
class inventory (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id_inventory : Long? = null,
    val slot: Int,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "person_id", referencedColumnName = "id_person")
    val person_id: person,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "item_id", referencedColumnName = "id_item")
    val item_id: item
)