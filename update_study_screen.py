import re

with open("app/src/main/java/com/stotra/sahasranamam/presentation/srisuktam/SriSuktamStudyScreen.kt", "r") as f:
    content = f.read()

# Make the main verse card clickable
old_card_modifier = """                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),"""
new_card_modifier = """                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleAudioPlayback() },"""
content = content.replace(old_card_modifier, new_card_modifier)

# Replace PadaCardItem
old_pada_item = """@Composable
fun PadaCardItem(
    pada: Pada,
    onPlayClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Word Pronunciation",
                            tint = SaffronPrimary
                        )
                    }

                    Text(
                        text = pada.sanskritCombined,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!pada.sandhiRuleName.isNullOrBlank()) {
                    Surface(
                        color = SaffronPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = pada.sandhiRuleName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Split: ${pada.sanskritSplit}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SandhiHighlight
            )

            Text(
                text = pada.iast,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Meaning: ${pada.meaning}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            if (!pada.sandhiRuleExplanation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rule: ${pada.sandhiRuleExplanation}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}"""

new_pada_item = """@Composable
fun PadaCardItem(
    pada: Pada,
    onPlayClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Word Pronunciation",
                        tint = SaffronPrimary
                    )
                }

                Text(
                    text = pada.sanskritCombined,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            
            if (!pada.sandhiRuleName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = SaffronPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = pada.sandhiRuleName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Split: ${pada.sanskritSplit}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SandhiHighlight,
                textAlign = TextAlign.Center
            )

            Text(
                text = pada.iast,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Meaning: ${pada.meaning}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            if (!pada.sandhiRuleExplanation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rule: ${pada.sandhiRuleExplanation}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}"""
content = content.replace(old_pada_item, new_pada_item)

with open("app/src/main/java/com/stotra/sahasranamam/presentation/srisuktam/SriSuktamStudyScreen.kt", "w") as f:
    f.write(content)

