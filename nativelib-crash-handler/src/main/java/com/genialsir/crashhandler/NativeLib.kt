package com.genialsir.crashhandler

class NativeLib {



    companion object {
        // Used to load the 'crashhandler' library on application startup.
        init {
            System.loadLibrary("crashhandler")
        }
    }

    /**
     * A native method that is implemented by the 'crashhandler' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun initNativeCrashHandler(logDirPath: String)

    external fun testCrash()

}