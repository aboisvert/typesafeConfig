/** Run this file with:
 *  scala-cli run example.scala
 */

//> using scala 3.3
//> using files .

import TypesafeConfig._

val config = typesafeConfig(
  "domain" -> "example.com",
  "port" -> 8080,
  "protocol" -> "https",
  "proxy" -> false,
  "methods" -> List("GET", "PUT"),
)

val port: Int = config.port // correctly typed as `Int`

@main def run() =
  println(s"Type of config.port is ${config.port.getClass}")