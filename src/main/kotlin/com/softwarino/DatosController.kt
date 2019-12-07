package com.softwarino

import com.softwarino.Clases.*
import com.softwarino.Interface.Iinventory
import com.softwarino.Interface.Iitem
import com.softwarino.Interface.Iperson
import com.softwarino.Interface.Iplayer
import org.apache.jena.rdf.model.ModelFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringWriter
import java.security.MessageDigest
import java.util.stream.Collectors
import javax.xml.crypto.dsig.DigestMethod.SHA256

@Controller
@RequestMapping("/getdata")
class DatosController {
    @Autowired
    lateinit var iInventory: Iinventory
    @Autowired
    lateinit var iItem: Iitem
    @Autowired
    lateinit var iPerson: Iperson
    @Autowired
    lateinit var iPlayer: Iplayer


    @GetMapping("/players")
    fun index(model: Model): String {
        var player = getAllPlayers()
        model.addAttribute("players", player)
        return "players.html"
    }

    @RequestMapping("/player/{id_player}")
    fun detailPlayer(model: Model, @PathVariable("id_player") id_player: Long): String{
        val detailsPlayer = getDetailsPlayer(id_player)
        //val person_id = detailsPlayer.person_id.toString().toLong()
        val detailsPerson = getDetailsPerson(1)
        val listInventory = getPlayerItems(1)
        var m = ModelFactory.createDefaultModel()


        val player = m.createResource("http://purl.org/net/VideoGameOntology#Player")
        val uriPlayer = m.createResource("https://app-softwarinos.herokuapp.com/getdata/player/"+detailsPlayer.id_player, player)

        val username = m.createProperty("http://www.softwarino.cps#username")
        val health = m.createProperty("http://www.softwarino.cps#health")
        val armor = m.createProperty("http://www.softwarino.cps#armor")
        val damage = m.createProperty("http://www.softwarino.cps#damage")
        val oro = m.createProperty("http://www.softwarino.cps#oro")
        val hunger = m.createProperty("http://www.softwarino.cps#hunger")
        val thirst = m.createProperty("http://www.softwarino.cps#thirst")

        val itemType = m.createResource("http://www.softwarino.cps#item")
        val hasItem = m.createProperty("http://www.softwarino.cps#hasItem")

        m.add(uriPlayer, username, detailsPlayer.username)
        m.add(uriPlayer, health, detailsPerson.health.toString())
        m.add(uriPlayer, armor, detailsPerson.armor.toString())
        m.add(uriPlayer, damage, detailsPerson.damage.toString())
        m.add(uriPlayer, oro, detailsPerson.oro.toString())
        m.add(uriPlayer, hunger, detailsPerson.hunger.toString())
        m.add(uriPlayer, thirst, detailsPerson.thirst.toString())


        for(item in listInventory){
            val itemResource = m.createResource("https://app-softwarinos.herokuapp.com/getdata/item/"+item.id_item, itemType)
            val name = m.createProperty("http://www.softwarino.cps#nameItem")
            val tipo = m.createProperty("http://www.softwarino.cps#tipo")
            val atributo = m.createProperty("http://www.softwarino.cps#atributo")

            m.add(itemResource,name,item.name)
            m.add(itemResource,tipo,item.tipo)
            m.add(itemResource,atributo,item.atributo.toString())
            m.add(uriPlayer,hasItem, itemResource)
        }


        val jsonLdWrite = StringWriter()
        m.write(jsonLdWrite, "JSON-LD")
        model.addAttribute("jsonld",jsonLdWrite)
        model.addAttribute("tituloplayer","Información de la Cuenta")
        model.addAttribute("subtituloplayer","Datos (" + detailsPlayer.username.toUpperCase() + ") :")
        model.addAttribute("player",detailsPlayer)
        model.addAttribute("tituloperson","Información del Personaje :")
        model.addAttribute("subtituloperson","Datos del Personaje:")
        model.addAttribute("person",detailsPerson)
        model.addAttribute("subtituloinventory","Inventario :")
        model.addAttribute("inventory",listInventory)

        return "detailsplayer.html"
    }


    @RequestMapping("/item/{id_item}")
    fun detailItem(model: Model, @PathVariable("id_item") id_item: Long): String{
        val detailsItem = getDetailsItem(id_item)
        model.addAttribute("titulo","Información del Item")
        model.addAttribute("subtitulo","Datos :")
        model.addAttribute("item",detailsItem)
        return "detailsitem.html"
    }


