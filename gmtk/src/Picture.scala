package GMTK
import org.scalajs.dom.raw.HTMLImageElement
import org.scalajs.dom.{CanvasRenderingContext2D => Ctx, _}
import org.scalajs.dom

sealed trait Drawable

//case class Triangle(Vector21:Vector2,Vector22:Vector2,Vector23:Vector2) extends Drawable
case class Texture (image:Image, dimensions:Vector2, val localposition:Vector2 = Vector2(0,0), val localdim:Vector2 = Vector2(1,1) ) extends Drawable
case class Text(text:String,font:String,size:Double,align:String) extends Drawable{
    val style = s"${(size/3).toInt}px "+font
}
case class Transform(matrix:Matrix3,drawable:Drawable) extends Drawable
case class Compound(list:List[Drawable]) extends Drawable
class Image(src: String) {
  private var ready: Boolean = false

  val element = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
  element.src = src
  element.onload = (e: dom.Event) => {
    println("Image Loaded")
    ready = true
  }
  def isReady: Boolean = ready
}

case object Draw {

    def draw(ctx:Ctx,drawable:Drawable,matrix:Matrix3 = Matrix3.IDENTITY):Unit = drawable match {
        case Texture (img, Vector2(w, h), Vector2(lx,ly),Vector2(lw,lh) ) => if(img.isReady){
          val Vector2(x,y) = Vector2.ZERO*matrix
          val scalex = (Vector2(1,0)*matrix - Vector2(x,y)).mag
          val scaley = (Vector2(0,1)*matrix - Vector2(x,y)).mag
          val scale = Vector2(scalex,scaley)
          val Vector2(rx,ry) = (Vector2(1,0)*matrix - Vector2(x,y))//*Matrix2.scale(Vector2(1.0/scalex,1.0/scaley))
          var theta = Math.atan(ry/rx)
          if(rx == 0 && ry >= 0) theta = Math.PI/2
          if(rx == 0 && ry < 0) theta = Math.PI/2 + Math.PI
          if(rx < 0) theta += Math.PI
          ctx.save()
          ctx.translate(x,y)
          //println(rx,ry,theta)
          ctx.rotate(theta)
          
          ctx.drawImage(img.element,img.element.width*lx,img.element.height*ly,img.element.width*lw,img.element.height*lh,-w*scale.x/2,-h*scale.y/2, w*scale.x, h*scale.y)
          ctx.restore()
        }
        case t:Text => {
            val Vector2(x,y) = Vector2.ZERO*matrix
            ctx.textAlign = t.align
            ctx.font = t.style
            ctx.fillText(t.text,x,y)
        }
        case Transform(matrix2,drawable) => draw(ctx,drawable,matrix2*matrix)
        case Compound(list) => for(elem <- list)draw(ctx,elem,matrix)
    }
    
}
