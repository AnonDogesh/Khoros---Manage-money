package com.khoros.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.khoros.app.data.local.KhorosDatabase
import com.khoros.app.data.model.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RoomDatabaseTest {
    private lateinit var db: KhorosDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            KhorosDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndReadTransaction() = runBlocking {
        db.transactionDao().insert(TransactionEntity(type = "Expense", category = "Food", amount = 200f, date = "2026-01-01"))
        val items = db.transactionDao().observeTransactions().first()
        assertEquals(1, items.size)
        assertEquals("Food", items.first().category)
    }
}
