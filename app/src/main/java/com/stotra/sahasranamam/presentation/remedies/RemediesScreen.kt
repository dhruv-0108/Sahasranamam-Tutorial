package com.stotra.sahasranamam.presentation.remedies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stotra.sahasranamam.domain.model.RemedyCategory
import com.stotra.sahasranamam.presentation.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemediesScreen(
    viewModel: RemediesViewModel,
    onCategorySelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Remedies & Guidance",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Select a situation or problem you are facing in life to see recommended chants:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.categories) { category ->
                        RemedyCategoryCard(
                            category = category,
                            onClick = { onCategorySelected(category.categoryId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemedyCategoryCard(
    category: RemedyCategory,
    onClick: () -> Unit
) {
    val gradientColors = when (category.categoryId) {
        "wealth_prosperity" -> listOf(Color(0xFFFFD700).copy(alpha = 0.25f), Color(0xFFFFA500).copy(alpha = 0.25f))
        "health_healing" -> listOf(Color(0xFFE0F7FA).copy(alpha = 0.3f), Color(0xFF80DEEA).copy(alpha = 0.3f))
        "protection_shield" -> listOf(Color(0xFFFFEBEE).copy(alpha = 0.3f), Color(0xFFFFCDD2).copy(alpha = 0.3f))
        "wisdom_intellect" -> listOf(Color(0xFFEDE7F6).copy(alpha = 0.3f), Color(0xFFD1C4E9).copy(alpha = 0.3f))
        "vak_siddhi" -> listOf(Color(0xFFF1F8E9).copy(alpha = 0.3f), Color(0xFFDCEDC8).copy(alpha = 0.3f))
        else -> listOf(Color(0xFFFFF3E0).copy(alpha = 0.3f), Color(0xFFFFE0B2).copy(alpha = 0.3f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.titleHindi,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.titleEnglish,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = SaffronPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.descriptionEnglish,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    maxLines = 3
                )
            }
        }
    }
}
