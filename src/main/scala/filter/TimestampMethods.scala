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

package kuzminki.filter

import java.sql.Timestamp
import kuzminki.column.TypeCol
import kuzminki.assign._
import kuzminki.filter.types._
import kuzminki.fn.types._
import kuzminki.fn.general.Interval
import kuzminki.fn.Cast


trait TimestampMethods extends ComparativeMethods[Timestamp] {

  // filters

  def age = TimestampAgeFn(col)

  def epochSecs = ExtractEpochSecsFn(col)
  def epochMillis = ExtractEpochMillisFn(col)

  def century = ExtractCenturyFn(col)
  def day = ExtractDayFn(col)
  def decade = ExtractDecadeFn(col)
  def dow = ExtractDowFn(col)
  def doy = ExtractDoyFn(col)
  def hour = ExtractHourFn(col)
  def isoDow = ExtractIsoDowFn(col)
  def isoYear = ExtractIsoDowFn(col)
  def microseconds = ExtractMicrosecondsFn(col)
  def milliseconds = ExtractMillisecondsFn(col)
  def minute = ExtractMinuteFn(col)
  def month = ExtractMonthFn(col)
  def quarter = ExtractQuarterFn(col)
  def second = ExtractSecondFn(col)
  def week = ExtractWeekFn(col)
  def year = ExtractYearFn(col)

  def asDate = TimestampCastDateFn(col)
  def asTime = TimestampCastTimeFn(col)
  def asString = Cast.asString(col)

  def format(value: String) = TimestampStringFormatFn(col, value)

  // update

  def setNow = TimestampNow(col)
  def +=(interval: Interval) = TimestampInc(col, interval.value)
  def -=(interval: Interval) = TimestampDec(col, interval.value)
}















