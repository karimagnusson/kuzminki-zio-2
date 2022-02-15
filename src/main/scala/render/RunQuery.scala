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

package kuzminki.render

import zio._
import kuzminki.api.{db, Kuzminki}


trait RunQuery[R] {

  def render: RenderedQuery[R]

  def run: RIO[Kuzminki, List[R]] =
    db.query(render)

  def runHead: RIO[Kuzminki, R] =
    db.queryHead(render)

  def runHeadOpt: RIO[Kuzminki, Option[R]] =
    db.queryHeadOpt(render)
}


trait RunQueryParams[P, R] {

  def render(params: P): RenderedQuery[R]

  def run(params: P): RIO[Kuzminki, List[R]] =
    db.query(render(params))

  def runHead(params: P): RIO[Kuzminki, R] =
    db.queryHead(render(params))

  def runHeadOpt(params: P): RIO[Kuzminki, Option[R]] =
    db.queryHeadOpt(render(params))
}


















