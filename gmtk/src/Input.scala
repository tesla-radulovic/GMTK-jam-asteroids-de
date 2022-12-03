package GMTK

import org.scalajs.dom
import scala.scalajs.js.Dynamic.{ global => jsg, newInstance => jsnew }
import scala.scalajs.js
import scala.collection.mutable.HashMap
import Math._



import scalajs.js
import org.scalajs.dom
trait Input {
  var isPressing:Boolean = false
  var justPressed:Boolean = false
  var justReleased:Boolean = false
  def update() = {
    justPressed = false
    justReleased = false
  }
  def ||(in:Input):ORasOneInput = ORasOneInput(this,in)
  def +(in:Input):ORasTwoInput = ORasTwoInput(this,in)
  def &&(in:Input):ANDInput = ANDInput(this,in)
  def clear = {
    justPressed = false
    justReleased = false
    isPressing = false
  }
  
  
}
case object Input{
  var movementX = 0.0
  var movementY = 0.0
  implicit def stringToInput(s:String) = s match {
    case "leftclick" => MouseInput(s)
    case "middleclick" => MouseInput(s)
    case "rightclick" => MouseInput(s)
    case _ => KeyInput(s)
  }
  implicit def stringToButton(s:String):Int = s match {
    case "leftclick" => 1
    case "middleclick" => 2
    case "rightclick" => 3
    case _ => -1
  }
  
  
  //var listen:Boolean = false
  def update = {
    movementX = 0.0
    movementY = 0.0
  }
  def apply(in:Input):Input = in
}
case class InputSpace(var listen:Boolean = true){
  var Inputs:Set[Input] = Set()
  def register(in:Input):Input = {Inputs += in;in}
  def clear = Inputs.foreach(_.clear)
  def stop = {
    listen = false
    clear
  }
  def start = {
    clear
    listen = true
  }
  def update = if(listen)Inputs.foreach(_.update())
}

case object MouseInput{
  //change to Array?
  val Mice:HashMap[Int,List[MouseInput]] = HashMap()
  try{
    jsg.document.addEventListener("mousedown", (e:js.Dynamic)=> {
      if(Mice.contains(e.which.asInstanceOf[Int])){
        Mice(e.which.asInstanceOf[Int]).foreach(mouseinput => {
          if(!mouseinput.isPressing) mouseinput.justPressed = true
          mouseinput.isPressing = true
        })
      }
    }, false)
    jsg.document.addEventListener("mouseup", (e:js.Dynamic)=> {
        if(Mice.contains(e.which.asInstanceOf[Int]))Mice(e.which.asInstanceOf[Int]).foreach(mouseinput => {
          if(mouseinput.isPressing) mouseinput.justReleased = true
          mouseinput.isPressing = false
        })
    }, false)
    jsg.document.addEventListener("mousemove", (e:js.Dynamic)=> {
        Mice.values.foreach(_.foreach(mouseinput => {
          mouseinput.movementY += e.movementY.asInstanceOf[Double]
          mouseinput.movementX += e.movementX.asInstanceOf[Double]
          mouseinput.posX = e.pageX.asInstanceOf[Double]
          mouseinput.posY = e.pageY.asInstanceOf[Double]
        }))
    }, false)
    
  }catch{
    case t:Throwable => println(t.getStackTrace())
  }
}


case class MouseInput(button:Int) extends Input {
  if(MouseInput.Mice.contains(button))MouseInput.Mice(button) = MouseInput.Mice(button) ++ List(this) else MouseInput.Mice += ((button,List(this)))
  var movementX = 0.0
  var movementY = 0.0
  var posX = 0.0
  var posY = 0.0
  override def update() = {
    //posX += movementX
    //posY += movementY
    movementX = 0.0
    movementY = 0.0
    super.update()
  }
  override def clear = {
    movementX = 0.0
    movementY = 0.0
    //posX = 0.0
    //posY = 0.0
    super.clear
  }
}

case class DragInput(button:Int) extends Input {
  val in = MouseInput(button)
  var movementX = 0.0
  var movementY = 0.0
  var posX = 0.0
  var posY = 0.0
  override def update() = {
    
    isPressing = in.isPressing
    justReleased = in.justReleased
    justPressed = in.justPressed
    if(justPressed){
      posX = in.posX
      posY = in.posY
    }
    if(isPressing){
      movementX = in.movementX
      movementY = in.movementY
      posX += movementX
      posY += movementY
      movementX = 0.0
      movementY = 0.0
    }
    in.update()
    super.update()
  }
  override def clear = {
    movementX = 0.0
    movementY = 0.0
    posX = 0.0
    posY = 0.0
    super.clear
  }
}

