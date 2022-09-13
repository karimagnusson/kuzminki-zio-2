[![license](https://img.shields.io/github/license/rdbc-io/rdbc.svg?style=flat-square)](https://github.com/rdbc-io/rdbc/blob/master/LICENSE)
# kuzminki-zio-2

Kuzminki is query builder and access library for PostgreSQL written in Scala.  
This version is for ZIO 2.

Kuzminki is written for those who like SQL. Queries are written with the same logic you write SQL statements. As a result the code is easy to read and memorise while the resulting SQL statement is predictable.

This library is also available for ZIO 1 [kuzminki-zio](https://github.com/karimagnusson/kuzminki-zio)  

See full documentation at [https://kuzminki.io/](https://kuzminki.io/)

You can take a look at [kuzminki-zhttp-demo](https://github.com/karimagnusson/kuzminki-zhttp-demo) for a example of a REST API using this library and [zio-http](https://github.com/dream11/zio-http)

Release 0.9.4-RC2 adds the following featurees:
- Support for jsonb field
- Support for uuid field
- Streaming from and to the database
- Support for transactions
- Ability to use postgres functions
- Improved ability to cache statements


Attention! There are some changes to the API in this version. They affect INSERT, UPDATE and DELETE.

#### Sbt
```sbt
libraryDependencies += "io.github.karimagnusson" % "kuzminki-zio-2" % "0.9.4-RC2" // ZIO 2.0.0
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
      .values(("Joe", 35))
      .run
    
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

  val dbLayer = Kuzminki.layer(DbConfig.forDb("company"))

  def run = job.provide(dbLayer)
}
```

