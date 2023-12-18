package com.cokimutai.nowamusic


import android.content.Context
import javax.inject.Inject

class ContextCover @Inject constructor(private val context: Context) {

  fun supplyCtx(): Context {
    return context
  }

}