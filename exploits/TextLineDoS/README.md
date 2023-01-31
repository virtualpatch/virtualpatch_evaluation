## CVE-2019-2232

-   Status: Completed but patch has some limitations.

-   Description: In handleRun of TextLine.java, there is a possible application crash due to improper input validation. This could lead to remote denial of service when processing Unicode with no additional execution privileges needed. User interaction is not needed for exploitation.

-   Type: DoS

-   Severity: Critical

-   Links:

1. [NVD](https://nvd.nist.gov/vuln/detail/CVE-2019-2232)
2. [Google source](https://android.googlesource.com/platform/frameworks/base/+/4ce901e4058d93336dca3413dc53b81bbdf9d3e8)
3. [Where I found the payload](https://android.stackexchange.com/questions/195426/why-does-this-string-of-ltr-and-rtl-markers-cause-android-devices-to-hang)

-   Exploit: Crash the application with the payload I found online

-   Patch: Like google source
