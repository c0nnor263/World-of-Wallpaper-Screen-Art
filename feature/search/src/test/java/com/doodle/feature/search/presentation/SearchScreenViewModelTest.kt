package com.doodle.feature.search.presentation

import com.doodle.core.data.domain.ApplicationPagingDataStore
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.SearchNavArgs
import com.doodle.core.domain.source.remote.repository.SearchImagePagingRepository
import com.doodle.core.testing.rules.MainCoroutineRule
import com.doodle.feature.search.data.source.local.repository.FakeSearchImagePagingRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config

@RunWith(MockitoJUnitRunner::class)
@Config(manifest = Config.NONE)
class SearchScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SearchScreenViewModel
    private val applicationPagingDataStore: ApplicationPagingDataStore = mock()
    private val searchImagePagingRepository: SearchImagePagingRepository =
        FakeSearchImagePagingRepositoryImpl()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

    @Before
    fun setup() {
        viewModel = SearchScreenViewModel(searchImagePagingRepository, applicationPagingDataStore)
    }

    @Test
    fun setSearchState_shouldUpdateSearchQueryState() = runTest {
        val query = "test_query"
        val pagingKey = PagingKey.LATEST
        val navArgs = SearchNavArgs(query, pagingKey)
        viewModel.setSearchState(navArgs)
        assertEquals(query, viewModel.searchQueryState.query.value)
        assertEquals(pagingKey, viewModel.searchQueryState.pagingKey)
    }

    @Test
    fun setSearchState_blankQuery_return() = runTest {
        val query = ""
        val pagingKey = PagingKey.LATEST
        val navArgs = SearchNavArgs(query, pagingKey)
        viewModel.setSearchState(navArgs)
        assertEquals(query, viewModel.searchQueryState.query.value)
        assertNotEquals(pagingKey, viewModel.searchQueryState.pagingKey)
    }

    @Test
    fun setSearchState_sameQuery_return() = runTest {
        val testQuery = "test_query"
        viewModel.searchQueryState.updateQuery(testQuery)

        val pagingKey = PagingKey.LATEST
        val navArgs = SearchNavArgs(testQuery, pagingKey)
        viewModel.setSearchState(navArgs)
        assertEquals(testQuery, viewModel.searchQueryState.query.value)
        assertNotEquals(pagingKey, viewModel.searchQueryState.pagingKey)
    }
}
