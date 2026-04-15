package com.example.vpntestapp.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.vpntestapp.domain.model.Country
import com.example.vpntestapp.domain.model.VpnServer
import com.example.vpntestapp.domain.model.VpnStatus
import com.example.vpntestapp.ui.theme.AccentCyan
import com.example.vpntestapp.ui.theme.ConnectedColor
import com.example.vpntestapp.ui.theme.ConnectingColor
import com.example.vpntestapp.ui.theme.DarkBackground
import com.example.vpntestapp.ui.theme.DarkCard
import com.example.vpntestapp.ui.theme.DarkSurface
import com.example.vpntestapp.ui.theme.DisconnectedColor
import com.example.vpntestapp.ui.theme.ErrorColor
import com.example.vpntestapp.ui.theme.TextSecondary
import com.example.vpntestapp.ui.viewmodel.DEFAULT_SERVERS
import com.example.vpntestapp.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        val glowColor by animateColorAsState(
            targetValue = when (uiState.vpnStatus) {
                VpnStatus.CONNECTED -> ConnectedColor.copy(alpha = 0.07f)
                VpnStatus.CONNECTING -> ConnectingColor.copy(alpha = 0.07f)
                VpnStatus.DISCONNECTED -> AccentCyan.copy(alpha = 0.04f)
            },
            animationSpec = tween(800),
            label = "glow"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        radius = 700f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VPN Guard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = uiState.vpnStatus)
            }
            Spacer(modifier = Modifier.height(56.dp))
            VpnPowerButton(
                status = uiState.vpnStatus,
                onClick = viewModel::onConnectToggle
            )
            Spacer(modifier = Modifier.height(40.dp))
            StatusLabel(status = uiState.vpnStatus)
            Spacer(modifier = Modifier.height(48.dp))
            ServerCard(
                server = uiState.selectedServer,
                onClick = viewModel::toggleServerPicker
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tap the shield to ${if (uiState.vpnStatus == VpnStatus.CONNECTED) "disconnect" else "connect"}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }

    if (uiState.isServerPickerVisible) {
        ModalBottomSheet(
            onDismissRequest = viewModel::toggleServerPicker,
            sheetState = sheetState,
            containerColor = DarkSurface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(TextSecondary.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                )
            }
        ) {
            ServerPickerSheet(
                defaultServers = DEFAULT_SERVERS,
                countries = uiState.countries,
                isLoading = uiState.isLoadingCountries,
                error = uiState.countriesError,
                selectedServer = uiState.selectedServer,
                onServerSelected = viewModel::onServerSelected,
                onCountrySelected = viewModel::onServerFromCountry,
                onRetry = viewModel::retryLoadCountries
            )
        }
    }
}

@Composable
private fun VpnPowerButton(
    status: VpnStatus,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation"
    )
    val buttonColor by animateColorAsState(
        targetValue = when (status) {
            VpnStatus.CONNECTED -> ConnectedColor
            VpnStatus.CONNECTING -> ConnectingColor
            VpnStatus.DISCONNECTED -> AccentCyan
        },
        animationSpec = tween(600),
        label = "btnColor"
    )
    val ringScale = if (status == VpnStatus.CONNECTING) pulseScale else 1f
    val ringAlpha = if (status == VpnStatus.CONNECTING) 0.3f else 0.15f
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(ringScale)
                .background(
                    color = buttonColor.copy(alpha = ringAlpha),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    color = buttonColor.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )
        if (status == VpnStatus.CONNECTING) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(148.dp)
                    .graphicsLayer { rotationZ = rotationAngle },
                color = buttonColor,
                strokeWidth = 3.dp,
                trackColor = buttonColor.copy(alpha = 0.15f)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(128.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            buttonColor.copy(alpha = 0.25f),
                            DarkCard
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = buttonColor.copy(alpha = 0.6f),
                    shape = CircleShape
                )
                .clickable(
                    enabled = status != VpnStatus.CONNECTING,
                    onClick = onClick
                )
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "VPN Toggle",
                tint = buttonColor,
                modifier = Modifier.size(44.dp)
            )
        }
    }
}

@Composable
private fun StatusLabel(status: VpnStatus) {
    val color by animateColorAsState(
        targetValue = when (status) {
            VpnStatus.CONNECTED -> ConnectedColor
            VpnStatus.CONNECTING -> ConnectingColor
            VpnStatus.DISCONNECTED -> TextSecondary
        },
        animationSpec = tween(500),
        label = "statusColor"
    )
    val label = when (status) {
        VpnStatus.CONNECTED -> "Connected"
        VpnStatus.CONNECTING -> "Connecting..."
        VpnStatus.DISCONNECTED -> "Not Protected"
    }
    val sub = when (status) {
        VpnStatus.CONNECTED -> "Your connection is secure"
        VpnStatus.CONNECTING -> "Establishing secure tunnel"
        VpnStatus.DISCONNECTED -> "Your IP address is visible"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = sub,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun StatusBadge(status: VpnStatus) {
    val (color, label) = when (status) {
        VpnStatus.CONNECTED -> ConnectedColor to "ON"
        VpnStatus.CONNECTING -> ConnectingColor to "..."
        VpnStatus.DISCONNECTED -> DisconnectedColor to "OFF"
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Text(text = label, color = color, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ServerCard(server: VpnServer, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = server.flagUrl,
                contentDescription = server.country,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = "Selected Server",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = server.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Change server",
            tint = TextSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServerPickerSheet(
    defaultServers: List<VpnServer>,
    countries: List<Country>,
    isLoading: Boolean,
    error: String?,
    selectedServer: VpnServer,
    onServerSelected: (VpnServer) -> Unit,
    onCountrySelected: (Country) -> Unit,
    onRetry: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        item {
            Text(
                text = "Choose Server",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp, top = 4.dp)
            )
        }
        item {
            Text(
                text = "RECOMMENDED",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(defaultServers) { server ->
            ServerListItem(
                name = server.name,
                flagUrl = server.flagUrl,
                isSelected = server.id == selectedServer.id,
                onClick = { onServerSelected(server) }
            )
        }
        item {
            Text(
                text = "ALL COUNTRIES",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
        }
        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = AccentCyan,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading countries...",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            error != null -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ErrorColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Failed to load countries",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = error,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(" Retry", color = Color(0xFF001F2A))
                        }
                    }
                }
            }
            countries.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No countries available",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            else -> {
                items(countries, key = { it.code }) { country ->
                    ServerListItem(
                        name = country.name,
                        flagUrl = country.flagUrl,
                        isSelected = country.code == selectedServer.countryCode,
                        onClick = { onCountrySelected(country) }
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun ServerListItem(
    name: String,
    flagUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AccentCyan.copy(alpha = 0.1f) else Color.Transparent)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) AccentCyan.copy(alpha = 0.4f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = flagUrl,
                contentDescription = name,
                modifier = Modifier
                    .size(width = 32.dp, height = 22.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) AccentCyan else MaterialTheme.colorScheme.onBackground
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = AccentCyan,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