    @PostMapping("/players/save")
    fun newUser(model: Model, @ModelAttribute("sendUrl") form: formurl): String{

       val getUrl = form.url
       val getPassword = form.password
       val doc: Document = Jsoup.connect(getUrl).get()
       val json = doc.select("script[type=\"application/ld+json\"]")
       val jsonText = json.stream().map(Element::html).collect(Collectors.toList())
       val jsonString: String = jsonText[0].toString()
       val targetStream: InputStream = ByteArrayInputStream(jsonString.toByteArray())
       val m = ModelFactory.createDefaultModel()
       try {
           m.read(targetStream, "", "JSON-LD")
       } catch (e: Exception) {
           e.toString()
       }

       val player = m.getResource(getUrl)

       val usernameProperty = m.createProperty("http://www.sotomax.cps#emailPlayer")

       val healthProperty = m.createProperty("http://www.sotomax.cps#currentLife")
       val damageProperty = m.createProperty("http://www.sotomax.cps#damage")

       val nameSkillProperty = m.createProperty("http://www.sotomax.cps#nameSkill")
       val propertySkillProperty = m.createProperty("http://www.sotomax.cps#porpertySkill")

       val hasItemProperty = m.createProperty("http://www.sotomax.cps#hasItem")
       val nameItemProperty = m.createProperty("http://www.sotomax.cps#nameItem")
       val propertyItemProperty = m.createProperty("http://www.sotomax.cps#propertyItem")

       val username = player.getProperty(usernameProperty)

       val health = player.getProperty(healthProperty)
       val damage = player.getProperty(damageProperty)
       var newperson = person(health= health.int, armor= 5, damage= damage.int, oro= 0, hunger= 100, thirst= 100)
       iPerson.save(newperson)

       var newplayer = player(username = username.toString(), password = getPassword, person_id = newperson)
       iPlayer.save(newplayer)


       val items = player.listProperties(hasItemProperty)

       for (item in items){
           val nameItem = item.getProperty(nameItemProperty)
           val propertyItem = item.getProperty(propertyItemProperty)
           var newitem = item(name= nameItem.string, tipo= "pocion", atributo = propertyItem.int)
           iItem.save(newitem)
       }

        return "redirect:/getdata/players"
    }




    @ResponseBody
    fun getDetailsPlayer(id_player: Long): player{
        return iPlayer.findById(id_player).get()
    }
    @ResponseBody
    fun getDetailsPerson(id_person: Long): person{
        return iPerson.findById(id_person).get()
    }
    @ResponseBody
    fun getDetailsItem(id_item: Long): item{
        return iItem.findById(id_item).get()
    }
    @ResponseBody
    fun getPlayerItems(id_person: Long): Iterable<item>{
        return iItem.findAll()
    }
    @ResponseBody
    fun getAllPlayers(): Iterable<player>{
        return iPlayer.findAll()
    }

    @GetMapping("/player/jsonPlayer/{id_player}")
    fun getJsonPlayer(model: Model, @PathVariable id_player: Long): String {
        val detailsPlayer = getDetailsPlayer(id_player)
        var m = ModelFactory.createDefaultModel()

        val player = m.createResource("http://purl.org/net/VideoGameOntology#Player")
        val uriPlayer = m.createResource("http://localhost:8081/getdata/player/jsonPlayer/"+detailsPlayer.id_player.toString(), player)
        //playerdetails
        val username = m.createProperty("http://www.softwarino.cps#usernamePlayer")
        val password = m.createProperty("http://www.softwarino.cps#passwordPlayer")
        //persondetails
        val health = m.createProperty("http://www.softwarino.cps#health")
        val armor = m.createProperty("http://www.softwarino.cps#armor")
        val damage = m.createProperty("http://www.softwarino.cps#damage")
        val oro = m.createProperty("http://www.softwarino.cps#oro")
        val hunger = m.createProperty("http://www.softwarino.cps#hunger")
        val thirst = m.createProperty("http://www.softwarino.cps#thirst")

        val itemType = m.createProperty("http://www.softwarino.cps#item")
        val hasItem = m.createProperty("http://www.softwarino.cps#hasItem")

        val personType = m.createProperty("http://www.softwarino.cps#person")
        val hasPerson = m.createProperty("http://www.softwarino.cps#hasPerson")
        uriPlayer.addProperty(username, detailsPlayer.username)
        //m.add(uriPlayer, username, detailsPlayer.username)
        m.add(uriPlayer, password, detailsPlayer.password)
        m.add(uriPlayer, health, detailsPlayer.person_id.health.toString())
        m.add(uriPlayer, armor, detailsPlayer.person_id.armor.toString())
        m.add(uriPlayer, damage, detailsPlayer.person_id.damage.toString())
        m.add(uriPlayer, oro, detailsPlayer.person_id.oro.toString())
        m.add(uriPlayer, hunger, detailsPlayer.person_id.hunger.toString())
        m.add(uriPlayer, thirst, detailsPlayer.person_id.thirst.toString())

        //for(item in detailsPlayer.something.item){
        for(x in 1 until 2){//pone a todos los items xd, arreglar
            val detailsItem=getDetailsItem(x.toLong())
            val itemResource = m.createResource("http://localhost:8081/getdata/item/"+x,itemType)
            val name = m.createProperty("http://www.softwarino.cps#nameItem")
            val tipo = m.createProperty("http://www.softwarino.cps#tipo")
            val atributo = m.createProperty("http://www.softwarino.cps#atributo")

            m.add(itemResource,name,detailsItem.name)
            m.add(itemResource,tipo,detailsItem.tipo)
            m.add(itemResource,atributo,detailsItem.atributo.toString())
            m.add(uriPlayer,hasItem,itemResource)
        }


        val jsonLdWrite = StringWriter()
        m.write(jsonLdWrite, "JSON-LD")

        model.addAttribute("jsonld",jsonLdWrite.toString())
        return "dataJson.html"
    }

}