package com.dicoding.habitapp.data

import android.content.Context
import android.nfc.tech.MifareUltralight
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dicoding.habitapp.utils.HabitSortType
import com.dicoding.habitapp.utils.SortUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HabitRepository(private val habitDao: HabitDao, private val executor: ExecutorService) {

    companion object {

        @Volatile
        private var instance: HabitRepository? = null

        fun getInstance(context: Context): HabitRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    val database = HabitDatabase.getInstance(context)
                    instance = HabitRepository(
                        database.habitDao(),
                        Executors.newSingleThreadExecutor()
                    )
                }
                return instance as HabitRepository
            }

        }
    }

    //TODO 4 : Use SortUtils.getSortedQuery to create sortable query and build paged list
    fun getHabits(filter: HabitSortType): LiveData<PagedList<Habit>> {
        val filHabit = SortUtils.getSortedQuery(filter)
        val filteredHabit = habitDao.getHabits(filHabit)

        val conf = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(MifareUltralight.PAGE_SIZE)
            .setPageSize(MifareUltralight.PAGE_SIZE)
            .build()

        return LivePagedListBuilder(filteredHabit, conf).build()
    }

    //TODO 5 : Complete other function inside repository
    fun getHabitById(idHabit: Int): LiveData<Habit> = habitDao.getHabitById(idHabit)

    fun insertHabit(habitBaru: Habit) {
        executor.execute {
            habitDao.insertHabit(habitBaru)
        }
    }

    fun deleteHabit(habit: Habit) {
        executor.execute {
            habitDao.deleteHabit(habit)
        }
    }

    fun getRandomHabitByPriorityLevel(level: String): LiveData<Habit> = habitDao.getRandomHabitByPriorityLevel(level)
}