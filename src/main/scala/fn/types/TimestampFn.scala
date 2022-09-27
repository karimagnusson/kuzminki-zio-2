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

import java.sql.Time
import java.sql.Date
import java.sql.Timestamp
import kuzminki.column._
import kuzminki.render.Prefix
import kuzminki.api.Jsonb


case class TimestampAgeFn(col: TypeCol[Timestamp]) extends LongCol {
  def name = "age_%s".format(col.name)
  def template = "EXTRACT(MILLISECONDS FROM age(%s))::bigint"
  def render(prefix: Prefix) = template.format(col.render(prefix))
  val args = col.args
}

case class TimestampAgeTwoFn(col: TypeCol[Timestamp], col2: TypeCol[Timestamp]) extends LongCol {
  def name = "age_%s_%s".format(col.name, col2.name)
  def template = "EXTRACT(MILLISECONDS FROM age(%s))::bigint"
  def render(prefix: Prefix) = template.format(col.render(prefix), col2.render(prefix))
  val args = col.args ++ col2.args
}

// cast

case class TimestampCastDateFn(col: TypeCol[Timestamp]) extends DateCol {
  def name = "cast_%s".format(col.name)
  def template = "%s::date"
  def render(prefix: Prefix) = template.format(col.render(prefix))
  val args = col.args
}

case class TimestampCastTimeFn(col: TypeCol[Timestamp]) extends TimeCol {
  def name = "cast_%s".format(col.name)
  def template = "%s::time"
  def render(prefix: Prefix) = template.format(col.render(prefix))
  val args = col.args
}

case class TimestampCastStringFn(col: TypeCol[Timestamp]) extends StringCol {
  def name = "cast_%s".format(col.name)
  def template = "%s::text"
  def render(prefix: Prefix) = template.format(col.render(prefix))
  val args = col.args
}

case class TimestampStringFormatFn(col: TypeCol[Timestamp], format: String) extends StringCol {
  def name = "cast_%s".format(col.name)
  def template = "to_char(%s, ?)"
  def render(prefix: Prefix) = template.format(col.render(prefix))
  val args = col.args ++ Vector(format)
}









