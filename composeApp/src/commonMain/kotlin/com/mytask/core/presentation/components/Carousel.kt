package com.mytask.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Carousel - HorizontalPager with page indicators and optional auto-scroll.
 *
 * Usage:
 * ```
 * Carousel(
 *     pageCount = banners.size,
 *     autoScroll = true,
 *     autoScrollDelayMillis = 3000L
 * ) { page ->
 *     ImageCard(
 *         imageUrl = banners[page].imageUrl,
 *         title = banners[page].title
 *     )
 * }
 * ```
 */
@Composable
fun Carousel(
    pageCount: Int,
    modifier: Modifier = Modifier,
    autoScroll: Boolean = false,
    autoScrollDelayMillis: Long = 3000L,
    pageContent: @Composable (page: Int) -> Unit
) {
    if (pageCount <= 0) return

    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    if (autoScroll && pageCount > 1) {
        LaunchedEffect(pagerState) {
            while (true) {
                delay(autoScrollDelayMillis)
                val nextPage = (pagerState.currentPage + 1) % pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            pageContent(page)
        }

        if (pageCount > 1) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .size(
                                width = if (index == pagerState.currentPage) 24.dp else 8.dp,
                                height = 8.dp
                            )
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }
        }
    }
}

data class CarouselItem<T>(
    val id: String,
    val content: T
)
