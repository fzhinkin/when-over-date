package org.example

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.datetime.LocalDate
import kotlin.random.Random

class DayValue(f1: String, f2: String)

fun get(date: LocalDate): DayValue {
    return when (date) {
        LocalDate(2024, 5, 22) -> DayValue("Workshop", "Day")
        LocalDate(2024, 5, 23) -> DayValue("Conference", "Day 1")
        LocalDate(2024, 5, 24) -> DayValue("Conference", "Day 2")
        else -> DayValue("", "")
    }
}

object Dates {
    val day1 = LocalDate(2024, 5, 22)
    val day2 = LocalDate(2024, 5, 23)
    val day3 = LocalDate(2024, 5, 24)
}

fun getComparingToConstants(date: LocalDate): DayValue {
    return when (date) {
        Dates.day1 -> DayValue("Workshop", "Day")
        Dates.day2 -> DayValue("Conference", "Day 1")
        Dates.day3 -> DayValue("Conference", "Day 2")
        else -> DayValue("", "")
    }
}

fun getWithScopedConstants(date: LocalDate): DayValue {
    val day1 = LocalDate(2024, 5, 22)
    val day2 = LocalDate(2024, 5, 23)
    val day3 = LocalDate(2024, 5, 24)
    return when (date) {
        day1 -> DayValue("Workshop", "Day")
        day2 -> DayValue("Conference", "Day 1")
        day3 -> DayValue("Conference", "Day 2")
        else -> DayValue("", "")
    }
}

fun getLikeWeAreRunningOutOfMemory(date: LocalDate): DayValue {
    if (date.year == 2024 && date.monthNumber == 5) {
         when (date.dayOfMonth) {
            22 -> return DayValue("Workshop", "Day")
            23 -> return DayValue("Conference", "Day 1")
            24 -> return DayValue("Conference", "Day 2")
        }
    }
    return DayValue("", "")
}

@State(Scope.Benchmark)
open class WhenOverObjects {
    private val dates = Array<LocalDate>(30) {
        LocalDate(2024, 5, it + 1)
    }.apply { shuffle(Random(42)) }

    @Benchmark
    fun allocateDateInWhenClause(blackhole: Blackhole) {
        dates.forEach {
            blackhole.consume(get(it))
        }
    }

    @Benchmark
    fun createPreAllocatedConstants(blackhole: Blackhole) {
        dates.forEach {
            blackhole.consume(getComparingToConstants(it))
        }
    }

    @Benchmark
    fun createConstantsWithinFunction(blackhole: Blackhole) {
        dates.forEach {
            blackhole.consume(getWithScopedConstants(it))
        }
    }

    @Benchmark
    fun dontAllocateLocalDates(blackhole: Blackhole) {
        dates.forEach {
            blackhole.consume(getLikeWeAreRunningOutOfMemory(it))
        }
    }
}
