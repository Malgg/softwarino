package com.softwarino.Interface
import com.softwarino.Clases.inventory
import org.springframework.data.repository.CrudRepository

interface Iinventory : CrudRepository<inventory, Long>