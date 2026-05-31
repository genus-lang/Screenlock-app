package com.screenlock.app.features.design.presentation

import android.Manifest
import android.app.TimePickerDialog
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BlurCircular
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.screenlock.app.features.lock.presentation.LockViewModel
import com.screenlock.app.features.lock.presentation.SchedulerWindow
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

private val BgTop = Color(0xFF070A17)
private val BgBottom = Color(0xFF02030A)
private val CardGlass = Color(0xFF121A31)
private val NeonPurple = Color(0xFF8A4DFF)
private val NeonBlue = Color(0xFF21C8FF)
private val NeonPink = Color(0xFFFF4FD8)
private val NeonCyan = Color(0xFF34FFD7)
private val GreenOn = Color(0xFF44EE8A)

private object AppRoute {
    const val Splash = "splash"
    const val Onboarding1 = "onboarding1"
    const val Onboarding2 = "onboarding2"
    const val Onboarding3 = "onboarding3"
    const val Permission = "permission"
    const val Home = "home"
    const val Modes = "modes"
    const val Voice = "voice"
    const val Scheduler = "scheduler"
    const val Floating = "floating"
    const val Activity = "activity"
    const val Settings = "settings"
    const val HelpFaq = "help_faq"
    const val PrivacyPolicy = "privacy_policy"
}

@Composable
fun ScreenLockDesignApp() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    val hasSeenOnboarding = sharedPrefs.getBoolean("has_seen_onboarding", false)
    val startDestination = if (hasSeenOnboarding) AppRoute.Home else AppRoute.Splash

    val navController = rememberNavController()
    MaterialTheme {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(AppRoute.Splash) { SplashScreen(navController) }
            composable(AppRoute.Onboarding1) { OnboardingScreen1(navController) }
            composable(AppRoute.Onboarding2) { OnboardingScreen2(navController) }
            composable(AppRoute.Onboarding3) { OnboardingScreen3(navController) }
            composable(AppRoute.Permission) { PermissionScreen(navController) }
            composable(AppRoute.Home) { HomeScreen(navController) }
            composable(AppRoute.Modes) { ModesScreen(navController) }
            composable(AppRoute.Voice) { VoiceLockScreen(navController) }
            composable(AppRoute.Scheduler) { SchedulerScreen(navController) }
            composable(AppRoute.Floating) { FloatingLockScreen(navController) }
            composable(AppRoute.Activity) { ActivityScreen(navController) }
            composable(AppRoute.Settings) { SettingsScreen(navController) }
            composable(AppRoute.HelpFaq) { HelpFaqScreen(navController) }
            composable(AppRoute.PrivacyPolicy) { PrivacyPolicyScreen(navController) }
        }
    }
}

@Composable
private fun FloatingParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val particles = listOf(
            Triple(0.2f, 0.3f, 1f),
            Triple(0.8f, 0.7f, 1.5f),
            Triple(0.5f, 0.2f, 0.8f),
            Triple(0.3f, 0.8f, 1.2f),
            Triple(0.7f, 0.4f, 1.1f),
            Triple(0.1f, 0.6f, 0.9f),
            Triple(0.9f, 0.1f, 1.3f)
        )
        for ((px, py, speed) in particles) {
            val yOffset = (kotlin.math.sin(phase * speed) * 40.dp.toPx()).toFloat()
            val xOffset = (kotlin.math.cos(phase * speed * 0.7f) * 20.dp.toPx()).toFloat()
            val x = w * px + xOffset
            val y = h * py + yOffset
            drawCircle(
                color = NeonPurple.copy(alpha = 0.3f),
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = 1.5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun CyberBg(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(listOf(BgTop, BgBottom))
            )
    ) {
        FloatingParticles()
        Box(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxSize()
        ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopStart)
                .background(Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.25f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomEnd)
                .background(Brush.radialGradient(listOf(NeonBlue.copy(alpha = 0.25f), Color.Transparent)), CircleShape)
        )
        content()
        }
    }
}

@Composable
private fun SplashScreen(navController: NavHostController) {
    val loadingSweep by rememberInfiniteTransition(label = "splash_loading")
        .animateFloat(
            initialValue = -220f,
            targetValue = 220f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "splash_loading_sweep"
        )

    LaunchedEffect(Unit) {
        delay(1400)
        navController.navigate(AppRoute.Onboarding1) {
            popUpTo(AppRoute.Splash) { inclusive = true }
        }
    }
    CyberBg {
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HeroLockButton(label = "", onClick = {}, size = 180.dp)
                Spacer(modifier = Modifier.height(28.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Screen", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lock", modifier = Modifier.graphicsLayer {}, fontSize = 34.sp, fontWeight = FontWeight.Bold, style = androidx.compose.ui.text.TextStyle(brush = Brush.horizontalGradient(listOf(NeonPurple, NeonBlue))))
                }
                Text("Smart Lock, Smarter You", color = Color(0xFF9BA7CB), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(90.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.64f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2A4A))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.22f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .graphicsLayer { translationX = loadingSweep }
                            .background(Brush.horizontalGradient(listOf(NeonPurple, NeonBlue)))
                    )
                }
            }
    }
}

