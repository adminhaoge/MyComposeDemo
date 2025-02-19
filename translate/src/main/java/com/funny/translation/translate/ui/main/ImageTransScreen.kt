package com.funny.translation.translate.ui.main

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.qhplus.emo.photo.activity.PhotoPickResult
import cn.qhplus.emo.photo.activity.PhotoPickerActivity
import cn.qhplus.emo.photo.activity.getPhotoPickResult
import cn.qhplus.emo.photo.coil.CoilMediaPhotoProviderFactory
import cn.qhplus.emo.photo.ui.GestureContent
import cn.qhplus.emo.photo.ui.GestureContentState
import com.funny.compose.loading.LoadingState
import com.funny.jetsetting.core.ui.SimpleDialog
import com.funny.translation.AppConfig
import com.funny.translation.helper.BitmapUtil
import com.funny.translation.helper.Log
import com.funny.translation.helper.toastOnUi
import com.funny.translation.translate.FunnyApplication
import com.funny.translation.translate.Language
import com.funny.translation.translate.R
import com.funny.translation.translate.activity.CustomPhotoPickerActivity
import com.funny.translation.translate.appCtx
import com.funny.translation.translate.enabledLanguages
import com.funny.translation.translate.engine.ImageTranslationEngine
import com.funny.translation.translate.findLanguageById
import com.funny.translation.translate.ui.widget.AutoResizedText
import com.funny.translation.translate.ui.widget.CustomCoilProvider
import com.funny.translation.translate.ui.widget.ExchangeButton
import com.funny.translation.ui.FixedSizeIcon
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File
import kotlin.math.min

private const val TAG = "ImageTransScreen"

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ImageTransScreen(
    imageUri: Uri? = null,
    sourceId: Int? = null,
    targetId: Int? = null,
    doClipFirst: Boolean = false
) {
    val vm: ImageTransViewModel = viewModel()
    val context = LocalContext.current
    val imagePickResult: MutableState<PhotoPickResult?> = remember {
        mutableStateOf(null)
    }
    var photoName by rememberSaveable { mutableStateOf("") }
    val currentEnabledLanguages by enabledLanguages.collectAsState()

    DisposableEffect(key1 = Unit) {
        if (!AppConfig.userInfo.value.isValid()) {
            context.toastOnUi(R.string.login_to_use_image_translation, Toast.LENGTH_LONG)
        }
        onDispose {
            vm.imageUri = null
            vm.cancel()
        }
    }

    val clipperLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { intent ->
                    val uri = UCrop.getOutput(intent)
                    vm.updateImageUri(uri)
                    val width = UCrop.getOutputImageWidth(intent)
                    val height = UCrop.getOutputImageHeight(intent)
                    vm.updateImgSize(width, height)
                }
            }
        }

    val doClip = remember {
        { uri: Uri ->
            clipperLauncher.launch(
                UCrop.of(uri, DESTINATION_IMAGE_URI)
                    .withOptions(UCrop.Options().apply {
                        setCompressionFormat(Bitmap.CompressFormat.JPEG)
                    })
                    .getIntent(context)
            )
            imagePickResult.value = null
        }
    }

    val pickLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.getPhotoPickResult()?.let { result ->
                    imagePickResult.value = result
                    val img = result.list[0]
                    photoName = img.name
                    doClip(img.uri)
                }
            }
        }

    // 如果进入页面时参数携带了图片uri
    LaunchedEffect(key1 = imageUri) {
        if (imageUri == null) return@LaunchedEffect
        // 如果不需要裁剪，那么直接翻译
        if (!doClipFirst) {
            vm.imageUri = imageUri
            val imageSize = BitmapUtil.getImageSizeFromUri(appCtx, imageUri)
            if (imageSize == (-1 to -1)) return@LaunchedEffect
            vm.updateImgSize(imageSize.first, imageSize.second)
            vm.sourceLanguage = sourceId?.let { findLanguageById(it) } ?: Language.AUTO
            vm.targetLanguage = targetId?.let { findLanguageById(it) } ?: Language.CHINESE
            vm.translate()
        } else {
            // 反之调到裁剪页面
            doClip(imageUri)
        }
    }

    if (vm.imageUri != null) {
        ImageTranslationPart(vm = vm)
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraCapture(
                modifier = Modifier.fillMaxSize(),
                onSavedImageFile = { uri ->
                    photoName = "photo_${System.currentTimeMillis()}.jpg"
                    doClip(uri)
                },
                onError = {
                    context.toastOnUi("拍照失败！")
                },
                startChooseImage = {
                    pickLauncher.launch(
                        PhotoPickerActivity.intentOf(
                            context,
                            CoilMediaPhotoProviderFactory::class.java,
                            CustomPhotoPickerActivity::class.java,
                            pickedItems = arrayListOf<Uri>().apply {
                                imagePickResult.value?.list?.mapTo(
                                    this
                                ) { it.uri }
                            },
                            pickLimitCount = 1,
                        )
                    )
                }
            )
            LanguageSelectRow(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(0.dp, 24.dp),
                exchangeButtonTint = Color.White,
                sourceLanguage = vm.sourceLanguage,
                updateSourceLanguage = vm::updateSourceLanguage,
                targetLanguage = vm.targetLanguage,
                updateTargetLanguage = vm::updateTargetLanguage,
                enabledLanguages = currentEnabledLanguages
            )
        }

    }

}

