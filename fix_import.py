with open("app/src/main/kotlin/com/synq/app/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("ContextContextCompat.getMainExecutor", "ContextCompat.getMainExecutor")

with open("app/src/main/kotlin/com/synq/app/MainActivity.kt", "w") as f:
    f.write(content)
