with open("app/src/main/kotlin/com/synq/app/data/repository/ChatRepositoryImpl.kt", "r") as f:
    content = f.read()

content = content.replace("} catch (e: Exception) {} ", "} catch (e: Exception) { android.util.Log.e(\"ChatRepository\", \"Sync failed\", e) } ")

with open("app/src/main/kotlin/com/synq/app/data/repository/ChatRepositoryImpl.kt", "w") as f:
    f.write(content)
