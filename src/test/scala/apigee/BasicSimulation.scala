package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val apikey = System.getProperty("apikey")

  val httpConf = http
    .baseURL("http://seanwilliams-test.apigee.net/sean-catalogs2") // Here is the root for all relative URLs
    .acceptHeader("application/json") // Here are the common headers
    .doNotTrackHeader("1")
    //.acceptLanguageHeader("en-US,en;q=0.5")
    //.acceptEncodingHeader("gzip, deflate")
    //.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val headers_10 = Map("Content-Type" -> """application/json""") // Note the headers specific to a given request

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(http("Get catalog by ID")
      .get("/catalogs/2")
      .queryParam("apikey", apikey))
    .pause(5) // Note that Gatling has recorder real time pauses
    .exec(http("Get calalog with query param")
      .get("/computers?q=books")
      .queryParam("apikey", apikey))
    .pause(2)
    .exec(http("Post Catalog Item") // Here's an example of a POST request
      .post("/catalogs")
      .queryParam("apikey", apikey)
      .headers(headers_10)
      //.body(RawFileBody("Post_Catalog.json")).asJSON)
      .body(StringBody("""{
        "name": "books",
        "description": "this is the books collection"
}       """)).asJSON)
  //setUp(scn.inject(atOnceUsers(5) over (10 seconds)).protocols(httpConf))
  setUp(scn
      .inject(rampUsers(5) over (10 seconds)).protocols(httpConf) 
  ).assertions(
        global.responseTime.max.lessThan(50), //ms
        global.successfulRequests.percent.greaterThan(95)
      )
}