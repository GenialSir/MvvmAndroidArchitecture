#include <jni.h>
#include <string>
#include <signal.h>
#include <ucontext.h>
#include <unistd.h>
#include <fstream>
#include <android/log.h>
#include <ctime>

#define LOG_TAG "NativeCrashHandler"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// -----------------------------
// 本地 crash trace 宏
// -----------------------------
static std::string logDirPath;

// 宏定义，自动写入文件名和行号
#define CRASH_TRACE() logCrashLocation(__FILE__, __LINE__)

// 记录函数调用位置
static void logCrashLocation(const char* file, int line) {
    std::string filePath = logDirPath + "/crash-locations.log";
    std::ofstream ofs(filePath, std::ios::app);
    ofs << "Trace at: " << file << ":" << line << std::endl;
    ofs.close();
}

// -----------------------------
// 获取当前时间
// -----------------------------
static std::string getTimeStr() {
    time_t now = time(nullptr);
    char buf[64];
    strftime(buf, sizeof(buf), "%Y-%m-%d-%H-%M-%S", localtime(&now));
    return std::string(buf);
}

// -----------------------------
// 信号名转字符串
// -----------------------------
static const char* signalName(int sig) {
    switch (sig) {
        case SIGSEGV: return "SIGSEGV (Invalid memory reference)";
        case SIGABRT: return "SIGABRT (Abort signal)";
        case SIGILL:  return "SIGILL (Illegal instruction)";
        case SIGFPE:  return "SIGFPE (Floating point error)";
        default: return "Unknown signal";
    }
}

// -----------------------------
// 信号处理函数
//PC → 哪条指令导致崩溃
//SP → 当前函数栈顶位置
//LR → 调用该函数的返回地址
//RIP 指向触发crash的指令地址
//RSP 指向当时栈顶
// -----------------------------
static void sigactionHandler(int sig, siginfo_t* info, void* context) {
    std::string filePath = logDirPath + "/crash-native-" + getTimeStr() + ".log";
    std::ofstream ofs(filePath);

    ofs << "=== Native Crash Detected ===\n";
    ofs << "Signal: " << signalName(sig) << " (" << sig << ")\n";
    ofs << "Fault address: " << info->si_addr << "\n";

    ucontext_t* uctx = (ucontext_t*)context;
#if defined(__aarch64__)
    ofs << "PC: " << std::hex << uctx->uc_mcontext.pc << "\n";
    ofs << "SP: " << std::hex << uctx->uc_mcontext.sp << "\n";
    ofs << "LR: " << std::hex << uctx->uc_mcontext.regs[30] << "\n"; // aarch64 LR
#elif defined(__arm__)
    ofs << "PC: " << std::hex << uctx->uc_mcontext.arm_pc << "\n";
    ofs << "SP: " << std::hex << uctx->uc_mcontext.arm_sp << "\n";
    ofs << "LR: " << std::hex << uctx->uc_mcontext.arm_lr << "\n";
#elif defined(__x86_64__)
    ofs << "RIP: " << std::hex << uctx->uc_mcontext.gregs[REG_RIP] << "\n";
    ofs << "RSP: " << std::hex << uctx->uc_mcontext.gregs[REG_RSP] << "\n";
#endif

    ofs << "=============================\n";
    ofs.close();

    LOGE("Native crash log written: %s", filePath.c_str());

    // 恢复默认处理，确保系统仍生成 tombstone
    signal(sig, SIG_DFL);
    raise(sig);
}

// -----------------------------
// 初始化函数（Java 调用）
// -----------------------------
extern "C" JNIEXPORT void JNICALL
Java_com_genialsir_crashhandler_NativeLib_initNativeCrashHandler(
        JNIEnv* env,
        jobject /* this */,
        jstring logDir) {

    const char* path = env->GetStringUTFChars(logDir, nullptr);
    logDirPath = path;
    env->ReleaseStringUTFChars(logDir, path);

    struct sigaction sa;
    sa.sa_sigaction = sigactionHandler;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = SA_SIGINFO;

    sigaction(SIGSEGV, &sa, nullptr);
    sigaction(SIGABRT, &sa, nullptr);
    sigaction(SIGILL,  &sa, nullptr);
    sigaction(SIGFPE,  &sa, nullptr);

    LOGE("Native crash handler initialized, logs at: %s", logDirPath.c_str());
}

// -----------------------------
// JNI 测试方法（触发 crash）
// -----------------------------
extern "C" JNIEXPORT void JNICALL
Java_com_genialsir_crashhandler_NativeLib_testCrash(
        JNIEnv* env,
        jobject /* this */) {

    CRASH_TRACE();  // 自动记录文件和行号

    int* p = nullptr;
    *p = 42;  // 故意触发 SIGSEGV
}

// -----------------------------
// 普通 JNI 方法
// -----------------------------
extern "C" JNIEXPORT jstring JNICALL
Java_com_genialsir_crashhandler_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    CRASH_TRACE();  // 记录调用位置

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