@Composable
private fun LanguageSelectRow(
    modifier: Modifier,
    exchangeButtonTint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    sourceLanguage: Language,
    updateSourceLanguage: (Language) -> Unit,
    targetLanguage: Language,
    updateTargetLanguage: (Language) -> Unit,
    enabledLanguages: List<Language>,
    textColor: Color = Color.White
) {
    Row(modifier.horizontalScroll(rememberScrollState())) {
        LanguageSelect(
            Modifier.semantics {
                contentDescription = appCtx.getString(R.string.des_current_source_lang)
            },
            language = sourceLanguage,
            languages = enabledLanguages,
            updateLanguage = updateSourceLanguage,
            textColor = textColor
        )
        ExchangeButton(tint = exchangeButtonTint) {
            val temp = sourceLanguage
            updateSourceLanguage(targetLanguage)
            updateTargetLanguage(temp)
        }
        LanguageSelect(
            Modifier.semantics {
                contentDescription = appCtx.getString(R.string.des_current_target_lang)
            },
            language = targetLanguage,
            languages = enabledLanguages,
            updateLanguage = updateTargetLanguage,
            textColor = textColor
        )
    }
}

@Composable
private fun LanguageSelect(
    modifier: Modifier = Modifier,
    language: Language,
    languages: List<Language>,
    updateLanguage: (Language) -> Unit,
    textColor: Color = Color.White
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    TextButton(
        modifier = modifier, onClick = {
            expanded = true
        }
    ) {
        Text(
            text = language.displayText,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = textColor
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach {
                DropdownMenuItem(onClick = {
                    updateLanguage(it)
                    expanded = false
                }, text = {
                    Text(it.displayText)
                })
            }
        }
    }
}

@Composable
private fun ImageTranslationPart(
    vm: ImageTransViewModel
) {
    val goBackTipDialogState = remember {
        mutableStateOf(false)
    }
    val currentEnabledLanguages by enabledLanguages.collectAsState()
    val goBack = remember {
        {
            if (vm.isTranslating()) goBackTipDialogState.value = true
            else vm.updateImageUri(null)
        }
    }

    SimpleDialog(
        openDialogState = goBackTipDialogState,
        stringResource(id = R.string.tip),
        "当前翻译正在进行中，您确定要退出吗？",
        confirmButtonAction = {
            vm.updateImageUri(null)
        }
    )

    BackHandler(onBack = goBack)

    LaunchedEffect(vm.imageUri, vm.sourceLanguage, vm.targetLanguage, vm.translateEngine) {
        vm.translate()
    }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = goBack) {
                FixedSizeIcon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            LanguageSelectRow(
                modifier = Modifier,
                sourceLanguage = vm.sourceLanguage,
                updateSourceLanguage = vm::updateSourceLanguage,
                targetLanguage = vm.targetLanguage,
                updateTargetLanguage = vm::updateTargetLanguage,
                enabledLanguages = currentEnabledLanguages,
                textColor = MaterialTheme.colorScheme.onBackground
            )
            EngineSelect(
                engine = vm.translateEngine,
                updateEngine = vm::updateTranslateEngine,
                allEngines = vm.allEngines
            )
        }
        ResultPart(modifier = Modifier.fillMaxSize(), vm = vm)
    }
}

