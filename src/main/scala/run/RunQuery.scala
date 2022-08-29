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

package kuzminki.run

import zio._
import kuzminki.api.db
import kuzminki.render.RenderedQuery


trait RunQuery[R] {

  def render: RenderedQuery[R]

  def run = db.query(render)

  def runAs[T](implicit transform: R => T) =
    db.queryAs(render, transform)

  def runHead = db.queryHead(render)

  def runHeadAs[T](implicit transform: R => T) =
    db.queryHeadAs(render, transform)

  def runHeadOpt = db.queryHeadOpt(render)

  def runHeadOptAs[T](implicit transform: R => T) =
    db.queryHeadOptAs(render, transform)
}


trait RunQueryParams[P, R] {

  def render(params: P): RenderedQuery[R]

  def run(params: P) = db.query(render(params))

  def runAs[T](params: P)(implicit transform: R => T) =
    db.queryAs(render(params), transform)

  def runHead(params: P) = db.queryHead(render(params))

  def runHeadAs[T](params: P)(implicit transform: R => T) =
    db.queryHeadAs(render(params), transform)

  def runHeadOpt(params: P) = db.queryHeadOpt(render(params))

  def runHeadOptAs[T](params: P)(implicit transform: R => T) =
    db.queryHeadOptAs(render(params), transform)
}

















