package GMTK
import scala.collection.mutable.Queue

case class Ship(var position:Vector2,var velocity:Vector2 = Vector2.ZERO,var health:Double = 100.0) extends Entity{
    val radius = 48/2
    val mass = 5000
    var steering = false
    var backwards = false
    var left = true
    var shields = false
    var sensors = false
    var framecount = 0
    var lifesupport = false
    var shooting = false
    var sensorframe = -10000
    var repairs:Queue[Repair] = Queue.empty
    var theta = 0.0
    def draws:Drawable = {
        Transform(Matrix3.trans2D(position),Compound( Transform(Matrix3.rot2D(theta),Texture(Resources.Cannon,Vector2(48,48))):: Texture(Resources.Ship,Vector2(48,48))::Texture(Resources.Shield,Vector2(48,48))::Nil ) )
    }
    
}
//case object SpaceChunk{
//    val SIZE = 256
//}
//case class SpaceChunk(x:Int,y:Int,entities:List[Future[Entity]])

sealed trait Entity{
    var position:Vector2
    var velocity:Vector2
    val mass:Double
    val radius:Double
    var health:Double
}

case object Entity {
    val G = 50
    def attract[E1 <: Entity,E2 <: Entity](e1:E1,e2:E2,delta:Double):(E1,E2) = {
        val R = (e1.position - e2.position)*(e1.position - e2.position)
        val F = G*e1.mass*e2.mass/R
        val s = (e1.position - e2.position).norm
        val sp = Vector2(-s.y,s.x)
        e1.velocity = e1.velocity - s*F*(1.0/e1.mass)*delta
        e2.velocity = e2.velocity + s*F*(1.0/e2.mass)*delta
        (e1,e2)
    }
    def collide[E1 <: Entity,E2 <: Entity](e1:E1,e2:E2):(E1,E2) = {
        if( (e1.position - e2.position).mag <= e1.radius + e2.radius ){
            
            val s = (e1.position-e2.position).norm
            val sp = Vector2(-s.y,s.x)
            val viprp = ( e1.velocity.x - (e1.velocity.y*( s.x/s.y  )) )/( -s.y - (s.x*s.x/s.y) )
            val vipar = (e1.velocity.x + viprp*s.y)/s.x
            val vjprp = ( e2.velocity.x - (e2.velocity.y*( s.x/s.y  )) )/( -s.y - (s.x*s.x/s.y) )
            val vjpar = (e2.velocity.x + vjprp*s.y)/s.x
            
            if( (vjpar-vipar) >= 0){

                var v1 = ((vjpar*e2.mass)-(vipar*e2.mass )+(e1.mass*vipar)+(e2.mass*vjpar))/(e1.mass+e2.mass)
                var v2 = vipar + v1 - vjpar
                if(e2.mass==Double.PositiveInfinity){
                    v1 = -vipar
                    v2 =  vjpar
                }
                if(e1.mass==Double.PositiveInfinity){
                    v1 =  vipar
                    v2 = -vjpar
                }
                
                e1.velocity = s*v1 + sp*viprp
                e2.velocity = s*v2 + sp*vjprp
                //while( (e1.position - e2.position).mag <= e1.radius + e2.radius ){
                //    e1.position = e1.position + e1.velocity*(.01)
                //    e2.position = e2.position + e2.velocity*(.01)
                //}
                
                e1 match {
                    case s:Ship => e1.health -= 20
                    case _ => e1.health -= 1
                }
                e2 match {
                    case s:Ship => e2.health -= 20
                    case _ => e2.health -= 1
                }


                var lookM = if(GMTK.gameState.ship.sensors) Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(Vector2(GMTK.canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,GMTK.canvas.height/2-Math.min(GMTK.gameState.ship.position.y*2,-GMTK.canvas.height/2))) else Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(Vector2(GMTK.canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,GMTK.canvas.height/4-Math.min(GMTK.gameState.ship.position.y*2,(1.0/4.0- 1.0)*GMTK.canvas.height)) )
                    if(GMTK.gameState.ship.sensors){
                        val tween = Math.min(60,GMTK.gameState.ship.framecount - GMTK.gameState.ship.sensorframe)
                        val ws = Vector2(GMTK.canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,GMTK.canvas.height/2-Math.min(GMTK.gameState.ship.position.y*2,-GMTK.canvas.height/2))
                        val ns = Vector2(GMTK.canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,GMTK.canvas.height/4-Math.min(GMTK.gameState.ship.position.y*2,(1.0/4.0- 1.0)*GMTK.canvas.height))
                        var lookV = ws*(tween/60.0) + ns*(1-tween/(60.0))
                        lookM =  Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(lookV)
                    }
                    if(!GMTK.gameState.ship.sensors){
                        val tween = Math.min(60,GMTK.gameState.ship.framecount - GMTK.gameState.ship.sensorframe)
                        val ns = Vector2(GMTK.canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,GMTK.canvas.height/2-Math.min(GMTK.gameState.ship.position.y*2,-GMTK.canvas.height/2))
                        val ws = Vector2(GMTK.canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,GMTK.canvas.height/4-Math.min(GMTK.gameState.ship.position.y*2,(1.0/4.0- 1.0)*GMTK.canvas.height))
                        var lookV = ws*(tween/60.0) + ns*(1-tween/(60.0))
                        lookM =  Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(lookV)
                    }
                val look1 = e1.position * lookM
                val look2 = e1.position * lookM
                if( (look1.x > 0 && look1.x < GMTK.canvas.width && look1.y > 0 && look1.y < GMTK.canvas.height) ||
                    (look2.x > 0 && look2.x < GMTK.canvas.width && look2.y > 0 && look2.y < GMTK.canvas.height)
                ){
                    Resources.Collide.play()
                }
                
                //e1.health -= 10
                //e2.health -= 10
            }
        }
        (e1,e2)
    }
}

