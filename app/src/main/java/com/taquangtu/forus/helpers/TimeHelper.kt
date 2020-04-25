package com.taquangtu.forus.helpers

class TimeHelper {
    companion object{
        fun greaterThan3Days(time1: Long, time2:Long):Boolean{
            val abs = if (time1>time2) (time1 - time2) else (time2 - time1)
            return abs> 259200000
        }
    }
}