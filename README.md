[![license](https://img.shields.io/github/license/rdbc-io/rdbc.svg?style=flat-square)](https://github.com/rdbc-io/rdbc/blob/master/LICENSE)
# kuzminki-zio-2

Kuzminki is query builder and access library for PostgreSQL written in Scala.  
This version is for ZIO 2.

Kuzminki is written for those who like SQL. Queries are written with the same logic you write SQL statements. As a result the code is easy to read and memorise while the resulting SQL statement is predictable.

This library is also available for ZIO 1 [kuzminki-zio](https://github.com/karimagnusson/kuzminki-zio)  
And For Akka [kuzminki-akka](https://github.com/karimagnusson/kuzminki-akka)

See full documentation at [https://kuzminki.io/](https://kuzminki.io/)

#### Sbt
```sbt
libraryDependencies += "io.github.karimagnusson" % "kuzminki-zio-2" % "0.9.4-RC1" // ZIO 2.0.0
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

  val dbLayer = Kuzminki.layer(DbConfig.forDb("company"))

  def run = job.provide(dbLayer)
}
```

#### Streaming
Streaming is available in the latest push, not in version 0.9.4-RC1.

Streaming from the database.
```scala
sql
  .select(client)
  .cols3(_.all)
  .all
  .orderBy(_.id.asc)
  .stream
  .map(makeLine)
  .run(fileSink(Paths.get("clints.txt")))
```

Streaming into the database. The same logic can be used for UPDATE and DELETE.
```scala
val insertStm = sql
  .insert(client)
  .cols2(t => (t.username, t.age))
  .cache

// insert one at a time.
readFileIntoStream("clints.txt")
  .map(makeTupleFromLine)
  .run(insertStm.asSink)

// insert in chunks of 100 using transaction.
readFileIntoStream("clints.txt")
  .map(makeTupleFromLine)
  .transduce(testStm.collect(100))
  .run(insertStm.asChunkSink)
```

#### Transaction
Transaction is available in the latest push, not in version 0.9.4-RC1.

Do INSERT, UPDATE and DELETE in one transaction.
```scala
sql.transaction(
  sql.insert(client).cols2(t => (t.username, t.age)).render(("Joe", 25)),
  sql.update(client).set(_.age ==> 31).where(_.id === 45),
  sql.delete(client).where(_.id === 83)
).run
```

Insert many rows in one transaction. The same logic can be used for UPDATE and DELETE.
```scala
val clientList: List[Tuple2[String, Int]] = //...

insertStm.execList(clientList)
```