case class Asteroid(var position:Vector2,var velocity:Vector2,mass:Double,var theta:Double,omega:Double) extends Entity{
    var health:Double = 30.0
    val radius = 16/2*2
    def draws():Drawable = Transform(Matrix3.rot2D(theta)*Matrix3.trans2D(position),Texture(Resources.Asteroid,Vector2(16*2,16*2)))
}

case class Ion(var position:Vector2,var velocity:Vector2,var theta:Double,omega:Double) {
    val radius = 4
    var life = 0.0
    def draws():Drawable = Transform(Matrix3.rot2D(theta)*Matrix3.trans2D(position),Texture(Resources.Ion,Vector2(16,16)))
}

sealed trait Repair{
    val dim = 32/2
    var position:Vector2
    var offset:Vector2 = Vector2.ZERO
    def draws:Drawable
}
case object Repair{
    def randomRepair(position:Vector2):Repair = (Math.random*6).toInt match {
        case 0 => Shields(position)
        case 1 => LifeSupport(position)
        case 2 => RetroThrusters(position)
        case 3 => Sensors(position)
        case 4 => Steering(position)
        case _ => Targeting(position)
    }
}
case class Shields(var position:Vector2) extends Repair {
    def draws:Drawable = Transform(Matrix3.trans2D(position+offset),Compound(Texture(Resources.Repair,Vector2(32,32) )::Texture(Resources.Shields,Vector2(32,32) )::Nil ) )
}
case class LifeSupport(var position:Vector2) extends Repair {
    def draws:Drawable = Transform(Matrix3.trans2D(position+offset),Compound(Texture(Resources.Repair,Vector2(32,32) )::Texture(Resources.LifeSupport,Vector2(32,32) )::Nil ) )
}
case class RetroThrusters(var position:Vector2) extends Repair {
    def draws:Drawable = Transform(Matrix3.trans2D(position+offset),Compound(Texture(Resources.Repair,Vector2(32,32) )::Texture(Resources.RetroThrusters,Vector2(32,32) )::Nil ) )
}
case class Sensors(var position:Vector2) extends Repair {
    def draws:Drawable = Transform(Matrix3.trans2D(position+offset),Compound(Texture(Resources.Repair,Vector2(32,32) )::Texture(Resources.Sensors,Vector2(32,32) )::Nil ) )
}
case class Targeting(var position:Vector2) extends Repair {
    def draws:Drawable = Transform(Matrix3.trans2D(position+offset),Compound(Texture(Resources.Repair,Vector2(32,32) )::Texture(Resources.Targeting,Vector2(32,32) )::Nil ) )
}
case class Steering(var position:Vector2) extends Repair {
    def draws:Drawable = Transform(Matrix3.trans2D(position+offset),Compound(Texture(Resources.Repair,Vector2(32,32) )::Texture(Resources.Steering,Vector2(32,32) )::Nil ) )
}