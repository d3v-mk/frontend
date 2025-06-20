package com.panopoker.ui.lobby.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.panopoker.R

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CarrosselLoby(
    imagens: List<Int>,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit = {} // callback pro clique na imagem
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    // Troca automática
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % imagens.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        HorizontalPager(
            count = imagens.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 0.dp),
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(0.75f) // ou o que preferir
        ) { page ->
            Image(
                painter = painterResource(id = imagens[page]),
                contentDescription = "Imagem $page",
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick(page) }
            )
        } // <3

        // Indicadores (bolinhas)
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .padding(top = 8.dp)
                .height(16.dp),
            activeColor = androidx.compose.ui.graphics.Color.White,
            inactiveColor = androidx.compose.ui.graphics.Color.Gray
        )
    }
}
