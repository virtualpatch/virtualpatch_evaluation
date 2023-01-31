## CVE-2018-9493

-   Status: Completed.

-   Description: In the content provider of the download manager, there is a possible SQL injection due to improper input validation. This could lead to local information disclosure with no additional execution privileges needed. User interaction is not needed for exploitation.

-   Type: ID

-   Severity: High

-   Links:

1. [NVD](https://nvd.nist.gov/vuln/detail/CVE-2018-9493)
2. [Execute "strict" queries with extra parentheses](https://android.googlesource.com/platform/frameworks/base/+/462aaeaa616e0bb1342e8ef7b472acc0cbc93deb)
3. [All untrusted selections must go through builder](https://android.googlesource.com/platform/packages/providers/DownloadProvider/+/e7364907439578ce5334bce20bb03fef2e88b107)
4. [Extend SQLiteQueryBuilder for update and delete](https://android.googlesource.com/platform/frameworks/base/+/ebc250d16c747f4161167b5ff58b3aea88b37acf)
5. [PoC](https://github.com/IOActive/AOSP-DownloadProviderDbDumper)

-   Exploit: Perform SQL injection like in the PoC

-   Patch: Check if parentheses are opened and closed correctly, if not in order then a sqli is detected and a null Cursor is returned. Done for the ContentResolver query method.
