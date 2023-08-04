import scala.quoted.*

class TypesafeConfig(map: Map[String, Any]) extends Selectable:
  /** `selectDynamic` is invoked for simple member selections `v.m` */
  def selectDynamic(name: String): Any = map(name)

object TypesafeConfig:
  transparent inline def typesafeConfig(inline pairs: (String, Any)*) = 
    ${ typesafeConfigImpl('pairs) }

  private def typesafeConfigImpl(pairs: Expr[Seq[(String, Any)]])(using Quotes): Expr[Any] =
    import quotes.reflect.*
    val unpacked = Varargs.unapply(pairs).getOrElse(Nil).map:
      case '{ ($k: String) -> ($v: t) } => k.valueOrAbort -> v
      case _ => throw new Exception("Key should be a string")

    val typ = unpacked.foldLeft(TypeRepr.of[TypesafeConfig]): (acc, entry) =>
      val (key, value) = entry
      Refinement(acc, key, value.asTerm.tpe.widen)

    val params = unpacked.map { (k, v) => '{ ${Expr(k)} -> $v } }
    typ.asType match
      case '[t] => '{ TypesafeConfig(Map(${Varargs(params)}: _*)).asInstanceOf[t] }

