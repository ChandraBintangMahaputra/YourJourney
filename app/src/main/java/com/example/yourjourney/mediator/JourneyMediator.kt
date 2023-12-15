package com.example.yourjourney.mediator


import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.yourjourney.data.JourneyService
import com.example.yourjourney.data.dcRemoteKeys
import com.example.yourjourney.database.AppDatabase
import com.example.yourjourney.database.JourneyDb
import com.example.yourjourney.extention.Value.INITIAL_PAGE_INDEX
import com.example.yourjourney.map.JourneyToEntity
import com.example.yourjourney.map.journeyToWidget
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class JourneyMediator(
    private val database: AppDatabase,
    private val service: JourneyService,
    private val token: String
) : RemoteMediator<Int, JourneyDb>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, JourneyDb>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            database.getWidgetContentDao().deleteAllWidgets()

            val responseData = service.getAllJourney(token, page, state.config.pageSize)
            val storyList = responseData.listStory.map {
                journeyToWidget(it)
            }
            database.getWidgetContentDao().insertNewWidgets(storyList)

            val endOfPagination = responseData.listStory.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getRemoteKeysDao().deleteRemoteKeys()
                    database.getStoryDao().deleteAllStories()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPagination) null else page + 1
                val keys = responseData.listStory.map {
                    dcRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.getRemoteKeysDao().insertAll(keys)

                responseData.listStory.forEach {
                    val storyEntity = JourneyToEntity(it)

                    database.getStoryDao().insertStories(storyEntity)
                }
            }
            return MediatorResult.Success(endOfPagination)
        } catch (ex: Exception) {
            Timber.e(ex, "Error loading data: ${ex.message}")
            return MediatorResult.Error(ex)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, JourneyDb>): dcRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.getRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, JourneyDb>): dcRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.getRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, JourneyDb>): dcRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.getRemoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
}