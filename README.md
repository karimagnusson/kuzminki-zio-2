[![license](https://img.shields.io/github/license/rdbc-io/rdbc.svg?style=flat-square)](https://github.com/rdbc-io/rdbc/blob/master/LICENSE)
# kuzminki-zio-2

Kuzminki-zio is query builder and access library for PostgreSQL and [ZIO](https://zio.dev/) written in Scala.

This library is also available for ZIO 1 [kuzminki-zio](https://github.com/karimagnusson/kuzminki-zio)

See full documentation at [https://kuzminki.io/](https://kuzminki.io/)

#### Sbt
```sbt
libraryDependencies += "io.github.karimagnusson" % "kuzminki-zio-2" % "0.9.3"
```

#### Example
```scala
import zio._
import kuzminki.api._

object ExampleApp extends ZIOAppDefault {

  class Client extends Model("client") {
    val id = column[Int]("id")
    val username = column[String]("username")
    val age = column[Int]("age")
    def all = (id, username, age)
  }

  val client = Model.get[Client]

  val job = for {
    _ <- sql
      .insert(client)
      .cols2(t => (t.username, t.age))
      .run(("Joe", 35))
    
    _ <- sql
      .update(client)
      .set(_.age ==> 24)
      .where(_.id === 4)
      .run
    
    _ <- sql.delete(client).where(_.id === 7).run
    
    clients <- sql
      .select(client)
      .cols3(_.all)
      .where(_.age > 25)
      .limit(5)
      .run
    
    _ <- ZIO.foreach(clients) {
      case (id, username, age) =>
        Console.printLine(s"$id $username $age")
    }
  } yield ()

  val dbConfig = DDbConfig.forDb("company").getConfig
  val dbLayer = Kuzminki.layer(dbConfig)

  def run = job.provide(kuzminkiLayer)
}
```





