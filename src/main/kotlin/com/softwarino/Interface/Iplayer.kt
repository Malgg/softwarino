package com.softwarino.Interface
import com.softwarino.Clases.player
import org.springframework.data.repository.CrudRepository

interface Iplayer : CrudRepository<player, Long>