package scalix

import scala.io.Source
import org.json4s.*
import org.json4s.native.JsonMethods.*

import scala.collection.immutable.HashMap
import scala.language.postfixOps
import scala.collection.mutable._

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
  //println(findActorId("Sigourney", "Weaver").get)
  //println(findActorMovies(10205))

  //TODO dont forget import scalix.Scalix.*
  def findActorId(name: String, surname: String):Option[Int]=
    val x = movieCast.filter(_.asInstanceOf[HashMap[String, Any]].get("name").get.equals(name + " " + surname))
    Some(x(0).asInstanceOf[HashMap[String, Any]].get("id").get.asInstanceOf[BigInt].toInt)

  /**
   * val json = parse(contents)
   *
   * if (json \ "total_results" == JInt(0)) None
   *
   * else Option((json \ "results")(0) \ "id" match {
   * case JInt(id) => id.toInt
   * case _ => throw new Exception("Error: id is not an integer")
   * })
   */

  def findActorMovies(actorId: Int): Set[(Int, String)]=
    val request = "https://api.themoviedb.org/3/discover/movie?api_key="+api_key+"&with_people="+actorId

    var array = Set[(Int, String)]()

    val newReq = Source.fromURL(request)
    val newCon = newReq.mkString
    val res=parse(newCon).extract[Map[String, Any]]
    val films=res("results").asInstanceOf[List[Any]]
    films.foreach((e)=>{
      val lint=e.asInstanceOf[HashMap[String, Any]].get("id").get.asInstanceOf[BigInt].toInt
      val lstring=e.asInstanceOf[HashMap[String, Any]].get("title").get.asInstanceOf[String]
      array += (lint,lstring)
    })
    array

  def findMovieDirector(movieId: Int): Option[(Int, String)]=
    val request = "https://api.themoviedb.org/3/movie/"+movieId.toString+"/credits?api_key="+api_key
    val newReq = Source.fromURL(request)
    val newCon = newReq.mkString
    val newMovie = parse(newCon).extract[Map[String, Any]]
    val crew = newMovie("crew").asInstanceOf[List[Any]]
    val x = crew.filter(_.asInstanceOf[HashMap[String, Any]].get("job").get.equals("Director"))
    val id = Some(x(0).asInstanceOf[HashMap[String, Any]].get("id").get.asInstanceOf[BigInt].toInt)
    val name = Some(x(0).asInstanceOf[HashMap[String, Any]].get("name").get.asInstanceOf[String].toString)
    Option[(Int,String)](id.get,name.get)

  //def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)]
}

