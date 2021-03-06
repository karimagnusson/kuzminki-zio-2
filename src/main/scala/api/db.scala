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

import zio._

import kuzminki.api._
import kuzminki.render.{
  RenderedQuery,
  RenderedOperation
}


object db { 

  def query[R](render: => RenderedQuery[R]): RIO[Kuzminki, List[R]] = {
    for {
      db   <- Kuzminki.get
      rows <- db.query(render)
    } yield rows
  }

  def queryHead[R](render: => RenderedQuery[R]): RIO[Kuzminki, R] = {
    for {
      db   <- Kuzminki.get
      head <- db.queryHead(render)
    } yield head
  }

  def queryHeadOpt[R](render: => RenderedQuery[R]): RIO[Kuzminki, Option[R]] = {
    for {
      db      <- Kuzminki.get
      headOpt <- db.queryHeadOpt(render)
    } yield headOpt
  }

  def exec(render: => RenderedOperation): RIO[Kuzminki, Unit] = {
    for {
      db <- Kuzminki.get
      _  <- db.exec(render)
    } yield ()
  }

  def execNum(render: => RenderedOperation): RIO[Kuzminki, Int] = {
    for {
      db   <- Kuzminki.get
      num  <- db.execNum(render)
    } yield num
  }
}





















