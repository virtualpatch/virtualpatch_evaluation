# VirtualPatch: Fixing Android Security Vulnerabilities through Application-Level Virtualization

This repo contains the code of the exploits and the security patches we developed
for the CVEs used in the paper "VirtualPatch: Fixing Android Security 
Vulnerabilities through Application-Level Virtualization"

## CVE-2019-9376
  * CVE entry: [CVE-2019-9376](https://www.cve.org/CVERecord?id=CVE-2019-9376)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/32e85796389f57e2539c28f9e670277ab610459a)
  * VirtualPatch Patch: [accountpatch](patches/accountpatch)
  * Exploit: [shiva](exploits/shiva)
## CVE-2021-0313
  * CVE entry: [CVE-2021-0313](https://www.cve.org/CVERecord?id=CVE-2021-0313)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/minikin/+/ffb33bcf2520208166cb29f47c60add9c0e37349)
  * VirtualPatch Patch: [MinikinPatch](patches/MinikinPatch)
  * Exploit: [nobreak](exploits/nobreak)
## CVE-2021-0604
  * CVE entry: [CVE-2021-0604](https://www.cve.org/CVERecord?id=CVE-2021-0604) 
  * Original Patch: [[1]](https://android.googlesource.com/platform/packages/apps/Bluetooth/+/caf10da52ea7ce198c9e880833b2c2c408f7c740)
  * VirtualPatch Patch: [BtMMSPatch](patches/BtMMSPatch)
  * Exploit: [btmms](exploits/btmms)
## CVE-2021-0444
  * CVE entry: [CVE-2021-0444](https://www.cve.org/CVERecord?id=CVE-2021-0444) 
  * Original Patch: [[1]](https://android.googlesource.com/platform/packages/apps/Contacts/+/93c93923c8d4c0c91c8ba66cd7e65036d4ba9062)
  * VirtualPatch Patch: [LeakContactPatch](patches/LeakContactPatch)
  * Exploit: [LeakContact](exploits/LeakContact)
## CVE-2021-0341
  * CVE entry: [CVE-2021-0341](https://www.cve.org/CVERecord?id=CVE-2021-0521)
  * Original Patch: [[1]](https://android.googlesource.com/platform/external/okhttp/+/ddc934efe3ed06ce34f3724d41cfbdcd7e7358fc)
    [[2]](https://android.googlesource.com/platform/libcore/+/4076be9a99bad5ff7651540df976c57462c3b8ce)
  * VirtualPatch Patch: [okhttppatch](patches/okhttppatch)
  * Exploit: -
## CVE-2021-0521
  * CVE entry: [CVE-2021-0521](https://www.cve.org/CVERecord?id=CVE-2021-0521) 
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/9b694ef4d45ca54bcc4b7de6940f5608047a1a16)
  * VirtualPatch Patch: [PackagesPatch](patches/PackagesPatch)
  * Exploit: [AllApplications](exploits/AllApplications)
## CVE-2021-0591
  * CVE entry: [CVE-2021-0591](https://www.cve.org/CVERecord?id=CVE-2021-0591)
  * Original Patch: [[1]](https://android.googlesource.com/platform/packages/apps/Settings/+/f1d1bb78162209335b086ee10d8b7449879bcc64)
  * VirtualPatch Patch: [BroadcastHijackPatch](patches/BroadcastHijackPatch) [[2]](https://android.googlesource.com/platform/packages/apps/Settings/+/cdf9a1509b0ef1450b2b9b8c349abdbc7902be95)
  * Exploit: [BroadcastHijack](exploits/BroadcastHijack)
## CVE-2018-9493
  * CVE entry: [CVE-2018-9493](https://nvd.nist.gov/vuln/detail/CVE-2018-9493)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/462aaeaa616e0bb1342e8ef7b472acc0cbc93deb) [[2]](https://android.googlesource.com/platform/packages/providers/DownloadProvider/+/e7364907439578ce5334bce20bb03fef2e88b107)
  * VirtualPatch Patch: [DownloadManagerSQLiPatch](patches/DownloadManagerSQLiPatch)
  * Exploit: [DownloadManagerSQLi](exploits/DownloadManagerSQLi)
## CVE-2018-9452
  * CVE entry: [CVE-2018-9452](https://nvd.nist.gov/vuln/detail/CVE-2018-9452)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/54f661b16b308cf38d1b9703214591c0f83df64d) [[2]](https://android.googlesource.com/platform/frameworks/base/+/3b6f84b77c30ec0bab5147b0cffc192c86ba2634)
  * VirtualPatch Patch: [DoSWidthCalculationPatch](patches/DoSWidthCalculationPatch)
  * Exploit: [DoSWidthCalculation](exploits/DoSWidthCalculation)
## CVE-2018-9525
  * CVE entry: [CVE-2018-9525](https://nvd.nist.gov/vuln/detail/CVE-2018-9525)
  * Original Patch: [[1]](https://android.googlesource.com/platform/packages/apps/Settings/+/6409cf5c94cc1feb72dc078e84e66362fbecd6d5)
  * VirtualPatch Patch: not needed
  * Exploit: [ChangeDeviceSettings](exploits/ChangeDeviceSettings)
## CVE-2018-9548
  * CVE entry: [CVE-2018-9548](https://nvd.nist.gov/vuln/detail/CVE-2018-9548)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/c97efaa05124e020d7cc8c6e08be9c3b55ac4ea7)
  * VirtualPatch Patch: [ContentProviderUriValidationPatch](patches/ContentProviderUriValidationPatch)
  * Exploit: [ContentProviderUriValidation](exploits/ContentProviderUriValidation)
## CVE-2021-0931
  * CVE entry: [CVE-2021-0931](https://www.cve.org/CVERecord?id=CVE-2021-0931)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/afa5f3c37aea6dd0e14576c035d12fa84c95f2cb)
  * VirtualPatch Patch: [BluetoothAliasPatch](patches/BluetoothAliasPatch)
  * Exploit: [BluetoothAlias](exploits/BluetoothAlias)
## CVE-2018-9582
  * CVE entry: [CVE-2018-9582](https://nvd.nist.gov/vuln/detail/CVE-2018-9582) 
  * Original Patch: [[1]](https://android.googlesource.com/platform/packages/apps/PackageInstaller/+/ab39f6cb7afc48584da3c59d8e2a5e1ef121aafb)
  * VirtualPatch Patch: not needed
  * Exploit: [PackageInstallerSpoofing](exploits/PackageInstallerSpoofing)
## CVE-2019-2003
  * CVE entry: [CVE-2019-2003](https://www.cve.org/CVERecord?id=CVE-2019-2003)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/5acf81a1f4df34451b76e76a416b8a262ba7f485)
  * VirtualPatch Patch: [LinkPhishingPatch](patches/LinkPhishingPatch)
  * Exploit: [LinkPhishing](exploits/LinkPhishing)
## CVE-2019-2232
  * CVE entry: [CVE-2019-2232](https://nvd.nist.gov/vuln/detail/CVE-2019-2232)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/4ce901e4058d93336dca3413dc53b81bbdf9d3e8)
  * VirtualPatch Patch: [TextLineDoSPatch](patches/TextLineDoSPatch)
  * Exploit: [TextLineDoS](exploits/TextLineDoS)
## CVE-2018-9467
  * CVE entry: [CVE-2018-9467](https://www.cve.org/CVERecord?id=CVE-2018-9467) 
  * Original Patch: [[1]](https://android.googlesource.com/platform/libcore/+/518e8d27de9f32eb86bc3090ee2759ea93b9fb93)
  * VirtualPatch Patch: [HostnameParsingPatch](patches/HostnameParsingPatch)
  * Exploit: [HostnameParsing](exploits/HostnameParsing)
## CVE-2020-0239
  * CVE entry: [CVE-2020-0239](https://nvd.nist.gov/vuln/detail/CVE-2020-0239)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/556de438237965857fde874d22aff0c4232d4d99)
  * VirtualPatch Patch: [DocumentMetadataPatch](patches/DocumentMetadataPatch)
  * Exploit: [DocumentMetadataLeak](exploits/DocumentMetadataLeak)
## CVE-2020-0441
  * CVE entry: [CVE-2020-0441](https://www.cve.org/CVERecord?id=CVE-2020-0441)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/aaf6b40e1746db6189f6078dcd28d8f153a4cc50)
  * VirtualPatch Patch: [NotificationDOSPatch](patches/NotificationDOSPatch)
  * Exploit: [LongNotificationDOS](exploits/LongNotificationDOS)
## CVE-2020-0459
  * CVE entry: [CVE-2020-0459](https://www.cve.org/CVERecord?id=CVE-2020-0459)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/opt/net/wifi/+/db04b29f0f6a96b19850fc17e23818855f800d61) [[2]](https://android.googlesource.com/platform/frameworks/base/+/4bd54c477c89d11cfe2d84ff20098aed01cf5de9) [[3]](https://android.googlesource.com/platform/packages/apps/Car/Settings/+/dd7bed0670fbdf03d9097f2ba35967544467c863) [[4]](https://android.googlesource.com/platform/packages/apps/Settings/+/a9a7f65a10b7514a4070a93d419796498926b5b3) [[5]](https://android.googlesource.com/platform/packages/services/Car/+/54cc1b21d5b1e75f8c1d92cac32beaa2cad6a88c)
  * VirtualPatch Patch: not needed
  * Exploit: [NetworkInfoLeaker](exploits/NetworkInfoLeaker)
## CVE-2020-0391
  * CVE entry: [CVE-2020-0391](https://www.cve.org/CVERecord?id=CVE-2020-0391)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/860fd4b6a2a4fe5d681bc07f2567fdc84f0d1580)
  * VirtualPatch Patch: not needed
  * Exploit: [UnprotectedBroadcastPixel](exploits/UnprotectedBroadcastPixel)
## CVE-2021-1929
  * CVE entry: [CVE-2021-1929](https://www.cve.org/CVERecord?id=CVE-2021-1929) 
  * Original Patch: -
  * VirtualPatch Patch: not needed
  * Exploit: [QualcomQmmiLeaker](exploits/QualcomQmmiLeaker)
## CVE-2020-0014
  * CVE entry: [CVE-2020-0014](https://www.cve.org/CVERecord?id=CVE-2020-0014)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/d885c3279f3fecb2c08e382c733a440113dae644)
  * VirtualPatch Patch: [ClickableToastPatch](patches/ClickableToastPatch)
  * Exploit: [MaliciousToast](exploits/MaliciousToast)
## CVE-2019-2137
  * CVE entry: [CVE-2019-2137](https://www.cve.org/CVERecord?id=CVE-2019-2137) 
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/236b44274ebd1d7e3c706a24fd2a83d534d68ab0) [[2]](https://android.googlesource.com/platform/packages/services/Telecomm/+/e01da9a84f49b3e7ff4c8a876cdeb32b7beec1ea)
  * VirtualPatch Patch: [EndCallPatch](patches/EndCallPatch)
  * Exploit: [EndCallAttack](exploits/EndCallAttack)
## CVE-2020-0443
  * CVE entry: [CVE-2020-0443](https://www.cve.org/CVERecord?id=CVE-2020-0443)
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/base/+/d3a2b5832f6ca9da74cda814ec76aec679b3389a)
  * VirtualPatch Patch: [SettingsProviderPatch](patches/SettingsProviderPatch)
  * Exploit: [LocaleBootloop](exploits/LocaleBootloop)
## CVE-2021-0597
  * CVE entry: [CVE-2021-0597](https://www.cve.org/CVERecord?id=CVE-2021-0597) 
  * Original Patch: [[1]](https://android.googlesource.com/platform/frameworks/opt/net/voip/+/0e459673aa944d65989181b659c820504117ab51)
  * VirtualPatch Patch: not needed
  * Exploit: [SIPLeaker](exploits/SIPLeaker)

## Notes

Some CVEs do not require a patch because VirtualApp implementation of app-level
virtualization prevents the exploits by design. For instance, only certain
Broadcasts are delivered to or forwarded from apps running inside the virtual environment, so
exploits that use other Broadcast messages are blocked by default.