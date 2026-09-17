package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.HisnContentProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("حصن المسلم", appName)
    }

    @Test
    fun `verify categories and adhkar provider`() {
        val categories = HisnContentProvider.categories
        assertTrue("Categories should not be empty", categories.isNotEmpty())
        assertTrue("Categories count should be 19", categories.size >= 19)

        val morningDhikrs = HisnContentProvider.getDhikrsByCategory("morning")
        assertTrue("Morning dhikrs should exist", morningDhikrs.isNotEmpty())

        val eveningDhikrs = HisnContentProvider.getDhikrsByCategory("evening")
        assertTrue("Evening dhikrs should exist", eveningDhikrs.isNotEmpty())

        val ruqyahDhikrs = HisnContentProvider.getDhikrsByCategory("ruqyah")
        assertTrue("Ruqyah dhikrs should exist", ruqyahDhikrs.isNotEmpty())
    }

    @Test
    fun `verify search returns matching dhikr`() {
        val searchResults = HisnContentProvider.searchDhikrs("الكرسي")
        assertTrue("Search for Ayat Al-Kursi should return results", searchResults.isNotEmpty())
        assertTrue(searchResults.any { it.title.contains("الكرسي") })
    }

    @Test
    fun `verify authentic dhikr fields are populated`() {
        val masterOfIstighfar = HisnContentProvider.getDhikrById(104)
        assertNotNull(masterOfIstighfar)
        assertEquals("سيد الاستغفار", masterOfIstighfar!!.title)
        assertTrue(masterOfIstighfar.source.contains("البخاري"))
        assertTrue(masterOfIstighfar.benefit.isNotEmpty())
        assertEquals(1, masterOfIstighfar.count)
    }
}
