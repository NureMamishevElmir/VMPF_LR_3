# Файл правил ProGuard/R8. Для навчального проєкту мініфікацію вимкнено,
# тож правила тут мінімальні.

# Зберегти моделі даних, що серіалізуються Gson
-keep class com.nure.vmpf.catalog.data.model.** { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-dontwarn okhttp3.**
-dontwarn retrofit2.**
