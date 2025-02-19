@file:OptIn(ExperimentalMaterial3Api::class)

package com.funny.translation.translate.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SocialDistance
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Badge
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funny.data_saver.core.rememberDataSaverState
import com.funny.translation.AppConfig
import com.funny.translation.Consts
import com.funny.translation.NeedToTransConfig
import com.funny.translation.debug.rememberStateOf
import com.funny.translation.helper.SimpleAction
import com.funny.translation.helper.UserUtils
import com.funny.translation.network.api
import com.funny.translation.translate.LocalNavController
import com.funny.translation.translate.LocalSnackbarState
import com.funny.translation.translate.R
import com.funny.translation.translate.TranslationEngine
import com.funny.translation.translate.appCtx
import com.funny.translation.translate.engine.selectKey
import com.funny.translation.translate.navigateSingleTop
import com.funny.translation.translate.ui.TranslateScreen
import com.funny.translation.translate.ui.social.ComposeChat.accountProvider
import com.funny.translation.translate.ui.widget.HintText
import com.funny.translation.translate.ui.widget.SimpleNavigation
import com.funny.translation.ui.AnyPopDialog
import com.funny.translation.ui.FixedSizeIcon
import com.hjq.toast.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

// 用于选择引擎时的回调
private interface UpdateSelectedEngine {
    fun add(engine: TranslationEngine)
    fun remove(engine: TranslationEngine)
}

// 当前主页面正处在什么状态
enum class MainScreenState {
    Normal,     // 正常情况
    Inputting,  // 正在输入
    Translating // 正在翻译
}

