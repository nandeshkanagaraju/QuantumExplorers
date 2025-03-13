@Composable
fun GameQuestion(
    question: Question,
    onAnswerSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAnswer by remember { mutableStateOf("") }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Display multiple choice options
            question.options.forEach { option ->
                AnswerOption(
                    text = option,
                    isSelected = selectedAnswer == option,
                    onSelect = {
                        selectedAnswer = option
                        onAnswerSelected(true)
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Replace CircuitBuilder with a placeholder for circuit levels
            question.circuitLevel?.let { level ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Circuit Challenge",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Circuit building functionality is temporarily unavailable",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    
                    Button(
                        onClick = {
                            // For now, just mark it as incomplete
                            onAnswerSelected(false)
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Skip Circuit Challenge")
                    }
                }
            }
        }
    }
} 