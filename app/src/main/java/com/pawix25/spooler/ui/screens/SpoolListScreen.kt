package com.pawix25.spooler.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pawix25.spooler.data.PrintJob as PrintJobData
import com.pawix25.spooler.data.Spool
import com.pawix25.spooler.viewmodel.SpoolViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoolListScreen(
    spools: List<Spool>,
    onAddSpool: () -> Unit,
    onSpoolClick: (Spool) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Spooler",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            var clicked by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (clicked) 0.95f else 1f,
                animationSpec = tween(100),
                finishedListener = { clicked = false },
                label = "fab_scale"
            )
            
            ExtendedFloatingActionButton(
                onClick = { 
                    clicked = true
                    onAddSpool()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Spool")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Spool", fontWeight = FontWeight.Medium)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        if (spools.isEmpty()) {
            EmptySpoolState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onAddSpool = onAddSpool
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(spools, key = { it.id }) { spool ->
                    val index = spools.indexOf(spool)
                    AnimatedSpoolItem(
                        spool = spool, 
                        onClick = { onSpoolClick(spool) },
                        animationDelay = index * 50
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySpoolState(
    modifier: Modifier = Modifier,
    onAddSpool: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated floating card with pulsing effect
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse_scale"
            )
            
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, CircleShape)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "No spools found",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Add your first filament spool to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            var buttonClicked by remember { mutableStateOf(false) }
            val buttonScale by animateFloatAsState(
                targetValue = if (buttonClicked) 0.95f else 1f,
                animationSpec = tween(100),
                finishedListener = { buttonClicked = false },
                label = "button_scale"
            )
            
            FilledTonalButton(
                onClick = { 
                    buttonClicked = true
                    onAddSpool()
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Your First Spool")
            }
        }
    }
}

@Composable
fun AnimatedSpoolItem(
    spool: Spool, 
    onClick: () -> Unit,
    animationDelay: Int = 0
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(spool.id) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        ) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        SpoolItem(spool = spool, onClick = onClick)
    }
}

