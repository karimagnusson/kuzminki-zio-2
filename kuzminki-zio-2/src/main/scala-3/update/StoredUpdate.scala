/*
* Copyright 2021 Kári Magnússon
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package kuzminki.update

import java.sql.SQLException
import scala.deriving.Mirror.ProductOf
import kuzminki.api.{db, Kuzminki}
import kuzminki.shape.{ParamConv, RowConv}
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation,
  JoinArgs
}
import zio._
import zio.stream.ZSink


class StoredUpdate[P1, P2](
  val statement: String,
  args: Vector[Any],
  changes: ParamConv[P1],
  filters: ParamConv[P2]
) extends JoinArgs {

  def render(p1: P1, p2: P2) = RenderedOperation(
    statement,
    joinArgs(args, changes.fromShape(p1) ++ filters.fromShape(p2))
  )

  def run(p1: P1, p2: P2): ZIO[Kuzminki, SQLException, Unit] =
    db.exec(render(p1, p2))

  def runNum(p1: P1, p2: P2): ZIO[Kuzminki, SQLException, Int] =
    db.execNum(render(p1, p2))

  def runList(list: Seq[Tuple2[P1, P2]]): ZIO[Kuzminki, SQLException, Unit] =
    db.execList(list.map(p => render(p._1, p._2)))

  def collect(size: Int): ZSink[Any, Nothing, Tuple2[P1, P2], Tuple2[P1, P2], Chunk[Tuple2[P1, P2]]] =
    ZSink.collectAllN[Tuple2[P1, P2]](size)

  def asSink: ZSink[Kuzminki, SQLException, Tuple2[P1, P2], Nothing, Unit] = {
    ZSink.foreach { (p: Tuple2[P1, P2]) =>
      db.exec(render(p._1, p._2))
    }
  }

  def asChunkSink: ZSink[Kuzminki, SQLException, Chunk[Tuple2[P1, P2]], Nothing, Unit] = {
    ZSink.foreach { (chunk: Chunk[Tuple2[P1, P2]]) =>
      db.execList(chunk.toList.map(p => render(p._1, p._2)))
    }
  } 

  def printSql: StoredUpdate[P1, P2] = {
    println(statement)
    this
  }
    
  def printSqlAndArgs(p1: P1, p2: P2): StoredUpdate[P1, P2] =
    render(p1, p2).printStatementAndArgs(this)
    
  def printSqlWithArgs(p1: P1, p2: P2): StoredUpdate[P1, P2] =
    render(p1, p2).printStatementWithArgs(this)
}


class StoredUpdateReturning[P1, P2, R](
  val statement: String,
  args: Vector[Any],
  changes: ParamConv[P1],
  filters: ParamConv[P2],
  rowConv: RowConv[R]
) extends JoinArgs {

  def render(p1: P1, p2: P2) = RenderedQuery(
    statement,
    joinArgs(args, changes.fromShape(p1) ++ filters.fromShape(p2)),
    rowConv
  )

  def run(p1: P1, p2: P2): ZIO[Kuzminki, SQLException, List[R]] =
    db.query(render(p1, p2))

  def runHead(p1: P1, p2: P2): ZIO[Kuzminki, SQLException, R] =
    db.queryHead(render(p1, p2))

  def runHeadOpt(p1: P1, p2: P2): ZIO[Kuzminki, SQLException, Option[R]] =
    db.queryHeadOpt(render(p1, p2))

  def runAs[T](p1: P1, p2: P2)(
    using transform: R => T
  ): ZIO[Kuzminki, SQLException, List[T]] =
    db.queryAs(render(p1, p2), transform)

  def runHeadAs[T](p1: P1, p2: P2)(
    using transform: R => T
  ): ZIO[Kuzminki, SQLException, T] =
    db.queryHeadAs(render(p1, p2), transform)

  def runHeadOptAs[T](p1: P1, p2: P2)(
    using transform: R => T
  ): ZIO[Kuzminki, SQLException, Option[T]] =
    db.queryHeadOptAs(render(p1, p2), transform)

  def runType[T](p1: P1, p2: P2)(
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes
  ): ZIO[Kuzminki, SQLException, List[T]] = {
    db.queryAs(render(p1, p2), (r: R) => mirror.fromProduct(r))
  }

  def runHeadType[T](p1: P1, p2: P2)(
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes
  ): ZIO[Kuzminki, SQLException, T] =
    db.queryHeadAs(render(p1, p2), (r: R) => mirror.fromProduct(r))

  def runHeadOptType[T](p1: P1, p2: P2)(
    using mirror: ProductOf[T],
          ev: R <:< mirror.MirroredElemTypes
  ): ZIO[Kuzminki, SQLException, Option[T]] = {
    db.queryHeadOptAs(render(p1, p2), (r: R) => mirror.fromProduct(r))
  }

  def printSql: StoredUpdateReturning[P1, P2, R] = {
    println(statement)
    this
  }
    
  def printSqlAndArgs(p1: P1, p2: P2): StoredUpdateReturning[P1, P2, R] =
    render(p1, p2).printStatementAndArgs(this)
    
  def printSqlWithArgs(p1: P1, p2: P2): StoredUpdateReturning[P1, P2, R] =
    render(p1, p2).printStatementWithArgs(this)
}








