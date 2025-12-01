package com.genialsir.mvvmcommon.base

import androidx.lifecycle.ViewModel
import com.genialsir.mvvmcommon.usecase.error.ErrorManager
import javax.inject.Inject

/**
 * @author genialsir@163.com (GenialSir) on 2025/9/10
 */
abstract class BaseViewModel : ViewModel(){

    /**
     * Inject Singleton ErrorManager
     * Use this errorManager to get the Errors
     */
    @Inject
    lateinit var errorManager: ErrorManager
}