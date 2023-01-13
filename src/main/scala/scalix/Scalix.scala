package scalix

import scala.io.Source
import org.json4s.*
import org.json4s.native.JsonMethods.*

import scala.collection.immutable.HashMap

object Scalix extends App {

  val api_key = "d38ee4bb2a73d29b441cf0d729ba367a"
  val url = s"https://api.themoviedb.org/3/movie/19995/credits?api_key=$api_key"
  //val url = s"https://api.themoviedb.org/3/list/8235623?api_key=$api_key"

  val source = Source.fromURL(url)
  val contents = source.mkString
  //println(contents)
  //val json = parse(contents)

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats
  //Extract
  val movie = parse(contents).extract[Map[String, Any]]
  //Movie Id
  val movieId = movie("id").asInstanceOf[BigInt].toInt
  //Get cast
  val movieCast = movie("cast").asInstanceOf[List[Any]]
  //Get crew
  val movieCrew = movie("crew").asInstanceOf[List[Any]]
  println(findActorId("Sigourney", "Weaver").get)

  println(movieId)
  println(movieCast)
  println(movieCrew)

  def findActorId(name: String, surname: String):Option[Int]=
    val x = movieCast.filter(_.asInstanceOf[HashMap[String, Any]].get("name").get.equals(name + " " + surname))
    Some(x(0).asInstanceOf[HashMap[String, Any]].get("id").get.asInstanceOf[BigInt].toInt)
}

