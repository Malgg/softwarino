package com.softwarino.Interface
import com.softwarino.Clases.item
import org.springframework.data.repository.CrudRepository

interface Iitem : CrudRepository<item, Long>