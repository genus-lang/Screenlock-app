package com.screenlock.app.features.lock.presentation

import android.Manifest
import android.app.TimePickerDialog
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.BorderStroke
import java.util.Locale
import java.util.Calendar

val NeonPurple = Color(0xFF8A2BE2)
val ElectricBlue = Color(0xFF00E5FF)
val DeepNavy = Color(0xFF070B19)
val CardBackground = Color(0xFF131521)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreen(viewModel: LockViewModel = viewModel()) {
    val context = LocalContext.current
    var isAdminActive by remember { mutableStateOf(viewModel.isDeviceAdminActive(context)) }
    var isAccessibilityActive by remember { mutableStateOf(viewModel.isAccessibilityActive()) }
    
    var isVoiceLockEnabled by remember { mutableStateOf(viewModel.isVoiceLockEnabled(context)) }
    var isSchedulerEnabled by remember { mutableStateOf(viewModel.isSchedulerEnabled(context)) }
    var schedulerWindow by remember { mutableStateOf(viewModel.getSchedulerWindow(context)) }

    // Recheck states when returning to the app
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isAdminActive = viewModel.isDeviceAdminActive(context)
        isAccessibilityActive = viewModel.isAccessibilityActive()

        // Keep feature toggles in sync with system-level permission changes.
        val canLock = viewModel.canLockNow(context)
        val hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!canLock || !hasAudioPermission) {
            if (isVoiceLockEnabled) {
                viewModel.toggleVoiceLock(context, false)
            }
            isVoiceLockEnabled = false
            viewModel.setVoiceLockEnabled(context, false)
        } else if (viewModel.isVoiceLockEnabled(context)) {
            isVoiceLockEnabled = true
            viewModel.toggleVoiceLock(context, true)
        }

        isSchedulerEnabled = viewModel.isSchedulerEnabled(context)
        schedulerWindow = viewModel.getSchedulerWindow(context)
    }

    val adminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        isAdminActive = viewModel.isDeviceAdminActive(context)
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted && (isAdminActive || isAccessibilityActive)) {
            isVoiceLockEnabled = true
            viewModel.toggleVoiceLock(context, true)
            viewModel.setVoiceLockEnabled(context, true)
        } else {
            isVoiceLockEnabled = false
            viewModel.toggleVoiceLock(context, false)
            viewModel.setVoiceLockEnabled(context, false)
        }
    }

    val startTimePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val selectedStartHour = hour
            val selectedStartMinute = minute

            TimePickerDialog(
                context,
                { _, endHour, endMinute ->
                    schedulerWindow = SchedulerWindow(
                        startHour = selectedStartHour,
                        startMinute = selectedStartMinute,
                        endHour = endHour,
                        endMinute = endMinute
                    )
                    isSchedulerEnabled = true
                    viewModel.saveSchedulerWindow(
                        context,
                        selectedStartHour,
                        selectedStartMinute,
                        endHour,
                        endMinute
                    )
                    viewModel.setSchedulerEnabled(context, true)
                    viewModel.toggleSmartScheduler(
                        context,
                        true,
                        selectedStartHour,
                        selectedStartMinute,
                        endHour,
                        endMinute
                    )
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
        bottomBar = { BottomNavBar() },
        containerColor = DeepNavy
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DeepNavy, Color.Black)))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // TOP SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Menu */ }) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ScreenLock",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Smart Lock, Smarter You",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = { /* Premium */ }) {
                    Icon(Icons.Rounded.Star, contentDescription = "Premium", tint = Color(0xFFFFD700))
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // HERO CIRCULAR BUTTON
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                // Neon glow background
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .blur(40.dp)
                        .background(
                            brush = Brush.radialGradient(listOf(NeonPurple, Color.Transparent)),
                            shape = CircleShape
                        )
                )

                // Actual button
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF1E1E38), Color(0xFF0F0F23))))
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(listOf(NeonPurple, ElectricBlue)),
                            shape = CircleShape
                        )
                        .clickable {
                            if (isAdminActive || isAccessibilityActive) {
                                viewModel.lockDevice(context)
                            } else {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Lock",
                            tint = ElectricBlue,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "LOCK DEVICE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tap to lock your device instantly",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // DEVICE ADMIN CARD / ACCESSIBILITY
            PermissionCard(
                title = "Device Admin",
                subtitle = if (isAdminActive) "Active" else "Disabled",
                isActive = isAdminActive,
                onCheckedChange = {
                    if (!isAdminActive) {
                        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, viewModel.getDeviceAdminComponent(context))
                        }
                        adminLauncher.launch(intent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            // GRID TITLE
            Text(
                text = "SMART LOCK MODES",
                color = Color(0xFFAAAAAA),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // SMART LOCK MODES GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp), // Fixed height to allow scrolling outer column
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                item { 
                    ModeItem(Icons.Rounded.Notifications, "Voice Lock", "Say 'phone lock'", NeonPurple, isVoiceLockEnabled) {
                        if (isAdminActive || isAccessibilityActive) {
                            if (!isVoiceLockEnabled) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                    isVoiceLockEnabled = true
                                    viewModel.toggleVoiceLock(context, true)
                                    viewModel.setVoiceLockEnabled(context, true)
                                } else {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            } else {
                                isVoiceLockEnabled = false
                                viewModel.toggleVoiceLock(context, false)
                                viewModel.setVoiceLockEnabled(context, false)
                            }
                        }
                    }
                }
                item { ModeItem(Icons.Rounded.ThumbUp, "Clap Lock", "Clap to lock", ElectricBlue, false) {} }
                item { ModeItem(Icons.Rounded.Lock, "Pocket Lock", "In pocket", Color(0xFFFFB300), false) {} }
                item { ModeItem(Icons.Rounded.Phone, "Shake Lock", "Shake to lock", NeonPurple, false) {} }
                item { ModeItem(Icons.Rounded.PlayArrow, "Double Tap", "Tap to lock", Color(0xFFFF5252), false) {} }
                
                item { 
                    ModeItem(
                        Icons.Rounded.DateRange,
                        "Scheduler",
                        if (isSchedulerEnabled) {
                            "${formatTime(schedulerWindow.startHour, schedulerWindow.startMinute)} - ${formatTime(schedulerWindow.endHour, schedulerWindow.endMinute)}"
                        } else {
                            "Set time range"
                        },
                        Color(0xFF00E676),
                        isSchedulerEnabled
                    ) {
                        if (isAdminActive || isAccessibilityActive) {
                            if (!isSchedulerEnabled) {
                                startTimePickerDialog.show()
                            } else {
                                isSchedulerEnabled = false
                                viewModel.setSchedulerEnabled(context, false)
                                viewModel.toggleSmartScheduler(context, false)
                            }
                        }
                    } 
                }
                item { ModeItem(Icons.Rounded.AccountBox, "Quick Tile", "Quick access", ElectricBlue, false) {} }
                item { ModeItem(Icons.Rounded.Face, "Fake Lock", "Secure mode", Color(0xFFFF1744), false) {} }
                item { ModeItem(Icons.Rounded.AddCircle, "Floating Lock", "Overlay button", Color(0xFFFFD700), false) {} }
                item { ModeItem(Icons.Rounded.Settings, "Settings", "Preferences", Color.White, false) {} }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // RECENT ACTIVITY TITLE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View All",
                    color = NeonPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // RECENT ACTIVITY LIST
            RecentActivityItem(Icons.Rounded.Lock, "Device locked", "Manually", "9:41 AM", NeonPurple)
            Spacer(modifier = Modifier.height(8.dp))
            RecentActivityItem(Icons.Rounded.Notifications, "Voice lock triggered", "Lock my phone", "9:32 AM", Color(0xFF00E676))
            Spacer(modifier = Modifier.height(8.dp))
            RecentActivityItem(Icons.Rounded.Lock, "Pocket lock activated", "Proximity detected", "9:21 AM", Color(0xFFFFB300))

            Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom nav
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val normalizedHour = ((hour + 11) % 12) + 1
    val amPm = if (hour >= 12) "PM" else "AM"
    return String.format(Locale.US, "%d:%02d %s", normalizedHour, minute, amPm)
}

@Composable
fun PermissionCard(title: String, subtitle: String, isActive: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBackground,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isActive) NeonPurple.copy(alpha = 0.5f) else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock, 
                contentDescription = null, 
                tint = if (isActive) NeonPurple else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = subtitle, color = if (isActive) Color(0xFF00E676) else Color.Gray, fontSize = 12.sp)
            }
            Switch(
                checked = isActive,
                onCheckedChange = { onCheckedChange(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonPurple,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF222222)
                )
            )
        }
    }
}

@Composable
fun ModeItem(icon: ImageVector, title: String, subtitle: String, color: Color, isActive: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, if (isActive) color.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = if (isActive) color else color.copy(alpha = 0.7f), modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, color = Color.Gray, fontSize = 10.sp, textAlign = TextAlign.Center)
        if (isActive) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF00E676)))
        }
    }
}

@Composable
fun RecentActivityItem(icon: ImageVector, title: String, subtitle: String, time: String, iconColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBackground,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Text(text = time, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = DeepNavy.copy(alpha = 0.95f),
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPurple,
                selectedTextColor = NeonPurple,
                indicatorColor = DeepNavy
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Menu, contentDescription = "Activity") },
            label = { Text("Activity") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.List, contentDescription = "Modes") },
            label = { Text("Modes") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { }
        )
    }
}