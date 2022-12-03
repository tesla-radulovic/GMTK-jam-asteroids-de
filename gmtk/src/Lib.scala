package GMTK

case class Matrix2( a: Double, b:Double, c:Double, d:Double){
  def row1:Vector2 = Vector2(a,b)
  def row2:Vector2 = Vector2(c,d)
  def col1:Vector2 = Vector2(a,c)
  def col2:Vector2 = Vector2(b,d)
  def *(v:Vector2):Vector2 = Vector2( row1 * v, row2 * v)
  def *(m:Matrix2):Matrix2 = Matrix2(row1*m.col1,row1*col2
                                    ,row2*m.col1,row2*col2
                                    )
}

case object Matrix2{
  val ZERO = Matrix2(0,0,0,0)
  val IDENTITY = Matrix2(1,0,0,1)
  def scale(v:Vector2) = Matrix2(v.x,0,0,v.y)
  def rot(theta:Double) = Matrix2(Math.cos(theta),-Math.sin(theta),Math.sin(theta),Math.cos(theta))
}

case class Vector2 (var x: Double,var y: Double) {
  def + (v: Vector2) : Vector2 = Vector2(x + v.x, y + v.y)
  def - (v: Vector2) : Vector2 = Vector2(x - v.x, y - v.y)
  def scaleBy (v: Vector2) : Vector2 = Vector2(x * v.x, y * v.y)
  def * (d: Double) : Vector2 = Vector2(x * d, y * d)
  def * (v: Vector2) : Double = ( x * v.x + y * v.y )
  def *(m:Matrix2):Vector2 = Vector2( this*m.col1,this*m.col2 )
  def *(m:Matrix3):Vector2 = {
    val tmp = Vector3(x,y,1)
    Vector2(tmp*m.col1,tmp*m.col2)
  }
  def mag:Double = Math.sqrt(this*this)
  def norm:Vector2 = this/this.mag
  def / (d: Double) : Vector2 = Vector2(x / d, y / d)
  def lerp (v: Vector2, s: Vector2) : Vector2 = Vector2(x + (v.x - x)*s.x, y + (v.y - y)*s.y)
  def unary_- = Vector2(-x, -y)
}

object Vector2 {
  
  val ZERO = Vector2(0, 0)
  val ONE = Vector2(1, 1)
}

case object Vector3 {
    val ZERO = Vector3(0, 0, 0)
    val ONE = Vector3(1, 1, 1)
}
case class Vector3 (var x: Double,var  y: Double ,var  z: Double) {
  def + (v: Vector3) : Vector3 = Vector3(x + v.x, y + v.y, z + v.z)
  def * (v: Vector3) : Double = ( x * v.x + y * v.y + z * v.z)
  def mag:Double = Math.sqrt(this*this)
  def / (d: Double) : Vector3 = Vector3(x / d, y / d, z/d)
}

case class Matrix3(  a: Double,  b:Double,  c:Double,
                     d: Double,  e:Double,  f:Double,
                     g: Double,  h:Double,  i:Double
                  ){
    def row1:Vector3 = Vector3(a,b,c)
    def row2:Vector3 = Vector3(d,e,f)
    def row3:Vector3 = Vector3(g,h,i)
    def col1:Vector3 = Vector3(a,d,g)
    def col2:Vector3 = Vector3(b,e,h)
    def col3:Vector3 = Vector3(c,f,i)
    def *(v:Vector3):Vector3 = Vector3(row1 * v,row2 * v, row3 * v)
    def *(m:Matrix3):Matrix3 = Matrix3( row1*m.col1,row1*m.col2,row1*m.col3,
                                        row2*m.col1,row2*m.col2,row2*m.col3,
                                        row3*m.col1,row3*m.col2,row3*m.col3
                                      )
  }
case object Matrix3{
  val ZERO = Matrix3(
    0,0,0,
    0,0,0,
    0,0,0)
  val IDENTITY = Matrix3(
    1,0,0,
    0,1,0,
    0,0,1)
  def scale2D(v:Vector2) = Matrix3(
    v.x,0  ,0,
    0  ,v.y,0,
    1  ,1  ,1)
  def rot2D(theta:Double) = Matrix3(
    Math.cos(theta),-Math.sin(theta),0,
    Math.sin(theta), Math.cos(theta),0,
    1              ,1               ,1)
  def trans2D(v:Vector2) =  Matrix3(
    1  ,0  ,0,
    0  ,1  ,0,
    v.x,v.y,1)
}