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

package kuzminki.select

import java.sql.SQLException
import zio.stream.ZStream
import kuzminki.api.Kuzminki
import kuzminki.section.OffsetSec


class Offset[M, R](model: M, coll: SelectCollector[R]) extends Limit(model, coll) {

  def offset(num: Int) = {
    new Limit(
      model,
      coll.add(
        OffsetSec(num)
      )
    )
  }

  def asPages(size: Int) = Pages(render, size)

  def stream: ZStream[Kuzminki, SQLException, R] = stream(100)

  def stream(size: Int): ZStream[Kuzminki, SQLException, R] = {
    val gen = new StreamQuery(asPages(size))
    ZStream.unfoldChunkZIO(gen)(a => a.next)
  }

  @deprecated("this method will be removed, Use 'stream(size = 200)'", "0.9.5")
  def streamBatch(size: Int) = stream(size)

  @deprecated("this method will be removed", "0.9.5")
  def streamBatchBuffer(batchSize: Int, bufferSize: Int) = {
    streamBatch(batchSize).buffer(bufferSize)
  }
}