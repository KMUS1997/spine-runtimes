package com.esotericsoftware.spine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.esotericsoftware.spine.android.AndroidSkeletonDrawable
import com.esotericsoftware.spine.android.AndroidTextureAtlas
import com.esotericsoftware.spine.android.SpineController
import com.esotericsoftware.spine.android.SpineView
import com.esotericsoftware.spine.android.utils.SkeletonDataUtils
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheBoys(nav: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = Destination.TheBoys.title) },
                navigationIcon = {
                    IconButton({ nav.navigateUp() }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            null,
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        var viewportSize by remember { mutableStateOf(Size.Zero) }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    viewportSize = coordinates.size.toSize()
                }
        ) {
            if (viewportSize != Size.Zero) {
                val contentSize = Size(viewportSize.width * 2, viewportSize.height * 2)

                val context = LocalContext.current
                val cachedAtlas = remember { AndroidTextureAtlas.fromAsset("spineboy.atlas", context) }
                val cachedSkeletonData = remember { SkeletonDataUtils.fromAsset(cachedAtlas, "spineboy-pro.json", context) }

                val spineboys = remember {
                    val rng = Random(System.currentTimeMillis())
                    List(100) {
                        val scale = 0.1f + rng.nextFloat() * 0.2f
                        val position = Offset(rng.nextFloat() * contentSize.width, rng.nextFloat() * contentSize.height)
                        SpineBoyData(scale, position, "walk")
                    }
                }

                Box(
                    modifier = Modifier
                        .size(contentSize.width.dp, contentSize.height.dp)
                ) {
                    spineboys.forEach { spineBoyData ->
                        AndroidView(
                            modifier = Modifier
                                .scale(spineBoyData.scale)
                                .offset(
                                    -(contentSize.width / 2).dp + spineBoyData.position.x.dp,
                                    -(contentSize.height / 2).dp + spineBoyData.position.y.dp
                                ),
                            factory = { ctx ->
                                SpineView.loadFromDrawable(
                                    AndroidSkeletonDrawable(cachedAtlas, cachedSkeletonData),
                                    ctx,
                                    SpineController {
                                        it.animationState.setAnimation(0, spineBoyData.animation, true)
                                    }
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

data class SpineBoyData(
    val scale: Float,
    val position: Offset,
    val animation: String
)

