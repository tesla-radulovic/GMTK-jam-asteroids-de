package GMTK

case class Player(var position:Vector2,var velocity:Vector2){
    
    def draws:Drawable = Transform(Matrix3.trans2D(position),Texture(Resources.Cevanti,Vector2(100,100)))
}