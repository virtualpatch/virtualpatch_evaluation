## CVE-2019-2003

-   Status: Completed.

-   Description: In addLinks of Linkify.java, there is a possible phishing vector due to an unusual root cause. This could lead to remote code execution or misdirection of clicks with no additional execution privileges needed. User interaction is needed for exploitation.

-   Type: EoP

-   Severity: High

-   Links:

1. [MITRE](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2019-2003)
2. [Google source](https://android.googlesource.com/platform/frameworks/base/+/5acf81a1f4df34451b76e76a416b8a262ba7f485)
3. [Source code referenced for patch](https://android.googlesource.com/platform/frameworks/base/+/430fc97/core/java/android/text/util/Linkify.java)

-   Exploit: Create a misleading link with unsupported characters

-   Patch: Like google source but in virtualapp I can't click on links and open a browser. Still I can patch addLinks to not display a link.
