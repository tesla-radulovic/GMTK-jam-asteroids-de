package GMTK
import scalajs.js
import js.Dynamic.{ global => jsg, newInstance => jsnew }
case object Resources {
    val Cevanti = new Image("assets/cevanti.png")
    val Valkyrie = new Image("assets/valkyrie.png")
    val Asteroid = new Image("assets/asteroid.png")
    val Ship = new Image("assets/ship.png")
    val Shield = new Image("assets/shield.png")
    val Grid = new Image("assets/backgrid.png")
    val Buoy = new Image("assets/buoy.png")
    val Ion = new Image("assets/ion.png")
    val Cannon = new Image("assets/cannon.png")

    val Repair = new Image("assets/repair.png")
    val LifeSupport = new Image("assets/lifesupport.png")
    val RetroThrusters = new Image("assets/retrothrusters.png")
    val Sensors = new Image("assets/sensors.png")
    val Shields = new Image("assets/shields.png")
    val Targeting = new Image("assets/targeting.png")
    val Steering = new Image("assets/steering.png")

    val Stars = new Image("assets/stars.png")

    val Pickup = jsnew(jsg.Audio)("assets/pickup.wav")
    val Collide = jsnew(jsg.Audio)("assets/collide.wav")
    val Destroy = jsnew(jsg.Audio)("assets/destroy.wav")
    val Fire = jsnew(jsg.Audio)("assets/fire.wav")
    val Lose = jsnew(jsg.Audio)("assets/lose.wav")
    val Win = jsnew(jsg.Audio)("assets/win.wav")
    //val BGM = jsnew(jsg.Audio)("assets/dancing2OG.mp3")
    val BGM = jsnew(jsg.Audio)("assets/2051final.mp3")
    BGM.loop = true
    

}
