#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string>
#include <vector>
#include <fstream>
#include <sys/stat.h>

#define APPNAME "YouMatter"
#define MAX_LINE 512

// Define suspicious strings as a vector of strings
const std::vector<std::string> suspiciousStrings = {
        "frida-server", "frida-server-64", "frida-server-32", "re.frida.server", "re.frida",
        "frida.", "frida-agent-64.so", "rida-agent-64.so", "agent-64.so", "frida-agent-32.so",
        "frida-helper-32", "frida-helper", "frida-agent", "pool-frida", "frida", "frida-",
        "frida-server", "linjector", "gum-js-loop", "frida_agent_main", "gmain", "magisk",
        ".magisk", "libriru", "xposed",
        // Add more suspicious strings as needed
};

// Helper function to check for suspicious strings in a file
bool containsSuspiciousStrings(const std::string& filePath) {
    std::ifstream file(filePath);
    if (!file.is_open()) {
        return false;
    }

    std::string line;
    while (std::getline(file, line)) {
        for (const auto& suspicious : suspiciousStrings) {
            if (line.find(suspicious) != std::string::npos) {
                file.close();
                return true;
            }
        }
    }

    file.close();
    return false;
}

// Helper function to check if a file exists
bool fileExists(const std::string& path) {
    struct stat buffer;
    return (stat(path.c_str(), &buffer) == 0);
}

// Frida server detection function
void *detect_frida_loop(void *) {
    struct sockaddr_in sa;
    memset(&sa, 0, sizeof(sa));
    sa.sin_family = AF_INET;
    inet_aton("127.0.0.1", &(sa.sin_addr));

    int sock;
    char res[7];
    int ret;

    while (1) {
        for (int i = 0; i <= 65535; ++i) {
            sock = socket(AF_INET, SOCK_STREAM, 0);
            if (sock == -1) {
                // Handle socket creation error
                __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Socket creation failed");
                continue;
            }

            sa.sin_port = htons(i);

            if (connect(sock, (struct sockaddr *)&sa, sizeof(sa)) != -1) {
                send(sock, "\x00", 1, 0);
                send(sock, "AUTH\r\n", 6, 0);

                usleep(100); // Give it some time to answer

                memset(res, 0, 7);
                ret = recv(sock, res, 6, MSG_DONTWAIT);
                if (ret > 0 && strcmp(res, "REJECT") == 0) {
                    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "FRIDA DETECTED [1] - frida server running on port %d!", i);
                }
            }

            close(sock);
        }

        // Add your memory scanning logic here (if required)

        sleep(3); // Sleep for 3 seconds between scans
    }

    return nullptr; // Should never reach here
}

// JNI native methods
extern "C"
JNIEXPORT void JNICALL Java_com_caressa_allizhealth_app_security_ui_SplashScreenActivity_init(JNIEnv *env, jobject thisObj) {
    pthread_t t;
    pthread_create(&t, NULL, detect_frida_loop, nullptr);
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_caressa_allizhealth_app_security_ui_SplashScreenActivity_checkForSuspiciousFiles(JNIEnv *env, jobject obj) {
    const std::vector<std::string> suspiciousPaths = {
            "/data/local/tmp/re.frida.server/frida-agent-64.so",
            "/data/local/tmp/re.frida.server",
            "/sbin/.magisk",
    };

    for (const auto& path : suspiciousPaths) {
        if (fileExists(path)) {
            return JNI_TRUE;
        }
    }
    return JNI_FALSE;
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_caressa_allizhealth_app_security_ui_SplashScreenActivity_isFridaDetected(JNIEnv *env, jobject obj) {
    return containsSuspiciousStrings("/proc/self/maps");
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_caressa_allizhealth_app_security_ui_SplashScreenActivity_checkProcMaps(JNIEnv *env, jobject obj) {
    return containsSuspiciousStrings("/proc/self/maps");
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_caressa_allizhealth_app_security_ui_SplashScreenActivity_checkProcTasks(JNIEnv *env, jobject obj) {
    return containsSuspiciousStrings("/proc/self/task");
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_caressa_allizhealth_app_security_ui_SplashScreenActivity_checkProcMounts(JNIEnv *env, jobject obj) {
    return containsSuspiciousStrings("/proc/mounts");
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_caressa_allizhealth_app_security_ui_SplashScreenActivity_checkProcExe(JNIEnv *env, jobject obj) {
    return containsSuspiciousStrings("/proc/self/exe");
}