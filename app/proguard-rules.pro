# Room Database keep rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt keep rules
-keep class * extends android.app.Application
-keep class * extends androidx.activity.ComponentActivity
