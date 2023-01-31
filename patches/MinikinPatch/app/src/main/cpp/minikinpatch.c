//
// Created by virtualpatch on 11/5/2021.
//
#include <string.h>
#include "jni.h"
#include <android/log.h>
#include "bytehook.h"
#include "Bypass/bypass_dlfcn.h"

#define LOG(...)  __android_log_print(ANDROID_LOG_INFO, "HOOK_LOG", __VA_ARGS__)

#define NO_EDIT 0

typedef float (*do_layout_word_t)(uint16_t*, size_t, size_t, size_t, bool, void* paint, size_t bufStart, uint8_t, uint8_t, void *, void*, float*, void*, void* bounds, void*);
typedef float (*do_layout_run_cached_t)(void *, void*, uint32_t *, bool, void *, size_t,uint8_t, uint8_t, void*, void*, float*, void*, void* , void*);

static inline bool isWordBreakAfter(uint16_t c) {
    if (c == ' ' || (0x2000 <= c && c <= 0x200A) || c == 0x3000) {
        // spaces
        return true;
    }
    // Break layout context before and after BiDi control character.
    if ((0x2066 <= c && c <= 0x2069) || (0x202A <= c && c <= 0x202E) || c == 0x200E ||
        c == 0x200F) {
        return true;
    }
    // Note: kana is not included, as sophisticated fonts may kern kana
    return false;
}

static inline bool isWordBreakBefore(uint16_t c) {
    // CJK ideographs (and yijing hexagram symbols)
    return isWordBreakAfter(c) || (0x3400 <= c && c <= 0x9FFF);
}

static inline size_t getPrevWordBreakForCache(const uint16_t* chars, size_t offset, size_t len) {
    //LOG("getPrevWordBreakForCache called");
    if (offset == 0) {
        return 0;
    }
    if (offset > len) offset = len;
    if (isWordBreakBefore(chars[offset - 1])) {
        return offset - 1;
    }
    for (size_t i = offset - 1; i > 0; i--) {
        if (isWordBreakBefore(chars[i]) || isWordBreakAfter(chars[i - 1])) {
            return i;
        }
    }
    return 0;
}

static inline size_t getNextWordBreakForCache(const uint16_t* chars, size_t offset, size_t len) {
    //LOG("getNextWordBreakForCache called");
    if (offset >= len) {
        return len;
    }
    if (isWordBreakAfter(chars[offset])) {
        return offset + 1;
    }
    for (size_t i = offset + 1; i < len; i++) {
        // No need to check isWordBreakAfter(chars[i - 1]) since it is checked
        // in previous iteration.  Note that isWordBreakBefore returns true
        // whenever isWordBreakAfter returns true.
        if (isWordBreakBefore(chars[i])) {
            return i;
        }
    }
    return len;
}

do_layout_word_t do_layout_word = NULL;
do_layout_run_cached_t do_layout_run_cached_prev = NULL;

void load_do_layout_word() {
    void *h = bp_dlopen("/system/lib64/libminikin.so", RTLD_NOW);
    do_layout_word = (do_layout_word_t) bp_dlsym(h, "_ZN7minikin6Layout12doLayoutWordEPKtmmmbRKNS_12MinikinPaintEmNS_15StartHyphenEditENS_13EndHyphenEditEPKNS_12LayoutPiecesEPS0_PfPNS_13MinikinExtentEPNS_11MinikinRectEPS8_");
}

do_layout_run_cached_hook_callback(bytehook_stub_t task_stub, int status_code, const char *caller_path_name, const char *sym_name, void *new_func, void *prev_func, void *arg) {
if(BYTEHOOK_STATUS_CODE_ORIG_ADDR == status_code)
{
do_layout_run_cached_prev = (do_layout_run_cached_t) prev_func;
LOG(">>>>> save original address: %x", (uintptr_t)prev_func);
}
else
{
LOG(">>>>> hooked. stub: %x"", status: %d, caller_path_name: %s, sym_name: %s, new_func: %x, prev_func: %x, arg: %x", (uintptr_t)task_stub, status_code, caller_path_name, sym_name, (uintptr_t)new_func, (uintptr_t)prev_func, (uintptr_t)arg);
}
}