/**
 * 项目的翻译页面, [图片](https://www.funnysaltyfish.fun/trans)
 */
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun MainScreen(
) {
     TextTransScreen()
//    BuyAIPointScreen()
     // LongTextTransDetailScreen(id = "244", sourceTextKey = "")
//    LongTextTransListScreen()
//    LongTextTransScreen()
//    LongTextTest()
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun TextTransScreen() {
    val vm: MainViewModel = viewModel()

    // 内置引擎
    val bindEngines by vm.bindEnginesFlow.collectAsState(emptyList())
    // 插件
    val jsEngines by vm.jsEnginesFlow.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    // 使用 staticCompositionLocal 传递主页面 scaffold 的 snackbarHostState
    // 方便各个页面展示 snackBar
    // CompositionLocal 相关知识可参阅 https://developer.android.google.cn/jetpack/compose/compositionlocal?hl=zh-cn
    val snackbarHostState = LocalSnackbarState.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = NeedToTransConfig) {
        if (!NeedToTransConfig.isValid()) return@LaunchedEffect
        // 防止回退到此页面时仍然触发翻译
        // 也就是：通过 deeplink 打开 MainScreen -> 跳转到其他页面 -> 返回后仍然触发翻译
        // 这个实现无疑并不优雅，但是目前我还没有想到更好的办法
        // 如果您有更好的办法，欢迎提出 PR 或者 issue 讨论
        vm.translateText = NeedToTransConfig.sourceString!!
        vm.sourceLanguage = NeedToTransConfig.sourceLanguage!!
        vm.targetLanguage = NeedToTransConfig.targetLanguage!!
        vm.translate()
        NeedToTransConfig.clear()
    }

    DisposableEffect(key1 = softwareKeyboardController){
        onDispose {
            softwareKeyboardController?.hide()
        }
    }

    // Compose函数会被反复重新调用（重组），所以变量要remember
    val updateSelectedEngine = remember {
        object : UpdateSelectedEngine {
            override fun add(engine: TranslationEngine) {
                vm.addSelectedEngines(engine)
            }

            override fun remove(engine: TranslationEngine) {
                vm.removeSelectedEngine(engine)
            }
        }
    }

    val showSnackbar: (String) -> Unit = remember {
        {
            scope.launch {
                snackbarHostState.showSnackbar(it, withDismissAction = true)
            }
        }
    }

    var showEngineSelect by rememberStateOf(value = false)
    if (showEngineSelect) {
        AnyPopDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            onDismissRequest = { showEngineSelect = false },
            isActiveClose = false
        ) {
            EngineSelect(
                modifier = Modifier,
                bindEngines,
                jsEngines,
                vm.modelEngines,
                updateSelectedEngine
            )
        }
    }

    val showEngineSelectAction = remember {
        {
            showEngineSelect = true
        }
    }

    // 使用 BoxWithConstraints 用于适配横竖屏
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth > 720.dp) { // 横屏
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Drawer(
                    Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                MainPart(isScreenHorizontal = true, showEngineSelectAction = showEngineSelectAction, showSnackbar = showSnackbar, openDrawerAction = null)
            }
        } else {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            BackHandler(enabled = drawerState.isOpen) {
                scope.launch {
                    drawerState.close()
                }
            }
            ModalNavigationDrawer(
                drawerContent = {
                    Drawer(
                        Modifier
                            .fillMaxHeight()
                            .width(280.dp)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                            .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                    )
                },
                drawerState = drawerState
            ) {
                MainPart(
                    isScreenHorizontal = false,
                    showEngineSelectAction = showEngineSelectAction,
                    showSnackbar = showSnackbar,
                    openDrawerAction = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MainPart(
    modifier: Modifier = Modifier,
    isScreenHorizontal: Boolean,
    showEngineSelectAction: SimpleAction,
    showSnackbar: (String) -> Unit,
    openDrawerAction: SimpleAction?,
) {
    val vm: MainViewModel = viewModel()

    SimpleNavigation(
        currentScreen = vm.currentState,
        modifier = modifier
            .statusBarsPadding()
            .then(
                if (isScreenHorizontal) Modifier.navigationBarsPadding() else Modifier
            )
    ) { state ->
        when (state) {
            MainScreenState.Normal -> MainPartNormal(
                vm = vm,
                isScreenHorizontal = isScreenHorizontal,
                showEngineSelectAction = showEngineSelectAction,
                openDrawerAction = openDrawerAction
            )
            MainScreenState.Inputting -> MainPartInputting(
                vm = vm,
                showEngineSelectAction = showEngineSelectAction,
                showSnackbar = showSnackbar
            )
            MainScreenState.Translating -> MainPartTranslating(vm = vm)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
private fun EngineSelect(
    modifier: Modifier,
    bindEngines: List<TranslationEngine> = arrayListOf(),
    jsEngines: List<TranslationEngine> = arrayListOf(),
    modelEngines: List<TranslationEngine> = arrayListOf(),
    updateSelectEngine: UpdateSelectedEngine
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        EnginePart(
            title = stringResource(id = R.string.bind_engine),
            engines = bindEngines,
            updateSelectEngine = updateSelectEngine
        )

        if (jsEngines.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            EnginePart(
                title = stringResource(id = R.string.plugin_engine),
                engines = jsEngines,
                updateSelectEngine = updateSelectEngine
            )
        }

        if (modelEngines.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            EnginePart(
                title = stringResource(id = R.string.model_engine),
                engines = modelEngines,
                updateSelectEngine = updateSelectEngine
            )
            HintText(text = stringResource(R.string.llm_engine_tip), fontSize = 8.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EnginePart(
    title: String,
    engines: List<TranslationEngine>,
    updateSelectEngine: UpdateSelectedEngine
) {
    val vm: MainViewModel = viewModel()
    Text(
        text = title,
        fontWeight = W600
    )
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalArrangement = spacedBy(8.dp),
    ) {
        engines.forEach { task ->
            var taskSelected by rememberDataSaverState(
                key = task.selectKey,
                default = task.selected
            )
            FilterChip(selected = taskSelected, onClick = {
                if (!taskSelected) { // 选中了
                    if(vm.selectedEngines.size >= Consts.MAX_SELECT_ENGINES) {
                        val resId = if (AppConfig.isVip()) R.string.message_out_of_max_engine_limit
                        else R.string.message_out_of_max_engine_limit_novip
                        ToastUtils.show(appCtx.getString(resId).format(Consts.MAX_SELECT_ENGINES, vm.selectedEngines.size))
                        return@FilterChip
                    }
                    updateSelectEngine.add(task)
                } else updateSelectEngine.remove(task)
                taskSelected = !taskSelected
            }, label = {
                Text(text = task.name)
            })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Drawer(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val drawerItemIcon = @Composable { icon: ImageVector, contentDescription: String ->
        FixedSizeIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun drawerItem(
        icon: ImageVector,
        targetScreen: TranslateScreen,
        badge: (@Composable () -> Unit)? = null
    ) {
        NavigationDrawerItem(
            icon = {
                drawerItemIcon(
                    icon,
                    stringResource(id = targetScreen.titleId)
                )
            },
            label = {
                Text(text = stringResource(id = targetScreen.titleId), modifier = Modifier.padding(start = 12.dp))
            },
            selected = false,
            onClick = {
                navController.navigateSingleTop(targetScreen.route)
            },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Unspecified
            ),
            badge = badge
        )
    }

    val divider = @Composable {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
    }

    // 刷新用户信息
    var refreshing by remember { mutableStateOf(false) }
    val state = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshing = true
            val user = AppConfig.userInfo.value
            if (user.isValid()){
                if (user.isValid()) accountProvider.login(user.username)
                api(UserUtils.userService::getInfo, user.uid) {
                    addSuccess {
                        it.data?.let {  user -> AppConfig.login(user) }
                    }
                }
            }
            delay(100) // 组件bug：时间过短，收不回去
            refreshing = false
        }
    })
    Box(modifier = modifier.pullRefresh(state)) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            UserInfoPanel(navHostController = navController)
            Spacer(modifier = Modifier.height(8.dp))
            drawerItem(Icons.Filled.Verified, TranslateScreen.TransProScreen) {
                if (!AppConfig.userInfo.value.isSoonExpire()) return@drawerItem
                Badge(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = stringResource(R.string.soon_expire))
                }
            }
            divider()
            drawerItem(Icons.Default.Settings, TranslateScreen.SettingScreen)
            drawerItem(icon = Icons.Default.Chat, TranslateScreen.ChatScreen)
            drawerItem(Icons.Default.Article, TranslateScreen.LongTextTransScreen) {
                Badge(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = "Beta")
                }
            }
            drawerItem(Icons.Default.PictureInPicture, TranslateScreen.FloatWindowScreen)
            divider()
            drawerItem(Icons.Default.Info, TranslateScreen.AboutScreen)
            drawerItem(Icons.Default.Favorite, TranslateScreen.ThanksScreen)
            drawerItem(Icons.Default.SocialDistance, TranslateScreen.SocialScreen)
            divider()
            drawerItem(Icons.Default.Apps, TranslateScreen.AppRecommendationScreen)
            drawerItem(Icons.Default.Redeem, TranslateScreen.AnnualReportScreen)
        }
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}
