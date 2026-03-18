package com.example.forgithubclient.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.forgithubclient.data.User
import com.example.forgithubclient.data.UserDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserViewModel,
    onUserClick: (String) -> Unit
) {
    val state by viewModel.sortedUsersState.collectAsState()
    val currentSortOrder by viewModel.sortOrder.collectAsState()
    var showSortDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GitHub Users", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                    }
                    IconButton(onClick = { viewModel.fetchUsers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val currentState = state) {
                is UiState.Loading -> {
                    LoadingCarAnimation()
                }
                is UiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(currentState.data, key = { it.id }) { user ->
                            UserItem(user = user, onClick = { onUserClick(user.login) })
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorView(message = currentState.message, onRetry = { viewModel.fetchUsers() })
                }
                else -> {}
            }
        }

        if (showSortDialog) {
            AlertDialog(
                onDismissRequest = { showSortDialog = false },
                title = { Text("Sort by") },
                text = {
                    Column {
                        SortOptionRow("ID Number", currentSortOrder == SortOrder.ID) {
                            viewModel.setSortOrder(SortOrder.ID)
                            showSortDialog = false
                        }
                        SortOptionRow("Name", currentSortOrder == SortOrder.NAME) {
                            viewModel.setSortOrder(SortOrder.NAME)
                            showSortDialog = false
                        }
                        SortOptionRow("Public Repos", currentSortOrder == SortOrder.REPOS) {
                            viewModel.setSortOrder(SortOrder.REPOS)
                            showSortDialog = false
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSortDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SortOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun UserItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Avatar of ${user.login}",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.login,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ID: ${user.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                user.publicRepos?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "$it repos",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
        
        user.company?.let { company ->
            Text(
                text = company,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp).widthIn(max = 100.dp),
                textAlign = TextAlign.End
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    username: String,
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.userDetailState.collectAsState()

    LaunchedEffect(username) {
        viewModel.fetchUserDetail(username)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(username, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val currentState = state) {
                is UiState.Loading -> {
                    LoadingCarAnimation()
                }
                is UiState.Success -> {
                    UserDetailContent(detail = currentState.data)
                }
                is UiState.Error -> {
                    ErrorView(message = currentState.message, onRetry = { viewModel.fetchUserDetail(username) })
                }
                else -> {}
            }
        }
    }
}

@Composable
fun UserDetailContent(detail: UserDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = detail.avatarUrl,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = detail.name ?: detail.login,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "@${detail.login}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        
        detail.bio?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoCard(label = "Followers", value = detail.followers.toString())
            InfoCard(label = "Following", value = detail.following.toString())
            InfoCard(label = "Repos", value = detail.publicRepos.toString())
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            detail.location?.let { ProfileInfoRow(icon = Icons.Default.LocationOn, label = "Location", value = it) }
            detail.company?.let { ProfileInfoRow(icon = Icons.Default.Work, label = "Company", value = it) }
            detail.blog?.let { ProfileInfoRow(icon = Icons.Default.Public, label = "Blog", value = it) }
            detail.email?.let { ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = it) }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error: $message", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun LoadingCarAnimation() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val infiniteTransition = rememberInfiniteTransition(label = "carTransition")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = screenWidth.value + 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "carOffset"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                HorizontalDivider(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                Canvas(
                    modifier = Modifier
                        .offset(x = offsetX.dp)
                        .size(100.dp, 50.dp)
                ) {
                    val carColor = Color(0xFF2196F3)
                    drawRoundRect(
                        color = carColor,
                        size = Size(90.dp.toPx(), 25.dp.toPx()),
                        cornerRadius = CornerRadius(10f, 10f),
                        topLeft = Offset(0f, 15.dp.toPx())
                    )
                    drawRoundRect(
                        color = carColor,
                        size = Size(50.dp.toPx(), 20.dp.toPx()),
                        cornerRadius = CornerRadius(12f, 12f),
                        topLeft = Offset(20.dp.toPx(), 0f)
                    )
                    drawRect(
                        color = Color.White.copy(alpha = 0.6f),
                        size = Size(18.dp.toPx(), 12.dp.toPx()),
                        topLeft = Offset(25.dp.toPx(), 4.dp.toPx())
                    )
                    drawRect(
                        color = Color.White.copy(alpha = 0.6f),
                        size = Size(18.dp.toPx(), 12.dp.toPx()),
                        topLeft = Offset(47.dp.toPx(), 4.dp.toPx())
                    )
                    drawCircle(color = Color(0xFF333333), radius = 8.dp.toPx(), center = Offset(20.dp.toPx(), 40.dp.toPx()))
                    drawCircle(color = Color(0xFF333333), radius = 8.dp.toPx(), center = Offset(70.dp.toPx(), 40.dp.toPx()))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Searching the GitHub universe...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
