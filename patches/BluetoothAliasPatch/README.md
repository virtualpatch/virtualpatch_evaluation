## CVE-2021-0931

-   Status: Completed.

-   Description: In getAlias of BluetoothDevice.java, there is a possible way to create misleading permission dialogs due to missing data filtering. This could lead to local information disclosure with User execution privileges needed. User interaction is needed for exploitation.

-   Type: ID

-   Severity: High

-   Links:

1. [MITRE](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2021-0931)
2. [Google source](https://android.googlesource.com/platform/frameworks/base/+/afa5f3c37aea6dd0e14576c035d12fa84c95f2cb)

-   Exploit: Modify the alias for every connected device to the victims phone to a misleading text.

-   Patch: Like Google source but also hooked the setAlias method to avoid allowing the usage of these characters also here.