static bool allow_filter_for_hook_all(const char *caller_path_name, void *arg)
{
    (void)arg;
    if(NULL != strstr(caller_path_name, "liblog.so")) return false;
    return true;
}

float doLayoutRunCached(void* textBuf[2], const uint32_t *range, bool isRtl, void *paint, size_t dstStart,
                        uint8_t startHyphen, uint8_t endHyphen, void* lpIn, void* layout, float* advances,
                        void* extents, void* bounds, void* lpOut) {
    uint32_t range_start = range[0];
    uint32_t range_end = range[1];
    if (range_start == UINT32_MAX || range_end == UINT32_MAX) {
        BYTEHOOK_POP_STACK();
        return 0.0f;  // ICU failed to retrieve the bidi run?
    }
    const uint16_t *buf = (uint16_t*) textBuf[0];
    const uint32_t bufSize = (uint32_t) textBuf[1];
    const uint32_t start = range_start;
    const uint32_t end = range_end;
    float advance = 0;
    if (!isRtl) {
// left to right
        uint32_t wordstart =
                start == bufSize ? start : getPrevWordBreakForCache(buf, start + 1, bufSize);
        uint32_t wordend;
        for (size_t iter = start; iter < end; iter = wordend) {
            wordend = getNextWordBreakForCache(buf, iter, bufSize);
            const uint32_t wordcount = ((end < wordend) ? end : wordend) - iter;
            const uint32_t offset = iter - start;
            advance += do_layout_word(buf + wordstart, iter - wordstart, wordcount,
                                      wordend - wordstart, isRtl, paint, iter - dstStart,
// Only apply hyphen to the first or last word in the string.
                                      iter == start ? startHyphen : NO_EDIT,
                                      wordend >= end ? endHyphen : NO_EDIT, lpIn,
                                      layout, advances ? advances + offset : NULL,
                                      extents ? extents + offset : NULL, bounds, lpOut);
            wordstart = wordend;
        }
    } else {
// right to left
        uint32_t wordstart;
        uint32_t wordend = end == 0 ? 0 : getNextWordBreakForCache(buf, end - 1, bufSize);
        for (size_t iter = end; iter > start; iter = wordstart) {
            wordstart = getPrevWordBreakForCache(buf, iter, bufSize);
            uint32_t bufStart = start > wordstart ? start : wordstart;
            const uint32_t offset = bufStart - start;
            advance += do_layout_word(buf + wordstart, bufStart - wordstart, iter - bufStart,
                                      wordend - wordstart, isRtl, paint, bufStart - dstStart,
// Only apply hyphen to the first (rightmost) or last (leftmost)
// word in the string.
                                      wordstart <= start ? startHyphen : NO_EDIT,
                                      iter == end ? endHyphen : NO_EDIT, lpIn, layout,
                                      advances ? advances + offset : NULL,
                                      extents ? extents + offset : NULL, bounds, lpOut);
            wordend = wordstart;
        }
    }
    BYTEHOOK_POP_STACK();
    return advance;
}

void startMinikinHook() {
    LOG("loading minikin patch...");
    //bytehook_set_debug(true);
    load_do_layout_word();
    bytehook_hook_partial(allow_filter_for_hook_all, NULL, NULL, "_ZN7minikin6Layout17doLayoutRunCachedERKNS_14U16StringPieceERKNS_5RangeEbRKNS_12MinikinPaintEmNS_15StartHyphenEditENS_13EndHyphenEditEPKNS_12LayoutPiecesEPS0_PfPNS_13MinikinExtentEPNS_11MinikinRectEPSC_", doLayoutRunCached, do_layout_run_cached_hook_callback, NULL);
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if ((*vm)->GetEnv(vm, (void **)(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // Find your class. JNI_OnLoad is called from the correct class loader context for this to work.
    jclass c = (*env)->FindClass(env, "dev/virtualpatch/patch/NativeLoader");
    if (c == NULL) return JNI_ERR;

    // Register your class' native methods.
    static const JNINativeMethod methods[] = {
            {"nativeLoad", "()V", (void *)(startMinikinHook)},
    };
    int rc = (*env)->RegisterNatives(env, c, methods, sizeof(methods)/sizeof(JNINativeMethod));
    if (rc != JNI_OK) return rc;

    return JNI_VERSION_1_6;

}