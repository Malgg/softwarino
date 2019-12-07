package com.softwarino.Interface
import com.softwarino.Clases.person
import org.springframework.data.repository.CrudRepository

interface Iperson : CrudRepository<person, Long>