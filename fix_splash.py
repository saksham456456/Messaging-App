with open("app/src/main/kotlin/com/synq/app/presentation/screens/splash/AnimatedSplashScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.draw.scale", "import androidx.compose.ui.draw.scale\nimport androidx.compose.ui.graphics.Color")

with open("app/src/main/kotlin/com/synq/app/presentation/screens/splash/AnimatedSplashScreen.kt", "w") as f:
    f.write(content)