@Composable
private fun ResultPart(modifier: Modifier, vm: ImageTransViewModel) {
    val density = LocalDensity.current
    var showResult by remember { mutableStateOf(true) }
    // 图片为了铺满屏幕进行的缩放
    var imageInitialScale by remember { mutableStateOf(1f) }
    var scaleByWidth by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()

    val photoProvider = remember(vm.imageUri) {
        vm.imageUri?.let {
            CustomCoilProvider(it, it, vm.imgWidth.toFloat() / vm.imgHeight, lazyListState)
        }
    }

    photoProvider?.let {
        val gestureState = remember(it) {
            GestureContentState(
                ratio = it.ratio,
                isLongContent = it.isLongImage(),
            )
        }
        LaunchedEffect(key1 = gestureState.layoutInfo) {
            gestureState.layoutInfo?.let { layoutInfo ->
                val sw = layoutInfo.px.contentWidth / vm.imgWidth
                val sh = layoutInfo.px.contentHeight / vm.imgHeight
                if (gestureState.isLongContent) {
                    scaleByWidth = true
                    imageInitialScale = sw
                } else {
                    scaleByWidth = sw < sh
                    imageInitialScale = min(sw, sh)
                    Log.d(
                        TAG,
                        "ResultPart: size: ${layoutInfo.contentWidth}, ${layoutInfo.contentHeight}, img: ${vm.imgWidth}, ${vm.imgHeight}"
                    )
                    Log.d(
                        TAG,
                        "ResultPart: sw: $sw, sh: $sh, imageInitialScale: $imageInitialScale, scaleByWidth: $scaleByWidth"
                    )
                }
            }
        }
        GestureContent(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { drawRect(Color.Black) },
            state = gestureState,
            onTap = { if (vm.translateState.isSuccess) showResult = !showResult }
        ) { _ ->
            // imageGestureScale = gestureScale
            // imageOffsetRect = rect
            // Log.d(TAG, "ResultPart: gestureScale: $gestureScale, rect: $rect")
            photoProvider.photo().Compose(
                contentScale = ContentScale.Fit, //if (scaleByWidth) ContentScale.FillWidth else ContentScale.FillHeight,
                isContainerDimenExactly = true,
                onSuccess = {},
                onError = { context.toastOnUi(R.string.failed_to_load_image) }
            )

            if (vm.translateState.isLoading) {
                CircularProgressIndicator(
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else if (vm.translateState.isSuccess) {
                val alpha by animateFloatAsState(targetValue = if (showResult) 1f else 0f)
                val layoutInfo = gestureState.layoutInfo ?: return@GestureContent
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha)
                        .background(Color.LightGray.copy(0.9f))
                        .clipToBounds()
                ) {
                    Box(
                        Modifier
                            .width(layoutInfo.contentWidth)
                            .height(layoutInfo.contentHeight)
                            .align(Alignment.Center)
                            .border(2.dp, color = Color.White)
                            .offset {
                                IntOffset(
                                    0,
                                    (-(lazyListState.firstVisibleItemScrollOffset + lazyListState.firstVisibleItemIndex * layoutInfo.px.containerHeight)).toInt()
                                )
                            }
                    ) {
                        val data = (vm.translateState as LoadingState.Success).data
                        data.content.forEach { part ->
                            val w =
                                remember { (part.width * imageInitialScale / density.density).dp }
                            val h =
                                remember { (part.height * imageInitialScale / density.density).dp }
                            AutoResizedText(
                                modifier = Modifier
                                    .size(w, h)
                                    .offset {
                                        IntOffset(
                                            (part.x * imageInitialScale).toInt(),
                                            (part.y * imageInitialScale).toInt()
                                        )
                                    },
                                text = part.target,
                                color = Color.White,
                            )
                        }
                    }

                }
            }
        }
    }

}

@Composable
private fun EngineSelect(
    engine: ImageTranslationEngine,
    updateEngine: (ImageTranslationEngine) -> Unit,
    allEngines: List<ImageTranslationEngine>,
) {
    var expand by remember {
        mutableStateOf(false)
    }
    TextButton(onClick = { expand = !expand }) {
        Text(text = engine.name)
        DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
            allEngines.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.name) },
                    onClick = {
                        updateEngine(it)
                        expand = false
                    }
                )
            }
        }
    }
}

private val DESTINATION_IMAGE_URI =
    File(FunnyApplication.ctx.cacheDir.absolutePath + "/temp_des_img.png").toUri()
