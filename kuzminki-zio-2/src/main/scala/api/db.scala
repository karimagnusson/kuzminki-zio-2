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
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}

import zio._


object db { 

  def query[R](render: => RenderedQuery[R]): ZIO[Kuzminki, SQLException, List[R]] = {
    for {
      inst    <- Kuzminki.get
      rows    <- inst.query(render).refineToOrDie[SQLException]
    } yield rows
  }

  def queryAs[R, T](render: => RenderedQuery[R], transform: R => T): ZIO[Kuzminki, SQLException, List[T]] = {
    for {
      inst    <- Kuzminki.get
      rows    <- inst.queryAs(render, transform).refineToOrDie[SQLException]
    } yield rows
  }

  def queryHead[R](render: => RenderedQuery[R]): ZIO[Kuzminki, SQLException, R] = {
    for {
      inst    <- Kuzminki.get
      head    <- inst.queryHead(render).refineToOrDie[SQLException]
    } yield head
  }

  def queryHeadAs[R, T](render: => RenderedQuery[R], transform: R => T): ZIO[Kuzminki, SQLException, T] = {
    for {
      inst    <- Kuzminki.get
      rows    <- inst.queryHeadAs(render, transform).refineToOrDie[SQLException]
    } yield rows
  }

  def queryHeadOpt[R](render: => RenderedQuery[R]): ZIO[Kuzminki, SQLException, Option[R]] = {
    for {
      inst    <- Kuzminki.get
      headOpt <- inst.queryHeadOpt(render).refineToOrDie[SQLException]
    } yield headOpt
  }

  def queryHeadOptAs[R, T](render: => RenderedQuery[R], transform: R => T): ZIO[Kuzminki, SQLException, Option[T]] = {
    for {
      inst    <- Kuzminki.get
      headOpt <- inst.queryHeadOptAs(render, transform).refineToOrDie[SQLException]
    } yield headOpt
  }

  def exec(render: => RenderedOperation): ZIO[Kuzminki, SQLException, Unit] = {
    for {
      inst    <- Kuzminki.get
      _       <- inst.exec(render).refineToOrDie[SQLException]
    } yield ()
  }

  def execNum(render: => RenderedOperation): ZIO[Kuzminki, SQLException, Int] = {
    for {
      inst    <- Kuzminki.get
      num     <- inst.execNum(render).refineToOrDie[SQLException]
    } yield num
  }

  def execList(stms: Seq[RenderedOperation]): ZIO[Kuzminki, SQLException, Unit] = {
    for {
      inst    <- Kuzminki.get
      _       <- inst.execList(stms).refineToOrDie[SQLException]
    } yield ()
  }
}





















