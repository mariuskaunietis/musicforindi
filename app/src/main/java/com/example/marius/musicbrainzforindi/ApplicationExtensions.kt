package com.example.marius.musicbrainzforindi

import android.app.Application
import com.example.marius.musicbrainzforindi.api.AppService
import com.example.marius.musicbrainzforindi.api.AppServiceFactory

//cheap DI
val Application.appService by lazy {
  AppServiceFactory().createService<AppService>()
}