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

package kuzminki.filter.types

import kuzminki.filter.Filter
import kuzminki.conv.ValConv
import kuzminki.render.Prefix
import kuzminki.render.Renderable


trait SubqueryFilter extends Filter {
  val col: Renderable
  val sub: Renderable
  def render(prefix: Prefix) = template.format(col.render(prefix), sub.render(prefix))
  val args = col.args ++ sub.args
}

case class FilterInSubquery(col: Renderable, sub: Renderable) extends SubqueryFilter {
  val template = "%s = ANY(%s)"
}

case class FilterNotInSubquery(col: Renderable, sub: Renderable) extends SubqueryFilter {
  val template = "%s != ANY(%s)"
}

// cache

case class CacheInSubquery[P](
  col: Renderable,
  sub: Renderable,
  conv: ValConv[P]
) extends CacheFilterSub[P] {
  val template = "%s = ANY(%s)"
}

case class CacheNotInSubquery[P](
  col: Renderable,
  sub: Renderable,
  conv: ValConv[P]
) extends CacheFilterSub[P] {
  val template = "%s != ANY(%s)"
}