@Composable
private fun OnboardingScreen1(navController: NavHostController) {
    OnboardingScaffold(
        navController = navController,
        title = "Lock Your Device\nSmarter & Faster",
        description = "ScreenLock provides multiple smart ways to secure your device instantly.",
        page = 1,
        nextRoute = AppRoute.Onboarding2
    ) {
        GlowCard {
            Icon(Icons.Filled.Security, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("3D Secure Shield", color = Color(0xFF9CB7E8), fontSize = 12.sp)
        }
    }
}

@Composable
private fun OnboardingScreen2(navController: NavHostController) {
    OnboardingScaffold(
        navController = navController,
        title = "Multiple Smart\nLock Modes",
        description = "Choose from voice lock, shake lock, clap lock, pocket lock and more.",
        page = 2,
        nextRoute = AppRoute.Onboarding3
    ) {
        GlowCard {
            IconGridPreview()
        }
    }
}

@Composable
private fun OnboardingScreen3(navController: NavHostController) {
    OnboardingScaffold(
        navController = navController,
        title = "Enable Instant Lock",
        description = "To lock your device instantly with fingerprint unlock support, we need Accessibility permission.",
        page = 3,
        nextRoute = AppRoute.Permission,
        nextText = "Enable Now"
    ) {
        GlowCard {
            Icon(Icons.Filled.Security, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(84.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text("We never collect your data", color = Color(0xFFC1CAE6), fontSize = 12.sp)
            Text("No personal information used", color = Color(0xFFC1CAE6), fontSize = 12.sp)
            Text("100% secure and private", color = Color(0xFFC1CAE6), fontSize = 12.sp)
        }
    }
}

@Composable
private fun OnboardingScaffold(
    navController: NavHostController,
    title: String,
    description: String,
    page: Int,
    nextRoute: String,
    nextText: String = "Next",
    visual: @Composable () -> Unit
) {
    var appeared by remember(page) { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 550),
        label = "onboarding_alpha"
    )
    val contentOffsetY by animateFloatAsState(
        targetValue = if (appeared) 0f else 36f,
        animationSpec = tween(durationMillis = 550),
        label = "onboarding_offset"
    )
    val bobbing by rememberInfiniteTransition(label = "onboarding_bob")
        .animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "onboarding_bob_value"
        )

    LaunchedEffect(page) {
        appeared = true
    }

    CyberBg {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 26.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Skip",
                    color = Color(0xFF8D97B7),
                    modifier = Modifier.clickable {
                        context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                            .edit().putBoolean("has_seen_onboarding", true).apply()
                        navController.navigate(AppRoute.Home) {
                            popUpTo(AppRoute.Splash) { inclusive = true }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Column(
                        modifier = Modifier.graphicsLayer {
                            alpha = contentAlpha
                            translationY = contentOffsetY
                        },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // support titles with a newline; style second line with gradient for a premium look
                        if (title.contains("\n")) {
                            val parts = title.split("\n")
                            Text(parts[0], color = Color.White, fontSize = 30.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(parts[1], fontSize = 30.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, style = androidx.compose.ui.text.TextStyle(brush = Brush.horizontalGradient(listOf(NeonPurple, NeonBlue))))
                        } else {
                            Text(title, color = Color.White, fontSize = 30.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(description, color = Color(0xFF9CA8CB), fontSize = 14.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(modifier = Modifier.graphicsLayer { translationY = bobbing }) {
                            visual()
                        }
                    }
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index + 1 == page) 28.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (index + 1 == page) NeonPurple else Color(0xFF2A324B))
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            NeonActionButton(text = nextText) { navController.navigate(nextRoute) }
        }
    }
}

@Composable
private fun PermissionScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isAccessibilityActive by remember { mutableStateOf(LockViewModel().isAccessibilityActive()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isAccessibilityActive = LockViewModel().isAccessibilityActive()
    }

    CyberBg {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 22.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
                }
            }
            Text("Lock Screen\nPermission", color = Color.White, fontSize = 30.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Please activate Accessibility Service for instant locking with Fingerprint/Face Unlock support.", color = Color(0xFF9CA8CB), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(22.dp))
            PanelCard {
                StepText("1  Tap on Activate below")
                StepText("2  Find 'ScreenLock' and enable it")
                StepText("3  Come back to the app")
            }
            Spacer(modifier = Modifier.height(16.dp))
            NeonActionButton(text = if (isAccessibilityActive) "Activated" else "Activate") {
                if (!isAccessibilityActive) {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                } else {
                    context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                        .edit().putBoolean("has_seen_onboarding", true).apply()
                    navController.navigate(AppRoute.Home) {
                        popUpTo(AppRoute.Splash) { inclusive = true }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            PanelCard {
                Text("Accessibility Service allows us to lock the screen without breaking your biometric unlock methods.", color = Color(0xFFB0BCD9), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            NeonActionButton(text = "Continue") {
                context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    .edit().putBoolean("has_seen_onboarding", true).apply()
                navController.navigate(AppRoute.Home) {
                    popUpTo(AppRoute.Splash) { inclusive = true }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(navController: NavHostController, viewModel: LockViewModel = viewModel()) {
    val context = LocalContext.current
    var isAccessibilityActive by remember { mutableStateOf(viewModel.isAccessibilityActive()) }
    var isAdminActive by remember { mutableStateOf(viewModel.isDeviceAdminActive(context)) }
    var isVoiceEnabled by remember { mutableStateOf(viewModel.isVoiceLockEnabled(context)) }
    var isSchedulerEnabled by remember { mutableStateOf(viewModel.isSchedulerEnabled(context)) }
    var schedulerWindow by remember { mutableStateOf(viewModel.getSchedulerWindow(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isAccessibilityActive = viewModel.isAccessibilityActive()
        isAdminActive = viewModel.isDeviceAdminActive(context)

        val canLock = viewModel.canLockNow(context)
        val hasMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!canLock || !hasMic) {
            isVoiceEnabled = false
            viewModel.toggleVoiceLock(context, false)
            viewModel.setVoiceLockEnabled(context, false)
        } else if (viewModel.isVoiceLockEnabled(context)) {
            isVoiceEnabled = true
            viewModel.toggleVoiceLock(context, true)
        }

        isSchedulerEnabled = viewModel.isSchedulerEnabled(context)
        schedulerWindow = viewModel.getSchedulerWindow(context)
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && (isAccessibilityActive || isAdminActive)) {
            isVoiceEnabled = true
            viewModel.toggleVoiceLock(context, true)
            viewModel.setVoiceLockEnabled(context, true)
        }
    }

    val startPicker = TimePickerDialog(
        context,
        { _, startH, startM ->
            TimePickerDialog(
                context,
                { _, endH, endM ->
                    schedulerWindow = SchedulerWindow(startH, startM, endH, endM)
                    isSchedulerEnabled = true
                    viewModel.saveSchedulerWindow(context, startH, startM, endH, endM)
                    viewModel.setSchedulerEnabled(context, true)
                    viewModel.toggleSmartScheduler(context, true, startH, startM, endH, endM)
                },
                schedulerWindow.endHour,
                schedulerWindow.endMinute,
                false
            ).show()
        },
        schedulerWindow.startHour,
        schedulerWindow.startMinute,
        false
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { AppBottomNav(navController, AppRoute.Home) }
    ) { pv ->
        CyberBg(modifier = Modifier.padding(pv)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(title = "ScreenLock", subtitle = "Smart Lock, Smarter You", onMenu = { navController.navigate(AppRoute.Settings) }, onRight = {})
                Spacer(modifier = Modifier.height(24.dp))
                HeroLockButton(
                    label = "LOCK DEVICE",
                    onClick = {
                        if (isAccessibilityActive || isAdminActive) {
                            viewModel.lockDevice(context)
                        } else {
                            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        }
                    },
                    size = 220.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Tap to lock your device instantly", color = Color(0xFF8F9BBD), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(18.dp))
                AdminStatusCard(
                    active = isAccessibilityActive || isAdminActive,
                    onToggle = {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                )
                Spacer(modifier = Modifier.height(18.dp))
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    PanelCard {
                        SettingRow(
                            icon = Icons.Filled.Widgets,
                            title = "Add Widget to Home Screen",
                            value = "",
                            modifier = Modifier.clickable {
                                val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(context)
                                val myProvider = android.content.ComponentName(context, com.screenlock.app.features.lock.receiver.LockWidgetProvider::class.java)
                                if (appWidgetManager.isRequestPinAppWidgetSupported) {
                                    appWidgetManager.requestPinAppWidget(myProvider, null, null)
                                } else {
                                    android.widget.Toast.makeText(context, "Not supported on this launcher", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
                SectionTitle("SMART LOCK MODES")
                Spacer(modifier = Modifier.height(12.dp))
                SmartModeGrid(
                    isVoiceEnabled = isVoiceEnabled,
                    schedulerLabel = if (isSchedulerEnabled) {
                        "${formatTime(schedulerWindow.startHour, schedulerWindow.startMinute)} - ${formatTime(schedulerWindow.endHour, schedulerWindow.endMinute)}"
                    } else {
                        "Set range"
                    },
                    isSchedulerEnabled = isSchedulerEnabled,
                    onVoiceToggle = {
                        if (!isVoiceEnabled) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                isVoiceEnabled = true
                                viewModel.toggleVoiceLock(context, true)
                                viewModel.setVoiceLockEnabled(context, true)
                            } else {
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        } else {
                            isVoiceEnabled = false
                            viewModel.toggleVoiceLock(context, false)
                            viewModel.setVoiceLockEnabled(context, false)
                        }
                    },
                    onSchedulerToggle = {
                        if (!isSchedulerEnabled) {
                            startPicker.show()
                        } else {
                            isSchedulerEnabled = false
                            viewModel.setSchedulerEnabled(context, false)
                            viewModel.toggleSmartScheduler(context, false)
                        }
                    },
                    onOpenModes = { navController.navigate(AppRoute.Modes) },
                    onOpenFloating = { navController.navigate(AppRoute.Floating) },
                    onOpenSettings = { navController.navigate(AppRoute.Settings) }
                )
                Spacer(modifier = Modifier.height(18.dp))
                SectionTitle("RECENT ACTIVITY", trailing = "View All") { navController.navigate(AppRoute.Activity) }
                Spacer(modifier = Modifier.height(10.dp))
                val recentEvents = com.screenlock.app.features.lock.presentation.LockViewModel.getLockEvents(context).take(3)
                if (recentEvents.isEmpty()) {
                    Text("No recent activity", color = Color(0xFF9CA9CC), modifier = Modifier.padding(12.dp))
                } else {
                    val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                    recentEvents.forEach { event ->
                        val timeStr = formatter.format(java.util.Date(event.timestamp))
                        val icon = when {
                            event.source.contains("Voice") -> Icons.Filled.Mic
                            event.source.contains("Shake") -> Icons.Filled.PhoneAndroid
                            event.source.contains("Pocket") -> Icons.Filled.Security
                            event.source.contains("Floating") -> Icons.Filled.BlurCircular
                            event.source.contains("Scheduler") -> Icons.Filled.CalendarMonth
                            else -> Icons.Filled.Lock
                        }
                        val color = when {
                            event.source.contains("Voice") -> NeonCyan
                            event.source.contains("Shake") -> NeonBlue
                            event.source.contains("Pocket") -> Color(0xFFFFBE49)
                            event.source.contains("Floating") -> NeonPurple
                            event.source.contains("Scheduler") -> NeonPink
                            else -> NeonPurple
                        }
                        ActivityRow(icon, "Device locked", event.source, timeStr, color)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(92.dp))
            }
        }
    }
}

@Composable
private fun ModesScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    
    val items = listOf(
        Triple(Icons.Filled.Mic, "Voice Lock", "Say command to lock") to AppRoute.Voice,
        Triple(Icons.Filled.PhoneAndroid, "Shake Lock", "Sensitivity settings link") to AppRoute.Voice, // Mocked to Voice for now
        Triple(Icons.Filled.Apps, "Quick Tile", "Setup instructions link") to AppRoute.Voice, // Mocked
        Triple(Icons.Filled.BlurCircular, "Floating Lock", "Open floating lock settings") to AppRoute.Floating,
        Triple(Icons.Filled.Security, "Pocket Lock", "Proximity sensor toggle") to AppRoute.Voice, // Mocked
        Triple(Icons.Filled.CalendarMonth, "Scheduler", "Set time range") to AppRoute.Scheduler,
        Triple(Icons.Filled.Face, "Fake Lock", "Setup pattern") to AppRoute.Voice // Mocked
    )
    
    val states = remember {
        items.map { 
            val key = "mode_${it.first.second.replace(" ", "_")}"
            mutableStateOf(prefs.getBoolean(key, false)) 
        }
    }

    AppPageScaffold(navController, "Smart Lock Modes", AppRoute.Modes) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEachIndexed { index, pair ->
                val item = pair.first
                val route = pair.second
                val key = "mode_${item.second.replace(" ", "_")}"
                
                PanelCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if (route != AppRoute.Modes) {
                                navController.navigate(route)
                            }
                        }
                    ) {
                        Icon(item.first, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.second, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(item.third, color = Color(0xFF9CA8CB), fontSize = 12.sp)
                        }
                        Switch(
                            checked = states[index].value,
                            onCheckedChange = { 
                                states[index].value = it 
                                prefs.edit().putBoolean(key, it).apply()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonPurple,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color(0xFF2B344F)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceLockScreen(navController: NavHostController) {
    var command by remember { mutableStateOf("Lock my phone") }
    var sensitivity by remember { mutableStateOf(0.62f) }

    AppPageScaffold(navController, "Voice Lock", AppRoute.Voice) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeroCircle(icon = Icons.Filled.Mic, colorA = NeonPurple, colorB = NeonBlue, size = 176.dp)
            Spacer(modifier = Modifier.height(14.dp))
            Text("Lock your device by saying a custom voice command.", color = Color(0xFFA0AED2), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(14.dp))
            PanelCard {
                Text("Voice Command", color = Color(0xFF9BA7C9), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F1630))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(command, color = Color.White)
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFF8191B9), modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text("Sensitivity", color = Color(0xFF9BA7C9), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                SliderLine(value = sensitivity) { sensitivity = it }
            }
            Spacer(modifier = Modifier.height(16.dp))
            NeonActionButton("Start Listening") {}
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SchedulerScreen(navController: NavHostController, viewModel: com.screenlock.app.features.lock.presentation.LockViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    var window by remember { mutableStateOf(viewModel.getSchedulerWindow(context)) }
    var isEnabled by remember { mutableStateOf(viewModel.isSchedulerEnabled(context)) }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val selected = remember { days.associateWith { true }.toMutableMap() }

    val startPicker = TimePickerDialog(
        context,
        { _, h, m ->
            window = window.copy(startHour = h, startMinute = m)
            viewModel.saveSchedulerWindow(context, window.startHour, window.startMinute, window.endHour, window.endMinute)
            if (isEnabled) viewModel.toggleSmartScheduler(context, true, window.startHour, window.startMinute, window.endHour, window.endMinute)
        },
        window.startHour,
        window.startMinute,
        false
    )

    val endPicker = TimePickerDialog(
        context,
        { _, h, m ->
            window = window.copy(endHour = h, endMinute = m)
            viewModel.saveSchedulerWindow(context, window.startHour, window.startMinute, window.endHour, window.endMinute)
            if (isEnabled) viewModel.toggleSmartScheduler(context, true, window.startHour, window.startMinute, window.endHour, window.endMinute)
        },
        window.endHour,
        window.endMinute,
        false
    )

    AppPageScaffold(navController, "Scheduler", AppRoute.Scheduler) {
        PanelCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Add Schedule", color = Color.White, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Color(0xFF8A98BF), modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Start Time", color = Color.White)
                Text(
                    formatTime(window.startHour, window.startMinute),
                    color = NeonPurple,
                    modifier = Modifier.clickable { startPicker.show() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("End Time", color = Color.White)
                Text(
                    formatTime(window.endHour, window.endMinute),
                    color = NeonPurple,
                    modifier = Modifier.clickable { endPicker.show() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Repeat", color = Color(0xFF99A6CB), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                days.forEach { day ->
                    val isOn = selected[day] == true
                    Text(
                        text = day,
                        color = if (isOn) Color.White else Color(0xFF8D9ABF),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isOn) NeonPurple else Color(0xFF121A31))
                            .clickable { selected[day] = !isOn }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            NeonActionButton(if (isEnabled) "Update Schedule" else "Enable Schedule") {
                isEnabled = true
                viewModel.setSchedulerEnabled(context, true)
                viewModel.toggleSmartScheduler(context, true, window.startHour, window.startMinute, window.endHour, window.endMinute)
                android.widget.Toast.makeText(context, "Schedule saved and enabled!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        SectionTitle("Your Schedules")
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("${formatTime(window.startHour, window.startMinute)} - ${formatTime(window.endHour, window.endMinute)}", color = Color.White, fontWeight = FontWeight.Medium)
                    Text("Daily Schedule", color = Color(0xFF9AA6C9), fontSize = 12.sp)
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = {
                        isEnabled = it
                        viewModel.setSchedulerEnabled(context, it)
                        viewModel.toggleSmartScheduler(context, it, window.startHour, window.startMinute, window.endHour, window.endMinute)
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = NeonPurple)
                )
            }
            if (isEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                Text("Locks in ${calculateTimeUntilLock(window.startHour, window.startMinute)}", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private fun calculateTimeUntilLock(startHour: Int, startMinute: Int): String {
    val now = java.util.Calendar.getInstance()
    val lockTime = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, startHour)
        set(java.util.Calendar.MINUTE, startMinute)
        set(java.util.Calendar.SECOND, 0)
    }
    if (lockTime.before(now)) {
        lockTime.add(java.util.Calendar.DAY_OF_YEAR, 1)
    }
    val diff = lockTime.timeInMillis - now.timeInMillis
    val hours = diff / (1000 * 60 * 60)
    val mins = (diff / (1000 * 60)) % 60
    return if (hours > 0) "$hours hr $mins min" else "$mins min"
}

@Composable
private fun FloatingLockScreen(navController: NavHostController, viewModel: com.screenlock.app.features.lock.presentation.LockViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(viewModel.isFloatingLockEnabled(context)) }
    var size by remember { mutableStateOf(viewModel.getFloatingLockSize(context)) }
    var alpha by remember { mutableStateOf(viewModel.getFloatingLockAlpha(context)) }
    var hasPermission by remember { mutableStateOf(android.provider.Settings.canDrawOverlays(context)) }

    val overlayPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) {
        hasPermission = android.provider.Settings.canDrawOverlays(context)
        if (hasPermission && isEnabled) {
            viewModel.toggleFloatingLock(context, true)
        }
    }

    AppPageScaffold(navController, "Floating Lock", AppRoute.Floating) {
        Text("Enable floating lock button to lock your device from anywhere.", color = Color(0xFF9CA9CC), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(12.dp))
        PanelCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enable Overlay Button", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = {
                        isEnabled = it
                        viewModel.setFloatingLockEnabled(context, it)
                        if (it && !hasPermission) {
                            val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, android.net.Uri.parse("package:${context.packageName}"))
                            overlayPermissionLauncher.launch(intent)
                        } else {
                            viewModel.toggleFloatingLock(context, it)
                        }
                    },
                    colors = SwitchDefaults.colors(checkedTrackColor = NeonPurple)
                )
            }
            if (!hasPermission) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Requires 'Draw over other apps' permission.", color = Color(0xFFF77272), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Size", color = Color(0xFF9CA9CC), fontSize = 12.sp)
            SliderLine(value = size) { 
                size = it 
                viewModel.setFloatingLockSize(context, it)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Transparency", color = Color(0xFF9CA9CC), fontSize = 12.sp)
            SliderLine(value = alpha) { 
                alpha = it 
                viewModel.setFloatingLockAlpha(context, it)
            }
        }
    }
}

@Composable
private fun ActivityScreen(navController: NavHostController, viewModel: com.screenlock.app.features.lock.presentation.LockViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    var events by remember { mutableStateOf(com.screenlock.app.features.lock.presentation.LockViewModel.getLockEvents(context)) }

    AppPageScaffold(navController, "Activity", AppRoute.Activity) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Locked ${events.size} times", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "Clear History", 
                color = Color(0xFFF77272), 
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    com.screenlock.app.features.lock.presentation.LockViewModel.clearLockEvents(context)
                    events = emptyList()
                }.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (events.isEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                HeroCircle(Icons.Rounded.Menu, NeonPurple, NeonBlue, 120.dp)
                Spacer(modifier = Modifier.height(24.dp))
                Text("No Recent Activity", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your lock history will appear here.", color = Color(0xFF9CA9CC))
            }
        } else {
            val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            val dateFmt = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
            
            // Group events roughly by date string
            val grouped = events.groupBy { 
                val cal = java.util.Calendar.getInstance()
                val now = java.util.Calendar.getInstance()
                cal.timeInMillis = it.timestamp
                if (now.get(java.util.Calendar.YEAR) == cal.get(java.util.Calendar.YEAR) &&
                    now.get(java.util.Calendar.DAY_OF_YEAR) == cal.get(java.util.Calendar.DAY_OF_YEAR)) {
                    "Today"
                } else if (now.get(java.util.Calendar.YEAR) == cal.get(java.util.Calendar.YEAR) &&
                    now.get(java.util.Calendar.DAY_OF_YEAR) - 1 == cal.get(java.util.Calendar.DAY_OF_YEAR)) {
                    "Yesterday"
                } else {
                    dateFmt.format(cal.time)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                grouped.forEach { (dateHeader, dateEvents) ->
                    Column {
                        SectionTitle(dateHeader)
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            dateEvents.forEach { event ->
                                val timeStr = formatter.format(java.util.Date(event.timestamp))
                                val icon = when {
                                    event.source.contains("Voice") -> Icons.Filled.Mic
                                    event.source.contains("Shake") -> Icons.Filled.PhoneAndroid
                                    event.source.contains("Pocket") -> Icons.Filled.Security
                                    event.source.contains("Floating") -> Icons.Filled.BlurCircular
                                    event.source.contains("Scheduler") -> Icons.Filled.CalendarMonth
                                    else -> Icons.Filled.Lock
                                }
                                val color = when {
                                    event.source.contains("Voice") -> NeonCyan
                                    event.source.contains("Shake") -> NeonBlue
                                    event.source.contains("Pocket") -> Color(0xFFFFBE49)
                                    event.source.contains("Floating") -> NeonPurple
                                    event.source.contains("Scheduler") -> NeonPink
                                    else -> NeonPurple
                                }
                                ActivityRow(icon, "Device locked", event.source, timeStr, color)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dpm = context.getSystemService(android.content.Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = android.content.ComponentName(context, com.screenlock.app.features.lock.receiver.MyDeviceAdminReceiver::class.java)

    var vibration by remember { mutableStateOf(com.screenlock.app.features.lock.presentation.LockViewModel.isVibrationEnabled(context)) }
    var sound by remember { mutableStateOf(com.screenlock.app.features.lock.presentation.LockViewModel.isSoundEnabled(context)) }
    var showOnLockScreen by remember { mutableStateOf(com.screenlock.app.features.lock.presentation.LockViewModel.isShowOnLockScreenEnabled(context)) }
    var uninstallProtection by remember { mutableStateOf(dpm.isAdminActive(adminComponent)) }
    
    var hasOverlayPermission by remember { mutableStateOf(android.provider.Settings.canDrawOverlays(context)) }
    var hasAccessibilityPermission by remember { 
        mutableStateOf(
            android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                ?.contains(context.packageName) == true
        ) 
    }

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasOverlayPermission = android.provider.Settings.canDrawOverlays(context)
                hasAccessibilityPermission = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)?.contains(context.packageName) == true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    val adminLauncher = androidx.activity.compose.rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) {
        uninstallProtection = dpm.isAdminActive(adminComponent)
    }

    AppPageScaffold(navController, "Settings", AppRoute.Settings) {
        SectionTitle("GENERAL")
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard { SettingRow(Icons.Filled.Tune, "Theme", "Dark") }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard { 
            SettingToggleRow(Icons.Filled.GraphicEq, "Vibration", vibration) {
                vibration = it
                com.screenlock.app.features.lock.presentation.LockViewModel.setVibrationEnabled(context, it)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard { 
            SettingToggleRow(Icons.Filled.Notifications, "Sound", sound) {
                sound = it
                com.screenlock.app.features.lock.presentation.LockViewModel.setSoundEnabled(context, it)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard { 
            SettingToggleRow(Icons.Filled.Security, "Uninstall Protection", uninstallProtection) {
                if (it) {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Required to protect app from uninstallation.")
                    }
                    adminLauncher.launch(intent)
                } else {
                    dpm.removeActiveAdmin(adminComponent)
                    uninstallProtection = false
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard {
            SettingToggleRow(Icons.Filled.Apps, "Overlay Permission", hasOverlayPermission) {
                if (!hasOverlayPermission) {
                    val intent = Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                } else {
                    // Inform user they must disable it in system settings
                    android.widget.Toast.makeText(context, "Please disable in System Settings", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard {
            SettingToggleRow(Icons.Filled.Face, "Accessibility Permission", hasAccessibilityPermission) {
                if (!hasAccessibilityPermission) {
                    val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                } else {
                    // Inform user they must disable it in system settings
                    android.widget.Toast.makeText(context, "Please disable in System Settings", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard { SettingRow(Icons.Filled.Apps, "Language", "English") }

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("SUPPORT")
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard {
            SettingRow(Icons.Filled.Star, "Rate Us", "", Modifier.clickable {
                val packageName = context.packageName
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=$packageName")))
                } catch (e: android.content.ActivityNotFoundException) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
            })
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard {
            SettingRow(Icons.Filled.Share, "Share App", "", Modifier.clickable {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out ScreenLock, the best smart lock app! https://play.google.com/store/apps/details?id=${context.packageName}")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
            })
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard {
            SettingRow(Icons.Filled.HelpOutline, "Help & FAQ", "", Modifier.clickable {
                navController.navigate(AppRoute.HelpFaq)
            })
        }
        Spacer(modifier = Modifier.height(8.dp))
        PanelCard {
            SettingRow(Icons.Filled.Lock, "Privacy Policy", "", Modifier.clickable {
                navController.navigate(AppRoute.PrivacyPolicy)
            })
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                "Reset All Settings",
                color = Color(0xFFF77272),
                modifier = Modifier.clickable {
                    com.screenlock.app.features.lock.presentation.LockViewModel.resetAllSettings(context)
                    // Reset local state
                    vibration = true
                    sound = true
                    showOnLockScreen = false
                    android.widget.Toast.makeText(context, "Settings reset", android.widget.Toast.LENGTH_SHORT).show()
                }.padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Made by lazyar Tech Company", color = Color(0xFF6B7A99), fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun AppPageScaffold(navController: NavHostController, title: String, currentRoute: String, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { AppBottomNav(navController, currentRoute) }
    ) { pv ->
        CyberBg(modifier = Modifier.padding(pv)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 14.dp, bottom = 92.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Composable
private fun TopBar(title: String, subtitle: String, onMenu: () -> Unit, onRight: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(Icons.Rounded.Menu, onMenu)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = Color(0xFF8D9ABE), fontSize = 12.sp)
        }
        CircleIconButton(Icons.Filled.Star, onRight, tint = Color(0xFFFFD968))
    }
}

@Composable
private fun CircleIconButton(icon: ImageVector, onClick: () -> Unit, tint: Color = Color.White) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF121A31))
            .border(1.dp, Color(0xFF24355F), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint)
    }
}

@Composable
private fun HeroLockButton(label: String, onClick: () -> Unit, size: androidx.compose.ui.unit.Dp) {
    val pulseScale by rememberInfiniteTransition(label = "hero_pulse")
        .animateFloat(
            initialValue = 0.94f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hero_pulse_scale"
        )
    val ringRotation by rememberInfiniteTransition(label = "hero_rotate")
        .animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 9000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "hero_ring_rotation"
        )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val touchScale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1f, label = "touchScale")

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size * 0.92f)
                .blur(36.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    alpha = 0.75f
                }
                .background(Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.4f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(size * 0.9f)
                .graphicsLayer { rotationZ = ringRotation }
                .border(1.5.dp, Brush.sweepGradient(listOf(NeonPurple, NeonBlue, NeonPink, NeonPurple)), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(size * 0.84f)
                .graphicsLayer {
                    scaleX = touchScale
                    scaleY = touchScale
                }
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF121A33), Color(0xFF0B1124))))
                .border(2.dp, Brush.linearGradient(listOf(NeonPurple, NeonBlue)), CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.foundation.LocalIndication.current
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFB7D4FF), modifier = Modifier.size(42.dp))
                if (label.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GlowCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        color = CardGlass.copy(alpha = 0.66f),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 4.dp,
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
private fun AdminStatusCard(active: Boolean, onToggle: (Boolean) -> Unit) {
    val title = if (active) "Protected" else "Permission Required"
    val subtitle = if (active) "Your device is fully secured." else "Tap to enable instant locking feature."
    val color = if (active) GreenOn else Color(0xFFFF4858)
    
    PanelCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, color = Color(0xFF8F9BBD), fontSize = 12.sp)
            }
            if (!active) {
                Text("ENABLE", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { onToggle(true) })
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SmartModeGrid(
    isVoiceEnabled: Boolean,
    schedulerLabel: String,
    isSchedulerEnabled: Boolean,
    onVoiceToggle: () -> Unit,
    onSchedulerToggle: () -> Unit,
    onOpenModes: () -> Unit,
    onOpenFloating: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val items = listOf(
        ModeData(Icons.Filled.Mic, "Voice Lock", "Say command to lock", NeonPurple),
        ModeData(Icons.Filled.PhoneAndroid, "Shake Lock", "Shake your device", NeonCyan),
        ModeData(Icons.Filled.Apps, "Quick Tile", "Notification tile", NeonPink),
        ModeData(Icons.Filled.BlurCircular, "Floating Lock", "Overlay button", Color(0xFF6EA2FF)),
        ModeData(Icons.Filled.Security, "Pocket Lock", "Proximity detect", Color(0xFFFFB75E)),
        ModeData(Icons.Filled.CalendarMonth, "Scheduler", schedulerLabel, Color(0xFFFF5D92)),
        ModeData(Icons.Filled.Face, "Fake Lock", "Secure mode", Color(0xFFD5DEFF))
    )

    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEachIndexed { index, item ->
            Box(modifier = Modifier.weight(1f, fill = false)) {
                ModeCard(
                    item = item,
                    active = when (index) {
                        0 -> isVoiceEnabled
                        5 -> isSchedulerEnabled
                        else -> false
                    },
                    onClick = when (index) {
                        0 -> onVoiceToggle
                        3 -> onOpenFloating
                        5 -> onSchedulerToggle
                        else -> onOpenModes
                    }
                )
            }
        }
    }
}

@Composable
private fun ModeCard(item: ModeData, active: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(108.dp)
            .aspectRatio(0.86f)
            .clip(RoundedCornerShape(18.dp))
            .background(CardGlass.copy(alpha = 0.7f))
            .border(1.dp, if (active) item.color else Color(0xFF24345C), RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeroCircle(item.icon, item.color, NeonBlue, 36.dp)
        Spacer(modifier = Modifier.height(7.dp))
        Text(item.title, color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(item.subtitle, color = Color(0xFF99A6CA), fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 2)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (active) GreenOn else Color.Transparent)
        )
    }
}

@Composable
private fun ActivityRow(icon: ImageVector, title: String, subtitle: String, time: String, color: Color) {
    PanelCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HeroCircle(icon = icon, colorA = color, colorB = NeonBlue, size = 38.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color(0xFF9BA7C9), fontSize = 12.sp)
            }
            Text(time, color = Color(0xFF8795B7), fontSize = 11.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFF7F8DB3), modifier = Modifier.size(13.dp))
        }
    }
}

@Composable
private fun PanelCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardGlass.copy(alpha = 0.72f),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier, content = content)
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(14.dp).fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = Color(0xFFBBD4FF), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = Color.White, modifier = Modifier.weight(1f))
        if (value.isNotBlank()) {
            Text(value, color = Color(0xFF8FA1C7), fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color(0xFF7F8DB3), modifier = Modifier.size(12.dp))
    }
}

@Composable
private fun SettingToggleRow(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = Color(0xFFBBD4FF), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = Color.White, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = NeonPurple)
        )
    }
}

@Composable
private fun SectionTitle(text: String, trailing: String? = null, onTrailing: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = Color(0xFFA4AFCD), letterSpacing = 0.7.sp, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        if (trailing != null) {
            Text(
                text = trailing,
                color = NeonPurple,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = if (onTrailing != null) Modifier.clickable { onTrailing() } else Modifier
            )
        }
    }
}

@Composable
private fun HeroCircle(icon: ImageVector, colorA: Color, colorB: Color, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(colorA.copy(alpha = 0.25f), colorB.copy(alpha = 0.18f)))),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = colorA, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
private fun NeonActionButton(text: String, onClick: () -> Unit) {
    val shimmerOffset by rememberInfiniteTransition(label = "button_shimmer")
        .animateFloat(
            initialValue = -280f,
            targetValue = 620f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2300, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "button_shimmer_value"
        )

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(NeonPurple, NeonBlue, NeonPurple),
                    start = Offset(shimmerOffset, 0f),
                    end = Offset(shimmerOffset + 350f, 120f)
                ),
                RoundedCornerShape(28.dp)
            )
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF98A6C9), fontSize = 12.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StepText(text: String) {
    Text(text = text, color = Color(0xFFC2CEE8), fontSize = 13.sp, modifier = Modifier.padding(vertical = 5.dp))
}

@Composable
private fun SliderLine(value: Float, onValueChange: (Float) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(CircleShape)
            .background(Color(0xFF2D3554))
            .clickable {
                val next = if (value >= 0.85f) 0.2f else value + 0.15f
                onValueChange(next)
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(value)
                .height(3.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(NeonPurple, NeonBlue)))
        )
        Box(
            modifier = Modifier
                .padding(start = (240 * value).dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, NeonPurple, CircleShape)
                .align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun ToggleRow(label: String, initial: Boolean) {
    var checked by remember { mutableStateOf(initial) }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, fontSize = 13.sp)
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonPurple)
        )
    }
}

@Composable
private fun AppBottomNav(navController: NavHostController, route: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0E1A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(navController, route, AppRoute.Home, Icons.Filled.Home, "Home", Modifier.weight(1f))
            NavItem(navController, route, AppRoute.Activity, Icons.Filled.Timeline, "Activity", Modifier.weight(1f))
            NavItem(navController, route, AppRoute.Modes, Icons.Filled.Apps, "Market", Modifier.weight(1f))
            NavItem(navController, route, AppRoute.Settings, Icons.Filled.Settings, "Settings", Modifier.weight(1f))
        }
    }
}

@Composable
private fun NavItem(navController: NavHostController, currentRoute: String, targetRoute: String, icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    val selected = currentRoute == targetRoute
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (!selected) {
                navController.navigate(targetRoute) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(if (selected) Color(0xFF1B223C) else Color.Transparent)
                .then(
                    if (selected) Modifier.border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    else Modifier
                )
                .padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon, 
                contentDescription = label, 
                tint = if (selected) NeonPurple else Color(0xFF8190B8),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label, 
                fontSize = 12.sp, 
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) NeonPurple else Color(0xFF8190B8)
            )
        }
    }
}

@Composable
private fun IconGridPreview() {
    val previewItems = listOf(
        Icons.Filled.Mic,
        Icons.Filled.GraphicEq,
        Icons.Filled.Security,
        Icons.Filled.PhoneAndroid,
        Icons.Filled.TouchApp,
        Icons.Filled.Apps,
        Icons.Filled.Face,
        Icons.Filled.Settings
    )
    val colors = listOf(NeonPurple, NeonBlue, Color(0xFFFFB75E), NeonCyan, NeonPink, Color(0xFF6EA2FF), Color(0xFFFF5D92), Color(0xFFD5DEFF))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(4) { i ->
                MiniIconCard(previewItems[i], colors[i], Modifier.weight(1f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(4) { i ->
                MiniIconCard(previewItems[i + 4], colors[i + 4], Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MiniIconCard(icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF101831))
            .border(1.dp, Color(0xFF273760), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}

private data class ModeData(val icon: ImageVector, val title: String, val subtitle: String, val color: Color)
private data class Quad(val icon: ImageVector, val title: String, val subtitle: String, val time: String, val color: Color)

private fun formatTime(hour: Int, minute: Int): String {
    val normalizedHour = ((hour + 11) % 12) + 1
    val amPm = if (hour >= 12) "PM" else "AM"
    return String.format(Locale.US, "%d:%02d %s", normalizedHour, minute, amPm)
}

@Composable
private fun HelpFaqScreen(navController: NavHostController) {
    AppPageScaffold(navController, title = "Help & FAQ", currentRoute = AppRoute.HelpFaq) {
        PanelCard {
            Text("FAQ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(14.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Q: How does the Smart Lock work?\nA: It uses your environment (WiFi, Location) to keep your device unlocked when safe.", color = Color(0xFFA4AFCD), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Q: Why does the floating lock disappear?\nA: Ensure you have enabled the 'Display over other apps' permission in Settings.", color = Color(0xFFA4AFCD), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Q: Who developed this app?\nA: This app is proudly made by lazyar Tech Company.", color = Color(0xFFA4AFCD), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PrivacyPolicyScreen(navController: NavHostController) {
    AppPageScaffold(navController, title = "Privacy Policy", currentRoute = AppRoute.PrivacyPolicy) {
        PanelCard {
            Text("Privacy Policy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(14.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your privacy is important to us at lazyar Tech Company. We only collect the necessary permissions (Accessibility, Overlay, Notification) to ensure the proper functioning of the smart lock features. All data is processed locally on your device.", color = Color(0xFFA4AFCD), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("We do not sell or share your personal data with third parties.", color = Color(0xFFA4AFCD), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
