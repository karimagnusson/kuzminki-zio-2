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

package kuzminki.fn.types

import java.sql.Date
import kuzminki.column._
import kuzminki.fn.types._
import kuzminki.render.Prefix
import kuzminki.api.Jsonb


case class DateCastTimestampFn(col: TypeCol[Date]) extends TimestampCol {
  def name = "cast_%s".format(col.name)
  def template = "%s::timestamp"
  def render(prefix: Prefix) = template.format(col.render(prefix))
  val args = col.args
}
