import os

# Update NavGraph.kt
with open('app/src/main/java/com/stotra/sahasranamam/presentation/navigation/NavGraph.kt', 'r') as f:
    nav = f.read()

nav = nav.replace('import com.stotra.sahasranamam.presentation.srisuktam.SriSuktamStudyScreen', 'import com.stotra.sahasranamam.presentation.study.StotraStudyScreen')
nav = nav.replace('import com.stotra.sahasranamam.presentation.srisuktam.SriSuktamViewModel', 'import com.stotra.sahasranamam.presentation.study.StotraStudyViewModel')
nav = nav.replace('const val SRI_SUKTAM_STUDY = "sri_suktam_study"', 'const val STUDY = "study/{stotraId}"\n    const val CATEGORY = "category/{categoryName}"')
nav = nav.replace('composable(NavRoutes.SRI_SUKTAM_STUDY) {', 'composable(NavRoutes.STUDY) {')
nav = nav.replace('val viewModel: SriSuktamViewModel = hiltViewModel()', 'val viewModel: StotraStudyViewModel = hiltViewModel()')
nav = nav.replace('SriSuktamStudyScreen(', 'StotraStudyScreen(')
nav = nav.replace('onSriSuktamStudyClick = { navController.navigate(NavRoutes.SRI_SUKTAM_STUDY) }', 'onCategoryClick = { categoryName -> navController.navigate("category/$categoryName") }')
nav = nav.replace('import com.stotra.sahasranamam.presentation.home.HomeScreen', 'import com.stotra.sahasranamam.presentation.home.HomeScreen\nimport com.stotra.sahasranamam.presentation.category.CategoryScreen\nimport com.stotra.sahasranamam.presentation.category.CategoryViewModel')

new_route = """        composable(NavRoutes.CATEGORY) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            val viewModel: CategoryViewModel = hiltViewModel()
            CategoryScreen(
                categoryName = categoryName,
                viewModel = viewModel,
                onStotraClick = { stotraId -> navController.navigate("study/$stotraId") },
                onBackClick = { navController.popBackStack() }
            )
        }
"""
nav = nav.replace('        composable(NavRoutes.STUDY) {', new_route + '        composable(NavRoutes.STUDY) {')

with open('app/src/main/java/com/stotra/sahasranamam/presentation/navigation/NavGraph.kt', 'w') as f:
    f.write(nav)

# Update StotraStudyViewModel.kt
with open('app/src/main/java/com/stotra/sahasranamam/presentation/study/StotraStudyViewModel.kt', 'r') as f:
    vm = f.read()

vm = vm.replace('package com.stotra.sahasranamam.presentation.srisuktam', 'package com.stotra.sahasranamam.presentation.study\n\nimport androidx.lifecycle.SavedStateHandle')
vm = vm.replace('class SriSuktamViewModel @Inject constructor(', 'class StotraStudyViewModel @Inject constructor(\n    private val savedStateHandle: SavedStateHandle,')
vm = vm.replace('private val _uiState = MutableStateFlow(SriSuktamUiState())', 'private val _uiState = MutableStateFlow(SriSuktamUiState())')
vm = vm.replace('val uiState: StateFlow<SriSuktamUiState> = _uiState.asStateFlow()', 'val uiState: StateFlow<SriSuktamUiState> = _uiState.asStateFlow()')
vm = vm.replace('fun loadSriSuktam() {', 'fun loadStotra(stotraId: String) {')
vm = vm.replace('loadSriSuktam()', 'val stotraId = savedStateHandle.get<String>("stotraId")\n        if (stotraId != null) {\n            loadStotra(stotraId)\n        }')
vm = vm.replace('repository.getShlokasForStotra("sri_suktam")', 'repository.getShlokasForStotra(stotraId)')

with open('app/src/main/java/com/stotra/sahasranamam/presentation/study/StotraStudyViewModel.kt', 'w') as f:
    f.write(vm)

# Update StotraStudyScreen.kt
with open('app/src/main/java/com/stotra/sahasranamam/presentation/study/StotraStudyScreen.kt', 'r') as f:
    screen = f.read()

screen = screen.replace('package com.stotra.sahasranamam.presentation.srisuktam', 'package com.stotra.sahasranamam.presentation.study')
screen = screen.replace('fun SriSuktamStudyScreen(', 'fun StotraStudyScreen(')
screen = screen.replace('viewModel: SriSuktamViewModel', 'viewModel: StotraStudyViewModel')
screen = screen.replace('text = "श्रीसूक्तम् (Sri Suktam)"', 'text = "Stotra / Suktam"')

with open('app/src/main/java/com/stotra/sahasranamam/presentation/study/StotraStudyScreen.kt', 'w') as f:
    f.write(screen)