@Composable
fun SpoolItem(spool: Spool, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (spool.color.isNotEmpty()) {
                                        try {
                                            Color(android.graphics.Color.parseColor(spool.color))
                                        } catch (e: Exception) {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    if (spool.color.isNotEmpty()) {
                                        try {
                                            Color(android.graphics.Color.parseColor(spool.color)).copy(alpha = 0.7f)
                                        } catch (e: Exception) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    }
                                )
                            )
                        )
                        .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = spool.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = spool.material,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Progress section with animations
            val progress = if (spool.totalWeight > 0f && spool.remainingWeight.isFinite()) {
                (spool.remainingWeight / spool.totalWeight).coerceIn(0f, 1f)
            } else {
                0f
            }
            val progressPercentage = (progress * 100).toInt()
            
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                ),
                label = "progress_animation"
            )
            
            val progressColor by animateColorAsState(
                targetValue = when {
                    progress > 0.5f -> MaterialTheme.colorScheme.primary
                    progress > 0.2f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                ),
                label = "progress_color_animation"
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$progressPercentage%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
                Text(
                    text = "${spool.remainingWeight.toInt()}g / ${spool.totalWeight.toInt()}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpoolScreen(onSave: (Spool) -> Unit, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var material by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("#4CAF50") }
    var totalWeight by rememberSaveable { mutableStateOf("") }
    var remainingWeight by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Add New Spool",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Spool Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Spool Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    OutlinedTextField(
                        value = material,
                        onValueChange = { material = it },
                        label = { Text("Material (e.g., PLA, PETG, ABS)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            ColorPicker(color = color, onColorChange = { color = it })

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Weight Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    OutlinedTextField(
                        value = totalWeight,
                        onValueChange = { totalWeight = it },
                        label = { Text("Total Weight (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    OutlinedTextField(
                        value = remainingWeight,
                        onValueChange = { remainingWeight = it },
                        label = { Text("Remaining Weight (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    val newSpool = Spool(
                        name = name,
                        material = material,
                        color = color,
                        totalWeight = totalWeight.toFloatOrNull() ?: 0f,
                        remainingWeight = remainingWeight.toFloatOrNull() ?: 0f
                    )
                    onSave(newSpool)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotEmpty() && material.isNotEmpty() && totalWeight.isNotEmpty(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Save Spool",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ColorPicker(color: String, onColorChange: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val colors = listOf(
        "#4CAF50", "#2196F3", "#FF9800", "#F44336", "#9C27B0", "#673AB7",
        "#3F51B5", "#009688", "#CDDC39", "#FFEB3B", "#795548", "#607D8B",
        "#E91E63", "#FF5722", "#000000", "#FFFFFF"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Color Selection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDialog = true }
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (color.isNotEmpty()) {
                                try {
                                    Color(android.graphics.Color.parseColor(color))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                        .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Selected Color",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = color,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { 
                Text(
                    "Select Color",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(colors) { colorItem ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(
                                    if (colorItem.isNotEmpty()) {
                                        try {
                                            Color(android.graphics.Color.parseColor(colorItem))
                                        } catch (e: Exception) {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                                .border(
                                    if (colorItem == color) 3.dp else 1.dp,
                                    if (colorItem == color) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    CircleShape
                                )
                                .clickable {
                                    onColorChange(colorItem)
                                    showDialog = false
                                }
                        ) {
                            if (colorItem == color) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(24.dp),
                                    tint = if (colorItem == "#FFFFFF" || colorItem == "#FFEB3B") 
                                        Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Done")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoolDetailsScreen(
    spoolId: Int, 
    onBack: () -> Unit, 
    onAddPrint: (PrintJobData) -> Unit, 
    viewModel: SpoolViewModel
) {
    val spool by viewModel.getSpool(spoolId).collectAsState()
    val printJobs by viewModel.getPrintJobsForSpool(spoolId).collectAsState(initial = emptyList())
    var showAddPrintDialog by remember { mutableStateOf(false) }
    var printName by remember { mutableStateOf("") }
    var printWeight by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        spool.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            var fabClicked by remember { mutableStateOf(false) }
            val fabScale by animateFloatAsState(
                targetValue = if (fabClicked) 0.95f else 1f,
                animationSpec = tween(100),
                finishedListener = { fabClicked = false },
                label = "fab_scale_details"
            )
            
            ExtendedFloatingActionButton(
                onClick = { 
                    fabClicked = true
                    showAddPrintDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .graphicsLayer {
                        scaleX = fabScale
                        scaleY = fabScale
                    }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Print")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Print", fontWeight = FontWeight.Medium)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status card showing progress
            item {
                var statusCardVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(100)
                    statusCardVisible = true
                }
                
                AnimatedVisibility(
                    visible = statusCardVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        try {
                                            Color(android.graphics.Color.parseColor(spool.color))
                                        } catch (e: Exception) {
                                            MaterialTheme.colorScheme.primary
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Column {
                                val percentageText = if (spool.totalWeight > 0f && spool.remainingWeight.isFinite()) {
                                    "${((spool.remainingWeight / spool.totalWeight) * 100).toInt()}%"
                                } else {
                                    "0%"
                                }
                                Text(
                                    text = percentageText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Remaining", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val detailProgress = if (spool.totalWeight > 0f && spool.remainingWeight.isFinite()) {
                            (spool.remainingWeight / spool.totalWeight).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                        val animatedDetailProgress by animateFloatAsState(
                            targetValue = detailProgress,
                            animationSpec = tween(
                                durationMillis = 1200,
                                easing = FastOutSlowInEasing
                            ),
                            label = "detail_progress_animation"
                        )
                        
                        LinearProgressIndicator(
                            progress = { animatedDetailProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    }
                }
            }
            
            // Details card
            item {
                var detailsCardVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(250)
                    detailsCardVisible = true
                }
                
                AnimatedVisibility(
                    visible = detailsCardVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Details", 
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SpoolDetailItem("Material", spool.material)
                        SpoolDetailItem("Color", spool.color)
                        SpoolDetailItem("Remaining", "${spool.remainingWeight.toInt()}g")
                        SpoolDetailItem("Total", "${spool.totalWeight.toInt()}g")
                    }
                    }
                }
            }
            
            // Print history
            item {
                var historyCardVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(400)
                    historyCardVisible = true
                }
                
                AnimatedVisibility(
                    visible = historyCardVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Print History (${printJobs.size})", 
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (printJobs.isEmpty()) {
                            Text(
                                "No prints yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            printJobs.forEachIndexed { index, printJob ->
                                var itemVisible by remember { mutableStateOf(false) }
                                
                                LaunchedEffect(printJob.id) {
                                    kotlinx.coroutines.delay((index * 100).toLong())
                                    itemVisible = true
                                }
                                
                                AnimatedVisibility(
                                    visible = itemVisible,
                                    enter = slideInHorizontally(
                                        initialOffsetX = { it / 4 },
                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                    ) + fadeIn(animationSpec = tween(300))
                                ) {
                                    Column {
                                        PrintHistoryItem(printJob)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                    }
                }
            }
        }
    }
    
    // Add Print Dialog with animations
    AnimatedVisibility(
        visible = showAddPrintDialog,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(200)),
        exit = scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(150))
    ) {
        AlertDialog(
            onDismissRequest = { showAddPrintDialog = false },
            title = { Text("Add Print") },
            text = {
                Column {
                    OutlinedTextField(
                        value = printName,
                        onValueChange = { printName = it },
                        label = { Text("Print Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = printWeight,
                        onValueChange = { printWeight = it },
                        label = { Text("Weight Used (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val weight = printWeight.toFloatOrNull() ?: 0f
                        if (weight > 0f && printName.isNotEmpty()) {
                            onAddPrint(PrintJobData(
                                name = printName,
                                spoolId = spoolId,
                                weight = weight,
                                date = Date()
                            ))
                            printName = ""
                            printWeight = ""
                            showAddPrintDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPrintDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SpoolDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PrintHistoryItem(printJob: PrintJobData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = printJob.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(printJob.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${printJob.weight.toInt()}g",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}