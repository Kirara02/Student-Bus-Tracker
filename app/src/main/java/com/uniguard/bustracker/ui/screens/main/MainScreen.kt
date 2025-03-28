package com.uniguard.bustracker.ui.screens.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.uniguard.bustracker.R
import com.uniguard.bustracker.ui.screens.main.viewmodel.MainViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val mStr by viewModel.mStr.collectAsState()
    val displayText by viewModel.displayText.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Logo and text at the top left
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.uniguard_logo),
                contentDescription = "UniGuard Logo",
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "UNIGUARD",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Settings Icon for navigation
        IconButton(
            onClick = { navController.navigate("/setting") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.secondary,
            )
        }

        // Main content centered in the screen
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title at the top
            Text(
                text = stringResource(R.string.main_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Larger Profile Section
            ProfileSection(viewModel)

            // Info text at the bottom
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.scan_instruction),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileSection(viewModel: MainViewModel) {
    val borderColor = MaterialTheme.colorScheme.secondary
    val displayText by viewModel.displayText.collectAsState()
    val userData by viewModel.userData.collectAsState()

    // Get the primary color outside the Canvas scope
    val primaryColor = MaterialTheme.colorScheme.primary

    // Start countdown animation when displayText is not empty
    val isNfcDetected = displayText.isNotEmpty()

    // Animation for countdown
    val countdownAnimation = remember(isNfcDetected) {
        if (isNfcDetected) {
            Animatable(1f)
        } else {
            Animatable(0f)
        }
    }

    // Effect to animate countdown when displayText is not empty
    LaunchedEffect(displayText) {
        if (displayText.isNotEmpty()) {
            countdownAnimation.snapTo(1f)
            countdownAnimation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 10000, easing = LinearEasing)
            )
        } else {
            countdownAnimation.snapTo(0f)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .heightIn(min = 300.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image with square border and countdown indicator
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp)
            ) {
                // Square border
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = borderColor,
                        style = Stroke(width = 3f)
                    )
                }

                // Profile Image
                if (userData?.imageUrl != null) {
                    AsyncImage(
                        model = userData?.imageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile_placeholder),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Countdown indicator - only shown when displayText is not empty
                if (isNfcDetected) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Get dimensions of the canvas
                        val width = size.width
                        val height = size.height

                        // Draw countdown border with thinner stroke
                        val strokeWidth = 4f

                        // Use the color extracted outside the Canvas with transparency
                        val lineColor = primaryColor.copy(alpha = 0.7f)

                        // Calculate how much of each side to draw based on countdown progress
                        val progress = countdownAnimation.value
                        val totalLength = (width + height) * 2
                        val currentLength = totalLength * progress

                        // Draw the square border progressively
                        val pathStroke = Stroke(width = strokeWidth, cap = StrokeCap.Butt)

                        // Top edge
                        if (currentLength > 0) {
                            val topLength = minOf(width, currentLength)
                            drawLine(
                                color = lineColor,
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(topLength, 0f),
                                strokeWidth = strokeWidth
                            )
                        }

                        // Right edge
                        if (currentLength > width) {
                            val rightLength = minOf(height, currentLength - width)
                            drawLine(
                                color = lineColor,
                                start = androidx.compose.ui.geometry.Offset(width, 0f),
                                end = androidx.compose.ui.geometry.Offset(width, rightLength),
                                strokeWidth = strokeWidth
                            )
                        }

                        // Bottom edge
                        if (currentLength > width + height) {
                            val bottomLength = minOf(width, currentLength - width - height)
                            drawLine(
                                color = lineColor,
                                start = androidx.compose.ui.geometry.Offset(width, height),
                                end = androidx.compose.ui.geometry.Offset(
                                    width - bottomLength,
                                    height
                                ),
                                strokeWidth = strokeWidth
                            )
                        }

                        // Left edge
                        if (currentLength > width + height + width) {
                            val leftLength = minOf(height, currentLength - width - height - width)
                            drawLine(
                                color = lineColor,
                                start = androidx.compose.ui.geometry.Offset(0f, height),
                                end = androidx.compose.ui.geometry.Offset(0f, height - leftLength),
                                strokeWidth = strokeWidth
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Profile details in a column on the right side
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Today's date at the top
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calendar icon
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Date label
                        Text(
                            text = stringResource(R.string.date_label) + ": ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )

                        // Actual date
                        Text(
                            text = java.text.SimpleDateFormat(
                                "EEEE, dd MMMM yyyy",
                                java.util.Locale.getDefault()
                            )
                                .format(java.util.Date()),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Header for attendance
                Text(
                    text = stringResource(R.string.attendance_record),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Name (using user data if available)
                Text(
                    text = when {
                        userData != null -> userData!!.name
                        isNfcDetected && userData == null -> displayText
                        else -> stringResource(R.string.scan_nfc_card)
                    },
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Email if available
                userData?.email?.let { email ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = email,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status information
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isNfcDetected) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = if (userData != null)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (userData != null)
                                        stringResource(R.string.verified)
                                    else
                                        "User not found",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (userData != null)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onErrorContainer
                                )

                                // Display countdown seconds
                                val remainingTime = (countdownAnimation.value * 10f).toInt() + 1
                                if (remainingTime > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "($remainingTime)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Light,
                                        color = if (userData != null)
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                        else
                                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Current time as text - enhanced for better readability
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = java.text.SimpleDateFormat(
                                        "HH:mm:ss",
                                        java.util.Locale.getDefault()
                                    )
                                        .format(java.util.Date()),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = stringResource(R.string.time_format).substringBefore(":"),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = stringResource(R.string.waiting_for_scan),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}