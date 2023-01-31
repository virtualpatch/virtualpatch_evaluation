import os
import subprocess
from os import path
import shutil

PATCHES = [
    "accountpatch",
    "LeakContactPatch",
    "okhttppatch",
    "PackagesPatch",
    "BroadcastHijackPatch",
    "BtMMSPatch",

    "DocumentMetadataPatch",
    "BluetoothAliasPatch",
    "ContentProviderUriValidationPatch",
    "DoSWidthCalculationPatch",
    "DownloadManagerSQLiPatch",
    "HostnameParsingPatch",
    "LinkPhishingPatch",
    "TextLineDoSPatch",

    "EndCallPatch",
    "NotificationDOSPatch",
    "SettingsProviderPatch",
    "ClickableToastPatch",
]

cwd = os.getcwd()

os.makedirs("build", exist_ok=True)

for patch in PATCHES:
    os.chdir(f"{cwd}/{patch}")
    subprocess.run(["./gradlew", "clean"])
    subprocess.run(["./gradlew", "assembleDebug"])
    shutil.copyfile("app/build/outputs/apk/debug/app-debug.apk", f"{cwd}/build/{patch}.apk")