case object KeyInput{
  val Keys:HashMap[String,List[KeyInput]] = HashMap()
  try{
    jsg.document.addEventListener("keydown", (e:dom.KeyboardEvent)=> {
      if(Keys.contains(e.key.toUpperCase)){
        Keys(e.key.toUpperCase).foreach(keyinput => {
          if(!keyinput.isPressing) keyinput.justPressed = true
          keyinput.isPressing = true
        })
      }
    }, false)
    jsg.document.addEventListener("keyup", (e:dom.KeyboardEvent)=> if(Keys.contains(e.key.toUpperCase)){
        Keys(e.key.toUpperCase).foreach(keyinput => {
          keyinput.justReleased = true
          keyinput.isPressing = false
        })
    }, false)
    
  }catch{
    case t:Throwable => println(t.getStackTrace())
  }
}

case class KeyInput(key:String) extends Input {
  /*
  try{
    jsg.document.addEventListener("keydown", (e:dom.KeyboardEvent)=> {
        if(e.key == key){
          if(!isPressing){
            justPressed = true
          }
          isPressing = true

         }
    }, false)
    jsg.document.addEventListener("keyup", (e:dom.KeyboardEvent)=> {
        if(e.key == key){
          if(isPressing) justReleased = true
          isPressing = false
        }
    }, false)
  }catch{
    case _ => {}
  }
*/
  if(KeyInput.Keys.contains(key.toUpperCase))KeyInput.Keys(key.toUpperCase) = KeyInput.Keys(key.toUpperCase) ++ List(this) else KeyInput.Keys += ((key.toUpperCase,List(this)))
  
}
case class DoubleInput(in:Input) extends Input {
  var lastPress:Long = -1
  override def update() = {
    super.update()
    val now = System.currentTimeMillis
    if(in.justPressed){
      if(lastPress != -1){
        if(now - lastPress < 200){
          justPressed = true
        }
      }
      lastPress = now
    }
    if(justPressed){
      isPressing = true
    }
    if(isPressing && in.justReleased){
      isPressing = false
      justReleased = true
    }
    in.update()
  }
}
case class ORasOneInput(in1:Input,in2:Input) extends Input {
  override def update() = {
    super.update()
    if(!isPressing)justPressed = in1.justPressed || in2.justPressed
    isPressing = in1.isPressing || in2.isPressing
    if(!isPressing)justReleased = in1.justReleased || in2.justReleased
    in1.update()
    in2.update()
  }
}
case class ORasTwoInput(in1:Input,in2:Input) extends Input {
  override def update() = {
    super.update()
    justPressed = in1.justPressed || in2.justPressed
    isPressing = in1.isPressing || in2.isPressing
    justReleased = in1.justReleased || in2.justReleased
    in1.update()
    in2.update()
  }
}
case class ANDInput(in1:Input,in2:Input) extends Input {
  override def update() = {
    super.update()
    justPressed = (in1.justPressed && in2.isPressing) || (in2.justPressed && in1.isPressing)
    if(isPressing) justReleased = (in1.justReleased ) || (in2.justReleased)
    isPressing = in1.isPressing && in2.isPressing
    in1.update()
    in2.update()
  }
}


/*

case object Input {
  
  val presses:Hashmap[String,(Long,Long,Boolean)] = HashMap.empty
  
  dom.document.addListener("keydown", (e:dom.KeyboardEvent)=>{
    if(presses.contains(e.key)) presses += ( (e.key, (presses(e.key)._2,System.currentTimeMillis(),true) ) )
      else presses += ((e.key,( -1, System.currentTimeMillis(),true) ))
  } )
  
  dom.document.addListener("keyup", (e:dom.KeyboardEvent)=>{
    if(presses.contains(e.key)) presses += ((e.key, (presses(e.key)._1,presses(e.key)._2,false) ) )
  } )
  
  
  
}

trait InputEvent {
  private val triggers:Array[String]
  
  def pressing :Boolean = {
    var j = false
    var i = 0
    while(!j){
      if(Input.presses.contains(triggers(i)))  j = Input.presses(triggers(i))._3
      i+=1
    }
    j
  }
  def justPressed :Boolean = {
    var j = false
    var i = 0
    while(!j){
      if(Input.presses.contains(triggers(i)))  j = System.currentTimeMillis() - Input.presses(triggers(i))._2 < justpresstime
      i+=1
    }
    j
  }
  def jdoublePressed :Boolean = {
    var j = false
    var i = 0
    while(!j){
      if(Input.presses.contains(triggers(i)))  j = System.currentTimeMillis() - Input.presses(triggers(i))._2 < justpresstime && 
                                                   Input.presses(triggers(i))._2 - Input.presses(triggers(i))._1 < justpresstime
      i+=1
    }
    j
  }
  var justpresstime:Long = 100
  
  var lastpress:Long
  
  
  
}*/