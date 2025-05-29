import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.productivityapp.R
import com.example.productivityapp.models.Role
import com.example.productivityapp.ui.theme.BottomNavContainer
import com.example.productivityapp.ui.theme.BottomNavSelectIndicator
import com.example.productivityapp.ui.theme.Purple80

@Composable
fun BottomNavMenu(
    navController: NavController,
    role: Role?,
    modifier: Modifier = Modifier
) {
    val items =
        when (role) {
            Role.DOCTOR -> {
                listOf(
                    TopLevelRoute(
                        "Пациенты",
                        Screens.PatientList.route,
                        Icons.Default.Face
                    ),
                    TopLevelRoute(
                        "Профиль",
                        Screens.DoctorProfile.route,
                        Icons.Default.AccountCircle
                    )

                )
            }

            Role.PATIENT -> {
                listOf(
                    TopLevelRoute(
                        "Статистика",
                        Screens.AppStatistics.route,
                        ImageVector.vectorResource(R.drawable.statistics)
                    ),
                    TopLevelRoute(
                        "Чат",
                        Screens.ChatList.route,
                        Icons.Default.Email
                    ),
                    TopLevelRoute(
                        "Профиль",
                        Screens.UserProfile.route,
                        Icons.Default.AccountCircle
                    )

                )
            }

            null -> emptyList()
        }
    val routes = items.map { it.route }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    if ((currentDestination != null) && (routes.contains(currentDestination.route))
    ) {
        NavigationBar(
            modifier = modifier,
            containerColor = BottomNavContainer
        ) {
            items.forEach { screen ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = BottomNavSelectIndicator
                    ),
                    icon = {
                        Icon(
                            painter = rememberVectorPainter(
                                image = screen.icon
                            ),
                            tint = Color.Black,
                            contentDescription = "${screen.route} icon"
                        )
                    },
                    label = {
                        Text(
                            text = screen.name,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            softWrap = false
                        )
                    },
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}