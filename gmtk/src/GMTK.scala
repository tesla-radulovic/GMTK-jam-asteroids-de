package GMTK

import org.scalajs.dom.{CanvasRenderingContext2D => Ctx, _}
import scala.scalajs.js.annotation._
import scalajs.js
import scala.concurrent._

@JSExportTopLevel("GMTK")
case object GMTK {
    val fullScale = 2
    val numast = 25
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    val reg = InputSpace()
    val up = reg.register( KeyInput("w") || KeyInput("ArrowUp") || KeyInput(",") || KeyInput("њ") )
    val down = reg.register( KeyInput("s") || KeyInput("ArrowDown") || KeyInput("o") || KeyInput("с") )
    val left = reg.register( KeyInput("a") || KeyInput("ArrowLeft") || KeyInput("а") )
    val right = reg.register( KeyInput("d") || KeyInput("ArrowRight") || KeyInput("e") || KeyInput("д"))
    val mouse = MouseInput(1)
    val click = reg.register(mouse)

    val canvas = document.getElementById("screen").asInstanceOf[html.Canvas]
    val endlength = 720*18
    var gameState:GameState = null

    def start() = {

    }

    @JSExport
    def main() = {
        Resources.BGM.play()
        println("here2")
        
        val ctx = canvas.getContext("2d")
        ctx.imageSmoothingEnabled = false
        ctx.mozImageSmoothingEnabled = false
        ctx.webkitImageSmoothingEnabled = false

        

        
        val draw = ctx.asInstanceOf[Ctx]
        
        var bouys:List[Drawable] = Nil
                    var y = 0.0
                    while(y < endlength){
                        y += (canvas.width/fullScale)/8.0
                        if(y < endlength) for(xi <- 0 to 8){
                            bouys = Transform(Matrix3.trans2D(Vector2( (xi/8.0)*canvas.width/fullScale-canvas.width/(fullScale*2) + (1.0/16.0)*(canvas.width/fullScale),-y) ),Texture(Resources.Buoy,Vector2(1,1) ) ):: bouys
                        }
                    }
                    for(xi <- 0 to 32){
                            bouys = Transform(Matrix3.trans2D(Vector2( (xi/32.0)*canvas.width/fullScale-canvas.width/(fullScale*2) + (1.0/64.0)*(canvas.width/fullScale),-endlength) ),Texture(Resources.Buoy,Vector2(1,1) ) ):: bouys
                    } 

        val stars = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2)), Texture(Resources.Stars,Vector2(canvas.width,canvas.height)))
        var win = false
        var lose = false
        var start = false
        var menu = true
        def render(): Unit = {
            if(menu){
                draw.fillStyle = "#00FFFF"
                val healthText = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2)) ,Text(s"Click to Play","Glasstown-NBP",500,"center"))
                Draw.draw(draw,stars)
                Draw.draw(draw,healthText)
                if(click.justPressed){
                    menu = false
                    start = true
                }
                reg.update
            }
            if(start){
                Resources.BGM.play()
                draw.fillStyle = "#00FFFF"
                val healthText = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2-70*2)) ,Text(s"You are piloting a broken ship","Glasstown-NBP",250,"center"))
                val healthText2 = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2-70)) ,Text(s"You must escape the asteroid field ahead","Glasstown-NBP",250,"center"))
                val healthText22 = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2)) ,Text(s"You can pick up repairs","Glasstown-NBP",250,"center"))
                val healthText222 = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2+70)) ,Text(s"But you can only have two at a time","Glasstown-NBP",250,"center"))
                val healthText2222 = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2+70*3)) ,Text(s"Click to begin","Glasstown-NBP",250,"center"))
                
                Draw.draw(draw,stars)
                Draw.draw(draw,healthText)
                Draw.draw(draw,healthText2)
                Draw.draw(draw,healthText22)
                Draw.draw(draw,healthText222)
                Draw.draw(draw,healthText2222)

                if(click.justPressed){
                    start = false
                    gameState = (GameState( (Ship(Vector2(0,-32*2),Vector2(0,0))) ) )
                    gameState = spawnAsteroids(gameState,numast)
                    gameState.repairs = Repair.randomRepair(Vector2(Math.random*canvas.width/(3*fullScale)-canvas.width/(fullScale*2) , -canvas.height/8) ) :: Nil//Shields(Vector2(Math.random*canvas.width/fullScale-canvas.width/(fullScale*2) , -canvas.height/8) ) :: Nil
                    val repairn = 30
                    for(i <- 1 to repairn){
                        val y = -endlength*(i/(repairn.toDouble))
                        if(i % 10 != 5){
                            gameState.repairs = Repair.randomRepair(Vector2(Math.random*canvas.width/(fullScale)-canvas.width/(fullScale*2) , y) ) :: gameState.repairs
                        }else{
                            gameState.repairs = LifeSupport(Vector2(Math.random*canvas.width/(fullScale)-canvas.width/(fullScale*2) , y) ) :: gameState.repairs
                        }//Shields(Vector2(Math.random*canvas.width/fullScale-canvas.width/(fullScale*2) , -canvas.height/8) ) :: Nil
                    }
                    
                    tick(gameState,System.currentTimeMillis)
                }
                reg.update
            }
            if(!start && ! menu){
            val fg = gameState
            val fp = fg.ship
            val entities = fg.entities
            if(fp.health <= 0) lose = true
            

                    //draw.clearRect(0,0,canvas.width,canvas.height)
                    draw.fillStyle = "#000000"
                    Draw.draw(draw,stars)
                    //draw.fillRect(0,0,canvas.width,canvas.height)
                    //val grid = Transform(Matrix3.trans2D(Vector2(0,-1260/4)),Texture(Resources.Grid,Vector2(1280/2,1260/2)))
                    
                    val drawList =  bouys :::(fp.draws :: (for(entity <- entities)yield entity match {
                            case a:Asteroid => a.draws
                        }) ::: (for(ion<-fg.ions)yield ion.draws)) ::: (for(repair <- fg.repairs)yield repair.draws)


                    var lookM = if(fp.sensors) Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(Vector2(canvas.width/2/*-fp.position.x*2*/,canvas.height/2-Math.min(fp.position.y*2,-canvas.height/2))) else Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(Vector2(canvas.width/2/*-fp.position.x*2*/,canvas.height/4-Math.min(fp.position.y*2,(1.0/4.0- 1.0)*canvas.height)) )
                    if(fp.sensors){
                        val tween = Math.min(60,fp.framecount - fp.sensorframe)
                        val ws = Vector2(canvas.width/2/*-fp.position.x*2*/,canvas.height/2-Math.min(fp.position.y*2,-canvas.height/2))
                        val ns = Vector2(canvas.width/2/*-fp.position.x*2*/,canvas.height/4-Math.min(fp.position.y*2,(1.0/4.0- 1.0)*canvas.height))
                        var lookV = ws*(tween/60.0) + ns*(1-tween/(60.0))
                        lookM =  Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(lookV)
                    }
                    if(!fp.sensors){
                        val tween = Math.min(60,fp.framecount - fp.sensorframe)
                        val ns = Vector2(canvas.width/2/*-fp.position.x*2*/,canvas.height/2-Math.min(fp.position.y*2,-canvas.height/2))
                        val ws = Vector2(canvas.width/2/*-fp.position.x*2*/,canvas.height/4-Math.min(fp.position.y*2,(1.0/4.0- 1.0)*canvas.height))
                        var lookV = ws*(tween/60.0) + ns*(1-tween/(60.0))
                        lookM =  Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(lookV)
                    }
                    val toDraw = Transform( lookM,Compound(drawList ) )
                    
                    
                    Draw.draw(draw,toDraw) 
                    
                    draw.fillStyle = "#005555"
                    draw.fillRect(0,40,100.0*2,30)
                    draw.fillStyle = "#00FFFF"
                    val healthText = Transform(Matrix3.trans2D(Vector2(0,30)) ,Text(s"Health:","Glasstown-NBP",150,"start"))
                    draw.fillRect(0,40,fp.health*2,30)
                    Draw.draw(draw,healthText)
                    if(fp.lifesupport){
                        val minutes = ((fg.timeRemaining) / 60.0).toInt
                        val seconds = (fg.timeRemaining - minutes*60.0).toInt
                        val timeText = Transform(Matrix3.trans2D(Vector2(0,canvas.height)) ,Text(s"Life Support! $minutes:$seconds","Glasstown-NBP",250,"start"))
                    
                        Draw.draw(draw,timeText)
                    }else{
                        val minutes = (fg.timeRemaining / 60.0).toInt
                        val seconds = (fg.timeRemaining - minutes*60.0).toInt
                        
                        val timeText = Transform(Matrix3.trans2D(Vector2(0,canvas.height)) ,Text(s"$minutes:$seconds","Glasstown-NBP",250,"start"))
                    
                        Draw.draw(draw,timeText)
                    }
                    if(fg.timeRemaining < 0)lose = true
                    
                    val metersRemaining = Math.max( ( (endlength + fp.position.y) ).toInt,0 )
                    if(metersRemaining == 0){
                        win = true
                    }
                    val meterText = Transform(Matrix3.trans2D(Vector2(canvas.width,canvas.height)) ,Text(s"$metersRemaining m Remaining","Glasstown-NBP",250,"end"))
                    Draw.draw(draw,meterText)
                    
                    draw.fillRect(canvas.width/2+ (0-16-2)*2,canvas.height-34*2,(32*2+4)*2,34*2)
                    draw.fillStyle = "#000000"
                    draw.fillRect(canvas.width/2+ (0-16)*2,canvas.height-32*2,(32*2)*2,34*2)
                    var posxx = canvas.width/2+ (0-16)*2 + 16*2
                    var i =0

                    for(repair <- fp.repairs){
                        i += 1
                        val v = Vector2(posxx,canvas.height-16*2)
                        repair.offset = Vector2(0,0)
                        repair.position = v*(1.0/2.0)
                        if(i <= 2)Draw.draw(draw,Transform(Matrix3.scale2D(Vector2(2,2)),repair.draws ) )
                        posxx += 32*2
                        var text = ""
                        repair match {
                            case steering:Steering => text =                "Steering: Ctrl L/R Movement"
                            case lifesupport:LifeSupport => text =          "LifeSupport: Infinite Time"
                            case targeting:Targeting => text =              "Targeting: Control Shooting"
                            case shields:Shields => text =                  "Shields: Regen ShieldHealth"
                            case sensor:Sensors => text =                   "Sensors: See Farther Ahead"
                            case retroThrusters:RetroThrusters => text =    "RetroThrusters: Move Back"
                        }
                        draw.fillStyle = "#00FFFF"
                        val rText = Transform(Matrix3.trans2D(Vector2(canvas.width,30+(i-1)*40)) ,Text(text,"Glasstown-NBP",150,"end"))
                        Draw.draw(draw,rText)
                    }
            
            if( !(!lose && !win) )  {
                if(win){
                    draw.fillStyle = "#00FFFF"
                    val winText = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2)) ,Text(s"You Win","Glasstown-NBP",500,"center"))
                    val rt = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2+70*3)) ,Text(s"Click to Play Again","Glasstown-NBP",250,"center"))
                    if(click.justPressed){
                        start = true
                        win = false
                    }
                    reg.update
                    Resources.BGM.pause()
                    Draw.draw(draw,winText)
                    Draw.draw(draw,rt)
                    Resources.Win.play()
                }else{
                    draw.fillStyle = "#00FFFF"
                    val loseText = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2)) ,Text(s"You Lose","Glasstown-NBP",500,"center"))
                    val rt = Transform(Matrix3.trans2D(Vector2(canvas.width/2,canvas.height/2+70*3)) ,Text(s"Click to Play Again","Glasstown-NBP",250,"center"))
                    
                    if(click.justPressed){
                        start = true
                        lose = false
                    }
                    reg.update
                    Draw.draw(draw,loseText)
                    Draw.draw(draw,rt)
                    Resources.BGM.pause()
                    Resources.Lose.play()
                }
            } 
            }
            window.requestAnimationFrame((_: Double) => render())
        }
        render()

        def tick(g:GameState,time:Long):Unit = if(!win && !lose)js.timers.setTimeout(16){
            gameState = run(g,time)
            tick(gameState,System.currentTimeMillis)
        }
        

    }
    

    def chunkCoord(e:Entity):(Int,Int) =  (if(e.position.x > 0) (e.position.x/100).toInt else (e.position.x/100).toInt -1,if(e.position.y > 0) (e.position.y/100).toInt else (e.position.y/100).toInt -1 )
    def collide(g:GameState,delta:Double):GameState = {
        
        val ship = g.ship
        val entities = g.entities
        
        val uwu = collideEntities(ship,entities)
        g.ship = uwu._1
        g.entities = for(entity <- collideAllEntities(uwu._2))yield entity
        //g.entities = for(entity <- attractAllEntities(g.entities,delta))yield entity
        g
        
    }
    def collideAllEntities(entities:List[Entity]):(List[Entity]) = {
        val head :: tail = entities
        tail match {
            case Nil => head :: Nil
            case _ => {
                val (ohead,otail) = collideEntities(head,tail)
                ohead :: collideAllEntities(tail)
            }
        }
    }
    
    def collideEntities[H <: Entity](head:H,tail:List[Entity]):(H,List[Entity]) = {
        tail.foldRight[ (H,List[Entity]) ](head,Nil)( {
            case(fe,(h,fes) ) => {
                val out = Entity.collide(h,fe)
                (out._1,(out._2) :: fes)
            }
        } )
    }
    def attractAllEntities(entities:List[Entity],delta:Double):(List[Entity]) = {
        val head :: tail = entities
        tail match {
            case Nil => head :: Nil
            case _ => {
                val (ohead,otail) = attractEntities(head,tail,delta)
                ohead :: attractAllEntities(tail,delta)
            }
        }
    }
    def attractEntities[H <: Entity](head:H,tail:List[Entity],delta:Double):(H,List[Entity]) = {
        tail.foldRight[ (H,List[Entity]) ](head,Nil)( {
            case(fe,(h,fes) ) => {
                val out = Entity.attract(h,fe,delta)
                (out._1,(out._2) :: fes)
            }
        } )
    }
    def tickRepairs(g:GameState,delta:Double):GameState = {
        for(repair <- g.repairs){
            repair.offset = Vector2(0,Math.sin(System.currentTimeMillis/1000.0)*10)
        }
        g.repairs = g.repairs.filter(repair => {
            val points = Vector2(-16,-16) :: Vector2(-16,16) :: Vector2(16,-16) :: Vector2(16,16) :: Nil
            var keep = true
            for(point <- points){
                if( (point + repair.position + repair.offset - g.ship.position).mag <= g.ship.radius ){
                    keep = false
                    
                }
            }
            if(!keep) {
                Resources.Pickup.play()
                    repair match {
                        case sensors:Sensors => {
                            g.ship.sensors = true
                            var setFrame = true
                            for(arepair <- g.ship.repairs)arepair match{
                                case s2:Sensors => setFrame = false
                                case _ =>
                            }
                            if(setFrame) g.ship.sensorframe = g.ship.framecount
                        }
                        case _ =>
                    }
                    g.ship.repairs += repair
            }
            keep
        })
        g
    }
    def tickEntities(g:GameState,delta:Double):GameState = {
        g.entities = for(entity <- g.entities) yield {
            val e = entity match {
                case a:Asteroid => {
                    a.theta += a.omega*delta
                    a
                }
            }
            e.position.x = e.position.x + e.velocity.x*delta
            if(e.position.x + e.radius > canvas.width/(fullScale*2)){
                e.position.x = canvas.width/(fullScale*2) - e.radius
                e.velocity.x = -e.velocity.x
            }
            if(e.position.x - e.radius < -canvas.width/(fullScale*2)){
                e.position.x = e.radius - canvas.width/(fullScale*2)
                e.velocity.x = -e.velocity.x
            }
            e.position.y = e.position.y + e.velocity.y*delta
            if(e.position.y + e.radius > 0){
                e.position.y = -e.radius
                e.velocity.y = -e.velocity.y
            }
            e
        }
        
        g
    }
    def tickIons(g:GameState,delta:Double):GameState = {
            val entities = g.entities
            var nions:List[Ion] = Nil
            for(ion <- g.ions){
                for(entity <- entities){
                    if( (ion.position - entity.position).mag < ion.radius + entity.radius ){
                        entity.health -= 100
                        Resources.Destroy.play()
                        ion.life = 10
                    }
                }
                ion.position += ion.velocity*delta
                ion.life += delta
                ion.theta += delta
                if(ion.life < 1)nions = ion :: nions
                
            }
            g.ions = nions
            g
    }
    def tickShip(g:GameState,delta:Double):GameState = {
        g.ship = {
            val ship = g.ship
            ship.steering = false
            ship.backwards = false
            ship.shields = false
            ship.sensors = false
            ship.lifesupport = false
            ship.shooting = false
            while(ship.repairs.length > 2){
                ship.repairs.dequeue match {
                    case sensors:Sensors => {
                        var setFrame = true
                        for(grepair <- ship.repairs)grepair match {
                            case s2:Sensors => setFrame = false
                            case _ =>
                        }
                        if(setFrame) ship.sensorframe = ship.framecount
                    }
                    case _ =>
                }
            }
            for(repair <- ship.repairs)repair match {
                        case shields:Shields => g.ship.shields = true
                        case lifeSupport:LifeSupport =>  g.ship.lifesupport = true
                        case retroThrusters:RetroThrusters => g.ship.backwards = true
                        case sensors:Sensors => g.ship.sensors = true
                        case targeting:Targeting => g.ship.shooting = true
                        case steering:Steering => g.ship.steering = true
                    }



            if(up.isPressing) ship.velocity.y -= 1500*delta
            if(down.isPressing && ship.backwards) ship.velocity.y += 1500*delta
            if(ship.steering){
                if(left.isPressing) ship.velocity.x -= 1500*delta
                if(right.isPressing) ship.velocity.x += 1500*delta
            }else{
                if(ship.left) ship.velocity.x -= 1500*delta else ship.velocity.x += 1500*delta
            }
            var lookM = if(GMTK.gameState.ship.sensors) Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(Vector2(canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,canvas.height/2-Math.min(GMTK.gameState.ship.position.y*2,-canvas.height/2))) else Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(Vector2(canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,canvas.height/4-Math.min(GMTK.gameState.ship.position.y*2,(1.0/4.0- 1.0)*canvas.height)) )
                    if(GMTK.gameState.ship.sensors){
                        val tween = Math.min(60,GMTK.gameState.ship.framecount - GMTK.gameState.ship.sensorframe)
                        val ws = Vector2(canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,canvas.height/2-Math.min(GMTK.gameState.ship.position.y*2,-canvas.height/2))
                        val ns = Vector2(canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,canvas.height/4-Math.min(GMTK.gameState.ship.position.y*2,(1.0/4.0- 1.0)*canvas.height))
                        var lookV = ws*(tween/60.0) + ns*(1-tween/(60.0))
                        lookM =  Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(lookV)
                    }
                    if(!GMTK.gameState.ship.sensors){
                        val tween = Math.min(60,GMTK.gameState.ship.framecount - GMTK.gameState.ship.sensorframe)
                        val ns = Vector2(canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,canvas.height/2-Math.min(GMTK.gameState.ship.position.y*2,-canvas.height/2))
                        val ws = Vector2(canvas.width/2/*-GMTK.gameState.ship.position.x*2*/,canvas.height/4-Math.min(GMTK.gameState.ship.position.y*2,(1.0/4.0- 1.0)*canvas.height))
                        var lookV = ws*(tween/60.0) + ns*(1-tween/(60.0))
                        lookM =  Matrix3.scale2D(Vector2(2,2))*Matrix3.trans2D(lookV)
                    }
                    val look = ship.position*lookM
            ship.framecount += 1
            var cont = ship.framecount % 60 == 0
            if(ship.shooting) cont = ship.framecount % 5 == 0
            val dirr = (look-Vector2(mouse.posX,mouse.posY))
            ship.theta = Math.atan(-dirr.y/dirr.x) + Math.PI/2
            if(dirr.x <= 0) ship.theta += Math.PI
            if( (click.justPressed)&&ship.shooting ){
                //println(mouse.posX,mouse.posY)
                //println(look.x,look.y)
                var dir = (Vector2(mouse.posX,mouse.posY)-look).norm
                g.ions = Ion(ship.position,dir*1000,random,random) :: g.ions
                Resources.Fire.play()
            }
            if(!ship.shooting && cont /*&& Math.random < 0.5*/){
                var dir = (Vector2(mouse.posX,mouse.posY)-look).norm
                g.ions = Ion(ship.position,dir*1000,random,random) :: g.ions
                Resources.Fire.play()
            }

            reg.update
            

            ship.velocity = ship.velocity-(ship.velocity*7.0*delta)
            ship.position.x = ship.position.x + ship.velocity.x*delta
            if(ship.position.x + ship.radius > canvas.width/(fullScale*2)){
                ship.position.x = canvas.width/(fullScale*2) - ship.radius
                ship.velocity.x = -ship.velocity.x
                ship.left = !ship.left
            }
            if(ship.position.x - ship.radius < -canvas.width/(fullScale*2)){
                ship.position.x = ship.radius - canvas.width/(fullScale*2)
                ship.velocity.x = -ship.velocity.x
                ship.left = !ship.left
            }
            ship.position.y = ship.position.y + ship.velocity.y*delta
            if(ship.position.y + ship.radius > 0){
                ship.position.y = -ship.radius
                ship.velocity.y = -ship.velocity.y
            }
            if(ship.shields) ship.health += 10.0*delta 
            ship.health = Math.min(100.0,ship.health)

            ship
        }
        g
    }
    def run(fgameState:GameState,time:Long):GameState = {
        val ntime = System.currentTimeMillis
        val delta = (ntime - time).toDouble/1000.0
            
        val ng0 = tickShip(fgameState,delta)
        val ng1 = tickEntities(ng0,delta)
        val ng2 = collide(ng1,delta)
        var ng3 = tickIons(ng2,delta)
        ng3 = tickRepairs(ng3,delta)
        val entities = ng3.entities
            
        if(!ng3.ship.lifesupport)ng3.timeRemaining -= delta
        ng3.entities = for(entity <- entities if(entity.health > 0 && Math.abs(entity.position.y - ng3.ship.position.y) < canvas.height ) )yield entity
        if(ng3.entities.length < numast){
            ng3 = spawnAsteroids(ng3,numast-ng3.entities.length)
        }
        ng3
    }
    def random = Math.random*2-1
    def spawnAsteroid(g:GameState):GameState = {
        
        val ship = g.ship
        val entities = g.entities
        
        var test = Asteroid(Vector2(Math.random*canvas.width/fullScale-canvas.width/(fullScale*2),ship.position.y-canvas.height/(2.0)-Math.random*canvas.height/(2.0) ),Vector2(random*100,random*100),3000,random*Math.PI*2,random*random*random*Math.PI*2 )
        while(testCollision(test,ship :: entities)){
            test = Asteroid(Vector2(Math.random*canvas.width/fullScale-canvas.width/(fullScale*2),ship.position.y-canvas.height/(2.0)-Math.random*canvas.height/(2.0) ),Vector2(random*100,random*100),3000,random*Math.PI*2,random*random*random*Math.PI*2 )
        }
        g.entities = test :: g.entities
        g
    }
    def spawnAsteroids(fg:GameState,amount:Int):GameState = amount match {
        case 0 => fg
        case _ => {
            val ffg = (spawnAsteroid(fg))
            spawnAsteroids(ffg,amount-1)
        }
    }
    def testCollision(test:Entity,others:List[Entity]):Boolean = {
        for(entity <- others){
            if( (test.position - entity.position).mag <= test.radius + entity.radius ) return true
        }
        return false
    }
    
    

}