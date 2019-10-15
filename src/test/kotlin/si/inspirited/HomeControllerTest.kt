
package si.inspirited

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.ui.ModelMap
import org.springframework.validation.MapBindingResult
import org.springframework.validation.ObjectError

@ExtendWith(MockKExtension::class)
class HomeControllerTest {

    private lateinit var map: ModelMap
    private lateinit var ctrl: HomeController
    private lateinit var repository: RecordRepository

    @BeforeEach
    fun setUp() {
        map = ModelMap()
        repository = mockk(relaxed = true)
        ctrl = HomeController(repository)
    }

    @Nested
    internal inner class Home {

        @Test
        fun shouldAddInsertRecordToModelMap() {
            ctrl.home(map)

            assertThat(map).containsKey("insertRecord")
            assertThat(map["insertRecord"]).isInstanceOf(Record::class.java)

            val insertRecord = map["insertRecord"] as Record
            assertThat(insertRecord.data).isNull()
        }

        @Test
        fun shouldQueryRepositoryForAllRecords() {
            ctrl.home(map)

            verify(exactly = 1) { repository.findAll() }
            confirmVerified(repository)
        }

        @Test
        fun shouldAddRecordsFromRepositoryToModelMap() {
            every { repository.findAll() }.returns(listOf(Record(), Record(), Record()))

            ctrl.home(map)

            assertThat(map).containsKey("records")
            assertThat(map["records"]).isInstanceOf(List::class.java)

            @Suppress("UNCHECKED_CAST")
            val records = map["records"] as List<Record>
            assertThat(records).hasSize(3)
        }
    }

    @Nested
    internal inner class InsertData {

        private lateinit var bindingResult: MapBindingResult
        private val record = Record()

        @BeforeEach
        fun setUp() {
            bindingResult = MapBindingResult(mutableMapOf<Any, Any>(), "insertRecord")
            every { repository.save(allAny<Record>()) }.returns(record)
        }

        @Test
        fun shouldSaveRecordWhenThereAreNoErrors() {
            insertData(record)

            verify(exactly = 1) { repository.save(record) }
        }

        @Test
        fun shouldNotSaveRecordWhenThereAreErrors() {
            bindingResult.addError(ObjectError("", ""))

            insertData(Record())

            verify(exactly = 0) { repository.save(allAny<Record>()) }
        }

        @Test
        fun shouldAddNewInsertRecordToModelMap() {
            insertData(record)

            assertThat(map).containsKey("insertRecord")
            assertThat(map["insertRecord"]).isNotSameAs(record)
        }

        @Test
        fun shouldAddRecordsToModelMap() {
            insertData(record)

            assertThat(map).containsKey("records")
        }

        private fun insertData(record: Record) {
            ctrl.insertData(map, record, bindingResult)
        }
    }
}
