import os
from PIL import Image

def resize_and_save(img, size, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    resized = img.resize((size, size), Image.Resampling.LANCZOS)
    resized.save(path)

img = Image.open('/workspace/app_icon.png').convert('RGBA')

# Android
android_base = '/workspace/composeApp/src/androidMain/res'
android_sizes = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192
}

for density, size in android_sizes.items():
    resize_and_save(img, size, f"{android_base}/mipmap-{density}/ic_launcher.png")
    resize_and_save(img, size, f"{android_base}/mipmap-{density}/ic_launcher_round.png")

# iOS
ios_base = '/workspace/iosApp/iosApp/Assets.xcassets/AppIcon.appiconset'
resize_and_save(img, 1024, f"{ios_base}/app-icon-1024.png")

# Web & JVM
resize_and_save(img, 256, '/workspace/composeApp/src/webMain/resources/favicon.png')
resize_and_save(img, 512, '/workspace/composeApp/src/jvmMain/resources/app_icon.png')
# For JVM, let's just make sure the directory exists and copy it
print("Resize complete!")
