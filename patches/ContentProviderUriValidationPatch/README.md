## CVE-2018-9548

-   Status: Completed.

-   Description: In multiple functions of ContentProvider.java, there is a possible permission bypass due to a missing URI validation. This could lead to local information disclosure with no additional execution privileges needed. User interaction is not needed for exploitation.

-   Type: ID

-   Severity: High

-   Links:

1. [NVD](https://nvd.nist.gov/vuln/detail/CVE-2018-9548)
2. [Google Source](https://android.googlesource.com/platform/frameworks/base/+/c97efaa05124e020d7cc8c6e08be9c3b55ac4ea7)
3. [To build the ContentProvider](https://stackoverflow.com/questions/26901644/meaning-of-android-content-urimatcher)
4. [<path-permission>](https://developer.android.com/guide/topics/manifest/path-permission-element)

-   Exploit: Create a vulnerable app with a content provider, with a path prefix and some permission for a "/private" path. Access it with the exploit app using the instructions provided in the poc of the Google Source website.

-   Patch: Add a Uri verification for all the operations exported by the ContentResolver.
