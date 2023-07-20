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

package kuzminki.api

import java.sql.SQLException
import kuzminki.api._
import kuzminki.jdbc.SingleConnection
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}

import zio._


object Kuzminki {

  Class.forName("org.postgresql.Driver")

  private def createPool(conf: DbConfig): RIO[Any, Pool] = for {
    connections <- ZIO.foreach(1 to conf.poolSize) { _ =>
                     ZIO.attemptBlocking {
                       SingleConnection.create(conf.url, conf.props)
                     }
                   }
    queue       <- Queue.bounded[SingleConnection](conf.poolSize)
    _           <- queue.offerAll(connections)
  } yield new Pool(queue, connections.toList)

  def forConfig(conf: DbConfig) = create(conf)

  def create(conf: DbConfig): RIO[Any, Kuzminki] = for {
    pool <- createPool(conf)
  } yield new DefaultApi(pool)

  def layer(conf: DbConfig): ZLayer[Any, Throwable, Kuzminki] = {
    ZLayer.scoped(ZIO.acquireRelease(create(conf))(_.close))
  }
  
  def createSplit(getConf: DbConfig,
                  setConf: DbConfig): RIO[Any, Kuzminki] = for {
    getPool <- createPool(getConf)
    setPool <- createPool(setConf)
  } yield new SplitApi(getPool, setPool)

  def layerSplit(getConf: DbConfig,
                 setConf: DbConfig): ZLayer[Any, Throwable, Kuzminki] = {
    ZLayer.scoped(ZIO.acquireRelease(createSplit(getConf, setConf))(_.close))
  }
  
  def get = ZIO.service[Kuzminki]
}


trait Kuzminki {

  def query[R](render: => RenderedQuery[R]): Task[List[R]]

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T): Task[List[T]]

  def queryHead[R](render: => RenderedQuery[R]): Task[R]

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T): Task[T]

  def queryHeadOpt[R](render: => RenderedQuery[R]): Task[Option[R]]

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T): Task[Option[T]]

  def exec(render: => RenderedOperation): Task[Unit]

  def execNum(render: => RenderedOperation): Task[Int]

  def execList(stms: Seq[RenderedOperation]): Task[Unit]

  def close: URIO[Any, Unit]
}


private class Pool(queue: Queue[SingleConnection], all: List[SingleConnection]) {
  
  def get = ZIO.scoped(ZIO.acquireRelease(queue.take)(queue.offer(_)))

  def close = for {
    _ <- ZIO.foreach(all)(_.close()).orDie
    _ <- queue.shutdown
  } yield ()
}


private class DefaultApi(pool: Pool) extends Kuzminki {

  def query[R](render: => RenderedQuery[R]) = for {
    stm     <- ZIO.attempt(render)
    rows    <- pool.get.flatMap(_.query(stm))
  } yield rows

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T) =
    query(render).map(_.map(transform))

  def queryHead[R](render: => RenderedQuery[R]) = 
    query(render).map(_.headOption.getOrElse(throw NoRowsException("Query returned no rows")))

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T) =
    queryHead(render).map(transform)

  def queryHeadOpt[R](render: => RenderedQuery[R]): Task[Option[R]] =
    query(render).map(_.headOption)

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T) =
    query(render).map(_.headOption.map(transform))

  def exec(render: => RenderedOperation) = for {
    stm     <- ZIO.attempt(render)
    _       <- pool.get.flatMap(_.exec(stm))
  } yield ()

  def execNum(render: => RenderedOperation) = for {
    stm     <- ZIO.attempt(render)
    num     <- pool.get.flatMap(_.execNum(stm))
  } yield num

  def execList(stms: Seq[RenderedOperation]) = for {
    _       <- pool.get.flatMap(_.execList(stms))
  } yield ()

  def close = pool.close
}


private class SplitApi(getPool: Pool, setPool: Pool) extends Kuzminki {

  private def router(stm: String) = stm.split(" ").head match {
    case "SELECT" => getPool
    case _ => setPool
  }

  def query[R](render: => RenderedQuery[R]) = for {
    stm     <- ZIO.attempt(render)
    rows    <- router(stm.statement).get.flatMap(_.query(stm))
  } yield rows

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T) =
    query(render).map(_.map(transform))

  def queryHead[R](render: => RenderedQuery[R]) = 
    query(render).map(_.headOption.getOrElse(throw NoRowsException("Query returned no rows")))

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T) =
    queryHead(render).map(transform)

  def queryHeadOpt[R](render: => RenderedQuery[R]) =
    query(render).map(_.headOption)

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T) =
    query(render).map(_.headOption.map(transform))

  def exec(render: => RenderedOperation) = for {
    stm     <- ZIO.attempt(render)
    _       <- setPool.get.flatMap(_.exec(stm))
  } yield ()

  def execNum(render: => RenderedOperation) = for {
    stm     <- ZIO.attempt(render)
    num     <- setPool.get.flatMap(_.execNum(stm))
  } yield num

  def execList(stms: Seq[RenderedOperation]) = for {
    _       <- setPool.get.flatMap(_.execList(stms))
  } yield ()

  def close = for {
    _ <- getPool.close
    _ <- setPool.close
  } yield ()
}
























