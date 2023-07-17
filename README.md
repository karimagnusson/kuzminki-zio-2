[![license](https://img.shields.io/github/license/rdbc-io/rdbc.svg?style=flat-square)](https://github.com/rdbc-io/rdbc/blob/master/LICENSE)
# kuzminki-zio-2

Kuzminki is query builder and access library for PostgreSQL written in Scala.  
This version is for ZIO 2.

Kuzminki is written for those who like SQL. Queries are written with the same logic you write SQL statements. As a result the code is easy to read and memorise while the resulting SQL statement is predictable.

This library is also available for ZIO 1 [kuzminki-zio](https://github.com/karimagnusson/kuzminki-zio)  

See full documentation at [https://kuzminki.io/](https://kuzminki.io/)

Take a look at [kuzminki-zhttp-demo](https://github.com/karimagnusson/kuzminki-zhttp-demo) for an example of a REST API using this library and [zio-http](https://github.com/dream11/zio-http)

Release 0.9.4-RC4 adds the following featurees:
- Select row as JSON string
- Debug, print query
- Insert, update null values
- Timestamp, Date, Time methods

Attention! There are some changes to the API in this version. They affect cached queries.

#### Sbt
```sbt
libraryDependencies += "io.github.karimagnusson" % "kuzminki-zio-2" % "0.9.4-RC4" // ZIO 2.0.0
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
    
  } yield clients

  val dbLayer = Kuzminki.layer(DbConfig.forDb("company"))

  def run = job.provide(dbLayer)
}
```

#### In the latest push

Changes:
Improved connection pool.
Improved exceptions. Queries return typed exception SQLException.
Improved custom functions.
Added Pages.

#### Custom functions
```scala
case class FullName(
  title: String,
  first: TypeCol[String],
  second: TypeCol[String]
) extends StringFn {
  val name = "full_name"
  val template = s"concat_ws(' ', '$title', %s, %s)"
  val cols = Vector(first, second)
}

sql
  .select(user)
  .cols2(t => (
    t.id,
    FullName("Mr", t.firstName, t.lastName)
  ))
  .where(_.id === 10)
  .runHead

```
If you need to have the driver fill in arguments:
```scala
case class FullNameParam(
  title: String,
  first: TypeCol[String],
  second: TypeCol[String]
) extends StringParamsFn {
  val name = "full_name"
  val template = s"concat_ws(' ', ?, %s, %s)"
  val cols = Vector(first, second)
  val params = Vector(title)
}
```

#### Pages
```scala
val pages = sql
  .select(user)
  .cols3(t => (
    t.id,
    t.firstName,
    t.lastName)
  ))
  .orderBy(_.id.asc)
  .asPages(10) // 10 rows

val job = for {
  next  <- pages.next
  page3 <- pages.page(3)
} yield (next, page3)
```














